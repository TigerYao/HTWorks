package com.huatu.handheld_huatu.business.arena.downloadpaper.bean;

/**
 * 为了对比本地是否是最新的试卷，获取网络试卷信息进行对比
 */
public class ArenaPaperInfoNet {

    private String gmtModify;

    private long id;

    public String getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(String gmtModify) {
        this.gmtModify = gmtModify;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
