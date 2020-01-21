package com.huatu.handheld_huatu.business.me.bean;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.Exposition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chq on 2017/9/15.
 */
public class OrderDetailData {
    public String addTime;//下单时间
    public String address;//详细地址
    public String calcDisCount;//	7.1.11-立减金额	string
    public String levelDiscount;//	7.1.11-等级优惠	string
    public String  logisticsCost;//	7.1.11 - 运费, 只有hasLogistics为true时显示	string
    public String addressId;//拼团地址id
    public boolean hasLogistics;//是否有物流信息
    public String logisticsTime;//发货时间
    public String moneyDisCount;//优惠金额
    public String name;//收货人姓名
    public String orderId;//订单ID
    public String collageOrderId;//跳小程序id
    public String orderNum;//订单号
    public int payStatus;//支付状态
    public String payTime;	//支付时间;
    public String paymentDesc;	//支付状态描述
    public String paymentType;//支付方式	string
    public String price;//	订单价格
    public String  classTotalPrice;//课程总价
    public String status;//	订单状态
    public String statusDesc;//	订单状态描述,如 已付款
    public String  userName;//	用户名
    public String protocolUrl;//协议url
    public int goldPay;//是否支持学习币支
    public String statusAt;//拼团失败退款时间
    public long countDown;//订单关闭倒计时，单位秒

    public ArrayList<Course> classInfo;
    public Group group;
    public String mobile;//手机号

    public class Group{
        public long activityId;//活动id
        public long groupId;//团id
        public int number;//成团人数
        public int showNum;//已售人数
        public int status;//团状态 1：未开始 2：发起 3：成功 4：失败
        public long surplusSecond;//距结束倒计时 单位秒
        public int surplusNumber;	//剩余拼团人数

//        activityId	活动id	number
//        groupId	团id	number	@mock=21
//        number	成团人数	number	@mock=2
//        showNum	已售人数	number	@mock=2
//        status	团状态	number	1：未开始 2：发起 3：成功 4：失败
//        surplusSecond	距结束倒计时 单位秒	number	@mock=0
//        surplusNumber	剩余拼团人数	number	@mock=0
    }
   /**
    * 拼团订单数据
    * addTime	订单发起时间	string	@mock=2018-08-14 20:34:48
    address	地址	string	@mock=北京市市辖区西城区大厦
    addressId	地址id	number	@mock=123
    classInfo		array<object>
            group		object

    mobile	收货人手机号	string	@mock=18399998888
    name	收货人姓名	string	@mock=htwxtianzy
    orderId	订单id	number	@mock=29
    orderNum	订单号	string	@mock=MPP_2018081420344864657873
    payTime	支付时间	string	@mock=2018-08-14 20:34:48
    paymentDesc	支付描述	number	微信支付
    paymentType	支付方式	number	32 ：微信支付
    price	订单金额	string	@mock=0.00
    status	订单状态	number	支付状态, 1-未支付, 2-已支付, 3-已退款, 4-已取消, 5-支付失败
    statusAt	退款时间	string
    moneyDisCount	优惠金额	string
    */


//    public float Discount;//折扣优惠
//    public float MoneyFree;//代金券优惠金额
//    public float otherDiscount;//手机下单满减优惠金额
//    public float MoneySum;//未支付时的实际应收订单金额
//    public float MoneyReceipt;//已支付的实际应收订单金额
//    public float MoneyTotal;//未扣减时的订单总金额
//    public String PayDate;//支付时间
//    public String Point;//获赠积分
//    public int Status;//订单状态0，未支付，1已支付
//    public int isLogistics;//是否含有邮寄1是，0否
//    public ArrayList<Logistic> logisticsDetail;
//
//    addTime	下单时间	string	@mock=2018-05-22 18:32:00
//    address	地址	string	@mock=浙江省金华市浦江县和平北路2号联通营业厅
//    classInfo	课程信息	array<object>
//    hasLogistics	是否有物流	array<boolean>	@mock=false
//    logisticsTime	发货时间	string
//    moneyDisCount	优惠金额	string
//    name	收件人	string	@mock=盛伟明
//    orderId	订单ID	string	@mock=11428517
//    orderNum	订单编号	string	@mock=YZ_E20180522183219113422442
//    payStatus	支付状态	number	@mock=1
//    payTime	支付时间	string	@mock=2018-05-22 18:32:34.000
//    paymentDesc	支付状态描述	string	@mock=微店支付
//    paymentType	支付方式	string	@mock=34
//    price	订单价格	string	@mock=98.00
//    status	订单状态	string	@mock=2
//    statusDesc	订单状态描述	string	@mock=已付款
//            userName

    public class Course {
        public String price;//课程原价
        public String finalPrice;//普通订单实际课程价格
        public String actualPrice;//拼团订单实际课程价格
        public String brief;	//拼团订单课程介绍
        public String endDate;	//拼团订单结束时间
        public String startDate;	//拼团订单开始时间
        public String title;//课程标题
        public String img;//课程图片
        public String scaleImg;//拼团订单课程图片
        public String teacherStr;//普通订单教师
        public String teacherDesc;	//拼团订单老师描述
        public String editEndTime;//无限申论批改,结束时间, 存在则表示无限批改,否则为限次批改
        public String TeacherIds;//@mock=,448,487,531,890,1025,1192,1509,1516,1524,1558,1673,
//        public int argumentEdit;//文章写作批改
        public String entity;//图书赠送
        public String lessonCount;//普通订单课时
        public String timeLength;//拼团订单课时
//        public int multiEdit;//套题批改次数
        public String videoTypes;//普通订单课程类型
        public String videoType;//拼团订单课程类型
//        public int normEdit;//标准答案批改次数
        public int orderId;//订单ID
        public int rid;//课程ID
        public ArrayList<Teachers> teachers;//普通老师
        public ArrayList<Teachers> teacherInfo;//拼团老师
        public List<Exposition> exposition;
/**
 * 拼团订单数据
//        actualPrice	课程实际价格	string	@mock=1111.11
//        brief	课程介绍	string	@mock=秒杀测试
//        endDate	结束时间	string	@mock=1970-01-01
//        price	课程原价	string	@mock=100.00
//        rid	课程id	number	@mock=66028
//        startDate	开始时间	string	@mock=2018-06-20
//        teacherDesc	老师描述	string	@mock=顾斐
//        teacherInfo		array<object>
//
//        timeLength	课时	string	@mock=10
//        title	标题	string	@mock=7.14秒杀测试2
//        videoType	课程类型	string	@mock=1
//         scaleImg	课程图片	string
 */


//        TeacherIds		string	@mock=,448,487,531,890,1025,1192,1509,1516,1524,1558,1673,
//        argumentEdit	文章写作批改	number	@mock=24
//        editEndTime	无限申论批改,结束时间, 存在则表示无限批改,否则为限次批改	string	@mock=1900-01-01
//        entity	图书赠送	string	@mock=11本讲义, 7本模考宝典
//        finalPrice	实际价格	string	@mock=98
//        img	课程封面	string
//        lessonCount	课时	string	@mock=0
//        multiEdit	套题批改次数	string	@mock=42
//        normEdit	标准答案批改次数	number	@mock=12
//        orderId	订单ID	string	@mock=11428517
//        price	价格	string	@mock=980
//        rid	课程ID	number	@mock=65164
//        teachers		array<object>
    }

    public class Teachers {
        public String roundPhoto;//头像
        public String teacherId;//教师ID
        public String teacherName;//教师名字

//        roundPhoto	老师头像	string	@mock=http://upload.htexam.com/teacherphoto/1506300446553913.jpg
//        teacherId	老师id	string	@mock=1
//        teacherName	老师名称	string	@mock=顾斐
         }

//           roundPhoto	头像	string	@mock=http://upload.htexam.com/teacherphoto/1509508655.jpg
//          eacherId	教师ID	string	@mock=448
   //     teacherName	教师名字	string	@mock=李梦娇

//    public class Logistic {
//        public String context;
//        public String number;
//        public String time;
//
////        logisticsDetail|5		array<object>
////        context		string	@mock=$order('客户 签收人: 邮件收发章 已签收 感谢使用圆通速递，期待再次为您服务','客户 签收人: 邮件收发章 已签收 感谢使用圆通速递，期待再次为您服务','客户 签收人: 门卫 已签收 感谢使用圆通速递，期待再次为您服务','客户 签收人: 门卫 已签收 感谢使用圆通速递，期待再次为您服务','客户 签收人: 本人签收 已签收 感谢使用圆通速递，期待再次为您服务')
////        number		string	@mock=$order('809097193365','809097192066','809097199730','809097192573','809097190507')
////        time        string	@mock=$order('2017-01-16 12:21:06','2017-01-16 12:21:14','2017-01-17 12:56:49','2017-01-17 12:57:24','2017-01-15 16:40:03')
//
//    }

}
