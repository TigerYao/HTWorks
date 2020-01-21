/**
 * <pre>
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for XDW-Android-Client
 *
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
package com.huatu.handheld_huatu.base;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.utils.Preconditions;


/**
 * ListView 的headerView 处理基类
 *
 * author: xing Created on 2016/5/ .
 * email :
 */
public abstract class BaseHeader {

    protected LayoutInflater mLayoutInflater;
    protected Context mContext;
    private AbsFragment mFragment;
    protected View mContentView;

/*
    public void instantiate(@NonNull Context context){
        onCreate(context);
    }*/

    public void instantiate(@NonNull AbsFragment fragment){
        mFragment = fragment;
        onCreate(mFragment.getActivity());
    }

    public void instantiate(@NonNull AbsFragment fragment,ViewGroup parent){
        mFragment = fragment;
        onCreate(mFragment.getActivity(),parent);
    }

    /** HeaderViewHelper 初始化 */
    public void onCreate(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        onCreateView(mLayoutInflater, null);
    }

    public void onCreate(Context context,ViewGroup parent){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        onCreateView(mLayoutInflater, parent);
    }

    protected final View onCreateView(LayoutInflater inflater, ViewGroup container){
        mContentView = onCreateView(inflater, container, false);
        onViewCreated(mContentView);
        return mContentView;
    }

    abstract public int inflateViewId();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, boolean attachToRoot) {
        return inflater.inflate( inflateViewId(), container, attachToRoot);
    }


    /** View初始化完毕 */
    protected void onViewCreated(View view){
        onPrepare();
        setListener();
    }

    protected void setListener(){

    }

    /** 初始化数据 */
    public void onPrepare(){

    }

    public void onLoadData(){

    }



    /** 绑定到 ListView */
    public void attachToParent(ListView listView){
        Preconditions.checkNotNull(listView);
        listView.addHeaderView(getContentView(), null, false);
    }
    /** 绑定到 ViewGroup */
    public void attachToParent(ViewGroup viewGroup){
        Preconditions.checkNotNull(viewGroup);
        viewGroup.addView(getContentView());
    }


    public final Context getContext() {
        return mContext;
    }
    public final AbsFragment getFragment() {
        return mFragment;
    }

    public View getContentView() {
        return mContentView;
    }

    /** 获取Activity */
    protected Activity getActivity(){
        if(mContext instanceof Activity){
            return (Activity) mContext;
        }
        return null;
    }



    public void setText(@IdRes int viewId, CharSequence text){
        AbsFragment.setText(mContentView, viewId, text);
    }


    protected <T extends View> T findViewById(@IdRes int id) {
        return (T)mContentView.findViewById(id);
    }

    public void setText(@IdRes int viewId, @StringRes int resId){
        AbsFragment.setText(mContentView,viewId,resId);

    }
    public void setVisibility(@IdRes int viewId, int visibility){
        View tmpView= mContentView.findViewById(viewId);
        if(null!=tmpView) tmpView.setVisibility(visibility);

    }

    public void setEnabled(@IdRes int viewId, boolean isEnable){
        View tmpView= mContentView.findViewById(viewId);
        if(null!=tmpView) tmpView.setEnabled(isEnable);

    }

    public void onDestroy(){
        mContentView = null;
        mLayoutInflater = null;
        mContext = null;
    }
}
