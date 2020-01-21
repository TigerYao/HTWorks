package com.huatu.handheld_huatu.business.essay.mainfragment;


import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.business.essay.adapter.DownLoadEssayAdapter;
import com.huatu.handheld_huatu.business.essay.bhelper.DlEssayDataCache;
import com.huatu.handheld_huatu.business.essay.bhelper.DownLoadEssayHelper;
import com.huatu.handheld_huatu.mvpmodel.essay.DownloadEssayBean;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.handheld_huatu.view.MultiItemTypeSupport;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.handheld_huatu.view.swiperecyclerview.swipemenu.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by chq on 2018/7/19.
 * 申论试卷下载
 */
public class EssayDownLoadFragment extends BaseFragment implements DownLoadEssayHelper.DownLoadUpdate, View.OnClickListener {

    private static final String TAG = "DownLoadEssay";

    private TopActionBar mTitleBar;

    @BindView(R.id.down_essay_counts)
    protected TextView down_essay_count;
    @BindView(R.id.tv_single)
    protected TextView tv_single;
    @BindView(R.id.tv_multi)
    protected TextView tv_multi;
    @BindView(R.id.tv_argue)
    protected TextView tv_argue;
    @BindView(R.id.iv_tab)
    protected ImageView iv_tab;

    @BindView(R.id.layout_nodata)
    protected RelativeLayout layout_nodata;

    @BindView(R.id.base_list_view_id)
    protected ListViewForScroll listView;                           // 下载失败、下载中列表

    protected CommonAdapter<DownloadEssayBean> mAdapter;            // 下载失败、下载中列表adapter
    private List<DownloadEssayBean> showList = new ArrayList<>();   // 下载失败、下载中列表数据

    protected SwipeMenuRecyclerView srv_down_success;               // 下载成功列表
    private DownLoadEssayAdapter mDownloadAdapter;                  // 下载成功adapter

    private List<TextView> listTV = new ArrayList<>();

    private DlEssayDataCache mDownLoadEssayCahce;                   // 试卷下载的数据
    private DownLoadEssayHelper mDownLoadEssayHelper;               // 下载帮助类（开始下载，下载进度回调）
    private int mType = 0;
    int currIndex;
    private int one;                        // 两个相邻页面的偏移量
    private int offset;                     // 图片移动的偏移量
    private int bmpW;                       // 横线图片宽度
    private int offset1;
    private int currentstate = 0;

    @Override
    public int onSetRootViewId() {
        return R.layout.download_essay_fragment;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        initTitleBar();
        initTVList();
        initListener();
        mDownLoadEssayCahce = DlEssayDataCache.getInstance();
        mDownLoadEssayHelper = DownLoadEssayHelper.getInstance();
        mDownLoadEssayHelper.setmDownLoadUpdate(this);
        mDownLoadEssayCahce.downType = mType;
        initListData();
        initSwipeRecyclerView();
        initAdapter();
        initSuccessCount();
        showNoDataView();
        getActivity().getWindow()
                .getDecorView()
                .getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        initTab();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            getActivity().getWindow()
                                    .getDecorView()
                                    .getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        } else {
                            getActivity().getWindow()
                                    .getDecorView()
                                    .getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                });
        startAnimation(0);
    }

    private void initTitleBar() {
        mTitleBar = rootView.findViewById(R.id.fragment_title_bar);
        mTitleBar.setTitle("下载");
        mTitleBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    private void initTVList() {
        listTV.add(tv_single);
        listTV.add(tv_multi);
        listTV.add(tv_argue);
    }

    private void initListener() {
        tv_single.setOnClickListener(this);
        tv_multi.setOnClickListener(this);
        tv_argue.setOnClickListener(this);
    }

    private void initListData() {
        if (mType == 0) {
            // 标准答案下载
            showList.clear();
            showList.addAll(mDownLoadEssayCahce.dl_failList);
            showList.addAll(mDownLoadEssayCahce.dl_ingList);
            showList.addAll(mDownLoadEssayCahce.dl_successList);
        } else if (mType == 1) {
            // 套题下载
            showList.clear();
            showList.addAll(mDownLoadEssayCahce.multi_dl_failList);
            showList.addAll(mDownLoadEssayCahce.multi_dl_ingList);
            showList.addAll(mDownLoadEssayCahce.multi_dl_successList);
        } else if (mType == 2) {
            // 文章写作下载
            showList.clear();
            showList.addAll(mDownLoadEssayCahce.argue_dl_failList);
            showList.addAll(mDownLoadEssayCahce.argue_dl_ingList);
            showList.addAll(mDownLoadEssayCahce.argue_dl_successList);
        }
    }

    private void initSwipeRecyclerView() {
        mDownloadAdapter = new DownLoadEssayAdapter(mActivity);
        srv_down_success = rootView.findViewById(R.id.srv_down_success);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        srv_down_success.setLayoutManager(layoutManager);
        srv_down_success.setPullRefreshEnabled(false);
        srv_down_success.setLoadingMoreEnabled(false);
        srv_down_success.setSwipeDirection(SwipeMenuRecyclerView.DIRECTION_LEFT);//左滑（默认）
        srv_down_success.setHasFixedSize(true);
        srv_down_success.setAdapter(mDownloadAdapter);
        initAdapterData();
    }

    private void initAdapter() {
        mAdapter = new CommonAdapter<DownloadEssayBean>(mActivity.getApplicationContext(),
                showList, new MultiItemTypeSupport<DownloadEssayBean>() {
            @Override
            public int getLayoutId(int position, DownloadEssayBean downloadEssayBean) {
                if (downloadEssayBean.getStatus() == DownloadEssayBean.DOWNLOAD_ING) {
                    return R.layout.download_essay_fragitem_ing_layout;
                } else if (downloadEssayBean.getStatus() == DownloadEssayBean.DOWNLOAD_FAIL) {
                    return R.layout.download_essay_fragitem_fail_layout;
                } else {
                    return R.layout.download_essay_fragitem_ing_layout;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 3;
            }

            @Override
            public int getItemViewType(int position, DownloadEssayBean downloadEssayBean) {
                return downloadEssayBean.getStatus();
            }
        }) {
            @Override
            public void convert(ViewHolder holder, final DownloadEssayBean item, final int position) {
                if (item.getStatus() == DownloadEssayBean.DOWNLOAD_ING || item.getStatus() == DownloadEssayBean.DOWNLOAD_WAIT) {
                    holder.setText(R.id.downing_essay_title, item.title);
//                    holder.setText(R.id.downing_essay_progress,item.curFileSize+"/"+item.fileSize);
                    holder.setText(R.id.downing_essay_progress, item.downProgress + "%   ");
                } else if (item.getStatus() == DownloadEssayBean.DOWNLOAD_FAIL) {
                    holder.setText(R.id.down_fail_essay_title, item.title);
                    holder.setViewOnClickListener(R.id.down_fail_essay_tip, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DownLoadEssayHelper.getInstance().againDowningFailEssay(item);
                        }
                    });
                } else {
                    holder.setViewVisibility(R.id.downing_essay_title, View.GONE);
                    holder.setViewVisibility(R.id.downing_essay_progress, View.GONE);
                }
            }
        };

        listView.setAdapter(mAdapter);
        mDownLoadEssayHelper.nextDowningEssay();
    }

    /**
     * 初始化动画位置
     */
    public void initTab() {
        iv_tab.setVisibility(View.VISIBLE);
    }

    public void startAnimation(int position) {
        int scrollX;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_tab.getLayoutParams();
        if (position == 1) {
            params.width = DisplayUtil.dp2px(30);
            iv_tab.setLayoutParams(params);
        } else {
            params.width = DisplayUtil.dp2px(60);
            iv_tab.setLayoutParams(params);
        }
        bmpW = iv_tab.getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        one = screenW / 3;
        offset1 = (screenW / 3 - DisplayUtil.dp2px(30)) / 2;
        offset = (screenW / 3 - DisplayUtil.dp2px(60)) / 2;

        if (position == 1) {
            scrollX = position * one + offset1;
        } else {
            scrollX = position * one + offset;
        }

        Animation animation = new TranslateAnimation(currentstate, scrollX, 0, 0);
        currentstate = scrollX;
        currIndex = position;
        animation.setFillAfter(true);       // 动画终止时停留在最后一帧，不然会回到没有执行前的状态
        animation.setDuration(200);         // 动画持续时间0.2秒
        iv_tab.startAnimation(animation);   // 是用ImageView来显示动画的
        setColor();
        listTV.get(position).setSelected(true);
    }

    public void setColor() {
        for (int i = 0; i < listTV.size(); i++) {
            listTV.get(i).setSelected(false);
        }
    }

    @Override
    public void updateView(int progress) {
        if (progress > 0) {
            DownloadEssayBean var = mDownLoadEssayCahce.getmDownloadingEssayBean();
            if (var != null) {
                var.downProgress = progress;
            }
            mAdapter.notifyDataSetChanged();
            LogUtils.d(TAG, "progress:" + progress);
        } else {
            refreshView();
        }
    }

    public void refreshView() {
        mAdapter.clear();
        initListData();
        mAdapter.setDataAndNotify(showList);
        initSuccessCount();
        showNoDataView();
        initAdapterData();
    }

    private void showNoDataView() {
        if (showList != null && showList.size() > 0) {
            layout_nodata.setVisibility(View.GONE);
        } else {
            layout_nodata.setVisibility(View.VISIBLE);
        }
    }

    private void initSuccessCount() {
        if (mType == 0) {
            down_essay_count.setText("已下载（" + mDownLoadEssayCahce.dl_successList.size() + ")");
        } else if (mType == 1) {
            down_essay_count.setText("已下载（" + mDownLoadEssayCahce.multi_dl_successList.size() + ")");
        } else if (mType == 2) {
            down_essay_count.setText("已下载（" + mDownLoadEssayCahce.argue_dl_successList.size() + ")");
        }
    }

    private void initAdapterData() {
        if (mType == 0) {
            mDownloadAdapter.clear();
            mDownloadAdapter.setData(mDownLoadEssayCahce.dl_successList);
        } else if (mType == 1) {
            mDownloadAdapter.clear();
            mDownloadAdapter.setData(mDownLoadEssayCahce.multi_dl_successList);
        } else {
            mDownloadAdapter.clear();
            mDownloadAdapter.setData(mDownLoadEssayCahce.argue_dl_successList);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_single:
                if (mType != 0) {
                    mType = 0;
                    tv_single.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                    tv_multi.setTextColor(ContextCompat.getColor(mActivity, R.color.black250));
                    tv_argue.setTextColor(ContextCompat.getColor(mActivity, R.color.black250));
                    startAnimation(0);
                    readFromFile(0);
                    refreshView();
                    writeToFile(0);
                }
                break;
            case R.id.tv_multi:
                if (mType != 1) {
                    mType = 1;
                    tv_multi.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                    tv_single.setTextColor(ContextCompat.getColor(mActivity, R.color.black250));
                    tv_argue.setTextColor(ContextCompat.getColor(mActivity, R.color.black250));
                    startAnimation(1);
                    readFromFile(1);
                    refreshView();
                    writeToFile(1);
                }
                break;
            case R.id.tv_argue:
                if (mType != 2) {
                    mType = 2;
                    tv_argue.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
                    tv_single.setTextColor(ContextCompat.getColor(mActivity, R.color.black250));
                    tv_multi.setTextColor(ContextCompat.getColor(mActivity, R.color.black250));
                    startAnimation(2);
                    readFromFile(0);
                    refreshView();
                    writeToFile(2);
                }
                break;
        }
    }

    private void readFromFile(int type) {
        if (mDownLoadEssayCahce != null) {
            if (type == 0) {
                mDownLoadEssayCahce.readSingleFromFile();
            } else if (type == 1) {
                mDownLoadEssayCahce.readMultiFromFile();
            } else if (type == 2) {
                mDownLoadEssayCahce.readArgueFromFile();
            }
        }
    }

    private void writeToFile(int type) {
        if (mDownLoadEssayCahce != null) {
            if (type == 0) {
                mDownLoadEssayCahce.writeMultiToFile();
                mDownLoadEssayCahce.writeArgueToFile();
            } else if (type == 1) {
                mDownLoadEssayCahce.writeSingleToFile();
                mDownLoadEssayCahce.writeArgueToFile();
            } else if (type == 2) {
                mDownLoadEssayCahce.writeSingleToFile();
                mDownLoadEssayCahce.writeMultiToFile();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDownLoadEssayCahce != null) {
            mDownLoadEssayCahce.writeToFileCache();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "  onDestroy");
        if (mDownLoadEssayCahce != null) {
            mDownLoadEssayCahce.writeToFileCache();
        }
        if (mDownLoadEssayHelper != null) {
            mDownLoadEssayHelper.setmDownLoadUpdate(null);
            mDownLoadEssayHelper.clearData();
        }
    }
}
