package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.library.PullNeoNestedToRefresh;
import com.huatu.library.PullToRefreshBase;

import com.huatu.library.PullToRefreshBase.Mode;
import com.huatu.library.PullToRefreshBase.Orientation;
import com.huatu.library.PullToRefreshBase.State;
import com.huatu.library.PullToRefreshBase.AnimationStyle;
/**
 * Created by Administrator on 2017/1/11.
 */


/**
 * Created by Administrator on 2016/8/25.
 */
public class PullNestRefreshRecyclerView extends PullNeoNestedToRefresh<RecyclerViewEx> {

    boolean DEBUG=false;
    private int mAdjustDistance=0;
    //排除top decoration的影响
    public void setAdjustDistance(int topDistance){
        mAdjustDistance=topDistance;
    }

    public PullNestRefreshRecyclerView(Context context, Mode mode) {
        super(context, mode);
        //mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public PullNestRefreshRecyclerView(Context context) {
        super(context);
        //mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public PullNestRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public PullNestRefreshRecyclerView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
       // mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public FrameLayout getRefreshContainer(){
        return super.getRefreshableViewWrapper();
    }

    boolean canPullToRefresh=true;
    public void setCanPull(boolean pullEnable){
        canPullToRefresh=pullEnable;
    }

    @Override
    public  boolean isPullToRefreshEnabled() {
        return canPullToRefresh&&super.isPullToRefreshEnabled();
    }

    @Override
    public void setOnPullEventListener(PullToRefreshBase.OnPullEventListener<RecyclerViewEx> listener){

    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerViewEx createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerViewEx recyclerView = new RecyclerViewEx(context,attrs);
        recyclerView.setId(android.R.id.list);
        LinearLayoutManager mannagerTwo = new LinearLayoutManager(context);
        mannagerTwo.setOrientation(LinearLayoutManager.VERTICAL);
        //recyclerView.addItemDecoration(
        //    new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        recyclerView.setLayoutManager(mannagerTwo);

        return recyclerView;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration){
        mRefreshableView.addItemDecoration(itemDecoration);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        mRefreshableView.setAdapter(adapter);
    }

    @Override protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    @Override protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    private boolean isLastItemVisible() {
        final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.getItemCount()==0) {
            if (DEBUG) {
                Log.d("LOG_TAG", "isLastItemVisible. Empty View.");
            }
            return true;
        } else {
            final int lastItemPosition = adapter.getItemCount() - 1;
            final int lastVisiblePosition = getLastVisiblePosition();

            if (DEBUG) {
                Log.d("LOG_TAG", "isLastItemVisible. Last Item Position: "
                        + lastItemPosition
                        + " Last Visible Pos: "
                        + lastVisiblePosition);
            }

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - getFirstVisiblePosition();
                final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getRight() <= mRefreshableView.getRight();
                }
            }
        }

        return false;
    }

    private int getFirstVisiblePosition(){
        int position = 0;
        RecyclerView.LayoutManager manager =  mRefreshableView.getLayoutManager();
        if (manager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            return  linearLayoutManager.findFirstVisibleItemPosition();
        }

        return position;
    }

    private int getLastVisiblePosition(){
        int position = 0;
        RecyclerView.LayoutManager manager =  mRefreshableView.getLayoutManager();
        if (manager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
            return  linearLayoutManager.findLastVisibleItemPosition();
        }
        return position;
    }

    private boolean isFirstItemVisible() {
        final RecyclerView.Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.getItemCount() ==0) {
            if (DEBUG) {
                Log.d("LOG_TAG", "isFirstItemVisible. Empty View.");
            }
            return true;
        } else {

            /**
             * This check should really just be:
             * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (getFirstVisiblePosition() < 1) {
                final View firstVisibleChild = mRefreshableView.getChildAt(0);
                if (firstVisibleChild != null) {
                   // return firstVisibleChild.getLeft() >= mRefreshableView.getLeft();
                    return (firstVisibleChild.getTop()-mAdjustDistance )>= mRefreshableView.getTop();
                }
            }
        }

        return false;
    }


}