package com.huatu.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.DensityUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

/**
 * Created by cjx on 2018\12\25 0025.
 */

public class ScrollLinearLayout  extends LinearLayout {

    public interface OnPulltoUpListener{
        void onPulltoUpEvent(boolean isExpland);
    }

    OnPulltoUpListener mOnPulltoUpListener;
    private int mTouchSlop;
    public void setOnPulltoUpListener(OnPulltoUpListener upListener){
        mOnPulltoUpListener=upListener;
    }

    private Scroller mScroller;
    public ScrollLinearLayout(Context context) {
        super(context);

        setInit(context);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setInit(context);
    }

    public ScrollLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setInit(context);
    }

    private int mTopDistance=0;

    public void setTopDistance(int distance){
       mTopDistance=distance;
    }

    private int mDiffScroll=0;

    private int mOffsetScroll=0;

    public void setDiffScroll(int scroll){
        mDiffScroll=scroll;
    }

    private ScrollableHelper mHelper;
    //手指向右滑动时的最小距离
    private  int XDISTANCE_MIN = 0;
    private void setInit(Context context) {

        mHelper = new ScrollableHelper();
        mScroller = new Scroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        Log.e("mTouchSlop",mTouchSlop+"");

        //手指向右滑动时的最小距离
        XDISTANCE_MIN=DensityUtils.dp2px(context,3);
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
            mScroller.startScroll(0, getScrollY(), dx, dy-getScrollY(),400);
        }
        else{
            int curScroll=getScrollY();

            mScroller.startScroll(0,curScroll, dx, -dy+mOffsetScroll,400);
            if(mOffsetScroll!=0)
                mOffsetScroll=0;
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


    private static boolean isRecyclerViewTop(View recyView) {

        if(recyView instanceof RecyclerView){
            RecyclerView recyclerView=(RecyclerView)recyView;
            if (recyclerView != null) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    View childAt = recyclerView.getChildAt(0);
                    if (childAt == null || (firstVisibleItemPosition == 0 && childAt.getTop() == 0)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private View mScrollView;
    public void setScrollableView(View scrollableView){
        mScrollView=scrollableView;
    }

    private boolean isReadyForPull() {
        return true;
    }

    protected boolean isReadyForPullStart() {
        if(mIsExpland) return true;
        else {
            if(isRecyclerViewTop(mScrollView)) return true;
            return false;
        }
    }

    protected boolean isReadyForPullEnd(){
        if(mIsExpland) return true;
        else {
              return false;
        }


    }

    @Override
    public  boolean onInterceptTouchEvent(MotionEvent event) {

        //if(!mIsExpland)  return false;
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


                if (isReadyForPull())  {
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
                        if ( diff >= 1f && isReadyForPullStart()) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;

                        } else if (diff <= -1f&&isReadyForPullEnd() ) {
                            mLastMotionY = y;
                            mLastMotionX = x;
                            mIsBeingDragged = true;

                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if (isReadyForPull()){
                    mLastMotionY = mInitialMotionY = event.getY();
                    mLastMotionX = mInitialMotionX = event.getX();
                    mInitialMoveY=0;
                    mOffsetScroll=0;
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

    private float mInitialMoveY=0;
    @Override
    public final boolean onTouchEvent(MotionEvent event) {

        Log.e("GestureFrameLayout", "onTouchEvent_"+ getActionName(event.getAction()));
        //if(!mIsExpland)  return false;
        if(mIsPlaying)   return  false;
        if(mIsLandScape) return  false;
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                createVelocityTracker(event);
                if (mIsBeingDragged) {
                    final float diff, oppositeDiff, absDiff;
                    final float y = event.getY(), x = event.getX();

                    diff = y - mLastMotionY;
                    oppositeDiff = x - mLastMotionX;
                    if(mInitialMoveY==0)
                        mInitialMoveY=y;

                    absDiff = Math.abs(diff);

                    if (absDiff > mTouchSlop && (!mFilterTouchEvents || absDiff > Math.abs(oppositeDiff))) {
                        if ( diff >= 1f ) {      // 向下滑动
                            mLastMotionY = event.getY();
                            mLastMotionX = event.getX();

                            if(!mIsExpland){
                                mInitialMoveY=0;
                                int action = event.getAction();
                                event.setAction(MotionEvent.ACTION_CANCEL);
                                boolean dispathResult = this.dispatchTouchEvent(event);
                                event.setAction(action);
                                return true;
                            }

                        } else if (diff <= -1f ) {// 向上滑动
                            mLastMotionY = event.getY();
                            mLastMotionX = event.getX();
                           /* if(diff<3*mTouchSlop)
                               scrollBy(0,- (int)diff);
                            else*/ {

                                //mLastMotionY = y;
                                // mLastMotionX = x;
                                mInitialMoveY=0;
                                int action = event.getAction();
                                event.setAction(MotionEvent.ACTION_CANCEL);
                                boolean dispathResult = this.dispatchTouchEvent(event);
                                event.setAction(action);
                                return true;
                            }


                        }
                    }else if((absDiff < mTouchSlop )&&(absDiff > Math.abs(oppositeDiff))) {

                       if(diff<0){// 向上滑动

                           if(Math.abs(y-mInitialMoveY)> 3*mTouchSlop){
                               mInitialMoveY=0;
                               int action = event.getAction();
                               event.setAction(MotionEvent.ACTION_CANCEL);
                               boolean dispathResult = this.dispatchTouchEvent(event);
                               event.setAction(action);
                               return true;
                           }
                           else
                               scrollBy(0,- (int)diff);
                       }else {  // 向下滑动

                           if(!mIsExpland){
                              if((Math.abs(y-mInitialMoveY)> 3*mTouchSlop)){

                                   mInitialMoveY=0;
                                   int action = event.getAction();
                                   event.setAction(MotionEvent.ACTION_CANCEL);
                                   boolean dispathResult = this.dispatchTouchEvent(event);
                                   event.setAction(action);
                                   return true;
                               }
                               else{
                                  mOffsetScroll+=diff;
                                  scrollBy(0,-(int)diff);
                              }

                           }

                       }
                        mLastMotionY = event.getY();
                        mLastMotionX = event.getX();
                    }

                 /*    mLastMotionY = event.getY();
                    mLastMotionX = event.getX();*/
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
                    mInitialMoveY=0;
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

                    if(((getScrollY())!=0&&Math.abs(distanceY) > mTouchSlop) || ((xSpeed > XSPEED_MIN))){//distanceY<0&&
                        boolean oldFlag=isExpland();
                        switchExpland();

                        if(null!=mOnPulltoUpListener) mOnPulltoUpListener.onPulltoUpEvent(!oldFlag);
                    }
                  /*  else if((getScrollY())!=0||distanceY > mTouchSlop || (distanceY>0&&(xSpeed > XSPEED_MIN))) {
                        //finish();

                        switchExpland();
                        if(null!=mOnPulltoUpListener) mOnPulltoUpListener.onPulltoUpEvent(false);
                    } */
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
