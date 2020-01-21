package com.huatu.handheld_huatu.business.play.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saiyuan on 2018/7/6.
 */

public class VideoPlayVideoInfoBean implements Serializable {
    public String classId;         //课程id
    public String activityEndTime;   //如果有活动，活动结束时间
    public String activityStartTime;  //如果有活动，活动开始时间
    public int activityType;         //如果有活动，活动类型 1直降 2打折 4满减 3组合优惠 5优惠券21团购
    public String actualPrice;        //实际价格
    public String classTitle;        //课程标题
    public String coverPhoto;        //视频封面
    public int isBuy;                //是否已购买 0:否  1:是
    public int isDiscount;           //是否有活动 0否   1是
    public int isRushClass;          //是否抢购课 0否   1是
    public int isRushOut;            //是否抢购完毕0否1是
    public int isSaleOut;            //是否售罄0否1是
    public String isSeckill;         //	是否秒杀课  0否  1是
    public int isTermined;           //是否待售  0否  1是
    public String rushEnd;           //抢购结束时间
    public String rushStart;         //抢购开始时间
    public int saleEnd;              //距离抢购结束时间戳
    public int saleStart;            //距离抢购开始/已开始时间戳
    public String title;             //课程介绍
    public String token;
    public String videoId;
    public String videoSize;
    public int isMianshou;             // 是否高端面授课程 0否 1是
    public int isProvincialFaceToFace; //是否为省考高端面授 0否 1是
    public int limitUserCount;         //限购数量
    public int activityLimitUserCount;  //如果有活动，活动限报人数
    public int limitType;           //团购）仅限老学员参加0不限1新学员2老学员
    public String autoCancelAt;       //（团购）剩余成团时间isCollage = 1 时展示，用来做倒计时 单位（秒）
    public String beginAt;             //团购）拼团活动开始时间
    public int collagePeople;          //成团人数
    public String collagePrice;         //拼团价格
    public String endAt;               //（团购）拼团活动结束时间
    public int isCollage;              //团购）是否参团 0：没有正在拼团 1：正在拼团 2: 拼团成功
    public int surplusNumber;          //团购）剩余成团人数
    public int activityClassId; //跳小程序classId
    public int courseType;           //1直播，2录播
    public int collageStatus;	//团状态1正常；2，3下线
    public int allowCollage; //（团购）直接参团0不允许1允许
    public int isCollect; //是否收藏
    public int cateId;//分类Id
    public int categoryId;//小分类id
//    public FilterOptions filterList;
//    public class FilterOptions {
//        public String title;
//        public List<OptionsInfo> list;
//    }

}
