package com.huatu.handheld_huatu.business.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.me.ExamTargetAreaActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadAssist;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.Event;
import com.huatu.handheld_huatu.helper.GlobalRouterInterceptor;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.CouponStatusBean;
import com.huatu.handheld_huatu.mvpmodel.account.UserInfoBean;
import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.DensityUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ljzyuhenda on 16/7/13.
 */
public class LoginByPasswordActivity extends BaseActivityForLoginWRegister implements View.OnClickListener, TextWatcher {

    private static final String TAG = "LoginByPasswordActivity";

    @BindView(R.id.tv_forgetPassword)
    TextView tv_forgetPassword;
    @BindView(R.id.et_account)
    EditText et_account;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.tv_loginconfirm)
    TextView tv_loginconfirm;
    @BindView(R.id.tv_quick_login)
    TextView tv_quick_login;

    @BindView(R.id.tv_read_rules)
    TextView tvRules;

    private boolean mEnable = false;
    private CompositeSubscription mCompositeSubscription;
    private HttpService mZtkService;

    ToolTipsManager mToolTipsManager;

    @Override
    public void onCreate(Bundle savedInstanceStat) {
        super.onCreate(savedInstanceStat);
        super.TAG = TAG;
        ButterKnife.bind(this);

        if (true || mIsQuickLogin) {
            View closeBtn = this.findViewById(R.id.close_btn);
            closeBtn.setVisibility(View.VISIBLE);
            closeBtn.setOnClickListener(this);
        }
        mZtkService = RetrofitManager.getInstance().getService();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        String userAccount = PrefStore.getUserAccount();
        String userPassword = PrefStore.getUserPassword();
        et_account.setText(userAccount);
        et_password.setText(userPassword);
        setFocusable(userAccount, userPassword);
        if (!TextUtils.isEmpty(userAccount) && !TextUtils.isEmpty(userPassword)) {
            mEnable = true;
            tv_loginconfirm.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
        } else {
            mEnable = false;
            tv_loginconfirm.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
        }
        setListener();
        SignUpTypeDataCache.getInstance().getCategoryListNet(1, compositeSubscription, null);
        StudyCourseStatistic.popLoginPage();
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_loginbypassword;
    }

    @Override
    protected void onInitView() {
        SpannableStringBuilder sb = new SpannableStringBuilder("注册/登录即表示阅读并同意《华图在线用户服务协议》和《隐私政策》");
        sb.setSpan(new PrivacyClickableSpan(0)
        , 13, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.setSpan(new PrivacyClickableSpan(1) , 26, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRules.setText(sb);
        tvRules.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //设置焦点，账号不存在就在账号栏，账号存在就在密码栏，并且显示的密码栏的最后位置
    private void setFocusable(String userAccount, String userPassword) {
        if (!TextUtils.isEmpty(userAccount)) {
            et_password.requestFocus();
            if (!TextUtils.isEmpty(userPassword)) {
                et_password.setSelection(et_password.getText().toString().length());
            } else {
                et_password.setSelection(0);
            }
        } else {
            et_account.requestFocus();
        }
    }

    private void setListener() {
        tv_forgetPassword.setOnClickListener(this);
        et_account.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        tv_loginconfirm.setOnClickListener(this);
        tv_quick_login.setOnClickListener(this);
    }

    public static void newIntent(Context context) {
        newIntent(context, false);
    }

    public static void newIntent(Context context, boolean isquickLogin) {
        Intent intent = new Intent(context, LoginByPasswordActivity.class);
        intent.putExtra(ArgConstant.QUICK_LOGIN, isquickLogin);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_btn:
                this.onBackPressed();
                break;
            case R.id.tv_forgetPassword:
                ForgetPasswordActivity.newIntent(this, mIsQuickLogin);
                break;
            case R.id.tv_quick_login:
                StudyCourseStatistic.clickSignUpOrPhoneCode();
                LoginActivity.newIntent(this, mIsQuickLogin);
                MobclickAgent.onEvent(this, "register");
                break;
            case R.id.tv_loginconfirm:
                if (mEnable) {
                    String registrationId = PushAgent.getInstance(UniApplicationContext.getContext()).getRegistrationId();
                    Observable<UserInfoBean> loginObservable = mZtkService.login(et_account.getText().toString(), et_password.getText().toString(), null, -1, registrationId);
                    Subscription subscriptionLogin = loginObservable.subscribeOn(Schedulers.io())
                            .doOnSubscribe(new Action0() {
                                @Override
                                public void call() {
                                    showLoadingDialog();
                                }
                            }).subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<UserInfoBean>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();

                                    Toast.makeText(LoginByPasswordActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                                    dismissLoadingDialog();
                                }

                                @Override
                                public void onNext(UserInfoBean userInfoBean) {
                                    dismissLoadingDialog();

                                    if ("1110001".equals(userInfoBean.code)) {
                                        Toast.makeText(LoginByPasswordActivity.this, R.string.userAccountOrPwdError, Toast.LENGTH_SHORT).show();
                                    } else if ("1110003".equals(userInfoBean.code)) {
                                        Toast.makeText(LoginByPasswordActivity.this, R.string.userAccountNotExit, Toast.LENGTH_SHORT).show();
                                    } else if ("1000000".equals(userInfoBean.code)) {

                                        if ((null != userInfoBean.data) && (!TextUtils.isEmpty(userInfoBean.data.mobile))) {
                                            SensorsDataAPI.sharedInstance().login(String.valueOf(userInfoBean.data.mobile));

                                            String registrationId = JPushInterface.getRegistrationID(LoginByPasswordActivity.this);
                                            if (!TextUtils.isEmpty(registrationId)) {
                                                SensorsDataAPI.sharedInstance().profilePushId("jgId", registrationId);
                                            }
                                        }
                                        UserInfoUtil.setUserInfo(userInfoBean);
                                        DownLoadAssist.getInstance().resetDownAssist();
                                        PrefStore.setUserAccount(et_account.getText().toString());
                                        PrefStore.setUserPassword(et_password.getText().toString());
                                        if (userInfoBean != null && userInfoBean.data != null && userInfoBean.data.subject == -1) {
                                            ExamTargetAreaActivity.newIntent(LoginByPasswordActivity.this, ExamTargetAreaActivity.NO_SET_REGISTER);
                                        } else {
                                            if (!mIsQuickLogin)
                                                MainTabActivity.newIntent(LoginByPasswordActivity.this);
                                            else {
                                                if (null != GlobalRouterInterceptor.mOnLoginResultListener) {
                                                    GlobalRouterInterceptor.mOnLoginResultListener.onLoginSuccess();
                                                }
                                            }
                                        }
                                        mRxBus.send(new Event.CloseLoginWRegisterEvent());


                                    } else {
                                        Toast.makeText(LoginByPasswordActivity.this, R.string.loginFailure, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    mCompositeSubscription.add(subscriptionLogin);
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
        if (et_password.getText().length() > 0 && et_account.getText().length() > 0) {
            mEnable = true;
        } else {
            mEnable = false;
        }

        if (mEnable) {
            tv_loginconfirm.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
        } else {
            tv_loginconfirm.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if(null!=mToolTipsManager)  mToolTipsManager.dismissAll();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final long bgtime = System.currentTimeMillis();
        ServiceExProvider.visit(mCompositeSubscription, CourseApiService.getApi().getGifCourseStatus(),
                new NetObjResponse<CouponStatusBean>() {
                    @Override
                    public void onSuccess(BaseResponseModel<CouponStatusBean> model) {
                        if (model.data.status == 1 && (null != tv_quick_login) && (!TextUtils.isEmpty(model.data.title))) {

                            long endTime = System.currentTimeMillis();
                            if (endTime - bgtime > 1000)
                                showTip(tv_quick_login, (ViewGroup) LoginByPasswordActivity.this.findViewById(R.id.whole_content), model.data.title);
                            else {
                                final String title = model.data.title;
                                tv_quick_login.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showTip(tv_quick_login, (ViewGroup) LoginByPasswordActivity.this.findViewById(R.id.whole_content), title);
                                    }
                                }, 1000);
                            }
                        }

                    }

                    @Override
                    public void onError(String message, int type) {
                    }
                });
    }

    private void showTip(final TextView mTextView, ViewGroup rootLayout, String message) {
        if (null == mToolTipsManager) {
            mToolTipsManager = new ToolTipsManager(new ToolTipsManager.TipListener() {
                @Override
                public void onTipDismissed(View view, int anchorViewId, boolean byUser) {
                    mToolTipsManager.findAndDismiss(mTextView);
                }
            }, 0.3f);
        }
        // message="注册就送图币";
        mToolTipsManager.findAndDismiss(mTextView);
        ToolTip.Builder builder;
        builder = new ToolTip.Builder(this, mTextView, rootLayout, message, ToolTip.POSITION_BELOW);
        builder.setAlign(ToolTip.ALIGN_LEFT);
        // builder.setGravity(ToolTip.GRAVITY_CENTER);
        builder.setTextAppearance(R.style.TooltipStyle);
        builder.setBackgroundColor(0xFFFFE0AB);
        builder.setOffsetX(DensityUtils.dp2px(this, 10));
        //  builder.setTypeface(mCustomFont);
        mToolTipsManager.show(builder.build());
    }

    @Override
    public void finish() {
        GlobalRouterInterceptor.ClearListener();

        if (AppContextProvider.hasFlag(AppContextProvider.GUESTSPLASH)) {
            AppContextProvider.removeFlag(AppContextProvider.GUESTSPLASH);
        }
        if(mIsQuickLogin){
            AppContextProvider.addFlags(AppContextProvider.WEBVIEW_REFRESHTYPE);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (AppContextProvider.hasFlag(AppContextProvider.GUESTSPLASH)) {
            AppContextProvider.removeFlag(AppContextProvider.GUESTSPLASH);
            if (!ActivityStack.getInstance().hasRootActivity()) {
                MainTabActivity.newIntent(this);
            }
        }
        super.onBackPressed();
    }
}
