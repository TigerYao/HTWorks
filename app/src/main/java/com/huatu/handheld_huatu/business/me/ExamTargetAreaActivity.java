package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsHtEventActivity;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.me.fragment.SettingExamTargetAreaFragment;
import com.huatu.handheld_huatu.business.me.fragment.SettingExamTypeFragment;
import com.huatu.handheld_huatu.business.me.fragment.SettingExamTypeFromFirstFragment;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.Event;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.mvppresenter.me.ExamTargetAreaImpl;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxBus;
import com.huatu.handheld_huatu.view.CommonErrorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置目标考试地区
 */
public class ExamTargetAreaActivity extends AbsHtEventActivity {

    @BindView(R.id.ht_main_container)
    FrameLayout htoMainContainerId;
    @BindView(R.id.ht_main_error_layout)
    CommonErrorView layoutErrorView;
    private String mActivityFrom;
    public static final String ACTIVITYFROM = "activityfrom";
    public static final String NO_SET_REGISTER = "NO_SET_REGISTER";

    private static final String TAG = "ExamTargetAreaActivity";
    private ExamTargetAreaImpl mPresenter;
    private int data;
    protected boolean mIsQuickLogin;//游客快速登录

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(BaseMessageEvent<ExamTypeAreaMessageEvent> event) {
        if (event == null || event.typeExObject == null) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
        if (super.onEventUpdateHelper(event)) {
            return true;
        }
        if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_START_TO_MAINTAB_ACTIVITY_SUCCESS) {
            startToMainTabActivity();
        } else if (event.type == ExamTypeAreaMessageEvent.ETA_MSG_SettingExamTargetAreaFragment_EFORM_SettingExamTypeFragment) {
            showExamTypeAreaMessageEvent(event.extraBundle);
        }
        return true;
    }

    private void startToMainTabActivity() {
        if (NO_SET_REGISTER.equals(mActivityFrom) && !Method.isActivityFinished(this)) {
            //完善资料页面进入该页面,需要跳转首页以及关闭注册相关页面
            if (!mIsQuickLogin) {
                MainTabActivity.newIntent(this);
            }
            RxBus mRxBus = new RxBus();
            mRxBus.send(new Event.CloseLoginWRegisterEvent());
            mPresenter.finish();
        }
    }

    public void showExamTypeAreaMessageEvent(Bundle arg) {
        if (arg == null) {
            arg = new Bundle();
        }
        arg.putInt("request_type", requestType);
        arg.putString(ACTIVITYFROM, mActivityFrom);
        SettingExamTargetAreaFragment fragment = SettingExamTargetAreaFragment.newInstance(arg);
        addFragment(SettingExamTypeFragment.class.getSimpleName(), fragment, R.id.ht_main_container, true, true, false);
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.layout_activity_ht_container;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return R.id.ht_main_container;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (originIntent != null) {
            mActivityFrom = originIntent.getStringExtra(ACTIVITYFROM);
        }
        mIsQuickLogin = getIntent().getBooleanExtra(ArgConstant.QUICK_LOGIN, false);
        mPresenter = new ExamTargetAreaImpl(compositeSubscription);
        bindFragment();
    }

    private void bindFragment() {
        Bundle arg = new Bundle();
        arg.putInt("request_type", requestType);
        arg.putString(ACTIVITYFROM, mActivityFrom);
        if (requestType == ExamTypeAreaMessageEvent.ETA_MSG_ExamTargetAreaActivity_EFORM_CompleteUserInfoActivity) {
            SettingExamTypeFromFirstFragment fragment = SettingExamTypeFromFirstFragment.newInstance(arg);
            addFragment(SettingExamTypeFromFirstFragment.class.getSimpleName(), fragment, R.id.ht_main_container, false, true, false);
        } else {
            SettingExamTypeFragment fragment = SettingExamTypeFragment.newInstance(arg);
            addFragment(SettingExamTypeFragment.class.getSimpleName(), fragment, R.id.ht_main_container, false, true, false);
        }
    }

    @Override
    public void onLoadData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putInt("data", data);
        super.onSaveInstanceState(outState);
        LogUtils.d(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            data = savedInstanceState.getInt("data");
        }
        LogUtils.d(TAG, "onRestoreInstanceState");
    }

    protected void restoreSavedBundle(Bundle savedInstanceState) {

    }

    @OnClick({R.id.ht_main_error_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ht_main_error_layout:
                onClickErrorLayout();
                break;
        }
    }

    public void onClickErrorLayout() {
        onLoadData();
    }

    public void onLoadDataFailed() {
        layoutErrorView.updateUI();
        layoutErrorView.setVisibility(View.VISIBLE);
    }

    public static void newIntent(Context context) {
        newIntent(context, null, false);
    }

    public static void newIntent(Context context, String activityfrom) {
        newIntent(context, activityfrom, false);
    }

    /**
     * @param context
     * @param activityfrom 来源页面
     *                     如果是 ExamTargetAreaActivity.REGISTERACTIVITY,则选择地域成功时,跳转首页
     */
    public static void newIntent(Context context, String activityfrom, boolean isquickLogin) {
        Intent intent = new Intent(context, ExamTargetAreaActivity.class);
        intent.putExtra(ArgConstant.QUICK_LOGIN, isquickLogin);
        if (activityfrom != null) {
            intent.putExtra(ACTIVITYFROM, activityfrom);
            if (activityfrom == ExamTargetAreaActivity.NO_SET_REGISTER) {
                intent.putExtra("request_type", ExamTypeAreaMessageEvent.ETA_MSG_ExamTargetAreaActivity_EFORM_CompleteUserInfoActivity);
            }
        }
        context.startActivity(intent);
    }
}
