package com.huatu.handheld_huatu.business.matches.cache;

/**
 * 模考数据缓存单例
 */
public class MatchCacheData {

    private static MatchCacheData instence;

    public static MatchCacheData getInstance(){
        if (instence == null){
            synchronized (MatchCacheData.class){
                if (instence == null){
                    instence = new MatchCacheData();
                }
            }
        }
        return instence;
    }

    private MatchCacheData(){

    }

    // 哪里跳转到模考大赛页的，"前项页面"，用于神策数据埋点
    public String matchPageFrom = "";
}
