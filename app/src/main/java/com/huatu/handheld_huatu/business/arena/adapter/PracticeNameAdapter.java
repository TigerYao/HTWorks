package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.StringUtils;
import com.huatu.widget.MessageImageView;

import java.util.List;


public class PracticeNameAdapter extends RecyclerView.Adapter<PracticeNameAdapter.MyViewHolder> {

    private Context context;
    private List<HomeIconBean> list;
    private OnItemClickListener itemClickListener;

    public PracticeNameAdapter(Context context, List<HomeIconBean> list, OnItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 找到item的布局
        View view = LayoutInflater.from(context).inflate(R.layout.item_practicename, parent, false);
        return new MyViewHolder(view);      // 将布局设置给holder
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 绑定视图到holder,就如同ListView的getView(),但是这里已经把复用实现了,我们只需要填充数据就行,复用的时候都是调用该方法填充数据
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        HomeIconBean bean = list.get(position);
        // 填充数据
        holder.mTextView.setText(bean.name);
        if (!StringUtils.isEmpty(bean.url)) {
            ImageLoad.load(context, bean.url, holder.mImageView, bean.icon);
        } else {
            holder.mImageView.setImageResource(bean.icon);
        }
        holder.mImageView.setMessageNum(bean.tipNum);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        MessageImageView mImageView;
        TextView mTextView;

        MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.root_view);
            mImageView = itemView.findViewById(R.id.practice_name_image);
            mTextView = itemView.findViewById(R.id.practice_name_text);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
