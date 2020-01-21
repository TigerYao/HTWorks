package com.huatu.handheld_huatu.business.me.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.RadioButton;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsHtEventFragment;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 错题练习设置页面
 */

public class SettingErrorPaperDoCountFragment extends AbsHtEventFragment {

    @BindView(R.id.gl_count)
    GridLayout glCount;
    @BindView(R.id.gl_mode)
    GridLayout glMode;

    private int selectCount = 0;
    private int doMode = 0;             // 0、做题 1、背题

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_setting_error_paper_do_count_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        selectCount = SpUtils.getErrorQuestionSize();
        doMode = SpUtils.getErrorQuestionMode();
        initRadio();
    }

    private void initRadio() {

        final int countCount = glCount.getChildCount();
        for (int i = 0; i < countCount; i++) {
            RadioButton button = (RadioButton) glCount.getChildAt(i);
            if (button.getTag().equals(String.valueOf(selectCount))) {
                button.setChecked(true);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectCount = Integer.valueOf((String) v.getTag());
                    for (int i = 0; i < countCount; i++) {
                        View child = glCount.getChildAt(i);
                        if (!child.getTag().equals(String.valueOf(selectCount))) {
                            ((RadioButton) child).setChecked(false);
                        }
                    }
                }
            });
        }

        int styleCount = glMode.getChildCount();
        for (int i = 0; i < styleCount; i++) {
            RadioButton button = (RadioButton) glMode.getChildAt(i);
            if (button.getTag().equals(String.valueOf(doMode))) {
                button.setChecked(true);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doMode = Integer.valueOf((String) v.getTag());
                    if (doMode == 0) {
                        RadioButton button = glMode.findViewWithTag("1");
                        button.setChecked(false);
                    } else {
                        RadioButton button = glMode.findViewWithTag("0");
                        button.setChecked(false);
                    }
                }
            });
        }

    }

    @OnClick({R.id.root_view, R.id.iv_close, R.id.ll_card, R.id.tv_save})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_card:
                break;
            case R.id.tv_save:
                SpUtils.setErrorQuestionSize(selectCount);
                SpUtils.setErrorQuestionMode(doMode);
                ToastUtils.showShort("设置成功");
                StudyCourseStatistic.doExercisesSetting("错题重练", String.valueOf(selectCount), doMode == 0 ? "做题模式" : "背题模式");
            case R.id.root_view:
            case R.id.iv_close:
                mActivity.onBackPressed();
                break;
        }
    }


    @Override
    protected void onLoadData() {
        super.onLoadData();
    }

    public static SettingErrorPaperDoCountFragment newInstance(Bundle extra) {
        SettingErrorPaperDoCountFragment fragment = new SettingErrorPaperDoCountFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
