package com.huatu.handheld_huatu.mvpmodel.me;

import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;

import java.util.List;

/**
 * Created by ht on 2016/7/9.
 */
public class RecordDataBean {
    private long cursor;
    private List<RealExamBeans.RealExamBean> resutls;
    private int total;

    public long getCursor() {
        return cursor;
    }

    public void setCursor(long cursor) {
        this.cursor = cursor;
    }

    public List<RealExamBeans.RealExamBean> getResult() {
        return resutls;
    }

    public void setResult(List<RealExamBeans.RealExamBean> result) {
        this.resutls = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "RecordDataBean{" +
                "cursor=" + cursor +
                ", result=" + resutls +
                ", total=" + total +
                '}';
    }
}
