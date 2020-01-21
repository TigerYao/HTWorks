package com.huatu.handheld_huatu.business.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.login.BaseActivityForLoginWRegister;
import com.huatu.handheld_huatu.business.login.PasswordResetActivity;
import com.huatu.handheld_huatu.business.me.ExamTargetAreaActivity;
import com.huatu.handheld_huatu.event.Event;
import com.huatu.handheld_huatu.mvpmodel.account.UserInfoBean;
import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.EditextUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by ljzyuhenda on 16/7/14.
 */
public class CompleteUserInfoActivity extends BaseActivityForLoginWRegister implements View.OnClickListener, TextWatcher {
    private static final String TAG = "CompleteUserInfoActivity";
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;
    @BindView(R.id.et_nick)
    EditText et_nick;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.tv_loginconfirm)
    TextView tv_loginconfirm;

    private boolean mEnable;
    private HttpService mZtkService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        tv_title_titlebar.setText(R.string.completeUserAccount);

        mZtkService = RetrofitManager.getInstance().getService();
        setListener();
        initEditext();
    }

    private void initEditext() {
        et_password.setFilters(EditextUtils.getEditextFilters());
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_completeuserinfo;
    }

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);
        et_nick.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        tv_loginconfirm.setOnClickListener(this);
    }

    public static void newIntent(Context context,boolean isquickLogin) {
        Intent intent = new Intent(context, CompleteUserInfoActivity.class);
        intent.putExtra(ArgConstant.QUICK_LOGIN,isquickLogin);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_topbar:
                finish();

                break;
            case R.id.tv_loginconfirm:
                if (mEnable) {
                    //2018/5/25 guangju 添加长度小于6位或者大于20位的判断
                    if (et_password.getText().toString().trim().length() < 6||et_password.getText().toString().trim().length() >20) {
                        Toast.makeText(CompleteUserInfoActivity.this, "设置密码长度应为6~20位", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Observable<UserInfoBean> userInfoBeanObservable = mZtkService.completeAccount(et_password.getText().toString(), et_nick.getText().toString());
                    userInfoBeanObservable.subscribeOn(Schedulers.io())
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
                                    Toast.makeText(CompleteUserInfoActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(UserInfoBean userInfoBean) {
                                    dismissLoadingDialog();

                                    if ("1000000".equals(userInfoBean.code)) {
                                        //储存信息
                                        UserInfoUtil.setUserInfo(userInfoBean);
                                        //成功 进入地域选择页面
                                        ExamTargetAreaActivity.newIntent(CompleteUserInfoActivity.this, ExamTargetAreaActivity.NO_SET_REGISTER,mIsQuickLogin);
                                        if (mRxBus != null) {
                                            mRxBus.send(new Event.CloseLoginWRegisterEvent());
                                        }
                                    } else if ("1000101".equals(userInfoBean.code)) {
                                        //非法参数
                                        Toast.makeText(CompleteUserInfoActivity.this, R.string.paramsIllegal, Toast.LENGTH_SHORT).show();
                                    } else if ("1110002".equals(userInfoBean.code)) {
                                        //回话过期
                                        Toast.makeText(CompleteUserInfoActivity.this, R.string.loginTimeOut, Toast.LENGTH_SHORT).show();
                                    }else if((null!=userInfoBean)&&(!TextUtils.isEmpty(userInfoBean.message))){
                                        Toast.makeText(CompleteUserInfoActivity.this,userInfoBean.message, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CompleteUserInfoActivity.this, R.string.toast_error_unknown, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
        if (et_nick.getText().length() > 0 && et_password.getText().length() > 0) {
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
