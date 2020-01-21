package com.huatu.handheld_huatu.business.me.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.PayResultEvent;
import com.huatu.handheld_huatu.event.PayMessageEvent;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PayUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的账户之立即充值
 * Created by chq on 2017/8/30.
 */
public class RechargeNowActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private static final String TAG="RechargeNowActivity";

    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;
    @BindView(R.id.et_recharge_num)
    EditText et_recharge_num;
    @BindView(R.id.rl_aliPay)
    RelativeLayout rl_aliPay;
    @BindView(R.id.rl_weChatPay)
    RelativeLayout rl_weChatPay;
    private PayUtils mPayUtils;
    private PayInfo var;
    private IWXAPI api;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUIUpdate(PayMessageEvent event) {
        if (event == null) {
            return;
        }
//        if(PayUtils.payAliReqType == PayUtils.PAY_ALI_ACCOUNT_CHARGE) {
//            PayUtils.payAliReqType=-1;
            if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_SUCCESS) {
//                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
                ToastUtils.showMyRewardToast("支付成功！","+"+var.recharge+"图币");
                setResult(Activity.RESULT_OK);
                finish();
            } else if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_FAIL) {
                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
            } else if (event.type == PayMessageEvent.PAY_MESSAGE_TYPE_ALI_8000) {
                Toast.makeText(this, "支付异常", Toast.LENGTH_SHORT).show();
            }
//        }
        LogUtils.d(TAG, getClass().getSimpleName()+" onEventUIUpdate  event.type "+event.type);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(PayResultEvent event) {
        if(event.type == PayResultEvent.PAY_RESULT_EVENT_RESULT_BACK) {
           if(event.params == PayUtils.PAY_RESULT_SUCC){
//               Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
               ToastUtils.showMyRewardToast("充值成功！","+"+var.recharge+"图币");
               setResult(Activity.RESULT_OK);
               finish();
            }else {
               Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
           }
        }
    }
    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_recharge_now;
    }

    @Override
    protected void onInitView() {
        initView(rootView);
        setListener();
        initData();
    }

    private void initData() {
        mPayUtils=new PayUtils();
        api = WXAPIFactory.createWXAPI(this.getApplicationContext(), PayUtils.APP_ID);
        api.registerApp(PayUtils.APP_ID);
    }

    private void setListener() {
        rl_left_top_bar.setOnClickListener(this);
        et_recharge_num.addTextChangedListener(this);
        rl_aliPay.setOnClickListener(this);
        rl_weChatPay.setOnClickListener(this);
    }

    private void initView(View view) {
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
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

    public static void newInstance(Activity context,int reqCode) {
        Intent intent=new Intent(context,RechargeNowActivity.class);
        context.startActivityForResult(intent,reqCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_left_top_bar:
                RechargeNowActivity.this.finish();
                break;
            case R.id.rl_aliPay:
                if (!NetUtil.isConnected()){
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                payAliWX(1);
                break;
            case R.id.rl_weChatPay:
                if (!NetUtil.isConnected()){
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                payAliWX(2);
                break;
        }

    }

    private void payAliWX(final int payType) {
        final String MoneySum=et_recharge_num.getText().toString().trim();
        if ("0".equals(MoneySum)||TextUtils.isEmpty(MoneySum)||MoneySum.toString().startsWith("0")){
            Toast.makeText(this, "充值金额不能为0或空", Toast.LENGTH_SHORT).show();
            et_recharge_num.setText("");
            return;
        }
        showProgress();
        ServiceProvider.postAccountChargePayInfo(compositeSubscription,MoneySum,payType+"", new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                var=(PayInfo)model.data;
                var.recharge = Integer.parseInt(MoneySum) * 100;

                if(var!=null){
                  if(payType == 1){
                      if (mPayUtils != null) {
                          mPayUtils.payZFBV2(RechargeNowActivity.this, var.aliSign);
//                          mPayUtils.payZFB(RechargeNowActivity.this,var.title,var.description,var.MoneySum,var.orderNum,var.notify_url);
                      }
                  }else {
                      if (mPayUtils != null) {
//                          showProgress();
                          mPayUtils.payWX(api,var,RechargeNowActivity.this);
                      }
                  }
                }
                hideProgress();
                LogUtils.d(model.data);
            }

            @Override
            public void onError(final Throwable e) {
                hideProgress();
                CommonUtils.showToast("输入格式错误");
                onLoadDataFailed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void onLoadDataFailed() {
//        layoutErrorView.updateUI();
//        layoutErrorView.setVisibility(View.VISIBLE);
    }
}
