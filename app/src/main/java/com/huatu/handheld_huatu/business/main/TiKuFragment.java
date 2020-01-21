package com.huatu.handheld_huatu.business.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.mainfragment.SingleExamEssay;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ht-ldc on 2018/7/4.
 */

public class TiKuFragment extends BaseFragment {

    private LinearLayout ti_ku_essay;
    private LinearLayout ti_ku_home;

    private int mUserId;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMessage(MessageEvent event) {
        if (event.message == MessageEvent.HOME_FRAGMENT_CHANGE_TO_ESSAY) {
            //跳申论
            addEssayFragment();
            changeShow(1);
        } else if (event.message == MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_UPDATE_VIEW) {       // 更新View
            //跳行测
            changeShow(0);
        } else if (event.message == MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_SHOW_ARENA) {        // 申论下点击行测，显示行测
            //跳行测
            changeShow(0);
        }
    }

    private void changeShow(int type) {
        if (ti_ku_essay != null && ti_ku_home != null) {
            if (type == 0) {
                ti_ku_essay.setVisibility(View.GONE);
                ti_ku_home.setVisibility(View.VISIBLE);
            } else {
                ti_ku_essay.setVisibility(View.VISIBLE);
                ti_ku_home.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_ti_ku;
    }

    @Override
    protected void onInitListener() {
        super.onInitListener();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        mUserId = UserInfoUtil.userId;
        initView();
        if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
            addEssayFragment();
            addHomeFragment();
        } else {
            addHomeFragment();
        }

        if (SpUtils.getUserSubject() == Type.CS_ExamType.ESSAY_TESTS_FOR_CIVIL_SERVANTS) {
            changeShow(1);
        } else {
            changeShow(0);
        }
    }

    private void initView() {
        ti_ku_essay = rootView.findViewById(R.id.ll_tiku2);
        ti_ku_home = rootView.findViewById(R.id.ll_tiku1);
    }

    private void addEssayFragment() {
        Fragment essayFragment = getChildFragmentManager().findFragmentByTag("s");
        if (essayFragment == null) {
            essayFragment = new SingleExamEssay();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.ll_tiku2, essayFragment, "s").commit();
        }
    }

    private void addHomeFragment() {
        Fragment homeFragment = getChildFragmentManager().findFragmentByTag("h");
        if (homeFragment == null) {
            homeFragment = new HomeFragment();
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.ll_tiku1, homeFragment, "h").commit();
        }
    }


    public static TiKuFragment newInstance() {
        return new TiKuFragment();
    }

    @Override
    public void onVisibilityChangedToUser(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            checkUserStatus();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            checkUserStatus();
        }
    }

    private void checkUserStatus() {
        if (mUserId != UserInfoUtil.userId) {
            // 登录状态与非登录状态切换时强刷新分类
            mUserId = UserInfoUtil.userId;
            // 跳首页
            if (SpUtils.getUserCatgory() == Type.SignUpType.CIVIL_SERVANT) {
                addEssayFragment();
            }
            // 游客登录，刷新行测页面
            Fragment homeFragment = getChildFragmentManager().findFragmentByTag("h");
            if (homeFragment != null && homeFragment.isAdded() && (!homeFragment.isDetached())) {
                if (homeFragment instanceof HomeFragment) {
                    ((HomeFragment) homeFragment).getDataUpdateViews();
                }
            }
            // 游客登录，刷新申论页面，如果有的话
            Fragment essayFragment = getChildFragmentManager().findFragmentByTag("s");
            if (essayFragment != null && essayFragment.isAdded() && (!essayFragment.isDetached())) {
                if (essayFragment instanceof SingleExamEssay) {
                    ((SingleExamEssay) essayFragment).onLoadData();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
