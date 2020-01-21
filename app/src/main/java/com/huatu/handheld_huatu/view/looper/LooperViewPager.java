package com.huatu.handheld_huatu.view.looper;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.huatu.handheld_huatu.listener.OnAdvItemClickListener;

/**
 * @author zhaodongdong
 */
public class LooperViewPager extends ViewPager {
    private boolean isCanScroll = true;
    private boolean mCanLoop = true;
    private LooperAdapter mAdapter;
    private OnAdvItemClickListener mItemClickListener;
    private OnPageChangeListener mOutPageChangeListener;
    private float oldX = 0, newX = 0;
    private static final float SENS = 5;


    public LooperViewPager(Context context) {
        this(context, null);
    }

    public LooperViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.addOnPageChangeListener(onPageChangeListener);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousPosition = -1;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOutPageChangeListener != null) {
                if (position != mAdapter.getRealCount() - 1) {
                    mOutPageChangeListener.onPageScrolled(position, positionOffset,
                            positionOffsetPixels);
                } else {
                    if (positionOffset > 0.5) {
                        mOutPageChangeListener.onPageScrolled(0, 0, 0);
                    } else {
                        mOutPageChangeListener.onPageScrolled(position, 0, 0);
                    }
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            int realPosition = mAdapter.toRealPosition(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                if (mOutPageChangeListener != null) {
                    mOutPageChangeListener.onPageSelected(realPosition);
                }
            }
//            Log.e("1111", "onPageSelected: "+position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOutPageChangeListener != null) {
                mOutPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    public boolean isCanScroll() {
        return isCanScroll;
    }

    public void setCanScroll(boolean canScroll) {
        isCanScroll = canScroll;
    }

    public void setCanLoop(boolean canLoop) {
        this.mCanLoop = canLoop;
        if (!canLoop) {
            setCurrentItem(getRealItem(), false);
        }
        if (mAdapter == null) {
            return;
        }
        mAdapter.setCanLoop(canLoop);
        mAdapter.notifyDataSetChanged();
    }

    public void setAdapter(PagerAdapter pagerAdapter, boolean canLoop) {
        mAdapter = (LooperAdapter) pagerAdapter;
        mAdapter.setCanLoop(canLoop);
        mAdapter.setViewPager(this);
        super.setAdapter(mAdapter);
        setCurrentItem(getFirstItem(), false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isCanScroll && super.onInterceptTouchEvent(ev);

    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        mOutPageChangeListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            if (mItemClickListener != null) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = ev.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        newX = ev.getX();
                        if (Math.abs(oldX - newX) < SENS) {
                            mItemClickListener.onItemClick(getRealItem());
                        }
                        oldX = 0;
                        newX = 0;
                        break;
                }
            }
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    /**
     * @return real position
     */
    public int getRealItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    /**
     * @return first position
     */
    public int getFirstItem() {
        return mCanLoop ? mAdapter.getRealCount() : 0;
    }

    /**
     * @return last position
     */
    public int getLastItem() {
        return mAdapter.getRealCount() - 1;
    }

    /**
     * 设置itemClick监听
     *
     * @param itemClickListener listener
     */
    public void setItemClickListener(OnAdvItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}
