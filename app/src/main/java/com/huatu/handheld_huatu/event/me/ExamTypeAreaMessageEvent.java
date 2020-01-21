package com.huatu.handheld_huatu.event.me;

/**
 *
 */
public class ExamTypeAreaMessageEvent {

    private static final int ETA_MSG_TYPE_BASE = 10000 * 3;

    public static final int ETA_MSG_SET_TARGET_AREA_SUCCESS = ETA_MSG_TYPE_BASE + 1;

    public static final int ETA_MSG_GET_TARGET_AREA_LIST_SUCCESS = ETA_MSG_SET_TARGET_AREA_SUCCESS + 1;

    public static final int ETA_MSG_GET_TARGET_SIGN_UP_LIST_SUCCESS = ETA_MSG_GET_TARGET_AREA_LIST_SUCCESS + 1;

    public static final int ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS = ETA_MSG_GET_TARGET_SIGN_UP_LIST_SUCCESS + 1;                              // 切换地区成功

    public static final int ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS_from_SettingExamTypeFromFirstFragment = ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS + 1;

    public static final int ETA_MSG_START_TO_MAINTAB_ACTIVITY_SUCCESS = ETA_MSG_SET_AREA_TYPE_CONFIG_SUCCESS_from_SettingExamTypeFromFirstFragment + 1;

    public static final int ETA_MSG_HOME_FRAGMENT_TITLE_VIEW_SEARCH_SHOW = ETA_MSG_START_TO_MAINTAB_ACTIVITY_SUCCESS + 1;               // HomeFragment TitleView 显示   搜索按钮
    public static final int ETA_MSG_HOME_FRAGMENT_TITLE_VIEW_SEARCH_NOT_SHOW = ETA_MSG_HOME_FRAGMENT_TITLE_VIEW_SEARCH_SHOW + 1;        // HomeFragment TitleView 不显示 搜索按钮


    private static final int ETA_MSG_TYPE_BASE_REQUEST_TYPE = 10000 * 3 + 1000;
    public static final int ETA_MSG_ExamTargetAreaActivity_EFORM_CompleteUserInfoActivity = ETA_MSG_TYPE_BASE_REQUEST_TYPE + 1;
    public static final int ETA_MSG_SettingExamTypeFragment_EFORM_ExamTargetAreaActivity = ETA_MSG_ExamTargetAreaActivity_EFORM_CompleteUserInfoActivity + 1;
    public static final int ETA_MSG_SettingExamTargetAreaFragment_EFORM_SettingExamTypeFragment = ETA_MSG_SettingExamTypeFragment_EFORM_ExamTargetAreaActivity + 1;

    public ExamTypeAreaMessageEvent() {

    }
}
