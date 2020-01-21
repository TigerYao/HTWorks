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
 */

public class DownloadingAddCourseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    //DecimalFormat df = new DecimalFormat("0.0"); //取所有整数部分

    private List<DownLoadLesson> mListCourse;

    int stopColor=Color.parseColor("#FF3F47");
    int errorColor=Color.parseColor("#4A4A4A");
    int waitColor=Color.parseColor("#9B9B9B");
    int norColor=Color.parseColor("#333333");

    int stopBgcolor=Color.parseColor("#FFF2F3");
    int errorBgcolor=Color.parseColor("#F3F3F3");
    int waitBgcolor=Color.parseColor("#F2F4FF");

    private int mCurItemType;

    private int mActionMode=0;//正常0，编辑1
    private final int NormalMode=0;
    private final int EditMode=1;
    private String mDownloadingId="";


    public void setActionMode(boolean isEdit){
        mDownloadingId="";
        mActionMode=isEdit? EditMode:NormalMode;
        this.notifyDataSetChanged();
    }

    public boolean isEditMode(){
        return mActionMode==EditMode;
    }

    public DownloadingAddCourseAdapter(Context context, List<DownLoadLesson> cacheCourses) {
        this.mContext = context;
        mListCourse = cacheCourses;
    }

    public void removeAndRefresh(int postion){
        mListCourse.remove(postion);
        this.notifyItemRemoved(postion);
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
    public int getItemViewType(int position) {
        if(mListCourse.get(position).getDownStatus()==0) return 0;
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.down_loading_list_additem, parent, false);
            return new DownStatusViewHolder(textView);
        } else {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.down_loading_list_noritem, parent, false);
            return new NorCheckViewHolder(textView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads){
        //super.onBindViewHolder(holder,position,payloads);
        if(ArrayUtils.isEmpty(payloads))
            onBindViewHolder(holder,position);
        else {
            DownStatusViewHolder viewHolder=(DownStatusViewHolder)holder;
            viewHolder.mProgressbar.setProgress(mListCourse.get(position).getDownloadProgress());
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==1){
            DownStatusViewHolder textViewHolder = (DownStatusViewHolder) holder;
            DownLoadLesson curObj = mListCourse.get(position);
            textViewHolder.bindUi(curObj, (position + 1), curObj.getDownloadProgress());
        }else {
            NorCheckViewHolder textViewHolder = (NorCheckViewHolder) holder;
            DownLoadLesson curObj = mListCourse.get(position);
            textViewHolder.bindUi(curObj, (position + 1), curObj.getDownloadProgress());
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

    private class NorCheckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameText;
        TextView mStatusText;
        CheckBox mChkBox;
        NorCheckViewHolder(View itemView) {
            super(itemView);
            this.mNameText = (TextView) itemView.findViewById(R.id.name_txt);
            this.mStatusText = (TextView) itemView.findViewById(R.id.status_txt);
            this.mChkBox = (CheckBox) itemView.findViewById(R.id.chk_btn);
            // mProgressbar.setProgressDrawable(initDrawable);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.chk_btn).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chk_btn:
                case R.id.whole_content:
                    if (mClickListener != null)
                        mClickListener.onItemClick(getAdapterPosition(), mChkBox, EventConstant.EVENT_JOIN_IN);
                    break;
             }
        }

        public void bindUi(DownLoadLesson downloadlession, int postion, int progress) {
            LogUtils.e("bindUi", progress + "," + postion);

            int serNum=downloadlession.getLesson();
            Spannable span = new SpannableString(serNum + "  " + downloadlession.getSubjectName());
            span.setSpan(new RelativeSizeSpan(1.2f), 0,StringUtils.getIntSize(serNum), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mChkBox.setVisibility( View.VISIBLE);
            mChkBox.setChecked(downloadlession.isSelect());
            mNameText.setText(span);
            mStatusText.setText(CommonUtils.formatSpaceSize(downloadlession.getSpace()));
            //downloadlession.oldDownStatus = downloadlession.getDownStatus();
        }
    }

    private class DownStatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        DownProgressBar mProgressbar;
        TextView mNameText;
        TextView mStatusText;
        CheckBox mChkBox;
        TextView mDelActionTxt;

        DownStatusViewHolder(View itemView) {
            super(itemView);
            this.mProgressbar = (DownProgressBar) itemView.findViewById(R.id.progressBar);
            this.mNameText = (TextView) itemView.findViewById(R.id.name_txt);
            this.mStatusText = (TextView) itemView.findViewById(R.id.status_txt);
            this.mChkBox=(CheckBox)itemView.findViewById(R.id.chk_btn);
            mDelActionTxt=(TextView)itemView.findViewById(R.id.delete) ;
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this);
           // mProgressbar.setProgressDrawable(initDrawable);
       }

        @Override
        public  void onClick(View v){
            switch (v.getId()){
                case R.id.whole_content:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                    break;
                case R.id.delete:
                    if(mClickListener!=null)
                        mClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_DELETE);
                    break;
             }
        }

        public void bindUi(DownLoadLesson downloadlession,int postion,int progress) {

            LogUtils.e("bindUi",progress+","+postion);
            int serNum= StringUtils.getIntSize(downloadlession.getLesson());
            Spannable span = new SpannableString(downloadlession.getLesson()+"  "+downloadlession.getSubjectName());
            span.setSpan(new RelativeSizeSpan(1.2f), 0, serNum, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mChkBox.setVisibility(DownAddAdapter.isDownedOrLoading(downloadlession)?View.INVISIBLE: View.VISIBLE);
            mNameText.setText(span);
            mDelActionTxt.setText("删除缓存");
            if (downloadlession.getDownStatus() == DownLoadStatusEnum.STOP.getValue()) {// 4
                mStatusText.setText("已暂停");
                mDelActionTxt.setText("取消下载");
                mStatusText.setTextColor(stopColor);
                mProgressbar.setProgressColor(stopBgcolor);
                mProgressbar.setProgress(progress);
            } else {
                if (downloadlession.getDownStatus() ==DownLoadStatusEnum.ERROR.getValue() ) {//3
                    mStatusText.setText("下载已暂停");
                    mDelActionTxt.setText("取消下载");
                    mStatusText.setTextColor(errorColor);
                    mProgressbar.setProgressColor(errorBgcolor);
                    mProgressbar.setProgress(100);

                } else if (downloadlession.getDownStatus() ==DownLoadStatusEnum.FINISHED.getValue() ) {//2
                    mProgressbar.setProgress(0);
                    mStatusText.setTextColor(norColor);
                    mStatusText.setText("已下载");
                    mDelActionTxt.setText("删除缓存");
                } else if(downloadlession.getDownStatus() ==DownLoadStatusEnum.START.getValue()) {// 1
                    mStatusText.setTextColor(waitColor);
                    mStatusText.setText("缓存中");
                    mDelActionTxt.setText("取消下载");
                    mProgressbar.setProgressColor(waitBgcolor);
                    if(downloadlession.getDownloadID().equals(mDownloadingId)){
                        mProgressbar.setProgress(downloadlession.getDownloadProgress());
                    }else {
                        mProgressbar.setProgress(downloadlession.getDownloadProgress());
                        mDownloadingId=downloadlession.getDownloadID();
                    }

                } else if(downloadlession.getDownStatus()==DownLoadStatusEnum.PREPARE.getValue()
                        ||downloadlession.getDownStatus()==DownLoadStatusEnum.INIT.getValue()) {
                    mStatusText.setTextColor(waitColor);
                    mStatusText.setText("等待缓存");
                    mDelActionTxt.setText("取消下载");
                    mProgressbar.setProgress(0);
                    //PREPARE(-1),  //准备  INIT
                }else {
                    mStatusText.setText("");
                    mProgressbar.setProgress(0);
                }
            }

        }
    }


}
