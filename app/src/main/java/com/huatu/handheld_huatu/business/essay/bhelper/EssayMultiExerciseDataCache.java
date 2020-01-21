package com.huatu.handheld_huatu.business.essay.bhelper;

import com.huatu.handheld_huatu.business.essay.bean.MultiExerciseData;

import java.util.HashMap;

/**
 * Created by ht on 2017/12/5.
 */

public class EssayMultiExerciseDataCache {
    private static HashMap<Long, MultiExerciseData> mapmultidata;

    private EssayMultiExerciseDataCache() {
    }

    static {
        mapmultidata = new HashMap<>();
    }

    private static EssayMultiExerciseDataCache instance = new EssayMultiExerciseDataCache();

    public static EssayMultiExerciseDataCache getInstance() {
        return instance;
    }

    //用于保存缓存
    public static void addCache(Long key, MultiExerciseData value) {
        mapmultidata.put(key, value);
    }

    //用于得到缓存
    public static MultiExerciseData getCache(Long key) {
        return mapmultidata.get(key);
    }

    public void clearCache() {
        mapmultidata.clear();
    }
}
