package com.huatu.handheld_huatu.business.matchsmall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.List;

/**
 * 阶段性测试报告里可展开的答题卡Adapter
 */
public class ExpandableCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ArenaExamQuestionBean> questionBeanList;

    private OnItemSelectedListener onItemSelectedListener;

    private boolean expand = false;                 // 是否展开

    public ExpandableCardAdapter(Context context, List<ArenaExamQuestionBean> questionBeanList, OnItemSelectedListener onItemSelectedListener) {
        this.mContext = context;
        this.questionBeanList = questionBeanList;
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public boolean expand() {
        this.expand = !expand;
        this.notifyDataSetChanged();
        return expand;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResultHolder(LayoutInflater.from(mContext).inflate(R.layout.item_expandable_answer_card, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int nightMode = SpUtils.getDayNightMode();
        ResultHolder resultHolder = (ResultHolder) holder;

        ArenaExamQuestionBean info = questionBeanList.get(position);
        resultHolder.tv_result_answer_card.setText(String.valueOf(info.index + 1));
        if (info.type == ArenaConstant.QUESTION_TYPE_SUBJECTIVE) {                  // 不可作答题型。（试题类型：单选99|多选100|不定项101|对错题109|复合题105）
            // 不可作答
            resultHolder.tvInvalid.setVisibility(View.VISIBLE);
            resultHolder.tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.ans_card_no_ans));
            if (nightMode == 0) {
                resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_light);             // 没做、不可作答
            } else {
                resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_night);             // 没做、不可作答
            }
        } else {                                                                    // 解析模式
            resultHolder.tvInvalid.setVisibility(View.INVISIBLE);
            if (info.isCorrect == 0) {                                                  // 是否正确 00表示未做答 1:正确 2:错误
                // 未作答
                resultHolder.tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.ans_card_no_ans));
                if (nightMode == 0) {
                    resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_light);             // 没做、不可作答
                } else {
                    resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_night);             // 没做、不可作答
                }
            } else if (info.isCorrect == 1) {
                // 正确
                if (nightMode == 0) {
                    resultHolder.tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.white));
                    resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_select_right_light);            // 正确
                } else {
                    resultHolder.tv_result_answer_card.setTextColor(Color.parseColor("#1D1D1D"));
                    resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_select_right_night);            // 正确
                }
            } else {
                // 错误
                if (nightMode == 0) {
                    resultHolder.tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.white));
                    resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_wrong_light);            // 错误
                } else {
                    resultHolder.tv_result_answer_card.setTextColor(Color.parseColor("#1D1D1D"));
                    resultHolder.tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_wrong_night);            // 错误
                }
            }
        }
        if (info.doubt == 1) {
            resultHolder.icDoubt.setVisibility(View.VISIBLE);
        } else {
            resultHolder.icDoubt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        int count;
        if (questionBeanList == null) {
            count = 0;
        } else if (expand) {        // 如果展开
            count = questionBeanList.size() > 0 ? questionBeanList.size() : 0;
        } else {                    // 折叠
            count = questionBeanList.size() > 5 ? 5 : questionBeanList.size();
        }
        return count;
    }

    class ResultHolder extends RecyclerView.ViewHolder {

        ImageView icDoubt;
        TextView tv_result_answer_card;                     // 数字圈
        TextView tvInvalid;                                 // 圈下面的不可作答

        ResultHolder(View itemView) {
            super(itemView);

            icDoubt = itemView.findViewById(R.id.iv_doubt);
            tv_result_answer_card = itemView.findViewById(R.id.tv_result_answer_card);
            tvInvalid = itemView.findViewById(R.id.tv_result_answer_card_invalid);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }
}
