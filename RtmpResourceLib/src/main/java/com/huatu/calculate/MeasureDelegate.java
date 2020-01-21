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

import android.content.Context;
import android.view.View;

/**
 * author: Soulwolf Created on 2015/7/26 12:48.
 * email : Ching.Soulwolf@gmail.com
 */
public interface MeasureDelegate {

    /**
     * Returns the context the view is running in, through which it can
     * access the current theme, resources, etc.
     *
     * @return The view's Context.
     */
    public Context getDelegateContext();

    /**
     * <p>This method must be called by {url #onMeasure(int, int)} to store the
     * measured width and measured height. Failing to do so will trigger an
     * exception at measurement time.</p>
     *
     * @param measuredWidth The measured width of this view.  May be a complex
     * bit mask as defined by {url #MEASURED_SIZE_MASK} and
     * {url #MEASURED_STATE_TOO_SMALL}.
     * @param measuredHeight The measured height of this view.  May be a complex
     * bit mask as defined by {url #MEASURED_SIZE_MASK} and
     * {url #MEASURED_STATE_TOO_SMALL}.
     */
    public void setDelegateMeasuredDimension(int measuredWidth, int measuredHeight);

    /**
     * Return proxy objects!
     * @return Return proxy objects!
     */
    public View getNoumenon() ;
}
