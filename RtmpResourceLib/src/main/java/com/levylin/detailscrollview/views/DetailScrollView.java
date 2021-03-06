package com.levylin.detailscrollview.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.Scroller;


import com.gensee.rtmpresourcelib.R;
import com.levylin.detailscrollview.views.listener.OnScrollBarShowListener;
import com.tencent.smtt.sdk.WebView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by LinXin on 2017/3/25.
 * 1.
 */
public class DetailScrollView extends ViewGroup {

    private static final String TAG = DetailScrollView.class.getSimpleName();
    private static boolean isDebug = false;

    public static final int DIRECT_BOTTOM = 1;
    public static final int DIRECT_TOP = -1;

    private IDetailListView mListView;
    private IDetailWebView mWebView;
    private Scroller mScroller;
    private float mLastY;
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int maxScrollY;
    private boolean isBeingDragged;
    private boolean isTouched;
    private MyScrollBarShowListener mScrollBarShowListener;

    private int oldScrollY;
    private int oldWebViewScrollY;
    private int mTouchSlop;

    public DetailScrollView(Context context) {
        super(context);
        init(context);
    }

    public DetailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DetailScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mTouchSlop = configuration.getScaledTouchSlop();
        mScrollBarShowListener = new MyScrollBarShowListener();

        //初始化滚动条
        boolean hasScrollBarVerticalThumb = false;//有的手机没有滚动条，开启滚动条的话会崩溃
        try {


            TypedArray a = context.obtainStyledAttributes(R.styleable.View);
            Method method = View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class);
            method.setAccessible(true);
            method.invoke(this, a);
            a.recycle();
            Field field = View.class.getDeclaredField("mScrollCache");
            field.setAccessible(true);
            Object mScrollCache = field.get(this);
            if (mScrollCache != null) {
                Field scrollbarField = mScrollCache.getClass().getDeclaredField("scrollBar");
                scrollbarField.setAccessible(true);
                Object scrollBar = scrollbarField.get(mScrollCache);
                Field verticalThumbField = scrollBar.getClass().getDeclaredField("mVerticalThumb");
                verticalThumbField.setAccessible(true);
                Object verticalThumb = verticalThumbField.get(scrollBar);
                hasScrollBarVerticalThumb = verticalThumb != null;//有滚动条才显示滚动条
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hasScrollBarVerticalThumb) {
            setVerticalScrollBarEnabled(true);
            setScrollbarFadingEnabled(true);
            setWillNotDraw(false);//awakenScrollBars的时候会调用invalidate，设置这个为false，可以使invalidate调用draw方法，从而达到画进度条
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof IDetailListView) {
                mListView = (IDetailListView) child;
            } else if (child instanceof IDetailWebView) {
                mWebView = (IDetailWebView) child;
            }
        }
        if (mListView != null) {
            mListView.setScrollView(this);
            mListView.setOnScrollBarShowListener(mScrollBarShowListener);
        }
        if (mWebView != null) {
            mWebView.setScrollView(this);
            mWebView.setOnScrollBarShowListener(mScrollBarShowListener);
            observeWebViewContentHeightChange();
        }
    }

    /**
     * 监听webView内容高度发送变化
     * 做法是如果webView的底部已经不在scrollview的底部，则把webView扩展至底部与scrollview重合
     * 反之则不处理
     */
    private void observeWebViewContentHeightChange() {
        if (mWebView instanceof DetailX5WebView) {
            View view = ((DetailX5WebView) mWebView).getView();
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                private int mOldWebViewContentHeight;
                private Rect outRect = new Rect();

                @Override
                public boolean onPreDraw() {
                    WebView webView = (WebView) mWebView;
                    webView.getGlobalVisibleRect(outRect);
                    int distBottom = DetailScrollView.this.getHeight() - outRect.height();
                    if (distBottom <= 0
                            || DetailScrollView.this.getScrollY() == 0//没触发滑动的时候不扩展
                            || webView.getHeight() < DetailScrollView.this.getHeight())//webView本身高度就小于scrollview，也不扩展
                        return true;
                    int newWebViewContentHeight = webView.getContentHeight();
                    if (mOldWebViewContentHeight == newWebViewContentHeight)
                        return true;
                    mOldWebViewContentHeight = newWebViewContentHeight;
                    LayoutParams lp = webView.getLayoutParams();
                    lp.height = webView.getMeasuredHeight() + distBottom;
                    webView.setLayoutParams(lp);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int parentLeft = getPaddingLeft();
        int parentHeight = b - t;
        int lastBottom = getPaddingTop();
        maxScrollY = 0;

        int webHeight = 0;
        int listHeight = 0;
        int otherHeight = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                final int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

                if (height == 0) {
                    height = parentHeight;//默认webView高度等于scrollview高度
                }

                int childLeft = parentLeft + lp.leftMargin;
                int childTop = lastBottom + lp.topMargin;
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
                lastBottom = childTop + height + lp.bottomMargin;
                maxScrollY += lp.topMargin;
                maxScrollY += lp.bottomMargin;
                if (child instanceof IDetailWebView) {
                    webHeight = height;
                } else if (child instanceof IDetailListView) {
                    listHeight = height;
                } else {
                    otherHeight += height;
                }
            }
        }

        //简化最大可滑动区域的计算方式
        maxScrollY = webHeight + listHeight + otherHeight - parentHeight;
        if (maxScrollY < 0) {
            maxScrollY = 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        isTouched = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE;
        float y = event.getY();
        boolean isAtTop = getScrollY() >= maxScrollY;//MyScroll是否在头部
        boolean isAtBottom = getScrollY() <= 0;//MyScroll是否在底部
        acquireVelocityTracker(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                if (!mScroller.isFinished()) {//按下去的时候就要取消动画，在move的时候取消动画就太迟了，会造成已经进入webView滑动事件，同时触发MyScrollView的滚动事件的奇葩bug
                    mScroller.abortAnimation();
                }
                LogE(TAG + ".onTouchEvent.DOWN.......mLastY=" + mLastY);
                break;
            case MotionEvent.ACTION_MOVE:
                float delta = y - mLastY;
                int dy = adjustScrollY((int) -delta);
                LogE(TAG + ".onTouchEvent.Move.......dy=" + dy + "\n"
                        + ",delta=" + delta + "\n"
                        + ",y=" + y + "\n"
                        + ",mLastY=" + mLastY + "\n"
                        + ",isAtBottom=" + isAtBottom + "\n"
                        + ",isAtTop=" + isAtTop + "\n"
                        + ",getScrollY()=" + getScrollY());
                if (dy != 0) {
                    if (mListView.canScrollVertically(DIRECT_TOP) && isAtTop) {//因为ListView上滑操作导致ListView可以继续下滑，故要先ListView滑到顶部，再滑动MyScrollView
                        mListView.customScrollBy((int) -delta);
                    } else if (mWebView.canScrollVertically(DIRECT_BOTTOM) && isAtBottom) {//因为WebView下滑，导致WebView可以继续上滑，故要先让WebView滑到底部，再滑动MyScrollView
                        mWebView.customScrollBy(-(int) delta);
                    } else {//当ListView处在顶部，WebView处在底部时，滑动MyScrollView
                        customScrollBy(dy);
                    }
                } else {//dy==0代表是滑动到顶部或者底部了
                    if (delta < 0 && isAtTop) {//ListView上滑操作。。。。代表是向上滑，应该让ListView跟着向上滑
                        mListView.customScrollBy((int) -delta);
                    } else if (delta > 0 && isAtBottom) {//向下滑，让WebView跟着向下滑
                        mWebView.customScrollBy(-(int) delta);
                    }
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int yVelocity = (int) velocityTracker.getYVelocity(0);
                LogE(TAG + ".onTouchEvent.ACTION_UP.......action=" + action + "\n"
                        + ",yVelocity=" + yVelocity + "\n"
                        + ",isAtBottom=" + isAtBottom + "\n"
                        + ",isAtTop=" + isAtTop + "\n"
                        + ",getScrollY()=" + getScrollY());
                if ((Math.abs(yVelocity) > mMinimumVelocity)) {
                    if (isAtTop) {//因为ListView可以继续下滑，故先让丫的处理fling事件
                        if (mListView.canScrollVertically(DIRECT_TOP)) {
                            LogE(TAG + ".onTouchEvent.ACTION_UP.......action=" + action + ",listview fling:" + (-yVelocity));
                            mListView.startFling(-yVelocity);
                        }
                    } else if (isAtBottom) {//因为WebView可以继续上滑，故让丫的处理fling事件
                        if (mWebView.canScrollVertically(DIRECT_BOTTOM)) {
                            LogE(TAG + ".onTouchEvent.ACTION_UP.......action=" + action + ",webview fling:" + (-yVelocity));
                            mWebView.startFling(-yVelocity);
                        }
                    } else {//上面两个没有处理fling事件，才轮到MyScrollView去处理
                        LogE(TAG + ".onTouchEvent.ACTION_UP.......action=" + action + ",scrollview fling:" + (-yVelocity));
                        fling(-yVelocity);
                    }
                }
                releaseVelocityTracker();
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int childCount = getChildCount();
        if (childCount < 2) {
            return false;
        }
        if (!touchInView((View) mWebView, ev) && !touchInView((View) mListView, ev)) {
            return false;
        }
        boolean isCanScrollBottom = getScrollY() < maxScrollY && mScroller.isFinished();//是否可以向下滑
        boolean isCanScrollTop = getScrollY() > 0 && mScroller.isFinished();//是否可以向上滑
        boolean isWebViewCanScrollBottom = mWebView.canScrollVertically(DIRECT_BOTTOM);//Web可否继续下滑
        boolean isListViewCanScrollTop = mListView.canScrollVertically(DIRECT_TOP);//ListView可否继续上滑
        LogE("onInterceptTouchEvent.getScrollY=" + getScrollY() + "\n"
                + ",maxScrollY=" + maxScrollY + "\n"
                + ",mScroller.isFinished()=" + mScroller.isFinished() + "\n"
                + ",isCanScrollBottom=" + isCanScrollBottom + "\n"
                + ",isCanScrollTop=" + isCanScrollTop);
        final int action = ev.getAction();
        acquireVelocityTracker(ev);
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mLastY = (int) ev.getY();
                // 在Fling状态下点击屏幕
                isBeingDragged = !mScroller.isFinished();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int y = (int) ev.getY();
                int deltaY = (int) (y - mLastY);
                LogE("onInterceptTouchEvent.Move.......deltaY=" + deltaY + ",mTouchSlop=" + mTouchSlop);
                if (Math.abs(deltaY) < mTouchSlop)
                    return false;
                if (deltaY < 0) { // Scroll To Bottom
                    if (touchInView((View) mWebView, ev)) {
                        isBeingDragged = !isWebViewCanScrollBottom && isCanScrollBottom;// WebView不可以继续向下滚动，否则由这个WebView自己处理滚动
                        LogE("onInterceptTouchEvent.Move.......触摸点在WebView...deltaY=" + deltaY + "\n"
                                + ",isWebViewCanScrollBottom=" + isWebViewCanScrollBottom + "\n"
                                + ",isCanScrollBottom=" + isCanScrollBottom + "\n"
                                + ",mIsBeingDragged=" + isBeingDragged);
                    } else if (touchInView((View) mListView, ev)) { // 触摸点在第二个View
                        isBeingDragged = isCanScrollBottom;
                        LogE("onInterceptTouchEvent.Move.......触摸点在ListView...deltaY=" + deltaY + "\n"
                                + ",isCanScrollBottom=" + isCanScrollBottom + "\n"
                                + ",mIsBeingDragged=" + isBeingDragged);
                    } else {
                        isBeingDragged = false;
                    }
                } else if (deltaY > 0) { // Scroll To Top
                    if (touchInView((View) mWebView, ev)) {
                        isBeingDragged = isCanScrollTop;
                        LogE("onInterceptTouchEvent.Move.......触摸点在WebView...deltaY=" + deltaY + "\n"
                                + ",isCanScrollTop=" + isCanScrollTop + "\n"
                                + ",mIsBeingDragged=" + isBeingDragged);
                    } else if (touchInView((View) mListView, ev)) {
                        isBeingDragged = isCanScrollTop && !isListViewCanScrollTop;// ListView不可以继续向上滚动，否则由这个ListView自己处理滚动
                        LogE("onInterceptTouchEvent.Move.......触摸点在ListView...deltaY=" + deltaY + "\n"
                                + ",isListViewCanScrollTop=" + isListViewCanScrollTop + "\n"
                                + ",isCanScrollTop=" + isCanScrollTop
                                + ",mIsBeingDragged=" + isBeingDragged + "\n");
                    } else {
                        isBeingDragged = false;
                    }
                }
                mLastY = y;
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                isBeingDragged = false;
                releaseVelocityTracker();
                break;
            }
        }
        LogE("onInterceptTouchEvent........mIsBeingDragged=" + isBeingDragged + "," + ev.getAction());
        return isBeingDragged;
    }

    public void customScrollBy(int dy) {
        int oldY = getScrollY();
        scrollBy(0, dy);
//        LogE(TAG + ".customScrollBy.......oldY=" + oldY + ",getScrollY()=" + getScrollY());
        onScrollChanged(getScrollX(), getScrollY(), getScrollX(), oldY);
    }

    private boolean touchInView(View child, MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        final int scrollY = getScrollY();
        return !(y < child.getTop() - scrollY
                || y >= child.getBottom() - scrollY
                || x < child.getLeft()
                || x >= child.getRight());
    }

    public int adjustScrollY(int delta) {
        int dy;
        if (delta + getScrollY() >= maxScrollY) {
            dy = maxScrollY - getScrollY();
        } else if (delta + getScrollY() <= 0) {
            dy = -getScrollY();
        } else {
            dy = delta;
        }
        LogE(TAG + ".adjustScrollY...finally...dy=" + dy + ",delta=" + delta);
        return dy;
    }

    public void fling(int velocity) {
        LogE("ScrollView fling...." + velocity + "\n"
                + ",mScroller.isFinished()=" + mScroller.isFinished() + "\n"
                + "isTouched=" + isTouched);
        if (isTouched)//当webview不能继续向下滑的时候，继续下拉会触发scrollView下滑，此时webview不能再响应dispatchTouchEvent事件，scrollView响应onTouch事件，然后由scrollView去判断是否是isTouched
            return;
        if (!mScroller.isFinished())
            return;
        int minY = -mWebView.customGetContentHeight();
        LogE("ScrollView do fling...." + velocity);
        mScroller.fling(getScrollX(), getScrollY(), 0, velocity, 0, 0, minY, computeVerticalScrollRange());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        if (!mScroller.computeScrollOffset()) {
            super.computeScroll();
            return;
        }
        int oldX = getScrollX();
        int oldY = getScrollY();
        int currX = mScroller.getCurrX();
        int currY = mScroller.getCurrY();
        int curVelocity = getCappedCurVelocity();
        LogE("computeScroll...oldY=" + oldY + ",currY=" + currY + ",maxScrollY=" + maxScrollY + ",curVelocity=" + curVelocity);
        if (currY < oldY && oldY <= 0) {//向下惯性滑动  到顶部之后
            if (curVelocity != 0) {
                LogE("computeScroll...webView start fling:" + (-curVelocity));
                this.mScroller.forceFinished(true);
                this.mWebView.startFling(-curVelocity);
                return;
            }                           //向上惯性滑动  到底部之后
        } else if (currY > oldY && oldY >= maxScrollY && curVelocity != 0 && mListView.startFling(curVelocity)) {
            LogE("computeScroll...listView start fling:" + (-curVelocity));
            mScroller.forceFinished(true);
            return;
        }
        int toY = Math.max(0, Math.min(currY, maxScrollY));
        if (oldX != currX || oldY != currY) {
            LogE("computeScroll...scrollTo.." + toY);
            scrollTo(currX, toY);
        }
        if (!awakenScrollBars()) {//这句一定要执行，否则会导致mScroller永远不会finish，从而导致一些莫名其妙的bug
            ViewCompat.postInvalidateOnAnimation(this);
        }
        super.computeScroll();
    }

    /**
     * 显示区域的高度
     *
     * @return
     */
    @Override
    protected int computeVerticalScrollExtent() {
        try {
            int webExtent = ViewUtils.computeVerticalScrollExtent((View) mWebView);
            int listExtent = ViewUtils.computeVerticalScrollExtent((View) mListView);
            return webExtent + listExtent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.computeVerticalScrollExtent();
    }

    /**
     * 已经向下滚动的距离，为0时表示已处于顶部。
     *
     * @return
     */
    protected int computeVerticalScrollOffset() {
        try {
            int webOffset = ViewUtils.computeVerticalScrollOffset((View) mWebView);
            int listOffset = ViewUtils.computeVerticalScrollOffset((View) mListView);
            return webOffset + getScrollY() + listOffset;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.computeVerticalScrollOffset();
    }

    /**
     * 整体的高度，注意是整体，包括在显示区域之外的。
     *
     * @return
     */
    @Override
    protected int computeVerticalScrollRange() {
        try {
            int webScrollRange = mWebView.customComputeVerticalScrollRange();
            int listScrollRange = ViewUtils.computeVerticalScrollRange((View) mListView);
            return webScrollRange + maxScrollY + listScrollRange;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.computeVerticalScrollRange();
    }

    @Override
    public boolean canScrollVertically(int direction) {
        LogE(TAG + ".canScrollVertically.getScrollY()=" + getScrollY() + ",mWebHeight=" + maxScrollY + ",direction=" + direction);
        if (direction > 0) {
            return getScrollY() > 0;
        } else {
            return getScrollY() < maxScrollY;
        }
    }

    private int getCappedCurVelocity() {
        return (int) this.mScroller.getCurrVelocity();
    }

    /**
     * @param event 向VelocityTracker添加MotionEvent
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 跳转到列表区域，如果已经在列表区域，则跳回去，如果滚动动画没有结束，则无响应
     */
    public void toggleScrollToListView() {
        if (!mScroller.isFinished()) {
            return;
        }
        View webView = (View) mWebView;
        int webHeight = webView.getHeight() - webView.getPaddingTop() - webView.getPaddingBottom();
        int dy;
        int webViewToY;
        int scrollY = getScrollY();
        if (scrollY >= maxScrollY) {//不是toggle模式才回到原来的位置
            dy = oldScrollY - maxScrollY;
            webViewToY = oldWebViewScrollY;
        } else {
            dy = maxScrollY - scrollY;
            oldScrollY = scrollY;
            oldWebViewScrollY = mWebView.customGetWebScrollY();
            webViewToY = mWebView.customComputeVerticalScrollRange() - webHeight;
        }
        mListView.scrollToFirst();
        mWebView.customScrollTo(webViewToY);
        mScroller.startScroll(getScrollX(), getScrollY(), 0, dy);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 强制滑动列表区域
     */
    public void forceScrollToListView() {
        int dy = maxScrollY - getScrollY();
        View webView = (View) mWebView;
        int webHeight = webView.getHeight() - webView.getPaddingTop() - webView.getPaddingBottom();
        int webViewToY = mWebView.customComputeVerticalScrollRange() - webHeight;
        mListView.scrollToFirst();
        mWebView.customScrollTo(webViewToY);
        mScroller.startScroll(getScrollX(), getScrollY(), 0, dy);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 回到顶部
     */
    public void scrollToTop() {
        mWebView.customScrollTo(0);
        mScroller.startScroll(getScrollX(), getScrollY(), 0, -getScrollY());
        mListView.scrollToFirst();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public static void LogE(String content) {
        if (isDebug) {
            Log.e(TAG, content);
        }
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    private class MyScrollBarShowListener implements OnScrollBarShowListener {

        private long mOldTimeMills;

        @Override
        public void onShow() {
            long timeMills = AnimationUtils.currentAnimationTimeMillis();
            if (timeMills - mOldTimeMills > 16) {
                mOldTimeMills = timeMills;
                awakenScrollBars();
            }
        }
    }
}
