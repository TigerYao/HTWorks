package com.huatu.handheld_huatu.mvpmodel.me;

import java.util.List;

/**
 * Created by ht on 2016/7/9.
 */
public class TreeBasic {

    private int code;
    private List<TreeViewBean> data;

    @Override
    public String toString() {
        return "TreeBasic{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<TreeViewBean> getData() {
        return data;
    }

    public void setData(List<TreeViewBean> data) {
        this.data = data;
    }
}
