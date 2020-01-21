/**
 * <pre>
 * Copyright (C) 2015  Soulwolf XiaoDaoW3.0
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package com.huatu.handheld_huatu.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.huatu.event.IAbsListView;
import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.mvpmodel.BaseListResponse;
import com.huatu.handheld_huatu.utils.LogUtils;


/**
 * 自动实现下拉刷新,加载更多的列表界面
 * <p/>
 * author : Soulwolf Create by 2015/6/17 14:46
 * email  : ToakerQin@gmail.com.
 */
public abstract class ABaseListFragment<RESPONSE extends BaseListResponse> extends AbsRefreshFragment<RESPONSE> implements IonLoadMoreListener, AdapterView.OnItemClickListener  {

    protected RESPONSE mListResponse;
    protected boolean mHasRefresh = true;

    protected int mCurrentPage = 1;//从第一页开始
    protected int mCurrentOffset = 0;
    private   int DEFAULT_LIMIT = 20;
    private   boolean isFirstload=true;

    //protected AbsListViewHandler mSupperScrollView;

    public boolean isFirstLoad() {return  isFirstload;}

    protected abstract IAbsListView getListView();

    public AbsListView getMylistView() {

        return (AbsListView) getListView();
    }

    /**
     * 列表每页的加载的条数
     */
    protected int getLimit() {  return DEFAULT_LIMIT; }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        onInitialize();

        //if(savedInstanceState!=null) XLog.e("ABaseListFragment_onActivityCreated","_isFirstload"+(isFirstload?"true":"false"));
        super.onActivityCreated(savedInstanceState);


    }

    protected void onInitialize() {
     /*   if (isLazyMode()) {
            initPrepare();
        } else */
        {
            onPrepare();

            setListener();
        }


    }

    /**
     * 页面生成完毕,可以对View进行操作
     */
    protected void onPrepare() { }

    /**
     * 设置点击事件
     */
    protected void setListener() { }

    /**
     * 加载更多
     */
    @Override
    public void OnLoadMoreEvent(boolean isRetry) {

       loadMode= RefreshMode.loadmore;
       if(!isRetry)  mCurrentPage++;
       onLoadData();
    }


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

    protected abstract void  onLoadData(int pageIndex, int limit);



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }



    /**
     * 是否正在刷新
     */
    protected boolean hasRefresh() {
        return mHasRefresh;
    }



    @Override
    public void onError(String throwable,int type) {
        if(isFragmentFinished())  return;
        super.onError(throwable, type);
        /*if(loadMode== RefreshMode.loadmore){
             MaterialToast.makeText(getContext(), R.string.xs_networkdata_failed).show();
        }
        else {
             showEmptyLayout();
        }*/
        if(getListView()!=null) getListView().selectionFromTop();
    }


    public void showEmpty(){

        if(loadMode== RefreshMode.loadmore) {
            //MaterialToast.makeText(getContext(), R.string.xs_networkdata_failed).show();
            getListView().checkloadMore(0);
            getListView().hideloading();
         }
        else showEmptyLayout();
    }


    @Override
    public void onSuccess(RESPONSE response)   {
        if(isFragmentFinished())  return;
        super.onSuccess(response);
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

    @Override
    public void onRefresh() {
        loadMode= RefreshMode.refresh;
        try {
            if(getListView()!=null){
                getListView().reset();
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
        if (getListView()!=null) {
            getListView().notifyDataSetChanged(positionStart,itemSize);
        }
    }



}
