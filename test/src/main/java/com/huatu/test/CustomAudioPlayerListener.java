package com.huatu.test;

public interface CustomAudioPlayerListener {
    void onPrepared();
    void onCompletion();
    void updateBufferProgress(int percent);
    void onSeekComplete();
    void onError(int code, String msg);
    void onLoading(boolean isLoading);
    void updatePostion(int duration, int position);
}
