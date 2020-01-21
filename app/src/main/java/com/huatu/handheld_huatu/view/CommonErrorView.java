package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2016/10/28.
 */
public class CommonErrorView extends RelativeLayout {
    private Context mContext;
    private View rootView;
    @BindView(R.id.arena_exam_main_error_img)
    ImageView imgError;
    @BindView(R.id.back_finish)
    ImageView back_finish;
    @BindView(R.id.arena_exam_main_error_tv)
    TextView tvError;

    private int resId = R.layout.common_error_view_layout;

    public CommonErrorView(Context context) {
        super(context, null);
        mContext = context;
        init();
    }

    public CommonErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);
        if (back_finish != null) {
            back_finish.setVisibility(GONE);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void updateUI() {
        if (NetUtil.isConnected()) {
            tvError.setText("加载失败，点击屏幕重新加载");
            imgError.setImageResource(R.drawable.down_no_num);
        } else {
            ToastUtils.showShort("网络错误，请检查您的网络");
            tvError.setText("加载失败，请检查您的网络连接情况");
            imgError.setImageResource(R.drawable.icon_common_net_unconnected);
        }
        setErrorImageVisible(true);
    }

    public void setErrorText(CharSequence text) {
        tvError.setText(text);
    }

    public void setErrorText(String text) {
        tvError.setText(text);
    }

    public void setErrorImage(int resId) {
        imgError.setImageResource(resId);
    }

    public void setErrorImageMargin(int top) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
                imgError.getLayoutParams();
        lp.topMargin = DisplayUtil.dp2px(top);
        imgError.setLayoutParams(lp);
    }

    public void setErrorImageVisible(boolean isShow) {
        if (isShow) {
            imgError.setVisibility(VISIBLE);
        } else {
            imgError.setVisibility(GONE);
        }
    }

    public void setback_finishVis(int v) {
        if (back_finish != null) {
            back_finish.setVisibility(v);
        }
    }

    public void setOnReloadButtonListener(OnClickListener l) {
        this.setOnClickListener(l);
    }
}
