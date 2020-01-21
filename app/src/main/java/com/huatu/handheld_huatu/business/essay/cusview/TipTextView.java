package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DBitmapUtil;
import com.huatu.handheld_huatu.utils.SpUtils;


public class TipTextView extends android.support.v7.widget.AppCompatTextView {

    private Context mContext;
    private String tag;

    public TipTextView(Context context) {
        super(context);
        initView(context);
    }

    public TipTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TipTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
    }
    public void setTag(final String tag) {
        this.tag = tag;
        if(SpUtils.getActivityTip(tag)){
            setVisibility(View.VISIBLE);
            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setVisibility(View.GONE);
                    SpUtils.setActivityTip(tag,false);
                }
            });
        }else {
            setVisibility(View.GONE);
        }
    }
}
