package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.OnSelectListener;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;
import com.huatu.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class RightContentLinearlayout extends LinearLayout {

    String TAG = "RightContentLinearlayout";
    Context mContext;
    @BindView(R.id.ess_ex_answer_tip_tv)
    OcTextView ess_ex_answer_tip_tv;

    @BindView(R.id.view_bottom)
    View viewBottom;                            // 底部的绿线

    @BindView(R.id.son_view)
    LinearLayout son_view;
    @BindView(R.id.topic)
    ExerciseTextView topic;                     // topic
    @BindView(R.id.subTopic)
    ExerciseTextView subTopic;                  // subTopic
    @BindView(R.id.callName)
    ExerciseTextView callName;                  // callName
    @BindView(R.id.ess_ex_answer_result)
    ExerciseTextView ess_ex_answer_result;      // ess_ex_answer_result
    @BindView(R.id.inscribedName)
    ExerciseTextView inscribedName;
    @BindView(R.id.inscribedDate)
    ExerciseTextView inscribedDate;

    int count;              // 用于记录总条数，如果条数 > 1,需要吧字下的绿线变短
    int index;              // 当前是第几个答案
    private AnswerListLinearlayout answerListLinearlayout;
    boolean isSingle;
    int answerFlag;
    int type;
    int posIndex;
    private int resId = R.layout.essay_right_content_layout;
    private View rootView;
    private NestedScrollView scroll_view;

    public RightContentLinearlayout(Context context) {
        super(context);
        initView(context);
    }

    public RightContentLinearlayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RightContentLinearlayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(this, rootView);
        addExpListener(ess_ex_answer_tip_tv, son_view);
    }

    public void refreshView(int count_, int pos, int type_, NestedScrollView scroll_, boolean isSingle_, int answerFlag_, String topicStr, String subTopicStr, String answerCommentStr, String callNameStr, String inscribedDateStr, String inscribedNameStr, final AnswerListLinearlayout answerListLinearlayout, final int index) {
        count = count_;
        scroll_view = scroll_;
        posIndex = pos;
        isSingle = isSingle_;
        answerFlag = answerFlag_;
        type = type_;
        this.answerListLinearlayout = answerListLinearlayout;
        this.index = index;
        setTipView(type_, answerFlag);
        String content = "";
//        setTopView(topic, topicStr);
//        setTopView(subTopic, subTopicStr);
//        setTopView(callName, callNameStr);
        if (!StringUtils.isEmpty(topicStr)) {
            content += ("<p style=\"text-align:center;\">" + topicStr + "</p>");
        }
        if (!StringUtils.isEmpty(subTopicStr)) {
            content += ("<p style=\"text-align:center;\">" + subTopicStr + "</p>");
        }
        if (!StringUtils.isEmpty(callNameStr)) {
            content += (callNameStr + "<br/>");
        }
        if (!StringUtils.isEmpty(answerCommentStr)) {
            content += (answerCommentStr);
        }

        if (!StringUtils.isEmpty(inscribedNameStr)) {
            content += ("<p style=\"text-align:end;\">" + inscribedNameStr + "</p>");
        }
        if (!StringUtils.isEmpty(inscribedDateStr)) {
            content += ("<p style=\"text-align:end;\">" + inscribedDateStr + "</p>");
        }
        if (ess_ex_answer_result != null) {
            if (!StringUtils.isEmpty(content)) {
//                ess_ex_answer_result.setHtmlSource(EssayHelper.getFilterTxt(content));
                ess_ex_answer_result.setHtmlSource(content);
                ess_ex_answer_result.openCopy();
                ess_ex_answer_result.mSelectableTextHelper.setSelectListener(new OnSelectListener() {
                    @Override
                    public void onTextSelected(CharSequence content) {
                        answerListLinearlayout.clearView(index);
                        EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_RIGHT_ANSWER_SELECT_clearview));
                    }

                    @Override
                    public void updateView(int type) {

                    }
                });
            }
        }
//        setTopView(inscribedName, inscribedNameStr);
//        setTopView(inscribedDate, inscribedDateStr);
    }

    public void clearView() {
        if (ess_ex_answer_result != null) {
            ess_ex_answer_result.clearView();
        }
        if (topic != null) {
            topic.clearView();
        }
        if (subTopic != null) {
            subTopic.clearView();
        }
        if (callName != null) {
            callName.clearView();
        }
        if (inscribedDate != null) {
            inscribedDate.clearView();
        }
        if (inscribedName != null) {
            inscribedName.clearView();
        }
    }

    private void initTipView() {
        if (son_view != null) {
            son_view.setVisibility(View.VISIBLE);
        }
        if (ess_ex_answer_tip_tv != null) {
            ess_ex_answer_tip_tv.setOpen();
        }
    }

    private void setTipView(int type, int answerFlag) {
        if (ess_ex_answer_tip_tv != null) {
            ess_ex_answer_tip_tv.refreshView(type);
            String appStr = "";
            if (posIndex != -1) {
                appStr = (posIndex + 1) + "";
            }
//            if (type == 0) {
            if (answerFlag == 0) {
                ess_ex_answer_tip_tv.setText("参考答案" + appStr);
            } else {
                ess_ex_answer_tip_tv.setText("标准答案" + appStr);
            }
            if (count == 1) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewBottom.getLayoutParams();
                layoutParams.width = DisplayUtil.dp2px(60);
                viewBottom.setLayoutParams(layoutParams);
            }
//            } else {
//                if (answerFlag == 0) {
//                    ess_ex_answer_tip_tv.setText("【参考答案" + appStr + "】");
//                } else {
//                    ess_ex_answer_tip_tv.setText("【标准答案" + appStr + "】");
//                }
//            }
        }
    }

    int[] sk = new int[2];

    private void addExpListener(final OcTextView essvar, final View sonView) {
        essvar.setOnCusiewClickListener(new OcTextView.OnCusViewClickListener() {
            @Override
            public void isOpen(boolean isOpen) {
                if (isOpen) {
                    sonView.setVisibility(View.VISIBLE);
                    essvar.getLocationOnScreen(sk);
                    int yof;
                    if (isSingle) {
                        yof = 180;
                    } else {
                        yof = 220;
                    }
                    final int dp = sk[1] - DisplayUtil.dp2px(yof);
                    if (scroll_view != null && dp != 0) {
                        scroll_view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (scroll_view != null) {
                                    scroll_view.smoothScrollBy(0, dp);
                                }
                            }
                        }, 100);

                        LogUtils.d(TAG, "-dp: " + " " + dp);
                    }
                } else {
                    sonView.setVisibility(View.GONE);
                    clearView();
                }
            }
        });
    }

    private void setTopView(ExerciseTextView topic, String content) {
        if (!TextUtils.isEmpty(content)) {
            if (topic != null) {
                topic.setHtmlSource(content);
                topic.setVisibility(View.VISIBLE);
                topic.openCopy();
            }
        } else {
            if (topic != null) {
                topic.setVisibility(View.GONE);
            }
        }
    }

    public void setInitView() {
        initTipView();
    }

    /**
     * 设置字体大小
     */
    public void setTextSize(float textSize) {
//        ess_ex_answer_tip_tv.setTextSize(textSize);
        topic.setTextSize(textSize);
        subTopic.setTextSize(textSize);
        callName.setTextSize(textSize);
        inscribedDate.setTextSize(textSize);
        inscribedName.setTextSize(textSize);
        ess_ex_answer_result.setTextSize(textSize);
    }
}
