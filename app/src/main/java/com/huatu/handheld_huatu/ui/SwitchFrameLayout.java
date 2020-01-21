package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huatu.utils.StringUtils;
import com.huatu.viewpagerindicator.ArrowlineView;

/**
 * Created by Administrator on 2017/1/6.
 */
public class SwitchFrameLayout extends FrameLayout implements View.OnClickListener {

    public interface OnTabChangeListener{
        void onTabChange(int pos);
    }

    OnTabChangeListener mOnTabChangeListener;
    public void setOnTabChangeListener(OnTabChangeListener onTabChangeListener) {
        this.mOnTabChangeListener = onTabChangeListener;
    }

    public SwitchFrameLayout(Context context) {
        super(context);

    }

    public SwitchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    String mCurTag="0";
    @Override
    public void onClick(View v) {
    /*    if (mSeekBar.getVisibility() == View.GONE) {
            AnimUtils.translateShowView(mSeekBar);
        }
        if (null != v.getTag()) {
            if (null != monPopViewListener)
                monPopViewListener.onPopViewClick(StringUtils.parseInt(v.getTag().toString()));
        }*/
        if(mCurTag.equals(v.getTag())) return;
        mCurTag=v.getTag().toString();
        if("0".equals(v.getTag())){
            mInteractiveTxt.setSelected(false);
            mScheduleTxt.setSelected(true);
            mLineView.scroll(0,0);
        }else {
            mInteractiveTxt.setSelected(true);
            mScheduleTxt.setSelected(false);
            mLineView.scroll(1,0);
        }
        if(null!=mOnTabChangeListener) mOnTabChangeListener.onTabChange(StringUtils.parseInt(mCurTag));
    }


    public void switchTab(int pos){
        String tmpPos=String.valueOf(pos);
        if(mCurTag.equals(tmpPos)) return;
        mCurTag=tmpPos;
        if("0".equals(tmpPos)){
            mInteractiveTxt.setSelected(false);
            mScheduleTxt.setSelected(true);
            mLineView.scrollnoAnim(0,0);
        }else {
            mInteractiveTxt.setSelected(true);
            mScheduleTxt.setSelected(false);
            mLineView.scrollnoAnim(1,0);
        }

    }

    View mLoadingBar;
    TextView mScheduleTxt,mInteractiveTxt;
    ArrowlineView  mLineView;
    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        mScheduleTxt= (TextView)getChildAt(0);
        mScheduleTxt.setSelected(true);
        mScheduleTxt.setOnClickListener(this);
        mInteractiveTxt=(TextView)getChildAt(1);
        mInteractiveTxt.setOnClickListener(this);
        mLineView=(ArrowlineView)getChildAt(2);
    }


}
