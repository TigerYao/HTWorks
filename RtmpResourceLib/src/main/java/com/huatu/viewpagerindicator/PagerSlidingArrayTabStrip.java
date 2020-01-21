/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huatu.viewpagerindicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gensee.rtmpresourcelib.R;
import com.huatu.utils.DensityUtils;

import java.util.Locale;

public class PagerSlidingArrayTabStrip extends HorizontalScrollView {


	public interface onSelectTabListener{
		int getCurrentItem();
		void  setCurrentItem(int postion, boolean isAnima);
 	}

	public interface onTabChangeIntercept{
		boolean OnBeforeTabIntercept(int index);
	}

	onTabChangeIntercept mOnTabChangeIntercept;
	public void setOnTabChangeIntercept(onTabChangeIntercept ontabChangeIntercept){
		this.mOnTabChangeIntercept=ontabChangeIntercept;
	}

	onSelectTabListener mOnSelectTabListener;
	public void setOnSelectTabListener(onSelectTabListener tabListener){
		mOnSelectTabListener=tabListener;
 	}

	public interface IconTabProvider {
		public int getPageIconResId(int position);
	}

	// @formatter:off
	private static final int[] ATTRS = new int[]{
			android.R.attr.textSize,
			android.R.attr.textColor
	};
	// @formatter:on

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	//private final PageListener pageListener = new PageListener();

	//public android.support.v4.view.ViewPager.OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	//private android.support.v4.view.ViewPager pager;

	private int tabCount;

	private int currentPosition = 0;
	private float currentPositionOffset = 0f;

	private Paint rectPaint;
	private Paint dividerPaint;

	private int indicatorColor = 0xFF666666;
	private int underlineColor = 0x1A000000;
	private int dividerColor = 0x1A000000;

	private boolean shouldExpand = false;
	private boolean textAllCaps = true;

	private int scrollOffset = 52;
	private int indicatorHeight = 8;
	//下划线与底部距离
	private int indicatorMarginBottom=0;

	private int indicatorWidth = 0;
	private int underlineHeight = 2;
	private int dividerPadding = 12;
	private int tabPadding = 24;
	private int dividerWidth = 1;

	private int tabTextSize = 12;
	private ColorStateList tabTextColor = ColorStateList.valueOf(0xFF666666);
	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.NORMAL;

	private int lastScrollX = 0;

	private int tabBackgroundResId =android.R.color.transparent;// R.color.xc_transparent;

	private Locale locale;

	private boolean mInitialize;

	//private int mDistance=0;

	protected boolean mNeedlineOffset=false;

	public PagerSlidingArrayTabStrip(Context context) {
		this(context, null);
	}

	public PagerSlidingArrayTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerSlidingArrayTabStrip(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setFillViewport(true);
		setWillNotDraw(false);
		indicatorMarginBottom= DensityUtils.dp2px(context,11);
		//mDistance= DensityUtils.dp2px(context,12);

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();

		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		//indicatorWidth =  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorWidth, dm);
		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
		//dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

		// get system attrs (android:textSize and android:textColor)

		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

		tabTextSize = a.getDimensionPixelSize(0, tabTextSize);

		a.recycle();

		// get custom attrs

		a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

		indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
		underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
		dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
		tabTextColor = a.getColorStateList(R.styleable.PagerSlidingTabStrip_pstsTextColor);
		indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
		indicatorWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorWidth, indicatorWidth);
		underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
		dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
		tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
		tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
		shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
		scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
		tabTextSize = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTextSize, tabTextSize);
		textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

		a.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Paint.Style.FILL);

		dividerPaint = new Paint();
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

		if (locale == null) {
			locale = getResources().getConfiguration().locale;
		}
	}

/*	public void setViewPager(android.support.v4.view.ViewPager pager) {
		if (this.pager != null) {
			this.pager.removeOnPageChangeListener(pageListener);
		}
		this.pager = pager;
		if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}

		pager.addOnPageChangeListener(pageListener);
		this.mInitialize = true;
		notifyDataSetChanged();
	}*/


	String[] mTitleStr;

	public void setTabArray(String[] titles){
		if(titles==null||titles.length<=0) return;
		mTitleStr=titles;
		this.mInitialize = false;
		notifyDataSetChanged();
 	}

	/*public void setOnPageChangeListener(android.support.v4.view.ViewPager.OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}*/

	public void notifyDataSetChanged() {

		tabsContainer.removeAllViews();

		tabCount = mTitleStr.length;//.getCount();

		for (int i = 0; i < tabCount; i++) {
 			addTextTab(i, mTitleStr[i].toString());
	 	}

		updateTabStyles();

		getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				if(null!=mOnSelectTabListener) {
 					currentPosition = mOnSelectTabListener.getCurrentItem();
					tabsContainer.getChildAt(currentPosition).setSelected(true);

					View oldView=tabsContainer.getChildAt(currentPosition);
					if(oldView instanceof BoldTextView){
						((BoldTextView)oldView).setBold(true);
					}
					scrollToChild(currentPosition, 0);
				}
			}
		});

	}

	private void addTextTab(final int position, String title) {

		BoldTextView tab = new BoldTextView(getContext());
		tab.setText(title);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		if (mInitialize) {
			tab.setSelected(true);
			mInitialize = false;
		}
		addTab(position, tab);
	}

	private void addIconTab(final int position, int resId) {

		ImageButton tab = new ImageButton(getContext());
		tab.setImageResource(resId);

		addTab(position, tab);

	}

	private void addTab(final int position, View tab) {
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if((null!=mOnTabChangeIntercept)&&(mOnTabChangeIntercept.OnBeforeTabIntercept(position)))
					return;

				showSelectChange(position,0);
				if(null!=mOnSelectTabListener)
					mOnSelectTabListener.setCurrentItem(position,false);
			}
		});

		tab.setPadding(tabPadding, 0, tabPadding, 0);
		tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
	}

	private void updateTabStyles() {

		for (int i = 0; i < tabCount; i++) {

			View v = tabsContainer.getChildAt(i);

			v.setBackgroundResource(tabBackgroundResId);

			if (v instanceof TextView) {

				TextView tab = (TextView) v;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
				tab.setTypeface(tabTypeface, tabTypefaceStyle);
				tab.setTextColor(tabTextColor);

				// setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
				// pre-ICS-build
				if (textAllCaps) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						tab.setAllCaps(true);
					} else {
						tab.setText(tab.getText().toString().toUpperCase(locale));
					}
				}
			}
		}

	}

	private void scrollToChild(int position, int offset) {

		if (tabCount == 0) {
			return;
		}

		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
		}

		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();

		// draw indicator line

		rectPaint.setColor(indicatorColor);

		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(currentPosition);
		float lineLeft = currentTab.getLeft();
		float lineRight = currentTab.getRight();

    /*    float lineOffset = 0.0f;
        if(indicatorWidth > 0 && lineRight - lineLeft > indicatorWidth) {
            lineOffset = (currentTab.getWidth()-indicatorWidth) / 2;
        }*/

		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

			View nextTab = tabsContainer.getChildAt(currentPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();

			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
		}
		int lineOffset =tabPadding;
		//画Tab线(默认5个TAB一屏幕)
		//int lineOffset = 0;
		if (mNeedlineOffset&&tabCount <= 5) {
			lineOffset = 20 * (5 - tabCount);
		}
		//canvas.drawRect(lineLeft + lineOffset, height - indicatorHeight, lineRight - lineOffset, height, rectPaint);
		canvas.drawRect(lineLeft + lineOffset, height - indicatorHeight-indicatorMarginBottom, lineRight - lineOffset, height-indicatorMarginBottom, rectPaint);

		// draw underline
		rectPaint.setColor(underlineColor);
		canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

		// draw divider

		dividerPaint.setColor(dividerColor);
		for (int i = 0; i < tabCount - 1; i++) {
			View tab = tabsContainer.getChildAt(i);
			canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
		}
	}

	public void showSelectChange(int position,int positionOffset){

		View oldView=tabsContainer.getChildAt(currentPosition);
		if(oldView instanceof BoldTextView){
			((BoldTextView)oldView).setBold(false);
		}
		tabsContainer.getChildAt(currentPosition).setSelected(false);
		currentPosition = position;

		if(tabsContainer.getChildAt(currentPosition) instanceof BoldTextView){
			((BoldTextView)tabsContainer.getChildAt(currentPosition)).setBold(true);
		}
		tabsContainer.getChildAt(currentPosition).setSelected(true);
		currentPositionOffset = positionOffset;

		scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

		invalidate();
	}

	private class PageListener implements android.support.v4.view.ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			//if(tabsContainer.getChildCount() > currentPosition){

			View oldView=tabsContainer.getChildAt(currentPosition);
			if(oldView instanceof BoldTextView){
				((BoldTextView)oldView).setBold(false);
			}

			tabsContainer.getChildAt(currentPosition).setSelected(false);
			currentPosition = position;

			if(tabsContainer.getChildAt(currentPosition) instanceof BoldTextView){
				((BoldTextView)tabsContainer.getChildAt(currentPosition)).setBold(true);
			}
			tabsContainer.getChildAt(currentPosition).setSelected(true);
			currentPositionOffset = positionOffset;

			scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

			invalidate();

			//}
		/*	if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}*/
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == android.support.v4.view.ViewPager.SCROLL_STATE_IDLE) {
				//scrollToChild(pager.getCurrentItem(), 0);
			}

			/*if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}*/
		}

		@Override
		public void onPageSelected(int position) {
			/*if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}*/
		}

	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.dividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	public int getDividerPadding() {
		return dividerPadding;
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.scrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		requestLayout();
	}

	public boolean getShouldExpand() {
		return shouldExpand;
	}

	public boolean isTextAllCaps() {
		return textAllCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.textAllCaps = textAllCaps;
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		updateTabStyles();
	}

	public int getTextSize() {
		return tabTextSize;
	}

	public void setTextColor(int textColor) {
		this.tabTextColor = ColorStateList.valueOf(textColor);
		updateTabStyles();
	}

	public void setTextColorResource(int resId) {
		this.tabTextColor = getResources().getColorStateList(resId);
		updateTabStyles();
	}

	public int getTextColor() {
		return tabTextColor.getDefaultColor();
	}

	public void setTypeface(Typeface typeface, int style) {
		this.tabTypeface = typeface;
		this.tabTypefaceStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.tabBackgroundResId = resId;
	}

	public int getTabBackground() {
		return tabBackgroundResId;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPadding = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return tabPadding;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}