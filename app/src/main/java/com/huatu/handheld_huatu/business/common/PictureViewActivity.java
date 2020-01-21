package com.huatu.handheld_huatu.business.common;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.resource.gif.GifDrawable;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.target.SimpleTarget;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.adapter.PageRecycleAdapter;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.view.photo.DragPhotoView;
import com.huatu.handheld_huatu.view.photo.PictureData;
import com.huatu.handheld_huatu.view.photo.RoundProgressView;
import com.huatu.handheld_huatu.view.photo.TransitionImageView;
import com.huatu.utils.ArrayUtils;
import com.huatu.widget.HackyViewPager;
import com.tencent.tinker.android.utils.SparseIntArray;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;

public class PictureViewActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    HackyViewPager viewPager;
    boolean inAnima = true;
    boolean finishAnimation = false;

    private ArrayList<PhotoInfo> photos;
    private int showIndex;

    DragPicturePageAdapter mPageAdapter;

    public static void show(Context context, ArrayList<PhotoInfo> photos, int showIndex) {
        Intent intent = new Intent(context, PictureViewActivity.class);
        intent.putParcelableArrayListExtra("photos", photos);
        intent.putExtra("showIndex", showIndex);
        context.startActivity(intent);
    }

    public static void startRemotePic(Context context, ArrayList<PictureData> data, int index) {
        Intent starter = new Intent(context, PictureViewActivity.class);
        starter.putParcelableArrayListExtra("data", data);
        starter.putExtra("index", index);
        starter.putExtra(ArgConstant.TYPE, false);
        context.startActivity(starter);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(color);
        }
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_picture_view;
    }


    private boolean mIsLocalPic = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getIntent().getBooleanExtra(ArgConstant.TYPE, true)) {
            mIsLocalPic = true;
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            getWindow().setBackgroundDrawableResource(R.color.black);
        }
        super.onCreate(savedInstanceState);
        if (!mIsLocalPic) {

            setStatusBarColor(Color.TRANSPARENT);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
            ArrayList<PictureData> data = getIntent().getParcelableArrayListExtra("data");
            int index = getIntent().getIntExtra("index", 0);

            mPageAdapter = new DragPicturePageAdapter(data);
            viewPager.setAdapter(mPageAdapter);
            viewPager.setCurrentItem(index, false);
        }
    }

    @Override
    protected void onInitView() {
        if (mIsLocalPic) {
            photos = originIntent.getParcelableArrayListExtra("photos");
            showIndex = originIntent.getIntExtra("showIndex", 0);

            PictureViewAdapter adapter = new PictureViewAdapter(PictureViewActivity.this, photos);
            viewPager.setBackgroundColor(Color.BLACK);
            viewPager.setAdapter(adapter);

            if (showIndex >= photos.size() && (!ArrayUtils.isEmpty(photos))) {
                showIndex = photos.size() - 1;
            }
            viewPager.setCurrentItem(showIndex);
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PictureViewActivity.this.finish();
                }
            });
        }
    }

    @Override
    public boolean canTransStatusbar() {
        return true;
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }


    @Override
    public void onBackPressed() {
        if (mIsLocalPic) {
            super.onBackPressed();
            return;
        }
        if (!finishAnimation) {
            PictureViewActivity.super.finish();
            overridePendingTransition(0, 0);
            return;
        }
        View view = mPageAdapter.getPrimaryItem();
        int curStatus = mPageAdapter.mMapload.get(viewPager.getCurrentItem());
        TransitionImageView photoView = view.findViewById(R.id.photoView);
        final DragPhotoView bigPhoto = view.findViewById(R.id.bigPhotoView);

        if (curStatus == 2) {
            Drawable curDrawable = bigPhoto.getDrawable();
            if (curDrawable != null)
                photoView.setRemoteDrawable(curDrawable, false);
        }
        bigPhoto.setVisibility(View.INVISIBLE);
        final View loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.GONE);
        photoView.setVisibility(View.VISIBLE);
        photoView.runFinishAnimation(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                PictureViewActivity.super.finish();
                overridePendingTransition(0, 0);
            }
        });
    }


    class DragPicturePageAdapter extends PageRecycleAdapter {
        //  SparseArray<View> map = new SparseArray<>();

        public SparseIntArray mMapload = new SparseIntArray();
        private ArrayList<PictureData> data;
        private View currentView;

        public DragPicturePageAdapter(ArrayList<PictureData> piclist) {
            data = piclist;
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
            int hasLoad = mMapload.get(position);//是否已加载 0 未加载 , 1已加载 2,多次加载
            final PictureData pictureData = data.get(position);
            if (hasLoad == 0) {
                // final Context context = container.getContext();
                // view = LayoutInflater.from(context).inflate(R.layout.layout_picture_page, container, false);
                final TransitionImageView photo = view.findViewById(R.id.photoView);
                final DragPhotoView bigPhoto = view.findViewById(R.id.bigPhotoView);
                final RoundProgressView loading = view.findViewById(R.id.loading);
                bigPhoto.setTransitionImageView(photo);
                bigPhoto.setVisibility(View.GONE);

                photo.reset();
                photo.setInitData(pictureData, null);
                photo.setEnableInAnima(inAnima);
                photo.setEnterAnimationListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        finishAnimation = true;
                        loading.setVisibility(View.VISIBLE);
                        loading.startAnimation();
                        inAnima = false;
                        final Context context = photo.getContext();
                        Glide.with(context)
                                .asDrawable()
                                .thumbnail(.2f)
                                .load(pictureData.originalUrl)
                                .apply(RequestOptions.placeholderOf(R.drawable.trans_bg).skipMemoryCache(true))
                                .into(new SimpleTarget<Drawable>() {
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
                    }
                });
                // map.put(position, view);
                mMapload.put(position, 1);
            } else {
                final DragPhotoView bigPhoto = view.findViewById(R.id.bigPhotoView);
                final TransitionImageView photo = view.findViewById(R.id.photoView);

                photo.setVisibility(View.INVISIBLE);
                photo.setInitData(pictureData, null);
                photo.setEnterAnimationListener(null);
                Glide.with(bigPhoto.getContext())
                        .asDrawable()
                        .thumbnail(.2f)
                        .load(pictureData.originalUrl)
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
                mMapload.put(position, 2);//此时，缩略图不会重新加载
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }
}
