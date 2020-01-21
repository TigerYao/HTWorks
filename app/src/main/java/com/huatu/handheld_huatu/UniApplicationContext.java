package com.huatu.handheld_huatu;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.mvpmodel.RewardInfoBean;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.tag.TagManager;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by saiyuan on 2016/12/22.
 */

public class UniApplicationContext {
    private static Application application;
    private static Context mContext;
    public static boolean isAppInBackground = false;
    private static Map<String, RewardInfoBean> rewardInfoBeanMap;
//    private static SharedPreferences rewardSp;
    private static PushAgent pushAgent;

    public static void setApplication(Application app) {
        application = app;
    }

    public static void setContext(Context con) {
        mContext = con;
    }

    public static Application getApplication() {
        return application;
    }

    public static Context getContext() {
        return mContext;
    }

    public static void setPushAgent(PushAgent agent) {
        pushAgent = agent;
        updatePushAgentTag();
    }

    public static void updatePushAgentTag() {
        if (UserInfoUtil.userId > 0&&(null!=pushAgent)){

            pushAgent.addAlias(String.valueOf(UserInfoUtil.userId), "personID", new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean b, String s) {}});
        }



        Observable.create(new Observable.OnSubscribe<Boolean>() {

            private Boolean longRunningOperation(){
                final String tag = SignUpTypeDataCache.getInstance().getCategoryTitle();
                if(pushAgent == null || TextUtils.isEmpty(tag)) {
                    return false;
                }
                pushAgent.getTagManager().addTags(new TagManager.TCallBack() {
                    @Override
                    public void onMessage(boolean isSuccess, ITagManager.Result result) {
                        LogUtils.i("set tag " + tag + ": " + isSuccess);
                        if(isSuccess) {
                            LogUtils.i("reset push tag success");
                        }
                        if(result != null) {
                            LogUtils.i("reset push tag reason: " + result.jsonString);
                        } else  {
                            LogUtils.i("reset push tag result == null");
                        }
                    }
                }, tag);
                return true;
            }

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(longRunningOperation());
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {  }

            @Override
            public void onError(Throwable e) {  }

            @Override
            public void onNext(Boolean downId) {  }
        });
    }

//    private static void ensureSp() {
//        if(rewardSp == null) {
//            rewardSp = UniApplicationContext.getContext().getSharedPreferences(
//                    "user_reward_map", Context.MODE_PRIVATE);
//        }
//    }

    public static void setRewardInfoMap(Map<String, RewardInfoBean> map) {
        rewardInfoBeanMap = map;
//        ensureSp();
//        if(rewardInfoBeanMap == null) {
//            return;
//        }
//        Set<String> keys = rewardInfoBeanMap.keySet();
//        if(keys == null) {
//            return;
//        }
//        Iterator<String> keyIterator = keys.iterator();
//        for(; keyIterator.hasNext();) {
//            String key = keyIterator.next();
//            if(TextUtils.isEmpty(key)) {
//                continue;
//            }
//            RewardInfoBean bean = rewardInfoBeanMap.get(key);
//            if(bean == null) {
//                continue;
//            }
//            if("ONCE".equalsIgnoreCase(bean.strategy)) {
//                rewardSp.edit().putBoolean(key + "_once", avatar).commit();
//            }
//        }
    }

    public static Map<String, RewardInfoBean> getRewardInfoMap() {
        return rewardInfoBeanMap;
    }

    public static RewardInfoBean getRewardInfo(String key) {
        if(rewardInfoBeanMap == null || TextUtils.isEmpty(key)) {
            return null;
        }
        return  rewardInfoBeanMap.get(key);
    }

//    /*
//    return vale: true：已提示奖励   false：未提示奖励
//     */
//    public static boolean getRewardInfoState(String key) {
//        if(TextUtils.isEmpty(key)) {
//            return true;
//        }
//        ensureSp();
//        return rewardSp.getBoolean(key, true);
//    }
//
//    public static void setRewardInfoState(String key) {
//        if(TextUtils.isEmpty(key)) {
//            return;
//        }
//        ensureSp();
//        rewardSp.edit().putBoolean(key, false);
//    }
}
