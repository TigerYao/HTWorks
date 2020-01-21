package com.huatu.handheld_huatu.business.ztk_vod.bean;

/**
 * Created by ht-ldc on 2017/9/2.
 */

import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * 网校课程列表筛选项的Bean
 *
 */
public class VideoListFilterBeans implements Serializable {
    public String code;
    public String msg;
    public JsonObject data;
    //公务员考试:"国考", "省考", "选调生", "村官", "党政公选", "三支一扶", "乡镇公务员"
    public static class ExamBean{
        public String examid;
        public String examname;
    }
    //事业单位"银行招聘" "农信社" "国家电网" "社会工作者"
    public static class SydwBean{
        public String sydwid;
        public String sydwname;
    }
    //教师 教师招聘" "教师资格" "特岗教师"
    public static class TeacherBean{
        public String teacherid;
        public String teachername;
    }
    // 科目:全部、行测、申论、面试、套餐课
    public static class SubjectBean {
        public String subjectid;
        public String subjectname;
    }
    // 价格排序:价格升序、价格降序
    public static class PriceOrderBean {
        public String orderid;
        public String ordername;
    }
    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"msg\":" + msg +
                ", data:" + data +
                '}';
    }
}

