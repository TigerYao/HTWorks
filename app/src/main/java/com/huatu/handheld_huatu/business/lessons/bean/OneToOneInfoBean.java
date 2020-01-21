package com.huatu.handheld_huatu.business.lessons.bean;

/**
 * Created by saiyuan on 2017/10/1.
 */

public class OneToOneInfoBean {
    public String ApplyJobs; //报考职位 必传 string
    public int Edu;// 教育经历必传 number $eduArr = array(1=>'研究生以上',2=>'大学本科',3=>'大学专科',4=>'高职以下');
    public int NetClassType;// 考试类型 number 1笔试2面试
    public String QQ;// QQ号 必传 string
    public int Sex;// 性别：1男，0女 必传 number
    public String Telephone;// 手机号必传 string
    public String UserBz;// 额外要求 必传 string
    public String UserReName;// 姓名，必传 string
    public String examType;

    public String Age;// 年龄 非必传 number
    public String ApplyNum ;//该职位招聘人数 非必传 number 笔试时显示
    public String ExamExperience;// 考试经历 string
    public String Examtime;// 考试时间 非必传 string 格式 Y-m-d H:i:s 面试时显示面试时间，笔试时显示笔试时间
    public String NetClassCategory;//课程类别 string
    public String NetClassName;// 课程名称 string
    public String ViewRatio;// 面试比例 非必传 string 面试时显示
    public String OrderNum;// 订单号 string
    public String score;// 分数和名次 string 面试时显示

    public String classTime;//可上课时段
    public String major;//专业
    public String subject;//报考科目
    public int stage;//报考学段
    public String area;//地区
    public int NetClassCategoryId;// 考试类型


}

