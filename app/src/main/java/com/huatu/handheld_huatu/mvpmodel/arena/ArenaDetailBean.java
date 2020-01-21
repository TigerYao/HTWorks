package com.huatu.handheld_huatu.mvpmodel.arena;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saiyuan on 2016/10/24.
 */
public class ArenaDetailBean implements Serializable {
    public int code;
    public String message;
    public long arenaId;
    public int limitTime;               // 比赛限时,单位:秒
    public int qcount;                  // 题量
    public int status;                  // 房间状态(1:已创建 2:进行中 3:已结束)
    public List<Long> playerIds;        // 参加人员id列表
    public List<Player> players;        // 参加人员详情列表
    public List<Long> practices;        // 参加人员对应的练习id
    public int id;                      // 房间号
    public int type;                    // 房间类型(1:智能推送 2:指定知识点)
    public int moduleId;
    public String module;               // 考试模块
    public String name;                 // 房间名称
    public long winner;                 // 胜者id(前端在竞技历史界面展示胜负时，根据userId匹配，如当前userId与winner相同，则表示该参赛者为该房间的胜者)
    public long createTime;             // 创建时间(交卷时间)
    public List<Result> results;
    public long practiceId;             // 练习ID

    public static class Player implements Serializable {
        public Player(long id) {
            uid = id;
        }

        public Player(AthleticPlayer player) {
            uid = player.uid;
            nick = player.nick;
            avatar = player.avatar;
        }

        public long uid;        // 用户id
        public String nick;     // 昵称
        public String avatar;   // 头像地址
        public ArenaProgressBean.ArenaProgressDataBean progressDataBean;

        @Override
        public String toString() {
            return "Player{" +
                    "uid=" + uid +
                    ", nick=" + nick +
                    "}";
        }
    }

    public static class Result implements Serializable {
        public long uid;            // 用户id
        public int rcount;          // 做对数量
        public int elapsedTime;     // 耗时
        public Player userInfo;

        @Override
        public String toString() {
            return "Result{" +
                    "uid=" + uid +
                    ", rcount=" + rcount +
                    ", elapsedTime=" + elapsedTime +
                    "}";
        }
    }

    @Override
    public String toString() {
        return "ArenaDetailBean{" +
                "arenaId=" + arenaId +
                ", status=" + status +
                ", players=" + players +
                ", practices=" + practices +
                ", winner=" + winner +
                ", results=" + results +
                ", practiceId=" + practiceId +
                '}';
    }
}
