package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.BJPlayTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.PurchasedCourseBean;
import com.huatu.handheld_huatu.mvpmodel.SyllabusClassesBean;
import com.huatu.handheld_huatu.mvpmodel.TeacherBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.PieProgressView;
import com.huatu.handheld_huatu.ui.SelectTextView;
import com.huatu.handheld_huatu.ui.WaveView;
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.text.RadiusBackgroundSpan;
import com.huatu.widget.text.RectStorkeBackgroundSpan;

import java.util.List;

/**
 * Created by cjx on 2018\7\20 0020.
 * 课程大纲，售后
 */

public class CatalogClassesAdapter extends SimpleBaseRecyclerAdapter<SyllabusClassesBean> {

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

    public void clearSelectedIds(){
        mSelectItem.clear();
        this.notifyDataSetChanged();
    }

    public List<SyllabusClassesBean> getAllItem(){ return mItems; }

    public CatalogClassesAdapter(Context context, List<SyllabusClassesBean> items) {
        super(context,items);
       // mNorDrawable= ResourceUtils.getDrawable(R.drawable.play_filterbg_nor);
       // mSelDrawable=ResourceUtils.getDrawable(R.drawable.play_filterbg_red);
    }

    @Override
    public int getItemViewType(int position) {
         return mItems.get(position).type;  //0  阶段1课程2课件	number	@mock=0
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==1){
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.catalog_filter_middleitem, parent, false);
            return new CourseNumViewHolder(collectionView);
        } else {
              View collectionView = LayoutInflater.from(mContext).inflate(R.layout.catalog_filter_topitem, parent, false);
              return new TopViewHolder(collectionView);
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
        }
    }

    protected class  TopViewHolder extends RecyclerView.ViewHolder {
          SelectTextView mTitle;
          TopViewHolder(View itemView) {
            super(itemView);
            mTitle = (SelectTextView) itemView.findViewById(R.id.title_txt);
        }

        public void bindUI(SyllabusClassesBean lessionBean,int pos){
            mTitle.setText(lessionBean.name);
        }
    }

    protected class CourseNumViewHolder extends TopViewHolder implements View.OnClickListener  {
         CourseNumViewHolder(View itemView) {
            super(itemView);
             mTitle.setHasBackground(true);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
         }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    SyllabusClassesBean curTeacher= (SyllabusClassesBean) getItem(getAdapterPosition()-1);
                    final  int key=curTeacher.nodeId;
                    LogUtils.e("onItemClick",key+","+getAdapterPosition());
                    boolean hasSelect=  mSelectItem.get(key)!=null;
                    if(hasSelect){
                        mTitle.setSelected(false);
                        mTitle.setTextColor(NorColor);
                        // mTitle.setBackground(mNorDrawable);
                        //mTitle.setEnabled(false);
                        mSelectItem.remove(key);
                    }else {
                        mTitle.setSelected(true);
                        mTitle.setTextColor(Color.WHITE);
                       //  mTitle.setBackground(mSelDrawable);
                       // mTitle.setEnabled(true);
                        mSelectItem.put(key,String.valueOf(key));
                    }
                    break;
            }
        }

        public void bindUI(SyllabusClassesBean lessionBean,int pos){
            super.bindUI(lessionBean,pos);
            final  int key=lessionBean.nodeId;
            if(mSelectItem.get(key)!=null){
                mTitle.setSelected(true);
                mTitle.setTextColor(Color.WHITE);
               // mTitle.setEnabled(false);
               // mTitle.setBackground(mSelDrawable);
            }
            else{
                mTitle.setSelected(false);
                mTitle.setTextColor(NorColor);
                //mTitle.setEnabled(true);
               // mTitle.setBackground(mNorDrawable);
            }
        }
     }




}

