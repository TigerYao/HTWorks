package com.huatu.handheld_huatu.business.essay.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.essay.bhelper.DownLoadEssayHelper;
import com.huatu.handheld_huatu.mvpmodel.essay.DownloadEssayBean;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.view.swiperecyclerview.swipemenu.SwipeMenuLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chq on 2018/7/20.
 */

public class DownLoadEssayAdapter extends RecyclerView.Adapter<DownLoadEssayAdapter.ViewHolder> {

    private BaseActivity mActivity;
    private List<DownloadEssayBean> mData = new ArrayList<>();

    public DownLoadEssayAdapter(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download_list_layout, parent, false);
        final ViewHolder holder = new DownLoadEssayAdapter.ViewHolder(view);
        holder.swipeMenuLayout.setSwipeEnable(true);
        return holder;
    }

    @Override
    public void onBindViewHolder(final DownLoadEssayAdapter.ViewHolder holder, final int position) {
        if (mData != null && mData.size() != 0) {
            final DownloadEssayBean mResult = mData.get(position);
            if (mResult.type == 1) {
                holder.tvHomework.setVisibility(View.VISIBLE);
            } else {
                holder.tvHomework.setVisibility(View.GONE);
            }
            holder.down_essay_title.setText(mResult.title);
            if (mResult.time != null) {
                String newTime = null;
                String year = mResult.time.substring(0, 4);
                String currentYear = DateUtils.getCurrentYear();
                if (mResult.time.contains("/")) {
                    newTime = DateUtils.changeFormatYMD(mResult.time);
                } else {
                    newTime = mResult.time;
                }
                if (year.equals(currentYear)) {
                    String sTime = newTime.substring(5);
                    holder.down_essay_time.setText(sTime);
                } else {
                    holder.down_essay_time.setText(newTime);
                }
            }
            if (mResult.check != 0) {
                holder.down_essay_check.setText("");
            } else {
                holder.down_essay_check.setText("未批改");
            }

            String varM = "M";
            if (mResult.fileSize == null) {
                mResult.fileSize = "0M";
            } else {
                if (mResult.fileSize.endsWith("M")) {
                    varM = "M";
                } else if (mResult.fileSize.endsWith("KB")) {
                    varM = "KB";
                }
                if (mResult.fileSize.contains(".")) {
                    String[] strAry = mResult.fileSize.split("\\.");
                    if (strAry != null && strAry.length > 1) {
                        if (strAry[1] != null) {
                            mResult.fileSize = strAry[0] + "." + strAry[1].substring(0, 2) + varM;
                        }
                    }
                }
            }
            holder.down_essay_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownLoadEssayHelper.getInstance().deleteDowningSuccessEssay(mResult);
//                        if (getItemCount() >position) {
//                        mData.remove(holder.getAdapterPosition());
//                        notifyItemRemoved(holder.getAdapterPosition());
//                        }
                        }

                    }, null, "确定删除该文件吗？", "取消", "确定");
                }
            });
            holder.smContentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mResult.filepath != null) {
                        if (new File(mResult.filepath).exists()) {
                            FileUtil.readPDF(mActivity, mResult.filepath);
                        } else {
                            Toast.makeText(mActivity, "文件不存在，已经被删除", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mActivity, "文件不存在，已经被删除", Toast.LENGTH_SHORT).show();
                        DownLoadEssayHelper.getInstance().deleteDowningSuccessEssay(mResult);
                    }
                }
            });
        }

    }

    public void setData(ArrayList<DownloadEssayBean> dataList) {
        mData.clear();
        mData.addAll(dataList);
        notifyDataSetChanged();
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.smContentView)
        View smContentView;
        @BindView(R.id.smMenuView)
        View smMenuView;
        @BindView(R.id.down_essay_title)
        TextView down_essay_title;
        @BindView(R.id.down_essay_time)
        TextView down_essay_time;
        @BindView(R.id.tv_homework)
        TextView tvHomework;
        @BindView(R.id.down_essay_check)
        TextView down_essay_check;
        @BindView(R.id.down_essay_delete)
        TextView down_essay_delete;
        SwipeMenuLayout swipeMenuLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeMenuLayout = (SwipeMenuLayout) itemView;
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
