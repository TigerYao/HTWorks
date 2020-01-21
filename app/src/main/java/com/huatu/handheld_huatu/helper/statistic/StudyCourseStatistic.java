package com.huatu.handheld_huatu.helper.statistic;

import android.text.TextUtils;

import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseBean;
import com.huatu.handheld_huatu.mvpmodel.CourseWorkReportBean;
import com.huatu.handheld_huatu.mvpmodel.Sensor.CourseInfoForStatistic;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.PointSeekBar;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyCourseStatistic {

    public static void deletedCourse(BuyCourseBean.Data data) {
        Map<String, Object> params = new HashMap<>();
        params.put("course_id", data.classId);
        params.put("course_title", data.title);
        params.put("class_id", data.lessonId);
        params.put("class_name", data.title);

    }


    public static void clickStatistic(String tab, String firstModule, String btnName) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_page", tab);
        params.put("first_module", firstModule);
        params.put("button_name", btnName);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_CLICKBUTTON_STATISTIC, params);
    }

    /**
     * 华图在线_app_华图在线_弹出登录界面
     */
    public static void popLoginPage() {
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_POPLOGINPAGE_STATISTIC);
    }

    /**
     * 华图在线_app_华图在线_点击注册或手机验证码登录
     */
    public static void clickSignUpOrPhoneCode() {
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_CLICKSIGNUPORPHONCODE_STATISTIC);
    }

    /**
     * 华图在线_app_华图在线_获取验证码
     *
     * @param user_phone 手机号
     */
    public static void getPhoneCode(String user_phone) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_phone", user_phone);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_GETPHONECODE_STATISTIC, params);
    }

    /**
     * 华图在线_app_华图在线_点击搜索框
     *
     * @param first_module 当前一级模块 题库，课程，学习
     */
    public static void clickSearchBar(String first_module) {
        Map<String, Object> params = new HashMap<>();
        params.put("first_module", first_module);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SEARCHBAR_STATISTIC, params);
    }

    /**
     * 华图在线_app_华图在线_点击课程搜索结果
     *
     * @param search_keyword       关键词
     * @param page_number          搜索结果页面序号
     * @param click_number         点击序列编号
     * @param course_id            课程id
     * @param course_title         课程名称
     * @param is_set_meal          是否套餐课
     * @param is_collection        是否合集课
     * @param is_free              是否免费
     * @param teacher_id           老师id
     * @param teacher_name         老师名字
     * @param course_kind          课程所属科目
     * @param course_price         课程原价
     * @param course_collage_price 课程拼团价
     * @param discount_price       课程现价
     */
    public static void clickCourseSearchResult(String search_keyword, String click_number, int page_number, String course_id, String course_title,
                                               boolean is_set_meal, boolean is_collection, boolean is_free, List<String> teacher_id,
                                               List<String> teacher_name, String course_kind, float course_price, float course_collage_price,
                                               float discount_price) {
        Map<String, Object> params = new HashMap<>();
        params.put("search_keyword", search_keyword);
        params.put("click_number", click_number);
        params.put("page_number", page_number);
        params.put("course_id", course_id);
        params.put("course_title", course_title);
        params.put("is_set_meal", is_set_meal);
        params.put("is_collection", is_collection);
        params.put("is_free", is_free);
        params.put("teacher_id", teacher_id);
        params.put("teacher_name", teacher_name);
        params.put("course_kind", course_kind);
        params.put("course_price", course_price);
        params.put("course_collage_price", course_collage_price);
        params.put("discount_price", discount_price);

        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SEARCHRESULTTESTCOURSE_STATISTIC, params);
    }

    /**
     * 华图在线_app_华图在线_点击试题搜索结果
     *
     * @param test_id        试题id
     * @param search_keyword 关键词
     * @param page_number    搜索结果页面序号
     * @param click_number   点击序列编号
     */
    public static void clickTiKuSearchResult(String test_id, String search_keyword, int page_number, String click_number) {
        Map<String, Object> params = new HashMap<>();
        params.put("test_id", test_id);
        params.put("search_keyword", search_keyword);
        params.put("page_number", page_number);
        params.put("click_number", click_number);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SEARCHRESULTTEST_STATISTIC, params);
    }


    /**
     * 华图在线_app_华图在线_申论批改点击购买批改次数
     *
     * @param standard_answer_remaining  标准答案批改剩余次数
     * @param questions_remaining        套题批改剩余次数
     * @param discussion_paper_remaining 文章写作批改剩余次数
     */
    public static void clickEssayCheckCount(int standard_answer_remaining, int questions_remaining, int discussion_paper_remaining) {
        Map<String, Object> params = new HashMap<>();
        params.put("standard_answer_remaining", standard_answer_remaining);
        params.put("questions_remaining", questions_remaining);
        params.put("discussion_paper_remaining", discussion_paper_remaining);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_CLICKBUYAPPLICATION_STATISTIC, params);
    }

    /**
     * 华图在线_app_华图在线_申论批改立即购买（点击立即购买）
     *
     * @param standard_answer_buy 标准答案批改购买次数
     * @param questions_buy       套题批改购买次数
     * @param discussion_buy      文章写作批改购买次数
     * @param payment_method      支付方式
     * @param order_amount        订单金额
     * @param page_source         前项来源申论购买三个入口：购买申论批改页，试题列表页，申论交卷页
     */
    public static void clickEssayCheckBuy(int standard_answer_buy, int questions_buy, int discussion_buy, String payment_method, float order_amount, String page_source) {

        Map<String, Object> params = new HashMap<>();
        params.put("standard_answer_buy", standard_answer_buy);
        params.put("questions_buy", questions_buy);
        params.put("discussion_buy", discussion_buy);
        params.put("payment_method", payment_method);
        params.put("order_amount", order_amount);
        params.put("page_source", page_source);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_BUYAPPLICATION_STATISTIC, params);
    }

    /**
     * 华图在线_app_华图在线_申论批改确认支付(购买成功)
     *
     * @param order_id            订单ID
     * @param standard_answer_buy 标准答案批改购买次数
     * @param questions_buy       套题批改购买次数
     * @param discussion_buy      文章写作批改购买次数
     * @param payment_method      支付方式
     * @param order_amount        订单金额
     * @param payment_amount      实际支付金额
     */
    public static void clickEssayCheckBuySucceed(String order_id, int standard_answer_buy, int questions_buy, int discussion_buy, String payment_method, float order_amount, float payment_amount) {
        Map<String, Object> params = new HashMap<>();
        params.put("order_id", order_id);
        params.put("standard_answer_buy", standard_answer_buy);
        params.put("questions_buy", questions_buy);
        params.put("discussion_buy", discussion_buy);
        params.put("payment_method", payment_method);
        params.put("order_amount", order_amount);
        params.put("payment_amount", payment_amount);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_BUYAPPLICATIONSUCCEED_STATISTIC, params);
    }

    /**
     * 华图在线_app_华图在线_点击申论试题
     *
     * @param on_module  所在模块	 字符串	套题，文章写作，标准答案
     * @param exam_title 试卷名称	字符串	如果是套题的话，上传名称和id，文章写作和标准答案直接上传试题id，不传名称
     * @param exam_id    试卷ID	字符串
     */
    //套题
    public static void clickEssayMulti(String on_module, String exam_title, String exam_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("exam_title", exam_title);
        params.put("exam_id", exam_id);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HuaTuOnline_app_HuaTuOnline_CilckApplicationExam, params);
    }

    //标准答案和议论
    public static void clickEssaySingle(String on_module, String exam_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("exam_id", exam_id);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HuaTuOnline_app_HuaTuOnline_CilckApplicationExam, params);
    }

    /**
     * HuaTuOnline_app_HuaTuOnline_ClickInform
     * 华图在线_app_华图在线_查看消息
     *
     * @param page_source  前向来源     消息推送，消息列表
     * @param inform_type  消息类型     课程通知，反馈通知，平台类型，物流通知
     * @param inform_title 消息名称
     */
    public static void checkMessage(String page_source, String inform_type, String inform_title) {
        Map<String, Object> params = new HashMap<>();
        params.put("page_source", page_source);
        params.put("inform_type", inform_type);
        params.put("inform_title", inform_title);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HuaTuOnline_app_HuaTuOnline_ClickInform, params);
    }


    /**
     * 华图在线_app_pc_华图在线_浏览课程详情
     *
     * @param page_source 前向来源
     */
    public static void clickCourse(String page_source, String classId) {
        Map<String, Object> params = new HashMap<>();
        params.put("page_source", page_source);
        sendTrackWithCourseInfo(SensorStatisticHelper.SENSORS_CLICKCOURSE_STATISTIC, classId, params);
    }

    /**
     * 华图在线_app_pc_华图在线_购买课程（点击立即购买）
     *
     * @param page_source
     * @param classId
     * @param payment_amount
     */
    public static void buyCourse(String page_source, String classId, String payment_amount) {
        Map<String, Object> params = new HashMap<>();
        params.put("page_source", page_source);
        params.put("payment_amount", payment_amount);
        sendTrackWithCourseInfo(SensorStatisticHelper.SENSORS_BUYCOURSE_STATISTIC, classId, params);
    }

    /**
     * 华图在线_app_pc_华图在线_取消订单
     *
     * @param order_id
     * @param classId
     * @param page_source
     * @param pay_time
     * @param receiver_address
     * @param receiver_name
     * @param payment_amount
     */

    public static void cancelOrder(String order_id, String classId, String page_source, String pay_time, String receiver_address, String receiver_name, String payment_amount) {
        Map<String, Object> params = new HashMap<>();
        params.put("page_source", page_source);
        params.put("pay_time", pay_time);
        params.put("receiver_address", receiver_address);
        params.put("receiver_name", receiver_name);
        params.put("order_id", order_id);
        params.put("payment_amount", payment_amount);
        sendTrackWithCourseInfo(SensorStatisticHelper.SENSORS_CANCELORDER_STATISTIC, classId, params);
    }

    /**
     * 华图在线_app_pc_华图在线_模考大赛我要报名
     *
     * @param page_source
     * @param match_id
     * @param match_title
     * @param match_subject
     * @param match_class
     */

    public static void simulatedSignUp(String page_source, String match_id, String match_title, String match_subject, String match_class) {
        Map<String, Object> params = new HashMap<>();
        params.put("page_source", page_source);
        params.put("match_id", match_id);
        params.put("match_title", match_title);
        params.put("match_subject", match_subject);
        params.put("match_class", match_class);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDSIGNUP_STATISTIC, params);
    }

    /**
     * 华图在线_app_pc_华图在线_模考大赛答题中间操作
     *
     * @param collect_operation 继续答题、退出
     * @param match_subject     申论
     * @param match_class       公务员，教师
     * @param match_id
     * @param match_title
     */
    public static void simulatedOperation(String collect_operation, String match_subject, String match_class, String match_id, String match_title) {
        Map<String, Object> params = new HashMap<>();
        params.put("collect_operation", collect_operation);
        params.put("match_subject", match_subject);
        params.put("match_class", match_class);
        params.put("match_id", match_id);
        params.put("match_title", match_title);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDOPERATION_STATISTIC, params);
    }

    /**
     * 华图在线_app_pc_华图在线_模考大赛查看报告
     *
     * @param match_subject
     * @param match_class
     * @param match_id
     * @param match_title
     * @param match_score           得分
     * @param match_correct_number  正确数
     * @param match_answer_duration 答题时长
     */
    public static void simulatedReport(String match_subject, String match_class, String match_id, String match_title,
                                       double match_score, int match_correct_number, long match_answer_duration) {
        Map<String, Object> params = new HashMap<>();
        params.put("match_subject", match_subject);
        params.put("match_class", match_class);
        params.put("match_id", match_id);
        params.put("match_title", match_title);
        params.put("match_score", match_score);
        params.put("match_correct_number", match_correct_number);
        params.put("match_answer_duration", match_answer_duration);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDVIEWREPORT_STATISTIC, params);
    }

    /**
     * 华图在线_app_pc_华图在线_查看错题解析
     *
     * @param on_module
     * @param match_subject
     * @param match_class
     * @param match_id
     * @param match_title
     * @param test_answer_duration 做题时长
     * @param test_correct_rate    正确率
     */
    public static void simulatedShowWrong(String on_module, String match_subject, String match_class, String match_id, String match_title,
                                          long test_answer_duration, double test_correct_rate) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("match_subject", match_subject);
        params.put("match_class", match_class);
        params.put("match_id", match_id);
        params.put("match_title", match_title);
        params.put("test_answer_duration", test_answer_duration);
        params.put("test_correct_rate", test_correct_rate);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDVIEWPARSING_STATISTIC, params);
    }

    /**
     * 华图在线_app_pc_华图在线_查看全部解析
     *
     * @param on_module
     * @param match_subject
     * @param match_class
     * @param match_id
     * @param match_title
     * @param test_answer_duration 做题时长
     * @param test_correct_rate    正确率
     */
    public static void simulatedShowAll(String on_module, String match_subject, String match_class, String match_id, String match_title,
                                        long test_answer_duration, double test_correct_rate) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("match_subject", match_subject);
        params.put("match_class", match_class);
        params.put("match_id", match_id);
        params.put("match_title", match_title);
        params.put("test_answer_duration", test_answer_duration);
        params.put("test_correct_rate", test_correct_rate);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDVIEWALLPARSING_STATISTIC, params);
    }

    /**
     * 华图在线_app_pc_华图在线_分享成绩
     *
     * @param on_module
     * @param match_subject
     * @param match_class
     * @param match_id
     * @param match_title
     * @param match_score   得分
     * @param share_type    分享渠道
     */
    public static void simulatedShareResult(String on_module, String match_subject, String match_class, String match_id, String match_title,
                                            double match_score, String share_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("match_subject", match_subject);
        params.put("match_class", match_class);
        params.put("match_id", match_id);
        params.put("match_title", match_title);
        params.put("match_score", match_score);
        params.put("share_type", share_type);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDSHARERESULT_STATISTIC, params);
    }

    /**
     * 华图在线_app_pc_华图在线_分享题目
     *
     * @param on_module        所在模块
     * @param test_id          试题ID
     * @param test_first_cate  第一知识点
     * @param test_second_cate 第二知识点
     * @param test_third_cate  第三知识点
     * @param share_type       分享渠道
     */
    public static void simulatedShareTest(String on_module, String test_id, String test_first_cate, String test_second_cate, String test_third_cate, String share_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("test_id", test_id);
        params.put("test_first_cate", test_first_cate);
        params.put("test_second_cate", test_second_cate);
        params.put("test_third_cate", test_third_cate);
        params.put("share_type", share_type);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_SHARETEST, params);
    }

    /**
     * 华图在线_app_华图在线_刷题设置
     *
     * @param on_module       模块 专项练习、错题练习
     * @param setting_numbers 数量
     * @param setting_mode    做题模式、背题模式
     */
    public static void doExercisesSetting(String on_module, String setting_numbers, String setting_mode) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("setting_numbers", setting_numbers);
        params.put("setting_mode", setting_mode);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_DOEXERCISESSETTING, params);
    }

    /**
     * 华图在线_app_pc_华图在线_阶段测试开始考试
     *
     * @param course_id    课程Id
     * @param course_title 课程名称
     * @param class_id     课时Id
     * @param class_title  课时名称
     * @param stage_id     阶段测试Id
     * @param stage_title  阶段测试名称
     */
    public static void stageStart(String course_id, String course_title, String class_id, String class_title, String stage_id, String stage_title) {
        Map<String, Object> params = new HashMap<>();
        params.put("course_id", course_id);
        params.put("course_title", course_title);
        params.put("class_id", class_id);
        params.put("class_title", class_title);
        params.put("stage_id", stage_id);
        params.put("stage_title", stage_title);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_STAGETESTSTART, params);
    }

    /**
     * 华图在线_app_pc_华图在线_阶段测试开始考试
     *
     * @param course_id         课程Id
     * @param course_title      课程名称
     * @param stage_id          阶段测试Id
     * @param stage_title       课时名称
     * @param correct_number    正确数量
     * @param exercise_done     已做题目数量
     * @param exercise_duration 用时
     * @param exercise_number   总题量
     */
    public static void stageReport(String course_id, String course_title, String stage_id, String stage_title, int correct_number, int exercise_done, int exercise_duration, int exercise_number) {
        Map<String, Object> params = new HashMap<>();
        params.put("course_id", course_id);
        params.put("course_title", course_title);
        params.put("stage_id", stage_id);
        params.put("stage_title", stage_title);
        params.put("correct_number", correct_number);
        params.put("exercise_done", exercise_done);
        params.put("exercise_duration", exercise_duration);
        params.put("exercise_number", exercise_number);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_STAGETESTREPORT, params);
    }

    /**
     * 点击申论答题按钮
     *
     * @param on_module  所在模块 套题，文章写作，标准答案
     * @param exam_title 如果是套题的话，上传名称和id，文章写作和标准答案直接上传试题id，不传名称
     * @param exam_id    试卷Id
     */
    public static void clickEssayAnswer(String on_module, String exam_title, String exam_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("exam_title", exam_title);
        params.put("exam_id", exam_id);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_CLICKAPPLICATIONANSWER, params);
    }

    /**
     * 输入申论答案（不做了）
     *
     * @param on_module  所在模块 套题，文章写作，标准答案
     * @param exam_title 如果是套题的话，上传名称和id，文章写作和标准答案直接上传试题id，不传名称
     * @param exam_id    试卷Id
     */
    public static void inputEssayAnswer(String on_module, String exam_title, String exam_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("exam_title", exam_title);
        params.put("exam_id", exam_id);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_INPUTAPPLICATIONANSWER, params);
    }

    /**
     * 查看申论材料
     *
     * @param on_module  所在模块 套题，文章写作，标准答案
     * @param exam_title 如果是套题的话，上传名称和id，文章写作和标准答案直接上传试题id，不传名称
     * @param exam_id    试卷Id
     */
    public static void viewEssayMaterial(String on_module, String exam_title, String exam_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("exam_title", exam_title);
        params.put("exam_id", exam_id);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_CLICKAPPLICATIONDATUM, params);
    }

    /**
     * 提交申论答案
     *
     * @param on_module  所在模块 套题，文章写作，标准答案
     * @param exam_title 如果是套题的话，上传名称和id，文章写作和标准答案直接上传试题id，不传名称
     * @param exam_id    试卷Id
     */
    public static void submitEssayAnswer(String on_module, String exam_title, String exam_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("exam_title", exam_title);
        params.put("exam_id", exam_id);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_APPLICATIONCLICKSUBMIT, params);
    }

    /**
     * 提交申论答案成功
     *
     * @param on_module  所在模块 套题，文章写作，标准答案
     * @param exam_title 如果是套题的话，上传名称和id，文章写作和标准答案直接上传试题id，不传名称
     * @param exam_id    试卷Id
     */
    public static void cubmitEssaysuccess(String on_module, String exam_title, String exam_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("on_module", on_module);
        params.put("exam_title", exam_title);
        params.put("exam_id", exam_id);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_APPLICATIONSUBMITSUCCEED, params);
    }

    /**
     * 小模考开始考试
     *
     * @param small_id   小模考Id
     * @param small_name 小模考名称
     */
    public static void startSmallMatch(String small_id, String small_name) {
        Map<String, Object> params = new HashMap<>();
        params.put("small_id", small_id);
        params.put("small_name", small_name);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_SMALLSIMULATESTART, params);
    }

    /**
     * 查看小模考报告
     *
     * @param small_id          小模考Id
     * @param small_name        小模考名称
     * @param correct_number    正确题数量
     * @param exercise_done     做了几道
     * @param exercise_duration 用时
     * @param exercise_number   总题量
     */
    public static void smallMatchReport(String small_id, String small_name, int correct_number, int exercise_done, int exercise_duration, int exercise_number) {
        Map<String, Object> params = new HashMap<>();
        params.put("small_id", small_id);
        params.put("small_name", small_name);
        params.put("correct_number", correct_number);
        params.put("exercise_done", exercise_done);
        params.put("exercise_duration", exercise_duration);
        params.put("exercise_number", exercise_number);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_SMALLSIMULATEREPORT, params);
    }


    // 后端做
//    /**
//     * 华图在线_app_pc_华图在线_模考大赛确认报名
//     * @param is_first_sign_up
//     * @param match_id
//     * @param match_title
//     * @param exam_type
//     * @param match_subject
//     * @param match_class ff3f47
//     */
//
//    public static void simulatedSureSignUp(boolean is_first_sign_up, String sign_up_city,String match_id, String match_title, String exam_type, String match_subject, String match_class){
//        Map<String, Object> params = new HashMap<>();
//        params.put("is_first_sign_up", is_first_sign_up);
//        params.put("sign_up_city", sign_up_city);
//        params.put("match_id", match_id);
//        params.put("match_title",match_title);
//        params.put("exam_type", exam_type);
//        params.put("match_subject", match_subject);
//        params.put("match_class", match_class);
//        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDSURESIGNUP_STATISTIC, params);
//    }
//
//    /**
//     * 华图在线_app_pc_华图在线_模考大赛开始答题
//     * @param match_id
//     * @param match_title
//     * @param exam_type
//     * @param match_subject
//     * @param match_class
//     */
//
//    public static void simulatedStartAnswer(boolean is_first_answer, String match_id, String match_title, String exam_type, String match_subject, String match_class){
//        Map<String, Object> params = new HashMap<>();
//        params.put("is_first_answer", is_first_answer);
//        params.put("match_id", match_id);
//        params.put("match_title",match_title);
//        params.put("exam_type", exam_type);
//        params.put("match_subject", match_subject);
//        params.put("match_class", match_class);
//        SensorStatisticHelper.sendTrack(SensorStatisticHelper.SENSORS_SIMULATEDSURESIGNUP_STATISTIC, params);
//    }


    /**
     * 分享课程
     *
     * @param classId param shareType
     */
    public static void shareCourse(String classId, SHARE_MEDIA shareMedia) {
        Map<String, Object> params = new HashMap<>();
        String shareType = "";
        switch (shareMedia) {
            case QQ:
                shareType = "QQ";
                break;
            case WEIXIN:
                shareType = "微信";
                break;
            case WEIXIN_CIRCLE:
                shareType = "朋友圈";
                break;
            case SINA:
                shareType = "微博";
                break;
            case SMS:
                shareType = "复制链接";
                break;
        }
        params.put("share_type", shareType);
        sendTrackWithCourseInfo(SensorStatisticHelper.HUATUONLINE_APP_HUATUONLINE_SHARECOURSE, classId, params);
    }

    /**
     * 课程详情咨询客服
     *
     * @param classId
     */
    public static void consultServer(String classId) {
        Map<String, Object> params = new HashMap<>();
        sendTrackWithCourseInfo(SensorStatisticHelper.HUATUONLINE_APP_HUATUONLINE_CONSULTSERVER, classId, params);
    }

    public static void sendTrackWithCourseInfo(final String type, String classId, final Map<String, Object> params) {
        ServiceProvider.getSensorsStatistic(classId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                CourseInfoForStatistic courseInfoForStatistic = (CourseInfoForStatistic) model.data;
                if (!TextUtils.isEmpty(courseInfoForStatistic.course_id))
                    params.put("course_id", courseInfoForStatistic.course_id);
                if (!TextUtils.isEmpty(courseInfoForStatistic.class_name))
                    params.put("class_name", courseInfoForStatistic.class_name);
                params.put("is_set_meal", courseInfoForStatistic.is_set_meal);
                params.put("is_collection", courseInfoForStatistic.is_collection);
                params.put("is_free", courseInfoForStatistic.is_free);
                if (!TextUtils.isEmpty(courseInfoForStatistic.course_title))
                    params.put("course_title", courseInfoForStatistic.course_title);
                params.put("course_price", courseInfoForStatistic.course_price);
                if (!TextUtils.isEmpty(courseInfoForStatistic.course_kind))
                    params.put("course_kind", courseInfoForStatistic.course_kind);
                params.put("course_collage_price", courseInfoForStatistic.course_collage_price);
                params.put("discount_price", courseInfoForStatistic.discount_price);
                if (courseInfoForStatistic.teacher_id != null)
                    params.put("teacher_id", courseInfoForStatistic.teacher_id);
                if (courseInfoForStatistic.teacher_name != null)
                    params.put("teacher_name", courseInfoForStatistic.teacher_name);
                SensorStatisticHelper.sendTrack(type, params);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    public static void sendDownloadCourseTrack(CourseInfoForStatistic courseInfoForStatistic, CourseWareInfo courseWareInfo, boolean isAdd) {

        Map<String, Object> params = new HashMap<>();
        if (!TextUtils.isEmpty(courseInfoForStatistic.course_id))
            params.put("course_id", courseInfoForStatistic.course_id);
        if (!TextUtils.isEmpty(courseInfoForStatistic.class_name))
            params.put("class_name", courseInfoForStatistic.class_name);
        params.put("is_set_meal", courseInfoForStatistic.is_set_meal);
        params.put("is_collection", courseInfoForStatistic.is_collection);
        params.put("is_free", courseInfoForStatistic.is_free);
        if (!TextUtils.isEmpty(courseInfoForStatistic.course_title))
            params.put("course_title", courseInfoForStatistic.course_title);
        params.put("course_price", courseInfoForStatistic.course_price);
        if (!TextUtils.isEmpty(courseInfoForStatistic.course_kind))
            params.put("course_kind", courseInfoForStatistic.course_kind);
        params.put("course_collage_price", courseInfoForStatistic.course_collage_price);
        params.put("discount_price", courseInfoForStatistic.discount_price);
        if (courseInfoForStatistic.teacher_id != null)
            params.put("teacher_id", courseInfoForStatistic.teacher_id);
        if (courseInfoForStatistic.teacher_name != null)
            params.put("teacher_name", courseInfoForStatistic.teacher_name);


        params.put("class_id", String.valueOf(courseWareInfo.classId));
        params.put("class_name", courseWareInfo.title);
        params.put("collect_operation", isAdd ? "下载" : "取消下载");

        SensorStatisticHelper.sendTrack("HuaTuOnline_app_pc_HuaTuOnline_DownloadCourse", params);
    }

    //筛选设置
    public static void sendFilterSettingTrack(String teacher, String examination_type, String price_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("teacher", teacher);
        params.put("examination_type", examination_type);
        params.put("price_type", price_type);

        SensorStatisticHelper.sendTrack("HuaTuOnline_app_HuaTuOnline_FilterSetting", params);
    }

    //课后作业开始作答
    public static void sendStartAfterWork(String courseID, String courseTitle, String classID, String classTitle) {
        Map<String, Object> params = new HashMap<>();
        params.put("course_id", courseID);
        params.put("course_title", courseTitle);
        params.put("class_id", classID);
        params.put("class_title", classTitle);

        //tesk_title
        SensorStatisticHelper.sendTrack("HuaTuOnline_app_pc_HuaTuOnline_AfterCourseStart", params);
    }

    public static void sendLookAfterWorkReport(String courseID, String courseTitle, String classID, String classTitle, CourseWorkReportBean reportBean) {
        Map<String, Object> params = new HashMap<>();
        params.put("report_type", "课后作业报告");
        params.put("course_id", courseID);
        params.put("course_title", courseTitle);
        params.put("class_id", classID);
        params.put("class_title", classTitle);

        params.put("correct_number", reportBean.rcount);//正确题目数
        params.put("exercise_done", reportBean.tcount - reportBean.ucount);//已做题目数
        params.put("exercise_number", reportBean.tcount);//总题目数
        params.put("is_update_view", false);//是否更新后查看            仅限学习报告，课后作业直接写死为否
        params.put("exercise_duration", reportBean.timesTotal);//作答用时

        SensorStatisticHelper.sendTrack("HuaTuOnline_app_pc_HuaTuOnline_ViewReport", params);
    }


    //随堂练提交答案
    public static void sendInclassWorkReport(String courseID, String courseTitle, String classID, String classTitle, PointSeekBar.Point reportBean, boolean hasFinish, int exerciseNum) {
        Map<String, Object> params = new HashMap<>();

        params.put("course_id", courseID);
        params.put("course_title", courseTitle);
        params.put("class_id", classID);
        params.put("class_title", classTitle);

        params.put("topic_id", String.valueOf(reportBean.id));//题目ID
        params.put("is_correct", reportBean.correct == 1);//是否正确
        params.put("exercise_duration", reportBean.time);//作答用时
        params.put("is_finish", hasFinish);//是否完成答题            该随堂练下习题全部做了，即为完成答题
        params.put("exercise_number", exerciseNum);//总题目数

        SensorStatisticHelper.sendTrack("HuaTuOnline_app_pc_HuaTuOnline_RecordedAnswerSubmitSucceed", params);
    }

    // 首页Banner图点击埋点
    public static void sendHomeBanner(String banner_id, String banner_name, String to_exam_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("banner_id", banner_id);
        params.put("banner_name", banner_name);
        params.put("to_exam_type", to_exam_type);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_CLICKBANNER, params);
    }

    // 错题导出，选择错题数量 selected_number 选择数量
    public static void selectExErrQuestion(int selected_number) {
        Map<String, Object> params = new HashMap<>();
        params.put("selected_number", selected_number);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_SELECTQUESTION, params);
    }

    // 错题导出，确认导出 export_number 导出数量
    public static void comfirmExportError(int export_number) {
        Map<String, Object> params = new HashMap<>();
        params.put("export_number", export_number);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_CLICKEXPORT, params);
    }

    /**
     * 错题导出，导出成功
     *
     * @param export_number 导出错题数量
     * @param coins_number  消耗金币数
     */
    public static void exportErrorSucceed(int export_number, int coins_number) {
        Map<String, Object> params = new HashMap<>();
        params.put("export_number", export_number);
        params.put("coins_number", coins_number);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_EXPORTSUCCEED, params);
    }

    /**
     * 错题导出，点击下载
     *
     * @param download_number 下载数量
     * @param download_id     试卷id
     * @param download_type   下载类型 错题/试卷
     */
    public static void downloadExErr(int download_number, String download_id, String download_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("download_number", download_number);
        params.put("download_id", download_id);
        params.put("download_type", download_type);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_CLICKDOWNLOAD, params);
    }

    /**
     * 错题导出，下载成功
     *
     * @param download_number 下载数量
     * @param download_id     试卷id
     * @param download_type   下载类型 错题/试卷
     */
    public static void downloadExErrSucceed(int download_number, String download_id, String download_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("download_number", download_number);
        params.put("download_id", download_id);
        params.put("download_type", download_type);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUATUONLINE_APP_PC_HUATUONLINE_DOWNLOADSUCCEED, params);
    }

    public static void openPushMsg(String msgId, String msgTitle) {
        Map<String, Object> params = new HashMap<>();
        params.put("msg_title", msgTitle);
        params.put("msg_id", msgId);
        SensorStatisticHelper.sendTrack(SensorStatisticHelper.HUAATUONLINE_APP_OPENNOTIFICATION, params);

    }
}