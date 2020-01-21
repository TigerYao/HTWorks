package com.huatu.handheld_huatu.mvpmodel.me;

public class DeleteResponseBean {
    public long code;
    public Data data;

    @Override
    public String toString() {
        return "DeleteResponseBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class Data {
        public String message;
        public String url;

        @Override
        public String toString() {
            return "Data{" +
                    "message='" + message + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }
}
