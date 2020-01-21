package com.huatu.handheld_huatu.business.arena.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityPad;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2016/11/15.
 * 试题ViewGroup
 */

public class ArenaPadQuestionView extends FrameLayout implements ArenaQuestionViewListener, NightSwitchInterface, TextSizeSwitchInterface {

    protected String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private View rootView;

    @BindView(R.id.question_header)
    ArenaQuestionViewHeader questionViewHeader;                     // 问题描述布局

    @BindView(R.id.ll_left)
    LinearLayout llLeft;
    @BindView(R.id.ns_right)
    NestedScrollView nsRight;
    @BindView(R.id.ll_right)
    LinearLayout llRight;

    @BindView(R.id.tv_multi_sure)
    TextView tvMultiSure;                                           // 背题模式，多选提交按钮

    private ArrayList<ArenaQuestionMaterial> cacheMViews = new ArrayList<>();
    private LinkedList<ArenaQuestionMaterial> materialViews = new LinkedList<>();       // 存放材料View
    private ArrayList<ArenaQuestionBody> cacheQViews = new ArrayList<>();
    private LinkedList<ArenaQuestionBody> bodyViews = new LinkedList<>();               // 存放问题View

    private ArenaExamQuestionBean questionBean = new ArenaExamQuestionBean();

    private boolean hasMaterial = false;                             // 是否是材料题

    private int resId = R.layout.arena_question_pad;

    public ArenaPadQuestionView(Context context) {
        super(context);
        init(context);
    }

    public ArenaPadQuestionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaPadQuestionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);

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
        for (ArenaExamQuestionBean question : questionBean.questions) {
            question.seeType = ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_AFTER;
        }
        questionViewHeader.nightSwitch();
        for (ArenaQuestionBody bodyView : cacheQViews) {
            bodyView.showAnalysis();
        }
        hideMutlBtn();
    }

    public void setQuestionBean(int fromType, ArenaExamQuestionBean bean, int count, int current) {
        this.questionBean = bean;

        questionViewHeader.setQuestionBean(fromType, bean, count, current);

        initMutlCommitBtn();        // 初始化多选提交按钮

        updateViews();
    }

    private void updateViews() {
        if (questionBean == null) {
            return;
        }

        for (int i = 0; i < llLeft.getChildCount(); i++) {
            dealChild(llLeft.getChildAt(i));
        }
        for (int i = 0; i < llRight.getChildCount(); i++) {
            dealChild(llRight.getChildAt(i));
        }
        for (ArenaQuestionMaterial materialView : materialViews) {
            ViewParent parent = materialView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(materialView);
            }
        }
        for (ArenaQuestionBody bodyView : bodyViews) {
            ViewParent parent = bodyView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(bodyView);
            }
        }

        hasMaterial = questionBean.isMaterial;

        if (questionBean.isMaterial && ArenaExamActivityPad.doStyle == 1) {
            LinearLayout.LayoutParams layoutParamsR = (LinearLayout.LayoutParams) nsRight.getLayoutParams();
            layoutParamsR.weight = 3;
            nsRight.setLayoutParams(layoutParamsR);

            ArenaQuestionMaterial materialView = getMaterialView();
            llLeft.addView(materialView);
            materialView.setQuestionBean(questionBean);

            for (ArenaExamQuestionBean bean : questionBean.questions) {
                ArenaQuestionBody bodyView = getBodyView();
                llRight.addView(bodyView);
                bodyView.setQuestionBean(bean);
            }
        } else {
            LinearLayout.LayoutParams layoutParamsR = (LinearLayout.LayoutParams) nsRight.getLayoutParams();
            layoutParamsR.weight = 0;
            nsRight.setLayoutParams(layoutParamsR);

            if (questionBean.isMaterial) {
                ArenaQuestionMaterial materialView = getMaterialView();
                llLeft.addView(materialView);
                materialView.setQuestionBean(questionBean);
            }

            for (ArenaExamQuestionBean bean : questionBean.questions) {
                ArenaQuestionBody bodyView = getBodyView();
                llLeft.addView(bodyView);
                bodyView.setQuestionBean(bean);
            }
        }
    }

    private ArenaQuestionMaterial getMaterialView() {
        if (materialViews.size() > 0) {
            return materialViews.removeFirst();
        }
        ArenaQuestionMaterial arenaQuestionMaterial = new ArenaQuestionMaterial(mContext);
        cacheMViews.add(arenaQuestionMaterial);
        return arenaQuestionMaterial;
    }

    private ArenaQuestionBody getBodyView() {
        if (bodyViews.size() > 0) {
            return bodyViews.removeFirst();
        }
        ArenaQuestionBody bodyView = new ArenaQuestionBody(mContext);
        bodyView.setQuestionView(this);
        cacheQViews.add(bodyView);
        return bodyView;
    }

    private void dealChild(View child) {
        if (child instanceof ArenaQuestionMaterial) {
            materialViews.add((ArenaQuestionMaterial) child);
        } else if (child instanceof ArenaQuestionBody) {
            bodyViews.add((ArenaQuestionBody) child);
        }
    }

    public void switchViewStyle() {
        if (questionBean.isMaterial) {
            ArrayList<View> temp = new ArrayList<>();
            LinearLayout.LayoutParams layoutParamsR = (LinearLayout.LayoutParams) nsRight.getLayoutParams();
            if (layoutParamsR.weight == 0) {
                for (int i = 0; i < llLeft.getChildCount(); i++) {
                    View child = llLeft.getChildAt(i);
                    if (child instanceof ArenaQuestionBody) {
                        temp.add(child);
                    }
                }
                for (View view : temp) {
                    llLeft.removeView(view);
                    llRight.addView(view);
                }
                layoutParamsR.weight = 3;
            } else {
                for (int i = 0; i < llRight.getChildCount(); i++) {
                    View child = llRight.getChildAt(i);
                    if (child instanceof ArenaQuestionBody) {
                        temp.add(child);
                    }
                }
                for (View view : temp) {
                    llRight.removeView(view);
                    llLeft.addView(view);
                }
                layoutParamsR.weight = 0;
            }
            nsRight.setLayoutParams(layoutParamsR);
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
//        int fontSizeMode = SpUtils.getFontSizeMode();

//        int[] size_12 = {12, 10, 14};   // 0、正常 1、小号 2、大号
//        int[] size_14 = {14, 12, 16};   // 0、正常 1、小号 2、大号
//        int[] size_16 = {16, 14, 18};   // 0、正常 1、小号 2、大号

//        int now_12 = size_12[fontSizeMode];
//        int now_14 = size_14[fontSizeMode];
//        int now_16 = size_16[fontSizeMode];

        questionViewHeader.setTextSize();
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            // 材料题的材料
            setBgColor(llLeft, R.color.arena_material_bg);

            // 问题及解析
            setBgColor(llRight, R.color.arena_common_bg);

            // 多选背题提交按钮
            setBgColor(tvMultiSure, R.color.arena_mutl_commit_btn_bg_light);
            setTextColor(tvMultiSure, R.color.arena_mutl_commit_btn_text_light);
        } else {
            setBgColor(llLeft, R.color.arena_material_bg_night);

            setBgColor(llRight, R.color.arena_common_bg_night);

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
//                        float value = (float) animation.getAnimatedValue();
//                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsMaterial.getLayoutParams();
//                        if (layoutParams.height + (value - pre) < DisplayUtil.dp2px(70)) {
//                            layoutParams.height = DisplayUtil.dp2px(70);
//                        } else {
//                            layoutParams.height += (value - pre);
//                        }
//                        nsMaterial.setLayoutParams(layoutParams);
//                        pre = value;
//                        if (pre == 0) {
//                            pre = dp48;
//                        }
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
//                        float value = (float) animation.getAnimatedValue();
//                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) nsMaterial.getLayoutParams();
//                        layoutParams.height += (value - pre);
//                        nsMaterial.setLayoutParams(layoutParams);
//                        pre = value;
//                        if (pre == dp48) {
//                            pre = 0;
//                        }
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
        for (ArenaQuestionMaterial materialView : cacheMViews) {
            materialView.nightSwitch();
        }
        for (ArenaQuestionBody bodyView : cacheQViews) {
            bodyView.nightSwitch();
        }
    }

    @Override
    public void sizeSwitch() {
        setTextSize();
        questionViewHeader.sizeSwitch();
        for (ArenaQuestionMaterial materialView : cacheMViews) {
            materialView.sizeSwitch();
        }
        for (ArenaQuestionBody bodyView : cacheQViews) {
            bodyView.sizeSwitch();
        }
    }
}
