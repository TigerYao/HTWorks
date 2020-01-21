package com.huatu.handheld_huatu.business.essay.examfragment;

import android.os.Bundle;
import android.util.Log;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.OnSelectListener;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 问题内容Fragment
 */
public class EssExQuestionContent extends BaseFragment {

    private static final String TAG = "EssExEditAnswerContent";

    @BindView(R.id.ess_ex_materials_ques_title)
    ExerciseTextView ess_ex_materials_ques_title;

    private SingleQuestionDetailBean singleQuestionDetailBean;

    @Override
    public int onSetRootViewId() {
        return R.layout.ess_ex_question_content_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize) {
            setTxtSize();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW) {
            // 取消选择
            if (ess_ex_materials_ques_title != null) {
                ess_ex_materials_ques_title.clearView();
            }
        }
        return true;
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (args != null) {
            singleQuestionDetailBean = (SingleQuestionDetailBean) args.getSerializable("singleQuestionDetailBean");
        }
        if (singleQuestionDetailBean != null) {
            ess_ex_materials_ques_title.setHtmlSource(EssayHelper.getFilterTxt(singleQuestionDetailBean.answerRequire));
            ess_ex_materials_ques_title.openCopy();
            ess_ex_materials_ques_title.mSelectableTextHelper.setSelectListener(new OnSelectListener() {
                @Override
                public void onTextSelected(CharSequence content) {
                    // 材料页 让材料取消选择
                    EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW));
                }

                @Override
                public void updateView(int type) {

                }
            });
        }
        setTxtSize();
    }

    private void setTxtSize() {
        float materialsTxtSize = EssayExamDataCache.getInstance().materialsTxtSize;
        if (materialsTxtSize > 0) {
            ess_ex_materials_ques_title.setTextSize(materialsTxtSize);
        }
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (!isVisibleToUser) {
            if (ess_ex_materials_ques_title != null && ess_ex_materials_ques_title.mSelectableTextHelper != null) {
                ess_ex_materials_ques_title.mSelectableTextHelper.clearView();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public static EssExQuestionContent newInstance(Bundle extra) {
        EssExQuestionContent fragment = new EssExQuestionContent();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
