package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecyclerViewItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xing on 2018/4/12.
 */

public class GuidePageAdapter  extends PagerAdapter {
    private List<View> pageViews;
    private int[] guideIds = new int[]{R.mipmap.guide_01, R.mipmap.guide_02, R.mipmap.guide_03,R.mipmap.guide_04,R.mipmap.guide_05};

    private OnRecyclerViewItemClickListener mViewItemClickListener;
    public GuidePageAdapter(Context context,OnRecyclerViewItemClickListener itemClickListener){
        pageViews=new ArrayList<>();
        mViewItemClickListener=itemClickListener;
        for(int i=0;i<guideIds.length-1;i++){
            ImageView tmp1=new ImageView(context);
            tmp1.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //tmp1.setImageResource(Resids[i]);
            pageViews.add(tmp1);
        }
        View lastView=View.inflate(context,R.layout.guide_last_layout,null);
        lastView.findViewById(R.id.quickin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(null!=mViewItemClickListener) mViewItemClickListener.onItemClick(EventConstant.EVENT_ALL,0);
            }
        });
        pageViews.add(lastView);
    }

    @Override
    public int getCount() {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        // TODO Auto-generated method stub
        ((ViewPager) arg0).removeView(pageViews.get(arg1));
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        // TODO Auto-generated method stub
        ((ViewPager) arg0).addView(pageViews.get(arg1),new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(arg1<getCount()-1){
            ((ImageView)pageViews.get(arg1)).setImageResource(guideIds[arg1]);
        }
        return pageViews.get(arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public Parcelable saveState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void startUpdate(ViewGroup arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void finishUpdate(ViewGroup arg0) {
        // TODO Auto-generated method stub

    }
}