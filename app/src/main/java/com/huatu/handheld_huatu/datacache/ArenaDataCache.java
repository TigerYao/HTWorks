package com.huatu.handheld_huatu.datacache;

import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;

import java.util.List;

/**
 * Created by michael on 17/7/20.
 * 行测问题缓存类。
 */
public class ArenaDataCache {
    public boolean isMockAutoSubmit = false;                        // 是否是模考自动提交
    private boolean EnableExam = true;                              // 是否可以做题
    public boolean isShowedAiTips = false;
    public boolean isShowedAiTipsIng = false;
    public boolean onclick_ZtkSchemeTargetStartTo = false;
    private int pPos;                                //（模考报名地区id）
    private int childPos;

    private String name;                                            // 名称

    public static final int PART_SIZE = 20;                         // 分页加载size

    public int partErrorIdIndex;                                    // 当前是第几页
    public int partErrorIdIndexMax;                                 // 最大页数
    private String errorIdsStr;                                     // 问题Ids
    public List<Integer> errorIdsAry;                               // 问题Ids

    public int errorAllCount;                                       // 问题数量

    public int deleteErrorCount;                                    // 删除数量
    public long inBackgroundTime;                                   // 后台时间

    public List<ArenaExamQuestionBean> questionBeanList;            // 问题集合

    public boolean isScAutoSubmit = false;                          // 是否是自动提交
    public boolean isFirstLoginIn = false;                          // 是否是第一次注册
    public boolean isDeletePaper = false;                           // 错题背题，是否删除有删除试题，用于错题页面刷新
    public int trainDailyFinishcount;

    // 在各种页面中传递的答题卡&试卷&试题信息
    // 因为存储在Bundle中传递，会数据量太大而报错，所以放到单例中传递
    // ArenaExamActivityNew             答题页
    // ArenaExamAnswerCardActivity      答题卡
    // ArenaExamReportActivity          报告一
    // ArenaExamReportExActivity        报告二
    // ScReportArenaFragmentNew         模考报告
    // 答题页 --> 答题卡  答题卡 --> 报告 报告 --> 答题
    public RealExamBeans.RealExamBean realExamBean;

    public static volatile ArenaDataCache instance = null;          // 单例

    public static ArenaDataCache getInstance() {
        if (instance == null) {
            synchronized (ArenaDataCache.class) {
                if (instance == null) {
                    instance = new ArenaDataCache();
                }
            }
        }
        return instance;
    }

    private ArenaDataCache() {
    }

    public int getpPos() {
        return pPos;
    }

    public void setpPos(int pPos) {
        this.pPos = pPos;
    }

    public int getChildPos() {
        return childPos;
    }

    public void setChildPos(int childPos) {
        this.childPos = childPos;
    }

    public boolean isEnableExam() {
        return EnableExam;
    }

    public void setEnableExam(boolean EnableExam) {
        this.EnableExam = EnableExam;
    }

    public void clearEnableExam() {
        EnableExam = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getErrorIdsAry() {
        return errorIdsAry;
    }

    public void setErrorIdsAry(List<Integer> errorIdsAry) {
        this.errorIdsAry = errorIdsAry;
        initData();
    }

    private void initData() {
        if (errorIdsAry != null) {
            errorAllCount = errorIdsAry.size();
            partErrorIdIndexMax = errorAllCount / PART_SIZE;
            if (errorAllCount % PART_SIZE > 0) {
                partErrorIdIndexMax++;
            }
        }
    }

    public String getErrorIdsStr() {
        return errorIdsStr;
    }

    public void setErrorIdsStr(String errorIdsStr) {
        this.errorIdsStr = errorIdsStr;
    }

    public int getPartErrorIdIndex() {
        return partErrorIdIndex;
    }

    public void setPartErrorIdIndex(int partErrorIdIndex) {
        this.partErrorIdIndex = partErrorIdIndex;
    }

    public int getPartErrorIdIndexMax() {
        return partErrorIdIndexMax;
    }

    public void setPartErrorIdIndexMax(int partErrorIdIndexMax) {
        this.partErrorIdIndexMax = partErrorIdIndexMax;
    }

    /**
     * 清除缓存内容
     */
    public void clearCacheErrorData() {
        name = null;
        errorAllCount = 0;
        partErrorIdIndex = 0;
        partErrorIdIndexMax = 0;
        deleteErrorCount = 0;
        errorIdsStr = null;
        if (questionBeanList != null) {
            questionBeanList = null;
        }
        if (errorIdsAry != null) {
            errorIdsAry = null;
        }
    }
}
