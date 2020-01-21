package com.huatu.handheld_huatu.business.faceteach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.faceteach.adapter.SplitOrderAdapter;
import com.huatu.handheld_huatu.business.faceteach.bean.SplitOrderBean;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.PayMessageEvent;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.PayUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SplitOrderActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_paid)
    TextView tvPaid;
    @BindView(R.id.tv_to_paid)
    TextView tvToPaid;
    @BindView(R.id.tv_split)
    TextView tvSplit;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.tv_refresh)
    TextView tvRefresh;
    @BindView(R.id.tv_reset)
    TextView tvReset;
    @BindView(R.id.price_info_layout)
    View priceInfoView;

    @BindView(R.id.rl_order)
    RecyclerView rlOrder;

    TextView tvWillSplite;
    TextView tvPayedPrice;

    private PayUtils mPayUtils;

    private ArrayList<SplitOrderBean> splitOrders;
    private List<SplitOrderBean> payedOrders;
    private CustomConfirmDialog splitDialog;

    private SplitOrderBean currentOrderBean;
    private SplitOrderAdapter adapter;
    private String oid;
    private String price;
    private String payedPrice = "0.0";
    private String willPayPrice = "0.0";
    private String title;
    private String orderDes;
    private String alipay_account;
    private String pid;
    private Intent mIntent;
    private boolean mIsLoading = false;
    private boolean isSplit = false;


    public static void startSplitOrderForResultActivity(Activity ctx, String oid, String title, String orderDes, String price, String alipay_account, String pid, boolean isSplit) {
        Intent intent = new Intent(ctx, SplitOrderActivity.class);
        intent.putExtra("oid", oid);
        intent.putExtra("price", price);
        intent.putExtra("title", title);
        intent.putExtra("orderDes", orderDes);
        intent.putExtra("alipay_account", alipay_account);
        intent.putExtra("pid", pid);
        intent.putExtra("isSplit", isSplit);
        ctx.startActivityForResult(intent,3001);
    }


    public static void startSplitOrderActivity(Context ctx, String oid, String title, String orderDes, String price, String alipay_account, String pid, boolean isSplit) {
        Intent intent = new Intent(ctx, SplitOrderActivity.class);
        intent.putExtra("oid", oid);
        intent.putExtra("price", price);
        intent.putExtra("title", title);
        intent.putExtra("orderDes", orderDes);
        intent.putExtra("alipay_account", alipay_account);
        intent.putExtra("pid", pid);
        intent.putExtra("isSplit", isSplit);
        ctx.startActivity(intent);
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_split_order;
    }

    @Override
    protected void onInitView() {
        QMUIStatusBarHelper.setStatusBarLightMode(SplitOrderActivity.this);
        rlOrder.setLayoutManager(new LinearLayoutManager(SplitOrderActivity.this));
        mIntent = getIntent();
        oid = mIntent.getStringExtra("oid");
        price = mIntent.getStringExtra("price");
        title = mIntent.getStringExtra("title");
        orderDes = getIntent().getStringExtra("orderDes");
        alipay_account = getIntent().getStringExtra("alipay_account");
        pid = getIntent().getStringExtra("pid");
        isSplit = getIntent().getBooleanExtra("isSplit", false);
        tvPrice.setText("¥" + price);
        tvToPaid.setText("待支付金额：" + price);
        tvName.setText(title);
        mPayUtils = new PayUtils();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(PayMessageEvent event) {
        if (event == null) {
            return;
        }
        if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_SUCCESS) {
            Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
            onLoadData();
        }else if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_FAIL) {
            Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
        } else if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_8000) {
            Toast.makeText(this, "支付异常", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onLoadData() {
        if (mIsLoading) return;
        if (Utils.isEmptyOrNull(oid)) {
            setCurrentOrderBean();
            return;
        }
        mIsLoading = true;
        String curTime = System.currentTimeMillis() + "";

        String md5 = String.format("oid=%s&timestamp=%s", oid, curTime);

        String sign = Md5Util.toSign(md5);

        String params = String.format("{'oid':'%s','timestamp':'%s','sign':'%s'}", oid, curTime, sign).replace("'", "\"");

        ServiceProvider.getFaceOrderChildren(compositeSubscription, params, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                if (e instanceof ApiException) {
                    if (((ApiException) e).getErrorCode() == -3) {
                        setCurrentOrderBean();
                    }
                }
                mIsLoading = false;
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                    ArrayList<SplitOrderBean> orders = (ArrayList<SplitOrderBean>) model.data;
                if (splitOrders == null)
                    splitOrders = orders;
                else {
                    splitOrders.clear();
                    splitOrders.addAll(orders);
                }
                if (splitOrders.isEmpty()) {
                    setCurrentOrderBean();
                } else {
                    showSplitList();
                    getPayedList();
                }
                mIsLoading = false;
            }
        });
    }

    private void setCurrentOrderBean() {
        currentOrderBean = new SplitOrderBean();
        currentOrderBean.sonstate = "-1";
        currentOrderBean.sonprice = TextUtils.isEmpty(willPayPrice) || Float.parseFloat(willPayPrice) <= 0f? price : willPayPrice;
        if (splitOrders == null) {
            splitOrders = new ArrayList<>();
        }
        if (splitOrders.isEmpty())
            splitOrders.add(currentOrderBean);
    }

    private void getPayedList() {
        float hasPayed = 0;
        float willPayed = 0;
        payedOrders = new ArrayList<>();
        for (SplitOrderBean bean : splitOrders) {
            if (bean.getState() == 1) {
                payedOrders.add(bean);
                hasPayed += Float.parseFloat(bean.sonprice);
            }
        }
        if (hasPayed != 0){
            willPayed =  Float.parseFloat(price) - hasPayed;
            payedPrice = String.format("%.2f", hasPayed);
            willPayPrice = String.format("%.2f", willPayed);
            tvToPaid.setText("待支付金额：" + willPayPrice);
            tvPaid.setText("已支付金额："+payedPrice);
            if (willPayed <= 0f){
                EventBus.getDefault().post(new BaseMessageEvent<>(BaseMessageEvent.BASE_EVENT_TYPE_ON_PAYED_ALL));
                PrefStore.putUserSettingInt("refresh_order_status",1);
                finish();
            }
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_reset, R.id.tv_split, R.id.tv_confirm, R.id.tv_refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_reset:
                isSplit = true;
                reset();
                break;
            case R.id.tv_confirm:
                isSplit = false;
                submitPriceList();
                break;
            case R.id.tv_split:
                isSplit = true;
                showSpitDialog();
                break;
            case R.id.tv_refresh:
                onLoadData();
                break;
        }
    }

    private void showSpitDialog() {
        if (splitDialog == null) {
            splitDialog = DialogUtils.createDialog(SplitOrderActivity.this, R.layout.face_split_order_dialog, "", "");
            View contentView = splitDialog.getContentView();
            final EditText contentEt = contentView.findViewById(R.id.et_split);
            ImageView ivBack = contentView.findViewById(R.id.iv_back);
            TextView tvShouldPrice = contentView.findViewById(R.id.tv_price);
            tvPayedPrice = contentView.findViewById(R.id.tv_paid);
            tvShouldPrice.setText("应支付金额：¥" + price);
            tvWillSplite = contentView.findViewById(R.id.tv_split_price);
            contentView.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (contentEt.getText() != null) {
                        String content = contentEt.getText().toString();
                        if (Utils.isEmptyOrNull(content))
                            return;
                        SplitOrderBean bean = new SplitOrderBean();
                        bean.sonstate = "-1";
                        bean.sonprice = String.format("%.2f", Float.parseFloat(content));
                        if (bean.getPrice() < 5000) {
                            ToastUtils.showMessage("拆分金额不能低于5000，请输入正确的拆分金额~");
                            return;
                        } else if (bean.getPrice() >= currentOrderBean.getPrice()) {
                            ToastUtils.showMessage("您输入的金额过大，请输入正确的拆分金额~");
                            return;
                        } else {
                            currentOrderBean.sonprice = String.format("%.2f", currentOrderBean.getPrice() - bean.getPrice());
                            splitOrders.add(bean);
                            showSplitList();
                        }
                    }
                    splitDialog.dismiss();
                }
            });
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    splitDialog.dismiss();
                }
            });
        }
        tvPayedPrice.setText("已支付金额：¥"+payedPrice);
        tvWillSplite.setText("待拆分金额：¥" + currentOrderBean.sonprice);
        splitDialog.show();

        Window window = splitDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = DisplayUtil.getScreenWidth() - DisplayUtil.dp2px(60);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
    }

    //显示和刷新拆分列表
    private void showSplitList() {
        if (adapter == null) {
            adapter = new SplitOrderAdapter(SplitOrderActivity.this, splitOrders, new SplitOrderAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    currentOrderBean = splitOrders.get(position);
                    if (currentOrderBean.getState() == -1)
                        showSpitDialog();
                    else if (currentOrderBean.getState() == 0) {
                        if (Float.parseFloat(currentOrderBean.sonprice) <= 5000){
                            mPayUtils.payZFB(SplitOrderActivity.this, title, orderDes, currentOrderBean.sonprice, currentOrderBean.sonoid, "https://bm.huatu.com/api/new_alipay2/notify_url.php",pid, alipay_account, false);
                        }else {
                            mIntent.setClass(SplitOrderActivity.this, FacePayWayActivity.class);
                            mIntent.putExtra("payOid", currentOrderBean.sonoid);
                            mIntent.putExtra("payPrice", currentOrderBean.sonprice);
                            mIntent.putExtra("isFromSplit", true);
                            startActivity(mIntent);
                        }
                    }
                }
            });
            setSplitView();
            rlOrder.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();

        if (rlOrder.getVisibility() == View.GONE) {
            rlOrder.setVisibility(View.VISIBLE);
            tvSplit.setVisibility(View.GONE);
        }

        if (isSplit && tvConfirm.getVisibility() != View.VISIBLE){
            tvConfirm.setVisibility(View.VISIBLE);
        }
    }

    private void reset() {
        if (adapter != null) {
            splitOrders.clear();
            setCurrentOrderBean();
            if (payedOrders != null && !payedOrders.isEmpty())
                splitOrders.addAll(payedOrders);
            adapter.notifyDataSetChanged();
            rlOrder.setVisibility(View.GONE);
            tvConfirm.setVisibility(View.GONE);
            tvSplit.setVisibility(View.VISIBLE);
        }

    }

    //提交订单
    private void submitPriceList() {
        if (splitOrders != null && !splitOrders.isEmpty() && adapter != null) {
            showProgress();
            if (payedOrders != null && !payedOrders.isEmpty())
                splitOrders.removeAll(payedOrders);

            String data;
            StringBuilder dataForSign = new StringBuilder();
            String[] dataStr = new String[splitOrders.size()];
            for (int i = 0; i < splitOrders.size(); i++) {
                dataStr[i] = splitOrders.get(i).sonprice;
                dataForSign.append(splitOrders.get(i).sonprice).append(",");
            }
            dataForSign.deleteCharAt(dataForSign.length() - 1);
            data = new Gson().toJson(dataStr);

            if (Utils.isEmptyOrNull(oid)) {
                adapter.setConfirm(true);
                adapter.notifyDataSetChanged();
                tvRefresh.setVisibility(View.VISIBLE);
                tvConfirm.setVisibility(View.GONE);
                tvReset.setVisibility(View.GONE);
                hideProgress();
                return;
            }
            String curTime = System.currentTimeMillis() + "";

            String md5 = String.format("data=%s&oid=%s&timestamp=%s", dataForSign, oid, curTime);

            String sign = Md5Util.toSign(md5);

            String params = String.format("{'data':%s,'oid':'%s','timestamp':'%s','sign':'%s'}", data, oid, curTime, sign).replace("'", "\"");

            ServiceProvider.postFaceSplitOrders(compositeSubscription, params, new NetResponse() {
                @Override
                public void onListSuccess(BaseListResponseModel model) {
                    super.onListSuccess(model);
                    List<String> orderIds = model.data;
                    for (int i = 0; i < orderIds.size(); i++)
                        splitOrders.get(i).sonoid = orderIds.get(i);
                    if (payedOrders != null && !payedOrders.isEmpty())
                        splitOrders.addAll(0,payedOrders);
                    adapter.setConfirm(true);
                    tvRefresh.setVisibility(View.VISIBLE);
                    tvConfirm.setVisibility(View.GONE);
                    tvReset.setVisibility(View.GONE);
                    priceInfoView.setVisibility(View.GONE);
                    hideProgress();

                    PrefStore.putUserSettingInt("refresh_split_status_"+oid,1);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    ToastUtils.showMessage("提交错误，请稍后重试");
                    hideProgress();
                }
            });
        }

    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        onInitView();
        if(adapter != null) {
            setSplitView();
        }
        onLoadData();
    }

    private void setSplitView(){
        adapter.setConfirm(!isSplit);
        if(!isSplit) {
            tvConfirm.setVisibility(View.GONE);
            priceInfoView.setVisibility(View.GONE);
            tvReset.setVisibility(View.GONE);
            tvRefresh.setVisibility(View.VISIBLE);
        }else {
            priceInfoView.setVisibility(View.VISIBLE);
            tvRefresh.setVisibility(View.GONE);
            tvConfirm.setVisibility(View.VISIBLE);
            tvReset.setVisibility(View.VISIBLE);
        }
    }
}
