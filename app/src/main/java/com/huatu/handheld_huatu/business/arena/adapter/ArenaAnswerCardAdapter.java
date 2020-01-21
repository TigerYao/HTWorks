package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.PaperBean;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2016/10/21.
 */
public class ArenaAnswerCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ITEM_EXERCISE_TYPE = 1;
    private final static int ITEM_EXERCISE_RESULT = 2;
    // 记录题型模块数据
    private final List<ModuleBean> moduleBeanList = new ArrayList<>();
    // 题目总数量
    private int exerciseAmount;
    // 记录Title位置
    private List<Integer> posList;
    private int requestType;
    private LayoutInflater mLayoutInflater;
    private PaperBean paperBean;
    private Context mContext;
    private final List<ArenaExamQuestionBean> questionBeanList = new ArrayList<>();

    public ArenaAnswerCardAdapter(Context context, int reqType, int examType, boolean isShowModule, PaperBean bean) {
        this.posList = new ArrayList<>();
        requestType = reqType;
        mLayoutInflater = LayoutInflater.from(context);
        this.paperBean = bean;
        mContext = context;
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG) {
            if (paperBean.wrongQuestionBeanList != null) {
                questionBeanList.addAll(paperBean.wrongQuestionBeanList);
            }
            if (paperBean.wrongModules != null) {
                moduleBeanList.addAll(paperBean.wrongModules);
            }
        } else {
            if (paperBean.questionBeanList != null) {
                questionBeanList.addAll(paperBean.questionBeanList);
            }
            if (paperBean.modules != null) {
                moduleBeanList.addAll(paperBean.modules);
            }
        }
        // 单一题型不显示Title，题型不止1种才显示Title区
        // 指定类型不显示分类信息
        if (paperBean.modules != null
                && paperBean.modules.size() > 1
                && examType != ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI          // 错题重练
                && examType != ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN           // 每日特训
                && isShowModule
        ) {
            posList.add(0);
            for (int i = 1; i < moduleBeanList.size(); i++) {
                posList.add(posList.get(i - 1) + moduleBeanList.get(i - 1).qcount + 1);
            }
        }
        exerciseAmount = questionBeanList.size() + posList.size();
    }

    /**
     * 实现不同列数
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            // 这里实现是占一行的位置，还是占一个的位置，要配合GridLayoutManager。
            // 总列数为6，
            // 返回6，就是占整行
            // 返回3，就是占半行
            // 返回2就是占1/3行的位置
            // 返回1，就是占原来的大小位置
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == ITEM_EXERCISE_TYPE) {
                        return gridManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_EXERCISE_TYPE) {
            return new TypeHolder(mLayoutInflater.inflate(R.layout.item_type_recyclerview_answer_card, parent, false));
        } else {
            return new ResultHolder(mLayoutInflater.inflate(R.layout.item_result_recyclerview_answer_card, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int nightMode = SpUtils.getDayNightMode();
        int realPosition;
        if (holder instanceof TypeHolder) {
            realPosition = getTypeRealPosition(position);
            if (realPosition >= moduleBeanList.size()) return;
            ((TypeHolder) holder).tv_type_answer_card.setText(moduleBeanList.get(realPosition).name);
        } else {
            realPosition = getExerciseRealPosition(position);
            if (realPosition >= questionBeanList.size()) return;
            ArenaExamQuestionBean info = questionBeanList.get(realPosition);
            ((ResultHolder) holder).tv_result_answer_card.setText(String.valueOf(info.index + 1));
            if (info.type == ArenaConstant.QUESTION_TYPE_SUBJECTIVE) {                  // 不可作答题型。（试题类型：单选99|多选100|不定项101|对错题109|复合题105）
                // 不可作答
                ((ResultHolder) holder).tvInvalid.setVisibility(View.VISIBLE);
                ((ResultHolder) holder).tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.ans_card_no_ans));
                if (nightMode == 0) {
                    ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_light);             // 没做、不可作答
                } else {
                    ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_night);             // 没做、不可作答
                }
            } else if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                    && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE) {    // 做题模式（并且不是背题模式)，只标做没做过
                ((ResultHolder) holder).tvInvalid.setVisibility(View.INVISIBLE);
                if (info.userAnswer != 0) {                                                 // 对应的题的作答答案. 0表示未做答,数字和字母对应关系: 1=>A,2=>B,3=>C,4=>D,5=>E,6=>F,7=>G,8=>H,答案AB转换后为12
                    // 做过了
                    if (nightMode == 0) {
                        ((ResultHolder) holder).tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.white));
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_select_light);             // 做过
                    } else {
                        ((ResultHolder) holder).tv_result_answer_card.setTextColor(Color.parseColor("#1D1D1D"));
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_select_night);             // 做过
                    }
                } else {
                    // 未作答
                    ((ResultHolder) holder).tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.ans_card_no_ans));
                    if (nightMode == 0) {
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_light);             // 没做、不可作答
                    } else {
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_night);             // 没做、不可作答
                    }
                }
            } else {                                                                    // 解析模式
                ((ResultHolder) holder).tvInvalid.setVisibility(View.INVISIBLE);
                if (info.isCorrect == 0) {                                                  // 是否正确 00表示未做答 1:正确 2:错误
                    // 未作答
                    ((ResultHolder) holder).tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.ans_card_no_ans));
                    if (nightMode == 0) {
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_light);             // 没做、不可作答
                    } else {
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_def_night);             // 没做、不可作答
                    }
                } else if (info.isCorrect == 1) {
                    // 正确
                    if (nightMode == 0) {
                        ((ResultHolder) holder).tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.white));
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_select_right_light);            // 正确
                    } else {
                        ((ResultHolder) holder).tv_result_answer_card.setTextColor(Color.parseColor("#1D1D1D"));
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_select_right_night);            // 正确
                    }
                } else {
                    // 错误
                    if (nightMode == 0) {
                        ((ResultHolder) holder).tv_result_answer_card.setTextColor(mContext.getResources().getColor(R.color.white));
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_wrong_light);            // 错误
                    } else {
                        ((ResultHolder) holder).tv_result_answer_card.setTextColor(Color.parseColor("#1D1D1D"));
                        ((ResultHolder) holder).tv_result_answer_card.setBackgroundResource(R.mipmap.option_single_wrong_night);            // 错误
                    }
                }
            }
            if (info.doubt == 1) {
                ((ResultHolder) holder).icDoubt.setVisibility(View.VISIBLE);
            } else {
                ((ResultHolder) holder).icDoubt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return exerciseAmount;
    }

    @Override
    public int getItemViewType(int position) {
        if (posList.contains(position))
            return ITEM_EXERCISE_TYPE;
        return ITEM_EXERCISE_RESULT;
    }

    class TypeHolder extends RecyclerView.ViewHolder {
        TextView tv_type_answer_card;

        TypeHolder(View itemView) {
            super(itemView);
            tv_type_answer_card = (TextView) itemView;
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private OnItemSelectedListener onItemSelectedListener;

    public void setOnItemSelectedListener(OnItemSelectedListener l) {
        onItemSelectedListener = l;
    }

    class ResultHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_doubt)
        ImageView icDoubt;
        @BindView(R.id.tv_result_answer_card)
        TextView tv_result_answer_card;                     // 数字圈
        @BindView(R.id.tv_result_answer_card_invalid)
        TextView tvInvalid;                                 // 圈下面的不可作答

        ResultHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tv_result_answer_card)
        void onResultClick() {
            int index = getExerciseRealPosition(getLayoutPosition());
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(index);
            }
        }
    }

    // 获取题目在exerciseList中的实际position
    private int getExerciseRealPosition(int position) {
        int prefix = 0;
        for (Integer i : posList) {
            if (position > i) {
                prefix++;
            }
        }
        return position - prefix;
    }

    // 获取题目类型Type在posList中的位置
    private int getTypeRealPosition(int position) {
        int i = 0;
        for (Integer integer : posList) {
            if (integer == position) {
                return i;
            } else {
                i++;
            }
        }
        return i;
    }
}
