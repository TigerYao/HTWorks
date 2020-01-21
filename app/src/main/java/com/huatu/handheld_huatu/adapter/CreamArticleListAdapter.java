package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.main.ArticleDetailActivity;
import com.huatu.handheld_huatu.business.other.DetailScrollViewFragment;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleListData;
import com.huatu.handheld_huatu.utils.ImageLoad;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2019/2/20.
 */

public class CreamArticleListAdapter extends RecyclerView.Adapter<CreamArticleListAdapter.ViewHolder> {
    private List<CreamArticleListData> mData = new ArrayList<>();
    private Context mContext;


    public CreamArticleListAdapter(Context context){
        mContext = context;
    }

    @NonNull
    @Override
    public CreamArticleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cream_atricle, null);
        CreamArticleListAdapter.ViewHolder holder = new CreamArticleListAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CreamArticleListAdapter.ViewHolder holder, int position) {
        final CreamArticleListData mResult=mData.get(position);
        if (mResult!=null){
            if (mResult.title!=null){
                holder.tv_article_title.setVisibility(View.VISIBLE);
                holder.tv_article_title.setText(mResult.title);
            }else {
                holder.tv_article_title.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(mResult.img)){
                holder.rl_no_img.setVisibility(View.GONE);
                if (mResult.isTop){
                    holder.tv_top_img.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_top_img.setVisibility(View.GONE);
                }
                if (mResult.click!=0){
                    holder.tv_scan_num_img.setVisibility(View.VISIBLE);
                    holder.tv_scan_num_img.setText(mResult.click+"");
                }else {
                    holder.tv_scan_num_img.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(mResult.typeName)){
                    holder.tv_type_img.setVisibility(View.VISIBLE);
                    holder.tv_type_img.setText(mResult.typeName);
                }else {
                    holder.tv_type.setVisibility(View.GONE);
                }
                holder.iv_img.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(mResult.img)){
                 ImageLoad.load(mContext,mResult.img,holder.iv_img, DiskCacheStrategy.DATA);
                }

               // Glide.with(mContext).load(mResult.img).into(holder.iv_img);
            }else {
                holder.rl_no_img.setVisibility(View.VISIBLE);
                holder.tv_scan_num_img.setVisibility(View.GONE);
                holder.iv_img.setVisibility(View.GONE);
                holder.tv_type_img.setVisibility(View.GONE);
                holder.tv_top_img.setVisibility(View.GONE);

                if (mResult.isTop){
                    holder.tv_top.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_top.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(mResult.typeName)){
                    holder.tv_type.setVisibility(View.VISIBLE);
                    holder.tv_type.setText(mResult.typeName);
                }else {
                    holder.tv_type.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(mResult.addTime)){
                    holder.tv_add_time.setVisibility(View.VISIBLE);
                    holder.tv_add_time.setText(mResult.addTime);
                }else {
                    holder.tv_add_time.setVisibility(View.GONE);
                }

                if (mResult.click!=0){
                    holder.tv_scan_num.setVisibility(View.VISIBLE);
                    holder.tv_scan_num.setText(mResult.click+"");
                }else {
                    holder.tv_scan_num.setVisibility(View.GONE);
                }

                if (mResult.goodPost!=0){
                    holder.tv_like_num.setVisibility(View.VISIBLE);
                    holder.tv_like_num.setText(mResult.goodPost+"");
                }else {
                    holder.tv_like_num.setVisibility(View.GONE);
                }

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //文章详情
//                ArticleDetailActivity.newInstance(mContext,mResult.id);
                    DetailScrollViewFragment.lanuch(mContext,mResult.id);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<CreamArticleListData> listResponse) {
        mData.clear();
        mData.addAll(listResponse);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        ImageView iv_img;
        @BindView(R.id.tv_article_title)
        TextView tv_article_title;
        @BindView(R.id.tv_top_img)
        TextView tv_top_img;
        @BindView(R.id.tv_type_img)
        TextView tv_type_img;
        @BindView(R.id.tv_scan_num_img)
        TextView tv_scan_num_img;
        @BindView(R.id.rl_no_img)
        RelativeLayout rl_no_img;
        @BindView(R.id.tv_top)
        TextView tv_top;
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_add_time)
        TextView tv_add_time;
        @BindView(R.id.tv_like_num)
        TextView tv_like_num;
        @BindView(R.id.tv_scan_num)
        TextView tv_scan_num;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setTag(this);
        }
    }
}
