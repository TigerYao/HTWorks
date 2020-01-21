package com.huatu.handheld_huatu.business.essay.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class EssayPaperReport implements Serializable {

    private double avgScore;
    private String correctDate;
    private double examScore;
    private double examScoreChange;
    private double maxScore;
    private double maxScoreChange;
    private int totalRankChange;
    private String paperName;
    private int questionCount;

    private ArrayList<QuestionVO> questionVOList;

    private double score;
    private int spendTime;
    private int totalCount;
    private int totalRank;
    private int unfinishedCount;

    public ArrayList<ReadPaperInfo> remarkList;
    public long audioId;
    public String audioToken;
    public int feedBackStatus;//学员是否评价批注 0 未评价 1 评价
    public String feedBackContent;//学员已经评价的内容
    public int feedBackStar; //学员评价星级
    public int type;
    public int convertCount; //转了多少人工批改

    public double getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(double avgScore) {
        this.avgScore = avgScore;
    }

    public String getCorrectDate() {
        return correctDate;
    }

    public void setCorrectDate(String correctDate) {
        this.correctDate = correctDate;
    }

    public double getExamScore() {
        return examScore;
    }

    public void setExamScore(double examScore) {
        this.examScore = examScore;
    }

    public double getExamScoreChange() {
        return examScoreChange;
    }

    public void setExamScoreChange(double examScoreChange) {
        this.examScoreChange = examScoreChange;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public int getTotalRankChange() {
        return totalRankChange;
    }

    public void setTotalRankChange(int totalRankChange) {
        this.totalRankChange = totalRankChange;
    }

    public double getMaxScoreChange() {
        return maxScoreChange;
    }

    public void setMaxScoreChange(double maxScoreChange) {
        this.maxScoreChange = maxScoreChange;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public ArrayList<QuestionVO> getQuestionVOList() {
        return questionVOList;
    }

    public void setQuestionVOList(ArrayList<QuestionVO> questionVOList) {
        this.questionVOList = questionVOList;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(int spendTime) {
        this.spendTime = spendTime;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalRank() {
        return totalRank;
    }

    public void setTotalRank(int totalRank) {
        this.totalRank = totalRank;
    }

    public int getUnfinishedCount() {
        return unfinishedCount;
    }

    public void setUnfinishedCount(int unfinishedCount) {
        this.unfinishedCount = unfinishedCount;
    }

    public static class QuestionVO implements Serializable{

        private double examScore;
        private int inputWordNum;
        private double score;
        private int sort;
        private int spendTime;
        private int type;
        private String typeName;

        public double getExamScore() {
            return examScore;
        }

        public void setExamScore(double examScore) {
            this.examScore = examScore;
        }

        public int getInputWordNum() {
            return inputWordNum;
        }

        public void setInputWordNum(int inputWordNum) {
            this.inputWordNum = inputWordNum;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public int getSpendTime() {
            return spendTime;
        }

        public void setSpendTime(int spendTime) {
            this.spendTime = spendTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }

    public static class ReadPaperInfo{
        public String content;
        public int sort;
    }
}
