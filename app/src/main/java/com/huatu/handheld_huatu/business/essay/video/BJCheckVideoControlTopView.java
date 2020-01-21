package com.huatu.handheld_huatu.business.essay.video;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.baijiahulian.player.playerview.IPlayerTopContact;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.examfragment.OnSwitchListener;

public class BJCheckVideoControlTopView implements IPlayerTopContact.TopView, View.OnClickListener {

    private Context mContext;
    private View rootView;
    private CheckBJPlayerView mPlayerView;
    private IPlayerTopContact.IPlayer iPlayer;

    private ImageView ivBack;                       // 返回按钮

    private OnSwitchListener onSwitchListener;

    BJCheckVideoControlTopView(View rootView, CheckBJPlayerView mPlayerView) {
        this.mContext = rootView.getContext();
        this.rootView = rootView;
        this.mPlayerView = mPlayerView;

        initView();
        initClick();
    }

    private void initView() {
        ivBack = rootView.findViewById(R.id.iv_back);
    }

    private void initClick() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onSwitchListener.onBackPressed();
                break;
        }
    }

    @Override
    public void onBind(IPlayerTopContact.IPlayer iPlayer) {
        this.iPlayer = iPlayer;
    }

    @Override
    public void setTitle(String s) {

    }

    @Override
    public void setOrientation(int i) {
        if (mPlayerView.isFullScreen()) {
            ivBack.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void setOnBackClickListener(View.OnClickListener onClickListener) {

    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }
}
