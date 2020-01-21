package com.huatu.test.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019\7\24 0024.
 */


public   class ScoreListEntity implements Serializable {
    /**
     * score : 40301
     * scorePoint : 测试内容c6c1
     * sequenceNumber : 10813
     */

    public double score;                        // 得分
    public String scorePoint;                   // 加、减分点
    public int sequenceNumber;                  // 序号
    public int type;                            // 1、内容得分 2、格式得分 3、减分 4、其他得分

    @Override
    public String toString() {
        return "ScoreListEntity{" +
                "score=" + score +
                ", scorePoint='" + scorePoint + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", type=" + type +
                '}';
    }
}