package com.huatu.handheld_huatu.mvpmodel.me;

import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;

import java.io.Serializable;
import java.util.List;

/**
 * 背题模式获取错题id列表 .
 */
public class ArenaReciteIdsBean {
    private int code;
    private ArenaReciteIdsData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArenaReciteIdsData getData() {
        return data;
    }

    public void setData(ArenaReciteIdsData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ErrorIdsBean{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }

    public class ArenaReciteIdsData implements Serializable {

        private int catgory;
        private float difficulty;
        private List<ModuleBean> modules;
        private String name;
        private int qcount;
        private List<Integer> questions;
        private int subject;

        public int getCatgory() {
            return catgory;
        }

        public void setCatgory(int catgory) {
            this.catgory = catgory;
        }

        public float getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(float difficulty) {
            this.difficulty = difficulty;
        }

        public List<ModuleBean> getModules() {
            return modules;
        }

        public void setModules(List<ModuleBean> modules) {
            this.modules = modules;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQcount() {
            return qcount;
        }

        public void setQcount(int qcount) {
            this.qcount = qcount;
        }

        public List<Integer> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Integer> questions) {
            this.questions = questions;
        }

        public int getSubject() {
            return subject;
        }

        public void setSubject(int subject) {
            this.subject = subject;
        }

        @Override
        public String toString() {
            return "ArenaReciteIdsData{" +
                    "catgory=" + catgory +
                    ", difficulty=" + difficulty +
                    ", modules=" + modules +
                    ", name='" + name + '\'' +
                    ", qcount=" + qcount +
                    ", questions=" + questions +
                    ", subject=" + subject +
                    '}';
        }
    }
}
