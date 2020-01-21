package com.huatu.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.huatu.utils.DensityUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;


/**
 * Created by Administrator on 2016/11/14.
 */
public class KitkatProfileLineLayout extends LinearLayout {

    public KitkatProfileLineLayout(Context context) {
        super(context);

        setInit();
    }

    public KitkatProfileLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setInit();
    }

    public KitkatProfileLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setInit();
    }

    private void setInit() {
        if (QMUIStatusBarHelper.supportTranslucent()){

            setPadding(getPaddingLeft(),
                    getPaddingTop()+ DensityUtils.getStatusHeight(getContext()),
                    getPaddingRight(),
                    getPaddingBottom() );

//            setBackgroundColor(Utils.resolveColor(getContext(), R.attr.colorPrimary, Color.BLACK));
        }
    }

}
