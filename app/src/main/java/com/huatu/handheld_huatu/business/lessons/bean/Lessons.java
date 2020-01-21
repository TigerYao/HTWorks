package com.huatu.handheld_huatu.business.lessons.bean;

import java.io.Serializable;

/**
 * @author zhaodongdong.
 */

public class Lessons extends BaseCourseListItemBean {

    public String ClassNo;
    public String StartDate;
    public String SubjectName;
    public String activeEnd;
    public String activeStart;
    public String endDate;
    public String hitsNum;
    public int isNormal;
    public int isverify;
    public String tuijian;
    public String zhibo;
    public int IsSuit;
    public String NetClassCategoryId;
    public String limitBuyNum;

    public Lessons(BaseCourseListItemBean itemBean) {
        this.isRushClass = itemBean.isRushClass;
        this.collectShowNum = itemBean.collectShowNum;
        this.collectLimitUserCount = itemBean.collectLimitUserCount;
        this.isTermined = itemBean.isTermined;
        this.purchasType = itemBean.purchasType;
        this.isSaleOut = itemBean.isSaleOut;
        this.terminedDesc = itemBean.terminedDesc;
        this.saleEnd = itemBean.saleEnd;
        this.saleStart = itemBean.saleStart;
        this.lSaleEnd = itemBean.lSaleEnd;
        this.lSaleStart = itemBean.lSaleStart;
        this.scaleimg = itemBean.scaleimg;
        this.isSeckill = itemBean.isSeckill;
        this.title = itemBean.title;
        this.TeacherDesc = itemBean.TeacherDesc;
        this.iszhibo = itemBean.iszhibo;
        this.limitUserCount = itemBean.limitUserCount;
        this.buyNum = itemBean.buyNum;
        this.isRushOut = itemBean.isRushOut;
        this.isBuy = itemBean.isBuy;
        this.ShortTitle = itemBean.ShortTitle;
        this.isCollect = itemBean.isCollect;
        this.ActualPrice = itemBean.ActualPrice;
        this.Price = itemBean.Price;
        this.CourseLength = itemBean.CourseLength;
        this.rid = itemBean.rid;
        this.NetClassId = itemBean.NetClassId;
        this.collectId = itemBean.collectId;
    }

//    "hitsNum": "32",
//    result	array	结果集数组
//    rid	string	课程id
//    title	string	课程名称
//    NetClassCategoryId	string	课程考试类型id
//    TeacherDesc	string	授课老师
//    ActualPrice	string	价格
//    scaleimg	string	图片
//    StartDate	string	课程上线日期
//    EndDate	string	课程下线日期
//    SubjectName	string	科目名称
//    limitUserCount	string	课程限购数量
//    saleStart	string	课程开抢时间倒计时
//    saleEnd	string	课程结束时间倒计时
//    ClassNo	string	课程编号
//    CourseLength	string	课时
//    iszhibo	int	是否今天有直播
//    buyNum	string	课程点击量
//    isBuy	int	是否购买1是0否
//    isRushClass	int	是否是抢购课程1是0
//    activeStart	string	抢购开始时间
//    activeEnd	string	抢购结束时间
//    count	int	已购数量
//    isSaleOut	int	是否售罄1是0否
//    isRushOut	int	是否抢光1是0否
//    zhibo	string	直播标题
//    isCollect	int	是否是合集 1是 0不是
 //   "limitBuyNum": "471/557",

}
