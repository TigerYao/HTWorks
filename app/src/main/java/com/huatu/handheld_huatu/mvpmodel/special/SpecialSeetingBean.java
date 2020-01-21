package com.huatu.handheld_huatu.mvpmodel.special;

import java.util.List;

/**
 * Created by ht on 2016/7/16.
 */
public class SpecialSeetingBean {

    private int code;
    private SpecialSeetingDataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SpecialSeetingDataBean getData() {
        return data;
    }

    public void setData(SpecialSeetingDataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SpecialSeetingBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class SpecialSeetingDataBean {
        private long createTime;
        private long id;
        private int number;
        private List<SpecialSeetingPointsBean> points;
        private int questionCount;
        private List<Integer> selects;
        private long userId;

        public SpecialSeetingDataBean(long createTime, long id, int number, List<SpecialSeetingPointsBean> points, int questionCount, List<Integer> selects, long userId) {
            this.createTime = createTime;
            this.id = id;
            this.number = number;
            this.points = points;
            this.questionCount = questionCount;
            this.selects = selects;
            this.userId = userId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public List<SpecialSeetingPointsBean> getPoints() {
            return points;
        }

        public void setPoints(List<SpecialSeetingPointsBean> points) {
            this.points = points;
        }

        public int getQuestionCount() {
            return questionCount;
        }

        public void setQuestionCount(int questionCount) {
            this.questionCount = questionCount;
        }

        public List<Integer> getSelects() {
            return selects;
        }

        public void setSelects(List<Integer> selects) {
            this.selects = selects;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        @Override
        public String toString() {
            return "SpecialSeetingDataBean{" +
                    "createTime=" + createTime +
                    ", id=" + id +
                    ", number=" + number +
                    ", points=" + points +
                    ", questionCount=" + questionCount +
                    ", selects=" + selects +
                    ", userId=" + userId +
                    '}';
        }
    }

    public class SpecialSeetingPointsBean {
        private int pointId;
        private String name;

        public SpecialSeetingPointsBean(int pointId, String name) {
            this.pointId = pointId;
            this.name = name;
        }

        public int getPointId() {
            return pointId;
        }

        public void setPointId(int pointId) {
            this.pointId = pointId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "SpecialSeetingPointsBean{" +
                    "pointId=" + pointId +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
