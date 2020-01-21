package com.huatu.handheld_huatu.business.ztk_zhibo.play.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baijiahulian.livecore.models.LPAnswerSheetModel;
import com.huatu.handheld_huatu.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveVideoTestResultView extends FrameLayout {
    @BindView(R.id.user_count)
    TextView mUserCountTv;
    @BindView(R.id.right_percent)
    TextView mRightPercent;
    @BindView(R.id.result_detail_rv)
    RecyclerView mDetailList;

    private LiveTestResultAdapter mAdapter;

    public LiveVideoTestResultView(@NonNull Context context) {
        this(context, null);
    }

    public LiveVideoTestResultView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveVideoTestResultView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.layout_videotest_result, this);
        ButterKnife.bind(this);

    }

    public void setData(LPAnswerSheetModel model) {
        if (mAdapter == null) {
            mAdapter = new LiveTestResultAdapter(getContext(), model);
            mDetailList.setLayoutManager(new LinearLayoutManager(getContext()));
            mDetailList.setAdapter(mAdapter);
        } else
            mAdapter.setData(model);
    }
}
