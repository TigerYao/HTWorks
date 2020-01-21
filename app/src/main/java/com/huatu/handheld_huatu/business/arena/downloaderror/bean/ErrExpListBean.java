package com.huatu.handheld_huatu.business.arena.downloaderror.bean;

import java.util.ArrayList;

/**
 * 错题导出本地试卷类
 */
public class ErrExpListBean {

    public int next;
    public ArrayList<ErrExpBean> result;
    public int size;
    public int total;
    public int totalPage;

    public static class ErrExpBean {

        public long answerId;           // 答题卡Id
        public int bizStatus;           // 0初始化1生成答题卡2生成文件3下载失败
        public long fileSize;           // 文件大小
        public String size;             // 文件大小
        public String fileUrl;          // 文件下载地址
        public long gmtCreate;         // 任务创建时间
        public String gmtModify;        // 任务更新时间
        public long id;                 // 任务id
        public long modifierId;
        public String name;             // 名称
        public int num;                 // 预计下载量
        public long orderNum;           // 金币支出低订单编号
        public String questionIds;      // 问题Ids
        public int status;
        public int subject;             // 科目
        public int sum;                 // 实际下载题量
        public int total;               // 消耗金币量
        public long userId;             // 用户Id

        // ------- 自定义属性，用于方便操作
        public boolean isDownLoad;      // 0、否 1、是
        public short isNew = 1;         // 0、已下载 1、未下载
        public String path;             // 本地文件位置

        public boolean isChecked;       // 是否选择了
    }
}
