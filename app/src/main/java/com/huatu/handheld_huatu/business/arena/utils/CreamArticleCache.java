package com.huatu.handheld_huatu.business.arena.utils;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.HashMap;

/**
 * Created by saiyuan on 2019/2/23.
 */

public class CreamArticleCache {
    public HashMap<String, Integer> creamArticleCache = new HashMap<>();

    public static volatile CreamArticleCache instance=null;
    public static CreamArticleCache getInstance(){
        synchronized(CreamArticleCache.class){
            if (instance==null){
                instance=new CreamArticleCache();
            }
        }
        return instance;
    }

    public void putCache(String id, int type) {
        creamArticleCache.put(id, type);
    }

    public int getCache(String id) {
        if (creamArticleCache!=null&&creamArticleCache.containsKey(id)){
            return  creamArticleCache.get(id);
        }else {
            return  1;
        }
    }

    public void writeCacheToFile(){
        if (creamArticleCache.size() > 0) {
            String var = GsonUtil.toJsonStr(creamArticleCache);
            if (var != null && var.length() > 0) {
                SpUtils.setPraiseDataPath(var);
            }
        } else {
            SpUtils.setPraiseDataPath("");
        }
    }
    public void readCacheToFile(){
        String strIng = SpUtils.getPraiseDataPath();
        if (!TextUtils.isEmpty(strIng)) {
            HashMap<String, Integer> dl_ingL = (HashMap<String, Integer>) GsonUtil.string2JsonObject(strIng,
                    new TypeToken<HashMap<String, Integer>>() {
                    }.getType());
            if (dl_ingL != null && dl_ingL.size() > 0) {
                creamArticleCache.clear();
                creamArticleCache.putAll(dl_ingL);
            }
        }
    }
    }
