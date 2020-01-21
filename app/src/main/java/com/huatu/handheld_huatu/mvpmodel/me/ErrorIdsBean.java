package com.huatu.handheld_huatu.mvpmodel.me;

import java.util.List;

/**
 * 获取错题id列表 .
 */
public class ErrorIdsBean {
    private int code;
    private ErrorIdsData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ErrorIdsData getData() {
        return data;
    }

    public void setData(ErrorIdsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ErrorIdsBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class ErrorIdsData {
        private long cursor;
        private List<Integer> resutls;
        private int total;

        public long getCursor() {
            return cursor;
        }

        public void setCursor(long cursor) {
            this.cursor = cursor;
        }

        public List<Integer> getResutls() {
            return resutls;
        }

        public void setResutls(List<Integer> resutls) {
            this.resutls = resutls;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "ErrorIdsData{" +
                    "cursor=" + cursor +
                    ", resutls=" + resutls +
                    ", total=" + total +
                    '}';
        }
    }
}
