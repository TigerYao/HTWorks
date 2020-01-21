package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Created by cjx on 2018\7\21 0021.
 */

public class FixedRatioBjPlayerView extends com.baijiahulian.player.BJPlayerView {

    private float mRatio = 1.0f;

    public FixedRatioBjPlayerView(Context var1) {
        this(var1, (AttributeSet)null);
    }

    public FixedRatioBjPlayerView(Context var1, AttributeSet var2) {
        this(var1, var2, 0);
    }

    public FixedRatioBjPlayerView(Context context, AttributeSet attrs, int var3){
        super(context,attrs,var3);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, com.gensee.rtmpresourcelib.R.styleable.FixedRatioImageView);
        if(null!=typedArray){
            mRatio = typedArray.getFloat(com.gensee.rtmpresourcelib.R.styleable.FixedRatioImageView_ratio, 1.0f);
            typedArray.recycle();
        }
    }
}
