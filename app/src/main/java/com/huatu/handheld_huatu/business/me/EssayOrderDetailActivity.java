package com.huatu.handheld_huatu.business.me;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.me.bean.MyEssayOrderData;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.ConfirmPaymentFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.xiaonengsdk.XiaoNengHomeActivity;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.NoScrollListView;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht-ldc on 2018/8/2.
 */

public class EssayOrderDetailActivity extends XiaoNengHomeActivity implements View.OnClickListener {
    @BindView(R.id.tv_title_title_bar)
    TextView tv_title_title_bar;
    @BindView(R.id.iv_consult)
     ImageView iv_consult;

    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;
    //顶部物流和收货地址
    @BindView(R.id.rl_logistic)
    RelativeLayout rl_logistic;
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
    @BindView(R.id.ll_essay_content)
    LinearLayout ll_essay_content;
    @BindView(R.id.rlv_course)
    RecyclerView rlv_course;
    @BindView(R.id.rlv_essay_content)
    NoScrollListView rlv_essay_content;


    @BindView(R.id.rl_course_price)
    RelativeLayout rl_course_price;
    @BindView(R.id.tv_price_v5)
    TextView tv_price_v5;
    @BindView(R.id.rl_discount_v5)
    RelativeLayout rl_discount_v5;
    @BindView(R.id.tv_discount_v5)
    TextView tv_discount_v5;
    @BindView(R.id.rl_real_pay)
    RelativeLayout rl_real_pay;
    @BindView(R.id.tv_real_pay)
    TextView tv_real_pay;
    @BindView(R.id.tv_real_pay_des)
    TextView tvPayDes;

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
    @BindView(R.id.tv_order_title)
    TextView tv_order_title;

    //取消或支付
    @BindView(R.id.rl_bottom)
    LinearLayout rl_bottom;
    @BindView(R.id.tv_pay)
    TextView tv_pay;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;


    private MyEssayOrderData.EssayOrderList mOrderData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        initData();
        refreshUI();
        initListener();
    }

    private void initListener() {
        rl_left_top_bar.setOnClickListener(this);
        tv_pay.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        rl_logistic.setOnClickListener(this);
        tv_order_num.setOnClickListener(this);
        tv_consult.setOnClickListener(this);
        iv_consult.setOnClickListener(this);
    }

    private void refreshUI() {
        rlv_course.setVisibility(View.GONE);
        rl_address.setVisibility(View.GONE);
        rl_discount_v5.setVisibility(View.GONE);
        ll_essay_content.setVisibility(View.VISIBLE);
        if (mOrderData.goodsList!=null&&mOrderData.goodsList.size()!=0){
//            SimpleCommonRVAdapter<MyEssayOrderData.EssayOrderList.GoodList> adapter = new SimpleCommonRVAdapter<MyEssayOrderData.EssayOrderList.GoodList>(this, mOrderData.goodsList, R.layout.essay_order_item_layout) {
//                @Override
//                public void convert(SimpleCommonRVAdapter.SimpleViewHolder holder, MyEssayOrderData.EssayOrderList.GoodList item, int position) {
//                    String content = item.getGoodTitle2();
//                    holder.setText(R.id.tv_order_content, content);
//                    holder.setText(R.id.tv_order_expiry_date, item.getExp());
//                }
//            };
//            rlv_essay_content.setLayoutManager(new LinearLayoutManager(this));
//            rlv_essay_content.setNestedScrollingEnabled(false);
//            rlv_essay_content.setAdapter(adapter);
            rlv_essay_content.setAdapter(new CommonAdapter<MyEssayOrderData.EssayOrderList.GoodList>(this, mOrderData.goodsList, R.layout.essay_order_item_layout) {
                @Override
                public void convert(ViewHolder holder, MyEssayOrderData.EssayOrderList.GoodList item, int position) {
                    String content = item.getGoodTitle2();
                    holder.setText(R.id.tv_order_content, content);
                    holder.setText(R.id.tv_order_expiry_date, item.getExp());
                }
            });
        }
//        订单状态。待付款0,已付款1, 已取消2和4都是取消, 5正在退款，6退款完成，7退款被拒
        //底部
        if (mOrderData.bizStatus==0) {
            rl_bottom.setVisibility(View.VISIBLE);
        }else {
            rl_bottom.setVisibility(View.GONE);
        }
        //顶部
        int stateIconId =  R.mipmap.essay_order_sure_icon;
        if (mOrderData.bizStatus==0){
            stateIconId = R.mipmap.essay_order_waiting_icon;
            tv_status.setText("等待支付");
            tv_logistic_content.setVisibility(View.GONE);
            tv_logistic_time.setVisibility(View.GONE);
            tv_time_remind.setVisibility(View.VISIBLE);
            if (mOrderData.leftTime != 0){
                tv_time_remind.setText("剩余"+ DateUtils.formatMyTime(Math.abs(mOrderData.leftTime))+"自动关闭");
            }
            rl_logistic.setBackgroundResource(R.mipmap.essay_order_unpay_bg);
        }else if (mOrderData.bizStatus==1){
            tv_status.setText("已完成");
            tv_status.setGravity(Gravity.CENTER);
            tv_logistic_content.setVisibility(View.GONE);
            tv_logistic_time.setVisibility(View.GONE);
            rl_logistic.setBackgroundResource(R.mipmap.essay_order_payed_bg);
        }else if(mOrderData.bizStatus == 5){
            stateIconId = R.mipmap.essay_order_waiting_icon;
            tv_status.setText(mOrderData.bizStatusName);
            tv_status.setGravity(Gravity.CENTER);
            tv_logistic_content.setVisibility(View.GONE);
            tv_logistic_time.setVisibility(View.GONE);
            rl_logistic.setBackgroundResource(R.mipmap.essay_order_unpay_bg);
        }else if(mOrderData.bizStatus == 6){
            tv_status.setText("已退款");
            tv_status.setGravity(Gravity.CENTER);
            tv_logistic_content.setVisibility(View.GONE);
            tv_logistic_time.setVisibility(View.GONE);
            rl_logistic.setBackgroundResource(R.mipmap.essay_order_back_bg);
        }else if(mOrderData.bizStatus == 7){
            stateIconId = R.mipmap.essay_order_refused_icon;
            tv_status.setText("退款申请驳回");
            tv_status.setGravity(Gravity.CENTER);
            tv_logistic_content.setVisibility(View.GONE);
            tv_logistic_time.setVisibility(View.GONE);
            rl_logistic.setBackgroundResource(R.mipmap.essay_order_refused_bg);
        }else {
            stateIconId = R.mipmap.essay_order_cancel_icon;
            tv_status.setText("已取消");
            tv_status.setGravity(Gravity.CENTER);
            tv_logistic_content.setVisibility(View.GONE);
            tv_logistic_time.setVisibility(View.GONE);
            rl_logistic.setBackgroundResource(R.mipmap.essay_order_refused_bg);
        }
        tv_status.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, stateIconId), null, null, null);
        ll_order_content.setVisibility(View.VISIBLE);
        if (mOrderData.totalMoney!=0){
            //课程总原价
            rl_course_price.setVisibility(View.VISIBLE);
            tv_price_v5.setText("¥ "+getYuan(mOrderData.totalMoney));
        }else{
            rl_course_price.setVisibility(View.GONE);
        }

        if (mOrderData.bizStatus==0){
            if (mOrderData.totalMoney!=0){
                rl_real_pay.setVisibility(View.VISIBLE);
                tvPayDes.setText("应付款");
                tv_real_pay.setText("¥ "+getYuan(mOrderData.totalMoney));
            }else {
                rl_real_pay.setVisibility(View.GONE);
            }
        }else  if (mOrderData.bizStatus==2||mOrderData.bizStatus==4){
            //实付款
                rl_real_pay.setVisibility(View.VISIBLE);
                tvPayDes.setText("应付款");
                tv_real_pay.setText("¥ "+getYuan(mOrderData.totalMoney));
        }
        else  if (mOrderData.bizStatus==5||mOrderData.bizStatus==6 || mOrderData.bizStatus==7){
            //实退款
            rl_real_pay.setVisibility(View.VISIBLE);
            tvPayDes.setText("实退款");
            tv_real_pay.setText("¥ "+getYuan(mOrderData.totalMoney));
        } else {
            rl_real_pay.setVisibility(View.VISIBLE);
            tvPayDes.setText("实付款");
            tv_real_pay.setText("¥ "+getYuan(mOrderData.realMoney));
        }


        if (mOrderData.orderNumStr!=null){
            ll_order_num.setVisibility(View.VISIBLE);
            tv_order_num.setText(mOrderData.orderNumStr);
        }else {
            ll_order_num.setVisibility(View.GONE);
        }
        if (mOrderData.createTime!=null){
            tv_submit_time.setText("下单时间：" + mOrderData.createTime);
        }
        if (mOrderData.bizStatus==1){
            ll_already_pay.setVisibility(View.VISIBLE);
            if (mOrderData.payTime!=null){
                tv_pay_time.setText("付款时间：" + mOrderData.payTime);
            }
            if (mOrderData.payType==0){
                tv_pay_type.setText("支付方式：支付宝支付");
            }else if (mOrderData.payType==1){
                tv_pay_type.setText("支付方式：微信支付");
            }else {
                tv_pay_type.setText("支付方式：图币支付");

            }
            tv_send_time.setVisibility(View.GONE);
        }else {
            ll_already_pay.setVisibility(View.GONE);
        }
    }

    private void initData() {
        Intent intent = getIntent();
        mOrderData = (MyEssayOrderData.EssayOrderList) intent.getSerializableExtra("DATA");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10001 && resultCode == Activity.RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_top_bar:
                EssayOrderDetailActivity.this.finish();
                break;
            case R.id.tv_pay:
                if (mOrderData == null) {
                    return;
                }
                PayInfo payInfo = new PayInfo();
                payInfo.OrderNum = mOrderData.orderNumStr;
                payInfo.MoneySum = String.valueOf(getYuan(mOrderData.totalMoney));
                payInfo.xxb = 1;
                Bundle arg = new Bundle();
                arg.putString("pay_number", String.valueOf(getYuan(mOrderData.totalMoney)));
                arg.putLong("order_id",mOrderData.id);
                arg.putBoolean("is_essay_order", true);
                arg.putSerializable("pay_info", payInfo);
                BaseFrgContainerActivity.newInstance(this,
                        ConfirmPaymentFragment.class.getName(), arg);
                break;
            case R.id.tv_cancel:
                if (!NetUtil.isConnected()){
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelOrder();

                    }
                },null,"确认取消订单","我再想想","确定");
                break;
            case R.id.tv_order_num:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (mOrderData.orderNumStr != null) {
                    cmb.setText(mOrderData.orderNumStr);
                    CommonUtils.showToast("订单编号复制成功");
                }
                break;
            case R.id.tv_consult:
                startChat();
                break;
            case R.id.iv_consult:
                startChat();
                break;
        }
    }
    private String getYuan(int price) {
        int y=price/100;
        if(price<10) {
            int f = price % 100;
            return y + ".0" + f;
        }else {
            int f = price % 100;
            return y + "." + f;
        }
    }
        @Override
        public void customChatParam () {
            if (mOrderData.bizStatus == 1) {
                customGroupId = TUTU_ROBOT_GROUPID;
            } else {
                customGroupId = HUAHUA_ROBOT_GROUPID;
            }
        }

        @Override
        public int getFragmentContainerId ( int clickId){
            return 0;
        }

        @Override
        public Serializable getDataFromActivity (String tag){
            return null;
        }


    private void cancelOrder() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress();
            }
        });
        ServiceProvider.cancelEssayOrder(compositeSubscription,mOrderData.id,new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                hideProgess();
                if (model.code==1000000){
                    setResult(RESULT_OK);
                    CommonUtils.showToast("订单取消成功");
                    tv_cancel.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                finish();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },800);

                }

            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                hideProgess();
            }
        });

    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_essay_order_detail;
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