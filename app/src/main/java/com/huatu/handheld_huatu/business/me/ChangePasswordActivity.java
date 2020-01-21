package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;
import com.huatu.handheld_huatu.business.login.PasswordResetActivity;
import com.huatu.handheld_huatu.mvpmodel.me.ChangePasswordBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.EditextUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改密码
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout rl_left_topbar;
    private EditText edittext_current_password;
    private EditText edittext_new_password;
    private EditText edit_repead_password;
    private RelativeLayout rl_commit;
    private CustomDialog customDialog;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void onInitView() {
        rl_left_topbar = (RelativeLayout) findViewById(R.id.rl_left_topbar);
        edittext_current_password = (EditText) findViewById(R.id.edittext_current_password);
        edittext_new_password = (EditText) findViewById(R.id.edittext_new_password);
        edit_repead_password = (EditText) findViewById(R.id.edit_repead_password);
        rl_commit = (RelativeLayout) findViewById(R.id.rl_commit);

        setListener();
        initEditext();
    }
    private void initEditext() {
        edittext_current_password.setFilters(EditextUtils.getEditextFilters());
        edittext_new_password.setFilters(EditextUtils.getEditextFilters());
        edit_repead_password.setFilters(EditextUtils.getEditextFilters());
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

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);
        rl_commit.setOnClickListener(this);
    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        context.startActivity(intent);
    }

    private boolean changeFlag = true;

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_left_topbar:
                finish();
                break;
            case R.id.rl_commit:
                if (changeFlag) {
                    changePassword();
                }
                break;
        }
    }

    private void changePassword() {
        String curPassword = edittext_current_password.getText().toString().trim();
        String newPassword = edittext_new_password.getText().toString().trim();
        String repeadPassword = edit_repead_password.getText().toString().trim();

        if (TextUtils.isEmpty(curPassword)) {
            CommonUtils.showToast("当前密码不能为空");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            CommonUtils.showToast("新的密码不能为空");
            return;
        }

        if (TextUtils.isEmpty(repeadPassword)) {
            CommonUtils.showToast("重复密码不能为空");
            return;
        }

        if (newPassword.length() < 6) {
            CommonUtils.showToast("设置密码长度应为6~20位");
            return;
        }
        if (newPassword.length() > 20) {
            CommonUtils.showToast("设置密码长度应为6~20位");
            return;
        }

        if (!newPassword.equals(repeadPassword)) {
            CommonUtils.showToast("新密码与重复密码不一致");
            return;
        }

        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络，请检查网络连接");
            return;
        }

        changeFlag = false;
        customDialog = new CustomDialog(ChangePasswordActivity.this, R.layout.dialog_feedback_commit);
        customDialog.show();

        Subscription subscription = RetrofitManager.getInstance().getService().changePassword(newPassword, curPassword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChangePasswordBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        changeFlag = true;
                        customDialog.dismiss();
                        CommonUtils.showToast("密码修改失败");
                    }

                    @Override
                    public void onNext(ChangePasswordBean changePasswordBean) {
                        changeFlag = true;
                        customDialog.dismiss();

                        int code = changePasswordBean.getCode();
                        if (code == 1000000) {
                            UserInfoUtil.clearUserInfo();
                            CommonUtils.showToast("密码修改成功，请重新登录");
                            PrefStore.setUserPassword("");
                            LoginByPasswordActivity.newIntent(ChangePasswordActivity.this);
                            ActivityStack.getInstance().finishAllActivity();
                        } else if (code == 1112104) {
                            CommonUtils.showToast("旧密码输入错误");
                        } else if (code == 1112107) {
                            CommonUtils.showToast("密码长度错误");
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            CommonUtils.showToast("密码修改失败");
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
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
