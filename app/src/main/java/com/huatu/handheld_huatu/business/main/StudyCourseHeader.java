package com.huatu.handheld_huatu.business.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsBaseHeader;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.business.other.CourseCalendarActivity;
import com.huatu.handheld_huatu.business.ztk_vod.activity.HomeworkActivity;
import com.huatu.handheld_huatu.business.ztk_vod.activity.SectionalExaminationActivity;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.SectionExamListFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.helper.retrofit.KindRetrofitCallBack;
import com.huatu.handheld_huatu.mvpmodel.CalendarLiveBean;
import com.huatu.handheld_huatu.mvpmodel.DateLiveListResponse;
import com.huatu.handheld_huatu.mvpmodel.UnReadStudyBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CalendarItemView;
import com.huatu.handheld_huatu.ui.CalendarView;
import com.huatu.handheld_huatu.ui.WeekView;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.CenterAlignImageSpan;
import com.huatu.widget.text.MessageTextView;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx .
 */
public class StudyCourseHeader extends AbsBaseHeader<DateLiveListResponse> implements WeekView.OnClickWeekViewListener {
    private KindRetrofitCallBack mHeadCallBack;


    private MessageTextView mTvExamEssence, mTvHomework, mTvSectionalExamination;//tv_exam_essence
    private TextView txtCurrentDate, tvTipView;
    private WeekView mWeekLiveView;
    private String mCurrentDay=null;
    private TextView mNoDataTipView;
    private ProgressBar mWaitProgressBar;
    private CalendarView.WeekDay mSelectWeekDay;
    private long mLastRefreshTime=0;
    public int showTip=0;

    private  CalendarItemView mFirstItemView,mSecondItemView;

    public StudyCourseHeader(@NonNull AbsFragment fragment,ViewGroup parent, @NonNull KindRetrofitCallBack headCallBack) {
        instantiate(fragment,parent);
        mHeadCallBack = headCallBack;
    }

    CompositeSubscription mCompositeSubscription;
    public void setCompositeSubscription(CompositeSubscription ComSubscription){
        mCompositeSubscription=ComSubscription;
    }

    public StudyCourseHeader(Context context) {
       onCreate(context);
    }

    @Override
    public int inflateViewId() {
        return R.layout.course_study_head_item;
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        mTvHomework = findViewById(R.id.mTvHomework);
        mTvSectionalExamination = findViewById(R.id.mTvSectionalExamination);
        mTvExamEssence= findViewById(R.id.tv_exam_essence);

        txtCurrentDate = findViewById(R.id.current_date_txt);
        tvTipView = findViewById(R.id.tip_title);
        mWaitProgressBar= findViewById(R.id.loadwait_view);

        mWeekLiveView=findViewById(R.id.date_weekview);
        mWeekLiveView.setOnViewInitListener(new WeekView.OnViewInitListener() {
            @Override
            public void onViewInit(CalendarView.WeekDay date) {
                LogUtils.e("onViewInit",String.valueOf(date.month));
                txtCurrentDate.setText(String.valueOf(date.month+"月"));
                mCurrentDay=String.valueOf(date.dayof);
                mSelectWeekDay=date;
             }
        });
        mWeekLiveView.setOnClickWeekViewListener(this);
        mFirstItemView=findViewById(R.id.first_layout);
        mSecondItemView=findViewById(R.id.second_layout);
        mNoDataTipView=findViewById(R.id.no_date_view);
        mNoDataTipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("1".equals(v.getTag())&&(null!=mContext)){
                    Intent intent = new Intent(mContext, MainTabActivity.class);
                    intent.putExtra("require_index",1);
                    mContext.startActivity(intent);
                }
            }
        });
        tvTipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=mSelectWeekDay){
                    CourseCalendarActivity.show(getContext(),mSelectWeekDay);
                }
            }
        });

        mTvHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=  new Intent(mContext, HomeworkActivity.class);
                intent.putExtra(HomeworkActivity.UNREADNNCOUNT,((MessageTextView)mTvHomework).getMessageCount());
                mContext.startActivity(intent);
            }
        });
        mTvSectionalExamination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent=  new Intent(mContext, SectionalExaminationActivity.class);
                intent.putExtra(SectionalExaminationActivity.UNREADNNCOUNT,((MessageTextView)mTvSectionalExamination).getMessageCount());
                mContext.startActivity(intent);*/
                SectionExamListFragment.lanuch(mContext,((MessageTextView)mTvSectionalExamination).getMessageCount());
               // UIJumpHelper.jumpFragment(mContext,SectionExamListFragment.class);

            }
        });

        mTvExamEssence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFrgContainerActivity.newInstance(v.getContext(), CreamArticleFragment.class.getName(), null);
            }
        });

    }


    @Override
    public void onError(String throwable, int type) {
        if(null!=mWaitProgressBar) {
            mWaitProgressBar.setVisibility(View.GONE);
        }
        ToastUtils.showMessage("数据刷新出错~");
    }

    @Override
    public void onSuccess(DateLiveListResponse response)   {
        mHeadInfoResponse=response;
        bindingData(response);
       // if(null!=mOnHeaderLoadListener) mOnHeaderLoadListener.onHeaderSuccess();
    }


    @Override
    public void bindingData(DateLiveListResponse data) {
        if(null!=mWaitProgressBar) {
            mWaitProgressBar.setVisibility(View.GONE);
        }
        if(data.data!=null){
            int curSize=ArrayUtils.size(data.data.liveData);
            if(curSize==0){
                mFirstItemView.setVisibility(View.GONE);
                if(data.data.type==1){
                    tvTipView.setVisibility(View.VISIBLE);
                    mNoDataTipView.setTag("0");
                    tvTipView.setText("查看课程表");
                    mNoDataTipView.setText("未来七天内，没有直播课哦～");
                    mNoDataTipView.setVisibility(View.VISIBLE);

                 }
                 else if(data.data.type==2){
                    tvTipView.setVisibility(View.VISIBLE);
                    mNoDataTipView.setTag("0");
                    tvTipView.setText("查看课程表");
                    mNoDataTipView.setText(String.valueOf(data.data.msg));
                    mNoDataTipView.setVisibility(View.VISIBLE);

                }
                 else if(data.data.type==3){
                    tvTipView.setVisibility(View.GONE);
                    String tmpStr="暂无直播安排，安排一下[img]";
                    SpannableStringBuilder builder = new SpannableStringBuilder(tmpStr);

                    ForegroundColorSpan redSpan = new ForegroundColorSpan(0xFFFD4312);

                    int Startindex=tmpStr.indexOf("安排一下");
                    builder.setSpan(redSpan, Startindex, Startindex+"安排一下".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    //得到drawable对象，即所要插入的图片
                    Drawable d = ResourceUtils.getDrawable(R.mipmap.calender_choose_other_icon);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

                    //drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    //用这个drawable对象代替字符串easy
                    CenterAlignImageSpan span = new CenterAlignImageSpan(d);
                    //包括0但是不包括"easy".length()即：4。[0,4)。值得注意的是当我们复制这个图片的时候，实际是复制了"easy"这个字符串。

                    int Startindex2=tmpStr.indexOf("[img]");
                    builder.setSpan(span, tmpStr.indexOf("[img]"), Startindex2+"[img]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    mNoDataTipView.setVisibility(View.VISIBLE);
                    mNoDataTipView.setTag("1");
                    mNoDataTipView.setText(builder);
                }
            }
            else if(curSize==1){
                tvTipView.setVisibility(View.VISIBLE);
                showTip=1;
                if (!SpUtils.getRecycleBinTipsShow()){
//                    showTipPop();
                }
                String as_detail =(data.data.day.equals(mCurrentDay) ?"今天共":data.data.day+"日共")+ StringUtils.fontColor("#FF3F47", "1")+"场直播" ;
                tvTipView.setText(  StringUtils.forHtml(as_detail));
                mNoDataTipView.setVisibility(View.GONE);
                mFirstItemView.setVisibility(View.GONE);
                mSecondItemView.showUI(data.data.liveData.get(0));
            }else if(curSize>=2){
                tvTipView.setVisibility(View.VISIBLE);
                showTip=1;
                if (!SpUtils.getRecycleBinTipsShow()){
//                    showTipPop();
                }
                String as_detail =(data.data.day.equals(mCurrentDay) ?"今天共":data.data.day+"日共")+ StringUtils.fontColor("#FF3F47", String.valueOf(curSize))+"场直播" ;
                tvTipView.setText(  StringUtils.forHtml(as_detail));
                mNoDataTipView.setVisibility(View.GONE);
                mFirstItemView.setVisibility(View.VISIBLE);
                mFirstItemView.showUI(data.data.liveData.get(0));
                mSecondItemView.showUI(data.data.liveData.get(1));
            }
         }
    }





//    public boolean onTouchEvent(MotionEvent event) {
//        super;
//        if (popupWindow != null && popupWindow.isShowing()) {
//
//            popupWindow.dismiss();
//
//            popupWindow = null;
//
//        }
//
//        return super.onTouchEvent(event);
//
//    }


    @Override
    protected void setListener() {
        super.setListener();
        findViewById(R.id.current_date_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseCalendarActivity.show(v.getContext());
            }
        });
    }

    @Override
    public void onClickCurrentWeek(CalendarView.WeekDay date){
        if(null!=mWaitProgressBar) {
            mWaitProgressBar.setVisibility(View.VISIBLE);
        }
        mSelectWeekDay=date;
         CourseApiService.getApi().getCalendarDetailBydate(date.day,date.lessionIds).enqueue(getCallback());
    }

    @Override
    public void onLoadData() {
        super.onLoadData();
        int delayMin=(int)((System.currentTimeMillis() - mLastRefreshTime) / (1000 * 60 * 5));//
        if(delayMin<=5){
            if(PrefStore.getSettingInt("study_unread_message_change",1)==1){
                PrefStore.putSettingInt("study_unread_message_change",0);
            }
            getUnReadMsgNum();
            if(null!=mOnHeaderLoadListener){
                mOnHeaderLoadListener.onHeaderSuccess();
                return;
            }
        }else {
            getCurrentweek();
            getUnReadMsgNum();
        }
    }

    private void getUnReadMsgNum(){
        ServiceExProvider.visit(mCompositeSubscription, CourseApiService.getApi().getUnreadStudyMessageNum(),
           new NetObjResponse<UnReadStudyBean>() {
            @Override
            public void onSuccess(BaseResponseModel<UnReadStudyBean> model) {
                if(null!=mTvHomework) mTvHomework.setMessageNum(model.data.courseWork);
                if(null!=mTvSectionalExamination) mTvSectionalExamination.setMessageNum(model.data.periodTest);
                if(null!=mTvExamEssence) mTvExamEssence.setMessageNum(model.data.preTestEssence);
            }

            @Override
            public void onError(String message, int type) {  }
        });

    }

    private void getCurrentweek(){
         ServiceExProvider.visitList(mCompositeSubscription, CourseApiService.getApi().getLearnCalendar("w", ""),
                new NetListResponse<CalendarLiveBean>() {
                    @Override
                    public void onSuccess(BaseListResponseModel<CalendarLiveBean> model) {
                        mLastRefreshTime=System.currentTimeMillis();
                        if(null!=mOnHeaderLoadListener){
                            mOnHeaderLoadListener.onHeaderSuccess();
                        }
                        if (null != mWeekLiveView) {
                            mWeekLiveView.refreshView(model.data);

                            if(null!=mWeekLiveView.getTodayWeekDay()){
                              onClickCurrentWeek(mWeekLiveView.getTodayWeekDay());
                            }
                        }
                    }

                    @Override
                    public void onError(String message, int type) {
                        if(null!=mHeadCallBack){
                            mHeadCallBack.onError(message,type);
                            return;
                        }
                    }
                });

    }


}
