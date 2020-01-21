package com.huatu.handheld_huatu.business.me.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chq on 2018/7/30.
 */

public class MyV5OrderContent {
    public String addTime;//下单时间
    public ArrayList<ClassInfo> classInfo;
    public List<String> collageAvatars;//参团人员头像
    public boolean hasLogistics;//是否有物流
    public boolean hasProtocol;//是否有协议
    public String moneyDisCount;//优惠金额
    public String orderId;//订单id
    public String orderNum;//订单编号
    public int payStatus;//	支付状态,0-未支付, 1-已支付, 2-已取消, 3-待确认	number	@mock=1
    public int isCollage;//	0-不是拼团订单, 1-拼团中订单, 2-拼团成功订单 3-拼团失败退款的订单
    public int invoiceStatus;//	查看是否可以开发票 (0 不可开 1 可开票  2已开票)

    public String invoiceMoney;
    public int isJump;//	是否可以跳转（0 否  1是）
    public int needNumber;//	还差几人成团
    public String paymentDesc;//支付方式描述
    public String paymentType;//支付方式
    public String price;//订单价格
    public String status;//订单价格
    public String statusDesc;//订单状态描述
    public String userName;//	用户名

    //    addTime	下单时间	string	@mock=2018-06-19 09:00:00
//    classInfo	课程信息(一个订单可能有多个课程)	array<object>
//
//    hasLogistics	是否有物流	array<boolean>	@mock=false
//    moneyDisCount	优惠金额	string	@mock=1.00
//    orderId	订单ID	string	@mock=11585903
//    orderNum	订单编号	string	@mock=YZ_20180619TestABCD
//    payStatus	支付状态,0-未支付, 1-已支付, 2-已取消, 3-待确认	number	@mock=1
//    paymentDesc	支付方式描述	string	@mock=微店支付
//    paymentType	支付方式	string	@mock=34
//    price	订单价格	string	@mock=10.00
//    status	订单状态	string	@mock=2
//    statusDesc	订单状态描述	string	@mock=已付款
//    userName	用户名      string	@mock=htwx_7723947


    public class ClassInfo {
        public String finalPrice;//	实际价格
        public String lessonCount;//课时数量
        public String orderId;//订单ID
        public String price;//订单ID
        public long rid;// 课程ID
        public String teachers;//	教师字符串	string	@mock=顾斐
        public String title;//	课程标题	string	@mock=直播无限批改赠送110
        public String videoTypes;//


        //        finalPrice	实际价格	string	@mock=1
//    lessonCount	课时数量	string	@mock=0
//    orderId	订单ID	string	@mock=11585903
//        price	价格	string	@mock=10
//        rid	课程ID	number	@mock=65902
//        teachers	教师字符串	string	@mock=顾斐
//        title	课程标题	string	@mock=直播无限批改赠送110
//        videoTypes		string	@mock=,3,4,7,1,
    }

}
