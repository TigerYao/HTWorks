package com.huatu.handheld_huatu.business.me.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFrgContainerActivity;
import com.huatu.handheld_huatu.business.me.EssayOrderDetailActivity;
import com.huatu.handheld_huatu.business.me.bean.MyEssayOrderData;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.PayInfo;
import com.huatu.handheld_huatu.business.ztk_zhibo.pay.ConfirmPaymentFragment;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.handheld_huatu.view.swiperecyclerview.swipemenu.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by ht-ldc on 2018/7/28.
 */

public class OrderEssayAdapter extends RecyclerView.Adapter<OrderEssayAdapter.ViewHolder>{
    private List<MyEssayOrderData.EssayOrderList> mOrderData=new ArrayList<>();
    private Activity mContext;
    private EssayExamImpl essayExamImpl;

    public OrderEssayAdapter(Activity mContext,EssayExamImpl essayExamImpl) {
        this.essayExamImpl=essayExamImpl;
        this.mContext = mContext;
    }

    public void setData(ArrayList<MyEssayOrderData.EssayOrderList> dataList) {
        mOrderData.clear();
        mOrderData.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public OrderEssayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_essay_order, parent, false);
        final ViewHolder holder = new ViewHolder(view);
//        holder.swipeMenuLayout.setSwipeEnable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(final OrderEssayAdapter.ViewHolder holder, final int position) {
        if (mOrderData == null || mOrderData.isEmpty())
            return;
        final MyEssayOrderData.EssayOrderList mData = mOrderData.get(position);
        if (mData.orderNumStr!=null){
            holder.tv_order_number.setText("订单号："+mData.orderNumStr);
        }else{
            holder.tv_order_number.setText("");

        }
        //        订单状态。 待付款0,已付款1, 已取消2和4都是取消, 5正在退款，6退款完成，7退款被拒
        holder.tv_pay_num.setText("");
        if (mData.bizStatus==0){
            holder.tv_order_status.setText("待付款");
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.red250));
            holder.tv_pay_type.setText("需支付");
            if (mData.totalMoney!=0){
                holder.tv_pay_num.setText("¥ "+getYuan(mData.totalMoney));
            }
            if (holder.tv_change_order_status.getVisibility() == View.GONE)
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
            holder.tv_change_order_status.setText("去支付");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext,R.drawable.selected_order));
        }else if (mData.bizStatus==1){
            holder.tv_order_status.setText("已完成");
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
            holder.tv_pay_type.setText("实付款");
            if (mData.realMoney!=0){
                holder.tv_pay_num.setText("¥ "+getYuan(mData.realMoney));
                holder.tv_change_order_status.setVisibility(View.GONE);
            }else {
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
            }
            holder.tv_change_order_status.setText("删除订单");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.outline_last_layer_text));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext,R.drawable.unselect_feedback));
        }else if (mData.bizStatus==2||mData.bizStatus==4){
            holder.tv_order_status.setText("已取消");
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
            holder.tv_pay_type.setText("需支付");
            holder.tv_pay_num.setText("¥ "+getYuan(mData.totalMoney));
            if (mData.totalMoney!=0){
                holder.tv_change_order_status.setVisibility(View.GONE);
            }else {
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
            }
            holder.tv_change_order_status.setText("删除订单");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.outline_last_layer_text));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext,R.drawable.unselect_feedback));
        }else if (mData.bizStatus == 5){
            holder.tv_order_status.setText(mData.bizStatusName);
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
            holder.tv_pay_type.setText("实退款");
            if (mData.realMoney!=0){
                holder.tv_pay_num.setText("¥ "+getYuan(mData.realMoney));
                holder.tv_change_order_status.setVisibility(View.GONE);
            }else {
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
            }
            holder.tv_change_order_status.setText("删除订单");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.outline_last_layer_text));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext,R.drawable.unselect_feedback));
        }else if (mData.bizStatus == 6){
            holder.tv_order_status.setText("已退款");
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
            holder.tv_pay_type.setText("实退款");
            if (mData.realMoney!=0){
                holder.tv_pay_num.setText("¥ "+getYuan(mData.realMoney));
                holder.tv_change_order_status.setVisibility(View.GONE);
            }else {
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
            }
            holder.tv_change_order_status.setText("删除订单");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.outline_last_layer_text));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext,R.drawable.unselect_feedback));
        }else if (mData.bizStatus == 7){
            holder.tv_order_status.setText(mData.bizStatusName);
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
            holder.tv_pay_type.setText("被拒款");
            if (mData.realMoney!=0){
                holder.tv_pay_num.setText("¥ "+getYuan(mData.realMoney));
                holder.tv_change_order_status.setVisibility(View.GONE);
            }else {
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
            }
            holder.tv_change_order_status.setText("删除订单");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext,R.color.outline_last_layer_text));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext,R.drawable.unselect_feedback));
        }
        if (mData.goodsList!=null&&mData.goodsList.size()>0){
            holder.goods_detail_list.setAdapter(new CommonAdapter<MyEssayOrderData.EssayOrderList.GoodList>(mContext, mData.goodsList, R.layout.essay_order_item_layout) {
                @Override
                public void convert(ViewHolder holder, MyEssayOrderData.EssayOrderList.GoodList item, int position) {
                    MyEssayOrderData.EssayOrderList.GoodList goods = item;
                    String content = goods.getGoodTitle();
                    holder.setText(R.id.tv_order_content, content);
                    holder.setText(R.id.tv_order_expiry_date, goods.getExp());
                }
            });
        }
        holder.goods_detail_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                holder.itemView.performClick();
            }
        });

          holder.tv_change_order_status.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  if (mData.bizStatus==0){
                      //支付
                      PayInfo payInfo = new PayInfo();
                      payInfo.OrderNum = mData.orderNumStr;
                      payInfo.MoneySum = String.valueOf(getYuan(mData.totalMoney));
                      payInfo.xxb = 1;
                      Bundle arg = new Bundle();
                      arg.putString("pay_number", String.valueOf(getYuan(mData.totalMoney)));
                      arg.putLong("order_id",mData.id);
                      arg.putBoolean("is_essay_order", true);
                      arg.putSerializable("pay_info", payInfo);
                      BaseFrgContainerActivity.newInstance(mContext,
                              ConfirmPaymentFragment.class.getName(), arg);
                      //  付款
//                      Intent intent=new Intent(mContext,EssayOrderDetailActivity.class);
//                      Bundle bundle=new Bundle();
//                      bundle.putSerializable("DATA", mData);
//                      intent.putExtras(bundle);
//                      mContext.startActivityForResult(intent, 10001);
                  }else {
                      // 删除
                      DialogUtils.onShowConfirmDialog(mContext, new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              if (!NetUtil.isConnected()){
                                  ToastUtils.showShort("网络未连接，请检查您的网络");
                                  return;
                              }
                              if (essayExamImpl!=null){
                                  essayExamImpl.deleteMyEssayOrder(mData.id);
                              }
                              if (getItemCount() >position) {
                                  mOrderData.remove(position);
                                  notifyItemRemoved(holder.getAdapterPosition());
                              }
                          }
                      },null,"确认删除？","取消","确定");

                  }

              }
          });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////  详情
                Intent intent=new Intent(mContext,EssayOrderDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("DATA", mData);
                intent.putExtras(bundle);
                mContext.startActivityForResult(intent, 10001);
            }
        });

    }
    private String getYuan(int price) {
        int y=price/100;
        if(price<10) {
            int f = price % 100;
            return y + ".0" + f;
        }else {
            int f = price % 100;
            return y + "." + f;
        }
    }

    @Override
    public int getItemCount() {
        return mOrderData.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_number)
        TextView tv_order_number;
        @BindView(R.id.tv_order_status)
        TextView tv_order_status;
        @BindView(R.id.tv_order_title)
        TextView tv_order_title;
        @BindView(R.id.goods_detail_list)
        ListViewForScroll goods_detail_list;
        @BindView(R.id.tv_pay_type)
        TextView tv_pay_type;
        @BindView(R.id.tv_pay_num)
        TextView tv_pay_num;
        @BindView(R.id.tv_change_order_status)
        TextView tv_change_order_status;
//        @BindView(R.id.smContentView)
//        View smContentView;
//        @BindView(R.id.smMenuView)
//        View smMenuView;
//        SwipeMenuLayout swipeMenuLayout;

        public ViewHolder(View itemView) {
            super(itemView);
//            swipeMenuLayout = (SwipeMenuLayout) itemView;
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
