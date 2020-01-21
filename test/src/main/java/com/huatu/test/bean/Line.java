package com.huatu.test.bean;

/**
 * Created by Administrator on 2019\7\9 0009.
 */


public class Line {
    public  int start;
    public  int end;
    public Tag tag;

    @Override
    public String toString() {
        return "Line{" +
                "start=" + start +
                ", end=" + end +
                ", tag=" + tag +
                '}';
    }
}
