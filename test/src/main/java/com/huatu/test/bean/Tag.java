package com.huatu.test.bean;

/**
 * Created by Administrator on 2019\7\9 0009.
 */


public class Tag {
    public int len;
    public int size;
    public String content;

    @Override
    public String toString() {
        return "Tag{" +
                "len=" + len +
                ", size=" + size +
                ", content='" + content + '\'' +
                '}';
    }
}
