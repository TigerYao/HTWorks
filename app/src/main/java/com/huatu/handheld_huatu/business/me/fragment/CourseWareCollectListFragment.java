package com.huatu.handheld_huatu.business.me.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.CourseWareCollectlistAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.OnSwitchListener;
import com.huatu.handheld_huatu.helper.db.CollectInfoDao;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.helper.retrofit.RetrofitCallback;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.CollectCourseWareListResponse;
import com.huatu.handheld_huatu.mvpmodel.CourseWareCollectBean;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.library.PullToRefreshBase;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\8\6 0006.
 */

public class CourseWareCollectListFragment extends ABaseListFragment<CollectCourseWareListResponse> implements OnRecItemClickListener, OnSwitchListener,AStripTabsFragment.IStripTabInitData {
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView mWorksListView;

    @BindView(R.id.deleteAll_btn)
    TextView mDelBtn;

    @BindView(R.id.selAll_btn)
    TextView mSelAllBtn;

    @BindView(R.id.bottom_action_layout)
    LinearLayout mAction_layout;

    CustomConfirmDialog mConfirmDialog;

    int mSelectNum = 0;
    public int getContentView() {
        return R.layout.course_mycollect_list_layout;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView.getRefreshableView();
    }

    CourseWareCollectlistAdapter mListAdapter;

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListResponse = new CollectCourseWareListResponse();
        mListResponse.mLessionlist = new ArrayList<>();
        mListAdapter = new CourseWareCollectlistAdapter(getContext(), mListResponse.mLessionlist);
        mListAdapter.setOnViewItemClickListener(this);
    }

    @Override
    public int isEditMode() {
        if (mListAdapter.getItemCount() <= 0) return 2;
        return mListAdapter.isEditMode() ? 1 : 0;
    }

    @Override
    public void switchMode() {

        if(mListAdapter.isEditMode()) {//当前删除模式->normal
            mSelectNum=0;
            mSelAllBtn.setText("全选");
            mDelBtn.setText("删除");
           // mRightMenu.setText(R.string.edit);
            for (CourseWareCollectBean curLession : mListAdapter.getAllItems()) {
                curLession.setSelect(false);
            }
            mListAdapter.setActionMode(false);
            mAction_layout.setVisibility(View.GONE);

            RelativeLayout.LayoutParams tmpParam=   (RelativeLayout.LayoutParams)mWorksListView.getLayoutParams();
            tmpParam.bottomMargin=0;
            mWorksListView.setLayoutParams(tmpParam);
            if(mOnSwipeItemTouchListener!=null)
                mOnSwipeItemTouchListener.setCanSwipe(true);
        }
        else {//当前normal->删除模式
          //  mRightMenu.setText(R.string.pickerview_cancel);
            mAction_layout.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams tmpParam=   (RelativeLayout.LayoutParams)mWorksListView.getLayoutParams();
            tmpParam.bottomMargin=DensityUtils.dp2px(getContext(),45);
            mWorksListView.setLayoutParams(tmpParam);
            mListAdapter.setActionMode(true);

            if(mOnSwipeItemTouchListener!=null)
                mOnSwipeItemTouchListener.setCanSwipe(false);
        }
    }

    @Override
    public void onStripTabRequestData(){
        //实时查询，去除不一致标记
       // PrefStore.putSettingInt("user_collection_count_change",0);
        onFirstLoad();
    }

/*    @Override
    public void requestData() {
        super.requestData();
        //实时查询，去除不一致标记
        PrefStore.putSettingInt("user_collection_count_change",0);
        onFirstLoad();
    }*/

    private void superOnRefresh(){
        super.onRefresh();
    }

    SwipeItemLayout.OnSwipeItemTouchListener mOnSwipeItemTouchListener;

    @Override
    protected void onPrepare() {
        super.onPrepare();
     /*   getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_my_empty);
        getEmptyLayout().setTipText(R.string.xs_none_related_organize);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);*/

        //this.findViewById(R.id.right_btn).setBackground(ResourceUtils.getDrawable(R.drawable.collect_confirm_bg));

        getEmptyLayout().setEmptyImg(R.mipmap.course_no_data_icon);//course_no_cache_icon
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text,R.string.xs_none_collectcourse);
        getEmptyLayout().setTipText(R.string.xs_choose_collectcourse);

        getEmptyLayout().findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainTabActivity.class);
                intent.putExtra("require_index",1);
                startActivity(intent);
            }
        });

        mOnSwipeItemTouchListener=new SwipeItemLayout.OnSwipeItemTouchListener(getContext());
        mWorksListView.getRefreshableView().addOnItemTouchListener(mOnSwipeItemTouchListener);

        mWorksListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        mWorksListView.getRefreshableView().showForcePageEnd();
        mWorksListView.getRefreshableView().setPagesize(getLimit());
        mWorksListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.getRefreshableView().setRecyclerAdapter(mListAdapter);
        mWorksListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f)));
    }

    @Override
    public void onError(String throwable, int type) {
        if (isFragmentFinished()) return;
        if (!isCurrentReMode()) {
            getListView().showNetWorkError();
        } else {
            if(mListAdapter.getItemCount()<=0){
                super.onError(throwable, type);
                // initNotify("网络加载出错~");
            }
            else {
                hideEmptyLayout();
                onRefreshCompleted();
                ToastUtils.showShortToast(UniApplicationContext.getContext(),"网络加载出错~");
            }
        }
    }

    @Override
    public void showEmpty() {
        if (isCurrentReMode()) {
            mListAdapter.clearAndRefresh();
            getListView().resetAll();
            //getListView().hideloading();
            showEmptyLayout();
        }else {
            getListView().checkloadMore(0);
            getListView().hideloading();
        }
    }
    @Override
    protected void onRefreshCompleted(){
        if(null!=mWorksListView) mWorksListView.onRefreshComplete();
    }

    @Override
    protected void setListener() {
        mWorksListView.getRefreshableView().setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getCourseWareCollectList(offset,limit).enqueue(getCallback());
    }


    /*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(PrefStore.getSettingInt("user_collection_count_change",0)==1){
                PrefStore.putSettingInt("user_collection_count_change",0);
                if(null!=mWorksListView){
                    mWorksListView.getRefreshableView().scrollToPosition(0);
                    mWorksListView.setRefreshing(true);//会触发onRefresh事件
                }
            }
        }
    }*/

    @Override
    public void onItemClick(int position, View view, int type) {

        CourseWareCollectBean curLession=mListAdapter.getItem(position);
        if(curLession==null) return;
        if(mListAdapter.isEditMode()){

            boolean oldSelect=curLession.isSelect();
            curLession.setSelect(!oldSelect);
            if(view instanceof CheckBox){
                ((CheckBox)view).setChecked(!oldSelect);
            }
            mSelectNum=mSelectNum+(oldSelect? -1:1);
            setDownloadBtnState();
            return;
        }
        switch (type){
            case EventConstant.EVENT_ALL:
                clickItemToPlay(curLession);
                break;
            case EventConstant.EVENT_DELETE:

                final  CourseWareCollectBean delLession=curLession;
                final  int               delPostion=position;

                DialogUtils.createSysDialog(getActivity(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<CourseWareCollectBean> tmpList=new ArrayList<>();
                                tmpList.add(delLession);
                                cancelCollection(tmpList,delPostion);
                            }
                        }, null,
                        "真的要取消收藏吗？", "取消", " 确定");
                //showDeleteDlg(mDownCourseAdapter.getCurrentItem(position),position);
                break;
        }
    }

    private void clickItemToPlay(final CourseWareCollectBean curLession){
        if (curLession.isExpired){
            DialogUtils.createSysDialog(getActivity(), null, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    startVideoPage(curLession, false);
                    MainTabActivity.newIntent(getActivity(), 1);
                }
            }, null, "该课程已过期，是否去看看其他课程？", "留在此页", "去看看");
        } else if (curLession.videoType == 2 && curLession.liveStatus == 0){
            ToastUtils.showShort("直播未开始");
        }else if (curLession.videoType == 2 && curLession.liveStatus == 2 && curLession.isPlayback != 1){
            DialogUtils.onShowOnlyConfirmRedDialog(getActivity(), R.layout.video_finished_tip_dialog, "本节直播课已结束，下次早点来哦",0);
        }else if (curLession.videoType == 2 && curLession.liveStatus == 2 && curLession.isPlayback == 1){
            DialogUtils.onShowOnlyConfirmRedDialog(getActivity(), R.layout.video_finished_tip_dialog, "本节直播课已结束，图图正在努力上传回放中",R.mipmap.system_tip_happy_icon);
        }else{
            startVideoPage(curLession);
        }
    }

    private void startVideoPage(CourseWareCollectBean  lession) {
        CourseWareInfo cuLession = GsonUtil.GsonToBean(GsonUtil.GsonString(lession), CourseWareInfo.class);;
        if (cuLession == null)
            return;
        if (lession.videoType == 1||cuLession.videoType==5) {
            BJRecordPlayActivity.lanuchForOnlive(getContext(), lession.netClassId + "", lession.classTitle, lession.classCover, cuLession, true, lession.alreadyStudyTime);
        } else
            LiveVideoForLiveActivity.start(getContext(), lession.netClassId + "", cuLession, null);
    }

    @OnClick({R.id.selAll_btn,R.id.deleteAll_btn})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.selAll_btn:
                if(mSelectNum==mListAdapter.getItemCount()){
                    mSelectNum=0;
                    for (CourseWareCollectBean curLession : mListAdapter.getAllItems()) {
                        curLession.setSelect(false);
                    }
                    setDownloadBtnState();
                    mListAdapter.notifyDataSetChanged();
                }else {
                    mSelectNum=mListAdapter.getItemCount();
                    for (CourseWareCollectBean curLession : mListAdapter.getAllItems()) {
                        curLession.setSelect(true);
                    }
                    setDownloadBtnState();
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.deleteAll_btn:
                if(mSelectNum==0){
                    ToastUtils.showShort("请先选择课件");
                    return;
                }
                 DialogUtils.createSysDialog(getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<CourseWareCollectBean> downIDs = new ArrayList<>();
                                    for (CourseWareCollectBean curLession:mListAdapter.getAllItems()) {
                                        if (curLession.isSelect()) {
                                            downIDs.add(curLession);
                                        }
                                    }
                                    cancelCollection(downIDs,-1);
                                }
                            }, null,
                            "真的要取消收藏吗？", "取消", " 确定");
                break;
            default:
                break;
        }
    }


    List<CourseWareCollectBean> mDelCourseList;
    private int mDelPostion=-1;
    private void cancelCollection(List<CourseWareCollectBean> courseIds,int postion){
        mDelCourseList=courseIds;
        if(ArrayUtils.isEmpty(courseIds)){
            ToastUtils.showShort("请先选择课件");
            return;
        }
        mDelPostion=postion;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < courseIds.size(); i++) {
            if (i == courseIds.size() - 1) {
                sb.append(courseIds.get(i).id);
            } else {
                sb.append(courseIds.get(i).id + ",");
            }
        }

        ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().unCollectCourseWare(sb.toString()), new NetObjResponse<String>() {
            @Override
            public void onSuccess(BaseResponseModel<String> model) {
                if(mDelPostion==-1){

                    List<String> StrArrs= ArrayUtils.arrayStringList(mDelCourseList, new ArrayUtils.GroupBy<String>() {
                        @Override
                        public String groupby(Object obj) {
                            CourseWareCollectBean d = (CourseWareCollectBean) obj;
                            return d.id; // 分组依据为课程ID
                        }
                    });
                    mListAdapter.getAllItems().removeAll(mDelCourseList) ;
                    for(String str:StrArrs){
                        CollectInfoDao.getInstance().delete(str);
                    }
                    if(null!=mDelBtn){
                        mSelectNum=0;
                        mDelBtn.setText("删除");
                    }
                    mListAdapter.notifyDataSetChanged();
                }
                else {
                    mListAdapter.removeAt(mDelPostion,mDelCourseList.get(0));
                    CollectInfoDao.getInstance().delete(mDelCourseList.get(0).id);
                }
                if (mListAdapter.getItemCount() <= 0) {
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    CourseWareCollectListFragment.this.finish();
                }
            }

            @Override
            public void onError(String message, int type) {
                ToastUtils.showShort("取消收藏失败");
            }
        });
    }

    private void setDownloadBtnState() {
        if(mSelectNum>0){
           /* mDelTipTxt.setVisibility(View.VISIBLE);
            mDelTipTxt.setText(String.valueOf(mSelectNum));
            AnimUtils.scaleView(mDelTipTxt);*/
            mDelBtn.setText(String.format("确认删除 (%d)",mSelectNum));
        }else{
           // mDelTipTxt.setVisibility(View.GONE);
            mDelBtn.setText("删除");
        }
        if (mListAdapter.getItemCount() == (mSelectNum)) {
            mSelAllBtn.setText("取消全选");
        }else {
            mSelAllBtn.setText("全选");
        }
    }
}
