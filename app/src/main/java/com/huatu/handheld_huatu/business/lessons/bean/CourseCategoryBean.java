package com.huatu.handheld_huatu.business.lessons.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by saiyuan on 2018/1/29.
 */

public class CourseCategoryBean implements Serializable {
    public int cateId;
    public boolean checked;
    public String name;
    public boolean isSelected;
    public String subjectId;
    public List<CategorysBean> categorys;

    public CourseCategoryBean clone() {
        CourseCategoryBean bean = new CourseCategoryBean();
        bean.cateId = this.cateId;
        bean.checked = this.checked;
        bean.name = this.name;
        bean.isSelected = this.isSelected;
        bean.categorys = this.categorys;
        bean.subjectId = this.subjectId;
        return bean;
    }

    public static class CategorysBean {
        public String catname;
        public int categoryid;
    }
}
