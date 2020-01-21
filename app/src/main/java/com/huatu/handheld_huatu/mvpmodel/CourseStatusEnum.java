package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by xing on 2018/4/2.
 */
//  ////1:直播中  0:直播已结束  -1:直播未开始  -2:录播   2:直播回放
public enum  CourseStatusEnum {
    PLAYING(1),//直播
    END(0),//录播
    NOTBEGIN(-1) ,
    RECORDING(-2),
    PLAYBACK(2) ;
    private final int value;

    /**
     * 构造器默认也只能是private, 从而保证构造函数只能在内部使用
     *
     */
    private CourseStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
