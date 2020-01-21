package com.huatu.handheld_huatu.business.lessons.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.lessons.view.GridViewForMeasure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zhaodongdong.
 */
public class ListFilterAdapter extends BaseAdapter {

    private Context context;
    private String[] filters = new String[]{"考试类型", "直播日期", "价格"};
    private HashMap<String, List<String>> mData;
    private OnFilterSelectListener mSelectListener;
    private boolean ensure = false;

    public ListFilterAdapter(Context context) {
        mData = new HashMap<>();
        String[] types = new String[]{"全部", "公务员", "事业单位", "其他"};
        mData.put(filters[0], Arrays.asList(types));
        String[] date = new String[]{"全部", "未开始", "已开始", "精彩回顾"};
        mData.put(filters[1], Arrays.asList(date));
        String[] prices = new String[]{"全部", "免费", "1-9元", "10-99元", "100-999元", "1000元以上"};
        mData.put(filters[2], Arrays.asList(prices));
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(filters[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_filter, null);
            viewHolder = new ViewHolder(convertView);
        }
        viewHolder.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int item, long id) {
                viewHolder.mFilterAdapter.setSelected(item);
                if (mSelectListener != null) {
                    mSelectListener.onSelected(position, item);
                }
            }
        });
        if (ensure) {
            viewHolder.mFilterAdapter.confirm();
        } else {
            viewHolder.mFilterAdapter.recover();
        }
        viewHolder.mText.setText(filters[position]);
        viewHolder.mFilterAdapter.setData(mData.get(filters[position]));
        viewHolder.mFilterAdapter.notifyDataSetChanged();
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.item_tv_filter_name)
        TextView mText;
        @BindView(R.id.item_popup_filter_gv)
        GridViewForMeasure mGridView;
        GridViewFilterAdapter mFilterAdapter;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            mFilterAdapter = new GridViewFilterAdapter(context);
            mGridView.setAdapter(mFilterAdapter);
            view.setTag(this);
        }
    }

    public void setSelectListener(OnFilterSelectListener selectListener) {
        mSelectListener = selectListener;
    }

    public interface OnFilterSelectListener {
        void onSelected(int parent, int child);
    }

    public void setEnsure(boolean ensure) {
        this.ensure = ensure;
        notifyDataSetChanged();
    }
}
