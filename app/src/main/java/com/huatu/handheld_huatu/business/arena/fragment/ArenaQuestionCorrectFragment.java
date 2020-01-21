package com.huatu.handheld_huatu.business.arena.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.listener.SimpleTextWatcher;
import com.huatu.handheld_huatu.mvppresenter.arena.ArenaCorrectErrorPresenterImpl;
import com.huatu.handheld_huatu.mvpview.BaseView;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.utils.StringUtils;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/10/25.
 * 题目纠错页面
 */
public class ArenaQuestionCorrectFragment extends BaseFragment implements BaseView {

    @BindView(R.id.arena_exam_correct_error_title_back)
    ImageView ivBack;
    @BindView(R.id.et_error_description)
    EditText et_error_description;
    @BindView(R.id.tv_contacts)
    TextView tv_contacts;
    @BindView(R.id.et_contact)
    EditText et_contact;
    @BindView(R.id.tv_commit_correct_error)
    TextView tv_commit_correct_error;

    boolean isTopEmpty = true, isBottomEmpty = true;

    private ArenaCorrectErrorPresenterImpl mPresenter;
    private CompositeSubscription compositeSubscription;
    private int practiseId;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_arena_correct_error_layout;
    }

    @Override
    protected void onInitView() {
        if (args != null) {
            practiseId = args.getInt("practice_id", 0);
        }
        if (practiseId == 0) {
            LogUtils.e("ArenaQuestionCorrectFragment getArguments is null, close activity");
            mActivity.finish();
            return;
        }
        compositeSubscription = new CompositeSubscription();
        mPresenter = new ArenaCorrectErrorPresenterImpl(compositeSubscription, this);
        setViewFocus(et_error_description);
        setViewFocus(et_contact);

        et_error_description.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                isTopEmpty = StringUtils.isEmpty(s.toString());
                setColor();
            }
        });

        et_contact.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                isBottomEmpty = StringUtils.isEmpty(s.toString());
                setColor();
            }
        });
        setColor();
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {                // 日间
            et_error_description.setHintTextColor(getResources().getColor(R.color.correct_text_edit_hint));
            et_contact.setHintTextColor(getResources().getColor(R.color.correct_text_edit_hint));
            ivBack.setImageResource(R.mipmap.icon_back_black_new);
            if (!isTopEmpty && !isBottomEmpty) {         // 两个都不为空
                tv_commit_correct_error.setBackgroundResource(R.drawable.correct_btn_red);
                tv_commit_correct_error.setTextColor(getResources().getColor(R.color.correct_text_commit_full));
            } else {
                tv_commit_correct_error.setBackgroundResource(R.drawable.correct_btn_gray);
                tv_commit_correct_error.setTextColor(getResources().getColor(R.color.correct_text_commit_empty));
            }
        } else {
            et_error_description.setHintTextColor(getResources().getColor(R.color.correct_text_edit_hint_night));
            et_contact.setHintTextColor(getResources().getColor(R.color.correct_text_edit_hint_night));
            ivBack.setImageResource(R.mipmap.icon_back_white_new_night);
            if (!isTopEmpty && !isBottomEmpty) {         // 两个都不为空
                tv_commit_correct_error.setBackgroundResource(R.drawable.correct_btn_red_night);
                tv_commit_correct_error.setTextColor(getResources().getColor(R.color.correct_text_commit_full_night));
            } else {
                tv_commit_correct_error.setBackgroundResource(R.drawable.correct_btn_gray_night);
                tv_commit_correct_error.setTextColor(getResources().getColor(R.color.correct_text_commit_empty_night));
            }

        }
    }

    void setViewFocus(final EditText tv) {
        if (tv != null && mActivity != null) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    tv.setFocusable(true);
                    tv.setFocusableInTouchMode(true);
                    tv.requestFocus();
                    if (manager != null) {
                        manager.showSoftInput(tv, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            hideInput();
        } else {
            et_error_description.setText("");
            et_contact.setText("");
        }
    }

    @OnClick(R.id.arena_exam_correct_error_title_back)
    public void onClickBack() {
        mActivity.onBackPressed();
    }

    @Override
    public boolean onBackPressed() {
        if (mActivity != null) {
            hideInput();
        }
        return super.onBackPressed();
    }

    private void hideInput() {
        View view = mActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @OnClick(R.id.tv_commit_correct_error)
    public void onConfirm() {
        String content = et_error_description.getText().toString().trim();
        String contact = et_contact.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            CommonUtils.showToast("请填写您的纠错建议");
        } else if (content.length() > 160) {
            CommonUtils.showToast("错误描述请不要超过160字");
        } else if (TextUtils.isEmpty(contact)) {
            CommonUtils.showToast("请填写您的联系方式");
        } else {
            mPresenter.correctError(practiseId, contact, content);
        }
    }

    @Override
    public void showProgressBar() {
        mActivity.showProgress();
    }

    @Override
    public void dismissProgressBar() {
        mActivity.hideProgress();
    }

    @Override
    public void onSetData(Object respData) {
//        mActivity.getSupportFragmentManager().popBackStackImmediate();
        if (respData == null) {
            mActivity.finish();
        }
    }

    @Override
    public void onLoadDataFailed() {
        CommonUtils.showToast("哦啊，提交失败了，请稍后重试~");
    }

    public static ArenaQuestionCorrectFragment newInstance(Bundle args) {
        ArenaQuestionCorrectFragment fragment = new ArenaQuestionCorrectFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
