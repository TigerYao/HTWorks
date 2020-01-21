/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.util.ArrayList;

/*
 * 同时实现下拉刷新和左右滑动删除的ListView
 */
public class XListViewNestedS extends ListView implements OnScrollListener , NestedScrollingChild {

	protected float mLastY = -1; // save event y
	protected float mLastX = -1; // save event x
	protected float mFirstY = -1; // save event y
	protected float mFirstX = -1; // save event x
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	protected IXListViewListener mListViewListener;

	// -- header view
	protected XListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
//	private TextView mHeaderTimeView;
	protected int mHeaderViewHeight; // header view's height
	protected boolean mEnablePullRefresh = true;
	protected boolean mPullRefreshing = false; // is refreashing.

	private ArrayList<String> mHeaderViewHashList = new ArrayList<String>();
	private ArrayList<String> mFooterViewHashList = new ArrayList<String>();
	// -- footer view
	protected XListViewFooter mFooterView;
	protected boolean mEnablePullLoad;
	protected boolean mPullLoading;
	protected boolean mSlideEnable = false;
	private boolean mIsFooterReady = false;

	// total list items, used to detect is at the bottom of listview.
	protected int mTotalItemCount;
	protected int firstVisibleItem;
	private int mAheadPullUpCount = 0; // 提前调用pullup的提前量
	private int mLastVisibleIndex = -1;
	private int mLastItemCount = -1;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	protected final static int SCROLL_DURATION = 400; // scroll back duration
	protected final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	protected final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.
	protected int mFlipDirection = 0;//0-vertical 1:horizon
	protected int mTouchSlop;
	protected int selectedItemPosition = -1;

	private final NestedScrollingChildHelper mScrollingChildHelper = new NestedScrollingChildHelper(this);


    /* listView的每一个item的布局 */
//    private View mEmptyErrorView;
//    protected ViewGroup mEmptyViewGroup;
//    protected ViewGroup mEmptyView;
//    protected AutoAttachRecyclingImageView mEmptyIconView;
//    protected TextView mEmptyTipView;
//    protected TextView mEmptyReloadView;
//    private int mEmptyResId = R.layout.common_list_empty;

	/**
	 * @param context
	 */
	public XListViewNestedS(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListViewNestedS(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListViewNestedS(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	@SuppressWarnings("deprecation")
	private void initWithContext(Context context) {
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
//		mHeaderTimeView = (TextView) mHeaderView
//				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);
		// init footer view
		mFooterView = new XListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		mTouchSlop = DisplayUtil.dp2px(7);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setNestedScrollingEnabled(true);
		}

//		mEmptyViewGroup = (LinearLayout) LayoutInflater.from(context).inflate(
//				R.layout.list_view_empty_layout, null);
//		addFooterView(mEmptyViewGroup, null, false);
//		mEmptyErrorView = new EmptyErrorView(context);
//		View btnReload = mEmptyErrorView.findViewById(R.id.list_network_error_reload_tv);
//		btnReload.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(mListViewListener != null) {
//					mListViewListener.onRefresh();
//				}
//			}
//		});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	@Override
	public void addHeaderView(View v) {
		int viewHash = v.hashCode();
		if(mHeaderViewHashList != null && mHeaderViewHashList.size() > 0) {
			for(String hash : mHeaderViewHashList) {
				if(hash != null && hash.equals(String.valueOf(viewHash))) {
					return;
				}
			}
		}
		mHeaderViewHashList.add(String.valueOf(viewHash));
		super.addHeaderView(v);
	}

	@Override
	public boolean removeHeaderView(View v) {
		int viewHash = v.hashCode();
		boolean isAdded = false;
		if(mHeaderViewHashList != null && mHeaderViewHashList.size() > 0) {
			for(String hash : mHeaderViewHashList) {
				if(hash != null && hash.equals(String.valueOf(viewHash))) {
					isAdded = true;
					break;
				}
			}
			if(isAdded) {
				mHeaderViewHashList.remove(String.valueOf(viewHash));
			}
		}
		return super.removeHeaderView(v);
	}

	@Override
	public void addFooterView(View v) {
		int viewHash = v.hashCode();
		if(mFooterViewHashList != null && mFooterViewHashList.size() > 0) {
			for(String hash : mFooterViewHashList) {
				if(hash != null && hash.equals(String.valueOf(viewHash))) {
					return;
				}
			}
		}
		mFooterViewHashList.add(String.valueOf(viewHash));
		super.addFooterView(v);
	}

	@Override
	public boolean removeFooterView(View v) {
		int viewHash = v.hashCode();
		boolean isAdded = false;
		if(mFooterViewHashList != null && mFooterViewHashList.size() > 0) {
			for(String hash : mFooterViewHashList) {
				if(hash != null && hash.equals(String.valueOf(viewHash))) {
					isAdded = true;
					break;
				}
			}
			if(isAdded) {
				mFooterViewHashList.remove(String.valueOf(viewHash));
			}
		}
		return super.removeFooterView(v);
	}

	/**
	 * enable or disable pull down refresh feature.
	 *
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * enable or disable pull up load more feature.
	 *
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		mPullRefreshing = false;
		resetHeaderHeight();
	}

	/**
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		mPullLoading = false;
		mFooterView.setState(XListViewFooter.STATE_NORMAL);
	}

	public void setSlideEnable(boolean isEnable) {
		mSlideEnable = isEnable;
		if(mSlideEnable) {
			mFlipDirection = -1;
		} else {
			mFlipDirection = 0;
		}
	}

	protected void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	protected void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * reset header view's height.
	 */
	protected void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}

		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		// trigger computeScroll
		invalidate();
	}

	protected void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load more.
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);
	}

	protected void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	protected synchronized void startLoadMore() {
		if(mPullLoading) {
			return;
		}
		LogUtils.e("startLoadMore");
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	private int lastFirstVisibleItem = 0;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getY();
		}
		if (mLastX == -1) {
			mLastX = ev.getX();
		}
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mFirstX = ev.getX();
			mFirstY = ev.getY();
			mLastX = ev.getX();
			mLastY = ev.getY();
			lastFirstVisibleItem = firstVisibleItem;
			if(mSlideEnable) {
				mFlipDirection = -1;
			} else {
				mFlipDirection = 0;
			}
			if(mEnablePullLoad) {
				mFooterView.show();
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
//			Log.i("changxin", "XListView: dispatchTouchEvent------MotionEvent.ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			if(mSlideEnable && mFlipDirection == 1) {
//				Log.i("changxin", "XListView: dispatchTouchEvent------MotionEvent.ACTION_MOVE----------return false");
				return super.dispatchTouchEvent(ev);
			}
			if(!mEnablePullRefresh && firstVisibleItem == 0) {
				return true;
			}
			if(mSlideEnable && mFlipDirection == -1) {
				if(Math.abs(ev.getY() - mFirstY) > mTouchSlop) {
					mFlipDirection = 0;//纵向
				} else if(Math.abs(ev.getX() - mFirstX) > mTouchSlop) {
					mFlipDirection = 1;//横向
				}
			}
			if(mFlipDirection == 0) {
				mLastX = ev.getX();
				final float deltaY = ev.getY() - mLastY;
				mLastY = ev.getY();
				if (getFirstVisiblePosition() == 0
						&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
					// the first item is showing, header has shown or pull down.
					updateHeaderHeight(deltaY / OFFSET_RADIO);
					invokeOnScrolling();
				} else if (mEnablePullLoad && getLastVisiblePosition() == mTotalItemCount - 1
						&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
					// last item, already pulled up or want to pull up.
					updateFooterHeight(-deltaY / OFFSET_RADIO);
				}
			} else if(mFlipDirection == 1) {
				if(selectedItemPosition < this.getHeaderViewsCount()
						|| selectedItemPosition >= this.getCount() - this.getFooterViewsCount()) {
					return super.dispatchTouchEvent(ev);
				}
				return super.dispatchTouchEvent(ev);
			}
//			Log.i("changxin", "XListView: dispatchTouchEvent------MotionEvent.ACTION_MOVE");
			break;
		default:
			mLastX = -1; // reset
			mLastY = -1; // reset
			mFirstX = -1;
			mFirstY = -1;
//			if(mSlideEnable) {
//				return false;
//			}
			if(!mSlideEnable || mFlipDirection != 1) {
				if (getFirstVisiblePosition() == 0) {
					// invoke refresh
					if (mEnablePullRefresh
							&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
						mPullRefreshing = true;
						mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
						if (mListViewListener != null) {
							mListViewListener.onRefresh();
						}
					}
					resetHeaderHeight();
				} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
					// invoke load more.
					if (mEnablePullLoad
					    && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA
					    && !mPullLoading) {
						startLoadMore();
					}
					resetFooterHeight();
				}
			}
			if(mSlideEnable && mFlipDirection == 1) {
				Log.i("changxin", "横向滑动结束");
				mFlipDirection = -1;
				return super.dispatchTouchEvent(ev);
			}
//			Log.i("changxin", "XListView: dispatchTouchEvent------MotionEvent.ACTION_UP");
			mFlipDirection = -1;
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	protected void buttonShow(View v, AnimationListener listener) {
    	Log.i("changxin", "buttonShow");
    	if(v != null) {
    		v.setVisibility(View.VISIBLE);
    		Animation anim = AnimationUtils.loadAnimation(
	        		getContext(), R.anim.slide_delete_button_show);
    		if(listener != null) {
    			anim.setAnimationListener(listener);
    		}
	        v.startAnimation(anim);
    	}
    }

    protected void buttonHide(final View v) {
    	Log.i("changxin", "buttonHide");
    	if(v != null) {
    		Animation anim = AnimationUtils.loadAnimation(
            		getContext(), R.anim.slide_delete_button_hide);
    		anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					v.setVisibility(View.GONE);
				}
			});
    		v.startAnimation(anim);
    	}
    }

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
//		LogUtils.i("firstVisibleItem = " + firstVisibleItem);
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
		if(mEnablePullLoad) {
			this.firstVisibleItem = firstVisibleItem;
            int lastVisibleItem = firstVisibleItem + visibleItemCount;
            // 距离底部mAheadPullUpCount调用加载更多，或滑动到底部也掉加载更多(为了解决加载出来的总数小于mAheadPullUpCount，导致不自动加载)
			if ((lastVisibleItem == totalItemCount && lastVisibleItem != mLastVisibleIndex)
					|| (lastVisibleItem + mAheadPullUpCount >= totalItemCount && mLastVisibleIndex + mAheadPullUpCount < mLastItemCount)) {
				// 在最后一个item内移动时，不要触发loadmore
                if (!mEnablePullLoad || mPullLoading)
        			return;
        		// 数量充满屏幕才触发
        		if (isFillScreenItem()) {
        			startLoadMore();
        		}
            }
            mLastVisibleIndex = lastVisibleItem;
            mLastItemCount = totalItemCount;
		}
	}

	/**
	 * 条目是否填满整个屏幕
	 */
	private boolean isFillScreenItem() {
		final int firstVisiblePosition = getFirstVisiblePosition();
		final int lastVisiblePostion = getLastVisiblePosition() - getFooterViewsCount();
		final int visibleItemCount = lastVisiblePostion - firstVisiblePosition + 1;
		final int totalItemCount = getCount() - getFooterViewsCount();
		if (visibleItemCount < totalItemCount)
			return true;
		return false;
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		public void onRefresh();
		public void onLoadMore();
	}

	public void setFooterViewVisible(boolean flag) {
		if (mEnablePullLoad && !flag) {
			mFooterView.hide();
		} else if (mEnablePullLoad && flag) {
			mFooterView.show();
		}
	}

	public boolean isPullRefreshing() {
		return mPullRefreshing;
	}


    public void setFooterText(String content){
        if(mFooterView!=null){
            mFooterView.show();
            this.setFooterViewVisible(true);
            mFooterView.setFooterText(content);
        }
    }

	@Override
	public void setNestedScrollingEnabled(boolean enabled) {
		mScrollingChildHelper.setNestedScrollingEnabled(enabled);
	}

	@Override
	public boolean isNestedScrollingEnabled() {
		return mScrollingChildHelper.isNestedScrollingEnabled();
	}

	@Override
	public boolean startNestedScroll(int axes) {
		return mScrollingChildHelper.startNestedScroll(axes);
	}

	@Override
	public void stopNestedScroll() {
		mScrollingChildHelper.stopNestedScroll();
	}

	@Override
	public boolean hasNestedScrollingParent() {
		return mScrollingChildHelper.hasNestedScrollingParent();
	}

	@Override
	public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
										int dyUnconsumed, int[] offsetInWindow) {
		return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
				dxUnconsumed, dyUnconsumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
		return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
	}

	@Override
	public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
		return mScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
	}

	@Override
	public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
		return mScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
	}


    public View getFooterView(){
        return mFooterView;
    }

//    boolean isEmptyViewAdded = false;
//	public void showEmptyView() {
//		if(mEmptyView == null) {
//	        LayoutInflater inflater = LayoutInflater.from(getContext());
//	        mEmptyView = (RelativeLayout) inflater.inflate(mEmptyResId, null);
//	        mEmptyIconView = (AutoAttachRecyclingImageView) mEmptyView.findViewById(R.id.empty_icon);
//	        mEmptyTipView = (TextView) mEmptyView.findViewById(R.id.empty_tip);
//	        mEmptyReloadView = (TextView) mEmptyView.findViewById(R.id.empty_error_btn);
//	        mEmptyReloadView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if(mListViewListener != null) {
//						mListViewListener.onRefresh();
//					}
//				}
//			});
//		}
//        if(!isEmptyViewAdded) {
//    		mEmptyViewGroup.removeAllViews();
//    		mEmptyViewGroup.addView(mEmptyView);
//        } else {
//    		mEmptyView.setVisibility(View.VISIBLE);
//        }
//		Adapter adpter = getAdapter();
//		if (adpter != null && adpter instanceof BaseAdapter) {
//			((BaseAdapter) adpter).notifyDataSetChanged();
//		}
//		isEmptyViewAdded = true;
//	}
//
//    public void hideEmptyView() {
//    	if(isEmptyViewAdded) {
//    		mEmptyViewGroup.removeAllViews();
//    		Adapter adpter = getAdapter();
//    		if (adpter != null && adpter instanceof BaseAdapter) {
//    			((BaseAdapter) adpter).notifyDataSetChanged();
//    		}
//    	}
//    	isEmptyViewAdded = false;
//    }
//
//	public void setEmptyViewResOnly(String textRes){
//		showEmptyView();
//		if(mEmptyTipView != null) {
//			mEmptyTipView.setText(textRes);
//		}
//		mEmptyIconView.setVisibility(View.GONE);
//	}
//
//	public void setEmptyViewRes(String textRes){
//    	setEmptyViewRes(R.drawable.empty_view_no_data, textRes);
//		mEmptyReloadView.setVisibility(View.GONE);
//	}
//
//    @SuppressWarnings("deprecation")
//	public void setEmptyViewRes(int iconRes, String textRes) {
//    	showEmptyView();
//    	if(mEmptyIconView != null) {
//		    mEmptyIconView.setImageResourceInner(iconRes);
//    	}
//    	if(mEmptyTipView != null) {
//    		mEmptyTipView.setText(textRes);
//    	}
//    }
//
//    public void setEmptyBtnView(String text, int textColor, int bgRes, OnClickListener listener) {
//    	showEmptyView();
//    	if(mEmptyReloadView != null) {
//    		mEmptyReloadView.setText(text);
//    		mEmptyReloadView.setTextColor(textColor);
//    		mEmptyReloadView.setBackgroundResource(bgRes);
//    		mEmptyReloadView.setOnClickListener(listener);
//    		mEmptyReloadView.setVisibility(View.VISIBLE);
//    	}
//    }
//
//	public void showEmptyBtnView() {
//		if(mEmptyReloadView != null) {
//			mEmptyReloadView.setVisibility(View.VISIBLE);
//		}
//	}
//
//	public void hideEmptyBtnView() {
//		if(mEmptyReloadView != null) {
//			mEmptyReloadView.setVisibility(View.GONE);
//		}
//	}
//
//    public void showNetError(boolean isImg) {
//    	showEmptyView();
//    	setEmptyViewRes(R.drawable.empty_view_net_error, "主人，网络异常，请检查");
//    	if(!isImg) {
//    		mEmptyIconView.setVisibility(View.INVISIBLE);
//    	} else {
//    		mEmptyIconView.setVisibility(View.VISIBLE);
//    	}
//		mEmptyReloadView.setVisibility(View.VISIBLE);
//    }
}
