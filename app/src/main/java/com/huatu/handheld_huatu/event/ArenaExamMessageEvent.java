package com.huatu.handheld_huatu.event;

import android.os.Bundle;

/**
 * Created by saiyuan on 2016/10/18.
 */
public class ArenaExamMessageEvent {
    public String tag;
    public int type;
    public Bundle extraBundle;

    public static final int ARENA_MESSAGE_TYPE_CHANGE_QUESTION = 1;

    public static final int ARENA_MESSAGE_TYPE_SHARE = 204;                             // 行测分享
    public static final int ARENA_MESSAGE_TYPE_SHARE_WAY = 206;                         // 行测报告分享渠道回传

    public static final int ARENA_MESSAGE_TYPE_START_EXAM = 301;                        // 模考开始考试
    public static final int ARENA_MESSAGE_TYPE_NOT_START_EXAM = 302;                    // 模考还没开始考试，显示五分钟倒计时

    public static final int ARENA_MESSAGE_TYPE_EXAM_COMMIT_PAPER_SUCCESS = 502;         // 行测交卷成功
    public static final int ARENA_MESSAGE_TYPE_EXAM_SAVE_PAPER_SUCCESS = 503;           // 行测做题过程中点击返回保存成功

    public static final int ARENA_MESSAGE_TYPE_EXAM_FINISH = 601;

    public static final int LARGE_IMG_SHOW_FINISH = 701;                                // 图片预览消失

    public static final int REFRESH_REPORT = 801;                                       // 刷新报告

    public ArenaExamMessageEvent(int type) {
        this.type = type;
    }
}
