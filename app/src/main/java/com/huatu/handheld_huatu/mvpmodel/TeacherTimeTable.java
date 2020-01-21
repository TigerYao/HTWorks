package com.huatu.handheld_huatu.mvpmodel;

import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;

import java.util.List;

/**
 * Created by Administrator on 2020\1\3 0003.
 */

public class TeacherTimeTable {

    public String         productId;//: 2497,
    public String         lessonTableId;//: 790,
    public String         lessonTableNo;//: KC20200102000007,
    public String         stageId;//: 1,
    public String         stageName;//: 基础,
    public String         subjectId;//: 1,
    public String         subjectName;//: 数量资料,

    public List<OneTimeTable> timeTableList;


    public TeacherTimeTypeInfo  toHeadBean(){

        TeacherTimeTypeInfo typeInfo=new TeacherTimeTypeInfo();
        typeInfo.stageName=stageName;
        typeInfo.subjectName=subjectName;
        typeInfo.lessonTableId=lessonTableId;
        return typeInfo;
    }


    public static class OneTimeTable{

        public String   lessonTableDetailId;
        public long   lessonDate;//: 1577980800000,
        public String   teacherNo;//: 041566,
        public String   teacherName;//: 花容,
        public String   duration;//: 30,
        public String             teacherDeviceId;//: 20013693,
        public String              teacherDeviceName;//: 成都市武侯区领事馆路7号保利中心南塔19楼19楼19层西昌厅

        public List<LessonTime> lessonTimeList;

    }

    public static class LessonTime{

        public long    startTime;//": 1577998800000,
        public long    endTime;//": 1577999400000
    }

    public static class  TeacherTimeTypeInfo{
      //  public String         productId;//: 2497,
        public String         lessonTableId;//: 790,
      //  public String         lessonTableNo;//: KC20200102000007,
     //   public String         stageId;//: 1,
        public String         stageName;//: 基础,
      //  public String         subjectId;//: 1,
        public String         subjectName;//: 数量资料,

        public String        lessonTableDetailId;

        public long lessonDate;

        public long  startTime;

        public long  endTime;

        public String  teacherName;

        public int type      ;//0  group 1 child


        public List<TeacherTimeTypeInfo> childs;

        private boolean isClosed;
        public void setClosed(boolean isclosed){
            isClosed=isclosed;
        }
        public boolean isClosed(){
            return isClosed;
        }

    }

}
