package com.huatu.handheld_huatu.business.arena.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huatu.AppContextProvider;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.activity.ExamPaperActivity;
import com.huatu.handheld_huatu.business.essay.mainfragment.ArgumentEssay;
import com.huatu.handheld_huatu.business.essay.mainfragment.MultExamEssay;
import com.huatu.handheld_huatu.business.guide.SplashActivity;
import com.huatu.handheld_huatu.business.main.CreamArticleFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.main.MoreCourseListFragment;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.business.matchsmall.activity.SmallMatchActivity;
import com.huatu.handheld_huatu.business.me.ActionDetailActivity;
import com.huatu.handheld_huatu.business.me.order.OrderActivity;
import com.huatu.handheld_huatu.business.me.order.OrderDetailActivity;
import com.huatu.handheld_huatu.business.other.DetailScrollViewFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.event.me.MeMsgMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseItem;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaItem;
import com.huatu.handheld_huatu.mvpmodel.exercise.PracticeIdBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.UserMetaBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.BuyStatusData;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.EventBusUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.netease.hearttouch.router.HTPageRouterCall;
import com.netease.hearttouch.router.HTRouterEntry;
import com.netease.hearttouch.router.HTRouterManager;

import org.greenrobot.eventbus.EventBus;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by michael on 17/9/13.
 */

public class ZtkSchemeTargetStartTo {
    private static final String TAG = "ZtkSchemeTargetStartTo";
// "ztk://h5/active":"H5活动页面",
//         "ztk://h5/simulate":"H5模考页面",
//         "ztk://course/detail":"课程页面",
//         "ztk://arena/home":"竞技场页面",
//         "ztk://pastPaper/home":"真题列表",
//         "ztk://estimatePaper/home":"估分列表",
//         "ztk://simulatePaper/home":"模考列表",
//         "ztk://pastPaper":"真题做题页面",
//         "ztk://estimatePaper":"估分做题页面",
//         "ztk://simulatePaper":"模考做题页面",
//         "ztk://match/detail":"模考大赛详情页"

//          "ztk://pastPaper/home":"真题列表",
//                  "ztk://estimatePaper/home":"估分列表",
//                  "ztk://simulatePaper/home":"模考列表",
//                  "ztk://estimatePaper":"估分做题页面",
//                  "ztk://simulatePaper":"模考做题页面",
//                  "ztk://match/detail":"模考大赛详情页"
//     "ztk://live/home" : "直播列表",
    //            "ztk://recording/home" :"录播列表"

    //        "ztk://small/estimate"           小模考
    //          "ztk://exquisite/small/course"   精品微课
    //           "ztk://exam/articles"            备考精华


    //
    public static boolean doPopDialogAction(Activity context, AdvertiseConfig config, CompositeSubscription compositeSubscription) {
        AdvertiseItem params = config.params;
        if (params == null) return false;
        Intent intent;
        boolean hasHandle = false;

        switch (config.target) {
            case "ztk://pastPaper":
                //真题演练  type 3|id 真题试卷id
                // 必添加字段action，常规做题为0 不做题直接看答题报告和解析为1(直接看解析需要增加practiseId属性，即练习/答题卡id)
                //必添加字段freshness，新做一份题为0，继续做题为1(继续做题需要增加practiseId属性，即练习/答题卡id)
                Bundle bundle = new Bundle();
                UserMetaBean userMeta = params.pastPaper.userMeta;
                if (userMeta == null) {
                    //第一次做题
                    bundle.putLong("point_ids", params.pastPaper.id);
                } else if (userMeta != null && userMeta.currentPracticeId == -1) {
                    //存在UserMeta，习题做完，重新做题
                    bundle.putLong("point_ids", params.pastPaper.id);
                } else {
                    //存在userMeta 并且userMeta的getCurrentPracticeId不为-1
                    //表示上一次没有做完试题
                    bundle.putLong("practice_id", userMeta.currentPracticeId);
                    bundle.putBoolean("continue_answer", true);
                }
                ArenaExamActivityNew.show(context, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, bundle);
                break;
//            case "ztk://estimatePaper":
//                if (params.estimatePaper!=null){
//                SimulationListItem item = params.estimatePaper;
//                UserMetaBean userMetaBean = item.userMeta;
//                Bundle arg = new Bundle();
//                int requestType = ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN;
//                arg.putLong("point_ids", item.id);
//                //试卷活动状态 1未开始，2正在进行，3已经结束，4已经下线，5可继续做题，6，可查看报告，7未出报告
//                switch (item.status) {
//                    case 1:
//                        if (!TextUtils.isEmpty(item.url)) {
//                            AdvertiseActivity.newInstance(context, "", item.url, 1, item.name);
//                        }
//                        break;
//                    case 2://正在进行
//                        ArenaExamActivityNew.show(context, requestType, arg);
//                        break;
//                    case 5:
//                        //可继续做题
//                        arg.putBoolean("continue_answer", true);
//                        arg.putLong("practice_id", userMetaBean.currentPracticeId);
//                        ArenaExamActivityNew.show(context, requestType, arg);
//                        break;
//                    case 6:
//                        if (userMetaBean != null && userMetaBean.practiceIds != null
//                                && userMetaBean.practiceIds.length > 0) {
//                            arg.putLong("practice_id", userMetaBean.practiceIds[userMetaBean.practiceIds.length - 1]);
//                            ArenaExamReportActivity.show(context, requestType, arg);
//                        }
//                        break;
//                }
//                }
//                break;
            default:
                MatchCacheData.getInstance().matchPageFrom = "app首页弹出图";
                ZtkSchemeTargetStartTo.startTo(context, params, config.target, false, compositeSubscription);
                break;

        }
        return false;
    }

    public static void startTo(final Activity activity, final AdvertiseItem params, String target, final boolean toHome, CompositeSubscription compositeSubscription) {
        startTo(activity, params, target, toHome, compositeSubscription, null);
    }

    //    ztk://h5/active ztk://arena/home ztk://h5/simulate ztk://course/detail ztk://pastPaper/home ztk://estimatePaper/home ztk://simulatePaper/home ztk://estimatePaper   ztk://pastPaper  ztk://simulatePaper  ztk://match/detail  ztk://live/home  ztk://recording/home  ztk://course/collection   ztk://course/seckill
    public static void startTo(final Activity activity, final AdvertiseItem params, String target, final boolean toHome, CompositeSubscription compositeSubscription, String pageSource) {
        if (params == null || activity == null) return;
        Intent intent;
        if (!toHome) {
            ArenaDataCache.getInstance().onclick_ZtkSchemeTargetStartTo = true;
        }
        Log.i(TAG, target);
        HTRouterEntry entity = HTRouterManager.findRouterEntryByUrl(target);
        //  ztk://h5/active  ztk://arena/home  ztk://h5/simulate  ztk://course/detail
        //为了防止匹配不上后循环跳，这里需要有个判断
        if (entity != null) {
            HTPageRouterCall.newBuilderV2(target)
                    .context(activity).sourceIntent(new Intent().setData(Uri.parse(target + "?" + params.formatString(toHome, pageSource))))
                    .build()
                    .start();
            return;
        }
        switch (target) {
            case "ztk://estimatePaper":                                             // 估分做题页面
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                int requestType = ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN;
                Bundle bundle1 = new Bundle();
                bundle1.putLong("point_ids", params.paperId);
                ArenaExamActivityNew.show(activity, requestType, bundle1);
//                if (params.estimatePaper!=null){
//                SimulationListItem item = params.estimatePaper;
//                UserMetaBean userMetaBean = item.userMeta;
//                Bundle arg = new Bundle();
//                arg.putBoolean("toHome", toHome);
//                int requestType = ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN;
//                arg.putLong("point_ids", item.id);
//                // 1、未开始，
//                // 2、正在进行，
//                // 3、已经结束，
//                // 4、已经下线，
//                // 5、可继续做题，
//                // 6、查看报告，可
//                // 7、未出报告
//                switch (item.status) {
//                    case 1:     // 1、未开始
//                        if (!TextUtils.isEmpty(item.url)) {
//                            intent = new Intent(activity, AdvertiseActivity.class);
//                            intent.putExtra("url", item.url);
//                            intent.putExtra("type", 1);
//                            intent.putExtra("name", item.name);
//                            intent.putExtra("toHome", toHome);
//                            activity.startActivity(intent);
//                        }
//                        break;
//                    case 2:     // 2、正在进行
//                        ArenaExamActivityNew.show(activity, requestType, arg);
//                        break;
//                    case 5:     // 5、可继续做题
//                        arg.putBoolean("continue_answer", true);
//                        arg.putLong("practice_id", userMetaBean.currentPracticeId);
//                        ArenaExamActivityNew.show(activity, requestType, arg);
//                        break;
//                    case 6:     // 6、可查看报告
//                        if (userMetaBean != null && userMetaBean.practiceIds != null && userMetaBean.practiceIds.length > 0) {
//                            arg.putLong("practice_id", userMetaBean.practiceIds[userMetaBean.practiceIds.length - 1]);
//                            ArenaExamReportActivity.show(activity, requestType, arg);
//                        }
//                        break;
//                }}
                break;
            case "ztk://pastPaper":                                                 // 真题做题页面
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                //真题演练  type 3|id 真题试卷id
                // 必添加字段action，常规做题为0 不做题直接看答题报告和解析为1(直接看解析需要增加practiseId属性，即练习/答题卡id)
                //必添加字段freshness，新做一份题为0，继续做题为1(继续做题需要增加practiseId属性，即练习/答题卡id)
                final Bundle bundle = new Bundle();
                final long mPaperId = params.paperId;
                ServiceProvider.getCurrentPracticeId(compositeSubscription, params.paperId, new NetResponse() {
                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        super.onSuccess(model);
                        if (model != null && model.data != null) {
                            PracticeIdBean practiceIdBean = (PracticeIdBean) model.data;
                            if (practiceIdBean != null && practiceIdBean.practiceId > 0) {
                                bundle.putLong("practice_id", practiceIdBean.practiceId);
                                bundle.putBoolean("continue_answer", true);
                            } else {
                                bundle.putLong("point_ids", mPaperId);
                            }
                            bundle.putBoolean("toHome", toHome);
                            ArenaExamActivityNew.show(activity, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, bundle);
                        } else {
                            if (toHome) {
                                MainTabActivity.newIntent(activity);
                            }
                        }
                        if (toHome) {
                            activity.finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (toHome) {
                            MainTabActivity.newIntent(activity);
                            activity.finish();
                        }
                    }

                });

                break;
            case "ztk://simulatePaper":                                             // 模考做题页面
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                Bundle arg2 = new Bundle();
                int paperId = params.paperId;
                arg2.putLong("point_ids", paperId);
                arg2.putBoolean("toHome", toHome);

                ArenaExamActivityNew.show(activity, ArenaConstant.EXAM_ENTER_FORM_TYPE_SIMULATION_CONTEST, arg2);
                break;
            case "ztk://match/detail":                                              // 模考大赛首页
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }

                Intent intentSc = new Intent(activity, SimulationContestActivityNew.class);
                intentSc.putExtra("mToHome", toHome);
                intentSc.putExtra("showSubject", params.subject);
                intentSc.putExtra("subject", SignUpTypeDataCache.getInstance().getCurSubject());
                activity.startActivity(intentSc);
                if (toHome) {
                    activity.finish();
                }
                break;
            case "ztk://match/essay":             // 申论模考大赛首页
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                    Intent intentScA = new Intent(activity, SimulationContestActivityNew.class);
                    intentScA.putExtra("mToHome", toHome);
                    intentScA.putExtra("subject", Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                    activity.startActivity(intentScA);
                    if (toHome) {
                        activity.finish();
                    }
                } else {
                    ToastUtils.showMessage("配置错误，请正确配置跳转地址");
                }
                break;
            case "ztk://live/home":                                                 // 直播列表
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                if (activity instanceof SplashActivity) {
                    MainTabActivity.newIntent(activity);
                    TimeUtils.delayTask(new Runnable() {
                        @Override
                        public void run() {
                            EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_LIVE_TAB, new MeMsgMessageEvent());
                        }
                    }, 500);
                } else {
                    SpUtils.setSelectedLiveCategory(params.cateId);
                    AppContextProvider.addFlags(AppContextProvider.BANNRCOURSETYPE);
                    EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_LIVE_TAB, new MeMsgMessageEvent());
                }
                break;
            case "ztk://recording/home":                                            // 录播列表
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                if (activity instanceof SplashActivity) {
                    MainTabActivity.newIntent(activity);
                    TimeUtils.delayTask(new Runnable() {
                        @Override
                        public void run() {
                            EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_RECORDING_TAB, new MeMsgMessageEvent());
                        }
                    }, 500);
                } else {
                    EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_RECORDING_TAB, new MeMsgMessageEvent());
                }
                break;
    /*        case "ztk://course/collection":                                         // 课程合集页面
                CourseCollectSubsetFragment.show(activity, params.rid + "", params.shortTitle, params.title, toHome);
                break;*/
            case "ztk://course/seckill":                                            // 秒杀课程
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                BaseFrgContainerActivity.newInstance(activity,
                        SecKillFragment.class.getName(),
                        SecKillFragment.getArgs(params.rid + "", params.title, toHome));
                break;
            case "ztk://essay/home":// 申论首页
                if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                    if (!toHome) {
                        // SignUpTypeDataCache.getInstance().curSubject = Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS;
                        SignUpTypeDataCache.getInstance().setCurSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                        SpUtils.setUserSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_CHANGE_TO_ESSAY));
                    } else {
                        //SignUpTypeDataCache.getInstance().curSubject = Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS;
                        SignUpTypeDataCache.getInstance().setCurSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                        SpUtils.setUserSubject(Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS);
                        MainTabActivity.newIntent(activity);
                    }
                } else {
                    ToastUtils.showMessage("配置错误，请正确配置跳转地址");
                }
                break;
            case "ztk://essay/paper":// 申论套题
                if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                    Bundle bundles = new Bundle();
                    bundles.putLong("areaId", params.areaId);
                    bundles.putBoolean("toHome", toHome);
                    BaseFrgContainerActivity.newInstance(activity,
                            MultExamEssay.class.getName(), bundles);
                } else {
                    ToastUtils.showMessage("配置错误，请正确配置跳转地址");
                }
                break;
            case "ztk://essay/argument":// 申论文章写作
                if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                    Bundle bundles = new Bundle();
                    bundles.putBoolean("toHome", toHome);
                    BaseFrgContainerActivity.newInstance(activity,
                            ArgumentEssay.class.getName(), null);
                } else {
                    ToastUtils.showMessage("配置错误，请正确配置跳转地址");
                }
                break;
            case "ztk://small/estimate":// 小模考
                if (!CommonUtils.checkLogin(activity)) {
                    return;
                }
                if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                    Bundle args = new Bundle();
                    args.putBoolean("toHome", true);
                    SmallMatchActivity.show(activity, ArenaConstant.EXAM_ENTER_FORM_TYPE_SMALL_MATCH, args);
                } else {
                    ToastUtils.showMessage("配置错误，请正确配置跳转地址");
                }
                break;
            case "ztk://pastPaper/province": //真题演练具体省份列表
                int requestTypes = ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN;
                ExamAreaItem examAreaItem = new ExamAreaItem();
                examAreaItem.area = params.area;
                examAreaItem.areaName = params.areaName;
                intent = new Intent(activity, ExamPaperActivity.class);
                intent.putExtra("examAreaData", examAreaItem);
                intent.putExtra("request_type", requestTypes);
                activity.startActivity(intent);
                break;

            default:
                break;
        }
    }

    //M站跳原生
    public static void startPageFromUri(final Context ctx, Uri uri) {
        if (uri == null)
            return;
        String path = uri.toString();
        if (TextUtils.isEmpty(path))
            return;
        if (!path.startsWith("ztk://course")) {
            return;
        }
        final Intent intent = new Intent();
        String scheme = uri.getScheme();
        String host = uri.getHost();
        String pathPrefix = uri.getPath();
        String query = uri.getQuery();
        LogUtils.d("ZtkSchemeTargetStartTos", "scheme = " + scheme + ", host = " + host + ", path = " + pathPrefix + ", query = " + query);
        if (path.startsWith("ztk://course/yearreport")) { //年报
            intent.setClass(ctx, ActionDetailActivity.class);
            intent.setData(Uri.parse(path.replace("#","{n}")));
            ctx.startActivity(intent);
        }
       else if (path.startsWith("ztk://course/detail")) {
            //课程详情
            intent.setClass(ctx, BaseIntroActivity.class);
            intent.setData(uri);
            ctx.startActivity(intent);
        } else if (path.startsWith("ztk://course/more")) {
            Log.i("TAG", "----- " + path);
            //1 查看更多课程
            String typeId = uri.getQueryParameter("typeId");
            String title = uri.getQueryParameter("title");
            String categoryId = uri.getQueryParameter("categoryId");
            LogUtils.d("ZtkSchemeTargetStartTos", title);
            String mTitle = Uri.decode(title);
            MoreCourseListFragment.launch(ctx, typeId, mTitle, categoryId);
        } else if (path.startsWith("ztk://course/exam/articles")) {
            //2 备考精华（乐见）
            BaseFrgContainerActivity.newInstance(ctx, CreamArticleFragment.class.getName(), null);
        } else if (path.startsWith("ztk://course/articles/detail")) {
            //3 备考精华详情（乐见文章详情）
            String id = uri.getQueryParameter("Id");
            long mId = Long.parseLong(id);
            DetailScrollViewFragment.lanuch(ctx, mId);

        } else if (path.startsWith("ztk://course/study/home")) {
            //4 学习列表
            EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_RECORDING_TAB, new MeMsgMessageEvent());
        } else if (path.startsWith("ztk://course/me")) {
            //5 我的页面
            EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_ME, new MeMsgMessageEvent());
        } else if (path.startsWith("ztk://course/order/home")) {
            //6 订单列表
            intent.setClass(ctx, OrderActivity.class);
            ctx.startActivity(intent);
        } else if (path.startsWith("ztk://course/order/detail")) {
            //7 课程订单详情
            String orderId = uri.getQueryParameter("orderId");
            String isCollage = uri.getQueryParameter("isCollage");
            int mIsCollage = Integer.parseInt(isCollage);
            intent.setClass(ctx, OrderDetailActivity.class);
            intent.putExtra("type", "1");
            intent.putExtra("orderId", orderId);
            intent.putExtra("isCollage", mIsCollage);
            ctx.startActivity(intent);
        } else if (path.startsWith("ztk://course/sold/syllabus")) {
            //8 售后大纲 先判断是否购买过
            final String classId = uri.getQueryParameter("classId");
            final String type = uri.getQueryParameter("type");
            ServiceProvider.getBuyStatus(classId, new NetResponse() {
                @Override
                public void onSuccess(BaseResponseModel model) {
                    super.onSuccess(model);
                    if (model != null && model.data != null) {
                        BuyStatusData data = (BuyStatusData) model.data;
                        int isBuy = data.isBuy;
                        if (isBuy == 1) {
                            //已买
                            int Type = Integer.parseInt(type);
                            intent.setClass(ctx, BJRecordPlayActivity.class);
                            intent.putExtra(ArgConstant.TYPE, Type);
                            intent.putExtra("classid", classId);
                            ctx.startActivity(intent);
                        } else {
                            //未买
                            ToastUtils.showEssayToast("还未购买过该课程");
                        }
                    } else {
                        ToastUtils.showEssayToast("网络出错啦");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    ToastUtils.showEssayToast("服务内部网络出错啦");
                    Log.d(TAG, "这里错了", e);
                }
            });

        }
//        else if (path.startsWith("ztk://course/collection")){
//            //9 课程合集
//            String rid = uri.getQueryParameter("rid");
//            String TITLE = uri.getQueryParameter("title");
//            String mTitle=Uri.decode(TITLE);
//            CourseCollectSubsetFragment.show(ctx, rid, mTitle, mTitle,true);
//
//        }
        //直播教室和直播回放先不跳了
//        else if (path.startsWith("ztk://course/live/room")){
//            //9 直播教室 先判断是否购买过
//            Gson gson=new Gson();
//            final String courseId =uri.getQueryParameter("courseId ");
//            String cuLession =uri.getQueryParameter("cuLession");
//            final CourseWareInfo mCourseWareInfo=gson.fromJson(cuLession,CourseWareInfo.class);
//            ServiceProvider.getBuyStatus(courseId,new NetResponse(){
//                @Override
//                public void onSuccess(BaseResponseModel model) {
//                    super.onSuccess(model);
//                    if (model!=null&&model.data!=null){
//                        BuyStatusData data= (BuyStatusData) model.data;
//                        int isBuy= data.isBuy;
//                        if (isBuy==1){
//                            //已买
//                            LiveVideoForLiveActivity.start(ctx,courseId,mCourseWareInfo,null);
//                        }else {
//                            //未买
//                            ToastUtils.showEssayToast("还未购买过该课程");
//                        }
//                    }else{
//                        ToastUtils.showEssayToast("网络出错");
//                    }
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    super.onError(e);
//                    ToastUtils.showEssayToast("网络出错");
//                }
//            });
//        }else if (path.startsWith("ztk://course/playback/room")){
//            //10 直播回放教室 先判断是否购买过
//
//            Gson gson=new Gson();
//            final String courseId =uri.getQueryParameter("courseId ");
//            String cuLession =uri.getQueryParameter("cuLession");
//            final CourseWareInfo mCourseWareInfo=gson.fromJson(cuLession,CourseWareInfo.class);
//
//            ServiceProvider.getBuyStatus(courseId,new NetResponse(){
//                @Override
//                public void onSuccess(BaseResponseModel model) {
//                    super.onSuccess(model);
//                    if (model!=null&&model.data!=null){
//                        BuyStatusData data= (BuyStatusData) model.data;
//                       int isBuy= data.isBuy;
//                        if (isBuy==1){
//                            //已买
//                            LiveVideoForLiveActivity.start(ctx,courseId,mCourseWareInfo,null);
//                        }else {
//                            //未买
//                            ToastUtils.showEssayToast("还未购买过该课程");
//                        }
//                    }else{
//                        ToastUtils.showEssayToast("网络出错");
//                    }
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    super.onError(e);
//                    ToastUtils.showEssayToast("网络出错");
//                }
//            });
//        }
    }
}
