package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by ht-djd on 2017/9/14.
 *
 */

public class TeacherJieshaoBean implements Serializable {
    public long code;
    public String msg;
    public DataBean data;
    public class DataBean {
        public String Brief;
        public String SubjectName;
        public String TeacherName;
        public String photo_url;
        public String teacherid;
        public String teachingstyle;

        public String getBrief() {
            return Brief;
        }

        public void setBrief(String brief) {
            Brief = brief;
        }

        public String getSubjectName() {
            return SubjectName;
        }

        public void setSubjectName(String subjectName) {
            SubjectName = subjectName;
        }

        public String getTeacherName() {
            return TeacherName;
        }

        public void setTeacherName(String teacherName) {
            TeacherName = teacherName;
        }

        public String getPhoto_url() {
            return photo_url;
        }

        public void setPhoto_url(String photo_url) {
            this.photo_url = photo_url;
        }

        public String getTeacherid() {
            return teacherid;
        }

        public void setTeacherid(String teacherid) {
            this.teacherid = teacherid;
        }

        public String getTeachingstyle() {
            return teachingstyle;
        }

        public void setTeachingstyle(String teachingstyle) {
            this.teachingstyle = teachingstyle;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "Brief='" + Brief + '\'' +
                    ", SubjectName='" + SubjectName + '\'' +
                    ", TeacherName='" + TeacherName + '\'' +
                    ", photo_url='" + photo_url + '\'' +
                    ", teacherid='" + teacherid + '\'' +
                    ", teachingstyle='" + teachingstyle + '\'' +
                    '}';
        }
    }




}
