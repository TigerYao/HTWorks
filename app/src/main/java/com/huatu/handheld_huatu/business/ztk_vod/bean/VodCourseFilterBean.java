package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ht-ldc on 2018/1/27.
 */

public class VodCourseFilterBean implements Serializable{
    /**
     * code : 1000000
     * data : [{"categorys":[{"catname":"全部","categoryid":"1000"}],"subjectid":"1000","isCheck":true,"categoryid":"1000","name":"公务员"},{"categorys":[{"catname":"国家公务员","categoryid":"1"}],"subjectid":"1001","isCheck":true,"categoryid":"1001","name":"事业单位"},{"categorys":[{"catname":"省考公务员","categoryid":"2"}],"subjectid":"1002","isCheck":false,"categoryid":"1002","name":"公检法"},{"categorys":[{"catname":"选调生","categoryid":"7"}],"subjectid":"1003","isCheck":false,"categoryid":"1003","name":"教师"},{"categorys":[{"catname":"遴选","categoryid":"20"}],"subjectid":"1004","isCheck":false,"categoryid":"1004","name":"医疗"},{"categorys":[{"catname":"全部","categoryid":"1000"}],"subjectid":"1005","isCheck":false,"categoryid":"1005","name":"金融"},{"categorys":[{"catname":"国家公务员","categoryid":"1"}],"subjectid":"1006","isCheck":false,"categoryid":"1006","name":"其他"}]
     */

    private int code;
    /**
     * categorys : [{"catname":"全部","categoryid":"1000"}]
     * subjectid : 1000
     * isCheck : true
     * categoryid : 1000
     * name : 公务员
     */

    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        public  String subjectid;
        public boolean isCheck;
        public String categoryid;
        public String name;
        /**
         * catname : 全部
         * categoryid : 1000
         */

        public  List<CategorysBean> categorys;

        public String getSubjectid() {
            return subjectid;
        }

        public void setSubjectid(String subjectid) {
            this.subjectid = subjectid;
        }

        public boolean isIsCheck() {
            return isCheck;
        }

        public void setIsCheck(boolean isCheck) {
            this.isCheck = isCheck;
        }

        public String getCategoryid() {
            return categoryid;
        }

        public void setCategoryid(String categoryid) {
            this.categoryid = categoryid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CategorysBean> getCategorys() {
            return categorys;
        }

        public void setCategorys(List<CategorysBean> categorys) {
            this.categorys = categorys;
        }

        public static class CategorysBean {
            private String catname;
            private String categoryid;

            public String getCatname() {
                return catname;
            }

            public void setCatname(String catname) {
                this.catname = catname;
            }

            public String getCategoryid() {
                return categoryid;
            }

            public void setCategoryid(String categoryid) {
                this.categoryid = categoryid;
            }
        }
        public DataBean clone() {
            VodCourseFilterBean.DataBean bean = new VodCourseFilterBean.DataBean();
            bean.categoryid = this.categoryid;
            bean.isCheck = this.isCheck;
            bean.name = this.name;
            bean.categorys = this.categorys;
            return bean;
        }
    }

}
