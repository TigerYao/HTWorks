package com.huatu.handheld_huatu.business.matchsmall.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.exercise.ExerciseReportBean;
import com.huatu.handheld_huatu.ui.CenterImageView;
import com.huatu.handheld_huatu.ui.RankTagTextView;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.List;

public class ExescisRankAdapter extends BaseAdapter {

    public static final int ExplandCount = 3;
    private List<ExerciseReportBean.UserScoreRank> mNamelist;


    private LayoutInflater inflater;

    private int itemCount = ExplandCount;


    private Bitmap mFirstRankIcon, mSecondRankIcon, mThirdRankIcon, mBottomTagIcon;

    public ExescisRankAdapter(Context context, List<ExerciseReportBean.UserScoreRank> namelist) {
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
        com.huatu.handheld_huatu.business.matchsmall.adapter.ExescisRankAdapter.ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new com.huatu.handheld_huatu.business.matchsmall.adapter.ExescisRankAdapter.ViewHolder();
            convertView = inflater.inflate(R.layout.essay_rank_item, parent, false);

            viewHolder.ImgUserView = convertView.findViewById(R.id.user_avater_img);
            viewHolder.tvOrderName = convertView.findViewById(R.id.order_txt);
            viewHolder.tvUserName = convertView.findViewById(R.id.username_txt);
            viewHolder.tvScore = convertView.findViewById(R.id.tv_score);
            viewHolder.tvTime = convertView.findViewById(R.id.tv_time_tip);
            viewHolder.tvCostTime = convertView.findViewById(R.id.tv_costtime);
            ((ViewGroup.MarginLayoutParams)viewHolder.tvOrderName.getLayoutParams()).leftMargin = DisplayUtil.dp2px(10) ;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (com.huatu.handheld_huatu.business.matchsmall.adapter.ExescisRankAdapter.ViewHolder) convertView.getTag();
        }
        String color;
        viewHolder.tvOrderName.setArrowType(-1);
        int dayNightMode = SpUtils.getDayNightMode();

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

        ExerciseReportBean.UserScoreRank scoreRank = (ExerciseReportBean.UserScoreRank) getItem(position);
        ImageLoad.displaynoCacheUserAvater(convertView.getContext(), scoreRank.avatar, viewHolder.ImgUserView, R.mipmap.user_default_avater);

        String tmpStr = StringUtils.asItalic(StringUtils.asStrong(scoreRank.examScore+""));
        viewHolder.tvScore.setText(Html.fromHtml(tmpStr));

        viewHolder.tvCostTime.setText(TimeUtils.getSecond2OnlyMinTime(scoreRank.spendTime) + "分钟");
        String tmpStr2 = scoreRank.submitTime;
        if(!TextUtils.isEmpty(tmpStr2)){
            if(tmpStr2.contains("-")) {
                String[] split = tmpStr2.split("-");
                tmpStr2 = StringUtils.asItalic(StringUtils.asStrong(split[0])) + "月";
                tmpStr2 = tmpStr2.concat(StringUtils.asItalic(StringUtils.asStrong(split[1])) + "日");
            }
            viewHolder.tvTime.setText(Html.fromHtml(tmpStr2));
        }
        viewHolder.tvUserName.setText(scoreRank.userName);

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
