package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.huatu.handheld_huatu.R;
import com.huatu.library.PullToRefreshBase;

public class HomeRefreshNestedScrollView extends PullToRefreshBase<NestedScrollView> {

    private NestedScrollView scrollView;

    public HomeRefreshNestedScrollView(Context context) {
        super(context);
    }

    public HomeRefreshNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeRefreshNestedScrollView(Context context, Mode mode) {
        super(context, mode);
    }

    public HomeRefreshNestedScrollView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }


    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected NestedScrollView createRefreshableView(Context context, AttributeSet attrs) {
        // 中间可滚动的内容
        scrollView = (NestedScrollView) LayoutInflater.from(context).inflate(R.layout.home_scroll_view, this, false);
        return scrollView;
    }

    public NestedScrollView getScrollView() {
        return scrollView;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return false;
    }

    @Override
    protected boolean isReadyForPullStart() {
        return scrollView != null && scrollView.getScrollY() == 0;
    }
}
