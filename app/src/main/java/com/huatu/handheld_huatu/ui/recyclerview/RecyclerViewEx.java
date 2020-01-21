package com.huatu.handheld_huatu.ui.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;

import com.baijiayun.glide.RequestManager;
import com.huatu.event.IAbsListView;
import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;


/**
 * Created by Administrator on 2016/8/8. RecyclerView.OnScrollListener,
 */
public class RecyclerViewEx extends RecyclerView implements IAbsListView, View.OnClickListener {


    public interface onTouchCancelListener {
        boolean isNeedTouchCancel();
    }

    onTouchCancelListener mOnTouchCancelListener;

    public void setonTouchCancelListener(onTouchCancelListener onTouchCancelListener) {
        mOnTouchCancelListener = onTouchCancelListener;
    }

    private boolean mForcePageEnd = false;//少于一页也要底部

    public void showForcePageEnd() {
        mForcePageEnd = true;
    }

    boolean mNeedCancel = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mNeedCancel) {
            LogUtils.e("onTouchEvent", mNeedCancel + "");
            mNeedCancel = false;
            return true;

        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null != mOnTouchCancelListener) {
            boolean needCancel = mOnTouchCancelListener.isNeedTouchCancel();
            if (needCancel) {
                mNeedCancel = true;
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    int pagesize = 20;
    boolean isLoading = false;
    RequestManager mImgLoader;
    boolean pauseOnScroll = false, pauseOnFling = true;
    boolean canloadmore = true;
    IonLoadMoreListener monLoadMoreListener;

    public void setImgLoader(RequestManager imgLoader) {
        mImgLoader = imgLoader;
    }

    public RecyclerViewEx(Context context) {
        this(context, null);
    }

    public RecyclerViewEx(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerViewEx(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.addOnScrollListener(mOnScrollListener);
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //根据newState状态做处理
            if (null == mImgLoader) return;
            switch (newState) {
                case SCROLL_STATE_IDLE://0
                    mImgLoader.resumeRequests();
                    // imageLoader.resume();
                    break;

                case SCROLL_STATE_DRAGGING://1
                    if (pauseOnScroll) {
                        mImgLoader.pauseRequests();//.pause();
                    } else {
                        mImgLoader.resumeRequests();//.resume();
                    }
                    break;

                case SCROLL_STATE_SETTLING://2
                    if (pauseOnFling) {
                        mImgLoader.pauseRequests();
                    } else {
                        mImgLoader.resumeRequests();
                    }
                    break;
            }

        }

        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);
            if (!canloadmore) {
                return;
            }
            if (mHeaderAndFooterRecyclerViewAdapter.getDataItemCount() < pagesize) {

                if (mForcePageEnd && (mHeaderAndFooterRecyclerViewAdapter.getDataItemCount() >= 3)) {
                    RecyclerViewStateUtils.setFooterViewState(RecyclerViewEx.this.getContext(), RecyclerViewEx.this, pagesize, LoadingFooter.State.TheEnd, null, true);
                }
                return;
            }
            if (isLoading) return;

            isLoading = true;
            RecyclerViewStateUtils.setFooterViewState(RecyclerViewEx.this.getContext(), RecyclerViewEx.this, pagesize, LoadingFooter.State.Loading, null);
            //mloadingview.show(true);//.setVisibility(View.VISIBLE);
            //	mloadingview.setState(PullToRefreshListFooter.STATE_LOADING);
            // super.setSelection(mBaseAdapter.getCount() - 1);
            RecyclerViewEx.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != monLoadMoreListener) {
                        // 这是为了解决向上惯性滚动过程中点击返回键Activity finish了，还会继续调用加载更多，造成网络访问空指针问题
                        Activity context = CommonUtils.getActivityFromView(RecyclerViewEx.this);
                        if (context != null) {
                            if (Method.isActivityFinished(context)) {
                                return;
                            }
                            monLoadMoreListener.OnLoadMoreEvent(false);
                        }
                    }
                }
            }, 400);

         /*
            LoadingFooter.State state = RecyclerViewStateUtils.getFooterViewState(RecyclerViewEx.this);
            if(state == LoadingFooter.State.Loading) {
                Log.d("@Cundong", "the state is Loading, just wait..");
                return;
            }*/

           /* if (mCurrentCounter < TOTAL_COUNTER) {
                // loading more
                RecyclerViewStateUtils.setFooterViewState(EndlessLinearLayoutActivity.this, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.Loading, null);
                requestData();
            } else {
                //the end
                RecyclerViewStateUtils.setFooterViewState(EndlessLinearLayoutActivity.this, mRecyclerView, REQUEST_COUNT, LoadingFooter.State.TheEnd, null);
            }*/
        }
    };

/*    public void setAdapter(Adapter adapter) {
        // bail out if layout is frozen
        setLayoutFrozen(false);
        setAdapterInternal(adapter, false, true);
        requestLayout();
    }*/

    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter = null;


    @Override
    public void setMyAdapter(ListAdapter dataAdapter) {
    }


    @Override
    public void setRecyclerAdapter(Adapter dataAdapter) {
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(dataAdapter);
        super.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
    }

    @Override
    public void setOnLoadMoreListener(IonLoadMoreListener onLoadMoreListener) {

        if (monLoadMoreListener != null) return;
        monLoadMoreListener = onLoadMoreListener;

    }

    @Override
    public void notifyDataSetChanged(int positionStart, int itemSize) {
        if (positionStart < pagesize) mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();
        else
            mHeaderAndFooterRecyclerViewAdapter.notifyItemRangeInserted(mHeaderAndFooterRecyclerViewAdapter.getDataItemCount(), itemSize);
        // mHeaderAndFooterRecyclerViewAdapter.notifyItemRangeChanged(positionStart,itemSize);
        // mHeaderAndFooterRecyclerViewAdapter.notifyItemRangeChanged(positionStart,mHeaderAndFooterRecyclerViewAdapter.getDataItemCount());
    }

    @Override
    public void setPagesize(int curPagesize) {
        pagesize = curPagesize;
    }

    @Override
    public void hideloading() {
        isLoading = false;

        RecyclerViewStateUtils.setFooterViewState(RecyclerViewEx.this.getContext(),
                RecyclerViewEx.this,
                pagesize,
                canloadmore ? LoadingFooter.State.Normal : LoadingFooter.State.TheEnd, null);
        //mloadingview.setVisibility(View.GONE);
        //mloadingview.setState(PullToRefreshListFooter.STATE_NORMAL);
    }


    @Override
    public void checkloadMore(int size) {
        if (size < pagesize) {

            // mloadingview.show(false);
            canloadmore = false;
        }
    }


    public void checkloadMore(boolean hasNext) {
        if (!hasNext) {
            // mloadingview.show(false);
            canloadmore = false;
            RecyclerViewStateUtils.setFooterViewState(RecyclerViewEx.this, LoadingFooter.State.TheEnd);
        }
    }

    // public void set  onRefreshComplete
    @Override
    public void reset() {
        canloadmore = true;
        isLoading = false;
        //super.stopRefresh();
    }

    public void resetAll() {
        reset();
        RecyclerViewStateUtils.setFooterViewState(RecyclerViewEx.this, LoadingFooter.State.Normal);
        // mloadingview.show(false);
    }

    @Override
    public void selectionFromTop() {
        this.scrollToPosition(0);
    }

    public void showNetWorkError() {
        RecyclerViewStateUtils.setFooterViewState(RecyclerViewEx.this.getContext(), RecyclerViewEx.this, pagesize, LoadingFooter.State.NetWorkError, this);
    }

    @Override
    public void onClick(View v) {

        RecyclerViewStateUtils.setFooterViewState(RecyclerViewEx.this.getContext(), RecyclerViewEx.this, pagesize, LoadingFooter.State.Loading, null);
        if (null != monLoadMoreListener) monLoadMoreListener.OnLoadMoreEvent(true);
    }

    public void forceTriggerLoadMore() {
        if (isLoading) return;
        if (null != mOnScrollListener) {
            int oldpageSize = pagesize;
            pagesize = 1;
            mOnScrollListener.onLoadNextPage(this);
            pagesize = oldpageSize;
        }
    }

    public boolean isLoadingMore() {
        return isLoading;
    }
}
