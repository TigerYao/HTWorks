package com.huatu.handheld_huatu.business.ztk_zhibo.pay;

/**
 * Created by saiyuan on 2017/9/22.
 */

public class PayResultEvent {
    public static final int PAY_RESULT_EVENT_RESULT_BACK = 1;
    public static final int PAY_RESULT_EVENT_SHOW_CUSTOMER = 2;

    public int type;// 1:显示对话框      2:显示客服对话框
    public int params;
}
