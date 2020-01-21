package com.huatu.scroll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**

 * Email:
 */

public abstract class ScrollerFrameLayout<T extends View>  extends LinearLayout {

    private float mLastMotionX, mLastMotionY;
    private float mDeltaX, mDeltaY;
    public static final int SMOOTH_SCROLL_DURATION_MS = 200;
    public static final int SMOOTH_SCROLL_LONG_DURATION_MS = 325;
    private boolean mOverScrollEnabled = true;
    private Mode mCurrentMode;

    T mRefreshableView;


    private enum Orientation{
        HORIZONTAL,
        VERTICAL
    }

    private boolean mFilterTouchEvents = true;
    State mState = State.RESET;
    private Orientation mOrientation;
    private int mTouchSlop;
    @SuppressLint("NewApi")
    public ScrollerFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context,attrs);
    }

    public ScrollerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public ScrollerFrameLayout(Context context) {
        super(context);
        init(context,null);
    }

    protected abstract T createRefreshableView(Context context, AttributeSet attrs);

    private ScrollChecker mScrollChecker;

    private void init(Context context, AttributeSet attrs){


        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        // Refreshable View
        // By passing the attrs, we can add ListView/GridView params via XML
        mRefreshableView = createRefreshableView(context, attrs);
        addRefreshableView(context, mRefreshableView);

        mScrollChecker = new ScrollChecker();

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {

        final T refreshableView = getRefreshableView();

        if (refreshableView instanceof ViewGroup) {
            ((ViewGroup) refreshableView).addView(child, index, params);
        } else {
            throw new UnsupportedOperationException("Refreshable View is not a ViewGroup so can't addView");
        }
    }


    public final T getRefreshableView() {
        return mRefreshableView;
    }

    private void addRefreshableView(Context context, T refreshableView) {
        FrameLayout mRefreshableViewWrapper = new FrameLayout(context);
        mRefreshableViewWrapper.addView(refreshableView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        addViewInternal(mRefreshableViewWrapper, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    protected final void addViewInternal(View child, ViewGroup.LayoutParams params) {
        super.addView(child, -1, params);
    }

    private boolean mIsBeingDragged = false;
    private boolean mScrollingWhileRefreshingEnabled = false;

    protected boolean isRefreshing(){return false;}

    //  private boolean isReadyForPull() {return true; }

    private boolean isReadyForPull() {
   /*     switch (mMode) {
            case PULL_FROM_START:
                return isReadyForPullStart();
            case PULL_FROM_END:
                return isReadyForPullEnd();
            case BOTH:
                return isReadyForPullEnd() || isReadyForPullStart();
            default:
                return false;
        }*/

        return isReadyForPullEnd() || isReadyForPullStart();
    }

    private float mInitialMotionX, mInitialMotionY;

    public  Orientation getPullToRefreshScrollDirection(){return Orientation.VERTICAL;}

    protected abstract boolean isReadyForPullEnd();

    protected abstract boolean isReadyForPullStart();


    //final
    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {


        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsBeingDragged = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                // If we're refreshing, and the flag is set. Eat all MOVE events
                if (!mScrollingWhileRefreshingEnabled && isRefreshing()) {
                    return true;
                }

                if (isReadyForPull()) {
                    final float y = event.getY(), x = event.getX();
                    final float diff, oppositeDiff, absDiff;

                    // We need to use the correct values, based on scroll
                    // direction
                    switch (getPullToRefreshScrollDirection()) {
                        case HORIZONTAL:
                            diff = x - mLastMotionX;
                            oppositeDiff = y - mLastMotionY;
                            break;
                        case VERTICAL:
                        default:
                            diff = y - mLastMotionY;
                            oppositeDiff = x - mLastMotionX;
                            break;
                    }
                    absDiff = Math.abs(diff);

                    if (absDiff > mTouchSlop && (!mFilterTouchEvents || absDiff > Math.abs(oppositeDiff))) {
                        if (  diff >= 1f && isReadyForPullStart()) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;
                       /*     if (mMode == Mode.BOTH) {
                                mCurrentMode = Mode.PULL_FROM_START;
                            }*/
                            mCurrentMode = Mode.PULL_FROM_START;
                        } else if (  diff <= -1f && isReadyForPullEnd()) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;
                       /*     if (mMode == Mode.BOTH) {
                                mCurrentMode = Mode.PULL_FROM_END;
                            }*/
                            mCurrentMode = Mode.PULL_FROM_END;
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()) {
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = mInitialMotionX = event.getX();
                    mIsBeingDragged = false;
                }
                break;
            }
        }

        return mIsBeingDragged;
    }


    @Override
    public final boolean onTouchEvent(MotionEvent event) {



        // If we're refreshing, and the flag is set. Eat the event
        if (!mScrollingWhileRefreshingEnabled && isRefreshing()) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                if (mIsBeingDragged) {
                    mLastMotionY = event.getY();
                    mLastMotionX = event.getX();    // 根据下拉距离改变比例

                    pullEvent();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()) {
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = mInitialMotionX = event.getX();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;

                 /*   if (mState == State.RELEASE_TO_REFRESH
                            && (null != mOnRefreshListener || null != mOnRefreshListener2)) {
                        setState(State.REFRESHING, true);
                        return true;
                    }*/

                    // If we're already refreshing, just scroll back to the top
                  /*  if (isRefreshing()) {
                        smoothScrollTo(0);
                        return true;
                    }*/

                    // If we haven't returned by here, then we're not in a state
                    // to pull, so just reset
                     setState(State.RESET);

                    return true;
                }
                break;
            }
        }

        return false;
    }

    /**
     * Called when the UI has been to be updated to be in the
     * {@link State#RESET} state.
     */
    protected void onReset() {
        mIsBeingDragged = false;
      //  mLayoutVisibilityChangesEnabled = true;

        // Always reset both layouts, just in case...
      //  mHeaderLayout.reset();
      //  mFooterLayout.reset();

        smoothScrollTo(0);
       // finalValue=0;
    }


    public final boolean isPullToRefreshOverScrollEnabled() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && mOverScrollEnabled
                && OverscrollHelper.isAndroidOverScrollEnabled(mRefreshableView);
    }



    static  float FRICTION = 2.0f;
    /**
     * Actions a Pull Event
     *
     * @return true if the Event has been handled, false if there has been no
     *         change
     */


    private void pullEvent() {
        final int newScrollValue;
        final int itemDimension;
        final float initialMotionValue, lastMotionValue;

        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                initialMotionValue = mInitialMotionX;
                lastMotionValue = mLastMotionX;
                break;
            case VERTICAL:
            default:
                initialMotionValue = mInitialMotionY;
                lastMotionValue = mLastMotionY;
                break;
        }
        FRICTION = (float) (2 + 0.2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                * (0 + Math.abs(initialMotionValue - lastMotionValue))));

       // Log.e("FRICTION",FRICTION+"");
        switch (mCurrentMode) {
            case PULL_FROM_END:

                newScrollValue = Math.round(Math.max(initialMotionValue - lastMotionValue, 0) / FRICTION);
               // itemDimension = getFooterSize();
                break;
            case PULL_FROM_START:
            default:
                newScrollValue = Math.round(Math.min(initialMotionValue - lastMotionValue, 0) / FRICTION);
               // itemDimension = getHeaderSize();
                break;
        }



        setHeaderScroll(newScrollValue);

      /*  if (newScrollValue != 0 && !isRefreshing()) {
            float scale = Math.abs(newScrollValue) / (float) itemDimension;
            switch (mCurrentMode) {
                case PULL_FROM_END:
                    mFooterLayout.onPull(scale);
                    break;
                case PULL_FROM_START:
                default:
                    mHeaderLayout.onPull(scale);
                    break;
            }

            if (mState != State.PULL_TO_REFRESH && itemDimension >= Math.abs(newScrollValue)) {
                setState(State.PULL_TO_REFRESH);
            } else if (mState == State.PULL_TO_REFRESH && itemDimension < Math.abs(newScrollValue)) {
                setState(State.RELEASE_TO_REFRESH);
            }
        }*/
    }

    /**
     * Helper method which just calls scrollTo() in the correct scrolling
     * direction.
     *
     * @param value - New Scroll value
     */
    protected final void setHeaderScroll(int value) {
     /*   if (DEBUG) {
            Log.d(LOG_TAG, "setHeaderScroll: " + value);
        }*/

        // Clamp value to with pull scroll range
        final int maximumPullScroll = getMaximumPullScroll();
        value = Math.min(maximumPullScroll, Math.max(-maximumPullScroll, value));
/*

        if (mLayoutVisibilityChangesEnabled) {
            if (value < 0) {
                mHeaderLayout.setVisibility(View.VISIBLE);
            } else if (value > 0) {
                mFooterLayout.setVisibility(View.VISIBLE);
            } else {
                mHeaderLayout.setVisibility(View.INVISIBLE);
                mFooterLayout.setVisibility(View.INVISIBLE);
            }
        }

        if (USE_HW_LAYERS) {
            */
/**
             * Use a Hardware Layer on the Refreshable View if we've scrolled at
             * all. We don't use them on the Header/Footer Views as they change
             * often, which would negate any HW layer performance boost.
             *//*

            ViewCompat.setLayerType(mRefreshableViewWrapper, value != 0 ? View.LAYER_TYPE_HARDWARE
                    : View.LAYER_TYPE_NONE);
        }
*/

        switch (getPullToRefreshScrollDirection()) {
            case VERTICAL:
                mDeltaY=value;
                scrollTo(0, value);
                break;
            case HORIZONTAL:
                scrollTo(value, 0);
                break;
        }
    }

    private int getMaximumPullScroll() {
        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                return Math.round(getWidth() / FRICTION);
            case VERTICAL:
            default:
                return Math.round(getHeight() / FRICTION);
        }

    }



    private void pull(float diff){
        int value = Math.round(diff / 2.0F);
        if(mOrientation == Orientation.VERTICAL){
            scrollTo(0, value);
        }else if(mOrientation == Orientation.HORIZONTAL){
            scrollTo(value, 0);
        }
    }
/*
    private void smoothScrollTo(float diff){
        int value = Math.round(diff / 2.0F);
        mScrollToHomeRunnable = new ScrollToHomeRunnable(value, 0);
        mState = State.REFRESHING;
        post(mScrollToHomeRunnable);
    }*/



    /**
     * Smooth Scroll to position using the default duration of
     * {@value #SMOOTH_SCROLL_DURATION_MS} ms.
     *
     * @param scrollValue - Position to scroll to
     */
    protected final void smoothScrollTo(int scrollValue) {
        smoothScrollTo(scrollValue,SMOOTH_SCROLL_DURATION_MS);
    }

    /**
     * Smooth Scroll to position using the specific duration
     *
     * @param scrollValue - Position to scroll to
     * @param duration - Duration of animation in milliseconds
     */
    private final void smoothScrollTo(int scrollValue, long duration) {
        smoothScrollTo(scrollValue, duration, 0);
    }
  //  private SmoothScrollRunnable mCurrentSmoothScrollRunnable;
    private Interpolator mScrollAnimationInterpolator;
    private final void smoothScrollTo(int newScrollValue, long duration, long delayMillis  ) {
      /*  if (null != mCurrentSmoothScrollRunnable) {
            mCurrentSmoothScrollRunnable.stop();
        }*/
       // mScrollChecker.abortIfWorking();

        final int oldScrollValue;
        switch (getPullToRefreshScrollDirection()) {
            case HORIZONTAL:
                oldScrollValue = getScrollX();
                break;
            case VERTICAL:
            default:
                oldScrollValue = getScrollY();
                break;
        }

        if (oldScrollValue != newScrollValue) {
          /*  if (null == mScrollAnimationInterpolator) {
                // Default interpolator is a Decelerate Interpolator
                mScrollAnimationInterpolator = new DecelerateInterpolator();
            }
            mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration);

            if (delayMillis > 0) {
                postDelayed(mCurrentSmoothScrollRunnable, delayMillis);
            } else {
                post(mCurrentSmoothScrollRunnable);
            }*/

            mScrollChecker.tryToScrollTo(0, (int)duration);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollChecker != null) {
            mScrollChecker.destroy();
        }

     /*   if (mPerformRefreshCompleteDelay != null) {
            removeCallbacks(mPerformRefreshCompleteDelay);
        }*/
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
          /*  if (DEBUG) {
                if (deltaY != 0) {
                    PtrCLog.v(LOG_TAG,
                            "scroll: %s, start: %s, to: %s, currentPos: %s, current :%s, last: %s, delta: %s",
                            finish, mStart, mTo, mPtrIndicator.getCurrentPosY(), curY, mLastFlingY, deltaY);
                }
            }*/
            if (!finish) {
                mLastFlingY = curY;
                //movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
        /*    if (DEBUG) {
                PtrCLog.v(LOG_TAG, "finish, currentPos:%s", mPtrIndicator.getCurrentPosY());
            }*/
            reset();
           // onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
               // onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
           /* if (mPtrIndicator.isAlreadyHere(to)) {
                return;
            }*/
            mStart =(int)mDeltaY;
            mTo = to;
            int distance = to - mStart;
          /*  if (DEBUG) {
                PtrCLog.d(LOG_TAG, "tryToScrollTo: start: %s, distance:%s, to:%s", mStart, distance, to);
            }*/
            removeCallbacks(this);

            mLastFlingY = 0;

            // fix #47: Scroller should be reused, https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/issues/47
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, -distance, 0, 0, duration);
            post(this);
            mIsRunning = true;
        }
    }

    final void setState(State state, final boolean... params) {
        mState = state;


        switch (mState) {
            case RESET:
                onReset();
                break;
            case PULL_TO_REFRESH:
               // onPullToRefresh();
                break;
            case RELEASE_TO_REFRESH:
                //onReleaseToRefresh();
                break;
            case REFRESHING:
            case MANUAL_REFRESHING:
              //  onRefreshing(params[0]);
                break;
            case OVERSCROLLING:
                // NO-OP
                break;
        }


    }

    public final State getState() {
        return mState;
    }
    public static enum Mode {

        /**
         * Disable all Pull-to-Refresh gesture and Refreshing handling
         */
        DISABLED(0x0),

        /**
         * Only allow the user to Pull from the start of the Refreshable View to
         * refresh. The start is either the Top or Left, depending on the
         * scrolling direction.
         */
        PULL_FROM_START(0x1),

        /**
         * Only allow the user to Pull from the end of the Refreshable View to
         * refresh. The start is either the Bottom or Right, depending on the
         * scrolling direction.
         */
        PULL_FROM_END(0x2),

        /**
         * Allow the user to both Pull from the start, from the end to refresh.
         */
        BOTH(0x3),

        /**
         * Disables Pull-to-Refresh gesture handling, but allows manually
         * setting the Refresh state via
         * {link PullToRefreshBase#setRefreshing() setRefreshing()}.
         */
        MANUAL_REFRESH_ONLY(0x4);

        /**
         * @deprecated Use {@link #PULL_FROM_START} from now on.
         */
        public static Mode PULL_DOWN_TO_REFRESH = Mode.PULL_FROM_START;

        /**
         * @deprecated Use {@link #PULL_FROM_END} from now on.
         */
        public static Mode PULL_UP_TO_REFRESH = Mode.PULL_FROM_END;

        /**
         * Maps an int to a specific mode. This is needed when saving state, or
         * inflating the view from XML where the mode is given through a attr
         * int.
         *
         * @param modeInt - int to map a Mode to
         * @return Mode that modeInt maps to, or PULL_FROM_START by default.
         */
        static Mode mapIntToValue(final int modeInt) {
            for (Mode value : Mode.values()) {
                if (modeInt == value.getIntValue()) {
                    return value;
                }
            }

            // If not, return default
            return getDefault();
        }

        static Mode getDefault() {
            return PULL_FROM_START;
        }

        private int mIntValue;

        // The modeInt values need to match those from attrs.xml
        Mode(int modeInt) {
            mIntValue = modeInt;
        }

        /**
         * @return true if the mode permits Pull-to-Refresh
         */
        boolean permitsPullToRefresh() {
            return !(this == DISABLED || this == MANUAL_REFRESH_ONLY);
        }

        /**
         * @return true if this mode wants the Loading Layout Header to be shown
         */
        public boolean showHeaderLoadingLayout() {
            return this == PULL_FROM_START || this == BOTH;
        }

        /**
         * @return true if this mode wants the Loading Layout Footer to be shown
         */
        public boolean showFooterLoadingLayout() {
            return this == PULL_FROM_END || this == BOTH || this == MANUAL_REFRESH_ONLY;
        }

        int getIntValue() {
            return mIntValue;
        }

    }

    public static enum State {

        /**
         * When the UI is in a state which means that user is not interacting
         * with the Pull-to-Refresh function.
         */
        RESET(0x0),

        /**
         * When the UI is being pulled by the user, but has not been pulled far
         * enough so that it refreshes when released.
         */
        PULL_TO_REFRESH(0x1),

        /**
         * When the UI is being pulled by the user, and <strong>has</strong>
         * been pulled far enough so that it will refresh when released.
         */
        RELEASE_TO_REFRESH(0x2),

        /**
         * When the UI is currently refreshing, caused by a pull gesture.
         */
        REFRESHING(0x8),

        /**
         * When the UI is currently refreshing, caused by a call to
         * {link PullToRefreshBase#setRefreshing() setRefreshing()}.
         */
        MANUAL_REFRESHING(0x9),

        /**
         * When the UI is currently overscrolling, caused by a fling on the
         * Refreshable View.
         */
        OVERSCROLLING(0x10);

        /**
         * Maps an int to a specific state. This is needed when saving state.
         *
         * @param stateInt - int to map a State to
         * @return State that stateInt maps to
         */
        static State mapIntToValue(final int stateInt) {
            for (State value : State.values()) {
                if (stateInt == value.getIntValue()) {
                    return value;
                }
            }

            // If not, return default
            return RESET;
        }

        private int mIntValue;

        State(int intValue) {
            mIntValue = intValue;
        }

        int getIntValue() {
            return mIntValue;
        }
    }
}