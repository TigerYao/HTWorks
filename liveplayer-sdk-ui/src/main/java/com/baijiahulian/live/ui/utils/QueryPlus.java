package com.baijiahulian.live.ui.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;


import com.baijiahulian.live.ui.R;
import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.RequestOptions;

import rx.Observable;

/**
 * 缓存引用，减少findViewById次数
 * Created by Shubo on 2017/3/15.
 */

public class QueryPlus extends Query {

    private SparseArray<View> viewRefCache;
    private static final RequestOptions transbgOptions =
            new RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA).error(R.drawable.trans_bg)
                    .placeholder(R.drawable.trans_bg).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);
    private QueryPlus(View contentView) {
        super(contentView);
        viewRefCache = new SparseArray<>();
    }

    public static QueryPlus with(View contentView) {
        return new QueryPlus(contentView);
    }

    @Override
    public QueryPlus id(int id) {
        View cachedView = viewRefCache.get(id);
        if (cachedView != null) {
            view = cachedView;
        } else {
            super.id(id);
            viewRefCache.put(id, super.view());
        }
        return this;
    }

    public QueryPlus image(Context context, String url) {
        if (view instanceof ImageView) {
            //Picasso.with(context).load(url).into((ImageView) view);

            Glide.with(context).load(url).apply(transbgOptions)
                    .transition(DrawableTransitionOptions.withCrossFade(250)).into((ImageView) view);
        }
        return this;
    }

    public Observable<Void> clicked() {
        return RxUtils.clicks(view);
    }

    public QueryPlus background(int color) {
        view.setBackgroundColor(color);
        return this;
    }

    public QueryPlus backgroundDrawable(Drawable drawable) {
        ViewCompat.setBackground(view, drawable);
        return this;
    }
}
