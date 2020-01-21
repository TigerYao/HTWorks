package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 我的 -> 批改次数列表
 */
public class CheckCountBean implements Serializable {

    /**
     * id : 1
     * totalNum : 5
     * type : 0  0单题批改 1多题批改 2文章写作批改
     * usefulNum : 5
     */

    public int id;
    public int totalNum;
    public int correctSum;
    public int type;
    public int usefulNum;

    public int singleNum;
    public int multiNum;
    public int argumentationNum;


    public ArrayList<CheckCountDataBean> machineCorrect;
    public ArrayList<CheckCountDataBean> manualCorrect;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUsefulNum() {
        return usefulNum;
    }

    public void setUsefulNum(int usefulNum) {
        this.usefulNum = usefulNum;
    }
}
