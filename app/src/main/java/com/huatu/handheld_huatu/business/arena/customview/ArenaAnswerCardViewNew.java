package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.adapter.ArenaAnswerCardAdapter;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2016/11/15.
 * 答题卡View
 */

public class ArenaAnswerCardViewNew extends RelativeLayout {

    private Context mContext;
    private View rootView;

    @BindView(R.id.arena_answer_card_name_tv)
    TextView tvAnswerCardName;                              // 题目

    @BindView(R.id.arena_answer_card_rv)
    RecyclerView rvAnswerCard;                              // 答题卡选项RecyclerView

    private boolean isShowModule = true;                    // 是否显示分类信息

    ArenaAnswerCardAdapter answerCardAdapter;               // adapter
    GridLayoutManager gridLayoutManager;                    // manager

    private int resId = R.layout.arena_answer_card_layout_new;

    private int requestType;
    RealExamBeans.RealExamBean realExamBean;
    private String tagFrom;
    private boolean isAttached = false;

    public ArenaAnswerCardViewNew(Context context) {
        super(context);
        init(context);
    }

    public ArenaAnswerCardViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaAnswerCardViewNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
    }

    public void setTitleVisible(int visible) {
        tvAnswerCardName.setVisibility(visible);
    }

    public void setShowModule(boolean isShowModule) {
        this.isShowModule = isShowModule;
    }

    public void setData(RealExamBeans.RealExamBean bean, int type, int examType, String tag) {
        realExamBean = bean;
        requestType = type;
        this.tagFrom = tag;
        answerCardAdapter = new ArenaAnswerCardAdapter(mContext, requestType, examType, isShowModule, realExamBean.paper);
        gridLayoutManager = new GridLayoutManager(mContext, 5);
        rvAnswerCard.setLayoutManager(gridLayoutManager);
        rvAnswerCard.setAdapter(answerCardAdapter);
        rvAnswerCard.setNestedScrollingEnabled(false);
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_WRONG) {
            tvAnswerCardName.setText("错题解析");
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_SINGLE) {
            tvAnswerCardName.setText("单题解析");
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE) {
            tvAnswerCardName.setText("收藏解析");
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE) {
            tvAnswerCardName.setText("智能刷题");
        } else {
            tvAnswerCardName.setText(realExamBean.name);
        }
        answerCardAdapter.setOnItemSelectedListener(new ArenaAnswerCardAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                ArenaExamMessageEvent event = new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_CHANGE_QUESTION);
                event.extraBundle = new Bundle();
                event.extraBundle.putInt("request_index", position);
                if ("ArenaAnswerCardFragment".equals(tagFrom)) {
                    event.extraBundle.putBoolean("pop_stack", true);
                }
                event.tag = tagFrom;
                EventBus.getDefault().post(event);
            }
        });
        updateViews(realExamBean);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    public void updateViews(RealExamBeans.RealExamBean realExamBean) {
        if (!isAttached && realExamBean == null || realExamBean.paper == null || answerCardAdapter == null) {
            LogUtils.i("updateViews direct return because questionBean is null");
            return;
        }
        answerCardAdapter.notifyDataSetChanged();
    }
}
