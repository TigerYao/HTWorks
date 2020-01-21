package com.huatu.transformer;

import android.view.View;

import com.huatu.utils.ViewAnimHelper;


public class ZoomOutSlideTransformer extends BaseTransformer {

	private static final float MIN_SCALE = 0.9f;
	private static final float MIN_ALPHA = 0.9f;

	@Override
	protected void onTransform(View view, float position) {
		if (position >= -1 || position <= 1) {
			// Modify the default slide transition to shrink the page as well
			final float height = view.getHeight();
			final float weight=view.getWidth();
			final float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			final float vertMargin = height * (1 - scaleFactor) / 2;
			final float horzMargin = view.getWidth() * (1 - scaleFactor) / 2;

            // Center vertically
			ViewAnimHelper.setPivotY(view,0.5f * height);
			ViewAnimHelper.setPivotX(view,0.5f*weight);


			if (position < 0) {
				ViewAnimHelper.setTranslationX(view,horzMargin - vertMargin / 2);
			} else {
				ViewAnimHelper.setTranslationX(view,-horzMargin + vertMargin / 2);
			}

			// Scale the page down (between MIN_SCALE and 1)
			ViewAnimHelper.setScaleX(view,scaleFactor);
			ViewAnimHelper.setScaleY(view,scaleFactor);

			// Fade the page relative to its size.
			ViewAnimHelper.setAlpha(view,MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
		}
	}

}
