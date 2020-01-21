package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * Created by ht on 2016/7/15.
 */
public class ActionNumberAddBean {
    public int code;
    public ActionNumberData data;

    @Override
    public String toString() {
        return "ActionNumberAddBean{" +
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

    public ActionNumberData getData() {
        return data;
    }

    public void setData(ActionNumberData data) {
        this.data = data;
    }

    public class ActionNumberData {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        @Override
        public String toString() {
            return "ActionNumberData{" +
                    "message='" + message + '\'' +
                    '}';
        }
    }
}
