package com.huatu.handheld_huatu.base;

/**
 * Created by saiyuan on 2016/11/21.
 */

public class ApiException extends Exception {
    private int errorCode;
    private String errorMsg;

    public ApiException(int code, String msg) {
        super(msg);
        this.errorCode = code;
        errorMsg = msg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
