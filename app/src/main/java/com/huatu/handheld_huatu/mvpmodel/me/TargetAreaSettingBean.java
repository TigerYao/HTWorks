package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * 选择目标考试区域后，提交服务器后返回的bean
 */
public class TargetAreaSettingBean {
    public long id;
    public String email;
    public String mobile;
    public String nick;
    public String expireTime;
    public String token;
    public int subject;
    public int area;

    @Override
    public String toString() {
        return "SettingData{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", nick='" + nick + '\'' +
                ", expireTime='" + expireTime + '\'' +
                ", token='" + token + '\'' +
                ", subject=" + subject +
                ", area=" + area +
                '}';
    }
}
