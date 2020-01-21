package com.huatu.handheld_huatu.business.arena.newtips.bean;

public class TipNewBean {

    public int category;            // 考试类型
    public int subject;             // 科目
    public long[] match;            // 当前模考大赛id
    public long[] small;            // 当前小模考id

    public int[] matchReadFlag;     // 模考大赛是否已读标记   0、未读 1、已读
    public int[] smallReadFlag;

}
