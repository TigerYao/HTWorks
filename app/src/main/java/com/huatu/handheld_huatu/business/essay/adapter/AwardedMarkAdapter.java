package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.utils.StringUtils;

import java.util.List;

/**
 * Created by ht on 2017/11/23.
 */
public class AwardedMarkAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<CheckDetailBean.ScoreListEntity> mData;
    Typeface mTypeface;

    public AwardedMarkAdapter(Context context, List<CheckDetailBean.ScoreListEntity> listdata, Typeface typeface) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mData =listdata;
        mTypeface=typeface;
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
            convertView = mInflater.inflate(R.layout.essay_awardedmark_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CheckDetailBean.ScoreListEntity curInfo=mData.get(position);
        holder.tvTag.setText(String.valueOf(curInfo.sequenceNumber));

        holder.tvContent.setTypeface(mTypeface);


        holder.tvContent.setText(StringUtils.forHtml(curInfo.scorePoint+ (curInfo.score==0? "":"   "+StringUtils.fontColor("#FF6D73",(curInfo.score>0 ? "+":"")+ CommonUtils.formatScore(curInfo.score)+"分")) ));
        return convertView;
    }



    static class ViewHolder {

        TextView tvTag;
        TextView tvContent;

        public ViewHolder(View view) {
            tvTag=view.findViewById(R.id.tag_txt);
            tvContent=view.findViewById(R.id.content_txt);

        }
    }

}
