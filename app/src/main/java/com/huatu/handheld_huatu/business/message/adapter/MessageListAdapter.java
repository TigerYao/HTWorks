package com.huatu.handheld_huatu.business.message.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.onRecyclerViewItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.MessageBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ljzyuhenda on 16/7/19.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolderMessage> {
    private List<MessageBean.MessageBeanData> mDatas;
    private Context mContext;
    private onRecyclerViewItemClickListener onItemClickListener;
    private SimpleDateFormat mDateFormat;

    public MessageListAdapter(Context context) {
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mContext = context;
    }

    @Override
    public ViewHolderMessage onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_message, null);
        ViewHolderMessage holderSimulation = new ViewHolderMessage(view);

        return holderSimulation;
    }

    @Override
    public void onBindViewHolder(ViewHolderMessage holder, int position) {
        MessageBean.MessageBeanData messageBeanData = mDatas.get(position);

        holder.itemView.setTag(R.id.tag_position_item, position);
        if (0 == messageBeanData.status) {
            holder.iv_circle.setVisibility(View.GONE);
        } else {
            holder.iv_circle.setVisibility(View.VISIBLE);
        }
        if (messageBeanData.content!=null){
        String content=messageBeanData.content;
            if (!TextUtils.isEmpty(content)){
                int startIndex=content.indexOf("<img");
                if(startIndex>0){
                    content = content.substring(0,startIndex);
                    holder.tv_content_message.setText(Html.fromHtml(content));
                }else {
                    String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
                    Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
                    Matcher m_html = p_html.matcher(content);
                    content = m_html.replaceAll(""); //过滤html标签
                    holder.tv_content_message.setText(content);
                }
            }
        }
        holder.tv_title_message.setText(messageBeanData.title);
        String time = mDateFormat.format(new Date(Long.valueOf(messageBeanData.createTime)));
        holder.tv_date.setText(time);
    }

    public MessageBean.MessageBeanData getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) {
            return 0;
        }

        return mDatas.size();
    }

    public class ViewHolderMessage extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_title_message)
        TextView tv_title_message;
        @BindView(R.id.tv_date)
        TextView tv_date;
        @BindView(R.id.iv_circle)
        ImageView iv_circle;
        @BindView(R.id.tv_content_message)
        TextView tv_content_message;

        public ViewHolderMessage(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, (int) view.getTag(R.id.tag_position_item), -1);
            }
        }
    }

    public List<MessageBean.MessageBeanData> getMessageList() {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }

        return mDatas;
    }

    public void setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
