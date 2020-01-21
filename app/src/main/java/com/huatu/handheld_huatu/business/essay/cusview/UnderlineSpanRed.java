/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huatu.handheld_huatu.business.essay.cusview;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;

import java.lang.reflect.Method;

public class UnderlineSpanRed extends UnderlineSpan
         {


    @Override
    public void updateDrawState(TextPaint ds) {
        try {
            final Method method = TextPaint.class.getMethod("setUnderlineText",
                    Integer.TYPE,
                    Float.TYPE);
            method.invoke(ds, UniApplicationContext.getApplication().getResources().getColor(R.color.common_style_text_color), 3.0f);
        } catch (final Exception e) {
            ds.setUnderlineText(true);
            ds.setColor(UniApplicationContext.getApplication().getResources().getColor(R.color.common_style_text_color));
        }
    }
}
