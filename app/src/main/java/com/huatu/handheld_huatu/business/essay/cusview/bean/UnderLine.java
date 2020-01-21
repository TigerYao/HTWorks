package com.huatu.handheld_huatu.business.essay.cusview.bean;

/**
 * Created by michael on 18/4/16.
 */

public  class UnderLine{
   public int start;
    public int end;
    public boolean isMult;
    public String score;
    public String addscore;
    public int seq;  //采用类view测量，高位存类型，低位存真实的序号
    @Override
    public String toString() {
        return "UnderLine{" +
                "start=" + start +
                ", end=" + end +
                ", isMult=" + isMult +
                ", score='" + score + '\'' +
                ", addscore='" + addscore + '\'' +
                '}';
    }
}
