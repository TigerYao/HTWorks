package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.baijia.player.playback.LivePlaybackSDK;
import com.baijia.player.playback.bean.PBDownloadInfo;
import com.baijia.player.playback.bean.PBRoomData;
import com.baijia.player.playback.dataloader.PBDataLoader;
import com.baijia.player.playback.util.PBUtils;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.player.bean.CDNInfo;
import com.baijiahulian.player.bean.PlayItem;
import com.baijiahulian.player.utils.Utils;
import com.baijiayun.download.DownloadManager;
import com.baijiayun.download.DownloadModel;
import com.baijiayun.download.DownloadService;
import com.baijiayun.download.DownloadTask;
import com.baijiayun.download.VideoUtils;
import com.baijiayun.download.constant.FileType;
import com.baijiayun.download.constant.VideoDefinition;
import com.baijiayun.log.BJFileLog;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by cjx on 2019\11\27 0027.
 *  注掉一DownloadServer的启动
 *  fix bug https://mobile.umeng.com/platform/54645278fd98c565730006bc/error_types/list/2774434044188
 *          https://www.cnblogs.com/awkflf11/p/6075244.html
 * copy form com.baijia.player.playback.downloader.PlaybackDownloader
 */


public class BJPlaybackDownloader {
    private DownloadManager manager;
    private PBDataLoader mDataLoader;
    private int deploy;
    private int encryptType;
    private PublishSubject<DownloadTask> publishSubject;
    private Executor executor;
    private AtomicInteger completedTaskCount;

    public BJPlaybackDownloader(Context var1, long var2, String var4, int var5) {
        this.publishSubject = PublishSubject.create();
        this.executor = Executors.newFixedThreadPool(8);
        this.completedTaskCount = new AtomicInteger(0);
        this.deploy = LivePlaybackSDK.deployType.getType();
        //this.manager = DownloadService.getDownloadManager(var1);

        this.manager =DownloadManager.getInstance(var1);
        this.manager.setTargetFolder(var4);
        this.manager.loadDownloadInfo(var2, var5);
        this.mDataLoader = new PBDataLoader(var5);
        this.mDataLoader.setUaString(PBUtils.getUAString(var1));
        this.mDataLoader.setIsDownload(1);
        this.encryptType = var5;
    }

    public BJPlaybackDownloader(Context var1, long var2, String var4) {
        this(var1, var2, var4, 1);
    }

    public Observable<DownloadTask> downloadRoomPackage(String var1, long var2, long var4, String var6, List<VideoDefinition> var7, int var8, String var9) {
        return this.downloadRoomPackage(new PBDownloadInfo(var2, var4, var6, var1, var9), var7);
    }

    public Observable<DownloadTask> downloadRoomPackage(final PBDownloadInfo var1, final List<VideoDefinition> var2) {
        return Observable.create(new Observable.OnSubscribe<DownloadTask>() {
            public void call(final Subscriber<? super DownloadTask> var1x) {
                BJPlaybackDownloader.this.mDataLoader.loadRoomInfo(String.valueOf(var1.roomId), var1.sessionId, var1.token, BJPlaybackDownloader.this.deploy, new PBDataLoader.a<PBRoomData>() {
                    public void a(LPError var1xx) {
                        var1x.onError(var1xx);
                        BJFileLog.e(BJPlaybackDownloader.class, "PlaybackDownloader", "downloadRoomPackage onFailure code= " + var1xx.getCode() + ", message=" + var1xx.getMessage());
                    }

                    public void onSuccess(PBRoomData var1xx) {
                        var1xx.pbDownloadInfo = var1;
                        BJPlaybackDownloader.this.innerCreateRoomPackageDownloadTask(var2, var1xx, var1x, false);
                    }
                });
            }
        });
    }

    public void batchCreateRoomPackageDownloadTask(final List<PBDownloadInfo> var1, final List<VideoDefinition> var2) {
        Observable.from(var1).flatMap(new Func1<PBDownloadInfo, Observable<PBRoomData>>() {
            public Observable<PBRoomData> call(PBDownloadInfo var1) {
                return BJPlaybackDownloader.this.mDataLoader.getLoadRoomInfoObservable(var1, BJPlaybackDownloader.this.deploy).subscribeOn(Schedulers.from(BJPlaybackDownloader.this.executor));
            }
        }).flatMap(new Func1<PBRoomData, Observable<DownloadTask>>() {
            public Observable<DownloadTask> call(final PBRoomData var1) {
                return Observable.create(new Observable.OnSubscribe<DownloadTask>() {
                    public void call(Subscriber<? super DownloadTask> var1x) {
                        BJPlaybackDownloader.this.innerCreateRoomPackageDownloadTask(var2, var1, var1x, true);
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Action1<DownloadTask>() {
            public void call(DownloadTask var1x) {
                BJPlaybackDownloader.this.publishSubject.onNext(var1x);
                if(BJPlaybackDownloader.this.completedTaskCount.getAndIncrement() + 1 == var1.size()) {
                    BJPlaybackDownloader.this.manager.saveToFile();
                    BJPlaybackDownloader.this.completedTaskCount.set(0);
                    BJPlaybackDownloader.this.publishSubject.onCompleted();
                    BJPlaybackDownloader.this.publishSubject = PublishSubject.create();
                }

            }
        }, new Action1<Throwable>() {
            public void call(Throwable var1x) {
                BJPlaybackDownloader.this.publishSubject.onError(var1x);
                if(BJPlaybackDownloader.this.completedTaskCount.getAndIncrement() + 1 == var1.size()) {
                    BJPlaybackDownloader.this.manager.saveToFile();
                    BJPlaybackDownloader.this.completedTaskCount.set(0);
                }

                BJPlaybackDownloader.this.publishSubject = PublishSubject.create();
            }
        });
    }

    private void innerCreateRoomPackageDownloadTask(List<VideoDefinition> var1, PBRoomData var2, final Subscriber<? super DownloadTask> var3, boolean var4) {
        VideoDefinition var5 = null;
        Iterator var6 = var1.iterator();

        while(var6.hasNext()) {
            VideoDefinition var7 = (VideoDefinition)var6.next();
            if(PBUtils.hasThisDefinition(var2, var7.ordinal())) {
                var5 = var7;
                break;
            }
        }

        if(var5 == null) {
            var3.onError(new Exception("没有找到对应清晰度"));
        } else if(var2.packageSignal == null) {
            var3.onError(new Exception("没有收到离线下载信令"));
        } else {
            String var13 = Utils.replaceIllegalChar(var2.pbDownloadInfo.fileName);
            if(this.manager.isDownloadAudio() && !TextUtils.isEmpty(var2.audioUrl)) {
                var5 = VideoDefinition.AUDIO;
                var2.playInfo.audio = new PlayItem();
                var2.playInfo.audio.cdnList = new CDNInfo[1];
                CDNInfo var14 = new CDNInfo();
                var14.url = var2.audioUrl;
                var14.size = var2.audioSize;
                var2.playInfo.audio.cdnList[0] = var14;
            }

            DownloadModel var15 = new DownloadModel();
            CDNInfo var8 = VideoUtils.decryptDownloadUrl(var2, var5.getType());
            var15.playItem = VideoUtils.getCurrentDefinitionCDNList(var2, var5.getType());
            if(var5 == VideoDefinition.AUDIO) {
                var15.url = var8.url;
                var15.fileType = FileType.Audio;
                var15.availableCND.add(var15.url);
            } else {
                var15.url = Utils.decodeUrl(var8.enc_url);
                var15.fileType = FileType.Video;
                CDNInfo[] var9 = var15.playItem.cdnList;
                int var10 = var9.length;

                for(int var11 = 0; var11 < var10; ++var11) {
                    CDNInfo var12 = var9[var11];
                    var15.availableCND.add(Utils.decodeUrl(var12.enc_url));
                }
            }

            var15.definition = var5;
            var15.videoId = var2.videoId;
            var15.sessionId = var2.pbDownloadInfo.sessionId;
            var15.videoDuration = var2.duration;
            var15.roomId = var2.pbDownloadInfo.roomId;
            var15.targetName = var13 + "_" + var2.videoId + "." + MimeTypeMap.getFileExtensionFromUrl(var15.url);
            var15.videoName = var2.pbDownloadInfo.fileName;
            var15.encryptType = this.encryptType;
            var15.videoToken = var2.pbDownloadInfo.token;
            var15.extraInfo = var2.pbDownloadInfo.extraInfo;
            var15.totalLength = var8.size;
            var15.targetFolder = this.manager.getTargetFolder();
            var15.coverUrl = var2.initPicture;
            DownloadModel var16 = new DownloadModel();
            var16.url = var2.packageSignal.packageUrl;
            var16.availableCND.add(var16.url);
            var16.extraInfo = var2.pbDownloadInfo.extraInfo;
            var16.videoId = var2.videoId;
            var16.sessionId = var2.pbDownloadInfo.sessionId;
            var16.roomId = var2.pbDownloadInfo.roomId;
            var16.fileType = FileType.Signal;
            var16.targetName = "s_" + var13 + "_" + var2.videoId;
            var16.videoName = "s_" + var2.pbDownloadInfo.fileName;
            var16.encryptType = this.encryptType;
            var16.videoToken = var2.pbDownloadInfo.token;
            var16.totalLength = var2.packageSignal.packageSize;
            var16.targetFolder = this.manager.getTargetFolder();
            var15.nextModel = var16;
            this.manager.newPlayBackTask(var15, var4).subscribe(new Action1<DownloadTask>() {
                public void call(DownloadTask var1) {
                    BJFileLog.d(BJPlaybackDownloader.class, "PlaybackDownloader", " videoModel=" + var1.getDownloadInfo().toString());
                    var3.onNext(var1);
                }
            }, new Action1<Throwable>() {
                public void call(Throwable var1) {
                    PBUtils.onError(var3, var1);
                }
            });
        }
    }

    public String getVideoPathByRoom(long var1, long var3) {
        DownloadTask var5 = this.manager.getTaskByRoom(var1, var3);
        return var5.getDownloadInfo().targetFolder + var5.getFileName();
    }

    public String getSignalPathByRoom(long var1, long var3) {
        DownloadTask var5 = this.manager.getTaskByRoom(var1, var3);
        return var5.getDownloadInfo().targetFolder + var5.getSignalFileName();
    }

    public DownloadManager getManager() {
        return this.manager;
    }

    public PublishSubject<DownloadTask> getBatchDownloadObserver() {
        return this.publishSubject;
    }
}
