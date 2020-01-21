package com.huatu.handheld_huatu.business.ztk_zhibo.refresh;

public interface OnRefreshListener {
	/**
	 * 下拉刷新
	 */
	void onDownPullRefresh();
	/**
	 * 上拉加载
	 */
	void onLoadingMore();
}
