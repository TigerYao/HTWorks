package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by Administrator on 2016/8/2.
 */
public class BaseResponse   implements Response{
    //public int status;
    //public int res;
    public String message;
    public int code;


    //子类通过复写这个方法来兼容教育的响应码
    public int getCode(){
        return code;
    }
}
