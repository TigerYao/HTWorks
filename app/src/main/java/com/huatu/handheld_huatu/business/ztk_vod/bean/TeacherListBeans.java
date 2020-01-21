package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/9/6.
 */

public class TeacherListBeans implements Serializable{
    public String TeacherId;
    public String TeacherName;
    public String goodat;
    public String roundPhoto;
    public String style;
    public String score;

    @Override
    public String toString() {
        return "TeacherListBeans{" +
                "TeacherId='" + TeacherId + '\'' +
                ", TeacherName='" + TeacherName + '\'' +
                ", goodat='" + goodat + '\'' +
                ", roundPhoto='" + roundPhoto + '\'' +
                ", style='" + style + '\'' +
                ", score='" + score + '\'' +
                '}';
    }
}
