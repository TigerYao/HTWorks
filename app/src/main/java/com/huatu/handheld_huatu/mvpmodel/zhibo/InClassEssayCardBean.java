package com.huatu.handheld_huatu.mvpmodel.zhibo;

/**
 * Created by Administrator on 2019\9\3 0003.
 */

public class InClassEssayCardBean {

    public long questionId;
    public int  paperId;
    public int  questionType; // 0套题  1单题
    public int  similarId;

    public int bizStatus;

    public String paperName;

    public String areaName;
    public String correctMemo;

    public int correctNum;

    public int fcount;//多题列表，已完成的题数

    public String clickContent;


    public int buildType;
    public long  answerCardId;
    public boolean isSingle(){
        return buildType==0;
    }



}
