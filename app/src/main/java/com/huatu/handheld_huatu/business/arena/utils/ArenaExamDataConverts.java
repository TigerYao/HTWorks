package com.huatu.handheld_huatu.business.arena.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaDetailBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaPushUnfinishedBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeans;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseBeansNew;
import com.huatu.handheld_huatu.mvpmodel.exercise.ModuleBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.RealExamBeans;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2016/10/21.
 */
public class ArenaExamDataConverts {

    /**
     * 把获取的问题信息，对应成本地问题对象结构
     */
    public static ArenaExamQuestionBean convertFromExerciseBean(ExerciseBeans.ExerciseBean dataBean) {
        ArenaExamQuestionBean bean = new ArenaExamQuestionBean();
        bean.id = dataBean.id;
        bean.type = dataBean.type;
        if (bean.type == 99 || bean.type == 109) {
            bean.isSingleChoice = true;
        }
        bean.from = dataBean.from;
        bean.material = dataBean.material;
        bean.year = dataBean.year;
        bean.area = dataBean.area;
        bean.status = dataBean.status;
        bean.mode = dataBean.mode;
        bean.subject = dataBean.subject;
        bean.stem = dataBean.stem;
        bean.answer = dataBean.answer;
        bean.recommendedTime = dataBean.recommendedTime;
        if (dataBean.choices != null && dataBean.choices.size() > 0) {
            bean.questionOptions = new ArrayList<>();
            for (int j = 0; j < dataBean.choices.size(); j++) {
                ArenaExamQuestionBean.QuestionOption option = new ArenaExamQuestionBean.QuestionOption();
                option.optionDes = dataBean.choices.get(j);
                bean.questionOptions.add(option);
            }
        }
        bean.analysis = dataBean.analysis;
        bean.extend = dataBean.extend;
        bean.score = dataBean.score;
        bean.difficult = dataBean.difficult;

        if (dataBean.pointList != null && dataBean.pointList.size() > 0) {
            ExerciseBeans.PointsList pointsList = dataBean.pointList.get(0);
            if (pointsList.pointsName != null && pointsList.pointsName.size() > 0) {
                bean.knowledgePointsList = new ArrayList<>();
                for (int i = 0; i < pointsList.pointsName.size(); i++) {
                    ArenaExamQuestionBean.KnowledgePoint point = new ArenaExamQuestionBean.KnowledgePoint();
                    point.name = pointsList.pointsName.get(i);
                    if (pointsList.points != null && pointsList.points.size() > i) {
                        point.id = pointsList.points.get(i);
                    }
                    bean.knowledgePointsList.add(point);
                }
            }
        }

        bean.parent = dataBean.parent;
        bean.meta = dataBean.meta;
        bean.teachType = dataBean.teachType;
        bean.materials = dataBean.materials;
        bean.require = dataBean.require;
        bean.scoreExplain = dataBean.scoreExplain;
        bean.referAnalysis = dataBean.referAnalysis;
        bean.answerRequire = dataBean.answerRequire;
        bean.examPoint = dataBean.examPoint;
        bean.solvingIdea = dataBean.solvingIdea;
        return bean;
    }

    /**
     * 新模考大赛新结构对应
     */
    public static ArenaExamQuestionBean convertFromExerciseBeanNew(ExerciseBeansNew.ExerciseBean dataBean) {
        ArenaExamQuestionBean bean = new ArenaExamQuestionBean();
        bean.id = dataBean.id;
        bean.type = dataBean.type;
        if (bean.type == 99 || bean.type == 109) {
            bean.isSingleChoice = true;
        }
        bean.from = dataBean.source;
        bean.material = dataBean.material;
        bean.year = dataBean.year;
        bean.area = dataBean.area;
        bean.status = dataBean.status;
        bean.mode = dataBean.mode;
        bean.subject = dataBean.subject;
        bean.stem = dataBean.stem;
        bean.answer = dataBean.answer;
        bean.recommendedTime = dataBean.recommendedTime;
        if (dataBean.choiceList != null && dataBean.choiceList.size() > 0) {
            bean.questionOptions = new ArrayList<>();
            for (int j = 0; j < dataBean.choiceList.size(); j++) {
                ArenaExamQuestionBean.QuestionOption option = new ArenaExamQuestionBean.QuestionOption();
                option.optionDes = dataBean.choiceList.get(j);
                bean.questionOptions.add(option);
            }
        }
        bean.analysis = dataBean.analysis;
        bean.extend = dataBean.extend;
        bean.score = dataBean.score;
        bean.difficult = dataBean.difficult;

        if (dataBean.pointList != null && dataBean.pointList.size() > 0) {
            ExerciseBeansNew.PointsList pointsList = dataBean.pointList.get(0);
            if (pointsList.pointsName != null && pointsList.pointsName.size() > 0) {
                bean.knowledgePointsList = new ArrayList<>();
                for (int i = 0; i < pointsList.pointsName.size(); i++) {
                    ArenaExamQuestionBean.KnowledgePoint point = new ArenaExamQuestionBean.KnowledgePoint();
                    point.name = pointsList.pointsName.get(i);
                    if (pointsList.points != null && pointsList.points.size() > i) {
                        point.id = pointsList.points.get(i);
                    }
                    bean.knowledgePointsList.add(point);
                }
            }
        }

        bean.parent = dataBean.parentId;
        ExerciseBeans.Meta meta = new ExerciseBeans.Meta();
        if (dataBean.meta != null) {
            meta.percents = dataBean.meta.percents;
            meta.rindex = dataBean.meta.rindex;
            meta.yc = dataBean.meta.yc;
            meta.answers = dataBean.meta.answers;
        }

        bean.meta = meta;
        bean.teachType = dataBean.teachType;
        bean.materials = dataBean.materialList;
        bean.require = dataBean.require;
        bean.scoreExplain = dataBean.scoreExplain;
        bean.referAnalysis = dataBean.referAnalysis;
        bean.answerRequire = dataBean.answerRequire;
        bean.examPoint = dataBean.examPoint;
        bean.solvingIdea = dataBean.solvingIdea;
        return bean;
    }

    /**
     * 处理收藏信息
     */
    public static List<Integer> parsePractiseCollectionList(JsonObject jsonObject) {
        List<Integer> collectList = null;
        if (jsonObject != null && jsonObject.has("code")
                && jsonObject.get("code").getAsInt() == 1000000 && jsonObject.get("data") != null) {
            try {
                JsonArray dataArray = jsonObject.get("data").getAsJsonArray();
                collectList = new Gson().fromJson(dataArray,
                        new TypeToken<List<Integer>>() {
                        }.getType());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return collectList;
    }

    public static ArenaDetailBean parseArenaDetailFromPush(String message) {
        if (TextUtils.isEmpty(message)) {
            return null;
        }
        message = message.replaceAll(" ", "").trim();
        LogUtils.i("message: " + message);
        ArenaDetailBean arenaDetailBean = null;
        ArenaPushUnfinishedBean response = null;
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new StringReader(message));
            reader.setLenient(true);
            response = gson.fromJson(reader, new TypeToken<ArenaPushUnfinishedBean>() {
            }.getType());
            if (response != null && response.data != null) {
                arenaDetailBean = response.data;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (arenaDetailBean != null && arenaDetailBean.players == null) {
            arenaDetailBean.players = new ArrayList<>();
        }
        return arenaDetailBean;
    }

    public static JsonArray createAnswerCardJson(List<ArenaExamQuestionBean> questionBeanList) {
        List<AnswerCardBean> list = new ArrayList<>();
        if (questionBeanList != null) {
            for (ArenaExamQuestionBean bean : questionBeanList) {
                AnswerCardBean answerCardBean = createAnswerCardBean(bean);
                if (answerCardBean != null) {
                    list.add(answerCardBean);
                }
            }
        }
        JsonArray jsonArray = new Gson().toJsonTree(list,
                new TypeToken<List<AnswerCardBean>>() {
                }.getType()).getAsJsonArray();
        return jsonArray;
    }

    public static AnswerCardBean createAnswerCardBean(ArenaExamQuestionBean bean) {
        if (bean.userAnswer == 0 && bean.usedTime == 0 && bean.doubt == 0) {
            return null;
        }
        AnswerCardBean answerCardBean = new AnswerCardBean();
        answerCardBean.questionId = bean.id;
        answerCardBean.answer = bean.userAnswer;
        answerCardBean.time = bean.usedTime;
        answerCardBean.correct = bean.isCorrect;
        answerCardBean.doubt = bean.doubt;
        if (answerCardBean.answer != 0 && answerCardBean.correct != 0 && answerCardBean.time <= 0) {
            answerCardBean.time = 1;
        }
        return answerCardBean;
    }

    /**
     * 处理收藏信息
     */
    public static void processExamCollection(List<ArenaExamQuestionBean> questionList, List<Integer> collectList) {
        if (questionList == null || collectList == null) {
            return;
        }
        for (int i = 0; i < questionList.size(); i++) {
            for (int j = 0; j < collectList.size(); j++) {
                if (questionList.get(i).id == collectList.get(j)) {
                    questionList.get(i).isFaverated = true;
                    break;
                }
            }
        }
    }

    /**
     * 处理答题卡信息
     */
    public static void dealExamBeanAnswers(RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.questionBeanList == null) {
            return;
        }
        // 已经选择的答案，对应到答案中
        for (int i = 0; i < realExamBean.paper.questionBeanList.size(); i++) {
            if (realExamBean.answers != null && realExamBean.answers.size() > i) {
                realExamBean.paper.questionBeanList.get(i).userAnswer = realExamBean.answers.get(i);
                int userAnswer = realExamBean.answers.get(i);
                if (userAnswer != 0) {
                    realExamBean.paper.questionBeanList.get(i).isSubmitted = true;
                    if (realExamBean.paper.questionBeanList.get(i).questionOptions != null) {
                        int index = 0;
                        while (userAnswer > 0) {
                            index = userAnswer % 10;
                            if (index - 1 < realExamBean.paper.questionBeanList.get(i).questionOptions.size()) {
                                realExamBean.paper.questionBeanList.get(i).questionOptions.get(index - 1).isSelected = true;
                            }
                            userAnswer /= 10;
                        }
                    }
                }
            }
        }

        // 是否分模块
        boolean isDealModule = false;
        // 是否正确
        if (realExamBean.corrects != null && realExamBean.corrects.size() > 0) {
            if (realExamBean.paper.modules != null && realExamBean.paper.modules.size() > 1) {
                isDealModule = true;
            }
            for (int i = 0; i < realExamBean.paper.questionBeanList.size(); i++) {
                realExamBean.paper.questionBeanList.get(i).isCorrect = realExamBean.corrects.get(i);
            }
        }

        // 把错误问题过滤添加进paper
        realExamBean.paper.wrongQuestionBeanList = new ArrayList<>();
        for (int i = 0; i < realExamBean.paper.questionBeanList.size(); i++) {
            realExamBean.paper.questionBeanList.get(i).index = i;
            if (realExamBean.paper.questionBeanList.get(i).isCorrect == 2) {
                realExamBean.paper.wrongQuestionBeanList.add(realExamBean.paper.questionBeanList.get(i));
            }
        }

        // 分模块
        if (realExamBean.paper.modules == null) {
            realExamBean.paper.modules = new ArrayList<>();
        }
        // 解析错误模块
        if (isDealModule) {
            realExamBean.paper.wrongModules = new ArrayList<>();
            List<ModuleBean> moduleBeanList = dealWrongModules(realExamBean);
            if (moduleBeanList != null) {
                realExamBean.paper.wrongModules.addAll(moduleBeanList);
            }
        }
        // 做题时间
        if (realExamBean.times != null && realExamBean.times.size() > 0) {
            for (int i = 0; i < realExamBean.paper.questionBeanList.size(); i++) {
                realExamBean.paper.questionBeanList.get(i).usedTime = realExamBean.times.get(i);
            }
        }

        // 给没一道题添加类型
        List<ModuleBean> modules = null;

        if (realExamBean.modules != null) {
            modules = realExamBean.modules;
        } else if (realExamBean.paper != null && realExamBean.paper.modules != null) {
            modules = realExamBean.paper.modules;
        }

        List<ArenaExamQuestionBean> questionBeanList = realExamBean.paper.questionBeanList;
        for (int i = 0; i < questionBeanList.size(); i++) {
            ArenaExamQuestionBean bean = questionBeanList.get(i);
            // 给每一道题添加类型
            if (modules != null) {
                for (int j = 0; j < modules.size(); j++) {
                    int count = 0;
                    for (int k = 0; k <= j; k++) {
                        count += modules.get(k).qcount;
                    }
                    if (i < count) {
                        bean.categoryName = modules.get(j).name;
                        break;
                    }
                }
            }
        }
    }

    /**
     * 解析错误模块
     */
    private static List<ModuleBean> dealWrongModules(RealExamBeans.RealExamBean realExamBean) {
        if (realExamBean == null || realExamBean.paper == null || realExamBean.paper.modules == null || realExamBean.paper.wrongQuestionBeanList == null) {
            return null;
        }
        final List<ModuleBean> moduleBeanList = new ArrayList<>();
        int[] moduleIndexArray = new int[realExamBean.paper.modules.size()];
        moduleIndexArray[0] = realExamBean.paper.modules.get(0).qcount;
        for (int i = 1; i < realExamBean.paper.modules.size(); i++) {
            moduleIndexArray[i] = moduleIndexArray[i - 1] + realExamBean.paper.modules.get(i).qcount;
        }
        int index = 0;
        for (int i = 0; i < realExamBean.paper.wrongQuestionBeanList.size(); i++) {
            int beanIndex = realExamBean.paper.wrongQuestionBeanList.get(i).index;
            while (beanIndex >= moduleIndexArray[index]) {
                index++;
            }
            int moduleIndex = -1;
            for (int j = 0; j < moduleBeanList.size(); j++) {
                if (moduleBeanList.get(j).category == realExamBean.paper.modules.get(index).category) {
                    moduleIndex = j;
                    break;
                }
            }
            if (moduleIndex >= 0) {
                moduleBeanList.get(moduleIndex).qcount++;
            } else {
                ModuleBean moduleBean = new ModuleBean();
                moduleBean.category = realExamBean.paper.modules.get(index).category;
                moduleBean.qcount = 1;
                moduleBean.name = realExamBean.paper.modules.get(index).name;
                moduleBeanList.add(moduleBean);
            }
        }
        return moduleBeanList;
    }
}
