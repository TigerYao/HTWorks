package com.huatu.handheld_huatu.business.arena.downloadpaper.bean;

/**
 * 保存本地的已下载的试卷信息
 */
public class ArenaPaperFileBean {

    private long paperId;
    private String name;                // 试卷名称
    private int catgory;                // 报考科目
    private int littleType;                 // 报考科目下得小分类
    private String size;                // 大小
    private String filePath;            // 文件路径
    private String updateTime;          // 更新时间，用于判断是否是最新的试卷

    private long downloadTime;          // 下载的时间，用于下载列表排序

    // 本地使用变量
    private boolean isChecked;
    private boolean hasNew;             // 网络上有新试卷

    public long getPaperId() {
        return paperId;
    }

    public void setPaperId(long paperId) {
        this.paperId = paperId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCatgory() {
        return catgory;
    }

    public void setCatgory(int catgory) {
        this.catgory = catgory;
    }

    public int getLittleType() {
        return littleType;
    }

    public void setLittleType(int littleType) {
        this.littleType = littleType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }

    public long getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(long downloadTime) {
        this.downloadTime = downloadTime;
    }
}
