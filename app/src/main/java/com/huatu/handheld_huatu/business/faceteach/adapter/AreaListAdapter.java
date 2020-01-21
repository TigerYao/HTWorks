package com.huatu.handheld_huatu.business.faceteach.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.DateLiveBean;
import com.huatu.handheld_huatu.mvpmodel.FaceAreaBean;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.StatusbgTextView;
import com.huatu.utils.ArrayUtils;

import java.util.List;

/**
 * 适配器
 *
 */

public class AreaListAdapter extends SimpleBaseRecyclerAdapter<FaceAreaBean> {

    private String mSelectID;
    private int mSelectIndex=-1;

    public int getSelectIndex(){
        if(mSelectIndex>-1&&(mSelectIndex<getItemCount())) return mSelectIndex;
        for(int i=0;i<getItemCount();i++){
            if(getItem(i).areaid.equals(mSelectID)) {
                mSelectIndex=i;
                break;
            }
        }
        return mSelectIndex;
    }
    public void setSeletedID(String seletedID){
        mSelectID=seletedID;
     }

     public String getSeletedID(){
        return mSelectID;
     }

    public AreaListAdapter(Context context, List<FaceAreaBean> listbeans) {
        super(context,listbeans);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

         return  new ArticleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.comm_text_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            ArticleViewHolder h = (ArticleViewHolder) holder;
            FaceAreaBean item = getItem(position);
            if(item.areaid.equals(mSelectID)){
                h.mTextTitle.setTextColor(Color.BLACK);
                h.mTextTitle.getPaint().setFakeBoldText(true);
                mSelectIndex=position;
                h.mTextTitle.setSelected(true);
            }else {
                h.mTextTitle.getPaint().setFakeBoldText(false);
               h.mTextTitle.setTextColor(0xFF4A4A4A);
                h.mTextTitle.setSelected(false);
            }
            h.mTextTitle.setText(item.areaname);
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
