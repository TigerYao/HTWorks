package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Administrator on 2019\6\25 0025.
 */

public class ExplandViewLayout  extends FrameLayout {

    private   ImageView  mExplandView;
    public ExplandViewLayout(Context context) {
        this(context, null);
    }

    public ExplandViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExplandViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        mExplandView=this.findViewWithTag("rotate_img");
    }

    private void init(){
        this.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


            }
        });
    }


}
