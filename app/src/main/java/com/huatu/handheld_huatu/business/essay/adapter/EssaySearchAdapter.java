package com.huatu.handheld_huatu.business.essay.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.onRecyclerViewItemClickListener;
import com.huatu.handheld_huatu.business.essay.bean.EssaySearchData;
import com.huatu.handheld_huatu.business.essay.mainfragment.EssayAllResultListFragment;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht on 2018/2/22.
 * 申论搜索适配器
 */
public class EssaySearchAdapter extends RecyclerView.Adapter<EssaySearchAdapter.ViewHolderSimulation> {

    private Context mContext;
    private List<EssaySearchData> mData;
    private onRecyclerViewItemClickListener onItemClickListener;
    private String mKeyWord;
    private EssaySearchContentAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription;

    public EssaySearchAdapter(Context mContext, CompositeSubscription mCompositeSubscription) {
        this.mContext = mContext;
        this.mCompositeSubscription = mCompositeSubscription;
    }

    @Override
    public ViewHolderSimulation onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.item_essay_search, null);
        ViewHolderSimulation holderSimulation = new ViewHolderSimulation(view);
        return holderSimulation;
    }

    @Override
    public void onBindViewHolder(ViewHolderSimulation holder, int position) {
        final EssaySearchData mResult = mData.get(position);
        holder.itemView.setTag(R.id.tag_position_item, position);
        mAdapter = new EssaySearchContentAdapter(mContext, mCompositeSubscription);
//        if (holder.rlv_search.getAdapter()==null){
        holder.rlv_search.setAdapter(mAdapter);
//        }
        if (mResult.data.totalElements <= 3) {
            holder.tv_all_content.setVisibility(View.GONE);
        } else {
            holder.tv_all_content.setVisibility(View.VISIBLE);
        }
        mAdapter.setData(mResult.data.content, mResult.type, mKeyWord);
        if (mResult.typeName != null) {
            holder.tv_label.setText(mResult.typeName);
        }
        if (mResult.type == 0) {
            holder.iv_type_bottom.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.essay_single_pink_bottom));
        } else if (mResult.type == 1) {
            holder.iv_type_bottom.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.essay_multi_pink_bottom));
        } else {
            holder.iv_type_bottom.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.essay_argue_pink_bottom));
        }
        holder.tv_all_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看全部
                StudyCourseStatistic.clickStatistic("题库->申论", mResult.typeName + "搜索结果", "查看全部");
                EssayAllResultListFragment.launch(mContext, mResult.type, mResult.typeName, mKeyWord);
            }
        });
    }

    public EssaySearchData getItem(int position) {
        return mData.get(position);
    }

    public List<EssaySearchData> getDataList() {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        return mData;
    }

    public void setOnRecyclerViewItemClickListener(onRecyclerViewItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    public void setData(ArrayList<EssaySearchData> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolderSimulation extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_type_bottom)
        ImageView iv_type_bottom;
        @BindView(R.id.tv_label)
        TextView tv_label;
        @BindView(R.id.tv_all_content)
        TextView tv_all_content;
        @BindView(R.id.rlv_search)
        RecyclerView rlv_search;

        public ViewHolderSimulation(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            rlv_search.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, (int) view.getTag(R.id.tag_position_item), -1);
            }
        }
    }

    public void setKeyWords(String keyWords) {
        mKeyWord = keyWords;
    }
}
