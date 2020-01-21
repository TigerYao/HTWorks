package com.huatu.handheld_huatu.business.me.bean;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ht on 2017/9/15.
 */
public class MyOrderData {
    public int next;
    public List<OrderList> result;


//    next		number	@mock=1
//    result|10		array<object>
//    AddTime		string	@mock=$order('2017-09-15 11:40:00','2017-09-11 09:53:00','2017-09-07 09:35:00','2017-09-05 20:02:00','2017-09-05 19:49:00','2017-09-04 16:59:00','2017-09-04 16:58:00','2017-09-04 16:07:00','2017-09-04 16:05:00','2017-09-04 16:03:00')
//    MoneyReceipt		number	@mock=$order(13849,272,2108,0,0,0.85,0.85,0,0,0)
//    MoneySum		number	@mock=$order(13849,272,2108,2660,2660,1,1,0,0,0)
//    OrderID		string	@mock=$order('6087124','6087110','6087094','6087092','6087091','6087088','6087087','6087086','6087085','6087084')
//    OrderNum		string	@mock=$order('HTKC_2017091511401908014232','HTKC_2017091109530315711595','HTKC_2017090709345331908322','HTKC_Oi_2017090520020158832223','HTKC_Oi_2017090519490222735301','HTKC_2017090416591658385432','HTKC_2017090416575967573144','HTKC_2017090416065526955110','HTKC_2017090416043547812546','HTKC_2017090416032712332771')
//    Status		number	@mock=$order(2,2,2,2,2,2,2,2,2,2)
//    Title		array<string>	@mock=$order('2017年江苏省公务员考试红领全程套餐A','2017年多省联考“成公智胜”全程系统套餐','2017年公务员考试“成公智胜”8H1对1套餐','面试考前押题冲刺课（第四期）','结构化面试9小时·专岗专训1对1家教班')

    public class OrderList {
        public float MoneyReceipt;//实际收到金额
        public float MoneySum;//订单金额
        public String refundMoney;//退款金额
        public String AddTime;//下单时间
        public String OrderID;//订单ID
        public String OrderNum;//订单编号
        public int Status;//订单状态1未支付2已支付
        public List<String> Title;
        public int isPass ;//0不显示按钮，1显示填写退款信息的状态， 2:退款完成
    }
}
