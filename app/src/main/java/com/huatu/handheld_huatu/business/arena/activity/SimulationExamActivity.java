package com.huatu.handheld_huatu.business.arena.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseListViewActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.matches.activity.ScReportActivity;
import com.huatu.handheld_huatu.business.matches.activity.SimulationContestActivityNew;
import com.huatu.handheld_huatu.business.matches.bean.GiftDescribeBean;
import com.huatu.handheld_huatu.business.matches.customview.ShakeImageView;
import com.huatu.handheld_huatu.business.me.RecordActivity;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.event.ArenaExamMessageEvent;
import com.huatu.handheld_huatu.mvpmodel.exercise.SimulationListItem;
import com.huatu.handheld_huatu.mvpmodel.exercise.UserMetaBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.MultiItemTypeSupport;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.utils.StringUtils;
import com.netease.hearttouch.router.HTRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * Created by saiyuan on 2016/12/16.
 * 专项模考/精准估分
 */
@HTRouter(url = {"ztk://estimatePaper/home","ztk://simulatePaper/home"},  needLogin = false)
public class SimulationExamActivity extends BaseListViewActivity {

    @BindView(R.id.rl_bottom_gift)
    RelativeLayout rlGift;                          // 下边的弹窗
    @BindView(R.id.iv_gift_close)
    ImageView ivClose;                              // 关闭弹窗
    @BindView(R.id.tv_bottom_gift)
    TextView tvGift;                                // 弹窗内容
    @BindView(R.id.iv_bottom_gift)
    ImageView ivGift;                               // 左边的图标

    private int currentPage = 1;
    private int requestType;
    private Bundle extraArgs = null;
    private String reqType;
    private String TAG = "SimulationExamActivity";
    private boolean mToHome;
    private GiftDescribeBean giftDescribeBean;
    private int subject;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_list_view_gufen_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventUpdate(ArenaExamMessageEvent event) {
        if (event == null || event.type <= 0) {
            return;
        }
        if (event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_COMMIT_PAPER_SUCCESS || event.type == ArenaExamMessageEvent.ARENA_MESSAGE_TYPE_EXAM_SAVE_PAPER_SUCCESS) {
            // 行测交卷、保存成功
            onRefresh();
        }
    }

    @Override
    protected void onInitView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (originIntent != null) {

            Uri uri = originIntent.getData();
            if (uri != null) {
                try {
                    requestType=  "estimatePaper".equals(uri.getHost())?ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN
                                                                       :ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN;
                    mToHome="1".equals(uri.getQueryParameter("toHome")) ;
                    if(requestType==ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN){
                        subject =StringUtils.parseInt(uri.getQueryParameter("subject"));
                        if(subject<=0) subject=-1;
                    }else {
                        subject=-1;
                    }

                } catch (Exception e) { }
            }else {
                mToHome = originIntent.getBooleanExtra("toHome", false);
                requestType = originIntent.getIntExtra("request_type", 0);
                subject = originIntent.getIntExtra("subject", -1);
                extraArgs = originIntent.getBundleExtra("extra_args");
                if (extraArgs == null) {
                    extraArgs = new Bundle();
                }
            }

        }
        super.onInitView();
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN) {
            reqType = "2";
        } else {
            reqType = "8";
        }
        isPageDivided = true;

        LogUtils.d(TAG, "requestType:" + requestType);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlGift.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_MOKAOGUFEN) {
            topActionBar.setTitle("专项模考");
            topActionBar.showButtonText("模考历史", topActionBar.RIGHT_AREA);
        } else if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ACCURATE_GUFEN) {
            topActionBar.setTitle("精准估分");
            topActionBar.showButtonText("估分历史", topActionBar.RIGHT_AREA);
        }
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                if (mToHome&& !ActivityStack.getInstance().hasRootActivity()) {
                    MainTabActivity.newIntent(SimulationExamActivity.this);
                }
                SimulationExamActivity.this.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                if (!CommonUtils.checkLogin(SimulationExamActivity.this)) {
                    return;
                }
                RecordActivity.newInstance(SimulationExamActivity.this, requestType);
            }
        });
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<SimulationListItem>(getApplicationContext(),
                dataList, new MultiItemTypeSupport<SimulationListItem>() {
            @Override
            public int getLayoutId(int position, SimulationListItem simulationListItem) {
                return R.layout.item_simulation;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getItemViewType(int position, SimulationListItem simulationListItem) {
                return 0;
            }
        }) {
            @Override
            public void convert(ViewHolder holder, final SimulationListItem item, int position) {

                if (!TextUtils.isEmpty(item.descrp)) {
                    holder.setText(R.id.simulation_desc, item.descrp);
                    holder.setViewVisibility(R.id.simulation_desc, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.simulation_desc, View.GONE);
                }

                if (item.startTime != 0) {
                    String timeDay = CommonUtils.getDataTime(item.startTime);
//                    String timeDay2 = CommonUtils.getSCDayFormatData(item.endTime);
//                    String timeHourStart = CommonUtils.getSCHourFormatData(item.startTime);
//                    String timeHourEnd = CommonUtils.getSCHourFormatData(item.endTime);
                    holder.setText(R.id.simulation_time, timeDay + " 开始");
                } else {
                    holder.setText(R.id.simulation_time, "");
                }

                // 心跳图片
                ShakeImageView ivShake = holder.getView(R.id.iv_shake);

                String iconUrl = item.iconUrl;
                if (!StringUtils.isEmpty(iconUrl)) {
                    ivShake.setVisibility(View.VISIBLE);
                    ImageLoad.load(mContext, iconUrl.trim(), ivShake);
                    ivShake.startAnim();
                } else {
                    ivShake.setVisibility(View.GONE);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CommonUtils.checkLogin(SimulationExamActivity.this)) {
                            return;
                        }
                        onClickItem(item);
                    }
                });

                String strStatus = "";
                if (item.type == ArenaConstant.EXAM_PAPER_TYPE_SIMULATION_CONTEST) {

                    holder.setViewBackgroundColor(R.id.item_simulation_rl, R.color.item_sc_bg_color);
                    holder.setViewVisibility(R.id.item_simulation_contest_icon, View.VISIBLE);
                    holder.setText(R.id.simulation_title, "        " + item.name);
                    holder.setTextColorRes(R.id.simulation_title, R.color.common_style_text_color);

                    if (item.status == 6) {
                        strStatus = "查看报告";
                        holder.setColorText(R.id.simulation_status, R.color.item_sc_status_color, strStatus);
                    } else {
                        holder.setText(R.id.simulation_status, "");
                    }

                } else {
                    holder.setViewBackgroundColor(R.id.item_simulation_rl, R.color.white);
                    holder.setText(R.id.simulation_title, item.name);
                    holder.setTextColorRes(R.id.simulation_title, R.color.text_color_dark);
                    holder.setViewVisibility(R.id.item_simulation_contest_icon, View.GONE);
                    int textColor = R.color.text_color_light;
                    switch (item.status) {
                        case 1:
                            strStatus = "未开始";
                            if (!TextUtils.isEmpty(item.url)) {
                                textColor = R.color.text_color_light;
                            }
                            break;
                        case 2:
                            strStatus = "进行中";
                            textColor = R.color.item_sc_status_color;
                            break;
                        case 3:
                            strStatus = "已结束";
                            break;
                        case 4:
                            strStatus = "已下线";
                            break;
                        case 5:
                            strStatus = "继续做题";
                            textColor = R.color.item_sc_status_color;
                            break;
                        case 6:
                            strStatus = "查看报告";
                            textColor = R.color.item_sc_status_color;
                            break;
                        case 7:
                            strStatus = "未出报告";
                            break;
                    }
                    holder.setColorText(R.id.simulation_status, textColor, strStatus);
                }
            }
        };
    }

    private void onClickItem(final SimulationListItem item) {
        final Bundle arg = new Bundle();
        UserMetaBean userMetaBean = item.userMeta;
        if (item.type == ArenaConstant.EXAM_PAPER_TYPE_SIMULATION_CONTEST) {
            if (item.status == 6) {
                if (userMetaBean != null && userMetaBean.practiceIds != null
                        && userMetaBean.practiceIds.length > 0) {
                    arg.putBoolean("show_statistic", true);
                    arg.putLong("practice_id", userMetaBean.practiceIds[0]);
                } else {
                    LogUtils.e(TAG, "if (userMetaBean != null && userMetaBean.practiceIds != null\n" +
                            "                        && userMetaBean.practiceIds.length > 0) {");
                    return;
                }
                Intent intent = new Intent(SimulationExamActivity.this, ScReportActivity.class);
                intent.putExtra("tag", 1);
                intent.putExtra("arg", arg);
                startActivity(intent);
            } else {
                arg.putInt("paper_id", item.id);

                Intent intent = new Intent(SimulationExamActivity.this, SimulationContestActivityNew.class);
                intent.putExtra("mToHome", mToHome);
                intent.putExtra("subject", SignUpTypeDataCache.getInstance().getCurSubject());
                startActivity(intent);
                finish();
            }
        } else {
            arg.putLong("point_ids", item.id);
            //试卷活动状态 2正在进行，5可继续做题，6，可查看报告
            switch (item.status) {
                case 1:
                    Toast.makeText(SimulationExamActivity.this, "暂未开始", Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(item.url)) {
                        AdvertiseActivity.newInstance(SimulationExamActivity.this, "", item.url, 1, item.name);
                    }
                    break;
                case 2:     // 正在进行(做题)
                    if (!StringUtils.isEmpty(item.iconUrl)) {
                        showGiftDescribe(arg);
                    } else {
                        ArenaExamActivityNew.show(SimulationExamActivity.this, requestType, arg);
                    }
                    break;
                case 5:    // 可继续做题（做题）
                    arg.putBoolean("continue_answer", true);
                    arg.putLong("practice_id", userMetaBean.currentPracticeId);
                    if (!StringUtils.isEmpty(item.iconUrl)) {
                        showGiftDescribe(arg);
                    } else {
                        ArenaExamActivityNew.show(SimulationExamActivity.this, requestType, arg);
                    }
                    break;
                case 6:     // 报告
                    if (userMetaBean != null && userMetaBean.practiceIds != null
                            && userMetaBean.practiceIds.length > 0) {
                        arg.putLong("practice_id", userMetaBean.practiceIds[0]);
                        ArenaExamReportActivity.show(SimulationExamActivity.this, requestType, arg);
                    }
                    break;
            }
        }

        if (arg != null) {
            LogUtils.d("SimulationExamActivity -item", item.toString());
            LogUtils.d("SimulationExamActivity -bundle", arg.toString());
        }

    }

    @Override
    protected void onLoadData() {
        onRefresh();
        if (reqType.equals("8")) {
            getGiftDescribe();
        }
    }

    /**
     * 如果是精准估分，获取大礼包描述详情
     */
    private void getGiftDescribe() {
        if (!NetUtil.isConnected()) {
            return;
        }
        ServiceProvider.getEstiMateInfo(compositeSubscription, 13, subject, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                if (model.code == 1000000) {
                    if (model.data instanceof GiftDescribeBean) {
                        giftDescribeBean = (GiftDescribeBean) model.data;
                        if (giftDescribeBean.isShow == 1) {
                            rlGift.setVisibility(View.VISIBLE);
                            tvGift.setText(giftDescribeBean.giftIntroduce);
                            ImageLoad.load(SimulationExamActivity.this, giftDescribeBean.iconUrl.trim(), ivGift);
                        } else {
                            rlGift.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }


    /**
     * 显示礼包描述弹窗
     */
    private void showGiftDescribe(final Bundle arg) {

        if (giftDescribeBean == null) {
            return;
        }

        View layout = LayoutInflater.from(SimulationExamActivity.this).inflate(R.layout.gift_dialog_layout, null);

        //对话框
        final Dialog dialog = new AlertDialog.Builder(SimulationExamActivity.this).create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(layout);

        WindowManager.LayoutParams lp = window.getAttributes();
        int width = DisplayUtil.getScreenWidth();                           // 获取屏幕宽、高用
        lp.width = (int) (width * 0.8);                                     // 高度设置为屏幕的0.6
        window.setAttributes(lp);

        // 内容
        TextView tvContent = layout.findViewById(R.id.tv_content);
        tvContent.setText(giftDescribeBean.bigBagRemind);

        // OK按钮
        TextView tvOk = layout.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ArenaExamActivityNew.show(SimulationExamActivity.this, requestType, arg);
            }
        });
    }

    @Override
    public void onRefresh() {
        getData(true);
    }

    @Override
    public void onLoadMore() {
        getData(false);
    }

    private void getData(final boolean isRefresh) {
        if (isRefresh) {
            currentPage = 1;
        }
        showProgressBar();
        ServiceProvider.getSimulationExamList(compositeSubscription, currentPage, pageSize, reqType, subject, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                List<SimulationListItem> tmpList = model.data;
                currentPage++;
                SimulationExamActivity.this.onSuccess(tmpList, isRefresh);
            }

            @Override
            public void onError(final Throwable e) {
                SimulationExamActivity.this.onLoadDataFailed();
            }
        });
    }

    @Override
    public void onSetData(Object respData) {

    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mToHome&& !ActivityStack.getInstance().hasRootActivity()) {
            MainTabActivity.newIntent(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public static void newIntent(Context context, int from, Bundle args) {
        Intent intent = new Intent(context, SimulationExamActivity.class);
        intent.putExtra("request_type", from);
        intent.putExtra("extra_args", args);
        context.startActivity(intent);
    }
}
