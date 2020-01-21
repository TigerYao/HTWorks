package com.huatu.handheld_huatu.business.matchsmall.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.CourseWorkReportBean;
import com.huatu.handheld_huatu.ui.CenterImageView;
import com.huatu.handheld_huatu.ui.RankTagTextView;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.List;

/**
 * Created by cjx on 2018\8\7 0007.
 */

public class StageRankExplandAdapter extends BaseAdapter {

    public static final int ExplandCount = 3;
    private List<CourseWorkReportBean.ScoreRank> mNamelist;


    private LayoutInflater inflater;

    private int itemCount = ExplandCount;


    private Bitmap mFirstRankIcon, mSecondRankIcon, mThirdRankIcon, mBottomTagIcon;

    public StageRankExplandAdapter(Context context, List<CourseWorkReportBean.ScoreRank> namelist) {
        this.mNamelist = namelist;

        inflater = LayoutInflater.from(context);
        mFirstRankIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_top_icon);
        mSecondRankIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_top2_icon);

        mThirdRankIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_top3_icon);
//        mBottomTagIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_bottom_icon);
        mBottomTagIcon = null;
    }

    public int getRealCount() {
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
            convertView = inflater.inflate(R.layout.stage_rank_listitem, parent, false);

            viewHolder.ImgUserView = convertView.findViewById(R.id.user_avater_img);
            viewHolder.tvOrderName = convertView.findViewById(R.id.order_txt);
            viewHolder.tvUserName = convertView.findViewById(R.id.username_txt);
            viewHolder.tvScore = convertView.findViewById(R.id.tv_score);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time_tip);
            viewHolder.tvCostTime = convertView.findViewById(R.id.tv_costtime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvOrderName.setArrowType(-1);
        int dayNightMode = SpUtils.getDayNightMode();
        String color;
        if (position == 0) {
            viewHolder.ImgUserView.setRankText("").setStrokeColor(0xFFF4AC1A).setCenterImgShow(true, mFirstRankIcon, mBottomTagIcon);
            color = dayNightMode == 0 ? "#F5A623" : "#F5A623";
        } else if (position == 1) {
            viewHolder.ImgUserView.setRankText("").setStrokeColor(0xFF616161).setCenterImgShow(true, mSecondRankIcon, mBottomTagIcon);
            color = dayNightMode == 0 ? "#777777" : "#6B6B6B";
        } else if (position == 2) {
            viewHolder.ImgUserView.setRankText("").setStrokeColor(0xFF925D2E).setCenterImgShow(true, mThirdRankIcon, mBottomTagIcon);
            color = dayNightMode == 0 ? "#B07D50" : "#B07D50";
        } else {
            viewHolder.ImgUserView.setRankText("").setCenterImgShow(false, null, null);
            color = dayNightMode == 0 ? "#7E7E7E" : "#3A3A3A";
        }

        viewHolder.tvCostTime.setTextColor(Color.parseColor(color));
        viewHolder.tvScore.setTextColor(Color.parseColor(color));
        viewHolder.tvTime.setTextColor(Color.parseColor(color));
        viewHolder.tvUserName.setTextColor(Color.parseColor(color));

        CourseWorkReportBean.ScoreRank scoreRank = mNamelist.get(position);
        ImageLoad.displaynoCacheUserAvater(convertView.getContext(), scoreRank.avatar, viewHolder.ImgUserView, R.mipmap.user_default_avater);

        String tmpStr = StringUtils.asItalic(StringUtils.asStrong(scoreRank.rcount)) + "分";
        viewHolder.tvScore.setText(Html.fromHtml(tmpStr));

        viewHolder.tvCostTime.setText(Html.fromHtml(StringUtils.asItalic(StringUtils.asStrong(scoreRank.expendTime+"")) + "分钟"));
        String[] split = scoreRank.submitTimeDes.split("-");
        String tmpStr2 = StringUtils.asItalic(StringUtils.asStrong(split[0])) + "月";
        tmpStr2 = tmpStr2.concat(StringUtils.asItalic(StringUtils.asStrong(split[1])) + "日");
        viewHolder.tvTime.setText(Html.fromHtml(tmpStr2));
        viewHolder.tvUserName.setText(scoreRank.uname);

        viewHolder.tvOrderName.setText(scoreRank.rank + "");
        return convertView;
    }

    class ViewHolder {
        RankTagTextView tvOrderName;
        TextView tvUserName;
        TextView tvScore;
        TextView tvTime, tvCostTime;
        CenterImageView ImgUserView;
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