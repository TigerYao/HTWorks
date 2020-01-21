package com.huatu.handheld_huatu.business.me.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.me.bean.BalanceDetailResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ht on 2017/11/27.
 */
public class EssayConsumeAdapter extends BaseAdapter{
    private Context mContext;
    private LayoutInflater mInflate;
    private List<BalanceDetailResult> mData;

    public EssayConsumeAdapter(Context context) {
        mContext = context;
        mData=new ArrayList<>();
        mInflate=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView=mInflate.inflate(R.layout.item_activity_balance_detail,parent,false);
            holder=new ViewHolder(convertView);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        BalanceDetailResult mDataList=mData.get(position);
        if (!TextUtils.isEmpty(mDataList.name)){
            holder.tv_event.setText(mDataList.name);
        }else {
            holder.tv_event.setText("购买申论批改");
        }

        if (mDataList.payTime!=null){
            holder.tv_time.setText(mDataList.payTime);
        }
        if (mDataList.payMsg!=null){
            holder.tv_num.setText(mDataList.payMsg);
        }
        if(position==getCount()-1){
            holder.tv_foot.setVisibility(View.VISIBLE);
        }else {
            holder.tv_foot.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setData(ArrayList<BalanceDetailResult> mEssayData) {
        mData.clear();
        mData.addAll(mEssayData);
        notifyDataSetChanged();
    }

    static class ViewHolder{
        @BindView(R.id.tv_event)
        TextView tv_event;
        @BindView(R.id.tv_time)
        TextView tv_time;
        @BindView(R.id.tv_num)
        TextView tv_num;
        @BindView(R.id.tv_foot)
        TextView tv_foot;
        public ViewHolder(View view) {
            ButterKnife.bind(this,view);
            view.setTag(this);
        }
    }
}
