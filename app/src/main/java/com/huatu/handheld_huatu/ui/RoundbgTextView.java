package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.huatu.utils.DensityUtils;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButtonDrawable;

/**
 * Created by cjx on 2018\8\10 0010.
 *
 * https://www.cnblogs.com/linux007/p/5798656.html
 */

public class RoundbgTextView extends AppCompatTextView {

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }

    public RoundbgTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public RoundbgTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        QMUIRoundButtonDrawable bg = QMUIRoundButtonDrawable.fromAttributeSet(context, attrs, defStyleAttr);
        QMUIViewHelper.setBackgroundKeepingPadding(this, bg);
    }


    public void setColor(@ColorInt int argb){
        if(null!=this.getBackground()){

            if(this.getBackground() instanceof GradientDrawable){
                ((GradientDrawable)this.getBackground()).setColor(argb);
            }
        }

    }

}
