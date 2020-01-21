package com.huatu.handheld_huatu.event;

/**
 * Created by Administrator on 2019\5\24 0024.
 */

public class InvoiceStatusEvent {
    public  String orderID;
    public  String orderNum;

    public InvoiceStatusEvent(String ordeid,String ordernum){
        orderID=ordeid;
        orderNum=ordernum;
    }
}
