package com.huatu.handheld_huatu.business.lessons.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zhaodongdong.
 */
public class ListSortAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private int checkItemPosition = 0;

    public void setCheckItem(int position) {
        checkItemPosition = position;
        notifyDataSetChanged();
    }

    public ListSortAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_sort, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        fillValue(position, viewHolder);
        return convertView;
    }

    private void fillValue(int position, ViewHolder viewHolder) {
        viewHolder.mText.setText(list.get(position));
        if (checkItemPosition != -1) {
            if (checkItemPosition == position) {
                viewHolder.mText.setTextColor(ContextCompat.getColor(context, R.color.text_bg_un_click));
                viewHolder.mView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mText.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                viewHolder.mView.setVisibility(View.INVISIBLE);
            }
        }
    }

    static class ViewHolder {
        @BindView(R.id.item_tv_sort_name)
        TextView mText;
        @BindView(R.id.item_img_sort_icon)
        ImageView mView;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
