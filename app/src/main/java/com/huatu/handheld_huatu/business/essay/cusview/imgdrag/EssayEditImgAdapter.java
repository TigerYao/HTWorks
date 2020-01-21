package com.huatu.handheld_huatu.business.essay.cusview.imgdrag;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.bitmap.CenterCrop;
import com.baijiayun.glide.load.resource.bitmap.RoundedCorners;
import com.baijiayun.glide.load.resource.drawable.DrawableTransitionOptions;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.camera.SimpleViewTargetV2;
import com.huatu.handheld_huatu.business.matchsmall.customview.ViewCircleBar;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.view.photo.RoundProgressView;
import com.huatu.utils.StringUtils;

import java.util.List;

/**
 * 图片显示Adapter
 */
public class EssayEditImgAdapter extends RecyclerView.Adapter<EssayEditImgAdapter.MyViewHolder> {

    private List<AnswerImage> mDatas;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private final RequestOptions options;

    public EssayEditImgAdapter(Context context, List<AnswerImage> datas) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        options = new RequestOptions()
                .transforms(new CenterCrop(), new RoundedCorners(20))
                .skipMemoryCache(false)
                .override(DisplayUtil.getScreenWidth() / 3, (int) (DisplayUtil.getScreenWidth() / 3 * 1.34))
                .diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_img_drag, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (position >= 9) {            // 图片已选完时，隐藏添加按钮
            holder.imageView.setVisibility(View.GONE);
            holder.tvCount.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);

            AnswerImage answerImage = mDatas.get(position);

            if (position == mDatas.size() - 1) {
                holder.tvCount.setVisibility(View.VISIBLE);
                holder.tvCount.setText(position + "/9");
            } else {
                holder.tvCount.setVisibility(View.GONE);
            }

            String url = StringUtils.isEmpty(answerImage.originPath) ? answerImage.imageUrl : answerImage.originPath;

            if (!url.equals(holder.imageView.getTag(R.id.reuse_cachetag))) {
                holder.imageView.setTag(R.id.reuse_cachetag, url);
                GlideApp.with(mContext)
                        .load(url)
                        .apply(options)
                        .transition(DrawableTransitionOptions.withCrossFade(250))
                        .into(holder.targetV2);
            }

            holder.viewBg.setVisibility(View.GONE);
            holder.llProgress.setVisibility(View.GONE);
            holder.tvProgress.setVisibility(View.GONE);
            holder.llFail.setVisibility(View.GONE);

            if (position < mDatas.size() - 1) {
                if (StringUtils.isEmpty(answerImage.content)) {
                    if (answerImage.upState == 3) {
                        holder.viewBg.setVisibility(View.VISIBLE);
                        holder.llProgress.setVisibility(View.VISIBLE);
                        holder.tvProgress.setVisibility(View.VISIBLE);
                        holder.circleBar.setData((float) answerImage.progress / 100f);
                        holder.tvProgress.setText(answerImage.progress + "%");
                    } else if (answerImage.upState == -4) {
                        holder.viewBg.setVisibility(View.VISIBLE);
                        holder.llFail.setVisibility(View.VISIBLE);
                        holder.llProgress.setVisibility(View.GONE);
                        holder.tvProgress.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        RoundProgressView progressView;
        ImageView imageView;
        TextView tvCount;
        View viewBg;
        LinearLayout llProgress;
        ViewCircleBar circleBar;
        TextView tvProgress;
        LinearLayout llFail;

        SimpleViewTargetV2<Drawable> targetV2;

        MyViewHolder(View itemView) {
            super(itemView);
            progressView = itemView.findViewById(R.id.progress);
            imageView = itemView.findViewById(R.id.sdv);
            tvCount = itemView.findViewById(R.id.tv_count);
            viewBg = itemView.findViewById(R.id.view_bg);
            llProgress = itemView.findViewById(R.id.ll_progress);
            circleBar = itemView.findViewById(R.id.circle);
            circleBar.setColorBg(DisplayUtil.dp2px(4), Color.parseColor("#00000000"));
            tvProgress = itemView.findViewById(R.id.tv_progress);
            llFail = itemView.findViewById(R.id.ll_fail);

            targetV2 = new SimpleViewTargetV2<Drawable>(imageView) {
                @Override
                public void onLoadStarted(@Nullable Drawable placeholder) {
                    progressView.startAnimation();
                }

                @Override
                public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                    progressView.setVisibility(View.GONE);
                    imageView.setImageDrawable(drawable);
                }
            };
        }
    }
}
