package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseHandoutGroupAdapter;
import com.huatu.handheld_huatu.base.PagePullToRefreshStatusBlock;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.base.fragment.IPageStripTabInitData;
import com.huatu.handheld_huatu.business.other.PdfViewFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
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
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.AnimUtils;
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
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UriUtil;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2019\12\24 0024.
 */

public class CourseHandoutGroupFragment extends AbsSettingFragment  implements IPageStripTabInitData,
         ScrollableHelper.ScrollableContainer,OnRecItemClickListener,OnDLHandoutListener{

    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mLoadingView;

    @Override
    public View getScrollableView(){
        return mWorksListView;
    }

    private String mCourseId = "";
    private boolean mIsLocal=false;

    CourseHandoutGroupAdapter mListAdapter;

    PagePullToRefreshStatusBlock<SimpleListResponse<HandoutBean.CourseTypeInfo>> mPageBlock;

    @Override
    public int getContentView() {
        return R.layout.comm_recyclerlist_nopull_fragment;
    }

    List<DownHandout> mDownHandoutList;
    HashMap<String,DownHandout> mDownHandoutMap=new HashMap<>();

    private HashMap<String,Integer> mDownLoadingMap; //下载中

    private HashMap<String,Integer> getDownLoadMap(){
        if(null==mDownLoadingMap){
            mDownLoadingMap=new HashMap<>();
        }
        return mDownLoadingMap;
    }

    public static CourseHandoutGroupFragment getInstance(String courseId,boolean isLocal) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putBoolean(ArgConstant.IS_LOCAL_VIDEO,isLocal);
        CourseHandoutGroupFragment tmpFragment = new CourseHandoutGroupFragment();
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
        DownHandoutAssist.getInstance().addDownloadListener(this);
    }

    @Override
    protected void setListener() {

        mLoadingView.setStatusStringId(R.string.xs_loading_text, R.string.xs_none_handout);
        mLoadingView.setTipText(R.string.xs_my_empty);
        mLoadingView.setEmptyImg(R.drawable.down_no_num);

        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPageBlock=new PagePullToRefreshStatusBlock<>();
        mPageBlock.setLimit(1000000);
        mPageBlock.setListView(mWorksListView);
        mPageBlock.setloadingView(mLoadingView);

        SimpleListResponse<HandoutBean.CourseTypeInfo> response= new SimpleListResponse<>();
        response.mAdapterList = new ArrayList<>();
        mPageBlock.mListResponse=response;

        mPageBlock.setTypeFactory(new PagePullToRefreshStatusBlock.TypeFactory<SimpleListResponse<HandoutBean.CourseTypeInfo>>(){

            @Override
            public Observable<SimpleListResponse<HandoutBean.CourseTypeInfo>> getListObservable(int pageIndex, int limit){
                return CourseApiService.getApi().getGroupHandoutInfo(mCourseId)
                         .map(new Func1<SimpleListResponse<HandoutBean.CourseGroup>, SimpleListResponse<HandoutBean.CourseTypeInfo>>() {
                            @Override
                            public SimpleListResponse<HandoutBean.CourseTypeInfo> call(SimpleListResponse<HandoutBean.CourseGroup> listResponse) {

                                SimpleListResponse<HandoutBean.CourseTypeInfo> response= new SimpleListResponse<>();
                                response.code=listResponse.code;
                                response.message=listResponse.message;
                                response.list=new ArrayList<>();
                                if(listResponse.list!=null&&(!ArrayUtils.isEmpty(listResponse.list))){

                                    for(int i=0;i<listResponse.list.size();i++){
                                        HandoutBean.CourseGroup groupInfo=listResponse.list.get(i);
                                        response.list.add(groupInfo.toHeadBean());

                                        if(!ArrayUtils.isEmpty(groupInfo.list)){
                                            for(int k=0;k<groupInfo.list.size();k++){
                                                HandoutBean.CourseTypeInfo bean=groupInfo.list.get(k);
                                                if(mDownHandoutMap.containsKey(bean.rid)){
                                                    bean.downStatus= DownBtnLayout.FINISH;
                                                    bean.localPath=mDownHandoutMap.get(bean.rid).getFileUrl();
                                                }
                                                response.list.add(bean);
                                            }
                                        }
                                     }
                                }
                                return response;
                            }
                        });
            }

            @Override
            public RecyclerView.Adapter getAdapter(SimpleListResponse<HandoutBean.CourseTypeInfo> response){
                mListAdapter = new CourseHandoutGroupAdapter(getContext(), response.mAdapterList);
                mListAdapter.setOnViewItemClickListener(CourseHandoutGroupFragment.this);
                return mListAdapter;
            }

            @Override
            public CompositeSubscription getComSubscription(){
                return getSubscription();
            }
        });
        mPageBlock.build();
       // block.onFirstLoad();
    }

    @Override
    public void onStripTabRequestData() {

       /* mDownHandoutList = SQLiteHelper.getInstance().getAllDownHandouts(mCourseId,true);
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
        } else*/

       if(mIsLocal){


       }else {
           mDownHandoutList = SQLiteHelper.getInstance().getAllDownHandouts(mCourseId,true);
           {
               for (DownHandout bean : mDownHandoutList) {
                   mDownHandoutMap.put(bean.getSubjectID(), bean);
               }
               mPageBlock.onFirstLoad();
           }
       }
    }

    private void deleteConfrim(){

        if(mCurrentItem==null) return;
        if(mDeleteDlg == null) {
            mDeleteDlg = DialogUtils.createDialog(getActivity(),
                    "", "是否删除该讲义\n");
            mDeleteDlg.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int flag=  SQLiteHelper.getInstance().deleteSingleHandOut(mCurrentItem.rid,mCourseId);
                    if(flag>-1){
                        FileUtil.deleteFile(mCurrentItem.localPath);
                        LogUtils.e("onItemClick",mCurrentItem.localPath+"");
                        if(mIsLocal){
                            mListAdapter.removeAt(mCurrentPostion,mCurrentItem);
                            if(mListAdapter.getItemCount()==0) mLoadingView.showEmptyStatus();
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

    CustomConfirmDialog mDeleteDlg;
    private HandoutBean.CourseTypeInfo mCurrentItem ;
    private int mCurrentPostion;
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
            case EventConstant.EVENT_MORE:
                mCurrentItem = mListAdapter.getItem(position);
                if(mCurrentItem.isClosed()){
                    AnimUtils.showOpenRotation(view);
                }else {
                    // LogUtils.e("onItemClick",view.getRotation()+"");
                    AnimUtils.showCloseRotation(view);
                }
                loadChild(mCurrentItem,position);
                // doCollectAction(view, String.valueOf(item.id), position);
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

    private void readHandout( HandoutBean.CourseTypeInfo course,final int position) {

        if(course.downStatus==DownBtnLayout.FINISH&&FileUtil.isFileExist(course.localPath)){
            LogUtils.e("readHandout",course.localPath);
            //FileUtil.deleteFile(handoutfilePath);

            readPDF(course.localPath);
        }
        else {
            if (course.downStatus == DownBtnLayout.DOWNLOADING){
                return;
            }

            if (TextUtils.isEmpty(course.fileUrl)) {
                ToastUtils.showShort("下载地址为空");
                return;
            }
            checkHasCourseInfo();
            DownHandoutAssist.getInstance().add(course, mCourseId);
            mListAdapter.notifyItemChanged(position);
            getDownLoadMap().put(course.rid,position);
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


    private void loadChild(final HandoutBean.CourseTypeInfo curItem, final int postion){
        if(!curItem.isClosed()){
            // List<PurchasedCourseBean.Data> tmplist= getAllCollapseChilds(curItem.childs);

            if(!ArrayUtils.isEmpty(curItem.childs)){
                mListAdapter.removeAllAt(postion+1,curItem.childs);
                curItem.setClosed(true);
            }else {
                ArrayList<HandoutBean.CourseTypeInfo> treeAdapterItems = new ArrayList<>();
                String curParentId=curItem.classId;
                boolean isEndList=true;
                for(int i=(postion+1);i<mListAdapter.getItemCount();i++){
                    HandoutBean.CourseTypeInfo curBean=mListAdapter.getItem(i);
                    if(curParentId.equals(curBean.netClassId)){
                        treeAdapterItems.add(curBean);
                    }else {
                        isEndList=false;
                        break;
                    }
                }

                curItem.childs=treeAdapterItems;
                mListAdapter.removeAllAt(postion+1,treeAdapterItems);
                curItem.setClosed(true);
            }
        }else {//展开
            if (!ArrayUtils.isEmpty(curItem.childs)) {
                curItem.setClosed(false);

                boolean isLastPostion = postion == (mListAdapter.getItemCount() - 1);
                mListAdapter.addAllAt(postion + 1, curItem.childs);
                if (isLastPostion) {
                    mWorksListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=mWorksListView){
                                mWorksListView.smoothScrollToPosition(postion + 1);
                            }
                        }
                    }, 500);
                }
             }
        }
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
