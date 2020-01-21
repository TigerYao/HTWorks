package com.huatu.handheld_huatu.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.SimpleResponseModel;
import com.huatu.handheld_huatu.business.arena.bean.EvaluateReportBean;
import com.huatu.handheld_huatu.business.arena.bean.ExportDescription;
import com.huatu.handheld_huatu.business.arena.downloaderror.bean.ErrExpListBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaDownLoadUrlBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperInfoNet;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipNewBean;
import com.huatu.handheld_huatu.business.essay.bean.CheckCountNum;
import com.huatu.handheld_huatu.business.essay.bean.EssayPaperReport;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchData;
import com.huatu.handheld_huatu.business.essay.bean.EssyMockRemindTime;
import com.huatu.handheld_huatu.business.essay.bean.HomeworkSingleListBean;
import com.huatu.handheld_huatu.business.essay.bean.IsCheckData;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseTabData;
import com.huatu.handheld_huatu.business.essay.bean.MyCheckData;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseTabData;
import com.huatu.handheld_huatu.business.essay.bean.UpLoadEssayData;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.ImageUpResult;
import com.huatu.handheld_huatu.business.faceteach.bean.F2fJobBean;
import com.huatu.handheld_huatu.business.faceteach.bean.F2fJobStatusBean;
import com.huatu.handheld_huatu.business.faceteach.bean.SplitOrderBean;
import com.huatu.handheld_huatu.business.lessons.LiveSearchKeyword;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.lessons.bean.CourseSuit;
import com.huatu.handheld_huatu.business.lessons.bean.Courses;
import com.huatu.handheld_huatu.business.lessons.bean.FaceToFaceCourseBean;
import com.huatu.handheld_huatu.business.lessons.bean.OneToOneInfoBean;
import com.huatu.handheld_huatu.business.lessons.bean.ProtocolResultBean;
import com.huatu.handheld_huatu.business.lessons.bean.PurchasedBean;
import com.huatu.handheld_huatu.business.matches.bean.GiftDescribeBean;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperListData;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperTopData;
import com.huatu.handheld_huatu.business.me.bean.BalanceDetailData;
import com.huatu.handheld_huatu.business.me.bean.CancelOrderBean;
import com.huatu.handheld_huatu.business.me.bean.EssayOrderBean;
import com.huatu.handheld_huatu.business.me.bean.ExchangeVoucherBean;
import com.huatu.handheld_huatu.business.me.bean.LastLogisticData;
import com.huatu.handheld_huatu.business.me.bean.LevelBean;
import com.huatu.handheld_huatu.business.me.bean.MyAccountBean;
import com.huatu.handheld_huatu.business.me.bean.MyRedPacketBean;
import com.huatu.handheld_huatu.business.me.bean.MySignBean;
import com.huatu.handheld_huatu.business.me.bean.MyV5OrderData;
import com.huatu.handheld_huatu.business.me.bean.OrderDetailData;
import com.huatu.handheld_huatu.business.me.bean.OrderLogisticBean;
import com.huatu.handheld_huatu.business.me.bean.RecordTypeData;
import com.huatu.handheld_huatu.business.me.bean.RedBagInfo;
import com.huatu.handheld_huatu.business.me.bean.RedBagShareInfo;
import com.huatu.handheld_huatu.business.me.bean.RedConfirmCodeBean;
import com.huatu.handheld_huatu.business.me.bean.RedPacketBean;
import com.huatu.handheld_huatu.business.me.bean.RedPacketDetailBean;
import com.huatu.handheld_huatu.business.me.bean.ScanCourseData;
import com.huatu.handheld_huatu.business.me.bean.SignBean;
import com.huatu.handheld_huatu.business.me.order.OrderRefundResp;
import com.huatu.handheld_huatu.business.message.model.MessageGroupData;
import com.huatu.handheld_huatu.business.play.bean.CourseActDetailBean;
import com.huatu.handheld_huatu.business.play.bean.CourseDetailBean;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineBean;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineItemBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherCourseBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherIntroInfoBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherListItemBean;
import com.huatu.handheld_huatu.business.play.bean.HistoryCourseBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.CourseBuyDetailBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.GetEvaluateBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HighEndBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.MianShouInfoBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.ShaixuanBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherDefenBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherDetailListBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherJieshaoBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherLishiBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherListBeans;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherPingjiaBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCourse;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.setMianShouBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.AddressInfoBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CalenderCourseBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CourseSyllabus;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.OrdersPrevInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.XxbRemainderBean;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;
import com.huatu.handheld_huatu.mvpmodel.Category;
import com.huatu.handheld_huatu.mvpmodel.CollectionResultBean;
import com.huatu.handheld_huatu.mvpmodel.CommentResult;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleDetail;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleListResponse;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleTabData;
import com.huatu.handheld_huatu.mvpmodel.ExerciseBean;
import com.huatu.handheld_huatu.mvpmodel.HomeAdvBean;
import com.huatu.handheld_huatu.mvpmodel.HomeConfig;
import com.huatu.handheld_huatu.mvpmodel.HomeReport;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;
import com.huatu.handheld_huatu.mvpmodel.MessageBean;
import com.huatu.handheld_huatu.mvpmodel.PraiseData;
import com.huatu.handheld_huatu.mvpmodel.RewardInfoBean;
import com.huatu.handheld_huatu.mvpmodel.Sensor.CourseInfoForStatistic;
import com.huatu.handheld_huatu.mvpmodel.ShareInfo;
import com.huatu.handheld_huatu.mvpmodel.UpdateInfoBean;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.mvpmodel.account.UserInfoBean;
import com.huatu.handheld_huatu.mvpmodel.area.AreaBean;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaItem;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaVersion;
import com.huatu.handheld_huatu.mvpmodel.area.ProvinceBeanList;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ExamPagerItem;
import com.huatu.handheld_huatu.mvpmodel.arena.ExportErrBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ExportErrBeanPre;
import com.huatu.handheld_huatu.mvpmodel.arena.PaperListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountDetail;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountInfo;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodData;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodOrderBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CreateAnswerCardIdBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CreateAnswerCardPostBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayAnswerImageSortBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayCommitResponse;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayPayInfo;
import com.huatu.handheld_huatu.mvpmodel.essay.EssaySearchAnswerCardStateInfo;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.MaterialsFileUrlBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperCommitBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleAreaListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.TeacherBusyStateInfo;
import com.huatu.handheld_huatu.mvpmodel.exercise.EvaluatorDetailBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeansNew;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseReportBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.PracticeIdBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.SimulationListItem;
import com.huatu.handheld_huatu.mvpmodel.exercise.TimeBean;
import com.huatu.handheld_huatu.mvpmodel.faceteach.FaceClassDetailBean;
import com.huatu.handheld_huatu.mvpmodel.faceteach.FaceGoodsDetailBean;
import com.huatu.handheld_huatu.mvpmodel.faceteach.FacePlaceOrderBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.EssayScReportBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.MatchTabBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.SignSuccessBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationEssayPastPaperData;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBean;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScDetailBeanNew;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScHistory;
import com.huatu.handheld_huatu.mvpmodel.matchs.SimulationScHistoryTag;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallMatchBean;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallMatchReportBean;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.SmallReportListBean;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.StageBean;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.StageReportBean;
import com.huatu.handheld_huatu.mvpmodel.matchsmall.StageReportOtherBean;
import com.huatu.handheld_huatu.mvpmodel.me.ActionListBean;
import com.huatu.handheld_huatu.mvpmodel.me.ActionNumberAddBean;
import com.huatu.handheld_huatu.mvpmodel.me.ChangeNicknameBean;
import com.huatu.handheld_huatu.mvpmodel.me.ChangePasswordBean;
import com.huatu.handheld_huatu.mvpmodel.me.CollectionIdsBean;
import com.huatu.handheld_huatu.mvpmodel.me.CoverManulResBean;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.mvpmodel.me.ErrorIdsBean;
import com.huatu.handheld_huatu.mvpmodel.me.ErrorTopBean;
import com.huatu.handheld_huatu.mvpmodel.me.ExamTypeAreaConfigBean;
import com.huatu.handheld_huatu.mvpmodel.me.FeedbackBean;
import com.huatu.handheld_huatu.mvpmodel.me.FeedbackResponseBean;
import com.huatu.handheld_huatu.mvpmodel.me.LogoutBean;
import com.huatu.handheld_huatu.mvpmodel.me.RecordBean;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.mvpmodel.me.TreeBasic;
import com.huatu.handheld_huatu.mvpmodel.me.TreeViewBean;
import com.huatu.handheld_huatu.mvpmodel.me.UnReadActCountBean;
import com.huatu.handheld_huatu.mvpmodel.me.UploadImgBean;
import com.huatu.handheld_huatu.mvpmodel.previewpaper.PreviewPaperBean;
import com.huatu.handheld_huatu.mvpmodel.special.DailySpecialBean;
import com.huatu.handheld_huatu.mvpmodel.special.SettingBean;
import com.huatu.handheld_huatu.mvpmodel.special.SpecialSeetingBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.BuyStatusData;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseDiss;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LastCourseBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.VideoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * @author zhaodongdong
 */
public interface HttpService {

    String prefix = "";
    // final String APIURL = "http://rap.htexam.com/mockjsdata/48/";

    @GET("/time")
    Observable<BaseResponseModel<Long>> getServiceTime();


    //HomeFragment
    // 获取题库接口（已弃用 ---> 重新启用，为了专项练习后保持树形展开结构，获取全部数据，进行数据比对刷新）
    // flag 1、做题模式还是 2、背题模式 本地Sp里保存的是 0、做题 1、背题，所以请求的时候用 Sp + 1
    @GET("k/v1/points")
    Observable<BaseListResponseModel<HomeTreeBeanNew>> getHomeTreeData(@Query("flag") int flag);

    @GET("/p/v3/papers/idList")
    Observable<BaseListResponseModel<TipNewBean>> getMatchIdForNewTip();

    /**
     * 题库，获取清空做题记录时候弹的提示
     */
    @GET("/k/v4/points/clear/remain")
    Observable<BaseResponseModel<ConfirmCodeBean.ConfirmCode>> getClearRecordMsg();

    /**
     * 题库，清空做题记录
     */
    @DELETE("/k/v4/points/clear")
    Observable<BaseResponseModel<ConfirmCodeBean.ConfirmCode>> getClearRecord();

    /**
     * 分层级获取首页Tree数据
     *
     * @param parentId 父类知识点id 默认为0，是获取第一级
     * @param flag     1、做题模式还是 2、背题模式 本地Sp里保存的是 0、做题 1、背题，所以请求的时候用 Sp + 1
     */
    @GET("k/v1/points/collectionsByNode")
    Observable<BaseListResponseModel<HomeTreeBeanNew>> getHomeTreeDataById(@Query("parentId") int parentId, @Query("flag") int flag);

    @GET("/u/v5/users/bc/list")
    Observable<BaseListResponseModel<AdvertiseConfig>> getHomeAdvertise(@Query("category") int category, @Query("fur") int fur);

    /**
     * 获取行测主页的icon图标
     */
    @GET("/u/icon")
    Observable<BaseListResponseModel<HomeIconBean>> getHomeIcons(@Query("subjectId") int subjectId);

    @GET("r/v1/report/summary")
    Observable<BaseResponseModel<HomeReport>> getHomeReport();

    /**
     * 根据试卷id，获取真题/模考试卷的信息
     *
     * @param id 试卷id，如817
     * @return
     */
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @POST("p/v1/practices/papers")
    Observable<RealExamBeans> getRealOrMockPaper(
            @Query("id") long id
    );

    /**
     * 根据试卷id，获取继续做题的id
     *
     * @param id 试卷id
     */
    @POST("/p/v2/practices/advertPapers")
    Observable<BaseResponseModel<PracticeIdBean>> getCurrentPracticeId(
            @Query("id") long id);

    @POST("p/v1/practices/matches")
    Observable<RealExamBeans> getScMockPaper(
            @Query("id") long id
    );

    /**
     * 小模考获取试题
     */
    @POST("/p/v4/small/estimate/papers")
    Observable<RealExamBeans> getSmallMatchPaper(
            @Query("id") long id
    );

    /**
     * 阶段性测试获取试题
     */
    @POST("/p/v4/periodTest/practice/{paperId}")
    Observable<RealExamBeans> getStagePaper(@Path("paperId") long paperId, @Query("syllabusId") long syllabusId);

    /**
     * 获取错题训练试卷的信息
     *
     * @param pointId 知识点id
     * @param size    题目数量，默认10
     * @return
     */
    @POST("p/v2/practices/errors")
    Observable<RealExamBeans> getErrorTrainingPaper(
            @Query("pointId") long pointId,
            @Query("size") int size
    );

    /**
     * 获取首页专项练习试卷的信息
     *
     * @param pointId 知识点id
     * @param size    题目数量，默认10
     * @param flag    1、做题，2、背题
     * @return
     */
    @POST("p/v4/practice/customizes")
    Observable<RealExamBeans> getHomeQuestionPaper(
            @Query("pointId") long pointId,
            @Query("size") int size,
            @Query("flag") int flag
    );

    /**
     * 获取错题训练试卷的信息
     *
     * @param pointId 知识点id
     * @param size    题目数量，默认10
     * @param flag    1、做题，2、背题  这里为单独作为做题的接口
     * @return
     */
    @POST("p/v4/practice/errors")
    Observable<RealExamBeans> getErrorQuestionPaper(
            @Query("pointId") long pointId,
            @Query("size") int size,
            @Query("flag") int flag
    );

    /**
     * 获取专项训练试卷的信息
     *
     * @param pointId 知识点id
     * @param size    题目数量，默认10
     * @return
     */
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @POST("p/v2/practices/customizes")
    Observable<RealExamBeans> getSpecialTrainingPaper(
            @Query("pointId") long pointId,
            @Query("size") int size
    );

    /**
     * 获取每日特训试卷信息
     *
     * @param pointId 知识点id
     * @return
     */
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @POST("p/v1/practices/daytrain")
    Observable<RealExamBeans> getDailyTrainingPaper(
            @Query("pointId") long pointId
    );

    /**
     * 智能出题
     *
     * @return
     */
    @POST("p/v1/practices/smarts")
    Observable<RealExamBeans> getArtificialIntelligencePaper(
    );

    /**
     * 根据试题id，获取试题
     *
     * @param ids 试题id，用","隔开，如31375,31373
     * @return
     */
//    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG)
    @GET("q/v1/questions/")
    Observable<ExerciseBeans> getExercises(
            @Query("ids") String ids
    );

    /**
     * 查看练习详情
     *
     * @param id 练习id
     * @return
     */
    @GET("p/v1/practices/{id}")
    Observable<RealExamBeans> getPractiseDetails(@Path("id") long id);

    /**
     * 行测模考报告
     */
    @GET("p/v2/practices/{paperId}")
    Observable<RealExamBeans> getPractiseDetailsV2(@Path("paperId") long paperId);

    /**
     * 查询竞技记录详情
     *
     * @param arenaId 竞技房间id
     * @return
     */
    @GET("/a/v1/arenas/{arenaId}")
    Observable<BaseResponseModel<ArenaDetailBean>> getArenaDetails(
            @Path("arenaId") long arenaId
    );

    /**
     * 查询竞技结果分享信息
     *
     * @return
     */
    @POST("/pc/v2/share/arena/record")
    Observable<BaseResponseModel<ShareInfoBean>> getArenaStatisticShareInfo(@Query("id") long arenaId);

    /**
     * 提交答题卡
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @PUT("p/v1/practices/{practiceId}")
    Observable<RealExamBeans> submitAnswerCard(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 小模考交卷
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @POST("/p/v4/small/estimate/{practiceId}")
    Observable<RealExamBeans> submitSMatchAnswerCard(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 阶段性测试交卷
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @POST("/p/v4/periodTest/{practiceId}")
    Observable<BaseResponseModel> submitStageAnswerCard(
            @Path("practiceId") long answerCardId,
            @Query("syllabusId") long syllabusId,
            @Body JsonArray answers
    );

    /**
     * 提交估分答题卡
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @PUT("/p/v1/practices/estimate/{practiceId}")
    Observable<BaseResponseModel<RealExamBeans.RealExamBean>> submitGufenAnswerCard(
            @Path("practiceId") long answerCardId,
            @Query("cardType") int cardType,
            @Body JsonArray answers
    );

    /**
     * 提交模考大赛答题卡（新）
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @POST("/match/v1/answerCard/{practiceId}/submit")
    Observable<BaseResponseModel<RealExamBeans.RealExamBean>> submitScCard(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 保存答题卡
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @PUT("p/v1/practices/{practiceId}/answers")
    Observable<BaseResponseModel> saveAnswerCard(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 小模考保存答题卡
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @PUT("/p/v4/small/estimate/{practiceId}/answer")
    Observable<BaseResponseModel> saveSMatchAnswerCard(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 阶段性测试保存答题卡
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @PUT("/p/v4/periodTest/{practiceId}/answer")
    Observable<BaseResponseModel> saveStageAnswerCard(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 课后作业 - 提交答题卡（已完成）
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @PUT("/p/v1/courseWork/{practiceId}")
    Observable<RealExamBeans> submitAfterClassAnswerCard(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 保存答题卡(新模考)
     *
     * @param answerCardId 练习id/答题卡id
     * @param answers      答题卡数据信息
     * @return
     */
    @POST("/match/v1/answerCard/{practiceId}/save")
    Observable<BaseResponse> saveScAnswerCardNew(
            @Path("practiceId") long answerCardId,
            @Body JsonArray answers
    );

    /**
     * 试题纠错
     *
     * @param qid            题号
     * @param questionAdvice 纠错内容："qid":31375|"contacts":10086|"content":"hehe"
     */
    @POST("q/v1/questions/{qid}/advice")
    Observable<BaseResponseModel<String>> correctError(
            @Path("qid") int qid,
            @Body JsonObject questionAdvice
    );

    /**
     * 删除错题
     *
     * @param qid 试题id
     * @return
     */
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_SHORT)
    @DELETE("k/v4/errors/questions/{qid}")
    Observable<BaseResponseModel<String>> deleteWrongExerciseV4(
            @Path("qid") int qid
    );

    /**
     * 收藏试题
     *
     * @param questionId 试题id
     * @return
     */
    @POST("k/v1/collects/{questionId}")
    Observable<BaseResponseModel<String>> collectExercise(
            @Path("questionId") int questionId
    );

    /**
     * 取消收藏试题
     *
     * @param questionId 试题id
     * @return
     */
    @DELETE("k/v1/collects/{questionId}")
    Observable<BaseResponseModel<String>> cancelCollection(
            @Path("questionId") int questionId
    );

    /**
     * 根据试题id,批量查询哪些试题被收藏过
     *
     * @param qids 试题id列表,用逗号分割,例如: 307245,52348,12345
     * @return
     */
    @GET("k/v1/collects/batch")
    Observable<JsonObject> getExerciseCollectStatus(
            @Query("qids") String qids
    );

    /**
     * 获取用户未读活动的个数
     *
     * @return
     */
    @GET("u/v1/users/bc/actCount")
    Observable<UnReadActCountBean> getActionRedInfo();

    /**
     * 提交意见反馈
     *
     * @param feedbackBean
     * @return
     */
    @POST("u/v2/users/feedback")
    Observable<FeedbackResponseBean> sendFeedback(@Body FeedbackBean feedbackBean);

    @POST("u/v2/users/feedback")
    Observable<BaseResponseModel<String>> sendBugReport(@Body FeedbackBean feedbackBean);

    @POST("e/api/v1/correctFeedBack")
    Observable<BaseResponseModel<String>> essayResultFeedback(@Body RequestBody body);

    /**
     * 获取活动中心列表
     *
     * @return
     */
    @GET("u/v1/users/bc/actlist")
    Observable<ActionListBean> getActionListInfo();

    /**
     * 活动点击量增加
     *
     * @param aid
     * @return
     */
    @PUT("u/v1/users/bc/pvadd")
    Observable<ActionNumberAddBean> addActionNum(@Query("aid") long aid);

    /**
     * 获取收藏列表
     *
     * @return
     */
    @GET("k/v1/collects/trees")
    Observable<TreeBasic> getCollectionList();


    /**
     * 清除错题本
     *
     * @return
     */
    @DELETE("k/v4/errors/clear")
    Observable<BaseResponseModel> clearErrorList();


    /**
     * 查询错题列表知识点树v
     *
     * @return
     */
    @GET("k/v1/errors/trees")
    Observable<BaseListResponseModel<TreeViewBean>> getErrorList();


    /**
     * 查询错题列表知识点树v
     *
     * @return
     */
    @GET("k/v1/errors/trees")
    Observable<BaseListResponseModel<HomeTreeBeanNew>> getErrorListX();

    /**
     * 获取错题导出说明
     */
    @GET("k/v4/errors/down/description")
    Observable<BaseResponseModel<ExportDescription>> getExportDescription();

    /**
     * 错题预导出
     */
    @POST("k/v4/errors/down/pre")
    Observable<BaseResponseModel<ExportErrBeanPre>> preExportErrorArena(@Query("pointIds") String pointIds, @Query("num") int num);

    /**
     * 导出错题
     */
    @POST("k/v4/errors/down/task")
    Observable<BaseResponseModel<ExportErrBean>> exportErrorArena(@Query("pointIds") String pointIds);

    /**
     * 导出错题列表
     */
    @GET("k/v4/errors/down/list")
    Observable<BaseResponseModel<ErrExpListBean>> getErrExpList(@Query("page") int page, @Query("size") int size);

    /**
     * 点击获取下载信息
     */
    @GET("k/v4/errors/down/info")
    Observable<BaseResponseModel<ErrExpListBean.ErrExpBean>> getErrExpDownloadInfo(@Query("taskId") long taskId);

    /**
     * 删除下载任务
     */
    @DELETE("k/v4/errors/down/remove")
    Observable<BaseResponseModel<Object>> delErrExport(@Query("taskIds") String taskIds);

    /**
     * 错题本上的错题数据
     */
    @GET("hadoop/v1/ability/assessment/accuracy")
    Observable<BaseResponseModel<ArrayList<ErrorTopBean>>> getErrorTopData();

    /**
     * 获取目标考试地区liebi
     *
     * @return
     */
    @GET("u/v2/users/areas")
    Observable<BaseResponseModel<ProvinceBeanList>> getTargetAreaList(@Query("catgory") int catgory);

    /**
     * 获取科目信息
     *
     * @return
     */
    @GET("/k/v2/subjects/tree/static")
    Observable<BaseListResponseModel<Category>> getSignUpDataList();

    /**
     * 修改昵称
     *
     * @param nickname
     * @return
     */
    @PUT("u/v1/users/nick")
    Observable<ChangeNicknameBean> changeNickname(@Query("nickname") String nickname);

    /**
     * 修改密码
     *
     * @param newpwd
     * @param oldpwd
     * @return
     */
    @PUT("u/v1/users/modifypwd")
    Observable<ChangePasswordBean> changePassword(@Query("newpwd") String newpwd,
                                                  @Query("oldpwd") String oldpwd);

    /**
     * 答题历史接口
     *
     * @param cardType 答题卡类型，默认0
     * @param cardTime 答题时间，默认为空 2016-07或2016-7
     * @param cursor   第几页，默认0
     * @return
     */
    @GET("p/v3/practices")
    Observable<RecordBean> getRecordList(@Query("cardType") int cardType,
                                         @Query("cardTime") String cardTime,
                                         @Query("cursor") long cursor);

    /**
     * 每日特训列表
     *
     * @return
     */
    @GET("p/v3/train")
    Observable<BaseResponseModel<DailySpecialBean>> getDailyList();

    /**
     * 获取（喜闻、乐见等）广告列表
     *
     * @return
     */
    @GET("/u/v5/users/bc/homeOperation")
    Observable<BaseResponseModel<HomeAdvBean>> getHomeAdvList(@Query("category") int category);

    /**
     * 获取每日特训设置
     *
     * @return
     */
    @GET("p/v1/train/settings")
    Observable<SpecialSeetingBean> getSettingInfo();

    /**
     * 提交每日特训设置的内容
     *
     * @param settingBean
     * @return
     */
    @PUT("p/v1/train/settings")
    Observable<ResponseBody> commitDailySetting(@Body SettingBean settingBean);

    /**
     * 获取真题演练地区数据
     *
     * @return
     */
    @GET("p/v3/papers/summary")
    Observable<BaseListResponseModel<ExamAreaItem>> getExamAreaList(@Query("subject") Integer subject);

    /**
     * 获取真题演练地区数据
     *
     * @return
     */
    @GET("p/v3/truePaperVersion/getRootVersion")
    Observable<BaseResponseModel<ExamAreaVersion>> getExamAreaVersion();

    /**
     * 获取指定区域的试题列表
     *
     * @param page      页码，默认1
     * @param size      页数大小，默认20
     * @param area      考试区域
     * @param paperType 试卷类型
     * @return
     */
    @GET("p/v3/papers/list")
    Observable<BaseResponseModel<ExamPagerItem>> getAreaPaperList(@Query("page") int page,
                                                                  @Query("size") int size,
                                                                  @Query("area") int area,
                                                                  @Query("subject") Integer subject,
                                                                  @Query("paperType") int paperType);

    /**
     * 根据知识点，查询用户的错题列表
     *
     * @param pointId
     * @return
     */
    @GET("k/v2/errors/")
    Observable<ErrorIdsBean> getErrorIdsList(@Query("pointId") int pointId);

    /**
     * 根据知识点，查询用户的收藏问题列表
     *
     * @param points
     * @return
     */
    @GET("k/v2/collects/")
    Observable<CollectionIdsBean> getCollectionIdsList(@Query("pointId") int points, @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 用户注销登录
     *
     * @return
     */
    @GET("u/v1/users/logout")
    Observable<LogoutBean> userExitAccount();

    @Multipart
    @POST("u/v1/users/avatar")
    Observable<DeleteResponseBean> sendImage(@Part MultipartBody.Part file);


    @Multipart
    @POST("/u/v2/users/uploadImage")
    Observable<UploadImgBean> sendFeedBackImage(@Part MultipartBody.Part file);

    @DELETE("p/v1/practices/{id}")
    Observable<DeleteResponseBean> deleteRecordItem(@Path("id") long id);

    @GET("s/v1/search/course/")
    Observable<BaseResponseModel<String>> saveSearchLiveKeywords(@Query("q") String q);

    @GET("s/v1/search/course/keywords")
    Observable<BaseResponseModel<LiveSearchKeyword>> getSearchLiveKeywords();

    @DELETE("s/v1/search/course/keywords")
    Observable<BaseResponseModel<String>> deleteLiveSearchKeyword(@Query("q") String q);

    @GET("q/v1/questions/myrecords")
    Observable<TimeBean> getQuestionsMyRecords(@Query("qids") String qids);

    @POST("pc/v2/share/myreport")
    Observable<BaseResponseModel<ShareInfoBean>> shareMyReport();

    @POST("pc/v2/share/practice")
    Observable<BaseResponseModel<ShareInfoBean>> sharePractice(@Query("practiceId") long practiceId);

    //v4版分享
    @POST("pc/v4/share/practice")
    Observable<BaseResponseModel<ShareInfoBean>> newSharePractice(@Query("practiceId") long practiceId, @Query("type") int type);

    @POST("pc/v2/share/question/{qid}")
    Observable<BaseResponseModel<ShareInfoBean>> shareQuestion(@Path("qid") int qid);

    @GET("/u/v1/users/red")
    Observable<BaseResponseModel<HomeConfig>> getHomeConfig();

    //  考试类型；1，公考 2，教师考试 3，事业单位 4，医疗
    //  考试科目；1，行测 2，公共基础 3，行测
    //1：公务员行测 ，2：事业单位公基 ，3：事业单位行测
    @PUT("/u/v2/users/config")
    Observable<BaseResponseModel<ExamTypeAreaConfigBean>> setUserAreaTypeConfig(@Query("catgory") Integer category,
                                                                                @Query("area") Integer area,
                                                                                @Query("subject") Integer subject,
                                                                                @Query("qcount") Integer qcount,
                                                                                @Query("errorQcount") Integer errorQcount);

    /**
     * 查询用户地址
     */
    @GET("/c/v3/address")
    Observable<BaseListResponseModel<AddressInfoBean>> searchAddress();

    /**
     * 新建用户地址
     *
     * @param addAddressBeanJson
     * @return
     */
    @POST("/c/v3/address")
    Observable<BaseResponseModel<Integer>> addAddress(@Body AddressInfoBean addAddressBeanJson);

    /**
     * 删除地址
     */
    @DELETE("/c/v3/address/{id}")
    Observable<BaseResponseModel<String>> deleteAddress(@Path("id") long id);

    /**
     * 更新地址
     */
    @PATCH("/c/v3/address/{id}")
    Observable<BaseResponseModel<String>> updateAddress(@Path("id") long id,
                                                        @Body AddressInfoBean updateAddressBeanJson);

    /**
     * 分享课程
     */
    @POST("/pc/v2/share/course")
    Observable<BaseResponseModel<ShareInfo>> sendClass(@Query("courseId") long id);

    /**
     * 分享音频课件
     *
     * @return
     */
    @POST("/pc/v4/share/course")
    Observable<BaseResponseModel<ShareInfo>> getAudioShareInfo(@Query("courseId") long id, @Query("courseWareId") long courseWareId, @Query("shareSyllabusId") long shareSyllabusId, @Query("type") int type);

    @POST("pc/v2/share/arena/todayrank")
    Observable<BaseResponseModel<ShareInfo>> shareAthleticDaily();

    @POST("pc/v2/share/arena/summary")
    Observable<BaseResponseModel<ShareInfo>> shareAthleticHome();

    @GET("/u/v2/users/check")
    Observable<BaseResponseModel<UpdateInfoBean>> checkUpdate(@Query("source") String sourceId, @Query("ver") String vercode);

    // 首次应用商店评价送图币接口
    @POST("/c/v1/activity/appStoreEvalution")
    Observable<BaseResponseModel<String>> firstComment();

    @GET("/c/v3/reward/_settings")
    Observable<BaseResponseModel<Map<String, RewardInfoBean>>> getRewardInfo();

    @GET("/c/v4/userGold/info")
    Observable<BaseResponseModel<XxbRemainderBean>> getXxbRemainder();

    @POST("/pc/v2/share/reward")
    Observable<BaseResponseModel<RewardInfoBean>> postShareReward(@Query("id") String id);

    @POST("/u/v1/users/bc/course")
    Observable<BaseResponseModel<CommentResult>> getCommentLesson();

    @GET("c/v3/courses/{courseId}/secrinfo")
    Observable<BaseResponseModel<VideoBean>> getCourseInfo(@Path("courseId") String courseId);


    @POST("hbase/video/process")
    Observable<BaseResponseModel<String>> saveBaijiaCourseProgress(@Query("playTime") long playTime, @Query("roomId") String roomId, @Query("sessionId") String sessionId, @Query("wholeTime") long wholeTime);

    @POST("hbase/video/process")
    Observable<BaseResponseModel<String>> saveGeeseeProgress(@Query("playTime") long playTime, @Query("joinCode") String joinCode, @Query("wholeTime") long wholeTime);


    @POST("hbase/video/process")
    Observable<BaseResponseModel<String>> saveRecordCourseProgress(@Query("playTime") long playTime, @Query("videoIdWithoutTeacher") String videoIdWithoutTeacher, @Query("videoIdWithTeacher") String videoIdWithTeacher, @Query("wholeTime") long wholeTime);

    /**
     * 登录
     *
     * @param account     账户名,手机号或者邮箱
     * @param password    密码
     * @param captcha     手机验证码
     * @param deviceToken 用户设备唯一标识（非必填）
     * @return
     */
    @POST("/u/v3/users/login")
    Observable<UserInfoBean> login(
            @Query("account") String account,
            @Query("password") String password,
            @Query("captcha") String captcha,
            @Query("catgory") int catgory,
            @Query("deviceToken") String deviceToken
    );

    /**
     * 提交channelId
     *
     * @param deviceToken deviceToken
     * @param gmtCreate   时间戳
     * @param ip          手机ip地址
     * @param source      channel
     * @return
     */
    @POST("/u/v2/channel")
    Observable<BaseResponseModel<String>> setChannel(
            @Query("deviceToken") String deviceToken,
            @Query("gmtCreate") long gmtCreate,
            @Query("ip") String ip,
            @Query("source") int source
    );

    /**
     * 上报地理位置信息
     *
     * @param city         param市名称
     * @param district     param区名称
     * @param number       门牌号
     * @param positionName 详细名称
     * @param province     param省名称
     * @param street       param 街道
     * @return
     */
    @POST("/u/v1/users/report/position")
    Observable<BaseResponseModel<Object>> setReportPosition(
            @Query("city") String city,
            @Query("district") String district,
            @Query("number") String number,
            @Query("positionName") String positionName,
            @Query("province") String province,
            @Query("street") String street
    );

    /**
     * 发送验证码
     *
     * @param mobile 手机号
     * @return
     */
    @GET("/u/v2/users/captcha/{mobile}")
    Observable<ConfirmCodeBean> sendConfirmCode(
            @Path("mobile") String mobile
    );

    /**
     * 忘记密码发送验证码
     *
     * @param mobile 手机号
     * @return
     */
    @GET("/u/v2/users/findPasswordCaptcha/{mobile}")
    Observable<ConfirmCodeBean> sendConfirmCodeForgetPassWord(
            @Path("mobile") String mobile
    );

    /**
     * 重置密码
     *
     * @param mobile   手机号
     * @param password 用户名
     * @param captcha  验证码
     * @return
     */
    @PUT("/u/v1/users/resetpwd")
    Observable<ConfirmCodeBean> resetPwd(
            @Query("mobile") String mobile,
            @Query("password") String password,
            @Query("captcha") String captcha
    );

    /**
     * 注册
     *
     * @param mobile  手机号
     * @param captcha 验证码
     * @return
     */
    @POST("/u/v1/users/register")
    Observable<UserInfoBean> registerAccount(
            @Query("mobile") String mobile,
            @Query("captcha") String captcha
    );

    /**
     * 完善用户资料
     * header -> token
     *
     * @param password 用户密码
     * @param nick     用户昵称
     * @return
     */
    @PUT("/u/v1/users/complete")
    Observable<UserInfoBean> completeAccount(
            @Query("password") String password,
            @Query("nick") String nick
    );

    /**
     * 获取模考试卷列表
     *
     * @param size 每页数据数量,默认20
     * @param page 页码默认1
     * @return
     */
    @GET("/p/v2/papers/estimateList")
    Observable<BaseListResponseModel<SimulationListItem>> getSimulationList(
            @Query("size") int size,
            @Query("page") int page,
            @Query("types") String types,
            @Query("subjectId") int subjectId

    );

    /**
     * 模考大赛首页Tab列表
     *
     * @return
     */
    @GET("match/v1/tag/subject/{subject}")
    Observable<BaseListResponseModel<MatchTabBean>> getSimulationTabList(
            @Path("subject") int subject,
            @Query("subjectId") int subjectId
    );

    /**
     * 行测模考大赛首页，列表页（新）
     *
     * @return
     */
    @GET("/match/v1/search")
    Observable<BaseResponseModel<SimulationScDetailBeanNew>> getSimulationContestDetailNew(
            @Query("size") int size,
            @Query("page") int page,
            @Query("subjectId") int subjectId
    );

    /**
     * 申论模考大赛详情页
     *
     * @return
     */
    @GET("/e/api/v3/mock")
    Observable<BaseListResponseModel<SimulationScDetailBean>> getSimulationEssayContestDetail();

    /**
     * 行测模考大赛报名（新）
     *
     * @param matchId
     * @param positionId
     * @return
     */
    @POST("/match/v1/enroll/{matchId}")
    Observable<BaseResponseModel<SignSuccessBean>> postScSignUpNew(
            @Path("matchId") int matchId, @Query("positionId") int positionId
    );

    /**
     * 申论模考大赛报名
     *
     * @param paperId
     * @param positionId
     * @return
     */
    @POST("/e/api/v3/mock")
    Observable<BaseResponseModel<String>> postSimulationEssayCSignUp(
            @Query("paperId") int paperId, @Query("positionId") int positionId
    );

    /**
     * 模考报告（新）
     *
     * @return
     */
    @GET("/match/v1/report/{tagId}")
    Observable<BaseResponseModel<SimulationScHistory>> getNewScReport(
            @Path("tagId") int tagId,
            @Query("subjectId") int subjectId
    );

    /**
     * 模考大赛历史记录标签(新)
     *
     * @return
     */
    @GET("/match/v1/tag/{subject}")
    Observable<BaseListResponseModel<SimulationScHistoryTag>> getNewScHistoryTag(
            @Path("subject") int subject,
            @Query("subjectId") int subjectId
    );


    /**
     * 获取消息列表
     *
     * @param cursor 默认 0
     * @param size   消息个数默认 20
     * @return
     */
    @GET("/u/v1/users/messages")
    Observable<MessageBean> getMessages(
            @Query("cursor") long cursor,
            @Query("size") long size
    );

    /**
     * 查询消息详情
     *
     * @param mid
     * @return
     */
    @GET("/u/v1/users/messages/{mid}")
    Observable<ResponseBody> getMessageDetail(
            @Path("mid") long mid
    );

    /**
     * 获取评估报告详情信息（旧）
     */
    @GET("/r/v1/report/detail")
    Observable<EvaluatorDetailBean> getEvaluatorDetail();

    /**
     * 获取评估报告详情信息新
     */
    @GET("hadoop/v1/ability/assessment/report")
    Observable<BaseResponseModel<EvaluateReportBean>> getEvaluatorDetailNew();

    /**
     * 根据userId和subject科目获取评估报告详情信息
     */
    @GET("hadoop/v1/ability/assessment/report/top")
    Observable<BaseResponseModel<EvaluateReportBean>> getEvaluatorDetailByUserId(@Query("userid") long userid, @Query("subject") int subject);

//    /**
//     * 根据关键字查询习题
//     *
//     * @param q            查询关键字
//     * @param page         页数，默认1
//     * @param size         每页记录数,默认20
//     * @param questionType 试题类型，默认-1
//     * @return
//     */
//    @GET("/s/v1/search/question")
//    Observable<ExerciseBean> getSearchedExersices(
//            @Query("q") String q,
//            @Query("page") int page,
//            @Query("size") int size,
//            @Query("questionType") int questionType
//    );

    /**
     * 新的搜索接口
     * <p>
     * 根据关键字查询习题
     *
     * @param keyword 查询关键字
     * @param page    页数，默认1
     * @param size    每页记录数,默认20
     * @param mode    试题类型，默认1
     * @return
     */
    @GET("/ns/ht/v2/question")
    Observable<ExerciseBean> getSearchedExersices(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size,
            @Query("mode") int mode
    );

    /**
     * 获取启动图
     */
    @GET("/u/v5/users/bc/launch")
    Observable<BaseListResponseModel<AdvertiseConfig>> getSplashConfig(@Query("category") int catgory);

    @GET("/u/v5/users/bc/popup")
    Observable<BaseListResponseModel<AdvertiseConfig>> getAdvertise(@Query("category") int category);

    @GET("/c/v4/evaluates/collectionClasses")
    Observable<BaseResponseModel<Courses>> getCourses(@Query("collectionId") String collectionId, @Query("page") int page);

    @GET("/c/v3/courses/lives")
    Observable<BaseResponseModel<Courses>> getLiveCourses(@Query("category") String category,
                                                          @Query("keywords") String keywords, @Query("orderid") int orderid,
                                                          @Query("priceid") int priceid, @Query("page") int page);

    @GET("c/v3/courses/{courseId}/handouts")
    Observable<HandoutBean> getHandoutInfo(@Path("courseId") int courseId);

    /**
     * 我的直播-套餐课程
     */
    @GET("/c/v3/courses/{courseId}/suit")
    Observable<CourseSuit> getCourseSuit(@Path("courseId") int courseId);

    /**
     * 课程大纲
     */
    @GET("/c/v3/courses/{courseId}/timetable")
    Observable<BaseResponseModel<CourseSyllabus>> getCourseSyllabus(@Path("courseId") int courseId);

    /**
     * 我的课程
     */
    @GET("/c/v3/my/courses")
    Observable<BaseResponseModel<CourseMineBean>> getMyCourseList(
            @Query("type") int type, @Query("page") int page);

    @GET("/c/v3/my/courses")
    Observable<BaseResponseModel<CourseMineBean>> searchMyCourseList(@Query("keywords") String keywords,
                                                                     @Query("type") int type, @Query("page") int page);

    /**
     * 我的隐藏课程列表
     */
    @GET("/c/v3/my/courses")
    Observable<BaseResponseModel<CourseMineBean>> getCourseDiss(@Query("_hide") int _hide
            , @Query("page") int page, @Query("type") int type);

    /**
     * 我的直播隐藏课程
     */
    @POST("/c/v3/courses/hide")
    Observable<CourseDiss> courseDiss(@Query("courseIds") String courseIds
            , @Query("orderIds") String orderIds);

    /**
     * 恢复隐藏课程
     */
    @DELETE("/c/v3/courses/hide")
    Observable<CourseDiss> recoverCourseDiss(@Query("courseIds") String courseIds
            , @Query("orderIds") String orderIds);


    /**
     * 清空搜索历史
     */
    @DELETE("/s/v1/search/course/keywords/record")
    Observable<BaseResponseModel<String>> clearLiveSearchHistory();

    /**
     * 课程详情
     */
    @GET("/c/v3/courses/{courseId}")
    Observable<CourseBuyDetailBean> getCourseDetail(@Path("courseId") long courseId);

//    /**
//     * 课程详情
//     */
//    @GET("/c/v5/courses/{courseId}/getClassDetailLive")
//    Observable<BaseResponseModel<CourseDetailBean>> getLiveCourseDetail(@Path("courseId") String courseId, @Query("collageActivityId") int collegaActivityId);
//
//    @GET("/c/v5/courses/{courseId}/getClassDetailNotLive")
//    Observable<BaseResponseModel<CourseDetailBean>> getPlayoffCourseDetail(@Path("courseId") String courseId, @Query("collageActivityId") int collegaActivityId);

    /**
     * 课程详情(新 整合)
     *
     * @param courseId
     * @return
     */
    @GET("/c/v6/courses/classInfo")
    Observable<BaseResponseModel<CourseDetailBean>> getCourseDetailInfo(@Query("classId") String courseId, @Query("collageActivityId") int collegaActivityId, @Query("selectCollection") int selectCollection);

    @GET("/c/v5/courses/{courseId}/getClassExt")
    Observable<BaseResponseModel<String>> getCourseDetailH5Url(@Path("courseId") String courseId);

//    @GET("/c/v5/courses/{courseId}/getCourseIntroduction")
//    Observable<BaseResponseModel<VideoPlayVideoInfoBean>> getCourseIntroInfo(@Path("courseId") String courseId, @Query("collageActivityId") int collegaActivityId);

    @GET("/c/v5/teacher/{teacherId}/getTeacherDetail")
    Observable<BaseResponseModel<CourseTeacherIntroInfoBean>> getCourseTeacherIntroInfo(
            @Path("teacherId") String teacherId);

    @GET("/c/v5/evaluation/getTeacherEvaluation")
    Observable<BaseResponseModel<CourseTeacherJudgeBean>> getCourseTeacherJudge(
            @Query("teacherId") String teacherId,
            @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("/c/v5/evaluation/getClassEvaluation")
    Observable<BaseResponseModel<CourseTeacherJudgeBean>> getCourseJudgeList(
            @Query("classId") String classId, @Query("isLive") int isLive,
            @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("/c/v5/teacher/getPayClassesAll")
    Observable<BaseResponseModel<CourseTeacherCourseBean>> getCourseTeacherCourseList(
            @Query("teacherName") String teacherName,
            @Query("page") int page, @Query("pageSize") int pageSize);

    @GET("/c/v5/courses/{courseId}/getCourseTeacherInfo")
    Observable<BaseListResponseModel<CourseTeacherListItemBean>> getCourseTeacherList(
            @Path("courseId") String courseId);


    @GET("/c/v5/courses/{classId}/classSyllabus")
    Observable<BaseResponseModel<CourseOutlineBean>> getCourseOutline(
            @Path("classId") String classId, @Query("page") int page, @Query("pageSize") int pageSize,
            @Query("parentId") int parentId, @Query("onlyTrial") int onlyTrial);

    /**
     * 课程详情
     */
    @GET("/c/v5/my/{idList}/liveCalendarDetail")
    Observable<BaseListResponseModel<CalenderCourseBean>> getCalenderCourse(@Path("idList") String idList);


    /**
     * 添加免费课程
     */
    @POST("/c/v3/orders/free")
    Observable<BaseResponseModel<String>> addFreeCourse(@Query("courseId") int courseId, @Query("pageSource") String pageSource);

    //账户学习币充值
//    @POST("/c/v3/account/charge")
    @POST("/c/v6/order/reCharge")
    Observable<BaseResponseModel<PayInfo>> postAccountChargePayInfo(@Query("amount") String Amount, @Query("payType") String payType);

    /**
     * 根据地区、年份获取试卷列表
     *
     * @param page      页码，默认1
     * @param size      页数大小，默认20
     * @param area      考试区域
     * @param year      试卷年限
     * @param paperType 试卷类型，默认是真题
     * @return PaperListBean
     */
    @GET("p/v1/papers/list")
    Observable<PaperListBean> getPaperList(
            @Query("page") int page,
            @Query("size") int size,
            @Query("area") int area,
            @Query("year") int year,
            @Query("paperType") int paperType
    );

    /*
    查看指定区域的试题列表V2
     */
    @GET("p/v2/papers/list")
    Observable<PaperListBean> getPaperListV2(
            @Query("page") int page,
            @Query("size") int size,
            @Query("area") int area,
            @Query("year") int year,
            @Query("paperType") int paperType,
            @Query("subject") int subject);

    /**
     * 地区接口
     *
     * @param depth 取区域深度,1:省级别 2:市级别 默认:1
     * @return AreaBean
     */
    @Headers(RetrofitManager.CACHE_CONTROL_AGE + RetrofitManager.CACHE_STALE_LONG * 2)
    @GET("u/v1/users/areas")
    Observable<AreaBean> getAreaData(
            @Query("depth") int depth
    );

    /**
     * 录播课主页获取列表接口
     *
     * @param page       页码
     * @param categoryid 分类:公务员、事业单位、教师、医疗、金融
     * @param subjectid  科目ID:全部、行测、申论、面试、套餐
     * @param orderid    排序:最热、最新、课时升序、课时降序、价格升序、价格降序
     * @return
     */
/*
    @GET("c/v4/courses/recordings")
    Observable<VodCourseBean> getVodListCourse(@Query("page") int page,
                                               @Query("categoryid") int categoryid,
                                               @Query("subjectid") String subjectid,
                                               @Query("orderid") String orderid,
                                               @Query("keywords") String keyword);
*/

    /**
     * 教师列表接口
     * 教师
     */
    @GET("c/v3/courses/{courseId}/teachers")
    Observable<BaseListResponseModel<TeacherListBeans>> getTeacherList(@Path("courseId") int courseId);

    /**
     * 录播播放页面接口
     */
    @GET("c/v3/courses/{courseId}/secrinfo")
    Observable<BaseResponseModel<VodCourse>> getVodCPlay(@Path("courseId") int courseId);

    /**
     * 我的账户接口
     */
    @GET("/c/v3/account/balance")
    Observable<MyAccountBean> getMyAccount();

    /**
     * 我的账户之余额明细接口
     * type=1 收入
     * type=2 课程支出
     */
    @GET("/c/v3/account/records")
    Observable<BaseResponseModel<BalanceDetailData>> getBalanceDetail(@Query("type") int type, @Query("page") int page);

    /**
     * 我的账户之申论支出明细接口
     */
    @GET("e/api/v1/user/goods/detailList")
    Observable<BaseResponseModel<BalanceDetailData>> getEssayConsume(@Query("page") int page);

    /**
     * 我的账户之导出下载错题支出明细接口
     */
    @GET("/k/v4/errors/down/order/list")
    Observable<BaseResponseModel<BalanceDetailData>> getDeriveExerciseConsume(@Query("page") int page);


    /**
     * 录播播放页面接口
     */
    @GET("c/v3/courses/{courseId}/secrinfo")
    Observable<VodCoursePlayBean> getVodCoursePlay(@Path("courseId") int courseId);

    @GET("c/v3/courses/{courseId}/secrinfo")
    Observable<BaseResponseModel<VodCoursePlayBean.DataBean>> getVodCoursePlayV2(@Path("courseId") int courseId);

    /**
     * 课程大纲免费试听
     */
    @GET("c/v3/courses/{courseId}/secrinfo")
    Observable<VodCoursePlayBean> getCourseSyllabus(@Path("courseId") String courseId, @Query("isTrial") int isTrial);

    /**
     * 教师介绍接口
     */
    @GET("c/v3/teachers/{teacherId}")
    Observable<TeacherJieshaoBean> getTeacherJieshao(@Path("teacherId") int teacherId);

    /**
     * 老师综合得分
     */
    @GET("c/v3/teachers/{teacherId}/score")
    Observable<TeacherDefenBean> getTeacherDefen(@Path("teacherId") int teacherId);

    /**
     * 老师历史课程
     */
    @GET("c/v3/teachers/{teacherId}/score/history")
    Observable<TeacherLishiBean> getTeacherLishi(@Path("teacherId") int teacherId, @Query("page")
            int page);

    /**
     * 老师在售课程列表
     */
    @GET("c/v3/teachers/{teacherId}/courses")
    Observable<TeacherDetailListBean> getTeacherDetailList(@Path("teacherId") int teacherId, @Query("page")
            int page);

    /**
     * 评价列表接口
     */
    @GET("c/v3/evaluates")
    Observable<TeacherPingjiaBean> getTeacherPingjia(@Query("classid") int classid, @Query("lessionid") int lessionid, @Query("page")
            int page);

    /**
     * 获取自己对某个课程的评价
     */
    @GET("c/v3/evaluates/self")
    Observable<GetEvaluateBean> getEvaluateContent(@Query("classid") int classid, @Query("lessionid") int lessionid);

    /**
     * 提交评价
     */
    @POST("c/v3/evaluates")
    Observable<ExchangeVoucherBean> commitEvaluate(@Query("classid") int classid, @Query("lessionid")
            int lessionid, @Query("courseRemark") String courseRemark, @Query("coursescore") int coursescore, @Query("type") int type);

    /**
     * 我的课程订单列表接口
     */
    @GET("c/v5/order/userOrderList")
    Observable<MyV5OrderData> getMyOrder(@Query("chooseStatus") int chooseStatus, @Query("mini") int mini, @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 我的申论订单列表接口
     */
    @GET("/e/api/v2/user/order/list")
    Observable<EssayOrderBean> getMyEssayOrder(@Query("type") int type, @Query("page") int page);

    /**
     * 取消课程订单接口
     */
    @PUT("c/v5/order/{orderId}/cancelOrder")
    Observable<BaseListResponseModel<CancelOrderBean>> cancelMyOrder(@Path("orderId") String orderId);

    /**
     * 取消申论订单接口
     */
    @PUT("e/api/v2/user/cancel/{id}")
    Observable<BaseResponseModel<CancelOrderBean>> cancelMyEssayOrder(@Path("id") long id);

    /**
     * 删除课程订单接口
     */
    @DELETE("c/v5/order/{orderId}")
    Observable<BaseListResponseModel<OrderDetailData>> deleteMyOrder(@Path("orderId") String orderId, @Query("type") int type);

    /**
     * 删除申论订单接口
     */
    @DELETE("e/api/v2/user/order/{id}")
    Observable<BaseResponseModel<CancelOrderBean>> deleteMyEssayOrder(@Path("id") long id);

    /**
     * 获取订单物流列表
     */
    @GET("c/v5/order/{orderId}/orderLogistics")
    Observable<OrderLogisticBean> getOrderLogistic(@Path("orderId") String orderId);

    /**
     * 我的普通订单详情列表接口
     */
    @GET("/c/v5/order/{orderId}")
    Observable<BaseResponseModel<OrderDetailData>> getOrderDetail(@Path("orderId") String orderId);

    /**
     * 我的拼团订单详情列表接口
     */
    @GET("c/v5/order/{orderId}/wechat")
    Observable<BaseResponseModel<OrderDetailData>> getGroupOrderDetail(@Path("orderId") String orderId);


    /**
     * 订单详情页顶部的一条物流信息
     */
    @GET("c/v5/order/{orderId}/lastOrderLogistics")
    Observable<BaseResponseModel<LastLogisticData>> getLastLogistics(@Path("orderId") String orderId);


    @POST("/c/v4/orders/seckill/pay")
    Observable<BaseResponseModel<PayInfo>> payOrderSecKill(
            @Query("ordernum") String ordernum, @Query("payment") int payment);

    @POST("/c/v3/orders/seckill/payWay")
    Observable<BaseResponseModel<PayInfo>> getSecKillPaymentParams(
            @Query("ordernum") String ordernum);

    /**
     * 更改或绑定手机号接口
     */
    @PUT("/u/v2/users/mobile")
    Observable<UserInfoBean> changePhone(@Query("mobile") String mobile, @Query("captcha") String captcha);


    /**
     * 录播列表筛选条件
     */
    @GET("c/v3/recording/query/_settings")
    Observable<ShaixuanBean> getVodCourseShaixuan();

    @POST("/c/v3/refund")
    Observable<BaseResponseModel<OrderRefundResp>> applyRefund(
            @Query("ordernum") String ordernum, @Query("remark") String remark);

    @GET("/c/v3/refund")
    Observable<BaseResponseModel<OrderRefundResp>> getRefundDetail(
            @Query("ordernum") String ordernum);

    /**
     * 订单支付页面获取支付信息
     */
    @GET("/c/v3/orders/previnfo")
    Observable<BaseResponseModel<OrdersPrevInfo>> getOrdersPrevInfo(@Query("rid") String rid, @Query("pageSource") String pageSource);

    @POST("/c/v3/orders/create")
    Observable<BaseResponseModel<PayInfo>> createOrder(@Query("FreeCardID") String FreeCardID,
                                                       @Query("addressid") String addressid, @Query("fromuser") String fromuser,
                                                       @Query("rid") String rid, @Query("tjCode") String tjCode);

    @POST("/c/v6/order/create")
    Observable<BaseResponseModel<PayInfo>> createOrder(@Query("actualPrice") String actualPrice,@Query("goodsId")int goodsid, @Query("p") String screatMsg,
                                                       @Query("addressId") String addressid, @Query("pageSource") String fromuser, @Query("payment") String payment,
                                                       @Query("classId") String classId, @Query("source") int source, @Query("userProtocolId") String treatyId);

    @POST("/c/v6/order/continue")
    Observable<BaseResponseModel<PayInfo>> continuePayOrder(@Query("orderId") String orderId, @Query("payment") int payment, @Query("source") int source);


    @POST("/c/v3/orders/{orderNo}/pay")
    Observable<BaseResponseModel<PayInfo>> payOrder(@Path("orderNo") String orderNo, @Query("payment") int payment);

    /**
     * v723 课程订单详情页支付接口
     * orderId	订单id	number
     * payment	下单方式	number	1：图币支付 32微信支付 33支付宝支付
     * source	下单来源	number	1：ios 2：android 3小程序 4:pc 5:m
     */
    @POST("/c/v6/order/continue")
    Observable<BaseResponseModel<PayInfo>> payCourseOrder(@Query("orderId") long orderId, @Query("payment") int payment, @Query("source") int source);

    /**
     * 根据 红包id 获取红包信息
     */
    @GET("c/v5/redPackage/{id}/detail")
    Observable<BaseResponseModel<RedBagInfo>> getRedBagInfo(@Path("id") long id);

    /**
     * 获取红包分享信息
     */
    @POST("/pc/v1/shareRedPackage")
    Observable<BaseResponseModel<RedBagShareInfo>> getRedBagShareInfo(@Query("moneyNum") String moneyNum,
                                                                      @Query("redPackageId") long redPackageId,
                                                                      @Query("param") String param);

    /**
     * 提交一对一学员信息表
     */
    @POST("/c/v6/my/1v1/{rid}")
    Observable<SimpleResponseModel> setOneToOneInfo(@Path("rid") String rid,
                                                    @Body OneToOneInfoBean info);

    /**
     * 获取一对一信息表
     */
    @GET("/c/v6/my/1v1/{rid}")
    Observable<BaseResponseModel<OneToOneInfoBean>> getOneToOneInfo(@Path("rid") String rid,
                                                                    @Query("OrderNum") String OrderNum);


    @GET("/v3/checkIsBuyWithId")
    Observable<BaseResponseModel<PurchasedBean>> courseAlreadyPurchased(@Query("username") String username, @Query("rid") String rid);

    /**
     * 等级
     */
    @GET("/c/v3/my/level")
    Observable<LevelBean> getLevel();

    /**
     * 检查红包入口是否显示
     */
    @GET("/c/v5/redPackage/showRedEvn")
    Observable<BaseResponseModel<MyRedPacketBean>> checkRedPacketShow();

    /**
     * 检查用户是否有红包
     */
    @GET("c/v5/redPackage/checkRedEnv")
    Observable<BaseResponseModel<MyRedPacketBean>> checkRedPacket();

    /**
     * 签到查询
     */
    @GET("/u/v1/users/sign")
    Observable<SignBean> getSign();

    /**
     * 查询
     */
    @POST("/u/v1/users/sign")
    Observable<MySignBean> mySign();

    /**
     * 申论单题tab
     */
    @GET("/e/api/v3/single/questionTypeList")
    Observable<BaseListResponseModel<SingleExerciseTabData>> getSingleExerciseTab();

    /**
     * 申论单题列表
     */
    @GET("/e/api/v4/single/questionList/{type}")
    Observable<BaseResponseModel<SingleExerciseData>> getSingleExercise(@Path("type") int type, @Query("page") int page);

    /**
     * 批改列表删除item
     */
    @DELETE("e/api/v2/answer/{type}/{answerId}")
    Observable<BaseResponseModel<Object>> deleteCheckEssay(@Path("type") int type, @Path("answerId") Long answerId);

    /**
     * 单题和文章写作批改列表
     */
    @GET("/e/api/v4/answer/question/correctDetailList/{type}")
    Observable<BaseResponseModel<MyCheckData>> getMySingleCheck(@Path("type") int type, @Query("page") int page);

    /**
     * 套题批改列表   @GET("/e/api/v3/answer/correctDetailList/{type}")
     */
    @GET("/e/api/v3/answer/correctDetailList/{type}")
    Observable<BaseResponseModel<MyCheckData>> getMyCheck(@Path("type") int type, @Query("page") int page);

    /**
     * 申论套题tab
     */
    @GET("/e/api/v1/paper/areaList/")
    Observable<BaseListResponseModel<MultiExerciseTabData>> getMultiExerciseTab();

    /**
     * 申论套题列表
     */
    @GET("/e/api/v4/paper/list/{areaId}")
    Observable<BaseResponseModel<MultiExerciseData>> getMultiExercise(@Path("areaId") long areaId, @Query("page") int page, @Query("pageSize") int pageSize);

    //check

    // 查询商品（批改）
    @GET("/e/api/v3/goods/correctGoodsList")
    Observable<BaseResponseModel<CheckGoodData>> getCheckGoodsList();

    // 查询是否批改达到限制的次数
    @GET("/e/api/v1/answer/correct/{type}/{baseId}")
    Observable<BaseResponseModel<CheckCountNum>> getCheckCountNum(@Path("type") int type, @Path("baseId") long baseId);

    // 申论上传图片
    @Multipart
    @POST("/e/api/v1/answer/photo/{type}")
    Observable<BaseResponseModel<UpLoadEssayData>> sendEssayPicture(@Part MultipartBody.Part test, @Path("type") int type);

    // 人工批改，上传图片
    @Multipart
    @POST("/e/api/v2/answer/photo/{answerId}")
    Observable<BaseResponseModel<ImageUpResult>> updateEssayPicture(@Part MultipartBody.Part file,
                                                                    @Path("answerId") long answerId,
                                                                    @Query("sort") int sort);

    // 申论人工批改，修改图片顺序
    @PUT("/e/api/v2/answer/photo/sort")
    Observable<BaseResponseModel<Object>> changePictureSort(@Body List<EssayAnswerImageSortBean> imgAnswerList);

    // 申论人工批改，修改图片顺序
    @DELETE("/e/api/v1/correctImg/{id}")
    Observable<BaseResponseModel<Object>> deletePicture(@Path("id") long imgId, @Query("answerId") long answerId);

    // 我的页面广告
    @GET("/u/v5/users/bc/myOperation")
    Observable<BaseListResponseModel<AdvertiseConfig>> getMeAdv(@Query("category") int category);

    // 查询用户批改次数
    @GET("/e/api/v4/user/correctTimes")
    Observable<BaseResponseModel<CheckCountBean>> getCheckCountList();

    // 查询用户批改次数
    @GET("/e/api/v4/user/check/correctTimes")
    Observable<BaseResponseModel<CheckCountInfo>> getCheckCount(@Query("type") long type, @Query("id") long id);

    // 老师工作是否饱和 & 批改次数
    @GET("/e/api/v4/answer/check/{answerCardId}")
    Observable<BaseResponseModel<TeacherBusyStateInfo>> getTeacherBusyState(@Path("answerCardId") long answerCardId, @Query("correctMode") int correctMode, @Query("questionType") int questionType, @Query("id") long id);

    // 点击搜索结果获取答题卡状态和是否有批改次数
    @GET("/e/api/v4/user/goods/status/{type}")
    Observable<BaseResponseModel<EssaySearchAnswerCardStateInfo>> getAnswerCardState(@Path("type") int type, @Query("id") long id);

    // 获取用户的批改次数详情
    @GET("/e/api/v4/user/goods/detailList")
    Observable<BaseResponseModel<CheckCountDetail>> getCheckCountDetailList(@Query("goodsType") int goodsType, @Query("page") int page);

    // 获取申论套题批改报告
    @GET("/e/api/v3/paper/report/{answerId}")
    Observable<BaseResponseModel<EssayPaperReport>> getEssayPaperReport(@Path("answerId") long answerId);

    // 获取申论单题批改报告
    @GET("/e/api/v1/question/report/{answerId}")
    Observable<BaseResponseModel<EssayPaperReport>> getEssaySinglePaperReport(@Path("answerId") long answerId);


    //查询批改详情
    @GET("/e/api/v3/answer/correctDetail/{type}/{answerId}")
    Observable<BaseListResponseModel<CheckDetailBean>> getCheckDetail(@Path("type") int type, @Path("answerId") long answerId, @Query("correctMode") int correctMode);


    // good
    // 创建（保存）用户订单
    @POST("/e/api/v4/user/goodsOrder")
    Observable<BaseResponseModel<EssayPayInfo>> createCheckOrder(@Body CheckGoodOrderBean goodsOrder);


    // single

    // 查询单题地区列表。根据试题获取地区列表（一道题可以属于多个地区）
    @GET("/e/api/v2/single/areaList/{similarId}")
    Observable<BaseListResponseModel<SingleAreaListBean>> getSingleAreaListDetail(@Path("similarId") long similarId);

    // 查询单题材料列表
    @GET("/e/api/v1/single/materialList/{questionBaseId}")
    Observable<BaseListResponseModel<ExamMaterialListBean>> getSingleMaterialList(@Path("questionBaseId") long questionBaseId);

    /**
     * 查询单题详情。根据试题获取试题详情
     *
     * @param questionBaseId 单题Id
     * @param correctMode    批改方式
     * @param answerId       如果是人工答题退回修改答案，需要传答题卡Id
     * @param bizStatus      如果是人工答题退回修改答案，答题卡状态
     * @param modeType       需要知道是否是课后作业，默认传 1、表示非课后作业 2、表示课后作业
     */
    @GET("/e/api/v4/single/questionDetail/{questionBaseId}")
    Observable<BaseResponseModel<SingleQuestionDetailBean>> getSingleQuestionDetail(@Path("questionBaseId") long questionBaseId, @Query("correctMode") int correctMode, @Query("modeType") int modeType, @Query("answerId") long answerId, @Query("bizStatus") int bizStatus);

    // 查询试卷中材料列表。通过试卷id返回所有材料
    @GET("/e/api/v1/paper/materialList/{paperId}")
    Observable<BaseListResponseModel<ExamMaterialListBean>> getPaperMaterials(@Path("paperId") long paperId);

    /**
     * 查询试卷中问题列表。返回试卷的问题，如果有答题记录，同时返回未完成的答案
     *
     * @param paperId     试卷Id
     * @param correctMode 批改方式
     * @param answerId    如果是人工答题退回修改答案，需要传答题卡Id
     * @param bizStatus   如果是人工答题退回修改答案，答题卡状态
     * @param modeType    需要知道是否是课后作业，默认传 1、表示非课后作业 2、表示课后作业
     */
    @GET("/e/api/v4/paper/questionList/{paperId}")
    Observable<BaseResponseModel<PaperQuestionDetailBean>> getPaperQuestionDetail(@Path("paperId") long paperId, @Query("correctMode") int correctMode, @Query("modeType") int modeType, @Query("answerId") long answerId, @Query("bizStatus") int bizStatus);

    // 查询模考试卷材料列表。数据结构和套题一样。
    @GET("/e/api/v1/mock/materialList/{paperId}")
    Observable<BaseListResponseModel<ExamMaterialListBean>> getMockPaperMaterials(@Path("paperId") long paperId);

    // 查询试卷问题列表。
    @GET("/e/api/v1/mock/questionList/{paperId}")
    Observable<BaseResponseModel<PaperQuestionDetailBean>> getMockPaperQuestionDetail(@Path("paperId") long paperId);

    // 获取申论模考剩余时间
    @GET("/e/api/v1/mock/leftTime")
    Observable<BaseResponseModel<EssyMockRemindTime>> getMockRemainTime(@Query("paperId") long paperId);

    /**
     * 申论课后作业创建答题卡
     */
    @POST("/e/api/v1/course/exercises/answerCard")
    Observable<BaseResponseModel<CreateAnswerCardIdBean>> createHomeworkAnswerCardNew(@Body CreateAnswerCardPostBean createAnswerCardPostBean);

    /**
     * 新的创建答题卡
     */
    @POST("/e/api/v2/answer/answerCard")
    Observable<BaseResponseModel<CreateAnswerCardIdBean>> createAnswerCardNew(@Body CreateAnswerCardPostBean createAnswerCardPostBean);

    /**
     * 申论提交答案
     */
    @POST("/e/api/v2/answer/paperAnswerCard")
    Observable<BaseResponseModel<EssayCommitResponse>> paperCommit(@Body PaperCommitBean paperCommitBean);

    /**
     * 申论课后作业提交答案
     */
    @POST("/e/api/v1/course/exercises/answerCard/submit")
    Observable<BaseResponseModel<EssayCommitResponse>> paperHomeworkCommit(@Body PaperCommitBean paperCommitBean);

    @POST("/e/api/v1/collect")
    Observable<BaseResponseModel<Object>> setCollectEssay(@Query("type") int type, @Query("similarId") Long similarId, @Query("baseId") Long baseId);

    @GET("/e/api/v1/collect/check")
    Observable<BaseResponseModel<IsCheckData>> checkCollectEssay(@Query("type") int type, @Query("similarId") Long similarId, @Query("baseId") Long baseId);

    @DELETE("/e/api/v1/collect")
    Observable<BaseResponseModel<Object>> deleteCollectEssay(@Query("type") int type, @Query("similarId") Long similarId, @Query("baseId") Long baseId);

    // download
    @GET("/e/api/v2/file")
    Observable<BaseResponseModel<MaterialsFileUrlBean>> getMaterialsDownloadUrl(@Query("paperAnswerId") long paperAnswerId, @Query("paperId") long paperId,
                                                                                @Query("questionAnswerId") long questionAnswerId, @Query("questionBaseId") long questionBaseId);

    /**
     * 保存高端面授学员信息
     */
    @POST("/c/v3/my/highend")
    Observable<setMianShouBean> setHighend(@Body HighEndBean info);

    /**
     * 获取高端面授学员信息
     */
    @GET("/c/v3/my/highend")
    Observable<MianShouInfoBean> getHighend();

    /**
     * 高端面授赠送课程
     */
    @POST("/c/v3/my/highend/receive")
    Observable<BaseResponseModel<String>> getReceiveHighend();

    @GET("e/api/v1/mock/report")
    Observable<BaseResponseModel<EssayScReportBean>> getEssayReport(@Query("paperId") long paperId);

    /**
     * 我的申论模考历史报告
     */
    @GET("e/api/v1/mock/history")
    Observable<BaseResponseModel<SimulationScHistory>> getEssayScHistoryList(@Query("tag") int tag);

    // commit paper sc
    @POST("e/api/v1/mock/mockAnswerCard")
    Observable<BaseResponseModel<Object>> paperCommit_sc(@Body PaperCommitBean paperCommitBean);


    //    @POST("/pc/v3/share/practice")
    @POST("pc/v4/share/match")
    Observable<BaseResponseModel<ShareInfoBean>> getScReportUrl(@Query("id") long id, @Query("type") int type);

    /**
     * 新的行测模考分享接口
     */
    @POST("match/v1/share")
    Observable<BaseResponseModel<ShareInfoBean>> getScArenaReportShare(@Query("paperId") long paperId);

    /**
     * 录播列表切换考试筛选条件
     */
    @GET("/c/v4/recording/query/_settings")
    Observable<BaseListResponseModel<CourseCategoryBean>> getVodCourseFilter();

    /**
     * 录播筛选条件保存
     */
    @POST("/c/v4/recording/query/_settings")
    Observable<BaseResponseModel<String>> setVodCourseCategoryList(
            @Query("categories") String categories);

    @GET("/c/v6/my/cateList")
    Observable<BaseListResponseModel<CourseCategoryBean>> getCourseCategoryList();

    /**
     * 往期申论模考
     */
    @GET("/e/api/v1/mock/papers")
    Observable<BaseResponseModel<SimulationEssayPastPaperData>> getEssayPastMatchList(@Query("page") int page, @Query("tag") int tag, @Query("subjectId") int subjectId);

    /**
     * 往期行测模考(新)
     */
    @GET("/p/v4/matches/past")
    Observable<BaseResponseModel<ScHistoryPaperListData>> getNewScHistoryPaper(
            @Query("tag") int tag,
            @Query("page") int page,
            @Query("subject") int subject,
            @Query("subjectId") int subjectId
    );

    /**
     * 模考顶部课程(新)
     */
    @GET("/p/v4/matches/matchCourseInfo")
    Observable<BaseResponseModel<ScHistoryPaperTopData>> getNewScHistoryPaperTopCourse(@Query("subject") int subject, @Query("tag") int tag);

    /**
     * 申论试题搜索
     */
    @GET("/e/api/v3/search")
    Observable<BaseListResponseModel<EssaySearchData>> getEssaySearchData(@Query("content") String content, @Query("type") int type, @Query("page") int page);

    /**
     * 答题记录搜索类型
     */
    @GET("/p/v3/practices/cardType")
    Observable<BaseListResponseModel<RecordTypeData>> getRecordTypeData();

    @GET("/c/v4/common/champion")
    Observable<BaseResponseModel<Integer>> applyFaceToFace(@Query("admissionTicket") String id, @Query("areaId") String areaId,
                                                           @Query("examNum") String strScore, @Query("examSort") String strRank,
                                                           @Query("name") String name);

    @GET("/c/v4/common/championupdate")
    Observable<BaseResponseModel<String>> fillFaceToFaceInfo(@Query("hid") String hid,
                                                             @Query("phone") String phone, @Query("sid") String sid);

    @GET("/c/v4/common/championclasslist")
    Observable<BaseListResponseModel<FaceToFaceCourseBean>> getFaceToFaceFreeCourseList();

    @GET("/c/v4/common/champion/sendnetclasslist")
    Observable<BaseResponseModel<String>> getFaceToFaceFreeCourse();

    @POST("/c/v4/common/protocol/userProtocolInfo")
    Observable<BaseResponseModel<ProtocolResultBean>> sendProtocolInfo(
            @Query("examCertifacteNo") String examCertifacteNo,
            @Query("feeAccountName") String feeAccountName,
            @Query("feeAccountNo") String feeAccountNo,
            @Query("feeBank") String feeBank,
            @Query("forExam") String forExam,
            @Query("idCard") String idCard,
            @Query("protocolId") long protocolId,
            @Query("rid") String rid,
            @Query("sex") int sex,
            @Query("studentName") String studentName,
            @Query("telNo") String telNo
    );

    /**
     * 申论收藏列表
     * 单题
     */
    @GET("/e/api/v2/collect/list/{type}")
    Observable<BaseResponseModel<SingleExerciseData>> getEssayCollectSingleList(@Path("type") int type, @Query("page") int page);

    // 套题
    @GET("/e/api/v2/collect/list/{type}")
    Observable<BaseResponseModel<MultiExerciseData>> getEssayCollectMultiList(@Path("type") int type, @Query("page") int page);


    @PUT("/c/v6/my/category")
    Observable<BaseListResponseModel<CancelOrderBean>> setCourseCategoryList(
            @Query("setList") String setList);


    /**
     * 我的直播隐藏课程
     */
    @GET("c/v5/courses/{netClassId}/lastPlayLesson")
    Observable<BaseResponseModel<LastCourseBean>> getLastLesson(@Path("netClassId") String netClassId);

    /**
     * 扫一扫听课详情
     */
    @GET("/c/v6/lesson/{lessionId}")
    Observable<BaseResponseModel<ScanCourseData>> getScanCourseData(@Path("lessionId") String lessionId);

    /**
     * 我的-红包领取列表
     */
    @GET("c/v5/redPackage/redEnvelopeCore")
    Observable<BaseResponseModel<RedPacketBean>> getRedPacketData(@Query("userName") String userName);

    /**
     * 我的-红包领取详情
     */
    @GET("c/v5/redPackage/receiveDetails")
    Observable<BaseResponseModel<RedPacketDetailBean>> getRedPacketDetail(@Query("redEnvelopeId") long redEnvelopeId);

    /**
     * 红包提现倒计时
     */
    @GET("/c/v5/redPackage/sendVerify/{phone}")
    Observable<BaseResponseModel<RedConfirmCodeBean>> getRedConfirmCode(@Path("phone") String phone);

    /**
     * 红包提现到微信
     */
    @POST("c/v5/redPackage/withdrawWechat")
    Observable<BaseResponseModel<Object>> getForwardToWx(@Query("openid") String openid, @Query("phone") String phone, @Query("verify") String verify);

    /**
     * 红包提现到支付宝
     */
    @POST("/c/v5/redPackage/withdrawAli")
    Observable<BaseResponseModel<Object>> getForwardToALi(@Query("payeeAccount") String payeeAccount, @Query("phone") String phone, @Query("verify") String verify);

    /**
     * 活动详情
     *
     * @return
     */
    @GET("c/v5/courses/{classId}/appClassActivityDetails")
    Observable<BaseResponseModel<CourseActDetailBean>> getCourseActDetail(@Path("classId") String classId);

    /**
     * 获取精准估分、模考大赛中大礼包描述
     * 12是模考 13是精准估分
     */
    @GET("/p/estimate/estimateInfo")
    Observable<BaseResponseModel<GiftDescribeBean>> getEstiMateInfo(@Query("type") int type, @Query("subjectId") int subjectId);

    /**
     * 行测根据试卷Id，获得试卷下载路径
     */
    @POST("/pand/pdf/paper")
    Observable<BaseResponseModel<ArenaDownLoadUrlBean>> getArenaPaperDownloadUrl(@Query("paperId") long paperId);

    /**
     * 行测根据试卷Id,Id,Id，获得试卷最新信息，和本地试卷创建时间对比，以判断是否是最新试卷
     */
    @GET("/pand/paper/activity/list")
    Observable<BaseListResponseModel<ArenaPaperInfoNet>> getArenaPaperInfoList(@Query("ids") String ids);

    /**
     * 行测根据扫描卷子上的二维码得到试卷Id，然后获得答题卡信息
     */
    @GET("/p/v4/qCode/{paperId}")
    Observable<BaseResponseModel<RealExamBeans.RealExamBean>> getScanArenaPaper(@Path("paperId") long paperId);

    /**
     * 老师评价页课件列表
     *
     * @param teacherId
     * @param classId
     * @param page
     * @param pageSize
     * @return
     */
    @GET("c/v5/teacher/{teacherId}/historyLessonList")
    Observable<BaseResponseModel<HistoryCourseBean>> getHistoryLessons(@Path("teacherId") String teacherId, @Query("classId") String classId, @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 老师详情页课程列表
     *
     * @param teacherid
     * @param page
     * @param pageSize
     * @return
     */
    @GET("c/v5/teacher/{teacherId}/historyCourse")
    Observable<BaseResponseModel<HistoryCourseBean>> getHistoryCourses(@Path("teacherId") String teacherid, @Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 老师详情页课件评价列表
     *
     * @param teacherId
     * @param classId
     * @param lessionId
     * @param page
     * @param pageSize
     * @return
     */
    @GET("c/v5/teacher/{teacherId}/lessonEvaluateList")
    Observable<BaseResponseModel<CourseTeacherJudgeBean>> getLessonEvaluates(@Path("teacherId") String teacherId, @Query("classId") String classId, @Query("lessonId") String lessionId, @Query("page") int page, @Query("pageSize") int pageSize);

    @Multipart
    @POST("/u/v2/users/uploadLog")
    Observable<UploadImgBean> sendFeedBackLog(@Part MultipartBody.Part file);

    /**
     * 添加收藏
     *
     * @param classId
     * @return
     */
    @PUT("/c/v6/courses/collection/add")
    Observable<DeleteResponseBean> addCollection(@Query("classId") String classId);

    /**
     * 取消收藏
     *
     * @param classId
     * @return
     */
    @PUT("/c/v6/courses/collection/cancel")
    Observable<DeleteResponseBean> cancelCollection(@Query("classIds") String... classId);

    @GET("/c/v5/courses/{netClassId}/classAuditionList")
    Observable<BaseResponseModel<List<CourseOutlineItemBean>>> getAuditionList(@Path("netClassId") String classId);

    @GET("/pass/galaxy/video")
    Observable<DeleteResponseBean> reportVideoPlayInfo(@QueryMap HashMap<String, String> videoPlayInfo);

    @GET("c/v5/courses/{netClassId}/classSensors")
    Observable<BaseResponseModel<CourseInfoForStatistic>> getSensorsStatistic(@Path("netClassId") String netClassId);

    @GET("/c/v6/courses/status/{classId}")
    Observable<BaseResponseModel<BuyStatusData>> getBuyStatus(@Path("classId") String classId);

    /**
     * 上报直播记录
     *
     * @return
     */
    @POST("/c/v7/my/liveRecord")
    Observable<DeleteResponseBean> reportLiveRecord(@Body RequestBody requestBody);

    /**
     * 新的模考大赛，创建/获取答题卡
     *
     * @param paperId id
     * @return
     */
    @PUT("/match/v1/answerCard/{paperId}")
    Observable<RealExamBeans> getAnswerCard(@Path("paperId") long paperId);

    /**
     * 新的模考大赛，获取试题信息
     *
     * @param paperId id
     * @return
     */
    @GET("/match/v1/question/{paperId}/simpleInfo")
    Observable<ExerciseBeansNew> getPaper(@Path("paperId") long paperId);

    /**
     * 新的模考大赛，获取错题信息
     *
     * @param practiceId id
     * @return
     */
    @GET("/match/v1/answerCard/{practiceId}/getWrongQuestionAnalysis")
    Observable<BaseListResponseModel<ArenaExamQuestionBean>> getScWrongQuestion(@Path("practiceId") long practiceId);

    /**
     * 新的模考大赛，获取全部试题信息
     *
     * @param practiceId id
     * @return
     */
    @GET("/match/v1/answerCard/{practiceId}/getAllAnalysisInfo")
    Observable<BaseListResponseModel<ExerciseBeansNew.ExerciseBean>> getScAllQuestion(@Path("practiceId") long practiceId);

    /**
     * 新的获取行测模考大赛报告
     *
     * @param paperId id
     * @return
     */
    @GET("/match/v1/practices/{paperId}")
    Observable<RealExamBeans> getScArenaReport(@Path("paperId") long paperId);

    /**
     * 备考精华tab
     */
    @GET("/c/v1/exam/typeList")
    Observable<BaseListResponseModel<CreamArticleTabData>> getCreamArticleTab();

    /**
     * 备考精华列表
     */
    @GET("/c/v1/exam/{type}")
    Observable<BaseResponseModel<CreamArticleListResponse>> getCreamArticleData(@Path("type") int type, @Query("page") int page, @Query("size") int pageSize);

    /**
     * 收藏 备考精华文章
     */
    @POST("/c/v6/article")
    Observable<BaseResponseModel<CollectionResultBean>> collectArticle(@Query("articleId") long articleId);

    /**
     * 取消收藏 备考精华文章
     */
    @DELETE("/c/v6/article")
    Observable<BaseResponseModel<CollectionResultBean>> cancelCollectArticle(@Query("articleIds") long articleIds);

    /**
     * 备考精华文章详情
     */
    @GET("/c/v1/exam/detail/{aid}")
    Observable<BaseResponseModel<CreamArticleDetail>> getCreamArticleDetail(@Path("aid") long aid);

    /**
     * 备考精华文章点赞
     */
    @POST("/c/v1/exam/like/{aid}")
    Observable<BaseListResponseModel<PraiseData>> getPraiseDetail(@Path("aid") long aid, @Query("type") int type);


    /**
     * 面授课程职位列表
     */
    @Headers({"url_name:education"})
    @FormUrlEncoded
    @POST("/v1/offlineCourse/getzwlist_snj")
    Observable<F2fJobBean> getF2fJobList(@Field("params") String params);

    /**
     * 校验职位是否可以报名
     */
    @Headers({"url_name:education"})
    @FormUrlEncoded
    @POST("/v1/offlineCourse/checkzwstatus")
    Observable<F2fJobStatusBean> checkF2fJobstatus(@Field("params") String params);

    /**
     * 消息分类列表
     */
    @GET("/push/v3/notice/view")
    Observable<BaseListResponseModel<MessageGroupData>> getMessageGroupList();

    /**
     * 消息全部已读
     */
    @PUT("/push/v3/notice/readAll")
    Observable<BaseResponseModel<Object>> readAllMessage();

    /**
     * 删除消息分类
     */
    @DELETE("/push/v3/notice/view/{view}")
    Observable<BaseResponseModel<Object>> deleteMessageGroup(@Path("view") String view);

    /**
     * 删除单条消息
     */
    @DELETE("/push/v3/notice/{noticeId}")
    Observable<BaseResponseModel<Object>> deleteMessage(@Path("noticeId") String noticeId);

    /**
     * 小模考首页
     */
    @GET("/p/v4/small/estimate")
    Observable<BaseListResponseModel<SmallMatchBean>> getSmallMatch();

    /**
     * 阶段性测试首页
     */
    @GET("/p/v4/periodTest/{paperId}")
    Observable<BaseResponseModel<StageBean>> getStageTest(@Path("paperId") long paperId, @Query("syllabusId") long syllabusId);

    /**
     * 小模考报告列表
     */
    @GET("/p/v4/small/estimate/list")
    Observable<BaseResponseModel<SmallReportListBean>> getSmallMatchReportList(@Query("page") int page,
                                                                               @Query("size") int size,
                                                                               @Query("startTime") long startTime,
                                                                               @Query("endTime") long endTime);

    /**
     * 小模考报告
     */
    @GET("/p/v4/small/estimate/practice/{practiceId}")
    Observable<BaseResponseModel<SmallMatchReportBean>> getSmallMatchReport(@Path("practiceId") long practiceId);

    /**
     * 阶段性测试报告
     */
    @GET("/p/v4/periodTest/{practiceId}/base/report")
    Observable<BaseResponseModel<StageReportBean>> getStageReport(@Path("practiceId") long practiceId);

    /**
     * 阶段性测试报告
     */
    @GET("/p/v4/periodTest/{practiceId}/report")
    Observable<BaseResponseModel<StageReportOtherBean>> getStageOtherReport(@Path("practiceId") long practiceId, @Query("syllabusId") long syllabusId);

    /**
     * 是否是白名单 & 白名单可见的试卷
     */
    @GET("/p/v4/matches/look")
    Observable<BaseListResponseModel<PreviewPaperBean>> getPreviewPaper(@Query("subjectId") int subjectId);

    /**
     * 获取面授商品详情
     */
    @FormUrlEncoded
    @Headers({"url_name:education"})
    @POST("/v1/online/getcourseinfo/")
    Observable<BaseResponseModel<FaceGoodsDetailBean>> getFaceGoodsDetail(@Field("params") String params);

    /**
     * 获取面授课程详情
     */
    @Headers({"url_name:education"})
    @GET("/v1/online/coursedetail")
    Observable<BaseResponseModel<FaceClassDetailBean>> getFaceClassDetail(@Query("cityid") String cityid, @Query("courseid") String courseid, @Query("timestamp") String timestamp, @Query("sign") String sign);

    /**
     * 创建订单
     */
    @FormUrlEncoded
    @Headers({"url_name:education"})
    @POST("/v1/offlineCourse/createorder/")
    Observable<BaseResponseModel<FacePlaceOrderBean>> createFaceOrder(@Field("params") String params);

    /**
     * 获取拆分的订单
     */
    @FormUrlEncoded
    @Headers({"url_name:education"})
    @POST("/v1/offlineCourse/getsuborder/")
    Observable<BaseListResponseModel<SplitOrderBean>> getOrderChildren(@Field("params") String params);

    /**
     * 拆分订单
     */
    @FormUrlEncoded
    @Headers({"url_name:education"})
    @POST("/v1/offlineCourse/createsuborder")
    Observable<BaseListResponseModel<String>> postSplitOrder(@Field("params") String params);

    /**
     * @param answerId 答题卡ID
     * @param type     0单题1套题
     * @return
     */
    @POST("/e/api/v2/correct/convert")
    Observable<BaseResponseModel<CoverManulResBean>> postConvertManualCheck(@Query("answerId") long answerId, @Query("type") int type, @Query("delayStatus") int delayStatus);

    /**
     * 获取申论课后作业多道单题列表
     */
    @GET("/e/api/v1/course/exercises/list")
    Observable<BaseResponseModel<HomeworkSingleListBean>> getHomeworkSingleList(@Query("courseType") int courseType, @Query("courseWareId") long courseWareId, @Query("syllabusId") long syllabusId);

    /**
     * 课后作业申论报告 单题
     *
     * @param answerId
     * @return
     */
    @GET("/e/api/v1/course/exercises/question/report")
    Observable<BaseResponseModel<ExerciseReportBean>> exerciseSingleReport(@Query("answerId") long answerId, @Query("syllabusId") long syllabusId);

    /**
     * 课后作业申论报告 套题
     *
     * @param answerId
     * @return
     */
    @GET("/e/api/v1/course/exercises/paper/report")
    Observable<BaseResponseModel<ExerciseReportBean>> exerciseReport(@Query("answerId") long answerId, @Query("syllabusId") long syllabusId);

    /**
     * 预下单页
     *
     * @param classId
     * @param pageSource
     * @return
     */
    @GET("/c/v6/order/place")
    Observable<BaseResponseModel<OrdersPrevInfo>> getPreOrderInfo(@Query("classId") String classId, @Query("goodsId") int goodsId, @Query("pageSource") String pageSource);

    @POST("/c/v6/order/zero")
    Observable<BaseResponseModel<String>> payFreeOrder(@Query("classId") String classId, @Query("pageSource") String pageSource, @Query("source") int source);

}

