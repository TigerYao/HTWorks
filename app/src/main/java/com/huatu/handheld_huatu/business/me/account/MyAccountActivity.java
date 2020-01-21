package com.huatu.handheld_huatu.business.me.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.me.bean.MyAccountBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的账户
 * Created by chq on 2017/8/29.
 */

public class MyAccountActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MyAccountActivity";
    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;
    @BindView(R.id.iv_detail)
    ImageView iv_detail;
    @BindView(R.id.tv_balance_num)
    TextView tv_balance_num;
    @BindView(R.id.tv_balance_detail)
    TextView tv_balance_detail;
    @BindView(R.id.tv_recharge)
    TextView tv_recharge;
//    @BindView(R.id.rl_integration)
//    RelativeLayout rl_integration;
//    @BindView(R.id.tv_integration)
//    TextView tv_integration;
//    @BindView(R.id.rl_voucher)
//    RelativeLayout rl_voucher;
    private final String murl="https://apitk.huatu.com/v3/h5/dialog_count_new.shtml";

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_my_account;
    }

    @Override
    protected void onInitView() {
        initView(rootView);
        setListener();
    }

    private void setListener() {
        rl_left_top_bar.setOnClickListener(this);
        iv_detail.setOnClickListener(this);
        tv_balance_detail.setOnClickListener(this);
        tv_recharge.setOnClickListener(this);
//        rl_integration.setOnClickListener(this);
//        rl_voucher.setOnClickListener(this);
    }

    private void initView(View view) {
        ButterKnife.bind(this);
    }

    @Override
    protected void onLoadData() {
        loadData();
    }

    private void loadData() {
        if (!NetUtil.isConnected()){
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        showProgress();
        Subscription subscribe = RetrofitManager.getInstance().getService().getMyAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MyAccountBean>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        ToastUtils.showShort("网络不稳定，请检查网络");

                    }

                    @Override
                    public void onNext(MyAccountBean myAccountBean) {
                        hideProgress();
                        if (myAccountBean.code == 1000000) {
                            MyAccountBean.MyAccountData mMyAccount = myAccountBean.data;
                            tv_balance_num.setText(mMyAccount.userCountres.UserMoney + "");
//                            tv_integration.setText("我的积分：" + mMyAccount.userCountres.UserPoint);
                        }else {
                            if(myAccountBean.message!=null){
                                CommonUtils.showToast(myAccountBean.message);
                            }
                        }
                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
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
                MyAccountActivity.this.finish();
                break;
            case R.id.iv_detail://规则
                if (!NetUtil.isConnected()){
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                DialogUtils.onShowRuleDialog(MyAccountActivity.this,null,murl,"我知道了");
                break;
            case R.id.tv_balance_detail://余额明细
                if (!NetUtil.isConnected()){
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                BalanceDetailActivity.newInstance(MyAccountActivity.this);
                break;
            case R.id.tv_recharge://立即充值
                if (!NetUtil.isConnected()){
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                    return;
                }
                RechargeNowActivity.newInstance(MyAccountActivity.this,10001);
                break;
//            case rl_integration://我的积分
//                MyIntegrationActivity.newInstance(MyAccountActivity.this);
//                break;
//            case rl_voucher://代金券
//                VoucherActivity.newInstance(MyAccountActivity.this,10001);
//                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode== Activity.RESULT_OK) {
            loadData();
        }
    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, MyAccountActivity.class);
        context.startActivity(intent);

    }
}
