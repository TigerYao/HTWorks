package com.huatu.handheld_huatu.business.essay.event;


import com.huatu.handheld_huatu.event.BaseMessageEvent;

/**
 *
 */
public class EssayExamMessageEvent extends BaseMessageEvent {

    public static final int EssayExam_start_EssayExamEditAnswer = 10000 * 1;
    public static final int EssayExam_show_soft = EssayExam_start_EssayExamEditAnswer + 1;
    public static final int EssayExam_hide_soft = EssayExam_show_soft + 1;
    public static final int EssayExam_start_EssayExamCheckDetail = EssayExam_hide_soft + 1;
    public static final int EssayExam_activity_finish = EssayExam_start_EssayExamCheckDetail + 1;


    public static final int EssayExam_start_EssayExamMaterials1 = EssayExam_activity_finish + 1;
    public static final int EssayExam_start_EssayExamAnswerDetail = EssayExam_start_EssayExamMaterials1 + 1;
    public static final int EssayExam_start_EssayExamEditAnswer1 = EssayExam_start_EssayExamAnswerDetail + 1;
    public static final int EssayExam_start_CheckToReport = EssayExam_start_EssayExamEditAnswer1 + 1;                       // 批改页点击返回到批改报告页
    public static final int EssayExam_time_heartbeat = EssayExam_start_CheckToReport + 1;                                   // 申论考试每秒计时

    public static final int ESSAYEXAM_AUTO_COMMIT_PAPER = EssayExam_time_heartbeat + 1;
    public static final int ESSAYEXAM_MOCK_AUTO_SAVE_PAPER = ESSAYEXAM_AUTO_COMMIT_PAPER + 1;                               // 申论模考自动保存

    public static final int ESSAYEXAM_essExMaterialsContent_setTextSize = ESSAYEXAM_MOCK_AUTO_SAVE_PAPER + 1;               // 设置字体大小

    public static final int ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW = ESSAYEXAM_essExMaterialsContent_setTextSize + 1;  // 材料页清除问题内容选择
    public static final int ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW = ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW + 1;  // 材料页清除材料内容选择

    public static final int ESSAYEXAM_essExCardViewShow_clearview = ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW + 1;        // 编辑答题页清除选择
    public static final int ESSAYEXAM_essExAnswerContent_clearview = ESSAYEXAM_essExCardViewShow_clearview + 1;             // 答题页清除选择
    public static final int ESSAYEXAM_essExCheckContent_clearview = ESSAYEXAM_essExAnswerContent_clearview + 1;             // 批改页清除选择

    public static final int ESSAYEXAM_RIGHT_ANSWER_SELECT_clearview = ESSAYEXAM_essExCheckContent_clearview + 1;                   // 参考答案选择，取消其他选择

    public static final int ESSAYEXAM_essExMaterialsContent_operator_oc = ESSAYEXAM_RIGHT_ANSWER_SELECT_clearview + 1;

    public static final int ESSAYEXAM_mMaterials_scroll_up = ESSAYEXAM_essExMaterialsContent_operator_oc + 1;
    public static final int ESSAYEXAM_mMaterials_scroll_down = ESSAYEXAM_mMaterials_scroll_up + 1;

    // get data from net
    public static final int EssayExam_get_data_from_net = 11000 * 1;
    public static final int EssayExam_net_getSingleAreaListDetail = EssayExam_get_data_from_net + 1;
    public static final int EssayExam_net_getSingleDataSuccess = EssayExam_net_getSingleAreaListDetail + 1;                 // 申论单题 材料 & 问题 获取成功
    public static final int EssayExam_net_getPaperDataSuccess = EssayExam_net_getSingleDataSuccess + 1;                     // 申论套题 材料 & 问题
    public static final int EssayExam_net_getDataFailed = EssayExam_net_getPaperDataSuccess + 1;                            // 申论 材料 & 问题 获取失败
    public static final int EssayExam_net_createAnswerCard = EssayExam_net_getDataFailed + 1;
    public static final int EssayExam_net_paperCommit = EssayExam_net_createAnswerCard + 1;
    public static final int EssayExam_net_paperCommit_fail = EssayExam_net_paperCommit + 1;
    public static final int EssayExam_net_getMaterialsDownloadUrl = EssayExam_net_paperCommit_fail + 1;
    public static final int EssayExam_net_getCheckCountList = EssayExam_net_getMaterialsDownloadUrl + 1;
    public static final int EssayExam_net_paperSave = EssayExam_net_getCheckCountList + 1;
    public static final int EssayExam_net_paperSave_fail = EssayExam_net_paperSave + 1;
    public static final int EssayExam_net_createAnswerCard_error = EssayExam_net_paperSave_fail + 1;
    public static final int EssayExam_net_setCollectEssay_success = EssayExam_net_createAnswerCard_error + 1;
    public static final int EssayExam_net_setCollectEssay_fail = EssayExam_net_setCollectEssay_success + 1;
    public static final int EssayExam_net_deleteCollectEssay_success = EssayExam_net_setCollectEssay_fail + 1;
    public static final int EssayExam_net_deleteCollectEssay_fail = EssayExam_net_deleteCollectEssay_success + 1;
    public static final int EssayExam_net_checkCollectEssay_success = EssayExam_net_deleteCollectEssay_fail + 1;
    public static final int EssayExam_net_checkCollectEssay_fail = EssayExam_net_checkCollectEssay_success + 1;
    public static final int EssayExam_net_download_essay_success = EssayExam_net_checkCollectEssay_fail + 1;
    public static final int EssayExam_net_delete_my_course_order = EssayExam_net_download_essay_success + 1;
    public static final int EssayExam_net_delete_my_essay_order = EssayExam_net_delete_my_course_order + 1;

    public static final int EssayExam_change_material = EssayExam_net_delete_my_essay_order + 1;                            // 改变材料页卡片
    public static final int EssayExam_SHOW_ESSAY_REPORT = EssayExam_change_material + 1;                                    // 批改报告页点击显示批改详情
    public static final int EssayExam_CHANGE_CORRECT_TYPE_GET = EssayExam_SHOW_ESSAY_REPORT + 1;                            // 申论答题，改变批改方式，通知材料也获取数据，智能 <--> 人工
    public static final int EssayExam_CHANGE_CORRECT_TYPE_GETED = EssayExam_CHANGE_CORRECT_TYPE_GET + 1;                    // 申论答题，改变批改方式，获取数据成功，通知答题页
    public static final int EssayExam_CHANGE_CORRECT_CLEAR_CONTENT = EssayExam_CHANGE_CORRECT_TYPE_GETED + 1;               // 申论答题，改变批改方式，清除答题内容
    public static final int EssayExam_SINGLE_CHANGE_ARED = EssayExam_CHANGE_CORRECT_CLEAR_CONTENT + 1;                      // 申论答题，单题切换地区，保存答案 & 清楚答题页存储的图片信息
    public static final int EssayExam_CONVERT_CORRECT_MODE = EssayExam_SINGLE_CHANGE_ARED + 1;                      // 智能转人工成功

    public EssayExamMessageEvent(int type) {
        this.type = type;
    }

    public EssayExamMessageEvent() {
    }
}
