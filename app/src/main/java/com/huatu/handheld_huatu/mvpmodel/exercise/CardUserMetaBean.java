package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;

/**
 * 用户排名分数信息Bean
 * Created by KaelLi on 2016/7/15.
 */
public class CardUserMetaBean implements Serializable{

    public int rank;                // 排名
    public int total;               // 总参加人次
    public double average;          // 全站平均分
    public String averageStr;       // String平均分
    public int beatRate;            // 几百比率
    public double max;              // 最高分
    public String maxStr;           // String最高分

    //------小模考首页添加数据
    public long reportTime;         // 统计数据更新时间
    public int rnumAverage;         // 平均做对题量
    public int rnumMax;             // 最大做对题数
    public int submitCount;         // 交卷人数
    public int submitRank;          // 交卷时间排序
    //------小模考首页添加数据
}
