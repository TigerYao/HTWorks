package com.huatu.handheld_huatu.business.arena.utils;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.HashMap;


public class RealListUpdateClickRecord {
    private HashMap<Integer, Integer> mRealListUpdateClickRecord = new HashMap<>();
    private HashMap<String, Long> mRealAreaVersionRecord = new HashMap<>();
    private HashMap<String, Integer> mRealAreaHitRecord = new HashMap<>();
    public static int versionRecord = -1;

    private RealListUpdateClickRecord() {
    }

    private static RealListUpdateClickRecord instance = null;

    public static RealListUpdateClickRecord getInstance() {
        synchronized (RealListUpdateClickRecord.class) {
            if (instance == null) {
                instance = new RealListUpdateClickRecord();
            }
        }
        return instance;
    }


    public void addRealAreaHitRecord(String key, Integer value) {
        mRealAreaHitRecord.put(key, value);
    }

    public void saveRealAreaHitRecordToFile() {
        if (mRealAreaHitRecord != null) {
            String var = GsonUtil.toJsonStr(mRealAreaHitRecord);
            if (var != null && var.length() > 0) {
                SpUtils.setRealAreaHit(var);
            }
        } else {
            SpUtils.setRealAreaHit("");
        }
    }

    public int getRealAreaHitRecord(String key) {
        String realAreaHit = SpUtils.getRealAreaHit();
        if (!TextUtils.isEmpty(realAreaHit)) {
            HashMap<String, Integer> data = (HashMap<String, Integer>) GsonUtil.string2JsonObject(realAreaHit,
                    new TypeToken<HashMap<String, Integer>>() {
                    }.getType());
            if (data != null && data.size() > 0) {
                mRealAreaHitRecord.putAll(data);
            }
        }

        return mRealAreaHitRecord.get(key) == null ? 0 : mRealAreaHitRecord.get(key);
    }


    public void addRealAreaVersion(String key, Long value) {
        mRealAreaVersionRecord.put(key, value);
    }

    public void saveRealAreaVersionRecordToFile() {
        if (mRealAreaVersionRecord != null) {
            String var = GsonUtil.toJsonStr(mRealAreaVersionRecord);
            if (var != null && var.length() > 0) {
                SpUtils.setRealAreaVersion(var);
            }
        } else {
            SpUtils.setRealAreaVersion("");
        }
    }

    public long getRealAreaVersionRecord(String key) {
        String realAreaVersion = SpUtils.getRealAreaVersion();
        if (!TextUtils.isEmpty(realAreaVersion)) {
            HashMap<String, Long> data = (HashMap<String, Long>) GsonUtil.string2JsonObject(realAreaVersion,
                    new TypeToken<HashMap<String, Long>>() {
                    }.getType());
            if (data != null && data.size() > 0) {
                mRealAreaVersionRecord.putAll(data);
            }
        }

        return mRealAreaVersionRecord.get(key) == null ? -1 : mRealAreaVersionRecord.get(key);
    }


    public void addRealListHitRecord(Integer key, Integer value) {
        mRealListUpdateClickRecord.put(key, value);
    }


    public void saveRealListHitRecordToFile() {
        if (mRealListUpdateClickRecord != null) {
            String var = GsonUtil.toJsonStr(mRealListUpdateClickRecord);
            if (var != null && var.length() > 0) {
                SpUtils.setRealListMap(var);
            }
        } else {
            SpUtils.setRealListMap("");
        }
    }

    public int getRealListHitRecord(Integer key) {
        String strMap = SpUtils.getRealListMap();
        if (!TextUtils.isEmpty(strMap)) {
            HashMap<Integer, Integer> data = (HashMap<Integer, Integer>) GsonUtil.string2JsonObject(strMap,
                    new TypeToken<HashMap<Integer, Integer>>() {
                    }.getType());
            if (data != null && data.size() > 0) {
                mRealListUpdateClickRecord.putAll(data);
            }
        }
        return mRealListUpdateClickRecord.get(key) == null ? 0 : mRealListUpdateClickRecord.get(key);

    }
}


