package com.huatu.handheld_huatu.business.essay.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht on 2017/11/27.
 */
public class SingleExerciseResult implements Serializable {

    public ArrayList<Province> essayQuestionBelongPaperVOList;      // 涉及省份
    public String showMsg;                                          // 题干信息
    public int isAnswered;                                          // 是否作答过 0未作答 1已作答
    public long similarId;                                          // item的ID
    public boolean isOnline;                                        // 是否上线，true上线，false是下线
    public int correctTimes;                                        // 自己批改的次数
    public int bizStatus;                                           // 试题状态 1、未批改
    public boolean videoAnalyzeFlag;                                // 是否有视频解析

    public int questionType;                                        // 问题类型

    public int correctNum;                  // 智能批改次数
    public int correctSum;                  // 全站智能批改次数

    public int manualNum;                   // 人工批改次数
    public int manualSum;                   // 全站人工批改次数

}