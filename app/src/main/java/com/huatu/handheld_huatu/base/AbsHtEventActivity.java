package com.huatu.handheld_huatu.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

import butterknife.OnClick;


public abstract class AbsHtEventActivity extends BaseActivity {

    private static final String TAG = "AbsHtEventActivity";
    protected int requestType;
    protected Bundle extraArgs = null;
    public boolean onEventUpdateHelper(BaseMessageEvent<?> event) {
        if (event == null) {
            return false;
        }
        LogUtils.d(TAG, getClass().getSimpleName() + " onEventUIUpdate  event.type " + event.type);
        if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKFINISH) {
            finish();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ON_BACKPRESS) {
            onBackPressed();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_SHOW_PROGRESS_BAR) {
            showProgress();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_DISMISS_PROGRESS_BAR) {
            hideProgress();
        } else if (event.type == BaseMessageEvent.BASE_EVENT_TYPE_ONLOAD_DATA_FAILED) {
            onLoadDataFailed();
        }else {
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportProgress(true);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        if (originIntent != null) {
            requestType = originIntent.getIntExtra("request_type", 0);
            extraArgs = originIntent.getBundleExtra("extra_args");
            if (extraArgs == null) {
                extraArgs = new Bundle();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle arg) {
        switch (clickId) {
            case R.id.ht_main_container:
                break;
        }
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



    protected void onLoadDataFailed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {
        if (data == null) {
            return;
        }
    }
}
