package com.huatu.handheld_huatu.business.me;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.mvpmodel.account.UserInfoBean;
import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomDialog;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ht on 2017/9/16.
 */
public class BindPhoneActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rl_left_top_bar)
    RelativeLayout rl_left_top_bar;
    @BindView(R.id.et_mobile)
    EditText et_mobile;
    @BindView(R.id.et_confirm_code)
    EditText et_confirm_code;
    @BindView(R.id.tv_time_clock)
    TextView tv_time_clock;
    @BindView(R.id.tv_complete)
    TextView tv_complete;


    private boolean mEnable = false;
    private boolean mCompleteEnable = false;
    private int mCount = 60;
    private Subscription mTimeClockSubscription;
    private CustomDialog mLoadingDialog;
    private HttpService mZtkService;


    @Override
    protected void onInitView() {
        initView();
        setListener();

    }

    private void initView() {
        ButterKnife.bind(this);
        mZtkService = RetrofitManager.getInstance().getService();


    }

    private void setListener() {
        rl_left_top_bar.setOnClickListener(this);
        tv_time_clock.setOnClickListener(this);
        tv_complete.setOnClickListener(this);
        et_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_mobile.length() >= 11) {
                    mEnable = true;
                } else {
                    mEnable = false;
                }
                if (mEnable) {
                    tv_time_clock.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
                    tv_time_clock.setTextColor(getResources().getColor(R.color.white));
                } else {
                    tv_time_clock.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
                    tv_time_clock.setTextColor(getResources().getColor(R.color.gray006));
                }
            }
        });
        et_confirm_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_mobile.length() >= 11 && et_confirm_code.length() >= 6) {
                    mCompleteEnable = true;
                } else {
                    mCompleteEnable = false;
                }

                if (mCompleteEnable) {
                    tv_complete.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
                } else {
                    tv_complete.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
                }
            }
        });

    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_bind_phone;
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
                BindPhoneActivity.this.finish();
                break;
            case R.id.tv_time_clock:
                if (mEnable&&tv_time_clock.isEnabled()) {
                    et_mobile.clearFocus();
                    et_confirm_code.requestFocus();
                    timeClockStart();
                }
                break;
            case R.id.tv_complete:
                if (mCompleteEnable) {
                    showLoadingDialog();
                    bindPhone();
                }
                break;

        }
    }

    private void bindPhone() {
        Subscription subscribe = RetrofitManager.getInstance().getService().changePhone(et_mobile.getText().toString(), et_confirm_code.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<UserInfoBean>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("网络异常");
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onNext(UserInfoBean userInfoBean) {
                        if (("1000000").equals(userInfoBean.code)) {
                            CommonUtils.showToast("绑定成功");
                            UserInfoUtil.clearUserInfo();
                            UserInfoUtil.setUserInfo(userInfoBean);
                            setResult(RESULT_OK);
                            BindPhoneActivity.this.finish();
                        }
                        if (("1110002").equals(userInfoBean.code)) {
                            CommonUtils.showToast("请求超时，请重试");
                        }
                        if (("1112102").equals(userInfoBean.code)) {
                            CommonUtils.showToast("验证码错误");
                        }
                        if (("1112101").equals(userInfoBean.code)) {
                            CommonUtils.showToast("非法的手机号");
                        }
                        if (("1115104").equals(userInfoBean.code)) {
                            CommonUtils.showToast("该手机号已被其他账号绑定");
                        }
                        if (("1112108").equals(userInfoBean.code)) {
                            CommonUtils.showToast("验证码已过期，请重新获取");
                        }

                    }
                });
        if (compositeSubscription != null) {
            compositeSubscription.add(subscribe);
        }

    }

    private void timeClockStart() {
        //发送验证码
        Observable<ConfirmCodeBean> confirmCodeBeanObservable = mZtkService.sendConfirmCode(et_mobile.getText() + "");
        Subscription subscription = confirmCodeBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConfirmCodeBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(BindPhoneActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ConfirmCodeBean confirmCodeBean) {
                        if ("1112101".equals(confirmCodeBean.code)) {
                            Toast.makeText(BindPhoneActivity.this, R.string.mobileNumIllegal, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        if (compositeSubscription != null) {
            compositeSubscription.add(subscription);
        }

        //开始倒计时
        changeTimeClockText(mCount);

        mTimeClockSubscription = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .take(mCount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        tv_time_clock.setText(R.string.getConfirmCode);
                        tv_time_clock.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
                        tv_time_clock.setTextColor(getResources().getColor(R.color.white));
                        tv_time_clock.setEnabled(true);
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

    private void changeTimeClockText(long count) {
        tv_time_clock.setTextColor(getResources().getColor(R.color.gray006));

//        SpannableString spannableString = new SpannableString(count+ "s后再次获取");
//        int startIndex = 5;
//        int endIndex = startIndex + String.valueOf(count).length() + 1;
//        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange001)), startIndex,
//                endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_time_clock.setText(count+ "s后再次获取");
        tv_time_clock.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);

    }

    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new CustomDialog(this, R.layout.dialog_type2);
            TextView tv = (TextView) mLoadingDialog.mContentView.findViewById(R.id.tv_notify_message);
            tv.setText("修改中...");
        }

        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
