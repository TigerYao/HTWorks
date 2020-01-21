package com.huatu.animation;

import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.view.View;



public class BounceTopEnter extends BaseAnimatorSet {
	public BounceTopEnter() {
		duration = 800;
 	}

	@Override
	public void setAnimation(View view) {
		 DisplayMetrics dm = view.getContext().getResources().getDisplayMetrics();
		//ObjectAnimator.ofFloat(view, "alpha", 0.9f, 1, 1, 1),//
		animatorSet.playTogether(
				ObjectAnimator.ofFloat(view, "translationY", -250 * dm.density, 30, -10, 0));
	}
}
