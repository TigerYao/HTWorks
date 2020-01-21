package com.huatu.handheld_huatu.mvpmodel.Sensor;

import java.util.List;

public class CourseInfoForStatistic extends BaseEvent{
    public String course_id ;//	课程ID
    public boolean  is_free	;//是否免费
    public boolean is_set_meal	;//是否套餐
    public boolean is_collection	;//是否合集
    public String  class_name	;//课件名称
    public String  course_title	;//课程名称
    public List<String> teacher_id;
    public List<String>  teacher_name	;//老师姓名
    public float course_price	;//课程原价
    public float course_collage_price	;//课程拼团价
    public String course_kind	;//课程类属
    public String course_type	;//课程类型
    public float discount_price;//
}
