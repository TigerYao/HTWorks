package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by xing on 2018/4/3.
 *
 *  之前定义的类型 用来数据库存储，现在类型为  ;
 *  //1点播2直播3直播回放	number	@mock=0  添加类型4为离线展示
 *
 */


public enum PlayerTypeEnum {

    Gensee(0),      //展示互动
    BaiJia(1),      //百家回放
    BjRecord(3),    //百家录播
    BjAudio(5),     //百家音频课程
    CCPlay(2);     //录播cc类型，虽然现在没有了


    private final int value;
    private PlayerTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}