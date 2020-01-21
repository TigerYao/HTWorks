package com.huatu.handheld_huatu.business.play.bean;

import java.util.List;

/**
 * Created by saiyuan on 2018/7/9.
 */

public class CourseTeacherCourseItemBean {
    public String ActualPrice;//	课程实售价格	number	@mock=0
    public String Price;//	课程原价	string	@mock=0
    public String TeacherDesc;//	授课老师	string	@mock=谌礼强、刘有珍、车春艺
    public String TimeLength;//	课件数量	string	@mock=4
    public String Title;//	标题	string	@mock=2019公安院校联考备考直通车-领航版
    public String classTitle;//	课程标签	number	@mock=1
    public String introduction;//	课程介绍	string	@mock=05月06日-05月08日
    public String rid;//	课程id	number	@mock=65560
    public String scaleimg;//	课程图片	string	@mock=http://upload.htexam.net/classimg/class/1525432244.jpg
    public int showNum;//	已售数量	string	@mock=830
    public int isTrial; //试听 0（不支持试听），1（支持试听）
    public List<TeacherPhotoNameBean> teacherImg;
    public String videoType; //课程类型
    public String tuijian;//推荐等级
    public int collageActiveId; //拼团活动id  大于0显示拼团标签
//    public int collageIsBuy;  //拼团是否已购	0否 1是
    public int redEnvelopeId; //红包活动id	0：不展示 大于0展示红包
}
