package com.huatu.handheld_huatu.adapter.order;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.MyOrderData;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.utils.ArrayUtils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018\7\12 0012.
 */

public class OrderStatusListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DecimalFormat df = new DecimalFormat("0.00");
    private Context mContext;
    private List<MyOrderData.OrderList> mListOrder;

    public OrderStatusListAdapter(Context context, List<MyOrderData.OrderList> listOrders) {
        this.mContext = context;
        mListOrder = listOrders;
    }

    public void removeAndRefresh(int postion) {
        mListOrder.remove(postion);
        this.notifyItemRemoved(postion);
    }

    public void clear(){

    }

    public void refresh(List<MyOrderData.OrderList> cacheCourses) {
        this.mListOrder.clear();
        mListOrder.addAll(cacheCourses);
        this.notifyDataSetChanged();
    }

    public MyOrderData.OrderList getCurrentItem(int position) {
        if (position >= 0 && position < ArrayUtils.size(mListOrder))
            return mListOrder.get(position);
        return null;
    }

    OnRecItemClickListener mClickListener;

    public void setOnItemClickListener(OnRecItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View textView = LayoutInflater.from(mContext).inflate(R.layout.item_activity_order, parent, false);
        return new OrderViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderViewHolder textViewHolder = (OrderViewHolder) holder;
        textViewHolder.bindUi(mListOrder.get(position));
    }


    @Override
    public int getItemCount() {
        return ArrayUtils.size(mListOrder);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ll_order_content)
        LinearLayout ll_order_content;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_num)
        TextView tv_num;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_payOrLogistics)
        TextView tv_payOrLogistics;
        @BindView(R.id.tv_consult)
        TextView tv_consult;
        @BindView(R.id.tv_refund)
        TextView btnRefund;

        OrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        /*
             itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            itemView.findViewById(R.id.delete).setOnClickListener(this) ;*/
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.whole_content:
                    if (mClickListener != null)
                        mClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);
                    break;
                case R.id.delete:
                    if (mClickListener != null)
                        mClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_DELETE);
                    break;

            }
        }

        public void bindUi(MyOrderData.OrderList itemData) {
            String aa;
            final StringBuffer bb = new StringBuffer();
            for (int i = 0; i < itemData.Title.size(); i++) {
                if (i == itemData.Title.size() - 1) {
                    aa = itemData.Title.get(i);
                } else {
                    aa = itemData.Title.get(i) + "\n";
                }
                bb.append(aa);
            }
            this.tv_title.setText(bb);
            if (itemData.OrderNum != null) {
                this.tv_num.setText("订单号码：" + itemData.OrderNum);
            }
            if (itemData.AddTime != null) {
                this.tv_time.setText("提交时间：" + itemData.AddTime);
            }
            if (itemData.Status == 1) {
                String mMoney = df.format(itemData.MoneySum);
                this.tv_price.setText("应付金额：" + mMoney);
                this.tv_consult.setText("课程咨询");
                this.tv_payOrLogistics.setText("立即支付");
                this.tv_consult.setVisibility(View.VISIBLE);
                this.tv_payOrLogistics.setVisibility(View.VISIBLE);
                this.btnRefund.setVisibility(View.GONE);
                this.tv_payOrLogistics.setTextColor(ContextCompat.getColor(mContext, R.color.red120));
                this.tv_payOrLogistics.setBackgroundResource(R.drawable.textview_red_border);
               /* this.tv_payOrLogistics.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,OrderDetailActivity.class);
                        intent.putExtra("type","1");
                        intent.putExtra("orderNum",mData.OrderNum);
                        mContext.startActivityForResult(intent, 10001);
                    }
                });
                holder.tv_consult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(orderSelectListener != null) {
                            orderSelectListener.onOrderSelect(mData);
                        }
                    }
                });*/
            } else if (itemData.Status == 2) {
                String mMoneyReceipt = df.format(itemData.MoneyReceipt);
                this.tv_price.setText("实付金额：" + mMoneyReceipt);
                this.tv_consult.setText("售后服务");
                this.tv_payOrLogistics.setText("订单详情");
                this.tv_consult.setVisibility(View.VISIBLE);
                this.tv_payOrLogistics.setVisibility(View.VISIBLE);
                if (itemData.isPass == 1) {
                    this.btnRefund.setText("我要退款");
                    this.btnRefund.setVisibility(View.VISIBLE);
                } else if (itemData.isPass == 2) {
                    this.btnRefund.setText("已退款");
                    this.btnRefund.setVisibility(View.VISIBLE);
                    this.tv_payOrLogistics.setVisibility(View.GONE);
                    this.tv_consult.setVisibility(View.GONE);
                } else {
                    this.btnRefund.setVisibility(View.GONE);
                }
                this.tv_payOrLogistics.setTextColor(ContextCompat.getColor(mContext, R.color.chat_name_other_people));
                this.tv_payOrLogistics.setBackgroundResource(R.drawable.textview_order_border);
               /* this.tv_payOrLogistics.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, OrderDetailActivity.class);
                        intent.putExtra("orderNum", mData.OrderNum);
                        intent.putExtra("type", "2");
                        mContext.startActivityForResult(intent, 10001);
                    }
                });
                holder.tv_consult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(orderSelectListener != null) {
                            orderSelectListener.onOrderSelect(mData);
                        }
                    }
                });
                holder.btnRefund.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mData.isPass == 1) {
                            OrderRefundFragment.newInstance(mContext, mData.OrderNum, mData.refundMoney);
                        } else if(mData.isPass == 2) {
                            OrderRefundDetailFragment.newInstance(mContext, mData.OrderNum, mData.refundMoney, null);
                        }
                    }
                });*/
            }
        }
    }
}
