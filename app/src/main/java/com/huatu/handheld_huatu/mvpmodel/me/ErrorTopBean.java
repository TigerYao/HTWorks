package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * 错题本头的雷达图数据
 */
public class ErrorTopBean {

    private int error;              // 错题数量
    private double accuracy;        // 正确率
    private int num;                // 数量
    private String id;              // 知识点
    private String name;            // 知识点

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
