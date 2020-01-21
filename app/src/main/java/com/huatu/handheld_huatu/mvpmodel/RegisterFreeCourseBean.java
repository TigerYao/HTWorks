package com.huatu.handheld_huatu.mvpmodel;

import java.util.List;

/**
 * Created by cjx on 2018\12\6 0006.
 */

public class RegisterFreeCourseBean {

     public int  rcoin;        //金币
     public int  rgrowUpValue; //成长值
     public String rtitle;         //注册成功送课页的标题
     public List<CouponCourseBean> rcourseList;
     //public int ucId;



     public static class CouponCourseBean{
          public int    lesson;//课时

          public String name; //课程名称
          public String time;

     }


}
