package com.huatu.handheld_huatu.business.arena.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.arena.Type;
import com.huatu.handheld_huatu.mvpmodel.special.DailySpecialBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.Serializable;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 每日特训
 */
public class DailySpecialActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_prompt;
    private ImageView image_empty;
    private TextView text_faile;
    private ProgressBar progress_bar;
    //    private RelativeLayout rl_left_topbar;
    private ImageView ivBack;
    private ListView listview;
    private RelativeLayout rl_right_topbar;
    private TextView tv_title_titlebar;
    private RelativeLayout rl_num;
    private TextView text_finish_number;
    private TextView text_set_number;
    private TextView text_suggest;
    private CustomDialog finishDialog;
    private int allCount;
    private int finishCount;
    private SpecialAdapter specialAdapter;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_daily_special;
    }

    @Override
    protected void onInitView() {
        ll_prompt = (LinearLayout) findViewById(R.id.ll_prompt);
        image_empty = (ImageView) findViewById(R.id.image_empty);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        text_faile = (TextView) findViewById(R.id.text_faile);

        ivBack = (ImageView) findViewById(R.id.iv_back);
        rl_right_topbar = (RelativeLayout) findViewById(R.id.rl_right_topbar);
        tv_title_titlebar = (TextView) findViewById(R.id.tv_title_titlebar);

        rl_num = (RelativeLayout) findViewById(R.id.rl_num);
        text_finish_number = (TextView) findViewById(R.id.text_finish_number);
        text_set_number = (TextView) findViewById(R.id.text_set_number);

        text_suggest = (TextView) findViewById(R.id.text_suggest);

        listview = (ListView) findViewById(R.id.listview);

        if (SpUtils.getDailySpecialTip()) {
            final TextView tip_one = (TextView) findViewById(R.id.tip_one);
            tip_one.setVisibility(View.VISIBLE);
            tip_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tip_one.setVisibility(View.GONE);
                    SpUtils.setDailySpecialTip(false);
                }
            });
        }
        //获取传递过来的数据
        DailySpecialBean data = (DailySpecialBean) getIntent().getSerializableExtra("daily_special_data_bean");

        if (data != null) {
            dealwithData(data);
        } else {
            show_loadData();
            loadData(true);
        }
        setListener();
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    private void setListener() {
        ivBack.setOnClickListener(this);
        rl_right_topbar.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClick(i);
            }
        });
    }

    private void listItemClick(int position) {
        if (allCount == finishCount) {
            showFinishDialog();
            return;
        }

        DailySpecialBean.DailySpecialPoints dailySpecialPoints = (DailySpecialBean.DailySpecialPoints) specialAdapter.getItem(position);
        int status = dailySpecialPoints.getStatus();

        if (status == 2) {
            ToastUtils.showEssayToast("已经做过了");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("point_ids", dailySpecialPoints.getQuestionPointId());
        ArenaExamActivityNew.show(DailySpecialActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_MEIRI_TEXUN, bundle);
    }

    private void showFinishDialog() {
        if (SignUpTypeDataCache.getInstance().getCurCategory() > Type.SignUpType.PUBLIC_INSTITUTION || SignUpTypeDataCache.getInstance().getCurSubject() == Type.PB_ExamType.PUBLIC_BASE) {
            DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    ArenaExamActivityNew.show(DailySpecialActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, bundle);
//                DailySpecialActivity.this.finish();
                }
            }, null, "今日特训已完成，去试试错题重练？", "不了，谢谢", "确认前往");
        } else {
            DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    ArenaExamActivityNew.show(DailySpecialActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_AI_PRACTICE, bundle);
//                DailySpecialActivity.this.finish();
                }
            }, null, "今日特训已完成，去试试智能刷题？", "不了，谢谢", "确认前往");
        }
//        if (finishDialog == null || (finishDialog != null && !finishDialog.isShowing())) {
//            finishDialog = new CustomDialog(DailySpecialActivity.this, R.layout.dialog_daily_special_finish);
//        }
//        finishDialog.show();
//
//        TextView text_ok = (TextView) finishDialog.mContentView.findViewById(R.id.text_ok);
//        text_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finishDialog.dismiss();
//            }
//        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.iv_back:
                DailySpecialActivity.this.finish();
                break;
            case R.id.rl_right_topbar:
                Intent intent = new Intent(DailySpecialActivity.this, DailySpecialSettingActivity.class);
                intent.putExtra("fromactivity", "DailySpecialActivity");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadData(false);
    }

    private void loadData(final boolean isFlag) {
        if (!NetUtil.isConnected()) {
            if (isFlag) {
                show_loadFaile("不好了，网络被外星人绑架了");
            } else {
                CommonUtils.showToast("无网络，请检查网络设置");
            }
            return;
        }

        Subscription subscription = RetrofitManager.getInstance().getService().getDailyList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel<DailySpecialBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isFlag) {
                            show_loadFaile("获取数据失败~");
                        } else {
                            CommonUtils.showToast("获取数据失败~");
                        }
                    }

                    @Override
                    public void onNext(BaseResponseModel<DailySpecialBean> dailySpecialBean) {
                        int code = dailySpecialBean.code;
                        DailySpecialBean data = dailySpecialBean.data;
                        if (code == 1000000) {
                            show_loadSuccess();
                            dealwithData(data);
                        } else if (code == 1110002) {
                            if (isFlag) {
                                show_loadFaile("用户会话过期");
                            } else {
                                CommonUtils.showToast("用户会话过期");
                            }
                        } else {
                            if (isFlag) {
                                show_loadFaile("获取数据失败~");
                            } else {
                                CommonUtils.showToast("获取数据失败~");
                            }
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    private void dealwithData(DailySpecialBean data) {
        if (data != null) {
            List<DailySpecialBean.DailySpecialPoints> points = data.getPoints();

            allCount = data.getAllCount();
            finishCount = data.getFinishCount();
            text_finish_number.setText(finishCount + "");
            text_set_number.setText("/" + allCount + "次");
            ArenaDataCache.getInstance().trainDailyFinishcount = finishCount;

            specialAdapter = new SpecialAdapter(data, points);
            listview.setAdapter(specialAdapter);
        } else {
            CommonUtils.showToast("获取数据失败~");
        }
    }

    private class SpecialAdapter extends BaseAdapter {
        private DailySpecialBean data;
        private List<DailySpecialBean.DailySpecialPoints> points;

        public SpecialAdapter(DailySpecialBean data, List<DailySpecialBean.DailySpecialPoints> points) {
            this.data = data;
            this.points = points;
        }

        @Override
        public int getCount() {
            if (points != null && points.size() > 0)
                return points.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            return points.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();

                convertView = View.inflate(DailySpecialActivity.this, R.layout.item_special_list, null);

                holder.text_name = (TextView) convertView.findViewById(R.id.text_name);
                holder.text_unit = (TextView) convertView.findViewById(R.id.text_unit);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.text_num = (TextView) convertView.findViewById(R.id.text_num);
                holder.text_suggest = (TextView) convertView.findViewById(R.id.text_suggest);

                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            DailySpecialBean.DailySpecialPoints specialPointsBean = points.get(position);

            String name = specialPointsBean.getName();
            if (!TextUtils.isEmpty(name)) {
                holder.text_name.setText(name);
            } else {
                holder.text_name.setText("");
            }

            int status = specialPointsBean.getStatus();
            //完成
            if (status == 2) {
                holder.text_name.setTextColor(getResources().getColor(R.color.gray006));
                holder.text_unit.setTextColor(getResources().getColor(R.color.gray006));
                holder.text_num.setTextColor(getResources().getColor(R.color.gray006));
                holder.text_suggest.setTextColor(getResources().getColor(R.color.gray006));
                holder.image.setImageResource(R.mipmap.special__finish);
                //未做
            } else {
                holder.text_name.setTextColor(getResources().getColor(R.color.black));
                holder.text_unit.setTextColor(getResources().getColor(R.color.gray010));
                holder.text_num.setTextColor(getResources().getColor(R.color.common_style_text_color));
                holder.text_suggest.setTextColor(getResources().getColor(R.color.gray010));
                holder.image.setImageResource(R.mipmap.homef_item_tree_right);
            }

            return convertView;
        }
    }

    class Holder {
        TextView text_name;
        TextView text_unit;
        ImageView image;
        TextView text_num;
        TextView text_suggest;
    }

    private void show_loadData() {
        rl_num.setVisibility(View.INVISIBLE);
        text_suggest.setVisibility(View.GONE);

        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText("获取数据中...");
    }

    private void show_loadFaile(String msg) {
        rl_num.setVisibility(View.INVISIBLE);
        text_suggest.setVisibility(View.GONE);

        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText(msg);
    }

    private void show_loadSuccess() {
        rl_num.setVisibility(View.VISIBLE);
        text_suggest.setVisibility(View.GONE);

        ll_prompt.setVisibility(View.GONE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.GONE);
        text_faile.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArenaDataCache.getInstance().trainDailyFinishcount = 0;
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

}
