package com.huatu.handheld_huatu.adapter.course;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseMineBean;
import com.huatu.handheld_huatu.ui.recyclerview.LetterSortAdapter;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by cjx on 2018\6\29 0029.
 */
@Deprecated
public class MyCourselistAdapter extends LetterSortAdapter<CourseMineBean.ResultBean> {


    private Context mContext;
    private List<CourseMineBean.ResultBean> mLessons;

    private OnRecItemClickListener onRecyclerViewItemClickListener;

    public void setOnRecyclerViewItemClickListener(OnRecItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public MyCourselistAdapter(Context context, List<CourseMineBean.ResultBean> lessons) {
        super(context,lessons);
        this.mContext = context;
        this.mLessons = lessons;
    }

    public void clear() {
        if (mLessons != null) mLessons.clear();
    }

    public CourseMineBean.ResultBean getCurrentItem(int position) {
        if (position >= 0 && position < ArrayUtils.size(mLessons))
            return mLessons.get(position);
        return null;
    }

    @Override
    public int getItemCount() {
        return ArrayUtils.size(mLessons);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_mine_list_item, parent, false);
            return new ViewHolderTwo(collectionView);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
    /*    switch (getItemViewType(position)) {
            case 0:
                final RecordingCourseAdapter.ViewHolderOne holdermult = (RecordingCourseAdapter.ViewHolderOne) holder;
                holdermult.bindUI(mLessons.get(position));
                break;
            case 1:
                RecordingCourseAdapter.ViewHolderTwo holderfour = (RecordingCourseAdapter.ViewHolderTwo) holder;
                holderfour.bindUI(mLessons.get(position));
                break;
        }*/

        ViewHolderTwo holderfour = (ViewHolderTwo) holder;
        holderfour.bindUI(mLessons.get(position));
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderHolder(mInflater.inflate(R.layout.course_letter_item_decoration, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder viewholder, int position) {
        viewholder.header.setText(mDatas.get(position).getLetter());
    }

    protected class ViewHolderTwo    extends LetterSortAdapter.ViewHolder  {

        @BindView(R.id.tv_item_course_mine_title)
        TextView mTitle;

        @BindView(R.id.iv_item_course_mine_gq)
        ImageView mOutDateImg;

        @BindView(R.id.tv_item_course_mine_timelength)
        TextView mTimeLength;

        @BindView(R.id.iv)
        ImageView mIsliveImg;

        @BindView(R.id.iv_item_course_mine_scaleimg)
        ImageView mCoverImg;

        @BindView(R.id.tv_item_course_mine_one_to_one)
        TextView mInfoCard;

        ViewHolderTwo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mInfoCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               /*     if (mineItem.oneToOne == 1) {
                        showOneToOneDlg(mineItem);
                    } else if (mineItem.oneToOne == 2) {
                        startOneToOne(mineItem, false);
                    } else if (Method.isEqualString("1",mineItem.isMianshou)) {
                        if (TextUtils.isEmpty(mineItem.treatyUrl)){
                            return;
                        }
                        HuaTuXieYiActivity.newIntent(getActivity().getApplicationContext()
                                , mineItem.treatyUrl);
                    }*/

                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_MORE);
                }
            });
            itemView.findViewById(R.id.whole_content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onRecyclerViewItemClickListener!=null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(),v, EventConstant.EVENT_ALL);
                }
            });
        }

        public void bindUI(CourseMineBean.ResultBean mineItem){
            //显示数据

            mTitle.setText(mineItem.title);
            //是否过期
            if (mineItem.isStudyExpired == 1) {
                mOutDateImg.setVisibility(View.VISIBLE);
            } else {
                mOutDateImg.setVisibility(View.GONE);
            }
            if (mineItem.courseType == 0) {
                //录播课的购课详情页，和我的课程列表，如果是录播课，不显示时间了，只显示课时哈
                mTimeLength.setText( mineItem.TimeLength);
            } else if (TextUtils.isEmpty(mineItem.endDate)) {
                mTimeLength.setText( mineItem.startDate + " (" + mineItem.TimeLength + ")");
            } else {
                mTimeLength.setText( mineItem.startDate  + "-" + mineItem.endDate + " (" + mineItem.TimeLength + ")");
            }
            if (mineItem.iszhibo == 1) {
                //是否为今日直播课
                mIsliveImg.setVisibility( View.VISIBLE);
            } else {
                mIsliveImg.setVisibility( View.GONE);
            }

            ImageLoad.displaynoCacheImage(mContext,R.mipmap.load_default,mineItem.scaleimg,mCoverImg);

            if (mineItem.oneToOne == 1) {
                mInfoCard.setVisibility(View.VISIBLE);
                mInfoCard.setTextColor(ResourceUtils.getColor(R.color.main_color)) ;
                mInfoCard.setText("填写信息卡");
            } else if (mineItem.oneToOne == 2) {

                mInfoCard.setVisibility(View.VISIBLE);
                mInfoCard.setTextColor(ResourceUtils.getColor(R.color.course_center_content)) ;
                mInfoCard.setText("查看信息卡");


            } else if (Method.isEqualString("1",mineItem.isMianshou)) {

                mInfoCard.setVisibility(View.VISIBLE);
                mInfoCard.setTextColor(ResourceUtils.getColor(R.color.course_center_content)) ;
                mInfoCard.setText("查看协议");


            } else {
                mInfoCard.setVisibility(View.GONE);
             }

        }

    }

}