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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.base.ReuseActivity;
import com.huatu.handheld_huatu.base.inter.FragmentKeyEvent;
import com.huatu.handheld_huatu.base.inter.IcsTitlebar;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 所有Fragment的基类,对Fragment的二次封装
 * <p/>
 * author : Soulwolf Create by 2015/6/8 11:01
 * email  : ToakerQin@gmail.com.
 */
public abstract class AbsFragment extends Fragment implements FragmentKeyEvent,
        IcsTitlebar, TitleBar.TileBerMenuInflate, TitleBar.OnTitleBarMenuClickListener {

    static final String SAVE_STATE = "fragment_save_state";
    private ViewGroup rootView;// 根视图
    public static final int RESULT_CANCEL = 403;
    private Unbinder mUnbinder;
    /**
     * TitleBar的点击事件实现方法
     *
     * @param titleBar TitleBar对象
     * @param menuItem 被点击的菜单项
     */
    @Override
    public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {

    }

    /**
     * 为Titlebar 添加按钮
     *
     * @param titleBar  TitleBar对象
     * @param container menu集合
     */
    @Override
    public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container) {

    }

    /**
     * 手机按键监听回调
     *
     * @param keyCode 键值
     * @param event   事件对象
     * @return 是否处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    /**
     * 返回键的按下
     */
    @Override
    public void onBackPressed() {

    }


    /**
     * 自定义TitleBar
     *
     * @param inflater  布局加载器
     * @param container rootView
     * @return 是否使用自定义TitleBar
     */
    @Override
    public boolean attachTitleBar(LayoutInflater inflater, ViewGroup container) {
        return false;
    }


    public Context getContext() {
        if (getActivity() == null) {
            return UniApplicationContext.getContext();
        }
        return getActivity();
    }

    public Fragment getFragment() {

        return AbsFragment.this;
    }

    public boolean isFragmentFinished() {
        return (this.getActivity() == null || getActivity().isFinishing() || AbsFragment.this.isDetached());
    }


    /**
     * 保存状态
     */
    protected void onSaveState(Bundle outState) {

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle bundle = new Bundle();
        onSaveState(bundle);
        outState.putBundle(SAVE_STATE, bundle);
    }

    /**
     * Restore Fragment's State here
     */
    protected void onRestoreState(Bundle savedInstanceState) {

    }

    public void finish() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    abstract protected int getContentView();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            //XLog.e("absFragment_onCreateView","onCreateView_savedInstanceState not null");
            onRestoreState(savedInstanceState.getBundle(SAVE_STATE));
        }
        if (getContentView() > 0) {
            if (rootView != null) {
                ViewGroup parent = (ViewGroup) rootView.getParent();
                if (parent != null)
                    parent.removeView(rootView);
            } else {
                rootView = (ViewGroup) inflater.inflate(getContentView(), null);
                rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                layoutInit(inflater, savedInstanceState);
            }
            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 子类重写这个方法，初始化视图
     *
     * @param inflater
     * @param savedInstanceSate
     */
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife.inject(this, view);
        mUnbinder= ButterKnife.bind(this, rootView);
        onCreateTitleBarMenu(getTitleBar(), getTitleBar() == null ? null : getTitleBar().getMenuItemContainer());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mUnbinder!=null) mUnbinder.unbind();
        //ButterKnife.reset(this) ;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if (savedInstanceState == null)
            requestData();
    }

    /**
     * 初次创建时默认会调用一次
     */
    public void requestData() {
    }

    /**
     * 获取Fragment跳转的时候参数实体
     *
     * @return 参数实体
     */
    public FragmentParameter getFragmentParameter() {
        if (getReuseActivity() != null) {
            return getReuseActivity().getFragmentParameter();
        }
        return null;
    }

    /**
     * 获取Fragment 的 根View
     *
     * @return 根View
     */
    public ViewGroup getContainerView() {
        if (getView() != null && getView() instanceof ViewGroup) {
            return (ViewGroup) getView();
        }
        return null;
    }

    /**
     * 获取TitleBar对象
     *
     * @return TitleBar对象
     */
    public TitleBar getTitleBar() {
        if (getReuseActivity() != null) {
            return getReuseActivity().getTitleBar();
        }
        return null;
    }

    /**
     * 获取复用的Activity
     *
     * @return 复用的Activity
     */
    public ReuseActivity getReuseActivity() {
        if (getActivity() != null && getActivity() instanceof ReuseActivity) {
            return (ReuseActivity) getActivity();
        }
        return null;
    }

    /**
     * 设置标题
     *
     * @param resId String id
     */
    public void setTitle(int resId) {
        if (getReuseActivity() != null) {
            getReuseActivity().setTitle(resId);
        }
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(CharSequence title) {
        if (getReuseActivity() != null) {
            getReuseActivity().setTitle(title);
        }
    }

    /**
     * 设置标题颜色
     * @param resId
     */
    public void setTitleColor(int resId){
        if (getReuseActivity() != null) {
            getReuseActivity().setTitleTextColor(getResources().getColor(resId));
        }
    }

    /**
     * 设置头像标题
     * create on Baron
     *
     * @param text
     * @param imageId
     */
    public void setTitle(CharSequence text, int imageId) {
        if (getReuseActivity() != null) {
            getReuseActivity().setTitle(text, imageId);
        }
    }

    /**
     * 设置标题的背景颜色
     * create on Baron
     */
    public void setTitleBackground(int color) {
        if (getReuseActivity() != null) {
            getReuseActivity().setTitleBackground(color);
        }
    }


    /**
     * 设置返回结果
     *
     * @param resultCode 返回码
     * @param data       返回参数
     */
    public void setResult(int resultCode, Intent data) {
        if (getFragmentParameter() != null) {
            getFragmentParameter().setResultCode(resultCode);
            getFragmentParameter().setResultParams(data);
        }
    }

    /**
     * 获取根视图
     * @return
     */
    public ViewGroup getRootView(){
        return rootView;
    }

     @Override
    public void onResume() {
        super.onResume();
        // 友盟页面统计
         MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        // 友盟页面统计

        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }


    /**
     * 设置返回结果
     *
     * @param resultCode 结果码
     */
    public void setResult(int resultCode) {
        setResult(resultCode, null);
    }

    /**
     * 是否包含返回值
     *
     * @return 是否包含返回值
     */
    public boolean hasResult() {
        if (getFragmentParameter() != null
                && getFragmentParameter().getResultCode() != FragmentParameter.NO_RESULT_CODE) {
            return true;
        }
        return false;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return (T) getContainerView().findViewById(id);
    }

    public void setText(@IdRes int viewId, CharSequence text) {
        setText(getContainerView(), viewId, text);
    }

    public void setText(@IdRes int viewId, @StringRes int resId) {
        setText(getContainerView(), viewId, resId);
    }


    public static void setText(View ConvertView, @IdRes int viewId, CharSequence text) {
        TextView view = getView(ConvertView, viewId);
        if (view != null) {
            view.setText(text != null ? text : "");
        }
    }

    public static void setText(View ConvertView, @IdRes int viewId, @StringRes int resId) {
        TextView view = getView(ConvertView, viewId);
        if (view != null) {
            view.setText(resId);
        }
    }

    public static <T extends View> T getView(View ConvertView, int viewId) {
        View view = ConvertView.findViewById(viewId);
        if (view == null) return null;
        return (T) view;
    }


}
