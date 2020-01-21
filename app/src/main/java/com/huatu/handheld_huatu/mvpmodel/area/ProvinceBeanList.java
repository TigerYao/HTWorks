package com.huatu.handheld_huatu.mvpmodel.area;


import java.io.Serializable;
import java.util.List;

/**
 * Created by Sai on 15/11/22.
 */
public class ProvinceBeanList implements Serializable {

    /**
     * areas : [{"id":-9,"name":"全国","parentId":0,"children":null},{"id":1,"name":"北京","parentId":0,"children":null}]
     * userArea : 41
     */

    private int userArea;
    private List<AreasEntity> areas;

    public int getUserArea() {
        return userArea;
    }

    public void setUserArea(int userArea) {
        this.userArea = userArea;
    }

    public List<AreasEntity> getAreas() {
        return areas;
    }

    public void setAreas(List<AreasEntity> areas) {
        this.areas = areas;
    }

    public static class AreasEntity implements Serializable {
        /**
         * id : -9
         * name : 全国
         * parentId : 0
         * children : null
         */

        private int id;
        private String name;
        private int parentId;
        private Object children;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public Object getChildren() {
            return children;
        }

        public void setChildren(Object children) {
            this.children = children;
        }
    }
}
