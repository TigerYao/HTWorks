/**
 * <pre>
 * Copyright 2014-2019 Soulwolf AppStructure
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
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.helper.retrofit.KindRetrofitCallBack;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallback;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallbackWrapper;
import com.huatu.handheld_huatu.mvpmodel.BaseResponse;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.DisplayUtil;


/**
 * Fragment基类
 * <p/>
 * author : Soulwolf Create by 2015/6/11 16:49
 * email  : ToakerQin@gmail.com.RetrofitCallback<HostActivityResponse>
 */
public abstract class AbsRefreshFragment<RESPONSE extends BaseResponse> extends AbsFragment implements KindRetrofitCallBack<RESPONSE>, SwipeRefreshLayout.OnRefreshListener {



    protected SwipeRefreshLayout mSwipeRefreshLayout;

     //获取状态显示layout  rootcontainer


    CommloadingView mStatusLayout;


    private boolean hasFirstResume = true;


    public CommloadingView getEmptyLayout(){
        return  mStatusLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parserParams(getArguments());
        }

    }

    protected void parserParams(Bundle arg) {

    }

    /**
     * 设置返回按钮是否可用
     * @param enabled 可用状态
     */
    protected void setHomeAsUpEnabled(boolean enabled){
        if(getTitleBar() != null){
            getTitleBar().setDisplayHomeAsUpEnabled(enabled);
        }
    }

    /**
     * 返回按钮的点击
     */
    protected void onGoBack(){
        if(!hasResult()){
            setResult(RESULT_CANCEL);
        }
        finish();
    }

    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        if(menuItem.getId() == R.id.xi_title_bar_home){
            onGoBack();
        }
    }

    /**
     * 设置TitleBar的背景颜色
     *
     * @param color 颜色
     */
    public void setTitleBarBackground(int color) {
        if (getTitleBar() != null) {
            getTitleBar().setBackgroundColor(color);
           // getTitleBar().setShadowColor(color);
        }
    }

    /**
     * 设置TitleBar 阴影颜色
     *
     * @param color 颜色
     */
    public void setShadowColor(int color) {
        if (getTitleBar() != null) {
            //getTitleBar().setShadowColor(color);
        }
    }

    public int getColor(@ColorRes int colorRes) {
        return getResources().getColor(colorRes);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);//ButterKnife
        // 获取刷新布局
        View tmpView=findViewById(R.id.xi_swipe_pull_to_refresh);
        if(null!=tmpView&&(tmpView instanceof SwipeRefreshLayout)){
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.xi_swipe_pull_to_refresh);//view,
        }
        // 获取首次加载布局
        mStatusLayout = (CommloadingView) findViewById(R.id.xi_layout_loading);
        mStatusLayout.setOnRtyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        if(mSwipeRefreshLayout!=null)  mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    /**
     * 根据比例计算大小
     *
     * @param w 宽比例
     * @param h 高比例
     * @return 返回结果
     */
    protected int generateStatusSpaceRatio(float w, float h) {
        int height = (int) (DisplayUtil.getScreenWidth() / w * h);
        generateStatusSpace(height);
        return height;
    }

    /**
     * 设置占位View高度
     *
     * @param height 高度
     */
    protected void generateStatusSpace(int height) {
      /*  if (mSpaceView != null) {
            mSpaceView.getLayoutParams().height = height;
            mSpaceView.requestLayout();
        }*/
    }




    /**
     * 显示无网络layout   //1 业务出错，2 http出错   3 网络出错
     */
    protected void showNetworkPrompt(int type) {

        if(type==3) {
            if (mStatusLayout != null && !mStatusLayout.isShownStatus(CommloadingView.StatusMode.network)) {
                mStatusLayout.showNetworkTip();
            }
        }else {
            if (mStatusLayout != null && !mStatusLayout.isShownStatus(CommloadingView.StatusMode.serverError)) {
                mStatusLayout.showServerError();
            }
        }

    }

    /**
     * 显示首次加载layout
     */
    protected void showFirstLoading() {

        if (mStatusLayout != null && !mStatusLayout.isShownStatus(CommloadingView.StatusMode.loading)) {
            mStatusLayout.showLoadingStatus();
        }
    }

    /**
     * 空状态显示
     */
    protected void showEmptyLayout() {
         if (mStatusLayout != null && !mStatusLayout.isShownStatus(CommloadingView.StatusMode.empty)) {
             mStatusLayout.showEmptyStatus();
         }
    }

    /**
     * 隐藏空状态
     */
    public void hideEmptyLayout(){
        if (mStatusLayout != null ) {//&& mStatusLayout.isShown()去掉判断，父不可见时，此时会导致让当前不可见
            mStatusLayout.hide();
        }
    }

    /**
     * 获取一个网络访问回调
     */
    protected RetrofitCallback<RESPONSE> getCallback() {
        return new RetrofitCallbackWrapper<RESPONSE>(this);
    }

/*    protected RetrofitCallback2<RESPONSE> getCallback2() {
        return new RetrofitCallbackWrapper2<RESPONSE>(this);
    }*/

    public void onSubscriberStart(){}


    /**
     * 数据获取错误,网络连接错误
     */
    @Override
    public void onError(String throwable,int type) {
         if(isFragmentFinished())  return;
         onRefreshCompleted();
         showNetworkPrompt(type);
    }

    /**
     * 数据加载成功
     */
    @Override
    public void onSuccess(RESPONSE response)   {
        if(isFragmentFinished())  return;
        onRefreshCompleted();

      /*   if (mStatusLayout != null && mStatusLayout.isShown()) {
            mStatusLayout.hide();
        }*/
        hideEmptyLayout();

    }


    @Override
    public void onRefresh() {  }

    public boolean isRefreshing(){

        return mSwipeRefreshLayout!=null?mSwipeRefreshLayout.isRefreshing():false;
    }


    protected void onRefreshCompleted() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
