package com.huatu.handheld_huatu.business.arena.utils;


import android.util.SparseArray;

import com.huatu.handheld_huatu.mvpmodel.arena.DrawPath;

import java.util.ArrayList;

/**
 * Created by saiyuan on 2018/10/25.
 */

public class DraftDataCache {

    private SparseArray<ArrayList<DrawPath>> draftCache = new SparseArray<>();
    private static volatile DraftDataCache instance = null;

    public static DraftDataCache getInstance() {
        if (instance == null) {
            synchronized (DraftDataCache.class) {
                if (instance == null) {
                    instance = new DraftDataCache();
                }
            }
        }
        return instance;
    }

    public ArrayList<DrawPath> getCache(int key) {
        return draftCache.get(key);
    }

    public void putCache(int key, ArrayList<DrawPath> mDraft) {
        draftCache.put(key, mDraft);
    }

    public void clearDraftCache() {
        if (draftCache != null && draftCache.size() != 0) {
            draftCache.clear();
        }
    }
}
