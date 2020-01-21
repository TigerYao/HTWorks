package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.util.Log;

import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saiyuan on 2016/10/13.
 * https://blog.csdn.net/lixuesong13/article/details/79065271
 */
public class ActivityStack {
    private static ActivityStack instance;
    private List<Activity> activityList = new ArrayList<Activity>();

    /**
     * 获取实例
     *
     * @return
     */
    public synchronized static ActivityStack getInstance() {
        if (instance == null) {
            instance = new ActivityStack();
        }
        return instance;
    }

    private ActivityStack() {

    }

    /**
     * 返回当前栈数据拷贝
     *
     * @return
     */
    public List<Activity> getStackCopy() {
        List<Activity> stackCopy = new ArrayList<Activity>();
        if (activityList != null) {
            stackCopy.addAll(activityList);
        }
        return stackCopy;
    }

    /**
     * 添加
     *
     * @param activity
     */
    public void add(Activity activity) {
        if (activity != null) {
            activityList.add(activity);
        }
    }

    /**
     * 删除
     *
     * @param activity
     */
    public void remove(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
        }
    }

    /**
     * finish 掉所有activity
     *
     * @deprecated 不推荐依赖Activity栈, 因为系统可能会把后台的Activity回调掉, 但这种Activity会从Activity栈内删除掉
     */
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            if(!Method.isActivityFinished(activity)) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    public void finishAllActivityExMain() {
        Activity mainTabActivity=null;
        for (Activity activity : activityList) {
            LogUtils.d("ActivityStack", activity.getClass().getName());
            if(activity!=null&&activity.getClass().getName().contains("MainTabActivity")){
                LogUtils.d("ActivityStack", "MainTabActivity");
                mainTabActivity=activity;
            }else if(!Method.isActivityFinished(activity)) {
                activity.finish();
            }
        }
        activityList.clear();
        if(mainTabActivity!=null){
            activityList.add(mainTabActivity);
        }
    }


    /**
     * finish 掉所有activity，只保存exceptActivity
     *
     * @param exceptActivity
     * @deprecated 不推荐依赖Activity栈, 因为系统可能会把后台的Activity回调掉, 但这种Activity会从Activity栈内删除掉
     */
    public void finishAllActivityExcept(BaseActivity exceptActivity) {
        boolean exist = false;
        for (Activity activity : activityList) {
            if (activity != exceptActivity) {
                activity.finish();
            } else {
                exist = true;
            }
        }
        activityList.clear();
        if (exist) {
            activityList.add(exceptActivity);
        }
    }

    /**
     * finish 掉所有activity，只保存exceptActivity
     *
     * @param exceptActivityCls
     * @deprecated 不推荐依赖Activity栈, 因为系统可能会把后台的Activity回调掉, 但这种Activity会从Activity栈内删除掉
     */
    public void finishAllActivityExcept(Class<? extends BaseActivity> exceptActivityCls) {
        Activity exceptActivity = null;
        for (Activity activity : activityList) {
            if (activity.getClass() != exceptActivityCls) {
                activity.finish();
            } else {
                if (exceptActivity != null && exceptActivity != activity) {
                    exceptActivity.finish();
                }
                exceptActivity = activity;
            }
        }
        activityList.clear();
        if (exceptActivity != null) {
            activityList.add(exceptActivity);
        }
    }

    /**
     * 获取当前的顶层activity
     */
    public Activity getTopActivity() {
        if(activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        }
        return null;
    }

    /**
     * 获取堆栈大小
     *
     * @return
     * @deprecated 不推荐依赖Activity栈, 因为系统可能会把后台的Activity回调掉, 但这种Activity会从Activity栈内删除掉
     */
    public int getSize() {
        return activityList.size();
    }

    public boolean hasRootActivity(){
        if(ArrayUtils.isEmpty(activityList)) return false;
        if(activityList.get(0).getClass().getCanonicalName().equals("com.huatu.handheld_huatu.business.main.MainTabActivity")) return true;
        return false;
    }

    /**
     * 获取Activity
     *
     * @param cls 类名
     * @return activity
     */
    public Activity getActivity(Class<?> cls) {
        if (activityList != null && cls != null) {
            for (Activity activity : activityList) {
                if (activity.getClass().equals(cls)) {
                    return activity;
                }
            }
        }
        return null;
    }
}
