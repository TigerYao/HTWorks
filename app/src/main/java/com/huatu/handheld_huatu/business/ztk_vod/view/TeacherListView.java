package com.huatu.handheld_huatu.business.ztk_vod.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.adapter.TeacherDetailListAdapter;
import com.huatu.handheld_huatu.business.ztk_vod.bean.TeacherDetailListBean;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.view.XListView;
import com.huatu.handheld_huatu.view.XListViewNestedS;

import java.util.ArrayList;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ht-djd on 2017/9/6.
 * 教师目录
 */

public class TeacherListView implements XListViewNestedS.IXListViewListener {
    private View mView;
    private String teacherId;
    private XListViewNestedS xlv_teacherlist;
    private Context mContext;
    private int currentPage = 1;
    private ArrayList<TeacherDetailListBean.ResultBean> mTeacherDetailList = new ArrayList<>();
    private TeacherDetailListAdapter teacherDetailListAdapter;
    private LinearLayout ll_down_no;

    public TeacherListView(Context context, View view, String personID) {
        this.mContext = context;
        this.mView = view;
        this.teacherId = personID;
        initView();
        onRefresh();
    }
    private void initView() {
        ll_down_no = (LinearLayout)mView.findViewById(R.id.ll_down_no);
        xlv_teacherlist = (XListViewNestedS) mView.findViewById(R.id.xlv_list);
        teacherDetailListAdapter = new TeacherDetailListAdapter(mContext, mTeacherDetailList);
        xlv_teacherlist.setPullLoadEnable(false);
        xlv_teacherlist.setPullRefreshEnable(true);
        xlv_teacherlist.setXListViewListener(this);
        xlv_teacherlist.setAdapter(teacherDetailListAdapter);
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
        DataController.getInstance().getTeacherDetailList(Integer.parseInt(teacherId), currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TeacherDetailListBean>() {
                    @Override
                    public void onCompleted() {
                        xlv_teacherlist.stopLoadMore();
                        xlv_teacherlist.stopRefresh();
                        if (mTeacherDetailList.isEmpty()) {
                            ll_down_no.setVisibility(View.VISIBLE);
                            xlv_teacherlist.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        xlv_teacherlist.stopLoadMore();
                        xlv_teacherlist.stopRefresh();
                        if (mTeacherDetailList.isEmpty()) {
                            ll_down_no.setVisibility(View.VISIBLE);
                            xlv_teacherlist.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNext(TeacherDetailListBean teacherDetailListBean) {
                        if (teacherDetailListBean.code == 1000000) {
                            ArrayList<TeacherDetailListBean.ResultBean> result = teacherDetailListBean.data.result;
                            xlv_teacherlist.stopLoadMore();
                            xlv_teacherlist.stopRefresh();
                            if (Method.isListEmpty(result)) {
                                ll_down_no.setVisibility(View.VISIBLE);
                                xlv_teacherlist.setVisibility(View.GONE);
                            } else {
                                ll_down_no.setVisibility(View.GONE);
                                xlv_teacherlist.setVisibility(View.VISIBLE);
                            }
                            if(isRefresh) {
                                mTeacherDetailList.clear();
                                mTeacherDetailList.addAll(result);
                                xlv_teacherlist.setSelection(0);
                            } else {
                                mTeacherDetailList.addAll(result);
                            }

                            if(teacherDetailListBean.data.next == 1) {
                                currentPage++;
                                xlv_teacherlist.setPullLoadEnable(true);
                            } else {
                                xlv_teacherlist.setPullLoadEnable(false);
                            }
                            teacherDetailListAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}
