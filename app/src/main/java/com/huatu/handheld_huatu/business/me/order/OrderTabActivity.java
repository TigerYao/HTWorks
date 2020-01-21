package com.huatu.handheld_huatu.business.me.order;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.MySupportActivity;
import com.huatu.handheld_huatu.base.MySupportFragment;


import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by cjx on 2018\7\12 0012.
 */

public class OrderTabActivity  extends MySupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comm_single_root_layout);
/*
         MySupportFragment fragment = findFragment(ShopFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.fl_container, ShopFragment.newInstance());
        }*/
   /*     initView();*/
    }

    /**
     * 设置动画，也可以使用setFragmentAnimator()设置
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
        // 设置自定义动画
//        return new FragmentAnimator(enter,exit,popEnter,popExit);
    }

    @Override
    public void onBackPressedSupport() {
       // ISupportFragment topFragment = getTopFragment();
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
             finish();
        }
     }


}
