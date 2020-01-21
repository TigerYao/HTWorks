package com.huatu.handheld_huatu.business.ztk_vod.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/21
 * <p>
 * 学习 --- 最后作业的数据
 */
public class SectionalExaminationBean {

    /**
     * currentPage : 1
     * next : 0
     * list : [{"classId":87052,"className":"wxr1113直播课程事业单位+测试","undoCount":4,"child":[{"examName":"阶段测试zc","examId":3528397,"endTime":"2019-03-13 22:26:16","startTime":"2019-03-11 22:26:16","isAlert":0,"syllabusId":8362039,"coursewareNum":2,"status":2,"showTime":"03月11日 22:26-13日 22:26","isEffective":1,"isExpired":true,"alreadyRead":0}]}]
     */

    public int currentPage;
    public int next;

    public List<ListBean> list;

    public static class ListBean {


        public int classId;
        public String className;
        public int undoCount;
        public List<ChildBean> child;

        public String type;
        public boolean unfold;

        public static class ChildBean {


            public String examName;
            public int examId;
            public String endTime;
            public String startTime;
            public int isAlert;

            public int syllabusId;

            public int coursewareNum;

            public int status;

            public String showTime;

            public int isEffective;

            public boolean isExpired;

            public int alreadyRead;
        }
    }
}


/**
 * examName : 阶段测试zc
 * examId : 3528397
 * endTime : 2019-03-13 22:26:16
 * startTime : 2019-03-11 22:26:16
 * isAlert : 0
 * syllabusId : 8362039
 * coursewareNum : 2
 * status : 2
 * showTime : 03月11日 22:26-13日 22:26
 * isEffective : 1
 * isExpired : true
 * alreadyRead : 0
 */
