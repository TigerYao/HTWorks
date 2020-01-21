package com.huatu.handheld_huatu.mvpmodel.essay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PaperAnsCacheBean implements Serializable {


    public List<ItemAnsBean> itemAnsBeanS=new ArrayList<>();
    public boolean isCharOver;
    public int     timeSecAll;

    public static class ItemAnsBean implements Serializable{

        public int     timeSec;
        public boolean isExp=true;
        public String  ansStr;

    }


}
