package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.helper.image.ImageUrlUtils;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.BuyCourseBean;
import com.huatu.handheld_huatu.mvpmodel.CourseTypeEnum;
import com.huatu.handheld_huatu.ui.MultTextView;
import com.huatu.handheld_huatu.ui.recyclerview.LetterSortAdapter;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.widget.IncreaseProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cjx on 2018\6\29 0029.
 */

public class MyBuyCourselistAdapter extends LetterSortAdapter<BuyCourseBean.Data> {


    private Context mContext;
    private List<BuyCourseBean.Data> mLessons;

    public int mOnTopCount=0;
    private boolean mIsSearchType=false;

    public void removeAt(int p){
        if (p < 0 || p >= mLessons.size()) return;
        this.mLessons.remove(p);
        this.notifyItemRemoved(p);
    }


    private OnRecItemClickListener onRecyclerViewItemClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public MyBuyCourselistAdapter(Context context, List<BuyCourseBean.Data> lessons,boolean isSearchType) {
        super(context,lessons);
        this.mContext = context;
        this.mLessons = lessons;
        this.mIsSearchType=isSearchType;
    }

    public void clear() {
        if (mLessons != null) mLessons.clear();
    }

    public BuyCourseBean.Data getCurrentItem(int position) {
        if (position >= 0 && position < ArrayUtils.size(mLessons))
            return mLessons.get(position);
        return null;
    }

    public void clearAndRefresh() {
        this.mLessons.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ArrayUtils.size(mLessons);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_study_list_item, parent, false);
         return new ViewHolderTwo(collectionView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ViewHolderTwo holderfour = (ViewHolderTwo) holder;
        holderfour.bindUI(mLessons.get(position),position);
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderHolder(mInflater.inflate(R.layout.course_letter_item_decoration, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        viewholder.header.setText(mDatas.get(position).getLetter());
    }

    private Drawable mlayerDrawabel;
    private Drawable getExpiredViewbg(){
        if(null==mlayerDrawabel){
            mlayerDrawabel= ResourceUtils.getDrawable(R.drawable.layer_outdate_bg);
        }
        return mlayerDrawabel;
    }

    protected class ViewHolderTwo    extends ViewHolder  implements View.OnClickListener {

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
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v,EventConstant.EVENT_CHANGE);
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

        public void bindUI(BuyCourseBean.Data mineItem,int postion){

            //显示数据

            mSetTopBtn.setText("恢复");
            if (mineItem.isExpired) {
                mWholeContentView.setBackground(getExpiredViewbg());
            } else
                mWholeContentView.setBackgroundColor(Color.WHITE);


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