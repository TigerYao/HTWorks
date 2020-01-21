package com.huatu.handheld_huatu.mvpmodel.matchs;


import java.io.Serializable;
import java.util.List;

public class SimulationScHistory implements Serializable {


    /**
     * line : {"categories":["全站平均得分","模考得分"],"series":[{"name":"8月18日","data":[8,6]}]}
     * list : [{"name":"模考大赛test01","practiceId":1775076581165760512,"startTime":1503040500000,"total":9}]
     */

    private LineEntity line;
    private List<ListEntity> list;

    public LineEntity getLine() {
        return line;
    }

    public void setLine(LineEntity line) {
        this.line = line;
    }

    public List<ListEntity> getList() {
        return list;
    }

    public void setList(List<ListEntity> list) {
        this.list = list;
    }

    public static class LineEntity implements Serializable{
        private List<String> categories;
        private List<SeriesEntity> series;

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public List<SeriesEntity> getSeries() {
            return series;
        }

        public void setSeries(List<SeriesEntity> series) {
            this.series = series;
        }

        public static class SeriesEntity implements Serializable{
            /**
             * name : 8月18日
             * data : [8,6]
             */

            private String name;
            private List<Float> data;
            private List<String> strData;       // String分数

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<Float> getData() {
                return data;
            }

            public void setData(List<Float> data) {
                this.data = data;
            }
        }
    }

    public static class ListEntity implements Serializable{
        /**
         * name : 模考大赛test01
         * practiceId : 1775076581165760512
         * startTime : 1503040500000
         * total : 9
         */

        private String name;
        public long paperId;
        private long startTime;
        private int total;
        public long essayPaperId;
        public int flag;//1只有行测2只有申论3申论行测都有

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
