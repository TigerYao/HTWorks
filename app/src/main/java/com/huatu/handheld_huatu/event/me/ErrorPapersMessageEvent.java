package com.huatu.handheld_huatu.event.me;

/**
 */
public class ErrorPapersMessageEvent {

    public static final int AEP_MSG_TYPE_BASE = 10000*4;

    public static final int AEP_MSG_SETTING_ERROR_PAPER_DO_COUNT_SUCCESS = AEP_MSG_TYPE_BASE+1;


    public static final int AEP_MSG_TYPE_BASE_REQUEST_TYPE= 10000*4+1000;
    public static final int AEP_MSG_SettingErrorPaperDoCountFragment_EFORM_ErrorPapersListFragment = AEP_MSG_TYPE_BASE_REQUEST_TYPE+1;

    public ErrorPapersMessageEvent() {

    }
}
