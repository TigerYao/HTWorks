/**
 * <pre>
 * Copyright 2015 Soulwolf Ching
 * Copyright 2015 The Android Open Source Project for xiaodaow3.0-branche
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
package com.huatu.calculate;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * author: Soulwolf Created on 2015/7/26 13:02.
 * email : Ching.Soulwolf@gmail.com
 */
public class CalculateSizeFrameLayout extends FrameLayout implements MeasureDelegate{

    private CalculateSizeDelegate mCalculateSizeDelegate;

    public CalculateSizeFrameLayout(Context context) {
        super(context);
    }

    public CalculateSizeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCalculateSizeDelegate = CalculateSizeDelegate.obtain(this,attrs);
    }

    public CalculateSizeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCalculateSizeDelegate = CalculateSizeDelegate.obtain(this,attrs,defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalculateSizeFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mCalculateSizeDelegate = CalculateSizeDelegate.obtain(this,attrs,defStyleAttr,defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mCalculateSizeDelegate != null){
            mCalculateSizeDelegate.onMeasure(widthMeasureSpec,heightMeasureSpec);
            widthMeasureSpec = mCalculateSizeDelegate.getWidthMeasureSpec();
            heightMeasureSpec = mCalculateSizeDelegate.getHeightMeasureSpec();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public Context getDelegateContext() {
        return getContext();
    }

    @Override
    public void setDelegateMeasuredDimension(int measuredWidth, int measuredHeight) {
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public View getNoumenon() {
        return this;
    }
}
