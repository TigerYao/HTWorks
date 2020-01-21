/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.huatu.library.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.gensee.rtmpresourcelib.R;
import com.huatu.library.PullToRefreshBase;
import com.huatu.library.PullToRefreshBase.Mode;
import com.huatu.library.PullToRefreshBase.Orientation;
import com.huatu.shimmer.ShimmerTextView;

public class RepeatCustomLoadingLayout extends LoadingLayout {

	static final int ROTATION_ANIMATION_DURATION = 1200;


	//private final Matrix mHeaderImageMatrix;

	private float mRotationPivotX, mRotationPivotY;

	private final boolean mRotateDrawableWhilePulling;
	private ShimmerTextView mHeadTextView;

	@Override
	protected int inflateResId(boolean isCustom){
		return 	 R.layout.ptr_repeatheader_vertical;
	}

	//private TextView mLoadbackTextView;

	public RepeatCustomLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs, boolean isCustom) {
		super(context, mode, scrollDirection, attrs,isCustom);

		//mLoadbackTextView = (TextView) findViewById(R.id.pull_to_back_img);

		mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);
		mHeadTextView=(ShimmerTextView)getHeaderText();
		mHeadTextView.setCustomReflectionColor(Color.parseColor("#333333"));
/*		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);*/


	}

	public void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
			mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
			mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
		}
	}

	protected void onPullImpl(float scaleOfLayout) {
		//Log.e("onpullImp",scaleOfLayout+"");
		if(scaleOfLayout> 0){//
 			if(scaleOfLayout<=1){
				float alpha=Math.min(scaleOfLayout,1f);
				mHeadTextView.setShimmering(true);
				float toX = mHeadTextView.getWidth();
				mHeadTextView.setGradientX((float)(toX*(alpha/1)));
 				//mLoadbackTextView.setAlpha(2*alpha-1.4f);
 			}
		    else {
				if(mHeadTextView.isShimmering()){
					mHeadTextView.setShimmering(false);
					mHeadTextView.invalidate();
				}
			}
  			//mLoadbackTextView.setVisibility(VISIBLE);
		}
		else {
		    if(mHeadTextView.isShimmering()){
				mHeadTextView.setShimmering(false);
				mHeadTextView.invalidate();
			}

		/*	mLoadbackTextView.setAlpha(0);
			mLoadbackTextView.setVisibility(INVISIBLE);*/
 		}
/*		float angle;
		if (mRotateDrawableWhilePulling) {
			angle = scaleOfLayout * 90f;
		} else {
			angle =Math.max(0f, Math.min(720f,  (scaleOfLayout+0.5f) * 360f - 0f));
		}*/

		//Log.e("angle",angle+"");
	/*	mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);*/
	}

	@Override
	protected void refreshingImpl() {

	}

	@Override
	protected void resetImpl() {

	}



	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.xiaza_shuaxin_icon;
	}

}
