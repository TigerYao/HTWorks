package com.huatu.handheld_huatu.mvpmodel.me;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ht on 2016/7/15.
 */
public class ActionListBean implements Serializable {
    public int code;
    public List<ActionListData> data;

    @Override
    public String toString() {
        return "ActionListBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<ActionListData> getData() {
        return data;
    }

    public void setData(List<ActionListData> data) {
        this.data = data;
    }

    public class ActionListData implements Serializable {
        public long id;
        public String name;
        public String image;
        public String link;
        public long createTime;
        public long beginTime;
        public long endTime;
        public long pv;
        public int status;



        @Override
        public String toString() {
            return "ActionListData{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", image='" + image + '\'' +
                    ", link='" + link + '\'' +
                    ", createTime=" + createTime +
                    ", beginTime=" + beginTime +
                    ", endTime=" + endTime +
                    ", pv=" + pv +
                    ", status=" + status +
                    '}';
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(long beginTime) {
            this.beginTime = beginTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getPv() {
            return pv;
        }

        public void setPv(long pv) {
            this.pv = pv;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
