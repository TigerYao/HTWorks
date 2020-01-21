package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.arch.ActivityDataBus;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.bean.ArenaDataBus;
import com.huatu.handheld_huatu.business.arena.setting.NightSwitchInterface;
import com.huatu.handheld_huatu.business.arena.setting.TextSizeSwitchInterface;
import com.huatu.handheld_huatu.business.arena.textselect.TextSelectManager;
import com.huatu.handheld_huatu.business.arena.textselect.impl.MarkerPenMenu;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArenaQuestionMaterial extends FrameLayout implements NightSwitchInterface, TextSizeSwitchInterface {

    private Context mContext;
    private View rootView;

    private int requestType;

    @BindView(R.id.tv_question_material)
    ExerciseTextView tvQuestionMaterial;                            // 材料
    @BindView(R.id.tv_question_material_requirement)
    ExerciseTextView tvMaterialRequirement;                         // 注意事项、要求

    private ArenaExamQuestionBean questionBean = new ArenaExamQuestionBean();

    private int resId = R.layout.arena_question_material;

    public ArenaQuestionMaterial(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ArenaQuestionMaterial(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ArenaQuestionMaterial(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
        setColor();
        setTextSize();

        requestType = ActivityDataBus.getData(context, ArenaDataBus.class).requestType;

        // 做题模式下，并且不是背题模式，开始复制高亮功能
        if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {
            TextSelectManager.newInstance().openSelectDealStyle(9, tvQuestionMaterial);
        }
    }

    public void setQuestionBean(ArenaExamQuestionBean bean) {
        this.questionBean = bean;
        updateViews();
    }

    private void updateViews() {
        int nightMode = SpUtils.getDayNightMode();
        String color;
        if (nightMode == 1) {
            color = "#421B29";
        } else {
            color = "#D3688F";
        }

        if (requestType < ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_ALL
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI) {
            MarkerPenMenu markMenu = (MarkerPenMenu) tvQuestionMaterial.getSelectTool().markMenu;
            markMenu.setMarkInfoList(questionBean.materialMark);
            markMenu.setQuestionId(questionBean.id);
        }

        tvQuestionMaterial.setVisibility(VISIBLE);
        if (questionBean != null && !Method.isListEmpty(questionBean.materials)) {
            String name = "<font color=\"" + color + "\">(资料)</font>";
            StringBuilder material = new StringBuilder();
            for (int i = 0; i < questionBean.materials.size(); i++) {
                material.append(questionBean.materials.get(i));
                if (i != questionBean.materials.size() - 1) {
                    material.append("<br/>");
                }
            }
            material = new StringBuilder(processStartSpace(material.toString()));

            String content;
            if (material.toString().startsWith("<p>")) {
                content = "<p>" + name + material.substring(3);
            } else {
                content = name + material;
            }
            tvQuestionMaterial.setHtmlSource(DisplayUtil.dp2px(360 - 30), content);
        } else if (questionBean != null && !TextUtils.isEmpty(questionBean.material)) {
            String name = "<font color=\"" + color + "\">(资料)</font>";
            String material = processStartSpace(questionBean.material);

            String content;
            if (material.startsWith("<p>")) {
                content = "<p>" + name + material.substring(3);
            } else {
                content = name + material;
            }
            tvQuestionMaterial.setHtmlSource(DisplayUtil.dp2px(360 - 30), content);
        } else {
            tvQuestionMaterial.setVisibility(GONE);
        }
        if (questionBean != null && !TextUtils.isEmpty(questionBean.require)) {
            tvMaterialRequirement.setVisibility(VISIBLE);
            String name = "<font color=\"" + color + "\">(注意事项/要求)</font>";
            String require = processStartSpace(questionBean.require);

            String content;
            if (require.startsWith("<p>")) {
                content = "<p>" + name + require.substring(3);
            } else {
                content = name + require;
            }

            tvMaterialRequirement.setHtmlSource(DisplayUtil.dp2px(360 - 20), content);
        } else {
            tvMaterialRequirement.setVisibility(GONE);
        }
    }

    /**
     * 去掉第一行开始的空格
     */
    private String processStartSpace(String content) {
        // 后台传的空格
        while (content.startsWith(" ")) {
            content = content.substring(1);
        }
        while (content.startsWith("<p> ")) {
            content = "<p>" + content.substring(4);
        }
        // 电脑键盘打的空格
        while (content.startsWith(" ")) {
            content = content.substring(1);
        }
        while (content.startsWith("<p> ")) {
            content = "<p>" + content.substring(4);
        }
        return content;
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

        tvQuestionMaterial.setTextSize(now_16);
        tvMaterialRequirement.setTextSize(now_16);
    }

    private void setColor() {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 0) {
            // 材料题的材料
            setBgColor(tvQuestionMaterial, R.color.arena_material_bg);
            setBgColor(tvMaterialRequirement, R.color.arena_material_bg);
        } else {
            setBgColor(tvQuestionMaterial, R.color.arena_material_bg_night);
            setBgColor(tvMaterialRequirement, R.color.arena_material_bg_night);
        }
    }

    private void setBgColor(View v, int color) {
        if (v != null) {
            v.setBackgroundColor(ContextCompat.getColor(getContext(), color));
        }
    }

    @Override
    public void nightSwitch() {
        setColor();
    }

    @Override
    public void sizeSwitch() {
        setTextSize();
    }
}
