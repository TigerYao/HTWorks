package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by DongDong on 2016/8/4.
 */
public class PushMessage {
    //消息标题
    public String title;
    //消息内容
    public String content;
    //类型 0为广告，1为课程购买页,2为活动页面，3为图书。
    // 4竞技赛场首页
//    5真题演练列表
//    6精准估分列表
//    7专项模考列表
//    8真题演练答题页
//    9模考大赛报名页
//    10直播课列表
//    11录播课列表
//    12消息中心
//    13申论批改列表
//    14申论人工批改报告
//    15售后大纲
    public int type;

    //跳转消息列表
    public String view;
    //广告的url，活动页的url
    public String url;
    //课程购买页的classId
    public String NetClassId;
    //活动中心活动页面的标题
    public String activityTitle;
    //活动中心活动页所需的id
    public long id;

    public long questionBaseId;
    public long paperId;
    public int topicType;//（0标准答案，1套题，2文章写作），除1之外，都是单题
    public long answerCardId;
    public String paperName;
    public String areaName;
    public int isLive;
    //跳售后大纲，已购课程的id
    public String netClassId;
   // 跳售后大纲，已购课程的课件id
    public int lession_id;
    // 跳售后大纲，已购课程的大纲id
    public String syllabusId;

}
