package com.huatu.handheld_huatu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.helper.db.CollectInfoDao;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 显示推送信息
 * Created by KaelLi on 2016/5/17.
 */
public class TokenConflictActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TokenConflictActivity";
    @BindView(R.id.tv_ok)
    TextView tv_ok;
    @BindView(R.id.tv_content1)
    TextView tv_content1;
/*    @BindView(R.id.tv_content2)
    TextView tv_content2;*/

    @BindView(R.id.custom_dialog_cancel_btn)
    TextView tv_cancel;

    public static final String MESSAGE = "message";
    private String mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        LogUtils.v(this.getClass().getName() + " onCreate()");
        setFinishOnTouchOutside(false);
        setContentView(R.layout.activity_tokenconflict);
        ButterKnife.bind(this);

        mMessage = getIntent().getStringExtra(MESSAGE);

        try{
            String[] messageArr = mMessage.split("\n");

            if (messageArr.length == 2) {
                tv_content1.setText(messageArr[0]);
               // tv_content2.setText(messageArr[1]);
            }
        }catch (Exception e){  }

        setListener();
    }

    public void setListener() {
        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        try{
             CollectInfoDao.getInstance().deleteAll();//退出清除所有收藏
        }catch (Exception e){ }
    }

    public static void newIntent(Context context, String message) {
        Intent intent = new Intent(context, TokenConflictActivity.class);
        intent.putExtra(MESSAGE, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_ok:
                ActivityStack.getInstance().finishAllActivity();
                AppContextProvider.addFlags(AppContextProvider.GUESTSPLASH);
                LoginByPasswordActivity.newIntent(this);
                finish();
                break;
            case R.id.custom_dialog_cancel_btn:
                //清除个人信息
                UserInfoUtil.clearUserInfo();
                MainTabActivity.newIntent(this);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }
}
