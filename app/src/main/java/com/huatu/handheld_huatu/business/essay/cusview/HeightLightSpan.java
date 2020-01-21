package com.huatu.handheld_huatu.business.essay.cusview;


import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.style.ReplacementSpan;
import android.util.Log;

/***
 */
public class HeightLightSpan extends ReplacementSpan {

	private int width;
    float lineWidth;
    float waveSize;
    int color;


    public HeightLightSpan(Resources resources) {
        this(resources, Color.RED, 1, 3);
    }

	public HeightLightSpan(Resources resources, int color, int lineWidth, int waveSize) {
        // Get the screen's density scale
        final float scale = resources.getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        this.lineWidth = lineWidth * scale + 0.5f;
        this.waveSize = waveSize * scale + 0.5f;

        this.color = color;
	}


	@Override
    public int getSize(Paint paint, CharSequence text,
                       int start, int end,
                       Paint.FontMetricsInt fm) {
        Log.d("ErrorSpan", "text:" + text);
        Log.d("ErrorSpan", "start:" + start);
        Log.d("ErrorSpan", "end:" + end);

//        width = paint.measureText(text, start, end);
        width= (int) (paint.measureText(text, start, end));
        Log.d("ErrorSpan", "width:" + width);
        return width;
	}
                         

	@Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {


        Log.d("ErrorSpan", "start:" + start);
        Log.d("ErrorSpan", "end:" + end);
        Log.d("ErrorSpan", "x:" + x);
        Log.d("ErrorSpan", "top:" + top);
        Log.d("ErrorSpan", "y:" + y);
        Log.d("ErrorSpan", "bottom:" + bottom);

        Paint p = new Paint(paint);
        p.setColor(color);
        p.setStrokeWidth(lineWidth);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeCap(Paint.Cap.BUTT);
        p.setStrokeJoin(Paint.Join.MITER);
        p.setPathEffect(new CornerPathEffect(1*lineWidth));   // set the path effect when they join.



        float doubleWaveSize = waveSize * 2;
        Path path = new Path();
        path.moveTo(x, bottom-waveSize-waveSize);
        for (float i = x; i < x + width; i += doubleWaveSize) {
            if(waveSize+20>width+x){
                path.rLineTo(i, waveSize+2);
                path.rLineTo(i, -waveSize-2);
            }else {
                path.rLineTo(waveSize + 20, waveSize + 2);
                path.rLineTo(waveSize + 20, -waveSize - 2);
            }
        }
        canvas.save();
        // clip the squigly line, add some pixels above and below to clipping to allow for round path join
        canvas.clipRect(new Rect(
                (int)x, // left
                (int)(bottom-waveSize-2*lineWidth), // top
                (int)(x+width-10), // right
                (int)(bottom+2*lineWidth)));  // bottom

        canvas.drawPath(path, p);
        canvas.restore();

        paint.setColor(Color.RED);
        canvas.drawText(text.subSequence(start, end).toString(), x, y, paint);
//        p.setTextSize(31);
//        canvas.drawText("1分",x+width/2, y+31,p);
	}
}