package com.huatu.handheld_huatu.business.arena.utils;

import android.text.TextUtils;

import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 17/7/26.
 */
public class ArenaHelper {

    private static String TAG = "ArenaHelper";

    /**
     * 是否是行测模考大赛
     */
    public static boolean isPaperSCType(RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean != null) {
            boolean isScType = realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST;
            LogUtils.d("ArenaExamActivity", "isPaperSCType isScType  " + isScType);
            return isScType;
        }
        LogUtils.d("ArenaExamActivity", "isPaperSCType false");
        return false;
    }

    /**
     * 是否要记录后台时间，不可暂停，页面后台，继续计时
     * 模考大赛
     * 小模考
     */
    public static boolean isRecordBackgroundTime(RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean != null) {
            return (realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST
                    && ArenaDataCache.getInstance().isEnableExam())                                 // 模考大赛，并开始考试
                    || realExamBean.type == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH;         // 小模考
        }
        return false;
    }

    // 查看是否是分页加载
    public static String getPartExerciseIds(String exerciseIds, int reqType) {
        if (TextUtils.isEmpty(exerciseIds)) {
            return "";
        }
        String ids = getPartExerciseIds2(exerciseIds, reqType);
        if (TextUtils.isEmpty(ids)) {
            return exerciseIds;
        }
        return ids;
    }

    // 分页加载，返回要加载的ids
    private static String getPartExerciseIds2(String exerciseIds, int reqType) {
        if (isRecyView(reqType)) {
            ArenaDataCache var = ArenaDataCache.getInstance();
            if (var != null) {
                if (var.errorIdsAry != null && var.errorIdsAry.size() > 0) {
                    List<Integer> idsAry = new ArrayList<>();
                    int startIndex = 0;
                    int endIndex = 0;
                    if ((var.partErrorIdIndex + 1) * var.PART_SIZE < var.errorIdsAry.size()) {
                        startIndex = var.partErrorIdIndex * var.PART_SIZE;
                        endIndex = (var.partErrorIdIndex + 1) * var.PART_SIZE;
                    } else {
                        startIndex = var.partErrorIdIndex * var.PART_SIZE;
                        endIndex = var.errorIdsAry.size();
                    }
                    for (int i = startIndex; i < endIndex; i++) {
                        idsAry.add(var.errorIdsAry.get(i));
                    }
                    var.partErrorIdIndex++;
                    return getExerciseIds(idsAry);
                }
            }
        }
        return exerciseIds;
    }

    public static boolean isLoadPartErrorIdComp() {
        ArenaDataCache var = ArenaDataCache.getInstance();
        if (var.partErrorIdIndex >= var.partErrorIdIndexMax) {
            return true;
        }
        return false;
    }

    // 试题分页加载，是否还有更多去加载
    public static boolean isNeedPartErrorIdLoad(int position) {
        if (!isLoadPartErrorIdComp()) {
            ArenaDataCache var = ArenaDataCache.getInstance();
            if (var != null) {
                if (position > var.partErrorIdIndex * var.PART_SIZE - 10) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getExerciseIds(List<Integer> resutls) {
        StringBuilder ids = new StringBuilder();
        for (Integer id : resutls) {
            ids.append(id).append(",");
        }
        if (!TextUtils.isEmpty(ids.toString())) {
            ids = new StringBuilder(ids.substring(0, ids.length() - 1));
        }
        return ids.toString();
    }

    public static List<ArenaExamQuestionBean> getQuestionBeanList(int reqType, List<ArenaExamQuestionBean> questionBeanList) {
        if (isRecyView(reqType)) {
            ArenaDataCache var = ArenaDataCache.getInstance();
            if (var != null) {
                if (var.questionBeanList == null) {
                    var.questionBeanList = new ArrayList<>();
                }
                var.questionBeanList.addAll(questionBeanList);
                return var.questionBeanList;
            }
        }
        return questionBeanList;
    }

    // 是否是收藏分页加载中的第一次加载
    public static boolean isFirstLoad(int reqType) {
        if (isRecyView(reqType)) {
            ArenaDataCache var = ArenaDataCache.getInstance();
            if (var.partErrorIdIndex == 0) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static void setEnableExamTrue() {
        ArenaDataCache.getInstance().setEnableExam(true);
        EventBus.getDefault().post(new ArenaExamMessageEvent(ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_START_EXAM));
    }

    /**
     * 是否是倒计时
     * 以前是：真题演练、专项模考、阶段测试、模考大赛、小模考
     * 2019.4.4改为：
     * 模考大赛、往期模考、小模考、精准估分、专项模考
     */
    public static boolean isCountDown(int requestType) {
        return requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_PAST_MOKAO
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN
                || requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN;
    }

    /**
     * 可以休息（暂停）
     * 模考大赛、小模考之外的都可以暂停休息
     */
    public static boolean isCanRest(int requestType) {
        return requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST
                && requestType != ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH;
    }

    // 计算做过多少题
    public static int getDoneCount(RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            return -1;
        }
        List<ArenaExamQuestionBean> questionBeanList = realExamBean.paper.questionBeanList;
        int answerCount = 0;                    // 坐过的题数量
        for (int i = 0; i < questionBeanList.size(); i++) {
            ArenaExamQuestionBean questionBean = questionBeanList.get(i);
            if (questionBean.userAnswer != 0 || questionBean.type == ArenaConstant.QUESTION_TYPE_SUBJECTIVE) {         // 做过了 或 是主观题 那就相当于做过了
                answerCount++;
            }
        }
        return answerCount;
    }

    public static final String MATCH_ENTER = "MATCH_ENTER";                     // EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST

    public static void showGoldExperience(String key) {
        if (SpUtils.getShowGoldExperience(key)) {
            ToastUtils.showRewardToast(key);
            SpUtils.setShowGoldExperience(key, false);
        }
    }

    public static boolean isRecyView(int requestType) {
        return requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE;
    }

    public static String setNoZero(String str) {
        if (str != null && str.endsWith(".0")) {
            int index = str.length() - ".0".length();
            if (index > 0 && index < str.length()) {
                return str.substring(0, index);
            }
        }
        return str;
    }
}
