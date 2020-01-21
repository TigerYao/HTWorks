package com.huatu.handheld_huatu.business.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
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
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ljzyuhenda on 16/7/14.
 */
public class ConfirmCodeActivity extends BaseActivityForLoginWRegister implements View.OnClickListener, TextWatcher {
    private static final String TAG = "ConfirmCodeActivity";
    @BindView(R.id.tv_nextstep)
    TextView tv_nextstep;
    @BindView(R.id.et_confirmcode)
    EditText et_confirmcode;
    @BindView(R.id.tv_timeclock)
    TextView tv_timeclock;
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;

    public static final String MOBILE = "mobile";
    private CompositeSubscription compositeSubscription;
    private HttpService mZtkService;
    private String mMobile;
    private Subscription mTimeClockSubscription;
    private int mCount = 60;
    private boolean mEnable = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        mZtkService = RetrofitManager.getInstance().getService();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);

        tv_title_titlebar.setText(R.string.inputConfirmCodeTitle);
        mMobile = getIntent().getStringExtra(MOBILE);

        intervalTimer();

        setListener();
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_confirmcode;
    }

    private void setListener() {
        tv_timeclock.setOnClickListener(this);
        tv_nextstep.setOnClickListener(this);
        et_confirmcode.addTextChangedListener(this);
        rl_left_topbar.setOnClickListener(this);
    }

    private void timeClockStart() {
        sendIdentifyCode();
        intervalTimer();


    }

    private void intervalTimer() {
        //开始倒计时
        changeTimeClockText(mCount);

        mTimeClockSubscription = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .take(mCount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        tv_timeclock.setText(R.string.getConfirmCode);
                        tv_timeclock.setTextColor(getResources().getColor(R.color.green001));
                        tv_timeclock.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        changeTimeClockText(mCount - aLong - 1);
                    }
                });

        compositeSubscription.add(mTimeClockSubscription);
    }

    private void sendIdentifyCode() {
        //发送验证码
        Observable<ConfirmCodeBean> confirmCodeBeanObservable = mZtkService.sendConfirmCodeForgetPassWord(mMobile);
        Subscription subscription = confirmCodeBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConfirmCodeBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(ConfirmCodeActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ConfirmCodeBean confirmCodeBean) {
                        if ("1112101".equals(confirmCodeBean.code)) {
                            Toast.makeText(ConfirmCodeActivity.this, R.string.mobileNumIllegal, Toast.LENGTH_SHORT).show();
                        }else if("10001".equals(confirmCodeBean.code)){
                            Toast.makeText(ConfirmCodeActivity.this, R.string.mobileNoUser, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    public static void newIntent(Context context,String mobile,boolean isquickLogin) {
        Intent intent = new Intent(context, ConfirmCodeActivity.class);
        intent.putExtra(ArgConstant.QUICK_LOGIN,isquickLogin);
        intent.putExtra(MOBILE,mobile);
        context.startActivity(intent);
    }

    private void changeTimeClockText(long count) {
        tv_timeclock.setTextColor(getResources().getColor(R.color.gray006));

        SpannableString spannableString = new SpannableString("重新发送(" + (count) + "s)");
        int startIndex = 5;
        int endIndex = startIndex + String.valueOf(count).length() + 1;
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange001)), startIndex,
                endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_timeclock.setText(spannableString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_timeclock:
                tv_timeclock.setEnabled(false);
                timeClockStart();
                break;
            case R.id.tv_nextstep:
                if (mEnable) {
                    //进入密码重置页面
//                    Observable<UserInfoBean> loginObservable = mZtkService.login(mMobile, null, et_confirmcode.getText().toString());
//                    Subscription subsciptionLogin = loginObservable.subscribeOn(Schedulers.io())
//                            .doOnSubscribe(new Action0() {
//                                @Override
//                                public void call() {
//                                    showLoadingDialog();
//                                }
//                            }).subscribeOn(AndroidSchedulers.mainThread())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(new Subscriber<UserInfoBean>() {
//                                @Override
//                                public void onCompleted() {
//                                }
//
//                                @Override
//                                public void onError(Throwable e) {
//                                    e.printStackTrace();
//
//                                    dismissLoadingDialog();
//                                    Toast.makeText(ConfirmCodeActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onNext(UserInfoBean userInfoBean) {
//                                    dismissLoadingDialog();
//
//                                    PasswordResetActivity.newIntent(ConfirmCodeActivity.this);
//                                }
//                            });
//
//                    compositeSubscription.add(subsciptionLogin);
                    PasswordResetActivity.newIntent(ConfirmCodeActivity.this, mMobile, et_confirmcode.getText().toString(),mIsQuickLogin);
                } else {
                    //do nothing
                }
                break;
            case R.id.rl_left_topbar:
                finish();
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
        if (s.length() > 0) {
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }
}
