package com.huatu.handheld_huatu.business.essay.bhelper;

import android.text.TextUtils;
import android.util.LongSparseArray;

import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.mvpmodel.essay.ExamMaterialListBean;
import com.huatu.handheld_huatu.mvpmodel.essay.PaperQuestionDetailBean;
import com.huatu.handheld_huatu.mvpmodel.essay.SingleQuestionDetailBean;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 数据缓存
 */

public class EssayExamDataCache {

    // 本地记录材料划线的东西吧！
    public HashMap<String, ArrayList<Integer>> materials_sel = new HashMap<>();

    public ArrayList<ExamMaterialListBean> cacheSingleMaterialListBeans;    // 当前的单题材料
    public SingleQuestionDetailBean cacheSingleQuestionDetailBean;          // 当前的单题问题
    private LongSparseArray<ArrayList<ExamMaterialListBean>> mapSingleMaterialListBeans = new LongSparseArray<>();   // 缓存单题材料

    public ArrayList<ExamMaterialListBean> cachePaperMaterialListBeans;     // 当前的套题材料
    public PaperQuestionDetailBean cachePaperQuestionDetailBean;            // 当前的套题问题
    private LongSparseArray<ArrayList<ExamMaterialListBean>> mapPaperMaterialListBeans = new LongSparseArray<>();    // 缓存套题材料
    public String titleArea;
    public int materialsTxtSize = 15;

    public EssayExamImpl mEssayExamImpl_m;

    public int correctMode = 0;             // 批改方式 1、智能批改 2、人工批改

    public static volatile EssayExamDataCache instance = null;

    public static EssayExamDataCache getInstance() {
        synchronized (EssayExamDataCache.class) {
            if (instance == null) {
                instance = new EssayExamDataCache();
            }
        }
        return instance;
    }

    private EssayExamDataCache() {
        readFromFileCahce_materials_sel();
    }

    private void readFromFileCahce_materials_sel() {
        String strIng = SpUtils.getEssayMaterSelList();
        if (!TextUtils.isEmpty(strIng)) {
            HashMap<String, ArrayList<Integer>> data = (HashMap<String, ArrayList<Integer>>) GsonUtil.string2JsonObject(strIng,
                    new TypeToken<HashMap<String, ArrayList<Integer>>>() {
                    }.getType());
            if (data != null && data.size() > 0) {
                if (materials_sel != null) {
                    materials_sel.clear();
                    materials_sel.putAll(data);
                }
            }
        }
    }

    public void writeToFileCahce_materials_sel() {
        SpUtils.setEssayMaterSelList(GsonUtil.toJsonStr(materials_sel));
    }

    public ArrayList<ExamMaterialListBean> getCacheSingleMaterialListBeans(long questionBaseId) {
        if (mapSingleMaterialListBeans != null) {
            cacheSingleMaterialListBeans = mapSingleMaterialListBeans.get(questionBaseId);
            return cacheSingleMaterialListBeans;
        }
        return null;
    }

    public void setCacheSingleMaterialListBeans(long questionBaseId, ArrayList<ExamMaterialListBean> var) {
        this.cacheSingleMaterialListBeans = var;
        if (mapSingleMaterialListBeans == null) {
            mapSingleMaterialListBeans = new LongSparseArray<>();
        }
        mapSingleMaterialListBeans.put(questionBaseId, var);
    }

    public ArrayList<ExamMaterialListBean> getCachePaperMaterialListBeans(long paperId) {
        if (mapPaperMaterialListBeans != null) {
            cachePaperMaterialListBeans = mapPaperMaterialListBeans.get(paperId);
            return cachePaperMaterialListBeans;
        }
        return null;
    }

    public void setCachePaperMaterialListBeans(long paperId, ArrayList<ExamMaterialListBean> var) {
        this.cachePaperMaterialListBeans = var;
        if (mapPaperMaterialListBeans == null) {
            mapPaperMaterialListBeans = new LongSparseArray<>();
        }
        mapPaperMaterialListBeans.put(paperId, var);
    }

    // 清理缓存数据
    public void clearData() {
        titleArea = "";
        mEssayExamImpl_m = null;
        correctMode = 0;
        if (cacheSingleMaterialListBeans != null) {
            cacheSingleMaterialListBeans.clear();
        }
        cacheSingleMaterialListBeans = null;
        cacheSingleQuestionDetailBean = null;
        if (mapSingleMaterialListBeans != null) {
            mapSingleMaterialListBeans.clear();
        }

        if (cachePaperMaterialListBeans != null) {
            cachePaperMaterialListBeans.clear();
        }
        cachePaperMaterialListBeans = null;
        cachePaperQuestionDetailBean = null;
        if (mapPaperMaterialListBeans != null){
            mapPaperMaterialListBeans.clear();
        }
    }
}
