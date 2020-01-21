package com.huatu.handheld_huatu.business.me.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.AbsHtEventFragment;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.customview.ViewRadarMap;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.me.tree.BasicTreeAdapter;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.event.me.ErrorPapersMessageEvent;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.me.ErrorTopBean;
import com.huatu.handheld_huatu.mvpmodel.me.TreeViewBean;
import com.huatu.handheld_huatu.mvppresenter.me.ErrorPapersImpl;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.AppUtils;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.XListView;
import com.netease.hearttouch.router.HTPageRouterCall;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 错题重练
 */

public class ErrorPapersListFragment extends AbsHtEventFragment {

    private static String TAG = "ErrorPapersListFragment";

    private CommonErrorView errorView;

    private View viewGuideBg;                                           // 引导背景
    private ImageView ivGuide;                                          // 引导图

    private ImageView ivBack;
    private ImageView ivExport;                                         // 错题导出
    private ImageView ivClearAll;
    private ListView listview;
    private ErrorAdapter errorAdapter;
    private ImageView ivTitleRight;
    private ErrorPapersImpl mPresenter;
    private View vHeader;
    private int firstVisibleItem;

    private View viewSpace;                                             // header 的空格

    private RelativeLayout rlNoData;                                    // 无数据显示布局
    private TextView tvGoExercise;                                      // 去考试
    private ImageView ivExplain;                                        // 问号解释

    private RelativeLayout rlUpContent;                                 // 有数据显示布局

    private ViewRadarMap radarMap;                                      // 雷达图
    private TextView tvRaise;                                           // 能力提升
    private TextView tvStrengthen;                                      // 巩固练习
    private TextView tvWrongDown;                                       // 错题数
    private int errCount;

    private List<TreeViewBean> treeViews = new ArrayList<>();

    @Override
    public int onSetRootViewId() {
        return R.layout.activity_error;
    }

    @Override
    protected void onInitView() {
        super.onInitView();

        errorView = rootView.findViewById(R.id.arena_exam_main_error_layout);

        viewGuideBg = rootView.findViewById(R.id.view_tip_bg);
        ivGuide = rootView.findViewById(R.id.iv_guide);

        ivBack = rootView.findViewById(R.id.iv_back);
        ivExport = rootView.findViewById(R.id.iv_export);
        ivClearAll = rootView.findViewById(R.id.iv_title_clear);
        listview = rootView.findViewById(R.id.listview);
        ivTitleRight = rootView.findViewById(R.id.iv_title_right);

        mPresenter = new ErrorPapersImpl(compositeSubscription);

        if (Method.isActivityFinished(mActivity)) {
            mPresenter.finish();
        }

        vHeader = LayoutInflater.from(mActivity).inflate(R.layout.err_header_radar_layout, listview, false);
        listview.addHeaderView(vHeader);
        vHeader.setOnClickListener(null);

        findHeaderView();

        errorAdapter = new ErrorAdapter(listview, mActivity, treeViews, 0);
        errorAdapter.setOffset(1);
        listview.setAdapter(errorAdapter);

        setListener();
    }

    /**
     * 引导图
     */
    private void initGuide() {
        if (SpUtils.getErrorGuideStatee() != AppUtils.getVersionCode()) {    // 不等于当前版本号，就显示引导

            viewGuideBg.setVisibility(View.VISIBLE);
            ivGuide.setVisibility(View.VISIBLE);

            viewGuideBg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    viewGuideBg.setVisibility(View.GONE);
                    ivGuide.setVisibility(View.GONE);
                }
            });

            SpUtils.setErrorGuideState();
        }
    }

    private void findHeaderView() {

        viewSpace = vHeader.findViewById(R.id.view_space);

        rlNoData = vHeader.findViewById(R.id.rl_no_data);
        tvGoExercise = vHeader.findViewById(R.id.tv_go_exercise);
        ivExplain = vHeader.findViewById(R.id.iv_explain);

        rlUpContent = vHeader.findViewById(R.id.rl_up_content);

        radarMap = vHeader.findViewById(R.id.view_radar);
        tvRaise = vHeader.findViewById(R.id.tv_raise);
        tvStrengthen = vHeader.findViewById(R.id.tv_strengthen);
        tvWrongDown = vHeader.findViewById(R.id.tv_wrong_count_down);

        tvGoExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    @Override
    protected void onLoadData() {
        loadData();
    }

    private void setListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.finish();
            }
        });
        ivExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyCourseStatistic.clickStatistic("题库", "错题重练", "错题导出");
                Intent sourceIntent = new Intent();
                HTPageRouterCall.newBuilderV2("ztk://arena/export/error")
                        .context(mActivity)
                        .sourceIntent(sourceIntent)
                        .build()
                        .start();
            }
        });
        ivTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.postEvent(ErrorPapersMessageEvent.AEP_MSG_SettingErrorPaperDoCountFragment_EFORM_ErrorPapersListFragment);
            }
        });
        listview.setOnScrollListener(new XListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItemv, int visibleItemCount, int totalItemCount) {
                firstVisibleItem = firstVisibleItemv;
            }
        });
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        ivExplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹窗介绍
                String content = "为准确评估您对知识点的掌握情况，系统会每一小时统计一次，并非实时更新";
                final CustomConfirmDialog dialog = DialogUtils.createDialog(mActivity, "提示", content);
                dialog.setBtnDividerVisibility(false);
                dialog.setCancelBtnVisibility(false);
                dialog.setMessage(content, 15);
                dialog.setContentGravity(Gravity.CENTER_HORIZONTAL);
                dialog.setOkBtnConfig(200, 50, R.drawable.eva_explain_btn_bg);
                dialog.setPositiveColor(Color.parseColor("#FFFFFF"));
                dialog.setPositiveButton("我知道啦", null);
                dialog.setTitleBold();
                View contentView = dialog.getContentView();
                LinearLayout llBtn = contentView.findViewById(R.id.ll_btn);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBtn.getLayoutParams();
                layoutParams.height = DisplayUtil.dp2px(66);
                contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        tvStrengthen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (errCount == 0) {
                    ToastUtils.showMessage("你还没有错题哦");
                    return;
                }
                if (SpUtils.getErrorQuestionMode() == 0) {           // 做题模式
                    reformError(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, -1, errCount);
                } else {                                             // 背题模式
                    reformError(ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI, -1, errCount);
                }
            }
        });
        tvRaise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = "工程师正在紧张开发中\n敬请期待";
                final CustomConfirmDialog dialog = DialogUtils.createDialog(mActivity, " ", content);
                dialog.setBtnDividerVisibility(false);
                dialog.setCancelBtnVisibility(false);
                dialog.setTitleSize(6);
                dialog.setMessage(content, 16);
                dialog.setOkBtnConfig(200, 50, R.drawable.eva_explain_btn_bg);
                dialog.setPositiveColor(Color.parseColor("#FFFFFF"));
                dialog.setPositiveButton("确定", null);
                View contentView = dialog.getContentView();
                LinearLayout llBtn = contentView.findViewById(R.id.ll_btn);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBtn.getLayoutParams();
                layoutParams.height = DisplayUtil.dp2px(66);
                contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        ivClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (errCount == 0) {
                    ToastUtils.showMessage("你还没有错题哦");
                    return;
                }
                // 弹窗介绍
                String content = "是否确定清空全部错题本";
                DialogUtils.onShowRedConfirmDialog(mActivity, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearErrAll();
                    }
                }, null, " ", content, "取消", "确定");
            }
        });
    }

    @Override
    protected void onSaveState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        outState.putBoolean("onSaveState", true);
        super.onSaveState(outState);
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        if (savedInstanceState != null) {
            loadData();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ArenaDataCache.getInstance().isDeletePaper) {
            ArenaDataCache.getInstance().isDeletePaper = false;
            loadData();
        }
    }

    private void loadData() {
        if (!NetUtil.isConnected()) {
            loadFailed(1);
            return;
        }

        mActivity.showProgress();

        getTopData();

        getErrorQuestion();
    }

    private void getErrorQuestion() {

        ServiceProvider.getErrorList(compositeSubscription, new NetResponse() {
            @Override
            public void onError(Throwable e) {
                mActivity.hideProgress();
                loadFailed(3);

            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                mActivity.hideProgress();
                if (model != null && model.data != null) {
                    treeViews.clear();
                    treeViews.addAll(model.data);
                    dealTreeData();
                } else {
                    loadFailed(3);
                }
            }
        });

    }

    private void getTopData() {
        Subscription subscription = RetrofitManager.getInstance().getService().getErrorTopData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel<ArrayList<ErrorTopBean>>>() {
                    @Override
                    public void onCompleted() {
                        initGuide();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.hideProgress();
                        loadFailed(3);
                    }

                    @Override
                    public void onNext(BaseResponseModel<ArrayList<ErrorTopBean>> data) {
                        mActivity.hideProgress();
                        if (data.code == 1000000) {
                            if (data.data != null && data.data.size() > 0) {
                                ArrayList<ErrorTopBean> errorPointList = data.data;

                                int count = 0;

                                for (ErrorTopBean errorTopBean : errorPointList) {
                                    count += errorTopBean.getNum();
                                }

                                if (count < 150) {
                                    setNoRadarData();
                                    return;
                                }

                                int size = errorPointList.size();
                                String[] names = new String[size];
                                Double[] mapData01 = new Double[size];
                                for (int i = 0; i < size; i++) {
                                    names[i] = errorPointList.get(i).getName();
                                    mapData01[i] = errorPointList.get(i).getAccuracy();
                                }
                                ArrayList<Double[]> mapData = new ArrayList<>();
                                mapData.add(mapData01);
                                radarMap.setData(mapData, names, true);
                            } else {
                                setNoRadarData();
                            }
                        } else {
                            loadFailed(3);
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    /**
     * 清除我的错题
     */
    private void clearErrAll() {
        if (!NetUtil.isConnected()) {
            loadFailed(1);
            return;
        }

        mActivity.showProgress();

        Subscription subscription = RetrofitManager.getInstance().getService().clearErrorList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponseModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mActivity.hideProgress();
                        ToastUtils.showShort("清空失败");
                    }

                    @Override
                    public void onNext(BaseResponseModel data) {
                        mActivity.hideProgress();
                        if (data != null) {
                            if (data.code == 1000000) {
                                ToastUtils.showShort("清空成功");
                                errCount = 0;
                                onLoadData();
                            } else {
                                ToastUtils.showShort(data.message);
                            }
                        } else {
                            ToastUtils.showShort("清空失败");
                        }
                    }
                });

        compositeSubscription.add(subscription);
    }

    private void dealTreeData() {
        if (treeViews != null && treeViews.size() > 0) {
            // 计算总错题数
            errCount = 0;
            for (TreeViewBean datum : treeViews) {
                if (datum.getLevel() == 0) {
                    errCount += datum.getWnum();
                }
            }
            tvWrongDown.setText(errCount + "道错题");

            errorView.setVisibility(View.GONE);
            updateTreeExpandState(treeViews);
            int topOffset = 0;
            if (errorAdapter != null && listview != null && listview.getChildAt(0) != null) {
                topOffset = listview.getChildAt(0).getTop();
            }

            errorAdapter = new ErrorAdapter(listview, mActivity, treeViews, 0);
            errorAdapter.setOffset(1);
            listview.setAdapter(errorAdapter);

            listview.setSelectionFromTop(firstVisibleItem, topOffset);
            errorAdapter.notifyDataSetChanged();
        } else {
            if (errorAdapter != null && errorAdapter.getVisiableTrees() != null) {
                errorAdapter.getVisiableTrees().clear();
                errorAdapter.notifyDataSetChanged();
            }
            tvWrongDown.setText(errCount + "道错题");
        }
    }

    private final SparseBooleanArray treeNodeSpare = new SparseBooleanArray();

    private void updateTreeExpandState(List<TreeViewBean> data) {
        if (data == null) {
            return;
        }
        for (TreeViewBean bean : data) {
            traversalSetTreeNodeExpandFlag(bean);
        }
    }

    private void traversalSetTreeNodeExpandFlag(TreeViewBean bean) {
        if (bean == null) {
            return;
        }
        int key = bean.getId();
        if (treeNodeSpare.get(key)) {
            bean.setExpand(true);
        }
        if (bean.getChildren() != null && bean.getChildren().size() > 0) {
            for (TreeViewBean childBean : bean.getChildren()) {
                traversalSetTreeNodeExpandFlag(childBean);
            }
        }
    }

    private void traversalTreeExpandFlag(List<TreeViewBean> data) {
        if (data == null) {
            return;
        }
        treeNodeSpare.clear();
        for (TreeViewBean bean : data) {
            traversalTreeNodeExpandFlag(bean);
        }
        LogUtils.i("treeNodeSpare: " + treeNodeSpare.toString());
    }

    private void traversalTreeNodeExpandFlag(TreeViewBean bean) {
        if (bean == null) {
            return;
        }
        if (bean.isExpand()) {
            treeNodeSpare.put(bean.getId(), true);
        }
        if (bean.getChildren() != null && bean.getChildren().size() > 0) {
            for (TreeViewBean childBean : bean.getChildren()) {
                traversalTreeNodeExpandFlag(childBean);
            }
        }
    }

    private class ErrorAdapter extends BasicTreeAdapter {
        ErrorAdapter(ListView listView, Context context, List<TreeViewBean> trees, int def) {
            super(listView, context, trees, def);
        }

        @Override
        public View getConverView(final TreeViewBean tree, int position, View converView, ViewGroup parent) {
            Holder holder;
            if (converView == null) {
                holder = new Holder();

                converView = inflater.inflate(R.layout.item_error_question_tree, parent, false);

                holder.rootView = converView.findViewById(R.id.root_view);
                holder.text_name = converView.findViewById(R.id.text_name);
                holder.text_err_number = converView.findViewById(R.id.text_err_number);
//                holder.text_look = converView.findViewById(R.id.text_look);
//                holder.text_redo = converView.findViewById(R.id.text_redo);
                holder.image_top = converView.findViewById(R.id.image_top);
                holder.image_select = converView.findViewById(R.id.image_select);
                holder.image_bottom = converView.findViewById(R.id.image_bottom);
                holder.rl_go = converView.findViewById(R.id.rl_go);
//                holder.view_line = converView.findViewById(R.id.view_line);

                converView.setTag(holder);
            } else {
                holder = (Holder) converView.getTag();
            }
            holder.text_name.setText(tree.getName());

            int level = tree.getLevel();
            boolean expand = tree.isExpand();
            List<TreeViewBean> children = tree.getChildren();

            boolean end = tree.isEnd();
            if (level == 0) {
                holder.rootView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if (expand) {
                    holder.image_select.setBackgroundResource(R.mipmap.tree_indicator1_expand);
                    holder.image_top.setVisibility(View.INVISIBLE);
                    holder.image_bottom.setVisibility(View.VISIBLE);
                } else {
                    holder.image_select.setBackgroundResource(R.mipmap.tree_indicator1_fold);
                    holder.image_top.setVisibility(View.INVISIBLE);
                    holder.image_bottom.setVisibility(View.INVISIBLE);
                }
            } else if (level == 1) {
                holder.rootView.setBackgroundColor(Color.parseColor("#FFF8F8"));
                holder.image_top.setVisibility(View.VISIBLE);
                if (expand) {
                    holder.image_select.setBackgroundResource(R.mipmap.tree_indicator2_expand);
                    holder.image_bottom.setVisibility(View.VISIBLE);
                } else {
                    if (end) {
                        holder.image_bottom.setVisibility(View.INVISIBLE);
                    } else {
                        holder.image_bottom.setVisibility(View.VISIBLE);
                    }
                    holder.image_select.setBackgroundResource(R.mipmap.tree_indicator2_fold);
                }
            } else if (level == 2) {
                holder.rootView.setBackgroundColor(Color.parseColor("#FFF0F1"));
                holder.image_top.setVisibility(View.VISIBLE);
                holder.image_select.setBackgroundResource(R.mipmap.tree_indicator3);
                if (end) {
                    holder.image_bottom.setVisibility(View.INVISIBLE);
                } else {
                    holder.image_bottom.setVisibility(View.VISIBLE);
                }
            }

//            if (level == 0) {
//                if (position == 0) {
//                    holder.view_line.setVisibility(View.INVISIBLE);
//                } else {
//                    holder.view_line.setVisibility(View.VISIBLE);
//                }
//            } else {
//                holder.view_line.setVisibility(View.INVISIBLE);
//            }

            final int wnum = tree.getWnum();
            String errNum = String.valueOf(wnum);
            SpannableString spannableString = new SpannableString(errNum + "道错题");
            int index = errNum.length();
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.common_style_text_color)), 0,
                    index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.text_err_number.setText(spannableString);

            holder.rl_go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    traversalTreeExpandFlag(getVisiableTrees());
                    if (SpUtils.getErrorQuestionMode() == 0) {           // 做题模式
                        reformError(ArenaConstant.EXAM_ENTER_FORM_TYPE_CUOTI_LIANXI, tree.getId(), wnum);
                    } else {                                             // 背题模式
                        reformError(ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_CUOTI_LIANXI, tree.getId(), wnum);
                    }
                }
            });

//            holder.text_look.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    traversalTreeExpandFlag(getVisiableTrees());
//                    getErrorIdsLosts(tree.getId(), wnum);
//                }
//            });
//
//            holder.text_redo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    traversalTreeExpandFlag(getVisiableTrees());
//                    reformError(tree.getId(), wnum);
//                }
//            });
            return converView;
        }
    }

    class Holder {
        View rootView;
        TextView text_name;
        TextView text_err_number;
        ImageView image_top;
        ImageView image_select;
        ImageView image_bottom;
        RelativeLayout rl_go;
//        View view_line;
//        TextView text_look;
//        TextView text_redo;
    }

    /**
     * 去做题
     */
    private void reformError(int requestType, int pointId, int errNum) {
        if (errNum == 0) {
            CommonUtils.showToast("该知识点暂时没有错题重做");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("point_ids", pointId);
        bundle.putInt("fromType", requestType);
        ArenaExamActivityNew.show(mActivity, requestType, bundle);
    }

    /**
     * 1、如果头部数据错题量累加为0，则显示无数据
     * 2、头部数据data为空，则显示无数据
     * 3、如果错题data返回的为空，则显示无数据
     */
    private void setNoRadarData() {
        viewSpace.setVisibility(View.GONE);
        rlNoData.setVisibility(View.VISIBLE);
        rlUpContent.setVisibility(View.GONE);
    }

    /**
     * @param flag 1、无网络
     *             2、无数据
     *             3、获取数据失败
     */
    private void loadFailed(int flag) {
        errorView.setVisibility(View.VISIBLE);
        switch (flag) {
            case 1:
                errorView.setErrorText("网络不太好，点击屏幕，刷新看看");
                errorView.setErrorImage(R.drawable.icon_common_net_unconnected);
                break;
            case 2:
                errorView.setErrorText("什么都没有");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
            case 3:
                errorView.setErrorText("获取数据失败，点击重试");
                errorView.setErrorImage(R.drawable.no_data_bg);
                break;
        }
        errorView.setErrorImageVisible(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    public static ErrorPapersListFragment newInstance(Bundle extra) {
        ErrorPapersListFragment fragment = new ErrorPapersListFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }
}
