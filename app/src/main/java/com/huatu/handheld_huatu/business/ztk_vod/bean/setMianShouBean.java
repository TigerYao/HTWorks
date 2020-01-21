package com.huatu.handheld_huatu.business.ztk_vod.bean;

/**
 * Created by ht-ldc on 2017/12/14.
 */

public class setMianShouBean {

    /**
     * code : 1000000
     * data : {"departCode":"035","department":"中国民航空中警察总队系统","mianshiScore":"51.7","position":"科员","positionCode":"0723002001","tips":"恭喜！您已获得本课程报名资格，请确认您的职位信息。"}
     */

    private int code;
    /**
     * departCode : 035
     * department : 中国民航空中警察总队系统
     * mianshiScore : 51.7
     * position : 科员
     * positionCode : 0723002001
     * tips : 恭喜！您已获得本课程报名资格，请确认您的职位信息。
     */

    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String departCode;
        private String department;
        private String mianshiScore;
        private String position;
        private String positionCode;
        private String tips;

        public String getDepartCode() {
            return departCode;
        }

        public void setDepartCode(String departCode) {
            this.departCode = departCode;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getMianshiScore() {
            return mianshiScore;
        }

        public void setMianshiScore(String mianshiScore) {
            this.mianshiScore = mianshiScore;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getPositionCode() {
            return positionCode;
        }

        public void setPositionCode(String positionCode) {
            this.positionCode = positionCode;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }
    }
}
