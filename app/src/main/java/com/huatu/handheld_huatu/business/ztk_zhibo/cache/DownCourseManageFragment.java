package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.adapter.course.DownCourseAdapter;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLHandoutProgressListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLVodListener;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.LocalVideoDeleteEvent;
import com.huatu.handheld_huatu.helper.FlagSubscriber;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.BaseDialog;
import com.huatu.widget.IncreaseProgressBar;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.SafeSubscriber;
import rx.schedulers.Schedulers;

/**
 * Created by cjx
 */

public class DownCourseManageFragment extends AbsSettingFragment implements OnDLVodListener,OnRecItemClickListener
        ,OldDownRecoveryAssist.onCacheRecoveryListener,OnDLHandoutProgressListener {

    @BindView(R.id.down_tilte_txt)
    TextView mDownTitleTxt;

    @BindView(R.id.tv_space)
    TextView mDownSpaceTxt;

    @BindView(R.id.space_tip_txt)
    TextView mSpaceTipTxt;

    @BindView(R.id.downfilenum_txt)
    TextView mDownMultTipTxt;

    @BindView(R.id.down_cover_img)
    ImageView mDownLoadingImg;

    @BindView(R.id.progressBar)
    IncreaseProgressBar mLoadingProgressBar;

    @BindView(R.id.tv_speed)
    TextView mDownSpeedTxt;

    @BindView(R.id.ll_down_no)
    View layoutNoData;

    @BindView(R.id.lv_downloaded)
    RecyclerView mListView;

    @BindView(R.id.ll_downing_manager)
    RelativeLayout mLayoutDownloading;

    @BindView(R.id.downstatus_txt)
    TextView mDownStatusTip;

    @BindView(R.id.need_down_txt)
    TextView mNeedDownTip;


    private CustomConfirmDialog deleteDlg;

    DownCourseAdapter mDownCourseAdapter;

    private long   mCurDownSpeed=0;//下载速度
    private String mHandoutFileSize="";

    private String mCurDownLoadingId;//当前下载的Id

    //course是一门课程  lesson是一节课
    private final List<DownLoadLesson> downloadingList = new ArrayList<>();

    private final List<DownLoadLesson> downloadingHandoutList = new ArrayList<>();

    private String reqFrom;
    private boolean mIsFirstLoad=true;
    OldDownRecoveryAssist mOldRecoverAssist;

    public static void lanuch(Activity context,String fromJump){

        Bundle arg=new Bundle();
        arg.putString("from_act",fromJump);
         UIJumpHelper.jumpFragment(context,1001,DownCourseManageFragment.class,arg);
    }

    @Override
    public int getContentView() {
        return R.layout.down_course_manage_layout;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("已缓存文件");
        EventBus.getDefault().register(this);
    }

    private Runnable mResizeLoadingRunable=new Runnable() {
        @Override
        public void run() {
            mCurDownLoadingId="";
           // refreshDownloadingCourse();
            readDbCourses(true);
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDeleteUpdate(LocalVideoDeleteEvent event){

        UniApplicationLike.getApplicationHandler().removeCallbacks(mResizeLoadingRunable);
        UniApplicationLike.getApplicationHandler().postDelayed(mResizeLoadingRunable,250);
    }

    @Override
    protected void parserParams(Bundle arg) {
        super.parserParams(arg);
        reqFrom = arg.getString("from_act");
    }

    @Override
    protected void setListener() {
        super.setListener(); //
        if(null!=mLoadingProgressBar)  mLoadingProgressBar.setFastOutInterpoator();
        mDownCourseAdapter=new DownCourseAdapter(getContext(),new ArrayList<DownLoadCourse>());
        mDownCourseAdapter.setOnItemClickListener(this);

        mListView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));
        DownLoadAssist.getInstance().addDownloadListener(this);
        DownHandoutAssist.getInstance().setOnDLHandoutProgressListener(this);

    }

    @Override
    public void requestData() {
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListView.setAdapter(mDownCourseAdapter);
        CommonUtils.setSpaceTip(mSpaceTipTxt,0);
        oldCacheRecover();
    }

    private void oldCacheRecover(){
        mOldRecoverAssist= new OldDownRecoveryAssist();
        mOldRecoverAssist.setOnCacheRecoveryListener(this);
        mOldRecoverAssist.recoryoldData();
    }

    @Override
    public void onCacheRecoveryFinish(){
        if(null!=mOldRecoverAssist){
            mOldRecoverAssist.setOnCacheRecoveryListener(null);
            mOldRecoverAssist=null;
        }
          //refreshFinishedCourses();
        readDbCourses(false);
    }

    private void readDbCourses(final boolean onlyLoading){

       LogUtils.e("readDbCourses",onlyLoading?"true":"false");
       getSubscription().add(Observable.create(new Observable.OnSubscribe< List<DownLoadCourse>>() {

            private  List<DownLoadCourse> longRunningOperation(boolean onlyLoading){
                List<DownLoadCourse> tmpList= Collections.emptyList();
                if(!onlyLoading){
                    tmpList=SQLiteHelper.getInstance().getAllFinishedCourses();
                }
                downloadingList.clear();
                downloadingHandoutList.clear();
                List<DownLoadCourse> courses = SQLiteHelper.getInstance().getCourses();
                if (!Method.isListEmpty(courses)) {
                     for (DownLoadCourse course : courses) {
                        if (course != null && !Method.isListEmpty(course.getLessonLists())) {
                            for (DownLoadLesson courseware : course.getLessonLists()) {
                                if (courseware != null && courseware.getDownStatus() != DownLoadStatusEnum.FINISHED.getValue()) {// 2
                                    downloadingList.add(courseware);
                                }
                            }
                        }
                    }
                }
                if (ArrayUtils.isEmpty(downloadingList)) {
                    buildLoadingHandout(false);
                }
                return tmpList;
            }

            @Override
            public void call(Subscriber<? super  List<DownLoadCourse>> subscriber) {

                subscriber.onNext(longRunningOperation(onlyLoading));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<DownLoadCourse>>() {
            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onNext(List<DownLoadCourse> downlist) {
                if(null!=mDownCourseAdapter){
                    if(!onlyLoading){
                        mDownCourseAdapter.refresh(downlist);
                    }
                    setHeadDownloadingState();
                    checkNoDataState();
                }
            }
        }));;
     }

    private void checkNoDataState(){
        if((mDownCourseAdapter.getItemCount()==0) && downloadingList.isEmpty()) {
            this.findViewById(R.id.tv_down_finished).setVisibility(View.GONE);
            layoutNoData.setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.tv_down_finished).setVisibility(View.VISIBLE);
            layoutNoData.setVisibility(View.GONE);
        }
     }

    private void checkHeadloadingState(boolean canAdpaterRefresh){
        setHeadDownloadingState();
        if((mDownCourseAdapter.getItemCount()==0) && downloadingList.isEmpty()) {
            layoutNoData.setVisibility(View.VISIBLE);
        } else {
            layoutNoData.setVisibility(View.GONE);
        }
        if(canAdpaterRefresh )
            mDownCourseAdapter.notifyDataSetChanged();
    }

   /* private void refreshFinishedCourses() {
        List<DownLoadCourse> tmpList = SQLiteHelper.getInstance().getAllFinishedCourses();
        mDownCourseAdapter.refresh(tmpList);
    }

    private void refreshDownloadingCourse() {
        downloadingList.clear();
        downloadingHandoutList.clear();
        List<DownLoadCourse> courses = SQLiteHelper.getInstance().getCourses();
        if(!Method.isListEmpty(courses)) {

            for(DownLoadCourse course : courses) {
                if(course != null && !Method.isListEmpty(course.getLessonLists())) {
                    for(DownLoadLesson courseware : course.getLessonLists()) {
                        if(courseware != null && courseware.getDownStatus() != DownLoadStatusEnum.FINISHED.getValue()) {// 2
                            downloadingList.add(courseware);
                        }
                    }
                }
            }
        }
        if(ArrayUtils.isEmpty(downloadingList)){
            buildLoadingHandout(false);
        }
        setHeadDownloadingState();
    }*/

    private void buildLoadingHandout(boolean hasLoading){
        downloadingHandoutList.clear();
        DownLoadCourse tmpCourse= SQLiteHelper.getInstance().getUnFinishHandoutCourse();
        if(null!=tmpCourse){
            LogUtils.e("refreshDownloadingCourse", GsonUtil.GsonString(tmpCourse));
            DownLoadLesson tmpLession=new DownLoadLesson();
            if(hasLoading){
                tmpLession.setDownStatus(DownLoadStatusEnum.START.getValue());
            }else {
                if(tmpCourse.getChangeStatus()== DownBtnLayout.PAUSE)
                    tmpLession.setDownStatus(DownLoadStatusEnum.STOP.getValue());
                else if(tmpCourse.getChangeStatus()== DownBtnLayout.DOWNLOADING){
                    tmpLession.setDownStatus(DownLoadStatusEnum.START.getValue());
                }
                else if(tmpCourse.getChangeStatus()== DownBtnLayout.ERROR){
                    tmpLession.setDownStatus(DownLoadStatusEnum.STOP.getValue());
                }
            }
            tmpLession.setSubjectName(tmpCourse.getCourseName());
            tmpLession.setImagePath(tmpCourse.getImagePath());
            tmpLession.setDownloadID("-1");

            tmpLession.setCourseNum(SQLiteHelper.getInstance().getUnFinishHandoutCount());
            downloadingHandoutList.add(tmpLession);
        }
        downloadingList.addAll(downloadingHandoutList);
    }

    @Override
    public void onDLHandoutProgress(String downID,float speed,String subjectName,String fileSize){
        //正在下载课件
        if(!TextUtils.isEmpty(mCurDownLoadingId)&&(!"-1".equals(mCurDownLoadingId)))
            return;
        //
        if("-10000".equals(downID)){
            mCurDownLoadingId="";
            //refreshDownloadingCourse();
            LogUtils.e("readDbCourses","onDLHandoutProgress");
            readDbCourses(true);
            return;
        }
        mCurDownSpeed=(long) speed;
        mHandoutFileSize=fileSize;
        if(Method.isListEmpty(downloadingHandoutList)) {
            buildLoadingHandout(true);
            //return;
        }else {
            downloadingHandoutList.get(0).setDownStatus(DownLoadStatusEnum.START.getValue());
            downloadingHandoutList.get(0).setCourseNum(SQLiteHelper.getInstance().getUnFinishHandoutCount());
        }

        checkHeadloadingState(false);
    }

    //设置头部下载状态
    private void setHeadDownloadingState() {
        if(Method.isListEmpty(downloadingList)) {
            mLayoutDownloading.setVisibility(View.GONE);
        } else {
            mLayoutDownloading.setVisibility(View.VISIBLE);
            int index = -1;
            for(int i = 0; i < downloadingList.size(); i++) {
                DownLoadLesson courseware = downloadingList.get(i);
                if(courseware.getDownStatus() ==DownLoadStatusEnum.START.getValue() ) {//1
                    index = i;
                    break;
                }
            }
            boolean isWating=false;
            if(index < 0) {
                for(int i = 0; i < downloadingList.size(); i++) {
                    DownLoadLesson courseware = downloadingList.get(i);
                    if(courseware.getDownStatus() ==DownLoadStatusEnum.PREPARE.getValue()) {// -1
                        index = i;
                        isWating=true;
                        break;
                    }
                }
            }
            boolean isStop=false;
            if(index < 0) {
                for(int i = 0; i < downloadingList.size(); i++) {
                    DownLoadLesson courseware = downloadingList.get(i);
                    if(courseware.getDownStatus() ==DownLoadStatusEnum.STOP.getValue()) {// -1
                        index = i;
                        isStop=true;
                        break;
                    }
                }
            }
            if(index >= 0) {

                DownLoadLesson downingLesson=downloadingList.get(index);

                if("-1".equals(downingLesson.getDownloadID())){
                    mNeedDownTip.setText(String.valueOf(downingLesson.getCourseNum()));
                    mCurDownLoadingId=downingLesson.getDownloadID();

                    if(!TextUtils.isEmpty(mHandoutFileSize)){
                        mDownSpaceTxt.setText(String.valueOf(mHandoutFileSize+"M"));
                        mDownSpaceTxt.setVisibility(View.VISIBLE);
                     }
                }
                else  if(!downingLesson.getDownloadID().equals(mCurDownLoadingId)){
                    mCurDownLoadingId=downingLesson.getDownloadID();
                  //  setSpaceTip(downingLesson.getSpace());
                    mNeedDownTip.setText(String.valueOf(downloadingList.size()));
                }
                mDownTitleTxt.setText(downingLesson.getSubjectName());

                if(!"-1".equals(downingLesson.getDownloadID())){
                    int size = (int) (downingLesson.getSpace() / 1024 / 1024);
                    mDownSpaceTxt.setText(size * downingLesson.getDownloadProgress() / 100  + "M/" + size + "M");
                    mDownSpaceTxt.setVisibility(View.VISIBLE);
                }

                mDownSpeedTxt.setText((mCurDownSpeed/1024)+"KB/s");
                ImageLoad.displaynoCacheImage(getActivity(),R.mipmap.load_default,downloadingList.get(index).getImagePath(),mDownLoadingImg);
                mLoadingProgressBar.setCurProgress(downingLesson.getDownloadProgress());

                mDownStatusTip.setText(isWating? "等待中":(isStop?"缓存暂停":"正在缓存"));
                mDownStatusTip.setEnabled(!isStop);
                if(isStop) mDownSpeedTxt.setText("");
                 // tvDownloadingNumber.setText("正在缓存(" + downloadingList.size() + ")");
            } else {

                // tvDownloadingNumber.setText("缓存暂停 ");
                if(!ArrayUtils.isEmpty(downloadingList)){
                    mDownTitleTxt.setText(downloadingList.get(0).getSubjectName());
                    if("-1".equals(downloadingList.get(0).getDownloadID())){
                        mNeedDownTip.setText(String.valueOf(downloadingList.get(0).getCourseNum()));
                    }
                    else{
                         mNeedDownTip.setText(String.valueOf(downloadingList.size()));
                    }
                }
                mDownSpeedTxt.setText("");
                mDownStatusTip.setText("缓存暂停");
                mDownStatusTip.setEnabled(false);
            }
            if(mDownMultTipTxt!=null&&(!TextUtils.isEmpty(mCurDownLoadingId))){
                mDownMultTipTxt.setVisibility(ArrayUtils.size(downloadingList)>1? View.VISIBLE:View.GONE);
            }
            if (downloadingList.size() <= 0) {
                mLayoutDownloading.setVisibility(View.GONE);
            } else {
                mLayoutDownloading.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemClick(int position,View view,int type){
        switch (type){
          case EventConstant.EVENT_ALL:
                DownLoadCourse curCourse= mDownCourseAdapter.getCurrentItem(position);
                if(curCourse==null) return;
              //  DownLessionManageFragment.lanuchForResult(this,curCourse,reqFrom);

              DownAllFinishedFragment.lanuchForResult(this,curCourse,reqFrom);
                break;
            case EventConstant.EVENT_DELETE:
                DownLoadCourse curCourse2= mDownCourseAdapter.getCurrentItem(position);
                if(curCourse2==null) return;
                 showDeleteDlg(curCourse2,position);
                break;
        }
     }

    @OnClick(R.id.ll_downing_manager)
    public void onClickDownloading() {
        DownAllLoadingFragment.lanuchForResult(this,1200);
        //UIJumpHelper.jumpFragment(this,1200,DownloadingCourseFragment.class);
    }

    private void showDeleteDlg(final DownLoadCourse item, final int position) {
        if(deleteDlg == null) {
            deleteDlg = DialogUtils.createDialog(getActivity(),
                    "提示", "即将删除所选课程\n该操作不可恢复");
        }
        deleteDlg.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownLoadCourse curCourse= mDownCourseAdapter.getCurrentItem(position);
                if(curCourse==null) return;
                List<DownLoadLesson> downIDs = new ArrayList<>();
                 {
                     //只有讲义
                     if(ArrayUtils.isEmpty(curCourse.getLessonLists())){
                         SQLiteHelper.getInstance().deleteFinishedHandOut(curCourse.getCourseID());
                        mDownCourseAdapter.removeAndRefresh(position);

                     }else {
                         List<DownLoadLesson> lists = curCourse.getLessonLists();
                         for (int j = 0; j < lists.size(); j++) {
                             downIDs.add(lists.get(j));
                         }
                         SQLiteHelper.getInstance().deleteCourse(curCourse.getCourseID(), "2");
                         SQLiteHelper.getInstance().deleteFinishedHandOut(curCourse.getCourseID());
                         DownLoadAssist.getInstance().delete(downIDs);
                         mDownCourseAdapter.removeAndRefresh(position);
                    }

                   // initFinishedCourses();
                   // initDownloadingCourse();
                   // setViewState();
                }
            }
        });
        if(!deleteDlg.isShowing()) {
            deleteDlg.show();
        }
    }


    @Override
    public void onDLProgress(final String s, final int progress,final long speed) {

        mCurDownSpeed=speed;
        if(Method.isListEmpty(downloadingList)) {
            return;
        }

        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < downloadingList.size(); i++) {
                    if (Method.isEqualString(downloadingList.get(i).getDownloadID(), s)) {
                        downloadingList.get(i).setDownStatus(DownLoadStatusEnum.START.getValue());//1
                        downloadingList.get(i).setDownloadProgress(progress);

                        if(mIsFirstLoad&&(downloadingList.get(i).getSpace()==0)){
                            mIsFirstLoad=false;
                            DownLoadLesson tmpLession= SQLiteHelper.getInstance().getCourseWare(s);
                            if(null!=tmpLession)  downloadingList.get(i).setSpace(tmpLession.getSpace());
                        }
                        break;
                    }
                }
                checkHeadloadingState(false);
            }
        });
    }

    @Override
    public void onDLError(final String s, int errorCode) {
        LogUtils.e("onDLError",s+","+errorCode);
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                 if(s.equals(mCurDownLoadingId)){
                    if(null!=mDownStatusTip)
                       mDownStatusTip.setText("等待中");
                }
            }
        });
    }

    @Override
    public void onDLFinished(String downID) {
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                mCurDownLoadingId="";
                if(mLoadingProgressBar!=null)
                    mLoadingProgressBar.setProgress(0);

                CommonUtils.setSpaceTip(mSpaceTipTxt,0);
                LogUtils.e("readDbCourses","onDLFinished");
                readDbCourses(false);
                //refreshFinishedCourses();
               // refreshDownloadingCourse();
               }
        });
    }



    @Override
    public void onDLPrepare(final String downID) {
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
              // refreshDownloadingCourse();
                LogUtils.e("readDbCourses","onDLPrepare");
                //添加 任务时容易触发两次 容易触发两次
                onVideoDeleteUpdate(null);
                // readDbCourses(true);
            }
        });
    }

    @Override
    public void onDLStop(String key,boolean keepWaiting) {
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
               // refreshDownloadingCourse();
                LogUtils.e("readDbCourses","onDLStop");
               // readDbCourses(true);
                onVideoDeleteUpdate(null);
             }
        });
    }

    @Override
    public void onDLFileStorage(String key, long space) {  }


    @Override
    public void onDestroy() {
        DownLoadAssist.getInstance().removeDownloadListener(this);
        EventBus.getDefault().unregister(this);
        UniApplicationLike.getApplicationHandler().removeCallbacks(mResizeLoadingRunable);
        DownHandoutAssist.getInstance().setOnDLHandoutProgressListener(null);
        if(null!=mOldRecoverAssist) mOldRecoverAssist.setOnCacheRecoveryListener(null);

        if(null!=mDownCourseAdapter) mDownCourseAdapter=null;
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1200&&resultCode == Activity.RESULT_OK){
            onDLFinished("");
            return;
        }
        if(resultCode == Activity.RESULT_OK) {
            if("VideoPlay".equals(reqFrom)) {
                this.setResult(Activity.RESULT_OK,data);
                this.finish();
            }else if(requestCode==1001){
                onDLFinished("");
            }
        }
    }


}
