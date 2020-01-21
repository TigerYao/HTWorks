package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.HandoutClickListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.utils.FileUtil;

import java.util.List;

public class HandoutAdapter extends BaseAdapter {
    private int courseId;
    private List<HandoutBean.Course> courseList;
    private Context context;
    private HandoutClickListener listener;

    public HandoutAdapter(int courseId, List<HandoutBean.Course> courseList, Context context) {
        this.courseList = courseList;
        this.courseId = courseId;
        this.context = context;
    }

    public void setOnHandoutClickListener(HandoutClickListener listener) {
        this.listener = listener;
    }

    public void setCourseList(List<HandoutBean.Course> courseList) {
        this.courseList = courseList;
    }

    @Override
    public int getCount() {
        if (null != courseList && courseList.size() > 0) {
            return courseList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return courseList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        HandoutHolder holder;
        if (view == null) {
            holder = new HandoutHolder();

            view = LayoutInflater.from(context).inflate(R.layout.item_handout, null);
            holder.image_icon = (ImageView) view.findViewById(R.id.image_icon);
            holder.text_title = (TextView) view.findViewById(R.id.text_title);
//            holder.text_time = (TextView) view.findViewById(R.id.text_time);
            holder.ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);
            holder.image_down = (ImageView) view.findViewById(R.id.image_down);
            holder.rl_delete = (RelativeLayout) view.findViewById(R.id.rl_delete);
            holder.text_size = (TextView) view.findViewById(R.id.text_size);

            view.setTag(holder);
        } else {
            holder = (HandoutHolder) view.getTag();
        }

        final HandoutBean.Course course = courseList.get(i);
        String downloadUrl = "";
        String title = "";
        String pubDate = "";
        String substring = "";
        if (course != null) {
            downloadUrl = course.downloadurl;
            title = course.title;
            pubDate = course.pubDate;
            if (!TextUtils.isEmpty(downloadUrl)) {
                substring = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
            }
        }
        String newName = courseId + "_" + course.id + "_" + substring;
        if (FileUtil.isFileExist(FileUtil.getFilePath("handout", newName))) {
            holder.image_down.setVisibility(View.GONE);
            holder.rl_delete.setVisibility(View.VISIBLE);
            String handout = FileUtil.getFileSize(FileUtil.getFilePath("handout", newName));
            holder.text_size.setText(handout);
        } else {
            holder.image_down.setVisibility(View.VISIBLE);
            holder.rl_delete.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(downloadUrl)) {
            if (downloadUrl.endsWith(".pdf")) {
                holder.image_icon.setBackgroundResource(R.drawable.image_note_pdf);
            } else if (downloadUrl.endsWith(".pptx") || downloadUrl.endsWith(".ppt")) {
                holder.image_icon.setBackgroundResource(R.drawable.image_note_ppt);
            } else {
                holder.image_icon.setBackgroundResource(R.drawable.image_note_word);
            }
        } else {
            holder.image_icon.setBackgroundResource(R.drawable.image_note_pdf);
        }

        if (!TextUtils.isEmpty(title)) {
            holder.text_title.setText(title);
        } else {
            holder.text_title.setText("");
        }

//        if (!TextUtils.isEmpty(pubDate)) {
//            holder.text_time.setText(pubDate);
//        } else {
//            holder.text_time.setText("");
//        }

        holder.image_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.downLoadHandout(i);
                }
            }
        });

        holder.rl_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.deleteHandout(i);
                }
            }
        });
        return view;
    }

    class HandoutHolder {
        ImageView image_icon;
        TextView text_title;
//        TextView text_time;
        LinearLayout ll_delete;
        ImageView image_down;
        RelativeLayout rl_delete;
        TextView text_size;
    }
}
