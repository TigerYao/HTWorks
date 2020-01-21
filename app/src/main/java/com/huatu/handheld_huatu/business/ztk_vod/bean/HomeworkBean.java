package com.huatu.handheld_huatu.business.ztk_vod.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/21
 * <p>
 * <p>
 * 学习 --- 课后作业的数据
 */
public class HomeworkBean {

    /**
     * courseTitle : wxr0312的直播事业单位的课程
     * courseId : 97351
     * undoCount : 1
     * wareInfoList : [{"courseWareTitle":"0312课的0319直播课程","courseWareId":37323,"videoType":2,"videoLength":"直播 - 03月19日 10:05-16:05","serialNumber":30,"answerCardId":861896851651977940,"questionIds":"","answerCardInfo":{"status":0,"wcount":0,"ucount":0,"rcount":0,"qcount":0},"isAlert":0}]
     */

//    @SerializedName("courseTitle")
    public String courseTitle;
//    @SerializedName("courseId")
    public long courseId;
//    @SerializedName("undoCount")
    public int undoCount;
//    @SerializedName("wareInfoList")
    public List<WareInfoListBean> wareInfoList;

    public static class WareInfoListBean {
        /**
         * courseWareTitle : 0312课的0319直播课程
         * courseWareId : 37323
         * videoType : 2
         * videoLength : 直播 - 03月19日 10:05-16:05
         * serialNumber : 30
         * answerCardId : 861896851651977940
         * questionType	  0 单题 1 套题
         * questionIds :
         * questionTitle	套题或试题 title
         * answerCardInfo : {"status":0,"wcount":0,"ucount":0,"rcount":0,"qcount":0}
         * isAlert : 0
         * type :1 行测 2 申论
         */

//        @SerializedName("courseWareTitle")
        public String courseWareTitle;
//        @SerializedName("correctMemo")

//        @SerializedName("courseWareId")
        public long courseWareId;
//        @SerializedName("syllabusId")
        public long syllabusId;
//        @SerializedName("videoType")
        public int videoType;
//        @SerializedName("videoLength")
        public String videoLength;
//        @SerializedName("serialNumber")
        public int serialNumber;
//        @SerializedName("answerCardId")
        public long answerCardId;
//        @SerializedName("questionIds")
        public String questionIds;
//        @SerializedName("questionTitle")
        public String questionTitle;
//        @SerializedName("questionType")
        public int  questionType;
//        @SerializedName("buildType")
        public int  buildType;
//        @SerializedName("answerCardInfo")
        public AnswerCardInfoBean answerCardInfo;
//        @SerializedName("isAlert")
        public int isAlert;


        public static class AnswerCardInfoBean {
            /**
             * status : 0
             * wcount : 0
             * ucount : 0
             * rcount : 0
             * qcount : 0
             */
            public String correctMemo;
//            @SerializedName("status")
            public int status;
//            @SerializedName("wcount")
            public int wcount;
//            @SerializedName("ucount")
            public int ucount;
//            @SerializedName("rcount")
            public int rcount;
//            @SerializedName("fcount")
            public int fcount;
//            @SerializedName("qcount")
            public int qcount;
            public int type;
            public long questionBaseId;
            public long paperId;
            public String areaName;
        }
    }
}
