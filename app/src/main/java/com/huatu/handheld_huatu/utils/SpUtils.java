package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;

/**
 * Created by ljzyuhenda on 16/7/22.
 */
public class SpUtils {
    public static final String FILENAME = "config";                                 // sp file 名称
    public static final String TOKEN = "token";                                     // token key
    public static final String MOBILE = "mobile";                                   // 手机号
    public static final String EMAIL = "email";                                     // 邮箱
    public static final String LOGINSTATE = "loginstate";                           // 登录状态
    public static final String NICK = "nick";                                       // 昵称 key
    public static final String UNAME = "uname";                                     // 网校唯一标示,与其交互时使用
    public static final String AREA = "exam_area";                                  // 考试区域对应id;
    public static final String AREANAME = "areaname";                               // 考试区域名字;
    public static final String UID = "User_Id";                                     // 用户Id
    public static final String DAY_NIGHT_MODE = "day_night_mode";                   // 日夜模式：0日间|1夜间
    public static final String FONT_SIZE_MODE = "font_size_mode";                   // 做题页面字号：0中号默认(19sp)|1小号(16sp)|2大号(22sp)
    public static final String APP_LAUNCH_NUMBER = "app_lunch_number_new";          // app的开启次数
    public static final String APP_COMMENTED = "app_commented";                     // 是否应用市场评价过
    public static final String AVATAR = "avatar";
    public static final String COMMENT_STATUS = "comment.status";
    public static final String COMMENT_PAIXU = "comment.paixu";
    public static final String COMMENT_KAOSHI = "comment.kaoshi";
    public static final String COMMENT_DATE = "comment.date";
    public static final String COMMENT_PRICE = "comment.price";
    public static final String COMMENT_MINE_PAIXU = "comment.paixu2";
    public static final String COMMENT_MINE_PAIXUORIDER = "comment.paixuorder";
    public static final String COMMENT_LIVE_PRICE = "live.price";
    public static final String COMMENT_LIVE_DATE = "live.date";
    public static final String COMMENT_LIVE_CATE = "live.cate";
    public static final String COMMENT_LIVE_PAIXU = "live.paixu";
    public static final String SHARE_DIALOG_SHOW_TIME = "share.dialog.show.time";
    public static final String SUGGEST_JJ_START = "jj_start";
    public static final String SUGGEST_HP_ML = "suggest_hp_ml";
    public static final String SUGGEST_HP_HD = "suggest_hp_hd";
    public static final String MORENNAME = "Morenname";
    public static final String MORENPHONE = "Morenphone";
    public static final String MORENPROVINCE = "Morenprovince";
    public static final String MORENCITY = "Morencity";
    public static final String MORENADDRESS = "Morenaddress";
    public static final String YCXIANSHI = "ycxianshi";
    public static final String EVALUATER_EPORT_TIPS_SHOWED = "EVALUATER_EPORT_TIPS_SHOWED";
    public static final String LEVEL_TIPS_SHOWED = "LEVEL_TIPS_SHOWED";
    public static final String DIALOG_SHOW = "DIALOG_SHOW";                                         // 申论答题页的批改两次dialog提示
    public static final String SELECT_POINT = "SELECT_POINT";
    public static final String ESSAY_DIALOG_SHOW = "ESSAY_DIALOG_SHOW";                             // 申论材料页的拍照识别dialog提示
    private static final String DOWN_PAPER_TIPS_SHOW = "DOWN_PAPER_TIPS_SHOW";                      // 试卷下载引导
    private static final String DOWN_REAL_PAPER_TIPS_SHOW = "DOWN_REAL_PAPER_TIPS_SHOW";            // 试卷下载引导
    private static final String DOWN_MO_KAO_PAPER_TIPS_SHOW = "DOWN_MO_KAO_PAPER_TIPS_SHOW";        // 试卷下载引导
    private static final String MULT_SELECT_POINT = "MULT_SELECT_POINT";
    private static final String ESSAY_SELECT_POINT = "ESSAY_SELECT_POINT";
    private static final String SON_TITLE_SELECT_POINT = "SON_TITLE_SELECT_POINT";
    private static final String PUBLIC_BASE_REAL_HIT = "PUBLIC_BASE_REAL_HIT";
    private static final String JOB_TEST_REAL_HIT = "JOB_TEST_REAL_HIT";
    private static final String PUBLIC_SECURITY_REAL_HIT = "PUBLIC_SECURITY_REAL_HIT";
    private static final String STATE_GRID_REAL_HIT = "STATE_GRID_REAL_HIT";
    private static final String CIVIL_SERVANT_REAL_HIT = "CIVIL_SERVANT_REAL_HIT";
    private static final String CIVIL_SERVANT_REAL_VERSION = "CIVIL_SERVANT_REAL_VERSION";
    private static final String PUBLIC_BASE_REAL_VERSION = "PUBLIC_BASE_REAL_VERSION";
    private static final String JOB_TEST_REAL_VERSION = "JOB_TEST_REAL_VERSION";
    private static final String PUBLIC_SECURITY_REAL_VERSION = "PUBLIC_SECURITY_REAL_VERSION";
    private static final String STATE_GRID_REAL_VERSION = "STATE_GRID_REAL_VERSION";
    public static SharedPreferences mSp;
    public static final String ISFIRST = "isfirst";
    private static final String HOST_URL = "host_url";
    private final static String TESTURLTAG = "testurltag";
    private final static String ABOUT_EMAIL = "aboutEmail";
    private final static String ABOUT_PHONE = "aboutPhone";
    private final static String COURSE_PHONE = "coursePhone";
    private final static String IS_WHITE_X = "IS_WHITE_X";                                                      // 是不是白名单
    public static final String DOUBT_TIPS_SHOW_X = "DoubtTipsShow_X";
    public static final String SHARE_PAPER_TIP_SHOW = "SHARE_PAPER_TIP_SHOW";
    public static final String DELETE_TIPS_SHOW_X = "DELETE_TIPS_SHOW_X";
    public static final String MATCH_REPORT_TIP_SHOW = "MATCH_REPORT_TIP_SHOW";                             // 新模考报告引导
    public static final String STUDY_TIME_TIPS_SHOW = "STUDY_TIME_TIPS_SHOW";
    public static final String ALL_COURSE_TIPS_SHOW = "ALL_COURSE_TIPS_SHOW";
    public static final String ME_COLLECT_TIPS_SHOW = "ME_COLLECT_TIPS_SHOW";
    public static final String RECYCLE_BIN_TIPS_SHOW = "RECYCLE_BIN_TIPS_SHOW";
    public static final String LEFT_SLIP_TIPS_SHOW = "LEFT_SLIP_TIPS_SHOW";
    public static final String DRAFT_TIPS_SHOW = "DraftTipsShow";
    public static final String CHOOSE_QUSEQ_TIPS_SHOW_X = "CHOOSE_QUSEQ_TIPS_SHOW_X";
    public static final String TIP_DEL_ERR_X = "TIP_DEL_ERR_X";                                             // 错题本中删除引导
    public static final String ARENA_AI_PRACTICE_TIPS_RL = "ARENA_AI_PRACTICE_TIPS_RL";
    public static final String HOMEF_MESSAGE_NOTIFY_CLOSE_LIST = "HOMEF_MESSAGE_NOTIFY_CLOSE_LIST";
    public static final String HT_SUBJECT_LIST = "HT_SUBJECT_LIST";
    public static final String CC_UPDATE_POSITION = "CC_UPDATE_POSITION";
    public static final String SHOW_GOLD_EXPERIENCE_TIP = "SHOW_GOLD_EXPERIENCE_TIP";
    // data list
    public static final String DOWN_LOAD_PATH = "DOWN_LOAD_PATH";
    public static final String PRAISE_CACHE_DATA = "PRAISE_CACHE_DATA";
    public static final String BIAO_ZHUN_DA_AN_ESSAY_DOWNLOAD_ING_LIST_ = "BIAO_ZHUN_DA_AN_ESSAY_DOWNLOAD_ING_LIST_";
    public static final String TAO_TI_ESSAY_DOWNLOAD_ING_LIST = "TAO_TI_ESSAY_DOWNLOAD_ING_LIST";
    public static final String YI_LUN_WEN_ESSAY_DOWNLOAD_ING_LIST = "YI_LUN_WEN_ESSAY_DOWNLOAD_ING_LIST";
    public static final String REAL_LIST_MAP = "REAL_LIST_MAP";
    public static final String REAL_AREA_VERSION = "REAL_AREA_VERSION";
    public static final String REAL_AREA_HIT = "REAL_AREA_HIT";
    public static final String ESSAY_SINGLE_DOWNLOAD_FAIL_LIST = "ESSAY_SINGLE_DOWNLOAD_FAIL_LIST";
    public static final String MULTI_ESSAY_DOWNLOAD_FAIL_LIST = "MULTI_ESSAY_DOWNLOAD_FAIL_LIST";
    public static final String ARGUE_ESSAY_DOWNLOAD_FAIL_LIST = "ARGUE_ESSAY_DOWNLOAD_FAIL_LIST";
    public static final String ESSAY_SINGLE_DOWNLOAD_SUCCESS_LIST = "ESSAY_SINGLE_DOWNLOAD_SUCCESS_LIST";
    public static final String MULTI_ESSAY_DOWNLOAD_SUCCESS_LIST = "MULTI_ESSAY_DOWNLOAD_SUCCESS_LIST";
    public static final String ARGUE_ESSAY_DOWNLOAD_SUCCESS_LIST = "ARGUE_ESSAY_DOWNLOAD_SUCCESS_LIST";

    public static final String ESSAY_MATER_SEL_LIST = "ESSAY_MATER_SEL_LIST";
    //essay multi
    public static final String ESSAY_MULTI_LIST = "ESSAY_MULTI_LIST";
    public static final String VOD_COURSE_EXAM = "VOD_COURSE_EXAM";
    public static final String VOD_COURSE_SUBJECT = "VOD_COURSE_SUBJECT";
    public static final String VOD_COURSE_CATEGORYLIST = "VOD_COURSE_CATEGORYLIST";
    public static final String VOD_COURSE_CATEGORY_POSITION = "VOD_COURSE_CATEGORY_POSITION";
    private static int updatephotoAnswer;
    private static String ESSAY_SINGLE_REWARD_SHOW = "ESSAY_SINGLE_REWARD_SHOW";                    // 申论单题批改奖励显示
    private static String ESSAY_MULTI_REWARD_SHOW = "ESSAY_MULTI_REWARD_SHOW";                      // 申论套题批改奖励显示
    private static String ESSAY_PHOTO_MSG = "ESSAY_PHOTO_MSG";
    private static String PHOTO_ANSWER_TYPE = "PHOTO_ANSWER_TYPE";
    private static String SELECT_TAB = "DETAIL_TAB";
    private static String SELECTED_CATEGORY_NAME = "SELECTED_CATEGORY_NAME";                        // 课程当前选中科目


    private static String CUSTOM_DEVICE_ID = "CUSTOM_DEVICE_ID";                                    // 本地生成的唯一标识
    private static String SHOW_LIVE_VIDEO_SWITCH_TIP = "LIVE_VIDEO_SHOW_SWITCH_TIP";                // 切换视频提示
    private static String SHOW_LIVE_VIDEO_OPERATION_TIP = "LIVE_VIDEO_OPERATION_TIPS";               // 视频操作提示

    private static String ARENA_PAPER_LOCAL_FILE = "ESSAY_PAPER_LOCAL_FILE";                        // 本地存储行测试卷信息

    private static String ARENA_ERROR_QUESTION_COUNT = "ARENA_ERROR_QUESTION_COUNT";                // 本地存储行测错题本一次练习数量
    private static String ARENA_ERROR_QUESTION_MODE = "ARENA_ERROR_QUESTION_MODE";                  // 本地存储行测错题本练习模式，0、做题 1、背题

    private static String ARENA_HOME_QUESTION_COUNT = "ARENA_HOME_QUESTION_COUNT";                  // 首页做题一次练习数量
    private static String ARENA_HOME_QUESTION_MODE = "ARENA_HOME_QUESTION_MODE";                    // 首页做题练习模式，0、做题 1、背题

    private static String HOME_GUIDE_STATE = "HOME_GUIDE_STATE";                                    // 首页引导图
    private static String ERROR_GUIDE_STATE = "ERROR_GUIDE_STATE";                                  // 错题本引导图

    private static String HOME_NEW_MATCH_TIPS = "HOME_NEW_MATCH_TIPS";                              // 记录首页模考大赛和小模考的更新提示角标

    private static String FACE_USER_INFORMATION = "FACE_USER_INFORMATION";                          // 面授课程，用户的信息记录

    private static String ESSAY_HOMEWORK_DOWNLOAD_TIPS = "ESSAY_HOMEWORK_DOWNLOAD_TIPS";            // 申论课后作业下载的提示窗是否显示

    private static String ARENA_PAD_DO_STYLE = "ARENA_PAD_DO_STYLE";                                // 行测pad做题模式 0、通屏 1、分屏

    public static void setArenaPadDoStyle(int style) {
        getSp().edit().putInt(ARENA_PAD_DO_STYLE, style).commit();
    }

    public static int getArenaPadDoStyle() {
        return getSp().getInt(ARENA_PAD_DO_STYLE, 0);
    }

    public static void clearVodCourseCategoryList() {
        getSp().edit().remove("VOD_COURSE_CATEGORYLIST").commit();
    }

    public static String getVodCourseCategoryList() {
        return getSp().getString("VOD_COURSE_CATEGORYLIST", "");
    }

    public static void setVodCourseCategoryList(String selPoint) {
        getSp().edit().putString("VOD_COURSE_CATEGORYLIST", selPoint).commit();

    }

    public static void clearVodCourseSubject() {
        getSp().edit().remove("VOD_COURSE_SUBJECT").commit();
    }

    public static void setVodCourseSubject(String string) {
        getSp().edit().putString(VOD_COURSE_SUBJECT, string).commit();
    }

    public static String getVodCourseSubject() {
        return getSp().getString(VOD_COURSE_SUBJECT, "fkzhuantiku@163.com");
    }

    public static void clearVodCourseExam() {
        getSp().edit().remove("VOD_COURSE_EXAM").commit();
    }

    public static void setVodCourseExam(int string) {
        getSp().edit().putInt(VOD_COURSE_EXAM, string).commit();
    }

    public static int getVodCourseExam() {
        return getSp().getInt(VOD_COURSE_EXAM, 0);
    }

    public static int CC_UPDATE_POSITION() {
        return getSp().getInt(CC_UPDATE_POSITION, 0);
    }

    public static void CC_UPDATE_POSITION(int position) {
        getSp().edit().putInt(CC_UPDATE_POSITION, position).commit();
    }

    public static boolean getArenaAiPracticeTipsCanShow() {
        return getSp().getBoolean(ARENA_AI_PRACTICE_TIPS_RL, true);
    }

    public static void setArenaAiPracticeTipsCanShow(boolean showed) {
        getSp().edit().putBoolean(ARENA_AI_PRACTICE_TIPS_RL, showed).commit();
    }

    public static boolean getLevelTipsShow() {
        return getSp().getBoolean(LEVEL_TIPS_SHOWED, false);
    }

    public static void setLevelTipsShow(boolean showed) {
        getSp().edit().putBoolean(LEVEL_TIPS_SHOWED, showed).commit();
    }

    public static boolean getDialogShow() {
        return getSp().getBoolean(DIALOG_SHOW, false);
    }

    public static void setDialogShow(boolean showed) {
        getSp().edit().putBoolean(DIALOG_SHOW, showed).commit();
    }

    public static boolean getEssayMaterialShow() {
        return getSp().getBoolean(ESSAY_DIALOG_SHOW, false);
    }

    public static void setEssayMaterialShow(boolean showed) {
        getSp().edit().putBoolean(ESSAY_DIALOG_SHOW, showed).commit();
    }

    public static boolean getShowGoldExperience(String key) {
        return getSp().getBoolean(SHOW_GOLD_EXPERIENCE_TIP + key, true);
    }

    public static void setShowGoldExperience(String key, boolean showed) {
        getSp().edit().putBoolean(SHOW_GOLD_EXPERIENCE_TIP + key, showed).commit();
    }

    public static int getDeleteTipsShowX() {
        return getSp().getInt(DELETE_TIPS_SHOW_X, 0);
    }

    public static void setDeleteTipsShowX() {
        getSp().edit().putInt(DELETE_TIPS_SHOW_X, AppUtils.getVersionCode()).commit();
    }

    public static boolean getStudyTimeTipsShow() {
        return getSp().getBoolean(STUDY_TIME_TIPS_SHOW, true);
    }

    public static void setStudyTimeTipsShow(boolean status) {
        getSp().edit().putBoolean(STUDY_TIME_TIPS_SHOW, status).commit();
    }

    public static boolean getAllCourseTipsShow() {
        return getSp().getBoolean(ALL_COURSE_TIPS_SHOW, true);
    }

    public static void setAllCourseTipsShow(boolean status) {
        getSp().edit().putBoolean(ALL_COURSE_TIPS_SHOW, status).commit();
    }

    public static boolean getMeCollectTipsShow() {
        return getSp().getBoolean(ME_COLLECT_TIPS_SHOW, true);
    }

    public static void setMeCollectTipsShow(boolean status) {
        getSp().edit().putBoolean(ME_COLLECT_TIPS_SHOW, status).commit();
    }

    public static boolean getRecycleBinTipsShow() {
        return getSp().getBoolean(RECYCLE_BIN_TIPS_SHOW, true);
    }

    public static void setRecycleBinTipsShow(boolean status) {
        getSp().edit().putBoolean(RECYCLE_BIN_TIPS_SHOW, status).commit();
    }

    public static boolean getLeftSlipTipsShow() {
        return getSp().getBoolean(LEFT_SLIP_TIPS_SHOW, true);
    }

    public static void setLeftSlipTipsShow(boolean status) {
        getSp().edit().putBoolean(LEFT_SLIP_TIPS_SHOW, status).commit();
    }

    public static int getDoubtTipsShowX() {
        return getSp().getInt(DOUBT_TIPS_SHOW_X, 0);
    }

    public static void setDoubtTipsShowX() {
        getSp().edit().putInt(DOUBT_TIPS_SHOW_X, AppUtils.getVersionCode()).commit();
    }


    public static boolean getSharePaperTipShow() {
        return getSp().getBoolean(SHARE_PAPER_TIP_SHOW, true);
    }

    public static void setSharePaperTipShow(boolean status) {
        getSp().edit().putBoolean(SHARE_PAPER_TIP_SHOW, status).commit();
    }

    public static boolean getDownPaperTip() {
        return getSp().getBoolean(DOWN_PAPER_TIPS_SHOW, true);
    }

    public static void setDownPaperTip(boolean status) {
        getSp().edit().putBoolean(DOWN_PAPER_TIPS_SHOW, status).commit();
    }

    public static boolean getDownRealPaperTip() {
        return getSp().getBoolean(DOWN_REAL_PAPER_TIPS_SHOW, true);
    }

    public static void setDownRealPaperTip(boolean status) {
        getSp().edit().putBoolean(DOWN_REAL_PAPER_TIPS_SHOW, status).commit();
    }

    public static boolean getDownMoKaoPaperTip() {
        return getSp().getBoolean(DOWN_MO_KAO_PAPER_TIPS_SHOW, true);
    }

    public static void setDownMoKaoPaperTip(boolean status) {
        getSp().edit().putBoolean(DOWN_MO_KAO_PAPER_TIPS_SHOW, status).commit();
    }

    public static int getChooseQueSeqShowX() {
        return getSp().getInt(CHOOSE_QUSEQ_TIPS_SHOW_X, 0);
    }

    public static void setChooseQueSeqShowX() {
        getSp().edit().putInt(CHOOSE_QUSEQ_TIPS_SHOW_X, AppUtils.getVersionCode()).commit();
    }

    /**
     * 重做错题，显示删除按钮的引导
     */
    public static int getTipErrDelX() {
        return getSp().getInt(TIP_DEL_ERR_X, 0);
    }

    public static void setTipErrDelX() {
        getSp().edit().putInt(TIP_DEL_ERR_X, AppUtils.getVersionCode()).commit();
    }

    public static boolean getDraftTipsShow() {
        return getSp().getBoolean(DRAFT_TIPS_SHOW, true);
    }

    public static void setDraftTipsShow(boolean status) {
        getSp().edit().putBoolean(DRAFT_TIPS_SHOW, status).commit();
    }

    public static boolean getFirst() {
        return getSp().getBoolean(ISFIRST, true);
    }

    public static void setFirst(boolean status) {
        getSp().edit().putBoolean(ISFIRST, status).commit();
    }

    public static boolean getYcxianshi() {
        return getSp().getBoolean(YCXIANSHI, true);
    }

    public static void setYcxianshi(boolean status) {
        getSp().edit().putBoolean(YCXIANSHI, status).commit();
    }

    public static SharedPreferences getSp() {
        return UniApplicationContext.getContext().getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences getSaveBundleSp() {
        return UniApplicationContext.getContext().getSharedPreferences("saved_bundle", Context.MODE_PRIVATE);
    }

    public static void setMorenname(String string) {
        getSp().edit().putString(MORENNAME, string).commit();
    }

    public static String getHomefMsgCloseList() {
        return getSp().getString(HOMEF_MESSAGE_NOTIFY_CLOSE_LIST, "");
    }

    public static void setHomefMsgCloseList(String string) {
        getSp().edit().putString(HOMEF_MESSAGE_NOTIFY_CLOSE_LIST, string).commit();
    }

    public static String getHtSubjectList() {
        return getSp().getString(HT_SUBJECT_LIST, "");
    }

    public static void setHtSubjectList(String string) {
        getSp().edit().putString(HT_SUBJECT_LIST, string).commit();
    }

    public static String getMorenname() {
        return getSp().getString(MORENNAME, "");
    }

    public static void setCoursePhone(String string) {
        getSp().edit().putString(COURSE_PHONE, string).commit();
    }

    public static String getCoursePhone() {
        return getSp().getString(COURSE_PHONE, ResourceUtils.getString(R.string.sys_server_phone));
    }

    public static void setSecKillUrl(String string) {
        getSp().edit().putString("SEC_KILL_URL", string).commit();
    }

    public static String getSecKillUrl() {
        return getSp().getString("SEC_KILL_URL", "http://sk.v.huatu.com");
    }

    public static void setIsWhite(int isWhite) {
        getSp().edit().putInt(IS_WHITE_X, isWhite).commit();
    }

    public static int getIsWhite() {
        return getSp().getInt(IS_WHITE_X, 0);
    }

    public static void setAboutPhone(String string) {
        getSp().edit().putString(ABOUT_PHONE, string).commit();
    }

    public static String getAboutPhone() {
        return getSp().getString(ABOUT_PHONE, ResourceUtils.getString(R.string.sys_server_phone));
    }

    public static void setAboutEmail(String string) {
        getSp().edit().putString(ABOUT_EMAIL, string).commit();
    }

    public static String getAboutEmail() {
        return getSp().getString(ABOUT_EMAIL, "fkzhuantiku@163.com");
    }

    public static void setMorenphone(String string) {
        getSp().edit().putString(MORENPHONE, string).commit();
    }

    public static String getMorenphone() {
        return getSp().getString(MORENPHONE, "");
    }

    public static void setMorenprovince(String string) {
        getSp().edit().putString(MORENPROVINCE, string).commit();
    }

    public static String getMorenprovince() {
        return getSp().getString(MORENPROVINCE, "");
    }

    public static void setMorencity(String string) {
        getSp().edit().putString(MORENCITY, string).commit();
    }

    public static String getMorencity() {
        return getSp().getString(MORENCITY, "");
    }

    public static void setMorenaddress(String string) {
        getSp().edit().putString(MORENADDRESS, string).commit();
    }

    public static String getMorenaddress() {
        return getSp().getString(MORENADDRESS, "");
    }

    public static boolean getSuggestHpML() {
        return getSp().getBoolean(SUGGEST_HP_ML, true);
    }

    public static void setSuggestHpMl(boolean flag) {
        getSp().edit().putBoolean(SUGGEST_HP_ML, flag).commit();
    }

    public static boolean getSuggestHpHD() {
        return getSp().getBoolean(SUGGEST_HP_HD, true);
    }

    public static void setSuggestHpHD(boolean flag) {
        getSp().edit().putBoolean(SUGGEST_HP_HD, flag).commit();
    }

//    public static boolean getSuggestJJStart() {
//        return getSp().getBoolean(SUGGEST_JJ_START, true);
//    }
//
//    public static void setSuggestJjStart(boolean status) {
//        getSp().edit().putBoolean(SUGGEST_JJ_START, status).commit();
//    }

    public static long getDialogShowTime() {
        return getSp().getLong(SHARE_DIALOG_SHOW_TIME, 0);
    }

    public static void setDialogShowTime(long time) {
        getSp().edit().putLong(SHARE_DIALOG_SHOW_TIME, time).commit();
    }

    public static int getCOMMENT_LIVE_PAIXU() {
        return getSp().getInt(COMMENT_LIVE_PAIXU, 0);
    }

    public static void setCOMMENT_LIVE_PAIXU(int status) {
        getSp().edit().putInt(COMMENT_LIVE_PAIXU, status).commit();
    }

    public static int getCOMMENT_LIVE_PRICE() {
        return getSp().getInt(COMMENT_LIVE_PRICE, 0);
    }

    public static void setCOMMENT_LIVE_PRICE(int status) {
        getSp().edit().putInt(COMMENT_LIVE_PRICE, status).commit();
    }

    public static void setCOMMENT_LIVE_DATE(int status) {
        getSp().edit().putInt(COMMENT_LIVE_DATE, status).commit();
    }

    public static int getCOMMENT_LIVE_DATE() {
        return getSp().getInt(COMMENT_LIVE_DATE, 0);
    }

    public static void setCOMMENT_LIVE_CATE(int status) {
        getSp().edit().putInt(COMMENT_LIVE_CATE, status).commit();
    }

    public static int getCOMMENT_LIVE_CATE() {
        return getSp().getInt(COMMENT_LIVE_CATE, 0);
    }

    public static int getCommentMINE_PAIXUORIDER() {
        return getSp().getInt(COMMENT_MINE_PAIXUORIDER, 0);
    }

    public static void setCommentMINE_PAIXUORIDER(int status) {
        getSp().edit().putInt(COMMENT_MINE_PAIXUORIDER, status).commit();
    }

    public static String getCommentMINE_PAIXU() {
        return getSp().getString(COMMENT_MINE_PAIXU, "全部课程");
    }

    public static void setCommentMINE_PAIXU(String status) {
        getSp().edit().putString(COMMENT_MINE_PAIXU, status).commit();
    }

    public static String getCommentPAIXU() {
        return getSp().getString(COMMENT_PAIXU, "综合排序");
    }

    public static void setCommentPAIXU(String status) {
        getSp().edit().putString(COMMENT_PAIXU, status).commit();
    }

    public static String getCommentKAOSHI() {
        return getSp().getString(COMMENT_KAOSHI, "全部");
    }

    public static void setCommentKAOSHI(String status) {
        getSp().edit().putString(COMMENT_KAOSHI, status).commit();
    }

    public static String getCommentDATE() {
        return getSp().getString(COMMENT_DATE, "全部");
    }

    public static void setCommentDATE(String status) {
        getSp().edit().putString(COMMENT_DATE, status).commit();
    }

    public static String getCommentPRICE() {
        return getSp().getString(COMMENT_PRICE, "全部");
    }

    public static void setCommentPRICE(String status) {
        getSp().edit().putString(COMMENT_PRICE, status).commit();
    }

    public static boolean getCommentStatus() {
        return getSp().getBoolean(COMMENT_STATUS, true);
    }

    public static void setCommentStatus(boolean status) {
        getSp().edit().putBoolean(COMMENT_STATUS, status).commit();
    }

    public static String getAvatar() {
        return getSp().getString(AVATAR, "");
    }

    public static void setAvatar(String avatar) {
        getSp().edit().putString(AVATAR, avatar).commit();
    }

    public static String getMobile() {
        return getSp().getString(MOBILE, "");
    }

    public static void setMobile(String mobile) {
        getSp().edit().putString(MOBILE, mobile).commit();
    }

    public static String getEmail() {
        return getSp().getString(EMAIL, "");
    }

    public static void setEmail(String email) {
        getSp().edit().putString(EMAIL, email).commit();
    }

    public static String getAreaname() {
        int curCatgory = SpUtils.getUserCatgory();
        return getSp().getString(AREANAME + "_" + curCatgory, "");
    }

    public static void setAreaname(String areaname) {
        int curCatgory = SpUtils.getUserCatgory();
        getSp().edit().putString(AREANAME + "_" + curCatgory, areaname).commit();
    }

    public static String getToken() {
        return getSp().getString(TOKEN, "");
    }

    public static void setToken(String token) {
        getSp().edit().putString(TOKEN, token).commit();
    }

    public static boolean getLoginState() {
        return getSp().getBoolean(LOGINSTATE, false);
    }

    public static void setLoginState(boolean loginstate) {
        getSp().edit().putBoolean(LOGINSTATE, loginstate).commit();
    }

    public static String getNick() {
        return getSp().getString(NICK, "");
    }

    public static void setUCenterId(String ucId) {
        getSp().edit().putString("app_sensor_mobileId", ucId).commit();
    }

    public static String getUCenterId() {
        return getSp().getString("app_sensor_mobileId", "");
    }

    public static void setNick(String nick) {
        getSp().edit().putString(NICK, nick).commit();
    }

    public static String getUname() {
        return getSp().getString(UNAME, "");
    }

    public static void setUname(String uname) {
        getSp().edit().putString(UNAME, uname).commit();
    }

    public static int getArea(int curCatgory) {
        return getSp().getInt(AREA + "_" + curCatgory, -9);
    }

    public static void setArea(int area) {
        int curCatgory = SpUtils.getUserCatgory();
        getSp().edit().putInt(AREA + "_" + curCatgory, area).commit();
    }

    public static int getUid() {
        int userId = getSp().getInt(UID, -1);
        if (userId <= 0) {
            String uid = getSp().getString("UID", null);
            if (!TextUtils.isEmpty(uid)) {
                userId = Method.parseInt(uid);
            }
        }
        return userId;
    }

    public static void setUid(int uid) {
        UserInfoUtil.userId = uid;
        getSp().edit().putInt(UID, uid).commit();
    }

    public static void setDayNightMode(int mode) {
        getSp().edit().putInt(DAY_NIGHT_MODE, mode).commit();
    }

    public static int getDayNightMode() {
        return getSp().getInt(DAY_NIGHT_MODE, 0);
    }

    public static void setFontSizeMode(int sizeMode) {
        getSp().edit().putInt(FONT_SIZE_MODE, sizeMode).commit();
    }

    public static int getFontSizeMode() {
        return getSp().getInt(FONT_SIZE_MODE, 0);
    }

    public static int getAppLaunchNumber() {
        return getSp().getInt(APP_LAUNCH_NUMBER, 0);
    }

    public static void setAppLaunchNumber(int launchNumber) {
        getSp().edit().putInt(APP_LAUNCH_NUMBER, launchNumber).commit();
    }

    public static void setAppCommented(boolean commented) {
        getSp().edit().putBoolean(APP_COMMENTED, commented).commit();
    }

    public static boolean getAppCommented() {
        return getSp().getBoolean(APP_COMMENTED, false);
    }

    public static void setUpdateBean(String value) {
        getSp().edit().putString("update_bean", value).commit();
    }

    public static String getUpdateBean() {
        return getSp().getString("update_bean", null);
    }

    public static void setUpdatePhotoAnswer(int flag) {
        getSp().edit().putInt("update_photo_answer", flag).commit();
    }

    public static int getUpdatePhotoAnswer() {
        return getSp().getInt("update_photo_answer", 0);
    }

    public static void setUpdatePhotoAnswerType(int flag) {
        getSp().edit().putInt(PHOTO_ANSWER_TYPE, flag).commit();
    }

    public static int getUpdatePhotoAnswerType() {
        return getSp().getInt(PHOTO_ANSWER_TYPE, -1);
    }

    public static void setUpdateVoiceAnswer(int flag) {
        getSp().edit().putInt("update_voice_answer", flag).commit();
    }

    public static int getUpdateVoiceAnswer() {
        return getSp().getInt("update_voice_answer", 0);
    }

    public static void setEssayCorrectFree(int flag) {
        getSp().edit().putInt("EssayCorrectFree", flag).commit();
    }

    public static int getEssayCorrectFree() {
        return getSp().getInt("EssayCorrectFree", 0);
    }

    public static void setUpdateFlag(boolean flag) {
        getSp().edit().putBoolean("has_update_info", flag).commit();
    }

    public static boolean getUpdateFlag() {
        return getSp().getBoolean("has_update_info", false);
    }

    public static void clearUpdateFlag() {
        getSp().edit().remove("has_update_info").commit();
    }

    public static void setUpdateLevelFlag(int flag) {
        getSp().edit().putInt("update_info_level", flag).commit();
    }

    public static int getUpdateLevelFlag() {
        return getSp().getInt("update_info_level", 1);
    }

    public static void clearUpdateLevelFlag() {
        getSp().edit().remove("update_info_level").commit();
    }

    public static void setUpdateLatestVersion(String url) {
        getSp().edit().putString("update_info_latest_version", url).commit();
    }

    public static String getUpdateLatestVersion() {
        return getSp().getString("update_info_latest_version", null);
    }

    public static void clearUpdateLatestVersion() {
        getSp().edit().remove("update_info_latest_version").commit();
    }

    public static void setUpdateMessage(String url) {
        getSp().edit().putString("update_info_message", url).commit();
    }

    public static String getUpdateMessage() {
        return getSp().getString("update_info_message", null);
    }

    public static void clearUpdateMessage() {
        getSp().edit().remove("update_info_message").commit();
    }

    public static void setUpdateUrl(String url) {
        getSp().edit().putString("update_info_url", url).commit();
    }

    public static String getUpdateUrl() {
        return getSp().getString("update_info_url", null);
    }

    public static void clearUpdateUrl() {
        getSp().edit().remove("update_info_url").commit();
    }

    public static void setTinkerClearFlag(boolean flag) {
        getSp().edit().putBoolean("tinker_clear_flag", flag).commit();
    }

    public static boolean getTinkerClearFlag() {
        return getSp().getBoolean("tinker_clear_flag", false);
    }

    public static void setTinkerUrl(String url) {
        getSp().edit().putString("tinker_url", url).commit();
    }

    public static String getTinkerUrl() {
        return getSp().getString("tinker_url", null);
    }

    public static void clearTinkerUrl() {
        getSp().edit().remove("tinker_url").commit();
    }

    public static void setTinkerMd5(String md5) {
        getSp().edit().putString("tinker_file_md5", md5).commit();
    }

    public static String getTinkerMd5() {
        return getSp().getString("tinker_file_md5", null);
    }

    public static void clearTinkerMd5() {
        getSp().edit().remove("tinker_file_md5").commit();
    }

    public static void clearTinkerInstalledPatchMd5() {
        getSp().edit().remove("tinker_installed_md5").commit();
    }

    public static void setTinkerInstalledPatchMd5(String md5) {
        getSp().edit().putString("tinker_installed_md5", md5).commit();
    }

    public static String getTinkerInstalledPatchMd5() {
        return getSp().getString("tinker_installed_md5", null);
    }

    public static void setArenaExamActRecreateState(boolean strBundle) {
        getSaveBundleSp().edit().putBoolean("arena_exam_act_recreate_flag", strBundle).commit();
    }

    public static boolean getArenaExamActRecreateState() {
        return getSaveBundleSp().getBoolean("arena_exam_act_recreate_flag", false);
    }

    public static void setArenaExamActExamBeanBundle(String strBundle) {
        getSaveBundleSp().edit().putString("arena_exam_act_real_exam_bean", strBundle).commit();
    }

    public static String getArenaExamActExamBeanBundle() {
        return getSaveBundleSp().getString("arena_exam_act_real_exam_bean", null);
    }

    public static void clearArenaExamActExamBeanBundle() {
        getSaveBundleSp().edit().remove("arena_exam_act_real_exam_bean").commit();
    }

    public static void setArenaMainFrgExamBeanBundle(String strBundle) {
        getSaveBundleSp().edit().putString("arena_main_frg_real_exam_bean", strBundle).commit();
    }

    public static String getArenaMainFrgExamBeanBundle() {
        return getSaveBundleSp().getString("arena_main_frg_real_exam_bean", null);
    }

    public static void clearArenaMainFrgExamBeanBundle() {
        getSaveBundleSp().edit().remove("arena_main_frg_real_exam_bean").commit();
    }

    public static void setArenaTitleFrgArenaInfoBundle(String strBundle) {
        getSaveBundleSp().edit().putString("arena_title_frg_arena_info", strBundle).commit();
    }

    public static String getArenaTitleFrgArenaInfoBundle() {
        return getSaveBundleSp().getString("arena_title_frg_arena_info", null);
    }

    public static void setArenaExamExerciseTitleFrgBundle(String strBundle) {
        getSaveBundleSp().edit().putString("arena_exam_exercise_title_frg", strBundle).commit();
    }

    public static String getArenaExamExerciseTitleFrgBundle() {
        return getSaveBundleSp().getString("arena_exam_exercise_title_frg", null);
    }

    public static void clearArenaExamExerciseTitleFrgBundle() {
        getSaveBundleSp().edit().remove("arena_exam_exercise_title_frg").commit();
    }

    public static void setHostUrl(String url) {
        getSp().edit().putString("host_url", url).commit();
    }

    public static String getHostUrl() {
        return getSp().getString("host_url", "");
    }

    public static void setTestUrlPosition(int position) {
        getSp().edit().putInt(TESTURLTAG, position).commit();
    }

    public static int getTestUrlPosition() {
        return getSp().getInt(TESTURLTAG, 0);
    }

    public static void setUserSubject(int userSubject) {
        if (userSubject != -1) {
            LogUtils.d("SpUtils", "userSubject " + userSubject);
            getSp().edit().putInt("user.subject", userSubject).commit();
        }
    }

    public static int getUserCatgory() {
        return getSp().getInt("user.catgory", 1);
    }

    public static void setUserCatgory(int catgory) {
        LogUtils.d("SpUtils", "usercatgory " + catgory);
        getSp().edit().putInt("user.catgory", catgory).commit();
    }

    public static int getUserSubject() {
        return getSp().getInt("user.subject", 1);
    }

    public static void setScanFromUser(String fromusers) {
        getSp().edit().putString("fromusers", fromusers).commit();
    }

    public static String getScanFromUser() {
        return getSp().getString("fromusers", "");
    }

    public static void setScanFromUserData(String fromusersData) {
        getSp().edit().putString("fromusersData", fromusersData).commit();
    }

    public static String getScanFromUserData() {
        return getSp().getString("fromusersData", "");
    }

    public static void setIsSimulationContest(boolean isSc) {
        getSp().edit().putBoolean("IsSimulationContest", isSc).commit();
    }

    public static boolean isSimulationContest() {
        return getSp().getBoolean("IsSimulationContest", false);
    }

    public static void setSimulationContestId(String strid) {
        getSp().edit().putString("IsSimulationContest_id", strid).commit();
    }

    public static String getSimulationContestId() {
        return getSp().getString("IsSimulationContest_id", "");
    }

    public static boolean getFreeCourseListenFlag() {
        return getSp().getBoolean("free_course_listen", true);
    }

    public static void setFreeCourseListenFlag(boolean btip) {
        getSp().edit().putBoolean("free_course_listen", btip).commit();
    }

    public static boolean getPayCourseListenFlag() {
        return getSp().getBoolean("pay_course_listen", true);
    }

    public static void setPayCourseListenFlag(boolean flag) {
        getSp().edit().putBoolean("pay_course_listen", flag).commit();
    }

    public static boolean getCourseJudgeFlag() {
        return getSp().getBoolean("course_judge", true);
    }

    public static void setCourseJudgeFlag(boolean flag) {
        getSp().edit().putBoolean("course_judge", flag).commit();
    }

    public static boolean getShareFlag() {
        return getSp().getBoolean("share_flag", true);
    }

    public static void setShareFlag(boolean flag) {
        getSp().edit().putBoolean("share_flag", flag).commit();
    }

    public static boolean getDailySpecialTip() {
        return getSp().getBoolean("DailySpecialTip", true);
    }

    public static void setDailySpecialTip(boolean btip) {
        getSp().edit().putBoolean("DailySpecialTip", btip).commit();
    }

    public static void setDownloadFilePath(String var) {
        getSaveBundleSp().edit().putString(DOWN_LOAD_PATH + "_" + getUid(), var).commit();
    }

    public static String getDownloadFilePath() {
        return getSaveBundleSp().getString(DOWN_LOAD_PATH + "_" + getUid(), null);
    }

    public static void setPraiseDataPath(String var) {
        getSaveBundleSp().edit().putString(PRAISE_CACHE_DATA + "_" + getUid(), var).commit();
    }

    public static String getPraiseDataPath() {
        return getSaveBundleSp().getString(PRAISE_CACHE_DATA + "_" + getUid(), null);
    }

    public static void setBiaoZhunDaAnEssayDownloadIngList(String strlist) {
        getSaveBundleSp().edit().putString(BIAO_ZHUN_DA_AN_ESSAY_DOWNLOAD_ING_LIST_ + "_" + getUid(), strlist).commit();
    }

    public static String getBiaoZhunDaAnEssayDownloadIngList() {
        return getSaveBundleSp().getString(BIAO_ZHUN_DA_AN_ESSAY_DOWNLOAD_ING_LIST_ + "_" + getUid(), null);
    }

    public static void setTaoTiEssayDownloadIngList(String strlist) {
        getSaveBundleSp().edit().putString(TAO_TI_ESSAY_DOWNLOAD_ING_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getTaoTiEssayDownloadIngList() {
        return getSaveBundleSp().getString(TAO_TI_ESSAY_DOWNLOAD_ING_LIST + "_" + getUid(), null);
    }

    public static void setYiLunWenEssayDownloadIngList(String strlist) {
        getSaveBundleSp().edit().putString(YI_LUN_WEN_ESSAY_DOWNLOAD_ING_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getYiLunWenEssayDownloadIngList() {
        return getSaveBundleSp().getString(YI_LUN_WEN_ESSAY_DOWNLOAD_ING_LIST + "_" + getUid(), null);
    }

    public static String getRealListMap() {
        return getSaveBundleSp().getString(REAL_LIST_MAP + "_" + getUid(), null);
    }

    public static void setRealListMap(String strMap) {
        getSaveBundleSp().edit().putString(REAL_LIST_MAP + "_" + getUid(), strMap).commit();
    }

    public static String getRealAreaVersion() {
        return getSaveBundleSp().getString(REAL_AREA_VERSION + "_" + getUid(), null);
    }

    public static void setRealAreaVersion(String strMap) {
        getSaveBundleSp().edit().putString(REAL_AREA_VERSION + "_" + getUid(), strMap).commit();
    }

    public static String getRealAreaHit() {
        return getSaveBundleSp().getString(REAL_AREA_HIT + "_" + getUid(), null);
    }

    public static void setRealAreaHit(String strMap) {
        getSaveBundleSp().edit().putString(REAL_AREA_HIT + "_" + getUid(), strMap).commit();
    }

    public static void setEssayMaterSelList(String strlist) {
        getSaveBundleSp().edit().putString(ESSAY_MATER_SEL_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getEssayMaterSelList() {
        return getSaveBundleSp().getString(ESSAY_MATER_SEL_LIST + "_" + getUid(), null);
    }

    public static void setEssayMultiList(String multilist) {
        getSaveBundleSp().edit().putString(ESSAY_MULTI_LIST, multilist).commit();
    }

    public static String getEssayMultiList() {
        return getSaveBundleSp().getString(ESSAY_MULTI_LIST, null);
    }

    public static void setSingleEssaydownloadFailList(String strlist) {
        getSaveBundleSp().edit().putString(ESSAY_SINGLE_DOWNLOAD_FAIL_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getSingleEssaydownloadFailList() {
        return getSaveBundleSp().getString(ESSAY_SINGLE_DOWNLOAD_FAIL_LIST + "_" + getUid(), null);
    }

    public static void setMultiEssaydownloadFailList(String strlist) {
        getSaveBundleSp().edit().putString(MULTI_ESSAY_DOWNLOAD_FAIL_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getMultiEssaydownloadFailList() {
        return getSaveBundleSp().getString(MULTI_ESSAY_DOWNLOAD_FAIL_LIST + "_" + getUid(), null);
    }

    public static void setArgueEssaydownloadFailList(String strlist) {
        getSaveBundleSp().edit().putString(ARGUE_ESSAY_DOWNLOAD_FAIL_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getArgueEssaydownloadFailList() {
        return getSaveBundleSp().getString(ARGUE_ESSAY_DOWNLOAD_FAIL_LIST + "_" + getUid(), null);
    }

    public static void setMultiEssayDownloadSuccessList(String strlist) {
        getSaveBundleSp().edit().putString(MULTI_ESSAY_DOWNLOAD_SUCCESS_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getMultiEssaydownloadSuccessList() {
        return getSaveBundleSp().getString(MULTI_ESSAY_DOWNLOAD_SUCCESS_LIST + "_" + getUid(), null);
    }

    public static String getSingleEssaydownloadSuccessList() {
        return getSaveBundleSp().getString(ESSAY_SINGLE_DOWNLOAD_SUCCESS_LIST + "_" + getUid(), null);
    }

    public static void setSingleEssayDownloadSuccessList(String strlist) {
        getSaveBundleSp().edit().putString(ESSAY_SINGLE_DOWNLOAD_SUCCESS_LIST + "_" + getUid(), strlist).commit();
    }

    public static String getArgueEssaydownloadSuccessList() {
        return getSaveBundleSp().getString(ARGUE_ESSAY_DOWNLOAD_SUCCESS_LIST + "_" + getUid(), null);
    }

    public static void setArgueEssayDownloadSuccessList(String strlist) {
        getSaveBundleSp().edit().putString(ARGUE_ESSAY_DOWNLOAD_SUCCESS_LIST + "_" + getUid(), strlist).commit();
    }


    public static boolean getActivityTip(String tag) {
        return getSp().getBoolean(tag, true);
    }

    public static void setActivityTip(String tag, boolean btip) {
        getSp().edit().putBoolean(tag, btip).commit();
    }

    public static void setSelectPoint(int selPoint) {
        getSp().edit().putInt(SELECT_POINT, selPoint).commit();

    }

    public static void setSelectTabPoint(int selPoint) {
        getSp().edit().putInt(SELECT_TAB, selPoint).commit();

    }

    public static int getSelectedLiveCategory() {
        return getSp().getInt("selected_live_category" + SpUtils.getUid(), -1);
    }

    public static void setSelectedLiveCategory(int selPoint) {
        getSp().edit().putInt("selected_live_category" + SpUtils.getUid(), selPoint).commit();

    }

    public static String getLiveCategoryList() {
        return getSp().getString("live_category_list" + SpUtils.getUid(), "");
    }

    public static void setLiveCategoryList(String selPoint) {
        getSp().edit().putString("live_category_list" + SpUtils.getUid(), selPoint).commit();

    }

    public static int getSelectPoint() {
        return getSp().getInt(SELECT_POINT, -1);
    }

    public static int getSelecTabtPoint() {
        return getSp().getInt(SELECT_TAB, -1);
    }

    public static void setMultSelectPoint(int selPoint) {
        getSp().edit().putInt(MULT_SELECT_POINT, selPoint).commit();

    }

    public static int getMultSelectPoint() {
        return getSp().getInt(MULT_SELECT_POINT, -1);
    }

    public static void setEssaySelectPoint(int selPoint) {
        getSp().edit().putInt(ESSAY_SELECT_POINT, selPoint).commit();

    }

    public static int getEssaySelectPoint() {
        return getSp().getInt(ESSAY_SELECT_POINT, 0);
    }

    public static boolean getSingleCheckReward() {
        return getSp().getBoolean(ESSAY_SINGLE_REWARD_SHOW, true);

    }

    public static void setSingleCheckReward(boolean status) {
        getSp().edit().putBoolean(ESSAY_SINGLE_REWARD_SHOW, status).commit();

    }

    public static boolean getMultiCheckReward() {
        return getSp().getBoolean(ESSAY_MULTI_REWARD_SHOW, true);

    }

    public static void setMultiCheckReward(boolean status) {
        getSp().edit().putBoolean(ESSAY_MULTI_REWARD_SHOW, status).commit();

    }

    public static void setUpdatePhotoAnswerMsg(String photoAnswerMsg) {
        getSp().edit().putString(ESSAY_PHOTO_MSG, photoAnswerMsg).commit();

    }

    public static String getUpdatePhotoAnswerMsg() {
        return getSp().getString("ESSAY_PHOTO_MSG", "");
    }

    public static void setSonTitleSelected(int position) {
        getSp().edit().putInt(SON_TITLE_SELECT_POINT, position).commit();
    }

    public static int getSonTitleSelected() {
        return getSp().getInt(SON_TITLE_SELECT_POINT, 0);
    }

    public static void setPublicBaseRealExamHit(int i) {
        getSp().edit().putInt(PUBLIC_BASE_REAL_HIT + "_" + getUid(), i).commit();
    }

    public static int getPublicBaseRealExamHit() {
        return getSp().getInt(PUBLIC_BASE_REAL_HIT + "_" + getUid(), 0);
    }

    public static void setJobTestRealExamHit(int i) {
        getSp().edit().putInt(JOB_TEST_REAL_HIT + "_" + getUid(), i).commit();

    }

    public static int getJobTestRealExamHit() {
        return getSp().getInt(JOB_TEST_REAL_HIT + "_" + getUid(), 0);
    }

    public static void setPublicSecurityRealExamHit(int i) {
        getSp().edit().putInt(PUBLIC_SECURITY_REAL_HIT + "_" + getUid(), i).commit();
    }

    public static int getPublicSecurityRealExamHit() {
        return getSp().getInt(PUBLIC_SECURITY_REAL_HIT + "_" + getUid(), 0);
    }

    public static void setStateGridRealExamHit(int i) {
        getSp().edit().putInt(STATE_GRID_REAL_HIT + "_" + getUid(), i).commit();

    }

    public static int getStateGridRealExamHit() {
        return getSp().getInt(STATE_GRID_REAL_HIT + "_" + getUid(), 0);
    }

    public static void setCivilServantRealExamHit(int i) {
        getSp().edit().putInt(CIVIL_SERVANT_REAL_HIT + "_" + getUid(), i).commit();
    }

    public static int getCivilServantRealExamHit() {
        return getSp().getInt(CIVIL_SERVANT_REAL_HIT + "_" + getUid(), 0);
    }

    public static void setCivilServantRealExamVersion(long l) {
        getSp().edit().putLong(CIVIL_SERVANT_REAL_VERSION + "_" + getUid(), l).commit();
    }

    public static long getCivilServantRealExamVersion() {
        return getSp().getLong(CIVIL_SERVANT_REAL_VERSION + "_" + getUid(), -1);
    }

    public static void setPublicBaseRealExamVersion(long l) {
        getSp().edit().putLong(PUBLIC_BASE_REAL_VERSION + "_" + getUid(), l).commit();
    }

    public static long getPublicBaseRealExamVersion() {
        return getSp().getLong(PUBLIC_BASE_REAL_VERSION + "_" + getUid(), -1);
    }

    public static void setJobTestRealExamVersion(long l) {
        getSp().edit().putLong(JOB_TEST_REAL_VERSION + "_" + getUid(), l).commit();

    }

    public static long getJobTestRealExamVersion() {
        return getSp().getLong(JOB_TEST_REAL_VERSION + "_" + getUid(), -1);
    }


    public static void setPublicSecurityRealExamVersion(long l) {
        getSp().edit().putLong(PUBLIC_SECURITY_REAL_VERSION + "_" + getUid(), l).commit();

    }

    public static long getPublicSecurityRealExamVersion() {
        return getSp().getLong(PUBLIC_SECURITY_REAL_VERSION + "_" + getUid(), -1);
    }


    public static void setStateGridRealExamVersion(long l) {
        getSp().edit().putLong(STATE_GRID_REAL_VERSION + "_" + getUid(), l).commit();
    }

    public static long getStateGridRealExamVersion() {
        return getSp().getLong(STATE_GRID_REAL_VERSION + "_" + getUid(), -1);

    }


    public static void setCustomDeviceId(String customDeviceId) {
        getSp().edit().putString(CUSTOM_DEVICE_ID, customDeviceId).commit();
    }

    public static String getCustomDeviceId() {
        return getSp().getString(CUSTOM_DEVICE_ID, "");

    }

    public static boolean isShowSwitchPPtTip() {
        return getSp().getBoolean(SHOW_LIVE_VIDEO_SWITCH_TIP, true);
    }

    public static void setShowSwitchPPtTip(boolean show) {
        getSp().edit().putBoolean(SHOW_LIVE_VIDEO_SWITCH_TIP, show).commit();
    }

    public static boolean isShowLiveVideoTip() {
        return getSp().getBoolean(SHOW_LIVE_VIDEO_OPERATION_TIP, true);
    }

    public static void setShowLiveVideoTip(boolean show) {
        getSp().edit().putBoolean(SHOW_LIVE_VIDEO_OPERATION_TIP, show).commit();
    }

    // 把本地下载的行测试卷信息保存到SP
    public static void setArenaPaperLocalFile(String fileContent) {
        getSp().edit().putString(ARENA_PAPER_LOCAL_FILE, fileContent).commit();
    }

    // 从SP取出下载的
    public static String getArenaPaperLocalFile() {
        return getSp().getString(ARENA_PAPER_LOCAL_FILE, null);
    }

    // 保存错题本一次练习的数量
    public static void setErrorQuestionSize(int count) {
        getSp().edit().putInt(ARENA_ERROR_QUESTION_COUNT + getUserSubject() + getUserCatgory(), count).commit();
    }

    // 取出错题本一次练习的数量
    public static int getErrorQuestionSize() {
        return getSp().getInt(ARENA_ERROR_QUESTION_COUNT + getUserSubject() + getUserCatgory(), 15);
    }

    // 保存错题本做题模式，0、做题 1、背题
    public static void setErrorQuestionMode(int mode) {
        getSp().edit().putInt(ARENA_ERROR_QUESTION_MODE + getUserSubject() + getUserCatgory(), mode).commit();
    }

    // 取出错题本做题模式，0、做题 1、背题
    public static int getErrorQuestionMode() {
        return getSp().getInt(ARENA_ERROR_QUESTION_MODE + getUserSubject() + getUserCatgory(), 0);
    }

    // 保存错题本一次练习的数量
    public static void setHomeQuestionSize(int count) {
        getSp().edit().putInt(ARENA_HOME_QUESTION_COUNT + getUserSubject() + getUserCatgory(), count).commit();
    }

    // 取出错题本一次练习的数量
    public static int getHomeQuestionSize() {
        return getSp().getInt(ARENA_HOME_QUESTION_COUNT + getUserSubject() + getUserCatgory(), 15);
    }

    // 保存错题本做题模式，0、做题 1、背题
    public static void setHomeQuestionMode(int mode) {
        getSp().edit().putInt(ARENA_HOME_QUESTION_MODE + getUserSubject() + getUserCatgory(), mode).commit();
    }

    // 取出错题本做题模式，0、做题 1、背题
    public static int getHomeQuestionMode() {
        return getSp().getInt(ARENA_HOME_QUESTION_MODE + getUserSubject() + getUserCatgory(), 0);
    }

    // HomeFragment引导是否显示，value为versionCode
    public static void setHomeGuideState() {
        getSp().edit().putInt(HOME_GUIDE_STATE, AppUtils.getVersionCode()).commit();
    }

    public static int getHomeGuideState() {
        return getSp().getInt(HOME_GUIDE_STATE, -1);
    }

    // 错题本引导是否显示，value为versionCode
    public static void setErrorGuideState() {
        getSp().edit().putInt(ERROR_GUIDE_STATE, AppUtils.getVersionCode()).commit();
    }

    public static int getErrorGuideStatee() {
        return getSp().getInt(ERROR_GUIDE_STATE, -1);
    }

    public static void setSelectedCategoryName(String name) {
        getSp().edit().putString(SELECTED_CATEGORY_NAME, name).commit();
    }

    public static String getSelectedCategoryName() {
        return getSp().getString(SELECTED_CATEGORY_NAME, null);
    }

    public static void setMatchReportTipShow() {
        getSp().edit().putBoolean(MATCH_REPORT_TIP_SHOW, false).commit();
    }

    public static boolean getMatchReportTipShow() {
        return getSp().getBoolean(MATCH_REPORT_TIP_SHOW, true);
    }

    // 首页模考大赛和小模考红色更新提示角标
    public static void setMatchNewTip(String content) {
        getSp().edit().putString(HOME_NEW_MATCH_TIPS, content).commit();
    }

    public static String getMatchNewTip() {
        return getSp().getString(HOME_NEW_MATCH_TIPS, "");
    }

    // 面授课程，记录用户的个人信息
    public static void setFaceUserInformation(String content) {
        getSp().edit().putString(FACE_USER_INFORMATION, content).commit();
    }

    public static String getFaceUserInformation() {
        return getSp().getString(FACE_USER_INFORMATION, "");
    }

    // 申论课后作业下载的提示窗是否显示
    public static void setHomeworkDownloadTipsShow() {
        getSp().edit().putBoolean(ESSAY_HOMEWORK_DOWNLOAD_TIPS, false).commit();
    }

    public static boolean getHomeworkDownloadTipsShow() {
        return getSp().getBoolean(ESSAY_HOMEWORK_DOWNLOAD_TIPS, true);
    }

}
