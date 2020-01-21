package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.customview.ArenaQuestionViewListener;
import com.huatu.handheld_huatu.business.arena.customview.ArenaQuestionViewNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2016/10/17.
 */
public class ArenaExamOptionNewAdapter extends RecyclerView.Adapter<ArenaExamOptionNewAdapter.OptionHolder> {

    private Context mContext;

    private ArenaExamQuestionBean questionBean;
    private ArenaQuestionViewListener questionViewNew;
    private int now_16 = 16;

    public ArenaExamOptionNewAdapter(ArenaExamQuestionBean bean, ArenaQuestionViewListener questionViewNew) {
        this.questionBean = bean;
        this.questionViewNew = questionViewNew;
        setTextSize();
    }

    public void setDataAndNotify(ArenaExamQuestionBean bean) {
        this.questionBean = bean;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_arena_question_adapter_layout, parent, false);
        return new OptionHolder(layout);
    }

    @Override
    public void onBindViewHolder(OptionHolder viewHolder, int position) {
        if (questionBean == null || questionBean.questionOptions == null || position < 0 || position >= questionBean.questionOptions.size()){
            return;
        }
        ArenaExamQuestionBean.QuestionOption item = questionBean.questionOptions.get(position);
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 1) {
            viewHolder.etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_common_bg_night));
            viewHolder.convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_common_bg_night));
            setTextColor(viewHolder.etv_answer_option, R.color.arena_exam_common_text_night);
        } else {
            viewHolder.etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_common_bg));
            viewHolder.convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_common_bg));
            setTextColor(viewHolder.etv_answer_option, R.color.arena_exam_common_text);
        }
        if (item.isSelected) {              // 选择过
            if (questionBean.seeType >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {      // 如果是解析
                if (questionBean.isCorrect == 1) {      // 正确
                    setOptionView(viewHolder.tv_choice, position, 4, nightMode);
                } else {                                // 错误
                    if (questionBean.isSingleChoice) {      // 单选
                        setOptionView(viewHolder.tv_choice, position, 5, nightMode);
                    } else {                                // 多选
                        String correctAns = String.valueOf(questionBean.answer);
                        String a = String.valueOf(position + 1);
                        if (correctAns.contains(a)) {           // 包含
                            setOptionView(viewHolder.tv_choice, position, 4, nightMode);
                        } else {                                // 不包含
                            setOptionView(viewHolder.tv_choice, position, 5, nightMode);
                        }
                    }
                }
            } else {                                                                // 如果是答题，就直接显示选择了
                setOptionView(viewHolder.tv_choice, position, 3, nightMode);
            }
        } else if (item.isDeleted) {        // 长按删除了
            setOptionView(viewHolder.tv_choice, position, 1, nightMode);
            if (nightMode == 1) {               // 夜间
                viewHolder.convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_option_delete_bg_night));
                viewHolder.etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_option_delete_bg_night));
                setTextColor(viewHolder.etv_answer_option, R.color.arena_option_delete_text_night);
            } else {                            // 日间
                viewHolder.convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_option_delete_bg));
                viewHolder.etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_option_delete_bg));
                setTextColor(viewHolder.etv_answer_option, R.color.arena_option_delete_text);
            }
        } else {                            // 没选择
            if (questionBean.seeType >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {      // 解析                                                            // 多选
                String correctAns = String.valueOf(questionBean.answer);
                String a = String.valueOf(position + 1);
                if (correctAns.contains(a)) {           // 包含--正确
                    setOptionView(viewHolder.tv_choice, position, 2, nightMode);
                } else {                                // 不包含--默认
                    setOptionView(viewHolder.tv_choice, position, 0, nightMode);
                }
            } else {                                                                // 答题
                setOptionView(viewHolder.tv_choice, position, 0, nightMode);
            }
        }
        String data = item.optionDes;
        data = data.replace("\t", "").replace("<p>", "").replace("</p>", "");
        if (data.contains("http") || data.contains("png") || data.contains("jpg")
                || data.contains("width") && data.contains("height")) {
            data = data + "&nbsp";
        }
        viewHolder.etv_answer_option.setHtmlSource(DisplayUtil.getScreenWidth() * 3 / 4, data);
        viewHolder.etv_answer_option.setTextSize(now_16);
    }

    private String[] option = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S"};

    private int[] singleOptionLight = {
            R.mipmap.option_single_def_light,
            R.mipmap.option_single_del_light,
            R.mipmap.option_single_right_light,
            R.mipmap.option_single_select_light,
            R.mipmap.option_single_select_right_light,
            R.mipmap.option_single_wrong_light};

    private int[] singleOptionNight = {
            R.mipmap.option_single_def_night,
            R.mipmap.option_single_del_night,
            R.mipmap.option_single_right_night,
            R.mipmap.option_single_select_night,
            R.mipmap.option_single_select_right_night,
            R.mipmap.option_single_wrong_night};

    private int[] multiOptionLight = {
            R.mipmap.option_multi_def_light,
            R.mipmap.option_multi_del_light,
            R.mipmap.option_multi_right_light,
            R.mipmap.option_multi_select_light,
            R.mipmap.option_multi_select_right_light,
            R.mipmap.option_multi_wrong_light};

    private int[] multiOptionNight = {
            R.mipmap.option_multi_def_night,
            R.mipmap.option_multi_del_night,
            R.mipmap.option_multi_right_night,
            R.mipmap.option_multi_select_night,
            R.mipmap.option_multi_select_right_night,
            R.mipmap.option_multi_wrong_night};

    private String[] optionColorLight = {
            "#4A4A4A",
            "#9B9B9B",
            "#49CF9E",
            "#FFFFFF",
            "#FFFFFF",
            "#FFFFFF",
    };

    private String[] optionColorNight = {
            "#4A4A4A",
            "#4A4A4A",
            "#2C4E42",
            "#1D1D1D",
            "#1D1D1D",
            "#1D1D1D",
    };

    /**
     * 给选项图标 A、B、C、 设置内容和样式
     *
     * @param tv_choice 图标
     * @param position  第几个，用于填ABC...
     * @param style     style   0、默认 1、长按删除 2、未选择的正确答案 3、选择 4、选择且正确 5、选择错误
     * @param nightMode 日夜间模式
     */
    private void setOptionView(TextView tv_choice, int position, int style, int nightMode) {
        tv_choice.setText(option[position]);
        if (nightMode == 1) {
            tv_choice.setTextColor(Color.parseColor(optionColorNight[style]));
        } else {
            tv_choice.setTextColor(Color.parseColor(optionColorLight[style]));
        }
        if (questionBean.isSingleChoice) {
            if (nightMode == 1) {
                tv_choice.setBackgroundResource(singleOptionNight[style]);
            } else {
                tv_choice.setBackgroundResource(singleOptionLight[style]);
            }
        } else {
            if (nightMode == 1) {
                tv_choice.setBackgroundResource(multiOptionNight[style]);
            } else {
                tv_choice.setBackgroundResource(multiOptionLight[style]);
            }
        }
    }

    private void setTextColor(TextView tv, int color) {
        if (tv != null) {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), color));
        }
    }

    private void startJumpToNext() {
        ArenaExamMessageEvent mainEvent = new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION);
        mainEvent.extraBundle = new Bundle();
        mainEvent.extraBundle.putInt("index", questionBean.index);
        mainEvent.tag = "ArenaExamQuestionAdapter";
        EventBus.getDefault().post(mainEvent);
    }

    @Override
    public int getItemCount() {
        if (questionBean == null || questionBean.questionOptions == null) {
            return 0;
        }
        return questionBean.questionOptions.size();
    }

    public class OptionHolder extends RecyclerView.ViewHolder {

        public View convertView;
        @BindView(R.id.tv_choice)
        public TextView tv_choice;
        @BindView(R.id.etv_answer_option)
        public ExerciseTextView etv_answer_option;

        OptionHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            convertView = itemView;
            etv_answer_option.setCenterImg(true);                // 支持文字图片上下居中

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!ArenaDataCache.getInstance().isEnableExam()) {
                        return;
                    }

                    // 如果是解析就不设置点击事件了
                    if (questionBean.seeType >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {
                        return;
                    }

                    int position = getAdapterPosition();
                    if (questionBean == null || questionBean.questionOptions == null || position < 0 || position >= questionBean.questionOptions.size()){
                        return;
                    }
                    ArenaExamQuestionBean.QuestionOption item = questionBean.questionOptions.get(position);

                    boolean isSingleSelected = false;
                    if (item.isSelected) {                      // 如果此选项被选了，就变为不选中。用户答案重制。
                        item.isSelected = false;
                        questionBean.userAnswer = 0;
                    } else {                                    // 如果选项没被选
                        if (item.isDeleted) {                       // 如果被删除，就不删除
                            item.isDeleted = false;
                        } else {
                            if (questionBean.isSingleChoice) {      // 如果是单选，就把其他选项变为不选
                                for (int i = 0; i < questionBean.questionOptions.size(); i++) {
                                    questionBean.questionOptions.get(i).isSelected = false;
                                }
                            }
                            item.isSelected = true;
                            item.isDeleted = false;
                            if (questionBean.isSingleChoice) {
                                isSingleSelected = true;
                            }
                        }
                    }

                    // 把选择的选项对应成数字
                    // 对应的题的作答答案. 0表示未做答,数字和字母对应关系: 1=>A,2=>B,3=>C,4=>D,5=>E,6=>F,7=>G,8=>H,答案AB转换后为12
                    int answer = 0;
                    for (int j = 0; j < questionBean.questionOptions.size(); j++) {
                        if (questionBean.questionOptions.get(j).isSelected) {
                            answer = answer * 10 + (j + 1);
                        }
                    }
                    questionBean.userAnswer = answer;

                    // 判断对错
                    if (answer == 0) {
                        questionBean.isCorrect = 0;
                    } else if (answer == questionBean.answer) {
                        questionBean.isCorrect = 1;
                    } else {
                        questionBean.isCorrect = 2;
                    }
                    if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {         // 背题模式
                        if (questionViewNew != null) {
                            if (questionBean.type == 100 || questionBean.type == 101) {             // 多选、不定项   弹出确定按钮
                                if (questionBean.userAnswer != 0) {         // 有答题显示按钮
                                    questionViewNew.showMutlBtn();
                                } else {                                    // 未答题隐藏按钮
                                    questionViewNew.hideMutlBtn();
                                }
                            } else {                                                                // 单选          直接显示解析
                                questionViewNew.showAnswer();
                            }
                        }
                    } else if (isSingleSelected) {                                              // 如果是单选，就跳下一题
                        startJumpToNext();
                    }
                    ArenaExamOptionNewAdapter.this.notifyDataSetChanged();
                }
            };

            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!ArenaDataCache.getInstance().isEnableExam()) {
                        return false;
                    }

                    // 如果是解析就不设置点击事件了
                    if (questionBean.seeType >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {
                        return false;
                    }

                    int position = getAdapterPosition();
                    if (questionBean == null || questionBean.questionOptions == null || position < 0 || position >= questionBean.questionOptions.size()){
                        return false;
                    }
                    ArenaExamQuestionBean.QuestionOption item = questionBean.questionOptions.get(position);

                    if (!item.isSelected) {
                        if (item.isDeleted) {
                            item.isDeleted = false;
                        } else {
                            item.isDeleted = true;
                        }
                    }
                    ArenaExamOptionNewAdapter.this.notifyDataSetChanged();
                    return true;
                }
            };

            View.OnTouchListener onTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    if (!ArenaDataCache.getInstance().isEnableExam()) {
                        return false;
                    }

                    // 如果是解析就不设置点击事件了
                    if (questionBean.seeType >= ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL) {
                        return false;
                    }
                    int position = getAdapterPosition();
                    if (questionBean == null || questionBean.questionOptions == null || position < 0 || position >= questionBean.questionOptions.size()){
                        return false;
                    }
                    ArenaExamQuestionBean.QuestionOption item = questionBean.questionOptions.get(position);
                    int nightMode = SpUtils.getDayNightMode();

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:   // 选项按下
                            if (nightMode == 1) {           // 夜间
                                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_option_bg_night_pressed));
                                etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_option_bg_night_pressed));
                            } else {                        // 日间
                                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_option_bg_pressed));
                                etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_option_bg_pressed));
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:     // 选项抬起，就是默认颜色
                            if (!item.isDeleted) {
                                if (nightMode == 1) {
                                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_exam_common_bg_night));
                                    etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_exam_common_bg_night));
                                    setTextColor(etv_answer_option, R.color.arena_exam_common_text_night);
                                } else {
                                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_exam_common_bg));
                                    etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_exam_common_bg));
                                    setTextColor(etv_answer_option, R.color.arena_exam_common_text);
                                }
                            } else {
                                // 0、默认 1、长按删除 2、未选择的正确答案 3、选择 4、选择且正确 5、选择错误
                                setOptionView(tv_choice, getAdapterPosition(), 1, nightMode);
                                if (nightMode == 1) {
                                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_option_delete_bg_night));
                                    etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_option_delete_bg_night));
                                    setTextColor(etv_answer_option, R.color.arena_option_delete_text_night);
                                } else {
                                    convertView.setBackgroundColor(mContext.getResources().getColor(R.color.arena_option_delete_bg));
                                    etv_answer_option.setBackgroundColor(ContextCompat.getColor(mContext, R.color.arena_option_delete_bg));
                                    setTextColor(etv_answer_option, R.color.arena_option_delete_text);
                                }
                            }
                            break;
                    }
                    return false;
                }
            };

            convertView.setOnClickListener(onClickListener);
            convertView.setOnLongClickListener(longClickListener);
            etv_answer_option.setOnClickListener(onClickListener);
            etv_answer_option.setOnLongClickListener(longClickListener);
            convertView.setOnTouchListener(onTouchListener);
            etv_answer_option.setOnTouchListener(onTouchListener);
        }
    }

    public void setTextSize() {
        int fontSizeMode = SpUtils.getFontSizeMode();
        int[] size_16 = {16, 14, 18};
        now_16 = size_16[fontSizeMode];
    }
}
