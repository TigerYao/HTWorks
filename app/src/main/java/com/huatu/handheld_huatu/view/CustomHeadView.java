package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


import com.baijiayun.glide.request.transition.Transition;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.LogUtils;

/**
 * Created by saiyuan on 2016/10/27.
 */
public class CustomHeadView extends View {

    private final String TAG = "CustomHeadView";

    protected Context mContext;

    /**
     * 头像区域
     */
    protected Paint headPaint = new Paint();
    protected RectF headRecF;
    protected Bitmap headBitmap;
    protected Bitmap headDefaultBitmap;
    protected Paint edgingPaint = new Paint();

    protected int mWidth;
    protected int mHeight;
    protected int centerX;
    protected int centerY;
    protected int mRadius;
    protected boolean isDetached;
    protected String headUrl;
    private volatile boolean isUrlLoaded = false;
    private boolean isShaderInit = false;

    public CustomHeadView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public CustomHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public CustomHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        headPaint.setAntiAlias(true);
        if(headDefaultBitmap == null){
            headDefaultBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.image11);
        }
        edgingPaint.setStrokeWidth(2);
        edgingPaint.setColor(Color.parseColor("#b2b2f2"));
        edgingPaint.setStyle(Paint.Style.STROKE);
        edgingPaint.setAntiAlias(true);
    }

    public void setEdgingColor(int color) {
        edgingPaint.setColor(color);
    }

    private void setBitmapShader(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = mRadius * 2 * 1.0f / bitmap.getWidth();
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        int top = 0;
        int bottom = bitmap.getWidth();
        int left = 0;
        int right = bitmap.getWidth();
        if(bitmap.getHeight() > bitmap.getWidth()){
            top = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            bottom = bitmap.getHeight() - (bitmap.getHeight() - bitmap.getWidth()) / 2;
        }else if(bitmap.getWidth() > bitmap.getHeight()){
            left = (bitmap.getWidth() - bitmap.getHeight()) / 2;
            right = left + bitmap.getHeight();
            top = 0;
            bottom = bitmap.getHeight();
        }
        RectF bitmapRect = new RectF(left, top, right, bottom);
        matrix.setRectToRect(bitmapRect, headRecF, Matrix.ScaleToFit.FILL);
        bitmapShader.setLocalMatrix(matrix);
        headPaint.setShader(bitmapShader);
        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mWidth = widthSize;
        mHeight = heightSize;
        centerX = mWidth / 2;
        centerY = mHeight / 2;
        mRadius = Math.min(mWidth, mHeight) / 2;
//        LogUtils.i("mWidth:" + mWidth + ", mHeight:" + mHeight);
//        int w = getMeasuredWidth();
//        int h = getMeasuredHeight();
        if(mWidth > 0 && mHeight > 0) {
            if(!isShaderInit) {
                isShaderInit = true;
                headRecF = new RectF();
                headRecF.set(0, 0, mRadius * 2, mRadius * 2);
                setBitmapShader((headBitmap == null || headBitmap.isRecycled()) ? headDefaultBitmap : headBitmap);
            }
            if(!isUrlLoaded) {
                loadHeadUrl();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(headRecF == null){
            return;
        }
        canvas.drawCircle(headRecF.left + headRecF.width() / 2, headRecF.top + headRecF.height() / 2, mRadius, headPaint);
        if(mRadius > 0) {
            canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius - edgingPaint.getStrokeWidth() / 2, edgingPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isDetached = true;
        if(headBitmap != null && !headBitmap.isRecycled()) {
            headBitmap.recycle();
            headBitmap = null;
        }
        if(headDefaultBitmap != null && !headDefaultBitmap.isRecycled()) {
            headDefaultBitmap.recycle();
            headDefaultBitmap = null;
        }
        headPaint.setShader(null);
    }

    protected void loadHeadUrl() {
        isUrlLoaded = true;
        final String loadHeadUrl = headUrl;
        GlideApp.with(mContext).asBitmap().load(loadHeadUrl).into(new com.baijiayun.glide.request.target.SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if(isDetached) {
                    return;
                }
                if(resource != null && !resource.isRecycled() && loadHeadUrl != null && loadHeadUrl.equals(headUrl)) {
                    isUrlLoaded = true;
                    headBitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight()) ;
                    LogUtils.i("setBitmapShader: " + headUrl);
                    setBitmapShader(headBitmap);
                } else {
                    isUrlLoaded = false;
//                    LogUtils.i("setBitmapShader direct return");
                }
            }
        } );

      /*  new SimpleTarget<Bitmap>(mWidth, mHeight){
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if(isDetached) {
                    return;
                }
                if(resource != null && !resource.isRecycled() && loadHeadUrl != null && loadHeadUrl.equals(headUrl)) {
                    isUrlLoaded = true;
                    headBitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight()) ;
                    LogUtils.i("setBitmapShader: " + headUrl);
                    setBitmapShader(headBitmap);
                } else {
                    isUrlLoaded = false;
//                    LogUtils.i("setBitmapShader direct return");
                }
            }
        }*/
    }

    public void setHeadUrl(final String url){
        if(TextUtils.isEmpty(url)) {
            return;
        }
        headUrl = url;
        isUrlLoaded = false;
        if(mRadius > 0) {
            loadHeadUrl();
        } else {
            LogUtils.i("mRadius <= 0");
        }
    }
}
