package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.load.resource.bitmap.RoundedCorners;
import com.baijiayun.glide.request.RequestOptions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.arena.customview.ArenaProportionImageView;
import com.huatu.handheld_huatu.business.lessons.CourseCollectSubsetFragment;
import com.huatu.handheld_huatu.business.lessons.bean.CourseListData;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.SecKillFragment;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.helper.statistic.SensorStatisticHelper;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;

public class ExperienceChildAdapter extends RecyclerView.Adapter<ExperienceChildAdapter.AdvHolder> {

    private Context context;
    private ArrayList<CourseListData> data = new ArrayList<>();
    private RequestOptions options;

    ExperienceChildAdapter(Context context) {
        this.context = context;

        options = new RequestOptions()
                .transforms(new RoundedCorners(60))
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
    }

    public void setDate(ArrayList<CourseListData> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdvHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AdvHolder(LayoutInflater.from(context).inflate(R.layout.item_experience_course, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdvHolder holder, int i) {
        CourseListData item = data.get(i);

        GlideApp.with(context)
                .load(item.img)
//                .apply(options)
                .into(holder.iv_img);

        holder.tv_title.setText(item.title);
        holder.tv_brief.setVisibility(View.VISIBLE);
        if (item.isCollect && !TextUtils.isEmpty(item.collectBrief)) {
            holder.tv_brief.setText(item.collectBrief);
        } else if (!TextUtils.isEmpty(item.brief)) {
            holder.tv_brief.setText(item.brief);
        } else {
            holder.tv_brief.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class AdvHolder extends RecyclerView.ViewHolder {

        ArenaProportionImageView iv_img;
        TextView tv_title;
        TextView tv_brief;

        AdvHolder(View itemView) {
            super(itemView);

            iv_img = itemView.findViewById(R.id.iv_img);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_brief = itemView.findViewById(R.id.tv_brief);

            iv_img.setProportion(0.564);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetUtil.isConnected()) {
                        ToastUtils.showShort("网络错误，请检查您的网络");
                        return;
                    }
                    int position = getAdapterPosition();
                    if (data == null || data.size() == 0 || position >= data.size()) {
                        return;
                    }

                    CourseListData item = data.get(position);

                    // 课程跳转
                    int videoType = 1;
                    if (!TextUtils.isEmpty(item.videoType)) {
                        videoType = Integer.parseInt(item.videoType);
                    }
                    if (item.isCollect) {
                        CourseCollectSubsetFragment.show(context,
                                item.collectId, item.title, item.title, videoType, SensorStatisticHelper.pageSource);
                    } else if (item.secondKill) {
                        BaseFrgContainerActivity.newInstance(context,
                                SecKillFragment.class.getName(),
                                SecKillFragment.getArgs(item.classId, item.title, false));
                    } else {
                        int collageActiveId = 0;
                        if (!TextUtils.isEmpty(item.collageActiveId)) {
                            collageActiveId = Integer.parseInt(item.collageActiveId);
                        }
                        Intent intent = new Intent(context, BaseIntroActivity.class);
                        intent.putExtra("rid", item.classId);
                        intent.putExtra("NetClassId", item.classId);//lesson.NetClassId
                        intent.putExtra("course_type", videoType);
                        intent.putExtra("price", item.actualPrice);
                        intent.putExtra("originalprice", item.price);
                        intent.putExtra("collageActiveId", collageActiveId);
                        intent.putExtra("saleout", item.isSaleOut);
                        intent.putExtra("rushout", item.isRushOut);
                        intent.putExtra("daishou", item.isTermined);
                        intent.putExtra("from", SensorStatisticHelper.pageSource);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
