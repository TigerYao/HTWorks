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

import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.gensee.rtmpresourcelib.R;


/**
 * author: Soulwolf Created on 2015/7/26 12:34.
 * email : Ching.Soulwolf@gmail.com
 */
public final class CalculateSizeDelegate {

    public static CalculateSizeDelegate obtain(MeasureDelegate measureDelegate, AttributeSet attrs) {
        return obtain(measureDelegate,attrs,0);
    }

    public static CalculateSizeDelegate obtain(MeasureDelegate measureDelegate, AttributeSet attrs, int defStyleAttr) {
        return obtain(measureDelegate,attrs,0,0);
    }

    public static CalculateSizeDelegate obtain(MeasureDelegate measureDelegate, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        return new CalculateSizeDelegate(measureDelegate,attrs,defStyleAttr,defStyleRes);
    }

    private final MeasureDelegate mMeasureDelegate;

    private DatumType mDatumType  ;

    private float     mWidthWeight                  = 0.0f;

    private float     mHeightWeight                 = 0.0f;

    private int mWidthMeasureSpec, mHeightMeasureSpec;

    private CalculateSizeDelegate(MeasureDelegate measureDelegate, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        this.mMeasureDelegate = measureDelegate;
        TypedArray typedArray = measureDelegate.getDelegateContext().obtainStyledAttributes(attrs, R.styleable.CMSViewSizeCalculate, defStyleAttr, 0);
        if(typedArray != null){
            int datum = typedArray.getInt(R.styleable.CMSViewSizeCalculate_cmsDatum, 0);
            if(datum == 1){
                mDatumType = DatumType.FROM_WIDTH;
            }else if(datum == 2){
                mDatumType = DatumType.FROM_HEIGHT;
            }
            mWidthWeight = typedArray.getFloat(R.styleable.CMSViewSizeCalculate_cmsWidthWeight,mWidthWeight);
            mHeightWeight = typedArray.getFloat(R.styleable.CMSViewSizeCalculate_cmsHeightWeight,mHeightWeight);
            typedArray.recycle();
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mWidthMeasureSpec = widthMeasureSpec;
        this.mHeightMeasureSpec = heightMeasureSpec;
        if(mDatumType != null && mWidthWeight != 0 && mHeightWeight != 0){
            mMeasureDelegate.setDelegateMeasuredDimension(View.getDefaultSize(0, mWidthMeasureSpec),
                    View.getDefaultSize(0, mHeightMeasureSpec));
            int measuredWidth = mMeasureDelegate.getNoumenon().getMeasuredWidth();
            int measuredHeight = mMeasureDelegate.getNoumenon().getMeasuredHeight();
            if(mDatumType == DatumType.FROM_WIDTH){
                measuredHeight = Math.round((measuredWidth / mWidthWeight * mHeightWeight));
            }else {
                measuredWidth = Math.round((measuredHeight / mHeightWeight * mWidthWeight));
            }
            mWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY);
            mHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY);
        }
    }

    public int getWidthMeasureSpec() {
        return mWidthMeasureSpec;
    }

    public int getHeightMeasureSpec() {
        return mHeightMeasureSpec;
    }
}
