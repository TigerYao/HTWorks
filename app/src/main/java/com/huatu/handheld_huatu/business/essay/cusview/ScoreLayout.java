package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.utils.DensityUtils;

/**
 * Created by Administrator on 2019\7\17 0017.
 */

public class ScoreLayout extends FrameLayout {

    public ScoreLayout(Context context) {
        this(context, null);
    }

    public ScoreLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    TextView mScoreTextView,mTimeTextView;
    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        mScoreTextView=(TextView)getChildAt(1);
        mTimeTextView=(TextView)getChildAt(2);
    }

    public void setScoreTxt(CheckDetailBean singleBean,double totalScore){

        mScoreTextView.setText(getScoreSpan(singleBean.totalExamScore,totalScore));
        mTimeTextView.setText("用时:"+TimeUtils.getSecond2MinTime(singleBean.totalSpendTime) + " ");
    }

    public static  SpannableString getScoreSpan(double curExamScore,double allTotalScore){

        int fontsize= ResourceUtils.getDimensionPixelSize(R.dimen.check_15sp);
        return getScoreSpan(curExamScore,allTotalScore,fontsize);
    }

    public static  SpannableString getScoreSpan(double curExamScore,double allTotalScore,int addFontSize){

        String curScoreStr= CommonUtils.formatScore(curExamScore);
        String totalScoreStr=CommonUtils.formatScore(allTotalScore);
        String content=curScoreStr+  " /"+totalScoreStr+" 分";

        SpannableString msp = new SpannableString(content);
        msp.setSpan(new ForegroundColorSpan(0XFFFF6D73), 0, curScoreStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        msp.setSpan(new AbsoluteSizeSpan(addFontSize), 0, curScoreStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, content.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体
        return msp;
    }

    public static  SpannableString getScoreSpan(String prefix , String suffix,double curExamScore,double allTotalScore,int addFontSize,int foreColor){

        String curScoreStr= CommonUtils.formatScore(curExamScore);
        String totalScoreStr=CommonUtils.formatScore(allTotalScore);
        String content=prefix+curScoreStr+  " /"+totalScoreStr+" "+suffix;

        int startIndex=prefix.length();
        SpannableString msp = new SpannableString(content);

        msp.setSpan(new ForegroundColorSpan(foreColor), startIndex,startIndex+ curScoreStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        msp.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(UniApplicationContext.getContext(),addFontSize)), startIndex,startIndex+ curScoreStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 0, content.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体
        return msp;
    }

    public static  SpannableString getUseTimeSpan(int useTime ,int inputWordNum){

        String userTimeStr=TimeUtils.getSecond2MinTime(useTime);
        String WordNumStr=String.valueOf(inputWordNum)+" 字";
        String content="字数:"+ WordNumStr+ "   用时:"+userTimeStr;


        SpannableString msp = new SpannableString(content);
        int start=content.indexOf(WordNumStr);
        msp.setSpan(new ForegroundColorSpan(0XFF9B9B9B), start, start+WordNumStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        msp.setSpan(new RelativeSizeSpan(13f/14), start,start+WordNumStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), start, start+WordNumStr.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体

        int timeStart=content.indexOf(userTimeStr);
        msp.setSpan(new ForegroundColorSpan(0XFF9B9B9B), timeStart, timeStart+userTimeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置前景色为洋红色
        msp.setSpan(new RelativeSizeSpan(13f/14), timeStart,timeStart+userTimeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), timeStart, timeStart+userTimeStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //斜体

        return msp;
    }
}
