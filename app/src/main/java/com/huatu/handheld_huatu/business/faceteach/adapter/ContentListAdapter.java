package com.huatu.handheld_huatu.business.faceteach.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.FaceAreaBean;

import java.util.List;

/**
 * 适配器
 *
 */

public class ContentListAdapter extends SimpleBaseRecyclerAdapter<FaceAreaBean> {

    public String mSeletedId;
    public void setSeletedID(String seletedID){
        mSeletedId=seletedID;
    }
    public ContentListAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          return  new ArticleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.face_text_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            ArticleViewHolder h = (ArticleViewHolder) holder;
            FaceAreaBean item = getItem(position);
            h.mTextTitle.setText(item.areaname);

            if(item.areaid.equals(mSeletedId)){
                h.mTextTitle.setSelected(true);
                h.mTextTitle.setTextColor(0XFFFF3F47);
            }
            else {
                h.mTextTitle.setTextColor(0XFF4A4A4A);
                h.mTextTitle.setSelected(false);
            }
    }



    private  class ArticleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextTitle ;



        private ArticleViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView;
           ;
            mTextTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(null!=onRecyclerViewItemClickListener){


                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                    }

                    // Toast.makeText(v.getContext(),"fadsfad"+getAdapterPosition(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


}
