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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadAssist;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.widget.MaterialLoadingDialog;

import rx.subscriptions.CompositeSubscription;


/**
 * Fragment基类
 *
 * author : Soulwolf Create by 2015/6/11 16:49
 * email  : ToakerQin@gmail.com.
 */
public abstract class AbsSettingFragment extends AbsFragment   {

    public static final int RESULT_CANCEL = 403;


    protected MaterialLoadingDialog mMaterialLoadingDialog;
    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            parserParams(getArguments());
        }

    }

    protected void parserParams(Bundle arg){

    }

    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
        super.onMenuClicked(titleBar, menuItem);
        if(menuItem.getId() == R.id.xi_title_bar_home){
            onGoBack();
        }
    }

    /**
     * 返回按钮的点击
     */
    protected void onGoBack(){
       // InputMethodUtils.hideMethod(getContext(), getContainerView());
        if(!hasResult()){
            setResult(RESULT_CANCEL);
        }
        finish();
    }

    /**
     * 设置是否显示返回按钮
     * @param enabled 返回按钮的状态
     * @param icon    返回按钮的图标
     */
    protected void setHomeAsUpEnabled(boolean enabled,@DrawableRes int icon){
        if(getTitleBar() != null){
            getTitleBar().setDisplayHomeAsUpEnabled(enabled, icon);
        }
    }

    /**
     * 设置是否显示返回按钮
     *
     * @param enabled 可用状态
     * @param str     按钮文字内容
     * @param color   按钮文字颜色
     */
    protected void setHomeAsUpEnabled(boolean enabled, @StringRes int str, @ColorRes int color) {
        if (getTitleBar() != null) {
            getTitleBar().setDisplayHomeAsUpEnabled(enabled, str, color);
        }
    }

    /**
     * 设置自定义返回按钮的事件操作
     * @param view 返回按钮
     */
    protected void setHomeAsUpView(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoBack();
            }
        });
    }

    /**
     * 设置自定义返回按钮的事件操作
     * @param container 自定义返回按钮的RootVew
     * @param id        返回按钮的Id
     */
    protected void setHomeAsUpView(View container,@IdRes int id){
        setHomeAsUpView(container.findViewById(id));
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
     * 设置TitleBar的背景颜色
     * @param color 颜色
     */
    public void setTitleBarBackground(int color){
        if(getTitleBar() != null){
            getTitleBar().setBackgroundColor(color);
            //getTitleBar().setShadowColor(color);
        }
    }

    //设置分割线状态
    public void setShadowVisibility(int visibility) {
        if(getTitleBar() != null){
            getTitleBar().setShadowVisibility(visibility);
            //getTitleBar().setShadowColor(color);
        }
    }

    /**
     * 设置TitleBar 阴影颜色
     * @param color 颜色
     */
    public void setShadowColor(int color){
        if(getTitleBar() != null){
           // getTitleBar().setShadowColor(color);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         // 创建ViewHolder快捷操作对象

        setListener();
    }



    /**
     * 设置点击事件
     */
    protected void setListener() {

    }
    /**
     * 显示加载弹窗
     */
    protected void showLoading(){
        if(null==mMaterialLoadingDialog){
            mMaterialLoadingDialog = MaterialLoadingDialog.newInstance(getContext());
        }
        if(!isDetached() && !mMaterialLoadingDialog.isShowing()){
            mMaterialLoadingDialog.show();
        }
    }

    /**
     * 显示加载弹窗,监听back按键
     */
    public void showLoading(final DialogInterface.OnKeyListener showLoadingCallback) {
        if(null==mMaterialLoadingDialog){
            mMaterialLoadingDialog = MaterialLoadingDialog.newInstance(getContext());
        }
        if (!isDetached() && !mMaterialLoadingDialog.isShowing()) {
            if (showLoadingCallback != null) {
                mMaterialLoadingDialog.setOnKeyListener(showLoadingCallback);
            }
            mMaterialLoadingDialog.show();
        }
    }


    protected void showLoading(String text){
        if(null==mMaterialLoadingDialog){
            mMaterialLoadingDialog = MaterialLoadingDialog.newInstance(getContext());
        }
        if(!isDetached() && !mMaterialLoadingDialog.isShowing()){
            mMaterialLoadingDialog.showTextWithLoading(text);
        }
    }

    /**
     * 隐藏加载弹窗
     */
    protected void hideLoading(){

        if(null==mMaterialLoadingDialog) return;
        if(!isDetached() && mMaterialLoadingDialog.isShowing()){
            mMaterialLoadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

}
