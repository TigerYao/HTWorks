package com.huatu.handheld_huatu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.mvpmodel.UserMessageBean;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;

import java.util.List;

/**
 * Created by cjx on 2018\8\4 0004.
 */

public class MessageListAdapter extends SimpleBaseRecyclerAdapter<UserMessageBean> {

    public static final int ONLINE = 1;//
    public static final int MOCK = 2;//
    public static final int LOGISTIC = 3;
    public static final int CORRECTION = 4;
    public static final int ESSAYREPORT = 5; //申论批改
    public static final int COURSEHOMEWORK = 6; //课后作业批改
    private OnDeleteListener mOnDeleteListener;

    public interface OnDeleteListener {
        void onDeleteClick(int position);
    }

    public MessageListAdapter(Context context, List<UserMessageBean> items, OnDeleteListener mOnDeleteListener) {
        super(context, items);
        this.mOnDeleteListener = mOnDeleteListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ONLINE) {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.message_onlive_time_item, parent, false);
            return new OnLiveViewHolder(textView);
        } else if (viewType == COURSEHOMEWORK){
            View textView = LayoutInflater.from(mContext).inflate(R.layout.message_onlive_time_item, parent, false);
            return new OnLiveViewHolder(textView);
        } else if (viewType == MOCK) {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.message_mockexam_timeitem, parent, false);
            return new MockexamHolder(textView);
        } else if (viewType == LOGISTIC) {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.message_logistic_timeitem, parent, false);
            return new LogisticHolder(textView);
        } else if (viewType == ESSAYREPORT) {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.message_essay_report_item, parent, false);
            return new BaseHolder(textView);
        } else {
            View textView = LayoutInflater.from(mContext).inflate(R.layout.message_error_correction_timeitem, parent, false);
            return new ErrorHolder(textView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).holdType;  //0  阶段1课程2课件	number	@mock=0
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == ONLINE) {
            OnLiveViewHolder holderfour = (OnLiveViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        } else if (getItemViewType(position) == COURSEHOMEWORK) {
            OnLiveViewHolder holderfour = (OnLiveViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }else if (getItemViewType(position) == MOCK) {
            MockexamHolder holderfour = (MockexamHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        } else if (getItemViewType(position) == LOGISTIC) {
            LogisticHolder holderfour = (LogisticHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        } else if (getItemViewType(position) == CORRECTION) {
            ErrorHolder holderfour = (ErrorHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        } else if (getItemViewType(position) == ESSAYREPORT) {
            BaseHolder baseHolder = (BaseHolder) holder;
            UserMessageBean bean = getItem(position);
            UserMessageBean.PayloadBean payloadBean = bean.payload;
            baseHolder.bindUI(payloadBean.title, payloadBean.text, bean.noticeTime, bean.isRead);
        }

    }


    protected class BaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTitleView, mContentView, mTimeView;
        public View mUnReadView;
        public SwipeItemLayout swipe_item;

        BaseHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.lll_delete).setOnClickListener(this);
            mTitleView = itemView.findViewById(R.id.title_txt);
            mContentView = itemView.findViewById(R.id.content_txt);
            mTimeView = itemView.findViewById(R.id.tv_time_tip);
            mUnReadView = itemView.findViewById(R.id.hasRead_view);
            swipe_item = itemView.findViewById(R.id.swipe_item);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if (onRecyclerViewItemClickListener != null)
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), mUnReadView, EventConstant.EVENT_ALL);
                    break;
                case R.id.lll_delete:
                    if (mOnDeleteListener != null)
                        mOnDeleteListener.onDeleteClick(getAdapterPosition());
                    swipe_item.close();
                    break;
            }
        }

        public void bindUI(String title, String content, String time, int hasRead) {
            mTitleView.setText(title);
            mContentView.setText(content);
            mTimeView.setText(time);
            mUnReadView.setVisibility(hasRead == 1 ? View.INVISIBLE : View.VISIBLE);
        }
    }

    protected class OnLiveViewHolder extends BaseHolder {
        TextView mTeacherView;
        ImageView mCoverImg;

        OnLiveViewHolder(View itemView) {
            super(itemView);
            mTeacherView = itemView.findViewById(R.id.techer_des_txt);
            mCoverImg = itemView.findViewById(R.id.cover_img);
        }

        public void bindUI(UserMessageBean item, int position) {
            try {
                final UserMessageBean.PayloadBean productMsg = item.payload;
                if (null != productMsg) {
                    super.bindUI(productMsg.title, productMsg.text, item.noticeTime, item.isRead);

                    if (productMsg.custom != null) {
                        if (!TextUtils.isEmpty(productMsg.custom.teacher)){
                            mTeacherView.setText(productMsg.custom.teacher);
                        }
                        ImageLoad.displaynoCacheImage(mContext, R.mipmap.load_default, productMsg.custom.picture, mCoverImg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected class MockexamHolder extends BaseHolder {
        MockexamHolder(View itemView) {
            super(itemView);
        }

        public void bindUI(UserMessageBean item, int position) {
            try {
                final UserMessageBean.PayloadBean productMsg = item.payload;
                if (null != productMsg) {
                    super.bindUI(productMsg.title, "模考时间：" + productMsg.custom.time, item.noticeTime, item.isRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected class LogisticHolder extends BaseHolder {
        TextView mOrderNumView;

        LogisticHolder(View itemView) {
            super(itemView);
            mOrderNumView = itemView.findViewById(R.id.tv_order_num);

        }

        public void bindUI(UserMessageBean item, int position) {
            try {
                final UserMessageBean.PayloadBean productMsg = item.payload;
                if (null != productMsg) {
                    super.bindUI(productMsg.title, productMsg.text, item.noticeTime, item.isRead);
                    mOrderNumView.setText("运单编号：" + productMsg.custom.businessId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected class ErrorHolder extends BaseHolder {

        TextView mErrorTypeView;

        ErrorHolder(View itemView) {
            super(itemView);
            mErrorTypeView = itemView.findViewById(R.id.chat_to_tv);

        }

        public void bindUI(UserMessageBean item, int position) {
            try {
                final UserMessageBean.PayloadBean productMsg = item.payload;
                if (null != productMsg) {

                    //String tmpWords= TextUtils.isEmpty(productMsg.text) ?"":productMsg.text.replace("\n","").replace("\r","");
                    super.bindUI(productMsg.title, productMsg.text, item.noticeTime, item.isRead);
                    mErrorTypeView.setText(item.detailType.equals("suggest") ? "建" : "纠");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
