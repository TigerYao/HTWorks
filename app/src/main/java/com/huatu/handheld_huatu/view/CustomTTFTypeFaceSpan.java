package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;

public class CustomTTFTypeFaceSpan extends TypefaceSpan {
    private Context ctx;
    public CustomTTFTypeFaceSpan(String family, Context ctx) {
        super(family);
        this.ctx = ctx;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (TextUtils.isEmpty(getFamily()))
            super.updateDrawState(ds);
        else
            apply(ds, getFamily());
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        if (TextUtils.isEmpty(getFamily()))
            super.updateDrawState(paint);
        else
           apply(paint, getFamily());
    }

    private void apply(Paint paint, String family) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), family);
        int fake = oldStyle & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}