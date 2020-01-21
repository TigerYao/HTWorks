package com.huatu.handheld_huatu.business.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.mvpmodel.me.ActionListBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.NetUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 活动中心
 */
public class ActionCenterActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout ll_prompt;
    private ImageView image_empty;
    private TextView text_faile;
    private ProgressBar progress_bar;
    private RelativeLayout rl_left_topbar;
    private ListView listview;
    private ActionAdapter actionAdapter;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_action_center;
    }

    @Override
    protected void onInitView() {
        ll_prompt = (LinearLayout) findViewById(R.id.ll_prompt);
        image_empty = (ImageView) findViewById(R.id.image_empty);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        text_faile = (TextView) findViewById(R.id.text_faile);

        rl_left_topbar = (RelativeLayout) findViewById(R.id.rl_left_topbar);
        listview = (ListView) findViewById(R.id.listview);
        actionAdapter = new ActionAdapter();
        listview.setAdapter(actionAdapter);

        show_loadData();
        loadData();
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

    private void loadData() {
        if (!NetUtil.isConnected()) {
            show_loadFaile("不好了，网络被外星人绑架了");
            return;
        }

        Subscription subscription = RetrofitManager.getInstance().getService().getActionListInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ActionListBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, e.getMessage() + "----1");
                        show_loadFaile("获取活动列表失败");
                    }

                    @Override
                    public void onNext(ActionListBean actionListBean) {
                        Log.i(TAG, "1-----" + actionListBean.toString());
                        show_loadSuccess();
                        int code = actionListBean.code;
                        List<ActionListBean.ActionListData> data = actionListBean.data;
                        if (code == 1000000) {
                            dealwithData(data);
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            show_loadFaile("获取活动列表失败");
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    private void dealwithData(List<ActionListBean.ActionListData> data) {
        if (data != null) {
            beans.clear();
            beans.addAll(data);
            actionAdapter.notifyDataSetChanged();
        }
    }

    private List<ActionListBean.ActionListData> beans = new ArrayList<ActionListBean.ActionListData>();

    private class ActionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (beans != null && beans.size() > 0)
                return beans.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            return beans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ActionHolder holder;
            if (convertView == null) {
                holder = new ActionHolder();
                convertView = View.inflate(ActionCenterActivity.this, R.layout.item_aciton, null);

                holder.view_line = convertView.findViewById(R.id.view_line);
                holder.rl_activity_showing = (RelativeLayout) convertView.findViewById(R.id.rl_activity_showing);
                holder.textview_avtivity_showing = (TextView) convertView.findViewById(R.id.textview_avtivity_showing);
                holder.image_wanderful_icon = (ImageView) convertView.findViewById(R.id.image_wanderful_icon);
                holder.textview_avtivity_time = (TextView) convertView.findViewById(R.id.textview_avtivity_time);
                holder.textview_activity_number = (TextView) convertView.findViewById(R.id.textview_activity_number);
                holder.textview_activity_name = (TextView) convertView.findViewById(R.id.textview_activity_name);
                holder.textview_activity_join = (TextView) convertView.findViewById(R.id.textview_activity_join);

                convertView.setTag(holder);
            } else {
                holder = (ActionHolder) convertView.getTag();
            }

            ActionListBean.ActionListData actionBean = beans.get(position);

            String name = actionBean.getName();
            if (!TextUtils.isEmpty(name))
                holder.textview_activity_name.setText(name);
            else
                holder.textview_activity_name.setText("");

            String image = actionBean.getImage();
            GlideApp.with(ActionCenterActivity.this)
                    .load(image)
//                    .placeholder(R.mipmap.load_default)

//                    .error(R.mipmap.load_default)
                    .into(holder.image_wanderful_icon);

            long pv = actionBean.getPv();
            holder.textview_activity_number.setText(pv + "人");

            long beginTime = actionBean.getBeginTime();
            long endTime = actionBean.getEndTime();
            String begin = DateUtils.formatMs(beginTime);
            String end = DateUtils.formatMs(endTime);
            holder.textview_avtivity_time.setText(begin + "-" + end);


            int status = actionBean.getStatus();
            if (status == 1) {//上线
                holder.textview_avtivity_showing.setText("正在进行");
                holder.rl_activity_showing.setBackgroundResource(R.mipmap.wanderful_activity_progress);
                holder.textview_activity_join.setText("马上参加");
                holder.textview_activity_join.setBackgroundResource(R.drawable.bg_wanderful_bt);
            } else if (status == 0) {//未开始
                holder.textview_avtivity_showing.setText("即将开始");
                holder.textview_activity_join.setText("活动详情");
                holder.textview_activity_join.setBackgroundResource(R.drawable.bg_wanderful_bt);
                holder.rl_activity_showing.setBackgroundResource(R.mipmap.wanderful_activity_progress);
            } else {//结束
                holder.textview_avtivity_showing.setText("已结束");
                holder.textview_activity_join.setText("已结束");
                holder.textview_activity_join.setBackgroundResource(R.drawable.bg_wanderful_out_bt);
                holder.rl_activity_showing.setBackgroundResource(R.mipmap.wanderful_activity_end);
            }


            if (position == 0) {
                holder.view_line.setVisibility(View.GONE);
                holder.rl_activity_showing.setVisibility(View.VISIBLE);
            } else {
                ActionListBean.ActionListData preAction = beans.get(position - 1);
                int preSta = preAction.getStatus();

                if (status == preSta) {
                    holder.rl_activity_showing.setVisibility(View.GONE);
                    holder.view_line.setVisibility(View.VISIBLE);
                } else {
                    holder.view_line.setVisibility(View.GONE);
                    holder.rl_activity_showing.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }
    }

    class ActionHolder {
        View view_line;
        RelativeLayout rl_activity_showing;
        TextView textview_avtivity_showing;
        ImageView image_wanderful_icon;
        TextView textview_avtivity_time;
        TextView textview_activity_number;
        TextView textview_activity_name;
        TextView textview_activity_join;
    }

    private int selectPosition;

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectPosition = position;
                ActionListBean.ActionListData actionBean = beans.get(position);
                int status = actionBean.getStatus();
                if (status != 2) {
                    Intent intent = new Intent(ActionCenterActivity.this, ActionDetailActivity.class);
                    intent.putExtra("action_center_bean_activity", actionBean);
                    startActivityForResult(intent, 110);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110) {
            ActionListBean.ActionListData actionBean = beans.get(selectPosition);
            long pv = actionBean.getPv();
            pv++;
            actionBean.setPv(pv);
            actionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_left_topbar:
                ActionCenterActivity.this.finish();
                break;
        }
    }

    private void show_loadData() {
        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText("获取数据中...");
    }

    private void show_loadFaile(String msg) {
        ll_prompt.setVisibility(View.VISIBLE);
        image_empty.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.VISIBLE);
        text_faile.setText(msg);
    }

    private void show_loadSuccess() {
        ll_prompt.setVisibility(View.GONE);
        image_empty.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        text_faile.setVisibility(View.GONE);
        text_faile.setText("");
    }
}
