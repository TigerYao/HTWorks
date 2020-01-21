package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by cjx on 2018\12\6 0006.
 */

public class InvoiceDetailBean {


     public String  invoiceContent;//		string	@mock=咨询费
     public String  invoiceMoney;//		   string	@mock=1.22发票金额
     public String  invoiceTitle;//		   string	@mock=个人 发票抬头
     public int     invoiceType;//		   number	@mock=1    //发票类型0电子发票1纸质发票
     public String  orderDate;//		   string	@mock=2019-05-15 17:57:13
     public String  orderNum;//		       string	@mock=HTKC_2019051517575313811462
     public int     status;//		       number	@mock=1     状态0已提交1已开票2发票异常，联系管理员处理
     public String  taxNum	;//           string	@mock=       //纳税人识别号
     public int     titleType;//             抬头类型0个人1单位

     public String getStatusDes(){
          if(status==0) return "已提交";
          if(status==1) return "已开票";
          if(status==2) return "发票异常";
          return "已提交";
     }

}
