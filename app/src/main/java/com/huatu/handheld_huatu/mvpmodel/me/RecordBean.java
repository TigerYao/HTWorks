package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * Created by ht on 2016/7/9.
 */
public class RecordBean {
    private int code;
    private RecordDataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public RecordDataBean getData() {
        return data;
    }

    public void setData(RecordDataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RecordBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
