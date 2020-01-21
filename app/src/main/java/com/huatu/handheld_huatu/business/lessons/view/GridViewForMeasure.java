package com.huatu.handheld_huatu.business.lessons.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author zhaodongdong.
 */

public class GridViewForMeasure extends GridView {
    public GridViewForMeasure(Context context) {
        super(context);
    }

    public GridViewForMeasure(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewForMeasure(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec + 50);
    }
}
