package com.huatu.handheld_huatu.base.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;

import java.util.LinkedList;

/**
 * Created by Administrator on 2019\7\18 0018.
 *
 * 从左滚动右，先destory再new
   从右滚动左，先new 再destory
   平滑滚动 viewpager默认3个  加LinkedList1个 ，最多会缓存  4个view
    跨页滚动，linkedlist最多缓存4个，一般会为3   最多会缓存  7个view(首尾)通常为6

 */

public abstract  class PageRecycleAdapter extends PagerAdapter {

    private LinkedList<View> mQuestionViewRecycle=new LinkedList<>();       // QuestionView回收栈

    protected View getRecycView(ViewGroup container,int layoutId) {

        // LogUtils.e("getRecycView_instantiateItem",mQuestionViewRecycle.size()+"");
        if (mQuestionViewRecycle.size() > 0) {
            return mQuestionViewRecycle.removeFirst();
        } else {
            View view = LayoutInflater.from(container.getContext()).inflate(layoutId, container, false);
            return view;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      // LogUtils.e("getRecycView_destroyItem_bef",mQuestionViewRecycle.size()+"");
        View rView = (View) object;
        cleanView(rView);
        container.removeView((View) object);
        mQuestionViewRecycle.add(rView);//添加到最后
        //LogUtils.e("getRecycView_destroyItem_aft",mQuestionViewRecycle.size()+"");
    }

    private void cleanView(View rView) {
        ViewGroup mPView = (ViewGroup) rView.getParent();
        if (mPView != null) {
            mPView.removeView(rView);
        }
    }
}
