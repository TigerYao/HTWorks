package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by xing on 2018/3/30.
 */


public enum CourseTypeEnum {
    LIVEVIDEO(1),//直播
    RECORDING(0);//录播
    private final int value;

    /**
     * 构造器默认也只能是private, 从而保证构造函数只能在内部使用
     *
     */
    private CourseTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}