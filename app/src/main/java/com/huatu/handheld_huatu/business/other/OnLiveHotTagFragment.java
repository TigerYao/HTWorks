package com.huatu.handheld_huatu.business.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.lessons.LiveSearchKeyword;
import com.huatu.handheld_huatu.business.lessons.bean.FaceToFaceCourseBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.FlowLayout;
import com.huatu.handheld_huatu.view.ListViewForScroll;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by cjx on 2018\8\28 0028.
 */

public  class OnLiveHotTagFragment extends AbsSettingFragment{

    @BindView(R.id.xi_layout_loading)
    CommloadingView mCommloadingView;

    private List<String> historyList = new ArrayList<>();
    private List<String> hotwordsList = new ArrayList<>();


    @BindView(R.id.search_live_hot_grid)
    FlowLayout gridView;

    @BindView(R.id.search_live_history_list)
    ListViewForScroll listViewHistory;

    CommonAdapter<String> mAdapterHistory;

    @BindView(R.id.search_live_history_tips)
    View tvHistoryTips;

    View footerView;

    @Override
    public int getContentView() {
        return R.layout.online_search_tag_layout;
    }

    @Override
    protected void setListener() {
        initAdapter();
        mCommloadingView.setOnRtyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestData();
            }
        });
        footerView = View.inflate(getContext(),R.layout.search_live_history_footer_layout, null);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });

        listViewHistory.setAdapter(mAdapterHistory);
        listViewHistory.addFooterView(footerView);
    }

    @Override
    public void requestData(){
        super.requestData();
        mCommloadingView.showLoadingStatus();
        ServiceExProvider.visit(getSubscription(), RetrofitManager.getInstance().getService().getSearchLiveKeywords(),
                new NetObjResponse<LiveSearchKeyword>() {
                    @Override
                    public void onSuccess(BaseResponseModel<LiveSearchKeyword> model) {
                        mCommloadingView.hide() ;
                        LiveSearchKeyword data = (LiveSearchKeyword) model.data;
                        List<String> hotwords = data.hotwords;
                        List<String> mywords = data.mywords;
                        historyList.clear();
                        if (mywords != null) {
                            historyList.addAll(mywords);
                        }
                        if (Method.isListEmpty(historyList)) {
                            listViewHistory.setVisibility(View.GONE);
                            tvHistoryTips.setVisibility(View.GONE);
                        } else {
                            listViewHistory.setVisibility(View.VISIBLE);
                            tvHistoryTips.setVisibility(View.VISIBLE);
                        }
                        hotwordsList.clear();
                        if (hotwords != null) {
                            hotwordsList.addAll(hotwords);
                        }
                        mAdapterHistory.setDataAndNotify(historyList);
                        gridView.removeAllViews();
                        LayoutInflater  mLayoutInflater = LayoutInflater.from(getContext());
                        for (int i = 0; i < hotwordsList.size(); i++) {
                            View view = mLayoutInflater.inflate(R.layout.gridview_item_hotwords, null);
                            TextView tvWord = (TextView) view.findViewById(R.id.bt_grid);
                            final String strWord = hotwordsList.get(i);
                            tvWord.setText(strWord);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    OnLiveSearchActivity activity = (OnLiveSearchActivity) getActivity();
                                    activity.doSearch(strWord,1,-1);
                                }
                            });
                            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp.rightMargin = DisplayUtil.dp2px(15);
                            lp.bottomMargin = DisplayUtil.dp2px(15);
                            gridView.addView(view, lp);
                         }
                     }

                    @Override
                    public void onError(String message, int type) {
                       // ToastUtils.showMessage(message);
                        if(type==3) {
                           mCommloadingView.showNetworkTip();
                        }else {
                           mCommloadingView.showServerError();
                        }
                    }

                });

    }

    private void initAdapter() {
        mAdapterHistory = new CommonAdapter<String>(historyList, R.layout.course_search_lishi) {
            @Override
            public void convert(ViewHolder holder, final String item, int position) {
                holder.setText(R.id.course_search_history_tv, item);
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        OnLiveSearchActivity activity = (OnLiveSearchActivity) getActivity();
                        activity.doSearch(item, -1,1);
                    }
                });
                holder.setViewOnClickListener(R.id.course_search_history_delete_btn, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteHistory(item);
                    }
                });
            }
        };
   }

    private void clearHistory() {
        ((SimpleBaseActivity)getActivity()).showProgress();
        ServiceProvider.clearLiveSearchHistory(getSubscription(), new NetResponse() {
            @Override
            public void onSuccess(BaseResponseModel model) {
                ((SimpleBaseActivity)getActivity()).hideProgess();
                ToastUtils.showShort("清除搜索历史成功");
                historyList.clear();
                mAdapterHistory.setDataAndNotify(historyList);
                listViewHistory.setVisibility(View.GONE);
                tvHistoryTips.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                ((SimpleBaseActivity)getActivity()).hideProgess();
            }
        });
    }

    private void deleteHistory(final String word) {
        ((SimpleBaseActivity)getActivity()).showProgress();
        ServiceProvider.deleteLiveSearchKeyword(getSubscription(), word, new NetResponse(){
            @Override
            public void onSuccess(BaseResponseModel model) {
                ((SimpleBaseActivity)getActivity()).hideProgess();
                ToastUtils.showShort("删除搜索历史成功");
                for(int i = 0; i < historyList.size(); i++) {
                    if(Method.isEqualString(word, historyList.get(i))) {
                        historyList.remove(i);
                        break;
                    }
                }
                mAdapterHistory.setDataAndNotify(historyList);
                if(Method.isListEmpty(historyList)) {
                    listViewHistory.setVisibility(View.GONE);
                    tvHistoryTips.setVisibility(View.GONE);
                } else {
                    listViewHistory.setVisibility(View.VISIBLE);
                    tvHistoryTips.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                ((SimpleBaseActivity)getActivity()).hideProgess();
                ToastUtils.showShort("删除搜索历史失败");
            }
        });
    }
}
