package com.huatu.handheld_huatu.business.lessons.bean;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.QQGroupAddInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHQ on 2017/4/22.
 */

public class CourseSuitData {
    public CourseSuitFather father;
    public ArrayList<CourseSuitChild> child;
    public List<TeacherInfo> teacher;
    public QQGroupAddInfoBean QQ;
    public class TeacherInfo {
        public String TeacherId;
        public String TeacherName;
        public String roundPhoto;
    }
}
