package com.huatu.handheld_huatu.business.arena.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by saiyuan on 2016/10/25.
 * 休息一会儿吧
 */
public class ArenaRestActivity extends BaseActivity {

    @BindView(R.id.tv_rest)
    TextView tvRest;

    @BindView(R.id.ll_un_do)
    LinearLayout llUnDo;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_un_do)
    TextView tvUnDo;

    @BindView(R.id.tv_any)
    TextView tvAny;

    @BindView(R.id.tv_five_tip)
    TextView tvFiveTip;                 // 五分钟未操作暂停页面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int nightMode = SpUtils.getDayNightMode();
        if (nightMode == 1) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int onSetRootViewId() {
        // 全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return R.layout.activity_arena_reset_layout;
    }

    @Override
    protected void onInitView() {
        int type = originIntent.getIntExtra("type", 0);
        if (type == 0) {                 // 手动暂停休息
            int count = originIntent.getIntExtra("count", 0);
            int doneCount = originIntent.getIntExtra("doneCount", 0);

            if (count != 0) {
                tvCount.setText(String.valueOf(count));
                tvUnDo.setText(String.valueOf(count - doneCount));
            } else {
                llUnDo.setVisibility(View.GONE);
            }
        } else if (type == 1) {           // 五分钟未做，自动暂停休息
            tvRest.setVisibility(View.GONE);
            llUnDo.setVisibility(View.GONE);
            tvAny.setVisibility(View.GONE);
            tvFiveTip.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.root_view)
    public void onClickContinue() {
        this.setResult(RESULT_OK);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        onClickContinue();
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }
}
