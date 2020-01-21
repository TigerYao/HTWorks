package com.huatu.handheld_huatu.ui.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.ArrayUtils;

import java.util.List;

/**
 *  字母排序适配器基类
 * @param <T>
 */
public abstract class LetterSortAdapter<T extends LetterSortAdapter.LetterSortBean> extends RecyclerView.Adapter<LetterSortAdapter.ViewHolder> implements
        StickyHeaderAdapter<LetterSortAdapter.HeaderHolder> {
    private Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int i;

    public LetterSortAdapter(Context mContext, List<T> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

 /*   @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_medicine, parent, false));
    }*/

   /* @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LetterSortBean LetterSortBean = mDatas.get(position);
        holder.tvName.setText(LetterSortBean.getName());
    }*/

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public String getHeaderId(int position) {
        //这里可能会底部的加载更多item
        int size= ArrayUtils.size(mDatas);
        if(position<size)  return mDatas.get(position).getLetter();
        return mDatas.get(size-1).getLetter();
    }

     @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
       // return new HeaderHolder(mInflater.inflate(R.layout.item_decoration, parent, false));
         return null;
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        viewholder.header.setText(mDatas.get(position).getLetter());
    }

    /**
     * 根据分类的首字母获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(String section) {
        for (int i = 0; i < mDatas.size(); i++) {
            String sortStr = mDatas.get(i).getLetter();
            if (sortStr.equals(section)) {
                return i;
            }
        }
        return -1;
    }

     public static class ViewHolder extends RecyclerView.ViewHolder {
        //TextView tvName;

        public ViewHolder(View itemView) {
            super(itemView);
          //  tvName = (TextView) itemView.findViewById(R.id.name);
        }
    }
    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView header;

        public HeaderHolder(View itemView) {
            super(itemView);
            header = (TextView) itemView;
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showShort("test");
                }
            });
        }
    }


    public static class LetterSortBean {

        public String letter;

        public String getLetter() {
            return letter;
        }

        public void setLetter(String letter) {
            this.letter = letter;
        }
    }

}
