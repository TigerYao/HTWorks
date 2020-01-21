package com.huatu.handheld_huatu.business.play.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.play.bean.HistoryCourseBean;
import com.huatu.handheld_huatu.business.play.bean.HistoryCourseInfo;

import java.util.ArrayList;
import java.util.List;

public class HistroyExpandAdapter extends RecyclerView.Adapter<HistroyExpandAdapter.HistroyViewHolder> {
    public static final int VIEW_TYPE_PARENT = 1;
    public static final int VIEW_TYPE_CHILD = 2;

    HistoryCourseBean historyCourseBean;
    List<HistoryCourseInfo> mDatas;
    private OnItemClickListener itemClickListener;
    private Context ctx;
    private LayoutInflater mInflater;

    public HistroyExpandAdapter(Context context, HistoryCourseBean historyCourseBean) {
        this.historyCourseBean = historyCourseBean;
        this.ctx = context;
        mInflater = LayoutInflater.from(context);
        this.historyCourseBean = historyCourseBean;
        if (historyCourseBean != null)
            fillDatas(historyCourseBean.data, false);
    }

    @NonNull
    @Override
    public HistroyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case VIEW_TYPE_PARENT:
                view = getGroupView(parent);
                break;
            case VIEW_TYPE_CHILD:
                view = getChildView(parent);
                break;
        }
        return new HistroyViewHolder(ctx, view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull final HistroyViewHolder holder, final int position) {
        final HistoryCourseInfo item = mDatas.get(position);
        final int gp = getGroupPosition(position);
        if (item != null && item.isParent()) {
            onBindGroupHolder(holder,item);
            holder.groupView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.isParent()&& item.isExpand()) {
                         collapseGroup(position);
                        notifyRecyclerViewData();
                    } else if (item.hasChilds()){
                        expandGroup(position);
                        notifyRecyclerViewData();
                    }else if (null != itemClickListener) {
                        itemClickListener.onGroupItemClick(item, position,gp, holder.groupView);
                    }
                }
            });
        } else {
            final int cp = getChildPosition(gp, position);
            onBindChildpHolder(holder,item);
            holder.childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != itemClickListener) {
                        itemClickListener.onChildItemClick(item, position, holder.childView);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.get(position).isParent()) {
            return VIEW_TYPE_PARENT;
        } else {
            return VIEW_TYPE_CHILD;
        }
    }

    public void setHistoryCourseBean(HistoryCourseBean bean) {
        this.historyCourseBean = bean;
        notifyRecyclerViewData();
    }

    /**
     * return groupView
     */
    public View getGroupView(ViewGroup parent) {
        return mInflater.inflate(R.layout.layout_history_course_item, parent, false);
    }

    ;

    /**
     * return childView
     */
    public View getChildView(ViewGroup parent) {
        return mInflater.inflate(R.layout.layout_history_lesson_item, parent, false);
    }


    /**
     * onBind groupData to groupView
     *
     * @param holder
     */
    public void onBindGroupHolder(HistroyViewHolder holder, HistoryCourseInfo info){
        holder.tvCourseName.setText(info.classTitle);
        ImageView arrow = holder.itemView.findViewById(R.id.history_course_img);
        if (info.isParent() && info.isExpand() && arrow != null){
            arrow.setImageResource(R.mipmap.history_course_expand);
        }else if (arrow != null){
            arrow.setImageResource(R.mipmap.histroy_course_icon);
        }
    }

    /**
     * onBind childData to childView
     *
     * @param holder
     */
    public void onBindChildpHolder(HistroyViewHolder holder, HistoryCourseInfo childData){
        holder.tvLessonName.setText(childData.lessonTitle);
    }

    /**
     * if return true Allow all expand otherwise Only one can be expand at the same time
     */
    public boolean canExpandAll() {
        return false;
    }


    public void notifyRecyclerViewData() {
        notifyDataSetChanged();
        mDatas.clear();
        fillDatas(historyCourseBean.data, false);
    }

    private void fillDatas(List<HistoryCourseInfo> infos, boolean isChild) {
        if (infos == null || infos.size() <= 0) return;
        if (mDatas == null)
            mDatas = new ArrayList();
        for (int i = 0; i < infos.size(); i++) {
            HistoryCourseInfo info = infos.get(i);
            info.mIsParent = !isChild;
            mDatas.add(info);
            if (info.isParent() && info.hasChilds()) {
                if (info.isExpand())
                    fillDatas(info.childDatas, true);
                else
                    mDatas.removeAll(info.childDatas);
            }
        }
    }

    /**
     * expandGroup
     * @param position showingDatas position
     */
    public void expandGroup(int position) {
        HistoryCourseInfo item = mDatas.get(position);
        if (null == item) {
            return;
        }
        if (!item.isParent()) {
            return;
        }
        if (item.isExpand()) {
            return;
        }
        if(!canExpandAll()){
            for(int i=0;i<mDatas.size();i++){
                if(i != position){
                    int tempPositino = collapseGroup(i);
                    if(tempPositino != -1){
                        position =  tempPositino;
                    }
                }
            }
        }

        List<HistoryCourseInfo> tempChilds;
        if (item.hasChilds()) {
            tempChilds = item.getChildDatas();
            item.onExpand();
            if(canExpandAll()){
                mDatas.addAll(position + 1, tempChilds);
                notifyItemRangeInserted(position+1,tempChilds.size());
                notifyItemRangeChanged(position+1,mDatas.size()-(position+1));
            }else {
                int tempPsi = mDatas.indexOf(item);
                mDatas.addAll(tempPsi + 1, tempChilds);
                notifyItemRangeInserted(tempPsi+1,tempChilds.size());
                notifyItemRangeChanged(tempPsi+1,mDatas.size()-(tempPsi+1));
            }
        }
    }

    /**
     * collapseGroup
     * @param position showingDatas position
     */
    private int collapseGroup(int position) {
        HistoryCourseInfo item = mDatas.get(position);
        if (null == item) {
            return -1;
        }
        if (!item.isParent()) {
            return -1;
        }
        if (!item.isExpand()) {
            return -1;
        }
        int tempSize = mDatas.size();
        List<HistoryCourseInfo> tempChilds;
        if (item.hasChilds()) {
            tempChilds = item.getChildDatas();
            item.onExpand();
            mDatas.removeAll(tempChilds);
            notifyItemRangeRemoved(position+1,tempChilds.size());
            notifyItemRangeChanged(position+1,tempSize-(position+1));
            return position;
        }
        return -1;
    }
    /**
     * @param position showingDatas position
     * @return GroupPosition
     */
    private int getGroupPosition(int position) {
        HistoryCourseInfo item = mDatas.get(position);
        if (item.isParent()) {
            for (int j = 0; j < historyCourseBean.data.size(); j++) {
                if(historyCourseBean.data.get(j).classId.equals(item.classId)){
                    return j;
                }
            }
        }
        return -1;
    }
    /**
     * @param groupPosition
     * @param showDataPosition
     * @return ChildPosition
     */
    private int getChildPosition(int groupPosition, int showDataPosition) {
        HistoryCourseInfo item = mDatas.get(showDataPosition);
        try {
            return historyCourseBean.data.get(groupPosition).childDatas.indexOf(item);
        } catch (IndexOutOfBoundsException ex) {

        }
        return 0;
    }


    class HistroyViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup childView;

        public ViewGroup groupView;
        public TextView tvCourseName;
        public TextView tvLessonName;

        public HistroyViewHolder(Context ctx, View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case VIEW_TYPE_PARENT:
                    groupView = (ViewGroup) itemView.findViewById(getGroupViewResId());
                    break;
                case VIEW_TYPE_CHILD:
                    childView = (ViewGroup) itemView.findViewById(getChildViewResId());
                    break;
            }
            tvCourseName = itemView.findViewById(R.id.history_course_name);
            tvLessonName = itemView.findViewById(R.id.histroy_lession_name);
        }

        /**
         * return ChildView root layout id
         */
        public int getChildViewResId() {
            return R.id.child;
        }

        /**
         * return GroupView root layout id
         */
        public int getGroupViewResId() {
            return R.id.group;
        }

    }

    public interface OnItemClickListener {
        /**
         * position 当前在列表中的position
         */
        void onGroupItemClick(HistoryCourseInfo info, int position, int groupPosition, View view);

        void onChildItemClick(HistoryCourseInfo info, int position ,View view);

    }

}