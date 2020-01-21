package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.business.main.MainTabActivity;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.DateLiveBean;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.StatusbgTextView;
import com.huatu.utils.ArrayUtils;

/**
 * 适配器
 *
 */

public class CalendarCourseAdapter extends SimpleBaseRecyclerAdapter<DateLiveBean> {

    private CommloadingView.StatusMode mEmptyStatus= CommloadingView.StatusMode.None;
    //private int mEmptyStatus=0;//0 空，1 loading ,2 error
    public void setEmptyStatus(CommloadingView.StatusMode emptyStatus){
        mEmptyStatus=emptyStatus;
    }

    public CalendarCourseAdapter(Context context) {
        super(context);
    }


    @Override
    public int getItemViewType(int position) {

        if(mEmptyStatus==CommloadingView.StatusMode.None) return 0;
        if(ArrayUtils.isEmpty(mItems)) return 1;
        return 0;  //0  课程1  空数据
    }

    @Override
    public int getItemCount(){
        if(mEmptyStatus==CommloadingView.StatusMode.None) return ArrayUtils.size(mItems);
        if(ArrayUtils.isEmpty(mItems)) return 1;
        return ArrayUtils.size(mItems);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==1){
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.loading_default_layout, parent, false);
            return new EmptyViewHolder(collectionView);
        }
        else {

            return   new ArticleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.course_calendar_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        int viewType=getItemViewType(position);
        if(viewType==0) {
            ArticleViewHolder h = (ArticleViewHolder) holder;

            DateLiveBean item = getItem(position);
            h.mTextTitle.setText(item.title);
            h.mTextContent.setText(item.shortTitle);
            h.mTeacherView.setText("主讲老师：" + item.teacherName);
            h.mTimeView.setText(item.beginTime + "-" + item.endTime);
            h.mStatusBgTextView.setTextColor(item.status==StatusbgTextView.END ? 0XFF9B9B9B: Color.WHITE);
            h.mStatusBgTextView.setText(item.actionDesc);
            h.mStatusBgTextView.setStatus(item.status,true);

        }else {
            EmptyViewHolder emptyViewHolder=(EmptyViewHolder)holder;
            emptyViewHolder.bindUI();

        }

    }



    protected class EmptyViewHolder extends RecyclerView.ViewHolder  {

        CommloadingView mLoadingView;
        EmptyViewHolder(View itemView) {
            super(itemView);
            mLoadingView=(CommloadingView)itemView;
            mLoadingView.setEmptyImg(R.mipmap.course_no_data2_icon);//course_no_cache_icon
            mLoadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_none_studycourse);
            mLoadingView.setTipText(R.string.xs_my_empty);
            mLoadingView.setOnRtyClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=onRecyclerViewItemClickListener)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_RELOAD);
                }
            });

            mLoadingView.disableDetachedFromWindow();
        }


        public void bindUI() {

            if(mEmptyStatus==CommloadingView.StatusMode.empty){
                mLoadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_none_studycourse);
                mLoadingView.setTipText(R.string.xs_my_empty);
                // mLoadingView.findViewById(R.id.xi_tv_tips).setVisibility(View.VISIBLE);
                mLoadingView.showEmptyStatus();
            }else if(mEmptyStatus==CommloadingView.StatusMode.loading){

                mLoadingView.showLoadingStatus();

            }else if(mEmptyStatus==CommloadingView.StatusMode.serverError){
               mLoadingView.showNetworkTip();

            }

        }
    }

    private  class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextTitle,mTeacherView,mTimeView,
                mTextContent;
        private StatusbgTextView mStatusBgTextView;


        private ArticleViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTextContent = (TextView) itemView.findViewById(R.id.tv_content);
            mTeacherView=(TextView)itemView.findViewById(R.id.tv_teacher) ;
            mTimeView=itemView.findViewById(R.id.tv_play_time) ;
            mStatusBgTextView=itemView.findViewById(R.id.tv_lession_status) ;
            itemView.findViewById(R.id.whole_content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(null!=onRecyclerViewItemClickListener)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                   // Toast.makeText(v.getContext(),"fadsfad"+getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }



}
