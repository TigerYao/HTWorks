package com.huatu.handheld_huatu.mvpmodel.arena;

import com.huatu.handheld_huatu.mvpmodel.exercise.UserMetaBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saiyuan on 2016/12/16.
 */

public class ExamPagerItem implements Serializable {
    public long cursor;
    public List<ExamPaperResult> resutls;
    public int total;

    @Override
    public String toString() {
        return "ExamPaperData{" +
                "cursor=" + cursor +
                ", resutls=" + resutls +
                ", total=" + total +
                '}';
    }

    public class ExamPaperResult implements Serializable {
        public int area;
        public double difficulty;                       // 难度系数
        public int id;                                  // 试卷id
        public List<ExamPaperModule> modules;
        public String name;
        public int passScore;                           // 及格线
        public int qcount;                              // 试题个数
        public int isNew;                               // 0否，1是
        public List<Integer> questions;
        public int score;
        public int status;
        public int subject;
        public int time;                                // 考试时间
        public int type;                                // 试卷类型
        public UserMetaBean userMeta;
        public int year;

        // 本地使用变量
        public boolean isDownloadPaper;                 // 试卷是否已经下载，仅用于判断本地
        public boolean hasNew;                          // 试卷是否有新版本

        @Override
        public String toString() {
            return "ExamPaperResult{" +
                    "area=" + area +
                    ", difficulty=" + difficulty +
                    ", id=" + id +
                    ", modules=" + modules +
                    ", name='" + name + '\'' +
                    ", passScore=" + passScore +
                    ", qcount=" + qcount +
                    ", questions=" + questions +
                    ", score=" + score +
                    ", status=" + status +
                    ", subject=" + subject +
                    ", time=" + time +
                    ", type=" + type +
                    ", userMeta=" + userMeta +
                    ", year=" + year +
                    '}';
        }
    }

    public class ExamPaperModule implements Serializable {
        public int category;
        public String name;
        public int qcount;

        @Override
        public String toString() {
            return "ExamPaperModule{" +
                    "category=" + category +
                    ", name='" + name + '\'' +
                    ", qcount=" + qcount +
                    '}';
        }
    }
}
