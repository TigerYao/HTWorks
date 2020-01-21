package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arch.ActivityDataBus;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaExamOptionNewAdapter;
import com.huatu.handheld_huatu.business.arena.bean.ArenaDataBus;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.textselect.TextSelectManager;
import com.huatu.handheld_huatu.business.arena.textselect.impl.MarkerPenMenu;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArenaQuestionBody extends FrameLayout implements NightSwitchInterface, TextSizeSwitchInterface {

    private Context mContext;
    private View rootView;

    private int requestType;

    @BindView(R.id.tv_question)
    ExerciseTextView tvQuestion;                                    // 问题描述
    @BindView(R.id.rl_option)
    RecyclerView rlOption;                                          // 答案选项

    @BindView(R.id.ll_no_answer)
    View llNoAnswer;                                                // 不支持作答
    @BindView(R.id.tv_no_answer_des)
    TextView tvNoAnswerDes;                                         // 不支持作答

    @BindView(R.id.layout_analysis)
    ArenaQuestionAnalysisLayoutNew layoutAnalysis;                  // 答案分析

    private ArenaQuestionViewListener arenaQuestionViewNew;

    private ArenaExamQuestionBean questionBean = new ArenaExamQuestionBean();
    private ArenaExamOptionNewAdapter optionAdapter;                // 答案选项Adapter

    private int resId = R.layout.arena_question_body;

    public ArenaQuestionBody(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ArenaQuestionBody(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaQuestionBody(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);

        tvQuestion.setCenterImg(true);                  // 文字和图片上下居中
        rlOption.setNestedScrollingEnabled(false);

        setTextSize();
        setColor();


        requestType = ActivityDataBus.getData(context, ArenaDataBus.class).requestType;

        // 做题模式下，并且不是背题模式，开始复制高亮功能
        if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {
            TextSelectManager.newInstance().openSelectDealStyle(10, tvQuestion);
        }
    }

    public void setQuestionView(ArenaQuestionViewListener arenaQuestionViewNew) {
        this.arenaQuestionViewNew = arenaQuestionViewNew;
    }

    public void setQuestionBean(ArenaExamQuestionBean bean) {
        this.questionBean = bean;
        updateViews();
        if (tvQuestion != null) {
            tvQuestion.setFocusableInTouchMode(true);
            tvQuestion.requestFocus();
        }
    }

    private void updateViews() {

        if (questionBean.type == ArenaConstant.QUESTION_TYPE_SUBJECTIVE) {
            if (questionBean.seeType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {
                llNoAnswer.setVisibility(VISIBLE);
                tvNoAnswerDes.setVisibility(VISIBLE);
            } else {
                llNoAnswer.setVisibility(GONE);
                tvNoAnswerDes.setVisibility(GONE);
            }
        } else {
            llNoAnswer.setVisibility(GONE);
        }

        updateAnalysis();
        showBody();
        setOption();
    }

    private void showBody() {
        int nightMode = SpUtils.getDayNightMode();
        String color;
        if (nightMode == 1) {
            color = "#421B29";
        } else {
            color = "#D3688F";
        }

        if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {
            MarkerPenMenu markMenu = (MarkerPenMenu) tvQuestion.getSelectTool().markMenu;
            markMenu.setMarkInfoList(questionBean.questionMark);
            markMenu.setQuestionId(questionBean.id);
        }

        if (!TextUtils.isEmpty(questionBean.stem)) {
            String source = processStartSpace(questionBean.stem);
            int pos = source.indexOf("<p>");
            String extra = "";
            switch (questionBean.type) {
                // 试题类型，单选：99，多选：100，不定项：101，对错题：109，复合题：105，
                case ArenaConstant.EXAM_QUESTION_TYPE_SINGLE_CHOICE:
                    extra = "<font color=\"" + color + "\">(单选题)</font>";
                    break;
                case ArenaConstant.EXAM_QUESTION_TYPE_MULTIPLE_CHOICE:
                    extra = "<font color=\"" + color + "\">(多选题)</font>";
                    break;
                case ArenaConstant.EXAM_QUESTION_TYPE_UNDIFINE_CHOICE:
                    extra = "<font color=\"" + color + "\">(不定项选择)</font>";
                    break;
                case ArenaConstant.EXAM_QUESTION_TYPE_JUDGEMENT:
                    extra = "<font color=\"" + color + "\">(判断题)</font>";
                    break;
                case ArenaConstant.EXAM_QUESTION_TYPE_COMPLEX:
                    extra = "<font color=\"" + color + "\">(复合题)</font>";
                    break;
                case ArenaConstant.QUESTION_TYPE_SUBJECTIVE:
                    if (TextUtils.isEmpty(questionBean.teachType)) {
                        extra = "<font color=\"" + color + "\">(主观题)</font>";
                    } else {
                        extra = "<font color=\"" + color + "\">(" + questionBean.teachType + ")</font>";
                    }
                    break;
                default:
                    if (TextUtils.isEmpty(questionBean.teachType)) {
                        extra = "<font color=\"" + color + "\">(单选题)</font>";
                    } else {
                        extra = "<font color=\"" + color + "\">(" + questionBean.teachType + ")</font>";
                    }
                    break;
            }
            String data;
            if (pos != -1) {
                data = source.substring(0, pos + 3) + extra + source.substring(pos + 3);
            } else {
                data = extra + source;
            }
            tvQuestion.setHtmlSource(DisplayUtil.dp2px(360 - 24), data);
        } else {
            tvQuestion.setText("");
        }
    }

    private void updateAnalysis() {
        if (questionBean.seeType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {
            layoutAnalysis.setVisibility(View.GONE);
        } else {
            layoutAnalysis.updateViews(questionBean);
            layoutAnalysis.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 去掉第一行开始的空格
     */
    private String processStartSpace(String content) {
        // 后台传的空格
        while (content.startsWith(" ")) {
            content = content.substring(1);
        }
        while (content.startsWith("<p> ")) {
            content = "<p>" + content.substring(4);
        }
        // 电脑键盘打的空格
        while (content.startsWith(" ")) {
            content = content.substring(1);
        }
        while (content.startsWith("<p> ")) {
            content = "<p>" + content.substring(4);
        }
        return content;
    }

    private void setOption() {
        if (optionAdapter == null) {
            optionAdapter = new ArenaExamOptionNewAdapter(questionBean, arenaQuestionViewNew);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            rlOption.setLayoutManager(linearLayoutManager);
            rlOption.setAdapter(optionAdapter);
        } else {
            optionAdapter.setDataAndNotify(questionBean);
        }
    }

    public void showAnalysis() {
        if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_AFTER) {
            updateAnalysis();
            if (optionAdapter != null) {
                optionAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 设置字体大小
     * 别忘了，还有个答题页面
     */
    public void setTextSize() {

        int fontSizeMode = SpUtils.getFontSizeMode();

//        int[] size_12 = {12, 10, 14};   // 0、正常 1、小号 2、大号
        int[] size_14 = {14, 12, 16};   // 0、正常 1、小号 2、大号
        int[] size_16 = {16, 14, 18};   // 0、正常 1、小号 2、大号

//        int now_12 = size_12[fontSizeMode];
        int now_14 = size_14[fontSizeMode];
        int now_16 = size_16[fontSizeMode];

        tvQuestion.setTextSize(now_16);
        tvNoAnswerDes.setTextSize(now_14);
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            setBgColor(tvQuestion, R.color.arena_common_bg);
        } else {
            setBgColor(tvQuestion, R.color.arena_common_bg_night);
        }
    }

    private void setTextColor(TextView tv, int color) {
        if (tv != null) {
            tv.setTextColor(ContextCompat.getColor(getContext(), color));
        }
    }

    private void setBgColor(View v, int color) {
        if (v != null) {
            v.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        }
    }

    @Override
    public void nightSwitch() {
        setColor();
        layoutAnalysis.nightSwitch();
        showBody();
        if (optionAdapter != null) {
            optionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void sizeSwitch() {
        setTextSize();
        layoutAnalysis.sizeSwitch();
        if (optionAdapter != null) {
            optionAdapter.notifyDataSetChanged();
        }
    }
}
