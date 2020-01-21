package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;


/**
 * Created by ljzyuhenda on 16/7/20.
 */
public class LoadMoreRecyclerView extends RecyclerView{
    private boolean mIsLoadingMore;
    private onLoadMoreListener mOnLoadMoreListener;
    private boolean mIsFooterEnable;
    private boolean mIsHeaderEnable;
    private int mLoadMorePositon;
    private int mFooterResId = R.layout.item_loading;
    private int mHeaderResId;
    private final int TYPE_HEADER = 2;
    private final int TYPE_FOOTER = 1;
    private final int TYPE_LIST = 0;


    public LoadMoreRecyclerView(Context context) {
        super(context);

        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (getAdapter() != null && null != mOnLoadMoreListener  && !mIsLoadingMore && dy > 0) {
                    int lastPosition = getLastVisiblePosition();

                    if (lastPosition == getAdapter().getItemCount() - 1) {
                        mIsFooterEnable = true;
                        getAdapter().notifyItemInserted(getAdapter().getItemCount() - 1);

                        setLoadingMore(true);
                        mLoadMorePositon = getAdapter().getItemCount() - 1;
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public void setOnLoadMoreListener(onLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    public interface onLoadMoreListener {
        void onLoadMore();
    }

    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }

        return position;
    }

    public void setLoadingMore(boolean isLoadingmore) {
        mIsLoadingMore = isLoadingmore;
    }

    public void addHeaderView(int resId) {
        mHeaderResId = resId;
    }

    public void setHeaderEnabled(boolean enable) {
        mIsHeaderEnable = enable;
    }

    public void addFooterView(int resId) {
        mFooterResId = resId;
    }

    public void setAutoLoadMoreEnable(boolean enable) {
        mIsFooterEnable = enable;
    }

    public void notifyMoreFinished(boolean hasMore) {
        setAutoLoadMoreEnable(false);
        getAdapter().notifyItemRemoved(mLoadMorePositon);
//        getAdapter().notifyItemRangeChanged(mLoadMorePositon, getAdapter().getItemCount());
        mIsLoadingMore = false;
    }

    public void notifyDataSetChanged(){
        getAdapter().notifyDataSetChanged();
    }

    /**
     *
     */
    public class AutoLoadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        /**
         * 数据adapter
         */
        private RecyclerView.Adapter mInternalAdapter;

        public AutoLoadAdapter(RecyclerView.Adapter adapter) {
            mInternalAdapter = adapter;
            mIsHeaderEnable = false;
        }

        @Override
        public int getItemViewType(int position) {
            int headerPosition = 0;
            int footerPosition = getItemCount() - 1;

            if (headerPosition == position && mIsHeaderEnable && mHeaderResId > 0) {
                return TYPE_HEADER;
            }
            if (footerPosition == position && mIsFooterEnable) {
                return TYPE_FOOTER;
            }

            return TYPE_LIST;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        mHeaderResId, parent, false));
            }

            if (viewType == TYPE_FOOTER) {
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                        mFooterResId, parent, false));
            }

            return mInternalAdapter.onCreateViewHolder(parent, viewType);

        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type != TYPE_FOOTER && type != TYPE_HEADER) {
                mInternalAdapter.onBindViewHolder(holder, position);
            }
        }

        /**
         * 需要计算上加载更多和添加的头部俩个
         *
         * @return
         */
        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (mIsFooterEnable) count++;
            if (mIsHeaderEnable) count++;

            return count;
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            super.swapAdapter(new AutoLoadAdapter(adapter), true);
        }
    }
}
