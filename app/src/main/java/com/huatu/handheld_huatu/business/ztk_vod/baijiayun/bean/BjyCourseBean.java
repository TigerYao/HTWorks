package com.huatu.handheld_huatu.business.ztk_vod.baijiayun.bean;

import java.io.Serializable;

/**
 * Created by ht-ldc on 2017/12/1.
 */

public class BjyCourseBean implements Serializable {
    public String classid;//课程Id
    public String classTitle; //课程名称
    public String classScaleimg; //课程图片
    public boolean isSelect; //是否选中
    public String rid;//课件Id
    public String teacher;//课件教师名称

    @Override
    public String toString() {
        return "BjyCourseBean{" +
                "classid='" + classid + '\'' +
                ", classTitle='" + classTitle + '\'' +
                ", classScaleimg='" + classScaleimg + '\'' +
                ", isSelect=" + isSelect +
                ", rid='" + rid + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    public String getClassTitle() {
        return classTitle;
    }

    public void setClassTitle(String classTitle) {
        this.classTitle = classTitle;
    }

    public String getClassScaleimg() {
        return classScaleimg;
    }

    public void setClassScaleimg(String classScaleimg) {
        this.classScaleimg = classScaleimg;
    }

}
