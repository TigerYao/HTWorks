package com.huatu.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
@Deprecated
public class ArrowlineView extends View {
   

	/**
	 * 绘制三角形的画笔
	 */
	private Paint mPaint;
	/**
	 * path构成一个三角形
	 */
	private Path mPath;
	/**
	 * 三角形的宽度
	 */
	private int mTriangleWidth;
	/**
	 * 三角形的高度
	 */
	private int mTriangleHeight;


	 
 	 
	public ArrowlineView(Context context) {
		this(context, null, 0);
	}

	public ArrowlineView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ArrowlineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {

		// 初始化画笔
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		// mPaint.setColor(Color.parseColor("#ffffffff"));
		mPaint.setColor(Color.parseColor("#FF32312e"));

		mPaint.setStyle(Style.FILL);
		mPaint.setPathEffect(new CornerPathEffect(3));
	}
 
 
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
 		//canvas.translate( + mTranslationX, 0);//getHeight() + 1
		canvas.drawPath(mPath, mPaint);
  	}
 
	/**
	 * 初始化三角形指示器
	 */
	private void initTriangle() {
		mPath = new Path();

		//mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
		mPath.moveTo(0, 0);
		mPath.lineTo(mTriangleWidth, 0);
		//mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		mPath.lineTo(mTriangleWidth / 2, mTriangleHeight);
		mPath.close();
	}

	
	/**
	 * 初始化三角形的宽度
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	/*	mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGEL);// 1/6 of width
		mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);*/

		mTriangleHeight=getHeight();
		mTriangleWidth=getWidth();
 		initTriangle(); // 初始化三角形
 		// 初始时的偏移量

	}

}
