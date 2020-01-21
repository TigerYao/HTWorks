package com.huatu.handheld_huatu;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.me.AboutActivity;
import com.huatu.handheld_huatu.business.message.MessageGroupListFragment;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.PushMessage;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.music.utils.LogUtil;
import com.netease.hearttouch.router.HTPageRouterCall;
import com.umeng.message.UTrack;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * Created by Administrator on 2019\12\9 0009.
 *     //https://developer.umeng.com/docs/66632/detail/98583
 */

public class UmengNotificationClickHandlerImpl extends UmengNotificationClickHandler {

    @Override
    public void handleMessage(Context context,final UMessage uMessage) {
        StudyCourseStatistic.openPushMsg(uMessage.msg_id, uMessage.title);
        super.handleMessage(context, uMessage);
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                UTrack.getInstance(UniApplicationContext.getContext()).trackMsgClick(uMessage);
            }
        });
    }

    @Override
    public void dealWithCustomAction(Context context,final UMessage uMessage) {
      //  Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                UTrack.getInstance(UniApplicationContext.getContext()).trackMsgClick(uMessage);
            }
        });

        LogUtil.e("dealWithCustomMessage", "dealWithCustomMessage: " + uMessage.getRaw().toString());
        boolean actionDone=false;
        try{

            PushMessage pushMessage = GsonUtil.GsonToBean(uMessage.custom, PushMessage.class);
            if(pushMessage!=null){
                actionDone=true;
                Intent action=  UmengMessageHandlerImpl.getActionByMessage(context,pushMessage,true);
                action.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(action);
            }
        }catch (Exception e){
            actionDone=false;
        }
        if(!actionDone){
            Intent action2=BaseFrgContainerActivity.createIntent(context, MessageGroupListFragment.class.getName(), null);;
            action2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            action2.putExtra("toHome", true);
            context.startActivity(action2);
        }
    }

    @Override
    public void launchApp(Context var1, UMessage var2){
        if(var2.custom != null && !TextUtils.isEmpty(var2.custom.trim())) {
            this.dealWithCustomAction(var1, var2);
        }
        else {
            super.launchApp(var1,var2);
        }

    }
}
