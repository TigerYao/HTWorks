package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;

import java.util.List;

/**
 * Created by ht-djd on 2017/10/16.
 */

public class GalleryEvaluateAdapter extends
        RecyclerView.Adapter<GalleryEvaluateAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<Integer> mDatas;

    public GalleryEvaluateAdapter(Context context, List<Integer> datats)
    {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }

        ImageView iv_teacher;
        TextView tv_title;
        TextView tv_teachername;
        ImageView iv_star1;
        ImageView iv_star2;
        ImageView iv_star3;
        ImageView iv_star4;
        ImageView iv_star5;
        TextView tv_teacherfengshu;
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.item_courseevaluate_activity,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.iv_teacher = (ImageView) view
                .findViewById(R.id.iv_teacher);
        viewHolder.iv_star1 = (ImageView) view
                .findViewById(R.id.iv_star1);
        viewHolder.iv_star2 = (ImageView) view
                .findViewById(R.id.iv_star2);
        viewHolder.iv_star3 = (ImageView) view
                .findViewById(R.id.iv_star3);
        viewHolder.iv_star4 = (ImageView) view
                .findViewById(R.id.iv_star4);
        viewHolder.iv_star5 = (ImageView) view
                .findViewById(R.id.iv_star5);
        viewHolder.tv_title = (TextView) view
                .findViewById(R.id.tv_title);
        viewHolder.tv_teachername = (TextView) view
                .findViewById(R.id.tv_teachername);
        viewHolder.tv_teacherfengshu = (TextView) view
                .findViewById(R.id.tv_teacherfengshu);
        return viewHolder;
    }
    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position)
    {
        viewHolder.iv_teacher.setImageResource(mDatas.get(position));
        viewHolder.tv_teachername.setText("张无忌");
        viewHolder.tv_title.setText("倚天屠龙记");
        viewHolder.tv_teacherfengshu.setText("9.8分");

    }
}
