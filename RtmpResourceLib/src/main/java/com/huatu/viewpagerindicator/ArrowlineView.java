package com.huatu.viewpagerindicator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.gensee.rtmpresourcelib.R;
import com.huatu.utils.DensityUtils;


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

	/**
	 * 三角形的宽度为单个Tab的1/6
	 */
	private static final float RADIO_TRIANGEL = 1.0f / 6;
	/**
	 * 三角形的最大宽度
	 */
	//private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RADIO_TRIANGEL);
	private final int DIMENSION_TRIANGEL_WIDTH = (int) (DensityUtils.dp2px(getContext(),30));

	/**
	 * 初始时，三角形指示器的偏移量
	 */
	private int mInitTranslationX;
	/**
	 * 手指滑动时的偏移量
	 */
	private float mTranslationX;

	private int mDefaultColor=Color.parseColor("#e9304e");


	/**
	 * tab数量
	 */
	private int mTabVisibleCount = 2;

	private boolean mTabFill=false;

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

		// 获得自定义属性，tab的数量
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimplePagerIndicator);
		mTabVisibleCount = a.getInt(R.styleable.SimplePagerIndicator_item_count, 2);
		mTabFill=a.getBoolean(R.styleable.SimplePagerIndicator_item_fill,false);
		mDefaultColor=a.getColor(R.styleable.SimplePagerIndicator_item_indcolor,mDefaultColor);

		if (mTabVisibleCount < 0)
			mTabVisibleCount = 2;
		a.recycle();

		// 初始化画笔
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		// mPaint.setColor(Color.parseColor("#ffffffff"));
		mPaint.setColor(mDefaultColor);

		mPaint.setStyle(Style.FILL);
		mPaint.setPathEffect(new CornerPathEffect(3));
	}


	private float mTmpTranslationx=0;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
 		canvas.translate(mInitTranslationX + mTmpTranslationx, 0);//getHeight() + 1
		canvas.drawPath(mPath, mPaint);
  	}

	/**
	 * 初始化三角形指示器
	 */
	/*private void initTriangle() {
		mPath = new Path();

		mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
		mPath.moveTo(0, 0);
		mPath.lineTo(mTriangleWidth, 0);

		//mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		mPath.lineTo(mTriangleWidth / 2, mTriangleHeight);
		mPath.close();
	}*/
    //矩形
	private void initTriangle() {
		mPath = new Path();

		if(!mTabFill)  mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
		mPath.moveTo(0, 0);
		mPath.lineTo(mTriangleWidth, 0);
		mPath.lineTo(mTriangleWidth, mTriangleHeight);
		mPath.lineTo(0, mTriangleHeight);
		//mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		//mPath.lineTo(mTriangleWidth / 2, mTriangleHeight);
		mPath.close();
	}

	/**
	 * 指示器跟随手指滚动，以及容器滚动
	 *
	 * @param position
	 * @param offset
	 */
	public void scroll(int position, float offset) {
		/**
		 * <pre>
		 *  0-1:position=0 ;1-0:postion=0;
		 * </pre>
		 */
		// 不断改变偏移量，invalidate
		mTranslationX = getWidth() / mTabVisibleCount * (position + offset);
		scrollAnim(mTmpTranslationx,mTranslationX);
 		//int tabWidth = getScreenWidth() / mTabVisibleCount;
 	    //invalidate();
	}

	public void scrollnoAnim(int position, float offset) {
		/**
		 * <pre>
		 *  0-1:position=0 ;1-0:postion=0;
		 * </pre>
		 */
		// 不断改变偏移量，invalidate
		mTranslationX = getWidth() / mTabVisibleCount * (position + offset);
		mTmpTranslationx=mTranslationX;
		invalidate();
		//scrollAnim(mTmpTranslationx,mTranslationX);
		//int tabWidth = getScreenWidth() / mTabVisibleCount;
		//invalidate();
	}

	private void scrollAnim(float fromTrans,float toTrans){

		ValueAnimator scaleAnim= ValueAnimator.ofFloat(fromTrans,toTrans);
		scaleAnim.setDuration(200);
		//scaleAnim.setRepeatCount(-1);
		scaleAnim.setStartDelay(0);
		scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mTmpTranslationx= (float) animation.getAnimatedValue();
				postInvalidate();

			}
		});
		scaleAnim.start();
	}



	/**
	 * 初始化三角形的宽度
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
 		if(mTabFill){
			mTriangleWidth= (int) (w / mTabVisibleCount);
			mTriangleHeight=h;
		}
		else {
			mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGEL);// 1/6 of width
			mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);
		}

 		initTriangle(); // 初始化三角形
 		// 初始时的偏移量
		mInitTranslationX = getWidth() / mTabVisibleCount / 2 - mTriangleWidth / 2;
	}

	/**
	 * 获得屏幕的宽度
	 *
	 * @return
	 */
	public int getScreenWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}
}
