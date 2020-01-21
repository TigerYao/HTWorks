package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.os.Environment;
import android.text.TextUtils;

import com.baijiahulian.downloader.download.DownloadInfo;
import com.baijiahulian.downloader.download.VideoDownloadManager;
import com.baijiahulian.downloader.download.VideoDownloadService;
import com.baijiahulian.player.BJPlayerView;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.download.IRecoveryCallback;
import com.baijiayun.download.RecoverDbHelper;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_vod.baijiayun.bean.BjyCourseBean;


import com.huatu.handheld_huatu.business.ztk_vod.utils.MediaUtil;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * http://dev.baijiayun.com/default/wiki/detail/8#h11-14
 * https://github.com/baijia/BJVideoPlayerDemo-Android/blob/master/BJVideoPlayerDemo/app/src/main/java/com/baijiahulian/download/SimpleVideoDownloadActivity.java
 * Created by cjx on 2018\6\22 0022.
 */

public class OldDownRecoveryAssist {

    public interface onCacheRecoveryListener{
         void onCacheRecoveryFinish();
    }
    onCacheRecoveryListener monCacheRecoveryListener;
    public void setOnCacheRecoveryListener(onCacheRecoveryListener recoveryListener){
        monCacheRecoveryListener=recoveryListener;
    }

    VideoDownloadManager mDownloadManager;

    private static String getDownloadPath(){
        String rootPath;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            rootPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath()
                    + File.separator
                    + "Android"
                    + File.separator
                    + "data"
                    + File.separator
                    + "com.huatu.handheld_huatu" + File.separator + "offline";
        } else {
            rootPath = UniApplicationContext.getContext().getFilesDir().getAbsolutePath();
        }
   /*     File fileDL = new File(rootPath, "fileDL");
        if (!fileDL.exists()) {
            fileDL.mkdirs();
        }*/
        return rootPath;
    }

    private void convertOldDataModel(){
        mDownloadManager = VideoDownloadService.getDownloadManager(UniApplicationContext.getContext());
        mDownloadManager.initDownloadPartner(33243432L, BJPlayerView.PLAYER_DEPLOY_ONLINE, 0);
        //DownloadManager downloadManager = DownloadService.getDownloadManager(getApplication().getBaseContext());
        //设置下载目标路径
        String rootPath=getDownloadPath();
        mDownloadManager.setTargetFolder(rootPath + File.separator + "bj_video_downloaded" + File.separator);
        //downloadManager.loadDownloadInfo(33243432);
        //设置最大下载并发数，默认3个
        //downloadManager.getThreadPool().setCorePoolSize(1);

        List<DownloadInfo> bjyDownloadInfos = mDownloadManager.getAllTask();

        HashMap<String,DownLoadCourse> curDownLoadMap=new HashMap<>();

        for(int i=0;i<bjyDownloadInfos.size();i++){
            DownloadInfo videoInfo= bjyDownloadInfos.get(i);

            // if(videoInfo.getState()!= DownloadManager.FINISH) continue;

       /*     token = mVodcoursePlayList.get(i).getToken();
            BjyCourseBean bjyCourseBean = new BjyCourseBean();
            bjyCourseBean.setClassid(classid);
            bjyCourseBean.setClassTitle(course.title);
            bjyCourseBean.setClassScaleimg(course.scaleimg);
            bjyCourseBean.setSelect(select);
            bjyCourseBean.setTeacher(teacher);
            bjyCourseBean.setRid(mVodcoursePlayList.get(i).getRid());
              //额外信息
            String extraInfo = GsonUtil.GsonString(bjyCourseBean);*/


            if(!TextUtils.isEmpty(videoInfo.getExtraInfo())){
                BjyCourseBean bjyCourseBean = GsonUtil.GsonToBean(videoInfo.getExtraInfo(), BjyCourseBean.class);
                String userCourseID=UserInfoUtil.userId+"_"+bjyCourseBean.classid;//因为是主键，添加用户区分
                if(bjyCourseBean!=null){
                    if(!curDownLoadMap.containsKey(bjyCourseBean.classid)){
                        DownLoadCourse tmpCourse=new DownLoadCourse();
                        tmpCourse.setLessonLists(new ArrayList<DownLoadLesson>());
                        //tmpCourse.setTotalNum(coursewares.size());
                        tmpCourse.setCourseID(userCourseID);
                        tmpCourse.setCourseName(bjyCourseBean.classTitle);
                        tmpCourse.setChangeStatus(0);
                        //tmpCourse.setCourseType(buyDetailInfo.TypeName);
                        tmpCourse.setTeacher(bjyCourseBean.teacher);
                        tmpCourse.setImageURL(bjyCourseBean.classScaleimg);
                        tmpCourse.setImagePath(bjyCourseBean.classScaleimg);
                        curDownLoadMap.put(bjyCourseBean.classid,tmpCourse);
                    }
                    DownLoadLesson courseware = new DownLoadLesson();
                    courseware.setCourseID(userCourseID);
                    courseware.setSubjectID(bjyCourseBean.rid);
                    courseware.setSubjectName(CommonUtils.getShortFileName2(videoInfo));
                    courseware.setCourseNum(i);

                    switch(videoInfo.getState()) {
                        case 1:
                        case 3:
                            // var2.status = TaskStatus.Pause;
                            courseware.setDownStatus(DownLoadStatusEnum.STOP.getValue());
                            break;
                        case 2:
                            //var2.status = TaskStatus.Downloading;
                            courseware.setDownStatus(DownLoadStatusEnum.START.getValue());
                            break;
                        case 4:
                            //var2.status = TaskStatus.Finish;
                            courseware.setDownStatus(DownLoadStatusEnum.FINISHED.getValue());
                            break;
                        case 5:
                            // var2.status = TaskStatus.Error;
                            courseware.setDownStatus(DownLoadStatusEnum.ERROR.getValue());
                            break;
                        default:
                            // var2.status = TaskStatus.New;
                            courseware.setDownStatus(DownLoadStatusEnum.STOP.getValue());
                    }
                    courseware.setPlayerType(PlayerTypeEnum.BjRecord.getValue());
                    // courseware.setDownloadID(String.valueOf(videoBean.bjyRoomId) + videoBean.bjySessionId);
                    courseware.setDownloadID(UserInfoUtil.userId+String.valueOf(videoInfo.getVideoId()));
                    courseware.setClarity(0);
                    courseware.setEncryptType(0);
                    courseware.setVideoToken(videoInfo.getVideoToken());
                    courseware.setReserve2(bjyCourseBean.teacher);
                    courseware.setImagePath(bjyCourseBean.classScaleimg);
                    courseware.setRoomId(String.valueOf(videoInfo.getVideoId()));//录播roomID存视频id
                    courseware.setPlayPath(videoInfo.getTargetPath());

                   // courseware.setDuration(videoInfo.getTask().get().get);
                    //
                    // values.put(DownLoadLesson.REVERSE_1, lesson.getReserve1());
           /*        courseware.put(DownLoadLesson.SPACE, videoInfo.getTotalLength());

           */
                    courseware.setDownloadProgress((int)videoInfo.getProgress());
                    courseware.setSpace(videoInfo.getTotalLength());

                    curDownLoadMap.get(bjyCourseBean.classid).getLessonLists().add(courseware);
                }
            }
        }

       /* DataSet.getInstance().addDownloadInfo(new DownloadVideoInfo(
                bean.getVodid(),//课件播放id
                rid,//课件id
                scaleimg,//课程图片
                title,//课程名称
                bean.getVoname(), //课件名称
                bean.getCc_uid(), //CC uid
                bean.getApi_key(), //CC key
                0,//progress
                null,// progressText
                Downloader.WAIT, //下载状态
                new Date(), //createTime
                10, //definition:20高清 10清晰
                bean.getSort(), classid, teacher)); // sort 不清楚 classid 课程id teacher 课件教师*/

       //cc的下载历史
       /* List<DownloadVideoInfo> ccDownlist= DataSet.getInstance().getDownloadInfos();
        if(!ArrayUtils.isEmpty(ccDownlist)){
            for(DownloadVideoInfo downloadInfo:ccDownlist){

                if(downloadInfo.getStatus()!=Downloader.FINISH) continue;
                String userCourseID=UserInfoUtil.userId+"_"+downloadInfo.getClassId();//因为是主键，添加用户区分
                if(!curDownLoadMap.containsKey(downloadInfo.getClassId())){
                    DownLoadCourse tmpCourse=new DownLoadCourse();
                    tmpCourse.setLessonLists(new ArrayList<DownLoadLesson>());
                    //tmpCourse.setTotalNum(coursewares.size());
                    tmpCourse.setCourseID(userCourseID);
                    tmpCourse.setCourseName(downloadInfo.getName());
                    tmpCourse.setChangeStatus(0);
                    //tmpCourse.setCourseType(buyDetailInfo.TypeName);
                    tmpCourse.setTeacher(downloadInfo.getTeacher());
                    tmpCourse.setImageURL(downloadInfo.getVcpic());
                    tmpCourse.setImagePath(downloadInfo.getVcpic());
                    curDownLoadMap.put(downloadInfo.getClassId(),tmpCourse);
                }
                DownLoadLesson courseware = new DownLoadLesson();
                courseware.setCourseID(userCourseID);
                courseware.setSubjectID(downloadInfo.getVcid());
                courseware.setSubjectName(downloadInfo.getTitle());
                courseware.setCourseNum(ArrayUtils.size(ccDownlist));

                courseware.setDownStatus(DownLoadStatusEnum.FINISHED.getValue());
                courseware.setPlayerType(PlayerTypeEnum.CCPlay.getValue());
                // courseware.setDownloadID(String.valueOf(videoBean.bjyRoomId) + videoBean.bjySessionId);

                //bean.getVodid(), bean.getCc_uid(), bean.getApi_key()
                courseware.setDownloadID(UserInfoUtil.userId+downloadInfo.getVideoId()+downloadInfo.getUserid());
                courseware.setClarity(0);
                courseware.setEncryptType(0);
                courseware.setVideoToken(downloadInfo.getApikey());
                courseware.setReserve2(downloadInfo.getTeacher());
                courseware.setImagePath(downloadInfo.getVcpic());
                courseware.setRoomId(downloadInfo.getVideoId());//录播roomID存视频id
                courseware.setSessionId(downloadInfo.getUserid());
                courseware.setPlayPath(MediaUtil.createMD5File(downloadInfo.getTitle(), MediaUtil.MP4_FILE_SUFFIX).getAbsolutePath());
           }
        }*/

        Iterator iter = curDownLoadMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            // Object key = entry.getKey();
            DownLoadCourse val = (DownLoadCourse)entry.getValue();
            val.setTotalNum(val.getLessonLists().size());

            for(DownLoadLesson item:val.getLessonLists()){
                SQLiteHelper.getInstance().insertDB(val,item);
            }
        }
    }

    public void recoryoldData(){

        convertOldDataModel();
        String rootPath= getDownloadPath();


        //TODO RecoverDbHelper为恢复旧版下载记录的工具类。没有从旧版迁移到新版的需求不用调用此工具类
        RecoverDbHelper.getInstance().setConvertCallback(new RecoverDbHelper.IConvertCallback() {
            @Override
            public void convertDownloadModel(DownloadModel downloadModel) {
                downloadModel.extraInfo = String.valueOf(UserInfoUtil.userId)+String.valueOf(downloadModel.videoId);// var1.getExtraInfo();
            }
        });
        RecoverDbHelper.getInstance().init(UniApplicationContext.getContext(), rootPath + File.separator + "bj_video_downloaded" + File.separator,
                new IRecoveryCallback() {
                    @Override
                    public void recoverySuccess() {

                        LogUtils.e("recoverySuccess","recoverySuccess");
                        if(mDownloadManager==null) return;
                        ArrayList var1 = new ArrayList();
                        Iterator var2 = mDownloadManager.getAllTask().iterator();
                        while(var2.hasNext()) {
                            DownloadInfo var3 = (DownloadInfo)var2.next();
                            var1.add(var3.getTaskKey());
                        }

                        var2 = var1.iterator();
                        while(var2.hasNext()) {
                            String var4 = (String)var2.next();
                            mDownloadManager.removeTask(var4);
                        }

                        if(monCacheRecoveryListener!=null)
                            monCacheRecoveryListener.onCacheRecoveryFinish();
                        //重新获取taskList，true代表强制刷新
                        //mDownloadManager.loadDownloadInfo(32975272, encryptType, true);
                        //adapter.notifyDataSetChanged();
                    }
                });
        //读取磁盘缓存的下载任务
        //manager.loadDownloadInfo(32975272, encryptType);
        //TODO 这一句必须在manager.loadDownloadInfo()之后，确保DownloadManager已初始化完毕

       if(RecoverDbHelper.getInstance().getRecoveryMark()) {
            if(monCacheRecoveryListener!=null)
               monCacheRecoveryListener.onCacheRecoveryFinish();
       }else {
           RecoverDbHelper.getInstance().recoveryDbData();
       }

    }
}
