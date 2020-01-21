package com.huatu.handheld_huatu.mvpmodel;

import java.util.List;

/**
 * Created by Administrator on 2018\12\3 0003.
 */

public class StudyFilterBean {


    public List<ClassStatus>    classStatus;
    public List<SpeakTeacher>   speakTeacher;
    public List<ExamType>       examType;
    public List<PriceAttribute> priceAttribute;

    public static class ClassStatus {
        public String classStatusName;
        public int    studyStaus;
    }

    public static class SpeakTeacher {
        public String teacherName;
        public int    teacherId;
    }

    public static class ExamType {
        public String catName;
        public int    categoryId;
    }

    public static class PriceAttribute {
        public String priceName;
        public int    priceStatus;
    }


}
