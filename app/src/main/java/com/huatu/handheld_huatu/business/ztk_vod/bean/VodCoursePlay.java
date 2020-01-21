package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/9/8.
 *
 */

public class VodCoursePlay implements Serializable {
    public String Title;
    public int status;
    public String liveId;
    public String serviceType;
    public String startTime;
    public String cc_key;
    public String cc_uid;
    public String cc_vid;
    public String fileSize;
    public int haszhibo;
    public String password;
    public String timeLength;
    public String rid;
    public String url;
    public String username;

    @Override
    public String toString() {
        return "VodCoursePlay{" +
                "Title='" + Title + '\'' +
                ", status=" + status +
                ", liveId='" + liveId + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", cc_key='" + cc_key + '\'' +
                ", cc_uid='" + cc_uid + '\'' +
                ", cc_vid='" + cc_vid + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", haszhibo=" + haszhibo +
                ", password='" + password + '\'' +
                ", timeLength='" + timeLength + '\'' +
                ", rid='" + rid + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
