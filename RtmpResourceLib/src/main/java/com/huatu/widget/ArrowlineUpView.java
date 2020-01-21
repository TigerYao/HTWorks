package com.huatu.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


//https://blog.csdn.net/lmj623565791/article/details/45022631
//深入理解Android中的自定义属性

public class ArrowlineUpView extends View {
	private static final int ATTR_ANDROID_TEXT = 0;
	private static final int[] mAttr = { android.R.attr.text };
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

	private int mPaintColor=Color.parseColor("#99000000");



	public ArrowlineUpView(Context context) {
		this(context, null, 0);
	}

	public ArrowlineUpView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ArrowlineUpView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);

		// ==>use typedarray
	/*	TypedArray ta = context.obtainStyledAttributes(attrs, mAttr);
        if(ta!=null){
			String text = ta.getString(ATTR_ANDROID_TEXT);
 			ta.recycle();
			mPaintColor=Color.parseColor(text);
		}*/
	 }

	private void init(Context context, AttributeSet attrs, int defStyle) {

		// 初始化画笔
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		// mPaint.setColor(Color.parseColor("#ffffffff"));
		//mPaint.setColor(Color.parseColor(/*"#FF5c5f67"*/"#99000000"));
		mPaint.setColor(mPaintColor);
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

/*	*//**
	 * 初始化三角形指示器
	 *//*
	private void initTriangle() {
		mPath = new Path();

		//mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
		mPath.moveTo(0, 0);
		mPath.lineTo(mTriangleWidth, 0);

		mPath.lineTo(mTriangleWidth / 2, mTriangleHeight);
		mPath.close();
	}*/

	/**
	 * 初始化三角形指示器
	 */
	private void initTriangle()
	{
		mPath = new Path();

		//mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
		mPath.moveTo(0, mTriangleHeight);
		mPath.lineTo(mTriangleWidth, mTriangleHeight);
		mPath.lineTo(mTriangleWidth / 2, 0);
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
