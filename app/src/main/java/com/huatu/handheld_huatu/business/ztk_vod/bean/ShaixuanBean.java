package com.huatu.handheld_huatu.business.ztk_vod.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ht-ldc on 2017/9/22.
 */

public class ShaixuanBean implements Serializable{

    /**
     * code : 1000000
     * data : {"category":[{"categoryid":"1000","catname":"全部"},{"categoryid":"1","catname":"国考"},{"categoryid":"2","catname":"省考"},{"categoryid":"7","catname":"选调生"},{"categoryid":"10","catname":"村官"},{"categoryid":"6","catname":"政法干警"},{"categoryid":"20","catname":"党政公选"},{"categoryid":"4","catname":"公安招警"}],"order":[{"orderid":"1","ordername":"最热"},{"orderid":"2","ordername":"最新"},{"orderid":"3","ordername":"课时升序"},{"orderid":"4","ordername":"课时降序"}],"price":[{"priceid":"1000","pricename":"全部"},{"priceid":"1","pricename":"1-500"},{"priceid":"2","pricename":"500以上"}],"priceorder":[{"orderid":"5","ordername":"价格升序"},{"orderid":"6","ordername":"价格降序"}],"subject":[{"subjectid":"1000","subjectname":"全部"},{"subjectid":"1","subjectname":"行测"},{"subjectid":"2","subjectname":"申论"},{"subjectid":"3","subjectname":"笔试"},{"subjectid":"4","subjectname":"面试"}],"type":[{"typeid":"1000","typename":"全部"},{"typeid":"1","typename":"普通课程"},{"typeid":"3","typename":"十元铺子"},{"typeid":"4","typename":"1对1课程"}]}
     * message : 测试内容77md
     */

    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * categoryid : 1000
         * catname : 全部
         */

        private ArrayList<CategoryBean> category;
        /**
         * orderid : 1
         * ordername : 最热
         */

        private ArrayList<OrderBean> order;
        /**
         * priceid : 1000
         * pricename : 全部
         */

        private ArrayList<PriceBean> price;
        /**
         * orderid : 5
         * ordername : 价格升序
         */

        private ArrayList<PriceorderBean> priceorder;
        /**
         * subjectid : 1000
         * subjectname : 全部
         */

        private ArrayList<SubjectBean> subject;
        /**
         * typeid : 1000
         * typename : 全部
         */

        private ArrayList<TypeBean> type;

        public ArrayList<CategoryBean> getCategory() {
            return category;
        }

        public void setCategory(ArrayList<CategoryBean> category) {
            this.category = category;
        }

        public ArrayList<OrderBean> getOrder() {
            return order;
        }

        public void setOrder(ArrayList<OrderBean> order) {
            this.order = order;
        }

        public List<PriceBean> getPrice() {
            return price;
        }

        public void setPrice(ArrayList<PriceBean> price) {
            this.price = price;
        }

        public ArrayList<PriceorderBean> getPriceorder() {
            return priceorder;
        }

        public void setPriceorder(ArrayList<PriceorderBean> priceorder) {
            this.priceorder = priceorder;
        }

        public ArrayList<SubjectBean> getSubject() {
            return subject;
        }

        public void setSubject(ArrayList<SubjectBean> subject) {
            this.subject = subject;
        }

        public ArrayList<TypeBean> getType() {
            return type;
        }

        public void setType(ArrayList<TypeBean> type) {
            this.type = type;
        }

        public static class CategoryBean {
            private String categoryid;
            private String catname;

            public String getCategoryid() {
                return categoryid;
            }

            public void setCategoryid(String categoryid) {
                this.categoryid = categoryid;
            }

            public String getCatname() {
                return catname;
            }

            public void setCatname(String catname) {
                this.catname = catname;
            }
        }

        public static class OrderBean {
            private String orderid;
            private String ordername;

            public String getOrderid() {
                return orderid;
            }

            public void setOrderid(String orderid) {
                this.orderid = orderid;
            }

            public String getOrdername() {
                return ordername;
            }

            public void setOrdername(String ordername) {
                this.ordername = ordername;
            }
        }

        public static class PriceBean {
            private String priceid;
            private String pricename;

            public String getPriceid() {
                return priceid;
            }

            public void setPriceid(String priceid) {
                this.priceid = priceid;
            }

            public String getPricename() {
                return pricename;
            }

            public void setPricename(String pricename) {
                this.pricename = pricename;
            }
        }

        public static class PriceorderBean {
            private String orderid;
            private String ordername;

            public String getOrderid() {
                return orderid;
            }

            public void setOrderid(String orderid) {
                this.orderid = orderid;
            }

            public String getOrdername() {
                return ordername;
            }

            public void setOrdername(String ordername) {
                this.ordername = ordername;
            }
        }

        public static class SubjectBean {
            private String subjectid;
            private String subjectname;

            public String getSubjectid() {
                return subjectid;
            }

            public void setSubjectid(String subjectid) {
                this.subjectid = subjectid;
            }

            public String getSubjectname() {
                return subjectname;
            }

            public void setSubjectname(String subjectname) {
                this.subjectname = subjectname;
            }
        }

        public static class TypeBean {
            private String typeid;
            private String typename;

            public String getTypeid() {
                return typeid;
            }

            public void setTypeid(String typeid) {
                this.typeid = typeid;
            }

            public String getTypename() {
                return typename;
            }

            public void setTypename(String typename) {
                this.typename = typename;
            }
        }
    }
}
