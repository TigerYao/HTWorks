package com.huatu.handheld_huatu.business.faceteach.bean;

/**
 * Created by Administrator on 2019\6\4 0004.
 */

public class FaceOrderBean {


    public String          oid;//: S-P1510559806RN784,   订单编号
    public String          priceCount;//: 6080.00,       订单价格
    public String          stime;//: 2017-11-13 16:45:37,
    public int             state;//: 4,                  订单状态代码   0-未支付，1-已支付，2-全部  ,4-已完成
    public String          aid;      //: 1175921,         课程ID
    public String          feiyong;//: 6080.00,          	商品价格
    public String          title;//: 模块精讲强化班,
    public String          alipay_account;//: fjxmcw@htexam.com,
    public String          pid;//: 2088801077583036,
    public String          wx_appid;//: ,                  预留
    public String          wx_partnerid;//: ,               预留
    public String          state_txt;//: 已完成,           	订单状态文字描述
    public String          goodsid;//: 58362,
    public int             hasElectronicProtocol;//: 1,     是否有电子签章 0-没有 1-有
    public int              IsElectronicProtocol;//: 0,     是否已经签 0-未签 1-已签
    public int              split;//: 0,                    是否拆分订单 0-未拆分，1-已拆分
    public String          signurl;//: http://test.bm.huatu.com/member/getPreviewProtocolUrlBeforeBuy.php?goodsId=58362    电子签章URL地址


}
