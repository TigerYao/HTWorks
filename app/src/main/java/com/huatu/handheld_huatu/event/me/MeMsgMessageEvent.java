package com.huatu.handheld_huatu.event.me;

/**
 */
public class MeMsgMessageEvent {

    public static final int MMM_MSG_TYPE_BASE = 10000*5;

    public static final int MMM_MSG_ME_MESSAGE_HAS = MMM_MSG_TYPE_BASE+1;
    public static final int MMM_MSG_ME_MESSAGE_NO = MMM_MSG_ME_MESSAGE_HAS+1;
    public static final int MMM_MSG_ME_MESSAGE_CLOSE = MMM_MSG_ME_MESSAGE_NO+1;
    public static final int MMM_MSG_ME_MESSAGE_SEL_LIVE_TAB = MMM_MSG_ME_MESSAGE_CLOSE+1;
    public static final int MMM_MSG_ME_MESSAGE_SEL_RECORDING_TAB = MMM_MSG_ME_MESSAGE_SEL_LIVE_TAB+1;
    public static final int MMM_MSG_ME_MESSAGE_SHOW_TIP = MMM_MSG_ME_MESSAGE_SEL_LIVE_TAB+1;
    public static final int MMM_MSG_ME_MESSAGE_INTENT_HOMEFRAGMENT = MMM_MSG_ME_MESSAGE_SHOW_TIP + 1; //回到home页面
    public static final int MMM_MSG_ME_MESSAGE_SEL_ME= MMM_MSG_ME_MESSAGE_INTENT_HOMEFRAGMENT + 1; //跳到我的页面

    public static final int MMM_MSG_TYPE_BASE_REQUEST_TYPE= 10000*5+1000;
    public static final int MMM_MSG_SettingErrorPaperDoCountFragment_EFORM_ErrorPapersListFragment = MMM_MSG_TYPE_BASE_REQUEST_TYPE+1;

    public MeMsgMessageEvent() {

    }
}
