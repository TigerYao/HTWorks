package com.huatu.handheld_huatu.business.ztk_vod;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.course.CatalogClassesAdapter;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.SyllabusClassesBean;
import com.huatu.handheld_huatu.mvpmodel.TeacherBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.BlurPopupWindow;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewStateUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2018\11\5 0005.
 */

public class CatalogFilterPopup extends BlurPopupWindow implements View.OnClickListener,OnRecItemClickListener {

    RecyclerViewEx mRecycleView;
    CatalogFilterHead mFilterHeadView;

    CommloadingView mCommloadingView;

    public interface OnCatalogFilterChange{
         void onCatalogSelect(String teacherIds,String teacherNames,String classNodeId,int filtType);
    }

    private OnCatalogFilterChange mOnCatFilterChangeListener;
    public void setOnCatalogFilterListener(OnCatalogFilterChange  onCatFilterChangeListener){
        mOnCatFilterChangeListener=onCatFilterChangeListener;
    }

    CatalogClassesAdapter mListAdapter;
    int filterWidth=0;
    int mPageIndex=1;
    private String mCourseId;

    private CompositeSubscription mCompositeSubscription = null;
    public CatalogFilterPopup(@NonNull Context context,String courseId,CompositeSubscription subscription) {
        super(context);
        this.mCourseId=courseId;
        this.mCompositeSubscription=subscription;

    }

    private List<SyllabusClassesBean> mAllCatalogList=new ArrayList<>();

    @Override
    protected View createContentView(ViewGroup parent) {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.play_catalogfilter_layout, parent, false);
        filterWidth= DisplayUtil.getScreenWidth()- DensityUtils.dp2px(UniApplicationContext.getContext(),35);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(filterWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.RIGHT;
        view.setLayoutParams(lp);
        view.setVisibility(INVISIBLE);

        mRecycleView=view.findViewById(R.id.xi_comm_page_list);
        mCommloadingView=view.findViewById(R.id.xi_layout_loading);


        mCommloadingView.setEmptyImg(R.drawable.no_data_bg);//course_no_cache_icon
        mCommloadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_error_message);
        mCommloadingView.setTipText(R.string.xs_retry_message);

        mCommloadingView.findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageIndex=1;
                v.setVisibility(GONE);
                loadData(mPageIndex);
            }
        });

   /*     mCommloadingView.setOnRtyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPageIndex=1;
                loadData(mPageIndex);
            }
        });*/
        view.findViewById(R.id.reset_btn).setOnClickListener(this);
        view.findViewById(R.id.confirm_btn).setOnClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
        mRecycleView.setLayoutManager(manager);
        mRecycleView.setPagesize(100000);

        mListAdapter = new CatalogClassesAdapter(getBaseContext(), new ArrayList<SyllabusClassesBean>());
       // mListAdapter.setOnViewItemClickListener(this);
        mRecycleView.setRecyclerAdapter(mListAdapter);
        mRecycleView.setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                if(!isRetry) mPageIndex++;
                loadData(mPageIndex);
            }
        });
        mListAdapter.setOnViewItemClickListener(this);

        mFilterHeadView = new CatalogFilterHead(){
            @Override
            public  void onExplandClick(boolean isExpland){
                if(isExpland){
                   mListAdapter.addAll(mAllCatalogList);
                }
                else {
                    mAllCatalogList.clear();
                    mAllCatalogList.addAll(mListAdapter.getAllItem());
                    mListAdapter.removeAllAt(0,mAllCatalogList);
                }
            }
        };
        mFilterHeadView.onCreate(getBaseContext(),mRecycleView);
//        rushSaleHeader.setOnHeaderLoadListener(this);

        RecyclerViewStateUtils.setHeaderView(mRecycleView, mFilterHeadView.getContentView());
        return view;
    }

    @Override
    protected void onShow(final boolean reShow) {
        super.onShow(reShow);
        getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);

            /*    getContentView().setVisibility(VISIBLE);
              //  int height = getContentView().getMeasuredHeight();
                ObjectAnimator.ofFloat(getContentView(), "translationX", filterWidth, 0).setDuration(getAnimationDuration()).start();
*/
                getContentView().setVisibility(VISIBLE);
                int height = getContentView().getMeasuredWidth();
                ObjectAnimator.ofFloat(getContentView(), "translationX", height, 0).setDuration(getAnimationDuration()).start();

                if(reShow)
                    ObjectAnimator.ofFloat(mBlurView, "alpha", mBlurView.getAlpha(), 1).setDuration(getAnimationDuration()).start();
                else {
                   // mFilterHeadView.bindUI();
                    mPageIndex=1;
                    loadData(mPageIndex);
                }
           }
        });
   }

   private void loadData(final int pageIndex){
       if(null!=mCommloadingView) {
           mCommloadingView.showLoadingStatus();
       }
        ServiceExProvider.visitList(mCompositeSubscription, CourseApiService.getApi().getSyllabusTeachers(StringUtils.parseInt(mCourseId),"",""),
           new NetListResponse<TeacherBean>() {
            @Override
            public void onSuccess(BaseListResponseModel<TeacherBean> model) {
                if(null!=mFilterHeadView)
                    mFilterHeadView.bindUI(model.data==null? new ArrayList<TeacherBean>():model.data);
            }

            @Override
            public void onError(String message, int type) {  }
        });
       ServiceExProvider.visitList(mCompositeSubscription, CourseApiService.getApi().getAllSyllabusClasse(StringUtils.parseInt(mCourseId)),
               new NetListResponse<SyllabusClassesBean>() {
                   @Override
                   public void onSuccess(BaseListResponseModel<SyllabusClassesBean> model) {
                       if(null!=mCommloadingView) {
                           mCommloadingView.removeFromParent();
                           mCommloadingView=null;
                       }
                       if(pageIndex==1&&(null!=mFilterHeadView)){
                           mFilterHeadView.setCatalogListSuccess(ArrayUtils.isEmpty(model.data));
                       }
                       mListAdapter.addAll(model.data);
                     /*  if(pageIndex>1){
                           mRecycleView.checkloadMore(model.data.next==1);
                           mRecycleView.hideloading();checkLessionEvalute
                       }*/
                   }

                   @Override
                   public void onError(String message, int type) {
                       if(null!=mCommloadingView) {
                            mCommloadingView.showEmptyStatus();
                       }
                       if(pageIndex>1)
                         mRecycleView.showNetWorkError();
                   }
               });

   }


    @Override
    protected ObjectAnimator createDismissAnimator() {
        int height = getContentView().getMeasuredWidth();
        return ObjectAnimator.ofFloat(getContentView(), "translationX", 0, height).setDuration(getAnimationDuration());
    }

    @Override
    protected ObjectAnimator createShowAnimator() {
        return null;
    }



    @Override
    public void onItemClick(int position,View view,int type){

    }

    @Override
    public void onClick(View v){
       switch (v.getId()){
           case R.id.reset_btn:
               if(null!=mFilterHeadView) mFilterHeadView.resetUI();
               if(null!=mOnCatFilterChangeListener){
                   mListAdapter.clearSelectedIds();
                   mOnCatFilterChangeListener.onCatalogSelect("","","",0);
                   dismiss();
               }
               break;
           case R.id.confirm_btn:
               if(null!=mOnCatFilterChangeListener){
                    String[] teacherIds=new String[]{"",""};

                    int filterType=0;
                    if(null!=mFilterHeadView){
                        teacherIds=mFilterHeadView.getSelectedTeachers();
                        filterType=mFilterHeadView.getFilterType();
                    }
                   mOnCatFilterChangeListener.onCatalogSelect(teacherIds[0],teacherIds[1],mListAdapter.getSelectedNodeIds(),filterType);
                   dismiss();
               }
               break;
        }
    }

    public static class Builder extends BlurPopupWindow.Builder<CatalogFilterPopup> {

        private CompositeSubscription mCompositeSubscription = null;
        private String mCourseId;
        public Builder(Context context) {
            super(context);
            this.setScaleRatio(0.25f).setBlurRadius(8).setTintColor(0x30000000);
        }

        public Builder setSubscription(CompositeSubscription subscription,String CourseId){
           this.mCompositeSubscription=subscription;
           this.mCourseId=CourseId;
           return this;
        }

        @Override
        protected CatalogFilterPopup createPopupWindow() {
            return new CatalogFilterPopup(mContext,mCourseId,mCompositeSubscription);
        }


    }
}