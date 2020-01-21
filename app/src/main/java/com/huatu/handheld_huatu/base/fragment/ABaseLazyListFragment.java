package com.huatu.handheld_huatu.base.fragment;

import android.os.Bundle;

import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;
import com.umeng.analytics.MobclickAgent;


/**
 * 懒加载的列表界面
 * Created by baron
 * Date : 2016/6/6 0006 10:43
 * Email: 5267621@qq.com
 */
public abstract class ABaseLazyListFragment<RESPONSE extends BaseListResponse> extends ABaseListFragment<RESPONSE> {

    private boolean isPrepared;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    /**
     * 第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
     */
    private boolean isFirstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    private boolean isFirstVisible = true;
    private boolean isFirstInvisible = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
            } else {
                onUserInvisible();
            }
        }
    }

    public synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }

    /**
     * 第一次fragment可见（进行初始化工作）
     */
    public void onFirstUserVisible() {
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    /**
     * fragment可见（切换回来或者onResume）
     */
    public void onUserVisible() {
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    /**
     * 第一次fragment不可见（不建议在此处理事件）
     */
    public void onFirstUserInvisible() {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    /**
     * fragment不可见（切换掉或者onPause）
     */
    public void onUserInvisible() {
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }
}
