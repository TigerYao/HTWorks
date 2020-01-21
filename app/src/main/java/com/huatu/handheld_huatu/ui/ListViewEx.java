package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.huatu.event.IAbsListView;
import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;


public class ListViewEx extends ListView implements OnScrollListener, IAbsListView {

    public interface onTouchCancelListener{
        boolean isNeedTouchCancel();
    }

    onTouchCancelListener mOnTouchCancelListener;
    public void setonTouchCancelListener(onTouchCancelListener onTouchCancelListener){
        mOnTouchCancelListener=onTouchCancelListener;
    }

    boolean mNeedCancel=false;
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        if(mNeedCancel){
            mNeedCancel=false;
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
    ListAdapter mBaseAdapter;

    CommListFooter mloadingview;

    boolean isLoading = false;
    IonLoadMoreListener monLoadMoreListener;
    private boolean mLastItemVisible;
    boolean canloadmore = true;

    OnScrollListener mOnScrollListener;

    public void addOnScrollListener(OnScrollListener OnScrollListener) {
        mOnScrollListener = OnScrollListener;
    }

    public ListViewEx(Context context) {
        super(context);
        initFoot();
    }

    public ListViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFoot();

    }

    public ListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFoot();

    }

    private void initFoot() {
        super.setOnScrollListener(this);

        //mloadingview= super.getFootView();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (null != mOnScrollListener) mOnScrollListener.onScrollStateChanged(view, scrollState);

        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && null != monLoadMoreListener && mLastItemVisible) {
            //mOnLastItemVisibleListener.onLastItemVisible();
            onLastItemVisible();
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (null != mOnScrollListener)
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        // send to user's listener
        if (null != monLoadMoreListener) {
            mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
        }
    }

    private void onLastItemVisible() {

        if (!canloadmore) return;
       // if (mBaseAdapter.getCount() < pagesize) return;
        if (isLoading) return;

        isLoading = true;

        mloadingview.showLoading();//.show(true);//.setVisibility(View.VISIBLE);
        //	mloadingview.setState(PullToRefreshListFooter.STATE_LOADING);
        super.setSelection(mBaseAdapter.getCount() - 1);
        if (null != monLoadMoreListener) monLoadMoreListener.OnLoadMoreEvent(false);
    }

    @Override
    public void setMyAdapter(ListAdapter madapter) {
        mBaseAdapter = madapter;
        super.setAdapter(madapter);
    }

    @Override
    public void setAdapter(ListAdapter madapter) {
        mBaseAdapter = madapter;
        super.setAdapter(madapter);
    }

    @Override
    public void setRecyclerAdapter(RecyclerView.Adapter madapter){

    }

    @Override
    public void setOnLoadMoreListener(IonLoadMoreListener onLoadMoreListener) {

        if (null != mloadingview) return;
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        mloadingview = (CommListFooter) inflater.inflate(R.layout.custom_default_listview_foot_my, null);//custom_default_listview_foot_my
        this.addFooterView(mloadingview);
        //mloadingview.setVisibility(View.GONE);
        mloadingview.show(false);
        monLoadMoreListener = onLoadMoreListener;

    }

    @Override
    public void notifyDataSetChanged(int positionStart,int itemSize) {
        if (mBaseAdapter != null && mBaseAdapter instanceof BaseAdapter) {
            ((BaseAdapter) mBaseAdapter).notifyDataSetChanged();
        }
    }

    @Override
    public void setPagesize(int curPagesize) {
        pagesize = curPagesize;
    }

    @Override
    public void hideloading() {
        isLoading = false;
        //mloadingview.setVisibility(View.GONE);
        //mloadingview.setState(PullToRefreshListFooter.STATE_NORMAL);
    }


    @Override
    public void checkloadMore(int size) {
        if (size < pagesize) {
            mloadingview.showEnd();
           //mloadingview.show(false);
            canloadmore = false;
        }
    }

    public void checkloadMore(boolean hasNext) {
        if (!hasNext) {
            mloadingview.show(false);
            canloadmore = false;
        }
    }

    // public void set  onRefreshComplete
    @Override
    public void reset() {
        canloadmore = true;
        isLoading = false;
        mloadingview.show(false);
        //super.stopRefresh();
    }

    public void resetLoading(){
        reset();
        mloadingview.show(false);
    }

 /*   public interface IonLoadMoreListener {
        void OnLoadMoreEvent();

    }*/

    @Override
    public void selectionFromTop() {

       if (Build.VERSION.SDK_INT >= 21){

           this.setSelectionFromTop(0,0);
       }
        else
             this.setSelection(0);
    }

    @Override
    public  void showNetWorkError(){}

    /**
     * 设置Listview滚动到底部
     *
     * @param position
     */
    public void setSelectionFromBottom(final int position) {
        ListAdapter listAdapter = this.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int startPosition = 0;
        //循环计算listview的item高度
        for (int i = position; i >= 0; i--) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, this);
            if (listItem == null) continue;
            listItem.measure(0, 0); // 计算子项View 的宽高,注意子Textview的行高
            totalHeight += listItem.getMeasuredHeight() + this.getDividerHeight(); // 统计所有子项的总高度
           // XLog.e("11111111", totalHeight + "  ");
            if (totalHeight > this.getHeight()) {
                startPosition = i;
                break;
            }
        }
        this.setSelectionFromTop(startPosition, -(totalHeight - this.getHeight()));
    }

    public void setSelectionFromBottom(final int position,final  int viewHeight){

        this.setSelectionFromTop(position, this.getBottom() - viewHeight);
    }

    //尺寸调整
    private OnSizeChangedListener onSizeChangedListener;

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }

    public interface OnSizeChangedListener {
        public void onSizeChanged();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(null!=onSizeChangedListener) onSizeChangedListener.onSizeChanged();
    }
}
