package com.huatu.handheld_huatu.mvpmodel;

/**
 * Created by Administrator on 2019\9\3 0003.
 * 申论答题卡枚举状态
 */

public enum EssayAfterStatusEnum {

    UNFINISHED(1),        //, "未完成"
    COMMIT(2),           //, "已交卷"
    CORRECT(3),          //, "已批改"
    INIT(0),             //, "空白"
    CORRECTING(4),       //,"批改中"
    CORRECT_RETURN(5);   //,"被退回"

    private final int value;
    private EssayAfterStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
