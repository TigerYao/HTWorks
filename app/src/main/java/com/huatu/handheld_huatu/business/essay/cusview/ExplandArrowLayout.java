package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huatu.handheld_huatu.R;
import com.huatu.utils.StringUtils;

/**
 * Created by Administrator on 2019\7\11 0011.
 */

public class ExplandArrowLayout extends FrameLayout {

    public interface OnExplandStatusListener {
        void onExplandClick(boolean isExpland, View explandLayout);
    }

    OnExplandStatusListener mOnExplandStatusListener;

    public void setOnExplandStatusListener(OnExplandStatusListener explandStatusListener) {
        this.mOnExplandStatusListener = explandStatusListener;
    }

    public ExplandArrowLayout(Context context) {
        this(context, null);
    }

    public ExplandArrowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExplandArrowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    ImageView mExplandView;

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mExplandView = (ImageView) getChildAt(getChildCount() - 1);
        mExplandView.setTag(R.id.reuse_tag2, "1");//1展开
    }

    View mExplandLayout;

    public ExplandArrowLayout setCanExplandLayout(View layout) {
        mExplandLayout = layout;
        return this;
    }

    private void init() {
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldStatus = StringUtils.valueOf(mExplandView.getTag(R.id.reuse_tag2));

                mExplandView.setImageResource(oldStatus.equals("1") ? R.mipmap.homef_title_pop_down1
                        : R.mipmap.homef_title_pop_up1);

                mExplandView.setTag(R.id.reuse_tag2, oldStatus.equals("1") ? "0" : "1");
                if (null != mExplandLayout) {
                    mExplandLayout.setVisibility(!oldStatus.equals("1") ? VISIBLE : GONE);
                }
                if (null != mOnExplandStatusListener) {
                    mOnExplandStatusListener.onExplandClick(!oldStatus.equals("1"), ExplandArrowLayout.this);
                    return;
                }

            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mOnExplandStatusListener)
            mOnExplandStatusListener = null;
    }
}
