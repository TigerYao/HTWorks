package com.huatu.handheld_huatu.mvpmodel.me;

import java.util.List;

/**
 * 收藏夹问题列表
 */
public class CollectionIdsBean {

    private int code;
    private CollectionIdsData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public CollectionIdsData getData() {
        return data;
    }

    public void setData(CollectionIdsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CollectionIdsBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class CollectionIdsData {
        private List<Integer> result;
        private int next;

        public List<Integer> getResult() {
            return result;
        }

        public void setResult(List<Integer> result) {
            this.result = result;
        }

        public int getNext() {
            return next;
        }

        public void setNext(int next) {
            this.next = next;
        }

        @Override
        public String toString() {
            return "CollectionIdsData{" +
                    ", resutls=" + result +
                    '}';
        }
    }
}
