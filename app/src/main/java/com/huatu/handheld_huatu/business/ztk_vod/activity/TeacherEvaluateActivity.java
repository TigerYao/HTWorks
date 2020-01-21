package com.huatu.handheld_huatu.business.ztk_vod.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.TeacherEvaluateAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherPingjiaBean;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.view.XListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ht-djd on 2017/9/16.
 * 老师评价列表页
 */

public class TeacherEvaluateActivity extends BaseActivity implements XListView.IXListViewListener, View.OnClickListener {
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_star1)
    ImageView iv_star1;
    @BindView(R.id.iv_star2)
    ImageView iv_star2;
    @BindView(R.id.iv_star3)
    ImageView iv_star3;
    @BindView(R.id.iv_star4)
    ImageView iv_star4;
    @BindView(R.id.iv_star5)
    ImageView iv_star5;
    @BindView(R.id.tv_teacherfengshu)
    TextView tv_teacherfengshu;
    @BindView(R.id.xlv_pingjia)
    XListView xlv_pingjia;
    @BindView(R.id.rl_pingjia)
    RelativeLayout rl_pingjia;
    private ArrayList<TeacherPingjiaBean.DataBean.ResultBean.EvaluationBean> mEvaluateList = new ArrayList<>();
    private int currentPage = 1;
    private List<ImageView> starList = new ArrayList<>();
    private String lessionid;
    private TeacherEvaluateAdapter teacherEvaluateAdapter;
    private String classid;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_teacherevaluate;
    }

    @Override
    protected void onInitView() {
        ButterKnife.bind(this);
        classid = getIntent().getStringExtra("classid");
        lessionid = getIntent().getStringExtra("lessionid");
        teacherEvaluateAdapter = new
                TeacherEvaluateAdapter(TeacherEvaluateActivity.this, mEvaluateList);
        xlv_pingjia.setPullLoadEnable(false);
        xlv_pingjia.setPullRefreshEnable(true);
        xlv_pingjia.setXListViewListener(this);
        xlv_pingjia.setAdapter(teacherEvaluateAdapter);
        rl_pingjia.setOnClickListener(this);
        rl_back.setOnClickListener(this);
    }

    @Override
    protected void onLoadData() {
       showProgress();
        onRefresh();
    }
    private void initDataView(String lessiontitle, String photo_url, String teachername, int star, String score) {
        tv_title.setText(lessiontitle);
        if (score.equals("0")){
            tv_teacherfengshu.setText("(暂无评分)");
        }else{

            tv_teacherfengshu.setText(score+"分");
        }
        starList.add(iv_star1);
        starList.add(iv_star2);
        starList.add(iv_star3);
        starList.add(iv_star4);
        starList.add(iv_star5);
        if (String.valueOf(star).equals("1")) {
            starList.get(0).setBackgroundResource(R.drawable.teacher_kxing);
        } else if (String.valueOf(star).equals("0")) {
            starList.get(0).setBackgroundResource(R.drawable.teacher_kxing);
        } else {
            for (int i = 1; i <= star / 2; i++) {
                if (star % 2 == 1) {
                    starList.get(i - 1).setBackgroundResource(R.drawable.teacher_xing);
                    starList.get(i).setBackgroundResource(R.drawable.teacher_bbxing);

                } else if (star % 2 == 0) {
                    starList.get(i - 1).setBackgroundResource(R.drawable.teacher_xing);
                }
            }
        }
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
        getData(true);
    }

    @Override
    public void onLoadMore() {
        getData(false);
    }
    private void getData(final boolean isRefresh) {
        if (isRefresh) {
            currentPage = 1;
        }
        DataController.getInstance().getTeacherPingjia(Integer.parseInt(classid), Integer.parseInt(lessionid), currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TeacherPingjiaBean>() {


                    @Override
                    public void onCompleted() {
                        hideProgress();
                        xlv_pingjia.stopLoadMore();
                        xlv_pingjia.stopRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        xlv_pingjia.stopLoadMore();
                        xlv_pingjia.stopRefresh();
                    }

                    @Override
                    public void onNext(TeacherPingjiaBean teacherPingjiaBean) {
                        if (teacherPingjiaBean.getCode() == 1000000) {
                            String lessiontitle = teacherPingjiaBean.getData().getResult().getLessiontitle();
                            String photo_url = teacherPingjiaBean.getData().getResult().getPhoto_url();
                            String teachername = teacherPingjiaBean.getData().getResult().getTeachername();
                            String teacherid = teacherPingjiaBean.getData().getResult().getTeacherid();
                            int star = teacherPingjiaBean.getData().getResult().getStar();
                            String score = teacherPingjiaBean.getData().getResult().getScore();
                            int flag = teacherPingjiaBean.getData().getResult().getFlag();
                            initDataView(lessiontitle, photo_url, teachername, star, score);
                            xlv_pingjia.stopLoadMore();
                            xlv_pingjia.stopRefresh();
                            ArrayList<TeacherPingjiaBean.DataBean.ResultBean.EvaluationBean> evaluation
                                    = teacherPingjiaBean.getData().getResult().getEvaluation();
                            if (isRefresh) {
                                mEvaluateList.clear();
                                mEvaluateList.addAll(evaluation);
                                xlv_pingjia.setSelection(0);
                            } else {
                                mEvaluateList.addAll(evaluation);
                            }
                            if (teacherPingjiaBean.getData().getNext() == 1) {
                                currentPage++;
                                xlv_pingjia.setPullLoadEnable(true);
                            } else {
                                xlv_pingjia.setPullLoadEnable(false);
                            }
                            hideProgress();
                            teacherEvaluateAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
    /**
     * @param context
     * @param classid   课程id
     * @param lessionid 课件id
     * @param type      类型
     */
    public static void newIntent(Context context, String classid, String lessionid, String type) {
        Intent intent = new Intent(context, TeacherEvaluateActivity.class);
        intent.putExtra("classid", classid);
        intent.putExtra("lessionid", lessionid);
        intent.putExtra("type", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_pingjia:
                LessionEvaluateActivity.newIntent(TeacherEvaluateActivity.this, classid, lessionid);
                break;
        }
    }
}
