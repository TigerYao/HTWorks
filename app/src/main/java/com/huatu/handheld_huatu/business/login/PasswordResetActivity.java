package com.huatu.handheld_huatu.business.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.register.CompleteUserInfoActivity;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.EditextUtils;
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
public class PasswordResetActivity extends BaseActivityForLoginWRegister implements View.OnClickListener, TextWatcher {
    private static final String TAG = "PasswordResetActivity";
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_passwordconfirm)
    EditText et_passwordconfirm;
    @BindView(R.id.tv_loginconfirm)
    TextView tv_loginconfirm;
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;

    public static final String MOBILE = "mobile";
    public static final String CONFIRMCODE = "confirmcode";
    private boolean mEnable = true;
    private String mMobile;
    private String mConfirmCode;
    private HttpService mZtkService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        mMobile = getIntent().getStringExtra(MOBILE);
        mConfirmCode = getIntent().getStringExtra(CONFIRMCODE);

        tv_title_titlebar.setText(R.string.resetPassword);
        mZtkService = RetrofitManager.getInstance().getService();
        setListener();
        initEditext();

    }
    private void initEditext() {
        et_password.setFilters(EditextUtils.getEditextFilters());
        et_passwordconfirm.setFilters(EditextUtils.getEditextFilters());
    }
    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_passwordreset;
    }

    private void setListener() {
        et_password.addTextChangedListener(this);
        et_passwordconfirm.addTextChangedListener(this);
        tv_loginconfirm.setOnClickListener(this);
        rl_left_topbar.setOnClickListener(this);
    }

    public static void newIntent(Context context, String mobile, String confirmcode,boolean isquickLogin) {
        Intent intent = new Intent(context, PasswordResetActivity.class);
        intent.putExtra(MOBILE, mobile);
        intent.putExtra(CONFIRMCODE, confirmcode);
        intent.putExtra(ArgConstant.QUICK_LOGIN,isquickLogin);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_loginconfirm:
                if (mEnable) {
                    //校验密码
                    if (et_passwordconfirm.length() < 6 ||et_passwordconfirm.length() > 20|| et_password.length() < 6||et_password.length() >20) {
                        //密码长度少于6位或者大于20位
                        Toast.makeText(PasswordResetActivity.this, "设置密码长度应为6~20位", Toast.LENGTH_SHORT).show();
                    } else if (!et_passwordconfirm.getText().toString().equals(et_password.getText().toString())) {
                        //密码不相同
                        Toast.makeText(PasswordResetActivity.this, R.string.passwordDifferentTwoTimes, Toast.LENGTH_SHORT).show();
                    } else {
                        //设置密码
                        Observable<ConfirmCodeBean> resetObservable = mZtkService.resetPwd(mMobile, et_password.getText().toString(), mConfirmCode);
                        resetObservable.subscribeOn(Schedulers.io())
                                .doOnSubscribe(new Action0() {
                                    @Override
                                    public void call() {
                                        showLoadingDialog();
                                    }
                                }).subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<ConfirmCodeBean>() {
                                    @Override
                                    public void onCompleted() {
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();

                                        dismissLoadingDialog();
                                        Toast.makeText(PasswordResetActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(ConfirmCodeBean confirmCodeBean) {
                                        dismissLoadingDialog();

                                        if ("1000000".equals(confirmCodeBean.code)) {
                                            LoginByPasswordActivity.newIntent(PasswordResetActivity.this,mIsQuickLogin);
                                            Toast.makeText(PasswordResetActivity.this, R.string.resetPasswordSuccess, Toast.LENGTH_SHORT).show();
                                        } else if ("1112102".equals(confirmCodeBean.code)) {
                                            Toast.makeText(PasswordResetActivity.this, R.string.confirmCodeError, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(PasswordResetActivity.this, R.string.resetFailure, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
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
        if (et_password.getText().length() > 0 && et_passwordconfirm.getText().length() > 0) {
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
