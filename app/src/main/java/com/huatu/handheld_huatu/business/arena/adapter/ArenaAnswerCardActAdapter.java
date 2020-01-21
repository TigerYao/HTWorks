package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;

import java.util.List;

/**
 * 扫码填写答题卡Adapter
 */
public class ArenaAnswerCardActAdapter extends BaseExpandableListAdapter {

    private int[] defaultSingle = ArenaConstant.SINGLE_IMAGE_DEF;
    private int[] selectSingle = ArenaConstant.SINGLE_IMAGE_SELECTED;
    private int[] defaultMulti = ArenaConstant.MULTI_IMAGE_DEF;
    private int[] selectMulti = ArenaConstant.MULTI_IMAGE_SELECTED;

    private Context mContext;
    private ModuleBean[] groups;
    private ArenaExamQuestionBean[][] children;

    public ArenaAnswerCardActAdapter(Context mContext, ModuleBean[] groups, ArenaExamQuestionBean[][] children) {
        this.mContext = mContext;
        this.groups = groups;
        this.children = children;
    }

    @Override
    public int getGroupCount() {
        return groups != null ? groups.length : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (children != null && children[groupPosition] != null) ? children[groupPosition].length : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_answer_card_act_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            groupViewHolder.ivDirection = convertView.findViewById(R.id.iv_direction);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(groups[groupPosition].name);
        if (!isExpanded) {
            groupViewHolder.ivDirection.setRotation(180);
        } else {
            groupViewHolder.ivDirection.setRotation(0);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            childViewHolder = new ChildViewHolder();
            convertView = createChildViewHolder(childViewHolder, parent);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        bindChildHolder(childViewHolder, groupPosition, childPosition);
        return convertView;
    }

    private View createChildViewHolder(ChildViewHolder childViewHolder, ViewGroup parent) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_answer_card_act_children, parent, false);
        childViewHolder.rootView = convertView;
        childViewHolder.topShadow = convertView.findViewById(R.id.view_top_shadow);
        childViewHolder.tvIndex = convertView.findViewById(R.id.tv_index);
        childViewHolder.tvNoAnswer = convertView.findViewById(R.id.tv_no_answer);
        childViewHolder.ivSingles[0] = convertView.findViewById(R.id.a_single);
        childViewHolder.ivSingles[1] = convertView.findViewById(R.id.b_single);
        childViewHolder.ivSingles[2] = convertView.findViewById(R.id.c_single);
        childViewHolder.ivSingles[3] = convertView.findViewById(R.id.d_single);
        childViewHolder.ivSingles[4] = convertView.findViewById(R.id.e_single);
        childViewHolder.ivSingles[5] = convertView.findViewById(R.id.f_single);
        childViewHolder.ivSingles[6] = convertView.findViewById(R.id.g_single);
        childViewHolder.ivSingles[7] = convertView.findViewById(R.id.h_single);
        childViewHolder.ivMultis[0] = convertView.findViewById(R.id.a_multi);
        childViewHolder.ivMultis[1] = convertView.findViewById(R.id.b_multi);
        childViewHolder.ivMultis[2] = convertView.findViewById(R.id.c_multi);
        childViewHolder.ivMultis[3] = convertView.findViewById(R.id.d_multi);
        childViewHolder.ivMultis[4] = convertView.findViewById(R.id.e_multi);
        childViewHolder.ivMultis[5] = convertView.findViewById(R.id.f_multi);
        childViewHolder.ivMultis[6] = convertView.findViewById(R.id.g_multi);
        childViewHolder.ivMultis[7] = convertView.findViewById(R.id.h_multi);
        convertView.setTag(childViewHolder);
        return convertView;
    }

    private void bindChildHolder(ChildViewHolder holder, int groupPosition, int childPosition) {

        // 阴影效果
        if (childPosition == 0) {
            holder.topShadow.setVisibility(View.VISIBLE);
        } else {
            holder.topShadow.setVisibility(View.GONE);
        }

        final ArenaExamQuestionBean questionBean = children[groupPosition][childPosition];
        holder.tvIndex.setText(String.valueOf(questionBean.index + 1));
        // 试题类型：单选99|多选100|不定项101|对错题109|复合题105
        if (questionBean.type == 99 || questionBean.type == 109) {               // 单选
            showOption(holder, 0);

            final ImageView[] ivSingles = holder.ivSingles;       // 单题选项Views

            // 初始化选项个数 和 是否已经选择了
            final List<ArenaExamQuestionBean.QuestionOption> questionOptions = questionBean.questionOptions;

            for (int i = 0; i < 8; i++) {
                if (i < questionOptions.size()) {
                    // 显示选项，并改变选择图片
                    ivSingles[i].setVisibility(View.VISIBLE);
                    if (questionOptions.get(i).isSelected) {
                        ivSingles[i].setImageResource(selectSingle[i]);
                    } else {
                        ivSingles[i].setImageResource(defaultSingle[i]);
                    }

                    // 添加点击选项监听
                    final int finalI = i;
                    ivSingles[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArenaExamQuestionBean.QuestionOption item = questionOptions.get(finalI);
                            if (item.isSelected) {                      // 如果此选项被选了，就变为不选中。用户答案重制。显示未选中
                                ivSingles[finalI].setImageResource(defaultSingle[finalI]);
                                item.isSelected = false;
                                questionBean.userAnswer = 0;                // 对应的题的作答答案. 0表示未做答,数字和字母对应关系: 1=>A,2=>B,3=>C,4=>D,5=>E,6=>F,7=>G,8=>H,答案AB转换后为12
                            } else {                                    // 如果选项没被选
                                // 就把其他选项变为不选
                                for (int i = 0; i < questionOptions.size(); i++) {
                                    questionOptions.get(i).isSelected = false;
                                    ivSingles[i].setImageResource(defaultSingle[i]);
                                }
                                ivSingles[finalI].setImageResource(selectSingle[finalI]);
                                item.isSelected = true;
                                questionBean.userAnswer = finalI + 1;
                            }

                            // 判断对错
                            if (questionBean.userAnswer == 0) {
                                questionBean.isCorrect = 0;
                            } else if (questionBean.userAnswer == questionBean.answer) {
                                questionBean.isCorrect = 1;
                                questionBean.isSubmitted = false;
                            } else {
                                questionBean.isCorrect = 2;
                                questionBean.isSubmitted = false;
                            }
                        }
                    });
                } else {
                    ivSingles[i].setVisibility(View.GONE);
                }
            }
        } else if (questionBean.type == 100 || questionBean.type == 101) {        // 多选
            showOption(holder, 1);
            final ImageView[] ivMultis = holder.ivMultis;       // 多选Views

            // 初始化选项个数 和 是否已经选择了
            final List<ArenaExamQuestionBean.QuestionOption> questionOptions = questionBean.questionOptions;

            for (int i = 0; i < 8; i++) {
                if (i < questionOptions.size()) {
                    // 显示选项，并改变选择图片
                    ivMultis[i].setVisibility(View.VISIBLE);
                    if (questionOptions.get(i).isSelected) {
                        ivMultis[i].setImageResource(selectMulti[i]);
                    } else {
                        ivMultis[i].setImageResource(defaultMulti[i]);
                    }

                    // 添加点击选项监听
                    final int finalI = i;
                    ivMultis[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArenaExamQuestionBean.QuestionOption item = questionOptions.get(finalI);
                            if (item.isSelected) {                      // 如果此选项被选了，就变为不选中。用户答案重制。显示未选中
                                ivMultis[finalI].setImageResource(defaultMulti[finalI]);
                                item.isSelected = false;
                            } else {                                    // 如果选项没被选
                                ivMultis[finalI].setImageResource(selectMulti[finalI]);
                                item.isSelected = true;
                            }

                            // 把选择的选项对应成数字
                            // 对应的题的作答答案. 0表示未做答,数字和字母对应关系: 1=>A,2=>B,3=>C,4=>D,5=>E,6=>F,7=>G,8=>H,答案AB转换后为12
                            int answer = 0;
                            for (int j = 0; j < questionOptions.size(); j++) {
                                if (questionOptions.get(j).isSelected) {
                                    answer = answer * 10 + (j + 1);
                                }
                            }
                            questionBean.userAnswer = answer;

                            // 判断对错
                            if (questionBean.userAnswer == 0) {
                                questionBean.isCorrect = 0;
                            } else if (questionBean.userAnswer == questionBean.answer) {
                                questionBean.isCorrect = 1;
                                questionBean.isSubmitted = false;
                            } else {
                                questionBean.isCorrect = 2;
                                questionBean.isSubmitted = false;
                            }
                        }
                    });
                } else {
                    ivMultis[i].setVisibility(View.GONE);
                }
            }
        } else {                                     // 不可作答
            showOption(holder, 2);
        }
    }

    /**
     * 控制选项的隐现
     *
     * @param holder 选项
     * @param type   0、单选  1、多选  2、不可选
     */
    private void showOption(ChildViewHolder holder, int type) {
        int visibleSingle = View.GONE, visibleMultis = View.GONE, visibleNoAnswer = View.GONE;
        switch (type) {
            case 0:
                visibleSingle = View.VISIBLE;
                break;
            case 1:
                visibleMultis = View.VISIBLE;
                break;
            case 2:
                visibleNoAnswer = View.VISIBLE;
                break;
        }
        for (int i = 0; i < holder.ivSingles.length; i++) {
            holder.ivSingles[i].setVisibility(visibleSingle);
        }
        for (int i = 0; i < holder.ivMultis.length; i++) {
            holder.ivMultis[i].setVisibility(visibleMultis);
        }
        holder.tvNoAnswer.setVisibility(visibleNoAnswer);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView tvTitle;
        ImageView ivDirection;
    }

    static class ChildViewHolder {
        View rootView;
        View topShadow;
        TextView tvIndex;
        TextView tvNoAnswer;
        ImageView[] ivSingles = new ImageView[8];
        ImageView[] ivMultis = new ImageView[8];
    }
}
