package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseHandoutAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.IPageStripTabInitData;
import com.huatu.handheld_huatu.business.other.PdfViewFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownHandoutAssist;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLHandoutListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseDataConverter;
import com.huatu.handheld_huatu.helper.image.MyPreloadTarget;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.SimpleListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.StorageUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UriUtil;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.StringUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * Created by cjx on 2018\7\20 0020.
 *  讲议
 *
 */
@Deprecated
public class CourseHandoutFragment extends ABaseListFragment<SimpleListResponse<HandoutBean.Course>> implements IPageStripTabInitData,
        OnRecItemClickListener,  ScrollableHelper.ScrollableContainer,OnDLHandoutListener {
    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @Override
    protected int getLimit() {  return 200; }

    public int getContentView() {
        return R.layout.comm_recyclerlist_nopull_fragment;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView;
    }


    @Override
    public View getScrollableView(){
        return mWorksListView;
    }

    CourseHandoutAdapter mListAdapter;

    private String mCourseId = "";
    private boolean mIsLocal=false;

    List<DownHandout> mDownHandoutList;
    HashMap<String,DownHandout> mDownHandoutMap=new HashMap<>();

    private HashMap<String,Integer> mDownLoadingMap; //下载中

    private HashMap<String,Integer> getDownLoadMap(){
        if(null==mDownLoadingMap){
            mDownLoadingMap=new HashMap<>();
        }
        return mDownLoadingMap;
    }

    public static CourseHandoutFragment getInstance(String courseId,boolean isLocal) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putBoolean(ArgConstant.IS_LOCAL_VIDEO,isLocal);
        CourseHandoutFragment tmpFragment = new CourseHandoutFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    @Override
    protected void parserParams(Bundle args) {

        mCourseId = args.getString(ArgConstant.COURSE_ID);
        mIsLocal=args.getBoolean(ArgConstant.IS_LOCAL_VIDEO);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListResponse = new SimpleListResponse<HandoutBean.Course>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new CourseHandoutAdapter(getContext(), mListResponse.mAdapterList);
        mListAdapter.setOnViewItemClickListener(this);
        DownHandoutAssist.getInstance().addDownloadListener(this);
    }

    @Override
    public void onStripTabRequestData() {

        mDownHandoutList = SQLiteHelper.getInstance().getAllDownHandouts(mCourseId,true);
        if (mIsLocal) {
            SimpleListResponse<HandoutBean.Course> tmpResponse = new SimpleListResponse<HandoutBean.Course>();
            tmpResponse.list =  new ArrayList<>();
            for (DownHandout bean : mDownHandoutList) {

                HandoutBean.Course tmpObj = new HandoutBean.Course();
                tmpObj.id = bean.getSubjectID();
                tmpObj.downStatus = bean.getDownStatus();
                tmpObj.localPath = bean.getFileUrl();
                tmpObj.title = bean.getSubjectName();
                tmpObj.fileSize = bean.getReserve1();
                tmpResponse.list.add(tmpObj);
            }
            super.onSuccess(tmpResponse);
        } else {
            for (DownHandout bean : mDownHandoutList) {
                mDownHandoutMap.put(bean.getSubjectID(), bean);
            }
            onFirstLoad();
        }

    }

/*    @Override
    public void requestData() {
        super.requestData() ;
        onFirstLoad();
    }*/

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_none_handout);
        getEmptyLayout().setTipText(R.string.xs_my_empty);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);

        mWorksListView.setPagesize(getLimit());
        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.setRecyclerAdapter(mListAdapter);
     }

    @Override
    protected void setListener() {
        mWorksListView.setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        //ProductApiServer.getMainpageWorks(mSellerId, offset, limit).enqueue(getCallback());
        CourseApiService.getApi().getHandoutInfo(StringUtils.parseInt(mCourseId),"1").enqueue(getCallback());
    }

    @Override
    public void onSuccess(SimpleListResponse<HandoutBean.Course> response)   {
         for(HandoutBean.Course bean:response.getListResponse()){
            if(mDownHandoutMap.containsKey(bean.id)){
                bean.downStatus=DownBtnLayout.FINISH;
                bean.localPath=mDownHandoutMap.get(bean.id).getFileUrl();
            }

        }
        super.onSuccess(response);
    }


    CustomConfirmDialog mDeleteDlg;
    private HandoutBean.Course mCurrentItem ;
    private int mCurrentPostion;
    private void deleteConfrim(){

         if(mCurrentItem==null) return;
         if(mDeleteDlg == null) {
            mDeleteDlg = DialogUtils.createDialog(getActivity(),
                    "", "是否删除该讲义\n");
            mDeleteDlg.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int flag=  SQLiteHelper.getInstance().deleteSingleHandOut(mCurrentItem.id,mCourseId);
                    if(flag>-1){
                        FileUtil.deleteFile(mCurrentItem.localPath);
                        LogUtils.e("onItemClick",mCurrentItem.localPath+"");
                        if(mIsLocal){
                            mListAdapter.removeAt(mCurrentPostion,mCurrentItem);
                            if(mListAdapter.getItemCount()==0) showEmptyLayout();
                        }else {
                            mCurrentItem.downStatus= DownBtnLayout.NORMAL;
                            mListAdapter.notifyItemChanged(mCurrentPostion);
                        }

                    }else ToastUtils.showShort("操作失败");
                }
            });
        }
        if(!mDeleteDlg.isShowing()) {
            mDeleteDlg.show();
        }

    }

    @Override
    public void onItemClick(int position,View view,int type) {
        mCurrentPostion=position;
        mCurrentItem = mListAdapter.getItem(position);
        if(null==mCurrentItem) return;
         switch (type) {
            case EventConstant.EVENT_JOIN_IN:

                if(mCurrentItem.downStatus==DownBtnLayout.DOWNLOADING) return;
                else if(mCurrentItem.downStatus==DownBtnLayout.NORMAL){
                    readHandout(mCurrentItem,position);
                }else {
                    deleteConfrim();
                }
               // doCollectAction(view, String.valueOf(item.id), position);
                break;
            case EventConstant.EVENT_ALL:

                readHandout(mCurrentItem,position);
                break;
        }
    }

    private void checkHasCourseInfo(){

        CourseInfoBean tmpCourse=null;
        if(getActivity() instanceof BJRecordPlayActivity){
            tmpCourse=((BJRecordPlayActivity)getActivity()).getCourseInfo();
        }
        if(tmpCourse==null) {
            ToastUtils.showShort("选择出错");
            return;
        }

        LogUtils.e("startDown",tmpCourse==null?"": GsonUtil.GsonString(tmpCourse));
        DownLoadCourse downLoadCourse= CourseDataConverter.convertCatalogInfoListToDownCourse(tmpCourse);

        final String  imgDataPath = FileUtil.getDownloadCourseImagePath(Md5Util.toMD5(downLoadCourse.getCourseName()));//加密一次防止出现关键字"/"
        if(!FileUtil.isFileExist(imgDataPath)){
             ImageLoad.downloadPhotoCover(downLoadCourse.getImageURL(),new MyPreloadTarget(imgDataPath) {
                @Override
                public void onDownFinished(boolean isSuccess, String filePath) {
                    try {
                        IoExUtils.copyFile(new File(filePath), new File(imgDataPath), false);
                    } catch (IOException e) {  }
                }
            });
        }
        downLoadCourse.setImagePath(imgDataPath);
         LogUtils.e("startDown2", GsonUtil.GsonString(downLoadCourse));
         SQLiteHelper.getInstance().insertDB(downLoadCourse,null);
    }

    private void readHandout( HandoutBean.Course course,final int position) {

        if(course.downStatus==DownBtnLayout.FINISH&&FileUtil.isFileExist(course.localPath)){
            LogUtils.e("readHandout",course.localPath);
            //FileUtil.deleteFile(handoutfilePath);

            readPDF(course.localPath);
        }
        else {
            if (course.downStatus == DownBtnLayout.DOWNLOADING){
                return;
            }

            if (TextUtils.isEmpty(course.downloadUrl)) {
                ToastUtils.showShort("下载地址为空");
                return;
            }
            checkHasCourseInfo();
            DownHandoutAssist.getInstance().add(course, mCourseId);
            mListAdapter.notifyItemChanged(position);
            getDownLoadMap().put(course.id,position);
        }
     }

    private void readPDF(final String handout) {
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String type = CommonUtils.getMIMEType(new File(handout));
                    intent= UriUtil.setIntentDataAndType(intent,type,new File(handout),false);

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, 100);
                    } else {
                        if(CommonUtils.hasX5nited())
                            PdfViewFragment.lanuch(getContext(),handout);
                        else
                           CommonUtils.showToast("请安装能打开此文件的程序哦");
                    }
                } catch (Exception e) {
                    if(CommonUtils.hasX5nited())
                        PdfViewFragment.lanuch(getContext(),handout);
                    else
                        CommonUtils.showToast("请安装能打开此文件的程序哦");
                }
            }
        });
    }


    @Override
    public void onDLError(String s, int errorCode){

    }

    @Override
    public void onDLFinished(String downID){
        if(getDownLoadMap().containsKey(downID)){
            int postion=getDownLoadMap().get(downID);
            mListAdapter.getItem(postion).selected=false;
            mListAdapter.getItem(postion).downStatus=DownBtnLayout.FINISH;
            mListAdapter.notifyItemChanged(postion);
        }
    }

    @Override
    public void onDestroy() {
        mListAdapter=null;
       // OkHttpUtils.getInstance().cancelTag(this);

        DownHandoutAssist.getInstance().removeDownloadListener(this);
         if(mDeleteDlg!=null&&mDeleteDlg.isShowing())
            mDeleteDlg.dismiss();
       // OkHttpUtils.cancelTag(this);//取消以Activity.this作为tag的请求
        super.onDestroy();

        /*try {
            if (currenttTitlePosition != -1) {
                //savePlayProgress(currenttTitlePosition,true);
            }
        } catch (Exception e) {
            // Log.e("player error", e.getMessage());
        }*/
    }
}
