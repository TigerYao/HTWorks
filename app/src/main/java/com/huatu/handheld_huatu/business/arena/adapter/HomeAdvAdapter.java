package com.huatu.handheld_huatu.business.arena.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.bitmap.RoundedCorners;
import com.baijiayun.glide.request.RequestOptions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.customview.ArenaProportionImageView;
import com.huatu.handheld_huatu.business.arena.utils.ZtkSchemeTargetStartTo;

import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.mvpmodel.AdvertiseConfig;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;

import rx.subscriptions.CompositeSubscription;

public class HomeAdvAdapter extends RecyclerView.Adapter<HomeAdvAdapter.AdvHolder> {

    private Activity activity;
    private ArrayList<AdvertiseConfig> advList;
    private CompositeSubscription compositeSubscription;
    private int count = 0;
    private RequestOptions options;

    public HomeAdvAdapter(Activity activity, ArrayList<AdvertiseConfig> advList, CompositeSubscription compositeSubscription) {
        this.activity = activity;
        this.advList = advList;
        this.compositeSubscription = compositeSubscription;
        options = new RequestOptions()
                .transforms(new RoundedCorners(50))
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @NonNull
    @Override
    public AdvHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AdvHolder(LayoutInflater.from(activity).inflate(R.layout.item_home_adv, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdvHolder holder, int i) {
        AdvertiseConfig advertiseConfig = advList.get(i);

        String url;
        if (CommonUtils.isPad(activity)) {
            url = !StringUtils.isEmpty(advertiseConfig.params.padImageUrl) ? advertiseConfig.params.padImageUrl : advertiseConfig.params.image;
        } else {
            url = !StringUtils.isEmpty(advertiseConfig.params.image) ? advertiseConfig.params.image : advertiseConfig.params.padImageUrl;
        }

        GlideApp.with(activity)
                .load(url)
                .apply(options)
                .into(holder.ivAdv);
    }

    @Override
    public int getItemCount() {
        return advList != null
                ? ((count == 0 || count > advList.size()) ? advList.size() : count)
                : 0;
    }

    class AdvHolder extends RecyclerView.ViewHolder {

        ArenaProportionImageView ivAdv;

        AdvHolder(View itemView) {
            super(itemView);
            ivAdv = itemView.findViewById(R.id.iv_adv);
            if (CommonUtils.isPad(activity)) {
                ivAdv.setProportion(0.331);
            } else {
                ivAdv.setProportion(0.487);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // AdvertiseConfig advertiseConfig = advList.get(getAdapterPosition());
                    /*if (advertiseConfig.target.equals("ztk://exquisite/small/course")) {
                        ExperienceCourseFragment.launch(activity);
                    } else*/
                    AdvertiseConfig advertiseConfig= ArrayUtils.getAtIndex(advList,getAdapterPosition());
                    if(null!=advertiseConfig) {
                        ZtkSchemeTargetStartTo.startTo(activity, advertiseConfig.params, advertiseConfig.target, false, compositeSubscription);
                    }
                }
            });
        }
    }
}
