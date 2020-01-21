package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;

/**
 * Created by djd
 */
public class RecordCourseDetailBean implements Serializable {
    private String Vc_id;// 课程ID
    private String Vc_name;// 课程名称
    private String Vc_price;// 课程价格
    private String Vodid;// 课件ID
    private String Voname;// 课件名称
    private String cc_uid;// cc账号uid
    private String api_key;// cc账号apikey
    private String flag;// 是否免费 1免费 0付费
    private String playtime;// 播放时长
    private int sort;// 排序字段

    public String getVc_id() {
        return Vc_id;
    }

    public void setVc_id(String vc_id) {
        Vc_id = vc_id;
    }

    public String getVc_name() {
        return Vc_name;
    }

    public void setVc_name(String vc_name) {
        Vc_name = vc_name;
    }

    public String getVc_price() {
        return Vc_price;
    }

    public void setVc_price(String vc_price) {
        Vc_price = vc_price;
    }

    public String getVodid() {
        return Vodid;
    }

    public void setVodid(String vodid) {
        Vodid = vodid;
    }

    public String getVoname() {
        return Voname;
    }

    public void setVoname(String voname) {
        Voname = voname;
    }

    public String getCc_uid() {
        return cc_uid;
    }

    public void setCc_uid(String cc_uid) {
        this.cc_uid = cc_uid;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getPlaytime() {
        return playtime;
    }

    public void setPlaytime(String playtime) {
        this.playtime = playtime;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "RecordCourseDetailBean{" +
                "Vc_id='" + Vc_id + '\'' +
                ", Vc_name='" + Vc_name + '\'' +
                ", Vc_price='" + Vc_price + '\'' +
                ", Vodid='" + Vodid + '\'' +
                ", Voname='" + Voname + '\'' +
                ", cc_uid='" + cc_uid + '\'' +
                ", api_key='" + api_key + '\'' +
                ", flag='" + flag + '\'' +
                ", playtime='" + playtime + '\'' +
                ", sort=" + sort +
                '}';
    }
}
