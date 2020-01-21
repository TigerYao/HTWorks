package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;

/**
 * 用户试卷统计
 * Created by ljzyuhenda on 16/7/19.
 */

public class UserMetaBean implements Serializable {
    //meta的id
    public String id;
    //用户未昨晚试卷练习id，-1:试卷已做完 >0:未做完练习id
    public long currentPracticeId;
    //用户完成该试卷次数
    public int finishCount;
    //用户已完成,取第一个
    public long[] practiceIds;
    //该试卷的id
    public int paperId;
    //用户id
    public long uid;

    @Override
    public String toString() {
        return "UserMeta{" +
                "currentPracticeId=" + currentPracticeId +
                ", finishCount=" + finishCount +
                ", id='" + id + '\'' +
                ", paperId=" + paperId +
                ", practiceIds=" + practiceIds +
                ", uid=" + uid +
                '}';
    }
}
