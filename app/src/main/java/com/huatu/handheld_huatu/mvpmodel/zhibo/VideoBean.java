package com.huatu.handheld_huatu.mvpmodel.zhibo;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.QQGroupAddInfoBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ht on 2016/12/14.
 */
public class VideoBean {
    public QQGroupAddInfoBean QQ;
    public CourseDetail course;
    public List<LiveCourse> live;
    public List<LiveCourse> lession;

    @Override
    public String toString() {
        return "Data{" +
                "CourseDetail=" + course +
                ", LiveCourse=" + live +
                ", RecordCourse=" + lession +
                '}';
    }

    public static class CourseDetail implements Serializable {
        public String NetClassId;
        public String SubjectName;
        public String TeacherDesc;
        public String TimeLength;
        public String TypeName;
        public String scaleimg;
        public String title;
        public int free;
        @Override
        public String toString() {
            return "CourseDetail{" +
//                    "ActualPrice='" + ActualPrice + '\'' +
//                    ", ClassNo='" + ClassNo + '\'' +
                    ", NetClassId='" + NetClassId + '\'' +
//                    ", Status=" + Status +
                    ", SubjectName='" + SubjectName + '\'' +
                    ", TeacherDesc='" + TeacherDesc + '\'' +
                    ", TimeLength='" + TimeLength + '\'' +
                    ", TypeName='" + TypeName + '\'' +
//                    ", descriptions='" + descriptions + '\'' +
//                    ", descriptionsType=" + descriptionsType +
//                    ", endDate='" + endDate + '\'' +
//                    ", limitUserCount='" + limitUserCount + '\'' +
//                    ", liveContent='" + liveContent + '\'' +
//                    ", rid='" + rid + '\'' +
                    ", scaleimg='" + scaleimg + '\'' +
//                    ", startDate='" + startDate + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public static class LiveCourseParams implements Serializable {
        public String url;//原domainname
        public String password;
        public String JoinCode;
        public String cc_key;
        public String cc_uid;
        public String cc_vid;
        public int status;//1:直播中  0:直播已结束  -1:直播未开始  -2:录播   2:直播回放
        public int playerType;
        public String bjyCode;
        public String bjyVideoId;
        public String bjyRoomId;
        public String bjySessionId;
        public String token;
        public int tinyLive;
        public float process;//学习进度
    }

    public static class LiveCourse extends LiveCourseParams {
        public String Title;
        public String fileSize;
        public String fileUrl;
//        public int haszhibo;//1:Live 0:record
        public String rid;     //貌似一节课的id
        public String serviceType;
        public String startTime;
        public String timeLength;
        public String username;
        public boolean isOffFlag = false;
        public String offFilePath;
        public String offSignalFilePath;
        public int isComment;
        public int clarity;
        public int encryptType;
        public String teacher;
        public int hasTeacher;

        @Override
        public String toString() {
            return "LiveCourse{" +
                    "JoinCode='" + JoinCode + '\'' +
                    ", Title='" + Title + '\'' +
                    ", Status=" + status +
                    ", timeLength='" + timeLength + '\'' +
                    '}';
        }
    }
}
