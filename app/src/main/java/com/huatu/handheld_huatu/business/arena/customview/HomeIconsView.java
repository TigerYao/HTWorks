package com.huatu.handheld_huatu.business.arena.customview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.arena.adapter.PracticeNameAdapter;
import com.huatu.handheld_huatu.business.arena.newtips.bean.TipBean;
import com.huatu.handheld_huatu.datacache.SignUpTypeDataCache;
import com.huatu.handheld_huatu.datacache.model.HomeIconBean;
import com.huatu.handheld_huatu.view.CustomScrollBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

public class HomeIconsView extends LinearLayout {

    @BindView(R.id.recycler_view_names)
    RecyclerView mRecyclerView;

    // 自定义的进度条
    @BindView(R.id.scroll_bar)
    CustomScrollBar scrollBar;

    private PracticeNameAdapter mRecyslerAdapter;
    private CompositeSubscription compositeSubscription;

    private Context mContext;
    private View rootView;
    private int resId = R.layout.layout_home_icons_ll;
    private List<HomeIconBean> listData;

    public HomeIconsView(Context context) {
        super(context);
        init(context);
    }

    public HomeIconsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeIconsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        mContext = ctx;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(rootView);

        compositeSubscription = new CompositeSubscription();

        listData = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //设置RecyclerView 布局
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyslerAdapter = new PracticeNameAdapter(mContext, listData, new PracticeNameAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (listData == null || position < 0 || position >= listData.size()) return;
                int paperType = listData.get(position).requestType;
                SignUpTypeDataCache.getInstance().startActivity(mContext, paperType, compositeSubscription);
            }
        });
        mRecyclerView.setAdapter(mRecyslerAdapter);
    }

    public void updateViews(ArrayList<TipBean> tipBeans, List<HomeIconBean> homeIconsList) {
        if (homeIconsList != null && homeIconsList.size() > 0) {
            listData.clear();
            listData.addAll(homeIconsList);
        } else {
            if (listData.size() == 0) {
                listData.addAll(SignUpTypeDataCache.getInstance().getHomeIconsList());
            }
        }

        if (tipBeans != null && tipBeans.size() > 0) {
            for (TipBean tipBean : tipBeans) {
                for (HomeIconBean homeIconBean : listData) {
                    if (homeIconBean.requestType == tipBean.type) {
                        homeIconBean.tipNum = tipBean.tipNum;
                    }
                }
            }
        } else {
            for (HomeIconBean homeIconBean : listData) {
                homeIconBean.tipNum = 0;
            }
        }
        if (listData.size() > 0) {
            mRecyslerAdapter.notifyDataSetChanged();
            scrollBar.setRecycleView(mRecyclerView);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
    }
}
