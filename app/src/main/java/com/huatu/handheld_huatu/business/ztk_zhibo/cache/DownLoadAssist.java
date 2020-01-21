package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.baijia.player.playback.downloader.PlaybackDownloader;
import com.baijiahulian.common.networkv2.HttpException;
import com.baijiayun.download.DownloadListener;
import com.baijiayun.download.DownloadManager;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.download.DownloadTask;
import com.baijiayun.download.constant.DownloadType;
import com.baijiayun.download.constant.TaskStatus;
import com.baijiayun.download.constant.VideoDefinition;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLVodListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDeleteCacheListener;
import com.huatu.handheld_huatu.helper.BaijDownloadService;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/*import com.gensee.download.VodDownLoadEntity;
import com.gensee.download.VodDownLoadStatus;
import com.gensee.download.VodDownLoader;
import com.gensee.entity.ChatMsg;
import com.gensee.entity.InitParam;
import com.gensee.entity.QAMsg;
import com.gensee.entity.VodObject;
import com.gensee.vod.VodSite;*/
//import com.huatu.handheld_huatu.business.ztk_zhibo.bean.VodSiteInitParamBuilder;


/**
 * Created by DongDong on 2016/3/30.
 */
public class DownLoadAssist {

    public  final static String RECOVERY_TAG = "DownLoadAssist_RecoveryDownLoadId";
    private static volatile DownLoadAssist instance;
   // private VodDownLoader mVodDownLoader;
  //  private VodDownLoader.OnDownloadListener onVodDownloadListener;
   // private VodSite vodSite;
   // @param encryptType 视频是否加密 1：加密；0：不加密
    private final int  encryptType=0;////老版本为0，兼容设为0

    //private List<OnDLVodListener> mDLVodListenerList = new ArrayList<>();
    private BJPlaybackDownloader mBaiJiaPbDownloader;
    // 清晰度优先数组
    private List<VideoDefinition> definitionList = new ArrayList<>(Arrays.asList(VideoDefinition.SD,
            VideoDefinition.HD, VideoDefinition.SHD, VideoDefinition._720P, VideoDefinition._1080P));
    private DownloadManager mBaiJiaDownloadManager;
    private String mRootPath = "";
    private DownloadListener mBaiJiaDownloadListener;

    private int mErrorTryTimes=0;

    // private String curDownloadingId;
    //  private int lastProgress;

    private int mDefinitonIndex=0;

    public void setCustomDefinition(int index){
        mDefinitonIndex=index;
    }

    private int mCurrentUserId;

    private DownLoadDbHelper mDownLoadDbHelper;

    private List<WeakReference<OnDeleteCacheListener>> mDeleteFileListener;

    public void addDeleteCacheListener(OnDeleteCacheListener deleteCacheListener){
        if (mDeleteFileListener == null) {
            mDeleteFileListener = new ArrayList<>();
        }
        mDeleteFileListener.add(new WeakReference<>(deleteCacheListener));
    }

    public void removeDeleteCacheListener(OnDeleteCacheListener deleteCacheListener){
       if (mDeleteFileListener != null) {
           for(WeakReference<OnDeleteCacheListener> weakReference : mDeleteFileListener) {
               if(weakReference.get() == deleteCacheListener) {
                   mDeleteFileListener.remove(weakReference);
                   break;
               }
           }
       }
    }

    public void addDownloadListener(OnDLVodListener mDLVodListener) {
        if(mDownLoadDbHelper==null){
            mDownLoadDbHelper=new DownLoadDbHelper();
        }
        mDownLoadDbHelper.addDownloadListener(mDLVodListener);
    }

    public void removeDownloadListener(OnDLVodListener mDLVodListener) {
        if(mDownLoadDbHelper!=null)
          mDownLoadDbHelper.removeDownloadListener(mDLVodListener);
    }

    private DownLoadAssist() {
    }

    public static DownLoadAssist getInstance() {
        if (instance == null) {
            synchronized (DownLoadAssist.class) {
                if (instance == null) {
                    instance = new DownLoadAssist();
                    instance.init(UniApplicationContext.getContext());
                }
            }
        }
        return instance;
    }

    private static String getDownloadPath() {
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
        File fileDL = new File(rootPath, "fileDL"+String.valueOf(UserInfoUtil.userId));
        if (!fileDL.exists()) {
            fileDL.mkdirs();
        }
        return rootPath;
    }


    public void resetDownAssist() {

        if (mCurrentUserId == UserInfoUtil.userId) {
            return;
        }
        mCurrentUserId = UserInfoUtil.userId;
        mRootPath = getDownloadPath();
        if (mDeleteFileListener != null) {
            mDeleteFileListener.clear();
        }
        if(mDownLoadDbHelper==null){
            mDownLoadDbHelper=new DownLoadDbHelper();
        }
      /*  if (mVodDownLoader != null) {
            mVodDownLoader.release();
        }
        mVodDownLoader = VodDownLoader.instance(UniApplicationContext.getContext(), String.valueOf(UserInfoUtil.userId),
                onVodDownloadListener, new File(mRootPath, "fileDL").getAbsolutePath());
        mVodDownLoader.setAutoDownloadNext(false);*/

        // 回放下载初始化  添加userId目录  33243432L

        if (mBaiJiaPbDownloader == null) {
            mBaiJiaPbDownloader = new BJPlaybackDownloader(UniApplicationContext.getContext(), Constant.BAIJIAYNN_PARTNER_KEY,
                    mRootPath + File.separator + "bj_video_downloaded" + File.separator + UserInfoUtil.userId + File.separator);
        } else {
            mBaiJiaPbDownloader.getManager().setTargetFolder(mRootPath + File.separator + "bj_video_downloaded" + File.separator + UserInfoUtil.userId + File.separator);
            // mBaiJiaPbDownloader.getManager().loadDownloadInfo(Constant.BAIJIAYNN_PARTNER_KEY);
            mBaiJiaPbDownloader.getManager().loadDownloadInfo(Constant.BAIJIAYNN_PARTNER_KEY, 1, true);

        }
        mBaiJiaDownloadManager = BaijDownloadService.getDownloadManager(UniApplicationContext.getContext());
    }

    /**
     * 初始化下载配置
     */
    public void init(Context context) {
        mRootPath = getDownloadPath();

     /*   mVodDownLoader = VodDownLoader.instance(context, String.valueOf(UserInfoUtil.userId),
                onVodDownloadListener, new File(mRootPath, "fileDL").getAbsolutePath());


        mVodDownLoader.setAutoDownloadNext(false);*/
        // 回放下载初始化  添加userId目录
        mBaiJiaPbDownloader = new BJPlaybackDownloader(UniApplicationContext.getContext(), 33243432L,
                mRootPath + File.separator + "bj_video_downloaded" + File.separator + UserInfoUtil.userId + File.separator);


        mBaiJiaDownloadManager = BaijDownloadService.getDownloadManager(  UniApplicationContext.getContext());

        mDownLoadDbHelper = new DownLoadDbHelper();
        mCurrentUserId = UserInfoUtil.userId;
    }



    String mCurrentDownloadId="";
    public void createBaiJiaDownloadListener() {
        if (mBaiJiaDownloadListener == null) {
            mBaiJiaDownloadListener = new DownloadListener() {
                @Override
                public void onProgress(DownloadTask downloadTask) {
                    if (downloadTask == null) {
                        LogUtils.i("downloadTask == null");
                        return;
                    }
                    DownloadModel downloadModel = downloadTask.getVideoDownloadInfo();
                    if (downloadModel == null) {
                        LogUtils.i("downloadModel == null");
                        return;
                    }
                    int progress = (int) ((downloadTask.getDownloadedLength() * 10000
                            / (float) downloadTask.getTotalLength()) / 100);

                    LogUtils.i("onProgress ", progress + "," + downloadModel.extraInfo);
                    mDownLoadDbHelper.updateDLProgress(downloadModel.extraInfo, progress,downloadTask.getSpeed());
                }

                @Override
                public void onError(DownloadTask downloadTask, HttpException e) {
                    LogUtils.i("onError: " + e.toString());
                    if (downloadTask == null) {
                        LogUtils.i("downloadTask == null");
                        return;
                    }
                    DownloadModel downloadModel = downloadTask.getVideoDownloadInfo();
                    if (downloadModel == null) {
                        LogUtils.i("downloadModel == null");
                        return;
                    }
                    mDownLoadDbHelper.onDownloadError(downloadModel.extraInfo, -99999);

                    //https://github.com/baijia/BJVideoPlayerDemo-Android/blob/master/BJVideoPlayerDemo/app/src/main/java/com/baijiahulian/download/SimpleVideoDownloadActivity.java
                    //下载地址已失效,5103(token已失效)
                    if (e.getCode() == 403 || e.getCode() >= 5101 && e.getCode() <= 5103) {
                        //TODO 需要用户传入新的token, 重新获取视频下载地址
                        mDownLoadDbHelper.refreshToken(downloadModel,downloadTask.getDownloadType()==DownloadType.Playback, new Action1<DownLoadLesson>() {
                            @Override
                            public void call(DownLoadLesson downLoadLesson) {
                                addDownload(downLoadLesson, true);
                            }
                        });
                    }
                    else {
                        //同一个downloadId错误,5次错误下载下一个
                        if((mErrorTryTimes<10)&&NetUtil.isConnected()){
                            mErrorTryTimes++;
                            if((!TextUtils.isEmpty(mCurrentDownloadId))&&mErrorTryTimes>4
                                    &&mCurrentDownloadId.equals(downloadModel.extraInfo)){
                                Intent autoDownIntent=new Intent("com.huatu.start_download_course");
                                autoDownIntent.putExtra(ArgConstant.IS_LIVE,true);
                                UniApplicationContext.getContext().sendBroadcast(autoDownIntent);

                            }else {
                                mCurrentDownloadId=downloadModel.extraInfo;
                                if(downloadTask!=null) downloadTask.start();
                            }
                       }else {

                            String errorMsg=e.getMessage();
                            if(e.getCode()==500){
                                ToastUtils.showShort("网络连接出错，请检查网络设置");
                            }
                            else if(!TextUtils.isEmpty(errorMsg)){
                                if(errorMsg.contains("timeout"))
                                    ToastUtils.showShort("网络连接超时，请检查网络设置");
                                else
                                    ToastUtils.showShort(e.getMessage());
                            }
                             /*  if(e instanceof UnknownHostException) {
                               ToastUtils.showShort("无法连接网络，请检查网络设置");
                             // onDownloadStop(downloadModel.extraInfo);
                              } else {*/
                        }
                    }

                }

                @Override
                public void onPaused(DownloadTask downloadTask) {
                    LogUtils.i("onPaused: ");
                    if (downloadTask == null) {
                        LogUtils.i("downloadTask == null");
                        return;
                    }
                    DownloadModel downloadModel = downloadTask.getVideoDownloadInfo();
                    if (downloadModel == null) {
                        LogUtils.i("downloadModel == null");
                        return;
                    }
                    LogUtils.i("onPaused: " + downloadModel.extraInfo);
                    mDownLoadDbHelper.onDbDownloadStop(downloadModel.extraInfo);
                }

                @Override
                public void onStarted(DownloadTask downloadTask) {
                    LogUtils.i("onStarted: ");
                    if (downloadTask == null) {
                        LogUtils.i("downloadTask == null");
                        return;
                    }
                    DownloadModel downloadModel = downloadTask.getVideoDownloadInfo();
                    if (downloadModel == null) {
                        LogUtils.i("downloadModel == null");
                        return;
                    }
                    mDownLoadDbHelper.onDbDownloadStarted(downloadModel.extraInfo);
                }

                @Override
                public void onFinish(DownloadTask downloadTask) {
                    LogUtils.i("onFinish: ");
                    setTaskFinish(downloadTask);
                }

                @Override
                public void onDeleted(long l) {
                    LogUtils.i("onDeleted: " + l);

                }
            };
        }
    }

    private void setTaskFinish(DownloadTask downloadTask) {
        if (downloadTask == null) {
            LogUtils.i("downloadTask == null");
            return;
        }
        DownloadModel downloadModel = downloadTask.getVideoDownloadInfo();
        if (downloadModel == null) {
            LogUtils.i("downloadModel == null");
            return;
        }
        /*String parentPath = mRootPath + File.separator + "bj_video_downloaded";
        String fullPath = parentPath + File.separator + downloadModel.targetName;*/
        LogUtils.e("DownloadModel", GsonUtil.GsonString(downloadModel));
        //downloadTask.getFileName();
        if(downloadTask.getDownloadType()== DownloadType.Playback){
            String signalPath = downloadModel.targetFolder  + downloadTask.getSignalFileName();
            SQLiteHelper.getInstance().upDateSinalFilePath(String.valueOf(downloadModel.extraInfo), signalPath);
        }
        mDownLoadDbHelper.onDownloadFinish(String.valueOf(downloadModel.extraInfo), downloadModel.targetFolder+downloadModel.targetName);
        //String signalPath = parentPath + File.separator+ downloadTask.getDownloadInfo().nextModel.targetName;
    }

    public void addDownload(final DownLoadLesson lesson, final boolean quickStart){

        BaijDownloadService.startService();
        addDownload(lesson,quickStart,definitionList);
    }
    //方法入口
    public void addDownload(final DownLoadLesson lesson, final boolean quickStart,List<VideoDefinition> customDefinitions) {
        if (lesson == null) {
            return;
        }
        LogUtils.i("addDownload: " + lesson.getDownloadID()+""+quickStart);
        if (lesson.getPlayerType() == PlayerTypeEnum.BaiJia.getValue()) {
            //与服务器异步交互返回DownloadModel ，newPlayBackTask转为DownloadTask,添加到DownloadManager的 taskList
            // 切换到ui线程，返回DownloadTask

            mBaiJiaPbDownloader.downloadRoomPackage(StringUtils.filterFileName(lesson.getSubjectName()),
                    Method.parseLong(lesson.getRoomId()),
                    Method.parseLong(lesson.getSessionId()), lesson.getVideoToken(),
                    customDefinitions, encryptType, lesson.getDownloadID())
                    .subscribe(new Action1<DownloadTask>() {
                        @Override
                        public void call(DownloadTask task) {

                            DownloadModel downloadModel = task.getVideoDownloadInfo();
                            createBaiJiaDownloadListener();
                            task.setDownloadListener(mBaiJiaDownloadListener);
                            if (downloadModel != null) {
                                long size = task.getTotalLength();
                                //时长更新本地数据库
                                mDownLoadDbHelper.onDownloadStorage(lesson.getDownloadID(), size,
                                        (int) task.getVideoDuration() * 1000);
                            } else {
                                LogUtils.i("downloadModel is null");
                            }
                            mDownLoadDbHelper.onDbDownloadPrepareAndStart(lesson);
                            if (quickStart) {
                                task.start();
                            }
                         }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable != null) {
                                throwable.printStackTrace();
                                LogUtils.i("ErrorCode: " + throwable.toString());
                            } else {
                                LogUtils.i("ErrorCode: throwable == null");
                            }
                            mDownLoadDbHelper.onDownloadError(lesson.getDownloadID(), -9999);
                        }
                    });

        } else if (isRecord(lesson.getPlayerType())) {
            long videoId = StringUtils.parseLong(lesson.getRoomId());
            if (videoId == 0) {
                ToastUtils.showShort("视频id出错");
                return;
            }

            mBaiJiaDownloadManager.newDownloadTask(StringUtils.filterFileName(lesson.getSubjectName()), videoId, lesson.getVideoToken(), customDefinitions, encryptType, lesson.getDownloadID())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<DownloadTask>() {
                        @Override
                        public void call(DownloadTask task) {

                            LogUtils.i("onVideoInfoGetSuccess:");
                            DownloadModel downloadModel = task.getVideoDownloadInfo();
                            createBaiJiaDownloadListener();
                            task.setDownloadListener(mBaiJiaDownloadListener);
                            if (downloadModel != null) {
                                long size = task.getTotalLength();
                                //时长更新本地数据库
                                mDownLoadDbHelper.onDownloadStorage(lesson.getDownloadID(), size,
                                        (int) task.getVideoDuration() * 1000);
                            } else {
                                LogUtils.i("downloadModel is null");
                            }
                            mDownLoadDbHelper.onDbDownloadPrepareAndStart(lesson);
                            if (quickStart) {
                                task.start();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable != null) {
                                throwable.printStackTrace();
                                LogUtils.i("ErrorCode: " + throwable.toString());
                            } else {
                                LogUtils.i("ErrorCode: throwable == null");
                            }
                            mDownLoadDbHelper.onDownloadError(lesson.getDownloadID(), -9999);
                        }
                    });
        } else {

        }
    }

    private String mRecoveryDownLoadId = "";//由于网络切换，暂停目前的下载，网络好时恢复

    private void stopBaijiaDownloading(DownloadTask task) {

        if (task.getTaskStatus() == TaskStatus.Downloading) {
            task.pause();
            DownloadModel downloadModel = task.getVideoDownloadInfo();
            if (downloadModel != null) {
                mDownLoadDbHelper.onDbDownloadStop(downloadModel.extraInfo);
                mRecoveryDownLoadId = downloadModel.extraInfo;
            }
        }
    }

    private void setTaskWaitingStatus(DownloadTask task) {
        if (task.getTaskStatus() == TaskStatus.Downloading) {
            task.setDownloadListener(null);
            task.pause();
            DownloadModel downloadModel = task.getVideoDownloadInfo();
            if (downloadModel != null) {
                mDownLoadDbHelper.onDbDownloadWaiting(downloadModel.extraInfo);
            }
        }
    }

    public void download(DownLoadLesson lesson){
        BaijDownloadService.startService();
        download(lesson,definitionList);
    }

    private boolean isRecord(int playType){
        return (playType==PlayerTypeEnum.BjRecord.getValue())||(playType==PlayerTypeEnum.BjAudio.getValue());
    }

    //下载时会停止其他的
    public void download(DownLoadLesson lesson,List<VideoDefinition> customDefinitions) {
        if (lesson == null) {
            return;
        }
        LogUtils.i("start download: " + lesson.getDownloadID());
        //curDownloadingId = lesson.getDownloadID();
        mDownLoadDbHelper.setCurDownloadingId(lesson.getDownloadID());
        boolean isStart = false;
        createBaiJiaDownloadListener();
        List<DownloadTask> allTask = mBaiJiaDownloadManager.getAllTasks();
       // List<VodDownLoadEntity> entityList = mVodDownLoader.getDownloadList();
        boolean isContain = false;
        //目前的课程为百家云
        if ((lesson.getPlayerType() == PlayerTypeEnum.BaiJia.getValue())||isRecord(lesson.getPlayerType())) {
            if (!Method.isListEmpty(allTask)) {
                //如果找到匹配 开始下载，不匹配停止下载
                for (DownloadTask task : allTask) {

                    task.setDownloadListener(mBaiJiaDownloadListener);
                    DownloadModel info = task.getVideoDownloadInfo();
                    if (info == null) continue;
                    if (Method.isEqualString(lesson.getDownloadID(), info.extraInfo)) {
                        isContain = true;

                        LogUtils.e("start download: " + task.getTaskStatus().getType());
                        if (task.getTaskStatus() != TaskStatus.Finish
                                || task.getTaskStatus() != TaskStatus.Error) {

                            task.start();//开始执行下载任务
                            isStart = true;
                        } else if (task.getTaskStatus() == TaskStatus.Finish) {//有时候，出现task与数据库的状态不一致，在此修复
                            setTaskFinish(task);
                        }
                    } else {
                        setTaskWaitingStatus(task);
                    }
                }
            }
           /* if (!Method.isListEmpty(entityList)) {
                for (VodDownLoadEntity entity : entityList) {
                    stopGenseeDownloading(entity, lesson.getDownloadID());
                }
            }*/
        } else {//目前的课程为展示
            /*if (!Method.isListEmpty(entityList)) {
                for (VodDownLoadEntity entity : entityList) {
                    if (Method.isEqualString(lesson.getDownloadID(), entity.getDownLoadId())) {
                        mVodDownLoader.download(lesson.getDownloadID());
                        isContain = true;
                        isStart = true;
                    } else {
                        stopGenseeDownloading(entity, lesson.getDownloadID());
                    }
                }
            }
            if (!Method.isListEmpty(allTask)) {
                for (DownloadTask task : allTask) {
                    stopBaijiaDownloading(task);
                }
            }*/
        }
        if (!isContain) {
            LogUtils.i("未在下载队列中，重新添加下载");
            addDownload(lesson, true,customDefinitions);
        }
        if (isStart) {
            //curDownloadingId = lesson.getDownloadID();
            // lastProgress = lesson.getDownloadProgress();
            mDownLoadDbHelper.onDbDownloadStarted(lesson.getDownloadID());
            mDownLoadDbHelper.setLastProgress(lesson.getDownloadProgress());
        }
    }

    //恢复网络下载
    public boolean recoveryNetDownload() {

        if (TextUtils.isEmpty(mRecoveryDownLoadId)) {
            mRecoveryDownLoadId = PrefStore.getSettingString(RECOVERY_TAG, "");
            if (TextUtils.isEmpty(mRecoveryDownLoadId))
                return false;
        }
        List<DownloadTask> allTask = mBaiJiaDownloadManager.getAllTasks();

        boolean hasfinded = false;
        if (!Method.isListEmpty(allTask)) {
            for (DownloadTask task : allTask) {
                task.setDownloadListener(mBaiJiaDownloadListener);
                DownloadModel info = task.getVideoDownloadInfo();
                if (info == null) {
                    continue;
                }
                if (Method.isEqualString(mRecoveryDownLoadId, info.extraInfo)) {
                    hasfinded = true;
                    if (task.getTaskStatus() == TaskStatus.Finish) setTaskFinish(task);
                    else {
                        mDownLoadDbHelper.onDbDownloadStarted(info.extraInfo);
                        task.start();
                    }
                    break;
                }
            }
        }
        if (hasfinded) {
            mRecoveryDownLoadId = "";
            PrefStore.putSettingString(RECOVERY_TAG, "");
            return true;
        }
      /*  List<VodDownLoadEntity> entityList = mVodDownLoader.getDownloadList();
        if (!Method.isListEmpty(entityList)) {
            for (VodDownLoadEntity entity : entityList) {
                if (Method.isEqualString(mRecoveryDownLoadId, entity.getDownLoadId())) {
                    hasfinded = true;
                    mDownLoadDbHelper.onDbDownloadStarted(entity.getDownLoadId());
                    mVodDownLoader.download(entity.getDownLoadId());
                    break;
                }
            }
        }*/
        mRecoveryDownLoadId = "";
        PrefStore.putSettingString(RECOVERY_TAG, "");
        return hasfinded;
    }

    public void stopAll(boolean canRecovery) {

        BaijDownloadService.cancelNotification();
        if(null==mBaiJiaDownloadManager) return;
        List<DownloadTask> allTask = mBaiJiaDownloadManager.getAllTasks();
        if (!Method.isListEmpty(allTask)) {
            for (DownloadTask task : allTask) {
                task.setDownloadListener(mBaiJiaDownloadListener);
                DownloadModel info = task.getVideoDownloadInfo();
                if (info == null) {
                    continue;
                }
                stopBaijiaDownloading(task);
            }
        }
       /* List<VodDownLoadEntity> entityList = mVodDownLoader.getDownloadList();
        if (!Method.isListEmpty(entityList)) {
            for (VodDownLoadEntity entity : entityList) {

                if (entity.getnStatus() != VodDownLoadStatus.FINISH.getStatus()
                        && entity.getnStatus() != VodDownLoadStatus.FAILED.getStatus()
                        && entity.getnStatus() != VodDownLoadStatus.STOP.getStatus()) {
                    mVodDownLoader.stop(entity.getDownLoadId());
                    mRecoveryDownLoadId = entity.getDownLoadId();
                }
            }
        }*/
        if (!canRecovery) {
            mRecoveryDownLoadId = "";
            PrefStore.putSettingString(RECOVERY_TAG, "");
        } else {
            if (!TextUtils.isEmpty(mRecoveryDownLoadId))
                PrefStore.putSettingString(RECOVERY_TAG, mRecoveryDownLoadId);
        }
    }

    public void stop(DownLoadLesson lesson) {
        if (lesson == null) {
            return;
        }
        LogUtils.i("stop download: " + lesson.getDownloadID());
        String curDownloadingId = mDownLoadDbHelper.getCurrentDownLoadingId();
        if (Method.isEqualString(lesson.getDownloadID(), curDownloadingId)) {
            mDownLoadDbHelper.onDbDownloadStop(lesson.getDownloadID());
        }
        boolean isStop = false;
        if (lesson.getPlayerType() == PlayerTypeEnum.BaiJia.getValue()||(isRecord(lesson.getPlayerType()))) {
            List<DownloadTask> allTask = mBaiJiaDownloadManager.getAllTasks();
            if (!Method.isListEmpty(allTask)) {
                for (DownloadTask task : allTask) {
                    task.setDownloadListener(mBaiJiaDownloadListener);
                    DownloadModel info = task.getVideoDownloadInfo();
                    if (info == null) {
                        continue;
                    }

                    if (Method.isEqualString(lesson.getDownloadID(), info.extraInfo)) {
                        if (task.getTaskStatus() == TaskStatus.Downloading) {
                            mDownLoadDbHelper.onDbDownloadStop(info.extraInfo, false);
                            task.pause();
                            isStop = true;
                            break;
                        } else if (task.getTaskStatus() == TaskStatus.Finish) {//有时候，出现task与数据库的状态不一致，在此修复
                            setTaskFinish(task);
                        }else {//wateiing-->stop
                            mDownLoadDbHelper.onDbDownloadStop(info.extraInfo, true);
                        }
                    }
                }
            }
        } else {
           /* List<VodDownLoadEntity> entityList = mVodDownLoader.getDownloadList();
            if (!Method.isListEmpty(entityList)) {
                for (VodDownLoadEntity entity : entityList) {
                    if (Method.isEqualString(entity.getDownLoadId(), lesson.getDownloadID())) {
                        if (entity.getnStatus() != VodDownLoadStatus.FINISH.getStatus()
                                && entity.getnStatus() != VodDownLoadStatus.FAILED.getStatus()
                                && entity.getnStatus() != VodDownLoadStatus.STOP.getStatus()) {
                            mDownLoadDbHelper.onDbDownloadStop(lesson.getDownloadID(), false);
                            mVodDownLoader.stop(lesson.getDownloadID());
                            isStop = true;
                            break;
                        }
                    }
                }
            }*/
        }
        if (isStop) {
            mDownLoadDbHelper.onDbDownloadStop(lesson.getDownloadID());
        }
    }


    //强制刷新数据库状态  无回调
    public void setLessionStatus(DownLoadLesson lesson, DownLoadStatusEnum loadStatusEnum) {

        if (loadStatusEnum == DownLoadStatusEnum.STOP)
            mDownLoadDbHelper.onDbDownloadStop(lesson.getDownloadID(), false);
        else if (loadStatusEnum == DownLoadStatusEnum.PREPARE)
            mDownLoadDbHelper.onDbDownloadPrepareAndStart(lesson, false);

    }

    public void delete(DownLoadLesson lesson) {
        delete(lesson, true);
    }

    public void delete(DownLoadLesson lesson, boolean isDeleteDb) {
        if (lesson == null) {
            return;
        }
        if (lesson.getPlayerType() == PlayerTypeEnum.BaiJia.getValue()||(isRecord(lesson.getPlayerType()))) {

            DownloadTask task = isRecord(lesson.getPlayerType()) ?
                    mBaiJiaDownloadManager.getTaskByVideoId(StringUtils.parseLong(lesson.getRoomId())) :
                    mBaiJiaDownloadManager.getTaskByRoom(Method.parseLong(lesson.getRoomId()), Method.parseLong(lesson.getSessionId()));
            if (task == null) {
                if (isDeleteDb) {
                    SQLiteHelper.getInstance().deleteLesson(lesson.getDownloadID(), lesson.getCourseID());
                }
                return;
            }
            if (task.getTaskStatus() == TaskStatus.Downloading) {
                task.setDownloadListener(null);
                task.cancel();
            }
            mBaiJiaDownloadManager.deleteTask(task);
        } else {
            // mVodDownLoader.delete(lesson.getDownloadID().replace(String.valueOf(UserInfoUtil.userId),""));
        }
        if (isDeleteDb) {
            SQLiteHelper.getInstance().deleteLesson(
                    lesson.getDownloadID(), lesson.getCourseID());

            if (mDeleteFileListener != null) {
                for (int i = 0, z = mDeleteFileListener.size(); i < z; i++) {
                    WeakReference<OnDeleteCacheListener> listener = mDeleteFileListener.get(i);
                    if (listener != null) {
                        OnDeleteCacheListener curlistener=listener.get();
                        if(curlistener!= null)
                           curlistener.onDeleteDownFile(lesson.getDownloadID(), lesson.getSubjectID());
                    }
                }
            }
            // mDownLoadDbHelper.deleteLesson(lesson);
        }
    }

    public void delete(List<DownLoadLesson> lessons) {
        if (Method.isListEmpty(lessons)) {
            return;
        }
        for (int i = 0; i < lessons.size(); i++) {
            delete(lessons.get(i));
        }
    }



}
