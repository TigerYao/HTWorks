package com.huatu.handheld_huatu.business.ztk_zhibo.play.utils;

import android.text.TextUtils;

import com.baijiahulian.player.BJPlayerView;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.VideoPlayStaticInfo;
import com.huatu.handheld_huatu.helper.statistic.SensorStatisticHelper;
import com.huatu.handheld_huatu.mvpmodel.Sensor.CourseInfoForStatistic;
import com.huatu.handheld_huatu.mvpmodel.me.DeleteResponseBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

public class VideoStatisticsUtil {
    public static final int PLAY_STOP_EVENT_TYPE = 1;
    public static final int PLAY_SEEK_FORWARD_EVENT_TYPE = 2;
    public static final int PLAY_SEEK_BACK_EVENT_TYPE = 3;
    public static final int PLAY_SPEED_EVENT_TYPE = 4;
    public static final int PLAY_FINISH_EVENT_TYPE = 5;
    public static final int PLAY_CLARITY_EVENT_TYPE = 6;
    public static final int PLAY_JUDGE_EVENT_TYPE = 7;
    public static final int PLAY_SHARE_EVENT_TYPE = 8;
    public static final int PLAY_DOWNLOAD_EVENT_TYPE = 9;
    public static final int PLAY_ERROR_EVENT_TYPE = 11;

    public static final String VIDEO_VIEW_FULL_MODE = "全屏";
    public static final String VIDEO_VIEW_PORT_MODE = "竖屏";
    public static final String VIDEO_VIEW_TEACHER_MODE = "助教笔记模式";
    public static final String VIDEO_VIEW_THREE_MODE = "三分屏";
    public static final String VIDEO_VIEW_FOUR_MODE = "四分屏";
    private String mVideoMode = VIDEO_VIEW_PORT_MODE;

    private CourseWareInfo courseWareInfo;
    private String courseId;
    //    public VideoPlayStaticInfo mVideoPlayStopEventInfo;
    public VideoPlayStaticInfo mVideoPlaySeekEventInfo;
    private VideoPlayStaticInfo mVideoPlayOtherEventInfo;
    private Map<String, Object> params;
    private long mStartTimeTag;
    private int mVideoTime;

    public void reBindData(CourseWareInfo courseWareInfo){
        this.courseWareInfo = courseWareInfo;
        bindData();
    }

    public VideoStatisticsUtil(CourseWareInfo courseWareInfo, String courseId) {
        this.courseWareInfo = courseWareInfo;
        this.courseId = courseId;
        bindData();
    }

    private void bindData(){
        Map<String, Object> objectMap = new HashMap<>();
        if (courseWareInfo.videoType == 2 && !TextUtils.isEmpty(courseWareInfo.liveStartTime)) {
            int startTime = (int) ((System.currentTimeMillis() / 1000) - Long.parseLong(courseWareInfo.liveStartTime));
            mVideoTime = TextUtils.isEmpty(courseWareInfo.liveEndTime) ? 0 : Integer.parseInt(courseWareInfo.liveEndTime) - Integer.parseInt(courseWareInfo.liveStartTime);
            objectMap.put("zxvideo_time", mVideoTime);
            objectMap.put("zxstart_time", TimeUtils.getSecond22MinTime(startTime));
            mStartTimeTag = System.currentTimeMillis();
        }
        getCourseInfo("HuaTuOnline_app_pc_HuaTuOnline_ReStartWatchCourseVideo", objectMap, courseWareInfo.videoType != 2);
    }

    public void startCollectPlayEvent(int startTime, int videoTime) {
        if (videoTime == 0)
            return;
        if (mVideoTime != videoTime)
            this.mVideoTime = videoTime;
        if (params != null && !params.containsKey("zxvideo_time"))
            params.put("zxvideo_time", videoTime);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("zxstart_time", TimeUtils.getSecond22MinTime(startTime));
        sendTrackInfo("HuaTuOnline_app_pc_HuaTuOnline_ReStartWatchCourseVideo", objectMap);
        mStartTimeTag = System.currentTimeMillis();
    }

    public void onSeekStart(int startTime) {
        if(mVideoPlaySeekEventInfo == null) {
            mVideoPlaySeekEventInfo = new VideoPlayStaticInfo(PLAY_SEEK_BACK_EVENT_TYPE, courseWareInfo.videoType, courseId, courseWareInfo.coursewareId);
            mVideoPlaySeekEventInfo.startTime = startTime;
        }
    }

    public void onSeekEnd(int progressTime) {
        if (mVideoPlaySeekEventInfo == null)
            return;
        boolean isForward = (progressTime - mVideoPlaySeekEventInfo.startTime > 0);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("zxvideo_start_time", TimeUtils.getSecond22MinTime((int)mVideoPlaySeekEventInfo.startTime));
        objectMap.put("zxvideo_end_time", TimeUtils.getSecond22MinTime(progressTime));
        objectMap.put("video_speed_time", Math.abs(mVideoPlaySeekEventInfo.startTime - progressTime));
        sendTrackInfo(isForward ? "HuaTuOnline_app_pc_HuaTuOnline_ReEndSpeedWatch" : "HuaTuOnline_app_pc_HuaTuOnline_ReEndSlowWatch", objectMap);
        mVideoPlaySeekEventInfo = null;
    }

    public void clickBack(int progressTime) {
        onVideoFinish(progressTime);
    }

    public void onVideoStop(int progressTime) {
        if (mStartTimeTag == 0)
            return;
        onVideoOperate(progressTime, "暂停");
        onVideoFinish(progressTime);
    }

    public void onVideoFinish(int progressTime) {
        Map<String, Object> objectMap = new HashMap<>();
        long realTime = mStartTimeTag == 0 ? 0 : System.currentTimeMillis() - mStartTimeTag;
        if (progressTime == 0 && !TextUtils.isEmpty(courseWareInfo.liveStartTime))
            progressTime = (int)((System.currentTimeMillis()/1000) - Long.parseLong(courseWareInfo.liveStartTime));
        objectMap.put("real_watch_time", (realTime / 1000));
        objectMap.put("zxwatch_time", TimeUtils.getSecond22MinTime(progressTime));
        sendTrackInfo("HuaTuOnline_app_pc_HuaTuOnline_ReFinishWatchCourseVideo", objectMap);
        mStartTimeTag = 0;
    }

    public void updateViewMode(boolean isFull, boolean isExpandVideo, boolean isExpandChat) {
        if (isFull) {
            if (!isExpandChat && !isExpandVideo)
                mVideoMode = VIDEO_VIEW_FULL_MODE;
            else if (isExpandChat && !isExpandVideo)
                mVideoMode = VIDEO_VIEW_TEACHER_MODE;
            else if (isExpandVideo && !isExpandChat)
                mVideoMode = VIDEO_VIEW_THREE_MODE;
            else
                mVideoMode = VIDEO_VIEW_FOUR_MODE;
        } else
            mVideoMode = VIDEO_VIEW_PORT_MODE;
    }

    public void onSpeedChanged(int progressTime, String speed) {
        onVideoOperate(progressTime, speed);
    }

    public void onJudege(int time, int level) {
        otherEvent(PLAY_JUDGE_EVENT_TYPE, time);
        mVideoPlayOtherEventInfo.level = level;
        reportEvent(mVideoPlayOtherEventInfo);
        mVideoPlayOtherEventInfo = null;
    }

    public void onClariyChanged(int progressTime, int defiValue) {
        String clariy = "省流";
        switch (defiValue) {
            case BJPlayerView.VIDEO_DEFINITION_1080p:
                clariy = "超高清";
                break;
            case BJPlayerView.VIDEO_DEFINITION_720p:
                clariy = "超清";
                break;
            case BJPlayerView.VIDEO_DEFINITION_SUPER:
                clariy = "高清";
                break;
            case BJPlayerView.VIDEO_DEFINITION_HIGH:
                clariy = "标清";
                break;
            case BJPlayerView.VIDEO_DEFINITION_STD:
            default:
                clariy = "省流";
                break;
        }
        onVideoOperate(progressTime, clariy);
    }

    public void onVideoOperate(int progressTime, String clariy) {
        Map<String, Object> objParams = new HashMap<>();
        objParams.put("video_operation", clariy);
        objParams.put("zxoperation_time", TimeUtils.getSecond22MinTime(progressTime));
        objParams.put("video_model", mVideoMode);
        sendTrackInfo("HuaTuOnline_app_pc_HuaTuOnline_ReWatchVideoOperation", objParams);

//        otherEvent(PLAY_CLARITY_EVENT_TYPE, progressTime);
//        mVideoPlayOtherEventInfo.clarity = clariy;
//        reportEvent(mVideoPlayOtherEventInfo);
//        mVideoPlayOtherEventInfo = null;
    }

    public void onErrorEvent(int progressTime, int errorCode, String errorMsg) {
        otherEvent(PLAY_ERROR_EVENT_TYPE, progressTime);
        mVideoPlayOtherEventInfo.errorId = errorCode;
        mVideoPlayOtherEventInfo.describe = errorMsg;
        reportEvent(mVideoPlayOtherEventInfo);
        mVideoPlayOtherEventInfo = null;
    }

    public static void reportDownloadEvent(String courseId, int syllabusId) {
        VideoPlayStaticInfo info = new VideoPlayStaticInfo(PLAY_DOWNLOAD_EVENT_TYPE, -1, courseId, syllabusId);
        reportEvent(info);
    }

    public static void reportShareEvent(String courseId, int syllabusId) {
        VideoPlayStaticInfo info = new VideoPlayStaticInfo(PLAY_SHARE_EVENT_TYPE, -1, courseId, syllabusId);
        reportEvent(info);
    }

    private void otherEvent(int type, long progressTime) {
        mVideoPlayOtherEventInfo = new VideoPlayStaticInfo(type, courseWareInfo.videoType, courseId, courseWareInfo.coursewareId);
        mVideoPlayOtherEventInfo.endTime = progressTime;
    }

    public static void reportEvent(VideoPlayStaticInfo videoPlayStaticInfo) {
        if (videoPlayStaticInfo != null) {
            Subscriber<DeleteResponseBean> subscriber = new Subscriber<DeleteResponseBean>() {
                @Override
                public void onCompleted() {
                    LogUtils.d("reportEvent", "onCompleted");
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    LogUtils.d("reportEvent", "onError");
                }

                @Override
                public void onNext(DeleteResponseBean deleteResponseBean) {
                    LogUtils.d("reportEvent", "onNext----" + deleteResponseBean.toString());
                }
            };
            ServiceProvider.reportPlayEvent(videoPlayStaticInfo.getEventStatiscString(), subscriber);
            videoPlayStaticInfo = null;
        }
    }

    public void getCourseInfo(final String type, final Map<String, Object> objParams, final boolean justGet) {
        ServiceProvider.getSensorsStatistic(courseWareInfo.classId + "", new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                CourseInfoForStatistic courseInfoForStatistic = (CourseInfoForStatistic) model.data;
                if (params == null || params.size() == 0) {
                    params = new HashMap<>();
                    params.put("course_id", courseInfoForStatistic.course_id);
                    params.put("class_name", courseWareInfo.title);
                    params.put("class_id", courseWareInfo.coursewareId+"");
//                    params.put("is_collection", courseInfoForStatistic.is_collection);
                    params.put("is_free", courseInfoForStatistic.is_free);
                    if (!TextUtils.isEmpty(courseInfoForStatistic.course_title))
                        params.put("course_title", courseInfoForStatistic.course_title);
                    params.put("course_price", courseInfoForStatistic.course_price);
                    if (!TextUtils.isEmpty(courseInfoForStatistic.course_kind))
                        params.put("course_kind", courseInfoForStatistic.course_kind);
//                    params.put("course_collage_price", courseInfoForStatistic.course_collage_price);
                    params.put("discount_price", courseInfoForStatistic.discount_price);
                    if (courseWareInfo.teacherIds != null)
                        params.put("teacher_id", courseWareInfo.teacherIds);
                    if (courseWareInfo.teacherNames != null)
                        params.put("teacher_name", courseWareInfo.teacherNames);
                    params.put("zxclass_type", courseWareInfo.videoType == 1 ? "录播" : courseWareInfo.videoType == 2 ? "直播" : "回放");
                    if(!TextUtils.isEmpty(courseInfoForStatistic.course_type))
                        params.put("course_type", courseInfoForStatistic.course_type);
                    if (mVideoTime > 0)
                        params.put("zxvideo_time", mVideoTime);
                    if (!justGet) {
                        objParams.putAll(params);
                        SensorStatisticHelper.sendTrack(type, objParams);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void sendTrackInfo(String type, final Map<String, Object> objParams) {
        if (params != null) {
            objParams.putAll(params);
            SensorStatisticHelper.sendTrack(type, objParams);
        } else {
            getCourseInfo(type, objParams, false);
        }
    }

}
