package com.huatu.handheld_huatu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.utils.VideoStatisticsUtil;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.PushMessage;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.exceptions.InvalidDataException;
import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * https://docs.jiguang.cn//jpush/client/Android/android_senior/
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JIGUANG-Example";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			LogUtils.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);

				// 将推送 ID 保存到用户表中
				SensorsDataAPI.sharedInstance().profilePushId("jgId",regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);
               // 用户收到了推送消息，使用神策分析追踪 "App 消息推送成功" 事件
				Bundle message = intent.getExtras();
				try {
					JSONObject properties = new JSONObject();
					// 获取消息标题，并保存在事件属性 msg_title 中
					//properties.put("msg_title", message.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE))
					properties.put("msg_title", message.getString(JPushInterface.EXTRA_MESSAGE));
					// 获取消息 ID，并保存在事件属性 msg_id 中
					properties.put("msg_id", message.getString(JPushInterface.EXTRA_MSG_ID));
					// 使用神策分析追踪 "App 消息推送成功" 事件
					SensorsDataAPI.sharedInstance().track("AppReceivedNotification", properties);
				} catch (JSONException e) {
					e.printStackTrace();
				}


			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

				// 用户收到了推送消息，使用神策分析追踪 "App 消息推送成功" 事件
				Bundle message = intent.getExtras();
				try {
					JSONObject properties = new JSONObject();
					// 获取消息标题，并保存在事件属性 msg_title 中
				    properties.put("msg_title", message.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
					//properties.put("msg_title", message.getString(JPushInterface.EXTRA_MESSAGE));
					// 获取消息 ID，并保存在事件属性 msg_id 中
					properties.put("msg_id", message.getString(JPushInterface.EXTRA_MSG_ID));
					// 使用神策分析追踪 "App 消息推送成功" 事件
					SensorsDataAPI.sharedInstance().track("AppReceivedNotification", properties);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");
				StudyCourseStatistic.openPushMsg(bundle.getString(JPushInterface.EXTRA_MSG_ID), bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));

			  /* //打开自定义的Activity
				Intent i = new Intent(context, MainTabActivity.class);
				i.putExtras(bundle);
				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity(i);*/

				handleOpenNotice(context, bundle);

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				LogUtils.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				LogUtils.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					LogUtils.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					LogUtils.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.get(key));
			}
		}
		return sb.toString();
	}


/*
	*/
/**
	 * 设置通知栏样式 - 定义通知栏Layout
	 *//*

	private void setStyleCustom() {
		CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(PushSetActivity.this, R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text);
		builder.layoutIconDrawable = R.drawable.ic_launcher;
		builder.developerArg0 = "developerArg2";
		JPushInterface.setPushNotificationBuilder(2, builder);
		Toast.makeText(PushSetActivity.this, "Custom Builder - 2", Toast.LENGTH_SHORT).show();
	}
*/


    private void handleOpenNotice(Context context, Bundle bundle){

		String customMsg=bundle.getString(JPushInterface.EXTRA_MESSAGE);
		boolean actionDone=false;
		try{
			UMessage var3   = new UMessage(new JSONObject(customMsg));
			PushMessage pushMessage = GsonUtil.GsonToBean(var3.custom, PushMessage.class);
			if(pushMessage!=null){
				actionDone=true;
				Intent action=  UmengMessageHandlerImpl.getActionByMessage(context,pushMessage,true);
				context.startActivity(action);
			}
		}catch (Exception e){
			actionDone=false;
		}
 	    if(!actionDone){
			Intent action2=new Intent(context,com.huatu.handheld_huatu.business.guide.SplashActivity.class);
			action2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(action2);
		}
	}

	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {

		String customMsg=bundle.getString(JPushInterface.EXTRA_MESSAGE);
		LogUtils.e("dealWithCustomMessage", "dealWithCustomMessage: " +String.valueOf(customMsg) );
 		PushMessage pushMessage = GsonUtil.GsonToBean(customMsg, PushMessage.class);
		if(null!=pushMessage){
 			UmengMessageHandlerImpl.showNotification(context, pushMessage,true);
		}

		/*if (MainActivity.isForeground) {
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (!ExampleUtil.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (extraJson.length() > 0) {
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
					}
				} catch (JSONException e) {

				}

			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}*/
	}
}
