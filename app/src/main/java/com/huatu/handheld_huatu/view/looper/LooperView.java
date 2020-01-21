package com.huatu.handheld_huatu.view.looper;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.view.looper.holder.LooperViewHolderCreator;
import com.huatu.handheld_huatu.listener.OnAdvItemClickListener;
import com.huatu.utils.ArrayUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaodongdong
 */
public class LooperView<T> extends LinearLayout {
    private LooperViewPager mLooper_viewPager;
    private ViewPagerScroll mScroll;
    private AdvSwitchTask mAdvSwitchTask;
    private List<T> mDatas;
    private int[] mIndicatorIds;
    private ArrayList<ImageView> mPointViews = new ArrayList<>();
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private IndicatorChangeListener mIndicatorChangeListener;
    //是否自动翻页
    private boolean mCanLoop;
    //是否正在翻页
    private boolean truning;
    //是否可以翻页
    private boolean canTurn = false;
    private long autoTurningTime;
    private ViewGroup mIndicator_group;

    private enum IndicatorOrientation {
        LEFT, CENTER, RIGHT
    }

    public LooperView(Context context) {
        this(context, null);
    }

    public LooperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LooperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LooperView);
        mCanLoop = typedArray.getBoolean(R.styleable.LooperView_canLoop, true);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.loop_viewpager, this, true);
        mLooper_viewPager = (LooperViewPager) view.findViewById(R.id.looper_ViewPager);
        mIndicator_group = (ViewGroup) findViewById(R.id.indicator_group);
        initViewPagerScroll();
        mAdvSwitchTask = new AdvSwitchTask(this);
    }

    /**
     * 设置viewpager的滑动速度
     */
    private void initViewPagerScroll() {
        Field scroll = null;
        try {
            scroll = ViewPager.class.getDeclaredField("mScroller");
            scroll.setAccessible(true);
            mScroll = new ViewPagerScroll(mLooper_viewPager.getContext());
            scroll.set(mLooper_viewPager, mScroll);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class AdvSwitchTask implements Runnable {
        final WeakReference<LooperView> mReference;

        public AdvSwitchTask(LooperView looperView) {
            mReference = new WeakReference<LooperView>(looperView);
        }

        @Override
        public void run() {
            LooperView looperView = mReference.get();
            if (looperView != null) {
                if (looperView.mLooper_viewPager != null && looperView.truning) {
                    int page = looperView.mLooper_viewPager.getCurrentItem() + 1;
                    looperView.mLooper_viewPager.setCurrentItem(page);
                    looperView.postDelayed(looperView.mAdvSwitchTask, looperView.autoTurningTime);
                }
            }
        }
    }

    /**
     * 通知数据变化
     */
    public void notifyDataSetChanged() {
        mLooper_viewPager.getAdapter().notifyDataSetChanged();
        if (mIndicatorIds != null) {
            setPagerIndicator(mIndicatorIds);
        }
    }

    /**
     * 是否支持手动滑动
     *
     * @return boolean
     */
    public boolean isManualPagerable() {
        return mLooper_viewPager.isCanScroll();
    }

    /**
     * 设置是否可以手动滑动
     *
     * @param manual boolean
     */
    public void setMaualPagerable(boolean manual) {
        mLooper_viewPager.setCanScroll(manual);
    }

    /**
     * 获取当前页面的index
     */
    public int getCurrentItem() {
        if (mLooper_viewPager != null) {
            return mLooper_viewPager.getRealItem();
        }
        return -1;
    }

    /**
     * 设置当前页面
     *
     * @param index position
     */
    public void setCurrentItem(int index) {
        if (mLooper_viewPager != null) {
            mLooper_viewPager.setCurrentItem(index);
        }
    }

    /**
     * 获取viewpager
     *
     * @return viewpager
     */
    public LooperViewPager getViewPager() {
        return mLooper_viewPager;
    }

    public void setCanLoop(boolean canLoop) {
        mCanLoop = canLoop;
        mLooper_viewPager.setCanLoop(canLoop);
    }

    /**
     * 开始翻页
     *
     * @param turningTime step time
     * @return looperView
     */
    public LooperView startTurning(long turningTime) {
        //如果正在翻页,先停下
        if (truning) {
            stopTurning();
        }
        //设置可以翻页,并开启翻页
        canTurn = true;
        autoTurningTime = turningTime;
        truning = true;
        postDelayed(mAdvSwitchTask, autoTurningTime);
        return this;
    }

    /**
     * 停止looper
     */
    public void stopTurning() {
        truning = false;
        removeCallbacks(mAdvSwitchTask);
    }

    /**
     * 设置翻转动画
     *
     * @param transformer pageTransformer
     * @return looperView
     */
    public LooperView setPageTransformer(ViewPager.PageTransformer transformer) {
        mLooper_viewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * 是否正在翻页
     *
     * @return boolean
     */
    public boolean isTruning() {
        return truning;
    }

    public LooperView setPages(LooperViewHolderCreator holderCreator, List<T> datas) {
        mDatas = datas;
        LooperAdapter pagerAdapter = new LooperAdapter(holderCreator, datas);
        mLooper_viewPager.setAdapter(pagerAdapter, mCanLoop);
        if (mIndicatorIds != null) {
            setPagerIndicator(mIndicatorIds);
        }
        return this;
    }

    /**
     * 初始化indicator资源图片
     *
     * @param indicatorIds id
     * @return loopView
     */
    public LooperView setPagerIndicator(int[] indicatorIds) {
        mIndicator_group.removeAllViews();
        mPointViews.clear();
        mIndicatorIds = indicatorIds;
        if (mDatas == null) {
            return this;
        }
        for (int i = 0; i < mDatas.size(); i++) {
            //翻页指示器的point
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty()) {
                pointView.setImageResource(indicatorIds[1]);
            } else {
                pointView.setImageResource(indicatorIds[0]);
            }
            mPointViews.add(pointView);
            mIndicator_group.addView(pointView);
        }
        mIndicatorChangeListener = new IndicatorChangeListener(mPointViews, indicatorIds);
        mLooper_viewPager.addOnPageChangeListener(mIndicatorChangeListener);
        mIndicatorChangeListener.onPageSelected(mLooper_viewPager.getRealItem());
        if (mOnPageChangeListener != null) {
            mIndicatorChangeListener.setOnPageChangeListener(mOnPageChangeListener);
        }
        return this;
    }

    /**
     * @param orientation orientation
     * @return LooperView
     */
    public LooperView setIndicatorOrientation(IndicatorOrientation orientation) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIndicator_group.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, orientation == IndicatorOrientation.LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, orientation == IndicatorOrientation.RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, orientation == IndicatorOrientation.CENTER ? RelativeLayout.TRUE : 0);
        mIndicator_group.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 设置底部indicator是否可见
     *
     * @param visible boolean
     * @return looperView
     */
    public LooperView setIndicatorVisible(boolean visible) {
        mIndicator_group.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        return this;
    }

    /**
     * 设置item点击事件
     *
     * @param itemClickListener listener
     * @return looperView
     */
    public LooperView setOnItemClickListener(OnAdvItemClickListener itemClickListener) {
        if (itemClickListener == null) {
            mLooper_viewPager.setItemClickListener(null);
            return this;
        }
        mLooper_viewPager.setItemClickListener(itemClickListener);
        return this;
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return mOnPageChangeListener;
    }

    /**
     * 设置翻页监听器
     *
     * @param onPageChangeListener onPageChangeListener
     * @return looperView
     */
    public LooperView setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
        //如果有默认的监听器(即是使用默认的翻页指示器),则把用户设置的依附到默认的上面,否则就直接设置
        if (mIndicatorChangeListener != null) {
            mIndicatorChangeListener.setOnPageChangeListener(onPageChangeListener);
        } else {
            mLooper_viewPager.addOnPageChangeListener(onPageChangeListener);
        }
        return this;
    }

    /**
     * 设置viewpager滚动速度
     *
     * @param scrollDuration duration
     */
    public void setScrollDuration(int scrollDuration) {
        mScroll.setScrollDuration(scrollDuration);
    }

    /**
     * 获取scrollDuration
     *
     * @return int
     */
    public int getScrollDuration() {
        return mScroll.getScrollDuration();
    }


    /**
     * 触碰控件时,翻页应停止,离开的时候,如果之前是开启了翻页则重新启动翻页
     *
     * @param ev motionEvent
     * @return boolean
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL ||
                action == MotionEvent.ACTION_OUTSIDE) {
            //开始翻页
            if (canTurn) {
                startTurning(autoTurningTime);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            //停止翻页
            if (canTurn) {
                stopTurning();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(ArrayUtils.isEmpty(mDatas)) return;
        if (visibility == VISIBLE) {
            if (canTurn) {
                startTurning(autoTurningTime);
            }
        } else {
            if (canTurn) {
                stopTurning();
            }
        }
    }

}
