package com.huatu.handheld_huatu.mvpmodel.exercise;

import java.io.Serializable;
import java.util.List;

public class TimeBean implements Serializable {
    public long code;
    public List<Data> data;

    @Override
    public String toString() {
        return "TimeBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    //试题做题信息,
    // answers 数组 用户对该道题的答题记录,按照时间正序排列,如:[1,2,3] 表示用户做了3次,答案分别选择了A,B,C
    // times 数组 用户对该道题的答题时间列表,规则同answers
    public class Data implements Serializable {
        public List<Integer> answers;
        public List<Integer> times;

        @Override
        public String toString() {
            return "Data{" +
                    "answers=" + answers +
                    ", times=" + times +
                    '}';
        }
    }
}
