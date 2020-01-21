package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CalenderCourseBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CourseCalenderBean;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.CourseCalenderTimeBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.utils.ArrayUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2017/9/8.
 */

public class CourseCalenderFragment extends BaseFragment {

    @BindView(R.id.course_calender_title_bar)
    TopActionBar topActionBar;

    @BindView(R.id.course_calender_grid_view)
    GridView mGridView;

    CommonAdapter<CourseCalenderTimeBean> gridAdapter;
    List<CourseCalenderBean> mCourseCalenderList = new ArrayList<>();
    List<CourseCalenderTimeBean> dateList = new ArrayList<>();

    @BindView(R.id.course_calender_list_view)
    ListView listView;
    CommonAdapter<CalenderCourseBean> listAdapter;

    List<CalenderCourseBean> courseList = new ArrayList<>();
  //  CompositeSubscription mCompositeSubscription;
    private long currentServerTime = System.currentTimeMillis();
    private int colorToday;
    private int colorNormal;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mLoadView;

    protected CompositeSubscription getSubscription(){
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        return compositeSubscription;
    }
    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_course_calender_layout;
    }

    private String mCurrentRids;
    @Override
    protected void onInitView() {
        super.onInitView();
        initTitleBar();

        mLoadView.setStatusStringId(R.string.xs_loading_text, R.string.xs_none_calendercourse);
        mLoadView.setTipText(R.string.xs_my_empty);
        mLoadView.setEmptyImg(R.drawable.down_no_num);
        mLoadView.setOnRtyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(ArrayUtils.isEmpty(mCourseCalenderList))
                   onLoadData();
                 else if(!TextUtils.isEmpty(mCurrentRids)){
                     getCalenderCourse(mCurrentRids);
                 }
            }
        });

        colorToday = Color.parseColor("#FF6D73");
        colorNormal = Color.parseColor("#4A4A4A");
        gridAdapter = new CommonAdapter<CourseCalenderTimeBean>(dateList, R.layout.item_calender_grid_layout) {
            @Override
            public void convert(ViewHolder holder, final CourseCalenderTimeBean item, final int position) {
                holder.setText(R.id.calender_course_item_date, item.shortDate);
                if(item.isToday) {
                    holder.setTextColor(R.id.calender_course_item_date, colorToday);
                } else {
                    holder.setTextColor(R.id.calender_course_item_date, colorNormal);
                }
                if(item.hasCourse) {
                    holder.setViewVisibility(R.id.calender_course_has_flag, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.calender_course_has_flag, View.GONE);
                }
                if(item.isSelected) {
                    holder.setViewBackgroundRes(R.id.calender_course_item_date, R.drawable.shape_my_course_selected_bg);
                } else {
                    holder.setViewBackgroundRes(R.id.calender_course_item_date, R.color.transparent);
                }
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i = 0; i < dateList.size(); i++) {
                            dateList.get(i).isSelected = false;
                        }
                        dateList.get(position).isSelected = true;
                        gridAdapter.setDataAndNotify(dateList);
                        String rids = "";
                        if(item.hasCourse && !Method.isListEmpty(item.rids)) {
                            for(String id : item.rids) {
                                rids += id + ",";
                            }
                            if(!TextUtils.isEmpty(rids)) {
                                int length = rids.length();
                                rids = rids.substring(0, length - 1);
                            }
                            listView.setVisibility(View.VISIBLE);
                            //tvNoDataDes.setVisibility(View.GONE);
                            mLoadView.hide();
                            getCalenderCourse(rids);
                        } else {
                            courseList.clear();
                            listAdapter.setDataAndNotify(courseList);
                            listView.setVisibility(View.GONE);

                            mLoadView.showEmptyStatus();

                            // tvNoDataDes.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };
        mGridView.setAdapter(gridAdapter);
        listAdapter = new CommonAdapter<CalenderCourseBean>(courseList, R.layout.item_calender_course_layout) {
            @Override
            public void convert(ViewHolder holder, final CalenderCourseBean item, final int position) {

                holder.setViewVisibility(R.id.top_line,position==0?View.GONE:View.VISIBLE);
                holder.setText(R.id.calender_course_item_time, item.BeginTime + " - " + item.EndTime);
                holder.setText(R.id.calender_course_item_title, item.ShortTitle);
                if(!TextUtils.isEmpty(item.Title)) {
                    holder.setText(R.id.calender_course_item_from, item.Title);
                    holder.setViewVisibility(R.id.calender_course_item_from, View.VISIBLE);
                } else {
                    holder.setViewVisibility(R.id.calender_course_item_from, View.GONE);
                }
                holder.setText(R.id.calender_course_item_teacher, "授课老师：" + item.TeacherName);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, BJRecordPlayActivity.class);
                        intent.putExtra("classid", item.netClassId);
                        intent.putExtra(ArgConstant.TYPE, 1);
                        mActivity.startActivity(intent);
                    }
                });
            }
        };
        listView.setAdapter(listAdapter);
        listView.setHeaderDividersEnabled(true);

    }

    private void initTitleBar() {
        topActionBar.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                setResultForTargetFrg(Activity.RESULT_OK, null);
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {

            }
        });
    }

    @Override
    protected void onLoadData() {
        if(mLoadView!=null)  mLoadView.showLoadingStatus();
         ServiceExProvider.visitList(getSubscription(), CourseApiService.getApi().getCourseCalender(),false,
                new NetListResponse<CourseCalenderBean>() {
                    @Override
                    public void onSuccess(BaseListResponseModel<CourseCalenderBean> model) {
                        mCourseCalenderList.clear();
                        if(!Method.isListEmpty(model.data)) {
                            currentServerTime = ((CourseCalenderBean)model.data.get(0)).unix * 1000;
                            for(int i = 0; i < model.data.size(); i++) {
                                mCourseCalenderList.add((CourseCalenderBean)model.data.get(i));
                            }
                        }
                        dateList.clear();
                        mGridView.setVisibility(View.VISIBLE);
                        mLoadView.hide();
                        generateDateList(currentServerTime);
                        gridAdapter.setDataAndNotify(dateList);

                    }

                    @Override
                    public void onError(String message, int type) {
                        mCourseCalenderList.clear();
                        dateList.clear();
                        gridAdapter.setDataAndNotify(dateList);
                        if(type==3)  mLoadView.showNetworkTip();
                        else    mLoadView.showServerError();
                    }
                });
    }

    private void addToDataList(Date date) {
        addToDataList(date, false);
    }

    private void addToDataList(Date date, boolean isToday) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = format.format(date);
//        LogUtils.i(strDate + ":  " + date.getDay());
        CourseCalenderTimeBean bean = new CourseCalenderTimeBean();
        bean.date = strDate;
        bean.isToday = isToday;
        for(int i = 0; i < mCourseCalenderList.size(); i++) {
            if(Method.isEqualString(strDate, mCourseCalenderList.get(i).date)) {
                bean.hasCourse = true;
                bean.rids = mCourseCalenderList.get(i).id;
                break;
            }
        }
        if(date.getDate() == 1) {
            bean.shortDate = String.valueOf(date.getMonth() + 1) + "月";
        } else {
            bean.shortDate = String.valueOf(date.getDate());
        }
        if(isToday) {
            bean.isSelected = true;
            if(!Method.isListEmpty(bean.rids)) {
                String rids = "";
                for(String id : bean.rids) {
                    rids += id + ",";
                }
                if(!TextUtils.isEmpty(rids)) {
                    int length = rids.length();
                    rids = rids.substring(0, length - 1);
                }
                getCalenderCourse(rids);
            } else {
                 listView.setVisibility(View.VISIBLE);
                 mLoadView.showEmptyStatus();
                // tvNoDataDes.setVisibility(View.VISIBLE);
            }
        }
        dateList.add(bean);
    }

    private void generateDateList(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));
        Date curDate = new Date(timeStamp);
        Calendar c;
        int day = curDate.getDay();
        if(day == 0) {
            day = 7;
        }
        //本周之前日期
        for(int i = day - 1; i > 0; i--) {
            c = (Calendar) calendar.clone();
            c.add(Calendar.DATE, -1 * i);
            addToDataList(c.getTime());
        }
        c = (Calendar) calendar.clone();
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, 0);
        Date lastDayOfMonth = c.getTime();
        c = (Calendar) calendar.clone();
        //当天
        addToDataList(c.getTime(), true);
        //本月剩余日期
        for (int i = 1; i <= (lastDayOfMonth.getDate() - curDate.getDate()); i++) {
            c = (Calendar) calendar.clone();
            c.add(Calendar.DATE, i);
            addToDataList(c.getTime());
        }
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//        LogUtils.i("firstDayOfMonth: " + format.format(firstDayOfMonth.getTime()));
        Calendar nextC = (Calendar) c.clone();
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, 0);
        lastDayOfMonth = c.getTime();
//        LogUtils.i("lastDayOfMonth: " + format.format(lastDayOfMonth.getTime()));
        for (int i = 0; i <= (lastDayOfMonth.getDate() - firstDayOfMonth.getDate()); i++) {
            c = (Calendar) nextC.clone();
            c.add(Calendar.DATE, i);
            addToDataList(c.getTime());
        }
    }

    private void getCalenderCourse(String rids) {
        mCurrentRids=rids;
        mActivity.showProgress();
        ServiceProvider.getCalenderCourse(getSubscription(), rids, new NetResponse(){
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mActivity.hideProgress();
                courseList.clear();
                courseList.addAll(model.data);
                listAdapter.setDataAndNotify(courseList);
                if(Method.isListEmpty(courseList)) {
                    listView.setVisibility(View.GONE);
                    //tvNoDataDes.setVisibility(View.VISIBLE);
                    mLoadView.showEmptyStatus();
                } else {
                    listView.setVisibility(View.VISIBLE);
                    mLoadView.hide();
                    //tvNoDataDes.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(final Throwable e) {
                mActivity.hideProgress();
                courseList.clear();
                listAdapter.setDataAndNotify(courseList);
                listView.setVisibility(View.GONE);
                if(NetUtil.isConnected())
                    mLoadView.showServerError();
                else
                    mLoadView.showNetworkTip();
                // tvNoDataDes.setVisibility(View.VISIBLE);
            }
        });
    }
}
