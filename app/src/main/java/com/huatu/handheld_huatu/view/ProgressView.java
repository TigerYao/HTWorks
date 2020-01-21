package com.huatu.handheld_huatu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.DisplayUtil;

/**
 * Created by ljzyuhenda on 16/7/16.
 */
public class ProgressView extends View {
    private Paint paint;
    private int strokeWidth;
    private float mPercent = 25;

    public ProgressView(Context context) {
        super(context);

        init();
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        if(isInEditMode()){
            strokeWidth = 35;
        }else{
            strokeWidth = (int) (DisplayUtil.getScreenWidth() * 0.0528);
        }

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int center = getWidth() / 2;
        int radius = center - strokeWidth / 2;

        paint.setColor(getResources().getColor(R.color.common_arena_gray_color));
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);
        canvas.drawArc(oval, 225 - 22.5f, 135, false, paint);

        paint.setColor(getResources().getColor(R.color.common_style_text_color));
        canvas.drawArc(oval, 225 - 22.5f, mPercent * 135 / 100, false, paint);

        Bitmap pointer = BitmapFactory.decodeResource(getResources(), R.drawable.icon_pointer);
        float widthOriginal = pointer.getWidth();
        float heightOriginal = pointer.getHeight();
        float percent = center / widthOriginal;
        float heightScaleAfter = heightOriginal * percent;

        Matrix matrix1 = new Matrix();
        matrix1.postTranslate(center - widthOriginal * 0.18f, center - heightScaleAfter / 2);

        float selfX = widthOriginal * 0.18f;
        float selfY = heightScaleAfter;
        matrix1.preRotate(225 - 22.5f + mPercent * 135 / 100);
        matrix1.preTranslate(-selfX, -selfY / 2);
        matrix1.postTranslate(selfX, selfY / 2);

        matrix1.preScale(percent, percent);

        canvas.drawBitmap(pointer, matrix1, null);
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }
}
