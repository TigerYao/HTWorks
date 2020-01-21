package com.huatu.Indicator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2018\10\24 0024.
 */

public class LineScaleWaveIndicator extends BaseExtendIndicatorController {

    public static final float SCALE = 1.0f;

/*    float[] scaleYFloats = new float[]{SCALE,
            SCALE,
            SCALE,
            SCALE};*/

    float[] scaleYFloats = new float[]{0.8f,1.0f,
            0.9f,
            0.7f};
    public int[] waveFloats;

    public LineScaleWaveIndicator(int[] waveFloats) {
        this.waveFloats = waveFloats;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float translateX = getWidth() / 14;
        float translateY = getHeight();
        for (int i = 0; i < 4; i++) {
            canvas.save();
            //平移
            int intRandom = (int) (1 + Math.random() * (10 - 1 + 1));
            float v = intRandom / 10f * getHeight();
            canvas.translate(((i + 1) * 2) * translateX + translateX * i, translateY);
            canvas.scale(SCALE, scaleYFloats[i]);
            RectF rectF = new RectF(-translateX, -getHeight(), 0, 0);
            canvas.drawRoundRect(rectF, 6, 6, paint);
            canvas.restore();
        }
    }

    @Override
    public List<Animator> createExAnimation() {
        List<Animator> animators = new ArrayList<>();
        long[] delays = new long[]{100, 200, 300, 400};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.4f, 1);
            scaleAnim.setDuration(1000);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleYFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();//刷新界面
                }
            });
            scaleAnim.start();
            animators.add(scaleAnim);
        }
        return animators;
    }


    @Override
    public  void createAnimation(){}

    @Override
    public  void stopAnimation(){
         setAnimationStatus(AnimStatus.END);
    }

}