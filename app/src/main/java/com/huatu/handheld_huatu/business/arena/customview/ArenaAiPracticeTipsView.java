package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.ArenaHelper;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 智能刷题提示 开始
 */
public class ArenaAiPracticeTipsView extends RelativeLayout {

    private Context mContext;
    private View rootView;
    @BindView(R.id.arena_ai_practice_know_tv)
    TextView ai_practice_know_tv;
    @BindView(R.id.arena_ai_practice_no_tip_ll)
    LinearLayout ai_practice_no_tip_ll;
    @BindView(R.id.arena_ai_practice_no_tip_iv)
    ImageView ai_practice_no_tip_iv;

    private int resId = R.layout.layout_viewstub_ai_practice_content_rl;
    private int requestType;
    private String tagFrom;
    private boolean isAttached = false;
    private boolean canShowTip = true;


    public ArenaAiPracticeTipsView(Context context) {
        super(context);
        init(context);
    }

    public ArenaAiPracticeTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaAiPracticeTipsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
        initView();
    }

    private void initView() {
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
        if (!isAttached && realExamBean == null || realExamBean.paper == null) {
            LogUtils.i("updateViews direct return because questionBean is null");
            return;
        }
    }

    @OnClick({
            R.id.arena_ai_practice_know_tv,
            R.id.arena_ai_practice_no_tip_ll,
            R.id.iv_back
    })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.arena_ai_practice_know_tv:
                SpUtils.setArenaAiPracticeTipsCanShow(canShowTip);
                ArenaHelper.setEnableExamTrue();
                ArenaDataCache.getInstance().isShowedAiTipsIng = false;
                this.setVisibility(GONE);
                break;
            case R.id.arena_ai_practice_no_tip_ll:
                canShowTip = !canShowTip;
                if (canShowTip) {
                    ai_practice_no_tip_iv.setImageResource(R.mipmap.arena_ai_practice_normal);
                } else {
                    ai_practice_no_tip_iv.setImageResource(R.mipmap.arena_ai_practice_checked);
                }
                break;
            case R.id.iv_back:
                EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_FINISH));
                break;
        }
    }
}
