package com.huatu.handheld_huatu.view.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by KaelLi on 2016/7/21.
 */
public class DraftPaperView extends View {
    private Context context;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Bitmap mBitmap;
    private Paint mPaint;

    private ArrayList<DrawPath> savePath;
    private ArrayList<DrawPath> deletePath;
    private DrawPath dp;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private int bitmapWidth;
    private int bitmapHeight;

    public DraftPaperView(Context c) {
        super(c);
        context = c;
        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();
        // 得到屏幕的分辨率
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
        bitmapWidth = dm.widthPixels;
        bitmapHeight = dm.heightPixels - 2 * 45;

        initCanvas();
    }

    public DraftPaperView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);

        bitmapWidth = dm.widthPixels;
        bitmapHeight = dm.heightPixels - 2 * 45;

        initCanvas();
    }

    // 初始化画布
    public void initCanvas() {
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);
        if (SpUtils.getDayNightMode() == 0) {
            mPaint.setColor(ContextCompat.getColor(context, R.color.black));
        } else {
            mPaint.setColor(ContextCompat.getColor(context, R.color.text_night_001));
        }

        mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
                Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawARGB(0x30, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
    }

    class DrawPath {
        Path path;
        Paint paint;
    }

    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            initCanvas();


            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            if (deletePath != null && deletePath.size() > 0) {
                EventBus.getDefault().post(new MessageEvent(3));
            }
            savePath.remove(savePath.size() - 1);
            if (savePath != null && savePath.size() == 0) {
                EventBus.getDefault().post(new MessageEvent(4));
            }

            Iterator<DrawPath> iter = savePath.iterator(); // 重复保存
            while (iter.hasNext()) {
                DrawPath dp = iter.next();
                mCanvas.drawPath(dp.path, dp.paint);

            }
            invalidate();
        }
    }

    public void onDestroyResource() {
        if(mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if(mPath != null && mPath.isEmpty()) {
            mPath.reset();
            mPath = null;
        }
    }

    public void redo() {
        if (deletePath.size() > 0) {
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            savePath.add(dp);
            if (savePath != null && savePath.size() > 0) {
                EventBus.getDefault().post(new MessageEvent(2));
                EventBus.getDefault().post(new MessageEvent(6));
            }
            mCanvas.drawPath(dp.path, dp.paint);
            deletePath.remove(deletePath.size() - 1);
            if (deletePath != null && deletePath.size() == 0) {
                EventBus.getDefault().post(new MessageEvent(5));
            }
            invalidate();
        }
    }

    public void removeAllPaint() {
        initCanvas();
        invalidate();
        savePath.clear();
        deletePath.clear();
        EventBus.getDefault().post(new MessageEvent(5));
        EventBus.getDefault().post(new MessageEvent(4));
        EventBus.getDefault().post(new MessageEvent(7));
    }


    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        savePath.add(dp);
        if (savePath != null && savePath.size() > 0) {
            EventBus.getDefault().post(new MessageEvent(2));
            EventBus.getDefault().post(new MessageEvent(6));
        }
        mPath = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPath = new Path();
                dp = new DrawPath();
                dp.path = mPath;
                dp.paint = mPaint;

                touch_start(x, y);
                invalidate(); // 清屏
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }
}
