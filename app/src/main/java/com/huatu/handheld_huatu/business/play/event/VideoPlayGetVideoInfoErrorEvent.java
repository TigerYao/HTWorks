package com.huatu.handheld_huatu.business.play.event;

/**
 * Created by saiyuan on 2018/7/6.
 */

public class VideoPlayGetVideoInfoErrorEvent {
    public String which;
    public int errCode;
    public String errMsg;
    public VideoPlayGetVideoInfoErrorEvent(String from) {
        which = from;
    }
}
