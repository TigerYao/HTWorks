package com.huatu.handheld_huatu.mvpmodel.arena;

import java.util.ArrayList;

/**
 * 错题导出返回
 */
public class ExportErrBean {

    public ArrayList<ExportPaperBean> list;
    public int payStatus;               // -4、图币不足

    public static class ExportPaperBean {
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

    }
}
