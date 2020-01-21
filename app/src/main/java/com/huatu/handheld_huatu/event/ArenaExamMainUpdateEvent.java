package com.huatu.handheld_huatu.event;

import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;



/**
 * Created by saiyuan on 2016/10/18.
 */
public class ArenaExamMainUpdateEvent {
    public String tag;
    public ArenaExamQuestionBean questionBean;
    public RealExamBeans.RealExamBean realExamBean;
}
