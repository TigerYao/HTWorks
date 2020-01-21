package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeans;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;
import com.huatu.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2016/10/18.
 * 答案分析ViewGroup
 */
public class ArenaQuestionAnalysisLayoutNew extends FrameLayout implements NightSwitchInterface, TextSizeSwitchInterface {

    private Context mContext;
    private View rootView;

    private String[] option = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S"};

    @BindView(R.id.question_analysis_subjective_layout)
    LinearLayout layoutSubjective;                              // 主观题布局
    @BindView(R.id.tv_title)
    TextView tvTitle;                                           // 18sp "答案"两个字
    @BindView(R.id.question_analysis_subjective_answer)
    ExerciseTextView etvSubjectiveAnswer;                       // 18sp 答案内容，主观题的参考解析，作为参考答案
    @BindView(R.id.question_analysis_subjective_analysis_des)
    TextView tvSubAnakysisDes;                                  // 18sp "解析"两个字
    @BindView(R.id.question_analysis_subjective_analysis_etv)
    ExerciseTextView etvSubjectiveAnalysis;                     // 18sp 解析内容

    @BindView(R.id.ll_extend_un_answer)
    LinearLayout llExtendUnAnswer;                              // 拓展布局
    @BindView(R.id.tv_more_un_answer)
    TextView tvMoreUnAnswer;                                    // 拓展
    @BindView(R.id.etv_exercise_expand_un_answer)
    ExerciseTextView tvExpandUnAnswer;                          // 拓展内容
    @BindView(R.id.ll_point_un_answer)
    LinearLayout llPointUnAnswer;                               // 知识点布局
    @BindView(R.id.tv_point_un_answer)
    TextView tvPointUnAnswer;                                   // 知识点
    @BindView(R.id.tv_exercise_points_un_answer)
    TextView tvExercisePointUnAnswer;                           // 知识点内容
    @BindView(R.id.ll_sj_from)
    LinearLayout llSjFrom;                                      // 来源
    @BindView(R.id.tv_from_un_answer)
    TextView tvFromUnAnswer;                                    // 来源
    @BindView(R.id.tv_exercise_source_un_answer)
    TextView tvExerciseFromUnAnswer;                            // 来源内容

    @BindView(R.id.question_analysis_objective_layout)
    LinearLayout layoutObjective;                               // 选择题布局
    @BindView(R.id.tv_answer)
    TextView tvAnswer;                                          // 15sp "答案"两个字
    @BindView(R.id.tv_correct_answer)
    TextView tv_correct_answer;                                 // 15sp 答案内容选项

    @BindView(R.id.tv_my_answer)
    TextView tvMyAnswer;                                        // 15sp "你的选项"四个字
    @BindView(R.id.tv_my_answer_content)
    TextView tvMyAnswerContent;                                 // 15sp 建议用时内容

    @BindView(R.id.ll_time)
    LinearLayout llTime;                                        // 用时、正确率、易错项 总布局
    @BindView(R.id.tv_exercise_time)
    TextView tv_exercise_time;                                  // 15sp "作答用时"四个字
    @BindView(R.id.tv_time)
    TextView tvTime;                                            // 15sp 作答用时时间
    @BindView(R.id.tv_exercise_source2)
    TextView tv_exercise_source2;                               // 15sp "正确率"
    @BindView(R.id.tv_correct)
    TextView tvCorrect;                                         // 15sp 正确率
    @BindView(R.id.tv_exercise_source3)
    TextView tv_exercise_source3;                               // 15sp "易错项"
    @BindView(R.id.tv_easy_wrong)
    TextView tvEasyWrong;                                       // 15sp 易错项

    @BindView(R.id.tv_analysis)
    TextView tvAnalysis;                                        // 15sp "解析"
    @BindView(R.id.etv_exercise_analyze)
    ExerciseTextView etv_exercise_analyze;                      // 15sp 解析内容
    @BindView(R.id.ll_extend)
    LinearLayout ll_extend;                                     // 拓展布局
    @BindView(R.id.tv_more)
    TextView tvMore;                                            // 15sp "拓展"
    @BindView(R.id.etv_exercise_expand)
    ExerciseTextView etv_exercise_expand;                       // 15sp 拓展内容
    @BindView(R.id.ll_point)
    LinearLayout llPoint;                                       // 考点布局
    @BindView(R.id.tv_point)
    TextView tvPoint;                                           // 15sp "考点"
    @BindView(R.id.tv_exercise_points)
    TextView tv_exercise_points;                                // 15sp 考点内容
    @BindView(R.id.ll_from)
    LinearLayout llFrom;                                        // 来源布局（如果没有来源，就不显示）
    @BindView(R.id.tv_from)
    TextView tvFrom;                                            // 15sp "来源"
    @BindView(R.id.tv_exercise_source)
    TextView tv_exercise_source;                                // 15sp 来源内容

    private int resId = R.layout.arena_question_analysis_layout;
    private ArenaExamQuestionBean questionBean;

    public ArenaQuestionAnalysisLayoutNew(Context context) {
        super(context, null);
        mContext = context;
        init();
    }

    public ArenaQuestionAnalysisLayoutNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
        etvSubjectiveAnalysis.setCenterImg(true);       // 支持文字图片上下居中显示
        etv_exercise_analyze.setCenterImg(true);        // 支持文字图片上下居中显示
        etv_exercise_expand.setCenterImg(true);         // 支持文字图片上下居中显示
        tvExpandUnAnswer.setCenterImg(true);         // 支持文字图片上下居中显示
        setColor();
        setTextSize();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void updateViews(ArenaExamQuestionBean questionBean) {
        if (questionBean == null) {
            return;
        }
        this.questionBean = questionBean;
        if (questionBean.type == ArenaConstant.QUESTION_TYPE_SUBJECTIVE) {
            setSubjectiveView(questionBean);
        } else {
            setObjectiveView(questionBean);
        }
        requestLayout();
    }

    /**
     * 主观题
     */
    private void setSubjectiveView(ArenaExamQuestionBean questionBean) {

        layoutSubjective.setVisibility(VISIBLE);
        layoutObjective.setVisibility(GONE);
        // 主观题答案
        if (!StringUtils.isEmpty(questionBean.referAnalysis)) {
            tvTitle.setVisibility(VISIBLE);
            etvSubjectiveAnswer.setVisibility(VISIBLE);
            etvSubjectiveAnswer.setHtmlSource(questionBean.referAnalysis);
        } else {
            tvTitle.setVisibility(GONE);
            etvSubjectiveAnswer.setVisibility(GONE);
        }
        // 分析
        String analysis = "";
        if (!TextUtils.isEmpty(questionBean.answerRequire)) {
            analysis += "答题要求:\n";
            analysis += questionBean.answerRequire + "\n";
        }
        if (!TextUtils.isEmpty(questionBean.examPoint)) {
            analysis += "审题要求:\n";
            analysis += questionBean.examPoint + "\n";
        }
        if (!TextUtils.isEmpty(questionBean.scoreExplain)) {
            analysis += "赋分说明:\n";
            analysis += questionBean.scoreExplain + "\n";
        }
        if (!TextUtils.isEmpty(questionBean.solvingIdea)) {
            analysis += "解题思路:\n";
            analysis += questionBean.solvingIdea + "\n";
        }
        if (!TextUtils.isEmpty(analysis)) {
            tvSubAnakysisDes.setVisibility(VISIBLE);
            etvSubjectiveAnalysis.setVisibility(VISIBLE);
            etvSubjectiveAnalysis.setHtmlSource(analysis);
        } else {
            tvSubAnakysisDes.setVisibility(GONE);
            etvSubjectiveAnalysis.setVisibility(GONE);
        }

        // 主观的拓展
        if (!TextUtils.isEmpty(questionBean.extend)) {
            llExtendUnAnswer.setVisibility(VISIBLE);
            tvExpandUnAnswer.setHtmlSource(DisplayUtil.getScreenWidth() * 3 / 4, questionBean.extend);
        } else {
            llExtendUnAnswer.setVisibility(GONE);
        }
        // 考点
        if (questionBean.knowledgePointsList != null && questionBean.knowledgePointsList.size() > 0) {
            llPointUnAnswer.setVisibility(VISIBLE);
            String pointText = "";
            for (ArenaExamQuestionBean.KnowledgePoint point : questionBean.knowledgePointsList) {
                pointText = pointText + point.name + ",";
            }
            pointText = pointText.substring(0, pointText.length() - 1);
            tvExercisePointUnAnswer.setText(pointText);
        } else {
            llPointUnAnswer.setVisibility(GONE);
        }
        // 来源
        if (!StringUtils.isEmpty(questionBean.from)) {
            llSjFrom.setVisibility(VISIBLE);
            tvExerciseFromUnAnswer.setText(questionBean.from);
        } else {
            llSjFrom.setVisibility(GONE);
        }
    }

    /**
     * 非主观题
     */
    private void setObjectiveView(ArenaExamQuestionBean questionBean) {

        layoutSubjective.setVisibility(GONE);
        layoutObjective.setVisibility(VISIBLE);

        // 作答时间，正确率，易错项
        tvTime.setText(questionBean.usedTime + "秒");
        ExerciseBeans.Meta meta = questionBean.meta;
        String ycStr = "";
        String ycStr1 = "";
        if (meta != null) {
            int yc = meta.yc;
            String ycstr2 = String.valueOf(yc);
            if (ycstr2.contains("1"))
                ycStr += "A";
            if (ycstr2.contains("2"))
                ycStr += "B";
            if (ycstr2.contains("3"))
                ycStr += "C";
            if (ycstr2.contains("4"))
                ycStr += "D";
            if (ycstr2.contains("5"))
                ycStr += "E";
            if (ycstr2.contains("6"))
                ycStr += "F";
            if (ycstr2.contains("7"))
                ycStr += "G";
            if (ycstr2.contains("8"))
                ycStr += "H";
            if (ycstr2.contains("9"))
                ycStr += "I";

            int rindex = meta.rindex;
            List<Integer> percents = meta.percents;
            if (percents != null && percents.size() > rindex) {
                ycStr1 = percents.get(rindex) + "%";
            }
            tvCorrect.setText(ycStr1);
            tvEasyWrong.setText(ycStr);
        }

        // 正确答案
        String correctAnswer = String.valueOf(questionBean.answer);
        String answerText = "";
        if (correctAnswer.contains("1"))
            answerText += "A";
        if (correctAnswer.contains("2"))
            answerText += "B";
        if (correctAnswer.contains("3"))
            answerText += "C";
        if (correctAnswer.contains("4"))
            answerText += "D";
        if (correctAnswer.contains("5"))
            answerText += "E";
        if (correctAnswer.contains("6"))
            answerText += "F";
        if (correctAnswer.contains("7"))
            answerText += "G";
        if (correctAnswer.contains("8"))
            answerText += "H";
        if (correctAnswer.contains("9"))
            answerText += "I";
        tv_correct_answer.setText(answerText);
        // 多选显示我的答案，选错的红色，选对的绿色
        int nightMode = SpUtils.getDayNightMode();
        if (questionBean.isSingleChoice || questionBean.userAnswer == 0) {      // 单选、未作答，都不显示我的答案。只有多选且作答了，才显示我的答案
            tvMyAnswer.setVisibility(GONE);
            tvMyAnswerContent.setVisibility(GONE);
        } else {
            tvMyAnswer.setVisibility(VISIBLE);
            tvMyAnswerContent.setVisibility(VISIBLE);

            String colorRight;
            String colorWrong;
            if (nightMode == 1) {
                colorRight = "#2A5243";
                colorWrong = "#6B3132";
            } else {
                colorRight = "#49CF9E";
                colorWrong = "#FF3F47";
            }

            String userAnswer = String.valueOf(questionBean.userAnswer);

            StringBuilder answerTextX = new StringBuilder();
            for (int i = 0; i < userAnswer.length(); i++) {
                char c = userAnswer.charAt(i);
                if (correctAnswer.contains(String.valueOf(c))) {
                    answerTextX.append("<font color=\"").append(colorRight).append("\">").append(option[Integer.valueOf(String.valueOf(c)) - 1]).append("</font>");
                } else {
                    answerTextX.append("<font color=\"").append(colorWrong).append("\">").append(option[Integer.valueOf(String.valueOf(c)) - 1]).append("</font>");
                }
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvMyAnswerContent.setText(Html.fromHtml(answerTextX.toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvMyAnswerContent.setText(Html.fromHtml(answerTextX.toString()));
            }
        }
        // 解析
        if (etv_exercise_analyze != null && !StringUtils.isEmpty(questionBean.analysis)) {
            tvAnalysis.setVisibility(VISIBLE);
            etv_exercise_analyze.setVisibility(VISIBLE);
            etv_exercise_analyze.setHtmlSource(DisplayUtil.getScreenWidth() * 3 / 4, questionBean.analysis);
        } else {
            tvAnalysis.setVisibility(GONE);
            etv_exercise_analyze.setVisibility(GONE);
        }
        // 拓展
        if (!TextUtils.isEmpty(questionBean.extend)) {
            ll_extend.setVisibility(VISIBLE);
            etv_exercise_expand.setHtmlSource(DisplayUtil.getScreenWidth() * 3 / 4, questionBean.extend);
        } else {
            ll_extend.setVisibility(GONE);
        }
        // 知识点
        if (questionBean.knowledgePointsList != null && questionBean.knowledgePointsList.size() > 0) {
            String pointText = "";
            for (ArenaExamQuestionBean.KnowledgePoint point : questionBean.knowledgePointsList) {
                pointText = pointText + point.name + ",";
            }
            pointText = pointText.substring(0, pointText.length() - 1);
            tv_exercise_points.setText(pointText);
            llPoint.setVisibility(VISIBLE);
        } else {
            llPoint.setVisibility(GONE);
        }
        // 来源
        if (StringUtils.isEmpty(questionBean.from)) {
            llFrom.setVisibility(GONE);
        } else {
            tv_exercise_source.setText(questionBean.from);
            llFrom.setVisibility(VISIBLE);
        }
    }


    /**
     * 设置字体大小
     */
    public void setTextSize() {

        int fontSizeMode = SpUtils.getFontSizeMode();

        int[] size_15 = {15, 13, 17};   // 0、正常 1、小号 2、大号

        int now_15 = size_15[fontSizeMode];

        tvTitle.setTextSize(now_15);                                           // 18sp
        tvAnswer.setTextSize(now_15);                                          // 15sp
        etvSubjectiveAnswer.setTextSize(now_15);                               // 18sp
        etvSubjectiveAnalysis.setTextSize(now_15);                             // 18sp
        tvSubAnakysisDes.setTextSize(now_15);                                  // 18sp
        tvMoreUnAnswer.setTextSize(now_15);                                  // 18sp
        tvExpandUnAnswer.setTextSize(now_15);                                  // 18sp
        tvPointUnAnswer.setTextSize(now_15);                                  // 18sp
        tvExercisePointUnAnswer.setTextSize(now_15);                                  // 18sp
        tvFromUnAnswer.setTextSize(now_15);                                  // 18sp
        tvExerciseFromUnAnswer.setTextSize(now_15);                                  // 18sp

        tvAnalysis.setTextSize(now_15);                                        // 15sp
        tvMore.setTextSize(now_15);                                            // 15sp
        tvPoint.setTextSize(now_15);                                           // 15sp
        tvFrom.setTextSize(now_15);                                            // 15sp
        tv_correct_answer.setTextSize(now_15);                                 // 15sp
        tv_exercise_time.setTextSize(now_15);                                  // 15sp
        tvTime.setTextSize(now_15);                                            // 15sp
        tvMyAnswerContent.setTextSize(now_15);                                 // 15sp
        tvMyAnswer.setTextSize(now_15);                                        // 15sp
        tv_exercise_source.setTextSize(now_15);                                // 15sp
        tvCorrect.setTextSize(now_15);                                         // 15sp
        tv_exercise_source2.setTextSize(now_15);                               // 15sp
        tv_exercise_source3.setTextSize(now_15);                               // 15sp
        tvEasyWrong.setTextSize(now_15);                                       // 15sp
        tv_exercise_points.setTextSize(now_15);                                // 15sp
        etv_exercise_analyze.setTextSize(now_15);                              // 15sp
        etv_exercise_expand.setTextSize(now_15);                               // 15sp
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {                           // 日间模式
            setBgColor(etvSubjectiveAnswer, R.color.arena_common_bg);
            setBgColor(etvSubjectiveAnalysis, R.color.arena_common_bg);
            setBgColor(tvExpandUnAnswer, R.color.arena_common_bg);

            setBgColor(llTime, R.color.arena_analysis_time_bg);
            setTextColor(tvTime, R.color.arena_analysis_time_text);
            setTextColor(tvCorrect, R.color.arena_analysis_correct_text);
            setTextColor(tvEasyWrong, R.color.arena_analysis_easy_wrong_text);

            setTextColor(tv_correct_answer, R.color.arena_analysis_option_green);

            setBgColor(etv_exercise_analyze, R.color.arena_common_bg);
            setBgColor(etv_exercise_expand, R.color.arena_common_bg);
        } else {                                        // 夜间模式
            setBgColor(etvSubjectiveAnswer, R.color.arena_common_bg_night);
            setBgColor(etvSubjectiveAnalysis, R.color.arena_common_bg_night);
            setBgColor(tvExpandUnAnswer, R.color.arena_common_bg_night);

            setBgColor(llTime, R.color.arena_analysis_time_bg_night);
            setTextColor(tvTime, R.color.arena_analysis_time_text_night);
            setTextColor(tvCorrect, R.color.arena_analysis_correct_text_night);
            setTextColor(tvEasyWrong, R.color.arena_analysis_easy_wrong_text_night);

            setTextColor(tv_correct_answer, R.color.arena_analysis_option_green_night);

            setBgColor(etv_exercise_analyze, R.color.arena_common_bg_night);
            setBgColor(etv_exercise_expand, R.color.arena_common_bg_night);
        }
    }

    private void setBgColor(View v, int color) {
        if (v != null) {
            v.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        }
    }

    private void setTextColor(TextView tv, int color) {
        if (tv != null) {
            tv.setTextColor(ContextCompat.getColor(getContext(), color));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expect = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expect);
    }

    @Override
    public void nightSwitch() {
        setColor();
        if (this.questionBean != null) {
            updateViews(this.questionBean);
        }
    }

    @Override
    public void sizeSwitch() {
        setTextSize();
    }
}
