package com.huatu.handheld_huatu.business.me.order;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;

import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.me.adapter.OrderDetailCourseAdapter;
import com.huatu.handheld_huatu.business.me.bean.LastLogisticData;
import com.huatu.handheld_huatu.business.me.bean.LastLogisticResult;
import com.huatu.handheld_huatu.business.me.bean.MyAccountBean;
import com.huatu.handheld_huatu.business.me.bean.OrderDetailData;
import com.huatu.handheld_huatu.business.me.fragment.LevelPrivilegeExplainFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.ConfirmPaymentFragment2;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.WXUtils;
import com.huatu.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 课程订单详情和订单支付
 * Created by chq on 2017/9/18.
 */

public class OrderDetailActivity extends XiaoNengHomeActivity implements View.OnClickListener {
    private static final String TAG = "OrderDetailActivity";

    @BindView(R.id.tv_title_title_bar)
    TextView tv_title_title_bar;

    @BindView(R.id.iv_consult)
    ImageView iv_consult;

    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;
    //顶部物流和收货地址
    @BindView(R.id.rl_logistic)
    RelativeLayout rl_logistic;
    @BindView(R.id.rl_group_order_top)
    RelativeLayout rl_group_order_top;
    @BindView(R.id.tv_group_order_status)
    TextView tv_group_order_status;
    @BindView(R.id.tv_group_order_time)
    TextView tv_group_order_time;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_time_remind)
    TextView tv_time_remind;
    @BindView(R.id.tv_logistic_content)
    TextView tv_logistic_content;
    @BindView(R.id.tv_logistic_time)
    TextView tv_logistic_time;
    @BindView(R.id.rl_address)
    RelativeLayout rl_address;
    @BindView(R.id.tv_user)
    TextView tv_user;
    @BindView(R.id.tv_phone)
    TextView tv_phone;
    @BindView(R.id.tv_address)
    TextView tv_address;

    @BindView(R.id.rl_order_course)
    RelativeLayout rl_order_course;
    @BindView(R.id.rlv_course)
    RecyclerView rlv_course;

    //选择支付方式块
    @BindView(R.id.ll_pay)
    LinearLayout ll_pay;
//    //微信支付
//    @BindView(R.id.rl_we_chat_pay)
//    RelativeLayout rl_we_chat_pay;
//    @BindView(R.id.iv_we_chat_pay)
//    ImageView iv_we_chat_pay;
//    //图币支付
//    @BindView(R.id.rl_tubi_pay)
//    RelativeLayout rl_tubi_pay;
//    @BindView(R.id.iv_tubi_pay)
//    ImageView iv_tubi_pay;
//    @BindView(R.id.tv_tubi_count)
//    TextView tv_tubi_count;
//    @BindView(R.id.tv_charge)
//    TextView tv_charge;
//    //支付宝
//    @BindView(R.id.rl_ali_pay)
//    RelativeLayout rl_ali_pay;
//    @BindView(R.id.iv_ali_pay)
//    ImageView iv_ali_pay;

  //价格详情块
    //  商品价格
    @BindView(R.id.rl_course_price)
    RelativeLayout rl_course_price;
    @BindView(R.id.tv_price_v5)
    TextView tv_price_v5;
    //立减
    @BindView(R.id.rl_discount_v5)
    RelativeLayout rl_discount_v5;
    @BindView(R.id.tv_discount_v5)
    TextView tv_discount_v5;
    // 等级优惠
    @BindView(R.id.rl_level_discount)
    RelativeLayout rl_level_discount;
    @BindView(R.id.iv_level)
    ImageView iv_level;
    @BindView(R.id.tv_level_discount)
    TextView tv_level_discount;
    //运费
    @BindView(R.id.rl_freight)
    RelativeLayout rl_freight;
    @BindView(R.id.tv_freight)
    TextView tv_freight;
    //实付or应付
    @BindView(R.id.rl_real_pay)
    RelativeLayout rl_real_pay;
    @BindView(R.id.tv_real_pay_des)
    TextView tv_real_pay_des;
    @BindView(R.id.tv_real_pay)
    TextView tv_real_pay;

    //订单号块
    @BindView(R.id.ll_order_content)
    LinearLayout ll_order_content;
    @BindView(R.id.ll_already_pay)
    LinearLayout ll_already_pay;
    @BindView(R.id.ll_order_num)
    LinearLayout ll_order_num;
    @BindView(R.id.tv_order_num)
    TextView tv_order_num;
    @BindView(R.id.tv_submit_time)
    TextView tv_submit_time;
    @BindView(R.id.tv_pay_time)
    TextView tv_pay_time;
    @BindView(R.id.tv_pay_type)
    TextView tv_pay_type;
    @BindView(R.id.tv_send_time)
    TextView tv_send_time;
    @BindView(R.id.tv_consult)
    TextView tv_consult;

    //取消或支付
    @BindView(R.id.rl_bottom)
    LinearLayout rl_bottom;
    @BindView(R.id.tv_pay)
    TextView tv_pay;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.rl_group_order_bottom)
    RelativeLayout rl_group_order_bottom;
    @BindView(R.id.tv_to_weChat)
    TextView tv_to_weChat;
    @BindView(R.id.state_layout)
    LinearLayout mStateLayout;


    private OrderDetailCourseAdapter mCourseAdapter;
    private String orderNo;
    private String orderId;
    private int isCollage;
    private int time = 0;
    private LastLogisticResult dataList;
    private ArrayList<OrderDetailData.Course> coursesData = new ArrayList<>();
    private OrderDetailData mData;
//    private int payType=1;
    private long mUserGold;
//    private long price=0l;
//    private IWXAPI wxApi;
//    private IWXAPI api;
//    private PayUtils mPayUtils;
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public boolean onEventUpdate(PayMessageEvent event) {
//        if (event == null) {
//            return false;
//        }
//        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event);
//        if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_SUCCESS) {
//            ToastUtils.showEssayToast("支付成功！");
//            paySuccess();
//        } else {
//            ToastUtils.showEssayToast("支付失败！");
//        }
//        return true;
//    }



//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public boolean onEventUpdate2(PayResultEvent event) {
//        if (event == null) {
//            return false;
//        }
//        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event);
//        if (event.type == PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK) {
//            if (event.params == PayUtils.PAY_RESULT_SUCC) {
////                ToastUtils.showEssayToast("支付成功！");
//                paySuccess();
//            } else {
////                ToastUtils.showEssayToast("支付失败！");
//            }
//        }
//        return true;
//    }


//    private void paySuccess() {
//        TimeUtils.delayTask(new Runnable() {
//            @Override
//            public void run() {
//                UIJumpHelper.startStudyPage(OrderDetailActivity.this);
//                EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.COURSE_BUY_SUCCESS));
//                OrderDetailActivity.this.setResult(Activity.RESULT_OK);
//                OrderDetailActivity.this.finish();
//            }
//        }, 1000);
//
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mPayUtils = new PayUtils();
        if(!CommonUtils.isPadv2(UniApplicationContext.getContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
//        ButterKnife.bind(this);
//        api = WXAPIFactory.createWXAPI(this.getApplicationContext(), PayUtils.APP_ID);
//        api.registerApp(PayUtils.APP_ID);
        onInitView();
        setListener();
        onLoadData();
        initCourseListView();


    }

    private void initCourseListView() {
        mCourseAdapter = new OrderDetailCourseAdapter(this);
        rlv_course.setLayoutManager(new LinearLayoutManager(this));
        rlv_course.setNestedScrollingEnabled(false);
        rlv_course.setAdapter(mCourseAdapter);
    }

    @Override
    public int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    private void setListener() {
        rl_left_top_bar.setOnClickListener(this);
        iv_consult.setOnClickListener(this);
        tv_pay.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rl_logistic.setOnClickListener(this);
        rl_address.setOnClickListener(this);
        tv_order_num.setOnClickListener(this);
        tv_consult.setOnClickListener(this);
        tv_to_weChat.setOnClickListener(this);
        iv_level.setOnClickListener(this);
//        rl_ali_pay.setOnClickListener(this);
//        rl_tubi_pay.setOnClickListener(this);
//        rl_we_chat_pay.setOnClickListener(this);
//        tv_charge.setOnClickListener(this);
    }


    protected void onInitView() {
        dataList = new LastLogisticResult();
        Intent intent = getIntent();
        orderNo = intent.getStringExtra("orderNum");
        orderId = intent.getStringExtra("orderId");
        isCollage = intent.getIntExtra("isCollage", -1);

    }


    protected void onLoadData() {
        if (isCollage == 0 || isCollage == 2) {
            //普通订单
            loadGeneralOrderData();
        } else {
            //拼团的订单
            loadGroupOrderData();
        }
    }

    private void loadGroupOrderData() {
        showProgress();
        ServiceProvider.getGroupOrderDetail(compositeSubscription, orderId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgess();
                if (model != null) {
                    mData = (OrderDetailData) model.data;
                }
                if (mData != null) {
                    refreshGroupOrderUI();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgess();
            }
        });
    }

    private void refreshGroupOrderUI() {
        refreshTopUI();
        if (mData.classInfo.get(0).price != null) {
            //课程总原价
            rl_course_price.setVisibility(View.VISIBLE);
            tv_price_v5.setText("¥ " + mData.classInfo.get(0).price);
        } else {
            rl_course_price.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.calcDisCount)) {
            //立减金额
            rl_discount_v5.setVisibility(View.VISIBLE);
            tv_discount_v5.setText("-¥ " + mData.calcDisCount);
        } else {
            rl_discount_v5.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.levelDiscount)) {
            //等级优惠
             float levelDis=StringUtils.parseFloat(mData.levelDiscount);
             if(levelDis>0){
                rl_level_discount.setVisibility(View.VISIBLE);
                tv_level_discount.setText("-¥ " + mData.levelDiscount);
            }
            else {
                rl_level_discount.setVisibility(View.GONE);
            }

        } else {
            rl_level_discount.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.price)) {
            //实付款
            rl_real_pay.setVisibility(View.VISIBLE);
            if (isCollage == 1) {
                tv_real_pay_des.setText("实付款");
            } else {
                tv_real_pay_des.setText("实付款");
            }
            tv_real_pay.setText("¥ " + mData.price);
        } else {
            rl_real_pay.setVisibility(View.GONE);
        }
        //订单号块
        ll_order_content.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mData.orderNum)) {
            ll_order_num.setVisibility(View.VISIBLE);
            tv_order_num.setText(mData.orderNum);
        } else {
            ll_order_num.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.addTime)) {
            tv_submit_time.setText("下单时间：" + mData.addTime);
        } else {
            tv_submit_time.setVisibility(View.GONE);
        }

        ll_already_pay.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mData.payTime)) {
            tv_pay_time.setText("付款时间：" + mData.payTime);
        } else {
            tv_pay_time.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.paymentDesc)) {
            tv_pay_type.setText("支付方式：" + mData.paymentDesc);
        } else {
            tv_pay_type.setVisibility(View.GONE);
        }
        if (isCollage == 1) {
            //拼团中的订单
            if (!TextUtils.isEmpty(mData.address)) {
                //有实物，需要发货
                if (mData.hasLogistics) {
                    if (!TextUtils.isEmpty(mData.logisticsTime)) {
                        tv_send_time.setText("发货时间：" + mData.logisticsTime);
                    }
                } else {
                    tv_send_time.setText("发货时间：待发货");
                }
                if (!TextUtils.isEmpty(mData.logisticsCost)) {
                    //运费
                    rl_freight.setVisibility(View.VISIBLE);
                    tv_freight.setText("¥ " + mData.logisticsCost);
                } else {
                    rl_freight.setVisibility(View.GONE);
                }
            } else {
                tv_send_time.setVisibility(View.GONE);
                rl_freight.setVisibility(View.GONE);
            }
            rl_group_order_bottom.setVisibility(View.VISIBLE);
        } else {
            //拼团失败退款的订单
            tv_send_time.setVisibility(View.GONE);
            rl_group_order_bottom.setVisibility(View.GONE);
        }
        //收货地址块
        if (!TextUtils.isEmpty(mData.address)) {
            rl_address.setVisibility(View.VISIBLE);
            tv_address.setText(mData.address);
            if (mData.name != null) {
                tv_user.setText(mData.name);
            }
            if (mData.mobile != null) {
                tv_phone.setText(mData.mobile);
            }
        } else {
            rl_address.setVisibility(View.GONE);
        }
        //课程
        coursesData.clear();
        coursesData.addAll(mData.classInfo);
        mCourseAdapter.setData(coursesData, mData.protocolUrl, isCollage, orderId);
    }

    private void loadGeneralOrderData() {
        showProgress();
        ServiceProvider.getOrderDetail(compositeSubscription, orderId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model != null) {
                    mData = (OrderDetailData) model.data;
                }
                if (mData != null) {
                    refreshUI();
                }
                hideProgess();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgess();
            }
        });
    }
    ConfirmPaymentFragment2 confirmPaymentFragment2;
    private void refreshUI() {
//        payStatus	支付状态,0-未支付, 1-已支付, 2-已取消, 3-待确认也是未付款的状态
        ll_order_content.setVisibility(View.VISIBLE);
        if (mData.classTotalPrice != null) {
            //课程总原价
            rl_course_price.setVisibility(View.VISIBLE);
            tv_price_v5.setText("¥ " + mData.classTotalPrice);
        } else {
            rl_course_price.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.calcDisCount)&& Float.parseFloat(mData.calcDisCount) > 0) {
            //立减
            rl_discount_v5.setVisibility(View.VISIBLE);
            tv_discount_v5.setText("-¥ " + mData.calcDisCount);
        } else {
            rl_discount_v5.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.levelDiscount) && Float.parseFloat(mData.levelDiscount) > 0) {
            //等级优惠
             float levelDis=StringUtils.parseFloat(mData.levelDiscount);
            if(levelDis>0){
                rl_level_discount.setVisibility(View.VISIBLE);
                tv_level_discount.setText("-¥ " + mData.levelDiscount);
            }
            else {
                rl_level_discount.setVisibility(View.GONE);
            }
        } else {
            rl_level_discount.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mData.price)) {
            //实付款
            rl_real_pay.setVisibility(View.VISIBLE);
            if (mData.payStatus == 1) {
                tv_real_pay_des.setText("实付款");
            } else {
                tv_real_pay_des.setText("应付款");
            }
            tv_real_pay.setText("¥ " + mData.price);
        } else {
            rl_real_pay.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.orderNum)) {
            ll_order_num.setVisibility(View.VISIBLE);
            tv_order_num.setText(mData.orderNum);
        } else {
            ll_order_num.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mData.addTime)) {
            tv_submit_time.setText("下单时间：" + mData.addTime);
        } else {
            tv_submit_time.setVisibility(View.GONE);

        }
        if (mData.payStatus == 1) {
            ll_already_pay.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mData.payTime)) {
                tv_pay_time.setText("付款时间：" + mData.payTime);
            } else {
                tv_pay_time.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mData.paymentDesc)) {
                tv_pay_type.setText("支付方式：" + mData.paymentDesc);
            } else {
                tv_pay_type.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mData.address)) {
                //有实物，需要发货
                if (mData.hasLogistics) {
                    if (!TextUtils.isEmpty(mData.logisticsTime)) {
                        tv_send_time.setText("发货时间：" + mData.logisticsTime);
                    }
                } else {
                    tv_send_time.setText("发货时间：待发货");
                }
                if (!TextUtils.isEmpty(mData.logisticsCost)) {
                    //运费
                    rl_freight.setVisibility(View.VISIBLE);
                    tv_freight.setText("¥ " + mData.logisticsCost);
                } else {
                    rl_freight.setVisibility(View.GONE);
                }
            } else {
                //没有实物，不需要发货，不显示发货时间
                tv_send_time.setVisibility(View.GONE);
                rl_freight.setVisibility(View.GONE);
//                if (!TextUtils.isEmpty(mData.payTime)){
//                    tv_send_time.setText("发货时间：" + mData.payTime);
//                }
            }
        } else {
            ll_already_pay.setVisibility(View.GONE);
        }
        if (mData.payStatus == 0 || mData.payStatus == 3) {
            //未支付
            rl_bottom.setVisibility(View.VISIBLE);
            ll_pay.setVisibility(View.VISIBLE);
            getUserTuBi();
        } else {
            rl_bottom.setVisibility(View.GONE);
            ll_pay.setVisibility(View.GONE);

        }

        if (mData.hasLogistics) {
            getLastLogistic();
        } else {
            refreshTopUI();
        }
        if (!TextUtils.isEmpty(mData.address)) {
            rl_address.setVisibility(View.VISIBLE);
            tv_address.setText(mData.address);
            if (mData.name != null) {
                tv_user.setText(mData.name);
            }
            if (mData.mobile != null) {
                tv_phone.setText(mData.mobile);
            }
        } else {
            rl_address.setVisibility(View.GONE);
        }
        coursesData.clear();
        coursesData.addAll(mData.classInfo);
        mCourseAdapter.setData(coursesData, mData.protocolUrl, isCollage, mData.collageOrderId);
    }

    private void getUserTuBi() {
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
                            refreshPayUI();
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

    private void refreshPayUI() {
//         price= (long) ((StringUtils.parseDouble(mData.price))*100);
        confirmPaymentFragment2 = new ConfirmPaymentFragment2();
        Bundle arg = new Bundle();
//        actualPrice = Method.parseFloat(payInfo.MoneySum);
        arg.putString("course_id", orderId);
        arg.putString("pay_number", mData.price+"");
        arg.putLong("xxbRemainder", mUserGold);
        confirmPaymentFragment2.setArguments(arg);
        getSupportFragmentManager().beginTransaction().replace(ll_pay.getId(), confirmPaymentFragment2).commitNow();
//        if (mUserGold<price){
//            //不够支付
//            tv_tubi_count.setText("-"+price+"图币，图币不足");
//            tv_charge.setVisibility(View.VISIBLE);
//        }else {
//            tv_tubi_count.setText("-"+price+"图币");
//            tv_charge.setVisibility(View.GONE);
//        }
    }

    private void getLastLogistic() {
        ServiceProvider.getLastLogistics(compositeSubscription, orderId, new NetResponse() {

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                if (model != null) {
                    LastLogisticData datas = (LastLogisticData) model.data;
                    dataList = datas.data;
                    refreshTopUI();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
//                refreshTopUI();
            }
        });
    }

//    protected Subscription timeSubscription;

//    protected void startCountDownTask() {
//        if (timeSubscription != null) {
//            timeSubscription.unsubscribe();
//            compositeSubscription.remove(timeSubscription);
//        }
//        timeSubscription = Observable.interval(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
//                    @Override
//                    public void call(Long aLong) {
//                        refreshGroupTopUI();
//                    }
//                });
//        compositeSubscription.add(timeSubscription);
//    }
//
//    private void refreshGroupTopUI() {
//        if (mData.group.surplusSecond > 0) {
//            mData.group.surplusSecond -= 1;
//            time++;
//
//            if (time % 60 == 0) {
//                time = 0;
//                tv_group_order_time.setText("还剩" + DateUtils.formatMyTime(mData.group.surplusSecond) + "拼团结束");
//            } else if (mData.group.surplusSecond == 0) {
//                isCollage = 3;
//                loadGroupOrderData();
//                if (timeSubscription != null) {
//                    timeSubscription.unsubscribe();
//                    compositeSubscription.remove(timeSubscription);
//                }
//            }
//
//        }
//    }

    private void refreshTopUI() {
        //  isCollage 0-不是拼团订单, 1-拼团中订单, 2-拼团成功订单 3-拼团失败退款的订单
        if (isCollage == 1) {
            rl_group_order_top.setVisibility(View.VISIBLE);
            tv_group_order_status.setText("拼团中，差" + mData.group.surplusNumber + "人");
            tv_group_order_time.setText("还剩" + DateUtils.formatMyTime(mData.group.surplusSecond) + "拼团结束");
//            startCountDownTask();
        } else if (isCollage == 3) {
            rl_group_order_top.setVisibility(View.VISIBLE);
            if (mData.status.equals("6")){
                tv_group_order_status.setText("退款中");
            }else {
                tv_group_order_status.setText("退款成功");
            }

            if (!TextUtils.isEmpty(mData.statusAt)) {
                tv_group_order_time.setText(mData.statusAt);
            } else {
                tv_group_order_time.setText("");
            }
        } else {
//        payStatus	支付状态,0-未支付, 1-已支付, 2-已取消, 3-待确认也是未付款的状态
            if (mData.statusDesc != null) {
                tv_status.setText(mData.statusDesc);
            }
            if (mData.payStatus == 0 || mData.payStatus == 3) {
                tv_time_remind.setVisibility(View.VISIBLE);
                tv_logistic_content.setVisibility(View.GONE);
                tv_logistic_time.setVisibility(View.GONE);
                if (mData.countDown > 0) {
                    tv_time_remind.setText("剩" + DateUtils.formatMyTime(mData.countDown) + "自动关闭");
                }
            } else if (mData.payStatus == 1) {
                if (mData.hasLogistics) {
                    if (dataList != null) {
                        tv_logistic_content.setVisibility(View.VISIBLE);
                        tv_logistic_time.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(dataList.context)) {
                            tv_logistic_content.setText(dataList.context);
                        }
                        if (!TextUtils.isEmpty(dataList.ftime)) {
                            tv_logistic_time.setText(dataList.ftime);
                        }
                    } else {
                        tv_status.setGravity(Gravity.CENTER);
                        tv_logistic_content.setVisibility(View.GONE);
                        tv_logistic_time.setVisibility(View.GONE);

                    }
                } else {
                    tv_status.setGravity(Gravity.CENTER);
                    tv_logistic_content.setVisibility(View.GONE);
                    tv_logistic_time.setVisibility(View.GONE);

                }
            } else if (mData.payStatus == 2) {
                tv_status.setGravity(Gravity.CENTER);
                tv_logistic_content.setVisibility(View.GONE);
                tv_logistic_time.setVisibility(View.GONE);

            }
        }
    }


    @Override
    public void customChatParam() {
        if (mData.payStatus == 1) {
            customGroupId = TUTU_ROBOT_GROUPID;
        } else {
            customGroupId = HUAHUA_ROBOT_GROUPID;
        }
        mTitleName = "订单详情页";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_top_bar:
                setResult(RESULT_OK);
                OrderDetailActivity.this.finish();
                break;
            case R.id.rl_logistic:
                //查看物流
                if (mData.hasLogistics) {
                    if (mData.orderId != null) {
                        LogisticDetailActivity.newInstance(OrderDetailActivity.this, mData.orderId);
                    }
                }

                break;
                case R.id.rl_address:
                     //修改地址 todo
                   break;
            case R.id.tv_pay:
                if (mData == null || confirmPaymentFragment2 == null) {
                    return;
                }
                if (confirmPaymentFragment2.getPayType() < 0) {
                    ToastUtils.showShort("请选择支付方式");
                    return;
                }
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                if (confirmPaymentFragment2.isCanNotPayTubi()) {
                    ToastUtils.showShort("图币余额不足，请充值");
                    return;
                }

                PayInfo payInfo = new PayInfo();
                payInfo.OrderNum = mData.orderNum;
                payInfo.orderId = StringUtils.parseLong(mData.orderId);
                payInfo.MoneySum = String.valueOf(mData.price);
                payInfo.xxb = mData.goldPay;
                payOrder(payInfo);
                break;
            case R.id.tv_cancel:
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder();
                    }
                }, null, "确认取消", "再想想", "确定");
                break;
            case R.id.tv_order_num:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (mData.orderNum != null) {
                    cmb.setText(mData.orderNum);
                    CommonUtils.showToast("订单编号复制成功");
                }
                break;
            case R.id.tv_consult:
                startChat();
                break;
            case R.id.iv_consult:
                startChat();
                break;
            case R.id.tv_to_weChat:
                // 跳小程序
                WXUtils.appOrderToWxApp(this, orderId);
                break;
            case R.id.iv_level:
                // 跳等级特权说明页
                if (!NetUtil.isConnected()){
                    ToastUtils.showShort("网络错误，请检查网络");
                    return;
                }
                BaseFrgContainerActivity.newInstance(this,
                        LevelPrivilegeExplainFragment.class.getName(),
                        LevelPrivilegeExplainFragment.getArgs());
                break;
//            case R.id.rl_ali_pay:
//                payType = 32;
//                iv_ali_pay.setImageResource(R.drawable.icon_checked);
//                iv_we_chat_pay.setImageResource(R.mipmap.mip_unselected);
//                iv_tubi_pay.setImageResource(R.mipmap.mip_unselected);
//                break;
//            case R.id.rl_we_chat_pay:
//                payType = 33;
//                iv_we_chat_pay.setImageResource(R.drawable.icon_checked);
//                iv_ali_pay.setImageResource(R.mipmap.mip_unselected);
//                iv_tubi_pay.setImageResource(R.mipmap.mip_unselected);
//                break;
//            case R.id.rl_tubi_pay:
//                payType = 1;
//                iv_tubi_pay.setImageResource(R.drawable.icon_checked);
//                iv_ali_pay.setImageResource(R.mipmap.mip_unselected);
//                iv_we_chat_pay.setImageResource(R.mipmap.mip_unselected);
//                break;
//            case R.id.tv_charge:
//                //去充值图币
//                if (!NetUtil.isConnected()){
//                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
//                    return;
//                }
//                RechargeNowActivity.newInstance(OrderDetailActivity.this,10003);
//                break;
        }
    }

    private void payOrder(PayInfo payInfo) {
        showProgress();
            ServiceProvider.payCourseOrder(compositeSubscription, payInfo.orderId, confirmPaymentFragment2.getPayType(),2, new NetResponse() {
                @Override
                public void onError(final Throwable e) {
                    hideProgess();
                    try { ToastUtils.showMessage(e.getMessage()); }catch (Exception ee){ }
                }

                @Override
                public void onSuccess(BaseResponseModel model) {
                    super.onSuccess(model);
                    hideProgess();
                    PayInfo  payInfoFromNet = (PayInfo) model.data;
                    payInfoFromNet.title = coursesData.get(0).title;
                    confirmPaymentFragment2.setPayInfo(payInfoFromNet);
                    confirmPaymentFragment2.onClickConfirm();
                }
            });
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
//            finish();
        }else if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
            getUserTuBi();
        }
    }

    private void cancelOrder() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress();
            }
        });
        ServiceProvider.cancelOrder(compositeSubscription, orderId, new NetResponse() {

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                hideProgess();
                if (model.code == 1000000) {
                    setResult(RESULT_OK);
                    CommonUtils.showToast("订单取消成功");
                    tv_cancel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 800);

                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgess();
                ToastUtils.showEssayToast("取消失败了，请稍后重试");
            }
        });
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_order_detail;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }
}
