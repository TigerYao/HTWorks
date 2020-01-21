package com.huatu.handheld_huatu.mvpmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by cjx on
 */

public class CourseStageBean   {

        @SerializedName("list")
        public List<SyllabusClassesBean> list;

        public int  hierarchy;

}

