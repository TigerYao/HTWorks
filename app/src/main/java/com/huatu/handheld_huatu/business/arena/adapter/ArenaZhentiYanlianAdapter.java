package com.huatu.handheld_huatu.business.arena.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
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
import com.huatu.handheld_huatu.business.arena.utils.RealListUpdateClickRecord;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.helper.LoginTrace;
import com.huatu.handheld_huatu.mvpmodel.arena.ExamPagerItem;
import com.huatu.handheld_huatu.mvpmodel.exercise.UserMetaBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class ArenaZhentiYanlianAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<ExamPagerItem.ExamPaperResult> data;
    private CompositeSubscription compositeSubscription;
    private int subject;

    public ArenaZhentiYanlianAdapter(Context context, ArrayList<ExamPagerItem.ExamPaperResult> data, CompositeSubscription compositeSubscription, int subject) {
        this.mContext = context;
        this.data = data;
        this.compositeSubscription = compositeSubscription;
        this.subject = subject;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_exam_paper, parent, false);
        return new RealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderX, int position) {
        final RealViewHolder holder = (RealViewHolder) holderX;
        final ExamPagerItem.ExamPaperResult item = data.get(position);
        item.isDownloadPaper = ArenaPaperLocalFileManager.newInstance().isDownLoadedPaper(item.id);
        if (item.isDownloadPaper) {
            holder.tvDown.setText("已下载PDF");
            ServiceProvider.getArenaPaperInfoList(compositeSubscription, String.valueOf(item.id), new NetResponse() {
                @Override
                public void onListSuccess(BaseListResponseModel model) {
                    if (model.data.size() > 0) {
                        List<ArenaPaperInfoNet> paperInfoNets = model.data;
                        ArenaPaperInfoNet arenaPaperInfoNet = paperInfoNets.get(0);
                        if (arenaPaperInfoNet.getId() == item.id && ArenaPaperLocalFileManager.newInstance().hasNewPaper(item.id, arenaPaperInfoNet.getGmtModify())) {
                            item.hasNew = true;
                        }
                    }
                }
            });
        } else {
            holder.tvDown.setText("下载PDF");
        }
        if (!TextUtils.isEmpty(item.name)) {

            if (item.isNew == 1) {
                int i = RealListUpdateClickRecord.getInstance().getRealListHitRecord(item.id);
                if (i != 1) {
                    SpannableStringBuilder ss = new SpannableStringBuilder(item.name + " ");
                    int len = ss.length();
                    //图片
                    Drawable d = ContextCompat.getDrawable(mContext, (R.mipmap.real_list_new));
                    d.setBounds(DisplayUtil.dp2px(8), DisplayUtil.dp2px(4), d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    //构建ImageSpan
                    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span, len - 1, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    holder.text_name.setText(ss);
                } else {
                    holder.text_name.setText(item.name);
                }
            } else {
                holder.text_name.setText(item.name);
            }
        } else {
            holder.text_name.setText("");
        }
        holder.text_number_time.setText("共" + item.qcount + "题，总时间" + item.time / 60 + "分钟");

        final UserMetaBean userMeta = item.userMeta;
        if (userMeta != null) {
            long currentPracticeId = userMeta.currentPracticeId;
            if (currentPracticeId == -1) {
                holder.rl_flag.setVisibility(View.INVISIBLE);
            } else {
                holder.rl_flag.setVisibility(View.VISIBLE);
            }
        } else {
            holder.rl_flag.setVisibility(View.INVISIBLE);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {

            @LoginTrace(type = 0)
            @Override
            public void onClick(View v) {

                // 为了关闭代码open的swipeItem，而进行拦截操作
                if (openedSwipeItem != null) {
                    openedSwipeItem.close();
                    openedSwipeItem = null;
                    return;
                }

                if (item.isNew == 1) {
                    RealListUpdateClickRecord.getInstance().addRealListHitRecord(item.id, 1);
                    RealListUpdateClickRecord.getInstance().saveRealListHitRecordToFile();
                    notifyDataSetChanged();
                }
                //真题演练  type 3|id 真题试卷id
                // 必添加字段action，常规做题为0 不做题直接看答题报告和解析为1(直接看解析需要增加practiseId属性，即练习/答题卡id)
                //必添加字段freshness，新做一份题为0，继续做题为1(继续做题需要增加practiseId属性，即练习/答题卡id)
                Bundle bundle = new Bundle();
                if (userMeta == null) {
                    //第一次做题
                    bundle.putLong("point_ids", item.id);
                } else if (userMeta != null && userMeta.currentPracticeId == -1) {
                    //存在UserMeta，习题做完，重新做题
                    bundle.putLong("point_ids", item.id);
                } else {
                    //存在userMeta 并且userMeta的getCurrentPracticeId不为-1
                    //表示上一次没有做完试题
                    bundle.putLong("practice_id", userMeta.currentPracticeId);
                    bundle.putBoolean("continue_answer", true);
                }
                ArenaExamActivityNew.show((Activity) mContext, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN, bundle);
            }
        };

        holder.root_view.setOnClickListener(onClickListener);
        holder.text_name.setOnClickListener(onClickListener);

        holder.llDown.setOnClickListener(new View.OnClickListener() {
            @LoginTrace(type = 0)
            @Override
            public void onClick(View v) {

                if (item.id <= 0) {
                    ToastUtils.showShort("试卷Id错误");
                    return;
                }

                if (item.isDownloadPaper) {
                    if (item.hasNew) {
                        CustomConfirmDialog exitConfirmDialog = DialogUtils.createDialog((Activity) mContext, "", "该试卷内容有新修正，可以重新下载最新试卷");
                        exitConfirmDialog.setPositiveButton("重新下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArenaPaperLocalFileManager.newInstance().downloadPaper(compositeSubscription, item.id, item.name, subject, new ArenaDownloadListener() {
                                    @Override
                                    public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                                        ((Activity) mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                item.hasNew = false;
                                                ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, item.id);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        exitConfirmDialog.setNegativeButton("继续打开", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, item.id);
                            }
                        });
                        exitConfirmDialog.setCancelBtnVisibility(true);
                        exitConfirmDialog.show();
                    } else {
                        ArenaPaperLocalFileManager.newInstance().openPaper((BaseActivity) mContext, item.id);
                    }
                } else {
                    ArenaPaperLocalFileManager.newInstance().downloadPaper(compositeSubscription, item.id, item.name, subject, new ArenaDownloadListener() {
                        @Override
                        public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                            if (!Method.isActivityFinished((Activity) mContext)) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        item.isDownloadPaper = true;
                                        if (subject == Type.PB_ExamType.NTEGRATED_APPLICATION) {
                                            //综合应用提示不同
                                            DialogUtils.onShowOnlyConfirmRedDialog((Activity) mContext, "提示", "试卷下载成功。请在科目首页右上角“试卷下载”入口查看下载好的试卷哦。", "我知道啦");
                                        } else {
                                            DialogUtils.onShowOnlyConfirmRedDialog((Activity) mContext, "提示", "试卷下载成功。请在“题库”页面，向左滑动图标，然后点击“试卷下载”图标就可以查看下载好的试卷了哦。", "我知道啦");
                                        }
                                        ArenaZhentiYanlianAdapter.this.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                }
                holder.swipeItem.close();
            }
        });

        // 如果现实了引导，默认打开第一项
        // 行测，事业单位-公基和职测，教师，招警有试卷下载
        if (position == 0 && SpUtils.getDownRealPaperTip()) {
            if (SignUpTypeDataCache.getInstance().getCurSubject() == Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST      // 公务员行测
                    || SignUpTypeDataCache.getInstance().getCurSubject() == Type.PB_ExamType.PUBLIC_BASE                // 事业单位公基
                    || SignUpTypeDataCache.getInstance().getCurSubject() == Type.PB_ExamType.JOB_TEST                   // 事业单位职测
                    || SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.PUBLIC_SECURITY             // 公安招警
                    || SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.TEACHER_T) {                // 教师
                SpUtils.setDownRealPaperTip(false);
                holder.swipeItem.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openedSwipeItem = holder.swipeItem;
                        holder.swipeItem.open();
                    }
                }, 200);
            }
        }
    }

    // 傻叉需求，关闭引导，还得（代码）打开默认第一个Item，然而打开其他，代码打开的，在点击外部的时候，不会自动关闭，所以这里做了个记录操作
    private SwipeItemLayout openedSwipeItem;

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    private class RealViewHolder extends RecyclerView.ViewHolder {

        private SwipeItemLayout swipeItem;

        private View root_view;
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
            text_number_time = itemView.findViewById(R.id.text_number_time);
            rl_flag = itemView.findViewById(R.id.rl_flag);
            llDown = itemView.findViewById(R.id.ll_down);
            tvDown = itemView.findViewById(R.id.tv_down);
        }
    }
}
