package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 */
public class CommonErrorViewExsc extends RelativeLayout {

    private Context mContext;
    private View rootView;

    @BindView(R.id.arena_exam_main_error_img)
    ImageView imgError;
    @BindView(R.id.arena_exam_main_error_tv)
    TextView tvError;

    private int resId = R.layout.common_sc_record_error_view_layout;

    public CommonErrorViewExsc(Context context) {
        super(context, null);
        mContext = context;
        init();
    }

    public CommonErrorViewExsc(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void updateUI(String msg) {
        if (NetUtil.isConnected()) {
            tvError.setText(msg);
            imgError.setImageResource(R.mipmap.sc_record_no_data);
        } else {
            ToastUtils.showShort("网络错误，请检查您的网络");
            tvError.setText("加载失败，请检查您的网络连接情况");
            imgError.setImageResource(R.mipmap.icon_network_fail);
        }
    }

    public void setErrorText(String text) {
        tvError.setText(text);
    }

    public void setErrorImage(int resId) {
        imgError.setImageResource(resId);
    }

    public void setOnReloadButtonListener(OnClickListener l) {
        this.setOnClickListener(l);
    }
}
