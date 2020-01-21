package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;
import java.util.List;

/**
 * 模考大赛用户排名分数信息,只存在于模考大赛
 */
public class ScMatchMetaBean implements Serializable{

    /**
     * positionId : 1212
     * positionName : 职位名称123
     * scoreLine : {"categories":["全站平均得分","模考得分"],"series":[{"name":"7月1日","data":[40,30]},{"name":"8月1日",
     * "data":[55,45]},{"name":"9月1日","data":[60,65]}]}
     * positionRank : 10
     * positionCount : 50
     * positionAverage : 50
     * positionMax : 85
     * positionBeatRate : 80
     *
     *
     * .positionId	int	否	职位id
     .positionName	string	否	职位名称
     .positionRank	int	否	职位排名
     .positionCount	int	否	职位报名人数
     .positionAverage	double	否	职位平均分
     .positionMax	double	否	职位最高分
     .Line	复合	否	分数折线,结构与评估报告相同
     */

    private int positionId;
    private String positionName;
    private ScoreLineEntity scoreLine;
    private int positionRank;
    private int positionCount;
    private double positionAverage;         // 新增职位平均分
    private String positionAverageStr;      // 新增职位平均分（字符串）
    private double positionMax;             // 新增职位最高分
    private String positionMaxStr;          // 新增职位最高分（字符串）
    private int positionBeatRate;

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public ScoreLineEntity getScoreLine() {
        return scoreLine;
    }

    public void setScoreLine(ScoreLineEntity scoreLine) {
        this.scoreLine = scoreLine;
    }

    public int getPositionRank() {
        return positionRank;
    }

    public void setPositionRank(int positionRank) {
        this.positionRank = positionRank;
    }

    public int getPositionCount() {
        return positionCount;
    }

    public void setPositionMax(int positionMax) {
        this.positionMax = positionMax;
    }

    public int getPositionBeatRate() {
        return positionBeatRate;
    }

    public void setPositionBeatRate(int positionBeatRate) {
        this.positionBeatRate = positionBeatRate;
    }

    public static class ScoreLineEntity implements Serializable{
        private List<String> categories;
        /**
         * name : 7月1日
         * data : [40,30]
         */

        private List<SeriesEntity> series;

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public List<SeriesEntity> getSeries() {
            return series;
        }

        public void setSeries(List<SeriesEntity> series) {
            this.series = series;
        }

        public static class SeriesEntity implements Serializable {
            private String name;
            private List<Float> data;
            private List<String> strData;       // 字符串数据（暂不用）

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<Float> getData() {
                return data;
            }

            public void setData(List<Float> data) {
                this.data = data;
            }
        }
    }
}
