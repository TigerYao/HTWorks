package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.event.EssayCheckMessageEvent;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.business.essay.examfragment.EssayMaterialsFragment;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.EventBusUtil;

import java.util.List;

public class MaterialsCardView extends RelativeLayout {

    private Context context;

    private EssayMaterialsFragment materialsFragment;                   // 材料Fragment
    private Animation translateAnimationIn;                             // 材料卡片弹出的动画
    private Animation translateAnimationOut;                            // 材料卡片隐藏的动画
    private boolean isShowMaterial = false;                             // 是否显示着当前卡片

    public MaterialsCardView(Context context) {
        this(context, null);
    }

    public MaterialsCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialsCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(context).inflate(R.layout.essay_material_card_view, this, true);

        ImageView ivClose = rootView.findViewById(R.id.iv_hide_material);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        this.setBackgroundResource(R.drawable.bg_essay_control);
        initAnimation();
    }

    // 是否显示着
    public boolean isShowMaterialCard() {
        return isShowMaterial;
    }

    public void show() {
        EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_essExCardViewShow_clearview));
        EventBusUtil.sendMessage(new EssayCheckMessageEvent(EssayCheckMessageEvent.EssayCheck_clearview_check));
        startAnimation(translateAnimationIn);
    }

    public void hide() {
        EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW));
        startAnimation(translateAnimationOut);
    }

    /**
     * 通过包含此控件的页面传进来FragmentManager去加载EssayMaterialsFragment
     */
    public void setChildFragmentManager(FragmentManager fragmentManager) {
        materialsFragment = new EssayMaterialsFragment();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.rl_content, materialsFragment);
        transaction.commit();
    }

    /**
     * 传递进来数据
     */
    public void setData(List<ExamMaterialListBean> cacheExamMaterialListBean) {
        if (materialsFragment != null) {
            materialsFragment.setData(cacheExamMaterialListBean);
        }
    }

    /**
     * 初始化出入动画
     */
    private void initAnimation() {
        if (translateAnimationIn == null) {
            translateAnimationIn = new TranslateAnimation(0, 0, DisplayUtil.getScreenHeight(), 0);
            // 步骤2：创建平移动画的对象：平移动画对应的Animation子类为TranslateAnimation
            // 参数分别是：
            // 1. fromXDelta ：视图在水平方向x 移动的起始值
            // 2. toXDelta ：视图在水平方向x 移动的结束值
            // 3. fromYDelta ：视图在竖直方向y 移动的起始值
            // 4. toYDelta：视图在竖直方向y 移动的结束值
            translateAnimationIn.setDuration(300);
            //        // 固定属性的设置都是在其属性前加“set”，如setDuration（）
            translateAnimationIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isShowMaterial = true;
                    setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        if (translateAnimationOut == null) {
            translateAnimationOut = new TranslateAnimation(0, 0, 0, DisplayUtil.getScreenHeight());
            translateAnimationOut.setDuration(300);
            translateAnimationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(View.INVISIBLE);
                    isShowMaterial = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
