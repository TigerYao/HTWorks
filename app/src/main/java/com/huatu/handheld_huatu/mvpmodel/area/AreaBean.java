package com.huatu.handheld_huatu.mvpmodel.area;

import java.util.List;

/**
 *
 * Created by KaelLi on 2016/12/27.
 */
public class AreaBean {
    public int code;
    public List<AreaModel> data;
    public class AreaModel {
        public int id;
        public String name;
        public int parentId;
        public List<AreaModel> children;
        //这个用来显示在PickerView上面的字符串,PickerView会通过反射获取getPickerViewText方法显示出来。
        public String getPickerViewText() {
            return name;
        }
    }
}
