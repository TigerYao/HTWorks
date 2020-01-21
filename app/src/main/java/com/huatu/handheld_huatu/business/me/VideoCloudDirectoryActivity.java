package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.mvpmodel.VideoCloudDirectoryBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.view.XListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ljzyuhenda on 16/9/20.
 */
public class VideoCloudDirectoryActivity extends BaseActivity implements  XListView.IXListViewListener {
    public static final String BNO = "bno";

    @BindView(R.id.common_list_view_toolbar_id)
    protected TopActionBar topActionBar;
    @BindView(R.id.base_list_view_id)
    protected XListView listView;

    private String mBno;
    private int mPage = 1;
    private CompositeSubscription mCompositeSubscription;
    private  List<VideoCloudDirectoryBean.VideoCloudDirectory> videoCloudSingleBeanList;
    private   CommonAdapter  mAdapter;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_videoclouddirectory;
    }

    @Override
    public void onInitView() {
        ButterKnife.bind(this);
        videoCloudSingleBeanList=new ArrayList<>();
        topActionBar.setTitle("视频目录");
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                 finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
            }
        });
        mBno = getIntent().getStringExtra(BNO);
        mAdapter= new CommonAdapter<VideoCloudDirectoryBean.VideoCloudDirectory>(this,
                videoCloudSingleBeanList, R.layout.item_direcory_videocloud) {
            @Override
            public void convert(ViewHolder holder,final VideoCloudDirectoryBean.VideoCloudDirectory item, final int position) {
                holder.setText(R.id.tv_content, item.title);
                holder.setViewOnClickListener(R.id.tv_content, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String videoNo = item.no;
                        Intent  intent1 = new Intent();
                        intent1.putExtra("course_id",videoNo);
                        intent1.putExtra("play_index",position);
                        intent1.putExtra("classid", String.valueOf(videoNo));
                        intent1.putExtra(ArgConstant.TYPE,1);
                        intent1.setClass(VideoCloudDirectoryActivity.this, BJRecordPlayActivity.class);
                        startActivity(intent1);
                    }
                });
            }
        };
        listView.setAdapter(mAdapter);
        listView.setHeaderDividersEnabled(false);
        listView.setPullLoadEnable(true);
        listView.setPullRefreshEnable(true);
        listView.setXListViewListener(this);
    }

    @Override
    protected void onLoadData() {
        super.onLoadData();
        loadData(mPage);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_left:
                break;
        }
    }

    public static void newIntent(Context context, String bno) {
        Intent intent = new Intent(context, VideoCloudDirectoryActivity.class);
        intent.putExtra(BNO, bno);
        context.startActivity(intent);
    }

    public void loadData(int page) {
        showProgress();
        Observable<VideoCloudDirectoryBean> videoCloudDirectoryBean = RetrofitManager.getInstance().getShopService().getVideoCloudDirectoryBean(mBno, page);
        videoCloudDirectoryBean.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VideoCloudDirectoryBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        hideProgress();
                        CommonUtils.showToast("加载失败");
                    }

                    @Override
                    public void onNext(VideoCloudDirectoryBean videoCloudDirectoryBean) {
                        hideProgress();
                        //返回数据：code -1 参数错误，0 暂无数据，1 正常返回数据
                        if (videoCloudDirectoryBean != null) {
                            if ("1".equals(videoCloudDirectoryBean.code)) {
                                mAdapter.addData(videoCloudDirectoryBean.data);
                                listView.setPullLoadEnable(true);
                                listView.stopRefresh();
                            } else if ("0".equals(videoCloudDirectoryBean.code)) {
                                CommonUtils.showToast("暂无数据");
                                listView.setPullLoadEnable(false);
                                listView.stopLoadMore();
                            } else if ("-1".equals(videoCloudDirectoryBean.code)) {
                                CommonUtils.showToast("加载失败");
                            } else {
                                CommonUtils.showToast("加载失败");
                            }
                        } else {
                            CommonUtils.showToast("加载失败");
                        }
                    }
                });
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
    public void onRefresh() {
        mPage=1;
        loadData(mPage);
    }

    @Override
    public void onLoadMore() {
        loadData(++mPage);
    }
}
