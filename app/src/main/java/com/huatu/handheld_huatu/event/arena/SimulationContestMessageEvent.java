package com.huatu.handheld_huatu.event.arena;

/**
 */
public class SimulationContestMessageEvent {

    //    public static final int BASE_EVENT_TYPE_2_DATA = SC_MESSAGE_TYPE_1_DATA+1;
    //
    public static final int SC_MSG_TYPE_BASE = 10000 * 1;

    //DATA
    public static final int BASE_EVENT_TYPE_SC_GET_DETAIL_DATA = SC_MSG_TYPE_BASE + 500;
    public static final int BASE_EVENT_TYPE_SC_DETAIL_DATA_ARENA = BASE_EVENT_TYPE_SC_GET_DETAIL_DATA + 1;                              // 获取行测模考大赛成功
    public static final int BASE_EVENT_TYPE_SC_ARENA_SAVE_SUCCESS = BASE_EVENT_TYPE_SC_DETAIL_DATA_ARENA + 1;                     // 行测做题保存成功
    public static final int BASE_EVENT_TYPE_SC_ESSAY_SAVE_SUCCESS = BASE_EVENT_TYPE_SC_ARENA_SAVE_SUCCESS + 1;                          // 申论做题保存成功
    public static final int BASE_EVENT_TYPE_SC_EXAM_COMP_HAS_REPORT_DATA = BASE_EVENT_TYPE_SC_ESSAY_SAVE_SUCCESS + 1;                   // 行测模考交卷成功（有报告）
    public static final int BASE_EVENT_TYPE_SC_EXAM_COMP_NOT_REPORT_DATA = BASE_EVENT_TYPE_SC_EXAM_COMP_HAS_REPORT_DATA + 1;            // 行测模考交卷成功（无报告）
    public static final int BASE_EVENT_TYPE_SC_EXAM_COMP_ESSAY = BASE_EVENT_TYPE_SC_EXAM_COMP_NOT_REPORT_DATA + 1;                      // 申论模考交卷成功
    public static final int BASE_EVENT_TYPE_SC_EXAM_SHARE_DATA = BASE_EVENT_TYPE_SC_EXAM_COMP_ESSAY + 1;
    public static final int BASE_EVENT_TYPE_SC_8_AGAIN_DATA = BASE_EVENT_TYPE_SC_EXAM_SHARE_DATA + 1;

    //    essay  data
    public static final int BASE_EVENT_TYPE_SC_essay_getEssayReport = BASE_EVENT_TYPE_SC_8_AGAIN_DATA + 1;
    public static final int BASE_EVENT_TYPE_SC_essay_getEssayScHistoryList = BASE_EVENT_TYPE_SC_essay_getEssayReport + 1;
    public static final int BASE_EVENT_TYPE_SC_pastPaper = BASE_EVENT_TYPE_SC_essay_getEssayScHistoryList + 1;

    public static final int BASE_EVENT_TYPE_SC_DETAIL_DATA_FAIL_ARENA = BASE_EVENT_TYPE_SC_pastPaper + 1;                               // 获取行测模考大赛失败
    public static final int BASE_EVENT_TYPE_SC_DETAIL_DATA_ESSAY = BASE_EVENT_TYPE_SC_DETAIL_DATA_FAIL_ARENA + 1;                       // 获取申论模考大赛成功
    public static final int BASE_EVENT_TYPE_SC_DETAIL_DATA_FAIL_ESSAY = BASE_EVENT_TYPE_SC_DETAIL_DATA_ESSAY + 1;                       // 获取申论模考大赛失败

    public static final int SHOW_GIFT_DESCRIBE = BASE_EVENT_TYPE_SC_DETAIL_DATA_FAIL_ESSAY + 1;   // 模考大赛列表点击礼包，弹窗
    //获取新的往期模考的tag成功
    public static final int NEW_BASE_EVENT_TYPE_SC_EXAM_HISTORY_TAG_DATA = SHOW_GIFT_DESCRIBE + 1;
    public static final int NEW_BASE_EVENT_TYPE_SC_EXAM_REPORT_DATA = NEW_BASE_EVENT_TYPE_SC_EXAM_HISTORY_TAG_DATA + 1;

    public SimulationContestMessageEvent() {

    }
}
