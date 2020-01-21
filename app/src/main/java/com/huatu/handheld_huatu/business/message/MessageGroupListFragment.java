package com.huatu.handheld_huatu.business.message;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.message.adapter.MessageGroupAdapter;
import com.huatu.handheld_huatu.business.message.model.MessageGroupData;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.ui.PullRefreshRecyclerView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorViewExsc;
import com.huatu.handheld_huatu.view.TopActionBar;
import com.huatu.library.PullToRefreshBase;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.netease.hearttouch.router.HTRouter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by saiyuan on 2019/2/27.
 *  消息中心
 */
@HTRouter(url = {"ztk://noticeCenter{fragment2}"},  needLogin = true)
public class MessageGroupListFragment extends BaseFragment implements IonLoadMoreListener, MessageGroupAdapter.OnDeleteClickListener {

    @BindView(R.id.top_title)
    TopActionBar top_title;
    @BindView(R.id.xi_comm_page_list)
    PullRefreshRecyclerView prv_message_group_list;

    @BindView(R.id.view_no_data)
    CommonErrorViewExsc view_no_data;
    private MessageGroupAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription;
    private List<MessageGroupData> mData = new ArrayList<>();

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_message_group_list;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        initTitleBar();
        onPrepare();
        setListener();
    }

    public void initTitleBar() {
        top_title.setTitle("消息");
        top_title.showButtonText("全部已读", TopActionBar.RIGHT_AREA, R.color.blackF4);
        top_title.setButtonClickListener(new TopActionBar.OnTopBarButtonClickListener() {
            @Override
            public void onLeftButtonClick(View view) {
                mActivity.finish();
            }

            @Override
            public void onRightButton2Click(View view) {

            }

            @Override
            public void onRightButtonClick(View view) {
                StudyCourseStatistic.clickStatistic("我的","消息","全部已读");
                ServiceProvider.readAllMessage(mCompositeSubscription, new NetResponse() {
                    @Override
                    public void onSuccess(BaseResponseModel model) {
                        super.onSuccess(model);
                        loadMessageData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        ToastUtils.showEssayToast("网络出错了，请稍后重试");
                    }
                });
            }
        });
    }

    protected void onPrepare() {
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        mAdapter = new MessageGroupAdapter(mActivity, this);

        prv_message_group_list.getRefreshableView().setPagesize(20);
        prv_message_group_list.getRefreshableView().setImgLoader(ImageLoad.getRequestManager(getActivity()));
        prv_message_group_list.getRefreshableView().addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));
        prv_message_group_list.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        // 加载过程中是否可以滑动
        prv_message_group_list.setPullToRefreshOverScrollEnabled(true);
        prv_message_group_list.getRefreshableView().setRecyclerAdapter(mAdapter);

    }


    protected void setListener() {
        prv_message_group_list.getRefreshableView().setOnLoadMoreListener(this);
        prv_message_group_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerViewEx>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerViewEx> refreshView) {
                superOnRefresh();
            }
        });
        view_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                superOnRefresh();
            }
        });
    }


    @Override
    protected void onLoadData() {

    }

    private void loadMessageData() {
        mActivity.showProgress();
        ServiceProvider.getMessageGroupList(mCompositeSubscription, new NetResponse() {
            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                mActivity.hideProgress();
                prv_message_group_list.onRefreshComplete();
                if (model != null && model.data != null) {
                    mData = model.data;
                    mAdapter.setData(mData);
                }
                view_no_data.setVisibility(View.GONE);
                if (mData.size() == 0) {
                    showEmptyView(3);
                }
                prv_message_group_list.getRefreshableView().checkloadMore(0);
                prv_message_group_list.getRefreshableView().hideloading();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
                prv_message_group_list.onRefreshComplete();
                showEmptyView(1);

            }
        });
    }

    private void showEmptyView(int type) {
        view_no_data.setVisibility(View.VISIBLE);
        switch (type) {
            case 1:
                view_no_data.setErrorText("网络不好，点击重试！");
                break;
            case 3:
                view_no_data.setErrorText("暂时没有消息哦");
                break;
        }
    }

    private void superOnRefresh() {
        mData.clear();
        loadMessageData();
    }

    @Override
    public void OnLoadMoreEvent(boolean isRetry) {

    }

    @Override
    public void onResume() {
        super.onResume();
        loadMessageData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);

    }

    @Override
    public void onDeleteClick(final int position) {
//        DialogUtils.onShowConfirmDialog(mActivity, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteMessageGroup(position);
//            }
//        },null,"真的要删除吗？","取消","确定");
    }

//    private void deleteMessageGroup(int position) {
//        //删除消息组
//        if (mData.size()>position){
//        final MessageGroupData item=mData.get(position);
//        ServiceProvider.deleteMessageGroup(mCompositeSubscription,item.view,new NetResponse(){
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//                ToastUtils.showEssayToast("删除失败了，请稍后重试");
//            }
//
//            @Override
//            public void onSuccess(BaseResponseModel model) {
//                super.onSuccess(model);
//                superOnRefresh();
//            }
//         });
//        }
//    }
}
