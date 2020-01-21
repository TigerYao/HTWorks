package com.huatu.handheld_huatu.business.me.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.MyV5OrderContent;
import com.huatu.handheld_huatu.business.me.fragment.BillDetailFragment;
import com.huatu.handheld_huatu.business.me.fragment.FillBillDetailFragment;
import com.huatu.handheld_huatu.business.me.order.LogisticDetailActivity;
import com.huatu.handheld_huatu.business.me.order.OrderDetailActivity;
import com.huatu.handheld_huatu.mvppresenter.essay.EssayExamImpl;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.WXUtils;
import com.huatu.handheld_huatu.view.CircleImageView;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.ListViewForScroll;
import com.huatu.handheld_huatu.view.PileLayout;
import com.huatu.utils.ArrayUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by chq on 2018/7/28.
 */

public class CourseOrderAdapter extends RecyclerView.Adapter<CourseOrderAdapter.ViewHolder> {

    private List<MyV5OrderContent> mOrderData = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("0.00");
    private Activity mContext;
    private CommonAdapter<MyV5OrderContent.ClassInfo> mAdapter;
    private EssayExamImpl mEssayExamImpl;

    public CourseOrderAdapter(Activity context, EssayExamImpl mEssayExamImpl) {
        this.mEssayExamImpl = mEssayExamImpl;
        this.mContext = context;
    }

    @Override
    public CourseOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_course_order, parent, false);
        final ViewHolder holder = new ViewHolder(view);
//        holder.swipeMenuLayout.setSwipeEnable(false);
        return holder;
    }


    private int mActionIndex=-1;
    public void refreshInvoiceStatus(String orderId,String orderNum){
         if(mActionIndex!=-1){
            int curIndex=mActionIndex;
            mActionIndex=-1;
            if((curIndex>-1)&&(curIndex < ArrayUtils.size(mOrderData))){
                MyV5OrderContent mData = mOrderData.get(curIndex);
                if(mData.orderId.equals(orderId)&&mData.orderNum.equals(orderNum)){
                    mData.invoiceStatus=2;
                    this.notifyItemChanged(curIndex);
                }
             }
        }
    }

    @Override
    public void onBindViewHolder(final CourseOrderAdapter.ViewHolder holder, final int position) {
        final MyV5OrderContent mData = mOrderData.get(position);
        if (mData.orderNum != null) {
            holder.tv_order_number.setText("订单号：" + mData.orderNum);
        }
        if (mData.price != null) {
            holder.tv_pay_num.setText("¥ " + mData.price);
        }
        if (mData.isCollage == 0) {
            //普通不拼团的订单
            if (!mData.hasLogistics) {
                holder.tv_to_logistic.setVisibility(View.GONE);
            } else {
                holder.tv_to_logistic.setVisibility(View.VISIBLE);
            }
            if (mData.payStatus == 0 || mData.payStatus == 3) {
                //未支付
                if (!TextUtils.isEmpty(mData.statusDesc)) {
                    holder.tv_order_status.setText(mData.statusDesc);
                } else {
                    holder.tv_order_status.setText("");
                }
                holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.red250));
                holder.tv_pay_type.setText("需支付");
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
                holder.tv_change_order_status.setText("去支付");
                holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selected_order));
                holder.tv_delete_order.setVisibility(View.GONE);
            } else if (mData.payStatus == 1) {
                //1-已支付
                if (!TextUtils.isEmpty(mData.statusDesc)) {
                    holder.tv_order_status.setText(mData.statusDesc);
                } else {
                    holder.tv_order_status.setText("");
                }
                holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
                holder.tv_pay_type.setText("实付款");
                holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.outline_last_layer_text));
                holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.unselect_feedback));
                if (mData.price.equals("0.00")) {
//                    holder.tv_change_order_status.setText("删除订单");
//                    holder.tv_change_order_status.setVisibility(View.VISIBLE);
                    holder.tv_delete_order.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_delete_order.setVisibility(View.GONE);
                }
                if (mData.invoiceStatus == 2) {
                    //已开发票
                    holder.tv_change_order_status.setVisibility(View.VISIBLE);
                    holder.tv_change_order_status.setText("开票详情");
                } else if (mData.invoiceStatus == 1) {
                    //可以开发票
                    holder.tv_change_order_status.setVisibility(View.VISIBLE);
                    holder.tv_change_order_status.setText("申请开票");
                } else {
                    holder.tv_change_order_status.setVisibility(View.GONE);
                }
            } else if (mData.payStatus == 2) {
                //已取消
                if (!TextUtils.isEmpty(mData.statusDesc)) {
                    holder.tv_order_status.setText(mData.statusDesc);
                } else {
                    holder.tv_order_status.setText("");
                }
                holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
                holder.tv_pay_type.setText("需支付");
                holder.tv_change_order_status.setVisibility(View.GONE);
//                holder.tv_change_order_status.setText("删除订单");
//                holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.outline_last_layer_text));
//                holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.unselect_feedback));
                if (!mData.price.equals("0.00")) {
                    holder.tv_delete_order.setVisibility(View.GONE);
                } else {
                    holder.tv_delete_order.setVisibility(View.VISIBLE);
                }
            } else {
                if (!TextUtils.isEmpty(mData.statusDesc)) {
                    holder.tv_order_status.setText(mData.statusDesc);
                } else {
                    holder.tv_order_status.setText("");
                }
                holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
                holder.tv_pay_type.setText("实付款");
                holder.tv_change_order_status.setVisibility(View.GONE);

//                holder.tv_change_order_status.setText("删除订单");
//                holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.outline_last_layer_text));
//                holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.unselect_feedback));
                if (!mData.price.equals("0.00")) {
                    holder.tv_delete_order.setVisibility(View.GONE);
                } else {
                    holder.tv_delete_order.setVisibility(View.VISIBLE);
                }
            }
        } else if (mData.isCollage == 1) {
            //拼团中的订单
            if (!mData.hasLogistics) {
                holder.tv_to_logistic.setVisibility(View.GONE);
            } else {
                holder.tv_to_logistic.setVisibility(View.VISIBLE);
            }
            holder.tv_order_status.setText("拼单中，差" + mData.needNumber + "人");
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.red250));
            holder.tv_change_order_status.setText("邀请好友拼单");
            holder.tv_change_order_status.setVisibility(View.VISIBLE);
            holder.tv_pay_type.setText("实付款");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.selected_order));
            holder.tv_delete_order.setVisibility(View.GONE);
        } else if (mData.isCollage == 2) {
            //拼团成功的订单
            if (!mData.hasLogistics) {
                holder.tv_to_logistic.setVisibility(View.GONE);
            } else {
                holder.tv_to_logistic.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(mData.statusDesc)) {
                holder.tv_order_status.setText(mData.statusDesc);
            } else {
                holder.tv_order_status.setText("");
            }
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
            holder.tv_pay_type.setText("实付款");
            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.outline_last_layer_text));
            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.unselect_feedback));
            if (mData.price.equals("0.00")) {
//                holder.tv_change_order_status.setVisibility(View.VISIBLE);
//                holder.tv_change_order_status.setText("删除订单");
                holder.tv_delete_order.setVisibility(View.VISIBLE);
            } else{
                holder.tv_delete_order.setVisibility(View.GONE);
            }
            if (mData.invoiceStatus == 2) {
                //已开发票
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
                holder.tv_change_order_status.setText("开票详情");
            } else if (mData.invoiceStatus == 1) {
                //可以开发票
                holder.tv_change_order_status.setVisibility(View.VISIBLE);
                holder.tv_change_order_status.setText("申请开票");
            } else {
                holder.tv_change_order_status.setVisibility(View.GONE);
            }
        } else if (mData.isCollage == 3) {
            //拼团失败退款的订单
            if (!mData.hasLogistics) {
                holder.tv_to_logistic.setVisibility(View.GONE);
            } else {
                holder.tv_to_logistic.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(mData.statusDesc)) {
                holder.tv_order_status.setText(mData.statusDesc);
            } else {
                holder.tv_order_status.setText("");
            }
            holder.tv_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
            holder.tv_pay_type.setText("实付款");
            holder.tv_change_order_status.setVisibility(View.GONE);

//            holder.tv_change_order_status.setText("删除订单");
//            holder.tv_change_order_status.setTextColor(ContextCompat.getColor(mContext, R.color.outline_last_layer_text));
//            holder.tv_change_order_status.setBackground(ContextCompat.getDrawable(mContext, R.drawable.unselect_feedback));
            if (!mData.price.equals("0.00")) {
                holder.tv_delete_order.setVisibility(View.GONE);
            } else {
                holder.tv_delete_order.setVisibility(View.VISIBLE);
            }
        }


        if (mData.classInfo != null && mData.classInfo.size() != 0) {
            if (mData.classInfo.size() > 1) {
                holder.tv_total_course.setVisibility(View.VISIBLE);
                holder.tv_total_course.setText("共" + mData.classInfo.size() + "个课程");
            } else {
                holder.tv_total_course.setVisibility(View.GONE);
            }
            mAdapter = new CommonAdapter<MyV5OrderContent.ClassInfo>(mContext, mData.classInfo, R.layout.item_for_list) {
                @Override
                public void convert(ViewHolder holder, MyV5OrderContent.ClassInfo item, int position) {
                    if (item.title != null) {
                        holder.setText(R.id.tv_list_course_title, item.title);
                    }
                    if (item.finalPrice != null) {
                        holder.setText(R.id.tv_list_real_price, "¥ " + item.finalPrice);
                    }
                    if (item.price != null) {
                        holder.setViewVisibility(R.id.tv_list_original_price, View.VISIBLE);
                        holder.setText(R.id.tv_list_original_price, "¥ " + item.price);
                        holder.getPaint(R.id.tv_list_original_price, Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        holder.setViewVisibility(R.id.tv_list_original_price, View.GONE);
                    }
                    if (item.teachers != null) {
                        holder.setText(R.id.tv_list_course_teacher, "授课老师：" + item.teachers);
                    }

                    if (item.lessonCount != null) {
                        holder.setText(R.id.tv_list_course_time, item.lessonCount + "课时");
                    }
                }
            };
            holder.lv_order_course_content.setAdapter(mAdapter);
            holder.lv_order_course_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    intent.putExtra("type", "1");
                    intent.putExtra("orderNum", mData.orderNum);
                    intent.putExtra("orderId", mData.orderId);
                    intent.putExtra("isCollage", mData.isCollage);
                    intent.putExtra("orderStatus", mData.payStatus);
                    mContext.startActivityForResult(intent, 10001);
                }
            });
        }
        holder.tv_change_order_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                if (mData.isCollage == 1) {
                    //批团中的订单  跳转小程序
                    WXUtils.appOrderToWxApp(mContext, mData.orderId);
                } else {
                    //其他订单
                    if (mData.payStatus == 0 || mData.payStatus == 3) {
                        //  付款
                        Intent intent = new Intent(mContext, OrderDetailActivity.class);
                        intent.putExtra("orderId", mData.orderId);
                        intent.putExtra("isCollage", mData.isCollage);
                        mContext.startActivityForResult(intent, 10001);

                    } else if (mData.invoiceStatus == 2) {
                        //跳开票详情
                        BillDetailFragment.lanuch(mContext,mData.orderId);

                    } else if (mData.invoiceStatus == 1) {
                        //跳开票页
                        if (mData.isJump == 0) {
                            //不可跳转 弹框提示
                            DialogUtils.onShowConfirmDialog(mContext, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }, null, "协议班需在公布拟录用名单之后联系客服申请发票", null, "知道了");
                        } else {
                            //可跳转
                            mActionIndex=position;
                            FillBillDetailFragment.lanuch(mContext,mData.orderId,mData.orderNum,mData.addTime,mData.invoiceMoney,mData.classInfo);
                        }
                    }
                }
            }
        });
        holder.tv_delete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                //  删除
                DialogUtils.onShowConfirmDialog(mContext, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!NetUtil.isConnected()) {
                            ToastUtils.showShort("网络未连接，请检查您的网络");
                            return;
                        }
                        if (mEssayExamImpl != null) {
                            if (mData.isCollage == 3) {
                                mEssayExamImpl.deleteMyOrder(mData.orderId, 1);
                            } else {
                                mEssayExamImpl.deleteMyOrder(mData.orderId, 0);
                            }
                        }
                        if (getItemCount() > position) {
                            mOrderData.remove(position);
                            notifyItemRemoved(holder.getAdapterPosition());
                        }
                    }
                }, null, "确认删除订单？", "取消", "确定");
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShort("网络未连接，请检查您的网络");
                    return;
                }
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                intent.putExtra("orderId", mData.orderId);
                intent.putExtra("isCollage", mData.isCollage);
                mContext.startActivityForResult(intent, 10001);
            }
        });
        ////  协议显示隐藏
        if (mData.hasProtocol) {
            holder.tv_protocols.setVisibility(View.VISIBLE);
        } else {
            holder.tv_protocols.setVisibility(View.GONE);
        }
        if (mData.collageAvatars.size() != 0) {
            holder.pile_layout.setVisibility(View.VISIBLE);
            if (holder.pile_layout != null) {
                holder.pile_layout.removeAllViews();
            }
            for (int i = 0; i < mData.collageAvatars.size(); i++) {
                CircleImageView imageView = (CircleImageView) LayoutInflater.from(mContext).inflate(R.layout.item_praise, holder.pile_layout, false);
                //Glide.with(mContext).load(mData.collageAvatars.get(i)).into(imageView);
                ImageLoad.load(mContext, mData.collageAvatars.get(i), imageView);
                holder.pile_layout.addView(imageView);
            }
        } else {
            holder.pile_layout.setVisibility(View.GONE);
        }

        holder.tv_to_logistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtil.isConnected()) {
                    ToastUtils.showEssayToast("网络未连接，请检查您的网络");
                    return;
                }
                LogisticDetailActivity.newInstance(mContext, mData.orderId);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mOrderData.size();
    }

    public void setData(ArrayList<MyV5OrderContent> mOrderList) {
        mOrderData.clear();
        mOrderData.addAll(mOrderList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_order_number)
        TextView tv_order_number;
        @BindView(R.id.tv_order_status)
        TextView tv_order_status;
        @BindView(R.id.tv_protocols)
        TextView tv_protocols;
        @BindView(R.id.pile_layout)
        PileLayout pile_layout;
        @BindView(R.id.tv_pay_type)
        TextView tv_pay_type;
        @BindView(R.id.tv_pay_num)
        TextView tv_pay_num;
        @BindView(R.id.tv_change_order_status)
        TextView tv_change_order_status;
        @BindView(R.id.tv_delete_order)
        TextView tv_delete_order;
        @BindView(R.id.tv_to_logistic)
        TextView tv_to_logistic;
        @BindView(R.id.tv_total_course)
        TextView tv_total_course;
        @BindView(R.id.lv_order_course_content)
        ListViewForScroll lv_order_course_content;
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
