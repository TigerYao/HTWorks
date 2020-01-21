package com.huatu.handheld_huatu.network;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.huatu.handheld_huatu.TokenConflictActivity;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.ApiErrorCode;
import com.huatu.handheld_huatu.base.ApiException;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.DailySpecialSettingActivity;
import com.huatu.handheld_huatu.business.arena.bean.ExportDescription;
import com.huatu.handheld_huatu.business.arena.downloaderror.bean.ErrExpListBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaDownLoadUrlBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperInfoNet;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipNewBean;
import com.huatu.handheld_huatu.business.essay.bean.CheckCountNum;
import com.huatu.handheld_huatu.business.essay.bean.EssayPaperReport;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchData;
import com.huatu.handheld_huatu.business.essay.bean.HomeworkSingleListBean;
import com.huatu.handheld_huatu.business.essay.bean.IsCheckData;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseTabData;
import com.huatu.handheld_huatu.business.essay.bean.MyCheckData;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseData;
import com.huatu.handheld_huatu.business.essay.bean.SingleExerciseTabData;
import com.huatu.handheld_huatu.business.essay.bean.UpLoadEssayData;
import com.huatu.handheld_huatu.business.essay.cusview.imgdrag.ImageUpResult;
import com.huatu.handheld_huatu.business.faceteach.bean.SplitOrderBean;
import com.huatu.handheld_huatu.business.lessons.LiveSearchKeyword;
import com.huatu.handheld_huatu.business.lessons.bean.CourseCategoryBean;
import com.huatu.handheld_huatu.business.lessons.bean.Courses;
import com.huatu.handheld_huatu.business.lessons.bean.FaceToFaceCourseBean;
import com.huatu.handheld_huatu.business.lessons.bean.OneToOneInfoBean;
import com.huatu.handheld_huatu.business.lessons.bean.ProtocolExamUserInfo;
import com.huatu.handheld_huatu.business.lessons.bean.ProtocolResultBean;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;
import com.huatu.handheld_huatu.business.matches.bean.GiftDescribeBean;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperListData;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperTopData;
import com.huatu.handheld_huatu.business.me.bean.BalanceDetailData;
import com.huatu.handheld_huatu.business.me.bean.CancelOrderBean;
import com.huatu.handheld_huatu.business.me.bean.LastLogisticData;
import com.huatu.handheld_huatu.business.me.bean.MyRedPacketBean;
import com.huatu.handheld_huatu.business.me.bean.OrderDetailData;
import com.huatu.handheld_huatu.business.me.bean.RecordTypeData;
import com.huatu.handheld_huatu.business.me.bean.RedBagInfo;
import com.huatu.handheld_huatu.business.me.bean.RedBagShareInfo;
import com.huatu.handheld_huatu.business.me.bean.RedConfirmCodeBean;
import com.huatu.handheld_huatu.business.me.bean.RedPacketBean;
import com.huatu.handheld_huatu.business.me.bean.RedPacketDetailBean;
import com.huatu.handheld_huatu.business.me.bean.ScanCourseData;
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
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.AddressInfoBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CalenderCourseBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CourseSyllabus;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.OrdersPrevInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.XxbRemainderBean;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.Category;
import com.huatu.handheld_huatu.mvpmodel.CommentResult;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleDetail;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleListResponse;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleTabData;
import com.huatu.handheld_huatu.mvpmodel.HomeAdvBean;
import com.huatu.handheld_huatu.mvpmodel.HomeConfig;
import com.huatu.handheld_huatu.mvpmodel.HomeReport;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;
import com.huatu.handheld_huatu.mvpmodel.PraiseData;
import com.huatu.handheld_huatu.mvpmodel.RewardInfoBean;
import com.huatu.handheld_huatu.mvpmodel.Sensor.CourseInfoForStatistic;
import com.huatu.handheld_huatu.mvpmodel.ShareInfo;
import com.huatu.handheld_huatu.mvpmodel.UpdateInfoBean;
import com.huatu.handheld_huatu.mvpmodel.account.ConfirmCodeBean;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaItem;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaVersion;
import com.huatu.handheld_huatu.mvpmodel.area.ProvinceBeanList;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ExamPagerItem;
import com.huatu.handheld_huatu.mvpmodel.arena.ExportErrBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ExportErrBeanPre;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountDetail;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountInfo;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodData;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckGoodOrderBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayAnswerImageSortBean;
import com.huatu.handheld_huatu.mvpmodel.essay.EssayPayInfo;
import com.huatu.handheld_huatu.mvpmodel.essay.EssaySearchAnswerCardStateInfo;
import com.huatu.handheld_huatu.mvpmodel.essay.MaterialsFileUrlBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperCommitBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleAreaListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.TeacherBusyStateInfo;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseReportBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.PracticeIdBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.SimulationListItem;
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
import com.huatu.handheld_huatu.mvpmodel.me.CoverManulResBean;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.mvpmodel.me.ExamTypeAreaConfigBean;
import com.huatu.handheld_huatu.mvpmodel.me.FeedbackBean;
import com.huatu.handheld_huatu.mvpmodel.me.ShareInfoBean;
import com.huatu.handheld_huatu.mvpmodel.me.TreeViewBean;
import com.huatu.handheld_huatu.mvpmodel.previewpaper.PreviewPaperBean;
import com.huatu.handheld_huatu.mvpmodel.special.DailySpecialBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.BuyStatusData;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.LastCourseBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.VideoBean;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.StringUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.internal.util.ActionSubscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2016/11/18.
 */
public class ServiceProvider {

    public static void getServiceTime(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<Long>> observable = RetrofitManager.getInstance().getService().getServiceTime();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getRewardInfo(final NetResponse response) {
        Observable<BaseResponseModel<Map<String, RewardInfoBean>>> observable =
                RetrofitManager.getInstance().getService().getRewardInfo();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response));
    }

    public static void postShareReward(final String id, final NetResponse response) {
        Observable<BaseResponseModel<RewardInfoBean>> observable =
                RetrofitManager.getInstance().getService().postShareReward(id);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response));
    }

    public static void checkUpdate(String sourceId, final NetResponse response) {
        Observable<BaseResponseModel<UpdateInfoBean>> observable =
                RetrofitManager.getInstance().getService().checkUpdate(sourceId, String.valueOf(AppUtils.getVersionCode()));
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response));
    }

    public static void firstComment(final NetResponse response) {
        Observable<BaseResponseModel<String>> observable = RetrofitManager.getInstance().getService().firstComment();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response));
    }

    public static void getXxbRemainder(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<XxbRemainderBean>> observable =
                RetrofitManager.getInstance().getService().getXxbRemainder();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getArenaDetailInfo(CompositeSubscription cs,
                                          long arenaId, final NetResponse response) {
        Observable<BaseResponseModel<ArenaDetailBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getArenaDetails(arenaId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCurrentPracticeId(CompositeSubscription cs,
                                            long id, final NetResponse response) {
        Observable<BaseResponseModel<PracticeIdBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getCurrentPracticeId(id);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void applyFaceToFace(CompositeSubscription cs, String areaId, String strName, String strId,
                                       String strScore, String strRank, final NetResponse response) {
        Observable<BaseResponseModel<Integer>> observable =
                RetrofitManager.getInstance().getService().applyFaceToFace(strId, areaId, strScore, strRank, strName);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void fillFaceToFaceInfo(CompositeSubscription cs, String hid,
                                          String phone, String sid, final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().fillFaceToFaceInfo(hid, phone, sid);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getFaceToFaceFreeCourseList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<FaceToFaceCourseBean>> observable =
                RetrofitManager.getInstance().getService().getFaceToFaceFreeCourseList();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void sendProtocolInfo(CompositeSubscription cs, ProtocolExamUserInfo info, final NetResponse response) {
        Observable<BaseResponseModel<ProtocolResultBean>> observable =
                RetrofitManager.getInstance().getService().sendProtocolInfo(
                        info.examCertifacteNo, info.feeAccountName, info.feeAccountNo,
                        info.feeBank, info.forExam, info.idCard, StringUtils.parseLong(info.protocolId), info.rid,
                        info.sex, info.studentName, info.telNo);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getFaceToFaceFreeCourse(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().getFaceToFaceFreeCourse();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

  /*  public static void getArenaStatisticShareInfo(CompositeSubscription cs,
                                                  long arenaId, final NetResponse response) {
        Observable<BaseResponseModel<ShareInfoBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getArenaStatisticShareInfo(arenaId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }*/

//    public static void getLiveCourseDetail(CompositeSubscription cs,
//                                           String id, int collageActivityId, final NetResponse response) {
//        Observable<BaseResponseModel<CourseDetailBean>> arenaDetailBeanObservable =
//                RetrofitManager.getInstance().getService().getLiveCourseDetail(id, collageActivityId);
//        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(createObjectSubscriber(response)));
//    }

    public static void getCourseDetailInfo(CompositeSubscription cs,
                                           String id, int collageActivityId, int selectCollection, final NetResponse response) {
        Observable<BaseResponseModel<CourseDetailBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getCourseDetailInfo(id, collageActivityId,selectCollection);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

//    public static void getPlayoffCourseDetail(CompositeSubscription cs,
//                                              String id, int collageActivityId, final NetResponse response) {
//        Observable<BaseResponseModel<CourseDetailBean>> arenaDetailBeanObservable =
//                RetrofitManager.getInstance().getService().getPlayoffCourseDetail(id, collageActivityId);
//        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(createObjectSubscriber(response)));
//    }

    public static void getCourseDetailH5Url(CompositeSubscription cs,
                                            String id, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getCourseDetailH5Url(id);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCourseTeacherIntroInfo(CompositeSubscription cs,
                                                 String id, final NetResponse response) {
        Observable<BaseResponseModel<CourseTeacherIntroInfoBean>> observable =
                RetrofitManager.getInstance().getService().getCourseTeacherIntroInfo(id);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCourseTeacherJudgeList(CompositeSubscription cs, String id,
                                                 final NetResponse response) {
        getCourseTeacherJudgeList(cs, id, 1, 10, response);
    }

    public static void getCourseTeacherJudgeList(CompositeSubscription cs, String id,
                                                 int page, int pageSize, final NetResponse response) {
        Observable<BaseResponseModel<CourseTeacherJudgeBean>> observable =
                RetrofitManager.getInstance().getService().getCourseTeacherJudge(id, page, pageSize);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCourseTeacherCourseList(CompositeSubscription cs,
                                                  String id, final NetResponse response) {
        getCourseTeacherCourseList(cs, id, 1, 10, response);
    }

    public static void getCourseTeacherCourseList(CompositeSubscription cs,
                                                  String id, int page, int pageSize, final NetResponse response) {
        Observable<BaseResponseModel<CourseTeacherCourseBean>> observable =
                RetrofitManager.getInstance().getService().getCourseTeacherCourseList(id, page, pageSize);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

//    public static void getCourseIntroInfo(CompositeSubscription cs,
//                                          String id, int activityId, final NetResponse response) {
//        Observable<BaseResponseModel<VideoPlayVideoInfoBean>> arenaDetailBeanObservable =
//                RetrofitManager.getInstance().getService().getCourseIntroInfo(id, activityId);
//        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(createObjectSubscriber(response)));
//    }


    public static void getCourseTeacherList(CompositeSubscription cs,
                                            String id, final NetResponse response) {
        Observable<BaseListResponseModel<CourseTeacherListItemBean>> observable =
                RetrofitManager.getInstance().getService().getCourseTeacherList(id);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getCourseJudgeList(CompositeSubscription cs, String id, int isLive,
                                          int page, int pageSize, final NetResponse response) {
        Observable<BaseResponseModel<CourseTeacherJudgeBean>> observable =
                RetrofitManager.getInstance().getService().getCourseJudgeList(id, isLive, page, pageSize);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getOutlineDetail(CompositeSubscription cs, String id, int page,
                                        int pageSize, int parentId, int onlyTrial, final NetResponse response) {
        Observable<BaseResponseModel<CourseOutlineBean>> observable =
                RetrofitManager.getInstance().getService().getCourseOutline(id, page, pageSize, parentId, onlyTrial);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCommentLesson(final NetResponse response) {
        Observable<BaseResponseModel<CommentResult>> observable =
                RetrofitManager.getInstance().getService().getCommentLesson();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response));
    }

    public static void submitGufenAnswer(CompositeSubscription cs, long practiceId, int cardType,
                                         JsonArray answers, final NetResponse response) {
        Observable<BaseResponseModel<RealExamBeans.RealExamBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().submitGufenAnswerCard(practiceId, cardType, answers);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void submitScCard(CompositeSubscription cs, long practiceId,
                                    JsonArray answers, final NetResponse response) {
        Observable<BaseResponseModel<RealExamBeans.RealExamBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().submitScCard(practiceId, answers);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getSimulationExamList(CompositeSubscription cs, int page,
                                             int pageSize, String types, int subject, final NetResponse response) {
        Observable<BaseListResponseModel<SimulationListItem>> observable =
                RetrofitManager.getInstance().getService().getSimulationList(pageSize, page, types, subject);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getSimulationTabList(CompositeSubscription cs, int subject, int subjectId, final NetResponse response) {
        Observable<BaseListResponseModel<MatchTabBean>> observable = RetrofitManager.getInstance().getService().getSimulationTabList(subject, subjectId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void getSimulationContestDetailNew(CompositeSubscription cs, int size, int page, int subjectId, final NetResponse response) {
        Observable<BaseResponseModel<SimulationScDetailBeanNew>> observable = RetrofitManager.getInstance().getService().getSimulationContestDetailNew(size, page, subjectId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    // 申论模考大赛获取详情
    public static void getSimulationEssayScDetail(CompositeSubscription cs,
                                                  final NetResponse response) {
        Observable<BaseListResponseModel<SimulationScDetailBean>> observable =
                RetrofitManager.getInstance().getService().getSimulationEssayContestDetail();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void postScSignUpNew(CompositeSubscription cs, int matchId, int positionId,
                                       final NetResponse response) {
        Observable<BaseResponseModel<SignSuccessBean>> observable =
                RetrofitManager.getInstance().getService().postScSignUpNew(matchId, positionId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    // 申论模考大赛报名接口
    public static void postSimulationEssayCSignUp(CompositeSubscription cs, int paperId, int positionId,
                                                  final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().postSimulationEssayCSignUp(paperId, positionId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    //申论之外的模考报告（新）
    public static void getNewScReport(CompositeSubscription cs, int tag, int subjectId, final NetResponse response) {
        Observable<BaseResponseModel<SimulationScHistory>> observable =
                RetrofitManager.getInstance().getService().getNewScReport(tag, subjectId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getNewScHistoryTag(CompositeSubscription cs, int subject, int subjectId, final NetResponse response) {
        Observable<BaseListResponseModel<SimulationScHistoryTag>> observable =
                RetrofitManager.getInstance().getService().getNewScHistoryTag(subject, subjectId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void postAccountChargePayInfo(CompositeSubscription cs, String Amount, String payType,
                                                final NetResponse response) {
        Observable<BaseResponseModel<PayInfo>> observable =
                RetrofitManager.getInstance().getService().postAccountChargePayInfo(Amount, payType);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getExamAreaList(CompositeSubscription cs, Integer subject, final NetResponse response) {
        Observable<BaseListResponseModel<ExamAreaItem>> observable =
                RetrofitManager.getInstance().getService().getExamAreaList(subject);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    // 真题演练是否有更新
    public static void getRealExamAreaVersion(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<ExamAreaVersion>> observable = RetrofitManager.getInstance().getService().getExamAreaVersion();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getRealExamAreaPaperList(CompositeSubscription cs, int page, int pageSize, int area, Integer subject, int paperType, final NetResponse response) {
        Observable<BaseResponseModel<ExamPagerItem>> observable = RetrofitManager.getInstance().getService().getAreaPaperList(page, pageSize, area, subject, paperType);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getTargetAreaList(CompositeSubscription cs, int catgory, final NetResponse response) {
        Observable<BaseResponseModel<ProvinceBeanList>> observable =
                RetrofitManager.getInstance().getService().getTargetAreaList(catgory);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getSignUpDataList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<Category>> observable =
                RetrofitManager.getInstance().getService().getSignUpDataList();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getErrorList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<TreeViewBean>> observable =
                RetrofitManager.getInstance().getService().getErrorList();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getErrorListX(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<HomeTreeBeanNew>> observable = RetrofitManager.getInstance().getService().getErrorListX();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void getExportDescription(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<ExportDescription>> observable = RetrofitManager.getInstance().getService().getExportDescription();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void preExportErrorArena(CompositeSubscription cs, String points, int num, final NetResponse response) {
        Observable<BaseResponseModel<ExportErrBeanPre>> observable = RetrofitManager.getInstance().getService().preExportErrorArena(points, num);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void exportErrorArena(CompositeSubscription cs, String points, final NetResponse response) {
        Observable<BaseResponseModel<ExportErrBean>> observable = RetrofitManager.getInstance().getService().exportErrorArena(points);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getErrExpList(CompositeSubscription cs, int page, int size, final NetResponse response) {
        Observable<BaseResponseModel<ErrExpListBean>> observable = RetrofitManager.getInstance().getService().getErrExpList(page, size);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getErrExpDownloadInfo(CompositeSubscription cs, long taskId, final NetResponse response) {
        Observable<BaseResponseModel<ErrExpListBean.ErrExpBean>> observable = RetrofitManager.getInstance().getService().getErrExpDownloadInfo(taskId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void delErrExport(CompositeSubscription cs, String taskIds, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable = RetrofitManager.getInstance().getService().delErrExport(taskIds);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    public static void addFavorPractice(CompositeSubscription cs,
                                        int questionId, final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().collectExercise(questionId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void deleteFavorPractice(CompositeSubscription cs,
                                           int questionId, final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().cancelCollection(questionId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void correctError(CompositeSubscription cs, int questionId,
                                    JsonObject jsonObject, final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().correctError(questionId, jsonObject);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void deleteWrongExerciseV4(CompositeSubscription cs, int questionId, final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().deleteWrongExerciseV4(questionId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    // type: 0:练习 1:题目 2:报告
    public static void getShareInfo(CompositeSubscription cs, int type, long practiceId, int practiceType, final NetResponse response) {
        Observable<BaseResponseModel<ShareInfoBean>> observable = null;
        if (type == 1) {  //好像没用了
            observable = RetrofitManager.getInstance().getService().shareQuestion((int) practiceId);
        } else if (type == 2) {
            if (practiceType == 1 || practiceType == 2) {
                observable = RetrofitManager.getInstance().getService().newSharePractice(practiceId, practiceType);
            } else {
                // observable = RetrofitManager.getInstance().getService().sharePractice(practiceId);
                observable = RetrofitManager.getInstance().getService().newSharePractice(practiceId, practiceType);
            }
        } else {           //好像没用了
            //observable = RetrofitManager.getInstance().getService().sharePractice(practiceId);
            observable = RetrofitManager.getInstance().getService().newSharePractice(practiceId, practiceType);
        }
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getHomeAdvertise(CompositeSubscription cs, int category, int fur, final NetResponse response) {
        Observable<BaseListResponseModel<AdvertiseConfig>> observable = RetrofitManager.getInstance().getService().getHomeAdvertise(category, fur);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void getHomeIcons(CompositeSubscription cs, int subjectId, final NetResponse response) {
        Observable<BaseListResponseModel<HomeIconBean>> observable = RetrofitManager.getInstance().getService().getHomeIcons(subjectId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void getHomeTreeData(CompositeSubscription cs, int flag, final NetResponse response) {
        Observable<BaseListResponseModel<HomeTreeBeanNew>> observable = RetrofitManager.getInstance().getService().getHomeTreeData(flag);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void getMatchIdForNewTip(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<TipNewBean>> observable = RetrofitManager.getInstance().getService().getMatchIdForNewTip();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void getClearRecordMsg(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<ConfirmCodeBean.ConfirmCode>> observable = RetrofitManager.getInstance().getService().getClearRecordMsg();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getClearRecord(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<ConfirmCodeBean.ConfirmCode>> observable = RetrofitManager.getInstance().getService().getClearRecord();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getHomeTreeDataById(CompositeSubscription cs, int parentId, int flag, final NetResponse response) {
        Observable<BaseListResponseModel<HomeTreeBeanNew>> observable = RetrofitManager.getInstance().getService().getHomeTreeDataById(parentId, flag);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void getHomeReport(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<HomeReport>> observable = RetrofitManager.getInstance().getService().getHomeReport();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getHomeConfig(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<HomeConfig>> observable =
                RetrofitManager.getInstance().getService().getHomeConfig();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void setUserAreaTypeConfig(CompositeSubscription cs,
                                             Integer category, Integer area,
                                             Integer subject, Integer qcount,
                                             Integer errorQcount, final NetResponse response) {
        Observable<BaseResponseModel<ExamTypeAreaConfigBean>> observable =
                RetrofitManager.getInstance().getService().setUserAreaTypeConfig(
                        category, area, subject, qcount, errorQcount);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getDailyList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<DailySpecialBean>> observable =
                RetrofitManager.getInstance().getService().getDailyList();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getHomeAdvList(CompositeSubscription cs, int category, final NetResponse response) {
        Observable<BaseResponseModel<HomeAdvBean>> observable = RetrofitManager.getInstance().getService().getHomeAdvList(category);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getCourseSyllabus(CompositeSubscription cs,
                                         int courseId, final NetResponse response) {
        Observable<BaseResponseModel<CourseSyllabus>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getCourseSyllabus(courseId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getMyPurchasedCourse(CompositeSubscription cs,
                                            int type, int page, final NetResponse response) {
        Observable<BaseResponseModel<CourseMineBean>> observable =
                RetrofitManager.getInstance().getService().getMyCourseList(type, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCourseList(CompositeSubscription cs, String categoryId, String keyword,
                                     int orderId, int priceId, int page, final NetResponse response) {
        Observable<BaseResponseModel<Courses>> observable =
                RetrofitManager.getInstance().getService().getLiveCourses(
                        categoryId, keyword, orderId, priceId, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCollectCourseList(CompositeSubscription cs,
                                            String collectionId, int page, final NetResponse response) {
        Observable<BaseResponseModel<Courses>> observable =
                RetrofitManager.getInstance().getService().getCourses(collectionId, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void searchMyCourseList(CompositeSubscription cs, String key, int type,
                                          int page, final NetResponse response) {
        Observable<BaseResponseModel<CourseMineBean>> observable =
                RetrofitManager.getInstance().getService().searchMyCourseList(key, type, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCourseDiss(CompositeSubscription cs,
                                     int page, int type, final NetResponse response) {
        Observable<BaseResponseModel<CourseMineBean>> observable =
                RetrofitManager.getInstance().getService().getCourseDiss(0, page, type);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

/*    public static void getCourseCalender(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<CourseCalenderBean>> observable =
                RetrofitManager.getInstance().getService().getCourseCalender();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }*/

    public static void getCalenderCourse(CompositeSubscription cs, String rids, final NetResponse response) {
        Observable<BaseListResponseModel<CalenderCourseBean>> observable =
                RetrofitManager.getInstance().getService().getCalenderCourse(rids);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getCourseInfo(CompositeSubscription cs, String courseId, final NetResponse response) {
        Observable<BaseResponseModel<VideoBean>> observable =
                RetrofitManager.getInstance().getService().getCourseInfo(courseId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getLastPlay(CompositeSubscription cs, String courseId, final NetResponse response) {
        Observable<BaseResponseModel<LastCourseBean>> observable =
                RetrofitManager.getInstance().getService().getLastLesson(courseId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }


//    public static void getOrdersPrevInfo(CompositeSubscription cs,
//                                         String courseId, String pageSource, final NetResponse response) {
//        Observable<BaseResponseModel<OrdersPrevInfo>> arenaDetailBeanObservable =
//                RetrofitManager.getInstance().getService().getOrdersPrevInfo(courseId, pageSource);
//        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(createObjectSubscriber(false, response)));
//    }

    public static void getOrdersPrevInfo(CompositeSubscription cs,
                                         String courseId, int goodsId, String pageSource, final NetResponse response) {
        Observable<BaseResponseModel<OrdersPrevInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getPreOrderInfo(courseId, goodsId, pageSource);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(true, response)));
    }


    public static void createOrder(CompositeSubscription cs, String acPrice, int goodsId,String classId, String scretMsg, String addressid, String fromuser, String payment, String treatyId,
                                   final NetResponse response) {
        Observable<BaseResponseModel<PayInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().createOrder(acPrice, goodsId,scretMsg, addressid, fromuser, payment, classId, 2, treatyId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void continuePayOrder(CompositeSubscription cs, String orderId, int payment,
                                        final NetResponse response) {
        Observable<BaseResponseModel<PayInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().continuePayOrder(orderId, payment, 2);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void payOrder(CompositeSubscription cs, String orderNo,
                                int payMethod, final NetResponse response) {
        Observable<BaseResponseModel<PayInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().payOrder(orderNo, payMethod);
        boolean isResult = true;
        if (payMethod == 1) {
            isResult = false;
        }
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(isResult, response)));
    }

    public static void payCourseOrder(CompositeSubscription cs, long orderNo,
                                      int payMethod, int source, final NetResponse response) {
        Observable<BaseResponseModel<PayInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().payCourseOrder(orderNo, payMethod, source);
        boolean isResult = true;
        if (payMethod == 1) {
            isResult = false;
        }
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(isResult, response)));
    }

    public static void payFreeOrder(CompositeSubscription cs, String classId,
                                    String pageSource, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().payFreeOrder(classId, pageSource, 2);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getRedBagInfo(CompositeSubscription cs, long id, final NetResponse response) {
        Observable<BaseResponseModel<RedBagInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getRedBagInfo(id);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getRedBagShareInfo(CompositeSubscription cs, String moneyNum, long redPackageId, String param, final NetResponse response) {
        Observable<BaseResponseModel<RedBagShareInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getRedBagShareInfo(moneyNum, redPackageId, param);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void updateAddress(CompositeSubscription cs,
                                     AddressInfoBean addressInfo, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().updateAddress(addressInfo.id, addressInfo);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void addAddress(CompositeSubscription cs,
                                  AddressInfoBean addressInfo, final NetResponse response) {
        Observable<BaseResponseModel<Integer>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().addAddress(addressInfo);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getAddressList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<AddressInfoBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().searchAddress();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void deleteAddress(CompositeSubscription cs, long id, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().deleteAddress(id);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void applyRefund(CompositeSubscription cs,
                                   String order, String reason, final NetResponse response) {
        Observable<BaseResponseModel<OrderRefundResp>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().applyRefund(order, reason);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getRefundDetail(CompositeSubscription cs, String order, final NetResponse response) {
        Observable<BaseResponseModel<OrderRefundResp>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getRefundDetail(order);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

/*    public static void setOneToOneInfo(CompositeSubscription cs, String rid,
                                       OneToOneInfoBean order, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().setOneToOneInfo(rid, order);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }*/

    public static void getOneToOneInfo(CompositeSubscription cs, String rid, String orderNum, final NetResponse response) {
        Observable<BaseResponseModel<OneToOneInfoBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getOneToOneInfo(rid, orderNum);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }


    public static void getOrderDetail(CompositeSubscription cs, String orderId, final NetResponse response) {
        Observable<BaseResponseModel<OrderDetailData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getOrderDetail(orderId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getGroupOrderDetail(CompositeSubscription cs, String orderId, final NetResponse response) {
        Observable<BaseResponseModel<OrderDetailData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getGroupOrderDetail(orderId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getLastLogistics(CompositeSubscription cs, String orderId, final NetResponse response) {
        Observable<BaseResponseModel<LastLogisticData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getLastLogistics(orderId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void cancelOrder(CompositeSubscription cs, String orderNum, final NetResponse response) {
        Observable<BaseListResponseModel<CancelOrderBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().cancelMyOrder(orderNum);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void cancelEssayOrder(CompositeSubscription cs, long id, final NetResponse response) {
        Observable<BaseResponseModel<CancelOrderBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().cancelMyEssayOrder(id);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void deleteMyOrder(CompositeSubscription cs, String orderId, int type, final NetResponse response) {
        Observable<BaseListResponseModel<OrderDetailData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().deleteMyOrder(orderId, type);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void deleteMyEssayOrder(CompositeSubscription cs, long orderId, final NetResponse response) {
        Observable<BaseResponseModel<CancelOrderBean>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().deleteMyEssayOrder(orderId);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getSecKillPaymentParams(CompositeSubscription cs, String orderNum, final NetResponse response) {
        Observable<BaseResponseModel<PayInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getSecKillPaymentParams(orderNum);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void payOrderSecKill(CompositeSubscription cs, String orderNum, int type, final NetResponse response) {
        Observable<BaseResponseModel<PayInfo>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().payOrderSecKill(orderNum, type);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void saveSearchLiveKeywords(CompositeSubscription cs, String key, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().saveSearchLiveKeywords(key);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getSearchLiveKeywords(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<LiveSearchKeyword>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getSearchLiveKeywords();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void deleteLiveSearchKeyword(CompositeSubscription cs, String word, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().deleteLiveSearchKeyword(word);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void clearLiveSearchHistory(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().clearLiveSearchHistory();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

/*    public static void addFreeCourse(CompositeSubscription cs, int rid, String pageSource, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().addFreeCourse(rid, pageSource);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }*/

    // 检查红包入口是否显示
    public static void checkRedPacketShow(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<MyRedPacketBean>> arenaDetailBeanObservable = RetrofitManager.getInstance().getService().checkRedPacketShow();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    // 检查用户是否有红包
    public static void checkRedPacket(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<MyRedPacketBean>> arenaDetailBeanObservable = RetrofitManager.getInstance().getService().checkRedPacket();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    // 申论之单题列表
    public static void getSingleExercise(CompositeSubscription cs, int type, int page, final NetResponse response) {
        Observable<BaseResponseModel<SingleExerciseData>> arenaDetailBeanObservable = RetrofitManager.getInstance().getService().getSingleExercise(type, page);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    // 申论之套题批改记录列表
    public static void getMyCheck(CompositeSubscription cs, int type, int page, final NetResponse response) {
        Observable<BaseResponseModel<MyCheckData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getMyCheck(type, page);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    // 申论之标准答案和文章写作批改记录列表
    public static void getMySingleCheck(CompositeSubscription cs, int type, int page, final NetResponse response) {
        Observable<BaseResponseModel<MyCheckData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getMySingleCheck(type, page);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    //账户明细之申论支出
    public static void getEssayConsume(CompositeSubscription cs, int page, final NetResponse response) {
        Observable<BaseResponseModel<BalanceDetailData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getEssayConsume(page);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    //账户明细之申论支出
    public static void getDeriveExerciseConsume(CompositeSubscription cs, int page, final NetResponse response) {
        Observable<BaseResponseModel<BalanceDetailData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getDeriveExerciseConsume(page);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    //账户明细之收入(type=1),课程支出（type=2）
    public static void getBalanceDetail(CompositeSubscription cs, int type, int page, final NetResponse response) {
        Observable<BaseResponseModel<BalanceDetailData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getBalanceDetail(type, page);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    //申论单题的tab
    public static void getSingleExerciseTab(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<SingleExerciseTabData>> arenaDetailBeanObservable = RetrofitManager.getInstance().getService().getSingleExerciseTab();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }


    // check
    public static void getCheckGoodsList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<CheckGoodData>> observable =
                RetrofitManager.getInstance().getService().getCheckGoodsList();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //申论套题的tab
    public static void getMultiExerciseTab(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<MultiExerciseTabData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getMultiExerciseTab();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    //备考精华的tab
    public static void getCreamArticleTab(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<CreamArticleTabData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getCreamArticleTab();
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }


    //备考精华列表
    public static void getCreamArticleData(CompositeSubscription cs, int type, int page, int pageSize, final NetResponse response) {
        Observable<BaseResponseModel<CreamArticleListResponse>> observable =
                RetrofitManager.getInstance().getService().getCreamArticleData(type, page, pageSize);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //备考精华文章详情
    public static void getCreamArticleDetail(CompositeSubscription cs, long aid, final NetResponse response) {
        Observable<BaseResponseModel<CreamArticleDetail>> observable = RetrofitManager.getInstance().getService().getCreamArticleDetail(aid);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    //备考精华文章点赞
    public static void getPraiseDetail(CompositeSubscription cs, long aid, int type, final NetResponse response) {
        Observable<BaseListResponseModel<PraiseData>> observable = RetrofitManager.getInstance().getService().getPraiseDetail(aid, type);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createArrayListSubscriber(response)));
    }

    //消息分类列表
    public static void getMessageGroupList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<MessageGroupData>> observable =
                RetrofitManager.getInstance().getService().getMessageGroupList();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createArrayListSubscriber(response)));
    }

    // 消息全部已读
    public static void readAllMessage(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().readAllMessage();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // 删除消息分组
/*    public static void deleteMessageGroup(CompositeSubscription cs, String view, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().deleteMessageGroup(view);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }*/

    // 删除单条消息
    public static void deleteMessage(CompositeSubscription cs, String noticeId, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().deleteMessage(noticeId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // 是否批改过两次
    public static void getCheckCountNum(CompositeSubscription cs, int type, long baseId, final NetResponse response) {
        Observable<BaseResponseModel<CheckCountNum>> observable =
                RetrofitManager.getInstance().getService().getCheckCountNum(type, baseId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //上传图片
    public static void sendEssayPicture(CompositeSubscription cs, MultipartBody.Part file, int type, final NetResponse response) {
        Observable<BaseResponseModel<UpLoadEssayData>> observable =
                RetrofitManager.getInstance().getService().sendEssayPicture(file, type);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // 新的上传图片
    public static void updateEssayPicture(CompositeSubscription cs, MultipartBody.Part file, long answerCardId, int sort, final NetResponse response) {
        Observable<BaseResponseModel<ImageUpResult>> observable =
                RetrofitManager.getInstance().getService().updateEssayPicture(file, answerCardId, sort);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // 改变图片顺序
    public static void changePictureSort(CompositeSubscription cs, List<EssayAnswerImageSortBean> imgAnswerList, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().changePictureSort(imgAnswerList);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // 删除上传的图片
/*    public static void deletePicture(CompositeSubscription cs, long imgId, long answerCardId, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().deletePicture(imgId, answerCardId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }*/

    public static void getMeAdv(CompositeSubscription cs, int category, final NetResponse response) {
        Observable<BaseListResponseModel<AdvertiseConfig>> observable = RetrofitManager.getInstance().getService().getMeAdv(category);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createArrayListSubscriber(response)));
    }

    public static void getCheckCountList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseResponseModel<CheckCountBean>> observable = RetrofitManager.getInstance().getService().getCheckCountList();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getCheckCount(CompositeSubscription cs, long type, long id, final NetResponse response) {
        Observable<BaseResponseModel<CheckCountInfo>> observable =
                RetrofitManager.getInstance().getService().getCheckCount(type, id);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getTeacherBusyState(CompositeSubscription cs, long answerCardId, int correctMode, int questionType, long id, final NetResponse response) {
        Observable<BaseResponseModel<TeacherBusyStateInfo>> observable =
                RetrofitManager.getInstance().getService().getTeacherBusyState(answerCardId, correctMode, questionType, id);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getAnswerCardState(CompositeSubscription cs, int type, long id, final NetResponse response) {
        Observable<BaseResponseModel<EssaySearchAnswerCardStateInfo>> observable =
                RetrofitManager.getInstance().getService().getAnswerCardState(type, id);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void createCheckOrder(CompositeSubscription cs, CheckGoodOrderBean goods, final NetResponse response) {
        Observable<BaseResponseModel<EssayPayInfo>> observable =
                RetrofitManager.getInstance().getService().createCheckOrder(goods);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getEssayPaperReport(CompositeSubscription cs, long answerId, final NetResponse response) {
        Observable<BaseResponseModel<EssayPaperReport>> observable =
                RetrofitManager.getInstance().getService().getEssayPaperReport(answerId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getEssaySinglePaperReport(CompositeSubscription cs, long answerId, final NetResponse response) {
        Observable<BaseResponseModel<EssayPaperReport>> observable =
                RetrofitManager.getInstance().getService().getEssaySinglePaperReport(answerId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void getCheckDetail(CompositeSubscription cs, int type, long answerId, int correctMode, final NetResponse response) {
        Observable<BaseListResponseModel<CheckDetailBean>> observable =
                RetrofitManager.getInstance().getService().getCheckDetail(type, answerId, correctMode);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createArrayListSubscriber(response)));
    }

    // 查询批改次数详情
    public static void getCheckCountDetailList(CompositeSubscription cs, int goodsType, int page, final NetResponse response) {
        Observable<BaseResponseModel<CheckCountDetail>> observable =
                RetrofitManager.getInstance().getService().getCheckCountDetailList(goodsType, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // single
    public static void getSingleAreaListDetail(CompositeSubscription cs, long similarId, final NetResponse response) {
        Observable<BaseListResponseModel<SingleAreaListBean>> observable =
                RetrofitManager.getInstance().getService().getSingleAreaListDetail(similarId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getMaterialsDownloadUrl(CompositeSubscription cs, long paperAnswerId, long paperId, long questionAnswerId, long questionBaseId, final NetResponse response) {
        Observable<BaseResponseModel<MaterialsFileUrlBean>> observable = RetrofitManager.getInstance().getService().getMaterialsDownloadUrl(paperAnswerId, paperId, questionAnswerId, questionBaseId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void paperCommit_sc(CompositeSubscription cs, PaperCommitBean paperCommitBean, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable = null;
        observable = RetrofitManager.getInstance().getService().paperCommit_sc(paperCommitBean);
        if (observable != null) {
            cs.add(observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createObjectSubscriber(false, response)));
        }
    }

    public static void setCollectEssay(CompositeSubscription cs, int type, Long similarId, Long baseId, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable = RetrofitManager.getInstance().getService().setCollectEssay(type, similarId, baseId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void checkCollectEssay(CompositeSubscription cs, int type, Long similarId, Long baseId, final NetResponse response) {
        Observable<BaseResponseModel<IsCheckData>> observable =
                RetrofitManager.getInstance().getService().checkCollectEssay(type, similarId, baseId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void deleteCollectEssay(CompositeSubscription cs, int type, Long similarId, Long baseId, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().deleteCollectEssay(type, similarId, baseId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void deleteCheckEssay(CompositeSubscription cs, int type, Long answerId, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().deleteCheckEssay(type, answerId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // 申论之套题列表
    public static void getMultiExercise(CompositeSubscription cs, long type, int page, int pageSize, final NetResponse response) {
        Observable<BaseResponseModel<MultiExerciseData>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().getMultiExercise(type, page, pageSize);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void sendFeedBack(CompositeSubscription cs, FeedbackBean feedbackBean, final NetResponse response) {
        Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                RetrofitManager.getInstance().getService().sendBugReport(feedbackBean);
        cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void sendFeedBack(CompositeSubscription cs, long answerId, int type, int star, String content, final NetResponse response) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("answerId", answerId);
            jsonObject.put("answerType", type);
            jsonObject.put("star", star);
            jsonObject.put("content", content);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
            Observable<BaseResponseModel<String>> arenaDetailBeanObservable =
                    RetrofitManager.getInstance().getService().essayResultFeedback(requestBody);
            cs.add(arenaDetailBeanObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createObjectSubscriber(false, response)));
        } catch (Exception e) {
            e.printStackTrace();
            response.onError(e);
        }
    }

    public static void getEssayReport(CompositeSubscription cs, long paperId, final NetResponse response) {
        Observable<BaseResponseModel<EssayScReportBean>> observable =
                RetrofitManager.getInstance().getService().getEssayReport(paperId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));

    }

    public static void getEssayScHistoryList(CompositeSubscription cs, int tag, final NetResponse response) {
        Observable<BaseResponseModel<SimulationScHistory>> observable =
                RetrofitManager.getInstance().getService().getEssayScHistoryList(tag);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));

    }

    public static void getScReportUrl(CompositeSubscription cs, long paperid, int type, final NetResponse response) {
        Observable<BaseResponseModel<ShareInfoBean>> observable =
                RetrofitManager.getInstance().getService().getScReportUrl(paperid, type);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getScArenaReportShare(CompositeSubscription cs, long paperId, final NetResponse response) {
        Observable<BaseResponseModel<ShareInfoBean>> observable =
                RetrofitManager.getInstance().getService().getScArenaReportShare(paperId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getCourseCategoryList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<CourseCategoryBean>> observable =
                RetrofitManager.getInstance().getService().getCourseCategoryList();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getRecordTypeData(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<RecordTypeData>> observable =
                RetrofitManager.getInstance().getService().getRecordTypeData();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    public static void getVodCourseCategoryList(CompositeSubscription cs, final NetResponse response) {
        Observable<BaseListResponseModel<CourseCategoryBean>> observable =
                RetrofitManager.getInstance().getService().getVodCourseFilter();
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createListSubscriber(response)));
    }

    //往期申论模考
    public static void getEssayPastMatchList(CompositeSubscription cs, int page, int tag, int subjectId, final NetResponse response) {
        Observable<BaseResponseModel<SimulationEssayPastPaperData>> observable =
                RetrofitManager.getInstance().getService().getEssayPastMatchList(page, tag, subjectId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //往期模考顶部课程(新)
    public static void getNewScHistoryPaperTopCourse(CompositeSubscription cs, int subject, int tag, final NetResponse response) {
        Observable<BaseResponseModel<ScHistoryPaperTopData>> observable =
                RetrofitManager.getInstance().getService().getNewScHistoryPaperTopCourse(subject, tag);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    // 往期行测模考(新)
    public static void getNewScHistoryPaper(CompositeSubscription cs, int tag, int page, int subjectId, final NetResponse response) {
        Observable<BaseResponseModel<ScHistoryPaperListData>> observable =
                RetrofitManager.getInstance().getService().getNewScHistoryPaper(tag, page, subjectId, subjectId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //申论搜索
    public static void getEssaySearch(CompositeSubscription cs, String content, int type, int page, final NetResponse response) {
        Observable<BaseListResponseModel<EssaySearchData>> observable =
                RetrofitManager.getInstance().getService().getEssaySearchData(content, type, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createArraySubscriber(false, response)));
    }

    //申论收藏夹单题列表
    public static void getEssayCollectSingleList(CompositeSubscription cs, int type, int page, final NetResponse response) {
        Observable<BaseResponseModel<SingleExerciseData>> observable =
                RetrofitManager.getInstance().getService().getEssayCollectSingleList(type, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //申论收藏夹套题列表
    public static void getEssayCollectMultiList(CompositeSubscription cs, int type, int page, final NetResponse response) {
        Observable<BaseResponseModel<MultiExerciseData>> observable =
                RetrofitManager.getInstance().getService().getEssayCollectMultiList(type, page);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //扫一扫听课详情
    public static void getScanCourseData(CompositeSubscription cs, String lessionId, final NetResponse response) {
        Observable<BaseResponseModel<ScanCourseData>> observable =
                RetrofitManager.getInstance().getService().getScanCourseData(lessionId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //我的-红包提现
    public static void getRedPacketData(CompositeSubscription cs, String userName, final NetResponse response) {
        Observable<BaseResponseModel<RedPacketBean>> observable =
                RetrofitManager.getInstance().getService().getRedPacketData(userName);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //我的-红包提现详情
    public static void getRedPacketDetail(CompositeSubscription cs, long redEnvelopeId, final NetResponse response) {
        Observable<BaseResponseModel<RedPacketDetailBean>> observable =
                RetrofitManager.getInstance().getService().getRedPacketDetail(redEnvelopeId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //我的-红包提现验证码
    public static void getRedConfirmCode(CompositeSubscription cs, String phone, final NetResponse response) {
        Observable<BaseResponseModel<RedConfirmCodeBean>> observable =
                RetrofitManager.getInstance().getService().getRedConfirmCode(phone);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //我的-红包提现到微信
    public static void getForwardToWx(CompositeSubscription cs, String openid, String phone, String verify, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().getForwardToWx(openid, phone, verify);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    //我的-红包提现到支付宝
    public static void getForwardToALi(CompositeSubscription cs, String payeeAccount, String phone, String verify, final NetResponse response) {
        Observable<BaseResponseModel<Object>> observable =
                RetrofitManager.getInstance().getService().getForwardToALi(payeeAccount, phone, verify);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(response)));
    }

    public static void setCourseCategoryList(CompositeSubscription cs, String ids, final NetResponse response) {
        Observable<BaseListResponseModel<CancelOrderBean>> observable =
                RetrofitManager.getInstance().getService().setCourseCategoryList(ids);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createArraySubscriber(false, response)));
    }

    public static void setVodCourseCategoryList(CompositeSubscription cs, String ids, final NetResponse response) {
        Observable<BaseResponseModel<String>> observable =
                RetrofitManager.getInstance().getService().setVodCourseCategoryList(ids);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getCourseActDetail(CompositeSubscription cs, String classid, final NetResponse response) {
        Observable<BaseResponseModel<CourseActDetailBean>> observable =
                RetrofitManager.getInstance().getService().getCourseActDetail(classid);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(false, response)));
    }

    public static void getEstiMateInfo(CompositeSubscription cs, int type, int subject, final NetResponse response) {
        Observable<BaseResponseModel<GiftDescribeBean>> observable =
                RetrofitManager.getInstance().getService().getEstiMateInfo(type, subject);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(true, response)));
    }

    public static void getArenaPaperDownloadUrl(CompositeSubscription cs, long paperId, final NetResponse response) {
        Observable<BaseResponseModel<ArenaDownLoadUrlBean>> observable =
                RetrofitManager.getInstance().getService().getArenaPaperDownloadUrl(paperId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(true, response)));
    }

    public static void getArenaPaperInfoList(CompositeSubscription cs, String ids, final NetResponse response) {
        Observable<BaseListResponseModel<ArenaPaperInfoNet>> observable =
                RetrofitManager.getInstance().getService().getArenaPaperInfoList(ids);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createArraySubscriber(true, response)));
    }

    public static void getScanArenaPaper(CompositeSubscription cs, long paperId, final NetResponse response) {
        Observable<BaseResponseModel<RealExamBeans.RealExamBean>> observable =
                RetrofitManager.getInstance().getService().getScanArenaPaper(paperId);
        cs.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createObjectSubscriber(true, response)));
    }

    public static void getHistoryCourses(CompositeSubscription cs, String teacherId, int page, int pageSize, NetResponse response) {
        Observable<BaseResponseModel<HistoryCourseBean>> observable = RetrofitManager.getInstance().getService().getHistoryCourses(teacherId, page, pageSize);
        cs.add(observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(true, response)));
    }

    public static void getHistoryLessons(CompositeSubscription cs, String teacherId, String classId, int page, int pageSize, NetResponse response) {
        Observable<BaseResponseModel<HistoryCourseBean>> observable = RetrofitManager.getInstance().getService().getHistoryLessons(teacherId, classId, page, pageSize);
        cs.add(observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(true, response)));
    }

    public static void getLessonEvaluates(CompositeSubscription cs, String teacherId, String classId, String lessonId, int page, int pageSize, NetResponse response) {
        Observable<BaseResponseModel<CourseTeacherJudgeBean>> observable = RetrofitManager.getInstance().getService().getLessonEvaluates(teacherId, classId, lessonId, page, pageSize);
        cs.add(observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(true, response)));
    }

    public static void addCourseCollection(CompositeSubscription cs, String classId, Subscriber<DeleteResponseBean> subscriber) {
        Observable<DeleteResponseBean> observable = RetrofitManager.getInstance().getService().addCollection(classId);
        cs.add(observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));
    }

    public static void cancelCourseCollection(CompositeSubscription cs, Subscriber<DeleteResponseBean> subscriber, String... classId) {
        Observable<DeleteResponseBean> observable = RetrofitManager.getInstance().getService().cancelCollection(classId);
        cs.add(observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));
    }

    public static void getAuditionList(CompositeSubscription cs, String classid, NetResponse response) {
        Observable<BaseResponseModel<List<CourseOutlineItemBean>>> observable = RetrofitManager.getInstance().getService().getAuditionList(classid);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(true, response)));
    }

    public static void getSmallMatch(CompositeSubscription cs, NetResponse response) {
        Observable<BaseListResponseModel<SmallMatchBean>> observable = RetrofitManager.getInstance().getService().getSmallMatch();
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createArraySubscriber(true, response)));
    }

    public static void getStageTest(CompositeSubscription cs, long paperId, long syllabusId, NetResponse response) {
        Observable<BaseResponseModel<StageBean>> observable = RetrofitManager.getInstance().getService().getStageTest(paperId, syllabusId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(true, response)));
    }

    public static void getSmallMatchReportList(CompositeSubscription cs, int page, int size, long startTime, long endTime, NetResponse response) {
        Observable<BaseResponseModel<SmallReportListBean>> observable = RetrofitManager.getInstance().getService().getSmallMatchReportList(page, size, startTime, endTime);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(true, response)));
    }

    public static void getSmallMatchReport(CompositeSubscription cs, long practiceId, final NetResponse response) {
        Observable<BaseResponseModel<SmallMatchReportBean>> observable = RetrofitManager.getInstance().getService().getSmallMatchReport(practiceId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    public static void getStageReport(CompositeSubscription cs, long practiceId, final NetResponse response) {
        Observable<BaseResponseModel<StageReportBean>> observable = RetrofitManager.getInstance().getService().getStageReport(practiceId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    public static void getStageOtherReport(CompositeSubscription cs, long practiceId, long syllabusId, final NetResponse response) {
        Observable<BaseResponseModel<StageReportOtherBean>> observable = RetrofitManager.getInstance().getService().getStageOtherReport(practiceId, syllabusId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    public static void getPreviewPaper(CompositeSubscription cs, int subjectId, final NetResponse response) {
        Observable<BaseListResponseModel<PreviewPaperBean>> observable = RetrofitManager.getInstance().getService().getPreviewPaper(subjectId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createArrayListSubscriber(response)));
    }

    public static void getFaceGoodsDetail(CompositeSubscription cs, String params, final NetResponse response) {
        Observable<BaseResponseModel<FaceGoodsDetailBean>> observable = RetrofitManager.getInstance().getService().getFaceGoodsDetail(params);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getFaceClassDetail(CompositeSubscription cs, String cityid, String courseid, String timestamp, String sign, final NetResponse response) {
        Observable<BaseResponseModel<FaceClassDetailBean>> observable = RetrofitManager.getInstance().getService().getFaceClassDetail(cityid, courseid, timestamp, sign);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void createFaceOrder(CompositeSubscription cs, String params, final NetResponse response) {
        Observable<BaseResponseModel<FacePlaceOrderBean>> observable = RetrofitManager.getInstance().getService().createFaceOrder(params);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void reportPlayEvent(HashMap<String, String> eventInfo, Subscriber<DeleteResponseBean> subscriber) {
       /* CompositeSubscription cs = RxUtils.getNewCompositeSubIfUnsubscribed(null);
        if (cs == null)
            return;
        Observable<DeleteResponseBean> observable = RetrofitManager.getInstance().getService().reportVideoPlayInfo(eventInfo);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));*/

        Observable<DeleteResponseBean> observable = RetrofitManager.getInstance().getService().reportVideoPlayInfo(eventInfo);
        observable.subscribeOn(Schedulers.io()).subscribe(subscriber);
    }

    public static void getSensorsStatistic(String classId, NetResponse response) {
        Observable<BaseResponseModel<CourseInfoForStatistic>> observable = RetrofitManager.getInstance().getService().getSensorsStatistic(classId);
        observable.subscribeOn(Schedulers.io()).subscribe(createObjectSubscriber(true, response));
    }

    public static void getBuyStatus(String classId, NetResponse response) {
        Observable<BaseResponseModel<BuyStatusData>> observable = RetrofitManager.getInstance().getService().getBuyStatus(classId);
        observable.subscribeOn(Schedulers.io()).subscribe(createObjectSubscriber(true, response));
    }

    public static void reportLiveRecord(String syllabusId, String bjyRoomId, int classId, int coursewareId) {
        try {
            JSONObject objectMap = new JSONObject();
            objectMap.put("syllabusId", syllabusId);
            objectMap.put("bjyRoomId", bjyRoomId);
            objectMap.put("classId", classId);
            objectMap.put("courseWareId", coursewareId);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), objectMap.toString());
            Observable<DeleteResponseBean> observable = RetrofitManager.getInstance().getService().reportLiveRecord(requestBody);
            Action1<DeleteResponseBean> onNext = Actions.empty();
            Action1<Throwable> onError = Actions.empty();
            Action0 onCompleted = Actions.empty();
            observable.subscribeOn(Schedulers.io()).subscribe(new ActionSubscriber<DeleteResponseBean>(onNext, onError, onCompleted));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getFaceOrderChildren(CompositeSubscription cs, String params, NetResponse response) {
        Observable<BaseListResponseModel<SplitOrderBean>> observable = RetrofitManager.getInstance().getService().getOrderChildren(params);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }


    public static void postFaceSplitOrders(CompositeSubscription cs, String params, NetResponse response) {
        Observable<BaseListResponseModel<String>> observable = RetrofitManager.getInstance().getService().postSplitOrder(params);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createListSubscriber(response)));
    }

    public static void postConvertManualCheck(CompositeSubscription cs, long answerId, int type, int delayStatus, NetResponse response) {
        Observable<BaseResponseModel<CoverManulResBean>> observable = RetrofitManager.getInstance().getService().postConvertManualCheck(answerId, type, delayStatus);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(false, response)));
    }

    public static void exerciseReport(CompositeSubscription cs, boolean isSingle, long answerId, long syllabusId, NetResponse response) {
        Observable<BaseResponseModel<ExerciseReportBean>> observable = isSingle ? RetrofitManager.getInstance().getService().exerciseSingleReport(answerId, syllabusId) : RetrofitManager.getInstance().getService().exerciseReport(answerId, syllabusId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));

    }

    public static void getHomeworkSingleList(CompositeSubscription cs, int courseType, long courseWareId, long syllabusId, NetResponse response) {
        Observable<BaseResponseModel<HomeworkSingleListBean>> observable = RetrofitManager.getInstance().getService().getHomeworkSingleList(courseType, courseWareId, syllabusId);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    public static void getAudioShareInfo(CompositeSubscription cs, long courseId, long courseWareId, long shareSyllabusId, int type, NetResponse response) {
        Observable<BaseResponseModel<ShareInfo>> observable = RetrofitManager.getInstance().getService().getAudioShareInfo(courseId, courseWareId, shareSyllabusId, type);
        cs.add(observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(createObjectSubscriber(response)));
    }

    private static void dealError(final Throwable e, final NetResponse response) {
        if (response != null) {
            response.onError(e);
        }
        if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            if (exception != null) {
                switch (exception.getErrorCode()) {
                    case ApiErrorCode.ERROR_TOKEN_CONFLICT:
                        TokenConflictActivity.newIntent(UniApplicationContext.getContext(), exception.getErrorMsg());
                        break;
                    case ApiErrorCode.ERROR_SESSION_TIMEOUT:
                        ActivityStack.getInstance().finishAllActivity();
                        LoginByPasswordActivity.newIntent(UniApplicationContext.getContext());
                        break;
                    case ApiErrorCode.ERROR_NOT_SETTING_SPECIAL:
                        Activity topAct = ActivityStack.getInstance().getTopActivity();
                        if (topAct != null) {
                            Intent intent = new Intent(topAct, DailySpecialSettingActivity.class);
                            intent.putExtra("fromActivity", "HomeFragment");
                            topAct.startActivity(intent);
                        } else {
                            Intent intent = new Intent(UniApplicationContext.getContext(),
                                    DailySpecialSettingActivity.class);
                            intent.putExtra("fromActivity", "HomeFragment");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            UniApplicationContext.getContext().startActivity(intent);
                        }
                        break;
                    default:
//                        String message = TextUtils.isEmpty(exception.getErrorMsg()) ?
//                                "网络请求错误，请重试" : exception.getErrorMsg();
//                        ToastUtils.showShort(message);
                }
            }
        } else {
            e.printStackTrace();
        }
    }

    private static Subscriber<? super BaseListResponseModel<?>> createListSubscriber(
            final NetResponse response) {
        Subscriber<? super BaseListResponseModel<?>> subscriber = new Subscriber<BaseListResponseModel<?>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                dealError(e, response);
            }

            @Override
            public void onNext(BaseListResponseModel<?> model) {
                if (model.code == ApiErrorCode.ERROR_SUCCESS || model.code == 200 || model.code == 0) {    // 华图教育，面授相关接口，返回 200 或 0 为成功
                    if (model.data != null && response != null && model.data instanceof List<?>) {
                        response.onListSuccess(model);
                    } else {
                        if (response != null) {
                            model.data = new ArrayList<>();
                            response.onListSuccess(model);
                        }
                        ToastUtils.showShort(model.message);
//                        ApiException exception = new ApiException(ApiErrorCode.ERROR_INVALID_DATA, model.message);
//                        dealError(exception, response);
                    }
                } else {
                    ApiException exception = new ApiException(model.code, model.message);
                    dealError(exception, response);
                }
            }
        };
        return subscriber;
    }

    private static Subscriber<? super BaseResponseModel<?>> createObjectSubscriber(
            final NetResponse response) {
        return createObjectSubscriber(true, response);
    }


    private static Subscriber<? super BaseResponseModel<?>> createObjectSubscriber(
            final boolean hasData, final NetResponse response) {
        Subscriber<? super BaseResponseModel<?>> subscriber = new Subscriber<BaseResponseModel<?>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                dealError(e, response);
            }

            @Override
            public void onNext(BaseResponseModel<?> model) {
                if (model.code == ApiErrorCode.ERROR_SUCCESS || model.code == 200 || model.code == 0) {    // 华图教育，面授相关接口，返回 200 或 0 为成功
                    if (!hasData || (hasData && model.data != null)) {
                        if (response != null) {
                            response.onSuccess(model);
                        }
                    } else {
                        ApiException exception = new ApiException(ApiErrorCode.ERROR_INVALID_DATA, model.message);
                        dealError(exception, response);
                    }
                } else {
                    ApiException exception = new ApiException(model.code, model.message);
                    dealError(exception, response);
                }
            }
        };
        return subscriber;
    }


    //增加ARRAY数据返回
    private static Subscriber<? super BaseListResponseModel<?>> createArrayListSubscriber(final NetResponse response) {
        return createArraySubscriber(true, response);
    }

    private static Subscriber<? super BaseListResponseModel<?>> createArraySubscriber(
            final boolean hasData, final NetResponse response) {
        Subscriber<? super BaseListResponseModel<?>> subscriber = new Subscriber<BaseListResponseModel<?>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                try {
                    dealError(e, response);
                } catch (Exception ex) {
                }
            }

            @Override
            public void onNext(BaseListResponseModel<?> model) {
                if (model.code == ApiErrorCode.ERROR_SUCCESS || model.code == 200 || model.code == 0) {    // 华图教育，面授相关接口，返回 200 或 0 为成功
                    if (!hasData || (hasData && model.data != null)) {
                        if (response != null) {
                            response.onListSuccess(model);
                        }
                    } else {
                        ApiException exception = new ApiException(
                                ApiErrorCode.ERROR_INVALID_DATA, model.message);
                        dealError(exception, response);
                    }
                } else {
                    ApiException exception = new ApiException(model.code, model.message);
                    dealError(exception, response);
                }
            }
        };
        return subscriber;
    }
}
