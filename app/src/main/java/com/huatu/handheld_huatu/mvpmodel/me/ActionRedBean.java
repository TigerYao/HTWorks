package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * Created by ht on 2016/7/14.
 */
public class ActionRedBean {
    public int code;
    public ActionRedData data;


    @Override
    public String toString() {
        return "ActionRedBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    class ActionRedData {
        //未读消息的个数
        public int unreadMsgCount;
        //未读活动的个数
        public int unreadActCount;


        @Override
        public String toString() {
            return "ActionRedData{" +
                    "unreadMsgCount=" + unreadMsgCount +
                    ", unreadActCount=" + unreadActCount +
                    '}';
        }
    }

}
