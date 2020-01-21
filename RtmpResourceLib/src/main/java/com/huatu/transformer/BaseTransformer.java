 
package com.huatu.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;


import com.huatu.utils.ViewAnimHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is all transformers father.
 *
 * BaseTransformer implement    }
 * which is just same as {@link ViewPager.PageTransformer}.
 *
 * After you call setPageTransformer(), transformPage() will be called by  com.daimajia.slider.library.Tricks.ViewPagerEx}
 * when your slider are animating.
 *
 * In onPreTransform() function, that will make   com.daimajia.slider.library.Animations.BaseAnimationInterface}
 * work.
 *
 * if you want to make an acceptable transformer, please do not forget to extend from this class.
 */
public abstract class BaseTransformer implements ViewPager.PageTransformer {
 

    /**
     * Called each {@link #transformPage(View, float)}.
     *
     * @param view
     * @param position
     */
    protected abstract void onTransform(View view, float position);

    private HashMap<View,ArrayList<Float>> h = new HashMap<View, ArrayList<Float>>();

    @Override
    public void transformPage(View view, float position) {
        onPreTransform(view, position);
        onTransform(view, position);
        onPostTransform(view, position);
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * visibility of the fragment to {@link View#GONE}. Returning false will force the fragment to {@link View#VISIBLE}.
     *
     * @return
     */
    protected boolean hideOffscreenPages() {
        return true;
    }

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return
     */
    protected boolean isPagingEnabled() {
        return false;
    }

    /**
     * Called each {@link #transformPage(View, float)} before {{@link #onTransform(View, float)} is called.
     *
     * @param view
     * @param position
     */
    protected void onPreTransform(View view, float position) {
        final float width = view.getWidth();

        ViewAnimHelper.setRotationX(view,0);
        ViewAnimHelper.setRotationY(view,0);
        ViewAnimHelper.setRotation(view,0);
        ViewAnimHelper.setScaleX(view,1);
        ViewAnimHelper.setScaleY(view,1);
        ViewAnimHelper.setPivotX(view,0);
        ViewAnimHelper.setPivotY(view,0);
        ViewAnimHelper.setTranslationY(view,0);
        ViewAnimHelper.setTranslationX(view,isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            ViewAnimHelper.setAlpha(view,position <= -1f || position >= 1f ? 0f : 1f);
        } else {
            ViewAnimHelper.setAlpha(view,1f);
        }
        
    }
    boolean isApp,isDis;
    /**
     * Called each {@link #transformPage(View, float)} call after {@link #onTransform(View, float)} is finished.
     *
     * @param view
     * @param position
     */
    protected void onPostTransform(View view, float position) {
        
    }


   

}