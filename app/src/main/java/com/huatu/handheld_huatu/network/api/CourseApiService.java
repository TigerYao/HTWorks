package com.huatu.handheld_huatu.network.api;

import android.text.TextUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.SimpleResponseModel;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchListResponse;
import com.huatu.handheld_huatu.business.faceteach.bean.FaceOrderListResponse;
import com.huatu.handheld_huatu.business.lessons.bean.SmallMatchClassBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherJudgeItemBean;
import com.huatu.handheld_huatu.business.play.bean.CourseTeacherListItemBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HomeworkData;
import com.huatu.handheld_huatu.business.ztk_vod.bean.SectionalExaminationBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CourseCalenderBean;
import com.huatu.handheld_huatu.mvpmodel.AllCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.BaseBooleanBean;
import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;
import com.huatu.handheld_huatu.mvpmodel.BaseStringBean;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.CalendarLiveBean;
import com.huatu.handheld_huatu.mvpmodel.CollectCourseWareListResponse;
import com.huatu.handheld_huatu.mvpmodel.CollectionBooleanBean;
import com.huatu.handheld_huatu.mvpmodel.CouponStatusBean;
import com.huatu.handheld_huatu.mvpmodel.CourseCollectBean;
import com.huatu.handheld_huatu.mvpmodel.CourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.CourseStageBean;
import com.huatu.handheld_huatu.mvpmodel.CourseWorkReportBean;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleFollowBean;
import com.huatu.handheld_huatu.mvpmodel.DateLiveListResponse;
import com.huatu.handheld_huatu.mvpmodel.FaceAreaBean;
import com.huatu.handheld_huatu.mvpmodel.InvoicResultBean;
import com.huatu.handheld_huatu.mvpmodel.InvoiceDetailBean;
import com.huatu.handheld_huatu.mvpmodel.PurchaseCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.mvpmodel.RecordCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.SimpleListResponse;
import com.huatu.handheld_huatu.mvpmodel.StudyBgBean;
import com.huatu.handheld_huatu.mvpmodel.StudyCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.StudyFilterBean;
import com.huatu.handheld_huatu.mvpmodel.SyllabusClassesBean;
import com.huatu.handheld_huatu.mvpmodel.TeacherBean;
import com.huatu.handheld_huatu.mvpmodel.TeacherTimeTable;
import com.huatu.handheld_huatu.mvpmodel.UnReadStudyBean;
import com.huatu.handheld_huatu.mvpmodel.UserMessageBean;
import com.huatu.handheld_huatu.mvpmodel.VideoStudyReportBean;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.mvpmodel.me.LogisticBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWarePointBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.DanmuBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.InClassAnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.InClassEssayCardBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.PeriodTestBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.PointExercisesBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.ReportIntBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.StringUtils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by xing on 2018\5\16 0016.
 */

public class CourseApiService {


    public class DownToken {
        public String token;
    }

    public class BooleanResult {
        public boolean result;
        public int isFinish;
    }

    public interface APIService {
     /*   @POST("hbase/video/process")
        Observable<BaseResponseModel<String>> saveBaijiaCourseProgress(@Query("playTime") long playTime, @Query("roomId") String roomId, @Query("sessionId") String sessionId, @Query("wholeTime") long wholeTime);

        @POST("hbase/video/process")
        Observable<BaseResponseModel<String>> saveGeeseeProgress(@Query("playTime") long playTime, @Query("joinCode") String joinCode, @Query("wholeTime") long wholeTime);


 Observable<BaseResponseModel<CouponStatusBean>> getGifCourseStatus2(@Url String test);

        @POST("hbase/video/process")  http://123.103.86.52:11146/
        Observable<BaseResponseModel<String>> saveRecordCourseProgress(@Query("playTime") long playTime, @Query("videoIdWithoutTeacher") String videoIdWithoutTeacher, @Query("videoIdWithTeacher") String videoIdWithTeacher, @Query("wholeTime") long wholeTime);
*/


        @Headers({"url_name:education"})
        @GET("/v1/online/catalogurl")
        Observable<BaseResponseModel<BaseStringBean>> getEducationCatalogurl(@Query("areaid") String areaid, @Query("catid") String catid
                , @Query("timestamp") String timestamp, @Query("typeid") int typeid, @Query("sign") String sign);


        @Headers({"url_name:education"})
        @GET("/v1/online/examcategory")
        Observable<BaseListResponseModel<FaceAreaBean>> getEducationExamCategory();

        @Headers({"url_name:education"})
        @GET("/v1/online/arealist")
        Observable<BaseListResponseModel<FaceAreaBean>> getEducationAllArea();

        @FormUrlEncoded
        @Headers({"url_name:education"})
        @POST("/v1/offlineCourse/getallorder")
        Call<FaceOrderListResponse> getEducationOrderByType(@Field("params") String params);

/*

        Call<FaceOrderListResponse> getEducationOrderByType(@Query("mid") String mid, @Query("bmmid") String bmmid
                , @Query("page") int page, @Query("rows") int rows, @Query("ostate") int ostate, @Query("timestamp") String timestamp, @Query("sign") String sign);*/


        @Headers({"url_name:education"})
        @GET("/v1/online/getmid")
        Observable<BaseResponseModel<BaseStringBean>> getEducationMid(@Query("mobile") String mobile, @Query("timestamp") String timestamp, @Query("sign") String sign);

        @Headers("Cache-Control: public, max-age=1800, max-stale=1800")
        @GET("/u/v1/users/checkRegisterGiveCourseStatus")
        Observable<BaseResponseModel<CouponStatusBean>> getGifCourseStatus();

        @POST("hbase/video/process/syllabus")
        Observable<BaseResponseModel<String>> saveCourseProgress(@Body Object object);

        @GET("c/v3/courses/{courseId}/secrinfo")
        Observable<BaseResponseModel<VodCoursePlayBean.DataBean>> getVodCoursePlayV2(@Path("courseId") long courseId);


        //0在线1教育
        @POST("e/api/v3/goods/getAliPaySign")
        Call<BaseResponseModel<String>> getAliPaySign(@Query("flag") int flag, @Body Object object);


        /**
         * 课程大纲免费试听
         */
        @GET("c/v3/courses/{courseId}/secrinfo")
        Observable<BaseResponseModel<VodCoursePlayBean.DataBean>> getCourseSyllabusV2(@Path("courseId") String courseId, @Query("isTrial") int isTrial);


        @GET("c/v4/evaluates/token")
        Observable<BaseResponseModel<DownToken>> refreshRecordToken(@Query("videoId") long videoId);

        @GET("c/v4/evaluates/token")
        Observable<BaseResponseModel<DownToken>> refreshPlaybackToken(@Query("bjyRoomId") long roomId, @Query("bjySessionId") long sessionId);

        @GET("c/v4/evaluates/token")
        Observable<BaseResponseModel<DownToken>> refreshPlaybackToken(@Query("bjyRoomId") long roomId);

        @GET("c/v4/evaluates/lession")
        Observable<BaseResponseModel<BooleanResult>> checkLessionEvalute(@Query("lessionId") String lessionId, @Query("syllabusId") String syllabusId);

        @PUT("c/v5/courses/addPlayIcon")
        Observable<SimpleResponseModel> getCoursePlayRewardInfo(@Query("isFree") int isFree);


        /**
         * 录播课主页获取列表接口
         *
         * @param page       页码
         * @param categoryid 分类:公务员、事业单位、教师、医疗、金融
         * @param subjectid  科目ID:全部、行测、申论、面试、套餐
         * @param orderid    排序:最热、最新、课时升序、课时降序、价格升序、价格降序
         * @return
         */
        @GET("c/v4/courses/recordings")
        Call<RecordCourseListResponse> getVodListCourse(@Query("page") int page,
                                                        @Query("categoryid") int categoryid,
                                                        @Query("subjectid") String subjectid,
                                                        @Query("orderid") String orderid,
                                                        @Query("keywords") String keyword);

        /*
                @GET("/c/v3/courses/lives")
                Call<RecordCourseListResponse> getLiveCourses(@Query("category") String category,
                                                              @Query("keywords") String keywords, @Query("orderid") int orderid,
                                                              @Query("priceid") int priceid, @Query("page") int page) ;*/
        //课程搜索
        @GET("/c/v6/courses/search")
        Call<CourseListResponse> getAllCoursesSearch(@Query("keyWord") String keyWord, @Query("cateId") int cateId, @Query("page") int page, @Query("pageSize") int pageSize, @Query("isRecommend") int isRecommend, @Query("isHistory") int isHistory);


        //课程列表  max-stale在请求头设置有效，在响应头设置无效。(因为max-stale是请求头设置参数,参考上面的缓存相关的知识第二个链接)
        @Headers("Cache-Control: public, max-age=3600, max-stale=3600")
        @GET("/c/v6/courses/listNew")
        Call<AllCourseListResponse> getAllCacheCourses(@Query("cateId") int cateId);

        @Headers("If-Modified-Since: 1560143738282")
        @GET("/c/v6/courses/listNew")
        Call<AllCourseListResponse> getAllCourses(@Query("cateId") int cateId);

        // 查看更多课程列表
        @GET("/c/v6/courses/detailNew")
        Call<CourseListResponse> getCourseList(@Query("cateId") int cateId, @Query("page") int page, @Query("pageSize") int pageSize, @Query("typeId") String typeId);

        @GET("/e/api/v3/search")
        Call<EssaySearchListResponse> getEssaySearchList(@Query("content") String content, @Query("type") int type, @Query("page") int page, @Query("pageSize") int pageSize);

        // 合集课程列表
        @GET("/c/v6/courses/collectNew")
        Call<CourseListResponse> getCollectCourseList(@Query("collectId") String collectId, @Query("page") int page, @Query("pageSize") int pageSize);
        /**
         * 我的课程
         */
 /*       @GET("/c/v3/my/courses")
        Call<MyCourseListResponse> getMyCourseList(@Query("type") int type, @Query("page") int page);*/


  /*      @GET("/c/v3/my/courses")
        Call<MyCourseListResponse> searchMyCourseList(@Query("keywords") String keywords,
                                                                         @Query("type") int type, @Query("page") int page);*/

        /**
         * 我的订单列表接口
         */
   /*     @GET("/c/v3/orders/my")
        Call<MyOrderListResponse> getMyOrder(@Query("type") int type, @Query("page") int page);*/


        //课程-获取课程大纲（售后）
      /*  @GET("/c/v5/courses/{classId}/purchasedClassSyllabus")
        Call<PurchaseCourseListResponse> getPurchasedClassSyllabus(@Path("classId") long classId, @Query("page") int pageIndex, @Query("pageSize") int pageSize, @Query("parentId") int parentId);*/

     /*   @GET("/c/v5/syllabus/{classId}/buyAfterSyllabus")
        Call<PurchaseCourseListResponse> getPurchasedClassSyllabus(@Path("classId") long classId,@Query("coursewareNodeId") String coursewareNodeId, @Query("page") String pageIndex
                , @Query("pageSize") int pageSize, @Query("classNodeId") String classNodeId,@Query("teacherId") String teacherId,@Query("position") int position);
*/
        @GET("/c/v7/syllabus/{netClassId}/buyAfterSyllabus")
        Call<PurchaseCourseListResponse> getPurchasedClassSyllabusV6(@Path("netClassId") long classId, @Query("classNodeId") String classNodeId, @Query("coursewareNodeId") String coursewareNodeId,
                                                                     @Query("nextClassNodeId") String nextClassNodeId,
                                                                     @Query("nextCoursewareNodeId") String nextCoursewareNodeId,//更多按钮的上一条记录id
                                                                     @Query("afterNodeId") String afterNodeId, @Query("beforeNodeId") String beforeNodeId,
                                                                     @Query("pageSize") int pageSize,
                                                                     @Query("parentNodeId") String parentNodeId,
                                                                     @Query("stageNodeId") String stageNodeId,//阶段

                                                                     @Query("teacherId") String teacherId, @Query("position") int position);

      /*  @GET("/c/v5/syllabus/{courserId}/buyAfterSyllabus")
        Call<PurchaseCourseListResponse> getbuyAfterSyllabus(@Path("courserId") long courserId, @Query("classId") String classId,
                                                             @Query("classNodeId") String classNodeId, @Query("page") String page, @Query("pageSize") String pageSize, @Query("teacherId") String teacherId);
*/

        //售后大纲老师列表
        @GET("/c/v6/syllabus/{netClassId}/syllabusTeachers")
        Observable<BaseListResponseModel<TeacherBean>> getSyllabusTeachers(@Path("netClassId") int netClassId, @Query("classNodeId") String classNodeId, @Query("stageNodeId") String stageNodeId);


        @GET("/c/v5/courses/{classId}/purchasedClassSyllabus")
        Observable<BaseResponseModel<PurchasedCourseBean>> getSimpleClassSyllabus(@Path("classId") long classId, @Query("page") int pageIndex, @Query("pageSize") int pageSize, @Query("parentId") int parentId);

        //我的-获取我的课程-未删除  //@Query("cateId") int cateId,
        @GET("/c/v5/my/getMyNotDeletedClasses")
        Call<BuyCourseListResponse> getMyCourses(@Query("keyWord") String keyWord, @Query("page") int pageIndex, @Query("pageSize") int pageSize);

        @GET("/c/v6/my/courses")
        Call<StudyCourseListResponse> getMyStudyCourses(@Query("isDelete") int isDelete, @Query("examStatus") String examStatus, @Query("priceStatus") String priceStatus, @Query("recentlyStudy") int recentlyStudy,
                                                        @Query("studyStatus") String studyStatus, @Query("teacherId") String teacherId,
                                                        @Query("page") int pageIndex, @Query("pageSize") int pageSize);


        @GET("/c/v6/my/courses")
        Call<BuyCourseListResponse> getMyRecyleCourses(@Query("keyWord") String keyWord, @Query("isDelete") int isDelete, @Query("examStatus") String examStatus, @Query("priceStatus") String priceStatus, @Query("recentlyStudy") int recentlyStudy,
                                                       @Query("studyStatus") String studyStatus, @Query("teacherId") String teacherId,
                                                       @Query("page") int pageIndex, @Query("pageSize") int pageSize);


        //课程-置顶课程
        @POST("/c/v5/courses/{classId}/topCourse")
        Observable<BaseResponseModel<BaseBooleanBean>> setCourseTop(@Path("classId") long classId, @Query("orderId") String orderId, @Query("cateId") int cateId);

        @DELETE("/c/v5/courses/{classId}/deleteTopCourse")
        Observable<BaseResponseModel<BaseBooleanBean>> cancelCourseOnTop(@Path("classId") long classId, @Query("orderId") String orderId);

        @DELETE("/c/v5/courses/{classId}")
        Observable<BaseResponseModel<BaseBooleanBean>> deleteCourse(@Path("classId") long classId, @Query("orderId") String orderId);


        @PUT("/c/v5/courses/{classId}")
        Observable<BaseResponseModel<BaseBooleanBean>> recoveryDelCourse(@Path("classId") long classId, @Query("orderId") String orderId);

        //课程-彻底删除回收站中的课程
        @DELETE("c/v5/courses/{orderId}/deepDeleteCourse")
        Observable<SimpleResponseModel> deleteRecoveryCourse(@Path("orderId") String orderId);

        @GET("c/v5/courses/{courseId}/handouts")
        Call<SimpleListResponse<HandoutBean.Course>> getHandoutInfo(@Path("courseId") int courseId ,@Query("download") String download);

        @GET("c/v5/courses/{courseId}/handouts")
        Observable<SimpleListResponse<HandoutBean.CourseGroup>> getGroupHandoutInfo(@Path("courseId") String courseId);


        //弹幕-新增弹幕
        @POST("/c/v5/barrages")
        Observable<BaseResponseModel<String>> addDanmu(@Query("background") int background, @Query("classId") long classId, @Query("content") String content, @Query("lessonId") long lessonId, @Query("videoNode") String videoNode);

        @GET("/c/v5/barrages")
        Observable<BaseResponseModel<DanmuBean>> getDanmuList(@Query("classId") long classId, @Query("lessonId") long lessonId, @Query("page") int pageIndex, @Query("pageSize") int pageSize, @Query("videoNode") int videoNode);

        //课程-获取 qq群、课程学习总进度


        @GET("/c/v5/courses/{classId}/getQqGroupSchedule")
        Observable<BaseResponseModel<CourseInfoBean>> getCourseLearnPercent(@Path("classId") String classId);

        //获取随堂练习节点
        @GET("/c/v1/breakPoint/{courseType}/{courseId}/listForAndroid")
        Observable<BaseListResponseModel<CourseWarePointBean>> getInClassPracticePoints(@Path("courseType") int courseType, @Path("courseId") long courseId);


        //app横屏课件列表
        @GET("/c/v5/courses/{classId}/getChooseCourseWare")
        Observable<BaseResponseModel<CourseWareBean>> getAllCourseWare(@Path("classId") long classId, @Query("page") int pageIndex, @Query("pageSize") int pageSize);

        @GET("/c/v5/courses/{classId}/getChooseCourseWare")
        Call<WarpListResponse<CourseWareInfo>> getAllCourseWarebyPage(@Path("classId") long classId, @Query("page") int pageIndex, @Query("pageSize") int pageSize);


      /*  @GET("/c/v1/breakPoint/{lessionId}")
        Observable<BaseListResponseModel<PointExercisesBean>> getLearnPointInfo(@Path("lessionId") long lessionId);*/

        @GET("/q/v3/questions/batch")
        Observable<BaseListResponseModel<PointExercisesBean>> getLearnPointInfo(@Query("ids") long ids);

        //课件播放-课中练习-生成答题卡
        @GET("/c/v1/breakPoint/{videoType}/{lessionId}/cardForAndroid")
        Observable<BaseResponseModel<InClassAnswerCardBean>> createPracticesInfo(@Path("lessionId") long lessionId, @Path("videoType") int videoType);

        //课件播放-课后练习-创建课后练习题卡
        @GET("/c/v2/exercises/{videoType}/{lessionId}/card")
        Observable<BaseResponseModel<InClassAnswerCardBean>> createAfterPracticesInfo(@Path("lessionId") long lessionId, @Path("videoType") int videoType, @Query("courseId") String courseId, @Query("syllabusId") String syllabusId);


        //根据大纲获取 试题信息 - 申论
        @GET("/c/v7/my/courseWork/questionInfo/{videoType}/{courseWareId}")
        Observable<BaseResponseModel<InClassEssayCardBean>> getEssayPagerInfo(@Path("videoType") int videoType, @Path("courseWareId") long courseWareId, @Query("answerCardId") long answerCardId, @Query("syllabusId") String syllabusId);


        //阶段测试-创建答题卡/继续答题
        @POST("/p/v4/periodTest/practice/{paperId}")
        Observable<BaseResponseModel<PeriodTestBean>> createPeriodTestInfo(@Path("paperId") String paperId, @Query("syllabusId") String syllabusId);


        @GET("/p/v4/periodTest/{syllabusId}/{paperId}/getPracticeId")
        Observable<BaseResponseModel<Long>> checkhasAnswerCard(@Path("paperId") String paperId, @Path("syllabusId") String syllabusId);

        /**
         * 提交答题卡
         *
         * @param answerCardId 练习id/答题卡id
         * @param answers      答题卡数据信息
         * @return
         */
        @PUT("p/v1/practices/{practiceId}")
        Observable<BaseResponseModel<String>> submitAnswerCard(@Path("practiceId") String answerCardId, @Body JsonArray answers);

        /**
         * 保存答题卡
         *
         * @param answerCardId 练习id/答题卡id
         * @param answers      答题卡数据信息
         * @return
         */
        @PUT("p/v1/practices/{practiceId}/answers")
        Observable<SimpleResponseModel> saveAnswerCard(@Path("practiceId") long answerCardId, @Body JsonArray answers);


        @GET("/c/v5/courses/{courseId}/getCourseTeacherInfo")
        Call<SimpleListResponse<CourseTeacherListItemBean>> getCourseTeacherList(@Path("courseId") String courseId);


        @GET("/c/v5/evaluation/getClassEvaluation")
        Call<WarpListResponse<CourseTeacherJudgeItemBean>> getCourseJudgeList(
                @Query("classId") String classId, @Query("isLive") int isLive,
                @Query("page") int page, @Query("pageSize") int pageSize);


        /**
         * 课程详情
         */
        @GET("/c/v5/my/getLiveCalendar")
        Observable<BaseListResponseModel<CourseCalenderBean>> getCourseCalender();

        @POST("/c/v5/evaluation/submit")
        Observable<SimpleResponseModel> judgeCourse(@Query("classId") String classid, @Query("lessonId")
                String lessonId, @Query("evaluation") String evaluation, @Query("score") int score, @Query("parentId") String parentId);


        @GET("/c/v3/logistics")
        Observable<BaseListResponseModel<LogisticBean>> getAllLogistics();

        //        @GET("/push/v2/notice/noticeList") //?page=1&size=3
//        Call<WarpListResponse<UserMessageBean>> getMessagelist(@Query("page") int pageIndex, @Query("size") int pageSize);
        //单类消息
        @GET("/push/v3/notice/{view}/noticeList")
        //?page=1&size=3
        Call<WarpListResponse<UserMessageBean>> getMessagelist(@Path("view") String view, @Query("page") int pageIndex, @Query("size") int pageSize);

        @PUT("/push/v2/notice/hasRead")
        Observable<SimpleResponseModel> setMessageRead(@Query("noticeId") long noticeId);

        @GET("/push/v2/notice/unReadCount")
        Observable<BaseResponseModel<String>> getMessageUnRead();


        @GET("/c/v5/syllabus/{courseId}/syllabusClasses")
        Observable<BaseListResponseModel<SyllabusClassesBean>> getAllSyllabusClasse(@Path("courseId") int courseId);

        @GET("/c/v6/my/filteredCourses")
        Observable<BaseResponseModel<StudyFilterBean>> getfilteredCourses();


        @GET("/c/v6/courses/calendarDetail")
        Call<DateLiveListResponse> getCalendarDetailBydate(@Query("date") String date, @Query("id") String id);

        @GET("/c/v6/my/learnCalendar")
        Observable<BaseListResponseModel<CalendarLiveBean>> getLearnCalendar(@Query("type") String type, @Query("date") String date);

        @GET("/c/v6/courses/collection/list")
            //?page=1&size=3
        Call<WarpListResponse<CourseCollectBean>> getMycollectionList(@Query("page") int pageIndex, @Query("pageSize") int pageSize);

        @GET("/c/v6/courses/collection/list")
            //?page=1&size=3
        Observable<WarpListResponse<CourseCollectBean>> getMycollectionList3(@Query("page") int pageIndex, @Query("pageSize") int pageSize);

        @GET("/c/v6/article")
            //?page=1&size=3
        Observable<WarpListResponse<CreamArticleFollowBean>> getMyArticlecollectList(@Query("page") int pageIndex, @Query("pageSize") int pageSize);


        @GET("/c/v6/lesson/collection")
            //?page=1&size=3
        Call<CollectCourseWareListResponse> getCourseWareCollectList(@Query("page") int pageIndex, @Query("pageSize") int pageSize);

        @PUT("/c/v6/courses/collection/cancel")
        Observable<SimpleResponseModel> cancelCollectionCourse(@Query("classIds") String classIds);


        @GET("/c/v6/syllabus/{netClassId}/syllabusClasses")
        Observable<BaseResponseModel<CourseStageBean>> getSyllabusClasses(@Path("netClassId") String netClassId, @Query("stageNodeId") String stageNodeId);


        /**
         * 接口名称 课后练习列表
         *
         * @return
         */
        @GET("/c/v7/my/courseWork/{type}/detailList")
        Observable<BaseResponseModel<HomeworkData>> getHomeworkLists(@Path("type") int type, @Query("page") int page, @Query("pageSize") int pageSize);
//        Observable<BaseResponseModel<List<HomeworkBean>>> getHomeworkLists(@Query("page") int page, @Query("pageSize") int pageSize, @Path("type") int type);

        /**
         * 接口名称 阶段测试列表
         *
         * @return
         */
     /*   @GET("/c/v6/my/periodTest/detailList")
        Observable<BaseResponseModel<SectionalExaminationBean>> getSectionalExaminationBeans(@Query("page") int page, @Query("pageSize") int pageSize);*/
        @GET("/c/v6/my/periodTest/detailList")
        Call<WarpListResponse<SectionalExaminationBean.ListBean>> getMySectionalExamList(@Query("page") int page, @Query("pageSize") int pageSize);

        /**
         * 接口名称 课后作业--全部已读
         *
         * @param type
         * @return
         */
//        @PUT("/c/v6/my/allRead/{type}")
        @PUT("/c/v7/my/allRead/{type}")
        Observable<SimpleResponseModel> getAllRead(@Path("type") String type);

        /**
         * 接口名称 阶段测试---单条已读
         *
         * @param syllabusId
         * @param courseId
         * @return
         */
        @PUT("/c/v6/my/oneRead/periodTest/{syllabusId}/{courseId}")
        Observable<SimpleResponseModel> getRead(@Path("syllabusId") int syllabusId, @Path("courseId") int courseId);

        /**
         * 接口名称 课后作业---单条已读
         *
         * @param type
         * @param syllabusId
         * @return
         */
//        @PUT("/c/v6/my/oneRead/courseWork/{courseType}/{courseWareId}")
        @PUT(" /c/v7/my/oneRead/courseWork/{type}/{syllabusId}")
        Observable<SimpleResponseModel> getHomeworkRead(@Path("type") int type, @Path("syllabusId") long syllabusId);

        /**
         * 接口名称 小模考-历史解析课列表
         *
         * @param page
         * @param size
         * @return
         */
        @GET("/c/v6/courses/analysisClassList")
        Observable<BaseResponseModel<SmallMatchClassBean>> getAnalysisClassList(@Query("page") int page, @Query("size") int size);

        @GET("/c/v6/my/courseWork/{id}")
        Observable<BaseResponseModel<CourseWorkReportBean>> getCourseWorkReport(@Path("id") long practice_id);

        //        @GET("/c/v6/my/unFinishNum")
        @GET("/c/v7/my/unFinishNum")
        Observable<BaseResponseModel<UnReadStudyBean>> getUnreadStudyMessageNum();

        //  /c/v6/my/courseWork/1
        @GET("/c/v6/my/learnReport")
        Observable<BaseResponseModel<VideoStudyReportBean>> getVideoStudyReport(@Query("bjyRoomId") String bjyRoomId,
                                                                                @Query("classCardId") int classCardId,
                                                                                @Query("classId") int classId,
                                                                                @Query("exerciseCardId") long exerciseCardId,
                                                                                @Query("lessonId") int lessonId,
                                                                                @Query("netClassId") String netClassId,
                                                                                @Query("videoType") int videoType,
                                                                                @Query("reportStatus") int reportStatus,
                                                                                @Query("syllabusId") String syllId);

        @GET("/c/v4/common/class/token")
        Observable<BaseResponseModel<DownToken>> getPlaybackToken(@Query("coursewareId") long coursewareId, @Query("netClassId") String netClassId, @Query("videoType") int videoType);


        @POST("/c/v6/lesson/collection")
        Observable<SimpleResponseModel> collectCourseWare(@Query("classId") int classId, @Query("lessonId") int lessonId, @Query("syllabusId") String syllabusId, @Query("videoType") int videoType);

        @DELETE("/c/v6/lesson/collection")
        Observable<SimpleResponseModel> unCollectCourseWare(@Query("syllabusId") String syllabusId);

        @DELETE("/c/v6/article")
        Observable<SimpleResponseModel> unCollectArticles(@Query("articleIds") String articleIds);


        @GET("/c/v6/lesson/isCollection")
        Observable<BaseResponseModel<CollectionBooleanBean>> isCollection(@Query("syllabusId") String syllabusId);


        @GET("/c/v6/my/learnReport/{videoType}/{courseWareId}")
        Observable<BaseResponseModel<ReportIntBean>> getLearnReportStatus(@Path("videoType") int videoType, @Path("courseWareId") int courseWareId);


        @POST("/c/v6/invoice/email")
        Observable<BaseResponseModel<ReportIntBean>> sendInvoiceEmail(@Query("email") String email, @Query("orderId") String orderId);

        @POST("/c/v6/invoice/write")
        Observable<BaseResponseModel<InvoicResultBean>> sendInvoiceRequest(@Query("invoiceContent") String invoiceContent,
                                                                           @Query("invoiceMoney") String invoiceMoney,
                                                                           @Query("invoiceTitle") String invoiceTitle,
                                                                           @Query("invoiceType") int invoiceType,//1电子发票2纸质发
                                                                           @Query("orderId") String orderId,
                                                                           @Query("orderNum") String orderNum,
                                                                           @Query("taxNum") String taxNum,        //纳税人识别号
                                                                           @Query("titleType") int titleType,
                                                                           @Query("orderDate") String orderDate);//抬头类型1个人2单位

        @POST("/c/v6/invoice/detail")
        Observable<BaseResponseModel<InvoiceDetailBean>> getInvoiceDetail(@Query("orderId") String orderId);

        @GET("/u/v5/users/bc/study")
        Observable<BaseListResponseModel<StudyBgBean>> getStudyBgDetail(@Query("category") int category);



        //  http://123.103.86.52/c/v1/snj/lessonTable/getTimeTableByGoodsId/61649
        @POST("/c/v1/snj/lessonTable/getTimeTableByGoodsId/{goodsId}")
        Observable<BaseListResponseModel<TeacherTimeTable>> getTeacherTimeTable(@Path("goodsId") String goodsId);


    }

    private static APIService mInstance;

    public static APIService getApi() {
        if (mInstance == null) {
            synchronized (CourseApiService.class) {
                if (mInstance == null) {
                    mInstance = RetrofitManager.getInstance().buildService(APIService.class);
                }
            }
        }
        return mInstance;
    }


    public static Observable<BaseResponseModel<String>> saveBaijiaCourseProgress(String syllabusId, long playTime, long onlineTime, String roomId, String sessionId, long wholeTime) {

        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("playTime", playTime);
        payerReg.addProperty("roomId", roomId);
        payerReg.addProperty("userPlayTime", onlineTime);
        payerReg.addProperty("sessionId", sessionId);
        payerReg.addProperty("wholeTime", wholeTime);
        if (!TextUtils.isEmpty(syllabusId))
            payerReg.addProperty("syllabusId", syllabusId);
        return getApi().saveCourseProgress(payerReg);
    }

//    public static Observable<BaseResponseModel<String>> saveGeeseeProgress( long playTime,long onlineTime, String joinCode,  long wholeTime) {
//
//        JsonObject payerReg = new JsonObject();
//        payerReg.addProperty("playTime", playTime);
//        payerReg.addProperty("userPlayTime", onlineTime);
//        payerReg.addProperty("joinCode", joinCode);
//        payerReg.addProperty("wholeTime", wholeTime);
//
//        return getApi().saveCourseProgress( payerReg);
//    }

    public static Observable<BaseResponseModel<String>> saveRecordCourseProgress(String joinCode, String roomId, String sessionId, String syllabusId, long playTime, long onlineTime, String videoIdWithoutTeacher, String videoIdWithTeacher, long wholeTime) {

        JsonObject payerReg = new JsonObject();
        payerReg.addProperty("playTime", playTime);
        payerReg.addProperty("userPlayTime", onlineTime);
        payerReg.addProperty("videoIdWithoutTeacher", videoIdWithoutTeacher);
        payerReg.addProperty("videoIdWithTeacher", videoIdWithTeacher);
        payerReg.addProperty("wholeTime", wholeTime);

        if (TextUtils.isEmpty(syllabusId))
            return Observable.empty();
        payerReg.addProperty("syllabusId", syllabusId);
        if (!TextUtils.isEmpty(joinCode))
            payerReg.addProperty("joinCode", joinCode);
        if (!TextUtils.isEmpty(roomId))
            payerReg.addProperty("roomId", roomId);
        if (!TextUtils.isEmpty(sessionId))
            payerReg.addProperty("sessionId", sessionId);


        return getApi().saveCourseProgress(payerReg);
    }

 /*   public static Observable<BaseResponseModel<VodCoursePlayBean.DataBean>> getVodCoursePlayV2(int courseId) {
         return getApi().getVodCoursePlayV2(courseId);
    }*/

    public static Observable<BaseResponseModel<VodCoursePlayBean.DataBean>> getCourseSyllabusV2(String courseId, int isTrial) {
        if (isTrial == 0) return getApi().getVodCoursePlayV2(StringUtils.parseLong(courseId));
        return getApi().getCourseSyllabusV2(courseId, isTrial);
    }

    public static Observable<BaseResponseModel<DownToken>> refreshDownloadToken(long roomId, long sessionId, boolean isPlayback, long videoId) {

        if (isPlayback) {
            if (sessionId == 0) return getApi().refreshPlaybackToken(roomId);
            return getApi().refreshPlaybackToken(roomId, sessionId);
        } else {
            return getApi().refreshRecordToken(videoId);
        }
    }

    // 0在线1教育
  /*  public static Call<BaseResponseModel<String>> getAliPaySign(boolean isHuatu , JsonObject payerReg) {
        if(isHuatu){
            return getApi().getAliPaySignV2(0,payerReg);
        }else {
            return getApi().getEduAliPaySign(1,payerReg);
        }

    }*/

    public static Observable<BaseResponseModel<BaseBooleanBean>> setCourseOnTop(long rid, String orderId, int catalogId, boolean isSetOnTop) {
        if (isSetOnTop) {
            return getApi().setCourseTop(rid, orderId, catalogId);
        } else {
            return getApi().cancelCourseOnTop(rid, orderId);
        }

    }

    public static void getGoldReWard(CompositeSubscription cs, final int isFree) {
        if (CommonUtils.checkPlayRewardHasDay(isFree)) {
            ServiceExProvider.visitSimple(cs, CourseApiService.getApi().getCoursePlayRewardInfo(isFree), new NetObjResponse<String>() {
                @Override
                public void onSuccess(BaseResponseModel<String> model) {
                    if (isFree == 1) {
                        //if (SpUtils.getFreeCourseListenFlag())
                        {
                            ToastUtils.showRewardToast("WATCH_FREE");
                            //SpUtils.setFreeCourseListenFlag(false);
                        }
                    } else if (isFree == 0) {
                        //if (SpUtils.getPayCourseListenFlag())
                        {
                            ToastUtils.showRewardToast("WATCH_PAY");
                            //SpUtils.setPayCourseListenFlag(false);
                        }
                    }
                }

                @Override
                public void onError(String message, int type) {

                }
            });
        }
    }
}
