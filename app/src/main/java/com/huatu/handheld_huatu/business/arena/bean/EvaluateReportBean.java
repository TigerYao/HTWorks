package com.huatu.handheld_huatu.business.arena.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class EvaluateReportBean implements Serializable {

    private boolean flag;                   // 是否有数据

    private long userId;                    // userId

    private String scoreText;               // 预测分评价
    private double predictedScore;          // 预测分
    private double dif_predictedScore;      // 同比变化 dif_predictedScore
    private double hasBeat;                 // 击败人数百分比
    private int doExerciseNum;              // 做题数量
    private String[] shangAn;               // 上岸正确率和上岸速度
    private double accuracy;                // 正确率
    private double doExerciseSpead;         // 做题速度
    private int rank;                       // 全站排名
    private long doExerciseTime;            // 累计时长
    private int doExerciseDay;              // 练习天数

    private ArrayList<String> textArea;     // 我的数据评价

    private long updateTime;                // 报告时间

    private ArrayList<TopUser> top10User;

    private ArrayList<String> weekTextArea; // 四个评语

    private ArrayList<UserInfo> userInfo;   // 用户头像、昵称、id

    private HashMap<Long, UserInfo> userInfoMap;    // 用户头像、昵称      自定义的数据

    private WeekReport weekReport;          // 周报表数据

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getScoreText() {
        return scoreText;
    }

    public void setScoreText(String scoreText) {
        this.scoreText = scoreText;
    }

    public double getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(double predictedScore) {
        this.predictedScore = predictedScore;
    }

    public double getDif_predictedScore() {
        return dif_predictedScore;
    }

    public void setDif_predictedScore(double dif_predictedScore) {
        this.dif_predictedScore = dif_predictedScore;
    }

    public double getHasBeat() {
        return hasBeat;
    }

    public void setHasBeat(double hasBeat) {
        this.hasBeat = hasBeat;
    }

    public int getDoExerciseNum() {
        return doExerciseNum;
    }

    public void setDoExerciseNum(int doExerciseNum) {
        this.doExerciseNum = doExerciseNum;
    }

    public String[] getShangAn() {
        return shangAn;
    }

    public void setShangAn(String[] shangAn) {
        this.shangAn = shangAn;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getDoExerciseSpead() {
        return doExerciseSpead;
    }

    public void setDoExerciseSpead(double doExerciseSpead) {
        this.doExerciseSpead = doExerciseSpead;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getDoExerciseTime() {
        return doExerciseTime;
    }

    public void setDoExerciseTime(long doExerciseTime) {
        this.doExerciseTime = doExerciseTime;
    }

    public int getDoExerciseDay() {
        return doExerciseDay;
    }

    public void setDoExerciseDay(int doExerciseDay) {
        this.doExerciseDay = doExerciseDay;
    }

    public ArrayList<String> getTextArea() {
        return textArea;
    }

    public void setTextArea(ArrayList<String> textArea) {
        this.textArea = textArea;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public ArrayList<TopUser> getTop10User() {
        return top10User;
    }

    public void setTop10User(ArrayList<TopUser> top10User) {
        this.top10User = top10User;
    }

    public ArrayList<String> getWeekTextArea() {
        return weekTextArea;
    }

    public void setWeekTextArea(ArrayList<String> weekTextArea) {
        this.weekTextArea = weekTextArea;
    }

    public ArrayList<UserInfo> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(ArrayList<UserInfo> userInfo) {
        this.userInfo = userInfo;
    }

    public HashMap<Long, UserInfo> getUserInfoMap() {
        return userInfoMap;
    }

    public void setUserInfoMap(HashMap<Long, UserInfo> userInfoMap) {
        this.userInfoMap = userInfoMap;
    }

    public WeekReport getWeekReport() {
        return weekReport;
    }

    public void setWeekReport(WeekReport weekReport) {
        this.weekReport = weekReport;
    }

    public static class UserInfo implements Serializable{

        private String avatar;
        private String nick;
        private long id;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class WeekReport implements Serializable{

        private ArrayList<String> pointIds;         // 知识点
        private ArrayList<String> pointName;        // 知识点名称

        private String week;                        // 周数据报表时间 2018-50
        private int doExerciseNum;                  // 周做题数量
        private int dif_doExerciseNum;              // 同比变化
        private double ave_doExerciseNum;           // 全站平均
        private ArrayList<Integer> exerciseNum;     // 科目和做题量饼图

        private double accuracy;                    // 周正确率
        private double dif_accuracy;                // 对比上周
        private double ave_accuracy;                // 全站平均
        private ArrayList<Double> accuracies;       // 科目和正确率
        private ArrayList<Double> lineAccuracies;   // 对比科目，上岸正确率 从shangAn中取出来放到这里 自定义数据

        private double speed;                       // 周做题速度
        private double dif_speed;                   // 对比上周
        private double ave_speed;                   // 全站平均
        private ArrayList<Double> speeds;           // 科目和速度
        private ArrayList<Double> lineSpeeds;       // 对比科目，上岸速度List 从shangAn中取出来放到这里 自定义数据

        private int rank;                           // 我的本周排名
        private int lastWeekRank;                   // 我的上周排名
        private double predictedScore;              // 预测分

        public ArrayList<String> getPointIds() {
            return pointIds;
        }

        public void setPointIds(ArrayList<String> pointIds) {
            this.pointIds = pointIds;
        }

        public ArrayList<String> getPointName() {
            return pointName;
        }

        public void setPointName(ArrayList<String> pointName) {
            this.pointName = pointName;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public int getDoExerciseNum() {
            return doExerciseNum;
        }

        public void setDoExerciseNum(int doExerciseNum) {
            this.doExerciseNum = doExerciseNum;
        }

        public int getDif_doExerciseNum() {
            return dif_doExerciseNum;
        }

        public void setDif_doExerciseNum(int dif_doExerciseNum) {
            this.dif_doExerciseNum = dif_doExerciseNum;
        }

        public double getAve_doExerciseNum() {
            return ave_doExerciseNum;
        }

        public void setAve_doExerciseNum(double ave_doExerciseNum) {
            this.ave_doExerciseNum = ave_doExerciseNum;
        }

        public ArrayList<Integer> getExerciseNum() {
            return exerciseNum;
        }

        public void setExerciseNum(ArrayList<Integer> exerciseNum) {
            this.exerciseNum = exerciseNum;
        }

        public double getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(double accuracy) {
            this.accuracy = accuracy;
        }

        public double getDif_accuracy() {
            return dif_accuracy;
        }

        public void setDif_accuracy(double dif_accuracy) {
            this.dif_accuracy = dif_accuracy;
        }

        public double getAve_accuracy() {
            return ave_accuracy;
        }

        public void setAve_accuracy(double ave_accuracy) {
            this.ave_accuracy = ave_accuracy;
        }

        public ArrayList<Double> getAccuracies() {
            return accuracies;
        }

        public void setAccuracies(ArrayList<Double> accuracies) {
            this.accuracies = accuracies;
        }

        public ArrayList<Double> getLineAccuracies() {
            return lineAccuracies;
        }

        public void setLineAccuracies(ArrayList<Double> lineAccuracies) {
            this.lineAccuracies = lineAccuracies;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public double getDif_speed() {
            return dif_speed;
        }

        public void setDif_speed(double dif_speed) {
            this.dif_speed = dif_speed;
        }

        public double getAve_speed() {
            return ave_speed;
        }

        public void setAve_speed(double ave_speed) {
            this.ave_speed = ave_speed;
        }

        public ArrayList<Double> getSpeeds() {
            return speeds;
        }

        public void setSpeeds(ArrayList<Double> speeds) {
            this.speeds = speeds;
        }

        public ArrayList<Double> getLineSpeeds() {
            return lineSpeeds;
        }

        public void setLineSpeeds(ArrayList<Double> lineSpeeds) {
            this.lineSpeeds = lineSpeeds;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getLastWeekRank() {
            return lastWeekRank;
        }

        public void setLastWeekRank(int lastWeekRank) {
            this.lastWeekRank = lastWeekRank;
        }

        public double getPredictedScore() {
            return predictedScore;
        }

        public void setPredictedScore(double predictedScore) {
            this.predictedScore = predictedScore;
        }
    }

    public static class TopUser implements Serializable{

        private long userId;            // 用户Id
        private int nowWeekRank;        // 用户排行
        private double prediceScore;    // 预测分

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public int getNowWeekRank() {
            return nowWeekRank;
        }

        public void setNowWeekRank(int nowWeekRank) {
            this.nowWeekRank = nowWeekRank;
        }

        public double getPrediceScore() {
            return prediceScore;
        }

        public void setPrediceScore(double prediceScore) {
            this.prediceScore = prediceScore;
        }
    }
}
