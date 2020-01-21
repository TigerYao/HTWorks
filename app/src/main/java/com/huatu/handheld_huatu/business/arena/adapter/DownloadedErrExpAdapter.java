package com.huatu.handheld_huatu.business.arena.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.activity.DownloadArenaPaperActivity;
import com.huatu.handheld_huatu.business.arena.downloaderror.bean.ErrExpListBean;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.List;

public class DownloadedErrExpAdapter extends RecyclerView.Adapter<DownloadedErrExpAdapter.ErrExpHolder> {

    private Context mContext;
    private List<ErrExpListBean.ErrExpBean> datas;
    private OnItemClickListener listener;

    public DownloadedErrExpAdapter(Context mContext, List<ErrExpListBean.ErrExpBean> localArenaListData, OnItemClickListener listener) {
        this.mContext = mContext;
        this.datas = localArenaListData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ErrExpHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.arena_downloaded_err_exp_item, parent, false);
        return new ErrExpHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ErrExpHolder holder, int position) {
        ErrExpListBean.ErrExpBean bean = datas.get(position);
        if (DownloadArenaPaperActivity.isEdit) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        if (bean.isNew == 1) {
            holder.viewNew.setVisibility(View.VISIBLE);
        } else {
            holder.viewNew.setVisibility(View.INVISIBLE);
        }
        holder.checkBox.setChecked(bean.isChecked);
        holder.tvTitle.setText(bean.name);
        if (bean.fileSize == 0) {
            holder.tvSize.setVisibility(View.GONE);
        } else {
            holder.tvSize.setVisibility(View.VISIBLE);
            holder.tvSize.setText(CommonUtils.FormatFileSize(bean.fileSize));
        }
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    class ErrExpHolder extends RecyclerView.ViewHolder {

        SwipeItemLayout swipeItem;

        View rootView;

        CheckBox checkBox;
        View viewNew;
        TextView tvTitle;
        TextView tvSize;

        LinearLayout llShare;

        ErrExpHolder(View itemView) {
            super(itemView);
            swipeItem = itemView.findViewById(R.id.swipe);
            rootView = itemView.findViewById(R.id.root_view);
            checkBox = itemView.findViewById(R.id.cb_view);
            viewNew = itemView.findViewById(R.id.view_new);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSize = itemView.findViewById(R.id.tv_size);
            llShare = itemView.findViewById(R.id.ll_share);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    datas.get(getAdapterPosition()).isChecked = isChecked;
                }
            });

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (swipeItem != null) {
                        swipeItem.close();
                    }
                    if (listener != null) {
                        listener.onClick(getAdapterPosition());
                    }
                }
            });

            llShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (swipeItem != null) {
                        swipeItem.close();
                    }
                    if (listener != null) {
                        listener.onShare(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onShare(int position);
    }
}
