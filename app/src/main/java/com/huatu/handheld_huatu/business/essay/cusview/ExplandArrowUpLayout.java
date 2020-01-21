package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huatu.handheld_huatu.R;

/**
 * Created by Administrator on 2019\8\9 0009.
 */

public class ExplandArrowUpLayout extends ExplandArrowLayout {

    public ExplandArrowUpLayout(Context context) {
        this(context, null);
    }

    public ExplandArrowUpLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExplandArrowUpLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
     }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mExplandView.setTag(R.id.reuse_tag2, "0");//1展开
        mExplandView.setImageResource( R.mipmap.homef_title_pop_down1);
    }
}
