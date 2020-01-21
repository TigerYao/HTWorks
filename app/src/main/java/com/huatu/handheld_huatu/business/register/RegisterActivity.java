package com.huatu.handheld_huatu.business.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.login.BaseActivityForLoginWRegister;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljzyuhenda on 16/7/14.
 */
public class RegisterActivity extends BaseActivityForLoginWRegister implements View.OnClickListener, TextWatcher {
    private static final String TAG = "RegisterActivity";
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;
    @BindView(R.id.tv_nextstep)
    TextView tv_nextstep;
    @BindView(R.id.et_mobile)
    EditText et_mobile;

    private boolean mEnable = false;
    private CustomDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        tv_title_titlebar.setText(R.string.register);
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

    public static void newIntent(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_left_topbar:
                if (mDialog == null || !mDialog.isShowing()) {
                    showDialog();
                }

                break;
            case R.id.tv_nextstep:
                if (mEnable) {
                    //进入验证码页
                    ConfirmCodeForRegisterActivity.newIntent(this, et_mobile.getText().toString().trim());
                } else {
                    //do nothing
                }
                break;
            case R.id.tv_ok:
                dismissDialog();
                break;
            case R.id.tv_cancel:
                dismissDialog();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mDialog == null || !mDialog.isShowing()) {
                showDialog();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        if (mDialog == null) {
            mDialog = new CustomDialog(this, R.layout.dialog_type1);
            TextView tv_ok = (TextView) mDialog.findViewById(R.id.tv_ok);
            TextView tv_cancel = (TextView) mDialog.findViewById(R.id.tv_cancel);

            tv_ok.setOnClickListener(this);
            tv_cancel.setOnClickListener(this);
            mDialog.setCancelable(false);
        }

        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
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
