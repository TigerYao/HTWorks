package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.essay.AnswerComment;
import com.huatu.handheld_huatu.view.custom.ExercisePasteTextView;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import java.util.List;

/**
 * Created by Administrator on 2019\7\24 0024.
 */

public class AnswerExercisePasteTextView extends ExercisePasteTextView {


    public AnswerExercisePasteTextView(Context context) {
        super(context);

    }

    public AnswerExercisePasteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public AnswerExercisePasteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    public void refreshStandAnswerView(List<AnswerComment> answerComments, ExplandArrowLayout answTitle, LinearLayout parentCotentView,
                                       ExplandArrowLayout.OnExplandStatusListener explandStatusListener,
                                       ExercisePasteTextView.OnLongClickListener longClickListener) {
        if (ArrayUtils.isEmpty(answerComments)) return;
        //ans_comment.refreshView(ls.size(), index, type, scroll_view, isSingle, var.answerFlag, var.topic, var.subTopic,
        // var.answerComment, var.callName, var.inscribedDate, var.inscribedName, this, i);
        // answerFlag = answerFlag_;
        //  type = type_;

        //  setTipView(type_, answerFlag);
        AnswerComment answerComment = answerComments.get(0);//0 参考答案 1标准答案
        String allTitleNamePre = answerComment.answerFlag == 1 ? "标准答案" : "参考答案";
        String content = answerComment.formatStandAnswer();

        if ((ArrayUtils.size(answerComments) > 1)) {
            TextView titiew = (TextView) ((ViewGroup) answTitle).getChildAt(0);
            titiew.setText(allTitleNamePre + 1);
        } else if (answerComment.answerFlag == 0) {
            TextView titiew = (TextView) ((ViewGroup) answTitle).getChildAt(0);
            titiew.setText(allTitleNamePre);
        }

        if (!StringUtils.isEmpty(content)) {
//                ess_ex_answer_result.setHtmlSource(EssayHelper.getFilterTxt(content));
            this.setHtmlSource(content);
        }

        if (answerComments.size() > 1) {
            LayoutInflater factory = LayoutInflater.from(getContext());
            int index = parentCotentView.indexOfChild(this);

            int step = 1;
            LinearLayout.LayoutParams tmpParm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            tmpParm.leftMargin = DensityUtils.dp2px(getContext(), 15);
            tmpParm.rightMargin = DensityUtils.dp2px(getContext(), 15);
            for (int k = 1; k < answerComments.size(); k++) {

                ExplandArrowLayout tmptitleLayout = (ExplandArrowLayout) factory.inflate(R.layout.essay_standanswer_item, parentCotentView, false);
                TextView titview = (TextView) (tmptitleLayout).getChildAt(0);
                titview.setText(allTitleNamePre + (k + 1));

                parentCotentView.addView(tmptitleLayout, index + step);
                step++;
                ExercisePasteTextView tmpTextView = new ExercisePasteTextView(getContext());
                tmpTextView.initDefaultStyle();
                tmpTextView.setVisibility(GONE);
                parentCotentView.addView(tmpTextView, index + step, tmpParm);
                tmptitleLayout.setCanExplandLayout(tmpTextView).setOnExplandStatusListener(explandStatusListener);
                tmpTextView.setOnLongClickListener(longClickListener);
                step++;

                String content2 = answerComments.get(k).formatStandAnswer();
                if (!StringUtils.isEmpty(content2)) {
                    tmpTextView.setHtmlSource(content2);

                }

            }
            //if(null!=mStickyScrollView) mStickyScrollView.notifyStickyAttributeChanged();
        }

    }
}
