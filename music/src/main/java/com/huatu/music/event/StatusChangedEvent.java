package com.huatu.music.event;

/**
 * Created by Administrator on 2019\8\20 0020.
 */

public class StatusChangedEvent {
    public  boolean misPrepared,misPlaying;
    public  int fromWating;    //0 默认值  1，正在缓冲，2结束缓冲
    public StatusChangedEvent(boolean isPrepared,boolean isPlaying){
        misPrepared=isPrepared;
        misPlaying=isPlaying;
    };
    public StatusChangedEvent(boolean isPrepared,boolean isPlaying,int isfromStop){
        misPrepared=isPrepared;
        misPlaying=isPlaying;
        fromWating=isfromStop;
    }
}
