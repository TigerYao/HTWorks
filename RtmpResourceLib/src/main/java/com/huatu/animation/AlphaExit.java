package com.huatu.animation;

import android.animation.ObjectAnimator;
import android.view.View;


public class AlphaExit extends BaseAnimatorSet {


	@Override
	public void setAnimation(View view) {

		animatorSet.playTogether( ObjectAnimator.ofFloat(view, "alpha", 1, 0));
	}
}
