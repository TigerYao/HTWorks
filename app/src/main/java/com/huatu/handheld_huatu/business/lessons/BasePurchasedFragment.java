package com.huatu.handheld_huatu.business.lessons;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.business.play.fragment.BaseIntroActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;
import com.huatu.handheld_huatu.business.ztk_vod.highmianshou.HuaTuXieYiActivity;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.mvpmodel.CourseTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseDiss;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.network.DataController;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//import com.huatu.handheld_huatu.business.ztk_vod.activity.MediaPlayActivity;
//import com.huatu.handheld_huatu.business.ztk_vod.baijiayun.activity.BJYMediaPlayActivity;

/**
 * Created by saiyuan on 2017/10/9.
 */

public abstract class BasePurchasedFragment extends BaseListFragment {
    protected int curPage = 1;
    protected CustomConfirmDialog confirmDialog;
    protected Dialog dialog;
    private PopupWindow popupWindow;
    private Button bt_hide;
    private TextView tv_hide_title;

    protected int courseType;

    @Override
    protected void onInitView() {
        super.onInitView();
        courseType = args.getInt("course_type");
        listView.setPullLoadEnable(false);
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<CourseMineBean.ResultBean>(
                UniApplicationContext.getContext(), dataList, R.layout.course_mine_item) {
            @Override
            public void convert(ViewHolder holder, final CourseMineBean.ResultBean mineItem, final int position) {
                //显示数据
                holder.setText(R.id.tv_item_course_mine_title, mineItem.title);
                //是否过期
                if (mineItem.isStudyExpired == 1) {
                    holder.setViewVisibility(R.id.iv_item_course_mine_gq, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.iv_item_course_mine_gq, View.GONE);
                }
                if (mineItem.courseType == 0) {
                    //录播课的购课详情页，和我的课程列表，如果是录播课，不显示时间了，只显示课时哈
                    holder.setText(R.id.tv_item_course_mine_timelength, mineItem.TimeLength);
                } else if (TextUtils.isEmpty(mineItem.endDate)) {
                    holder.setText(R.id.tv_item_course_mine_timelength, mineItem.startDate
                            + " (" + mineItem.TimeLength + ")");
                } else {
                    holder.setText(R.id.tv_item_course_mine_timelength, mineItem.startDate
                            + "-" + mineItem.endDate + " (" + mineItem.TimeLength + ")");
                }
                if (mineItem.iszhibo == 1) {
                    //是否为今日直播课
                    holder.setViewVisibility(R.id.iv, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.iv, View.GONE);
                }
                ImageView ivScaleimg = holder.getView(R.id.iv_item_course_mine_scaleimg);
                GlideApp.with(mContext).load(mineItem.scaleimg)


                        .placeholder(R.drawable.icon_default)
                        .skipMemoryCache(false)

                        .into(ivScaleimg);
                if (mineItem.oneToOne == 1) {
                    holder.setViewVisibility(R.id.tv_item_course_mine_one_to_one, View.VISIBLE);
                    holder.setColorText(R.id.tv_item_course_mine_one_to_one,
                            R.color.main_color, "填写信息卡");
                } else if (mineItem.oneToOne == 2) {
                    holder.setViewVisibility(R.id.tv_item_course_mine_one_to_one, View.VISIBLE);
                    holder.setColorText(R.id.tv_item_course_mine_one_to_one,
                            R.color.course_center_content, "查看信息卡");
                } else if (Method.isEqualString("1",mineItem.isMianshou)) {
                    holder.setViewVisibility(R.id.tv_item_course_mine_one_to_one, View.VISIBLE);
                    holder.setColorText(R.id.tv_item_course_mine_one_to_one,
                            R.color.course_center_content, "查看协议");
                } else {
                    holder.setViewVisibility(R.id.tv_item_course_mine_one_to_one, View.GONE);
                }
                holder.setViewOnClickListener(R.id.tv_item_course_mine_one_to_one,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mineItem.oneToOne == 1) {
                                    showOneToOneDlg(mineItem);
                                } else if (mineItem.oneToOne == 2) {
                                    startOneToOne(mineItem, false);
                                } else if (Method.isEqualString("1",mineItem.isMianshou)) {
                                    if (TextUtils.isEmpty(mineItem.treatyUrl)){
                                        return;
                                    }
                                    HuaTuXieYiActivity.newIntent(getActivity().getApplicationContext()
                                            , mineItem.treatyUrl);
                                }
                            }
                        });
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mineItem.oneToOne == 1) {
                            showOneToOneDlg(mineItem);
                            return;
                        }
                        if (!NetUtil.isConnected()) {
                            CommonUtils.showToast("网络错误，请检查您的网络");
                        }
                        Intent intent = new Intent();
                        intent.putExtra("course_id", mineItem.rid);
                        intent.putExtra("NetClassId", mineItem.NetClassId);
                        LogUtils.e("basePurchased", GsonUtil.GsonString(mineItem));
                        if (mineItem.isStudyExpired == 1 && mineItem.Status == -1) {
                            // 自定义弹窗
                            artdialog();
                        } else if (mineItem.isStudyExpired == 1 && mineItem.Status == 1) {
//                            intent.setClass(getActivity().getApplicationContext(),
//                                    BuyDetailsActivity.class);
                            intent.setClass(getActivity().getApplicationContext(),
                                    BaseIntroActivity.class);
                            intent.putExtra("Status", mineItem.Status);
                            startActivity(intent);
                        } /*else if (mineItem.isSuit == 1) {
                            intent.setClass(getActivity().getApplicationContext(), CourseSuitActivity.class);
                            startActivity(intent);
                        }*/ else if (mineItem.courseType == CourseTypeEnum.RECORDING.getValue()) {
                            //录播播放接口
                            //  initData(position, mineItem);
                            //百家云录播播放页面
                            startBJYMediaPlay(mineItem);
                        } else {
                            intent.setClass(mActivity, BJRecordPlayActivity.class);
                            intent.putExtra(ArgConstant.TYPE,1);
                            intent.putExtra("classid", mineItem.rid);
                            startActivity(intent);
                        }
                    }

                });
                holder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showHideWindow(position);
                        return true;
                    }
                });
            }
        };
    }

    private void startOneToOne(final CourseMineBean.ResultBean item, boolean isEdit) {
        OneToOnCourseInfoFragment fragment = new OneToOnCourseInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_edit", isEdit);
        bundle.putString("course_id", item.rid);
        bundle.putString("course_name", item.title);
        bundle.putString("order_number", item.orderNum);
        fragment.setArguments(bundle);
        startFragmentForResult(fragment);
    }

    private void showOneToOneDlg(final CourseMineBean.ResultBean item) {
        if (item == null) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = DialogUtils.createExitConfirmDialog(mActivity, null,
                    "此课程包含1对1内容，请尽快填写学员信息。学员可通过填写信息卡预约上课时间，" +
                            "上课老师等。若90天未填写学员信息卡，课程将失效且无法退款。", "取消", "去填写");
            confirmDialog.setContentGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        }
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOneToOne(item, true);
            }
        });
        if(confirmDialog != null) {
            confirmDialog.show();
        }
    }

    protected void artdialog() {
        LayoutInflater inflaterDl = LayoutInflater
                .from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflaterDl
                .inflate(R.layout.pop_window, null);
        dialog = new AlertDialog.Builder(
                getActivity(), R.style.dialog).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (t != null) {
                    t.cancel();
                }
            }
        }, 3000);
    }

    private void showHideWindow(final int position) {
        if (position < 0 || position >= dataList.size()) {
            return;
        }
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(getActivity()).inflate(
                    R.layout.course_mine_diss, null);
            popupWindow = new PopupWindow(contentView,
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setFocusable(true);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            popupWindow.setBackgroundDrawable(dw);
            // 设置背景颜色变暗
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.alpha = 0.4f;
            getActivity().getWindow().setAttributes(lp);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.alpha = 1f;
                    getActivity().getWindow().setAttributes(lp);
                }
            });
            bt_hide = (Button) contentView.findViewById(R.id.bt_dialog_sure);
            tv_hide_title = (TextView) contentView.findViewById(R.id.tv_dialog_content_top);
        }
        tv_hide_title.setText(((CourseMineBean.ResultBean) dataList.get(position)).title);
        bt_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHideCourse(position);
            }
        });
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    private void onClickHideCourse(final int position) {
        CourseMineBean.ResultBean courseMineItem = (CourseMineBean.ResultBean) dataList.get(position);
        DataController.getInstance().courseDiss(courseMineItem.NetClassId, courseMineItem.orderId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CourseDiss>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("隐藏失败");
                    }

                    @Override
                    public void onNext(CourseDiss courseDiss) {
                        if (courseDiss.code == 1000000) {
                            dataList.remove(position);
                            popupWindow.dismiss();
                            hideSuccessWindow();
                            mAdapter.setDataAndNotify(dataList);
                            if (Method.isListEmpty(dataList)) {

                                showEmptyView();
                                layoutErrorView.setErrorText("暂无相关课程");
                            }
                        }
                    }
                });
    }

    private void hideSuccessWindow() {
        LayoutInflater inflaterDl = LayoutInflater
                .from(getActivity());
        RelativeLayout layout = (RelativeLayout) inflaterDl
                .inflate(R.layout.yinchang_pop_window, null);
        dialog = new AlertDialog.Builder(
                getActivity(), R.style.dialog).create();
        dialog.show();
        dialog.getWindow().setContentView(layout);
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }, 3000);
    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    public boolean hasToolbar() {
        return false;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            onRefresh();
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

    @Override
    public void showProgressBar() {

    }

    @Override
    public void dismissProgressBar() {

    }

    @Override
    public void onSetData(Object respData) {

    }

    public abstract void getData(boolean isRefresh);

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
            compositeSubscription = null;
        }
        if (confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void initData(final int position, final CourseMineBean.ResultBean mineItem) {
        DataController.getInstance().getVodCoursePlay(Integer.parseInt(mineItem.NetClassId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<VodCoursePlayBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(VodCoursePlayBean vodCoursePlayBean) {
                        if (vodCoursePlayBean.code == 1000000) {
                            ArrayList<VodCoursePlayBean.LessionBean> livelessionBean = new ArrayList<VodCoursePlayBean.LessionBean>();
                            ArrayList<VodCoursePlayBean.LessionBean> lession = vodCoursePlayBean.data.lession;
                            ArrayList<VodCoursePlayBean.LessionBean> live = vodCoursePlayBean.data.live;
                            livelessionBean.addAll(live);
                            livelessionBean.addAll(lession);
                          //  Log.e("size", livelessionBean.size() + "``");
                            VodCoursePlayBean.CourseBean course = vodCoursePlayBean.data.course;
                            VodCoursePlayBean.QQBean qq = vodCoursePlayBean.data.QQ;
                            //跳转录播播放页
                            //startMediaPlay(position, mineItem, livelessionBean, lession, course, qq);
                        }
                    }
                });
    }

   /* private void startMediaPlay(int position, CourseMineBean.ResultBean mineItem,
                                ArrayList<VodCoursePlayBean.LessionBean> livelessionBean,
                                ArrayList<VodCoursePlayBean.LessionBean> lession,
                                VodCoursePlayBean.CourseBean course, VodCoursePlayBean.QQBean qqInfoBean) {
        Intent intent = new Intent(getActivity(), MediaPlayActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("current", -1);
        intent.putExtra("classid", mineItem.NetClassId);
        intent.putExtra("ClassTitle", mineItem.title);
        intent.putExtra("ClassScaleimg", mineItem.scaleimg);
        intent.putExtra("free", course.free);
        intent.putExtra("qqInfoBean", qqInfoBean.AndroidFunction);
        bundle.putSerializable("data", lession);
        bundle.putSerializable("livelession", livelessionBean);

        intent.putExtras(bundle);
        startActivity(intent);
    }*/

    private void startBJYMediaPlay(CourseMineBean.ResultBean mineItem) {
     /*   Intent intent = new Intent(getActivity(), BJYMediaPlayActivity.class);
        intent.putExtra("current", -1);
        intent.putExtra("classid", mineItem.NetClassId);
        startActivity(intent);*/
    }
}
