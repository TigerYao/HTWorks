package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Intent;

import com.baijiayun.download.DownloadModel;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLVodListener;
import com.huatu.handheld_huatu.helper.SimpleSubscriber;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xing on 2018/4/10.
 * todo添加课程的Id,减少事件的回调
 *
 */

public class DownLoadDbHelper {
    private float lastProgress;
    private String curDownloadingId;
    private List<WeakReference<OnDLVodListener>> mDLVodListenerList =new ArrayList<>();

    public void setLastProgress(int lastprogress){
        lastProgress=lastprogress;
    }

    public String getCurrentDownLoadingId(){
        return  curDownloadingId;
    }

    public void setCurDownloadingId(String downloadingId){
        curDownloadingId=downloadingId;
    }

    public void addDownloadListener(OnDLVodListener mDLVodListener) {
        if (mDLVodListenerList == null) {
            mDLVodListenerList = new ArrayList<>();
        }
        mDLVodListenerList.add(new WeakReference<>(mDLVodListener));
    }

    public void removeDownloadListener(OnDLVodListener mDLVodListener) {
       if (mDLVodListenerList != null) {
            for(WeakReference<OnDLVodListener> weakReference : mDLVodListenerList) {
                if(weakReference.get() == mDLVodListener) {
                    mDLVodListenerList.remove(weakReference);
                    break;
                }
            }
       }
    }

    private long mlastProgressTime=0;
    private boolean mIsFirstStart=false;

    public void onDbDownloadStarted(String s) {
        LogUtils.i("onDownloadStarted: " + s);

    /*    long diffProgressTime=(System.currentTimeMillis()-mlastProgressTime);
        if(diffProgressTime<500&&(s.equals(curDownloadingId))){
            mIsFirstStart=false;
            return;
        }*/

        mlastProgressTime=System.currentTimeMillis();
        curDownloadingId = s;
        mIsFirstStart=true;
        SQLiteHelper.getInstance().upDateDLStatus(s, DownLoadStatusEnum.START.getValue());//1
    }

    public void onDbDownloadPrepareAndStart(DownLoadLesson lesson){
        onDbDownloadPrepareAndStart(lesson,true);

    }


    public void onDbDownloadPrepareAndStart(DownLoadLesson lesson,boolean canCallback) {
        LogUtils.i("onDownloadPrepare: " + lesson.getDownloadID());
        curDownloadingId = lesson.getDownloadID();
        lastProgress = 0;
        SQLiteHelper.getInstance().upDateDLStatus(lesson.getDownloadID(), DownLoadStatusEnum.PREPARE.getValue() );//-1

        if(!canCallback) return;
        for (WeakReference<OnDLVodListener> mDLVodListener : mDLVodListenerList) {
            OnDLVodListener vodListener=mDLVodListener.get();
            if(vodListener!=null)
                vodListener.onDLPrepare(lesson.getDownloadID());
        }

        // download(lesson);
    }

    public void onDownloadFinish(String key, String path) {
        LogUtils.i("onDownloadFinish: " + key);
        curDownloadingId = "";
        SQLiteHelper.getInstance().upDateDLStatus(key, DownLoadStatusEnum.FINISHED.getValue(), path);//2
        for (WeakReference<OnDLVodListener> mDLVodListener : mDLVodListenerList) {
            OnDLVodListener vodListener=mDLVodListener.get();
            if(vodListener!=null)  vodListener.onDLFinished(key);
        }
        Intent autoDownIntent=new Intent("com.huatu.start_download_course");
        autoDownIntent.putExtra(ArgConstant.IS_LIVE,true);
        UniApplicationContext.getContext().sendBroadcast(autoDownIntent);
    }


    public void onDbDownloadWaiting(String key){
        curDownloadingId = "";
        SQLiteHelper.getInstance().upDateDLStatus(key, DownLoadStatusEnum.PREPARE.getValue());        //4

        for (WeakReference<OnDLVodListener> mDLVodListener : mDLVodListenerList) {
            OnDLVodListener vodListener=mDLVodListener.get();
            if(vodListener!=null)  vodListener.onDLStop(key,true);
        }
    }

    public void onDbDownloadStop(String key){
        onDbDownloadStop(key,true);
    }

    public void onDbDownloadStop(String key,boolean canCallback) {
        LogUtils.i("onDownloadStop: " + key);

      /*  long diffProgressTime=(System.currentTimeMillis()-mlastProgressTime);
        if(diffProgressTime<500&&(key.equals(curDownloadingId))) return;*/

        curDownloadingId = "";
        SQLiteHelper.getInstance().upDateDLStatus(key, DownLoadStatusEnum.STOP.getValue());        //4

        if(!canCallback) return;
        for (WeakReference<OnDLVodListener> mDLVodListener : mDLVodListenerList) {
            OnDLVodListener vodListener=mDLVodListener.get();
            if(vodListener!=null)  vodListener.onDLStop(key,false);
        }
    }

    public void onDownloadError(String key, int errorCode) {
        LogUtils.i("onDownloadError: " + key + ", errorCode:" + errorCode);
        curDownloadingId = "";
        DownLoadLesson lesson = SQLiteHelper.getInstance().getCourseWare(key);
        if(lesson != null && lesson.getDownStatus() == DownLoadStatusEnum.FINISHED.getValue()) {  //2
            LogUtils.i(key + " has been finished");
        } else {
            SQLiteHelper.getInstance().upDateDLStatus(key, DownLoadStatusEnum.ERROR.getValue());  //3
            for (WeakReference<OnDLVodListener> mDLVodListener : mDLVodListenerList) {
                OnDLVodListener vodListener=mDLVodListener.get();
                if(vodListener!=null)  vodListener.onDLError(key, errorCode);
            }
        }
    }

    public void onDownloadStorage(String key, long space, int duration) {
        LogUtils.i("onDownloadStorage: " + key + ", space:" + space);
        SQLiteHelper.getInstance().upDateDLSpaceAndDuration(key, space, duration);
        for (WeakReference<OnDLVodListener> mDLVodListener : mDLVodListenerList) {
            OnDLVodListener vodListener=mDLVodListener.get();
            if(vodListener!=null)  vodListener.onDLFileStorage(key, space);
        }
    }

    public void updateDLProgress(String key, int progress,long speed) {
        curDownloadingId = key;

        long diffProgressTime=(System.currentTimeMillis()-mlastProgressTime)/1000;

        if(mIsFirstStart ||(diffProgressTime>8)|| progress - lastProgress >= 5) {
            mIsFirstStart=false;
            SQLiteHelper.getInstance().upDateDLProgress(key, (int)progress);
            LogUtils.i("onProgress: " + key + ", progress:" + progress+","+diffProgressTime+","+speed);
            lastProgress = progress;
            mlastProgressTime=System.currentTimeMillis();

            for (WeakReference<OnDLVodListener> mDLVodListener : mDLVodListenerList) {
                OnDLVodListener vodListener=mDLVodListener.get();
                if(vodListener!=null)  vodListener.onDLProgress(key, progress,speed);
            }
        }
    }

    public void updateDownLoadToken(String key, String videoToken) {
             SQLiteHelper.getInstance().upDateLessionToken(key, videoToken);
            LogUtils.i("onProgress: " + key + ", progress:" + videoToken);

    }

    int mRefreshTimes=0;

    public void refreshToken(final DownloadModel curloadModel,boolean isPlayback,final Action1<DownLoadLesson> callback){
        mRefreshTimes++;
        if(mRefreshTimes>10) return;//一防万一进入死循环

        CourseApiService.refreshDownloadToken(curloadModel.roomId,curloadModel.sessionId,isPlayback,curloadModel.videoId)
                .subscribeOn(Schedulers.io()).subscribe(new SimpleSubscriber<CourseApiService.DownToken>(){
            @Override
            public void onError(Throwable e) {
                ToastUtils.showShort("刷新token失败");
            }
            @Override
            public void onSuccess(CourseApiService.DownToken response) {
                DownLoadLesson curLession=  SQLiteHelper.getInstance().getCourseWare(curloadModel.extraInfo);
                if(curLession!=null){
                    updateDownLoadToken(curloadModel.extraInfo,response.token);
                    curLession.setVideoToken(response.token);
                    if(null!=callback)  callback.call(curLession);
                }
                else LogUtils.e("refreshToken",curloadModel.extraInfo+"");
            }
        });
    }

}
