package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.utils.ArrayUtils;

import java.util.List;

/**
 * Created by cjx on 2018\8\7 0007.
 */

public class ExplandListAdapter extends BaseAdapter {

    public static final int ExplandCount = 3;
    private List<String> mNamelist;

    private List<String> mValuelist;
    private LayoutInflater inflater;

    private int itemCount=ExplandCount;

    public ExplandListAdapter(Context context, List<String> namelist, List<String> valueList) {
        this.mNamelist = namelist;
        this.mValuelist = valueList;

/*        this.mNamelist.add("test");
        this.mNamelist.add("test2");
        this.mValuelist.add("value");
        this.mValuelist.add("value2");*/
        inflater = LayoutInflater.from(context);
    }

    public int getRealCount(){
        return ArrayUtils.size(mNamelist);
    }

    @Override
    public int getCount() {
        // 这里是关键
        // 如果数据数量大于3，只显示3条数据。这里数量自己定义。
        // 否则，显示全部数量。
        if (mNamelist.size() > ExplandCount) {
            return itemCount;
        } else {
            return mNamelist.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mNamelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.play_course_weblist_item, parent, false);
            viewHolder.tvCircleName = (TextView) convertView.findViewById(R.id.name1);
            viewHolder.tvValueName = (TextView) convertView.findViewById(R.id.name1_value);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvCircleName.setText(mNamelist.get(position));
        viewHolder.tvValueName.setText(mValuelist.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tvCircleName;
        TextView tvValueName;
    }

    /**
     * 点击后设置Item的数量
     *
     * @param number
     */
    public void setItemNum(int number) {
        itemCount = number;
    }
}