package com.huatu.handheld_huatu.business.play.event;

import com.huatu.handheld_huatu.business.play.bean.CourseDetailBean;
import com.huatu.handheld_huatu.business.play.bean.VideoPlayVideoInfoBean;

public class VideoInfoMessageEvent {
    public static final int VideoInfo_VideoPlayVideoInfoBean  = 9988;
    public static final int VideoInfo_CourseDetailBean = 8899;
    public static final int VideoInfo_CourseActDetailDetailBean = 6666;
    public static VideoPlayVideoInfoBean mVideoPlayVideoInfoBean;
    public static CourseDetailBean mCourseDetailBean;
    public Object mObj;
    public int type;

    public VideoInfoMessageEvent(int type) {
       this.type = type;
    }
}
