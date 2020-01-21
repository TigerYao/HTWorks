package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;

/**
 * 进入申论答题，交卷时候校的验答题次数
 */
public class CheckCountInfo implements Serializable {

    public CountInfo intelligence;      // 智能批改状态，次数
    public CountInfo manual;            // 人工批改状态，次数

    public static class CountInfo implements Serializable {
        public String goodsName;
        public int goodsType;
        public int isLimitNum;      // 0、无次数限制 1、有次数限制
        public int num;             // 通用次数
        public int specialNum;      // 专用次数
        public int willExpireNum;
    }
}
