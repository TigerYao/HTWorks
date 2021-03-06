

package com.huatu.widget;

        import android.content.Context;
        import android.util.AttributeSet;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.VelocityTracker;
        import android.view.ViewConfiguration;
        import android.view.animation.LinearInterpolator;
        import android.widget.LinearLayout;
        import android.widget.Scroller;

        import com.huatu.utils.DensityUtils;
        import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

/**
 * Created by cjx on 2018\12\25 0025.
 */

public class ScrollLinearLayoutOld  extends LinearLayout {

    public interface OnPulltoUpListener{
        void onPulltoUpEvent();
    }

    OnPulltoUpListener mOnPulltoUpListener;
    private int mTouchSlop;
    public void setOnPulltoUpListener(OnPulltoUpListener upListener){
        mOnPulltoUpListener=upListener;
    }

    private Scroller mScroller;
    public ScrollLinearLayoutOld(Context context) {
        super(context);

        setInit(context);
    }

    public ScrollLinearLayoutOld(Context context, AttributeSet attrs) {
        super(context, attrs);

        setInit(context);
    }

    public ScrollLinearLayoutOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setInit(context);
    }

    private int mTopDistance=0;

    public void setTopDistance(int distance){
        mTopDistance=distance;
    }

    private int mDiffScroll=0;

    public void setDiffScroll(int scroll){
        mDiffScroll=scroll;
    }


    //手指向右滑动时的最小距离
    private  int XDISTANCE_MIN = 0;
    private void setInit(Context context) {
        mScroller = new Scroller(context,new LinearInterpolator());
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        Log.e("mTouchSlop",mTouchSlop+"");

        //手指向右滑动时的最小距离
        XDISTANCE_MIN= DensityUtils.dp2px(context,3);
    }

    private boolean mIsExpland=true;

    public void setExpland(boolean isExpland){
        mIsExpland=isExpland;
    }

    public boolean isExpland(){
        return mIsExpland;
    }

    public void switchExpland(){
        boolean oldFlag=mIsExpland;
        setExpland(!oldFlag);
        smoothScrollBy(0,mTopDistance-mDiffScroll,oldFlag);
    }

    // 调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy,boolean isExpland) {

        // 设置mScroller的滚动偏移量
        // mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy);


        if(isExpland){
            mScroller.startScroll(0, 0, dx, dy,400);
        }
        else{
            mScroller.startScroll(0,getScrollY(), dx, -dy,400);
        }
        invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {

        // 先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            // 这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            // 必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }/*else {

            Log.e("computeScroll","finished");
        }*/
        super.computeScroll();
    }

    // private boolean mIsAniming=false;
    private boolean mFilterTouchEvents = true;
    private boolean mIsBeingDragged = false;
    private float mLastMotionX, mLastMotionY;
    private float mInitialMotionX, mInitialMotionY;

    private boolean mIsPlaying=false;

    public void setPlayStatus(boolean isplay){
        if(mIsPlaying==isplay) return;
        mIsPlaying=isplay;
    }

    private boolean mIsLandScape=false;//LANDSCAPE
    public void setLandScape(boolean isLandScape){
        mIsLandScape=isLandScape;
    }
/*
    public void dispatchTouchEvent(){
        super.dispatchTouchEvent()
    }*/

    @Override
    public  boolean onInterceptTouchEvent(MotionEvent event) {

        if(!mIsExpland)  return false;
        if(mIsPlaying)   return  false;
        if(mIsLandScape) return  false;
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


                if (true) {
                    final float y = event.getY(), x = event.getX();
                    final float diff, oppositeDiff, absDiff;

                    // We need to use the correct values, based on scroll
                    // direction
              /*      switch (getPullToRefreshScrollDirection()) {
                        case HORIZONTAL:
                            diff = x - mLastMotionX;
                            oppositeDiff = y - mLastMotionY;
                            break;
                        case VERTICAL:
                        default:
                            diff = y - mLastMotionY;
                            oppositeDiff = x - mLastMotionX;
                            break;
                    }*/

                    diff = y - mLastMotionY;
                    oppositeDiff = x - mLastMotionX;

                    absDiff = Math.abs(diff);

                    if (absDiff > mTouchSlop && (!mFilterTouchEvents || absDiff > Math.abs(oppositeDiff))) {
                        if ( diff >= 1f ) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;

                        } else if (diff <= -1f ) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;

                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if (true) {
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = mInitialMotionX = event.getX();
                    mIsBeingDragged = false;
                }
                break;
            }
        }

        return mIsBeingDragged;
    }

    private final  String getActionName(int actionId){

        switch (actionId){
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            default:
                return ""+actionId;
        }
    }
    @Override
    public final boolean onTouchEvent(MotionEvent event) {

        Log.e("GestureFrameLayout", "onTouchEvent_"+ getActionName(event.getAction()));
        if(!mIsExpland)  return false;
        if(mIsPlaying)   return  false;
        if(mIsLandScape) return  false;
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                createVelocityTracker(event);
                if (mIsBeingDragged) {
                    mLastMotionY = event.getY();
                    mLastMotionX = event.getX();
                    //pullEvent();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                createVelocityTracker(event);
                if (true) {//isReadyForPull()
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

              /*      if (mState == State.RELEASE_TO_REFRESH
                            && (null != mOnRefreshListener || null != mOnRefreshListener2)) {
                        setState(State.REFRESHING, true);
                        return true;
                    }

                    // If we're already refreshing, just scroll back to the top
                    if (isRefreshing()) {
                        smoothScrollTo(0);
                        return true;
                    }*/

                    // If we haven't returned by here, then we're not in a state
                    // to pull, so just reset

                    YMove = event.getY();
                    //活动的距离
                    int distanceY = (int) (mInitialMotionY - YMove);
                    //获取顺时速度
                    int xSpeed = getScrollVelocity();
                    recycleVelocityTracker();
                    //当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
                    if(distanceY > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
                        //finish();
                        switchExpland();
                        if(null!=mOnPulltoUpListener) mOnPulltoUpListener.onPulltoUpEvent();
                    }
                    return true;
                }
                recycleVelocityTracker();
                break;
            }
        }

        return false;
    }


    //手指向右滑动时的最小速度
    private static final int XSPEED_MIN = 250;



    //记录手指按下时的横坐标。
    private float YDown;

    //记录手指移动时的横坐标。
    private float YMove;

    //用于计算手指滑动的速度。
    private VelocityTracker mVelocityTracker;
    /**
     * 创建VelocityTracker对象，并将触摸content界面的滑动事件加入到VelocityTracker当中。
     *
     * @param event
     *
     */
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * 回收VelocityTracker对象。
     */
    private void recycleVelocityTracker() {
        if(null!=mVelocityTracker){
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 获取手指在content界面滑动的速度。
     *
     * @return 滑动速度，以每秒钟移动了多少像素值为单位。
     */
    private int getScrollVelocity() {
        if(null!=mVelocityTracker){
            mVelocityTracker.computeCurrentVelocity(1000);
            int velocity = (int) mVelocityTracker.getYVelocity();
            return Math.abs(velocity);
        }
        return 0;
    }



}
