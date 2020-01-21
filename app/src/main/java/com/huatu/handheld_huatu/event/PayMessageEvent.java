package com.huatu.handheld_huatu.event;

import android.os.Bundle;

/**
 * Created by saiyuan on 2016/10/18.
 */
public class PayMessageEvent {
    public String tag;
    public int type;
    public Bundle extraBundle;

    private static final int PAY_MESSAGE_TYPE_BASE_ALI=1000;
    private static final int PAY_MESSAGE_TYPE_BASE_WEIXIN=2000;

    public static final int PAY_MESSAGE_TYPE_ALI_SUCCESS= PAY_MESSAGE_TYPE_BASE_ALI+1;
    public static final int PAY_MESSAGE_TYPE_ALI_FAIL= PAY_MESSAGE_TYPE_ALI_SUCCESS+1;
    public static final int PAY_MESSAGE_TYPE_ALI_8000= PAY_MESSAGE_TYPE_ALI_FAIL+1;


    public static final int PAY_MESSAGE_TYPE_WEIXIN_ECODE_0=PAY_MESSAGE_TYPE_BASE_WEIXIN+1;
    public static final int PAY_MESSAGE_TYPE_WEIXIN_ECODE_1=PAY_MESSAGE_TYPE_WEIXIN_ECODE_0+1;
    public static final int PAY_MESSAGE_TYPE_WEIXIN_ECODE_2=PAY_MESSAGE_TYPE_WEIXIN_ECODE_1+1;
    public static final int PAY_MESSAGE_TYPE_WEIXIN_ECODE_OTHER=PAY_MESSAGE_TYPE_WEIXIN_ECODE_2+1;


    public PayMessageEvent(int type) {
        this.type = type;
    }
}
