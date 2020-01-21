package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DBitmapUtil;
import com.huatu.handheld_huatu.utils.DisplayUtil;


public class OcTextView extends android.support.v7.widget.AppCompatTextView {

    private Context mContext;
    private boolean isOpen;
    private OnCusViewClickListener l;
    private int type;

    public interface OnCusViewClickListener {
        void isOpen(boolean isOpen);
    }

    public OcTextView(Context context) {
        super(context);
        initView(context);
    }

    public OcTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OcTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        setCompoundDrawablePadding(DisplayUtil.dp2px(4));
        isOpen = false;
        setCompoundDrawablesCus(isOpen);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    isOpen = false;
                    setCompoundDrawablesCus(isOpen);
                } else {
                    isOpen = true;
                    setCompoundDrawablesCus(isOpen);
                }
                if (l != null) {
                    l.isOpen(isOpen);
                }
            }
        });
    }

    private void setCompoundDrawablesCus(boolean isOpen) {
        if (!isOpen) {
//            setCompoundDrawables(left,null,DBitmapUtil.getDrawable(mContext, R.mipmap.homef_title_pop_down1),null);
            setCompoundDrawables(null, null, DBitmapUtil.getDrawable(mContext, R.mipmap.homef_title_pop_down1), null);
        } else {
//            setCompoundDrawables(left,null,DBitmapUtil.getDrawable(mContext, R.mipmap.homef_title_pop_up1),null);
            setCompoundDrawables(null, null, DBitmapUtil.getDrawable(mContext, R.mipmap.homef_title_pop_up1), null);
        }
    }

    Drawable left;

    public void refreshView(int type_) {
        type = type_;
//        if(type_==0) {
//            left = DBitmapUtil.getDrawable(mContext, R.drawable.fenlei);
        setCompoundDrawablesCus(isOpen);
//        }else  if(type_==1) {
//            setPadding(DisplayUtil.dp2px(5),DisplayUtil.dp2px(5),DisplayUtil.dp2px(5),DisplayUtil.dp2px(5));
//        }
    }

    public void setOnCusiewClickListener(OnCusViewClickListener l) {
        this.l = l;
    }

    public void setOpen() {
        isOpen = true;
        setCompoundDrawablesCus(isOpen);
    }
}
