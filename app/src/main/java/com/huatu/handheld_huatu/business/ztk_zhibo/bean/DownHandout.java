package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 课件Bean Created by cjx
 */
public class DownHandout implements Serializable {
    public static final String ID = "_id";
   // public static final String DOWNLOAD_ID = "download_ID";
    public static final String SUBJECT_ID = "subject_ID";
    public static final String SUBJECT_NAME = "subject_name";
    public static final String DOWN_STATUS = "down_status";
    public static final String COURSE_ID = "course_ID";
    public static final String FILE_TYPE = "file_type";
    public static final String SPACE = "space";
    public static final String FILE_URL = "file_url";
    public static final String REVERSE_1 = "reserve1";
    public static final String REVERSE_2 = "reserve2";
    public static final String USER_ID ="user_id";

    private String subject_ID;
    private String subject_name;

    private int down_status;
    private String course_ID;
    private long space;

    private int fileType;
    private String reserve1;
    private String reserve2;

    private long userId;

    private String fileUrl;

    public String getFileUrl() {
        return fileUrl;
    }
     public void setFileUrl(String fileurl) {
        this.fileUrl = fileurl;
    }


    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }



    public int getFileType() {
        return fileType;
    }

    public void setFileType(int playerType) {
        this.fileType = playerType;
    }



    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public String getCourseID() {
        return course_ID;
    }

    public String getRealCourseID() {
        if(TextUtils.isEmpty(course_ID)) return course_ID;
        return course_ID.contains("_") ? course_ID.split("_")[1]:course_ID;
    }

    public void setCourseID(String course_ID) {
        this.course_ID = course_ID;
    }



    public String getSubjectID() {
        return subject_ID;
    }

    public void setSubjectID(String subject_ID) {
        this.subject_ID = subject_ID;
    }

    public String getSubjectName() {
        return subject_name;
    }

    public void setSubjectName(String subject_name) {
        this.subject_name = subject_name;
    }


    public int getDownStatus() {
        return down_status;
    }

    public void setDownStatus(int down_status) {
        this.down_status = down_status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userid) {
        this.userId = userid;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    private boolean isSelect;
}
