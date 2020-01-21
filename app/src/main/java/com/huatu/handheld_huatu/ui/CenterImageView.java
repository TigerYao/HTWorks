package com.huatu.handheld_huatu.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huatu.utils.DensityUtils;

/**
 * Created by cjx on 2019\2\21 0021.
 */


public class CenterImageView extends AppCompatImageView {
    private Paint paint,mTextPaint;
    private boolean isCenterImgShow;
    private Bitmap mBitmap;
    private Bitmap mBottomBitmap;
    private String mRankText="";
    private int mStrokeColor=0xFFF4AC1A;

    public CenterImageView setRankText(String rankText){
        mRankText=rankText;
        return this;
    }

    public CenterImageView setStrokeColor(@ColorInt int color ){
        mStrokeColor=color;
        return this;
    }

    public void setCenterImgShow(boolean centerImgShow,Bitmap bitmap,Bitmap bottomBitmap) {
        isCenterImgShow = centerImgShow;
        mBitmap=bitmap;

        mBottomBitmap=bottomBitmap;
        if (isCenterImgShow&&(null!=mBitmap)) {

          //  bitmap = BitmapFactory.decodeResource(getResources(), drawableId);
            invalidate();
        }
    }
    public CenterImageView(Context context) {
        super(context);
        init(context);
    }
    public CenterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public CenterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * Text的基线
     */
    protected float mTextBaseLine;
    protected float mHollowCircleStroke;//空心圆粗细
    private void init(Context context) {
        //paint = new Paint();
        mHollowCircleStroke = DensityUtils.dp2px(context, 1) ;
        paint = new Paint();
        paint.setColor(0xFFF4AC1A);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(mHollowCircleStroke);


        mTextPaint=getPaint(0xFF8B572A, DensityUtils.sp2px(context,6));

        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        mTextBaseLine = -DensityUtils.dp2px(context,5) - metrics.descent + (metrics.bottom - metrics.top) / 2;

       // getMeasuredHeight()-DensityUtils.dp2px(getContext(),10)+DensityUtils.dp2px(context,5) - metrics.descent + (metrics.bottom - metrics.top) / 2;
    }

    private Paint getPaint(int paintColor, float paintSize) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setTextSize(paintSize);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isCenterImgShow){
            float mSelectCircleRadius=(getMeasuredWidth()-getPaddingLeft()-getPaddingRight())/2;
            float cy=(getMeasuredHeight()-getPaddingTop()-getPaddingBottom())/2;
            //初始化Path
            Path path = new Path();
            //Path.Direction.CW顺时针绘制圆 Path.Direction.CCW逆时针绘制圆
            path.addCircle( getMeasuredWidth()/2,cy+getPaddingTop(), mSelectCircleRadius - mHollowCircleStroke/2, Path.Direction.CW);

            paint.setColor(mStrokeColor);
            canvas.drawPath(path, paint);
        }

        if (isCenterImgShow && mBitmap != null) {
            canvas.drawBitmap(mBitmap, getMeasuredWidth()  - mBitmap.getWidth() , 0, paint);
        }

        if (isCenterImgShow && mBottomBitmap != null) {
            canvas.drawBitmap(mBottomBitmap, getMeasuredWidth() / 2 - mBottomBitmap.getWidth() / 2 ,  getMeasuredHeight()  - mBottomBitmap.getHeight() , paint);
            //canvas.drawBitmap(mBottomBitmap, getMeasuredWidth()- DensityUtils.dp2px() / 2 - mBottomBitmap.getWidth() / 2 ,  getMeasuredHeight()  - mBottomBitmap.getHeight() , paint);
        }

        if(!TextUtils.isEmpty(mRankText)){
           canvas.drawText(mRankText,  getMeasuredWidth() / 2, getMeasuredHeight()+mTextBaseLine,mTextPaint);
        }

    }
}