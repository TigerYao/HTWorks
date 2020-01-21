package com.huatu.handheld_huatu.business.play.event;

import com.huatu.handheld_huatu.business.play.bean.VideoPlayVideoInfoBean;

/**
 * Created by saiyuan on 2018/7/6.
 */

public class VideoPlayVideoInfoEvent {
    public String which;
    public VideoPlayVideoInfoBean videoInfoBean;
    public VideoPlayVideoInfoEvent(String fromPage) {
        which = fromPage;
    }
}
