package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.mvpmodel.me.ChangeNicknameBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CustomDialog;

import java.io.Serializable;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改昵称
 */
public class ChangeNicknameActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_left_topbar;
    private RelativeLayout rl_right_topbar;
    private EditText edittext_content;
    private RelativeLayout rl_close;
    private ImageView image_suggest;
    private CustomDialog customDialog;

    @Override
    protected void onInitView() {
        rl_left_topbar = (RelativeLayout) findViewById(R.id.rl_left_topbar);
        rl_right_topbar = (RelativeLayout) findViewById(R.id.rl_right_topbar);

        edittext_content = (EditText) findViewById(R.id.edittext_content);
        edittext_content.setCursorVisible(false);
        rl_close = (RelativeLayout) findViewById(R.id.rl_close);

        image_suggest = (ImageView) findViewById(R.id.image_suggest);
        image_suggest.setVisibility(View.INVISIBLE);
        rl_close.setVisibility(View.INVISIBLE);

        showEdittext_content();
        setListener();
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_change_nickname;
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

    private void showEdittext_content() {
        String nick = SpUtils.getNick();
        if (!TextUtils.isEmpty(nick)) {
            edittext_content.setText(nick);
            rl_close.setVisibility(View.VISIBLE);
        } else {
            edittext_content.setText("");
        }
    }

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);
        rl_right_topbar.setOnClickListener(this);
        rl_close.setOnClickListener(this);

        edittext_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittext_content.setCursorVisible(true);
            }
        });

        edittext_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = charSequence.toString();
//                if (str != null && str.length() < 2) {
//                    image_suggest.setVisibility(View.VISIBLE);
//                } else if (str != null && str.length() == 12) {
//                    image_suggest.setVisibility(View.VISIBLE);
//                } else {
//                    image_suggest.setVisibility(View.INVISIBLE);
//                }

                if (str != null && str.length() > 0) {
                    rl_close.setVisibility(View.VISIBLE);
                } else {
                    rl_close.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, ChangeNicknameActivity.class);
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
            case R.id.rl_close:
                edittext_content.setText("");
                break;
            case R.id.rl_right_topbar:
                if (changeFlag) {
                    changeNickname();
                }
                break;
        }
    }

    private void changeNickname() {
        final String nickname = edittext_content.getText().toString().trim();

        if (TextUtils.isEmpty(nickname)) {
            CommonUtils.showToast("请输入昵称");
            return;
        }

        if (nickname.length() > 12 || nickname.length() < 2) {
            CommonUtils.showToast("昵称的长度不符合要求");
            image_suggest.setVisibility(View.VISIBLE);
            return;
        }

        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络，请检查网络连接");
            return;
        }

        changeFlag = false;

        customDialog = new CustomDialog(ChangeNicknameActivity.this, R.layout.dialog_feedback_commit);
        customDialog.show();

        Subscription subscription = RetrofitManager.getInstance().getService().changeNickname(nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChangeNicknameBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        customDialog.dismiss();
                        changeFlag = true;
                        Log.i(TAG, e.getMessage() + "----2");
                        CommonUtils.showToast("昵称修改失败");
                    }

                    @Override
                    public void onNext(ChangeNicknameBean changeNicknameBean) {
                        customDialog.dismiss();
                        changeFlag = true;
                        int code = changeNicknameBean.getCode();
                        ChangeNicknameBean.ChangeData data = changeNicknameBean.getData();
                        if (code == 1000000) {
                            edittext_content.setText("");
                            image_suggest.setVisibility(View.INVISIBLE);
                            SpUtils.setNick(nickname);
                            CommonUtils.showToast("保存成功");
                            ChangeNicknameActivity.this.finish();
                        } else if (code == 1112105) {
                            CommonUtils.showToast("昵称格式错误");
                        } else if (code == 1112106) {
                            CommonUtils.showToast("昵称包含敏感词");
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            CommonUtils.showToast("昵称修改失败");
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
}
