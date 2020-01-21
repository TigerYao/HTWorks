package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by Administrator on 2018\11\5 0005.
 */

public class MostGridView extends GridView {

    public MostGridView(Context context) {
        super(context);
    }

    public MostGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MostGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }


}
