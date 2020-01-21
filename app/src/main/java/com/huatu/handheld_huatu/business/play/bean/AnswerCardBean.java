package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;

public class AnswerCardBean implements Serializable{
    public int rcount; //正确数量
    public int status;   //答题卡状态	0：未开始，1：未完成，2：已结束
    public int ucount;   //未答数量
    public int wcount;   //错误数量
}
