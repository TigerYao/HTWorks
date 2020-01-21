package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.viewpagerindicator.ArrowlineView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



/**
 * Created by cjx on 2018\7\25 0025.
 */

public class DownBtnLayout extends FrameLayout {

    private   ProgressBar mProgressBar;
    private   ImageView  mDownFlagView;

    @IntDef({NORMAL, DOWNLOADING, FINISH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DownStatus{};

    public static final int NORMAL=0;
    public static final int DOWNLOADING=1;
    public static final int FINISH=2;
    public static final int PAUSE=3;
    public static final int ERROR=4;

    private int curStatus=NORMAL;

    public DownBtnLayout(Context context) {
        this(context, null);
    }

    public DownBtnLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownBtnLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        mProgressBar= (ProgressBar)getChildAt(0);
        mDownFlagView=(ImageView)getChildAt(1);
    }

    public void setStatus(@DownStatus int status){

         if(status==curStatus) return;
         if(status==NORMAL){
             mProgressBar.setVisibility(GONE);
             mDownFlagView.setImageResource(R.mipmap.course_start_down_icon);
         }
        else if(status==DOWNLOADING){
            mProgressBar.setVisibility(VISIBLE);
            mDownFlagView.setImageResource(R.mipmap.course_downing_icon);
        }
         else if(status==FINISH){
             mProgressBar.setVisibility(GONE);
             mDownFlagView.setImageResource(R.mipmap.course_file_del_icon);
         }
        curStatus=status;
    }

}
