package com.huatu.handheld_huatu.business.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.me.RedPacketDetailActivity;
import com.huatu.handheld_huatu.business.me.bean.RedEnvelopeInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by acaige on 2018/9/27.
 */

public class RedPacketCourseInfoAdapter extends RecyclerView.Adapter<RedPacketCourseInfoAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<RedEnvelopeInfo> mData;
    private LayoutInflater mInflate;

    public RedPacketCourseInfoAdapter(Context context) {
        mContext = context;
        mInflate = LayoutInflater.from(mContext);
        mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.item_red_packets_course, parent, false);
        return new RedPacketCourseInfoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RedPacketCourseInfoAdapter.ViewHolder holder, int position) {
        final RedEnvelopeInfo mResult = mData.get(position);
        if (mResult.classTitle != null) {
            holder.tv_title.setText(mResult.classTitle);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder("");
        builder.append("助学红包已领取" + mResult.receiveSum);
        builder.setSpan(new ForegroundColorSpan(UniApplicationContext.getContext().getResources().getColor(
                R.color.indicator_color)),
                7, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append("/" + mResult.sumNum + ",");
        SpannableStringBuilder builder1 = new SpannableStringBuilder("");
        if (!TextUtils.isEmpty(mResult.receivePrice) && !TextUtils.isEmpty(mResult.sumPrice)) {
            builder1.append("共" + mResult.receivePrice);
            builder1.setSpan(new ForegroundColorSpan(UniApplicationContext.getContext().getResources().getColor(
                    R.color.indicator_color)),
                    1, builder1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder1.append("/" + mResult.sumPrice + "元");
            builder.append(builder1);
        }
        holder.tv_num.setText(builder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RedPacketDetailActivity.class);
                intent.putExtra("redId", mResult.id);
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(ArrayList<RedEnvelopeInfo> redEnvelopeInfo) {
        mData.addAll(redEnvelopeInfo);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_num)
        TextView tv_num;
        @BindView(R.id.iv_image)
        ImageView iv_image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setTag(this);
        }
    }
}
