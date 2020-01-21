package com.huatu.handheld_huatu.business.me.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.MyOrderData;
import com.huatu.handheld_huatu.business.me.bean.OrderLogisticBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chq on 2017/8/29.
 */

public class OrderLogisticAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflate;
    private String mFontPrefix ="【"+ "<font color='#37ADFF'>";
    private String mFontSubFix = "</font>"+"】";
    private List<OrderLogisticBean.DataEntityX.DataEntity> mOrderLogisticData;
    private onOrderSelectListener orderSelectListener;

    public OrderLogisticAdapter(Context context) {
        this.mContext = context;
        mOrderLogisticData = new ArrayList<>();
        mInflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mOrderLogisticData.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderLogisticData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflate.inflate(R.layout.item_activity_order_logistic, parent, false);
            holder = new OrderLogisticAdapter.ViewHolder(convertView);
        } else {
            holder = (OrderLogisticAdapter.ViewHolder) convertView.getTag();
        }
        final OrderLogisticBean.DataEntityX.DataEntity data = mOrderLogisticData.get(position);
        if (data != null) {
            if (position == 0) {
                holder.lineNoArri.setVisibility(View.GONE);
                holder.dianNoArri.setVisibility(View.GONE);
                holder.lineArri.setVisibility(View.VISIBLE);
                holder.dianArri.setVisibility(View.VISIBLE);
                holder.tvTitleLogistic.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
                holder.tvTimeLogistic.setTextColor(ContextCompat.getColor(mContext, R.color.blackF4));
            } else {
                holder.lineNoArri.setVisibility(View.VISIBLE);
                holder.dianNoArri.setVisibility(View.VISIBLE);
                holder.lineArri.setVisibility(View.GONE);
                holder.dianArri.setVisibility(View.GONE);
                holder.tvTitleLogistic.setTextColor(ContextCompat.getColor(mContext, R.color.black250));
                holder.tvTimeLogistic.setTextColor(ContextCompat.getColor(mContext, R.color.black250));
            }

            String time = data.time;
            if (time == null) {
                time = data.ftime;
            }
            if (time != null) {
                holder.tvTimeLogistic.setText(time);
            }

                if (data.context != null) {
                    if (data.status.equals("派件")){
                        String s=data.context;
                    if(s.contains("（") &&s.contains("）")){
                        s=s.replace("（", "(");
                        s=s.replace("）", ")");
                    }
                    if(s.contains("(") && s.contains(")")){
                        String prefixReplace = s.replace("(", mFontPrefix);
                        String subfixReplace = prefixReplace.replace(")", mFontSubFix);
                        holder.tvTitleLogistic.setText(Html.fromHtml(subfixReplace));
                    }else {
                        holder.tvTitleLogistic.setText(data.context);
                    }
                    }else {
                        holder.tvTitleLogistic.setText(data.context);
                    }


            }
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_title_logistic)
        TextView tvTitleLogistic;
        @BindView(R.id.tv_time_logistic)
        TextView tvTimeLogistic;
        @BindView(R.id.line_no_arri)
        View lineNoArri;
        @BindView(R.id.dian_no_arri)
        View dianNoArri;
        @BindView(R.id.line_arri)
        View lineArri;
        @BindView(R.id.dian_arri)
        View dianArri;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            view.setTag(this);
        }
    }

    public void setData(ArrayList<OrderLogisticBean.DataEntityX.DataEntity> mOrderList) {
        mOrderLogisticData.clear();
        mOrderLogisticData.addAll(mOrderList);
        notifyDataSetChanged();
    }

    public void setOnOrderSelectListener(onOrderSelectListener l) {
        orderSelectListener = l;
    }

    public interface onOrderSelectListener {
        void onOrderSelect(MyOrderData.OrderList order);
    }
}
