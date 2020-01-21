package com.huatu.handheld_huatu.business.essay.examfragment;

import android.os.Bundle;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayExamDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.OnSelectListener;
import com.huatu.handheld_huatu.business.essay.cusview.AnswerListLinearlayout;
import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 答案详情展示
 */
public class EssExRightContent extends BaseFragment {

    @BindView(R.id.ess_ex_materials_ques_title)
    ExerciseTextView essExMaterialsQuesTitle;                   // 问题内容
    @BindView(R.id.ess_ex_answer_result_ll)
    AnswerListLinearlayout ess_ex_answer_result_ll;             // 参考答案

    private boolean isSingle;
    private SingleQuestionDetailBean singleQuestionDetailBean;

    @Override
    public int onSetRootViewId() {
        return R.layout.ess_ex_answer_detail_content_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(EssayExamMessageEvent event) {
        if (event == null) {
            return false;
        }
        if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExMaterialsContent_setTextSize) {
            setTxtSize();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_essExCardViewShow_clearview) {
            // 全部清除选择
            clearView();
        } else if (event.type == EssayExamMessageEvent.ESSAYEXAM_RIGHT_ANSWER_SELECT_clearview) {
            // 清除问题选择
            clearQuestionView();
        }
        return true;
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        initData();
        setTxtSize();
    }

    private void refreshView() {
        if (essExMaterialsQuesTitle != null) {
            if (singleQuestionDetailBean.answerRequire != null) {
                essExMaterialsQuesTitle.setHtmlSource(EssayHelper.getFilterTxt(singleQuestionDetailBean.answerRequire));
                essExMaterialsQuesTitle.openCopy();
                essExMaterialsQuesTitle.mSelectableTextHelper.setSelectListener(new OnSelectListener() {
                    @Override
                    public void onTextSelected(CharSequence content) {
                        ess_ex_answer_result_ll.clearView();
                    }

                    @Override
                    public void updateView(int type) {

                    }
                });
            }
        }
        if (ess_ex_answer_result_ll != null) {
            if (singleQuestionDetailBean != null) {
                ess_ex_answer_result_ll.refreshView(1, null, isSingle, singleQuestionDetailBean.answerList);
            }
        }
    }

    private void initData() {
        if (args != null) {
            requestType = args.getInt("request_type");
            isSingle = args.getBoolean("isSingle");
            if (isSingle) {
                singleQuestionDetailBean = EssayExamDataCache.getInstance().cacheSingleQuestionDetailBean;
            } else {
                singleQuestionDetailBean = (SingleQuestionDetailBean) args.getSerializable("singleQuestionDetailBean");
            }
        }
        if (singleQuestionDetailBean == null) {
            return;
        }
        refreshView();
    }

    public void hideTogShowQuesTV(int visibility) {
        if (essExMaterialsQuesTitle != null) {
            essExMaterialsQuesTitle.setVisibility(visibility);
        }
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        super.onVisibilityChangedToUser(isVisibleToUser);
        if (!isVisibleToUser) {
            clearView();
        }
    }

    public void clearView() {
        if (essExMaterialsQuesTitle != null) {
            essExMaterialsQuesTitle.clearView();
        }
        if (ess_ex_answer_result_ll != null) {
            ess_ex_answer_result_ll.clearView();
        }
    }

    private void clearQuestionView() {
        if (essExMaterialsQuesTitle != null) {
            essExMaterialsQuesTitle.clearView();
        }
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
    }

    /**
     * 改变字体大小
     */
    private void setTxtSize() {
        float textSize = EssayExamDataCache.getInstance().materialsTxtSize;
        if (textSize > 0) {
            essExMaterialsQuesTitle.setTextSize(textSize);
            ess_ex_answer_result_ll.setTextSize(textSize);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public static EssExRightContent newInstance(Bundle extra) {
        EssExRightContent fragment = new EssExRightContent();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
