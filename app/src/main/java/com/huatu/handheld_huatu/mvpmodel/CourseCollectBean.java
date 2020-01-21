package com.huatu.handheld_huatu.mvpmodel;

import java.util.List;

/**
 * Created by cjx on 2018\11\8 0008.
 */

public class CourseCollectBean {

    public int          classId;// 87016,
    public String       title;// 测试红包领取查询功能003,
    public String       videoType;// 0,
    public int          timeLength;// 0,
    public String       img;// http://p.htwx.net/images/course_default.jpg,
    public int          price;// 98,
    public int          actualPrice;// 10,
    public String       redEnvelopeId;// 137,
    public String       terminedDesc;// ,
    public int          isTermined;// 0,
    public int          secondKill;// 0,
    public String        suit;// 0,
    public String        limit;// 0,
    public List<Teacher>    teacherInfo;
/*             teacherInfo;// [
    {
        teacherId;// 1861,
            teacherName;// 测试教师,
            roundPhoto;// http://v.huatu.com/images/default_teacher.jpg
    }
                ],*/
    public String                    liveDate;// 11月05日,
    public String                    count;// 6,
    public String                    description;// 6人已抢,
    public String                    collageActivityId;// ,
    //public List<String> activeTag;// [   红包   ],

    public String[] activeTag;
    public int                    isSaleOut;// 0,
    public int                     isRushOut;// 0,
    public int                     isBuy;// 0
    public boolean                  offLine ;//下架

    public static class Teacher{

        public String teacherName;
        public String roundPhoto;
    }

    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
