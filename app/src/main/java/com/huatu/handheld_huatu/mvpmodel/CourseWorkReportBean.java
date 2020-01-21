package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cjx on
 */

public class CourseWorkReportBean {

 /*       public int maxCorrect;   //最高答对题数
        public int tcount;         //总题数
        public int avgTimeCost;
        public String avgCorrect;  //平均答对题数
        public int rcount;         //对题数

        public String testTimeInfo;//测试时间
        public String submitTimeInfo ;//提交时间

        public List<KnowledgePoint> points;

        public List<ScoreRank> myRank;

        public List<Integer> corrects;          // 对应的题的答题结果(是否答对). 0;//未做答,1;//正确,2;//错误*/


    //  recommendedTime;// 120,
    //          id;// 802409285169854376,
    //          userId;// 233906517,
    //           subject;// 1,
    //          catgory;// 1,
    //          score;// 0,
    //          difficulty;// 5,
    public String name;// 课后练习,
    public int rcount;// 0,
    public String wcount;// 2,
    public int    ucount;// 0,

    public int timesTotal;
    //          status;// 3,
    //          type;// 15,
    //          terminal;// 1,
    //          expendTime;// 3,
    public String speed;// 1,
    public long createTime;// 1552385405910,
    public int lastIndex;// 1,
    public int remainingTime;// -1,
    //        cardCreateTime;// null,

    public List<Integer> corrects;

    public List<KnowledgePoint> points;

    //         moduleStatus;// null,
    //         moduleCreateTime;// null,
    //         iconUrl;// null,
    //         hasGift;// 0,
    //        syllabusId;// null,
    public String maxCorrect;// 0,
    // public String         ranks;// 0,
    public int avgTimeCost;// 0,
    public String avgCorrect;// 0,
    //         myRank;// [],
    public int tcount;// 2

    @SerializedName("myRank")
    public int myrank;

    @SerializedName("ranks")
    public List<ScoreRank> allScoreRank;


    public static class KnowledgePoint {

        public int id;// 642,
        public String name;// 判断推理,
        public String qnum;// 40,
        public String rnum;// 20,
        public int wnum;// 10,
        public int unum;// 10,
        public String times;// 45,
        public String speed;// 1,
        public int level;// 3,
        public String accuracy;// 22.5
    }

    public static class ScoreRank {

        public int rank;//;// 1,
        //    uid;// 233982730,
        public String uname;//;// app_ztk1156193081,
        public String avatar;//;// http;////tiku.huatu.com/cdn/images/vhuatu/avatars/default.png,
        public String rcount;//;// 20,
        public int expendTime;//;// 300,



        public long submitTimeInfo;//;// 0
        public String submitTimeDes;//;// 0
    }

}

