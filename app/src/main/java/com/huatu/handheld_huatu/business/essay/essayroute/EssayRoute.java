package com.huatu.handheld_huatu.business.essay.essayroute;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ManulReportFragment;
import com.huatu.handheld_huatu.business.essay.EssayExamActivity;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseResult;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseResult;
import com.huatu.handheld_huatu.business.essay.checkfragment.CheckOrderFragment;
import com.huatu.handheld_huatu.mvpmodel.EssayAfterStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.InClassEssayCardBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.StringUtils;

import rx.subscriptions.CompositeSubscription;

public class EssayRoute {

    /**
     * 这里跳转申论做题，单题，文章写作，套题
     * 检查批改次数，弹窗
     *
     * @param isSingle             是否是单题
     * @param singleExerciseResult 单题内容
     * @param multiExerciseResult  套题内容
     * @param extraBundle          需要传递的东西，可以放到这里边，然后
     */
//    @LoginTrace(type = 0)
    public static void navigateToAnswer(final Context context,
                                        CompositeSubscription compositeSubscription,
                                        final boolean isSingle,
                                        final SingleExerciseResult singleExerciseResult,
                                        final MultiExerciseResult multiExerciseResult,
                                        Bundle extraBundle) {

        if (!CommonUtils.checkLogin(context)) return;

        int questionType;           // 问题类型 0、套题 >1、单题对应类型
        long id = 0;                // 校验批改次数

        String titleView;           // title，可不传
        long questionBaseId = 0;    // 单题id
        long similarId = 0;         // 单题地区组id

        long paperId = 0;           // 试卷id
        int areaId = 0;             // 地区id

        int lastType = 0;           // 未完成试卷最后一次批改方式

        if (isSingle) {  // 单题
            titleView = singleExerciseResult.showMsg;
            if (!singleExerciseResult.essayQuestionBelongPaperVOList.isEmpty()) {
                id = questionBaseId = singleExerciseResult.essayQuestionBelongPaperVOList.get(0).questionBaseId;
                lastType = singleExerciseResult.essayQuestionBelongPaperVOList.get(0).lastType;
            }
            similarId = singleExerciseResult.similarId;
            questionType = singleExerciseResult.questionType;

        } else {
            titleView = multiExerciseResult.paperName;

            id = paperId = multiExerciseResult.paperId;
            areaId = multiExerciseResult.areaId;

            questionType = 0;

            lastType = multiExerciseResult.lastType;
        }

        CustomRunnable action = new CustomRunnable(isSingle, titleView, questionBaseId, similarId, paperId, areaId, lastType, questionType, extraBundle);
        action.setContext(context);

        checkCanCorrectNet(context, compositeSubscription,
                0,
                questionType,
                id,
                action,
                0,
                "");
    }

    public static class CustomRunnable implements Runnable {

        private long QuestionBaseId, SimilarId, paperId;
        private int LastType, QuestionType, AreaId;
        private Bundle mExtraBundle;
        private boolean mIsSingle;
        private String mTitleView;
        private Context mContext;

        public void setContext(Context context) {
            mContext = context;
        }

        CustomRunnable(boolean isSingle, String titleView, long finalQuestionBaseId, long finalSimilarId, long finalPaperId, int finalAreaId, int finalLastType, int questionType, Bundle extraBundle) {
            mIsSingle = isSingle;
            mTitleView = titleView;
            QuestionBaseId = finalQuestionBaseId;
            SimilarId = finalSimilarId;
            paperId = finalPaperId;
            AreaId = finalAreaId;
            LastType = finalLastType;
            QuestionType = questionType;
            mExtraBundle = extraBundle;
        }

        @Override
        public void run() {
            if (mContext != null) {
                goEssayAnswer(mContext, mIsSingle, mTitleView, QuestionBaseId, SimilarId, paperId, AreaId, LastType, QuestionType, mExtraBundle);
            }

        }
    }

    /**
     * 跳申论做题
     *
     * @param isSingle       是否单题
     * @param titleView      试题名称，可不传
     * @param questionBaseId 单题id
     * @param similarId      单体组id
     * @param paperId        试卷id
     * @param areaId         地区id
     * @param lastType       最后一次批改方式 1、智能 2、人工
     * @param questionType   问题类型，0、套题 >1、单题各个类型
     * @param extraBundle    其他参数
     */
    public static void goEssayAnswer(Context context,
                                     boolean isSingle,
                                     String titleView,
                                     long questionBaseId,
                                     long similarId,
                                     long paperId,
                                     int areaId,
                                     int lastType,
                                     int questionType,
                                     Bundle extraBundle) {
        if (extraBundle == null) {
            extraBundle = new Bundle();
        }
        // 共有属性
        extraBundle.putString("titleView", titleView);
        extraBundle.putBoolean("isSingle", isSingle);
        // 单题属性
        extraBundle.putLong("questionBaseId", questionBaseId);
        extraBundle.putLong("similarId", similarId);
        // 套题属性
        extraBundle.putLong("paperId", paperId);
        extraBundle.putInt("areaId", areaId);
        // 最后一次批改方式
        extraBundle.putInt("correctMode", lastType);
        extraBundle.putInt("questionType", questionType);

        // 其他属性就在bundle里
        // "isFromCollection"   从收藏过去，单题就不能选择地区了
        // "areaName"           地区名称
        // "showQuestionId"     搜索，显示默认的问题
        // "showMaterialId"     搜索，显示默认的材料
        // "staticCorrectMode"  指定批改方式，如果指定了就不可切换 0、不限 1、智能 2、人工  模考只能是智能批改
        // "answerId"           人工批改，被退回，修改答案的时候，需要答题卡Id
        // "bizStatus"          人工批改，被退回，修改答案的时候，需要批改状态

        EssayExamActivity.show(context, EssayExamActivity.ESSAY_EXAM_NORMAL, extraBundle);
    }

    /**
     * 检查 单题/套题 是否有 人工/智能/人工or智能 批改次数，并执行runnable
     *
     * @param correctMode    批改方式 1、人工 2、智能 0、不限
     * @param questionType   问题类型，0、套题 >1、单题对应类型
     * @param id             单题questionBaseId，或者paperId
     * @param enoughRunnable 批改次数足够，要执行的方法
     * @param type           批改次数不足，点击左按钮出发操作 0、先试试，执行enoughRunnable 1、不执行，dismiss
     * @param buyLeftText    批改次数不足，购买页面左按钮显示文字
     */
    public static void checkCanCorrectNet(final Context context,
                                          CompositeSubscription compositeSubscription,
                                          final int correctMode,
                                          int questionType,
                                          final long id,
                                          final Runnable enoughRunnable,
                                          final int type,
                                          final String buyLeftText) {

        final Activity baseActivity = CommonUtils.findActivity(context);
        if ((baseActivity instanceof BaseActivity)) {
            ((BaseActivity) baseActivity).showProgress(true);
        }
        ServiceProvider.getCheckCount(compositeSubscription, questionType, id, new NetResponse() {

            @Override
            public void onError(Throwable e) {
                if ((baseActivity instanceof BaseActivity)) {
                    ((BaseActivity) baseActivity).hideProgress();
                }
                if (e instanceof ApiException) {
                    ToastUtils.showEssayToast(((ApiException) e).getErrorMsg());
                } else {
                    ToastUtils.showEssayToast("检查批改次数失败，请重试！");
                }
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                if ((baseActivity instanceof BaseActivity)) {
                    ((BaseActivity) baseActivity).hideProgress();
                }
                CheckCountInfo checkCountInfo = (CheckCountInfo) model.data;
                if (checkCountInfo != null) {
                    checkCanCorrect(context, correctMode, checkCountInfo, enoughRunnable, type, buyLeftText);
                } else {
                    ToastUtils.showEssayToast("检查批改次数失败，请重试！");
                }
            }
        });
    }

    /**
     * 检查 单题/套题 是否有 人工/智能/人工or智能 批改次数，并执行runnable
     *
     * @param correctMode    批改方式 1、人工 2、智能 0、不限
     * @param checkCountInfo 批改次数对象
     * @param enoughRunnable 批改次数充足，要执行的方法
     * @param type           批改次数不足，点击左按钮出发操作 0、先试试，执行runnable 1、不执行，dismiss
     * @param buyLeftText    批改次数不足，购买页面左按钮显示文字
     */
    public static void checkCanCorrect(final Context context,
                                       final int correctMode,
                                       CheckCountInfo checkCountInfo,
                                       final Runnable enoughRunnable,
                                       int type,
                                       String buyLeftText) {
        if (checkCount(correctMode, checkCountInfo)) {      // 检查批改次数，足够
            if (enoughRunnable != null) {
                enoughRunnable.run();
            }
        } else {                                            // 批改次数不足
            showBuyDialog(context, enoughRunnable, type, buyLeftText);
        }
    }

    /**
     * 检查批改次数
     *
     * @param correctMode    批改类型
     * @param checkCountInfo 批改次数
     */
    public static boolean checkCount(int correctMode, CheckCountInfo checkCountInfo) {

        CheckCountInfo.CountInfo intelligence = checkCountInfo.intelligence;    // 智能批改状态
        CheckCountInfo.CountInfo manual = checkCountInfo.manual;                // 人工批改状态

        switch (correctMode) {
            case 0:         // 不限批改类型
                if (intelligence == null || manual == null) {
                    return false;
                }
                if (intelligence.isLimitNum == 0 || manual.isLimitNum == 0) {                                   // 只要有一个无限次，就可以
                    return true;
                } else {
                    return intelligence.num + intelligence.specialNum + manual.num + manual.specialNum > 0;     // 只要批改次数大于0就可以了
                }
            case 1:         // 智能
                if (intelligence == null) {
                    return false;
                }
                if (intelligence.isLimitNum == 0) {
                    return true;
                } else {
                    return intelligence.num + intelligence.specialNum > 0;
                }
            case 2:         // 人工
                if (manual == null) {
                    return false;
                }
                if (manual.isLimitNum == 0) {
                    return true;
                } else {
                    return manual.num + manual.specialNum > 0;
                }
        }
        return false;
    }

    /**
     * 显示购买批改次数弹窗
     *
     * @param type        批改次数不足，点击左按钮出发操作 0、左侧Negative去购买。右侧先练习，执行runnable  1、左侧取消，右侧Positive去购买
     * @param buyLeftText 批改次数不足，购买页面左按钮显示文字
     */
    private static void showBuyDialog(final Context context, final Runnable runnable, final int type, String buyLeftText) {
        final CustomConfirmDialog dialog = DialogUtils.createEssayDialog(context, "", type == 0 ? "批改次数不足，先练习再购买" : "批改次数不足", 16, context.getResources().getColor(R.color.gray_666666));

        if (type == 0) {    // 左侧Negative去购买，右侧先练习
            dialog.setNegativeButton("去购买", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseFrgContainerActivity.newInstance(CommonUtils.findActivity(context), CheckOrderFragment.class.getName(), null);
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("先练习", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (runnable != null) {
                        runnable.run();
                    }
                    dialog.dismiss();
                }
            });
        } else {            // 左侧取消，右侧Positive去购买
            dialog.setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("去购买", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseFrgContainerActivity.newInstance(CommonUtils.findActivity(context), CheckOrderFragment.class.getName(), null);
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    /**
     * 做申论课后作业 不需要检查批改次数
     *
     * @param isSingle       // 是否是单题
     *                       param isFromArgue    // 是否是文章写作
     * @param titleView      // title可不传
     * @param questionBaseId // 单题id
     * @param paperBaseId    // 套题id
     * @param courseType     // 1、录播 2、直播 3、直播回放
     * @param courseId       // 课程Id
     * @param courseWareId   // 课件ID
     * @param syllabusId     // 大纲ID
     */
    public static void goEssayHomeworkAnswer(Context context,
                                             boolean isSingle,
                                             String titleView,
                                             long questionBaseId,

                                             long paperBaseId,

                                             int courseType,
                                             long courseId,
                                             long courseWareId,
                                             long syllabusId,
                                             Bundle extraBundle) {
        if (extraBundle == null) {
            extraBundle = new Bundle();
        }
        // 共有属性
        extraBundle.putString("titleView", titleView);
        extraBundle.putBoolean("isSingle", isSingle);
        // 单题属性
        extraBundle.putLong("questionBaseId", questionBaseId);
        // 套题属性
        extraBundle.putLong("paperId", paperBaseId);
        // 课后作业需要的属性
        extraBundle.putInt("courseType", courseType);
        extraBundle.putLong("courseId", courseId);
        extraBundle.putLong("courseWareId", courseWareId);
        extraBundle.putLong("syllabusId", syllabusId);

        // 其他属性就在bundle里
        // extraBundle.putString("areaName", areaName);           地区名称
        // 课后作业如果有未完成的答题卡，一定要传过来
        // extraBundle.putLong("answerId", mResult.answerId);    人工批改，被退回，修改答案的时候，需要答题卡Id
        // 如果是被退回继续做，要添加这个值
        // extraBundle.putInt("bizStatus", mResult.bizStatus);   人工批改，被退回，修改答案的时候，需要批改状态

        EssayExamActivity.show(context, EssayExamActivity.ESSAY_EXAM_HOMEWORK, extraBundle);
    }

    public static void showEssayHomework(final Activity context,
                                         InClassEssayCardBean cardBean,
                                         final String courseId,
                                         final long courseWareId,
                                         final String syllabusId,
                                         final int bizStatus,
                                         final long answerId,
                                         final int videoType) {

        final InClassEssayCardBean cardInfo = cardBean;
        final boolean isSingle = cardBean.isSingle();

        int curBizStatus = cardBean.bizStatus;

        if (curBizStatus == EssayAfterStatusEnum.CORRECT.getValue()) {
            ManulReportFragment.lanuch(context, answerId,
                    isSingle,
                    videoType,
                    StringUtils.parseLong(courseId),
                    courseWareId,
                    StringUtils.parseLong(syllabusId),
                    cardInfo.paperName, cardInfo.areaName, cardInfo.correctNum);
            return;
        }

        if (bizStatus == EssayAfterStatusEnum.CORRECT_RETURN.getValue()) {

            DialogUtils.showEssaySendBackDialog(context, String.valueOf(cardInfo.correctMemo), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extraBundle = new Bundle();
                    extraBundle.putLong("answerId", answerId);      // 退回修改答案的时候，用答题卡id请求问题
                    extraBundle.putInt("bizStatus", bizStatus);     // 退回修改答案的时候，需要用批改状态\
                    extraBundle.putString("areaName", cardInfo.areaName);
                    EssayRoute.goEssayHomeworkAnswer(context,
                            isSingle,
                            cardInfo.paperName,
                            cardInfo.questionId,
                            cardInfo.paperId,
                            videoType,
                            StringUtils.parseLong(courseId),
                            courseWareId,
                            StringUtils.parseLong(syllabusId),
                            extraBundle);
                }
            });
            return;
        }

        if (curBizStatus == EssayAfterStatusEnum.INIT.getValue()
                || curBizStatus == EssayAfterStatusEnum.UNFINISHED.getValue()) {//空白  "未完成"

            Bundle extraBundle = new Bundle();
            extraBundle.putLong("answerId", answerId);
            extraBundle.putString("areaName", cardInfo.areaName);
            EssayRoute.goEssayHomeworkAnswer(context,
                    isSingle,
                    cardInfo.paperName,
                    cardInfo.questionId,
                    cardInfo.paperId,
                    videoType, StringUtils.parseLong(courseId),
                    courseWareId, StringUtils.parseLong(syllabusId),
                    extraBundle);
        } else if (curBizStatus == EssayAfterStatusEnum.CORRECTING.getValue()
                || curBizStatus == EssayAfterStatusEnum.COMMIT.getValue()) {
            ToastUtils.showEssayToast(cardInfo.clickContent + "");
        }
    }

}
