package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by xing on 2018/4/3.
 */


public enum  DownLoadStatusEnum {


    INIT(-2),      //只插入数据库成功,task没建
    START(1),     //开始  loading
    PREPARE(-1),  //准备
    FINISHED(2),  //完成
    STOP(4),      //停止
    ERROR(3) ;    //出错

    private final int value;
    private DownLoadStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DownLoadStatusEnum valueOf(int status){

        switch (status){

            case 1:
                return DownLoadStatusEnum.START;
            case -1:
                return DownLoadStatusEnum.PREPARE;
            case 2:
                return DownLoadStatusEnum.FINISHED;
            case 4:
                return DownLoadStatusEnum.STOP;
            case 3:
                return DownLoadStatusEnum.ERROR;
             case -2:
            default:
                return DownLoadStatusEnum.INIT;


        }
    }
}