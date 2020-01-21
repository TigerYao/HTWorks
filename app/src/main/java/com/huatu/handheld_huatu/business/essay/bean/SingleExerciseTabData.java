package com.huatu.handheld_huatu.business.essay.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ht on 2017/11/30.
 */
public class SingleExerciseTabData implements Serializable{
    public int id;
    public String name;
    public int sort;
    public ArrayList<SingleExerciseTabData> subList;
}
//    code		number	@mock=1000000
//        data		array<object>
//id		number	@mock=$order(1,2,3,4,5)
//        name	类型名称	string	@mock=$order('概括归纳','综合分析','解决问题','应用文','文章写作')
//        sort	优先级	number	@mock=$order(1,2,3,4,5)