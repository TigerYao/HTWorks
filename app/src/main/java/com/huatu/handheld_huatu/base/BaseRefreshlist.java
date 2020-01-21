package com.huatu.handheld_huatu.base;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.helper.retrofit.KindRetrofitCallBack;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallback;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallbackWrapper;
import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;
import com.huatu.handheld_huatu.ui.CommSmallLoadingView;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

/**
 * Created by cjx on 2016/8/25.
 * 用于dialog中分页
 */
public abstract class BaseRefreshlist<RESPONSE extends BaseListResponse> implements KindRetrofitCallBack<RESPONSE> {
    protected RESPONSE mListResponse;
    MenuRefreshMode loadMode=MenuRefreshMode.reset;
    int mCurrentPage=1;
    protected int getLimit() {  return 20; }


    protected abstract CommSmallLoadingView getLoadingView();
   // public abstract RecyclerView  getListView();

    public void OnLoadMoreEvent() {

        loadMode= MenuRefreshMode.loadmore;
        mCurrentPage++;
        onLoadData();
    }


    public void onRetry() {
        loadMode= MenuRefreshMode.loadmore;
        //  mCurrentPage=1;
        //reset();
        if(null!=getLoadingView()) getLoadingView().showLoadingStatus();
        onLoadData();
    }

    public void onRefresh() {
        loadMode= MenuRefreshMode.refresh;

        mCurrentPage=1;
        //reset();
        if(null!=getLoadingView()) getLoadingView().showLoadingStatus();
        onLoadData();
    }
    public boolean isCurrentRefreshMode(){ return  loadMode== MenuRefreshMode.refresh;}

    private void onLoadData() {
        int limit = getLimit();
        // mCurrentOffset = mCurrentPage * limit;
        onLoadData(mCurrentPage, limit);
    }

    protected abstract void  onLoadData(int pageIndex, int limit);


    /**
     * 获取一个网络访问回调
     */
    protected RetrofitCallback<RESPONSE> getCallback() {
        return new RetrofitCallbackWrapper<RESPONSE>(this);
    }

    protected abstract void notifyDataSetChanged();

    protected abstract void checkMoreAndHideLoad(int size);

    @Override
    public void onSubscriberStart(){}

    @Override
    public void onSuccess(RESPONSE response)   {
        if(isFragmentFinished())  return;

        LogUtils.e("onSuccess", "onSuccess");
        if (mListResponse != null) {

            int size = response.getListResponse().size();
            LogUtils.e("size", "onSuccess" + size);
            if(size==0){
                showEmpty();
                return;
            }
            if(loadMode== MenuRefreshMode.loadmore){
                /*getListView().checkloadMore(size);
                getListView().hideloading();*/
                checkMoreAndHideLoad(size);
            }

            if(loadMode== MenuRefreshMode.reset||loadMode== MenuRefreshMode.refresh)
                mListResponse.clearList();
            if(null!=getLoadingView()) getLoadingView().hide();
            mListResponse.addAllData(response);
            notifyDataSetChanged();
        }
        else
            showEmpty();
    }

    public void showEmpty(){

        if(loadMode== MenuRefreshMode.loadmore) {
            //MaterialToast.makeText(getContext(), R.string.xs_networkdata_failed).show();
           /* getListView().checkloadMore(0);
            getListView().hideloading();*/
            checkMoreAndHideLoad(0);
        }
        else {
            mListResponse.clearList();
            notifyDataSetChanged();
            checkMoreAndHideLoad(0);
            if(null!=getLoadingView()) getLoadingView().showEmptyStatus();
           // showEmptyLayout();
        }
    }

    @Override
    public void onError(String throwable,int type) {
        if(isFragmentFinished())  return;

        checkMoreAndHideLoad(getLimit());
        ToastUtils.makeText( UniApplicationContext.getContext(), R.string.xs_networkdata_failed).show();
        if(loadMode== MenuRefreshMode.loadmore)  {
            if(null!=getLoadingView()) getLoadingView().showNetworkError();
        }

        else
          if(null!=getLoadingView()) getLoadingView().showNetworkError();
        /*if(loadMode== RefreshMode.loadmore){
             MaterialToast.makeText(getContext(), R.string.xs_networkdata_failed).show();
        }
        else {
             showEmptyLayout();
        }*/
       // if(getListView()!=null) getListView().selectionFromTop();
    }
}
