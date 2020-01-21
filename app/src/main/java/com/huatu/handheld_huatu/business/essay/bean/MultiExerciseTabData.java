package com.huatu.handheld_huatu.business.essay.bean;

import java.util.ArrayList;

/**
 * Created by ht on 2017/12/2.
 */
public class MultiExerciseTabData {
    public long id;
    public String name;
    public ArrayList<MultiExerciseTabList> essayQuestionBelongPaperVOList;

    public class MultiExerciseTabList {
        public long id;
        public String name;
    }

}
    //    data		array<object>
//    essayQuestionBelongPaperVOList		array<object>
//    id		string	@mock=$order('110001','110002')
//        name		string	@mock=$order('北京A','北京A')
//        id		string	@mock=$order('110','120')
//        name		string	@mock=$order('北京','上海')