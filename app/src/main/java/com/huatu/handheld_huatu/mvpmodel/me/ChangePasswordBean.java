package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * Created by ht on 2016/7/15.
 */
public class ChangePasswordBean {
    private int code;
    private ChangePassword data;

    @Override
    public String toString() {
        return "ChangePasswordBean{" +
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

    public ChangePassword getData() {
        return data;
    }

    public void setData(ChangePassword data) {
        this.data = data;
    }

    public class ChangePassword {
        private String message;

        @Override
        public String toString() {
            return "ChangePassword{" +
                    "message='" + message + '\'' +
                    '}';
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
