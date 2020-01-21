package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckCountDetailBean;
import com.huatu.handheld_huatu.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckCountDetailListAdapter extends RecyclerView.Adapter <CheckCountDetailListAdapter.ViewHolder>{
    private Context mContext;
    private LayoutInflater mInflate;
    private List<CheckCountDetailBean> data;

    public CheckCountDetailListAdapter(Context context) {
        mContext=context;
        mInflate=LayoutInflater.from(context);
        data=new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =mInflate.inflate(R.layout.item_check_count_detail_list,parent,false);
        ViewHolder hold=new ViewHolder(view);
        return hold;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckCountDetailBean mResult=data.get(position);
        if (mResult!=null){
            if (!TextUtils.isEmpty(mResult.orderNumStr)){
                holder.tv_order_or_class.setText("订单号："+mResult.orderNumStr);
            }else {
                holder.tv_order_or_class.setText("");
            }
            if (!TextUtils.isEmpty(mResult.source)){
                holder.tv_source.setVisibility(View.VISIBLE);
                holder.tv_source.setText(mResult.source);
            }else {
                holder.tv_source.setVisibility(View.GONE);
            }
            //付款时间
            if (!TextUtils.isEmpty(mResult.payTime)){
                holder.tv_pay_time.setText("付款时间："+mResult.payTime);
            }else {
                holder.tv_pay_time.setText("");
            }
            //title 拼接？
            if (!TextUtils.isEmpty(mResult.name)){
                holder.tv_title.setText(mResult.name+"x"+mResult.unit);
            }else{
                holder.tv_title.setText("");
            }
            if (mResult.bizStatus==5){
                //退款中
                if (!TextUtils.isEmpty(mResult.bizStatusName)){
                    holder.tv_time_or_status.setText(mResult.bizStatusName);
                }
                holder.tv_time_or_status.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_order_or_class.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_source.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_left_days.setVisibility(View.VISIBLE);
                holder.tv_left_days.setText("剩余"+mResult.num+"次");
                holder.tv_left_days.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_left_days.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.check_count_num_bg));
            }else if (mResult.bizStatus==6){
                //已退款
                if (!TextUtils.isEmpty(mResult.bizStatusName)){
                    holder.tv_time_or_status.setText(mResult.bizStatusName);
                }
                holder.tv_time_or_status.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_order_or_class.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_source.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_left_days.setVisibility(View.VISIBLE);
                holder.tv_left_days.setText("剩余"+mResult.num+"次");
                holder.tv_left_days.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_left_days.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.check_count_num_bg));
            }else {
                    if (mResult.expireFlag==0){
                        //无限期
                        holder.tv_left_days.setVisibility(View.VISIBLE);
                        holder.tv_left_days.setText("无限期");
                        holder.tv_left_days.setTextColor(ContextCompat.getColor(mContext,R.color.goldD49));
                        holder.tv_left_days.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.check_count_num_gold_bg));
                    }else {
                        //有限期
                       if (!TextUtils.isEmpty(mResult.expireDateStr)){
                            //到期时间
                            holder.tv_left_days.setText(mResult.expireDateStr);
                            holder.tv_left_days.setTextColor(ContextCompat.getColor(mContext,R.color.goldD49));
                            holder.tv_left_days.setBackground(ContextCompat.getDrawable(mContext,R.mipmap.check_count_num_gold_bg));
                            holder.tv_left_days.setVisibility(View.VISIBLE);
                       }else {
                            holder.tv_left_days.setVisibility(View.GONE);
                        }
                    }

            if (mResult.expireFlag==1&&mResult.expireDate<=0) {
                //已过期
                holder.tv_time_or_status.setText("已过期");
                holder.tv_time_or_status.setTextColor(ContextCompat.getColor(mContext, R.color.black250));
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_order_or_class.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_source.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
            }else if (mResult.isLimitNum==0){
                //不限次
                holder.tv_time_or_status.setText("不限次");
                holder.tv_time_or_status.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                holder.tv_order_or_class.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                holder.tv_source.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
            }else if (mResult.num==0){
                //已使用
                holder.tv_time_or_status.setText("已使用");
                holder.tv_time_or_status.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_order_or_class.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_source.setTextColor(ContextCompat.getColor(mContext,R.color.black250));
                holder.tv_left_days.setVisibility(View.GONE);
            }else {
                holder.tv_title.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                holder.tv_order_or_class.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                holder.tv_source.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                holder.tv_time_or_status.setTextColor(ContextCompat.getColor(mContext,R.color.blackF4));
                SpannableStringBuilder sb = new SpannableStringBuilder("");
                sb.append("剩余");
                sb.append(mResult.num+"");
                sb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.goldD49)),2,sb.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                sb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 2,sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sb.append("次");
                holder.tv_time_or_status.setText(sb);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<CheckCountDetailBean> dataList){
        if (data!=null){
            data.clear();
            data.addAll(dataList);
        }
        notifyDataSetChanged();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_left_days)
        TextView tv_left_days;
        @BindView(R.id.tv_order_or_class)
        TextView tv_order_or_class;
        @BindView(R.id.tv_source)
        TextView tv_source;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_pay_time)
        TextView tv_pay_time;
        @BindView(R.id.tv_time_or_status)
        TextView tv_time_or_status;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setTag(this);
        }
    }
}
