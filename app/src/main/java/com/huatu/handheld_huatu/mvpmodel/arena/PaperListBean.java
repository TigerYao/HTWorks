package com.huatu.handheld_huatu.mvpmodel.arena;


import com.huatu.handheld_huatu.mvpmodel.exercise.PaperMetaBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.UserMetaBean;

import java.util.List;

/**
 * Created by KaelLi on 2016/12/30.
 */
public class PaperListBean {
    public int code;
    public PaperListData data;

    public class PaperListData {
        public List<PaperListModel> resutls;
        public long cursor;
        public int total;
    }

    public class PaperListModel {
        public long id;
        public String name;
        public int year;
        public int area;
        public int time;
        public int score;
        public int passScore;
        public int qcount;
        public double difficulty;
        public int isNew;                        // 0 表示：否，1 表示：是
        public int type;                        // 试卷类型 1：真题 2：模拟题
        public List<PaperListModule> modules;
        public int status;                      // 试卷活动状态：1未开始|2正在进行|3已经结束|4已经下线|5可继续做题|6可查看报告|7未出报告
        public int catgory;
        public List<Integer> questions;
        //用户试卷统计
        public UserMetaBean userMeta;
        //试卷统计信息
        public PaperMetaBean paperMeta;
    }

    public class PaperListModule {
        public int category;
        public String name;
        public int qcount;
    }
}
