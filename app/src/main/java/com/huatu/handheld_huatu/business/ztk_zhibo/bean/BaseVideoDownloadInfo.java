package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

/**
 * Created by saiyuan on 2017/12/6.
 */

public class BaseVideoDownloadInfo {
    public String downloadId;
    public int curPercent;
    public long totalLength;
    public int downloadLength;
    /* WAIT(-2),
    BEGIN(-1),
    DENY(0),
    START(1),
    FINISH(2),
    FAILED(3),
    STOP(4),
    LICENSE(12);  */
    public int downloadStatus;
    public int stopStatus;
    public String subjectName;
}
