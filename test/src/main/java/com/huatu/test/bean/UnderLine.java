package com.huatu.test.bean;

/**
 * Created by michael on 18/4/16.
 */

public  class UnderLine{
   public int start;
    public int end;
    public boolean isMult;
    public String score;
    public String addscore;
    public int seq;

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
