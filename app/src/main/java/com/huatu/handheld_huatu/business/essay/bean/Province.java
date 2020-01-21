package com.huatu.handheld_huatu.business.essay.bean;

import java.io.Serializable;

/**
 * Created by ht on 2017/12/5.
 */
public class Province implements Serializable {

    public long areaId;                 // 地区id
    public long id;                     //
    public long questionBaseId;         // 题目baseId
    public long questionDetailId;       // 题目detailId
    public int bizStatus;               // 批改状态 1、未批改
    public int correctSum;              // 单题练习人数
    public long limitTime;              // 限时
    public int correctTimes;            // 批改次数
    public String areaName;             // 地区名称
    public String questionYear;         // 年份
    public String questionDate;

    public int lastType;                // 最后一次进入的答题卡类型
}
