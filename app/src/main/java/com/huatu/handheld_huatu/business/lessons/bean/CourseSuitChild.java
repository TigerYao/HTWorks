package com.huatu.handheld_huatu.business.lessons.bean;

import java.util.ArrayList;

/**
 * Created CHQ ht on 2017/4/15.
 */

public class CourseSuitChild extends CourseSuitBase {
    public String Title;
    public String StartDate;
    public String EndDate;
    public String TimeLength;
    public boolean isExpand = false;
    public boolean isSuit;
    public ArrayList<CourseSyllabusLesson> lessions;
    public class CourseSyllabusLesson extends CourseSuitBase {
        public String lessionTitle;
        public int isTrial;
    }
}
