package com.huatu.handheld_huatu.business.essay.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeworkSingleListBean implements Serializable {

    public ArrayList<HomeworkSingleBean> exercisesItemList;
    public int finishedCount;
    public int total;

    public static class HomeworkSingleBean implements Serializable {

        public long questionBaseId;
        public long questionDetailId;
        public String stem;             // 题目
        public int inputWordNum;        // 字数
        public int questionType;        // 5为文章写作，其他为标准答案
        public int sort;                // 试卷排序
        public int spendtime;           // 用时
        public int areaId;              // 地区Id
        public String areaName;         // 地区名称
        public String correctMemo;      // 被退回原因
        public String clickContent;     // 批改中，点击提示
        public int correctNum;          // 批改的次数
        public long questionAnswerId;   // 答题卡Id，被退回需要
        public int bizStatus;           // 0未开始(1, "未完成")(3,已批改）2，4,"批改中"),5被退回
        public double examScore;        // 得分
        public double score;            // 试卷分数
    }
}
