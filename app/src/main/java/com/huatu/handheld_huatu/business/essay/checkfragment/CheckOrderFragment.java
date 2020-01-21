package com.huatu.handheld_huatu.business.essay.checkfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.adapter.GoodsAdapter;
import com.huatu.handheld_huatu.business.essay.adapter.PersonGoodsAdapter;
import com.huatu.handheld_huatu.business.essay.cusview.TipTextView;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.me.account.RechargeNowActivity;
import com.huatu.handheld_huatu.business.me.bean.MyAccountBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.PayResultEvent;
import com.huatu.handheld_huatu.event.PayMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodData;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodOrderBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayPayInfo;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PayUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 */
public class CheckOrderFragment extends BaseFragment {

    private static final String TAG = "CheckOrderFragment";

    @BindView(R.id.fragment_title_bar)
    TopActionBar topActionBar;
    @BindView(R.id.tip_one)
    TipTextView tipOne;
    @BindView(R.id.rl_good)
    RecyclerView rlGood;
    @BindView(R.id.rv_person_check)
    RecyclerView rv_person_check;
    @BindView(R.id.confirm_payment_zfb_img)
    ImageView ivZfb;
    @BindView(R.id.confirm_payment_zfb_layout)
    RelativeLayout layoutZfb;
    @BindView(R.id.rl_ai_check_count)
    RelativeLayout rl_ai_check_count;
    @BindView(R.id.rl_person_check_order)
    RelativeLayout rl_person_check_order;
    @BindView(R.id.confirm_payment_we_chat_img)
    ImageView ivWeChat;
    @BindView(R.id.confirm_payment_we_chat_layout)
    RelativeLayout layoutWeChat;
    @BindView(R.id.confirm_payment_xxb_img)
    ImageView ivXxb;
    @BindView(R.id.confirm_payment_xxb_layout)
    RelativeLayout layoutXxb;
    @BindView(R.id.confirm_order_pay_number_two)
    TextView confirmOrderPayNumberTwo;
    @BindView(R.id.confirm_order_confirm_btn)
    TextView confirmOrderConfirmBtn;

    private int typeGoodSingle = -1;
    private int typeGoodMult = -1;
    private int typeGoodArgue = -1;
    private String titleView;
    private boolean isSingle;
    private boolean isStartToCheckDetail;
    private Bundle extraArgs;
    private int selPos;
    private PopupWindow popupWindow;
    private int payType = 2;
    private long mUserGold;
    private PayUtils mPayUtils;
    private IWXAPI api;
    private  int single=0;
    private int multi=0;
    private int argue=0;
    private String orderId;
    private float orderMoney=0;
    private String pageFrom;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(PayMessageEvent event) {
        if (event == null) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event);
        if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_SUCCESS) {
            ToastUtils.showEssayToast("支付成功！");
            paySuccess(payType);
        } else {
//            ToastUtils.showEssayToast("支付失败！");
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate2(PayResultEvent event) {
        if (event == null) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event);
        if (event.type == PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK) {
            if (event.params == PayUtils.PAY_RESULT_SUCC) {
                ToastUtils.showEssayToast("支付成功！");
                paySuccess(payType);
            } else {
//                ToastUtils.showEssayToast("支付失败！");
            }
        }
        return true;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.check_order_flayout;
    }

    @Override
    protected void onInitView() {
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        mPayUtils = new PayUtils();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        api = WXAPIFactory.createWXAPI(mActivity.getApplicationContext(), PayUtils.APP_ID);
        api.registerApp(PayUtils.APP_ID);
        if (args != null) {
            requestType = args.getInt("request_type");
            extraArgs = args.getBundle("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
            pageFrom=args.getString("page_source");
            if (pageFrom==null){
                pageFrom="试题列表页";
            }
        }else{
            pageFrom="试题列表页";
        }
        initTitleBar();
//        int max = EssayCheckDataCache.getInstance().maxCorrectTimes;

//        if (max == 9999) {
            tipOne.setVisibility(View.GONE);
//        } else {
////            if (max <= 0) {
////                tipOne.setVisibility(View.GONE);
////            }else {
////                tipOne.setVisibility(View.VISIBLE);
////                tipOne.setTag("CheckOrderFragment_tip1");
////                tipOne.setText("同一单题或套题仅可批改" + max + "次");
////            }
////        }
        rlGood.setLayoutManager(new LinearLayoutManager(getContext()));
        rlGood.setNestedScrollingEnabled(false);

        rv_person_check.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_person_check.setNestedScrollingEnabled(false);
    }

    private void initTitleBar() {
        topActionBar.setTitle("申论批改订单");
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
//        topActionBar.showButtonImage(R.mipmap.download_paper_icon, TopActionBar.RIGHT_AREA);
//        topActionBar.showButtonImage(-1, TopActionBar.RIGHT_AREA);
        topActionBar.setDividerShow(true);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                mActivity.onBackPressed();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                Toast.makeText(mActivity, "download", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        getUserCoint();
        getGoodsListNet();
    }

    private void getUserCoint() {
        Subscription subscribe = RetrofitManager.getInstance().getService().getMyAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyAccountBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("网络不稳定，请检查您的网络");

                    }

                    @Override
                    public void onNext(MyAccountBean myAccountBean) {
                        if (myAccountBean.code == 1000000) {
                            MyAccountBean.MyAccountData mMyAccount = myAccountBean.data;
                            mUserGold = mMyAccount.userCountres.UserMoney;
                        } else {
                            if (myAccountBean.message != null) {
                                CommonUtils.showToast(myAccountBean.message);
                            }
                        }
                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }
    }


    public void getGoodsListNet() {
        if (mActivity != null) {
            mActivity.showProgress();
        }
        ServiceProvider.getCheckGoodsList(compositeSubscription, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                if (mActivity != null) {
                    mActivity.hideProgress();
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (mActivity != null) {
                    mActivity.hideProgress();
                }
                listGoods.clear();
                listPersonGoods.clear();
                if (model != null && model.data != null) {
                    CheckGoodData data= (CheckGoodData) model.data;
                    listGoods.addAll(data.machineCorrect);
                    listPersonGoods.addAll(data.manualCorrect);
                    refreshGoodListv();
                }
            }
        });
    }
    public List deepCopy(List src){
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
            out.writeObject(src);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = null;
        List dest =null;
        try {
            in = new ObjectInputStream(byteIn);
            dest = (List)in.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }
    ArrayList<CheckGoodBean> listGoods = new ArrayList<>();
    ArrayList<CheckGoodBean> listPersonGoods = new ArrayList<>();

    public void refreshGoodListv() {
        if (listPersonGoods.size()==0){
            rl_person_check_order.setVisibility(View.GONE);
        }

        if (listGoods.size()==0){
            rl_ai_check_count.setVisibility(View.GONE);
        }
        GoodsAdapter goodsAdapter = new GoodsAdapter(getContext(), listGoods, this);
        PersonGoodsAdapter personGoodsAdapter = new PersonGoodsAdapter(getContext(), listPersonGoods, this);
        rlGood.setAdapter(goodsAdapter);
        rv_person_check.setAdapter(personGoodsAdapter);

    }

    private String getYuan(int price) {
        int y = price / 100;
        int f = price % 100;
        if (f <10) {
            return y + ".0" + f;
        } else {
            return y + "." + f;
        }
    }

    int allPrice = 0;

    public void refreshAllPriceCount() {
        int totalPrice = 0;
        int totalPersonPrice = 0;
        for (CheckGoodBean var : listGoods) {
            if (var == null) {
                return;
            }
            if (var.isSelected)
                totalPrice += (var.userSetCount * var.activityPrice);
        }
        for (CheckGoodBean var : listPersonGoods) {
            if (var == null) {
                return;
            }
            if (var.isSelected)
                totalPersonPrice += (var.userSetCount * var.activityPrice);
        }
        allPrice = totalPrice+totalPersonPrice;
        confirmOrderPayNumberTwo.setText(getYuan(allPrice) + "");
        if (allPrice==0){
            confirmOrderConfirmBtn.setTextColor(ContextCompat.getColor(mActivity,R.color.blackF4));
            confirmOrderConfirmBtn.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.drawable_order_change_color));
        }else {
            confirmOrderConfirmBtn.setTextColor(ContextCompat.getColor(mActivity,R.color.white));
            confirmOrderConfirmBtn.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.drawable_order_change_red));
        }
    }

    @OnClick({R.id.confirm_payment_zfb_layout, R.id.confirm_payment_we_chat_layout, R.id.confirm_payment_xxb_layout,
            R.id.confirm_order_confirm_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm_payment_zfb_layout:
                StudyCourseStatistic.clickStatistic("我的->我的批改","申论批改订单页","支付宝支付");
                payType = 0;
                ivZfb.setImageResource(R.drawable.icon_checked);
                ivWeChat.setImageResource(R.mipmap.mip_unselected);
                ivXxb.setImageResource(R.mipmap.mip_unselected);
                break;
            case R.id.confirm_payment_we_chat_layout:
                StudyCourseStatistic.clickStatistic("我的->我的批改","申论批改订单页","微信支付");
                payType = 1;
                ivWeChat.setImageResource(R.drawable.icon_checked);
                ivZfb.setImageResource(R.mipmap.mip_unselected);
                ivXxb.setImageResource(R.mipmap.mip_unselected);
                break;
            case R.id.confirm_payment_xxb_layout:
                StudyCourseStatistic.clickStatistic("我的->我的批改","申论批改订单页","图币支付");
                payType = 2;
                ivXxb.setImageResource(R.drawable.icon_checked);
                ivZfb.setImageResource(R.mipmap.mip_unselected);
                ivWeChat.setImageResource(R.mipmap.mip_unselected);
                break;
            case R.id.confirm_order_confirm_btn:
                if (allPrice <= 0) {
                    ToastUtils.showEssayToast("请先填写购买数量再支付");
                    return;
                }
                if (payType == -1) {
                    ToastUtils.showEssayToast("请选择支付方式");
                    return;
                }

                if (payType == 2) {
                    single=0;
                    multi=0;
                    argue=0;
                    for(CheckGoodBean var: listGoods){
                        if(var==null){
                            return;
                        }
                        if (var.selectedUserSetCount > 0) {
                            if (var.type == 0) {
                                single+=var.selectedUserSetCount*var.num;
                            }
                            if (var.type == 1) {
                                multi+=var.selectedUserSetCount*var.num;
                            }

                            if (var.type == 2) {
                                argue+=var.selectedUserSetCount*var.num;
                            }
                        }
                    }
                    StudyCourseStatistic.clickEssayCheckBuy(single,multi,argue,"图币支付",allPrice,pageFrom);

                    if (allPrice < mUserGold) {
                        showPopWindow(0);
                    } else {
//                        ToastUtils.showEssayToast("金币余额不足");
                        showPopWindow(1);
                    }
                } else {
                    payOrder();
                }
                break;
        }
    }

    private void showPopWindow(final int type) {
        View inflate = View.inflate(mActivity, R.layout.pop_window_recharge, null);

        TextView tv_need_count = (TextView) inflate.findViewById(R.id.tv_need_count);
        TextView tv_useful_count = (TextView) inflate.findViewById(R.id.tv_useful_count);
        TextView tv_to_recharge = (TextView) inflate.findViewById(R.id.tv_to_recharge);
        ImageView iv_close = (ImageView) inflate.findViewById(R.id.iv_close);

        tv_need_count.setText(allPrice + "");
        tv_useful_count.setText(mUserGold + "");
        if (type == 0) {
            tv_to_recharge.setText("确认支付");
        } else {
            tv_to_recharge.setText("余额不足，立即充值");

        }
        tv_to_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetUtil.isConnected()){
                    ToastUtils.showShort("网络未连接，请检查您的网络设置");
                    return;
                }
                if (type==0){
                    hide();
                    payOrder();
                } else {
                    RechargeNowActivity.newInstance(mActivity, 1314);
                    hide();
                }
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });

        popupWindow = new PopupWindow(inflate);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.showAtLocation(topActionBar, Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.update();

//        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 100);
//        if (Build.VERSION.SDK_INT>= 24) {
//            int[] location = new int[2];
//            inflate.getLocationOnScreen(location);
//            popupWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM, location[0], location[1] + inflate.getHeight()+60);
////            Rect visibleFrame = new Rect();
//            inflate.getGlobalVisibleRect(visibleFrame);
//            int height = inflate.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
//            popupWindow.setHeight(height);
//            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 100);

//            popupWindow.showAsDropDown(inflate, 0, 150);
//        } else {
//            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 100);
//        }
    }


    private void hide() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private void payOrder() {
        if (!NetUtil.isConnected()){
            ToastUtils.showShort("网络未连接，请检查您的网络设置");
            return;
        }
         typeGoodMult=-1;
         typeGoodSingle=-1;
         typeGoodArgue=-1;
        single=0;
        multi=0;
        argue=0;

        CheckGoodOrderBean mvar=new CheckGoodOrderBean();
        for(CheckGoodBean var: listGoods){
            if(var==null){
                return;
            }
            if (var.selectedUserSetCount > 0) {
                CheckGoodOrderBean.GoodOrderBean var2 = new CheckGoodOrderBean.GoodOrderBean();
                var2.goodsId = var.id;
                var2.count = var.selectedUserSetCount;
                mvar.goods.add(var2);
                if (var.type == 0) {
                    typeGoodSingle = 0;
                    single+=var.selectedUserSetCount*var.num;
                }
                if (var.type == 1) {
                    typeGoodMult = 1;
                    multi+=var.selectedUserSetCount*var.num;
                }

                if (var.type == 2) {
                    typeGoodArgue = 2;
                    argue+=var.selectedUserSetCount*var.num;
                }
            }
        } for(CheckGoodBean personVar: listPersonGoods){
            if(personVar==null){
                return;
            }
            if (personVar.selectedUserSetCount > 0) {
                CheckGoodOrderBean.GoodOrderBean personVar2 = new CheckGoodOrderBean.GoodOrderBean();
                personVar2.goodsId = personVar.id;
                personVar2.count = personVar.selectedUserSetCount;
                mvar.goods.add(personVar2);

            }
        }
        mvar.total = allPrice;
        mvar.payType = payType;
        String mType;
        if (payType==0){
            mType="支付宝支付";
        }else if (payType==1){
            mType="微信支付";
        }else {
            mType="图币支付";
        }
//        String totalMon=getYuan(allPrice);
//        if (TextUtils.isEmpty(totalMon)){
//             orderMoney=Float.parseFloat("totalMon");
//        }
        orderMoney=allPrice;
        if (payType!=2){
            StudyCourseStatistic.clickEssayCheckBuy(single,multi,argue,mType,allPrice,pageFrom);
        }
        LogUtils.d(TAG, mvar.toString());
        if (mActivity != null) {
            mActivity.showProgress();
        }
        ServiceProvider.createCheckOrder(compositeSubscription, mvar, new NetResponse() {
            @Override
            public void onError(final Throwable e) {
                ToastUtils.showEssayToast("支付失败");
                typeGoodMult = -1;
                typeGoodSingle = -1;
                typeGoodArgue = -1;
                if (mActivity != null) {
                    mActivity.hideProgress();
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (mActivity != null) {
                    mActivity.hideProgress();
                }
//                mActivity.hideProgress();
                if (model != null && model.data != null) {
                    Log.d(TAG, "model.data:" + model.data);
                    EssayPayInfo var = (EssayPayInfo) model.data;
                    if (var.orderNum!=null){
                        orderId=var.orderNum;
                    }else {
                        orderId="0";
                    }
//                    if (!TextUtils.isEmpty(var.moneySum)){
//                        orderMoney=Float.parseFloat(var.moneySum);
//                    }
                    if (Method.isActivityFinished(mActivity)) {
                        return;
                    }
                    if (payType == 2) {
                        ToastUtils.showEssayToast("支付成功!");
                        paySuccess(payType);
                    } else if (payType == 0) {
                        if (mPayUtils != null) {
                           // mPayUtils.payZFB(mActivity, var.title, var.description, var.moneySum, var.orderNum, var.notifyUrl);
                            mPayUtils.payZFBV2(mActivity,var.orderStr);
                        }
                    } else if (payType == 1) {
                        PayInfo var2 = new PayInfo();
                        var2.appid = var.appId;
                        var2.noncestr = var.nonceStr;
                        var2.packageValue = var.packageValue;
                        var2.partnerid = var.partnerId;
                        var2.prepay_id = var.prepayId;
                        var2.sign = var.sign;
                        var2.timestamp = var.timestamp;
                        if (mPayUtils != null) {
                            mPayUtils.payWX(api, var2, mActivity);
                        }
                    }
                } else {
                    if (payType == 2) {
                        ToastUtils.showShort("支付成功");
                        paySuccess(payType);
                    }
                }
            }
        });
    }


    private void paySuccess(int payType) {
        String mType;
        if (payType==0){
            mType="支付宝支付";
        }else if (payType==1){
            mType="微信支付";
        }else {
            mType="图币支付";
        }
        StudyCourseStatistic.clickEssayCheckBuySucceed(orderId,single,multi,argue,mType,orderMoney,orderMoney);
        TimeUtils.delayTask(new Runnable() {
            @Override
            public void run() {
                EssayExamMessageEvent messageEvent = new
                        EssayExamMessageEvent(EssayExamMessageEvent.EssayExam_net_getCheckCountList);
                EventBus.getDefault().post(messageEvent);
                if (!Method.isActivityFinished(mActivity) && mActivity != null && mActivity.hasWindowFocus()) {
                    mActivity.onBackPressed();
                }
            }
        }, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
        hide();
    }

    public static CheckOrderFragment newInstance(Bundle extra) {
        CheckOrderFragment fragment = new CheckOrderFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (isVisibleToUser) {
            LogUtils.v(TAG, "------可见----");
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } else {
            hide();
            LogUtils.v(TAG, "------不可见----");

        }
    }
}
