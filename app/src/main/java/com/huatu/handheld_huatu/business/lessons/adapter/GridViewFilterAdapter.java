package com.huatu.handheld_huatu.business.lessons.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.huatu.handheld_huatu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaodongdong.
 */

public class GridViewFilterAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mData = new ArrayList<>();
    private int mSelectedPosition = 0;
    private int mRememberPosition = 0;
    private boolean isClick = false;

    public GridViewFilterAdapter(Context context) {
        super();
        mContext = context;
    }

    public void setSelected(int position) {
        mSelectedPosition = position;
        isClick = true;
        notifyDataSetChanged();
    }

    public void setData(List<String> data) {
        mData = data;
    }

    public void confirm() {
        mRememberPosition = mSelectedPosition;
        isClick = false;
    }

    public void recover() {
        mSelectedPosition = mRememberPosition;
        isClick = true;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_popup_filter_gv, null);
            holder.item_filter_bt_gv = (Button) convertView.findViewById(R.id.item_filter_bt_gv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_filter_bt_gv.setText(mData.get(position));
        if (isClick) {
            if (mSelectedPosition == position) {
                holder.item_filter_bt_gv.setBackgroundResource(R.drawable.button_bg_click);
                holder.item_filter_bt_gv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                holder.item_filter_bt_gv.setBackgroundResource(R.drawable.button_bg_unclick);
                holder.item_filter_bt_gv.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_dark));
            }
        } else {
            if (mRememberPosition == position) {
                holder.item_filter_bt_gv.setBackgroundResource(R.drawable.button_bg_click);
                holder.item_filter_bt_gv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                holder.item_filter_bt_gv.setBackgroundResource(R.drawable.button_bg_unclick);
                holder.item_filter_bt_gv.setTextColor(ContextCompat.getColor(mContext, R.color.text_color_dark));
            }
        }

        return convertView;
    }

    class ViewHolder {
        Button item_filter_bt_gv;
    }
}
