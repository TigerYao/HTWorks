package com.huatu.handheld_huatu.business.message.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.message.MessageListFragment;
import com.huatu.handheld_huatu.business.message.model.MessageGroupData;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saiyuan on 2019/2/27.
 */

public class MessageGroupAdapter extends RecyclerView.Adapter<MessageGroupAdapter.ViewHolder>{
    private Context mContext;
    private List<MessageGroupData> mData=new ArrayList<>();
    private OnDeleteClickListener mOnDeleteClickListener;

    public MessageGroupAdapter(Context context,OnDeleteClickListener mOnDeleteClickListener) {
        mContext=context;
        this.mOnDeleteClickListener=mOnDeleteClickListener;
    }

    @NonNull
    @Override
    public MessageGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_message_group,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageGroupAdapter.ViewHolder holder, final int position) {
        final MessageGroupData mResult=mData.get(position);
        if (mResult!=null){
            if (!TextUtils.isEmpty(mResult.view)){
                if (mResult.view.equals("course")){
                    holder.iv_message_type.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_message_course));
                }else  if (mResult.view.equals("feedBack")){
                    holder.iv_message_type.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_message_feedback));
                }else  if (mResult.view.equals("logistics")){
                    holder.iv_message_type.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_message_order));
                }else{
                    holder.iv_message_type.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_message_platform));
                }
            }
            if (mResult.count!=0){
                holder.tv_message_num.setVisibility(View.VISIBLE);
                if (mResult.count<100){
                    holder.tv_message_num.setText(mResult.count+"");
                }else {
                    holder.tv_message_num.setText("99+");
                }
            }else {
                holder.tv_message_num.setVisibility(View.GONE);

            }
            if (!TextUtils.isEmpty(mResult.name)){
                holder.tv_type.setText(mResult.name);
            }
            if (!TextUtils.isEmpty(mResult.timeInfo)){
                holder.tv_time.setText(mResult.timeInfo);
            }
            if (!TextUtils.isEmpty(mResult.content)){
                holder.tv_content.setText(mResult.content);
            }
//            holder.lll_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mOnDeleteClickListener!=null){
//                        mOnDeleteClickListener.onDeleteClick(position);
//                    }
//                 holder.swipe_item.close();
//                }
//            });

            holder.root_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //各类消息列表
                    Bundle bundle=new Bundle();
                    bundle.putString("view",mResult.view);
                    UIJumpHelper.jumpFragment(mContext,MessageListFragment.class,bundle);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void clearAndRefresh() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void setData(List<MessageGroupData> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
//        @BindView(R.id.swipe_item)
//        SwipeItemLayout swipe_item;
        @BindView(R.id.iv_message_type)
        ImageView iv_message_type;
        @BindView(R.id.tv_message_num)
        TextView tv_message_num;
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_content)
        TextView tv_content;
//        @BindView(R.id.lll_delete)
//        LinearLayout lll_delete;
        @BindView(R.id.root_view)
        RelativeLayout root_view;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setTag(this);
        }
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
