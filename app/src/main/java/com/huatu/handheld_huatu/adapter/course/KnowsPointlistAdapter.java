package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.CourseWorkReportBean;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;

import java.util.List;
import java.util.Random;

/**
 * Created by cjx on 2018\8\7 0007.
 */

public class KnowsPointlistAdapter extends BaseAdapter {


    private List<CourseWorkReportBean.KnowledgePoint> mNamelist;
    private LayoutInflater inflater;
    Random mColorRandom= new Random();

    int[] colorArr=new int[]{0XFFF8E31C,0XFFFFBC07,0XFFFF8047,0XFFFF6028,0XFFFA314C,0XFFFC5898,0XFFFF3CA7,0XFFFF53EB,0XFFDA5AFF,0XFF9472FD
                       ,0XFF7121FF,0XFF7076FA,0XFF4E8AF9,  0XFF2CBCFF,0XFF19BFA4,0XFF27D46B,0XFF86C935,0XFFC5DF26,0XFFE5ED08};

    public KnowsPointlistAdapter(Context context, List<CourseWorkReportBean.KnowledgePoint> namelist) {
        this.mNamelist = namelist;
        inflater = LayoutInflater.from(context);
     }

    @Override
    public int getCount() {
        return ArrayUtils.size(mNamelist);
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
            convertView = inflater.inflate(R.layout.study_knowledgepoint_listitem, parent, false);
            viewHolder.tvKnowName = (TextView) convertView.findViewById(R.id.know_name_txt);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.title_name_txt);
            viewHolder.tvScore = (TextView) convertView.findViewById(R.id.content_txt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
      //  viewHolder.tvOrderName.setText(mNamelist.get(position));
      //  viewHolder.tvUserName.setText(mValuelist.get(position));


        CourseWorkReportBean.KnowledgePoint knowledgePoint= mNamelist.get(position);
         if(!TextUtils.isEmpty(knowledgePoint.name))
           viewHolder.tvKnowName.setText(String.valueOf(knowledgePoint.name.charAt(0)));

        GradientDrawable drawable = new GradientDrawable();
        //drawable.setColor(colorArr[mColorRandom.nextInt(colorArr.length)]);
        drawable.setColor(colorArr[mColorRandom.nextInt(colorArr.length)]);

        drawable.setCornerRadius(DensityUtils.dp2floatpx(convertView.getContext(),10));
        viewHolder.tvKnowName.setBackground(drawable);

        viewHolder.tvUserName.setText(String.valueOf(knowledgePoint.name));

        //%1$d/%2$d
      /*  String tmpStr=String.format("共%1$s道，答对%2$s道，正确率%3$s%，总计用时%4$s%", StringUtils.fontColor("#4A4A4A",knowledgePoint.qnum)
                              , StringUtils.fontColor("#4A4A4A",knowledgePoint.rnum)
                              , StringUtils.fontColor("#4A4A4A",knowledgePoint.accuracy)
                              , StringUtils.fontColor("#4A4A4A",knowledgePoint.times));*/


        String tmpStr="共".concat(StringUtils.fontColor("#4A4A4A",knowledgePoint.qnum)).concat("道，答对")
                          .concat(StringUtils.fontColor("#4A4A4A",knowledgePoint.rnum)).concat("道，正确率")
                           .concat(StringUtils.fontColor("#4A4A4A",knowledgePoint.accuracy)).concat("%，总计用时")
                           .concat(StringUtils.fontColor("#4A4A4A",knowledgePoint.times)).concat("秒");

         viewHolder.tvScore.setText(StringUtils.forHtml(tmpStr));

        return convertView;
    }

    class ViewHolder {
        TextView tvKnowName;
        TextView tvUserName;
        TextView tvScore;

    }


}