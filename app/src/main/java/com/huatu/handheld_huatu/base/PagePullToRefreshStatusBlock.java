package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.huatu.event.IAbsListView;
import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.TokenConflictActivity;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.arena.activity.DailySpecialSettingActivity;
import com.huatu.handheld_huatu.business.login.LoginByPasswordActivity;
import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.library.PullToRefreshBase;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2019\10\14 0014.
 */

public class PagePullToRefreshStatusBlock<RESPONSE extends BaseListResponse> {

    public RESPONSE mListResponse;
    protected boolean mHasRefresh = true;

    protected int mCurrentPage = 1;//从第一页开始
    protected int mCurrentOffset = 0;
    private   int DEFAULT_LIMIT = 20;
    private   boolean isFirstload=true;

    //protected AbsListViewHandler mSupperScrollView;
    TypeFactory mTypeFactory;
    public boolean isFirstLoad() {return  isFirstload;}

    protected  IAbsListView mListView;
    protected  IAbsListView getListView(){return mListView;}

    private OnRefreshFinishListener mOnRefreshFinishListener;

    CommloadingView mCommloadingView;
    /**
     * 列表每页的加载的条数
     */
    protected int getLimit() {  return DEFAULT_LIMIT; }

    public void setLimit(int limit){
        DEFAULT_LIMIT=limit;
    }

    //已经刷新得到的数据源总和
    protected int mAllLimit = 0;

    public enum RefreshMode {
        /**
         * 重设数据
         */
        reset,
        /**
         * 拉取更多
         */
        loadmore,
        /**
         * 刷新最新
         */
        refresh
    }

    private RefreshMode loadMode= RefreshMode.reset;

    public boolean isCurrentResetMode(){ return  loadMode== RefreshMode.reset;}

    public boolean isCurrentReMode(){ return  loadMode== RefreshMode.refresh||loadMode== RefreshMode.reset;}
    /**
     * 加载更多
     */

/*    private void OnLoadMoreEvent(boolean isRetry) {
        loadMode= RefreshMode.loadmore;
        if(!isRetry)  mCurrentPage++;
        onLoadData();
    }*/


    public synchronized void onFirstLoad(){
        if(isFirstload){
            showFirstLoading();
            isFirstload=false;
            loadMode= RefreshMode.reset;
            onLoadData();
        }
    }

    /**
     * 分页加载数据
     */

    public void onLoadData() {
        int limit = getLimit();
        // mCurrentOffset = mCurrentPage * limit;
        onLoadData(mCurrentPage, limit);
    }


    private  void dealError(final Throwable e) {
        if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            switch (exception.getErrorCode()) {
                case ApiErrorCode.ERROR_TOKEN_CONFLICT:
                    TokenConflictActivity.newIntent( UniApplicationContext.getContext(), exception.getErrorMsg());
                    return;
                case ApiErrorCode.ERROR_SESSION_TIMEOUT:
                    ActivityStack.getInstance().finishAllActivity();
                    LoginByPasswordActivity.newIntent(UniApplicationContext.getContext());
                    return;
                case ApiErrorCode.ERROR_NOT_SETTING_SPECIAL:
                    Activity topAct = ActivityStack.getInstance().getTopActivity();
                    if (topAct != null) {
                        Intent intent = new Intent(topAct, DailySpecialSettingActivity.class);
                        intent.putExtra("fromActivity", "HomeFragment");
                        topAct.startActivity(intent);
                    } else {
                        Intent intent = new Intent(UniApplicationContext.getContext(),
                                DailySpecialSettingActivity.class);
                        intent.putExtra("fromActivity", "HomeFragment");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        UniApplicationContext.getContext().startActivity(intent);
                    }
                    return;
                default:
                    break;
            }
            String message = TextUtils.isEmpty(exception.getErrorMsg()) ?  "网络请求错误，请重试"
                    : exception.getErrorMsg();
            onError(message, 1);

        } else if (e instanceof HttpException) {
            onError(" 网络加载出错", ((HttpException)e).code()==504 ?3:2);

        } else {
            onError("数据加载出错", 3);
        }
    }

    protected   void  onLoadData(int pageIndex, int limit){

        Observable<RESPONSE> baseObservable= mTypeFactory.getListObservable(pageIndex,limit);
        Subscriber<RESPONSE> subscriber = new Subscriber<RESPONSE>() {
            @Override
            public void onCompleted() {  }

            @Override
            public void onError(Throwable e) {
                 dealError(e);  //LogUtils.e("onError",e.getMessage());
            }

            @Override
            public void onNext(RESPONSE model) {
                // LogUtils.e("onNext", GsonUtil.GsonString(model));
                if (model.code == ApiErrorCode.ERROR_SUCCESS||model.code==200) {//教育的地址响应码为200
                    onSuccess(model);
                } else {
                    ApiException exception = new ApiException(model.code, model.message);
                    dealError(exception);
                }
            }
        };
        mTypeFactory.getComSubscription().add(baseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber));
    }

    /**
     * 是否正在刷新
     */
    protected boolean hasRefresh() {
        return mHasRefresh;
    }

    public void onError(String throwable, int type) {
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if(mAllLimit<=0){
                onRefreshCompleted();
                showNetworkPrompt(type);
                if(getListView()!=null) getListView().selectionFromTop();
                // initNotify("网络加载出错~");
            }
            else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(),"网络加载出错~");
            }
        }
    }

    public void showEmpty(){
         if(loadMode== RefreshMode.loadmore) {
            //MaterialToast.makeText(getContext(), R.string.xs_networkdata_failed).show();
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
        else showEmptyLayout();
    }

    protected void onRefreshCompleted(){
        if((null!=mOnRefreshFinishListener)){
             mOnRefreshFinishListener.onRefreshCompleted();
        }
    }

    /**
     * 空状态显示
     */
    protected void showEmptyLayout() {
        if (mCommloadingView != null && !mCommloadingView.isShownStatus(CommloadingView.StatusMode.empty)) {
            mCommloadingView.showEmptyStatus();
        }
    }

    private void hideEmptyLayout(){
        if (mCommloadingView != null ) {//&& mStatusLayout.isShown()去掉判断，父不可见时，此时会导致让当前不可见
            mCommloadingView.hide();
        }
    }

    /**
     * 显示首次加载layout
     */
    protected void showFirstLoading() {
        if (mCommloadingView != null && !mCommloadingView.isShownStatus(CommloadingView.StatusMode.loading)) {
            mCommloadingView.showLoadingStatus();
        }
    }

    /**
     * 显示无网络layout   //1 业务出错，2 http出错   3 网络出错
     */
    protected void showNetworkPrompt(int type) {
         if(type==3) {
            if (mCommloadingView != null && !mCommloadingView.isShownStatus(CommloadingView.StatusMode.network)) {
                mCommloadingView.showNetworkTip();
            }
        }else {
            if (mCommloadingView != null && !mCommloadingView.isShownStatus(CommloadingView.StatusMode.serverError)) {
                mCommloadingView.showServerError();
            }
        }
    }

    public void onSuccess(RESPONSE response)   {
        onRefreshCompleted();
        hideEmptyLayout();
        LogUtils.e("onSuccess", "onSuccess");
        if (mListResponse != null) {

            int size = response.getListResponse().size();
            LogUtils.e("size", "onSuccess" + size);
            if(size==0){
                showEmpty();
                return;
            }
            if(loadMode== RefreshMode.loadmore&&getListView()!=null){
                getListView().checkloadMore(size);
                getListView().hideloading();
            }

            if(loadMode== RefreshMode.reset||loadMode== RefreshMode.refresh){
                mListResponse.clearList();
                notifyDataSetChanged(0,size);//fix bug   https://www.jianshu.com/p/a15764f6d673
            }

            mListResponse.addAllData(response);
            notifyDataSetChanged(mAllLimit,size);
            mAllLimit += response.getListResponse().size();
        }
        else
            showEmpty();
    }

    protected boolean addAllData(RESPONSE response) {
       return false;
    }

    private void onRefresh() {
        loadMode= RefreshMode.refresh;
        try {
            if(mListView!=null){
                mListView.reset();
            }
            else {
                return;
            }
        }catch (Exception e){
            return;
        }
        reset();
        onLoadData();
    }

    /**
     * 刷新已经加载过的数据
     */
    private void refreshReload() {

        mHasRefresh = true;
        mCurrentOffset = 0;
        onLoadData(1, mAllLimit);
        mAllLimit = 0;
    }

    protected void reset() {
        mCurrentOffset = 0;
        mCurrentPage = 1;
        mHasRefresh = true;
        mAllLimit = 0;
    }

    protected void notifyDataSetChanged(int positionStart,int itemSize) {
        if (mListView!=null) {
            mListView.notifyDataSetChanged(positionStart,itemSize);
        }
    }


    public interface TypeFactory<RESPONSE>{

         Observable<RESPONSE> getListObservable(int pageIndex, int limit) ;

         RecyclerView.Adapter getAdapter(RESPONSE response);

         CompositeSubscription getComSubscription();
    }

    public interface OnRefreshFinishListener{
        void  onRefreshCompleted();
    }

    public void setOnRefreshFinishListener(OnRefreshFinishListener refFinishListener){
        this.mOnRefreshFinishListener=refFinishListener;
    }

    private void superOnRefresh(){
        onRefresh();
    }

    public void setPullRefreshView(PullRefreshRecyclerView listview){
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        setListView(listview.getRefreshableView());
    }

    public PagePullToRefreshStatusBlock setListView(IAbsListView listView){
        this.mListView=listView;
        mListView.setPagesize(getLimit());
        mListView.setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                loadMode=RefreshMode.loadmore;
                if(!isRetry)  mCurrentPage++;
                onLoadData();
            }
        });
        return this;
    }

    public PagePullToRefreshStatusBlock setloadingView(CommloadingView loadingView){
        this.mCommloadingView=loadingView;
        mCommloadingView.setOnRtyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        return this;
    }

    public PagePullToRefreshStatusBlock setTypeFactory(TypeFactory typeFactory){
        this.mTypeFactory=typeFactory;
        return this;
    }


    public void build(){
         RecyclerView.Adapter adapter=  mTypeFactory.getAdapter(mListResponse);
        mListView.setRecyclerAdapter(adapter);
    }

}
