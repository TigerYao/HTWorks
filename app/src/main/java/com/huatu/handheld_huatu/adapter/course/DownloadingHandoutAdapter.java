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
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
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

public class DownloadingHandoutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    DecimalFormat df = new DecimalFormat("0.0"); //取所有整数部分

    private List<DownHandout> mListCourse;


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

    public DownloadingHandoutAdapter(Context context, List<DownHandout> cacheCourses) {
        this.mContext = context;
        mListCourse = cacheCourses;
        //mCurItemType=hasFinished? 1:0;
    }

    public void removeAndRefresh(int postion){
        mListCourse.remove(postion);
        this.notifyItemRemoved(postion);
     }

    public List<DownHandout> getAllLession(){
        return mListCourse;
    }

    public DownHandout getCurrentItem(int position){
        if(position>=0&&position<ArrayUtils.size(mListCourse))
            return mListCourse.get(position);
        return null;
    }

    public void  refresh(List<DownHandout> cacheCourses){
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

            View textView = LayoutInflater.from(mContext).inflate(R.layout.down_handoutloading_list_item, parent, false);
            return new DownLoadingViewHolder(textView);

     }
/*
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
    }*/

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            DownLoadingViewHolder textViewHolder = (DownLoadingViewHolder) holder;
            DownHandout curObj=mListCourse.get(position);
            textViewHolder.bindUi(curObj,(position+1));

     }

    @Override
    public int getItemCount() {
        return ArrayUtils.size(mListCourse);
    }



    private class DownLoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

       // DownProgressBar mProgressbar;
        TextView mNameText;
        TextView mStatusText;
        CheckBox mChkBox;

        DownLoadingViewHolder(View itemView) {
            super(itemView);
           // this.mProgressbar = (DownProgressBar) itemView.findViewById(R.id.progressBar);
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

        public void bindUi(DownHandout downloadlession,int postion) {

            mChkBox.setVisibility(mActionMode==EditMode?View.VISIBLE:View.GONE);
            mChkBox.setChecked(downloadlession.isSelect());
            LogUtils.e("bindUi", ","+postion);
            Spannable span = new SpannableString(postion+"  "+downloadlession.getSubjectName());
            span.setSpan(new RelativeSizeSpan(1.2f), 0, StringUtils.getIntSize(postion), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            mNameText.setText(span);
            if (downloadlession.getDownStatus() == DownBtnLayout.PAUSE) {// 4
                mStatusText.setText("已暂停");
                mStatusText.setTextColor(stopColor);

            } else {

                if (downloadlession.getDownStatus() ==DownBtnLayout.ERROR ) {//3
                    mStatusText.setTextColor(errorColor);
                    mStatusText.setText("下载已暂停");
                    //mProgressbar.setProgressColor(errorBgcolor);
                   // mProgressbar.setProgress(100);

                } else if (downloadlession.getDownStatus() ==DownBtnLayout.FINISH ) {//2
                   // mProgressbar.setProgress(0);
                    mStatusText.setText("缓存完成");
                } else if(downloadlession.getDownStatus() ==DownBtnLayout.DOWNLOADING) {// 1
                    mStatusText.setTextColor(waitColor);
                    mStatusText.setText("缓存中");
                    //mProgressbar.setProgressColor(waitBgcolor);
                   /* if(isEditMode()){
                        mProgressbar.setProgress(downloadlession.getDownloadProgress());
                    }else {
                        if(downloadlession.getDownloadID().equals(mDownloadingId)){
                            mProgressbar.setProgress(downloadlession.getDownloadProgress());

                        }else {
                            mProgressbar.setProgress(downloadlession.getDownloadProgress());
                            mDownloadingId=downloadlession.getDownloadID();
                        }
                    }*/

                } else {
                    mStatusText.setTextColor(waitColor);
                    mStatusText.setText("等待缓存");
                  //  mProgressbar.setProgressColor(waitBgcolor);
                   // mProgressbar.setProgress(downloadlession.getDownloadProgress());
                    //PREPARE(-1),  //准备  INIT
                }
            }
           // downloadlession.oldDownStatus=downloadlession.getDownStatus();
        }
    }


}
