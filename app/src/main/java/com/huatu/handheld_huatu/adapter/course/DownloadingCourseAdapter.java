package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.ui.DownProgressBar;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by cjx
 * https://blog.csdn.net/jdsjlzx/article/details/52893469
 * RecyclerView局部刷新那个坑
 */

public class DownloadingCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    DecimalFormat df = new DecimalFormat("0.0"); //取所有整数部分

    private List<DownLoadLesson> mListCourse;


    int stopColor=Color.parseColor("#FF3F47");
    int errorColor=Color.parseColor("#4A4A4A");
    int waitColor=Color.parseColor("#9B9B9B");

    int stopBgcolor=Color.parseColor("#FFF2F3");
    int errorBgcolor=Color.parseColor("#F3F3F3");
    int waitBgcolor=Color.parseColor("#F2F4FF");

    private int mCurItemType;

    private int mActionMode=0;//正常0，编辑1
    private final int NormalMode=0;
    private final int EditMode=1;
    private String mDownloadingId="";


    @Override
    public int getItemViewType(int position) {
        return mCurItemType;
    }

    public void setActionMode(boolean isEdit){
        mActionMode=isEdit? EditMode:NormalMode;
        mDownloadingId="";
        this.notifyDataSetChanged();
    }

    public void resetAndRefresh(){
        mDownloadingId="";
        this.notifyDataSetChanged();
    }

    public boolean isEditMode(){
        return mActionMode==EditMode;
    }

    public DownloadingCourseAdapter(Context context, List<DownLoadLesson> cacheCourses,boolean hasFinished) {
        this.mContext = context;
        mListCourse = cacheCourses;
        mCurItemType=hasFinished? 1:0;
    }

    public void removeAndRefresh(int postion){
        if((postion>=0)&&(postion<getItemCount())){
            mListCourse.remove(postion);
            this.notifyItemRemoved(postion);
        }
    }

    public List<DownLoadLesson> getAllLession(){
        return mListCourse;
    }

    public DownLoadLesson getCurrentItem(int position){
        if(position>=0&&position<ArrayUtils.size(mListCourse))
            return mListCourse.get(position);
        return null;
    }

    public void  refresh(List<DownLoadLesson> cacheCourses){
        this.mListCourse.clear();
        mListCourse.addAll(cacheCourses);
        this.notifyDataSetChanged();
    }


    OnRecItemClickListener mClickListener;
    public void setOnItemClickListener(OnRecItemClickListener clickListener){
        mClickListener=clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            View textView = LayoutInflater.from(mContext).inflate(R.layout.down_loading_list_item, parent, false);
            return new DownLoadingViewHolder(textView);
          }else {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.down_finish_list_item, parent, false);
            return new DownFinishViewHolder(textView);
        }
     }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads){
        //super.onBindViewHolder(holder,position,payloads);
        if(ArrayUtils.isEmpty(payloads))
           onBindViewHolder(holder,position);
        else {
            DownLoadingViewHolder viewHolder=(DownLoadingViewHolder)holder;
            viewHolder.mProgressbar.setProgress(mListCourse.get(position).getDownloadProgress());
            if(!"缓存中".equals(viewHolder.mStatusText.getText())){
                viewHolder.mStatusText.setTextColor(waitColor);
                viewHolder.mStatusText.setText("缓存中");
                viewHolder.mProgressbar.setProgressColor(waitBgcolor);
            }
         }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==0){
            DownLoadingViewHolder textViewHolder = (DownLoadingViewHolder) holder;
            DownLoadLesson curObj=mListCourse.get(position);
            textViewHolder.bindUi(curObj,(position+1),curObj.getDownloadProgress());
        }else {

            DownFinishViewHolder textViewHolder = (DownFinishViewHolder) holder;
            DownLoadLesson curObj=mListCourse.get(position);
            textViewHolder.bindUi(curObj,(position+1));
        }
     }

    @Override
    public int getItemCount() {
        return ArrayUtils.size(mListCourse);
    }

    public void refreshCurrentProgess(String downloadId,int progress){
       for (int i = 0; i < mListCourse.size(); i++) {
            if (Method.isEqualString(mListCourse.get(i).getDownloadID(), downloadId)) {
                mListCourse.get(i).setDownStatus(DownLoadStatusEnum.START.getValue());//1
                mListCourse.get(i).setDownloadProgress(progress);
                this.notifyItemChanged(i);
                break;
            }
        }
    }

    public void refreshPartProgess(String downloadId,int progress){
        for (int i = 0; i < mListCourse.size(); i++) {
            if (Method.isEqualString(mListCourse.get(i).getDownloadID(), downloadId)) {
                mListCourse.get(i).setDownStatus(DownLoadStatusEnum.START.getValue());//1
                mListCourse.get(i).setDownloadProgress(progress);
                this.notifyItemChanged(i,"partRefresh");
                break;
            }
        }
    }

    private class DownFinishViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mNameText;
        TextView mStatusText;
        CheckBox mChkBox;
        DownFinishViewHolder(View itemView) {
            super(itemView);
            this.mChkBox=(CheckBox)itemView.findViewById(R.id.chk_btn);
            this.mNameText = (TextView) itemView.findViewById(R.id.name_txt);
            this.mStatusText = (TextView) itemView.findViewById(R.id.status_txt);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this);
            mChkBox.setOnClickListener(this);
            // mProgressbar.setProgressDrawable(initDrawable);
        }

        @Override
        public  void onClick(View v){
            switch (v.getId()){
                case R.id.chk_btn:
                case R.id.whole_content:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),isEditMode()?mChkBox:v, EventConstant.EVENT_ALL);
                    break;
                case R.id.delete:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_DELETE);
                    break;

            }
        }

        public void bindUi(DownLoadLesson downloadlession,int postion) {

            mChkBox.setVisibility(mActionMode==EditMode?View.VISIBLE:View.GONE);
            mChkBox.setChecked(downloadlession.isSelect());
            if(downloadlession.getLesson()>0){
                Spannable span = new SpannableString(downloadlession.getLesson()+"  "+downloadlession.getSubjectName());
                int length= StringUtils.getIntSize(downloadlession.getLesson());
                span.setSpan(new RelativeSizeSpan(1.2f), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mNameText.setText(span);
            }else
                mNameText.setText("  "+downloadlession.getSubjectName());

           // int total = (int) (downloadlession.getSpace() / 1024 / 1024);

            String learnString="";
            float progress=0l;
            if (downloadlession.getDuration() != 0) {
                progress =   (((float)downloadlession.getPlay_duration()) * 100 / downloadlession
                        .getDuration());
            } else {
                progress = 0;
            }
            if (progress <0.1) {
                learnString=("未学习");
            } else {
                if(progress>=100){
                    learnString=("已学完");
                }else if(progress<100&&progress>=99.9){
                    learnString=("已学99.9%");
                } else {
                    learnString=("已学" +  df.format(progress) + "%");
                }
            }
            mStatusText.setText(learnString+"  |  "+ CommonUtils.formatSpaceSize(downloadlession.getSpace()) );
        }

    }

    private class DownLoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        DownProgressBar mProgressbar;
        TextView mNameText;
        TextView mStatusText;
        CheckBox mChkBox;

        DownLoadingViewHolder(View itemView) {
            super(itemView);
            this.mProgressbar = (DownProgressBar) itemView.findViewById(R.id.progressBar);
            this.mNameText = (TextView) itemView.findViewById(R.id.name_txt);
            this.mStatusText = (TextView) itemView.findViewById(R.id.status_txt);
            this.mChkBox=(CheckBox)itemView.findViewById(R.id.chk_btn);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this);
            ((CheckBox) itemView.findViewById(R.id.chk_btn)).setOnClickListener(this);
           // mProgressbar.setProgressDrawable(initDrawable);
      }

        @Override
        public  void onClick(View v){
            switch (v.getId()){

                case R.id.chk_btn:
                case R.id.whole_content:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),isEditMode()?mChkBox:v, EventConstant.EVENT_ALL);
                    break;
                case R.id.delete:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_DELETE);
                    break;

            }
        }

        public void bindUi(DownLoadLesson downloadlession,int postion,int progress) {

            mChkBox.setVisibility(mActionMode==EditMode?View.VISIBLE:View.GONE);
            mChkBox.setChecked(downloadlession.isSelect());
            LogUtils.e("bindUi",progress+","+postion);
            Spannable span = new SpannableString(postion+"  "+downloadlession.getSubjectName());
            span.setSpan(new RelativeSizeSpan(1.2f), 0, StringUtils.getIntSize(postion), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mNameText.setText(span);
            if (downloadlession.getDownStatus() == DownLoadStatusEnum.STOP.getValue()) {// 4
                mStatusText.setText("已暂停");
                mStatusText.setTextColor(stopColor);
                mProgressbar.setProgressColor(stopBgcolor);
                mProgressbar.setProgress(progress);
            } else {

                if (downloadlession.getDownStatus() ==DownLoadStatusEnum.ERROR.getValue() ) {//3
                    mStatusText.setTextColor(errorColor);
                    mStatusText.setText("下载已暂停");
                    mProgressbar.setProgressColor(errorBgcolor);
                    mProgressbar.setProgress(100);

                } else if (downloadlession.getDownStatus() ==DownLoadStatusEnum.FINISHED.getValue() ) {//2
                    mProgressbar.setProgress(0);
                    mStatusText.setText("缓存完成");
                } else if(downloadlession.getDownStatus() ==DownLoadStatusEnum.START.getValue()) {// 1
                    mStatusText.setTextColor(waitColor);
                    mStatusText.setText("缓存中");
                    mProgressbar.setProgressColor(waitBgcolor);
                    if(isEditMode()){
                        mProgressbar.setProgress(downloadlession.getDownloadProgress());
                    }else {
                        if(downloadlession.getDownloadID().equals(mDownloadingId)){
                            mProgressbar.setProgress(downloadlession.getDownloadProgress());

                        }else {
                            mProgressbar.setProgress(downloadlession.getDownloadProgress());
                            mDownloadingId=downloadlession.getDownloadID();
                        }
                    }

                } else {
                    mStatusText.setTextColor(waitColor);
                    mStatusText.setText("等待缓存");
                    mProgressbar.setProgressColor(waitBgcolor);
                    mProgressbar.setProgress(downloadlession.getDownloadProgress());
                    //PREPARE(-1),  //准备  INIT
                }
            }
           // downloadlession.oldDownStatus=downloadlession.getDownStatus();
        }
    }


}
