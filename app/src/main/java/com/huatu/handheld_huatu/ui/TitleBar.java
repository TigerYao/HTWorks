/**
 * <pre>
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for xiaodaow3.0-branch
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
package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.viewpagerindicator.BoldTextView;


/**
 * 自定义 TitleBar
 * <p>
 * author: Soulwolf Created on 2015/8/23 0:47.
 * email : Ching.Soulwolf@gmail.com
 */
public class TitleBar extends FrameLayout implements View.OnClickListener {

    MenuItem mHomeMenuItem;
    BoldTextView mTitleView;
    ImageView mTitleImage;
    LinearLayout mMenuItemContainer;
     View mShadowView;

    //顶部title
    RelativeLayout mTitleContainer;

    private boolean mHomeAsUpEnabled;

    private int mDefaultBackground;

    private OnTitleBarMenuClickListener mOnTitleBarMenuClickListener;
    private int mHomeAsUpIndicator = R.drawable.icon_arrow_left;
    private String mTitleText;


    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.titlebar_layout, this);

        mDefaultBackground = getResources().getColor(R.color.white);

        mTitleContainer = (RelativeLayout) findViewById(R.id.xi_title_container);

        mMenuItemContainer = (LinearLayout) findViewById(R.id.xi_title_bar_menu_container);
        mTitleView = (BoldTextView) findViewById(R.id.xi_toolbar_center_title);
        mTitleView.setBold(true);
        mTitleImage = (ImageView) findViewById(R.id.xi_toolbar_center_image);
        mHomeMenuItem = (MenuItem) findViewById(R.id.xi_title_bar_home);
        mHomeMenuItem.setPadding(0, 0, 100, 0);
        mShadowView = findViewById(R.id.xi_title_bar_shadow);
        mHomeMenuItem.setOnClickListener(this);
        setBackgroundColor(mDefaultBackground);

        setTitle(mTitleText);
    }

    /**
     * 设置是否显示 返回键(默认图标返回键)
     *
     * @param enabled 是否显示
     */
    public void setDisplayHomeAsUpEnabled(boolean enabled) {
        setDisplayHomeIconAsUpEnabled(true);
        setHomeAsUpIndicator(mHomeAsUpIndicator);
    }

    /**
     * 设置是否显示 返回键
     *
     * @param enabled 是否显示
     * @param icon    左侧图标
     */
    public void setDisplayHomeAsUpEnabled(boolean enabled, @DrawableRes int icon) {
        setHomeAsUpIndicator(icon);
        setDisplayHomeIconAsUpEnabled(enabled);
    }

    /**
     * 设置是否显示 返回键
     *
     * @param enabled 是否显示
     * @param res     左侧文字
     * @param color   左侧文字颜色
     */
    public void setDisplayHomeAsUpEnabled(boolean enabled, @StringRes int res, @ColorRes int color) {
        setHomeAsUpText(res, color);
        setDisplayHomeIconAsUpEnabled(enabled);
    }

    private void setDisplayHomeIconAsUpEnabled(boolean enabled) {
        mHomeAsUpEnabled = enabled;
        if (mHomeAsUpEnabled) {
            if (!mHomeMenuItem.isShown()) {
                mHomeMenuItem.setVisibility(VISIBLE);
            }
        } else {
            if (mHomeMenuItem.isShown()) {
                mHomeMenuItem.setVisibility(GONE);
            }
        }
        mHomeMenuItem.setVisibility(mHomeAsUpEnabled ? VISIBLE : GONE);
    }

    /**
     * 设置左边按钮图标
     */
    public void setHomeAsUpIndicator(@DrawableRes int icon) {
        mHomeAsUpIndicator = icon;
        int distance=DensityUtils.dp2px(getContext(),10);
        mHomeMenuItem.mIconView.setPadding(distance,0,distance,0);
        mHomeMenuItem.setIcon(icon);
    }

    /**
     * 设置左侧按钮的文字和颜色
     */
    public void setHomeAsUpText(@StringRes int str, @ColorRes int color) {
        mHomeMenuItem.setText(str, color);
    }


    public LinearLayout getMenuItemContainer() {
        return mMenuItemContainer;
    }

    public void setTitle(CharSequence text) {
        mTitleText = StringUtils.valueOf(text);
        mTitleView.setText(mTitleText);
    }

    public void setTitle(@StringRes int text) {
        mTitleText = getResources().getString(text);
        mTitleView.setText(text);
    }

    public void setTitleTextColor(int color) {
        mTitleView.setTextColor(color);
    }

    //create Baron 添加到icon的Title
    public void setTitle(CharSequence text, int id) {
        mTitleText = StringUtils.valueOf(text);
        mTitleView.setText(mTitleText);
        if (mTitleImage.getVisibility() != VISIBLE) {
            mTitleImage.setVisibility(VISIBLE);
            mTitleImage.setImageResource(id);
        }
    }

    public TitleBar add(MenuItem item) {
        return add(item, item.getId());
    }

    public TitleBar add(MenuItem item, @IdRes int id) {
        item.setId(id);
        mMenuItemContainer.removeView(item);
        mMenuItemContainer.addView(item, FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
        item.setOnClickListener(this);
        return this;
    }

    public TitleBar add(CharSequence text) {
        MenuItem item = new MenuItem(getContext());
        item.setText(text);
        return add(item);
    }

    public TitleBar add(@StringRes int text) {
        MenuItem item = new MenuItem(getContext());
        item.setText(text);
        return add(item);
    }

    public TitleBar add(CharSequence text, @IdRes int id) {
        MenuItem item = findMenuItem(id);
        if (item == null) {
            item = new MenuItem(getContext());
            item.setText(text);
            return add(item, id);
        } else {
            item.setText(text);
            return this;
        }
    }

    public TitleBar add(@StringRes int text, @IdRes int id) {
        MenuItem item = findMenuItem(id);
        if (item == null) {
            item = new MenuItem(getContext());
            item.setText(text);
            return add(item, id);
        } else {
            item.setText(text);
            return this;
        }

    }

    public TitleBar add(String text, int color, @IdRes int id) {
        MenuItem item = findMenuItem(id);
        if (item == null) {
            item = new MenuItem(getContext());
            item.setText(text, color);
            return add(item, id);
        } else {
            item.setText(text, color);
            return this;
        }
    }

    public TitleBar removeMenuItem(MenuItem item) {
        mMenuItemContainer.removeView(item);
        return this;
    }

    public TitleBar add(Drawable icon) {
        MenuItem item = new MenuItem(getContext());
        item.setIcon(icon);
        return add(item);
    }

    public TitleBar addIcon(@DrawableRes int icon) {
        MenuItem item = new MenuItem(getContext());
        item.setIcon(icon);
        return add(item);
    }

    public TitleBar add(Drawable icon, @IdRes int id) {
        MenuItem item = new MenuItem(getContext());
        item.setIcon(icon);
        return add(item, id);
    }

    public TitleBar addIcon(@DrawableRes int icon, @IdRes int id) {
        MenuItem item = findMenuItem(id);
        if (item == null) {
            item = new MenuItem(getContext());
            item.setIcon(icon);
            return add(item, id);
        } else {
            item.setIcon(icon);
            return this;
        }
    }

    public void setIcon(@DrawableRes int icon, @IdRes int id) {
        MenuItem item = findMenuItem(id);
        if (item != null) {
            item.setIcon(icon);
        }
    }

    public void reset() {
        mHomeAsUpEnabled = false;
        mHomeMenuItem.setVisibility(GONE);
        setTitle(null);
        mMenuItemContainer.removeAllViews();
        setBackgroundColor(mDefaultBackground);
    }

    public MenuItem findMenuItem(@IdRes int id) {
        View view = findViewById(id);
        if (view instanceof MenuItem) {
            return (MenuItem) view;
        }
        return null;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
    }

  /*   public void setShadowColor(int color) {
        mShadowView.setBackgroundColor(color);
    }
*/

    //设置分割线状态
    public void setShadowVisibility(int visibility) {
         mShadowView.setVisibility(visibility);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
       /* int black = getResources().getColor(R.color.dark_general_black);
        int white = getResources().getColor(R.color.white);
        if (color == black) {
            mTitleView.setTextColor(white);
            mShadowView.setBackgroundColor(color);
            setHomeAsUpIndicator(R.drawable.fh_sy_icon);

        }*/
    }

    /**
     * 设置背景 (评论回复详情界面 仿今日头条头部消息)
     */
    public void setMainBackgroud(int drawableId) {
        mTitleContainer.setBackground(ResourceUtils.getDrawable(drawableId));
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof MenuItem) {
            MenuItem menuItem = (MenuItem) v;
            if (mOnTitleBarMenuClickListener != null) {
                mOnTitleBarMenuClickListener.onMenuClicked(this, menuItem);
            }
        }
    }

    public void setOnTitleBarMenuClickListener(OnTitleBarMenuClickListener listener) {
        this.mOnTitleBarMenuClickListener = listener;
    }

    public void setTitleVisibility(int visibility) {
        mTitleView.setVisibility(visibility);
    }

    public interface OnTitleBarMenuClickListener {

        public void onMenuClicked(TitleBar titleBar, MenuItem menuItem);
    }

    public interface TileBerMenuInflate {

        public void onCreateTitleBarMenu(TitleBar titleBar, ViewGroup container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState state = new SavedState(parcelable);
        state.mTitleText = mTitleText;
        state.mHomeAsUpEnabled = mHomeAsUpEnabled;
        state.mHomeIndicator = mHomeAsUpIndicator;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mTitleText = savedState.mTitleText;
        mHomeAsUpIndicator = savedState.mHomeIndicator;
        mHomeAsUpEnabled = savedState.mHomeAsUpEnabled;
    }

    static class SavedState extends View.BaseSavedState {

        boolean mHomeAsUpEnabled;
        int mHomeIndicator;
        String mTitleText;

        public SavedState(Parcel source) {
            super(source);
            this.mTitleText = source.readString();
            this.mHomeAsUpEnabled = source.readInt() == 1;
            this.mHomeIndicator = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(mTitleText);
            dest.writeInt(mHomeAsUpEnabled ? 1 : 0);
            dest.writeInt(mHomeIndicator);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
