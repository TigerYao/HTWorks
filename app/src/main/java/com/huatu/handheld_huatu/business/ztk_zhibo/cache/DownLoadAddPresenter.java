package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.Manifest;
import android.app.Activity;
import android.view.View;

import com.baijiahulian.common.permission.AppPermissions;
import com.baijiayun.download.constant.VideoDefinition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.DownAddAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.helper.image.MyPreloadTarget;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.Sensor.CourseInfoForStatistic;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by cjx on 2018\7\10 0010.
 */

public class DownLoadAddPresenter {

    private DownLoadCourse mCourse;
    private Activity mContext;
    private List<DownLoadLesson> mDownLoadlists;
    private String mImgDataPath;

    private int mDefinitonIndex=0;

    // 清晰度优先数组
    private List<VideoDefinition> definitionList = new ArrayList<>(Arrays.asList(VideoDefinition.SD,
            VideoDefinition.HD, VideoDefinition.SHD, VideoDefinition._720P, VideoDefinition._1080P));
    onTaskAddListener mOnTaskAddListener;

    public void setCustomDefinition(int index){
        mDefinitonIndex=index;
    }

    public interface onTaskAddListener{
        void onTaskAdd();

        void onAllTaskAdd();
    }

    public DownLoadAddPresenter(DownLoadCourse course,Activity context,onTaskAddListener taskAddListener){
        this.mContext=context;
        this.mOnTaskAddListener=taskAddListener;
        if(course==null) return;
        setCurCourse(course);

    }

    public void setCurCourse(DownLoadCourse course){
        this.mCourse=course;
        this.mImgDataPath = FileUtil.getDownloadCourseImagePath(Md5Util.toMD5(mCourse.getCourseName()));//加密一次防止出现关键字"/"
         ImageLoad.downloadPhotoCover(mCourse.getImageURL(),new MyPreloadTarget(mImgDataPath) {
            @Override
            public void onDownFinished(boolean isSuccess, String filePath) {
                try {
                    IoExUtils.copyFile(new File(filePath), new File(mImgDataPath), false);
                } catch (IOException e) {  }
            }
        });
    }

    // 外部所有列表
    public void checkStartDownload(List<DownLoadLesson> downLoadLessons) {
        this.mDownLoadlists=downLoadLessons;
        if(CommonUtils.isFastDoubleClick()) {
            return;
        }
        AppPermissions.newPermissions(mContext)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            long totalAvailableSpace = FileUtil.getAvailableSpaceSize() / 1024;//单位M
                            if (totalAvailableSpace < 200) {
                                DialogUtils.onShowConfirmDialog(mContext, null, "",
                                        ResourceUtils.getString(R.string.warn_disk_space), "", "确定");
                                return;
                            }
                            startDownload();
                        } else {
                            ToastUtils.showMessage("您拒绝了存储权限请求,无法使用下载功能,您需要到设置里面允许此项权限");
                        }
                    }
                });
    }

    private void startDownload() {
        List<DownLoadLesson> coursewaresList = new ArrayList<>();
        for(int i = 0; i < mDownLoadlists.size(); i++) {
            if(DownAddAdapter.isDownedOrLoading(mDownLoadlists.get(i))) {
                continue;
            }
            if(mDownLoadlists.get(i).isSelect()) {
                coursewaresList.add(mDownLoadlists.get(i));
            }
        }
        if(coursewaresList.size() == 0) {
            return;
        }
        if (NetUtil.isConnected() && !NetUtil.isWifi() ) {
            showWarningDlg(coursewaresList);
        } else {
            startDownload(coursewaresList);
        }
    }

    private void showWarningDlg(final List<DownLoadLesson> coursewaresList) {
        boolean downflag=  PrefStore.canDownloadIn3G();
        if(downflag){
            startDownload(coursewaresList);
            return;
        }
        DialogUtils.onShowWarnTraffic(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload(coursewaresList);
        }});
    }


    //点击 初始化db  进入下载页
    private void startDownload(List<DownLoadLesson> needDownLessions) {
        List<VideoDefinition> customDefiniton;

        if(mDefinitonIndex!=0){
            VideoDefinition curDefinition= definitionList.get(mDefinitonIndex);
            customDefiniton=new ArrayList<>();
            customDefiniton.add(curDefinition);
            for(VideoDefinition bean:definitionList){
                if(bean!=curDefinition){
                    customDefiniton.add(bean);
                 }
             }
        } else {
            customDefiniton=definitionList;
        }
        LogUtils.e("startDownload",GsonUtil.GsonString(customDefiniton));

        for(DownLoadLesson courseware : needDownLessions) {
            int position = -1;
            //从需要下载的列表中找到原始的列表，并修改其状态
            for(int i = 0; i < mDownLoadlists.size(); i++) {
                if(Method.isEqualString(courseware.getDownloadID(),
                        mDownLoadlists.get(i).getDownloadID())) {
                    mDownLoadlists.get(i).setDownStatus(DownLoadStatusEnum.START.getValue());//1
                    mDownLoadlists.get(i).setSelect(false);
                    position = i;
                    break;
                }
            }
            mCourse.setImagePath(mImgDataPath);
            courseware.setDownStatus(DownLoadStatusEnum.INIT.getValue());//-2
            courseware.setImagePath(mImgDataPath);
            //courseware.setLesson(mDownLoadlists.size() - position);//倒序

            SQLiteHelper.getInstance().insertDB(mCourse, courseware);


            DownLoadAssist.getInstance().addDownload(courseware,false,customDefiniton);//
            if(mOnTaskAddListener!=null)
                mOnTaskAddListener.onTaskAdd();

            sendDownTrack(mCourse.getRealCourseID(),courseware.classId,courseware.getSubjectName(),true);
        }
        if(!ArrayUtils.isEmpty(needDownLessions)){//下载最前一个
            DownLoadAssist.getInstance().download(needDownLessions.get(0),customDefiniton);
        }

        IoExUtils.saveJsonFile(GsonUtil.GsonString(mDownLoadlists), Constant.NEEDDOWN_CACHE_LIST);
        if(mOnTaskAddListener!=null)
            mOnTaskAddListener.onAllTaskAdd();
     /*   UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
            @Override
            public void run() {


                onClickPreview();
            }
        }, 300);*/
    }

    private CourseInfoForStatistic mTrackCourseInfo;
    public void sendDownTrack(String courseId,  int classId,String title ,final boolean isAdd){

        final CourseWareInfo tmpWareInfo=new CourseWareInfo();
        tmpWareInfo.classId=classId;
        tmpWareInfo.title=title;
        if(mTrackCourseInfo!=null){
            StudyCourseStatistic.sendDownloadCourseTrack(mTrackCourseInfo,tmpWareInfo,isAdd);
            return;
        }
        ServiceProvider.getSensorsStatistic(courseId, new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                CourseInfoForStatistic courseInfoForStatistic = (CourseInfoForStatistic) model.data;
                mTrackCourseInfo=courseInfoForStatistic;
                StudyCourseStatistic.sendDownloadCourseTrack(courseInfoForStatistic,tmpWareInfo,isAdd);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    public void destory(){
        this.mContext=null;
        this.mDownLoadlists=null;
        this.mOnTaskAddListener=null;
    }

}
