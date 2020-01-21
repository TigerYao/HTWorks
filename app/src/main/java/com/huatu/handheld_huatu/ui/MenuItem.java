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
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;


/**
 * TitleBar 的 右边MenuItem对象
 *
 * author: Soulwolf Created on 2015/8/23 0:56.
 * email : Ching.Soulwolf@gmail.com
 */
public class MenuItem extends FrameLayout {

    public TextView mTextView;

    public ImageView mIconView;

    public MenuItem(Context context) {
        this(context, null);
    }

    public MenuItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.titlebar_menu_item, this);
        mTextView = (TextView) findViewById(R.id.xi_menu_item_text);
        mIconView = (ImageView) findViewById(R.id.xi_menu_item_icon);
    }

    public void setText(CharSequence text){
        onStateChanged(false);
        mTextView.setText(text);
    }



    public void setText(CharSequence text, int color){
        onStateChanged(false);
        mTextView.setTextColor(color);
        mTextView.setText(text);
    }

    public void setText(@StringRes int  text){
        onStateChanged(false);
        mTextView.setText(text);
    }

    public void setText(@StringRes int text, @ColorRes int color){
        onStateChanged(false);
        mTextView.setTextColor(getResources().getColor(color));
        mTextView.setText(text);
    }


    public void setIcon(Drawable drawable){
        onStateChanged(true);
        mIconView.setImageDrawable(drawable);
    }

    public void setIcon(@DrawableRes int drawable){
        onStateChanged(true);
        mIconView.setImageResource(drawable);
    }

    private void onStateChanged(boolean isIcon){
        if(isIcon){
            if(mTextView.isShown()){
                mTextView.setVisibility(GONE);
            }
            if(!mIconView.isShown()){
                mIconView.setVisibility(VISIBLE);
            }
        }else {
            if(mIconView.isShown()){
                mIconView.setVisibility(GONE);
            }
            if(!mTextView.isShown()){
                mTextView.setVisibility(VISIBLE);
            }
        }
    }

    public void show(){
        if(!isShown()){
            setVisibility(VISIBLE);
        }
    }

    public void hide(){
        if(isShown()){
            setVisibility(GONE);
        }
    }
}
