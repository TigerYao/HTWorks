package com.huatu.handheld_huatu.mvpmodel.special;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ht on 2016/7/16.
 */
public class DailySpecialBean implements Serializable {
        private int allCount;
        private long createTime;
        private int finishCount;
        private String id;
        private List<DailySpecialPoints> points;
        private int questionCount;

        @Override
        public String toString() {
            return "DailySpecialData{" +
                    "allCount=" + allCount +
                    ", createTime=" + createTime +
                    ", finishCount=" + finishCount +
                    ", id='" + id + '\'' +
                    ", points=" + points +
                    ", questionCount=" + questionCount +
                    '}';
        }

        public int getAllCount() {
            return allCount;
        }

        public void setAllCount(int allCount) {
            this.allCount = allCount;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getFinishCount() {
            return finishCount;
        }

        public void setFinishCount(int finishCount) {
            this.finishCount = finishCount;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<DailySpecialPoints> getPoints() {
            return points;
        }

        public void setPoints(List<DailySpecialPoints> points) {
            this.points = points;
        }

        public int getQuestionCount() {
            return questionCount;
        }

        public void setQuestionCount(int questionCount) {
            this.questionCount = questionCount;
        }


    public class DailySpecialPoints implements Serializable {
        private String name;
        private long practiceId;
        private int questionPointId;
        private int status;

        @Override
        public String toString() {
            return "DailySpecialPoints{" +
                    "name='" + name + '\'' +
                    ", practiceId=" + practiceId +
                    ", questionPointId=" + questionPointId +
                    ", status=" + status +
                    '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getPracticeId() {
            return practiceId;
        }

        public void setPracticeId(long practiceId) {
            this.practiceId = practiceId;
        }

        public int getQuestionPointId() {
            return questionPointId;
        }

        public void setQuestionPointId(int questionPointId) {
            this.questionPointId = questionPointId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
