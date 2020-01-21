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
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.mvpmodel.special.SettingBean;
import com.huatu.handheld_huatu.mvpmodel.special.SpecialSeetingBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.RxUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 每日特训设置
 */
public class DailySpecialSettingActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_prompt;
    private ImageView image_empty;
    private TextView text_faile;
    private ProgressBar progress_bar;
    private RelativeLayout rl_left_topbar;
    private ListView listview;
    private RelativeLayout rl_right_topbar;
    private TextView tv_title_titlebar;
    private List<SpecialSeetingBean.SpecialSeetingPointsBean> points;
    private int questionCount;//特训试题个数
    private int number;//每日特训次数
    private long userId;
    private long id;//每日特训id
    private TextView text_set_num;
    private PointAdapter pointAdapter;
    private RelativeLayout rl_setting_num;
    private PopupWindow popupWindow;
    private RelativeLayout rl_top_titlebar;
    private NumAdapter numAdapter;
    private View view_close;
    private CustomDialog customDialog;
    private TextView text_suggest;
    private TextView text_id;
    private CustomDialog successDialog;
    private Subscription subscriptionContent;
    private String fromUI;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_daily_setting;
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

    @Override
    protected void onInitView() {
        fromUI = getIntent().getStringExtra("fromActivity");

        ll_prompt = (LinearLayout) findViewById(R.id.ll_prompt);
        image_empty = (ImageView) findViewById(R.id.image_empty);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        text_faile = (TextView) findViewById(R.id.text_faile);

        rl_left_topbar = (RelativeLayout) findViewById(R.id.rl_left_topbar);
        rl_right_topbar = (RelativeLayout) findViewById(R.id.rl_right_topbar);
        tv_title_titlebar = (TextView) findViewById(R.id.tv_title_titlebar);

        rl_top_titlebar = (RelativeLayout) findViewById(R.id.rl_top_titlebar);
        rl_setting_num = (RelativeLayout) findViewById(R.id.rl_setting_num);
        listview = (ListView) findViewById(R.id.listview);

        text_set_num = (TextView) findViewById(R.id.text_set_num);

        text_suggest = (TextView) findViewById(R.id.text_suggest);
        text_id = (TextView) findViewById(R.id.text_id);

        show_loadData();
        loadData();
        setListener();
    }

    private void setListener() {
        rl_setting_num.setOnClickListener(this);
        rl_left_topbar.setOnClickListener(this);
        rl_right_topbar.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectPoint[position]) {
                    sumSelected--;
                    selectPoint[position] = false;
                } else {
                    sumSelected++;
                    selectPoint[position] = true;
                }
                pointAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_left_topbar:
                finish();
                break;
            case R.id.rl_setting_num:
                if (popupWindow == null || (popupWindow != null && !popupWindow.isShowing())) {
                    showNum();
                }
                break;
            case R.id.view_close:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                break;
            case R.id.rl_right_topbar:
                compositeSubscription.remove(subscriptionContent);
                if (isSettint) {
                    sendSetting();
                }
                break;
        }
    }

    private boolean isSettint = true;

    //选择的知识点
    private List<Integer> selects = new ArrayList<Integer>();

    private void sendSetting() {
        selects.clear();
        if (sumSelected == 0) {
            CommonUtils.showToast("请选择涉及的知识点");
            return;
        }
        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("网络连接异常，请检查网络");
            return;
        }
        isSettint = false;
        customDialog = new CustomDialog(DailySpecialSettingActivity.this, R.layout.dialog_feedback_commit);
        customDialog.show();

        for (int i = 0; i < selectPoint.length; i++) {
            boolean flag = selectPoint[i];
            if (flag) {
                SpecialSeetingBean.SpecialSeetingPointsBean specialSeetingPointsBean = points.get(i);
                int pointId = specialSeetingPointsBean.getPointId();
                selects.add(pointId);
            } else {
                continue;
            }
        }

        SettingBean settingBean = new SettingBean(id, number, questionCount, selects);

        Subscription subscriptionSetting = RetrofitManager.getInstance().getService().commitDailySetting(settingBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        customDialog.dismiss();
                        isSettint = true;
                        CommonUtils.showToast("每日特训内容设置失败");
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        customDialog.dismiss();
                        isSettint = true;
                        dealwithSettingData(responseBody);
                    }
                });

        compositeSubscription.add(subscriptionSetting);
    }

    private void dealwithSettingData(ResponseBody responseBody) {
        try {
            String string = responseBody.string().toString();
            JSONObject jsonObject = new JSONObject(string);
            int code = jsonObject.getInt("code");
            if (code == 1000000) {
                if (!TextUtils.isEmpty(fromUI) && "HomeFragment".equals(fromUI)) {
                    showSettingOkDialog("HomeFragment");
                } else {
                    showSettingOkDialog("DailySpecialActivity");
                }
            } else if (code == 1110002) {
                CommonUtils.showToast("用户会话过期");
            } else if (code == 1000103) {
                CommonUtils.showToast("资源未发现");
            } else if (code == 1000104) {
                CommonUtils.showToast("权限拒绝");
            } else {
                CommonUtils.showToast("每日特训内容设置失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSettingOkDialog(String showFlag) {
        successDialog = new CustomDialog(DailySpecialSettingActivity.this, R.layout.dialog_daily_setting_success);
        successDialog.show();
        TextView text_content = (TextView) successDialog.mContentView.findViewById(R.id.text_content);
        TextView text_ok = (TextView) successDialog.mContentView.findViewById(R.id.text_ok);

        if (!TextUtils.isEmpty(showFlag) && "HomeFragment".equals(showFlag)) {
            text_content.setText("你的计划已经开始,\n请每天坚持");
        } else {
            text_content.setText("你设置的计划将从次日生效\n请每天坚持");
        }

        text_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
                Intent intent = new Intent(DailySpecialSettingActivity.this, DailySpecialActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                DailySpecialSettingActivity.this.finish();
            }
        });
    }

    private void showNum() {
        final View numView = View.inflate(DailySpecialSettingActivity.this, R.layout.popupwidow_select_daily_num, null);

        view_close = numView.findViewById(R.id.view_close);
        ListView listview_num = (ListView) numView.findViewById(R.id.listview_num);
        numAdapter = new NumAdapter();
        listview_num.setAdapter(numAdapter);
        view_close.setOnClickListener(this);
        listview_num.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                number = numarr[position];
                text_set_num.setText(number + "次");
                numAdapter.notifyDataSetChanged();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow = new PopupWindow(numView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAsDropDown(rl_top_titlebar, 0, 0);
        popupWindow.update();
    }

    private static int[] numarr = {3, 5, 7, 10};

    private class NumAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return numarr.length;
        }

        @Override
        public Object getItem(int position) {
            return numarr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            NumHolder holder;
            if (convertView == null) {
                holder = new NumHolder();
                convertView = View.inflate(DailySpecialSettingActivity.this, R.layout.item_target_city, null);
                holder.text_city_name = (TextView) convertView.findViewById(R.id.text_city_name);
                holder.image_select = (ImageView) convertView.findViewById(R.id.image_select);
                convertView.setTag(holder);
            } else {
                holder = (NumHolder) convertView.getTag();
            }
            int selNum = numarr[position];
            holder.text_city_name.setText(selNum + "次");
            if (number == selNum) {
                holder.image_select.setVisibility(View.VISIBLE);
            } else {
                holder.image_select.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

    class NumHolder {
        TextView text_city_name;
        ImageView image_select;
    }

    private void loadData() {

        if (!NetUtil.isConnected()) {
            show_loadFaile("不好了，网络被外星人绑架了~");
            return;
        }
        subscriptionContent = RetrofitManager.getInstance().getService().getSettingInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SpecialSeetingBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        show_loadFaile("获取数据失败~");
                    }

                    @Override
                    public void onNext(SpecialSeetingBean specialSeetingBean) {
                        int code = specialSeetingBean.getCode();
                        SpecialSeetingBean.SpecialSeetingDataBean data = specialSeetingBean.getData();
                        if (code == 1000000) {
                            show_loadSuccess();
                            dealwidthData(data);
                        } else if (code == 1110002) {
                            show_loadFaile("会话过期，请重新登录~");
                        } else if (code == 1000102) {
                            show_loadFaile("服务器异常~");
                        } else {
                            show_loadFaile("获取数据失败~");
                        }
                    }
                });

        compositeSubscription.add(subscriptionContent);
    }

    private boolean[] selectPoint;
    private int sumSelected = 0;

    private void resetFlag() {
        sumSelected = 0;
        for (int i = 0; i < selectPoint.length; i++) {
            selectPoint[i] = false;
        }
    }

    private void dealwidthData(SpecialSeetingBean.SpecialSeetingDataBean data) {
        if (data != null) {
            points = data.getPoints();
            List<Integer> selects = data.getSelects();
            if (points != null && points.size() > 0) {
                selectPoint = new boolean[points.size()];
                resetFlag();
                for (int i = 0; i < points.size(); i++) {
                    SpecialSeetingBean.SpecialSeetingPointsBean specialSeetingPointsBean = points.get(i);
                    int pointId = specialSeetingPointsBean.getPointId();
                    if (selects != null && selects.size() > 0) {
                        sumSelected = selects.size();
                        for (Integer integer : selects) {
                            if (pointId == integer) {
                                selectPoint[i] = true;
                            }
                        }
                    }
                }
            }

            questionCount = data.getQuestionCount();
            number = data.getNumber();
            userId = data.getUserId();
            id = data.getId();
            text_set_num.setText(number + "次");

            pointAdapter = new PointAdapter(points);
            listview.setAdapter(pointAdapter);
        }
    }

    private class PointAdapter extends BaseAdapter {
        private List<SpecialSeetingBean.SpecialSeetingPointsBean> points;

        public PointAdapter(List<SpecialSeetingBean.SpecialSeetingPointsBean> points) {
            this.points = points;
        }

        @Override
        public int getCount() {
            if (points != null && points.size() > 0) {
                return points.size();
            } else {
                return 0;
            }
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
            PointHolder holder;
            if (convertView == null) {
                holder = new PointHolder();

                convertView = View.inflate(DailySpecialSettingActivity.this, R.layout.item_daily_special_setting, null);

                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.text = (TextView) convertView.findViewById(R.id.text);

                convertView.setTag(holder);
            } else {
                holder = (PointHolder) convertView.getTag();
            }

            SpecialSeetingBean.SpecialSeetingPointsBean specialSeetingPointsBean = points.get(position);
            String name = specialSeetingPointsBean.getName();
            if (!TextUtils.isEmpty(name)) {
                holder.text.setText(name);
            } else {
                holder.text.setText("");
            }

            boolean sel = selectPoint[position];
            if (sel) {
                holder.image.setBackgroundResource(R.mipmap.special_sel);
            } else {
                holder.image.setBackgroundResource(R.mipmap.special_normal);
            }
            return convertView;
        }
    }

    class PointHolder {
        ImageView image;
        TextView text;
    }

    private void show_loadData() {
        rl_setting_num.setVisibility(View.INVISIBLE);
        text_suggest.setVisibility(View.INVISIBLE);
        text_id.setVisibility(View.INVISIBLE);

        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText("获取数据中...");
    }

    private void show_loadFaile(String msg) {
        rl_setting_num.setVisibility(View.INVISIBLE);
        text_suggest.setVisibility(View.INVISIBLE);
        text_id.setVisibility(View.INVISIBLE);

        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText(msg);
    }

    private void show_loadSuccess() {
        rl_setting_num.setVisibility(View.VISIBLE);
        text_suggest.setVisibility(View.VISIBLE);
        text_id.setVisibility(View.VISIBLE);

        ll_prompt.setVisibility(View.GONE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.GONE);
        text_faile.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }
}
