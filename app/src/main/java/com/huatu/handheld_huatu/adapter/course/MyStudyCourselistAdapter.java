package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseBean;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.MultTextView;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.widget.IncreaseProgressBar;

import java.util.List;

/**
 * Created by cjx on 2018\6\29 0029.
 * https://www.jianshu.com/p/654dac931667 notifyItem闪屏
 */

public class MyStudyCourselistAdapter extends SimpleBaseRecyclerAdapter<BuyCourseBean.Study> {


    private Context mContext;
    private List<BuyCourseBean.Study> mLessons;

    public int mOnTopCount=0;

    private Drawable mlayerDrawabel;

    private boolean mIsFromFilter=false;
    private boolean mSortByJoin=false;


    public boolean isFromFilter(){
        return mIsFromFilter;
    }
    public void setFromFilter(boolean isFilter){
        mIsFromFilter=isFilter;
    }

    private OnRecItemClickListener onRecyclerViewItemClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }


    public MyStudyCourselistAdapter(Context context, List<BuyCourseBean.Study> lessons) {
        super(context,lessons);
        this.mContext = context;
        this.mLessons = lessons;

    }

    public void clear() {
        if (mLessons != null) mLessons.clear();
    }

    public BuyCourseBean.Data getCurrentItem(int position) {
        if (position >= 0 && position < ArrayUtils.size(mLessons))
            return mLessons.get(position);
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).holdType;  //0  课程1 课程分组头2 空数据
    }

    @Override
    public int getItemCount() {
        return ArrayUtils.size(mLessons);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==1){
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_study_headtype_item, parent, false);
            return new ViewHolderHead(collectionView);
        }
        else if(viewType==2){
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_empty_layout, parent, false);
            return new EmptyViewHolder(collectionView);
        }
        else {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_study_list_item, parent, false);
            return new ViewHolderTwo(collectionView);
        }
    }

    public void refreshOrderType(int status){
        BuyCourseBean.Study curItem=this.getItem(0);
        if(null!=curItem&&curItem.holdType==1){
            curItem.classType=status;

            this.mSortByJoin=status==0?false:true;
            this.notifyItemChanged(0);
        }
     }

 /*   @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position,@NonNull List<Object> payloads){
        //super.onBindViewHolder(holder,position,payloads);
        if(ArrayUtils.isEmpty(payloads))
            onBindViewHolder(holder,position);
        else {

            LogUtils.e("onBindViewHolder",position+""+ GsonUtil.toJsonStr(payloads));
            if(holder instanceof ViewHolderHead){
                ((ViewHolderHead)holder).switchOrderType();
            }
        }
    }*/

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int viewType=getItemViewType(position);
        if(viewType==0) {
            ViewHolderTwo holderfour = (ViewHolderTwo) holder;
            holderfour.bindUI(mLessons.get(position),position);
        }else if(viewType==1){
            ViewHolderHead holderfour = (ViewHolderHead) holder;
            holderfour.bindOrderType(mLessons.get(position).classType);
        }
        else if(viewType==2){
            EmptyViewHolder holderfour = (EmptyViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }
    }


    protected class EmptyViewHolder extends RecyclerView.ViewHolder  {

        CommloadingView mLoadingView;
        EmptyViewHolder(View itemView) {
            super(itemView);
            mLoadingView=(CommloadingView)itemView;
            mLoadingView.setEmptyImg(R.mipmap.course_no_data2_icon);//course_no_cache_icon
            mLoadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_none_studycourse);
            mLoadingView.setTipText(R.string.xs_choose_studycourse);
            mLoadingView.findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainTabActivity.class);
                    intent.putExtra("require_index",1);
                    mContext.startActivity(intent);
                 }
            });
        }


        public void bindUI(BuyCourseBean.Study mineItem, int postion) {

            if(!mIsFromFilter){
                mLoadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_none_studycourse);
                mLoadingView.setTipText(R.string.xs_choose_studycourse);
               // mLoadingView.findViewById(R.id.xi_tv_tips).setVisibility(View.VISIBLE);
                mLoadingView.showEmptyStatus();
             }else {
                mLoadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_filter_studycourse);
                mLoadingView.setTipText(R.string.xs_my_empty);
                mLoadingView.showEmptyStatus();
                mLoadingView.findViewById(R.id.xi_tv_tips).setVisibility(View.INVISIBLE);
             }

        }
    }

    protected class ViewHolderHead extends RecyclerView.ViewHolder implements View.OnClickListener {

        int orderType=0;
        TextView mOrderTypeTxt;
        ViewHolderHead(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.recycle_course_btn).setOnClickListener(this);
            itemView.findViewById(R.id.filter_course_btn).setOnClickListener(this);

            mOrderTypeTxt=itemView.findViewById(R.id.study_order_view);
            mOrderTypeTxt.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.recycle_course_btn:
                  //  UIJumpHelper.jumpFragment(mContext, MyRecylerCourseFragment.class);
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v,EventConstant.EVENT_CONCERN);
                    break;
                case R.id.study_order_view:
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v,EventConstant.EVENT_SET_DEFAULT);
                    break;
                case R.id.filter_course_btn:
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v,EventConstant.EVENT_CHANGE);
                    break;
            }
        }

        public void bindOrderType(int type){
            orderType=type;
            mOrderTypeTxt.setText(type==0 ?"最近学习":"最近加入");
        }

        public void switchOrderType() {
            if(orderType==0){
                mOrderTypeTxt.setText("最近加入");
                orderType=1;
            }else {
                mOrderTypeTxt.setText("最近学习");
                orderType=0;
            }
        }
    }

    private Drawable getExpiredViewbg(){
        if(null==mlayerDrawabel){
            mlayerDrawabel= ResourceUtils.getDrawable(R.drawable.layer_outdate_bg);
        }
        return mlayerDrawabel;
    }

    protected class ViewHolderTwo   extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTitle;

       // @BindView(R.id.iv_item_course_mine_gq)
       // ImageView mOutDateImg;
        TextView mTimeLength;

       // @BindView(R.id.iv)
       // ImageView mIsliveImg;

      //  ImageView mCoverImg;

      //  @BindView(R.id.tv_item_course_mine_one_to_one)
      //  TextView mInfoCard;

        IncreaseProgressBar mProgressBar;
        TextView mLearnTxt;
        Button mSetTopBtn;
       // ImageView mOnliveTipImg;
      //  LinearLayout mOnLiveLayout;
        TextView mLiveStatusTxt;
        MultTextView mTeacherNameTxt;

        View mSplitView;
        TextView mOneByoneView;
        View mWholeContentView;

        Button mStickBtn;

        ImageView[] mTeacherAvaters;

        ViewHolderTwo(View itemView) {
            super(itemView);

            mTeacherNameTxt=itemView.findViewById(R.id.techer_des_txt);
            mTitle=(TextView) itemView.findViewById(R.id.tv_item_course_mine_title);
            mTimeLength=(TextView)itemView.findViewById(R.id.tv_item_course_mine_timelength);
          //  mCoverImg=(ImageView)itemView.findViewById(R.id.iv_item_course_mine_scaleimg);
            mProgressBar=(IncreaseProgressBar)itemView.findViewById(R.id.rush_sale_progress);
            mLearnTxt=(TextView)itemView.findViewById(R.id.learn_tip_txt);
            mSetTopBtn=(Button)itemView.findViewById(R.id.stick);
            mLiveStatusTxt=(TextView)itemView.findViewById(R.id.onlive_status_txt) ;

            mSplitView=itemView.findViewById(R.id.one_split_view) ;
            mOneByoneView=(TextView)itemView.findViewById(R.id.onebyone_view) ;
            mWholeContentView=itemView.findViewById(R.id.whole_content);

            mTeacherAvaters=new ImageView[3];
            mTeacherAvaters[0]=itemView.findViewById(R.id.teacher_first_view);
            mTeacherAvaters[1]=itemView.findViewById(R.id.teacher_second_view);
            mTeacherAvaters[2]=itemView.findViewById(R.id.teacher_third_view);;

            mStickBtn=itemView.findViewById(R.id.stick);

            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.stick).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this);
            mOneByoneView.setOnClickListener(this);
        }

        @Override
        public  void onClick(View v){
            switch (v.getId()){
                case R.id.stick:

                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v,EventConstant.EVENT_LIKE);
                    break;
                case R.id.whole_content:
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                    break;
                case R.id.delete:
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_DELETE);
                    break;
                case R.id.onebyone_view:
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_MORE);
                    break;
            }
        }

        public void bindUI(BuyCourseBean.Study mineItem,int postion){

            mStickBtn.setVisibility((mIsFromFilter||mSortByJoin)?View.GONE:View.VISIBLE);
             //显示数据
            if (mOnTopCount >= 10) {
                if ((postion-1) < mOnTopCount) {//这里添加了排序类型一行数据，所以减一
                    mSetTopBtn.setText("取消\n置顶");
                   // mSetTopBtn.setEnabled(true);
                    mSetTopBtn.setSelected(false);
                    if(mineItem.isExpired)
                        mWholeContentView.setBackground(getExpiredViewbg());
                    else
                        mWholeContentView.setBackgroundColor(Color.parseColor("#FFFDF6"));
                } else {
                    //mSetTopBtn.setEnabled(false);
                    mSetTopBtn.setSelected(true);
                    mSetTopBtn.setText("置顶");
                    if(mineItem.isExpired)
                        mWholeContentView.setBackground(getExpiredViewbg());
                    else
                        mWholeContentView.setBackgroundColor(Color.WHITE);
                }
            } else {
               // mSetTopBtn.setEnabled(true);
                mSetTopBtn.setSelected(false);
               // mSetTopBtn.setText(mineItem.getLetter().equals("置顶") ? "取消\n置顶" : "置顶");
              //  mWholeContentView.setBackgroundColor(mineItem.getLetter().equals("置顶") ? Color.parseColor("#FFFDF6") : Color.WHITE);

                mSetTopBtn.setText(mineItem.isTop ? "取消\n置顶" : "置顶");
                if(mineItem.isExpired){
                    mWholeContentView.setBackground(getExpiredViewbg());
                }
                else
                    mWholeContentView.setBackgroundColor(mineItem.isTop ? Color.parseColor("#FFFDF6") : Color.WHITE);
             }

             if(mineItem.living){
                 mLiveStatusTxt.setVisibility(View.VISIBLE);
                 mLiveStatusTxt.setText("正在\n直播");
             }else if(mineItem.todayLive==1){
                 mLiveStatusTxt.setVisibility(View.VISIBLE);
                 mLiveStatusTxt.setText("今日\n直播");

            }else {
                 mLiveStatusTxt.setVisibility(View.GONE);
             }


             if(mineItem.oneToOne>0){
                 mSplitView.setVisibility(View.VISIBLE);
                 mOneByoneView.setVisibility(View.VISIBLE);
             }else {
                 mSplitView.setVisibility(View.GONE);
                 mOneByoneView.setVisibility(View.GONE);
             }

             int size=Math.min(3,ArrayUtils.size(mineItem.teacherImg));
             String[] names=new String[size];
             for(int i=0;i<3;i++){
                 if(i<size){
                     names[i]=mineItem.teacherImg.get(i).teacherName;
                     mTeacherAvaters[i].setVisibility(View.VISIBLE);
                     ImageLoad.displaynoCacheUserAvater(mContext,mineItem.teacherImg.get(i).roundPhoto,mTeacherAvaters[i],R.mipmap.user_default_avater);
                 }else
                     mTeacherAvaters[i].setVisibility(View.GONE);

             }
            mTeacherNameTxt.setNameList(names);
            mTitle.setText(mineItem.title);
            mTimeLength.setText(mineItem.descString);

           // int formatProgress=mineItem.schedule>0? (mineItem.schedule<1? 1:(int)mineItem.schedule):0;

            mProgressBar.setCurProgress2(mineItem.totalSchedule);
            mLearnTxt.setText("已学"+mineItem.totalSchedule+"%");

       /*     if(mineItem.classType== CourseTypeEnum.RECORDING.getValue()){
                mLearnTxt.setText("已学"+mineItem.totalSchedule+"%");
            }else {
                mLearnTxt.setText("已直播"+mineItem.totalSchedule+"%");
            }*/
           // ImageLoad.displaynoCacheImage(mContext,R.mipmap.load_default,mineItem.cover,mCoverImg);
      }
    }



}