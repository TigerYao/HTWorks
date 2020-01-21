package com.huatu.handheld_huatu.mvpmodel.arena;


import java.io.Serializable;

/**
 * Created by saiyuan on 2016/10/28.
 */
public class ArenaProgressBean implements Serializable {
    public int code;
    public String message;
    public ArenaProgressDataBean data;

    public static class ArenaProgressDataBean implements Serializable {
        public int arenaId;
        public int uid;
        public int practiceId;
        public int acount;
        public int rcount;
    }
}
