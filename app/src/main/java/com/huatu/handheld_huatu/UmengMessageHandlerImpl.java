package com.huatu.handheld_huatu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.baidu.mobstat.MtjConfig;
import com.google.gson.Gson;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.ReuseActivityHelper;
import com.huatu.handheld_huatu.business.arena.activity.AdvertiseActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamAreaActivity;
import com.huatu.handheld_huatu.business.arena.activity.ManualCheckReportActivity;
import com.huatu.handheld_huatu.business.arena.activity.SimulationExamActivity;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.essay.mainfragment.CheckCorrectEssay;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.matches.cache.MatchCacheData;
import com.huatu.handheld_huatu.business.me.ActionDetailActivity;
import com.huatu.handheld_huatu.business.message.MessageGroupListFragment;
import com.huatu.handheld_huatu.business.message.MessageListFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.mvpmodel.PushMessage;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.utils.StringUtils;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by saiyuan on 2016/12/22.
 */

public class UmengMessageHandlerImpl extends UmengMessageHandler {

    private boolean toHome = true;

    @Override
    public void dealWithCustomMessage(Context context, final UMessage uMessage) {
        super.dealWithCustomMessage(context, uMessage);
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                UTrack.getInstance(UniApplicationContext.getContext()).trackMsgClick(uMessage);
            }
        });

        Log.e("dealWithCustomMessage", "dealWithCustomMessage: " + uMessage.getRaw().toString());
        PushMessage pushMessage = GsonUtil.GsonToBean(uMessage.custom, PushMessage.class);
        if(null!=pushMessage){
           showNotification(context, pushMessage);
        }
    }

    public void showNotification(Context context, PushMessage msg) {
        showNotification(context, msg, toHome);
    }

    public static Intent getActionByMessage(Context context, PushMessage msg, boolean toHome) {
        Intent intent = null;
        boolean jump = true;
        int flag = Intent.FLAG_ACTIVITY_NEW_TASK;
        switch (msg.type) {
            case 0://url 广告页
                intent = new Intent(context, AdvertiseActivity.class);
                // push 标识
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                // 建议添加，如果是敏感信息，可以不添加，则报表上也就无法展现
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "广告页");
                intent.putExtra("url", msg.url);
                break;
            case 1://课程详情页
//                intent = new Intent(context, BuyDetailsActivity.class);
                intent = new Intent(context, BaseIntroActivity.class);
                intent.putExtra("NetClassId", msg.NetClassId);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "课程详情页");
                break;
            case 2://活动页面
                intent = new Intent(context, ActionDetailActivity.class);
                intent.putExtra("type", msg.type);
                intent.putExtra("url", msg.url);
                intent.putExtra("activityTitle", msg.activityTitle);
                intent.putExtra("id", msg.id);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "活动页面");
                break;
            //图书3
            case 4: //竞技场页面
//                intent = new Intent(context, AthleticHomeActivity.class);
//                intent.putExtra("toHome", toHome);
//                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
//                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "竞技场页面");
                break;
            case 5: //真题列表
                intent = new Intent(context, ArenaExamAreaActivity.class);
                intent.putExtra("request_type", ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN);
                intent.putExtra("toHome", toHome);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "真题列表");
                break;
            case 6: //估分列表
                intent = new Intent(context, SimulationExamActivity.class);
                intent.putExtra("request_type", ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN);
                intent.putExtra("toHome", toHome);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "估分列表");
                break;
            case 7: //专项模考列表
                intent = new Intent(context, SimulationExamActivity.class);
                intent.putExtra("request_type", ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN);
                intent.putExtra("toHome", toHome);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "专项模考列表");
                break;
//            case 8: //真题做题页面
//                //真题演练  type 3|id 真题试卷id
//                // 必添加字段action，常规做题为0 不做题直接看答题报告和解析为1(直接看解析需要增加practiseId属性，即练习/答题卡id)
//                //必添加字段freshness，新做一份题为0，继续做题为1(继续做题需要增加practiseId属性，即练习/答题卡id)
//                Bundle bundle = new Bundle();
//                UserMetaBean userMeta = params.pastPaper.userMeta;
//                if (userMeta == null) {
//                    //第一次做题
//                    bundle.putLong("point_ids", params.pastPaper.id);
//                } else if (userMeta != null && userMeta.currentPracticeId == -1) {
//                    //存在UserMeta，习题做完，重新做题
//                    bundle.putLong("point_ids", params.pastPaper.id);
//                } else {
//                    //存在userMeta 并且userMeta的getCurrentPracticeId不为-1
//                    //表示上一次没有做完试题
//                    bundle.putLong("practice_id", userMeta.currentPracticeId);
//                    bundle.putBoolean("continue_answer", true);
//                }
//                intent = new Intent(activity, ArenaExamActivity.class);
//                intent.putExtra("request_from", ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN);
//                intent.putExtra("extra_args", bundle);
//                intent.putExtra("toHome", toHome);
//                activity.startActivityForResult(intent, 1033);
//                break;
            case 9: //模考大赛报名页
                MatchCacheData.getInstance().matchPageFrom = "app消息";
                intent = new Intent(context, SimulationContestActivityNew.class);
                intent.putExtra("subject", SpUtils.getUserSubject());
                intent.putExtra("mToHome", toHome);
                break;
            case 10: //直播列表
                intent = new Intent(context, MainTabActivity.class);
                intent.putExtra("require_index", 1);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "直播列表");
//                    EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_LIVE_TAB, new MeMsgMessageEvent());
                break;
            case 11: //录播列表
                intent = new Intent(context, MainTabActivity.class);
                intent.putExtra("require_index", 2);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_CALL, true);
                intent.putExtra(MtjConfig.BAIDU_MTJ_PUSH_MSG, "录播列表");
//                    EventBusUtil.sendMessage(MeMsgMessageEvent.MMM_MSG_ME_MESSAGE_SEL_RECORDING_TAB, new
//                            MeMsgMessageEvent());
                break;
            case 12: //消息中心
                Bundle bundle = new Bundle();
                bundle.putString("view", msg.view);
                bundle.putBoolean("from_push", true);
                intent = ReuseActivityHelper.builder(context).setFragmentParameter(new FragmentParameter(MessageListFragment.class, bundle)).build();
                break;

            case 13: //人工批改记录列表页
                Bundle params = new Bundle();
                params.putBoolean("from_push", true);
                params.putInt("topicType", msg.topicType);
                intent = BaseFrgContainerActivity.createIntent(context, CheckCorrectEssay.class.getName(), params);
                flag |= Intent.FLAG_ACTIVITY_CLEAR_TOP;
//                EssayExamCheckDetailV2.lanuchForMsg(context,msg.topicType,msg.answerCardId,msg.questionBaseId,msg.paperId,msg.areaName,msg.paperName);
                break;
            case 14: //人工批改报告
                intent = new Intent(context, ManualCheckReportActivity.class);
                Bundle mEssayBundle = new Bundle();
                mEssayBundle.putLong("questionBaseId", msg.questionBaseId);
                mEssayBundle.putLong("paperId", msg.paperId);
                mEssayBundle.putString("areaName", msg.areaName);
                mEssayBundle.putLong("answerId", msg.answerCardId);
                mEssayBundle.putString("titleView", msg.paperName);
                mEssayBundle.putBoolean("isSingle", (msg.topicType == 0 || msg.topicType == 2));
                mEssayBundle.putBoolean("isFromArgue", true);
                intent.putExtra("params", mEssayBundle);
                intent.putExtra("from_push", true);
//                ManualCheckReportActivity.startForMsgCheckReport(context, msg.topicType,msg.answerCardId,msg.questionBaseId,msg.paperId);
                break;
            case 15:
                //课程大纲
                intent = new Intent(context, BJRecordPlayActivity.class);
                intent.putExtra(ArgConstant.TYPE, msg.isLive);
                intent.putExtra("classid", msg.netClassId);
                intent.putExtra(ArgConstant.LESSION_ID, msg.lession_id);
                intent.putExtra("toHome", true);
                intent.putExtra(ArgConstant.FROM_ACTION, StringUtils.parseInt(msg.syllabusId));
                break;

            default://什么都不做
                jump = false;
                break;
        }
        if (null != intent) {
            intent.setFlags(flag);
            intent.putExtra(ArgConstant.TO_HOME, toHome);
        }
        return intent;
    }

    public static void showNotification(Context context, PushMessage msg, boolean toHome) {
        NotificationManager systemService = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "message_channel_id");
        builder.setSmallIcon(R.drawable.icon_app)
                .setContentTitle(msg.title)
                .setContentText(msg.content)
                .setTicker("您有一条新的消息")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_DEFAULT);
        //增加channel
        boolean compatFlag = getTargetSdkVersion(context) >= 26;
        if (Build.VERSION.SDK_INT >= 26 && compatFlag) {
            NotificationChannel mChannel = new NotificationChannel("message_channel_id", "message_channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            systemService.createNotificationChannel(mChannel);
            builder.setChannelId("message_channel_id");
        }
        Intent action = getActionByMessage(context, msg, toHome);
        if (null != action) {
            //  action.setFlags(flag);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, action, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.setContentIntent(pendingIntent);
        }
        systemService.notify(1, builder.build());
    }

    private static int getTargetSdkVersion(Context var0) {
        int var1 = 0;

        try {
            PackageInfo var2 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0);
            var1 = var2.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
        }

        return var1;
    }

}
