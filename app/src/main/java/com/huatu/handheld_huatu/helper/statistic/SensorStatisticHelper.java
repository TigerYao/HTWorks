package com.huatu.handheld_huatu.helper.statistic;

import android.util.Log;

import com.huatu.handheld_huatu.mvpmodel.Sensor.BaseEvent;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by cjx on 2018\12\28 0028.
 */

public class SensorStatisticHelper {

    public static final String SENSORS_CLICKBUTTON_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_CilckButton";//按钮点击
    public static final String SENSORS_CLICKCOURSE_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_ClickCourse";//浏览课程详情
    public static final String SENSORS_BUYCOURSE_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_BuyCourse";//点击立即购买
    public static final String SENSORS_CANCELORDER_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_CancelOrder";//取消订单
    //登录
    public static final String SENSORS_POPLOGINPAGE_STATISTIC = "HuaTuOnline_app_HuaTuOnline_PopLoginPage";//弹出登录界面
    public static final String SENSORS_CLICKSIGNUPORPHONCODE_STATISTIC = "HuaTuOnline_app_HuaTuOnline_ClickSignUpOrPhoneCode";//点击注册或手机验证码登录
    public static final String SENSORS_GETPHONECODE_STATISTIC = "HuaTuOnline_app_HuaTuOnline_GetPhoneCode";//华图在线_app_华图在线_app获取验证码


    //购买申论批改
    public static final String SENSORS_CLICKBUYAPPLICATION_STATISTIC = "HuaTuOnline_app_HuaTuOnline_ClickBuyApplication";//申论批改点击购买批改次数
    public static final String SENSORS_BUYAPPLICATION_STATISTIC = "HuaTuOnline_app_HuaTuOnline_BuyApplication";//申论批改立即购买
    public static final String SENSORS_BUYAPPLICATIONSUCCEED_STATISTIC = "HuaTuOnline_app_HuaTuOnline_BuyApplicationSucceed";//申论批改确认支付
    //点击申论试题
    public static final String HuaTuOnline_app_HuaTuOnline_CilckApplicationExam = "HuaTuOnline_app_HuaTuOnline_CilckApplicationExam";//点击申论试题
    //查看消息
    public static final String HuaTuOnline_app_HuaTuOnline_ClickInform = "HuaTuOnline_app_HuaTuOnline_ClickInform";//查看消息


    //搜索
    public static final String SENSORS_SEARCHBAR_STATISTIC = "HuaTuOnline_app_HuaTuOnline_SearchBar";//点击搜索框
    public static final String SENSORS_SEARCHRESULTTEST_STATISTIC = "HuaTuOnline_app_HuaTuOnline_SearchResultTest";//点击试题搜索结果
    public static final String SENSORS_SEARCHRESULTTESTCOURSE_STATISTIC = "HuaTuOnline_app_HuaTuOnline_SearchResultCourse";//点击课程搜索结果


    //----模考大赛
    public static final String SENSORS_SIMULATEDSIGNUP_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedSignUp";//模考大赛我要报名
    // 后端做
//    public static final String SENSORS_SIMULATEDSURESIGNUP_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedSureSignUp";//模考大赛确认报名
//    public static final String SENSORS_SIMULATEDSTARTANSWER_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedStartAnswer";//模考大赛开始答题
//    public static final String SENSORS_SIMULATEDENDANSWER_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedEndAnswer";//模考大赛结束答题
    public static final String SENSORS_SIMULATEDOPERATION_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedOperation";//模考大赛答题中间操作
    public static final String SENSORS_SIMULATEDVIEWREPORT_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedViewReport";//模考大赛查看报告
    public static final String SENSORS_SIMULATEDVIEWPARSING_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedViewParsing";//查看错题解析
    public static final String SENSORS_SIMULATEDVIEWALLPARSING_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedViewAllParsing";//查看全部解析
    public static final String SENSORS_SIMULATEDSHARERESULT_STATISTIC = "HuaTuOnline_app_pc_HuaTuOnline_SimulatedShareResult";//分享模考大赛成绩

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_COLLECTTEST = "HuaTuOnline_app_pc_HuaTuOnline_CollectTest";//收藏题目
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_SHARETEST = "HuaTuOnline_app_pc_HuaTuOnline_ShareTest";//分享题目

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_DOEXERCISESSETTING = "HuaTuOnline_app_HuaTuOnline_DoExercisesSetting";//刷题设置

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_STARTWATCHCOURSEVIDEO = "HuaTuOnline_app_pc_HuaTuOnline_StartWatchCourseVideo";//开始观看
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_FINISHWATCHCOURSEVIDEO = "HuaTuOnline_app_pc_HuaTuOnline_FinishWatchCourseVideo";//结束观看
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_WATCHVIDEOOPERATION = "HuaTuOnline_app_pc_HuaTuOnline_WatchVideoOperation";//观看视频操作
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_ENDSPEEDWATCH = "HuaTuOnline_app_pc_HuaTuOnline_EndSpeedWatch";//结束视频快进
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_STICKCOURSE = "HuaTuOnline_app_pc_HuaTuOnline_StickCourse";//置顶课程
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_DOWNLOADCOURSE = "HuaTuOnline_app_pc_HuaTuOnline_DownloadCourse";//下载课程
    public static final String HUATUONLINE_APP_HUATUONLINE_POPLOGINPAGE = "HuaTuOnline_app_HuaTuOnline_PopLoginPage";//弹出登录界面
    public static final String HUATUONLINE_APP_HUATUONLINE_LOGINSUCCEED = "HuaTuOnline_app_HuaTuOnline_LoginSucceed";//登录成功
    public static final String HUATUONLINE_APP_HUATUONLINE_CLICKSIGNUPORPHONECODE = "HuaTuOnline_app_HuaTuOnline_ClickSignUpOrPhoneCode";//登录
    public static final String HUATUONLINE_APP_HUATUONLINE_GETPHONECODE = "HuaTuOnline_app_HuaTuOnline_GetPhoneCode";//获取验证码
    public static final String HUATUONLINE_APP_HUATUONLINE_BUYFREE = "HuaTuOnline_app_HuaTuOnline_BuyFree";//购买免费课程
    public static final String HUATUONLINE_APP_HUATUONLINE_CONSULTSERVER = "HuaTuOnline_app_HuaTuOnline_ConsultServer";//咨询客服
    public static final String HUATUONLINE_APP_HUATUONLINE_CLICKREDPACKET = "HuaTuOnline_app_HuaTuOnline_ClickRedPacket";//点击开红包
    public static final String HUATUONLINE_APP_HUATUONLINE_SHAREREDPACKET = "HuaTuOnline_app_HuaTuOnline_ShareRedPacket";//分享红包

    public static final String HUATUONLINE_APP_HUATUONLINE_PLACEORDERDETAIL = "HuaTuOnline_app_HuaTuOnline_PlaceOrderDetail";//提交订单详情
    public static final String HUATUONLINE_APP_HUATUONLINE_BUYSUCCEEDDETAIL = "HuaTuOnline_app_HuaTuOnline_BuySucceedDetail";//确认付款详情
    public static final String HUATUONLINE_APP_HUATUONLINE_SEARCHBAR = "HuaTuOnline_app_HuaTuOnline_SearchBar";//点击搜索框
    public static final String HUATUONLINE_APP_HUATUONLINE_SEARCHPLEASE = "HuaTuOnline_app_HuaTuOnline_SearchPlease";//发送搜索请求
    public static final String HUATUONLINE_APP_HUATUONLINE_SEARCHRESULTTEST = "HuaTuOnline_app_HuaTuOnline_SearchResultTest";//点击试题搜索结果
    public static final String HUATUONLINE_APP_HUATUONLINE_SEARCHRESULTCOURSE = "HuaTuOnline_app_HuaTuOnline_SearchResultCourse";//课程搜索结果
    public static final String HUATUONLINE_APP_HUATUONLINE_SHARECOURSE = "HuaTuOnline_app_HuaTuOnline_ShareCourse";//分享课程
    public static final String HUATUONLINE_APP_HUATUONLINE_EVALUATECOURSE = "HuaTuOnline_app_HuaTuOnline_EvaluateCourse";//提交课件评价
    public static final String HUATUONLINE_APP_HUATUONLINE_DELETECOURSE = "HuaTuOnline_app_HuaTuOnline_DeleteCourse";//删除课程

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_STAGETESTSTART = "HuaTuOnline_app_pc_HuaTuOnline_StageTestStart";                     // 阶段性测试开始考试
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_STAGETESTREPORT = "HuaTuOnline_app_pc_HuaTuOnline_StageTestReport";                   // 阶段测试查看报告

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_CLICKAPPLICATIONANSWER = "HuaTuOnline_app_HuaTuOnline_CilckApplicationAnswer";        // 点击申论答题
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_INPUTAPPLICATIONANSWER = "HuaTuOnline_app_HuaTuOnline_InputApplicationAnswer";        // 输入申论答案
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_CLICKAPPLICATIONDATUM = "HuaTuOnline_app_HuaTuOnline_ClickApplicationDatum";          // 点击查看申论资料
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_APPLICATIONCLICKSUBMIT = "HuaTuOnline_app_HuaTuOnline_ApplicationClickSubmit";        // 申论点击交卷
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_APPLICATIONSUBMITSUCCEED = "HuaTuOnline_app_HuaTuOnline_ApplicationSubmitSucceed";    // 申论交卷成功

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_SMALLSIMULATESTART = "HuaTuOnline_app_HuaTuOnline_SmallSimulateStart";                // 小模考开始考试
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_SMALLSIMULATEREPORT = "HuaTuOnline_app_HuaTuOnline_SmallSimulateReport";              // 小模考查看报告

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_CLICKBANNER = "HuaTuOnline_app_HuaTuOnline_ClickBanner";                              // 首页banner图点击埋点

    public static final String HUATUONLINE_APP_PC_HUATUONLINE_SELECTQUESTION = "HuaTuOnline_app_pc_HuaTuOnline_SelectQuestion";                     // 选择错题
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_CLICKEXPORT = "HuaTuOnline_app_pc_HuaTuOnline_ClickExport";                           // 确认导出
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_EXPORTSUCCEED = "HuaTuOnline_app_pc_HuaTuOnline_ExportSucceed";                       // 导出成功
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_CLICKDOWNLOAD = "HuaTuOnline_app_pc_HuaTuOnline_ClickDownload";                       // 点击下载
    public static final String HUATUONLINE_APP_PC_HUATUONLINE_DOWNLOADSUCCEED = "HuaTuOnline_app_pc_HuaTuOnline_DownloadSucceed";                   // 下载成功


    public static final String HUAATUONLINE_APP_OPENNOTIFICATION = "HuaTuOnline_app_HuaTuOnline_OpenNotification";// 打开推送消息
    public static String pageSource;

    public static void sendTrack(String type) {
        try {
            SensorsDataAPI.sharedInstance().track(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendTrack(String type, BaseEvent baseEvent) {
        try {
            SensorsDataAPI.sharedInstance().track(type, baseEvent.toJsonObject());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendTrack(String type, Map<String, Object> params) {
        try {
            JSONObject jsonObject = new JSONObject(params);
            Log.i("SensorStatisticHelper", jsonObject.toString());
            SensorsDataAPI.sharedInstance().track(type, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
