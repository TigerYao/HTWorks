package com.huatu.handheld_huatu.business.arena.utils;

import com.huatu.handheld_huatu.R;

/**
 * Created by saiyuan on 2016/10/13.
 */
public class ArenaConstant {

    // 试题类型，单选：99，多选：100，不定项：101，对错题：109，复合题：105
    public static final int EXAM_QUESTION_TYPE_SINGLE_CHOICE = 99;
    public static final int EXAM_QUESTION_TYPE_MULTIPLE_CHOICE = 100;
    public static final int EXAM_QUESTION_TYPE_UNDIFINE_CHOICE = 101;
    public static final int EXAM_QUESTION_TYPE_JUDGEMENT = 109;
    public static final int EXAM_QUESTION_TYPE_COMPLEX = 105;
    public static final int QUESTION_TYPE_SUBJECTIVE = 106;                         // (不可作答题型)

    /**
     * 启动做题页面
     *
     * @param context   Context
     * @param arguments JsonObject对象，根据不同题型加入不同属性，然后toString()
     * 必添加字段action，常规做题为0，不做题直接看答题报告和解析为1(直接看解析需要增加practiseId属性，即练习/答题卡id)
     * 必添加字段freshness，新做一份题为0，继续做题为1(继续做题需要增加practiseId属性，即练习/答题卡id)
     * 专项练习  type 2|pointId 知识点id|size 题量
     * 真题演练  type 3|id 真题试卷id
     * 错题训练  type 6|pointId 知识点id|size 题量
     * 每日训练  type 7|pointId 知识点id
     * 模考估分  type 9|id 模考试卷id
     * 单题解析和错题解析，添加2个字段：
     * howToAnalyze——单题解析值为1，错题集解析查看值为2，收藏夹解析查看为3
     * exerciseIdList——List形式，把单题或者所有错题的题目id添加到List<Integer>中，再转化成JsonArray，添加到JsonObject对象中
     * JsonArray jsonArray = (JsonArray) new Gson().toJsonTree(idsList, new TypeToken<List<Integer>>(){}.getType());
     * jsonObject.add("exerciseIdList", jsonArray);
     */
    public static final int EXAM_ENTER_FORM_TYPE_AI_PRACTICE = 1;                   // 智能刷题 artificial intelligence ai 智能练习  随手练
    public static final int EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI = 2;             // 专项练习
    public static final int EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN = 3;                // 真题演练
    public static final int EXAM_ENTER_FORM_TYPE_PAST_MOKAO = 14;                   // 往期模考
    public static final int EXAM_ENTER_FORM_TYPE_AI_SIMULATION = 4;                 // artificial intelligence ai 智能模考
    public static final int EXAM_ENTER_FORM_TYPE_JINGJI_DATI = 5;                   // 竞技答题记录
    public static final int EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI = 6;                  // 错题重练
    public static final int EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN = 7;                   // 每日特训
    public static final int EXAM_ENTER_FORM_TYPE_COLLECT_PRACTICE = 8;              // 收藏练习
    public static final int EXAM_ENTER_FORM_TYPE_MOKAOGUFEN = 9;                    // 专项模考
    public static final int EXAM_ENTER_FORM_TYPE_ZC_LIANXI = 10;                    // 砖超联赛
    public static final int EXAM_ENTER_FORM_TYPE_WEIXIN_LIANXI = 11;                // 微信练习
    public static final int EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST = 12;           // 答题卡类型	模考大赛
    public static final int EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN = 13;               // 精准估分
    public static final int EXAM_ENTER_FORM_TYPE_COURSE_EXERICE = 15;               // 课后练习
    public static final int EXAM_ENTER_FORM_TYPE_SMALL_MATCH = 17;                  // 小模考
    public static final int EXAM_ENTER_FORM_TYPE_STAGE_TEST = 18;                   // 阶段性测试（课程）
    public static final int EXAM_ENTER_FORM_TYPE_JINGJICHANG = 20;                  // 竞技场（再也不用了）
    public static final int EXAM_ENTER_FORM_TYPE_ERROR_EXPORT = 20;                 // 错题导出
    public static final int EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI = 21;     // 专项练习（背题模式）
    public static final int EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI = 22;          // 错题重练（背题模式）

    public static final int EXAM_ENTER_FORM_TYPE_RECITE_PRE = 50;                   // 背题模式 先答题状态

    // 启动 页面
    public static final int START_ATHLETIC_ARENA = 2001;                            // 竞技赛场
    public static final int START_EVALUATE_REPORT = 2002;                           // 评估报告
    public static final int START_PAGER_RECORD = 2003;                              // 答题记录
    public static final int START_PAGER_FAVORITE_COLLECT = 2004;                    // 收藏夹
    public static final int START_PAGER_DOWNLOAD_PAPER = 2005;                      // 行测试卷下载
    public static final int START_NTEGRATED_APPLICATION = 2006;                     // 综合应用

    public static final int EXAM_ENTER_FORM_TYPE_JIEXI_ALL = 100;                   // 全部解析，默认情况
    public static final int EXAM_ENTER_FORM_TYPE_JIEXI_SINGLE = 101;                // 单题解析
    public static final int EXAM_ENTER_FORM_TYPE_JIEXI_WRONG = 102;                 // 错题解析
    public static final int EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE = 103;              // 收藏解析

    public static final int EXAM_ENTER_FORM_TYPE_RECITE_AFTER = 104;                // 背题模式 后查看解析状态

    public static final int EXAM_ENTER_FORM_TYPE_JIEXI_PREVIEW = 150;               // 教师预览模式

    public static final int EXAM_PAPER_TYPE_SIMULATION_CONTEST = 9;                 // 试卷类型 模考大赛

    public static final int DEFAULT_PAGE_SIZE = 10;

    public static final int ARENA_TYPE_INTELLIGENT = 1;             // 智能推送
    public static final int ARENA_TYPE_DEFINED_KNOWLEDGE = 2;
    public static final int ARENA_MODULE_CHANGSHI_PANDUAN = 392;    // 常识判断
    public static final int ARENA_MODULE_YANYU_LIJIE = 435;         // 言语理解
    public static final int ARENA_MODULE_SHULIANG_GUANXI = 482;     // 数量关系
    public static final int ARENA_MODULE_PANDUAN_TUILI = 642;       // 判断推理
    public static final int ARENA_MODULE_ZILIAO_FENXI = 754;        // 资料分析

    public static final int ARENA_ROOM_STATE_BUILD = 1;
    public static final int ARENA_ROOM_STATE_PROCESSING = 2;
    public static final int ARENA_ROOM_STATE_FINISH = 3;

    public static final String ACTION_USER_PROGRESS_UPDATE = "com.athletic.progress";
    public static final String ACTION_NOTIFY_USER_CHECK_RESULT = "com.athletic.result";
    public static final String ACTION_MINE_PROGRESS_UPDATE = "com.athletic.progress.mine";

    // 获取img标签正则
    public static final String IMG_REGULAR = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    public static final String SRC_REGULAR = "http:\"?(.*?)(\"|>|\\s+)";

    public final static int[] SINGLE_IMAGE_DEF = {R.mipmap.single_a_1, R.mipmap.single_b_1, R.mipmap.single_c_1, R.mipmap.single_d_1, R.mipmap.single_e_1, R.mipmap.single_f_1, R.mipmap.single_g_1, R.mipmap.single_h_1};
    public final static int[] SINGLE_IMAGE_DEF_NIGHT = {R.mipmap.single_a_1_night, R.mipmap.single_b_1_night, R.mipmap.single_c_1_night, R.mipmap.single_d_1_night, R.mipmap.single_e_1_night, R.mipmap.single_f_1_night, R.mipmap.single_g_1_night, R.mipmap.single_h_1_night};

    public final static int[] SINGLE_IMAGE_RIGHT = {R.mipmap.single_a_4, R.mipmap.single_b_4, R.mipmap.single_c_4, R.mipmap.single_d_4, R.mipmap.single_e_4, R.mipmap.single_f_4, R.mipmap.single_g_4, R.mipmap.single_h_4};
    public final static int[] SINGLE_IMAGE_RIGHT_NIGHT = {R.mipmap.single_a_4_night, R.mipmap.single_b_4_night, R.mipmap.single_c_4_night, R.mipmap.single_d_4_night, R.mipmap.single_e_4_night, R.mipmap.single_f_4_night, R.mipmap.single_g_4_night, R.mipmap.single_h_4_night};

    public final static int[] SINGLE_IMAGE_SELECTED = {R.mipmap.single_a_2, R.mipmap.single_b_2, R.mipmap.single_c_2, R.mipmap.single_d_2, R.mipmap.single_e_2, R.mipmap.single_f_2, R.mipmap.single_g_2, R.mipmap.single_h_2};
    public final static int[] SINGLE_IMAGE_SELECTED_NIGHT = {R.mipmap.single_a_2_night, R.mipmap.single_b_2_night, R.mipmap.single_c_2_night, R.mipmap.single_d_2_night, R.mipmap.single_e_2_night, R.mipmap.single_f_2_night, R.mipmap.single_g_2_night, R.mipmap.single_h_2_night};

    public final static int[] SINGLE_IMAGE_WRONG = {R.mipmap.single_a_3, R.mipmap.single_b_3, R.mipmap.single_c_3, R.mipmap.single_d_3, R.mipmap.single_e_3, R.mipmap.single_f_3, R.mipmap.single_g_3, R.mipmap.single_h_3};
    public final static int[] SINGLE_IMAGE_WRONG_NIGHT = {R.mipmap.single_a_3_night, R.mipmap.single_b_3_night, R.mipmap.single_c_3_night, R.mipmap.single_d_3_night, R.mipmap.single_e_3_night, R.mipmap.single_f_3_night, R.mipmap.single_g_3_night, R.mipmap.single_h_3_night};

    public final static int[] DELETE_IMAGE_DAY = {R.mipmap.del_day_a, R.mipmap.del_day_b, R.mipmap.del_day_c, R.mipmap.del_day_d, R.mipmap.del_day_e, R.mipmap.del_day_f, R.mipmap.del_day_g, R.mipmap.del_day_h, R.mipmap.del_day_i};
    public final static int[] DELETE_IMAGE_NIGHT = {R.mipmap.del_night_a, R.mipmap.del_night_b, R.mipmap.del_night_c, R.mipmap.del_night_d, R.mipmap.del_night_e, R.mipmap.del_night_f, R.mipmap.del_night_g, R.mipmap.del_night_h, R.mipmap.del_night_i};

    public final static int[] DELETE_IMAGE_MOTE_DAY = {R.mipmap.delete_day_more_a, R.mipmap.delete_day_more_b, R.mipmap.delete_day_more_c, R.mipmap.delete_day_more_d, R.mipmap.delete_day_more_e, R.mipmap.delete_day_more_f, R.mipmap.delete_day_more_g, R.mipmap.delete_day_more_h, R.mipmap.delete_day_more_i};
    public final static int[] DELETE_IMAGE_MOTE_NIGHT = {R.mipmap.delete_night_more_a, R.mipmap.delete_night_more_b, R.mipmap.delete_night_more_c, R.mipmap.delete_night_more_d, R.mipmap.delete_night_more_e, R.mipmap.delete_night_more_f, R.mipmap.delete_night_more_g, R.mipmap.delete_night_more_h, R.mipmap.delete_night_more_i};

    public final static int[] MULTI_IMAGE_DEF = {R.mipmap.multi_a_1, R.mipmap.multi_b_1, R.mipmap.multi_c_1, R.mipmap.multi_d_1, R.mipmap.multi_e_1, R.mipmap.multi_f_1, R.mipmap.multi_g_1, R.mipmap.multi_h_1, R.mipmap.multi_i_1};
    public final static int[] MULTI_IMAGE_DEF_NIGHT = {R.mipmap.multi_a_1_night, R.mipmap.multi_b_1_night, R.mipmap.multi_c_1_night, R.mipmap.multi_d_1_night, R.mipmap.multi_e_1_night, R.mipmap.multi_f_1_night, R.mipmap.multi_g_1_night, R.mipmap.multi_h_1_night, R.mipmap.multi_i_1_night};

    public final static int[] MULTI_IMAGE_RIGHT = {R.mipmap.multi_a_4, R.mipmap.multi_b_4, R.mipmap.multi_c_4, R.mipmap.multi_d_4, R.mipmap.multi_e_4, R.mipmap.multi_f_4, R.mipmap.multi_g_4, R.mipmap.multi_h_4, R.mipmap.multi_i_4};
    public final static int[] MULTI_IMAGE_RIGHT_NIGHT = {R.mipmap.multi_a_4_night, R.mipmap.multi_b_4_night, R.mipmap.multi_c_4_night, R.mipmap.multi_d_4_night, R.mipmap.multi_e_4_night, R.mipmap.multi_f_4_night, R.mipmap.multi_g_4_night, R.mipmap.multi_h_4_night, R.mipmap.multi_i_4_night};

    public final static int[] MULTI_IMAGE_SELECTED = {R.mipmap.multi_a_2, R.mipmap.multi_b_2, R.mipmap.multi_c_2, R.mipmap.multi_d_2, R.mipmap.multi_e_2, R.mipmap.multi_f_2, R.mipmap.multi_g_2, R.mipmap.multi_h_2, R.mipmap.multi_i_2};
    public final static int[] MULTI_IMAGE_SELECTED_NIGHT = {R.mipmap.multi_a_2_night, R.mipmap.multi_b_2_night, R.mipmap.multi_c_2_night, R.mipmap.multi_d_2_night, R.mipmap.multi_e_2_night, R.mipmap.multi_f_2_night, R.mipmap.multi_g_2_night, R.mipmap.multi_h_2_night, R.mipmap.multi_i_2_night};

    public final static int[] MULTI_IMAGE_WRONG = {R.mipmap.multi_a_3, R.mipmap.multi_b_3, R.mipmap.multi_c_3, R.mipmap.multi_d_3, R.mipmap.multi_e_3, R.mipmap.multi_f_3, R.mipmap.multi_g_3, R.mipmap.multi_h_3, R.mipmap.multi_i_3};
    public final static int[] MULTI_IMAGE_WRONG_NIGHT = {R.mipmap.multi_a_3_night, R.mipmap.multi_b_3_night, R.mipmap.multi_c_3_night, R.mipmap.multi_d_3_night, R.mipmap.multi_e_3_night, R.mipmap.multi_f_3_night, R.mipmap.multi_g_3_night, R.mipmap.multi_h_3_night, R.mipmap.multi_i_3_night};

    public static String TINKER_DOWNLOAD_START = "TinkerDownloadStart";
    public static String TINKER_DOWNLOAD_SUCCESS = "TinkerDownloadSuccess";
    public static String TINKER_DOWNLOAD_FAIL = "TinkerDownloadFail";
    public static String TINKER_INSTALL_START = "TinkerInstallStart";
    public static String TINKER_INSTALL_SUCCESS = "TinkerInstallSuccess";
    public static String TINKER_INSTALL_FAIL = "TinkerInstallFAIL";

}
