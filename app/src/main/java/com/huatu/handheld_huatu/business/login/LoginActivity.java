package com.huatu.handheld_huatu.business.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.me.ExamTargetAreaActivity;
import com.huatu.handheld_huatu.business.register.CompleteUserInfoActivity;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.Event;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.mvpmodel.account.UserInfoBean;
import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.ChannelUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.EditextUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.MapLocationClient;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.StringUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
public class LoginActivity extends BaseActivityForLoginWRegister implements TextWatcher, View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private boolean isCilicked = false;

    @BindView(R.id.et_mobile)
    EditText et_mobile;
    @BindView(R.id.tv_nextstep)
    TextView tv_nextstep;
    @BindView(R.id.rl_confirmcode)
    RelativeLayout rl_confirmcode;
    @BindView(R.id.rl_inputmobile)
    RelativeLayout rl_inputmobile;
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;
    @BindView(R.id.tv_timeclock)
    TextView tv_timeclock;
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.tv_login_hint)
    TextView tv_login_hint;
    @BindView(R.id.et_confirmcode)
    EditText et_confirmcode;
    @BindView(R.id.cb_view)
    CheckBox cbView;
    @BindView(R.id.tv_read_rules)
    TextView tvRules;

    private int mCount = 60;
    private HttpService mZtkService;
    boolean mNextStepEnable = false;//true 表明 获取验证码 可点;否则 false 不可点
    private boolean mIsMobileInputState = true;
    private CompositeSubscription compositeSubscription;
    private Subscription mTimeClockSubscription;
    private boolean mGoIntoMainPageEnable = false;//true 表明进入砖题库可点,颜色变绿;否则 false 不可点变灰

    private String registrationId = "";                                     // 唯一标识

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.TAG = TAG;
        ButterKnife.bind(this);
        mZtkService = RetrofitManager.getInstance().getService();
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
//        changeUIByState(mIsMobileInputState);
        setListener();
        tv_timeclock.setEnabled(false);
        rl_left_topbar.setOnClickListener(this);
        tv_title_titlebar.setText("注册/登录");

        et_mobile.setFilters(EditextUtils.getEditextFilters());
        et_confirmcode.setFilters(EditextUtils.getEditextFilters());
        registrationId = SpUtils.getCustomDeviceId();
        if (registrationId.equals("")) {
            registrationId = UUID.randomUUID().toString().replace("-", "") + System.currentTimeMillis();
            SpUtils.setCustomDeviceId(registrationId);
        }
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onInitView() {

        SpannableStringBuilder sb = new SpannableStringBuilder("注册/登录即表示阅读并同意《华图在线用户服务协议》和《隐私政策》");
        sb.setSpan(new PrivacyClickableSpan(0), 13, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        sb.setSpan(new PrivacyClickableSpan(1), 26, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRules.setText(sb);
        tvRules.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setListener() {
        et_mobile.addTextChangedListener(this);
        et_confirmcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && mNextStepEnable) {
                    mGoIntoMainPageEnable = true;
                } else {
                    mGoIntoMainPageEnable = false;
                }

                if (mGoIntoMainPageEnable) {
                    tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
                } else {
                    tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
                }
            }
        });
        tv_nextstep.setOnClickListener(this);
        tv_timeclock.setOnClickListener(this);

        SignUpTypeDataCache.getInstance().getCategoryListNet(1, compositeSubscription, null);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 11) {
            mNextStepEnable = true;
        } else {
            mNextStepEnable = false;
        }

        if (mNextStepEnable) {
            tv_timeclock.setTextColor(getResources().getColor(R.color.red120));
            tv_timeclock.setEnabled(true);

        } else {
            tv_timeclock.setTextColor(getResources().getColor(R.color.gray006));
            tv_timeclock.setEnabled(false);

        }
    }

    private void showNextStep() {
        //下一步
        if (!cbView.isChecked()) {
            Toast.makeText(LoginActivity.this, "请先勾选同意《华图在线用户服务协议》和《隐私政策》", Toast.LENGTH_SHORT).show();
        } else if (mGoIntoMainPageEnable) {
            //登录
            Observable<UserInfoBean> loginObservable = mZtkService.login(et_mobile.getText().toString().trim(), null, et_confirmcode.getText().toString().trim(), -1, registrationId);
            Subscription subsciptionLogin = loginObservable.subscribeOn(Schedulers.io())
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

                            dismissLoadingDialog();
                            Toast.makeText(LoginActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(UserInfoBean userInfoBean) {
                            dismissLoadingDialog();

                            if ("1000000".equals(userInfoBean.code)) {
                                UserInfoUtil.setUserInfo(userInfoBean);

                                //上传地理位置信息
                                if (userInfoBean.data.firstLogin) {
                                    MapLocationClient mapLocationClient = new MapLocationClient(LoginActivity.this);
                                    mapLocationClient.init();
                                }

                                if (null != userInfoBean.data.registerFreeCourseDetailVo) {
                                    PrefStore.putUserSettingInt(Constant.APP_COUPON_CHECK, 1);
                                    IoExUtils.saveJsonFile(GsonUtil.toJsonStr(userInfoBean.data.registerFreeCourseDetailVo), Constant.APP_COUPON_CHECK);
                                } else {
                                    PrefStore.putUserSettingInt(Constant.APP_COUPON_CHECK, 0);
                                }

                                if ((null != userInfoBean.data) && (!TextUtils.isEmpty(userInfoBean.data.mobile))) {
                                    SensorsDataAPI.sharedInstance().login(String.valueOf(userInfoBean.data.mobile));

                                    // 注册成功/登录成功（调用 login 方法）后 保存 jgId 到用户表
                                    String registrationId = JPushInterface.getRegistrationID(LoginActivity.this);
                                    if (!TextUtils.isEmpty(registrationId)) {
                                        SensorsDataAPI.sharedInstance().profilePushId("jgId", registrationId);
                                    }
                                }

                                if ("2".equals(userInfoBean.data.status)) {
                                    //完善个人信息
                                    CompleteUserInfoActivity.newIntent(LoginActivity.this, mIsQuickLogin);
                                } else {
                                    UserInfoUtil.setUserInfo(userInfoBean);
                                    ArenaDataCache.getInstance().isFirstLoginIn = true;
                                    //进入砖题库
                                    if (userInfoBean != null && userInfoBean.data != null && userInfoBean.data.subject == -1) {
                                        ExamTargetAreaActivity.newIntent(LoginActivity.this, ExamTargetAreaActivity.NO_SET_REGISTER, mIsQuickLogin);
                                    } else {
                                        if (!mIsQuickLogin) {
                                            MainTabActivity.newIntent(LoginActivity.this);
                                        }
                                    }
                                    mRxBus.send(new Event.CloseLoginWRegisterEvent());
                                }
                                // 向服务器发送channelId
                                postChannelId();
                            } else {
                                Toast.makeText(LoginActivity.this, userInfoBean.message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            compositeSubscription.add(subsciptionLogin);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_nextstep:
                if (Build.VERSION.SDK_INT >= 23
                        && getApplicationInfo().targetSdkVersion >= 23) {
                    checkPermissions(needPermissions);
                } else {
                    showNextStep();
                }
                break;
            case R.id.tv_timeclock:
                String phone = et_mobile.getText().toString();
                boolean isValid = Method.isPhoneValid(phone);
                if (!isValid) {
                    ToastUtils.showShort("请输入有效的手机号");
                    tv_timeclock.setTextColor(getResources().getColor(R.color.gray006));
                    tv_timeclock.setEnabled(false);
                    return;
                }
                if (mNextStepEnable && tv_timeclock.isEnabled()) {
                    et_mobile.clearFocus();
                    et_confirmcode.requestFocus();
                    tv_timeclock.setEnabled(false);
                    timeClockStart();
                }
                StudyCourseStatistic.getPhoneCode(phone);
                break;
            case R.id.rl_left_topbar:
                if (isCilicked) {
                    if (!mIsMobileInputState) {
                        isCilicked = false;
                        mIsMobileInputState = true;
                        changeUIByState(mIsMobileInputState);

                        //取消倒计时
                        mTimeClockSubscription.unsubscribe();
                        compositeSubscription.remove(mTimeClockSubscription);
                    }
                } else {
                    finish();
                }
                break;
        }
    }

    /**
     * 注册成功后，发送channelId
     */
    private void postChannelId() {
        // deviceToken

        // 本机Ip
        String hostIP = getHostIP();
        // 获取ChannelId
        String channelId = AppUtils.getChannelId();
        int channelIdNum = 0;
        if (!StringUtils.isEmpty(channelId)) {
            Integer integer = ChannelUtils.newInstance().channel.get(channelId);
            channelIdNum = integer == null ? 0 : integer;
        }
        // 发送内容
        Observable<BaseResponseModel<String>> channelObservable = mZtkService.setChannel(registrationId, System.currentTimeMillis(), hostIP, channelIdNum);
        Subscription subsciptionLogin = channelObservable.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BaseResponseModel<String> model) {

                    }
                });

        compositeSubscription.add(subsciptionLogin);
    }

    public static void newIntent(Context context, boolean isquickLogin) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(ArgConstant.QUICK_LOGIN, isquickLogin);
        context.startActivity(intent);
    }

    public void changeUIByState(boolean isMobileInputState) {
        if (!isMobileInputState) {
            rl_confirmcode.setVisibility(View.VISIBLE);
            rl_inputmobile.setVisibility(View.GONE);
            tv_login_hint.setVisibility(View.INVISIBLE);
            tv_nextstep.setText(R.string.goIntoMainPage);
            tv_title_titlebar.setText("输入验证码");
            if (mGoIntoMainPageEnable) {
                tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
            } else {
                tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
            }
        } else {
            rl_confirmcode.setVisibility(View.GONE);
            rl_inputmobile.setVisibility(View.VISIBLE);
            tv_login_hint.setVisibility(View.VISIBLE);
            tv_nextstep.setText(R.string.nextStep);
            tv_title_titlebar.setText("注册/登录");
            if (mNextStepEnable) {
                tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_438c44);
            } else {
                tv_nextstep.setBackgroundResource(R.drawable.drawable_rectangle_bfbfbf);
            }

        }
    }

    private void changeTimeClockText(long count) {
        tv_timeclock.setTextColor(getResources().getColor(R.color.gray006));

        SpannableString spannableString = new SpannableString("重新发送(" + (count) + "s)");
        int startIndex = 5;
        int endIndex = startIndex + String.valueOf(count).length() + 1;
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red120)), startIndex,
                endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_timeclock.setText(spannableString);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    private void timeClockStart() {
        //发送验证码
        Observable<ConfirmCodeBean> confirmCodeBeanObservable = mZtkService.sendConfirmCode(et_mobile.getText().toString().trim());
        Subscription subscription = confirmCodeBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ConfirmCodeBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(ConfirmCodeBean confirmCodeBean) {
                        if ("1112101".equals(confirmCodeBean.code)) {
                            Toast.makeText(LoginActivity.this, R.string.mobileNumIllegal, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        compositeSubscription.add(subscription);

        //开始倒计时
        changeTimeClockText(mCount);

        mTimeClockSubscription = Observable.interval(1000, TimeUnit.MILLISECONDS)
                .take(mCount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        tv_timeclock.setText(R.string.getConfirmCode);
                        tv_timeclock.setTextColor(getResources().getColor(R.color.red120));
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

    public String getHostIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }


    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * @param permissions
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                    java.lang.reflect.Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class,
                            int.class});

                    method.invoke(this, array, PERMISSON_REQUESTCODE);
                } else {
                    showNextStep();
                }
            }
        } catch (Throwable e) {
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        if (Build.VERSION.SDK_INT >= 23
                && getApplicationInfo().targetSdkVersion >= 23) {
            try {
                for (String perm : permissions) {
                    java.lang.reflect.Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                    java.lang.reflect.Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                            String.class);
                    if ((Integer) checkSelfMethod.invoke(this, perm) != PackageManager.PERMISSION_GRANTED
                            || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            } catch (Throwable e) {

            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showNextStep();
            } else {
                // PrefStore.setStorageState(1);
                showNextStep();
            }
        }
    }

    /**
     * 显示提示信息
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。\\n\\n请点击\\\"设置\\\"-\\\"权限\\\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
