package com.huatu.test;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * 自定义播放器
 */
public abstract class CustomAudioInterface {

    public HandlerThread mPlayerHandlerThread;
    public Handler mPlayerHandler;
    public Handler mMainHandler;

    public abstract void start();

    public abstract void prepare();

    public abstract void pause();

    public abstract boolean isPlaying();

    public abstract void seekTo(long time);

    public abstract void release();

    public abstract long getCurrentPosition();

    public abstract long getDuration();

    public abstract void setVolume(float leftVolume, float rightVolume);

    public abstract void setSpeed(float speed);

}