package com.huatu.handheld_huatu.mvpmodel.essay;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2019\7\10 0010.
 */

public class SingleQuestionInfoBean {

 /*   public  long     videoId;//": 0,
    public  String   token;//": "",

    public String audioUrl;
    public int correctMode;
    public List<SubScoreInfo> subScoreList;
    public List<SubScoreInfo> addScoreList;
    public List<AnswerComment> answerList; //多个答案  answerFlag答案类型(0 参考答案 1标准答案)
    public boolean videoAnalyzeFlag;
    public String  answerRequire;
    public int     questionDetailId;//试题详情ID

    public int      bizStatus;      //答题状态(0 空白，未开始 1未完成 2 已交卷 3已批改)
    public String   stem;

    public String analyzeQuestion;  //审题要点 试题分析

    public List<UserMetaInfo> userMeta;

    public static class SubScoreInfo{

        public int     type;// 43767,
        public int     sequenceNumber;// 14086,
        public String     score;// 22415,  有可能是0.5、1.5
        public String  scorePoint;// \u6d4b\u8bd5\u5185\u5bb9f32r
    }*/

    public static class UserMetaInfo{

        public String     content;// 测试内容82x9,

       // @SerializedName("imageUrl")
        public String     finalUrl;// 测试内容884x,//批注完成后带批注截图的url

        public int        sort;// 47264
    }
}
