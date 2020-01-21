package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.essay.AnswerComment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */

public class AnswerListLinearlayout extends LinearLayout {

    String TAG = "AnswerListLinearlayout";
    Context mContext;

    @BindView(R.id.ans_comment_one)
    RightContentLinearlayout ans_comment_one;
    @BindView(R.id.ans_comment_two)
    RightContentLinearlayout ans_comment_two;
    @BindView(R.id.ans_comment_three)
    RightContentLinearlayout ans_comment_three;
    @BindView(R.id.ans_comment_four)
    RightContentLinearlayout ans_comment_four;
    @BindView(R.id.ans_comment_five)
    RightContentLinearlayout ans_comment_five;


    private int type;
    private boolean isSingle;
    private int resId = R.layout.essay_ans_comment_content_layout;
    private View rootView;
    private NestedScrollView scroll_view;

    public AnswerListLinearlayout(Context context) {
        super(context);
        initView(context);
    }

    public AnswerListLinearlayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AnswerListLinearlayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(this, rootView);
    }

    /**
     * 底部的多个答案
     *
     * @param type_     是答案页，还是批改页
     * @param scroll_   滚动的ScrollView，为了代码滚动到0
     * @param isSingle_
     * @param ls
     */
    public void refreshView(int type_, NestedScrollView scroll_, boolean isSingle_, List<AnswerComment> ls) {
        this.scroll_view = scroll_;
        this.isSingle = isSingle_;
        this.type = type_;
        if (ls != null && ls.size() > 0) {
            refreshSonView(type_, ans_comment_one, ls, 0);
            refreshSonView(type_, ans_comment_two, ls, 1);
            refreshSonView(type_, ans_comment_three, ls, 2);
            refreshSonView(type_, ans_comment_four, ls, 3);
            refreshSonView(type_, ans_comment_five, ls, 4);
        } else {
            setGone(ans_comment_one);
            setGone(ans_comment_two);
            setGone(ans_comment_three);
            setGone(ans_comment_four);
            setGone(ans_comment_five);
        }
    }

    private void refreshSonView(int type, RightContentLinearlayout ans_comment, List<AnswerComment> ls, int i) {
        if (ans_comment != null) {
            if (ls != null) {
                if (i < ls.size()) {
                    AnswerComment var = ls.get(i);
                    if (var != null) {
                        ans_comment.setVisibility(VISIBLE);
                        int index = -1;
                        if (ls.size() > 1) {
                            index = i;
                        }
                        ans_comment.refreshView(ls.size(), index, type, scroll_view, isSingle, var.answerFlag, var.topic, var.subTopic,
                                var.answerComment, var.callName, var.inscribedDate, var.inscribedName, this, i);
                        if (type == 1){
                            ans_comment.setInitView();
                        }
                    } else {
                        ans_comment.setVisibility(GONE);
                    }
                } else {
                    ans_comment.setVisibility(GONE);
                }
            }
        } else {
            ans_comment.setVisibility(GONE);
        }
    }

    private void setGone(RightContentLinearlayout ans_comment_one) {
        if (ans_comment_one != null) {
            ans_comment_one.setVisibility(GONE);
        }
    }

    private void setVisible(RightContentLinearlayout ans_comment_one) {
        if (ans_comment_one != null) {
            ans_comment_one.setVisibility(VISIBLE);
        }
    }

    public void clearView() {
        clearView(-1);
    }

    public void clearView(int index){
        if (ans_comment_one != null && index != 0) {
            ans_comment_one.clearView();
        }
        if (ans_comment_two != null && index != 1) {
            ans_comment_two.clearView();
        }
        if (ans_comment_three != null && index != 2) {
            ans_comment_three.clearView();
        }
        if (ans_comment_four != null && index != 3) {
            ans_comment_four.clearView();
        }
        if (ans_comment_five != null && index != 4) {
            ans_comment_five.clearView();
        }

    }

    /**
     * 设置字体大小
     */
    public void setTextSize(float textSize) {
        ans_comment_one.setTextSize(textSize);
        ans_comment_two.setTextSize(textSize);
        ans_comment_three.setTextSize(textSize);
        ans_comment_four.setTextSize(textSize);
        ans_comment_five.setTextSize(textSize);
    }
}
