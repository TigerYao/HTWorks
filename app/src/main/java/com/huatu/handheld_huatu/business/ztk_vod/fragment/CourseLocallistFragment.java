package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.adapter.course.RecordCatalogAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_vod.OnCoursePlaylistener;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.PurchaseCourseListResponse;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by cjx on 2018\7\20 0020.
 */

public class CourseLocallistFragment extends ABaseListFragment<PurchaseCourseListResponse> implements AStripTabsFragment.IStripTabInitData, OnRecItemClickListener,  ScrollableHelper.ScrollableContainer,OnPlaySelectListener  {
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

    RecordCatalogAdapter mListAdapter;

    private String mCourseId = "";
    private int mCourseWareId;

    public static CourseLocallistFragment getInstance(String courseId,int courseWareId) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putInt(ArgConstant.KEY_ID, courseWareId);
        CourseLocallistFragment tmpFragment = new CourseLocallistFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    @Override
    protected void parserParams(Bundle args) {
         mCourseId = args.getString(ArgConstant.COURSE_ID);
         mCourseWareId= args.getInt(ArgConstant.KEY_ID,0);
    }

    @Override
    public  List<PurchasedCourseBean.Data> getCurrentCourseList(){
        if(null!=mListAdapter){
            return mListAdapter.getAllItems();
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListResponse = new PurchaseCourseListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mListAdapter = new RecordCatalogAdapter(getContext(), mListResponse.mLessionlist,true);
        mListAdapter.setSelectId(mCourseWareId);
        mListAdapter.setOnViewItemClickListener(this);
    }

    @Override
    public  void onSelectChange(int id,int hasPlayTime){
        if(mListAdapter!=null){
            if(hasPlayTime==0) mListAdapter.setSelectId(id);
            else
                mListAdapter.setSelectId(id,hasPlayTime);
        }
    }

    @Override
    public void onStripTabRequestData() {
        onFirstLoad();
    }

    @Override
    public void requestData() {
        super.requestData() ;
        onFirstLoad();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_none_date);
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


    int mInitPlayIndex=0;
    @Override
    protected void onLoadData(int offset, int limit) {
        mInitPlayIndex=0;
        List<DownLoadLesson> mDownedCoursewares = SQLiteHelper.getInstance().getLessonsByStatus(mCourseId, "2");
        PurchaseCourseListResponse tmpResponse=new PurchaseCourseListResponse();
        tmpResponse.data=new PurchasedCourseBean();
        tmpResponse.data.list=new ArrayList<>();
        boolean hasFind=false;
        for(DownLoadLesson localLesson:mDownedCoursewares){

            PurchasedCourseBean.Data tmpObj=new PurchasedCourseBean.Data();

            if(localLesson.catalogId>0){
                tmpObj.id=String.valueOf(localLesson.catalogId);
            }
            tmpObj.setSerialNumber(localLesson.getLesson());
            tmpObj.title=localLesson.getSubjectName();
            tmpObj.type=2;
            tmpObj.isTrial="0";
            ////1点播2直播3直播回放	number	@mock=0

            if(localLesson.getPlayerType()== PlayerTypeEnum.BaiJia.getValue()){
                 tmpObj.videoType= 3;
                tmpObj.token=localLesson.getVideoToken();
                tmpObj.bjyRoomId=localLesson.getRoomId();
                tmpObj.bjySessionId=localLesson.getSessionId();

            }else if(localLesson.getPlayerType()== PlayerTypeEnum.BjRecord.getValue()){
                tmpObj.videoType= 1;
                tmpObj.videoId=localLesson.getRoomId();
                tmpObj.token=localLesson.getVideoToken();
            }
            else if(localLesson.getPlayerType()== PlayerTypeEnum.BjAudio.getValue()){
                tmpObj.videoType= 5;
                tmpObj.videoId=localLesson.getRoomId();
                tmpObj.token=localLesson.getVideoToken();
            }
            else if(localLesson.getPlayerType()== PlayerTypeEnum.Gensee.getValue()){
                tmpObj.videoType= 6;
                tmpObj.bjyRoomId=localLesson.getDomain();
                tmpObj.bjySessionId=localLesson.getLiveID();
                tmpObj.joinCode=localLesson.getDownloadID();
            }

            tmpObj.coursewareId=StringUtils.parseInt(localLesson.getSubjectID());
            if(!hasFind){
                if(tmpObj.coursewareId==mCourseWareId){
                    hasFind=true;
                }else {
                    mInitPlayIndex++;
                }
            }

            tmpObj.parentId="0";
            //tmpObj.hasChildren="0";
           // int total = (int) (localLesson.getSpace() / 1024 / 1024);

            tmpObj.fileSize=localLesson.getSpace()+"";// total+"M";
            tmpObj.targetPath=localLesson.getPlayPath();
            tmpObj.offSignalFilePath=localLesson.getSignalFilePath();
            /*/float progress=0l;
            if (downloadlession.getDuration() != 0) {
                progress =   (((float)downloadlession.getPlay_duration()) * 100 / downloadlession
                        .getDuration());
            }*/

            tmpObj.studyLength=localLesson.getPlay_duration()+"";
           // tmpObj.videoLength="视频 - "+ DateUtils.getTime(localLesson.getDuration())+"分钟";

            if(!TextUtils.isEmpty(localLesson.getReserve2())){
               tmpObj.teacher=""+localLesson.getReserve2();
            }else
                tmpObj.teacher="华图在线老师";
            tmpObj.hasTeacher="1".equals(localLesson.getReserve1())?1:0;
            tmpObj.isStudy=localLesson.getPlay_duration()>0?1:0;
            tmpObj.isFinish=localLesson.getPlay_duration()==localLesson.getDuration()?1:0;
            tmpObj.downStatus= DownBtnLayout.FINISH;

            String lengthDes="";
            if(localLesson.getPlayerType()== PlayerTypeEnum.BaiJia.getValue())
                lengthDes="视频 - ";
            else if(localLesson.getPlayerType()== PlayerTypeEnum.BjAudio.getValue())
                lengthDes="音频 - ";
            else{
                lengthDes="高清 - ";
            }

            if(tmpObj.isStudy==0){//0未开始学习
                //mLearnTimeTxt.setText("视频 - "+lessionBean.videoLength);
                tmpObj.videoLength=lengthDes+ DateUtils.getTime(localLesson.getDuration())+"分钟";
            }else if(tmpObj.isFinish==1){//1已学完
                //mLearnTimeTxt.setText("已学完"+lessionBean.videoLength);
                tmpObj.videoLength=lengthDes+"已学完  "+ DateUtils.getTime(localLesson.getDuration())+"分钟";
            }else {
                //mLearnTimeTxt.setText("视频 - 剩余"+lessionBean.videoLength);
                tmpObj.videoLength=lengthDes+"剩余"+ DateUtils.getTime(localLesson.getDuration()-localLesson.getPlay_duration())+"分钟";
            }
         /*   if(localLesson.getDuration() != 0){

                tmpObj.studyPercent=(((float)localLesson.getPlay_duration()) * 100 / localLesson
                        .getDuration())+"";
            }*/
            tmpObj.coursewareTimeLength=localLesson.getDuration()/1000;
            tmpObj.alreadyStudyTime=localLesson.getPlay_duration()/1000;
            tmpResponse.data.list.add(tmpObj);
        }

        onSuccess(tmpResponse);
    }

    @Override
    public void onSuccess(PurchaseCourseListResponse response){
        super.onSuccess(response);
        if(!Method.isActivityFinished(getActivity())){
             for(PurchasedCourseBean.Data item:response.getListResponse())
               ((OnCoursePlaylistener)getActivity()).getRecordList().add(item);

             if((getActivity() instanceof BJRecordPlayActivity)){
                 LogUtils.e("mInitPlayIndex",mInitPlayIndex+"");
                if(mInitPlayIndex>= ArrayUtils.size(mListAdapter.getAllItem())){
                    mInitPlayIndex=0;
                }
                 LogUtils.e("mInitPlayIndex2",mInitPlayIndex+"");
                 UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         if(!Method.isActivityFinished(getActivity())){
                             ((BJRecordPlayActivity)getActivity()).setLocationCourseWare(mListAdapter.getItem(mInitPlayIndex));
                         }

                     }
                 },500);

            }

        }
    }

    @Override
    public void onItemClick(int position,View view,int type) {
         switch (type) {
       /*     case EventConstant.EVENT_LIKE:
                WorkListItemInfo item = mListAdapter.getCurrentItem(position-1);
                doCollectAction(view, String.valueOf(item.id), position);
                break;*/

            case EventConstant.EVENT_ALL:

                if(mListAdapter.isSameSelectId(position)) return;
                PurchasedCourseBean.Data item = mListAdapter.getItem(position);
                if(null==item) return;
                if(getActivity() instanceof BJRecordPlayActivity){
                    int playTime= (int)((BJRecordPlayActivity)getActivity()).getDanmaCurrentTime();
                    mListAdapter.refreshLastPlayTime(playTime);
                }
                mListAdapter.setSelectIndex(position);
                 if(!Method.isActivityFinished(getActivity())){
                     ((OnCoursePlaylistener)getActivity()).onSelectPlayClick(item,true);
                   // ((BJRecordPlayActivity)getActivity()).onSelectPlayClick(item.targetPath,0,item.title,item.id,item.hasTeacher);
                }
              break;
        }
    }




}

