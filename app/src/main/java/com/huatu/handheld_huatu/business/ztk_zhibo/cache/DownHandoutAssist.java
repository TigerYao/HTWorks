package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLHandoutListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLHandoutProgressListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.StorageUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by cjx on 2018\12\13 0013.
 */

public class DownHandoutAssist {
    private static volatile DownHandoutAssist instance;

    private final static String TAG="DownHandoutAssist";
    private DownHandoutAssist() {
    }

    private boolean mPauseStatus=false;

    WeakReference<OnDLHandoutProgressListener> mDLHandoutProgressListener;
    public void setOnDLHandoutProgressListener(OnDLHandoutProgressListener progressListener){

        if(null==progressListener){
            mDLHandoutProgressListener=null;
        }else {
            mDLHandoutProgressListener=new WeakReference<>(progressListener);
        }

    }

    private List<WeakReference<OnDLHandoutListener>> mDLHandListenerList =new ArrayList<>();
    public void addDownloadListener(OnDLHandoutListener mDLVodListener) {
        if(mDLHandListenerList==null){
            mDLHandListenerList=new ArrayList();
        }
        mDLHandListenerList.add(new WeakReference<>(mDLVodListener));
    }

    public void removeDownloadListener(OnDLHandoutListener mDLVodListener) {
        if (mDLHandListenerList != null) {
            for(WeakReference<OnDLHandoutListener> weakReference : mDLHandListenerList) {
                if(weakReference.get() == mDLVodListener) {
                    mDLHandListenerList.remove(weakReference);
                    break;
                }
            }
        }
    }

    public static DownHandoutAssist getInstance() {
        if (instance == null) {
            synchronized (DownHandoutAssist.class) {
                if (instance == null) {
                    instance = new DownHandoutAssist();
                    instance.init();
                }
            }
        }
        return instance;
    }
    private @Nullable  Runnable idleCallback;
    //正在下载的课件列表
    //private SparseArray<DownHandout> mDownLoadingMap = new SparseArray<DownHandout>();
    private final int maxRequests = 5;
    /**
     * 初始化下载配置
     */
    private void init() {

        idleCallback=new Runnable() {
            @Override
            public void run() {
                if(null!=mDLHandoutProgressListener&&(null!=mDLHandoutProgressListener.get()))
                    mDLHandoutProgressListener.get().onDLHandoutProgress("-10000",0,"","");
            }
        };
       List<DownHandout>  tmplist=SQLiteHelper.getInstance().getAllUnFinishDownHandouts(false);

       LogUtils.e("DownHandoutAssist","size_"+ArrayUtils.size(tmplist)+"");
       for(DownHandout bean:tmplist)
           if(!TextUtils.isEmpty(bean.getReserve2())&&(bean.getDownStatus()!=DownBtnLayout.PAUSE))
              enqueue(bean);
    }

    private final Deque<DownHandout> readyAsyncCalls = new ArrayDeque<>();
    private final Deque<DownHandout> runningAsyncCalls = new ArrayDeque<>();


    public void add(HandoutBean.CourseTypeInfo course,String mCourseId){

        if(TextUtils.isEmpty(course.fileUrl)) return;
        if(!course.fileUrl.contains("/"))     return;
        String substring = course.fileUrl.substring(course.fileUrl.lastIndexOf("/") + 1);
        String newName = mCourseId + "_" + UserInfoUtil.userId + course.rid + "_" + StringUtils.filterFileName(substring);

        final File downFile = StorageUtils.getHuatuTempFile(UniApplicationContext.getContext(), newName);
        final DownHandout tmpObj = new DownHandout();
        tmpObj.setCourseID(mCourseId);
        tmpObj.setFileType(0);
        tmpObj.setReserve1(course.fileSize);
        tmpObj.setReserve2(course.fileUrl);
        tmpObj.setSubjectID(course.rid);
        tmpObj.setSubjectName(course.title);
        tmpObj.setFileUrl(downFile.getAbsolutePath());

        course.selected = false;
        course.downStatus = DownBtnLayout.DOWNLOADING;
        course.localPath = downFile.getAbsolutePath();
//            mListAdapter.notifyItemChanged(position);
        // handler.sendMessage(Message.obtain(handler, 110));

        SQLiteHelper.getInstance().insertHandOut(tmpObj);
        enqueue(tmpObj);
    }

    public void add(HandoutBean.Course course,String mCourseId){

        if(TextUtils.isEmpty(course.downloadUrl)) return;
        if(!course.downloadUrl.contains("/"))     return;
        String substring = course.downloadUrl.substring(course.downloadUrl.lastIndexOf("/") + 1);
        String newName = mCourseId + "_" + UserInfoUtil.userId + course.id + "_" + StringUtils.filterFileName(substring);

        final File downFile = StorageUtils.getHuatuTempFile(UniApplicationContext.getContext(), newName);
        final DownHandout tmpObj = new DownHandout();
        tmpObj.setCourseID(mCourseId);
        tmpObj.setFileType(0);
        tmpObj.setReserve1(course.fileSize);
        tmpObj.setReserve2(course.downloadUrl);
        tmpObj.setSubjectID(course.id);
        tmpObj.setSubjectName(course.title);
        tmpObj.setFileUrl(downFile.getAbsolutePath());

        course.selected = false;
        course.downStatus = DownBtnLayout.DOWNLOADING;
        course.localPath = downFile.getAbsolutePath();
//            mListAdapter.notifyItemChanged(position);
        // handler.sendMessage(Message.obtain(handler, 110));

        SQLiteHelper.getInstance().insertHandOut(tmpObj);
        enqueue(tmpObj);
    }

    public  synchronized void enqueue(DownHandout call) {
        if (runningAsyncCalls.size() < maxRequests) {
            runningAsyncCalls.add(call);
            //executorService().execute(call);
            startTask(call);
        } else {
            readyAsyncCalls.add(call);
        }
    }

    private void promoteCalls() {
        if(mPauseStatus) return;
        if (runningAsyncCalls.size() >= maxRequests) return; // Already running max capacity.
        if (readyAsyncCalls.isEmpty()) return; // No ready calls to promote.

        for (Iterator<DownHandout> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            DownHandout call = i.next();

            if (runningAsyncCalls.size()<maxRequests) {//;runningCallsForHost(call) < maxRequestsPerHost
                i.remove();
                runningAsyncCalls.add(call);
                startTask(call);
            }

            if (runningAsyncCalls.size() >= maxRequests) return; // Reached max capacity.
        }
    }

    public synchronized int runningCallsCount() {
        return runningAsyncCalls.size() +readyAsyncCalls.size();
    }

    private   void finished( DownHandout call, boolean promoteCalls) {
        int runningCallsCount;
        Runnable idleCallback;
        synchronized (this) {
           if (!runningAsyncCalls.remove(call)) throw new AssertionError("Call wasn't in-flight!");
           if (promoteCalls) promoteCalls();

           LogUtils.e("DownHandoutAssist","runningCallsCount_"+ArrayUtils.size(runningAsyncCalls)+"");
           runningCallsCount = runningCallsCount();
            idleCallback = this.idleCallback;
        }

        if (runningCallsCount == 0 && idleCallback != null) {
            idleCallback.run();
        }
    }

    private long mlastProgressTime=0;
    private void reportDownSpeed(float downSpeed,String downId,String downName,String fileSize){

        long diffProgressTime=(System.currentTimeMillis()-mlastProgressTime)/1000;
        if((diffProgressTime>8)) {
            mlastProgressTime=System.currentTimeMillis();

            if(null!=mDLHandoutProgressListener&&(null!=mDLHandoutProgressListener.get()))
                mDLHandoutProgressListener.get().onDLHandoutProgress(downId,downSpeed,downName,fileSize);
            LogUtils.e("DownHandoutAssist",downSpeed+"");
        }
    }


    private void startTask(DownHandout task){
        String substring = task.getReserve2().substring(task.getReserve2().lastIndexOf("/") + 1);
        String newName = task.getCourseID() + "_" + UserInfoUtil.userId + task.getSubjectID() + "_" + StringUtils.filterFileName(substring);

        LogUtils.e("startTask",task.getReserve2()+"");
        //File downFile = StorageUtils.getHuatuTempFile(UniApplicationContext.getContext(), newName);
       // SQLiteHelper.getInstance().insertHandOut(tmpObj);
        OkHttpUtils.get().url(task.getReserve2()).tag(TAG).build()
                .execute(new FileCallBack(StorageUtils.getHuatuTempDirectory(UniApplicationContext.getContext()).getAbsolutePath(), newName,task) {
                    @Override
                    public void inProgress(float progress, long total) {


                        reportDownSpeed(getDownloadSpeed(),this.getDownHandout().getSubjectID(),this.getDownHandout().getSubjectName(),this.getDownHandout().getReserve1());
                        if (progress == 1) {
                            SQLiteHelper.getInstance().upDateHandoutStatus(this.getDownHandout().getSubjectID(), DownBtnLayout.FINISH);
                            for (WeakReference<OnDLHandoutListener> mDLVodListener : mDLHandListenerList) {
                                OnDLHandoutListener vodListener=mDLVodListener.get();
                                if(vodListener!=null)  vodListener.onDLFinished(this.getDownHandout().getSubjectID());
                            }
                            LogUtils.e("DownHandoutAssist", "onSuccess," + this.getDownHandout().getSubjectName());
                            finished(this.getDownHandout(),true);
                           // LogUtils.e("onSuccessw", "onSuccessw," + downFile.getAbsolutePath());
                           //SQLiteHelper.getInstance().upDateHandoutStatus(tmpObj.getSubjectID(), DownBtnLayout.FINISH);
                           // readPDF(downFile.getAbsolutePath());
                        }
                    }

                    @Override
                    public void onError(Call request, Exception e) {
                        //Log.e(TAG, "onError :" + e.getMessage());
                        SQLiteHelper.getInstance().upDateHandoutStatus(this.getDownHandout().getSubjectID(), DownBtnLayout.ERROR);

                        LogUtils.e("DownHandoutAssist", "onError," + this.getDownHandout().getSubjectName());
                        finished(this.getDownHandout(),true);
                        for (WeakReference<OnDLHandoutListener> mDLVodListener : mDLHandListenerList) {
                            OnDLHandoutListener vodListener=mDLVodListener.get();
                            if(vodListener!=null)  vodListener.onDLError(this.getDownHandout().getSubjectID(),-1);
                        }
                    }

                    @Override
                    public void onResponse(File file) {
                        LogUtils.e("onResponse :", file.getAbsolutePath());
                    }
                });
    }

    public void stopAll(){
         mPauseStatus=true;
         OkHttpUtils.getInstance().cancelTag(TAG);
         readyAsyncCalls.clear();
         mPauseStatus=false;
    }

    public void reStartAll(){
        mPauseStatus=false;
        if(ArrayUtils.isEmpty(readyAsyncCalls)&&ArrayUtils.isEmpty(runningAsyncCalls)){
            init();
        }else{
            promoteCalls();
        }
     }

     public void DeleteTask(DownHandout task){
         mPauseStatus=true;//防止finish改变ready队列状态
         SQLiteHelper.getInstance().deleteSingleHandOut(task.getSubjectID(),task.getCourseID());
         for (Iterator<DownHandout> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
             DownHandout call = i.next();
             if (call.getSubjectID().equals(task.getSubjectID())) {//;runningCallsForHost(call) < maxRequestsPerHost
                  i.remove();
                  break;
             }
         }
         mPauseStatus=false;
         promoteCalls();
      }

    public void DeleteTaskList(List<DownHandout> tasklist){
        mPauseStatus=true;//防止finish改变ready队列状态
        for (Iterator<DownHandout> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            DownHandout call = i.next();
            for(DownHandout delCall:tasklist){
                if (call.getSubjectID().equals(delCall.getSubjectID())) {//;runningCallsForHost(call) < maxRequestsPerHost
                    i.remove();
                    break;
                }
            }
        }
        Map<String, List<DownHandout>> map = ArrayUtils.group(tasklist, new ArrayUtils.GroupBy<String>() {
            @Override
            public String groupby(Object obj) {
                DownHandout d = (DownHandout) obj;
                return d.getCourseID(); // 分组依据为课程ID
            }
        });
        for (String key : map.keySet()) {
            List<DownHandout> tmplist= map.get(key);
            List<String> joinStr= ArrayUtils.arrayStringList(tmplist, new ArrayUtils.GroupBy<String>() {
                @Override
                public String groupby(Object obj) {
                    DownHandout d = (DownHandout) obj;
                    return d.getSubjectID(); //
                }
            });

            LogUtils.e("DeleteTaskList",joinStr+","+key);
            SQLiteHelper.getInstance().deleteBatchHandOut(joinStr,key);
        }
      /*  for(DownHandout delBean:tasklist){
            SQLiteHelper.getInstance().deleteSingleHandOut(delBean.getSubjectID(),delBean.getCourseID());
        }*/
        mPauseStatus=false;
        promoteCalls();
    }
}
