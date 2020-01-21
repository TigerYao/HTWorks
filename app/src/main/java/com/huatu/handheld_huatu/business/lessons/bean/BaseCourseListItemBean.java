package com.huatu.handheld_huatu.business.lessons.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by saiyuan on 2018/2/28.
 * //alternate的备选字段 会后面的替换前面的
 * https://blog.csdn.net/axuanqq/article/details/51441590
 */

public class BaseCourseListItemBean implements Serializable {
    public int isRushClass; //是否为抢购课
    public int collectShowNum ;//合集销量
    public int  collectLimitUserCount ;//合集限购数量
    public int isTermined; //是否待售0否1是
    public int purchasType; //限报形式 0默认1限招2仅剩
    public int isSaleOut;  //是否售罄
    public String terminedDesc;
    public String saleEnd;   //	抢购结束时间
    public String saleStart; //抢购开始时间
    public long lSaleEnd;
    public long lSaleStart;
    public String scaleimg;         // 缩略图
    public int  isSeckill;      //秒杀 1是 0不是
    public String title;
    public String TeacherDesc;      // 教师名称
    public int iszhibo;
    public int limitUserCount;   //限购数量
    public String buyNum; //已购数量
    public int isRushOut;  //是否停售
    public int isBuy;//是否已买
    public String ShortTitle;//合集title
    public int isCollect; //合集字段 1是 0 不是
    public String collectId;
    public int   collageActiveId;  //大于0 显示拼团
   // public int   collageIsBuy;      // 拼团是否已购
    public int redEnvelopeId  ;//红包活动id   大于0展示

    @SerializedName(value ="ActualPrice",alternate = {"actualPrice"})
    public String ActualPrice;

    @SerializedName(value ="Price",alternate = {"price"})
    public String Price;

    @SerializedName(value ="CourseLength",alternate = {"timeLength"})
    public String CourseLength;

    @SerializedName(value ="rid",alternate = {"netClassId"})
    public String rid;
    public String NetClassId;

/*  VodCourseBean.convertToLessons

    itemBean.ActualPrice = resultBean.actualPrice;
    itemBean.Price = resultBean.price;
    itemBean.CourseLength = resultBean.timeLength + "课时";
    itemBean.rid = resultBean.netClassId;
    itemBean.NetClassId = resultBean.netClassId;*/
}
