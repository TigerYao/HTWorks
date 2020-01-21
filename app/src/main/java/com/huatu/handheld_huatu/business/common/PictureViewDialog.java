package com.huatu.handheld_huatu.business.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.resource.gif.GifDrawable;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.ImageViewTarget;
import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.PageRecycleAdapter;
import com.huatu.handheld_huatu.view.photo.DragPhotoView;
import com.huatu.handheld_huatu.view.photo.PictureData;
import com.huatu.handheld_huatu.view.photo.RoundProgressView;
import com.huatu.handheld_huatu.view.photo.TransitionImageView;
import com.huatu.widget.HackyViewPager;
import com.tencent.tinker.android.utils.SparseIntArray;

import java.util.ArrayList;

/**
 * Created by Administrator on 2019\8\1 0001.
 */

public class PictureViewDialog  extends DialogFragment implements DialogInterface.OnShowListener,DragPhotoView.onDragCloseListener {


    public ArrayList<PictureData> mPicListdata ;
    public int mShowIndex;
    boolean inAnima = true;
    private Drawable mFirstShowDrawable;
    boolean finishAnimation = false;

    public void setPictureData(ArrayList<PictureData> pictureData,int showIndex,Drawable firstShowDrawable){
        mPicListdata=pictureData;
        mShowIndex=showIndex;
        mFirstShowDrawable=firstShowDrawable;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    View mRootView;
    HackyViewPager mViewPager;


    @Override
    public void onDragClosed(){
        this.dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_picture_view, null);
        mViewPager=view.findViewById(R.id.view_pager);

        Dialog dialog = new Dialog(getActivity(), R.style.NoDimAnimThemeDialogPopup );//R.style.NoDimAnimThemeDialogPopup
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        // dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setOnShowListener(this);
        //dialog.setOnShowListener(this);

        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
      /*  WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =DensityUtils.dp2px(mContext,60);// 0;
        dialogWindow.setAttributes(lp);*/

        mRootView=view;
        return dialog;
    }

    DragPicturePageAdapter mPageAdapter;
    @Override
    public void onShow(DialogInterface dialog){

        mPageAdapter = new DragPicturePageAdapter(mPicListdata);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setCurrentItem(mShowIndex, false);
    }


    class DragPicturePageAdapter extends PageRecycleAdapter {
        //  SparseArray<View> map = new SparseArray<>();

        public SparseIntArray mMapload=new SparseIntArray();
        private ArrayList<PictureData> data;
        private View currentView;
        private boolean isFirst=true;

        public DragPicturePageAdapter(ArrayList<PictureData> piclist){
            data=piclist;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            currentView = (View) object;
        }

        public View getPrimaryItem() {
            return currentView;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = getRecycView(container, R.layout.layout_picture_page);
            int hasLoad= mMapload.get(position);//是否已加载 0 未加载 , 1已加载 2,多次加载
            final PictureData pictureData = data.get(position);

            // final Context context = container.getContext();
            // view = LayoutInflater.from(context).inflate(R.layout.layout_picture_page, container, false);
            final TransitionImageView photo = view.findViewById(R.id.photoView);
            final DragPhotoView bigPhoto = view.findViewById(R.id.bigPhotoView);
            bigPhoto.setOnDragCloseListener(PictureViewDialog.this);
            final RoundProgressView loading = view.findViewById(R.id.loading);
            bigPhoto.setTransitionImageView(photo);
            bigPhoto.setVisibility(View.GONE);

            //photo.reset();

            if (isFirst && position == mShowIndex) {
                isFirst = false;
                photo.setInitData(pictureData, mFirstShowDrawable);

                photo.setEnableInAnima(inAnima);
                photo.setEnterAnimationListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finishAnimation = true;
                        loading.setVisibility(View.VISIBLE);
                        loading.startAnimation();
                        inAnima = false;

                        Glide.with(PictureViewDialog.this).asDrawable()
                                // .thumbnail(.2f)
                                .load(pictureData.originalUrl)
                                .apply(RequestOptions.placeholderOf(R.drawable.trans_bg).skipMemoryCache(true))
                                .into(new ImageViewTarget<Drawable>(bigPhoto) {
                                    @Override
                                    protected  void setResource(@Nullable Drawable resource){

                                    }

                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        // hide loading
                                        loading.endAnimation();
                                        bigPhoto.setVisibility(View.VISIBLE);
                                        photo.setVisibility(View.INVISIBLE);
                                        bigPhoto.setImageDrawable(resource);
                                        if (resource instanceof GifDrawable) {
                                            ((GifDrawable) resource).start();
                                        }
                                    }

                                });
                       /* Glide.with(context).asDrawable()
                                // .thumbnail(.2f)
                                .load(pictureData.originalUrl)
                                .apply(RequestOptions.placeholderOf(R.drawable.trans_bg).skipMemoryCache(true))
                                .into(new SimpleViewTarget<Drawable>(bigPhoto) {

                              *//*      @Override
                                    protected  void setResource(@Nullable Drawable resource){
                                        view.setImageDrawable(resource);
                                    }*//*

                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                        // hide loading
                                        loading.endAnimation();
                                        bigPhoto.setVisibility(View.VISIBLE);
                                        photo.setVisibility(View.INVISIBLE);
                                        bigPhoto.setImageDrawable(resource);
                                        if (resource instanceof GifDrawable) {
                                            ((GifDrawable) resource).start();
                                        }
                                    }

                                });*/
                    }
                });
                mMapload.put(position,1);
            } else {
                view.setBackgroundColor(Color.BLACK);
                photo.setInitData(pictureData, null);

                if (hasLoad == 0){
                    loading.setVisibility(View.VISIBLE);
                    loading.startAnimation();
                    final Context context = photo.getContext();
                   /* Glide.with(context).asDrawable().load(pictureData.originalUrl)
                            .apply(RequestOptions.placeholderOf(R.drawable.trans_bg).skipMemoryCache(true))
                            .into(new SimpleViewTarget<Drawable>(bigPhoto) {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    // hide loading
                                    loading.endAnimation();
                                    bigPhoto.setVisibility(View.VISIBLE);
                                    photo.setVisibility(View.INVISIBLE);
                                    bigPhoto.setImageDrawable(resource);
                                    if (resource instanceof GifDrawable) {
                                        ((GifDrawable) resource).start();
                                    }
                                }
                            });*/
                    mMapload.put(position,1);
                }else {

                    Glide.with(bigPhoto.getContext()).asDrawable().load(pictureData.originalUrl)
                            .apply(RequestOptions.placeholderOf(R.drawable.trans_bg).skipMemoryCache(true))
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    // hide loading
                                    bigPhoto.setVisibility(View.VISIBLE);
                                    bigPhoto.setImageDrawable(resource);
                                    if (resource instanceof GifDrawable) {
                                        ((GifDrawable) resource).start();
                                    }
                                }
                            });
                    mMapload.put(position,2);
                }
            }

            //
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container,position,object);
        }
    }
}
