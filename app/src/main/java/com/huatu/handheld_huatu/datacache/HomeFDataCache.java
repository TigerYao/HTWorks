package com.huatu.handheld_huatu.datacache;


import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;

/**
 */
public class HomeFDataCache {
    public AdvertiseConfig scadvertiseConfig;
    public static volatile HomeFDataCache instance = null;
    public static HomeFDataCache getInstance() {
        synchronized (HomeFDataCache.class) {
            if (instance == null) {
                instance = new HomeFDataCache();
            }
        }
        return instance;
    }

    private HomeFDataCache() {
    }

    public void setScAdvertiseConfig(AdvertiseConfig advertiseConfig) {
        if(advertiseConfig!=null &&  "ztk://match/detail".equals(advertiseConfig.target)){
            scadvertiseConfig=advertiseConfig;
        }
    }

    public void  clearCacheErrorData(){

    }
}
