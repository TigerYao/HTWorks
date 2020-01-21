package com.huatu.chat.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.huatu.AppContextProvider;
import com.huatu.chat.XiaoNengProvider;

import cn.xiaoneng.chatcore.XNSDKCore;
import cn.xiaoneng.coreapi.ChatParamsBody;
import cn.xiaoneng.uiapi.Ntalker;
import cn.xiaoneng.uiapi.XNSDKListener;
import cn.xiaoneng.utils.CoreData;

/**
 * Created by Administrator on 2019\10\16 0016.
 */

public class ChatApiServer implements XiaoNengProvider {

    // 企业
    private final static String siteid = "kf_9846";// 示例kf_9979,kf_8002,kf_3004,zf_1000,yy_1000
    private final static String sdkkey = "DE0550A1-6364-495D-9DAB-DE382E24ECA8";// 示例FB7677EF-00AC-169D-1CAD-DEDA35F9C07B

    private ChatParamsBody chatparams = null;

  //  private static ChatApiServer instance;
    private boolean isInited = false;

    public boolean isInited(){
        return isInited;
    }
/*
    public static ChatApiServer getInstance() {
        if (instance == null) {
            synchronized (ChatApiServer.class) {
                instance = new ChatApiServer();
            }
        }
        return instance;
    }*/

    public void init(Activity activity) {
       // Toast.makeText(activity,"test",Toast.LENGTH_LONG).show();
        if( AppContextProvider.INSTANCE==null){
            AppContextProvider.INSTANCE=activity.getApplication();
        }
        if (isInited) return;
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        Ntalker.getBaseInstance().enableDebug(false);
        Ntalker.getInstance().getPermissions(activity, 200, permissions);
        int initSDK = Ntalker.getBaseInstance().initSDK(AppContextProvider.INSTANCE, siteid, sdkkey);
        if (0 == initSDK) {
            Log.e("initSDK", "初始化SDK成功");
        } else {
            Log.e("initSDK", "初始化SDK失败，错误码:" + initSDK);
        }
        try {

            SharedPreferences SpUtils= AppContextProvider.INSTANCE.getSharedPreferences("config", Context.MODE_PRIVATE);
            int userID= SpUtils.getInt("User_Id", -1);
            String userName=SpUtils.getString("uname","");
            XNSDKCore.getInstance().setReceiveUnReadMsgTime(5);
            int logIn = Ntalker.getBaseInstance().login(
                    String.valueOf(userID), userName, 0);// 登录时调
            if (0 == logIn) {
                Log.e("login", "登录成功");
                isInited = true;
            } else {
                Log.e("login", "登录失败，错误码:" + logIn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("login", "登录失败，错误码: XNSDKCore");
        }
        if (isInited)
            Ntalker.getInstance().setSDKListener(new XNSDKListener() {
                @Override
                public void onClickMatchedStr(String s, String s1) {

                }

                @Override
                public void onClickShowGoods(int i, int i1, String s, String s1, String s2, String s3, String s4, String s5) {

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onChatMsg(boolean b, String s, String s1, String s2, long l, boolean b1, int i, String s3) {

                }

                @Override
                public void onClickUrlorEmailorNumber(int i, String s) {

                }

                @Override
                public void onUnReadMsg(String s, String s1, String s2, int i) {

                }
            });
    }

    //小能SDK 开启窗口
    private void initChat() {
        chatparams = new ChatParamsBody();
        chatparams.itemparams.appgoodsinfo_type = CoreData.SHOW_GOODS_NO;
        chatparams.itemparams.clientgoodsinfo_type = CoreData.SHOW_GOODS_NO;
        chatparams.itemparams.clicktoshow_type = CoreData.CLICK_TO_APP_COMPONENT;
    }

    public void startChat(String groupId, String courseTitle, String goodsid){
        initChat();
        chatparams.startPageTitle = courseTitle;
        chatparams.itemparams.goods_id = goodsid;
        int startChat = Ntalker.getBaseInstance().startChat(AppContextProvider.INSTANCE, groupId, "面授课", chatparams);
        if (0 == startChat) {
            Log.e("startChat", "打开聊窗成功");
        } else {
            Log.e("startChat", "打开聊窗失败，错误码:" + startChat);
        }
    }

    public void logout(){
        if(!isInited)return;
        Ntalker.getBaseInstance().logout();

    }
}
