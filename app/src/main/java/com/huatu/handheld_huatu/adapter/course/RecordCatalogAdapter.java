package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.business.essay.cusview.ScoreLayout;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.BJPlayTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.EssayAfterStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.ui.CustomShapeDrawable;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.PieProgressView;
import com.huatu.handheld_huatu.ui.RoundbgTextView;
import com.huatu.handheld_huatu.ui.WaveView;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.library.internal.ViewCompat;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.CenterAlignImageSpan;
import com.huatu.widget.text.RectStorkeBackgroundSpan;

import java.util.List;

/**
 * Created by cjx on 2018\7\20 0020.
 * 课程大纲，售后
 */

public class RecordCatalogAdapter  extends SimpleBaseRecyclerAdapter<PurchasedCourseBean.Data> {

    private boolean mIsAfterShow=true;
    public void setShowAfterClass(boolean needShow){
        mIsAfterShow=needShow;
    }

    private boolean mCanExpland=true;
    public void setCanExpland(boolean canexpland){
        mCanExpland=canexpland;
    }

    public void destory(){
         mContext=null;
         onRecyclerViewItemClickListener=null;
    }
    //存放已播放的课件
    private SparseArray<PurchasedCourseBean.Data> mHasPlayCourseMap = new SparseArray<>();

    public void refreshLastPlayTime(int hasPlayTime){
        if(mSelectId==0||hasPlayTime==0) return;
        PurchasedCourseBean.Data tmpBean=  mHasPlayCourseMap.get(mSelectId);
        if(null!=tmpBean){
            int leftTime=  tmpBean.coursewareTimeLength-hasPlayTime;
            LogUtils.e("setSelectId",leftTime+"");

            String lengthDes="";
            if(!TextUtils.isEmpty(tmpBean.videoLength)&&tmpBean.videoLength.contains("-")){
                 lengthDes=tmpBean.videoLength.split("-")[0];

                if(!TextUtils.isEmpty(lengthDes))
                    lengthDes=lengthDes+"- ";
            }
            if(!lengthDes.contains("回放")){
                if(leftTime==0) {
                    tmpBean.videoLength=lengthDes+"已学完　"+ DateUtils.getStringTime(tmpBean.coursewareTimeLength*1000);
                }
                else
                    tmpBean.videoLength=lengthDes+"剩余"+DateUtils.getStringTime(leftTime*1000);
            }
            tmpBean.alreadyStudyTime=hasPlayTime;
        }
    }

    //选中的课件id
    private int mSelectId=0;
    private int mPlayIndex=0;
    public void setSelectIndex(int postion){
        mPlayIndex=postion;
        PurchasedCourseBean.Data tmpBean=getItem(postion);
        mSelectId= tmpBean.coursewareId;
        mHasPlayCourseMap.put(mSelectId,tmpBean);
        this.notifyDataSetChanged();
    }

 /*   public PurchasedCourseBean.Data getNextCoursWare(){
         for(int i=mPlayIndex;i<getItemCount();i++){
            if("0".equals(getItem(i).hasChildren)){
                return getItem(i);
            }
         }
        return null;
    }*/


    public void setLocationId(int id){
       this.mSelectId=id;
   }

    public int getLocationId(){
        return this.mSelectId;
    }

    public PurchasedCourseBean.Data getPlayingItem(){
         return this.getItem(mPlayIndex);
    }

    public int getPlayingIndex(){
        return mPlayIndex;
    }

    public void setPlayingIndex(int playIndex){
        this.mPlayIndex=playIndex;
    }

    public void setSelectId(int id){
        if(mSelectId==id) return;
        this.mSelectId=id;
        this.notifyDataSetChanged();
    }


    public void setSelectId(int id,int playTime){
        if(mSelectId==id){
            PurchasedCourseBean.Data tmpData= this.getItem(mPlayIndex);
            if(tmpData!=null&&(mSelectId==tmpData.coursewareId)){

                if(playTime==-1){
                    tmpData.isFinish=1;
                    playTime= tmpData.coursewareTimeLength;
                }
                int leftTime=  tmpData.coursewareTimeLength-playTime;
                LogUtils.e("setSelectId",leftTime+"");

                String lengthDes="";
                if(!TextUtils.isEmpty(tmpData.videoLength)&&tmpData.videoLength.contains("-")){
                   lengthDes=tmpData.videoLength.split("-")[0];

                   if(!TextUtils.isEmpty(lengthDes))
                       lengthDes=lengthDes+"- ";
                }
                if(!lengthDes.contains("回放")) {
                    if (leftTime == 0) {
                        tmpData.videoLength = lengthDes + "已学完　" + DateUtils.getStringTime(tmpData.coursewareTimeLength * 1000);
                    } else
                        tmpData.videoLength = lengthDes + "剩余" + DateUtils.getStringTime(leftTime * 1000);
                }
                tmpData.alreadyStudyTime=playTime;
                this.notifyItemChanged(mPlayIndex);
                //videoLengthDes= lessionBean.videoLength;//"视频 - "
            }
            return;
        }
        this.mSelectId=id;
        this.notifyDataSetChanged();
    }

    private boolean mIsLocal=false;

    //private Bitmap mPlayBackBitmap;

    public boolean isSameSelectId(int postion){
        return getItem(postion).coursewareId==mSelectId;
    }

    public RecordCatalogAdapter(Context context, List<PurchasedCourseBean.Data> items,boolean islocal) {
        super(context,items);
        mIsLocal=islocal;
       // Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.playback_icon);
       // mPlayBackBitmap = Bitmap.createScaledBitmap(bitmap, DensityUtils.dp2px(context,28), DensityUtils.dp2px(context,16), true);
    }

    public List<PurchasedCourseBean.Data> getAllItem(){ return mItems; }

    public void resetDataSource(List<PurchasedCourseBean.Data> listCourse){
         if(this.mItems!=listCourse){
            this.mItems=listCourse;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {

         if(mItems.get(position).videoType==4) return 4;   //4  为阶段测试
         return mItems.get(position).type;                 //0  阶段1课程2课件  ,3加载更多的	number	@mock=0
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0){
             View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_top_list_item, parent, false);
            return new TopViewHolder(collectionView);
        }else if(viewType==1){
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_middle_list_item, parent, false);
            return new CourseNumViewHolder(collectionView);
        }
        else if(viewType==3){
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_morebtn_list_item, parent, false);
            return new MoreBtnViewHolder(collectionView);
        }
        else if(viewType==4){
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_period_list_item, parent, false);
            return new PeriodViewHolder(collectionView);
        }
        else {
           // View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_bottom_list_item, parent, false);
           if(mIsLocal){
                 View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_bottom_list_item, parent, false);
                return new BottomViewHolder(collectionView);
            }else {
               View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_afterclass_list_item, parent, false);
                return new AfterClassViewHolder(collectionView);
            }
         }
     }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int viewType=getItemViewType(position);
        if(viewType==0) {
            TopViewHolder holderfour = (TopViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }else if(viewType==1){
            CourseNumViewHolder holderfour = (CourseNumViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }else if(viewType==3){

        }else if(viewType==4){
            PeriodViewHolder holderfour = (PeriodViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }else {
            if(mIsLocal){
                BottomViewHolder holderfour = (BottomViewHolder) holder;
                holderfour.bindUI(mItems.get(position), position);
             }else {
                AfterClassViewHolder holderfour = (AfterClassViewHolder) holder;
                holderfour.bindUI(mItems.get(position), position);
            }
        }
    }

    protected class PeriodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mRootView;
        ImageView mStatusImg;
        TextView mTitle,mLearnTimeTxt,mTeacherTxt;
        PeriodViewHolder(View itemView) {
            super(itemView);
            mRootView=itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            mLearnTimeTxt=(TextView) itemView.findViewById(R.id.learn_time_txt);
            mTeacherTxt=(TextView) itemView.findViewById(R.id.lession_teacher_txt);
            mStatusImg=(ImageView)itemView.findViewById(R.id.learn_status_img);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
        }

        public void bindUI(PurchasedCourseBean.Data lessionBean,int pos){
            String serialNumTxt=lessionBean.getSerialNumber()<=0 ?"":lessionBean.getSerialNumber()+"";
            mTitle.setText(serialNumTxt+"\u0020"+lessionBean.title);

            mLearnTimeTxt.setText(lessionBean.isEffective==1?String.valueOf(lessionBean.videoLength):"");
            if(lessionBean.testStatus==1){
                mTeacherTxt.setTextColor(0xFF4A4A4A);
                mTeacherTxt.setText("未开始");
            }else if(lessionBean.testStatus==2){
                mTeacherTxt.setTextColor(0xFF49CF9E);
                mTeacherTxt.setText("开始考试");
            }
            else if(lessionBean.testStatus==5){
                mTeacherTxt.setTextColor(0XFFFF6D73);
                mTeacherTxt.setText("继续作答");
            }
            else if(lessionBean.testStatus==6){
                mTeacherTxt.setTextColor(0xFFEC74A0);
                mTeacherTxt.setText("考试报告");
            }else {
                mTeacherTxt.setTextColor(0xFF4A4A4A);
                mTeacherTxt.setText("未开始");
            }
           mStatusImg.setImageLevel((lessionBean.testStatus==1? 1:0));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.SELECT_LESSON);
                    break;
            }
        }
    }

    protected class MoreBtnViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View mRootView;
        MoreBtnViewHolder(View itemView) {
            super(itemView);
            mRootView=itemView;
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_RELOAD);
                    break;
            }
        }
    }

    protected class CourseNumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle,mCourseNum;
        ImageView mOpenImg;
        View mRootView;

        CourseNumViewHolder(View itemView) {
            super(itemView);
            mRootView=itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            mCourseNum = (TextView) itemView.findViewById(R.id.course_num_txt);
            mOpenImg = (ImageView) itemView.findViewById(R.id.right_open_img);

            mOpenImg.setOnClickListener(this);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(!mCanExpland) return;
            switch (v.getId()) {
                 case R.id.whole_content:
                     if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), mOpenImg, EventConstant.EVENT_MORE);
                     break;
                case R.id.right_open_img:

                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), mOpenImg, EventConstant.EVENT_MORE);
                    break;
             }
        }

        public void bindUI(PurchasedCourseBean.Data lessionBean,int pos){
            mTitle.setText(lessionBean.title);
            mCourseNum.setText(String.valueOf(lessionBean.classHour+"课时"));

            if(mCanExpland){
                mOpenImg.setVisibility(View.VISIBLE);
                mOpenImg.setRotation(lessionBean.isClosed()?0:180);
            }else {
                mOpenImg.setVisibility(View.INVISIBLE);
            }
         }
    }

    protected class TopViewHolder extends CourseNumViewHolder  {
        TextView mLearnTimeTxt;
        TopViewHolder(View itemView) {
            super(itemView);
            mLearnTimeTxt= (TextView)itemView.findViewById(R.id.learn_time_txt);

        }

        @Override
        public void bindUI(PurchasedCourseBean.Data lessionBean,int pos){
            super.bindUI(lessionBean,pos);
           // mLearnTimeTxt.setText(String.format("学习时长 (%s)",lessionBean.studyLength));
            //mCourseNum.setText(String.format("%1$s/%2$s课时",lessionBean.studySchedule,lessionBean.classHour));//%1$d道题%2$d分
         }
    }

    int mSelectColor=Color.parseColor("#e9304e");
    int mNorColor=Color.parseColor("#000000");

    protected class BottomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitle,mLearnTimeTxt,mTeacherTxt;
       // ImageView mLearnStatusImg,mDownStatusImg;
        View mRootView;
        PieProgressView mLearnProgressBar;
        DownBtnLayout mDownBtnLayout;
        int mAdapterPos;


        BottomViewHolder(View itemView) {
            super(itemView);
            mRootView=itemView;
            mTitle = (TextView) itemView.findViewById(R.id.title_name_txt);
            mLearnTimeTxt=(TextView) itemView.findViewById(R.id.learn_time_txt);
            mTeacherTxt=(TextView) itemView.findViewById(R.id.lession_teacher_txt);

            mLearnProgressBar = (PieProgressView) itemView.findViewById(R.id.learn_status_img);
            mDownBtnLayout = (DownBtnLayout) itemView.findViewById(R.id.down_status_layout);
            mDownBtnLayout.setOnClickListener(this);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(mAdapterPos, v, EventConstant.EVENT_ALL);
                    break;
                case R.id.down_status_layout:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(mAdapterPos, v, EventConstant.EVENT_LIKE);
                    break;
                case R.id.after_layout:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(mAdapterPos, v, EventConstant.EVENT_COMMENT);
                    break;

            }
        }

        public void bindUI(PurchasedCourseBean.Data lessionBean,int pos){
            mAdapterPos=pos;
            String serialNumTxt=lessionBean.getSerialNumber()<=0 ?"":lessionBean.getSerialNumber()+"";
            //1点播2直播3直播回放
           /* if(lessionBean.videoType== BJPlayTypeEnum.PLAYBACK){
                String tmpContent= "回放";
                String lastStr=serialNumTxt+"\u0020"+tmpContent+lessionBean.title;//,26)
                int index=lastStr.indexOf(tmpContent);
               // StringUtils.cultString(lessionBean.title,14);
                SpannableString spanString = new SpannableString(lastStr);
                int end = tmpContent.length();

               // LogUtils.e("bindUI",lastStr+"_"+index+"_"+end+"_"+DensityUtils.dp2px(mContext,8));
                spanString.setSpan(new RadiusBackgroundSpan(Color.parseColor("#E1E1E1"), 8, DensityUtils.sp2px(mContext,11)), index, index+end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

               // ImageSpan span = new ImageSpan(mContext, mPlayBackBitmap);
                //spanString.setSpan(span, index, index + end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mTitle.setText(spanString);
            }
            else {
                mTitle.setText(serialNumTxt+"\u0020"+lessionBean.title);
            }*/

            mTitle.setText(serialNumTxt+"\u0020"+lessionBean.title);
            String videoLengthDes="";
          /*  if(lessionBean.isStudy==0){//0未开始学习
               //mLearnTimeTxt.setText("视频 - "+lessionBean.videoLength);
               videoLengthDes= lessionBean.videoLength;//"视频 - "
            }else if(lessionBean.isFinish==1){//1已学完
                //mLearnTimeTxt.setText("已学完"+lessionBean.videoLength);
                videoLengthDes="已学完 "+lessionBean.videoLength;

            }else {
                //mLearnTimeTxt.setText("视频 - 剩余"+lessionBean.videoLength);
                videoLengthDes="视频 - 剩余"+lessionBean.videoLength;
            }*/

           if(lessionBean.videoType==BJPlayTypeEnum.PLAYBACK){
              // videoLengthDes= lessionBean.videoLength.replace("视频","回放");
               videoLengthDes=lessionBean.videoLength;
           }
           else if(lessionBean.videoType==BJPlayTypeEnum.ONLIVE){
                videoLengthDes= lessionBean.videoLength.replace("视频","直播");
           }else if(lessionBean.videoType==BJPlayTypeEnum.RECORD){
               videoLengthDes= lessionBean.videoLength.replace("视频","高清");
           }else   {
               videoLengthDes= lessionBean.videoLength;
           }
            //"视频 - "
            mTitle.setTextColor(lessionBean.coursewareId==mSelectId ? mSelectColor:mNorColor);
            if(lessionBean.coursewareId==mSelectId) {
                mPlayIndex=pos;
                mHasPlayCourseMap.put(mSelectId,lessionBean);
            }
            mTeacherTxt.setText("主讲老师："+StringUtils.cultString(lessionBean.teacher,4));

            ////直播状态0未开始1进行中2已结束	number	@mock=0
            if(lessionBean.videoType==BJPlayTypeEnum.ONLIVE)
                if(lessionBean.liveStatus==1)
                   mLearnProgressBar.setStatus(PieProgressView.ONLIVE).setProgressValue(100);
                else if(lessionBean.liveStatus==2){
                    mLearnProgressBar.setStatus(PieProgressView.NORMAL).setProgressValue(100);
                }
                else
                    mLearnProgressBar.setStatus(PieProgressView.NORMAL).setProgressValue(0);
            else if((lessionBean.videoType==BJPlayTypeEnum.PLAYBACK)&&(!mIsLocal)){
                float leanPercent=lessionBean.coursewareTimeLength<=0 ?0:((float)lessionBean.alreadyStudyTime*100)/lessionBean.coursewareTimeLength;
                mLearnProgressBar.setStatus(PieProgressView.NORMAL).setProgressValue(leanPercent);
            }
            else{
                float leanPercent=lessionBean.coursewareTimeLength<=0 ?0:((float)lessionBean.alreadyStudyTime*100)/lessionBean.coursewareTimeLength;
                mLearnProgressBar.setStatus(PieProgressView.NORMAL).setProgressValue(leanPercent);
            }


            if(lessionBean.downStatus==DownBtnLayout.FINISH){
                mDownBtnLayout.setVisibility(View.GONE);
                videoLengthDes=videoLengthDes+"\u0020本地";
                int tmplen="本地".length();
                SpannableString spanString = new SpannableString(videoLengthDes);
                int end = videoLengthDes.length();
                spanString.setSpan(new RectStorkeBackgroundSpan(Color.parseColor("#CACACA"), 6, DensityUtils.sp2px(mContext,10),DensityUtils.dp2px(mContext,1.5f)), end-tmplen, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                mLearnTimeTxt.setText(spanString);
            }else if(lessionBean.downStatus==DownBtnLayout.DOWNLOADING){
                mLearnTimeTxt.setText(videoLengthDes);
                mDownBtnLayout.setVisibility(View.VISIBLE);
                mDownBtnLayout.setStatus(DownBtnLayout.DOWNLOADING);
            }else {
                mLearnTimeTxt.setText(videoLengthDes);

                if(lessionBean.videoType==BJPlayTypeEnum.ONLIVE){
                    mDownBtnLayout.setVisibility(View.GONE);
                }
                else{
                    mDownBtnLayout.setVisibility(View.VISIBLE);
                    mDownBtnLayout.setStatus(DownBtnLayout.NORMAL);
                }
             }
         }
    }

    Drawable mWrongDrawable,mRightDrawable;
    private Drawable getWrongIcon(){
        if(mWrongDrawable==null){
            mWrongDrawable=ResourceUtils.getDrawable(R.mipmap.course_after_erro_icon);
        }
        return mWrongDrawable;
    }

    private Drawable getRightIcon(){
        if(mRightDrawable==null){
            mRightDrawable=ResourceUtils.getDrawable(R.mipmap.course_after_right_icon);
        }
        return mRightDrawable;
    }


    private ShapeDrawable getDefaultBackground(int radius, int color, boolean isAllRound) {

        int r = DensityUtils.dp2px(mContext,radius);
        float[] outerR = new float[] {r, r,  0,  0,isAllRound?r: 0,isAllRound?r: 0, 0, 0};//// 前2个 左上角， 3 4 ， 右上角， 56， 右下， 78 ，左下，如果没弧度的话，传入null即可。

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        return drawable;
    }


    ShapeDrawable mUnsubmitDrawable,mReturnDrawable;
    GradientDrawable mCorrectingDrawable;
    private Drawable getEssayBackground(int status){
        if(status==3)  {
            return getAfterShapeDrawable(true);
        }
         switch (status){
             case 0:
                 return getAfterShapeDrawable(false);
             case 1:
                 if(mUnsubmitDrawable==null){
                     mUnsubmitDrawable=getDefaultBackground(4,0XFF5D9AFF,true);
                  }
                 return mUnsubmitDrawable;
             case 2:
             case 4:
                 if(mCorrectingDrawable==null){
                     //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                     int r = DensityUtils.dp2px(mContext,4);
                     float[] radius=     new float[] { r,r, 0, 0,r, r, 0,0 };
                    mCorrectingDrawable= CustomShapeDrawable.buildGradientBackgroud(0XFFFFC042, 0XFFFF9532, radius);
                 }
                 return mCorrectingDrawable;

             case 5:
                 if(mReturnDrawable==null){
                     mReturnDrawable=getDefaultBackground(4,0XFFFF6D73,true);
                 }
                 return mReturnDrawable;
        }
        return getAfterShapeDrawable(false);
    }


    ShapeDrawable mGreyDrawable,mRedDrawable;
    private ShapeDrawable getAfterShapeDrawable(boolean isRed){
        if(isRed){
            if(mRedDrawable==null){
                mRedDrawable=getDefaultBackground(4,0XFFFFEBF2,true);
            }
            return mRedDrawable;
        }else {
            if(mGreyDrawable==null){
                mGreyDrawable=getDefaultBackground(4,0XFFE0E0E0,true);
            }
            return mGreyDrawable;
        }
    }

    ShapeDrawable mUnfinsihDrawable,mFinishDrawable;
    private ShapeDrawable getReportDrawable(boolean hasFinish){
        if(hasFinish){
            if(mFinishDrawable==null){
                mFinishDrawable=getDefaultBackground(4,0XFFFFF0BC,true);
            }
            return mFinishDrawable;
        }else {
            if(mUnfinsihDrawable==null){
                mUnfinsihDrawable=getDefaultBackground(4,0XFFE0E0E0,true);
            }
            return mUnfinsihDrawable;
        }
    }

   // int testIndex=0;
    private class AfterClassViewHolder extends BottomViewHolder {

        View mAfterLayout;
        RoundbgTextView mAfterTitleView,mAfterReportView;
        WaveView mWaveView;
        View mAfterReportLayout;
        View mAfterWorkLayout;
        View mAfterReportStatus;

        AfterClassViewHolder(View itemView) {
            super(itemView);
            mAfterLayout=itemView.findViewById(R.id.after_layout);
            mAfterTitleView=(RoundbgTextView)itemView.findViewById(R.id.after_learn_txt);
            mAfterTitleView.setTag(R.id.reuse_tag2,"1");
            mAfterReportView=(RoundbgTextView)itemView.findViewById(R.id.after_report_txt);
            mWaveView=(WaveView)itemView.findViewById(R.id.onlive_bg);
            mAfterReportStatus=itemView.findViewById(R.id.after_report_status);
            mAfterWorkLayout=itemView.findViewById(R.id.after_work_layout);

            mAfterReportLayout=itemView.findViewById(R.id.after_report_layout);

            itemView.findViewById(R.id.after_work_layout).setOnClickListener(this);
            itemView.findViewById(R.id.after_report_layout).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
             switch (v.getId()) {
                case R.id.after_work_layout:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(mAdapterPos, v,  EventConstant.EVENT_COMMENT);
                    break;
                case R.id.after_report_layout:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(mAdapterPos, v, EventConstant.EVENT_COLLECT);
                    break;
                default:
                    super.onClick(v);
             }
        }

        @Override
        public void bindUI(PurchasedCourseBean.Data lessionBean, int pos) {
            super.bindUI(lessionBean, pos);

            if (lessionBean.videoType == BJPlayTypeEnum.ONLIVE && lessionBean.liveStatus == 1) {//
                mWaveView.setVisibility(View.VISIBLE);
            } else
                mWaveView.setVisibility(View.GONE);

            if (mIsAfterShow && ((lessionBean.afterCoreseNum > 0 && lessionBean.answerCard != null)||lessionBean.studyReport==1)) {
                mAfterLayout.setVisibility(View.VISIBLE);
                mAfterWorkLayout.setVisibility(lessionBean.afterCoreseNum > 0? View.VISIBLE:View.GONE);

                mAfterReportLayout.setVisibility(lessionBean.studyReport==1? View.VISIBLE:View.GONE);
                if(lessionBean.studyReport==1){
                   if(lessionBean.reportStatus<=0){
                       mAfterReportView.setText("未生成");
                       ViewCompat.setBackground(mAfterReportView,getReportDrawable(false));
                    }else {
                       mAfterReportView.setText(StringUtils.forHtml(StringUtils.fontColor("#D78B0F", "已生成")));
                       ViewCompat.setBackground(mAfterReportView,getReportDrawable(true));
                    }
                   // mAfterReportStatus.setVisibility(lessionBean.reportStatus<=0?View.GONE:View.VISIBLE);
                }
               if(lessionBean.afterCoreseNum>0){

                    if(lessionBean.subjectType==2){//申论
                         mAfterTitleView.setTextColor(Color.WHITE);
                         String oldbgFlag=String.valueOf(mAfterTitleView.getTag(R.id.reuse_tag2));

                         int curStatus=lessionBean.answerCard.status;
                         boolean isSingleList=(lessionBean.buildType==0)&&(lessionBean.afterCoreseNum>1);//多道单题列表

                         if(curStatus== EssayAfterStatusEnum.INIT.getValue()){
                            mAfterTitleView.setText("  未开始  ");
                            mAfterTitleView.setTextColor(0xFF4A4A4A);
                            mAfterTitleView.setTag(R.id.reuse_tag2,"1");//灰色背景
                         }
                        else if(curStatus==EssayAfterStatusEnum.UNFINISHED.getValue()){

                             if(!isSingleList){//单题  套题
                                 mAfterTitleView.setText("  未提交  ");
                                 mAfterTitleView.setTextColor(Color.WHITE );
                                 mAfterTitleView.setTag(R.id.reuse_tag2,"4");//绿色背景
                             } else {          //多道单题列表

                                 mAfterTitleView.setTextColor(0xFF4A4A4A);
                                 mAfterTitleView.setText(ScoreLayout.getScoreSpan("  ", "题", lessionBean.answerCard.fcount,
                                         lessionBean.afterCoreseNum, 11, 0xFFEC74A0));
                                 mAfterTitleView.setTag(R.id.reuse_tag2, "0");//红色背景
                             }
                        }
                         else if(curStatus==EssayAfterStatusEnum.CORRECTING.getValue()
                                 ||curStatus==EssayAfterStatusEnum.COMMIT.getValue()){

                            if(isSingleList){
                                mAfterTitleView.setTextColor(0xFF4A4A4A);
                                mAfterTitleView.setText(ScoreLayout.getScoreSpan("  ","题",lessionBean.answerCard.fcount,
                                        lessionBean.afterCoreseNum,11,0xFFEC74A0));
                                mAfterTitleView.setTag(R.id.reuse_tag2,"0");//红色背景
                            }else {
                                mAfterTitleView.setText("  批改中  ");//批改中  已交卷
                                mAfterTitleView.setTag(R.id.reuse_tag2,"5");//黄色背景
                            }
                         }
                         else if(curStatus==EssayAfterStatusEnum.CORRECT_RETURN.getValue()){
                             mAfterTitleView.setText("  被退回  ");//
                             mAfterTitleView.setTag(R.id.reuse_tag2,"6");     //深红色背景
                         }else if(curStatus==EssayAfterStatusEnum.CORRECT.getValue()){//已批改

                            if(isSingleList){
                                mAfterTitleView.setTextColor(0xFF4A4A4A);
                                mAfterTitleView.setText(ScoreLayout.getScoreSpan("  ","题", lessionBean.answerCard.fcount,
                                                     lessionBean.afterCoreseNum,11,0xFFEC74A0));
                                mAfterTitleView.setTag(R.id.reuse_tag2,"0");
                            }else {
                                mAfterTitleView.setTextColor(0xFF4A4A4A);
                                mAfterTitleView.setText(ScoreLayout.getScoreSpan("  ", "分",  lessionBean.answerCard.examScore,
                                        lessionBean.answerCard.score, 11, 0xFFEC74A0));
                                mAfterTitleView.setTag(R.id.reuse_tag2, "0");
                            }
                         }

                        if(!oldbgFlag.equals(mAfterTitleView.getTag(R.id.reuse_tag2))){
                             if(isSingleList&&(curStatus==EssayAfterStatusEnum.UNFINISHED.getValue())){
                                 ViewCompat.setBackground(mAfterTitleView,getEssayBackground(3));
                             }else {
                                 ViewCompat.setBackground(mAfterTitleView,getEssayBackground(curStatus));
                             }
                        }
                  /*      if(pos==8){
                            LogUtils.e("oldbgFlag",oldbgFlag+","+mAfterTitleView.getTag(R.id.reuse_tag2)+","+isSingleList+","+curStatus);
                        }*/
                    }else {
                        mAfterTitleView.setTextColor(0xFF4A4A4A);

                        String oldbgFlag=String.valueOf(mAfterTitleView.getTag(R.id.reuse_tag2));
                        if (lessionBean.answerCard.status == 3) {//2：已结束

                            String tmpStr = "   [img]  ".concat(String.valueOf(lessionBean.answerCard.rcount))
                                    .concat("     [A]  " + String.valueOf(lessionBean.answerCard.wcount)).concat("  ");
                            SpannableStringBuilder builder = new SpannableStringBuilder(tmpStr);

                            //得到drawable对象，即所要插入的图片
                            Drawable rightd = getRightIcon();
                            rightd.setBounds(0, 0, rightd.getIntrinsicWidth(), rightd.getIntrinsicHeight());

                            Drawable wrongd = getWrongIcon();
                            wrongd.setBounds(0, 0, wrongd.getIntrinsicWidth(), wrongd.getIntrinsicHeight());

                            int Startindex1 = tmpStr.indexOf("[img]");
                            builder.setSpan(new CenterAlignImageSpan(rightd), tmpStr.indexOf("[img]"), Startindex1 + "[img]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                            int Startindex2 = tmpStr.indexOf("[A]");
                            builder.setSpan(new CenterAlignImageSpan(wrongd), tmpStr.indexOf("[A]"), Startindex2 + "[A]".length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            mAfterTitleView.setText(builder);
                            mAfterTitleView.setTag(R.id.reuse_tag2,"1");

                        } else if (lessionBean.answerCard.status == 2) {//1：未做完
                            String as_detail = StringUtils.fontColor("#EC74A0",lessionBean.answerCard.ucount>0? String.format("剩余%d题", lessionBean.answerCard.ucount)
                                    : "待提交");
                            mAfterTitleView.setText(StringUtils.forHtml(as_detail));
                            mAfterTitleView.setTag(R.id.reuse_tag2,"0");

                        } else {
                            // mAfterTitleView.setBackground(getDefaultBackground(4,#E0E0E0,true));
                            mAfterTitleView.setText(String.format("练习题-%d道", lessionBean.afterCoreseNum));
                            mAfterTitleView.setTag(R.id.reuse_tag2,"1");
                        }
                        if(!oldbgFlag.equals(mAfterTitleView.getTag(R.id.reuse_tag2))){

                            //LogUtils.e("oldbgFlag",oldbgFlag+"_"+pos);
                            ViewCompat.setBackground(mAfterTitleView,getAfterShapeDrawable("1".equals(oldbgFlag)));

                            //mAfterTitleView.setColor("1".equals(oldbgFlag)?0XFFFFEBF2);
                        }
                     }
                 }

            } else mAfterLayout.setVisibility(View.GONE);
        }
    }
}

