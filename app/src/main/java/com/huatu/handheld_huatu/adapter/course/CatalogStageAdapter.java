package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.SyllabusClassesBean;
import com.huatu.handheld_huatu.ui.SelectTextView;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.utils.DensityUtils;

import java.util.List;

/**
 * Created by cjx on 2018\7\20 0020.
 * 课程大纲，售后
 */

public class CatalogStageAdapter extends SimpleBaseRecyclerAdapter<SyllabusClassesBean> {

    public void destory(){
         mContext=null;
         onRecyclerViewItemClickListener=null;
    }

    //private Drawable mNorDrawable,mSelDrawable;
    private final int NorColor=Color.parseColor("#ff4a4a4a");
    private SparseArray<String> mSelectItem=new SparseArray<>();


    public String getSelectedNodeIds(){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < mSelectItem.size(); i++) {
            if (i == mSelectItem.size() - 1) {
                sb.append(mSelectItem.keyAt(i));
            } else {
                sb.append(mSelectItem.keyAt(i) + ",");
            }
        }
        return sb.toString();
    }

    private boolean mNeedCenter=false;

    private int mSelectPostion=0;

    public void setSelectPostion(int postion){
        mSelectPostion=postion;
    }

    public void clearSelectedIds(){
        mSelectItem.clear();
        this.notifyDataSetChanged();
    }


    public CatalogStageAdapter(Context context, List<SyllabusClassesBean> items,boolean showCenter) {
        super(context,items);
        mNeedCenter=showCenter;
       // mNorDrawable= ResourceUtils.getDrawable(R.drawable.play_filterbg_nor);
       // mSelDrawable=ResourceUtils.getDrawable(R.drawable.play_filterbg_red);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         {
              View collectionView = LayoutInflater.from(mContext).inflate(R.layout.catalog_stagefilter_item, parent, false);
              return new TopViewHolder(collectionView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            TopViewHolder holderfour = (TopViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);

    }

    protected class  TopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
          TextView mTitle;
          TopViewHolder(View itemView) {
          super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title_txt);
            if(mNeedCenter){
                mTitle.setGravity(Gravity.CENTER);
                FrameLayout.LayoutParams tmpParam=(FrameLayout.LayoutParams)mTitle.getLayoutParams();
                tmpParam.leftMargin=0;
                tmpParam.rightMargin=0;
                mTitle.setLayoutParams(tmpParam);

            }
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
               if(null!=onRecyclerViewItemClickListener){

                   if(mSelectPostion==getAdapterPosition()) return;
                   onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
              }
        }

        public void bindUI(SyllabusClassesBean lessionBean,int pos){
            if(mNeedCenter){
                if("全部老师".equals(lessionBean.name)){
                    mTitle.setPadding(DensityUtils.dp2px(itemView.getContext(),20),mTitle.getPaddingTop(),0,mTitle.getPaddingBottom());
                    mTitle.setGravity(Gravity.LEFT);
                }else {
                    mTitle.setPadding(0,mTitle.getPaddingTop(),0,mTitle.getPaddingBottom());
                    mTitle.setGravity(Gravity.CENTER);
                }
            }
            mTitle.setText(lessionBean.name);
            mTitle.setTextColor(pos!=mSelectPostion?Color.BLACK:0XFFFF3F47);
         }
    }






}

