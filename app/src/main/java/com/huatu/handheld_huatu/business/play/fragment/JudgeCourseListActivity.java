package com.huatu.handheld_huatu.business.play.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subscriptions.CompositeSubscription;

public class JudgeCourseListActivity extends Activity implements View.OnClickListener{
    private final String TAG = "httpTecherCommentActivity";
    @BindView(R.id.techer_comment_back)
    ImageView backImage;
    @BindView(R.id.comment_count)
    TextView comment_count;
    @BindView(R.id.expandLayout)
    HistoryCourseExpandLayout historyCourseExpandLayout;
    protected CompositeSubscription compositeSubscription;
    private Unbinder unbinder;
    private String teacherId;
    private int pageSize = 20;

    public static void startJudgeCourseListActivity(Context ctx, String teacherId) {
        Intent intent = new Intent(ctx, JudgeCourseListActivity.class);
        intent.putExtra("teacherid", teacherId);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_techer_comment_courses);
        unbinder = ButterKnife.bind(this);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        teacherId = getIntent().getStringExtra("teacherid");
        initView();
        initData();
    }

    private void initView() {
        comment_count.setText("");
        backImage.setClickable(true);
        backImage.setOnClickListener(this);
        historyCourseExpandLayout.setRefreshAndLoad(true);
    }

    private void initData() {
       // getCourseList();
        historyCourseExpandLayout.loadData(compositeSubscription, teacherId, pageSize);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    @Override
    public void onClick(View v) {
        LogUtils.d(TAG, "onclick  v.getId() == R.id.techer_comment_back is : " + (v.getId() == R.id.techer_comment_back));
        if (v.getId() == R.id.techer_comment_back) {
            finish();
        }
    }
}
