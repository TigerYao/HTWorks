package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsHtEventActivity;
import com.huatu.handheld_huatu.business.me.fragment.ErrorPapersListFragment;
import com.huatu.handheld_huatu.business.me.fragment.SettingErrorPaperDoCountFragment;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.me.ErrorPapersMessageEvent;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 错题库
 */
public class ErrorActivity extends AbsHtEventActivity {

    @BindView(R.id.ht_main_container)
    FrameLayout htoMainContainerId;
    @BindView(R.id.ht_main_error_layout)
    CommonErrorView layoutErrorView;

    private static final String TAG = "ErrorActivity";

    private int data;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public boolean onEventUpdate(BaseMessageEvent<ErrorPapersMessageEvent> event) {
        if (event == null || !(event.typeExObject instanceof ErrorPapersMessageEvent)) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
        if (super.onEventUpdateHelper(event)) {
            return true;
        }
        if (event.type == ErrorPapersMessageEvent.AEP_MSG_SettingErrorPaperDoCountFragment_EFORM_ErrorPapersListFragment) {
            showSettingErrorPaperDoCountFragment(null);
        }
        return true;
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.layout_activity_ht_container;
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return true;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        bindFragment();
    }

    private void bindFragment() {
        Bundle arg = new Bundle();
        ErrorPapersListFragment fragment = ErrorPapersListFragment.newInstance(arg);
        addFragment(ErrorPapersListFragment.class.getSimpleName(), fragment, R.id.ht_main_container, false, true,
                false);
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return R.id.ht_main_container;
    }

    @Override
    public void onLoadData() {

    }

    @Override
    protected void onStop() {
        super.onStop();
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

    public void showSettingErrorPaperDoCountFragment(Bundle arg) {
        if (arg == null) {
            arg = new Bundle();
        }
        arg.putInt("request_type", ErrorPapersMessageEvent.AEP_MSG_SettingErrorPaperDoCountFragment_EFORM_ErrorPapersListFragment);
        SettingErrorPaperDoCountFragment fragment = SettingErrorPaperDoCountFragment.newInstance(arg);
        addFragment(ErrorPapersListFragment.class.getSimpleName(), fragment, R.id.ht_main_container, true, false, false);
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

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, ErrorActivity.class);
        context.startActivity(intent);
    }

}
