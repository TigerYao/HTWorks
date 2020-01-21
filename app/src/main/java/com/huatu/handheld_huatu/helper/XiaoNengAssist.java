package com.huatu.handheld_huatu.helper;


import android.app.Activity;

import com.huatu.chat.XiaoNengProvider;
import com.huatu.handheld_huatu.business.me.AboutActivity;

import java.util.ServiceLoader;

/**
 * Created by cjx on 2019\10\16 0016.
 */

public class XiaoNengAssist {

    private static XiaoNengAssist instance;
    private boolean isInited = false;

    public boolean isInited(){
        return isInited;
    }

    public static XiaoNengAssist getInstance() {
        if (instance == null) {
            synchronized (XiaoNengAssist.class) {
                if(instance==null)
                instance = new XiaoNengAssist();
            }
        }
        return instance;
    }

    private XiaoNengProvider mXiaoNengChatProivder;

    public void init(Activity activity){
         if(mXiaoNengChatProivder!=null){
             mXiaoNengChatProivder.init(activity);
             return;
         }
        //反射加载，https://www.2cto.com/kf/201712/705494.html
        //没有单例，故只有自已保存
        ServiceLoader<XiaoNengProvider> chatProviders=ServiceLoader.load(XiaoNengProvider.class);
        for(XiaoNengProvider t:chatProviders){
            t.init(activity);
            mXiaoNengChatProivder=t;
            break;
        }
    }

    public void startChat(String groupId, String courseTitle, String goodsid){
        if(mXiaoNengChatProivder!=null){
            mXiaoNengChatProivder.startChat(groupId,courseTitle,goodsid);
         }
    }

    public void logout(){
        if(mXiaoNengChatProivder!=null){
            mXiaoNengChatProivder.logout();
        }
    }
}
