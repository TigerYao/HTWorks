package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;


public class PaperCommitReturnBean implements Serializable {


    /**
     * code : 200
     * msg :
     */

    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
