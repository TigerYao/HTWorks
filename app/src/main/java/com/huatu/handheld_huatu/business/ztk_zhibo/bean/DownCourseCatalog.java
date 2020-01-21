package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 课程大纲目录 Created by cjx
 */
public class DownCourseCatalog implements Serializable {
    public static final String  ID = "_id";

    public static final String  CATALOG_ID = "catalogid";
    public static final String  PARENT_ID = "parentid";
    public static final String  SUBJECT_NAME = "subjectname";
    public static final String  LEARN_TIME = "learntime";          //学习时长
    public static final String  STUDY_SCHEDULE = "studySchedule";   //已学时  lessionBean.studySchedule,lessionBean.classHour
    public static final String  CLASSHOUR = "classHour";            //总学时

    public static final String  LEVEL = "level";                    //层级
    public static final String  REVERSE_1 = "reserve1";
    public static final String  REVERSE_2 = "reserve2";

    public static final String  COURSE_ID = "courseid";             //课程id
    public static final String  USER_ID ="user_id";

    public long   catalogid;
    public long   parentid;
    public String subjectname;
    public String learntime;
    public int    studyschedule;
    public int    classhour;
    public int    level;


    public String reserve1;
    public String reserve2;

    public long courseid;
    public long userId;
    //public int oldDownStatus;


}
