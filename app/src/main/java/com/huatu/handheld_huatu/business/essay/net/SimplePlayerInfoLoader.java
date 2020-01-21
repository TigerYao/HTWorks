package com.huatu.handheld_huatu.business.essay.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.baijiahulian.common.networkv2.HttpException;
import com.baijiahulian.player.BJPlayerView;
import com.baijiahulian.player.bean.VideoItem;
import com.baijiahulian.player.playerview.PlayerInfoLoader;
import com.baijiahulian.player.utils.BJLog;
import com.baijiayun.log.BJFileLog;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.util.UUID;

/**百家云视频信息获取
 *
 *   libLoader.loadLibrary("ijkbjffmpeg");
     libLoader.loadLibrary("ijkbjsdl");
     libLoader.loadLibrary("ijkbjplayer");
 * Created by cjx on 2019\7\11 0011.
 */

public class SimplePlayerInfoLoader implements  com.baijiahulian.player.playerview.PlayerInfoLoader.a<VideoItem>  {
    private PlayerInfoLoader bj;
    private String aK;//UAString
    private int bf;
    private String be;
    private String accessKey;
    private VideoItem mVideoItem;
    private long bd;
    private int aZ;
    private long bc;//VIDEO_ID

    public SimplePlayerInfoLoader(){
        initPartner(Constant.BAIJIAYNN_PARTNER_KEY, BJPlayerView.PLAYER_DEPLOY_ONLINE);
    }

    private void initPartner(long var1, int var3) {
        this.initPartner(var1, var3, 1);
    }

    private void initPartner(long var1, int var3, int var4) {
        SharedPreferences var5 = UniApplicationContext.getApplication().getSharedPreferences("baijiaCloud_video_player", 0);
        String var6 = var5.getString("uuid", (String)null);
        if(var6 == null) {
            var6 = UUID.randomUUID().toString();
            var5.edit().putString("uuid", var6).apply();
        }
       this.initPartner(var1, var6, var3, var4);

   }


    private void initPartner(long var1, String var3, int var4, int var5) {
        this.bf = Math.max(0, Math.min(2, var4));
        this.bj = new PlayerInfoLoader(var1, var3, var5);
        this.aK = getUAString(UniApplicationContext.getContext());
        if(this.bj != null) {
            this.bj.c(aK);
        }
    }


    private String getUAString(Context var1) {
        StringBuffer var2 = new StringBuffer();

        try {
            var2.append(Build.BRAND);
            var2.append(Build.MODEL);
            var2.append("-android-");
            var2.append(Build.VERSION.RELEASE);
            var2.append("|");
            PackageInfo var3 = var1.getPackageManager().getPackageInfo(var1.getPackageName(), 0);
            var2.append(var3.packageName);
            var2.append(var3.versionName);
            var2.append("|BJVideoPlayer");
            var2.append("1.8.0.3");
        } catch (PackageManager.NameNotFoundException var4) {
            var4.printStackTrace();
        }
        return var2.toString();
    }



    public void resetParams() {
         this.aZ = 1;
       // this.ba = 0;
        //this.bb = 1.0F;
        this.bc = 0L;
        this.bd = 0L;
       // this.bh = null;
        //this.aW = b.a.bo;
        this.mVideoItem = null;
        this.be = null;
        this.accessKey = "";
    }

    public synchronized void initVideoSerial(long var1, long var3, String var5) {
        this.resetParams();
        this.bd = var1;
        this.bc = var3;
        this.be = var5;
    }

    public synchronized void initVideoSerial(long var1, long var3, String var5, int var6) {
        this.resetParams();
        this.bd = var1;
        this.bc = var3;
        this.be = var5;
        this.aZ = var6;
    }

    public synchronized void initVideoSerial(long var1, String var3, String var4) {
        this.resetParams();
        this.bc = var1;//VIDEO_ID
        this.be = var3;//token
        this.accessKey = var4;
    }


    public void setVideoId(long var1, String var3) {
        this.setVideoId(0L, var1, var3, -1);
    }

    public void setVideoId(long var1, String var3, String var4) {
        this.accessKey = var4;
        this.setVideoId(0L, var1, var3);
    }

    public void setVideoId(long var1, String var3, int var4) {
        this.setVideoId(0L, var1, var3, var4);
    }

    public void setVideoId(long var1, long var3, String var5) {
        this.setVideoId(var1, var3, var5, -1);
    }

    public synchronized void setVideoId(long var1, long var3, String var5, int var6) {
        BJLog.i("BJPlayerView", "BJPlayerView setVideoId " + var1 + " " + var3);
        BJFileLog.i(BJPlayerView.class, "BJPlayerView", "BJPlayerView setVideoId " + var1 + " " + var3);

        if(var6 >= 0) {
            BJLog.i("BJPlayerView", "setPlayDefinition " + var6);
            this.initVideoSerial(var1, var3, var5, var6);
        } else {
            this.initVideoSerial(var3, var5, this.accessKey);
        }

    }

    public void getVideoInfo() {
        if(this.mVideoItem != null) {


        } else {
             {
               /* if(this.bc == 0L) {
                    this.aV.onVideoLoaded(new HttpException(PlayerError.VIDEO_ID_NOT_INIT, "视频信息加载失败，请退出重试!"));
                    return;
                }*/

                if(this.bj == null) {
                    throw new IllegalStateException("还未初始化合作方 ID");
                }

                if(this.be == null || this.be.trim().equals("")) {
                    throw new IllegalStateException("视频token不正确(当前值为null或空白字符串)");
                }

                this.bj.a(this.bc, this.bd, this.bf, this.be, this.accessKey, this);

            }

        }
    }

    @Override
    public void onFailure(HttpException var1){}


    @Override
    public void onSuccess(VideoItem var1){
        LogUtils.e("onSuccess", GsonUtil.toJsonStr(var1));
    }
}
