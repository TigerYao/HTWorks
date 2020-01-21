package com.huatu.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UmengMessageHandlerImpl;
import com.huatu.handheld_huatu.mvpmodel.PushMessage;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.umeng.message.UmengNotifyClickActivity;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * Created by Administrator on 2019\8\7 0007.
 */


public class MipushActivity extends UmengNotifyClickActivity {

    private static String TAG = MipushActivity.class.getName();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mipush);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i(TAG, body);

        boolean actionDone=false;
        try{
            UMessage var3   = new UMessage(new JSONObject(body));
            PushMessage pushMessage = GsonUtil.GsonToBean(var3.custom, PushMessage.class);
            if(pushMessage!=null){
                actionDone=true;
                Intent action=  UmengMessageHandlerImpl.getActionByMessage(this,pushMessage,true);
                this.startActivity(action);
            }
        }catch (Exception e){}

        if(!actionDone){
             Intent action2=new Intent(this,com.huatu.handheld_huatu.business.guide.SplashActivity.class);
             action2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(action2);
        }
        this.finish();


        //{"display_type":"notification","extra":{"test":"2","eeee":"vvvvv"},"msg_id":"uunooxa156523528131610","body":{"after_open":"go_app","play_lights":"false","ticker":"测试机苹果X","play_vibrate":"false","text":"测试机苹果X","title":"测试机苹果X","play_sound":"true"},"random_min":0}
      //  {"display_type":"notification","extra":{"eeee":"vvvvv"},"msg_id":"uu7ewlo156523260233210","body":{"after_open":"go_app","play_lights":"false","ticker":"eeeeeeeeeeeeee","play_vibrate":"false","text":"dddddddddddddd","title":"eeeeeeeeeeeeee","play_sound":"true"},"random_min":0}
    }
}