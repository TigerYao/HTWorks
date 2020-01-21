package com.huatu.handheld_huatu.mvpmodel;

import java.util.ArrayList;

public class VideoStudyReportBean {
    public CourseWorkPractice classPractice; //随堂练习
    public CourseWorkPractice courseWorkPractice;//课后作业
    public LiveReport liveReport;//直播听课记录
    public ArrayList<HomeTreeBeanNew> points;
    public String teacherComment;

    public static class CourseWorkPractice {
        //随堂练
        public int averageTime;
        public int classAverageRcount;
        public int classAverageTime;
        //课后作业
        public int avgCorrect;
        public int avgMyCost;
        public int avgTimeCost;

        public int practiceStatus;//是否已答题， 0未答题，1已答题

//        public int avgTimeOut;
//        public int classAvgCorrect;
//        public int classAvgTimeOut;
//        public int canCorrect;

        public ArrayList<String> answers;
        public int coin;
        public ArrayList<Integer> corrects;
        public ArrayList<Integer> doubts;
        public String id;
        public int rcount;//答对数
        public int ucount;//未做数
        public int wcount;//错误数
        public int timesTotal;//答题花费的时间
        public String submitTimeInfo;
//        public PaPerInfo paper;

        public int getMyCostTime(){
            return averageTime == 0 ? avgMyCost : averageTime;
        }

        public int getClassCostTime(){
            return classAverageTime == 0 ? avgTimeCost : classAverageTime;
        }

        public int getTotalCount(){
            return rcount + ucount + wcount;
        }
    }

    public static class LiveReport {
        public int abovePercent; //高于百分比
        public int gold; //金币
        public int learnPercent; //学习进度
        public int learnTime; //学习时间 分钟
        public String teacherComment;//老师评价
    }

    public int getTotalRightCount(){
        return classPractice.rcount + courseWorkPractice.rcount;
    }

    public int getWriteCount(){
        return getTotalRightCount() + classPractice.wcount + courseWorkPractice.wcount;
    }

    public int getAllQuestionCount(){
        return classPractice.getTotalCount() + courseWorkPractice.getTotalCount();
    }

    public int getTotalTime(){
        return classPractice.timesTotal + courseWorkPractice.timesTotal;
    }

//    public static class PaPerInfo{
//        public ArrayList<Integer> questions;
//    }
}
