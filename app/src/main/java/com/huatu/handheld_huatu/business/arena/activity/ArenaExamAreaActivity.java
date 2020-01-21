package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.ActivityStack;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseListViewActivity;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.arena.utils.RealListUpdateClickRecord;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.helper.LoginTrace;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.area.ExamAreaItem;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.netease.hearttouch.router.HTRouter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saiyuan on 2016/12/16.
 * 真题演练/综合应用
 */
@HTRouter(url = {"ztk://pastPaper/home"}, needLogin = true)
public class ArenaExamAreaActivity extends BaseListViewActivity {

    private int requestType;
    private Bundle extraArgs = null;
    private Integer subject = null;
    private boolean mToHome;
    private String mVersionHit;
    private HashMap<String, String> mVersionMap = new HashMap<>();

    @Override
    protected void onInitView() {
        if (originIntent != null) {
            Uri uri = originIntent.getData();
            if (uri != null) {
                try {
                    requestType = "pastPaper".equals(uri.getHost()) ? ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN : ArenaConstant.START_NTEGRATED_APPLICATION;
                    mToHome = "1".equals(uri.getQueryParameter("toHome"));
                } catch (Exception e) {
                }
            } else {
                mToHome = originIntent.getBooleanExtra("toHome", false);
                requestType = originIntent.getIntExtra("request_type", 0);
                extraArgs = originIntent.getBundleExtra("extra_args");
                if (extraArgs == null) {
                    extraArgs = new Bundle();
                }
            }
        }
        super.onInitView();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.gray005)));
        listView.setDividerHeight(1);
    }

    @Override
    public boolean hasToolbar() {
        return true;
    }

    @Override
    public void initToolBar() {
        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN) {
            topActionBar.setTitle("真题演练");
            // subject = SignUpTypeDataCache.getInstance().curSubject;
            subject = SignUpTypeDataCache.getInstance().getCurSubject();
        } else if (requestType == ArenaConstant.START_NTEGRATED_APPLICATION) {
            topActionBar.setTitle("综合应用");
            subject = Type.PB_ExamType.NTEGRATED_APPLICATION;
            topActionBar.showButtonText("试卷下载", TopActionBar.RIGHT_AREA);
        }
        topActionBar.showButtonImage(R.drawable.icon_arrow_left, TopActionBar.LEFT_AREA);
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
                    MainTabActivity.newIntent(ArenaExamAreaActivity.this);
                }
                ArenaExamAreaActivity.this.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @LoginTrace(type = 0)
            @Override
            public void onRightButtonClick(View view) {
                if (!CommonUtils.checkLogin(ArenaExamAreaActivity.this)) {
                    return;
                }
                if (requestType == ArenaConstant.START_NTEGRATED_APPLICATION) {
                    Intent intent = new Intent(ArenaExamAreaActivity.this, DownloadArenaPaperActivity.class);
                    intent.putExtra("curSubject", subject);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<ExamAreaItem>(getApplicationContext(),
                dataList, R.layout.item_exam_area_list) {
            @Override
            public void convert(final ViewHolder holder, final ExamAreaItem item, int position) {
                if (!TextUtils.isEmpty(item.areaName)) {
                    holder.setText(R.id.text_city, item.areaName);
                    if (item.version != -1) {
                        setRealVersionHitKey(item.area);
                    }
                    if (item.version == -1) {
                        holder.setViewVisibility(R.id.update_red_point, View.GONE);
                    } else if (item.version == RealListUpdateClickRecord.getInstance().getRealAreaVersionRecord(mVersionMap.get(mVersionHit + item.area))) {
                        //0是未点击过
                        if (RealListUpdateClickRecord.getInstance().getRealAreaHitRecord(mVersionMap.get(mVersionHit + item.area)) == 0) {
                            holder.setViewVisibility(R.id.update_red_point, View.VISIBLE);
                        } else {
                            holder.setViewVisibility(R.id.update_red_point, View.GONE);
                        }
                    } else {
                        holder.setViewVisibility(R.id.update_red_point, View.VISIBLE);
                        RealListUpdateClickRecord.getInstance().addRealAreaVersion(mVersionMap.get(mVersionHit + item.area), item.version);
                        RealListUpdateClickRecord.getInstance().saveRealAreaVersionRecordToFile();
                        RealListUpdateClickRecord.getInstance().addRealAreaHitRecord(mVersionMap.get(mVersionHit + item.area), 0);
                        RealListUpdateClickRecord.getInstance().saveRealAreaHitRecordToFile();
                    }
                } else {
                    holder.setText(R.id.text_city, "");
                    holder.setViewVisibility(R.id.update_red_point, View.GONE);
                }
                holder.setText(R.id.text_number, item.paperCount + "套");
                if (item.isSelected) {
                    holder.setViewVisibility(R.id.image_select, View.VISIBLE);
                    holder.setViewBackgroundRes(R.id.rl_item, R.color.gray004);
                } else {
                    holder.setViewVisibility(R.id.image_select, View.GONE);
                    holder.setViewBackgroundRes(R.id.rl_item, R.color.white);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {

                        if (requestType == ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHENTI_YANLIAN) {
                            StudyCourseStatistic.clickStatistic("题库->真题演练", "页面中部", item.areaName);
                        }

                        holder.setViewVisibility(R.id.update_red_point, View.GONE);
                        RealListUpdateClickRecord.getInstance().addRealAreaHitRecord(mVersionMap.get(mVersionHit + item.area), 1);
                        RealListUpdateClickRecord.getInstance().saveRealAreaHitRecordToFile();
                        int areaCode = item.area;
                        SpUtils.getSp().edit().putInt("select_aea_id" + subject, areaCode).commit();
                        for (int i = 0; i < dataList.size(); i++) {
                            ((ExamAreaItem) (dataList.get(i))).isSelected = false;
                        }
                        item.isSelected = true;
                        mAdapter.notifyDataSetChanged();
                        Intent intent = new Intent(ArenaExamAreaActivity.this, ExamPaperActivity.class);
                        intent.putExtra("examAreaData", item);
                        intent.putExtra("request_type", requestType);
                        startActivity(intent);
                    }
                });
            }
        };
    }

    private void setRealVersionHitKey(int area) {
        if (SignUpTypeDataCache.getInstance().getCurCategory() == Type.SignUpType.PUBLIC_INSTITUTION && SignUpTypeDataCache.getInstance().getCurSubject() != Type.PB_ExamType.NTEGRATED_APPLICATION) {
            mVersionHit = SignUpTypeDataCache.getInstance().getCurSubject() + "_" + SpUtils.getUid();
        } else {
            mVersionHit = SignUpTypeDataCache.getInstance().getCurCategory() + "_" + SpUtils.getUid();
        }
        mVersionMap.put(mVersionHit + area, mVersionHit + area);
    }

    @Override
    protected void onLoadData() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        showProgressBar();

        ServiceProvider.getExamAreaList(compositeSubscription, subject, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                List<ExamAreaItem> tmpList = model.data;
                int areaId = SpUtils.getSp().getInt("select_aea_id" + subject, -111111);
                for (int i = 0; i < tmpList.size(); i++) {
                    if (areaId == tmpList.get(i).area) {
                        tmpList.get(i).isSelected = true;
                    }
                }
                ArenaExamAreaActivity.this.onSuccess(tmpList, true);
                dismissProgressBar();
            }

            @Override
            public void onError(final Throwable e) {
                ArenaExamAreaActivity.this.onLoadDataFailed();
            }
        });
    }

    @Override
    public void onLoadMore() {
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
        if (mToHome && !ActivityStack.getInstance().hasRootActivity()) {
            MainTabActivity.newIntent(this);
        }
    }

    public static void newInstance(Context context, int from) {
        Intent intent = new Intent(context, ArenaExamAreaActivity.class);
        intent.putExtra("request_type", from);
        context.startActivity(intent);
    }
}
