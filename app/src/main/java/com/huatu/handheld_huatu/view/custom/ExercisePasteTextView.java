package com.huatu.handheld_huatu.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableMultTextHelper;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019\7\18 0018.
 */

public class ExercisePasteTextView extends ExerciseTextView {

    protected int mTouchX;                                                // 记录触摸Text的位置
    protected int mTouchY;

    public int getTouchX(){
        return mTouchX;
    }

    public int getTouchY(){
        return mTouchY;
    }

    public interface  OnLongClickListener{
       void onLongClick(ExercisePasteTextView v,int x,int y);
    }
    OnLongClickListener mOnLongClickListener;
    public void setOnLongClickListener(OnLongClickListener longClickListener){
        mOnLongClickListener=longClickListener;
    }

    SelectableMultTextHelper mSelectableMultTextHelper;
    public void setMultTextHelper(SelectableMultTextHelper selectableMultTextHelper){
        mSelectableMultTextHelper=selectableMultTextHelper;
    }

    public ExercisePasteTextView(Context context) {
        super(context);
        init();
    }

    public ExercisePasteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExercisePasteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    int mBottomSpaceHeight=0;
    private void init(){
        mBottomSpaceHeight= DensityUtils.dp2px(getContext(),10);
        this.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                return false;
            }

        });

        this.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(null!=mOnLongClickListener){
                     mOnLongClickListener.onLongClick(ExercisePasteTextView.this,mTouchX,mTouchY);
                }
                return true;
            }
        });
        this.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if(null!=mSelectableMultTextHelper){
                    mSelectableMultTextHelper.doViewClick(mTouchX,mTouchY);
                }

            }
        });
    }

   /* @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int oldColor=getPaint().getColor();
        getPaint().setColor(0xffF9F9F9);


        canvas.drawRect(0,getHeight()-mBottomSpaceHeight,0,getHeight(),getPaint());
        getPaint().setColor(oldColor);

    }*/

    private ArrayList<Integer> mSelectList;
    public void setSelectList(ArrayList<Integer> selectList){
        mSelectList=selectList;
    }


    public void setHtmlSource(String source) {
        if (TextUtils.isEmpty(source)) {
            return;
        }

        if(ArrayUtils.isEmpty(mSelectList)){
            super.setHtmlSource(source);
            return;
        }
        this.htmlSource = source;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSpanned = Html.fromHtml(htmlSource, Html.FROM_HTML_MODE_COMPACT,
                    new GlideImageV4Getter(htmlSource, ExercisePasteTextView.this,getContext()), new ImgTagHandler(true));
        } else {
            mSpanned = Html.fromHtml(htmlSource,
                    new GlideImageV4Getter(htmlSource, ExercisePasteTextView.this,getContext()),
                    new ImgTagHandler(true));
        }

        if(!ArrayUtils.isEmpty(mSelectList)&&(mSpanned instanceof Spannable)){
            Spannable spannableStr=(Spannable)mSpanned;
            for(int i=0;i<mSelectList.size();){
                spannableStr.setSpan(new BackgroundColorSpan(ResourceUtils.getColor(R.color.text_sel_color)), mSelectList.get(i), mSelectList.get(i+1), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                i++;
                i++;
            }

        }
        setText(mSpanned);
        this.setMovementMethod(LinkMovementMethod.getInstance());
        if (type != 1) {
            setHighlightColor(getResources().getColor(android.R.color.transparent));//方法重新设置文字背景为透明色。
        }
    }

    public void setHtmlSource(float widthdp, String source) {
        if (TextUtils.isEmpty(source)) {
            return;
        }
        if(ArrayUtils.isEmpty(mSelectList)){
            super.setHtmlSource(source);
            return;
        }
        this.htmlSource = source;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSpanned = Html.fromHtml(htmlSource, Html.FROM_HTML_MODE_COMPACT,
                    new GlideImageV4Getter(widthdp, htmlSource, ExercisePasteTextView.this,getContext()), new ImgTagHandler(true));
        } else {
            mSpanned = Html.fromHtml(htmlSource,
                    new GlideImageV4Getter(widthdp, htmlSource, ExercisePasteTextView.this,getContext()), new ImgTagHandler(true));
        }


        if(!ArrayUtils.isEmpty(mSelectList)&&(mSpanned instanceof Spannable)){
             Spannable spannableStr=(Spannable)mSpanned;
            for(int i=0;i<mSelectList.size();){

                LogUtils.e("setHtmlSource",i+"");
                spannableStr.setSpan(new BackgroundColorSpan(ResourceUtils.getColor(R.color.text_sel_color)), mSelectList.get(i), mSelectList.get(i+1), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                i++;
                i++;
            }

        }
        ExercisePasteTextView.this.setText(mSpanned);
        this.setMovementMethod(LinkMovementMethod.getInstance());
        if (type != 1) {
            setHighlightColor(getResources().getColor(android.R.color.transparent));//方法重新设置文字背景为透明色。
        }
    }
}
