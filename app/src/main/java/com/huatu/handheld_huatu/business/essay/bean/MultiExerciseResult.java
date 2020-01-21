package com.huatu.handheld_huatu.business.essay.bean;

/**
 * Created by ht on 2017/12/2.
 */
public class MultiExerciseResult {

    public int limitTime;                   // 限定时间
    public int paperDetailId;               // 试卷详情id
    public long paperId;                    // 试卷id
    public int areaId;                      // 地区id
    public String paperName;                // 试卷名称
    public String examDate;                 // 考试时间
    public boolean isOnline;                // 是否上线 true是上线 false是下线
    public boolean isAvailable;             // 是否可点击
    public boolean videoAnalyzeFlag;        // 是否有视频解析

    public int type;                        // 0、模考卷 1、真题卷  （模考不能人工批改）

    public int lastType;                    // 最后一次进入的答题卡类型

    public int correctNum;                  // 智能批改次数
    public int correctSum;                  // 全站智能批改次数

    public int manualNum;                   // 人工批改次数
    public int manualSum;                   // 全站人工批改次数

}
