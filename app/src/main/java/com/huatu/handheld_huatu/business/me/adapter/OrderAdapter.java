package com.huatu.handheld_huatu.business.me.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.MyOrderData;
import com.huatu.handheld_huatu.business.me.order.OrderActivity;
import com.huatu.handheld_huatu.business.me.order.OrderDetailActivity;
import com.huatu.handheld_huatu.business.me.order.OrderRefundDetailFragment;
import com.huatu.handheld_huatu.business.me.order.OrderRefundFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chq on 2017/8/29.
 */

public class OrderAdapter extends BaseAdapter {
    private Activity mContext;
    private LayoutInflater mInflate;
    private List<MyOrderData.OrderList> mOrderData;
    private onOrderSelectListener orderSelectListener;
    private DecimalFormat df = new DecimalFormat("0.00");


    public OrderAdapter(OrderActivity context) {
        this.mContext = context;
        mOrderData = new ArrayList<>();
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mOrderData.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflate.inflate(R.layout.item_activity_order, parent, false);
            holder = new OrderAdapter.ViewHolder(convertView);
        } else {
            holder = (OrderAdapter.ViewHolder) convertView.getTag();
        }
        final MyOrderData.OrderList mData = mOrderData.get(position);
        String aa;
        final StringBuffer bb = new StringBuffer();
        for (int i = 0; i < mData.Title.size(); i++) {
            if(i == mData.Title.size() - 1) {
                aa = mData.Title.get(i);
            } else {
                aa = mData.Title.get(i) + "\n";
            }
            bb.append(aa);
        }
        holder.tv_title.setText(bb);
        if (mData.OrderNum != null) {
            holder.tv_num.setText("订单号码：" + mData.OrderNum);
        }
        if (mData.AddTime != null) {
            holder.tv_time.setText("提交时间：" + mData.AddTime);
        }
        if (mData.Status == 1) {
            String mMoney=df.format(mData.MoneySum);
            holder.tv_price.setText("应付金额：" + mMoney);
            holder.tv_consult.setText("课程咨询");
            holder.tv_payOrLogistics.setText("立即支付");
            holder.tv_consult.setVisibility(View.VISIBLE);
            holder.tv_payOrLogistics.setVisibility(View.VISIBLE);
            holder.btnRefund.setVisibility(View.GONE);
            holder.tv_payOrLogistics.setTextColor(ContextCompat.getColor(mContext, R.color.red120));
            holder.tv_payOrLogistics.setBackgroundResource(R.drawable.textview_red_border);
            holder.tv_payOrLogistics.setOnClickListener(new View.OnClickListener() {
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
            });
        } else if (mData.Status == 2) {
            String mMoneyReceipt=df.format(mData.MoneyReceipt);
            holder.tv_price.setText("实付金额：" +mMoneyReceipt);
            holder.tv_consult.setText("售后服务");
            holder.tv_payOrLogistics.setText("订单详情");
            holder.tv_consult.setVisibility(View.VISIBLE);
            holder.tv_payOrLogistics.setVisibility(View.VISIBLE);
            if(mData.isPass == 1) {
                holder.btnRefund.setText("我要退款");
                holder.btnRefund.setVisibility(View.VISIBLE);
            } else if(mData.isPass == 2) {
                holder.btnRefund.setText("已退款");
                holder.btnRefund.setVisibility(View.VISIBLE);
                holder.tv_payOrLogistics.setVisibility(View.GONE);
                holder.tv_consult.setVisibility(View.GONE);
            } else {
                holder.btnRefund.setVisibility(View.GONE);
            }
            holder.tv_payOrLogistics.setTextColor(ContextCompat.getColor(mContext, R.color.chat_name_other_people));
            holder.tv_payOrLogistics.setBackgroundResource(R.drawable.textview_order_border);
            holder.tv_payOrLogistics.setOnClickListener(new View.OnClickListener() {
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
            });
        }

        return convertView;
    }

    static class ViewHolder {
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);

        }

    }

    public void setData(ArrayList<MyOrderData.OrderList> mOrderList) {
        mOrderData.clear();
        mOrderData.addAll(mOrderList);
        notifyDataSetChanged();
    }

    public void setOnOrderSelectListener(onOrderSelectListener l) {
        orderSelectListener = l;
    }

    public interface onOrderSelectListener {
        void onOrderSelect(MyOrderData.OrderList order);
    }
}
