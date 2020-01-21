package com.huatu.handheld_huatu.business.ztk_vod.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.ztk_vod.bean.HomeworkRecombinationBean;
import com.huatu.handheld_huatu.business.ztk_vod.bean.SectionExamRecombinBean;

import com.huatu.handheld_huatu.utils.CommonUtils;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/2/21
 * <p>
 * 阶段考试列表
 */
public class SectionalExaminationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View mView = null;
    private final LayoutInflater mInflater;
    private Context mContext;
    private List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans;

    private int indexCount = 0;

    public SectionalExaminationAdapter(Context mContext, List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans, ItemClickListener itemClickListener) {

        mInflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.sectionalExaminationRecombinationBeans = sectionalExaminationRecombinationBeans;
        this.itemClickListener = itemClickListener;

    }

    public void setHomeworkBeans(List<SectionExamRecombinBean> sectionalExaminationRecombinationBeans) {
        this.sectionalExaminationRecombinationBeans = sectionalExaminationRecombinationBeans;
    }

    public void clearAndRefresh() {
        this.sectionalExaminationRecombinationBeans.clear();
        this.notifyDataSetChanged();
    }

    public final SectionExamRecombinBean getItem(int position) {
        int p = position;
        if (p < 0 || p >= sectionalExaminationRecombinationBeans.size())
            return null;
        return sectionalExaminationRecombinationBeans.get(position);
    }

    public List<SectionExamRecombinBean> getAllItems(){
        return sectionalExaminationRecombinationBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        mView = mInflater.inflate(R.layout.item_sectionalexamination, viewGroup, false);
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
        TextView mTvNum, mTvTitle, mTvDate, mTvState;

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
            mTvState = itemView.findViewById(R.id.mTvState);

        }

        public void viewData(final int position) {

            final SectionExamRecombinBean sectionalExaminationRecombinationBean = sectionalExaminationRecombinationBeans.get(position);

            if (position == 0) {
                mViewLine1.setVisibility(View.GONE);
            } else {
                mViewLine1.setVisibility(View.VISIBLE);
            }

            //判断是否是展开，还是收缩
            if (sectionalExaminationRecombinationBean.unfold) {
                mIvFold.setImageResource(R.drawable.htzx_homework_list_shrink);
            } else {
                mIvFold.setImageResource(R.drawable.htzx_homework_list_unfold);
            }

            //判断是否是1级菜单
            if (sectionalExaminationRecombinationBean.type.equals(HomeworkRecombinationBean.CLASS_NAME)) {

                indexCount = sectionalExaminationRecombinationBean.child.size();

                mRlHomeworkClass.setVisibility(View.VISIBLE);
                mRlCourseDetails.setVisibility(View.GONE);
                mViewLine2.setVisibility(View.GONE);

                mTvClassName.setText(sectionalExaminationRecombinationBean.className);
                mTvTimes.setText(sectionalExaminationRecombinationBean.undoCount + "");

                mRlHomeworkClass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sectionalExaminationRecombinationBean.unfold) {
                            onClose(sectionalExaminationRecombinationBean, position);
                        } else {
                            onOpen(sectionalExaminationRecombinationBean, position);
                        }
                    }
                });
                //2级判断
            } else if (sectionalExaminationRecombinationBean.type.equals(HomeworkRecombinationBean.STUDY_DETAILS)) {

                mRlHomeworkClass.setVisibility(View.GONE);
                mRlCourseDetails.setVisibility(View.VISIBLE);

                if (indexCount == sectionalExaminationRecombinationBean.index) {
                    if (position == sectionalExaminationRecombinationBeans.size() -1){
                        mViewLine2.setVisibility(View.VISIBLE);
                    }else {
                        mViewLine2.setVisibility(View.GONE);
                    }
                } else {
                    mViewLine2.setVisibility(View.VISIBLE);
                }

                if (sectionalExaminationRecombinationBean.isAlert == 0) {
                    mIvCircle.setVisibility(View.GONE);
                } else {
                    mIvCircle.setVisibility(View.VISIBLE);
                }

                mTvTitle.setText(sectionalExaminationRecombinationBean.examName);
                mTvNum.setText(sectionalExaminationRecombinationBean.coursewareNum + "");

                if (CommonUtils.date_compare(sectionalExaminationRecombinationBean.startTime, sectionalExaminationRecombinationBean.endTime)) {
                    mTvDate.setText(CommonUtils.date_transform(sectionalExaminationRecombinationBean.startTime, "MM月dd日 HH:mm") + "-"
                            + CommonUtils.date_transform(sectionalExaminationRecombinationBean.endTime, "HH:mm"));
                }else{
                    mTvDate.setText(CommonUtils.date_transform(sectionalExaminationRecombinationBean.startTime, "MM月dd日 HH:mm") + "-"
                            + CommonUtils.date_transform(sectionalExaminationRecombinationBean.endTime, "MM月dd日 HH:mm"));
                }

                mTvState(sectionalExaminationRecombinationBean.status);

                mRlCourseDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClickCourse(v, sectionalExaminationRecombinationBean, position);
                    }
                });

            }
        }

        /**
         * 阶段测试状态 1未开始 2开始考试 5继续考试 6查看报告
         *
         * @param status
         */
        public void mTvState(int status) {

            switch (status) {
                case 1:
                    mTvState.setText("未开始");
                    mTvState.setTextColor(mContext.getResources().getColor(R.color.arena_exam_subject_name_text));
                    break;
                case 2:
                    mTvState.setText("开始考试");
                    mTvState.setTextColor(mContext.getResources().getColor(R.color.arena_exam_analysis_correct_text));
                    break;
                case 5:
                    mTvState.setText("继续作答");
                    mTvState.setTextColor(mContext.getResources().getColor(R.color.arena_exam_analysis_easy_wrong_text));
                    break;
                case 6:
                    mTvState.setText("考试报告");
                    mTvState.setTextColor(mContext.getResources().getColor(R.color.arena_answer_card_submit_bg));
                    break;
                default:
                    mTvState.setText("未开始");
                    mTvState.setTextColor(mContext.getResources().getColor(R.color.arena_exam_subject_name_text));
                    break;
            }

        }


        /**
         * 展开，修改ui图标， 对列表的数据修改
         *
         * @param sectionalExaminationRecombinationBean
         * @param position
         */
        public void onOpen(SectionExamRecombinBean sectionalExaminationRecombinationBean, int position) {
            if (sectionalExaminationRecombinationBean.child != null && sectionalExaminationRecombinationBean.child.size() > 0) {
                mIvFold.setImageResource(R.drawable.htzx_homework_list_unfold);
                sectionalExaminationRecombinationBeans.addAll(position + 1, sectionalExaminationRecombinationBean.child);
                sectionalExaminationRecombinationBean.unfold = true;
                notifyItemRangeInserted(position + 1, sectionalExaminationRecombinationBean.child.size());
//            notifyItemChanged(position);
                notifyDataSetChanged();
            }
        }

        /**
         * 收起
         * 修改ui图标， 对列表的数据修改
         *
         * @param sectionalExaminationRecombinationBean
         * @param position
         */
        public void onClose(SectionExamRecombinBean sectionalExaminationRecombinationBean, int position) {
            if (sectionalExaminationRecombinationBean.child != null && sectionalExaminationRecombinationBean.child.size() > 0) {
                mIvFold.setImageResource(R.drawable.htzx_homework_list_shrink);
                int nextSameOrHigherLevelNodePosition = sectionalExaminationRecombinationBeans.size() - 1;
                if (sectionalExaminationRecombinationBeans.size() > position + 1) {
                    closeChild(sectionalExaminationRecombinationBeans.get(position));
                    if (nextSameOrHigherLevelNodePosition > position) {
                        sectionalExaminationRecombinationBeans.subList(position + 1, sectionalExaminationRecombinationBean.child.size() + position + 1).clear();
                        sectionalExaminationRecombinationBean.unfold = false;
                        notifyItemRangeRemoved(position + 1, sectionalExaminationRecombinationBean.child.size() + position);
//                    notifyItemChanged(position);
                        notifyDataSetChanged();
                    }
                }
            }
        }
    }


    private void closeChild(SectionExamRecombinBean sectionalExaminationRecombinationBean) {
        if (sectionalExaminationRecombinationBean.child != null) {
            for (SectionExamRecombinBean child : sectionalExaminationRecombinationBean.child) {
                child.unfold = false;
                closeChild(child);
            }
        }
    }

    @Override
    public int getItemCount() {
        return sectionalExaminationRecombinationBeans.size();
    }

    /**
     * 对外提给点击的事件
     */
    ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onClickCourse(View view, SectionExamRecombinBean sectionalExaminationRecombinationBean, int position);
    }

}
