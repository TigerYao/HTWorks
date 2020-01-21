package com.huatu.handheld_huatu.business.essay.examfragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsDialogFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_vod.utils.Utils;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.CustomShapeDrawable;
import com.huatu.handheld_huatu.ui.RoundbgTextView;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.widget.CustomRatingBar;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2019\6\19 0019.
 */

public class FeedbackDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private long answerId;
    public int answerType, feedStar;
    private CustomRatingBar mRatingBar;
    private EditText mFbContent;
    public String feedbackContent;
    public boolean mIsSuccess = false;
    DialogInterface.OnDismissListener mDismissListener;

    public static FeedbackDialogFragment getInstance(String answerId, String answerType) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.TITLE, answerId);
        args.putString(ArgConstant.TYPE, answerType);

        FeedbackDialogFragment tmpFragment = new FeedbackDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    public static FeedbackDialogFragment getInstance(long answerId, int answerType, String content, int star) {
        Bundle args = new Bundle();
        args.putLong(ArgConstant.KEY_ID, answerId);
        args.putInt(ArgConstant.TYPE, answerType);
        args.putString(ArgConstant.BEAN, content);
        args.putInt(ArgConstant.FOR_RESUTL, star);

        FeedbackDialogFragment tmpFragment = new FeedbackDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    protected void parserParams(Bundle args) {
        answerId = args.getLong(ArgConstant.KEY_ID);
        answerType = args.getInt(ArgConstant.TYPE);
        feedStar = args.getInt(ArgConstant.FOR_RESUTL);
        feedbackContent = args.getString(ArgConstant.BEAN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parserParams(getArguments());
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private CompositeSubscription mCompositeSubscription = null;

    protected CompositeSubscription getSubscription() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mDismissListener) {
            mDismissListener = null;
        }

        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    View mRootView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.essay_feedback_layout, null);
        mRatingBar = view.findViewById(R.id.star);
        mFbContent = view.findViewById(R.id.et_feedback_content);
        RoundbgTextView submitBtn = view.findViewById(R.id.submit_btn);

        Dialog dialog = new Dialog(getActivity(), R.style.DimThemeDialogPopup);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        mRootView = view;

        view.findViewById(R.id.input_container_layout).setBackground(CustomShapeDrawable.buildRoundBackground(8, 0xFFF9F9F9));
        view.findViewById(R.id.close_btn).setBackground(CustomShapeDrawable.buildRoundBackground(2, 0xFFE0E0E0));

        if (feedbackContent != null && !feedbackContent.isEmpty()) {
            mFbContent.setText(feedbackContent);
            mRatingBar.setStar(feedStar);
            mRatingBar.setClickable(false);
            submitBtn.setColor(ContextCompat.getColor(getContext(), R.color.gray001));
            submitBtn.setText("已提交");
            submitBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.blackF4));
            mFbContent.setEnabled(false);
            mFbContent.setFocusable(false);
            mFbContent.setKeyListener(null);
        } else {
            submitBtn.setOnClickListener(this);
            mRatingBar.setOnRatingChangeListener(new CustomRatingBar.OnRatingChangeListener() {
                @Override
                public void onRatingChange(float ratingCount) {
                    feedStar = (int) ratingCount;
                }
            });
        }
        return dialog;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                if (feedStar == 0) {
                    ToastUtils.showMessage("请给老师个满意度！");
                    return;
                }
                if (mFbContent.getText() == null || TextUtils.isEmpty(mFbContent.getText())) {
                    ToastUtils.showMessage("请输入评价内容！");
                    return;
                }
                feedbackContent = mFbContent.getText().toString();
                ServiceProvider.sendFeedBack(getSubscription(), answerId, answerType, feedStar, feedbackContent, new NetResponse() {
                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        super.onSuccess(model);
                        mIsSuccess = true;
                        String message = model.message;
                        ToastUtils.showMessage(message);
                        if (mDismissListener != null)
                            mDismissListener.onDismiss(getDialog());
                        dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mIsSuccess = false;
                        ToastUtils.showMessage("评价失败");
                        if (mDismissListener != null)
                            mDismissListener.onDismiss(getDialog());
                        dismiss();
                    }
                });
                break;
        }
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mDismissListener = onDismissListener;
    }
}
