package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;

/**
 * Created by saiyuan on 2018/7/5.
 */

public class CourseTeacherListItemBean implements Serializable {
    public String Brief;
    public String SubjectType;//	专注科目	string	@mock=专注于申论
    public String allStudentNum;//	学生数量	number	@mock=0
    public String nickname;//	别名	number	@mock=1
    public String payClasses;	//在售课程	number	@mock=10
    public String roundPhoto;	//头像	string	@mock=http://upload.htexam.com/teacherphoto/15063005031910993.jpg
    public String teacherId;	//老师id	string	@mock=3
    public String teacherName;	//老师名称	string	@mock=钟君
    public String teacherRank;	//评分
    public float star;   //星星数量
}
