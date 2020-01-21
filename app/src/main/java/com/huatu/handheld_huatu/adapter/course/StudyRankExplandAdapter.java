package com.huatu.handheld_huatu.adapter.course;

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
import com.huatu.handheld_huatu.utils.DateUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;


import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by cjx on 2018\8\7 0007.
 */

public class StudyRankExplandAdapter extends BaseAdapter {

    public static final int ExplandCount = 3;
    private List<CourseWorkReportBean.ScoreRank> mNamelist;

    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH:mm");
    private LayoutInflater inflater;

    private int itemCount=ExplandCount;


    private Bitmap mFirstRankIcon,mSecondRankIcon,mThirdRankIcon,mBottomTagIcon;

    public StudyRankExplandAdapter(Context context, List<CourseWorkReportBean.ScoreRank> namelist) {
        this.mNamelist = namelist;

        inflater = LayoutInflater.from(context);
        mFirstRankIcon=  BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_top_icon);
        mSecondRankIcon=BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_top2_icon);

        mThirdRankIcon=BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_top3_icon);
        mBottomTagIcon=  BitmapFactory.decodeResource(context.getResources(), R.mipmap.study_rank_bottom_icon);
    }

    public int getRealCount(){
        return ArrayUtils.size(mNamelist);
    }

    public  String getFormat(int timeCost){
        String formatTime=timeCost<60? "1":Math.round(((float)timeCost)/60)+"";
        return formatTime;
    }

    @Override
    public int getCount() {
        // 这里是关键
        // 如果数据数量大于3，只显示3条数据。这里数量自己定义。
        // 否则，显示全部数量。
        if (getRealCount() > ExplandCount) {
            return itemCount;
        } else {
            return getRealCount();
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
            convertView = inflater.inflate(R.layout.course_studyrank_listitem, parent, false);

            viewHolder.ImgUserView=(CenterImageView) convertView.findViewById(R.id.user_avater_img);
            viewHolder.tvOrderName = (RankTagTextView) convertView.findViewById(R.id.order_txt);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.username_txt);
            viewHolder.tvScore = (TextView) convertView.findViewById(R.id.tv_score);
            viewHolder.tvTime=(TextView)convertView.findViewById(R.id.tv_time_tip) ;
            viewHolder.tvCostTime=(TextView)convertView.findViewById(R.id.tv_costtime) ;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
      //  viewHolder.tvOrderName.setText(mNamelist.get(position));
      //  viewHolder.tvUserName.setText(mValuelist.get(position));
        int color=0XFF7E7E7E;
        if(position==0){
            color=0XFFF5A623;
            viewHolder.tvOrderName.setArrowType(-1);
            viewHolder.ImgUserView.setRankText("").setStrokeColor(0xFFF4AC1A).setCenterImgShow(true,mFirstRankIcon,null);
        }
         else if(position==1){
            color=0XFF777777;
            viewHolder.tvOrderName.setArrowType(-1);
            viewHolder.ImgUserView.setRankText("").setStrokeColor(0xFF616161).setCenterImgShow(true,mSecondRankIcon,null);
        }else if(position==2){
            color=0XFFB07D50;
            viewHolder.tvOrderName.setArrowType(-1);
            viewHolder.ImgUserView.setRankText("").setStrokeColor(0xFF925D2E).setCenterImgShow(true,mThirdRankIcon,null);
        } else {
            color=0XFF7E7E7E;
            viewHolder.tvOrderName.setArrowType(-1);
            viewHolder.ImgUserView.setRankText("").setCenterImgShow(false,null,null);
        }

        viewHolder.tvCostTime.setTextColor(color);
        viewHolder.tvScore.setTextColor(color);
        viewHolder.tvTime.setTextColor(color);
        viewHolder.tvUserName.setTextColor(color);

        CourseWorkReportBean.ScoreRank scoreRank= mNamelist.get(position);
        ImageLoad.displaynoCacheUserAvater(convertView.getContext(),scoreRank.avatar,viewHolder.ImgUserView,R.mipmap.user_default_avater);

        //文案效果3  多色效果，并且加粗

         String tmpStr= StringUtils.asItalic(StringUtils.asStrong(scoreRank.rcount)) +"题";
         viewHolder.tvScore.setText(Html.fromHtml(tmpStr));

         viewHolder.tvCostTime.setText(Html.fromHtml(StringUtils.asItalic(StringUtils.asStrong(getFormat(scoreRank.expendTime))) +"分钟"));
        viewHolder.tvUserName.setText(scoreRank.uname);
        viewHolder.tvOrderName.setText(scoreRank.rank + "");

         if(scoreRank.submitTimeInfo>0){
             try{
                 String[] split = DateUtils.getFormatData(sdf,scoreRank.submitTimeInfo).split("-");
                 String tmpStr2 = StringUtils.asItalic(StringUtils.asStrong(split[0])) + "月";
                 tmpStr2 = tmpStr2.concat(StringUtils.asItalic(StringUtils.asStrong(split[1])) + "日");
                                  //.concat(StringUtils.asItalic(StringUtils.asStrong(split[2]))+ " ");
                 viewHolder.tvTime.setText(Html.fromHtml(tmpStr2));

             }catch (Exception e){}
        }

     /*    String tmpStr2= StringUtils.asItalic(StringUtils.asStrong("1")) +"月";
          tmpStr2=tmpStr2.concat(StringUtils.asItalic(StringUtils.asStrong("6")) +"日");
          tmpStr2=tmpStr2.concat(StringUtils.asItalic(StringUtils.asStrong(" 22:10")));
         viewHolder.tvTime.setText(Html.fromHtml(tmpStr2));
         viewHolder.tvUserName.setText(scoreRank.uname);

         viewHolder.tvOrderName.setText(String.valueOf(position+1));*/
         return convertView;
    }

    class ViewHolder {
        RankTagTextView tvOrderName;
        TextView tvUserName;
        TextView tvScore;
        TextView tvTime,tvCostTime;

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