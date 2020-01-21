package com.huatu.handheld_huatu.mvpmodel;

import java.io.Serializable;

/**
 * Created by dongd on 2016/10/31.
 */

public class ShareInfo implements Serializable {
    public String id;
    public String title;
    public String desc;
    public String url;

    public String imgUrl;//需要自已赋值
    public int imgResource;

    public VideoInfo videoInfo;//多媒体信息

    public class VideoInfo implements Serializable {
        public String thumbnail;//缩略图
        public String videoUrl;//音视频地址
    }
}
