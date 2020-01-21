package com.huatu.handheld_huatu.business.essay.cusview;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.business.essay.event.EssayExamMessageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 材料页被可被拖拽的问题卡片
 */
public class QuestionDragViewLayout extends RelativeLayout {

    public QuestionDragViewLayout(Context context) {
        this(context, null);
    }

    public QuestionDragViewLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionDragViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ChangeChildListener changeListener;

    public void setChangeListener(ChangeChildListener changeListener) {
        this.changeListener = changeListener;
    }

    PointF startPoint;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startPoint = new PointF();
                startPoint.x = ev.getRawX();
                startPoint.y = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                PointF nextPoint = new PointF();
                nextPoint.x = ev.getRawX();
                nextPoint.y = ev.getRawY();
                if (Math.abs(nextPoint.x - startPoint.x) < Math.abs(nextPoint.y - startPoint.y)) {
                    isClear = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isClear = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isClear) {
                    isClear = false;
                    EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_QUESTION_CONTENT_CLEAR_VIEW));
                    EventBus.getDefault().post(new EssayExamMessageEvent(EssayExamMessageEvent.ESSAYEXAM_ESSAY_MATERIAL_CONTENT_CLEAR_VIEW));
                }
                PointF nextPoint = new PointF();
                nextPoint.x = event.getRawX();
                nextPoint.y = event.getRawY();

                int dy = (int) (nextPoint.y - startPoint.y);

                if (changeListener != null) {
                    changeListener.changeChild(dy);
                }

                startPoint = nextPoint;

                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface ChangeChildListener {
        void changeChild(int dy);
    }
}
