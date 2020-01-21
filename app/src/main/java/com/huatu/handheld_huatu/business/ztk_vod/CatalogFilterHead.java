package com.huatu.handheld_huatu.business.ztk_vod;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseHeader;
import com.huatu.handheld_huatu.base.adapter.ABaseAdapter;
import com.huatu.handheld_huatu.mvpmodel.TeacherBean;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018\11\5 0005.
 */

public abstract class CatalogFilterHead  extends BaseHeader implements View.OnClickListener {

    Drawable mNorDrawable,mSelDrawable;
    Drawable mOpenDrawable,mCloseDrawable;

    private final int NorColor=Color.parseColor("#ff4a4a4a");
    public class SuggestPicItemView  extends  ABaseAdapter.AbstractItemView<TeacherBean>{

        TextView mNameView;
        @Override
        public int inflateViewId() {
            return R.layout.play_filter_teache_item;
        }

        @Override
        public void bindingView(View convertView) { mNameView= (TextView)convertView;  }

        @Override
        public void bindingData(View convertView, TeacherBean data) {
            mNameView.setText(data.teacherName);
            final  int key=data.teacherId;
            if(mSelectItem.get(key)!=null){
                mNameView.setTextColor(Color.WHITE);
                mNameView.setBackground(mSelDrawable);
            }
            else{
                mNameView.setTextColor(NorColor);
                mNameView.setBackground(mNorDrawable);
            }
         }
    }

    class MyBaseAdapter extends ABaseAdapter<TeacherBean> {

        private boolean mShowAll=false;
        public void setShowAll(boolean isAllShow){
            this.mShowAll=isAllShow;
            this.notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            if (!mShowAll)
                return Math.min(9,super.getCount());

            return super.getCount();
        }

        public int getRealCount(){
            return super.getCount();
        }

        public MyBaseAdapter(ArrayList<TeacherBean> datas, Context context) {
            super(datas, context);
        }

        @Override
        protected AbstractItemView<TeacherBean> newItemView() {
            return new SuggestPicItemView();
        }
    }

    GridView mGridView;
    TextView mAfterTextView,mLocalTextView,mTeacherTextView;
    ImageView mExplandView;

    MyBaseAdapter mListTeacherAdapter;
    int  mIsAferclass=0;
    int mIsLocalclass=0;
    boolean mIsCatalogSuccess=false;
    SparseArray<TeacherBean> mSelectItem=new SparseArray<>();

    public int getFilterType(){
        return mIsLocalclass*2+mIsAferclass;
    }

    public abstract void onExplandClick(boolean isExpland);

    @Override
    public int inflateViewId() {
        return R.layout.play_catalogfilter_head_layout;
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        mGridView=(GridView) findViewById(R.id.teach_gridview);
        mAfterTextView=findViewById(R.id.afterclass_txt);
        mAfterTextView.setOnClickListener(this);

        mLocalTextView=findViewById(R.id.localclass_txt);
        mLocalTextView.setOnClickListener(this);

        mTeacherTextView=findViewById(R.id.teacher_bar_txt);
        mTeacherTextView.setOnClickListener(this);

        findViewById(R.id.right_open_img).setOnClickListener(this);
        mNorDrawable= ResourceUtils.getDrawable(R.drawable.play_filterbg_nor);
        mSelDrawable=ResourceUtils.getDrawable(R.drawable.play_filterbg_red);
        mCloseDrawable=ResourceUtils.getDrawable(R.drawable.zf_icon_aa);
        mOpenDrawable=ResourceUtils.getDrawable(R.drawable.zf_icon_aa_rotate);
    }

    @Override
    public  void onClick(View v){
       switch (v.getId()){
           case R.id.afterclass_txt:
                v.setSelected(!v.isSelected());
               mIsAferclass=v.isSelected()? 1:0;
               break;
           case R.id.localclass_txt:
               v.setSelected(!v.isSelected());
               mIsLocalclass=v.isSelected()?1:0;
               break;

           case R.id.teacher_bar_txt:
               if(null==mListTeacherAdapter||(mListTeacherAdapter.getRealCount()<=9)) return;
               boolean isTeaExpand="1".equals(v.getTag());
               if(isTeaExpand){
                   mListTeacherAdapter.setShowAll(false);
               }else {
                    mListTeacherAdapter.setShowAll(true);
               }
               v.setTag(!isTeaExpand?"1":"0");
               mTeacherTextView.setCompoundDrawablesWithIntrinsicBounds(null,null,isTeaExpand? mCloseDrawable:mOpenDrawable,null);
               break;
           case R.id.right_open_img:
               if(!mIsCatalogSuccess) return;
               boolean isExpand="1".equals(v.getTag());
               if(isExpand){
                   AnimUtils.showCloseRotation(v);
               }else {
                   // LogUtils.e("onItemClick",view.getRotation()+"");
                   AnimUtils.showOpenRotation(v);
               }
               onExplandClick(!isExpand);
               v.setTag(!isExpand?"1":"0");
               break;
       }
    }

    public void setCatalogListSuccess(boolean isEmpty){
        mIsCatalogSuccess=true;
        if(!isEmpty){
            findViewById(R.id.catalog_explandlayout).setVisibility(View.VISIBLE);
        }
    }

    public void bindUI(List<TeacherBean> teacherBeanList){

        if(null!=mTeacherTextView&& (ArrayUtils.size(teacherBeanList)>9)){
             mTeacherTextView.setCompoundDrawablesWithIntrinsicBounds(null,null,mCloseDrawable,null);
        }
        mListTeacherAdapter=new MyBaseAdapter((ArrayList)teacherBeanList ,mContext);
        mGridView.setAdapter(mListTeacherAdapter );
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TeacherBean curTeacher= (TeacherBean) mListTeacherAdapter.getItem(position);
                final  int key=curTeacher.teacherId;
                LogUtils.e("onItemClick",key+"");
                TeacherBean curString= (TeacherBean) mListTeacherAdapter.getItem(position);
                boolean hasSelect=  mSelectItem.get(key)!=null;
                if(hasSelect){
                    ((TextView)view).setTextColor(NorColor);
                    view.setBackground(mNorDrawable);
                    mSelectItem.remove(key);
                }else {
                    ((TextView)view).setTextColor(Color.WHITE);
                    view.setBackground(mSelDrawable);
                    mSelectItem.put(key,curTeacher);
                }
            }
        });

    }

    public String[] getSelectedTeachers(){
        StringBuffer sb = new StringBuffer();
        StringBuffer sbName = new StringBuffer();
        for(int i = 0; i < mSelectItem.size(); i++) {
            if (i == mSelectItem.size() - 1) {
                sb.append(mSelectItem.keyAt(i));
                sbName.append(mSelectItem.get(mSelectItem.keyAt(i)).teacherName);
            } else {
                sb.append(mSelectItem.keyAt(i) + ",");
                sbName.append(mSelectItem.get(mSelectItem.keyAt(i)).teacherName);
            }
        }
        return new String[]{sb.toString(),sbName.toString()};
    }

    public void resetUI(){
        mSelectItem.clear();
        if(null!=mListTeacherAdapter){
            mListTeacherAdapter.notifyDataSetChanged();
        }
        mAfterTextView.setSelected(false);
        mLocalTextView.setSelected(false);
        mIsLocalclass=mIsAferclass=0;
    }
}
