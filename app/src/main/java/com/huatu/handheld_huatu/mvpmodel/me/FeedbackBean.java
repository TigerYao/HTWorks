package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * 意见反馈 提交数据
 */
public class FeedbackBean {
    private String content;
    private String contacts;
    private String imgs;
    private long createTime;
    private int type;
    private String log;

    public FeedbackBean(String content, String contacts,int type,String imgs) {
        this.content = content;
        this.contacts = contacts;
        this.type = type;
        this.imgs=imgs;
        this.createTime=System.currentTimeMillis();
    }

    public FeedbackBean(String content, String contacts,int type) {
        this.content = content;
        this.contacts = contacts;
        this.type = type;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
}
