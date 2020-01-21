package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.utils.SpUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht on 2017/5/11.
 */

public class TestAdapter extends BaseAdapter {
    private List<String> mList;
    private Context mContext;
    private LayoutInflater inflater;
    private int defaultSelection = SpUtils.getTestUrlPosition();

    public TestAdapter(List urlList, Context context) {
        this.mContext = context;
        this.mList = urlList;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_activity_test, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textUrl.setText(mList.get(position));

        if (defaultSelection==position) {
            holder.imgSame.setVisibility(View.VISIBLE);
        } else {
            holder.imgSame.setVisibility(View.GONE);

        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_item_test_url)
        TextView textUrl;
        @BindView(R.id.tv_same)
        ImageView imgSame;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }

    }
    public void setSelectPosition(int position) {
        if(position != defaultSelection){
            defaultSelection = position;
            notifyDataSetChanged();
        }
    }

}
