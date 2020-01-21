package com.huatu.handheld_huatu.base;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017/9/12.
 */
public class SelectedLoadViewPager extends com.huatu.handheld_huatu.ui.ViewPagerWrapper {
    private int currentPage = -1;
    private OnPageSelectedFlushListener onPageSelectedFlushListener;

    public SelectedLoadViewPager(Context context) {
        super(context);
    }

    public SelectedLoadViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        if(onPageSelectedFlushListener != null){
            if(offset == 0 && offsetPixels == 0){
                if(currentPage != position){
                    currentPage = position;
                    onPageSelectedFlushListener.onPageSelected(position);
                }
            }
        }
    }

    /**
     * 设置默认是否加载第一页(必须在加载数据之前调用)
     */
    public void setLoadFirstPage(boolean isLoadFirstPage){
        currentPage = isLoadFirstPage?-1:getCurrentItem();
    }

    /**
     * 设置页面选中刷新数据监听器
     */
    public void setOnPageSelectedFlushListener(OnPageSelectedFlushListener onPageSelectedFlushListener){
        this.onPageSelectedFlushListener = onPageSelectedFlushListener;
    }

    /**
     * 页面选中刷新数据的监听器
     */
    public interface OnPageSelectedFlushListener{
        void onPageSelected(int position);
    }
}