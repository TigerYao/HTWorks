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
package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 对ViewPager增强,实现左右滑动的拦截
 *
 * author : Soulwolf Create by 2015/6/12 13:45
 * email  : ToakerQin@gmail.com.
 */
public class ViewPagerWrapper extends  android.support.v4.view.ViewPager{

    private boolean mScrollable = true;

    public ViewPagerWrapper(Context context) {
        this(context,null);
    }

    public ViewPagerWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
       // super.setOffscreenPageLimit(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isScrollable() &&super.onInterceptTouchEvent(ev) ;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return  isScrollable()&&super.onTouchEvent(ev) ;
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.mScrollable = scrollable;
    }
}
