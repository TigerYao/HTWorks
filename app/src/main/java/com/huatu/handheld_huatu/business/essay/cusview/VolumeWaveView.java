package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.huatu.handheld_huatu.R;

public class VolumeWaveView extends View {
    public boolean boolA = true;
    private float flb = boolA(5.0F);
    private float flc = boolA(35.0F);
    private Path mPath = new Path();
    private Paint mPaint = new Paint();
    private DisplayMetrics mDisM = getResources().getDisplayMetrics();
    private float fld = this.mDisM.widthPixels;
    private float flcc = 1.0F;
    private float flas = 0.0F;
    private float flbs = this.flb;
    private float flks = this.fld / 1.5F;
    private float flss = 6.2831855F / this.flks * this.flcc;
    private float[] flmm = new float[(int)(this.fld / this.flcc)];
    public static  int pervolume;
    public boolean isOpen;
    public int offsety=-30;

    public VolumeWaveView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(getResources().getColor(R.color.common_style_text_color));
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeWidth(3.0F);
    }

    private int boolA(float dsip)
    {
        return (int)(0.5F + dsip * getResources().getDisplayMetrics().density);
    }

    public void  setOpen(boolean open){
        isOpen=open;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isOpen) {
            this.flas = ((float) (0.2D + this.flas));
            float f1 = this.flas;
            float f2 = this.flc / 30.0F;
            if (pervolume > 30) {
                pervolume = 30;
            }
            this.flbs = (f2 * pervolume);
            if ((this.flbs < this.flb) && (this.boolA)) {
                this.flbs = this.flb;
            }
            float f3 = f1;
            for (int n = 0; n < this.flmm.length; n++) {
                this.flmm[n] = ((float) Math.sin(f3) * this.flbs);
                f3 += this.flss;
            }
            float f4 = getHeight() / 2;
            this.mPath.moveTo(0.0F, f4 + this.flmm[0]+offsety);
            for (int i1 = 1; i1 < -1 + this.flmm.length; i1 += 2) {
                this.mPath.quadTo(i1 * this.flcc, f4 + this.flmm[i1]+offsety, (i1 + 1) * this.flcc, f4 + this.flmm[(i1 + 1)]+offsety);
            }
            canvas.drawPath(this.mPath, this.mPaint);
            this.mPath.reset();
        }else {
            float f=0;
            if(this.flmm.length>0) {
                f = this.flmm[0];
            }
            this.mPath.moveTo(0.0F, getHeight() / 2 + f+offsety);
            this.mPath.quadTo(0.0F, getHeight() / 2 + f+offsety, getWidth(), getHeight() / 2 +f+offsety);
            canvas.drawPath(this.mPath, this.mPaint);
            this.mPath.reset();
        }
        invalidate();
    }
}
