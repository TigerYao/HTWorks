package com.huatu.handheld_huatu.business.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.ReceiveDetail;
import com.huatu.handheld_huatu.utils.CircleTransform;
import com.huatu.handheld_huatu.utils.ImageLoad;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by acaige on 2018/9/28.
 */

public class RedPacketDetailAdapter extends RecyclerView.Adapter<RedPacketDetailAdapter.ViewHolder> {
    private ArrayList<ReceiveDetail> mData;
    private Context mContext;
    private LayoutInflater mInflate;
    public RedPacketDetailAdapter(Context context) {
        mContext=context;
        mData=new ArrayList<>();
        mInflate=LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflate.inflate(R.layout.item_receive_detail,parent,false);
        return new RedPacketDetailAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReceiveDetail mResult=mData.get(position);
        if (mResult.avatar!=null){
   /*         Glide.with(mContext)
                    .load(mResult.avatar)
                    .transform(new CircleTransform(mContext))
                    .placeholder(R.mipmap.me_default_avater)
                    .error(R.mipmap.me_default_avater)
                    .skipMemoryCache(false)
                    .placeholder(R.mipmap.me_default_avater)
                    .crossFade()
                    .into(holder.iv_avatar);*/

            ImageLoad.displaynoCacheUserAvater(mContext,mResult.avatar,holder.iv_avatar,R.mipmap.me_default_avater);
        }
        if (mResult.nickName!=null){
            holder.tv_weChat_name.setText(mResult.nickName);
        }
        if (mResult.receiveTime!=null){
            holder.tv_received_time.setText(mResult.receiveTime);
        }
        if (mResult.receivePrice!=null){
            holder.tv_received_money.setText(mResult.receivePrice+"元");
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<ReceiveDetail> receiveInfo) {
        mData.addAll(receiveInfo);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView iv_avatar;
         @BindView(R.id.tv_weChat_name)
         TextView tv_weChat_name;
         @BindView(R.id.tv_received_time)
         TextView tv_received_time;
         @BindView(R.id.tv_received_money)
         TextView tv_received_money;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setTag(this);
        }
    }
}
