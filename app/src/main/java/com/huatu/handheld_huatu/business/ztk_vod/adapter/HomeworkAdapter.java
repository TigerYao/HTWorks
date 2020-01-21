package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HomeworkRecombinationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/21
 * <p>
 * 课后作业列表
 */
public class HomeworkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View mView = null;
    private final LayoutInflater mInflater;
    private Context mContext;
    private List<HomeworkRecombinationBean> homeworkRecombinationBeans=new ArrayList<>();

    public HomeworkAdapter(Context mContext,  ItemClickListener itemClickListener) {

        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.itemClickListener = itemClickListener;

    }
    public HomeworkAdapter(Context mContext, List<HomeworkRecombinationBean> homeworkRecombinationBeans, ItemClickListener itemClickListener) {

        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.homeworkRecombinationBeans = homeworkRecombinationBeans;
        this.itemClickListener = itemClickListener;

    }

    public void setHomeworkBeans(List<HomeworkRecombinationBean> homeworkRecombinationBeans) {
        if (this.homeworkRecombinationBeans!=null){
            this.homeworkRecombinationBeans.clear();
            this.homeworkRecombinationBeans .addAll(homeworkRecombinationBeans);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        mView = mInflater.inflate(R.layout.item_homework, viewGroup, false);
        MViewHolder mViewHolderViewPager = new MViewHolder(mView);

        return mViewHolderViewPager;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        MViewHolder holderViewPage = (MViewHolder) viewHolder;

        holderViewPage.viewData(position);

    }

    public class MViewHolder extends RecyclerView.ViewHolder {

        View mViewLine1, mViewLine2;
        RelativeLayout mRlHomeworkClass, mRlCourseDetails;
        TextView mTvClassName, mTvTimes;
        ImageView mIvFold;

        ImageView mIvCircle, mIvExercises;
        TextView mTvNum, mTvTitle, mTvDate, mTvExercises, mTvState;

        public MViewHolder(@NonNull View itemView) {
            super(itemView);

            mViewLine1 = itemView.findViewById(R.id.mViewLine1);
            mViewLine2 = itemView.findViewById(R.id.mViewLine2);
            mRlHomeworkClass = itemView.findViewById(R.id.mRlHomeworkClass);
            mRlCourseDetails = itemView.findViewById(R.id.mRlCourseDetails);
            mTvClassName = itemView.findViewById(R.id.mTvClassName);
            mTvTimes = itemView.findViewById(R.id.mTvTimes);
            mIvFold = itemView.findViewById(R.id.mIvFold);

            mIvCircle = itemView.findViewById(R.id.mIvCircle);
            mIvExercises = itemView.findViewById(R.id.mIvExercises);
            mTvNum = itemView.findViewById(R.id.mTvNum);
            mTvTitle = itemView.findViewById(R.id.mTvTitle);
            mTvDate = itemView.findViewById(R.id.mTvDate);
            mTvExercises = itemView.findViewById(R.id.mTvExercises);
            mTvState = itemView.findViewById(R.id.mTvState);

        }

        public void viewData(final int position) {

            final HomeworkRecombinationBean homeworkRecombinationBean = homeworkRecombinationBeans.get(position);

            //判断每个层级的 横线， 第一个不显示
            if (position == 0) {
                mViewLine1.setVisibility(View.GONE);
            } else {
                mViewLine1.setVisibility(View.VISIBLE);
            }

            //判断是否是1级菜单
            if (homeworkRecombinationBean.type.equals(HomeworkRecombinationBean.CLASS_NAME)) {

                mRlHomeworkClass.setVisibility(View.VISIBLE);
                mRlCourseDetails.setVisibility(View.GONE);
                mViewLine2.setVisibility(View.GONE);

                mTvClassName.setText(homeworkRecombinationBean.courseTitle);
                mTvTimes.setText(homeworkRecombinationBean.undoCount + "次");

                //判断是否是展开，还是收缩
                if (homeworkRecombinationBean.unfold) {
                    mIvFold.setImageResource(R.drawable.htzx_homework_list_shrink);
                } else {
                    mIvFold.setImageResource(R.drawable.htzx_homework_list_unfold);
                }

                //展开/收起 数据，在此点击响应
                mRlHomeworkClass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (homeworkRecombinationBean.unfold) {
                            onClose(homeworkRecombinationBean, position);
                        } else {
                            onOpen(homeworkRecombinationBean, position);
                        }
                    }
                });

                //判断是否是2级菜单
            } else if (homeworkRecombinationBean.type.equals(HomeworkRecombinationBean.STUDY_DETAILS)) {

                mRlHomeworkClass.setVisibility(View.GONE);
                mRlCourseDetails.setVisibility(View.VISIBLE);

                if (homeworkRecombinationBean.index == 1) {
                    mViewLine2.setVisibility(View.GONE);
                } else {
                    mViewLine2.setVisibility(View.VISIBLE);
                }

                if (homeworkRecombinationBean.isAlert == 0) {
                    mIvCircle.setVisibility(View.GONE);
                } else {
                    mIvCircle.setVisibility(View.VISIBLE);
                }

                mTvTitle.setText(homeworkRecombinationBean.courseWareTitle);
                mTvNum.setText(homeworkRecombinationBean.serialNumber + "");

                mTvDate.setText(homeworkRecombinationBean.videoLength);
                mTvExercises.setText("课后作业");
                if (homeworkRecombinationBean.categoryId==1){
                    //行测
                    mTvState.setTextColor(ContextCompat.getColor(mContext,R.color.arena_exam_subject_name_text));
                    if (homeworkRecombinationBean.qcount != homeworkRecombinationBean.ucount) {

                        if (homeworkRecombinationBean.ucount == 0){
                            mTvState.setText("待提交");
                        }else {
                            mTvState.setText("剩余" + homeworkRecombinationBean.ucount + "题");
                        }
                    } else {
                    mTvState.setText("练习题-" + homeworkRecombinationBean.ucount + "道题");
                    }
                }else {
                    //申论 status答题卡状态， 行测不变， 申论使用 答题卡状态 0 未开始 1 未提交 5 被退回
                    if (homeworkRecombinationBean.status==0){
                        mTvState.setText("未开始");
                        mTvState.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                    }else if (homeworkRecombinationBean.status==5){
                        mTvState.setText("被退回");
                        mTvState.setTextColor(ContextCompat.getColor(mContext,R.color.redF3));
                    }else {
                        if (homeworkRecombinationBean.qcount>1){
                            mTvState.setText(homeworkRecombinationBean.fcount+"/"+homeworkRecombinationBean.qcount+"题");
                            mTvState.setTextColor(ContextCompat.getColor(mContext,R.color.pink250));
                        }else {
                            mTvState.setText("未提交");
                            mTvState.setTextColor(ContextCompat.getColor(mContext,R.color.blue5D9));
                        }
                    }
                }

                //展开后的item点击，回调，传给activity响应
                mRlCourseDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClickCourse(v, homeworkRecombinationBean, position);
                    }
                });

            }

        }

        /**
         * 展开，修改ui图标， 对列表的数据修改
         *
         * @param homeworkRecombinationBean
         * @param position
         */
        public void onOpen(HomeworkRecombinationBean homeworkRecombinationBean, int position) {
            if (homeworkRecombinationBean.child != null && homeworkRecombinationBean.child.size() > 0) {
                mIvFold.setImageResource(R.drawable.htzx_homework_list_shrink);
                homeworkRecombinationBeans.addAll(position + 1, homeworkRecombinationBean.child);
                homeworkRecombinationBean.unfold = true;
                notifyItemRangeInserted(position + 1, homeworkRecombinationBean.child.size());
//            notifyItemChanged(position);
                notifyDataSetChanged();
            }
        }

        /**
         * 收起
         * 修改ui图标， 对列表的数据修改
         *
         * @param homeworkRecombinationBean
         * @param position
         */
        public void onClose(HomeworkRecombinationBean homeworkRecombinationBean, int position) {
            if (homeworkRecombinationBean.child != null && homeworkRecombinationBean.child.size() > 0) {
                mIvFold.setImageResource(R.drawable.htzx_homework_list_unfold);
                int nextSameOrHigherLevelNodePosition = homeworkRecombinationBeans.size() - 1;
                if (homeworkRecombinationBeans.size() > position + 1) {
                    closeChild(homeworkRecombinationBeans.get(position));
                    if (nextSameOrHigherLevelNodePosition > position) {
                        homeworkRecombinationBeans.subList(position + 1, homeworkRecombinationBean.child.size() + position + 1).clear();
                        homeworkRecombinationBean.unfold = false;
                        notifyItemRangeRemoved(position + 1, homeworkRecombinationBean.child.size() + position);
//                    notifyItemChanged(position);
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void closeChild(HomeworkRecombinationBean homeworkRecombinationBean) {
        if (homeworkRecombinationBean.child != null) {
            for (HomeworkRecombinationBean child : homeworkRecombinationBean.child) {
                child.unfold = false;
                closeChild(child);
            }
        }
    }

    @Override
    public int getItemCount() {
        return homeworkRecombinationBeans.size();
    }

    /**
     * 对外提给点击的事件
     */
    ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onClickCourse(View view, HomeworkRecombinationBean homeworkRecombinationBean, int position);

//        void setIsRead(boolean isRead);
    }

}
