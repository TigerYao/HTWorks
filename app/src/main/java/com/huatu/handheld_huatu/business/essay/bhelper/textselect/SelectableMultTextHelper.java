package com.huatu.handheld_huatu.business.essay.bhelper.textselect;

import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.view.custom.ExercisePasteTextView;

/**
 * Created by cjx on 2019\7\18 0018.
 * 实现多个文本共用一个复制粘贴
 */

public   class SelectableMultTextHelper extends SelectableTextHelper  {


    public SelectableMultTextHelper(Builder builder) {
       super(builder);
    }

    @Override
    protected void init(){
        mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
        mTextView.setTag(R.id.reuse_tag2,"1");
         // 这里实现滑动的时候 隐藏光标和按钮窗，停止的时候显示。每次重绘都回调都延迟100ms，
        // 此方法在视图绘制前会被调用，测量结束，客户获取到一些数据。再计算一些动态宽高时可以使用。(调用一次后需要注销这个监听，否则会阻塞ui线程。)
        mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isHideWhenScroll) {
                    Log.i("TAG", "-onPreDraw-- " + isHideWhenScroll);
                    isHideWhenScroll = false;
                    // 延迟显示 光标和按钮窗
                    postShowSelectView(DEFAULT_SHOW_DURATION);
                }
                return true;
            }
        };
        mTextView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
        mTextView.addOnAttachStateChangeListener(this);
        // 滑动隐藏 光标和按钮窗
        mOnScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                doScrollChanged();
            }
        };
        mTextView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
       // mTextView.setOnClickListener(this);
        mOperateWindow = new OperateWindow(mTextView.getContext());
        ((ExercisePasteTextView)mTextView).setMultTextHelper(this);

    }

    public void setCurrentTextView(ExercisePasteTextView textView){
         if(mTextView!=textView){
            if(mTextView!=null){//老的文本
                mTextView.getViewTreeObserver().removeOnPreDrawListener(mOnPreDrawListener);

                mTextView.removeOnAttachStateChangeListener(this);
               // mTextView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
                mTextView.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
              //  mTextView.setOnClickListener(null);
                ((ExercisePasteTextView)mTextView).setMultTextHelper(null);
            }
            mTextView=textView;
            if(!"1".equals(mTextView.getTag(R.id.reuse_tag2))){
                mTextView.setText(mTextView.getText(), TextView.BufferType.SPANNABLE);
                mTextView.setTag(R.id.reuse_tag2,"1");
            }

             textView.setMultTextHelper(this);
            mTextView.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);
            mTextView.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener);
          //  mTextView.setOnClickListener(this);
         }
   }


}
