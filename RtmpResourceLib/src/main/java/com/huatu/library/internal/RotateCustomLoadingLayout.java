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
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView.ScaleType;

import com.gensee.rtmpresourcelib.R;
import com.huatu.library.PullToRefreshBase;
import com.huatu.library.PullToRefreshBase.Mode;
import com.huatu.library.PullToRefreshBase.Orientation;

public class RotateCustomLoadingLayout extends LoadingLayout {

	static final int ROTATION_ANIMATION_DURATION = 1200;

	private final Animation mRotateAnimation;
	private final Matrix mHeaderImageMatrix;

	private float mRotationPivotX, mRotationPivotY;

	private final boolean mRotateDrawableWhilePulling;

	@Override
	protected int inflateResId(boolean isCustom){
		return 	 R.layout.pull_to_refresh_customheader_vertical;
	}

	//private TextView mLoadbackTextView;

	public RotateCustomLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs, boolean isCustom) {
		super(context, mode, scrollDirection, attrs,isCustom);

		//mLoadbackTextView = (TextView) findViewById(R.id.pull_to_back_img);

		mRotateDrawableWhilePulling = attrs.getBoolean(R.styleable.PullToRefresh_ptrRotateDrawableWhilePulling, true);

		mHeaderImage.setScaleType(ScaleType.MATRIX);
		mHeaderImageMatrix = new Matrix();
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);

		mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
		mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
		mRotateAnimation.setRepeatCount(Animation.INFINITE);
		mRotateAnimation.setRepeatMode(Animation.RESTART);
	}

	public void onLoadingDrawableSet(Drawable imageDrawable) {
		if (null != imageDrawable) {
			mRotationPivotX = Math.round(imageDrawable.getIntrinsicWidth() / 2f);
			mRotationPivotY = Math.round(imageDrawable.getIntrinsicHeight() / 2f);
		}
	}

	protected void onPullImpl(float scaleOfLayout) {
 	  /* if(scaleOfLayout> PullToRefreshBase.OVER_PULL_FRICTION){//
 			if(scaleOfLayout<=1.4){
				float alpha=Math.min(scaleOfLayout,1.4f);
 				mLoadbackTextView.setAlpha(2*alpha-1.4f);
 			}
  			mLoadbackTextView.setVisibility(VISIBLE);
		}
		else {
			mLoadbackTextView.setAlpha(0);
			mLoadbackTextView.setVisibility(INVISIBLE);
 		}*/
		float angle;
		if (mRotateDrawableWhilePulling) {
			angle = scaleOfLayout * 90f;
		} else {
			angle =Math.max(0f, Math.min(720f,  (scaleOfLayout+0.5f) * 360f - 0f));
		}

		//Log.e("angle",angle+"");
		mHeaderImageMatrix.setRotate(angle, mRotationPivotX, mRotationPivotY);
		mHeaderImage.setImageMatrix(mHeaderImageMatrix);
	}

	@Override
	protected void refreshingImpl() {
		mHeaderImage.startAnimation(mRotateAnimation);
	}

	@Override
	protected void resetImpl() {
		mHeaderImage.clearAnimation();
		resetImageRotation();
	}

	private void resetImageRotation() {
		if (null != mHeaderImageMatrix) {
			mHeaderImageMatrix.reset();
			mHeaderImage.setImageMatrix(mHeaderImageMatrix);
		}
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
		return R.drawable.xiaza_xz_icon;
	}

}
