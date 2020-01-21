package com.huatu.handheld_huatu.business.arena.adapter;

import android.app.Activity;
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
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.activity.DownloadArenaPaperActivity;
import com.huatu.handheld_huatu.business.arena.downloadpaper.ArenaPaperLocalFileManager;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperFileBean;
import com.huatu.handheld_huatu.business.arena.downloadpaper.listener.ArenaDownloadListener;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class DownloadedArenaPaperAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<ArenaPaperFileBean> datas;
    private CompositeSubscription compositeSubscription;
    private int curSubject;

    public DownloadedArenaPaperAdapter(Context mContext, List<ArenaPaperFileBean> localArenaListData, CompositeSubscription compositeSubscription, int curSubject) {
        this.mContext = mContext;
        this.datas = localArenaListData;
        this.compositeSubscription = compositeSubscription;
        this.curSubject = curSubject;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.arena_downloaded_paper_item, parent, false);
        return new DownHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderX, int position) {
        final DownHolder holder = (DownHolder) holderX;
        final ArenaPaperFileBean bean = datas.get(position);
        if (DownloadArenaPaperActivity.isEdit) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        holder.checkBox.setChecked(bean.isChecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bean.setChecked(isChecked);
            }
        });
        holder.tvTitle.setText(bean.getName());
        holder.tvSize.setText(bean.getSize());
        holder.llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArenaPaperLocalFileManager.newInstance().sharePaper((BaseActivity) mContext, bean.getPaperId());
                holder.swipeItem.close();
            }
        });
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DownloadArenaPaperActivity.isEdit) {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                } else {
                    if (bean.isHasNew()) {
                        CustomConfirmDialog exitConfirmDialog = DialogUtils.createDialog((Activity) mContext, "", "该试卷内容有新修正，可以重新下载最新试卷");
                        exitConfirmDialog.setPositiveButton("重新下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArenaPaperLocalFileManager.newInstance().downloadPaper(compositeSubscription, bean.getPaperId(), bean.getName(), curSubject, new ArenaDownloadListener() {
                                    @Override
                                    public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                                        if (Method.isActivityFinished((Activity) mContext)) {
                                            return;
                                        }
                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((DownloadArenaPaperActivity) mContext).onLoadData();
                                                ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, bean.getPaperId());
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        exitConfirmDialog.setNegativeButton("继续打开", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, bean.getPaperId());
                            }
                        });
                        exitConfirmDialog.setCancelBtnVisibility(true);
                        exitConfirmDialog.show();
                    } else {
                        ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, bean.getPaperId());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    static class DownHolder extends RecyclerView.ViewHolder {

        private SwipeItemLayout swipeItem;

        View rootView;

        CheckBox checkBox;
        TextView tvTitle;
        TextView tvSize;

        LinearLayout llShare;

        DownHolder(View itemView) {
            super(itemView);
            swipeItem = itemView.findViewById(R.id.swipe);
            rootView = itemView.findViewById(R.id.root_view);
            checkBox = itemView.findViewById(R.id.cb_view);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSize = itemView.findViewById(R.id.tv_size);
            llShare = itemView.findViewById(R.id.ll_share);
        }
    }
}
