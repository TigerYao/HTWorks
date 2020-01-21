package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;


public class SingleAreaListBean implements Serializable {

    /**
     * areaId : 23375
     * areaName : 测试内容7755
     * bizStatus : 41587
     * correctTimes : 41621
     * limitTime : 11036
     *
     * bizStatus 0 空白 1未完成 2 已交卷 3已批改）
     */
    public int areaId;
    public String areaName;
    public int bizStatus;
    public int correctTimes;
    public int limitTime;
    public long questionBaseId;
    public long questionDetailId;

    public int correctNum;      // 我的智能批改次数
    public int manualNum;       // 我的人工批改次数
}
