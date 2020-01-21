package com.huatu.handheld_huatu.event;

/**
 * Created by KaelLi on 2016/7/25.
 */
public class MessageEvent {
    /**
     * 0 更改字体
     * 1 更改日夜模式
     * 2 草稿纸可撤销
     * 3 草稿纸可恢复
     * 4 草稿纸不可撤销
     * 5 草稿纸不可恢复
     * 6 草稿纸垃圾桶点亮
     * 7 草稿纸垃圾桶默认
     * 8 该试题已经收藏
     * 9 该试题没有收藏
     */
    public final int message;

    public final static int HOME_FRAGMENT_MSG_GET_DAILYINFO = 100002;                                   // 获取每日训练数据
    public final static int HOME_FRAGMENT_MSG_START_NTEGRATED_APPLICATION = 100003;                     // 打开 综合应用
    public final static int HOME_FRAGMENT_MSG_TYPE_CHANGE_UPDATE_VIEW = 100004;                         // 更新view
    public final static int COURSE_BUY_SUCCESS = 100007;
    public final static int HOME_FRAGMENT_MSG_TYPE_REFRESH_TITLE = 100008;
    public final static int HOME_FRAGMENT_CHANGE_TO_ESSAY = HOME_FRAGMENT_MSG_TYPE_REFRESH_TITLE + 1;   // 跳申论
    public final static int HOME_FRAGMENT_NO_LOOPER = HOME_FRAGMENT_CHANGE_TO_ESSAY + 1;                // 没有轮播图
    public final static int HOME_FRAGMENT_HAS_LOOPER = HOME_FRAGMENT_NO_LOOPER + 1;                     // 有轮播图
    public final static int HOME_FRAGMENT_MSG_TYPE_CHANGE_SHOW_ARENA = HOME_FRAGMENT_HAS_LOOPER + 1;    // 申论下点击行测，TikuFragment显示题库

    public final static int HOME_FRAGMENT_MSG_TYPE_TREE_DATA_UPDATE_VIEW_REFRESH_ALL = HOME_FRAGMENT_MSG_TYPE_CHANGE_SHOW_ARENA + 1;    // 首页题库树，全部刷新，保持展开结构

    public MessageEvent(int message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
