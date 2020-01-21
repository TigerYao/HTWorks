package com.huatu.handheld_huatu.business.arena.newtips.bean;

/**
 * 角标信息类
 */
public class TipBean {
    public int type;            // 类型(如果首页用就是试卷类型paperType，如果是模考大赛首页用就是Tab的subject)
    public int tipNum;          // 角标数字

    public TipBean(int type, int tipNum) {
        this.type = type;
        this.tipNum = tipNum;
    }
}
