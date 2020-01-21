package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by Administrator on 2018\11\6 0006.
 */

public class UserMessageBean {

    public int isRead;
    public String noticeId;
    public String noticeTime;
    public PayloadBean payload;
    public String    type;
    public int    userId;
    public int    holdType;
    public String detailType;


    public static class PayloadBean{

        public String text;
        public String title;
        public CustomBean custom;

    }

    public static class CustomBean{

        public String teacher;
        public String picture;
        public String time;
      //  public String mockId;
       // public String order;
        public String businessId;
        public int isLive;
        public String netClassId;//课程id
        public String syllabusId;//大纲id
        public String lession_id;//课件id

        // "classTitle": "课程直播课",
        //                        "areaName": "湖北",
        //                        "createTime": "2019-08-05 13:29:48",
        //                        "answerCardId": 625,
        //                        "paperName": "哈哈哈哈",
        //                        "questionBaseId": 41,
        //                        "paperId": 0,
        //                        "topicType": 0
        public String classTitle;
        public String areaName;
        public String paperName;
        public String createTime;
        public long answerCardId;
        public long questionBaseId;
        public long paperId;
        public int topicType;
    }
  //  {\"custom\":{\"teacher\":\"刘有珍\",\"picture\":\"http://www.baidu.com.123.png\"},\"text\":\"【2018年公考面试】结构化面试理论课结构化面试理论课\",\"title\":\"18:30您有一节直播课，不要迟到哦~\"}


}
