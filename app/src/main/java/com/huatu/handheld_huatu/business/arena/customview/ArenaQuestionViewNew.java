package com.huatu.handheld_huatu.business.arena.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.textselect.TextSelectManager;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2016/11/15.
 * 试题ViewGroup
 */

public class ArenaQuestionViewNew extends FrameLayout implements ArenaQuestionViewListener, NightSwitchInterface, TextSizeSwitchInterface {

    protected String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private View rootView;

    @BindView(R.id.question_header)
    ArenaQuestionViewHeader questionViewHeader;                     // 问题描述布局

    @BindView(R.id.ns_material)
    NestedScrollView nsMaterial;                                    // 问题材料ScrollView
    @BindView(R.id.question_material)
    ArenaQuestionMaterial questionMaterial;                         // 材料内容

    @BindView(R.id.ll_slide_view)
    LinearLayout llSlideView;                                       // 拖拽Bar
    @BindView(R.id.iv_drag_icon)
    ImageView ivDragIcon;                                           // 拖拽小图标
    @BindView(R.id.rl_slide)
    RelativeLayout rlSlide;                                         // 拖拽Bar头
    @BindView(R.id.tv_slide_title)
    TextView tvSlideTitle;                                          // 拖拽Bar内容
    @BindView(R.id.view_slide_divider)
    View viewSlideDivider;                                          // 拖拽头分割线

    @BindView(R.id.ns_question)
    NestedScrollView nsQuestion;                                    // 问题滑动ScrollView
    @BindView(R.id.question_body)
    ArenaQuestionBody questionBody;

    @BindView(R.id.tv_multi_sure)
    TextView tvMultiSure;                                           // 背题模式，多选提交按钮

    private ArenaExamQuestionBean questionBean = new ArenaExamQuestionBean();

    private boolean hasMaterial = false;                             // 是否是材料题

    private int resId = R.layout.arena_question_new;

    public ArenaQuestionViewNew(Context context) {
        super(context);
        init(context);
    }

    public ArenaQuestionViewNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaQuestionViewNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);

        questionBody.setQuestionView(this);

        if (llSlideView != null) {
            llSlideView.setOnTouchListener(new MaterialTouch());
        }

        setColor();
        setTextSize();

//        tvQuestionMaterial.openCopy();
//        tvQuestion.openCopy();

//        Typeface mTypeface = Typeface.createFromAsset(ctx.getAssets(), "font/Heavy.ttf");
//
//        tvQuestionMaterial.setTypeface(mTypeface);
//        tvQuestion.setTypeface(mTypeface);
    }

    @OnClick(R.id.iv_doubt)
    public void onClickDoubt() {
        if (questionBean == null) {
            return;
        }
        questionBean.doubt = (questionBean.doubt == 0 ? 1 : 0);
        questionViewHeader.setDoubtState();
    }

    @OnClick(R.id.tv_multi_sure)
    public void onClickMutlCommit() {
        showAnswer();
    }

    public void showAnswer() {
        questionBean.seeType = ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_AFTER;
        questionViewHeader.nightSwitch();
        questionBody.showAnalysis();
        hideMutlBtn();
    }


    public void setQuestionBean(int fromType, ArenaExamQuestionBean bean, int count, int current) {
        this.questionBean = bean;

        questionViewHeader.setQuestionBean(fromType, bean, count, current);
        questionMaterial.setQuestionBean(bean);
        questionBody.setQuestionBean(bean);

        initMutlCommitBtn();        // 初始化多选提交按钮

        updateViews();

        scroll();
    }

    public void scroll() {
        if (nsQuestion != null) {
            nsQuestion.scrollTo(0, 0);
        }
    }

    private void updateViews() {
        if (questionBean == null) {
            return;
        }

        if (TextUtils.isEmpty(questionBean.material)
                && Method.isListEmpty(questionBean.materials)
                && TextUtils.isEmpty(questionBean.require)) {
            hasMaterial = false;
            //“资料”和“注意事项/要求”有后台内容返回，则显示内容，无内容返回则不显示。若两项都为空，则都不显示
            nsMaterial.setVisibility(View.GONE);
            llSlideView.setVisibility(GONE);
        } else {
            hasMaterial = true;
            //show material
            nsMaterial.setVisibility(View.VISIBLE);
            llSlideView.setVisibility(VISIBLE);

            if (nsMaterial != null) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsMaterial.getLayoutParams();
                layoutParams.height = DisplayUtil.getScreenHeight() / 2 - DisplayUtil.dp2px(100);
                nsMaterial.setLayoutParams(layoutParams);
            }
        }
    }

    // 初始化多选提交按钮
    private void initMutlCommitBtn() {
        if (questionBean.seeType == ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_PRE && questionBean.userAnswer != 0) {
            tvMultiSure.setVisibility(VISIBLE);
            tvMultiSure.setTranslationY(0);
        } else {
            tvMultiSure.setVisibility(GONE);
            tvMultiSure.setTranslationY(dp48);
        }
    }

    /**
     * 设置字体大小
     * 别忘了，还有个答题页面
     */
    public void setTextSize() {
        int fontSizeMode = SpUtils.getFontSizeMode();

//        int[] size_12 = {12, 10, 14};   // 0、正常 1、小号 2、大号
//        int[] size_14 = {14, 12, 16};   // 0、正常 1、小号 2、大号
        int[] size_16 = {16, 14, 18};   // 0、正常 1、小号 2、大号

//        int now_12 = size_12[fontSizeMode];
//        int now_14 = size_14[fontSizeMode];
        int now_16 = size_16[fontSizeMode];

        questionViewHeader.setTextSize();

        tvSlideTitle.setTextSize(now_16);
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            // 材料题的材料
            setBgColor(nsMaterial, R.color.arena_material_bg);

            // 问题及解析
            setBgColor(nsQuestion, R.color.arena_common_bg);

            // 拖拽Bar
            ivDragIcon.setImageResource(R.mipmap.arena_drag_icon);
            tvSlideTitle.setBackgroundResource(R.drawable.arena_slide_bg);
            setTextColor(tvSlideTitle, R.color.arena_drag_bar_text);
            viewSlideDivider.setBackgroundColor(Color.parseColor("#E0E0E0"));

            // 多选背题提交按钮
            setBgColor(tvMultiSure, R.color.arena_mutl_commit_btn_bg_light);
            setTextColor(tvMultiSure, R.color.arena_mutl_commit_btn_text_light);
        } else {
            setBgColor(nsMaterial, R.color.arena_material_bg_night);

            setBgColor(nsQuestion, R.color.arena_common_bg_night);

            ivDragIcon.setImageResource(R.mipmap.arena_drag_icon_night);
            tvSlideTitle.setBackgroundResource(R.drawable.arena_setting_bottom_bg_night);
            setTextColor(tvSlideTitle, R.color.arena_drag_bar_text_night);
            setBgColor(viewSlideDivider, R.color.arena_common_bg_night);

            setBgColor(tvMultiSure, R.color.arena_mutl_commit_btn_bg_night);
            setTextColor(tvMultiSure, R.color.arena_mutl_commit_btn_text_night);
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

    int dp48 = DisplayUtil.dp2px(48);

    private ObjectAnimator animationBottomIn;

    // 显示背题多选，提交按钮
    public void showMutlBtn() {
        if (tvMultiSure.getVisibility() == VISIBLE) return;
        if (animationBottomIn == null) {
            animationBottomIn = ObjectAnimator.ofFloat(tvMultiSure, "translationY", dp48, 0);
            animationBottomIn.setDuration(100);
            animationBottomIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                float pre = dp48;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (hasMaterial) {
                        float value = (float) animation.getAnimatedValue();
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsMaterial.getLayoutParams();
                        if (layoutParams.height + (value - pre) < DisplayUtil.dp2px(70)) {
                            layoutParams.height = DisplayUtil.dp2px(70);
                        } else {
                            layoutParams.height += (value - pre);
                        }
                        nsMaterial.setLayoutParams(layoutParams);
                        pre = value;
                        if (pre == 0) {
                            pre = dp48;
                        }
                    }
                }
            });
            animationBottomIn.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    tvMultiSure.setVisibility(VISIBLE);
                }
            });
        }
        animationBottomIn.start();
    }

    private ObjectAnimator animationBottomOut;

    // 隐藏背题多选提交按钮
    public void hideMutlBtn() {
        if (tvMultiSure.getVisibility() == GONE) return;
        if (animationBottomOut == null) {
            animationBottomOut = ObjectAnimator.ofFloat(tvMultiSure, "translationY", 0, dp48);
            animationBottomOut.setDuration(100);
            animationBottomOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                float pre = 0f;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (hasMaterial) {
                        float value = (float) animation.getAnimatedValue();
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsMaterial.getLayoutParams();
                        layoutParams.height += (value - pre);
                        nsMaterial.setLayoutParams(layoutParams);
                        pre = value;
                        if (pre == dp48) {
                            pre = 0;
                        }
                    }
                }
            });
            animationBottomOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    tvMultiSure.setVisibility(GONE);
                }
            });
        }
        animationBottomOut.start();
    }

    @Override
    public void nightSwitch() {
        setColor();
        questionViewHeader.nightSwitch();
        questionMaterial.nightSwitch();
        questionBody.nightSwitch();
    }

    @Override
    public void sizeSwitch() {
        setTextSize();
        questionViewHeader.sizeSwitch();
        questionMaterial.sizeSwitch();
        questionBody.sizeSwitch();
    }

    private class MaterialTouch implements OnTouchListener {
        int lastY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = (int) event.getRawY();
                    TextSelectManager.newInstance().clearOthers(null);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dy = (int) event.getRawY() - lastY;

                    if (nsMaterial != null) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsMaterial.getLayoutParams();
                        if (layoutParams.height + dy < DisplayUtil.dp2px(70)) {
                            layoutParams.height = DisplayUtil.dp2px(70);
                        } else {
                            layoutParams.height = layoutParams.height + dy;
                        }
                        nsMaterial.setLayoutParams(layoutParams);
                    }

                    if (nsQuestion != null) {
                        if (nsQuestion.getScrollY() < 20) {
                            nsQuestion.scrollTo(0, 0);
                        }
                    }
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    }
}
