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
import com.huatu.utils.StringUtils;

import java.util.List;

/**
 * Created by ht on 2017/11/23.
 */
public class StyleScoreListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<CheckDetailBean.ScoreListEntity> mData;
    Typeface mTypeface;

    public StyleScoreListAdapter(Context context, List<CheckDetailBean.ScoreListEntity> listdata) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mData =listdata;
        mTypeface= Typeface.createFromAsset(context.getAssets(), "font/Heavy.ttf");
        ;
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
            convertView = mInflater.inflate(R.layout.item_essay_check_style_score, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CheckDetailBean.ScoreListEntity data = mData.get(position);
        holder.tvContent.setText("(" + (position + 1) + ")  (" + data.scorePoint + ")");
        if (data.type != 3) {
            holder.tvScore.setText(("+" + data.score + "分").replace(".0", ""));
            holder.tvScore.setBackgroundResource(R.drawable.ess_check_score_content_bg);
        } else {
            holder.tvScore.setText(("-" + data.score + "分").replace(".0", ""));
            holder.tvScore.setBackgroundResource(R.drawable.ess_check_score_style_bg);
        }
        return convertView;
    }

      class ViewHolder {

        TextView tvScore;
        TextView tvContent;

        public ViewHolder(View itemView) {
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvScore = (TextView) itemView.findViewById(R.id.tv_score);
            tvContent.setTypeface(mTypeface);

        }
    }

}
