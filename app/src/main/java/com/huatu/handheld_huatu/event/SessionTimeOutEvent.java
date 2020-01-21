package com.huatu.handheld_huatu.event;

/**
 * Created by Administrator on 2016/8/25.
 */
public class SessionTimeOutEvent {

    public SessionTimeOutEvent(int status) {
        this.status = status;
    }

    public SessionTimeOutEvent(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public int status;
    public int id;
}
