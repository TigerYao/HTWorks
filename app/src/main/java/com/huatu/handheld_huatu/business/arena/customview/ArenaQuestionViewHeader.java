package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArenaQuestionViewHeader extends FrameLayout implements NightSwitchInterface, TextSizeSwitchInterface {

    private Context mContext;
    private View rootView;

    @BindView(R.id.ll_question_des)
    LinearLayout llDesLayout;                                       // 问题类型布局
    @BindView(R.id.tv_subject)
    TextView tvSubjectName;                                         // 问题类型
    @BindView(R.id.iv_doubt)
    ImageView icDoubt;                                              // 有问题
    @BindView(R.id.tv_question_index)
    TextView tvQuestionIndex;                                       // 第几个问题
    @BindView(R.id.tv_question_number)
    TextView tvQuestionNumber;                                      // 共几个问题

    private ArenaExamQuestionBean questionBean = new ArenaExamQuestionBean();
    private int fromType;                                           // 从哪里过来的（现在应用于背题模式直接解析，需要知道是哪里过来，然后设置name）
    private int currentIndex = 0;                                   // 请求类型
    private int totalCount = 0;                                     // 总提数

    private int resId = R.layout.arena_question_header;

    public ArenaQuestionViewHeader(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ArenaQuestionViewHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaQuestionViewHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
        setTextSize();
        setTitleDefColor();
    }

    public void setQuestionBean(int fromType, ArenaExamQuestionBean bean, int count, int current) {
        this.fromType = fromType;
        this.questionBean = bean;
        this.totalCount = count;
        this.currentIndex = current;
        updateViews();
    }

    private void updateViews() {
        if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {                      // 解析全部
            setTitleColor();
            if (fromType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                    || fromType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {    // 背题查看，需要显示name
                tvSubjectName.setText(questionBean.name);
            } else {
                setQuestionName();
            }
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE
                || questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_AFTER) {            // 背题模式
            if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_AFTER) {   // 查看解析，要显示Title颜色
                setTitleColor();
            } else {                                                                 // 背题之前，要初始化Title
                setTitleDefColor();
            }
            tvSubjectName.setText(questionBean.name);
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG) {             // 错题解析
            setQuestionName();
            setTitleColor();
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI) {            // 错题练习
            tvSubjectName.setText("错题重练");
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE) {             // 智能刷题
            setQuestionName();
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE) {          // 收藏解析
            setTitleColor();
            tvSubjectName.setText("我的收藏");
        } else if ((questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN            // 真题演练
                || questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN)) {             // 模考估分
            setQuestionName();
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_SINGLE) {            // 单体解析
            tvSubjectName.setText("单题解析");
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH
                || questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_STAGE_TEST) {              // 小模考、阶段测试显示试卷名称
            tvSubjectName.setText(questionBean.name);
        } else if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ERROR_EXPORT) {            // 错题导出
            tvSubjectName.setText("错题下载");
        } else {
            setQuestionName();
        }

        setDoubtState();
        tvQuestionIndex.setText((currentIndex + 1) + "");
        tvQuestionNumber.setText("/" + totalCount);
    }

    // 设置问题名称
    private void setQuestionName() {
        if (!StringUtils.isEmpty(questionBean.categoryName)) {
            tvSubjectName.setText(questionBean.categoryName);
        } else {
            tvSubjectName.setText(questionBean.name);
        }
    }

    /**
     * 设置字体大小
     * 别忘了，还有个答题页面
     */
    public void setTextSize() {

        int fontSizeMode = SpUtils.getFontSizeMode();

        int[] size_14 = {14, 12, 16};   // 0、正常 1、小号 2、大号
//        int[] size_16 = {16, 14, 18};   // 0、正常 1、小号 2、大号
//        int[] size_12 = {12, 10, 14};   // 0、正常 1、小号 2、大号

//        int now_12 = size_12[fontSizeMode];
        int now_14 = size_14[fontSizeMode];
//        int now_16 = size_16[fontSizeMode];

        tvSubjectName.setTextSize(now_14);
        tvQuestionIndex.setTextSize(now_14);
        tvQuestionNumber.setTextSize(now_14);
    }

    public void setDoubtState() {
        if (questionBean.seeType >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                || questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {
            icDoubt.setVisibility(GONE);
            return;
        }
        int nightMode = SpUtils.getDayNightMode();
        icDoubt.setVisibility(VISIBLE);
        if (questionBean.doubt == 0) {
            if (nightMode == 0) {
                icDoubt.setImageResource(R.drawable.ic_doubt_unchecked_light);
            } else {
                icDoubt.setImageResource(R.drawable.ic_doubt_unchecked);
            }
        } else if (questionBean.doubt == 1) {
            if (nightMode == 0) {
                icDoubt.setImageResource(R.drawable.ic_doubt_checked_light);
            } else {
                icDoubt.setImageResource(R.drawable.ic_doubt_checked);
            }
        }
    }

    private void setTitleDefColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            setBgColor(llDesLayout, R.color.arena_top_title_bg);
            setTextColor(tvSubjectName, R.color.arena_subject_name_text);
            setTextColor(tvQuestionIndex, R.color.arena_red_index_text);
            setTextColor(tvQuestionNumber, R.color.arena_subject_name_text);
        } else {
            setBgColor(llDesLayout, R.color.arena_top_title_bg_night);
            setTextColor(tvSubjectName, R.color.arena_subject_name_text_night);
            setTextColor(tvQuestionIndex, R.color.arena_subject_name_text_night);
            setTextColor(tvQuestionNumber, R.color.arena_subject_name_text_night);
        }
    }

    private void setTitleColor() {
        if (questionBean.isCorrect == 1 || questionBean.isCorrect == 2) {
            int nightMode = SpUtils.getDayNightMode();
            if (nightMode == 0) {           // 日间模式
                if (questionBean.isCorrect == 1) {
                    setBgColor(llDesLayout, R.color.arena_exam_title_green_light_bg);             // 正确，绿色
                    setTextColor(tvQuestionIndex, R.color.arena_exam_title_green_light_text);
                    setTextColor(tvQuestionNumber, R.color.arena_exam_title_green_light_text);
                    setTextColor(tvSubjectName, R.color.arena_exam_title_green_light_text);
                } else if (questionBean.isCorrect == 2) {
                    setBgColor(llDesLayout, R.color.arena_exam_title_red_light_bg);               // 错题，红色
                    setTextColor(tvQuestionIndex, R.color.arena_exam_title_red_light_text);
                    setTextColor(tvQuestionNumber, R.color.arena_exam_title_red_light_text);
                    setTextColor(tvSubjectName, R.color.arena_exam_title_red_light_text);
                }
            } else {                        // 夜间模式
                if (questionBean.isCorrect == 1) {
                    setBgColor(llDesLayout, R.color.arena_exam_green_night_bg);             // 正确，绿色
                    setTextColor(tvQuestionIndex, R.color.arena_exam_green_night_text);
                    setTextColor(tvQuestionNumber, R.color.arena_exam_green_night_text);
                    setTextColor(tvSubjectName, R.color.arena_exam_green_night_text);
                } else if (questionBean.isCorrect == 2) {
                    setBgColor(llDesLayout, R.color.arena_exam_red_night_bg);               // 错题，红色
                    setTextColor(tvQuestionIndex, R.color.arena_exam_red_night_text);
                    setTextColor(tvQuestionNumber, R.color.arena_exam_red_night_text);
                    setTextColor(tvSubjectName, R.color.arena_exam_red_night_text);
                }
            }
        } else {
            setTitleDefColor();
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
        setDoubtState();
        if (questionBean.seeType >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {
            setTitleColor();
        } else {
            setTitleDefColor();
        }
    }

    @Override
    public void sizeSwitch() {
        setTextSize();
    }
}
