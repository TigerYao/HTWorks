package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;

/**
 * Created by saiyuan on 2018/7/9.
 */

public class CourseTeacherJudgeItemBean implements Serializable {
    public String courseRemark;//	评价内容	string	@mock=很好
    public int courseScore;//	评分	number	@mock=5
    public String lessonId;//	课件id	string	@mock=903660
    public String lessonTitle;//	课件标题	string	@mock=基础精讲-言语1
    public String periods;//	期数  直播有，录播没有
    public String rateDate;//	评价日期	string	@mock=2018.05.27 21:31
    public float star;//	分数对应的星星个数	number	@mock=10
    public String teacherId;//	教师id	string	@mock=114
    public String teacherName;//	教师姓名	string	@mock=郜爽
    public String userFace;//	教师头像	string	@mock=http://tiku.huatu.com/cdn/images/vhuatu/avatars/default.png
    public String userId;//	用户id	string	@mock=7889492
    public String userName;//	用户名	string	@mock=1330****272
}
