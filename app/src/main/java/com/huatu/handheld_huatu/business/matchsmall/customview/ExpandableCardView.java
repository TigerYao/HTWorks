package com.huatu.handheld_huatu.business.matchsmall.customview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.matchsmall.adapter.ExpandableCardAdapter;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;

import java.util.List;

/**
 * 可展开的答题卡，用于阶段性考试报告中
 */
public class ExpandableCardView extends RelativeLayout {

    private Context mContext;
    private View rootView;

    private RecyclerView recyclerView;
    private ImageView ivMore;

    private ImageView ivGood;
    private TextView tvWord;

    public ExpandableCardView(Context context) {
        this(context, null);
    }

    public ExpandableCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.expandable_card_view, this, true);
        recyclerView = rootView.findViewById(R.id.rl_card);
        ivMore = rootView.findViewById(R.id.iv_more);
        ivGood = rootView.findViewById(R.id.iv_good);
        tvWord = rootView.findViewById(R.id.tv_word);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
        recyclerView.setNestedScrollingEnabled(false);
    }

    /**
     * 给可展开的答题卡设置数据
     *
     * @param type             类型  0、用时超过50s的 1、难度系数高于0.5的 2、没做的题 -- 用于显示提示语
     * @param questionBeanList 问题数据
     * @param listener         点击回调
     */
    public void setQuestions(int type, List<ArenaExamQuestionBean> questionBeanList, final ExpandableCardAdapter.OnItemSelectedListener listener) {
        if (questionBeanList != null && questionBeanList.size() > 0) {
            final ExpandableCardAdapter adapter = new ExpandableCardAdapter(mContext, questionBeanList, new ExpandableCardAdapter.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int position) {
                    listener.onItemSelected(position);
                }
            });

            recyclerView.setAdapter(adapter);

            if (questionBeanList.size() <= 5) {
                ivMore.setVisibility(INVISIBLE);
            }

            ivMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.expand()) {
                        ivMore.setImageResource(R.mipmap.expandable_less);
                    } else {
                        ivMore.setImageResource(R.mipmap.expandable_more);
                    }
                }
            });
        } else {
            recyclerView.setVisibility(GONE);
            ivMore.setVisibility(GONE);
            switch (type) {
                case 0:
                    ivGood.setVisibility(VISIBLE);
                    tvWord.setVisibility(VISIBLE);
                    tvWord.setText("够快！正是上岸的速度");
                    break;
                case 1:
                    ivGood.setVisibility(VISIBLE);
                    tvWord.setVisibility(VISIBLE);
                    tvWord.setText("优秀！竟然没有能难住你");
                    break;
                case 2:
                    ivGood.setVisibility(VISIBLE);
                    tvWord.setVisibility(VISIBLE);
                    tvWord.setText("很棒！全部做完了");
                    break;
            }
        }
    }

}
