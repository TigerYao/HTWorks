package com.huatu.handheld_huatu.base;


import java.io.Serializable;

/**
 * Created by saiyuan on 2016/10/13.
 */
public class BaseResponseModel<T> implements Serializable{
    public int code;
    public String message;
    public T data;
}
