package com.huatu.handheld_huatu.mvpmodel.zhibo;

import java.io.Serializable;

/**
 * Created by cjx on 2018\7\30 0030.
 */

public class CourseInfoBean implements Serializable {

    public String androidFunction;	//安卓加QQ群	string	@mock=
    public String iosFunction	;   //IOS加QQ群	string	@mock=
    public String schedule      ;	//学习进度百分比	string	@mock=64%
    public String QQnum;
    public String title;
    public String orderNum;//一对一课程
    public long   courseId;
    public String scaleimg;
    public int    coursewareHours;
    public int   isFree ;

    public int    service;//渠道服务0无信息 1qq  2微信
    public String qrCode;//QQ二维码
    public String wechatNumber;
    public String wechatQrCode;//
    public boolean  iso2o;
    public String goodsId;

    public CourseInfoBean(String name,long courseid,String coverImg){
        this.title=name;
        this.courseId=courseid;
        this.scaleimg=coverImg;
    }

}
