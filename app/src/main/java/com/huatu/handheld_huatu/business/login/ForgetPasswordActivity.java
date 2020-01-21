package com.huatu.handheld_huatu.business.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ljzyuhenda on 16/7/13.
 */
public class ForgetPasswordActivity extends BaseActivityForLoginWRegister implements View.OnClickListener, TextWatcher {
    private static final String TAG = "ForgetPasswordActivity";
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;
    @BindView(R.id.tv_nextstep)
    TextView tv_nextstep;
    @BindView(R.id.et_mobile)
    EditText et_mobile;

    private boolean mEnable = false;
    private boolean mIsQuickLogin;//游客快速登录

    //
    private CompositeSubscription compositeSubscription;
    private HttpService mZtkService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.TAG=TAG;
        ButterKnife.bind(this);
        mZtkService = RetrofitManager.getInstance().getService();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        tv_title_titlebar.setText(R.string.forgetPassword);
        setListener();
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_forgetpassword;
    }

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);
        tv_nextstep.setOnClickListener(this);
        et_mobile.addTextChangedListener(this);
    }

    public static void newIntent(Context context,boolean isquickLogin) {
        Intent intent = new Intent(context, ForgetPasswordActivity.class);
        intent.putExtra(ArgConstant.QUICK_LOGIN,isquickLogin);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_topbar:
                finish();
                break;
            case R.id.tv_nextstep:
                if (mEnable) {
                    //2018/5/30  先去获取验证码,再去判断是否

                    if (!NetUtil.isConnected()){
                        ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
                        return;
                    }
                    Observable<ConfirmCodeBean> confirmCodeBeanObservable = mZtkService.sendConfirmCodeForgetPassWord(et_mobile.getText().toString());
                    Subscription subscription = confirmCodeBeanObservable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ConfirmCodeBean>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    Toast.makeText(ForgetPasswordActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(ConfirmCodeBean confirmCodeBean) {
                                    if (("1000000").equals(confirmCodeBean.code)) {
                                        //进入验证码页
                                        ConfirmCodeActivity.newIntent(ForgetPasswordActivity.this,et_mobile.getText().toString().trim(),mIsQuickLogin);
                                    }else if (("1112101").equals(confirmCodeBean.code)) {
                                        Toast.makeText(ForgetPasswordActivity.this, R.string.mobileNumIllegal, Toast.LENGTH_SHORT).show();
                                    }else if(("10001").equals(confirmCodeBean.code)){
                                        Toast.makeText(ForgetPasswordActivity.this, R.string.mobileNoUser, Toast.LENGTH_SHORT).show();
                                    }else if(("1112109").equals(confirmCodeBean.code)){
                                        ToastUtils.showEssayToast("请求过于频繁啦，请稍后再试");
                                    }else {
                                        ToastUtils.showEssayToast("网络连接出错，请检查您的网络设置");
                                    }
                                }
                            });

                    compositeSubscription.add(subscription);

                } else {
                    //do nothing
                }

                break;
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
        if (s.length() >= 11) {
            mEnable = true;
        } else {
            mEnable = false;
        }

        if (mEnable) {
            tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
        } else {
            tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
