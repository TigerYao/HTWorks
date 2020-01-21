package com.huatu.handheld_huatu.business.me.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.ReceiveInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by acaige on 2018/9/27.
 */

public class RedPacketReceivedInfoAdapter extends RecyclerView.Adapter<RedPacketReceivedInfoAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflate;
    private ArrayList<ReceiveInfo> mData;

    public RedPacketReceivedInfoAdapter(Context context) {
        mContext = context;
        mInflate = LayoutInflater.from(mContext);
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.item_received_red_pocket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RedPacketReceivedInfoAdapter.ViewHolder holder, int position) {
        ReceiveInfo mResult = mData.get(position);
        if (mResult.receivePrice != null) {
            if (mResult.status==2){
                //已提现
                holder.tv_money_num.setText("已提现 "+mResult.receivePrice+"元");
                holder.tv_money_num.setTextColor(ContextCompat.getColor(mContext, R.color.redF3));
            }else if (mResult.status==0){
                //失效过期
                holder.tv_money_num.setText("已过期 "+mResult.receivePrice+"元");
                holder.tv_money_num.setTextColor(ContextCompat.getColor(mContext, R.color.grayC4));
            }else {
                //正常
                holder.tv_money_num.setText(mResult.receivePrice+"元");
                holder.tv_money_num.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
            }
        }
        if (mResult.receiveTime != null) {
            holder.tv_receive_time.setText(mResult.receiveTime);
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<ReceiveInfo> receiveInfo) {
        mData.addAll(receiveInfo);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_money_num)
        TextView tv_money_num;
        @BindView(R.id.tv_receive_time)
        TextView tv_receive_time;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
