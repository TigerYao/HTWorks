package com.huatu.handheld_huatu.business.me.order;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetListResponse;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.me.LogisticBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by cjx on 2018\10\17 0017.
 */

public class LogisticListFragment extends AbsSettingFragment {

    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    LogisticListAdapter mListAdapter;

    @Override
    public int getContentView() {
        return R.layout.comm_recyclerlist_nopull_fragment;
    }


    @Override
    public void requestData() {
        super.requestData();
        mCommloadingView.showLoadingStatus();
        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListAdapter=new LogisticListAdapter(getActivity(),new ArrayList<LogisticBean>());
        mWorksListView.setRecyclerAdapter(mListAdapter);
        mListAdapter.setOnViewItemClickListener(new OnRecItemClickListener() {
            @Override
            public void onItemClick(int position, View view, int type) {
                if(!NetUtil.isConnected()){
                    ToastUtils.showShort("当前网络不可用");
                    return;
                }
                LogisticBean curBean=  mListAdapter.getItem(position);
                if(curBean==null) return;
                LogisticDetailActivity.newInstance(getActivity(),curBean.OrderNum);
            }
        });

    }


    private void loadData(){

        ServiceExProvider.visitList(getSubscription(), CourseApiService.getApi().getAllLogistics(), new NetListResponse<LogisticBean>() {
            @Override
            public void onSuccess(BaseListResponseModel<LogisticBean> model) {

            }

            @Override
            public void onError(String message, int type) {

            }
        });
    }

    public class LogisticListAdapter extends SimpleBaseRecyclerAdapter<LogisticBean> {

        public LogisticListAdapter(Context context, List<LogisticBean> items) {
            super(context, items);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.course_my_list_item, parent, false);
            return new MoreViewHolder(collectionView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            MoreViewHolder holderfour = (MoreViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }


        protected class MoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mTitle;

            TextView mTimeLength;
            ImageView mCoverImg;

            MoreViewHolder(View itemView) {
                super(itemView);

                mTitle=(TextView) itemView.findViewById(R.id.tv_item_course_mine_title);
                mTimeLength=(TextView)itemView.findViewById(R.id.tv_item_course_mine_timelength);
                mCoverImg=(ImageView)itemView.findViewById(R.id.iv_item_course_mine_scaleimg);
                itemView.findViewById(R.id.rush_sale_progress).setVisibility(View.GONE);
                itemView.findViewById(R.id.learn_tip_txt).setVisibility(View.GONE);;
                itemView.findViewById(R.id.whole_content).setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.whole_content:
                        if (onRecyclerViewItemClickListener != null)
                            onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);
                        break;
                }
            }

            public void bindUI(LogisticBean bean, int pos) {
                mTitle.setText(bean.Title+"");
                mTimeLength.setText(bean.ExpressCorp+" 快递单号 "+bean.ExpressNo);
                ImageLoad.displaynoCacheImage(mContext,R.mipmap.load_default,bean.scaleimg,mCoverImg);

            }
        }


    }
}
