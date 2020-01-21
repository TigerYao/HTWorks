package com.huatu.handheld_huatu.business.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CalendarCourseAdapter;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitStatusCallbackEx;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.CalendarLiveBean;
import com.huatu.handheld_huatu.mvpmodel.DateLiveBean;
import com.huatu.handheld_huatu.mvpmodel.DateLiveListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.StatusbgTextView;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class CourseCalendarActivity extends SimpleBaseActivity implements
        CalendarView.OnDateSelectedListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener {

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;
    Calendar mCurSelectCalendar;


    protected int mCourseType;



    private int mYear;
    CalendarLayout mCalendarLayout;
    RecyclerView mRecyclerView;
    CalendarCourseAdapter mCourseAdapter;

    private Map<String, Calendar> mLiveCalendarMap = new HashMap<>();
    private Map<String,Boolean>   mMonthLoadedMap=new HashMap<>();//此月已经加载过

    public static void show(Context context) {
        context.startActivity(new Intent(context, CourseCalendarActivity.class));
    }

    public static void show(Context context, com.huatu.handheld_huatu.ui.CalendarView.WeekDay weekDay) {
        Intent tmpIntent=new Intent(context, CourseCalendarActivity.class);
        tmpIntent.putExtra(ArgConstant.KEY_ID,weekDay.year+"-"+weekDay.month+"-"+weekDay.dayof);
        context.startActivity(tmpIntent);
    }

    @Override
    protected int onSetRootViewId() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        return R.layout.course_calendar_layout;
    }

    @Override
    protected void onResume() {
        super.onResume();
        QMUIStatusBarHelper.setStatusBarLightMode(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onInitView() {
        super.onInitView();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }
        mTextMonthDay = (TextView) findViewById(R.id.tv_month_day);
        mTextYear = (TextView) findViewById(R.id.tv_year);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mTextCurrentDay = (TextView) findViewById(R.id.tv_current_day);

        findViewById(R.id.left_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);

        String argDate=getIntent().getStringExtra(ArgConstant.KEY_ID);
        if(!TextUtils.isEmpty(argDate)&&argDate.contains("-")){
            String[] tmpDateArr=argDate.split("-");
            if(ArrayUtils.size(tmpDateArr)==3){
              mCalendarView.scrollToCalendar(StringUtils.parseInt(tmpDateArr[0]),StringUtils.parseInt(tmpDateArr[1]),
                        StringUtils.parseInt(tmpDateArr[2]));
            }
        }
        mCurSelectCalendar=mCalendarView.getSelectedCalendar();
        mCalendarView.setOnDateSelectedListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear())+ "年"+mCalendarView.getCurMonth() + "月" );


        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "");//日
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
        this.findViewById(R.id.show_pre_btn).setOnClickListener(this);
        this.findViewById(R.id.show_next_btn).setOnClickListener(this);
    }


    @Override
    protected void onLoadData() {

        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(mLiveCalendarMap);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
       // mRecyclerView.addItemDecoration(new GroupItemDecoration<String, Article>());
        mCourseAdapter=new CalendarCourseAdapter(this);
        mCourseAdapter.setOnViewItemClickListener(new OnRecItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int type) {
                 if(type== EventConstant.EVENT_RELOAD){
                     if(null!=mCurSelectCalendar){
                       loadDetailList(mCurSelectCalendar);
                     }
                 }else {
                    DateLiveBean tmpDto=mCourseAdapter.getItem(position);
                    if(tmpDto==null) return;

                     if(tmpDto.videoType == 2 && tmpDto.tinyLive==1 && tmpDto.status == StatusbgTextView.ONLIVEING){
                         CommonUtils.startLiveRoom(getSubscription(), CourseCalendarActivity.this,String.valueOf(tmpDto.classId), tmpDto.netClassId+"", tmpDto.parentId, tmpDto.joinCode, tmpDto.bjyRoomId, tmpDto.sign);
//                         LiveRoomActivity.start(CourseCalendarActivity.this, String.valueOf(tmpDto.netClassId), tmpDto.classId,tmpDto.parentId,tmpDto.lessonId , 1, tmpDto.joinCode);
                         return;
                     }
                    if(tmpDto.status== StatusbgTextView.END||tmpDto.status== StatusbgTextView.WAITING){
                        Intent intent = new Intent(CourseCalendarActivity.this, BJRecordPlayActivity.class);
                        intent.putExtra("classid", String.valueOf(tmpDto.netClassId));
                        intent.putExtra(ArgConstant.FROM_ACTION,tmpDto.id);
                        intent.putExtra(ArgConstant.LESSION_ID,tmpDto.lessonId);
                        intent.putExtra(ArgConstant.TYPE, 1);
                        startActivity(intent);
                        return;
                    }else {
                        LiveVideoForLiveActivity.start(CourseCalendarActivity.this,
                                      String.valueOf(tmpDto.netClassId), tmpDto.buildCourseWare(new CourseWareInfo()), "");
                    }
                  }
             }
        });
        mRecyclerView.setAdapter(mCourseAdapter);
       // mRecyclerView.notifyDataSetChanged();
        getCurrentMonth(mCalendarView.getSelectedCalendar());
    }


    @Override
    public void onClick(View v) {
         switch (v.getId()) {
            case R.id.show_pre_btn:
               // SolarActivity.show(this);
                if(null!=mCalendarView)
                   mCalendarView.scrollToPre();
                break;
            case R.id.show_next_btn:
                if(null!=mCalendarView)
                  mCalendarView.scrollToNext();
                break;

        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, String text,String lunar) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(Color.WHITE);
        calendar.setScheme(text);
        calendar.setLunar(lunar);//复用此字段为ids
/*        calendar.addScheme(0xFFa8b015, "rightTop");
        calendar.addScheme(0xFF423cb0, "leftTop");
        calendar.addScheme(0xFF643c8c, "bottom");*/

        return calendar;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSelected(Calendar calendar, boolean isClick) {
        LogUtils.e("onDateSelected","onDateSelected_"+isClick);

        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "");//日
        mTextYear.setText(String.valueOf(calendar.getYear()+ "年"+calendar.getMonth() + "月"));
        mYear = calendar.getYear();
        if(!isClick&&(calendar.equals(mCurSelectCalendar))){
            return;
        }else {
            if(calendar.getMonth()!=mCurSelectCalendar.getMonth()){
                mCurSelectCalendar=calendar;
                String tmpKey=calendar.getYear()+calendar.getMonth()+"";
                if(mMonthLoadedMap.containsKey(tmpKey)){
                     loadDetailList(calendar);
                }else {
                    getCurrentMonth(calendar);
                }
            }
            else{
                mCurSelectCalendar=calendar;
                //if(isClick)
                loadDetailList(calendar);
            }
        }
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null!=mCalendarView){
            mCalendarView.clearClickListener();
        }
        if(null!=mCourseAdapter){
            mCourseAdapter=null;
        }

         //  EventBus.getDefault().unregister(this);
    }

    private void loadDetailList(Calendar calendar) {

        String ids = "";
        if (mLiveCalendarMap.containsKey(calendar.toString())) {
            ids = mLiveCalendarMap.get(calendar.toString()).getLunar();
        }
        String date =calendar.formatString() ;

        if(null!=mCourseAdapter){
            mCourseAdapter.setEmptyStatus(CommloadingView.StatusMode.loading);
            mCourseAdapter.clearAndRefresh();
        }

        CourseApiService.getApi().getCalendarDetailBydate(date, ids).enqueue(
                new RetrofitStatusCallbackEx<DateLiveListResponse>(this) {
                    @Override
                    protected void onSuccess(Response<DateLiveListResponse> response) {
                        if(null!=mCourseAdapter){
                            mCourseAdapter.setEmptyStatus(CommloadingView.StatusMode.empty);
                            mCourseAdapter.refresh(response.body().data.liveData);
                        }

                    }

                    @Override
                    protected void onFailure(String error, int type) {
                        if(null!=mCourseAdapter) {
                            mCourseAdapter.setEmptyStatus(CommloadingView.StatusMode.serverError);
                            mCourseAdapter.clearAndRefresh();
                        }
                    }
                });

    }

    private void getCurrentMonth(final Calendar calendar) {
        showLoadingDialog();
        ServiceExProvider.visitList(getSubscription(), CourseApiService.getApi().getLearnCalendar("m",calendar.formatString()),
                new NetListResponse<CalendarLiveBean>() {
                    @Override
                    public void onSuccess(BaseListResponseModel<CalendarLiveBean> model) {
                        dismissLoadingDialog();
                        mMonthLoadedMap.put(calendar.getYear()+calendar.getMonth()+"",true);
                        buildSchemeCalendar(model.data,calendar.getYear(),calendar.getMonth());
                        onDateSelected(mCalendarView.getSelectedCalendar(),true);
                    }

                    @Override
                    public void onError(String message, int type) {
                        dismissLoadingDialog();
                        ToastUtils.showShort("加载日历数据失败");
                    }
                });
    }

    private void buildSchemeCalendar(List<CalendarLiveBean> liveBeans,int year,int month){

        if(ArrayUtils.isEmpty(liveBeans)) return;

        String[] tmpdata;
        for(CalendarLiveBean bean:liveBeans){
            if(bean.date.contains("-")){
                tmpdata= bean.date.split("-");
                Calendar  tmpCalendar= getSchemeCalendar(year, month, StringUtils.parseInt(tmpdata[tmpdata.length-1]),
                                       String.valueOf(bean.isHaveLive),StringUtils.arrayString(bean.id));
                mLiveCalendarMap.put(tmpCalendar.toString(),tmpCalendar);
            }
        }

        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(mLiveCalendarMap);

    }

    CustomDialog mLoadingDialog;
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new CustomDialog(this, R.layout.dialog_type2);
            TextView tv = (TextView) mLoadingDialog.mContentView.findViewById(R.id.tv_notify_message);
            tv.setText("加载中...");
        }
        if(!mLoadingDialog.isShowing())
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog() {

        if (mLoadingDialog != null&& mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

}
