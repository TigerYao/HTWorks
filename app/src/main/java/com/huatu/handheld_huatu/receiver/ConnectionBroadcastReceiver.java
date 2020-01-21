package com.huatu.handheld_huatu.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.business.guide.GuideActivity;
import com.huatu.handheld_huatu.business.guide.SplashActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadAssist;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.event.ConnectionEvent;
import com.huatu.handheld_huatu.helper.BaijDownloadService;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.ArrayUtils;
import com.tapadoo.alerter.Alerter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DongDong on 2016/3/30.
 */
public class ConnectionBroadcastReceiver extends BroadcastReceiver {

  	@Override
	public void onReceive(Context context, Intent intent) {
		boolean checkPause=false;
		boolean autoDownNext=false;
		if(intent!=null){
			checkPause   = intent.getBooleanExtra(ArgConstant.TYPE,false);
			autoDownNext = intent.getBooleanExtra(ArgConstant.IS_LIVE,false);
		}
  		//没有登录过就返回
 	    if(UserInfoUtil.userId<=0) return;
		boolean isCanDownIn3G= PrefStore.canDownloadIn3G();
		boolean isConnected = NetUtil.isConnected();
		boolean isWifi = NetUtil.isWifi();
		LogUtils.ex("DownLoadAssist","Connection changed, isConnected="+isConnected
				+ ", isWifi=" + isWifi + ", isUseMobileNet=" + isCanDownIn3G);
  		if(isConnected){
	 		if(isWifi)
	 				onStartDownLoad();// 下载等相关事宜。
			else{
  	           if(autoDownNext)
				   onStartDownLoad();
	           else{
	           	   if(!isCanDownIn3G){
					   UniApplicationLike.getApplicationHandler().removeCallbacks(mDelayRun);
					   UniApplicationLike.getApplicationHandler().postDelayed(mDelayRun,600);
				   }
 	           	   else
					   onStartDownLoad();
			   }
 		   }

		}else {
			// 下载暂停
			if(!checkPause) 	onPauseDownLoad();
 		}
 	}

 	private void showConfrimDialog(){

  		boolean hasNextDown=false;
		String currentDownloadId=PrefStore.getSettingString(DownLoadAssist.RECOVERY_TAG, "");
		hasNextDown=TextUtils.isEmpty(currentDownloadId)?false:true;
		if(!hasNextDown){
			List<DownLoadLesson> lessons = SQLiteHelper.getInstance().getUnFinishedCourseWare();
			if(!ArrayUtils.isEmpty(lessons)){
				for(DownLoadLesson lesson:lessons){
					 if(lesson.getDownStatus()==DownLoadStatusEnum.PREPARE.getValue()||
					    lesson.getDownStatus()==DownLoadStatusEnum.START.getValue()||
							 lesson.getDownStatus()==DownLoadStatusEnum.INIT.getValue() ){
						 hasNextDown=true;
					 	break;
					 }
 				}
			}
 		}

        if(!hasNextDown) return;
		Activity topActivity = ActivityStack.getInstance().getTopActivity();
		if (topActivity != null && !Method.isActivityFinished(topActivity)&&topActivity.hasWindowFocus()) {
 			if((topActivity instanceof SplashActivity)||(topActivity instanceof GuideActivity)) return;
 			try {

			    if(Alerter.isShowing())	 return;
				Alerter.create(topActivity)
						.setText("当前无wifi，下载会消耗流量，是否继续？")
						.enableInfiniteDuration(true)
						.disableOutsideTouch()
						.addButton("取消", R.style.AlertConcelButton,null)
						.addButton("继续", R.style.AlertOkButton, new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								onStartDownLoad();
							}
						})
						.show();
			}
			catch (Exception e){ 	}
 		}

	}

 	private Runnable mDelayRun=new Runnable() {
		@Override
		public void run() {
			showConfrimDialog();
		}
	};

	private void onPauseDownLoad() {
	/*	if(Method.isListEmpty(lessons)) {
			return;
        }
		for (int i = 0; i < lessons.size(); i++) {
            DownLoadAssist.getInstance().stop(lessons.get(i));
		}*/
		/*Activity topActivity= ActivityStack.getInstance().getTopActivity();
		//UniApplicationLike.getApplicationHandler().removeCallbacks(mDelayRun);


		LogUtils.e("onPauseDownLoad",topActivity.hasWindowFocus()+"test");//&& topActivity.hasWindowFocus()
		if (topActivity != null && !Method.isActivityFinished(topActivity) ){

			Alerter.create(topActivity)
					//.setTitle("Alert Title")
					.setText("无网络连接，下载暂停")
					.setBackgroundColorRes(R.color.white)
					.show();
		}*/


 		DownLoadAssist.getInstance().stopAll(true);
  		String currentDownloadId=PrefStore.getSettingString(DownLoadAssist.RECOVERY_TAG, "");
 		if(!TextUtils.isEmpty(currentDownloadId)){
 			try{
				Activity topActivity= ActivityStack.getInstance().getTopActivity();
				if (topActivity != null && !Method.isActivityFinished(topActivity) &&topActivity.hasWindowFocus()){
					if((topActivity instanceof SplashActivity)||(topActivity instanceof GuideActivity)) return;
					if(Alerter.isShowing())	 return;
					Alerter.create(topActivity)
							//.setTitle("Alert Title")
							.setText("无网络连接，下载暂停")
							.setBackgroundColorRes(R.color.white)
							.show();
				}
			}catch (Exception e){ }
  		}
	}


	/*INIT(-2),      //
	START(1),     //开始  loading
	PREPARE(-1),  //准备
	FINISHED(2),  //完成
	STOP(4),      //停止
	ERROR(3) ;    //出错*/
     //https://report.tingyun.com/mobile/mobileApp/28164/anrDetail?endTime=2018-6-4+14%3A2&timePeriod=1440&mobileAnrId=112717091&mobileAppVersionId=345988&anrCount=471
	private void onStartDownLoad() {

		Observable.create(new Observable.OnSubscribe<DownLoadLesson>() {

			private DownLoadLesson longRunningOperation(){

				List<DownLoadLesson> lessons = SQLiteHelper.getInstance().getUnFinishedCourseWare();
				if(Method.isListEmpty(lessons)) {
					return null;
				}
				DownLoadLesson downId = null;
 			    boolean hasRecoveryed= DownLoadAssist.getInstance().recoveryNetDownload();
				if(hasRecoveryed) return null;

				// int startIndex=-1,prepareIndex=-1,stopIndex=-1;
				if(downId == null) {
					for (int i = 0; i < lessons.size(); i++) {
						if (lessons.get(i).getDownStatus() ==DownLoadStatusEnum.PREPARE.getValue()) {// -1
							downId = lessons.get(i);
							break;
						}
					}
				}
				for (int i = 0; i < lessons.size(); i++) {
					if (lessons.get(i).getDownStatus() ==DownLoadStatusEnum.START.getValue() ) { //1
						downId = lessons.get(i);
						break;
					}
				}
                /*  if(downId == null) {
                    for (int i = 0; i < lessons.size(); i++) {
                     if (lessons.get(i).getDownStatus() ==DownLoadStatusEnum.STOP.getValue() ) {//4
                        downId = lessons.get(i);
                         break;
                      }
                    }
                }*/
				if(downId == null) {
					for (int i = 0; i < lessons.size(); i++) {
						if (lessons.get(i).getDownStatus() ==DownLoadStatusEnum.INIT.getValue() ) {//-2
							downId = lessons.get(i);
							break;
						}
					}
				}

				return downId;
			}

			@Override
			public void call(Subscriber<? super DownLoadLesson> subscriber) {
				subscriber.onNext(longRunningOperation());
				subscriber.onCompleted();
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe( new Subscriber<DownLoadLesson>() {
			@Override
			public void onCompleted() {
				//mRxButton.setEnabled(true);
			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(DownLoadLesson downId) {
				//Snackbar.make(mRootView, s, Snackbar.LENGTH_LONG).show();

				LogUtils.e("onStartDownLoad",downId==null? "false":"true"+","+downId.getDownloadID());
				if (downId != null && !TextUtils.isEmpty(downId.getDownloadID())) {
					DownLoadAssist.getInstance().download(downId);
				}else {
					BaijDownloadService.cancelNotification();
				}

			}
		});
 	}
}
