package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2018/7/4.
 */

public class CourseDetailBean implements Serializable{
    public String actualPrice;   //实际价格
    public int buyNum;  //销量
    public String courseIntroduction;
    public int isMianshou;//是否是高端面授0否1是
    public int isProvincialFaceToFace; //省考高端面授课0否1是
//    public String isSuit;    //是否套餐0否1是
//    public int limitUserCount;  //限购数量
    public String price;     //原价
    public int province;  //城市id
//    public String purchasType;  //限报形式0默认1限招2仅剩
    public int rid;         //课程id
    public String scaleimg;  //课程图片
//    public String teacherInfo; //教师姓名
    public String terminedDesc; //待售描述
//    public String timeTotal;   //总课时
    //    public String title;     //课程标题
    public String typeName;  //班次类型

    /**************************************/
//    public String studyDate; //直播日期

    /**************************************/
//    public int classExercisesNum;//配套练习数量
//    public String correctTotal;//申论批改总次数
//    public String entity;//	配套书籍（学习资料）
//    public int isFinite;//申论批改0无1有
//    public String mockExamNum;//模考次数
//    public String valueAddedService; ////增值服务
    public List<String> columnHeaders; //展示栏目标题
    public List<String> columnDetails; //展示栏目内容
//    public int miniActivityId; //小程序Id

    public int activityType; //活动类型1直降 2打折 4满减 3组合优惠 5优惠券21团购  number  @mock=0
//    public String beginAt;    //活动开始时间  string  @mock=''
//    public String endAt;  //活动结束时间  string  @mock=''
    public int collagePeople;   //X人团 number  @mock=0
    public int isCollect;  //是否收藏0否1是
    public int isDiscount;//是否有优惠活动0否1是
//    public String effectDateDesc;
//    public String payLimit;
//    public int buy;
    public int cateNu;
//    public String cateName;
    public String token;
    public String videoSize;
    public String videoId;
//    public String introductionTitle;
    public int classId;
    public String activityStartTime;
    public String activityEndTime;
    public double collagePrice;
//    public int discountPrice;
//    public int allowCollage;
    public String autoCancelAt;
    public int isCollage;
    public int surplusNumber;
    public int collageStatus;
    public int activityClassId; //拼团课程id
    public int limitType; //	拼团0:不限制,1:只允许新人参加,2:只允许老学员参加
//    public String isSeckill;
    public String classTitle;
    public int isTermined;
    public int isBuy;
    public int isRushClass;
    public int saleStart;
    public int saleEnd;
//    public String rushStart;
//    public String rushEnd;
    public int isSaleOut;
    public int isRushOut;
//    public int activityLimitUserCount;
    public int categoryId;
    public String aloneByPrice;
    public FilterOptions filterList;
    public List<ActDetailInfo> activityList;
    public int isLive;//是否是直播 1是

    public boolean iso2o;//是否是双师课
    public FilterOptions o2oFilterList;//线下课上课地点选择器

    public int timetableId; // 课表id;

    public class FilterOptions implements Serializable {
        public String title;
        public List<OptionsInfo> list;
    }
}
