package com.huatu.handheld_huatu.business.ztk_vod.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.TeacherLishiAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherDefenBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherLishiBean;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.view.XListViewNestedS;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ht-djd on 2017/9/6.
 *教师评价列表
 */

public class TeacherEvaluateView implements XListViewNestedS.IXListViewListener {
    private View mView;
    private Context mContext;
    private String teacherId;
    private ImageView iv_star1;
    private ImageView iv_star2;
    private ImageView iv_star3;
    private ImageView iv_star4;
    private ImageView iv_star5;
    private List<ImageView> starList = new ArrayList<>();
    private int star;
    private TextView tv_teacherfengshu;
    private int currentPage = 1;
    private XListViewNestedS xlv_teacherevaluate;
    ArrayList<TeacherLishiBean.history_course> mTeacherLishiList = new ArrayList<>();
    private TeacherDefenBean.DataBean teacherDefen;
    private TeacherLishiAdapter teacherLishiAdapter;

    public TeacherEvaluateView(Context context, View view, String personID) {
        this.mContext = context;
        this.mView = view;
        this.teacherId = personID;
        initView();
        initData(Integer.parseInt(teacherId));
        onRefresh();
    }
    private void initData(int teacherId) {
        DataController.getInstance().getTeacherDefen(teacherId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TeacherDefenBean>() {


                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TeacherDefenBean teacherDefenBean) {
                        if (teacherDefenBean.code == 1000000) {
                            teacherDefen = teacherDefenBean.data;
                            initNext();


                        }
                    }

                });
    }

    private void initView() {
        xlv_teacherevaluate = (XListViewNestedS) mView.findViewById(R.id.xlv_teacherevaluate);
        tv_teacherfengshu = (TextView) mView.findViewById(R.id.tv_teacherfengshu);
        teacherLishiAdapter = new TeacherLishiAdapter(mContext, mTeacherLishiList,teacherId);
        xlv_teacherevaluate.setHeaderDividersEnabled(false);
        xlv_teacherevaluate.setPullLoadEnable(false);
        xlv_teacherevaluate.setPullRefreshEnable(true);
        xlv_teacherevaluate.setXListViewListener(this);
        xlv_teacherevaluate.setAdapter(teacherLishiAdapter);
        iv_star1 = (ImageView) mView.findViewById(R.id.iv_star1);
        iv_star2 = (ImageView) mView.findViewById(R.id.iv_star2);
        iv_star3 = (ImageView) mView.findViewById(R.id.iv_star3);
        iv_star4 = (ImageView) mView.findViewById(R.id.iv_star4);
        iv_star5 = (ImageView) mView.findViewById(R.id.iv_star5);
        starList.add(iv_star1);
        starList.add(iv_star2);
        starList.add(iv_star3);
        starList.add(iv_star4);
        starList.add(iv_star5);


    }

    private void initNext() {
        star = teacherDefen.star;
        if (String.valueOf(teacherDefen.star).equals("1")) {
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

        if (teacherDefen.overall_ratings.equals("0")){
            tv_teacherfengshu.setText("(暂无评分)");
        }else{
            tv_teacherfengshu.setText(teacherDefen.overall_ratings+"分");
        }

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
        if(isRefresh) {
            currentPage = 1;
        }
        DataController.getInstance().getTeacherLishi(Integer.parseInt(teacherId), currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TeacherLishiBean>() {
                    @Override
                    public void onCompleted() {
                        xlv_teacherevaluate.stopLoadMore();
                        xlv_teacherevaluate.stopRefresh();
                    }

                    @Override
                    public void onError(Throwable e) {
                        xlv_teacherevaluate.stopLoadMore();
                        xlv_teacherevaluate.stopRefresh();
                    }

                    @Override
                    public void onNext(TeacherLishiBean teacherLishiBean) {
                        if (teacherLishiBean.code == 1000000) {
                            ArrayList<TeacherLishiBean.history_course> result = teacherLishiBean.data.result;
                            xlv_teacherevaluate.stopLoadMore();
                            xlv_teacherevaluate.stopRefresh();
                            if(isRefresh) {
                                mTeacherLishiList.clear();
                                mTeacherLishiList.addAll(result);
                                xlv_teacherevaluate.setSelection(0);
                            } else {
                                mTeacherLishiList.addAll(result);
                            }
                            if(teacherLishiBean.data.next == 1) {
                                currentPage++;
                                xlv_teacherevaluate.setPullLoadEnable(true);
                            } else {
                                xlv_teacherevaluate.setPullLoadEnable(false);
                            }
                            teacherLishiAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
