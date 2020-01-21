package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.bean.VodCoursePlayBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht-djd on 2017/9/14.
 *
 */

public class VodCourseDownLoadListAdapter extends BaseAdapter {
    private ArrayList<VodCoursePlayBean.LessionBean> mVodcoursePlayList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater inflater;
    private VodCoursePlayBean.LessionBean lessionBean;

    public VodCourseDownLoadListAdapter(Context context, ArrayList<VodCoursePlayBean.LessionBean> mVodcoursePlayList) {
        this.mContext = context;
        this.mVodcoursePlayList = mVodcoursePlayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mVodcoursePlayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVodcoursePlayList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        lessionBean = mVodcoursePlayList.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_record, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_record_lesson_name.setText(lessionBean.Title);
        holder.tv_record_order
                .setText(mContext.getResources().getString(
                        R.string.record_down_lesson_order,
                        mVodcoursePlayList.size() - position));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.iv_record_status
                        .setImageResource(R.drawable.record_down_unplay);
               down(lessionBean.Title,lessionBean.cc_key,lessionBean.cc_uid,lessionBean.cc_vid,lessionBean.rid,position);
            }



        });
        return convertView;
    }
    private void down(String title, String cc_key, String cc_uid, String cc_vid, String rid, int position) {

    }
    class ViewHolder {
        @BindView(R.id.tv_record_order)
        TextView tv_record_order;
        @BindView(R.id.iv_record_status)
        ImageView iv_record_status;
        @BindView(R.id.tv_record_lesson_name)
        TextView tv_record_lesson_name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }
}
