package com.huatu.handheld_huatu.mvpmodel.me;

/**
 * 用户退出账号时返回的Bean
 */
public class LogoutBean {

    private int code;
    private LogoutData data;

    @Override
    public String toString() {
        return "LogoutBean{" +
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

    public LogoutData getData() {
        return data;
    }

    public void setData(LogoutData data) {
        this.data = data;
    }

    public class LogoutData {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "LogoutData{" +
                    "message='" + message + '\'' +
                    '}';
        }
    }
}
