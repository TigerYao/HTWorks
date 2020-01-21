package com.huatu.handheld_huatu.business.me.order;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.me.adapter.OrderLogisticAdapter;
import com.huatu.handheld_huatu.business.me.bean.OrderLogisticBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.refresh.OnRefreshListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.refresh.RefreshListView;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CustomDialog;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 我的物流
 * Created by chq on 2017/8/29.
 */

public class LogisticDetailActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener {
    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;
    @BindView(R.id.ll_logistic_head)
    LinearLayout ll_logistic_head;
    @BindView(R.id.tv_order_num)
    TextView tv_order_num;
    @BindView(R.id.tv_carrier)
    TextView tv_carrier;
    @BindView(R.id.tv_carrier_call)
    TextView tv_carrier_call;
    @BindView(R.id.tv_carrier_phone)
    TextView tv_carrier_phone;
    @BindView(R.id.lv_logistic_detail)
    RefreshListView lv_logistic_detail;

    private CustomDialog mDailyDialog;
    private OrderLogisticAdapter mOrderLogisticAdapter;
    private OrderLogisticBean.DataEntityX data;
    private ArrayList<OrderLogisticBean.DataEntityX.DataEntity> dataList = new ArrayList<>();
    private String orderId;
    private AppPermissions rxPermissions;
    private AsyncTask<Void, Void, Void> mAsyncTask;


    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_logistic_detail;
    }

    @Override
    protected void onInitView() {
        orderId = originIntent.getStringExtra("orderId");
        LogUtils.d(TAG, "orderId : " + orderId);
        initView();
        showLoadingDialog();
        setListener();
    }

    private void setListener() {
        rl_left_top_bar.setOnClickListener(this);
        tv_order_num.setOnClickListener(this);
        tv_carrier_phone.setOnClickListener(this);
    }

    private void initView() {
        ButterKnife.bind(this);
        rxPermissions = new AppPermissions(this);
        mOrderLogisticAdapter = new OrderLogisticAdapter(this);
        lv_logistic_detail.setDivider(null);
        lv_logistic_detail.setAdapter(mOrderLogisticAdapter);
        lv_logistic_detail.setOnRefreshListener(this);

    }

    @Override
    protected void onLoadData() {
        loadMyOrderLogistic();
    }

    private void loadMyOrderLogistic() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLoadingDialog();
            }
        });
        Subscription subscribe = RetrofitManager.getInstance().getService().getOrderLogistic(orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<OrderLogisticBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onNext(OrderLogisticBean orderLogisticBean) {
                        dismissLoadingDialog();
                        if (orderLogisticBean.code == 1000000) {
                            data = orderLogisticBean.data;
                            dataList.clear();
                            refreshTopUI();
                            if (data != null && data.data != null) {
                                dataList.addAll(data.data);
                            } else {
                                if (orderLogisticBean != null && orderLogisticBean.message != null) {
                                    Toast.makeText(LogisticDetailActivity.this, orderLogisticBean.message, Toast.LENGTH_SHORT).show();
                                }
                            }
                            mOrderLogisticAdapter.setData(dataList);
                        }

                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }

    private void refreshTopUI() {
        if (data != null) {
            if (!TextUtils.isEmpty(data.nu)) {
                tv_order_num.setVisibility(View.VISIBLE);

                tv_order_num.setText("运单号：" + data.nu);
            } else {
                tv_order_num.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(data.comName)) {
                tv_carrier.setText("国内承运人：" + data.comName);
            }
            if (!TextUtils.isEmpty(data.comTel)) {
                tv_carrier_call.setVisibility(View.VISIBLE);
                tv_carrier_phone.setText(data.comTel);
            }
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_top_bar:
                LogisticDetailActivity.this.finish();
                break;
            case R.id.tv_order_num:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (data.nu != null) {
                    cmb.setText(data.nu);
                    CommonUtils.showToast("运单号复制成功");
                }
                break;
            case R.id.tv_carrier_phone:
//                DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        tel();
//                    }
//                }, null, "400—817-6111", "取消", "拨打");
                break;
        }

    }

//    private void tel() {
//        rxPermissions.request(Manifest.permission.CALL_PHONE)
//                .subscribe(new Subscriber<Boolean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        CommonUtils.showToast("获取打电话权限失败");
//                    }
//
//                    @Override
//                    public void onNext(Boolean aBoolean) {
//                        if (aBoolean) {
//                            String phone = "400-817-6111";
//                            if (phone.contains("-")) {
//                                phone = phone.replace("-", "");
//                            }
//                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
//                            startActivity(intent);
//                        } else {
//                            CommonUtils.showToast("没有打电话权限");
//                        }
//                    }
//                });
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void newInstance(Activity context,String orderId) {
        Intent intent = new Intent(context, LogisticDetailActivity.class);
        intent.putExtra("orderId",orderId);
        context.startActivityForResult(intent,10002);
    }

    @Override
    public void onDownPullRefresh() {
        mAsyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (NetUtil.isConnected()) {
                    SystemClock.sleep(1000);
                    dataList.clear();
                    loadMyOrderLogistic();
                } else {
                    SystemClock.sleep(1000);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (lv_logistic_detail != null) {
                    lv_logistic_detail.hideHeaderView();
                }
//                if (!isConnected) {
//                    CommonUtils.showToast("网络错误，请检查您的网络");
//                }
            }
        };
        mAsyncTask.execute(new Void[] { });
    }

    @Override
    public void onLoadingMore() {
        CommonUtils.showToast("没有更多内容了");
        lv_logistic_detail.hideFooterView();
    }

    private void showLoadingDialog() {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomDialog(this, R.layout.dialog_type2);
            TextView tv = (TextView) mDailyDialog.mContentView.findViewById(R.id.tv_notify_message);
            tv.setText("正在加载");
        }
        mDailyDialog.show();
    }

    public void dismissLoadingDialog() {
        try {
            if (mDailyDialog != null) {
                mDailyDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("AlertDialog  Exception:", e.getMessage() + "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

}
