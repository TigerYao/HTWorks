package com.huatu.handheld_huatu.business.matches.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.downloadpaper.ArenaPaperLocalFileManager;
import com.huatu.handheld_huatu.business.arena.downloadpaper.bean.ArenaPaperInfoNet;
import com.huatu.handheld_huatu.business.arena.downloadpaper.listener.ArenaDownloadListener;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.matches.bean.ScHistoryPaperListResult;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by chq on 2019/1/23.
 */

public class NewScArenaPastAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<ScHistoryPaperListResult> data;
    private CompositeSubscription compositeSubscription;
    private int curSubject;

    public NewScArenaPastAdapter(Context context, ArrayList<ScHistoryPaperListResult> data, CompositeSubscription compositeSubscription, int curSubject) {
        this.mContext = context;
        this.data = data;
        this.compositeSubscription = compositeSubscription;
        this.curSubject = curSubject;
    }

    public void setCurSubject(int curSubject) {
        this.curSubject = curSubject;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_new_exam_paper_list, parent, false);
        return new RealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderX, int position) {
        final RealViewHolder holder = (RealViewHolder) holderX;
        final ScHistoryPaperListResult item = data.get(position);
        if (!TextUtils.isEmpty(item.name)) {
            holder.text_name.setText(item.name);
        } else {
            holder.text_name.setText("");
        }
        if (item.answerCount == 0) {
            holder.text_number_time.setVisibility(View.GONE);
        } else {
            holder.text_number_time.setVisibility(View.VISIBLE);
            holder.text_number_time.setText(item.answerCount + "人已考");
        }
        if (item.completeCount > 0) {
            //我的作答次数
            holder.rl_flag.setVisibility(View.VISIBLE);
            holder.rl_flag.setText("完成" + item.completeCount + "次");
            holder.rl_flag.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
            holder.rl_flag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_new_paper_complete));
        } else if (item.answerStatus == 0) {
            //继续答题，未完成
            holder.rl_flag.setVisibility(View.VISIBLE);
            holder.rl_flag.setText("未完成");
            holder.rl_flag.setTextColor(ContextCompat.getColor(mContext, R.color.red250));
            holder.rl_flag.setBackground(ContextCompat.getDrawable(mContext, R.drawable.sc_new_paper));
        } else {
            //没做过
            holder.rl_flag.setText("");
            holder.rl_flag.setVisibility(View.GONE);
        }

        //名师解析
        if (item.courseId == 0) {
            holder.iv_paper_course.setVisibility(View.GONE);
        } else {
            holder.iv_paper_course.setVisibility(View.VISIBLE);
        }
        holder.iv_paper_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击名师解析，跳转售前
                BaseIntroActivity.newIntent(mContext, item.courseId+"");
            }
        });

        holder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (item.answerStatus == 1) {
                    //开始答题
                    bundle.putLong("point_ids", item.matchId);
                } else {
                    //表示上一次没有做完试题，继续答题
                    bundle.putLong("practice_id", item.practiceId);
                    bundle.putBoolean("continue_answer", true);
                }
                bundle.putBoolean("from_ScRecordFragment", true);
                ArenaExamActivityNew.show((Activity) mContext, ArenaConstant.EXAM_ENTER_FORM_TYPE_PAST_MOKAO, bundle);
            }
        });
        item.isDownLoaded = ArenaPaperLocalFileManager.newInstance().isDownLoadedPaper(item.matchId);
        if (item.isDownLoaded) {
            holder.tvDown.setText("已下载PDF");
            ServiceProvider.getArenaPaperInfoList(compositeSubscription, String.valueOf(item.matchId), new NetResponse() {
                @Override
                public void onListSuccess(BaseListResponseModel model) {
                    if (model.data.size() > 0) {
                        List<ArenaPaperInfoNet> paperInfoNets = model.data;
                        ArenaPaperInfoNet arenaPaperInfoNet = paperInfoNets.get(0);
                        if (arenaPaperInfoNet.getId() == item.matchId && ArenaPaperLocalFileManager.newInstance().hasNewPaper(item.matchId, arenaPaperInfoNet.getGmtModify())) {
                            item.hasNew = true;
                        }
                    }
                }
            });
        } else {
            holder.tvDown.setText("下载PDF");
        }

        holder.llDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (item.matchId <= 0) {
                    ToastUtils.showShort("试卷Id错误");
                    return;
                }

                if (item.isDownLoaded) {
                    if (item.hasNew) {
                        CustomConfirmDialog exitConfirmDialog = DialogUtils.createDialog((Activity) mContext, "", "该试卷内容有新修正，可以重新下载最新试卷");
                        exitConfirmDialog.setPositiveButton("重新下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArenaPaperLocalFileManager.newInstance().downloadPaper(compositeSubscription, item.matchId, item.name, curSubject, new ArenaDownloadListener() {
                                    @Override
                                    public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                item.hasNew = false;
                                                ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, item.matchId);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        exitConfirmDialog.setNegativeButton("继续打开", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, item.matchId);
                            }
                        });
                        exitConfirmDialog.setCancelBtnVisibility(true);
                        exitConfirmDialog.show();
                    } else {
                        ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, item.matchId);
                    }
                } else {
                    ArenaPaperLocalFileManager.newInstance().downloadPaper(compositeSubscription, item.matchId, item.name, curSubject, new ArenaDownloadListener() {
                        @Override
                        public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                            if (!Method.isActivityFinished((Activity) mContext)) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DialogUtils.onShowOnlyConfirmRedDialog((Activity) mContext, "提示", "试卷下载成功。请在“题库”页面，向左滑动图标，然后点击“试卷下载”图标就可以查看下载好的试卷了哦。", "我知道啦");
                                        item.isDownLoaded = true;
                                        NewScArenaPastAdapter.this.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                }
                holder.swipeItem.close();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class RealViewHolder extends RecyclerView.ViewHolder {

        private SwipeItemLayout swipeItem;

        private View root_view;
        private TextView iv_paper_course;
        private TextView text_name;
        private TextView text_number_time;
        private TextView rl_flag;

        private LinearLayout llDown;
        private TextView tvDown;

        private RealViewHolder(View itemView) {
            super(itemView);
            swipeItem = itemView.findViewById(R.id.swipe);
            root_view = itemView.findViewById(R.id.root_view);
            text_name = itemView.findViewById(R.id.text_name);
            iv_paper_course = itemView.findViewById(R.id.iv_paper_course);
            text_number_time = itemView.findViewById(R.id.text_number_time);
            rl_flag = itemView.findViewById(R.id.rl_flag);
            llDown = itemView.findViewById(R.id.ll_down);
            tvDown = itemView.findViewById(R.id.tv_down);
        }
    }
}
