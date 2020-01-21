package com.huatu.handheld_huatu.business.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.onRecyclerViewItemClickListener;
import com.huatu.handheld_huatu.business.message.adapter.MessageListAdapter;
import com.huatu.handheld_huatu.mvpmodel.MessageBean;
import com.huatu.handheld_huatu.network.HttpService;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.LoadMoreRecyclerView;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.MessageSharedPrefs;
import com.umeng.message.PushAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ljzyuhenda on 16/7/21.
 */
@Deprecated
public class MessageActivity extends Activity implements onRecyclerViewItemClickListener, View.OnClickListener {
    private static final String TAG = "MessageActivity";

    @BindView(R.id.rcv_simulation)
    LoadMoreRecyclerView rcv_simulation;
    @BindView(R.id.rl_left_topbar)
    RelativeLayout rl_left_topbar;
    @BindView(R.id.tv_title_titlebar)
    TextView tv_title_titlebar;
    @BindView(R.id.rl_right_topbar)
    RelativeLayout rl_right_topbar;

    @BindView(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @BindView(R.id.iv_no_data)
    ImageView ivNoData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;

    MessageListAdapter mMessageListAdapter;
    private long mCursor = 0;
    private CompositeSubscription mCompositeSubscription;
    HttpService mZtkService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(this.getClass().getName() + " onCreate()");
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);

        setListener();
        initDatas();

        loadMoreData(mCursor);
    }

    private void setListener() {
        rl_left_topbar.setOnClickListener(this);
        rlNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreData(mCursor);
            }
        });
    }

    private void initDatas() {
        rl_right_topbar.setVisibility(View.GONE);
        mZtkService = RetrofitManager.getInstance().getService();
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_simulation.setLayoutManager(linearLayoutManager);

        mMessageListAdapter = new MessageListAdapter(this);
        rcv_simulation.setAdapter(mMessageListAdapter);
        rcv_simulation.setAutoLoadMoreEnable(true);
        tv_title_titlebar.setText(R.string.message);

        rcv_simulation.setOnLoadMoreListener(new LoadMoreRecyclerView.onLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData(mCursor);
            }
        });

        mMessageListAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void loadMoreData(long cursor) {
        if (!NetUtil.isConnected()) {
            rlNoData.setVisibility(View.VISIBLE);
            ivNoData.setImageResource(R.drawable.icon_common_net_unconnected);
            tvNoData.setText("网络不太好，点击屏幕，刷新看看");
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        Observable<MessageBean> dataObservable = mZtkService.getMessages(cursor, 20);

        Subscription subscription = dataObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(MessageActivity.this, R.string.networkerror, Toast.LENGTH_SHORT).show();
                        rcv_simulation.notifyMoreFinished(true);
                    }

                    @Override
                    public void onNext(MessageBean messageBean) {
                        if ("1110002".equals(messageBean.code)) {
                            Toast.makeText(MessageActivity.this, R.string.sessionOutOfDateInfo, Toast.LENGTH_SHORT).show();
                        } else if ("1000000".equals(messageBean.code)) {
                            if (messageBean == null || messageBean.data == null || messageBean.data.resutls == null || messageBean.data.resutls.size() == 0) {
                                rlNoData.setVisibility(View.VISIBLE);
                                ivNoData.setImageResource(R.drawable.no_data_bg);
                                tvNoData.setText("暂无消息，点击刷新！");
                                Toast.makeText(MessageActivity.this, R.string.noMoreData, Toast.LENGTH_SHORT).show();
                            } else {
                                    rlNoData.setVisibility(View.GONE);
                                mCursor = messageBean.data.cursor;
                                mMessageListAdapter.getMessageList().addAll(messageBean.data.resutls);
//                                simulationAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(MessageActivity.this, R.string.toast_error_unknown, Toast.LENGTH_SHORT).show();
                        }

                        rcv_simulation.notifyMoreFinished(true);
                    }
                });

        mCompositeSubscription.add(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @Override
    public void onItemClick(View view, int position, int resId) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showEssayToast("网络未连接，请检查您的网络设置");
            return;
        }
        MessageBean.MessageBeanData data = mMessageListAdapter.getItem(position);
        WebActivity.newIntent(this, data.title, data.id);
    }

    public static void newIntent(Activity context) {
        Intent intent = new Intent(context, MessageActivity.class);
        context.startActivityForResult(intent, 130);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left_topbar:
                finish();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        mMessageListAdapter.getMessageList().clear();
        rcv_simulation.notifyDataSetChanged();

        rcv_simulation.setAutoLoadMoreEnable(true);
        mCursor = 0;
        loadMoreData(mCursor);
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
