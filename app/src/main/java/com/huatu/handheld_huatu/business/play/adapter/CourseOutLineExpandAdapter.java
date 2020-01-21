package com.huatu.handheld_huatu.business.play.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineItemBean;
import com.huatu.handheld_huatu.ui.recyclerview.LoadingFooter;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class CourseOutLineExpandAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<CourseOutlineItemBean> list;
    private OnExpandLayoutListener mExpandListener;
    private boolean mShowFootView;
    private LoadingFooter.State mState;
    private boolean hasBuy = false;

    public CourseOutLineExpandAdapter(Context context, List<CourseOutlineItemBean> beans) {
        this.context = context;
        fillDatas(beans);
    }

    public void setHasBuy(boolean hasBuy) {
        this.hasBuy = hasBuy;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == -1)
            viewHolder = new FooterViewHolder(new LoadingFooter(context, false));
        else if (viewType == 0)
            viewHolder = new FirstViewHolder(LayoutInflater.from(context).inflate(R.layout.item_threelayer_outline_layout, parent, false));
        else if (viewType == 1)
            viewHolder = new SecondViewHolder(LayoutInflater.from(context).inflate(R.layout.item_twolayer_outline_layout, parent, false));
        else
            viewHolder = new LastViewHolder(LayoutInflater.from(context).inflate(R.layout.item_onelayer_outline_layout, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == -1 && mState != null) {
            ((FooterViewHolder)holder).setViewState(mState);
            return;
        }
        CourseOutlineItemBean bean = list.get(position);
        switch (getItemViewType(position)) {
            case 0:
                onBindFirstViewHolder((FirstViewHolder) holder, bean, position);
                break;
            case 1:
                onBindSecondViewHolder((SecondViewHolder) holder, bean, position);
                break;
            case 2:
                onBindLastViewHolder((LastViewHolder) holder, bean);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() -1 && mShowFootView){
            return -1;
        }
        return list.get(position).type;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size() + (mShowFootView ? 1 : 0 );
    }

    public void setExpandListener(OnExpandLayoutListener expandListener) {
        this.mExpandListener = expandListener;
    }

    /**
     * if return true Allow all expand otherwise Only one can be expand at the same time
     */
    public boolean canExpandAll() {
        return true;
    }

    private void onBindFirstViewHolder(FirstViewHolder holder, final CourseOutlineItemBean bean, final int position) {
        holder.threelayer_title.setText(bean.title);
        holder.threelayer_video.setText(bean.studyLength);
        holder.threelayer_course.setText(bean.studySchedule + "/" + bean.classHour + "课时");
        holder.threelayer_up_down.setVisibility(bean.hasChildren == 1 ? View.VISIBLE : View.GONE);
        holder.threelayer_up_down.setImageResource(!bean.isExpand ? R.drawable.presale_expand : R.drawable.presale_unexpand);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.hasChildren != 1 || CommonUtils.isFastDoubleClick())
                    return;
                if (bean.hasChilds()){
                    if (bean.isExpand) {
                        collapseGroup(position);
                    }else{
                        expandGroup(position);
                    }
                } else if(!bean.isExpand && mExpandListener != null)
                    mExpandListener.onRequstChild(position,bean);
            }
        });
    }

    private void onBindSecondViewHolder(SecondViewHolder holder, final CourseOutlineItemBean bean, final int position) {
        holder.twolayer_title.setText(bean.title);
        holder.twolayer_course.setText(bean.classHour + "课时");
        holder.twolayer_up_down.setVisibility(bean.hasChildren == 1 ? View.VISIBLE : View.GONE);
        holder.twolayer_up_down.setImageResource(!bean.isExpand ? R.drawable.presale_expand : R.drawable.presale_unexpand);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.hasChildren != 1 || CommonUtils.isFastDoubleClick())
                    return;
                if (bean.hasChilds()){
                    if (bean.isExpand) {
                        collapseGroup(position);
                    }else{
                        expandGroup(position);
                    }
                }else if(!bean.isExpand && mExpandListener != null)
                    mExpandListener.onRequstChild(position, bean);
            }
        });

    }

    private void onBindLastViewHolder(LastViewHolder holder, final CourseOutlineItemBean bean) {
        //填充数据
        holder.onelayer_title.setText(bean.serialNumber + "   " + bean.title);
        holder.onelayer_video.setText(bean.videoLength);
        if (TextUtils.isEmpty(bean.teacher))
            holder.onelayer_teacher.setText("");
        else
            holder.onelayer_teacher.setText("主讲老师：" + bean.teacher);
        if (bean.afterCoreseNum > 0) {
            holder.homework_show.setVisibility(View.VISIBLE);
            holder.homewrok_hint.setText("练习题-" + bean.afterCoreseNum + "道题");
            holder.homework_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtils.cancle();
                    if (bean.subjectType == 2) {
                        ToastUtils.showShortToast(context, "申论课后作业需购买后在【学习】列表对应课程内作答", Gravity.CENTER);
                    }
                }
            });
        } else
            holder.homework_show.setVisibility(View.GONE);
        //非试听课隐藏试听按钮
        if (bean.isTrial == 0) {
            holder.onelayer_shiting.setVisibility(View.GONE);
            holder.onelayer_layout.setOnClickListener(null);
        } else {
            holder.onelayer_shiting.setVisibility(View.VISIBLE);
            holder.onelayer_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bean == null)
                        return;
                    Intent intent = new Intent("action.outline.trial.video");
                    intent.putExtra("CourseBean", bean);
                    context.sendBroadcast(intent);
                }
            });
        }
    }

    private void fillDatas(List<CourseOutlineItemBean> infos) {
        if (infos == null || infos.size() <= 0) return;
        if (list == null)
            list = new ArrayList();
        for (int i = 0; i < infos.size(); i++) {
            CourseOutlineItemBean info = infos.get(i);
            list.add(info);
            if (info.hasChilds()) {
                if (info.isExpand)
                    fillDatas(info.childList);
                else
                    list.removeAll(info.childList);
            }
        }
    }

    public void AddItems(List<CourseOutlineItemBean> infos){
        if(list == null)
            fillDatas(infos);
        else if(infos != null && infos.size() > 0){
            list.addAll(infos);
        }
        int position = getItemCount() -1;
        notifyItemRangeInserted(position, infos.size());
        notifyItemRangeChanged(position, infos.size());
    }

    public List<CourseOutlineItemBean> getList() {
        return list;
    }

    /**
     * expandGroup
     *
     * @param position showingDatas position
     */
    public void expandGroup(int position) {
        CourseOutlineItemBean item = list.get(position);
        if (null == item) {
            return;
        }
        if (item.type == 2) {
            return;
        }
        if (item.isExpand) {
            return;
        }
        if (!canExpandAll()) {
            for (int i = 0; i < list.size(); i++) {
                if (i != position) {
                    int tempPositino = collapseGroup(i);
                    if (tempPositino != -1) {
                        position = tempPositino;
                    }
                }
            }
        }

        List<CourseOutlineItemBean> tempChilds;
        if (item.hasChilds()) {
            tempChilds = item.childList;
            item.isExpand = !item.isExpand;
            if (canExpandAll()) {
                list.addAll(position + 1, tempChilds);
                notifyItemRangeInserted(position + 1, tempChilds.size());
                notifyItemRangeChanged(position, list.size() - position);
            } else {
                int tempPsi = list.indexOf(item);
                list.addAll(tempPsi + 1, tempChilds);
                notifyItemRangeInserted(tempPsi + 1, tempChilds.size());
                notifyItemRangeChanged(tempPsi, list.size() - tempPsi);
            }
        }
    }

    /**
     * collapseGroup
     *
     * @param position showingDatas position
     */
    private int collapseGroup(int position) {
        CourseOutlineItemBean item = list.get(position);
        if (null == item) {
            return -1;
        }
        if (item.type == 2) {
            return -1;
        }
        if (!item.isExpand) {
            return -1;
        }
        int tempSize = list.size();
        List<CourseOutlineItemBean> tempChilds = item.childList;
        if (item.type == 0 && item.hasChilds()){
            for(CourseOutlineItemBean bean : tempChilds){
                if (bean.hasChilds() && bean.isExpand)
                    collapseGroup(position + tempChilds.indexOf(bean) + 1);
            }
        }
        if (item.hasChilds()) {
            list.subList(position, position + tempChilds.size() + 1).removeAll(tempChilds);
            item.isExpand = !item.isExpand;
            notifyItemRangeRemoved(position + 1, tempChilds.size());
            notifyItemRangeChanged(position, list.size() - position);
            return position;
        }
        return -1;
    }

    public class LastViewHolder extends RecyclerView.ViewHolder {
        TextView onelayer_title;
        TextView onelayer_video;
        TextView onelayer_teacher;
        TextView onelayer_shiting;
        TextView homewrok_hint;
        RelativeLayout homework_show;
        RelativeLayout onelayer_layout;

        public LastViewHolder(View view) {
            super(view);
            onelayer_title = (TextView) view.findViewById(R.id.one_layer_title);
            onelayer_video = (TextView) view.findViewById(R.id.one_layer_videoinfo);
            onelayer_teacher = (TextView) view.findViewById(R.id.one_layer_teacherinfo);
            onelayer_shiting = (TextView) view.findViewById(R.id.one_layer_video_shiting);
            homewrok_hint = (TextView) view.findViewById(R.id.homework_description);
            homework_show = (RelativeLayout) view.findViewById(R.id.homework_show);
            onelayer_layout = (RelativeLayout) view.findViewById(R.id.one_layer_layout);
        }
    }

    public class SecondViewHolder extends RecyclerView.ViewHolder {
        TextView twolayer_title;
        TextView twolayer_course;
        ImageView twolayer_up_down;

        public SecondViewHolder(View view) {
            super(view);
            twolayer_title = (TextView) view.findViewById(R.id.two_layer_title);
            twolayer_course = (TextView) view.findViewById(R.id.two_layer_course);
            twolayer_up_down = (ImageView) view.findViewById(R.id.two_layer_up_down);
        }
    }

    public class FirstViewHolder extends RecyclerView.ViewHolder {
        TextView threelayer_title;
        TextView threelayer_video;
        TextView threelayer_course;
        ImageView threelayer_up_down;

        public FirstViewHolder(View view) {
            super(view);
            threelayer_title = (TextView) view.findViewById(R.id.three_layer_title);
            threelayer_course = (TextView) view.findViewById(R.id.three_layer_course_count);
            threelayer_video = (TextView) view.findViewById(R.id.three_layer_video_study);
            threelayer_up_down = (ImageView) view.findViewById(R.id.three_layer_up_down);
        }
    }

    public interface OnExpandLayoutListener{
//        void onExpandLayout(boolean isExpand);
        void onRequstChild(int position, CourseOutlineItemBean parentBean);
    }

    int mAddLastPostion = -1;

    public void setShowFootView(boolean showFootView, LoadingFooter.State state) {
        this.mShowFootView = showFootView;
        mState = state;
        if (showFootView && mAddLastPostion == -1) {
            mAddLastPostion = getItemCount() - 1;
            notifyItemInserted(mAddLastPostion);
        } else if (mAddLastPostion != -1) {
            notifyItemRemoved(mAddLastPostion);
            mAddLastPostion = -1;
        }

    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        LoadingFooter mFootView;
        public FooterViewHolder(View itemView) {
            super(itemView);
            mFootView = (LoadingFooter) itemView;
        }

        public void setViewState(LoadingFooter.State state){
            mFootView.setState(state, mShowFootView);
        }
    }

}
