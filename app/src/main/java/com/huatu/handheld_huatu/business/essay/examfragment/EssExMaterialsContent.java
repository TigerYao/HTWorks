package com.huatu.handheld_huatu.business.essay.examfragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.OnSelectListener;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableTextHelper;
import com.huatu.handheld_huatu.business.essay.cusview.drawimpl.CusAlignText;
import com.huatu.handheld_huatu.business.essay.cusview.drawimpl.CusDrawUnderLineEx;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 材料内容Fragment
 */
public class EssExMaterialsContent extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "EssExMaterialsContent";

    @BindView(R.id.ess_ex_materials_content)
    ExerciseTextView essExMaterialsContent;

    String content;
    String id;

    private Bundle extraArgs;

    SelectableTextHelper mSelectableTextHelper;
    CusAlignText cusAlignText;
    ArrayList<String> uContentls = new ArrayList<>();           // 原本自带下划线的内容匹配到这里

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
        if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize) {
            setTxtSize();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW) {
            if (mSelectableTextHelper != null) {
                mSelectableTextHelper.clearView();
            }
        }
        return true;
    }

    @Override
    public int onSetRootViewId() {
        return R.layout.ess_ex_materials_content_layout;
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (args != null) {
            requestType = args.getInt("request_type");
            content = args.getString("content");
            id = args.getString("id");
            extraArgs = args.getBundle("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
        }

        if (content != null) {
            essExMaterialsContent.setType(1);
            EssayHelper.getString(content, uContentls);                                                     // 把 "<u>.+?</u>" 中的内容添加进 ArrayList 中
            essExMaterialsContent.setTextImgTagEssay(content);                                              // 是否包含图片
            essExMaterialsContent.setHtmlSource(DisplayUtil.dp2px(360 - 20), content);              // 如果包含图片，限制图片宽度
            if (!Method.isActivityFinished(mActivity)) {
                mSelectableTextHelper = new SelectableTextHelper.Builder(essExMaterialsContent)             // 打开复制
                        .setSelectedColor(ContextCompat.getColor(mActivity, R.color.essay_sel_color))
                        .setCursorHandleSizeInDp(10)
                        .setCursorHandleColor(ContextCompat.getColor(mActivity, R.color.main_color))
                        .build();
                mSelectableTextHelper.setSel(id);
            }
            // 实现了 ExerciseTextView.CusDraw 接口，所以 ExerciseTextView 的绘制全交给这里
            cusAlignText = new CusAlignText(essExMaterialsContent);
            // 实现了 CusAlignText.DrawLine 划线接口，所以 CusAlignText 的下划线绘制会交给这里
            final CusDrawUnderLineEx cusDrawUnderLineEx = new CusDrawUnderLineEx(cusAlignText);

            cusAlignText.setFront(true);

            essExMaterialsContent.setmCusDraw(cusAlignText);

            mSelectableTextHelper.setSelectListener(new OnSelectListener() {
                @Override
                public void onTextSelected(CharSequence content) {
                    // 问题内容取消选择
                    EventBusUtil.sendMessage(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW));
                }

                @Override
                public void updateView(int type) {
                    Log.d(TAG, "updateView ==  " + type);
                    if (type == 0) {
                        cusAlignText.setSelectableTextHelper(null);
                    } else if (type == 1) {
                        cusAlignText.setSelectableTextHelper(null);
                    } else {
                        cusAlignText.setSelectableTextHelper(mSelectableTextHelper.mSelectionInfo);
                    }
                    // mSelectableTextHelper.indexList是已划线的区域
                    // uContentls是<u> 标签，下划线区域
                    cusDrawUnderLineEx.setUnderLine(mSelectableTextHelper.indexList, uContentls);
                }
            });
            // 把长按选择的 对象给 CusAlignText 重绘的时候，每次判断是否有选择然后绘制背景
            cusAlignText.setSelectableTextHelper(mSelectableTextHelper.mSelectionInfo);
            // 把下划线位置列表 indexList 和本身的下划线 uContentls 给CusDrawUnderLineEx 每次绘制的时候，进行绘制
            cusDrawUnderLineEx.setUnderLine(mSelectableTextHelper.indexList, uContentls);
        }

        setTxtSize();
    }

    private void setTxtSize() {
        float materialsTxtSize = EssayExamDataCache.getInstance().materialsTxtSize;
        if (materialsTxtSize > 0) {
            if (essExMaterialsContent != null) {
                if (cusAlignText != null) {
                    cusAlignText.setTxtModel();
                }
                essExMaterialsContent.setTextSize(materialsTxtSize);
            }
        }
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (!isVisibleToUser) {
            if (mSelectableTextHelper != null) {
                mSelectableTextHelper.clearView();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStop() {
        super.onStop();
        EssayExamDataCache.getInstance().writeToFileCahce_materials_sel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public static EssExMaterialsContent newInstance(Bundle extra) {
        EssExMaterialsContent fragment = new EssExMaterialsContent();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }

}
