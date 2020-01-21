package com.huatu.handheld_huatu.business.me.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.PagePullToRefreshStatusBlock;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.business.other.DetailScrollViewFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.OnSwitchListener;
import com.huatu.handheld_huatu.helper.db.ArticleInfoDao;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.CreamArticleFollowBean;
import com.huatu.handheld_huatu.mvpmodel.WarpListResponse;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.netease.hearttouch.router.HTPageRouterCall;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2019\12\2 0002.
 */

public class ArticleCollectListFragment  extends AbsSettingFragment  implements OnRecItemClickListener, OnSwitchListener,AStripTabsFragment.IStripTabInitData {

    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView mWorksListView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mLoadingView;

    int mSelectNum = 0;

    @BindView(R.id.deleteAll_btn)
    TextView mDelBtn;

    @BindView(R.id.selAll_btn)
    TextView mSelAllBtn;

    @BindView(R.id.bottom_action_layout)
    LinearLayout mAction_layout;

    @Override
    public int getContentView() {
        return R.layout.course_mycollect_list_layout;
    }
    CreamArticleListAdapter mListAdapter;

    SwipeItemLayout.OnSwipeItemTouchListener mOnSwipeItemTouchListener;
    PagePullToRefreshStatusBlock<WarpListResponse<CreamArticleFollowBean>> mPageRefreshblock;


    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        if(null==mCompositeSubscription){
            mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        }
        return mCompositeSubscription;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
    }

    @Override
    protected void setListener() {

        mLoadingView.setEmptyImg(R.mipmap.course_no_data_icon);//course_no_cache_icon
        mLoadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_none_collectcourse);
        mLoadingView.setTipText(R.string.xs_choose_collectarticle);

        mLoadingView.findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HTPageRouterCall.newBuilderV2("ztk://exam/articles")
                        .context(getActivity()).sourceIntent(new Intent().setData(Uri.parse("ztk://exam/articles")))
                        .build()
                        .start();
            }
        });
        mWorksListView.getRefreshableView().showForcePageEnd();
        mWorksListView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mPageRefreshblock=new PagePullToRefreshStatusBlock<>();

        mOnSwipeItemTouchListener=new SwipeItemLayout.OnSwipeItemTouchListener(getContext());
        mWorksListView.getRefreshableView().addOnItemTouchListener(mOnSwipeItemTouchListener);

        mPageRefreshblock.setPullRefreshView(mWorksListView);
        mPageRefreshblock.setloadingView(mLoadingView);
        mPageRefreshblock.setOnRefreshFinishListener(new PagePullToRefreshStatusBlock.OnRefreshFinishListener() {
            @Override
            public void onRefreshCompleted() {
                mWorksListView.onRefreshComplete();
            }
        });
        WarpListResponse<CreamArticleFollowBean> response= new WarpListResponse<CreamArticleFollowBean>();
        response.mAdapterList = new ArrayList<>();
        mPageRefreshblock.mListResponse=response;

        mPageRefreshblock.setTypeFactory(new PagePullToRefreshStatusBlock.TypeFactory<WarpListResponse<CreamArticleFollowBean>>(){

            @Override
            public Observable<WarpListResponse<CreamArticleFollowBean>> getListObservable(int pageIndex, int limit){
                return CourseApiService.getApi().getMyArticlecollectList(pageIndex,limit);
            }

            @Override
            public RecyclerView.Adapter getAdapter(WarpListResponse<CreamArticleFollowBean> response){
                mListAdapter = new CreamArticleListAdapter(getContext(), response.mAdapterList);
                mListAdapter.setOnViewItemClickListener(ArticleCollectListFragment.this);
                return mListAdapter;
            }

            @Override
            public CompositeSubscription getComSubscription(){
                return getSubscription();
            }
        });
        mPageRefreshblock.build();

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
            for (CreamArticleFollowBean curLession : mListAdapter.getAllItems()) {
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
            tmpParam.bottomMargin= DensityUtils.dp2px(getContext(),45);
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
        mPageRefreshblock.onFirstLoad();;
    }

    @Override
    public void onItemClick(int position, View view, int type) {

        CreamArticleFollowBean curLession=mListAdapter.getItem(position);
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
               // clickItemToPlay(curLession);
                DetailScrollViewFragment.lanuch(getContext(),curLession.articleId);
                break;
            case EventConstant.EVENT_DELETE:

                final  CreamArticleFollowBean delLession=curLession;
                final  int               delPostion=position;

                DialogUtils.createSysDialog(getActivity(), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<CreamArticleFollowBean> tmpList=new ArrayList<>();
                                tmpList.add(delLession);
                                cancelCollection(tmpList,delPostion);
                            }
                        }, null,
                        "真的要取消收藏吗？", "取消", " 确定");
                //showDeleteDlg(mDownCourseAdapter.getCurrentItem(position),position);
                break;
        }
    }

    @OnClick({R.id.selAll_btn,R.id.deleteAll_btn})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.selAll_btn:
                if(mSelectNum==mListAdapter.getItemCount()){
                    mSelectNum=0;
                    for (CreamArticleFollowBean curLession : mListAdapter.getAllItems()) {
                        curLession.setSelect(false);
                    }
                    setDownloadBtnState();
                    mListAdapter.notifyDataSetChanged();
                }else {
                    mSelectNum=mListAdapter.getItemCount();
                    for (CreamArticleFollowBean curLession : mListAdapter.getAllItems()) {
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
                                List<CreamArticleFollowBean> downIDs = new ArrayList<>();
                                for (CreamArticleFollowBean curLession:mListAdapter.getAllItems()) {
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


    List<CreamArticleFollowBean> mDelCourseList;
    private int mDelPostion=-1;
    private void cancelCollection(List<CreamArticleFollowBean> courseIds,int postion){
        mDelCourseList=courseIds;
        if(ArrayUtils.isEmpty(courseIds)){
            ToastUtils.showShort("请先选择课件");
            return;
        }
        mDelPostion=postion;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < courseIds.size(); i++) {
            if (i == courseIds.size() - 1) {
                sb.append(courseIds.get(i).articleId);
            } else {
                sb.append(courseIds.get(i).articleId + ",");
            }
        }

        ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().unCollectArticles(sb.toString()), new NetObjResponse<String>() {
            @Override
            public void onSuccess(BaseResponseModel<String> model) {
                if(mDelPostion==-1){

                    List<String> StrArrs= ArrayUtils.arrayStringList(mDelCourseList, new ArrayUtils.GroupBy<String>() {
                        @Override
                        public String groupby(Object obj) {
                            CreamArticleFollowBean d = (CreamArticleFollowBean) obj;
                            return d.articleId+""; // 分组依据为课程ID
                        }
                    });
                    mListAdapter.getAllItems().removeAll(mDelCourseList) ;
                   /* for(String str:StrArrs){
                        ArticleInfoDaoHelper.getInstance().delete(str);
                    }*/
                    if(null!=mDelBtn){
                        mSelectNum=0;
                        mDelBtn.setText("删除");
                    }
                    mListAdapter.notifyDataSetChanged();
                }
                else {
                    mListAdapter.removeAt(mDelPostion,mDelCourseList.get(0));
                   // ArticleInfoDaoHelper.getInstance().delete(mDelCourseList.get(0).id+"");
                }
                if (mListAdapter.getItemCount() <= 0) {
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    ArticleCollectListFragment.this.finish();
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


    public class CreamArticleListAdapter extends SimpleBaseRecyclerAdapter<CreamArticleFollowBean> {

        private int mActionMode=0;//正常0，编辑1
        private final int NormalMode=0;
        private final int EditMode=1;

        public void setActionMode(boolean isEdit){
            mActionMode=isEdit? EditMode:NormalMode;
            this.notifyDataSetChanged();
        }

        public boolean isEditMode(){
            return mActionMode==EditMode;
        }

        public CreamArticleListAdapter(Context context,List<CreamArticleFollowBean> listData){
            super(context,listData);
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.article_collect_list_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            ViewHolder holderfour = (ViewHolder) holder;
            holderfour.bindUI(mItems.get(position));
        }

        public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
            @BindView(R.id.techer_img)
            ImageView iv_img;

            @BindView(R.id.tv_item_course_mine_title)
            TextView tv_article_title;

            @BindView(R.id.tv_type)
            TextView mType;

            @BindView(R.id.chk_btn)
            CheckBox mCheckbox;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.findViewById(R.id.whole_content).setOnClickListener(this);
                itemView.findViewById(R.id.delete).setOnClickListener(this);
                mCheckbox.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.chk_btn:
                    case R.id.whole_content:
                        if (onRecyclerViewItemClickListener != null)
                            onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),isEditMode()?mCheckbox:v, EventConstant.EVENT_ALL);
                        break;
                    case R.id.delete:
                        if (onRecyclerViewItemClickListener != null)
                            onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_DELETE);
                        break;
                }
            }

            public void bindUI(CreamArticleFollowBean mResult){

                if (mResult!=null){
                    mCheckbox.setVisibility(mActionMode==EditMode?View.VISIBLE:View.GONE);
                    mCheckbox.setChecked(mResult.isSelect());

                    if(!TextUtils.isEmpty(mResult.img)){
                        iv_img.setVisibility(View.VISIBLE);
                        ImageLoad.load(iv_img.getContext(),mResult.img,iv_img, DiskCacheStrategy.DATA);
                    }else
                        iv_img.setVisibility(View.GONE);

                    tv_article_title.setText(mResult.title);
                    mType.setText(mResult.typeName);

                }
            }
        }
    }


}