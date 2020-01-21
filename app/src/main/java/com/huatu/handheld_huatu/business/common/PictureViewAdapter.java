package com.huatu.handheld_huatu.business.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.camera.SimpleViewTargetV2;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.view.photo.RoundProgressView;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 多选图片预览Adapter
 */
public class PictureViewAdapter extends PagerAdapter {

    Context context;
    private ArrayList<PhotoInfo> imgsUrl;
    private final RequestOptions options;

    PictureViewAdapter(Context context, ArrayList<PhotoInfo> imgsUrl) {
        this.context = context;
        this.imgsUrl = imgsUrl;
        options = new RequestOptions()
                .fitCenter()
                .override(DisplayUtil.getScreenWidth(), DisplayUtil.getScreenHeight())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    /**
     * 动态加载数据
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public int getCount() {
        return imgsUrl == null ? 0 : imgsUrl.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * 初始化
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.pic_view_layout, container, false);

        final RoundProgressView progressView = rootView.findViewById(R.id.progress);
        final PhotoView photoView = rootView.findViewById(R.id.photo_view);
        photoView.setEnabled(true);

        SimpleViewTargetV2<Drawable> targetV2 = new SimpleViewTargetV2<Drawable>(photoView) {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                progressView.startAnimation();
            }

            @Override
            public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                progressView.setVisibility(View.GONE);
                photoView.setImageDrawable(drawable);
            }
        };

        String path = imgsUrl.get(position).path;
        if (!StringUtils.isEmpty(path)) {
            if (!path.equals(photoView.getTag(R.id.reuse_cachetag))) {
                photoView.setTag(R.id.reuse_cachetag, path);
                GlideApp.with(context).load(path)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(250))
                        .into(targetV2);
            }
        } else {
            String imgPath = imgsUrl.get(position).uri.getPath();
            if (!imgPath.equals(photoView.getTag(R.id.reuse_cachetag))) {
                photoView.setTag(R.id.reuse_cachetag, imgPath);
                GlideApp.with(context).load(imgsUrl.get(position).uri)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(250))
                        .into(targetV2);
            }
        }
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onOutsidePhotoTap() {
            }
        });
        container.addView(rootView);
        return rootView;
    }

    /**
     * 销毁View
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
