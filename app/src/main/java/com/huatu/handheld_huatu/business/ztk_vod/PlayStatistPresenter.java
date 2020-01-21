package com.huatu.handheld_huatu.business.ztk_vod;

import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;

/**
 * Created by Administrator on 2019\9\3 0003.
 */

public class PlayStatistPresenter {

    public interface View{
       CourseWareInfo getPlayingCourseWare();
    }

    //当前播放位置
    private int mCurrentPlayTime;

    public int getCurPlayTime(){
        return mCurrentPlayTime;
    }

    //总播放时长
    private int mTotalPlayTime;

    private long mPlayOnlineTime = 0;//播放在线时长

    //退出时保存的播放进度
    private int  mLastSaveTime;

    private View mPlayContactView;

    public PlayStatistPresenter reset(){
        mPlayOnlineTime= mCurrentPlayTime=mTotalPlayTime=mLastSaveTime=0;
        return this;
    }


    public void setLastPlayTime(int lastPlayTime){

        mLastSaveTime=lastPlayTime;
    }

    public PlayStatistPresenter(View contactView){
        mPlayContactView=contactView;
    }

    public void setDuration(int duration){
        if(mTotalPlayTime==0){
            mPlayOnlineTime = System.currentTimeMillis();
        }
        mTotalPlayTime = duration;
    }

    public void setCurrentPosition(int position){
        mCurrentPlayTime = position;
        if(mTotalPlayTime>mCurrentPlayTime){
            savePlayProgress(false,false);
        }else if((mTotalPlayTime==mCurrentPlayTime)&&(mTotalPlayTime>0)){
            savePlayProgress(true,true);
            mTotalPlayTime=0;
        }
    }

    public void onPlayCompleted(){
        savePlayProgress(true,true);
        mCurrentPlayTime = 0;
    }

    public void saveCurrentPlayProgress(){
        savePlayProgress(true,false);
    }


    public void detactView(){
        mPlayContactView=null;
    }
    //int mCheckTimes=0;
    private void savePlayProgress(boolean forceSave,boolean isFinish) {
        // LogUtils.e("savePlayProgress","1");
        //五分钟定时上报 及 播放结束时上报
        boolean isTimeLowerDiff=Math.abs(mCurrentPlayTime - mLastSaveTime) < 300;//5 minutes
        LogUtils.e("savePlayProgress","2");
        if (isTimeLowerDiff&&(!forceSave)) {
            return;
        }
        if(null!=mPlayContactView){

            CourseWareInfo mPlayingCourseWare=mPlayContactView.getPlayingCourseWare();
            if (mPlayingCourseWare!=null) {
                if (isFinish||(mCurrentPlayTime > 0)) {

                    if(mTotalPlayTime==0)   return;
                    mLastSaveTime = mCurrentPlayTime;
                    LogUtils.i("currentTime  save: " + mCurrentPlayTime + "  , id=" + mTotalPlayTime);
                    //sharedPreferences.edit().putInt(course.rid, (isFinish?totalTime:currentTime)).commit();
                    PrefStore.userReadPreference().edit().putInt(String.valueOf(mPlayingCourseWare.coursewareId), (isFinish?mTotalPlayTime:mCurrentPlayTime)).commit();
                    if(mPlayingCourseWare.downStatus== DownBtnLayout.FINISH)
                        SQLiteHelper.getInstance().upDatePlayProgress(mPlayingCourseWare.getDownloadId(), (isFinish?mTotalPlayTime:mCurrentPlayTime),mTotalPlayTime);

                    long diffOnlineTime=(System.currentTimeMillis()-mPlayOnlineTime)/1000;

                    String videoIdWithoutTeacher=mPlayingCourseWare.hasTeacher==1?"":mPlayingCourseWare.videoId;//.bjyVideoId;
                    String videoIdWithTeacher=mPlayingCourseWare.hasTeacher==1? mPlayingCourseWare.videoId:"";
                    ServiceExProvider.visit(CourseApiService.saveRecordCourseProgress(mPlayingCourseWare.joinCode,
                            mPlayingCourseWare.bjyRoomId,
                            mPlayingCourseWare.bjySessionId,
                            mPlayingCourseWare.id,
                            isFinish?mTotalPlayTime:mCurrentPlayTime,
                            diffOnlineTime,
                            videoIdWithoutTeacher,
                            videoIdWithTeacher,
                            mTotalPlayTime));
                }
            }
        }

    }
}
