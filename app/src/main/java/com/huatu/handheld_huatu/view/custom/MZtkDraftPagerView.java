package com.huatu.handheld_huatu.view.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.utils.DraftDataCache;
import com.huatu.handheld_huatu.event.MessageEvent;
import com.huatu.handheld_huatu.mvpmodel.arena.DrawPath;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * Created by michael on 17/6/15.
 */
public class MZtkDraftPagerView extends View {

    private static final float TOUCH_TOLERANCE = 4;
    private static final int TOUCH_ACTION_ONE_POINTER_DOWN = 1;
    private static final int TOUCH_DELAYED = 50;
    private int bitmapHeight, bitmapWidth;
    private float downX, downY;
    private DrawPath dp;
    private Path mPath;
    private Paint mPaint;
    private Canvas mCacheCanvas;
    private Bitmap bgBitmap;
    private Paint mBitmapPaint;
    private float mMoveCanvasDX;
    private float mMoveCanvasDY;
    private ArrayList<DrawPath> savePath;
    private ArrayList<DrawPath> deletePath;
    boolean isMoveStatus;  //双手指 移动 状态

    Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            switch (msg.what) {
                case TOUCH_ACTION_ONE_POINTER_DOWN:
                    mPath = new Path();
                    dp = new DrawPath();
                    dp.path = mPath;
                    dp.paint = mPaint;
                    mPath.reset();
                    mPath.moveTo(downX, downY);
                    isMoveStatus = false;
                    break;
            }

        }
    };

    public MZtkDraftPagerView(Context context) {
        this(context, null);
    }

    public MZtkDraftPagerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MZtkDraftPagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initData(context);
    }

    private void initData(Context mContext) {

        savePath = new ArrayList<>();
        deletePath = new ArrayList<>();

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        bitmapWidth = dm.widthPixels;
        bitmapHeight = dm.heightPixels - 2 * 45;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        if (SpUtils.getDayNightMode() == 0) {
            mPaint.setColor(mContext.getResources().getColor(R.color.new_draft_color));
        } else {
            mPaint.setColor(mContext.getResources().getColor(R.color.new_draft_color_night));
        }

        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        bgBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        mCacheCanvas = new Canvas(bgBitmap);
        mCacheCanvas.drawARGB(0x30, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bgBitmap, 0, 0, mBitmapPaint);
        canvas.save();
        canvas.translate(mMoveCanvasDX, mMoveCanvasDY);
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        if (savePath != null && savePath.size() > 0) {
            for (int i = 0; i < savePath.size(); i++) {
                canvas.drawPath(savePath.get(i).path, mPaint);
            }
        }
        canvas.restore();
        LogUtils.d("MDraftPagerView", "savePath.size():" + savePath.size());
        LogUtils.d("MDraftPagerView", "deletePath.size():" + deletePath.size());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int pointerCount = event.getPointerCount();
        LogUtils.d("MDraftPagerView_touch", "pointerCount: " + pointerCount);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                LogUtils.d("MDraftPagerView_touch", "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y, pointerCount);
                LogUtils.d("MDraftPagerView_touch", "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                LogUtils.d("MDraftPagerView_touch", "ACTION_UP");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                LogUtils.d("MDraftPagerView_touch", "ACTION_POINTER_DOWN");
                isMoveStatus = true;
                mHandler.removeMessages(TOUCH_ACTION_ONE_POINTER_DOWN);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                LogUtils.d("MDraftPagerView_touch", "ACTION_POINTER_UP");
                break;
        }
        invalidate();
        return true;
    }

    private void touchDown(float x, float y) {

        // 偏移量
        x = x - mMoveCanvasDX;
        y = y - mMoveCanvasDY;

        mHandler.sendEmptyMessageDelayed(TOUCH_ACTION_ONE_POINTER_DOWN, TOUCH_DELAYED);
        isMoveStatus = true;
        downX = x;
        downY = y;
    }

    private void touchMove(float x, float y, int pointerCount) {
        if (isMoveStatus) {
            if (pointerCount > 1) {
                if (savePath.size() > 0) {
                    touchMoveCanvas(x, y);
                } else {
                    if (dp != null) {
                        savePath.add(dp);
                        dp = null;
                        if (savePath != null && savePath.size() > 0) {
                            EventBus.getDefault().post(new MessageEvent(2));
                            EventBus.getDefault().post(new MessageEvent(6));
                        }
                    }
                }
            }
        } else {
            touchMovePath(x, y);
        }
    }

    private void touchMoveCanvas(float x, float y) {
        mMoveCanvasDX = x - downX;
        mMoveCanvasDY = y - downY;
    }

    private void touchMovePath(float x, float y) {

        // 偏移量
        x = x - mMoveCanvasDX;
        y = y - mMoveCanvasDY;

        float dx = Math.abs(x - downX);
        float dy = Math.abs(y - downY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            if (mPath != null) {
                mPath.quadTo(downX, downY, (x + downX) / 2, (y + downY) / 2);
            }
            downX = x;
            downY = y;
        }
    }

    private void touchUp(float x, float y) {

        // 偏移量
        x = x - mMoveCanvasDX;
        y = y - mMoveCanvasDY;

        if (mPaint != null && dp != null) {
            if (mPath != null) {
                mPath.lineTo(downX, downY);
            }
            savePath.add(dp);
            if (savePath != null && savePath.size() > 0) {
                EventBus.getDefault().post(new MessageEvent(2));
                EventBus.getDefault().post(new MessageEvent(6));
            }
        }
        dp = null;
        mPath = null;
        isMoveStatus = false;
    }

    public void initView(int currentItem) {
        int day_night_mode = SpUtils.getDayNightMode();
        if (DraftDataCache.getInstance().getCache(currentItem) != null && DraftDataCache.getInstance().getCache(currentItem).size() != 0) {
            savePath.addAll(DraftDataCache.getInstance().getCache(currentItem));
            EventBus.getDefault().post(new MessageEvent(2));
            EventBus.getDefault().post(new MessageEvent(6));
            if (day_night_mode != 0) {
                EventBus.getDefault().post(new MessageEvent(10));
            }
        } else {
            if (day_night_mode != 0) {
                EventBus.getDefault().post(new MessageEvent(11));
            }
            DraftDataCache.getInstance().clearDraftCache();
        }
        invalidate();
    }

    public void undo() {
        if (savePath != null && savePath.size() > 0) {
            DrawPath drawPath = savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            if (deletePath != null && deletePath.size() > 0) {
                EventBus.getDefault().post(new MessageEvent(3));
            }
            savePath.remove(savePath.size() - 1);
            if (savePath != null && savePath.size() == 0) {
                EventBus.getDefault().post(new MessageEvent(4));
            }

            invalidate();
        }
    }

    public void onDestroyResource(int currentItem) {
        if (bgBitmap != null && !bgBitmap.isRecycled()) {
            bgBitmap.recycle();
            bgBitmap = null;
        }
        if (mPath != null && mPath.isEmpty()) {
            mPath.reset();
            mPath = null;
        }
        if (savePath.size() != 0) {
            DraftDataCache.getInstance().clearDraftCache();
            DraftDataCache.getInstance().putCache(currentItem, savePath);
        } else {
            DraftDataCache.getInstance().clearDraftCache();
        }
//        savePath.clear();
        deletePath.clear();
    }

    public void redo() {
        if (deletePath.size() > 0) {
            DrawPath dp = deletePath.get(deletePath.size() - 1);
            savePath.add(dp);
            if (savePath != null && savePath.size() > 0) {
                EventBus.getDefault().post(new MessageEvent(2));
                EventBus.getDefault().post(new MessageEvent(6));
            }
            deletePath.remove(deletePath.size() - 1);
            if (deletePath != null && deletePath.size() == 0) {
                EventBus.getDefault().post(new MessageEvent(5));
            }
            invalidate();
        }
    }

    public void removeAllPaint() {
        invalidate();
        savePath.clear();
        deletePath.clear();
        EventBus.getDefault().post(new MessageEvent(5));
        EventBus.getDefault().post(new MessageEvent(4));
        EventBus.getDefault().post(new MessageEvent(7));

    }
}
