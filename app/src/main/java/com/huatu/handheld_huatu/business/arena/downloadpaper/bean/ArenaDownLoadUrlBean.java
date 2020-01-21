package com.huatu.handheld_huatu.business.arena.downloadpaper.bean;

/**
 * 根据paperId获取到的pdf试卷下载路径和信息
 */
public class ArenaDownLoadUrlBean {

    private long paperId;

    private String updateTime;

    private String url;             // 根据paperId获取下载路径

    private int littleType;         // 报考科目下得小分类

    public long getPaperId() {
        return paperId;
    }

    public void setPaperId(long paperId) {
        this.paperId = paperId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLittleType() {
        return littleType;
    }

    public void setLittleType(int littleType) {
        this.littleType = littleType;
    }
}
