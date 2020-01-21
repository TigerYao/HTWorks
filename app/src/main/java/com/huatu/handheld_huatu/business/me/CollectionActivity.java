package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.business.me.tree.BasicTreeAdapter;
import com.huatu.handheld_huatu.datacache.ArenaDataCache;
import com.huatu.handheld_huatu.mvpmodel.me.CollectionIdsBean;
import com.huatu.handheld_huatu.mvpmodel.me.TreeBasic;
import com.huatu.handheld_huatu.mvpmodel.me.TreeViewBean;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.ShadowDrawable;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomDialog;
import com.huatu.utils.DensityUtils;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 收藏夹
 */
public class CollectionActivity extends BaseActivity {
    private final static int MODE_SELECTED_ALL = 1;
    private final static int MODE_CANCEL_ALL = 2;

    @BindView(R.id.xi_toolbar)
    TitleBar mTopTitleBar;
    @BindView(R.id.ll_bottom)
    View mBottomView;
    @BindView(R.id.arena_exam_main_error_layout)
    CommonErrorView errorView;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.tv_select_all)
    View allSelectView;
    @BindView(R.id.pop_title_layout)
    LinearLayout mPopBar;
    @BindView(R.id.selected_tv)
    TextView mSelectedTv;
    @BindView(R.id.view_line)
    View mLineView;
    @BindView(R.id.ll_confirm)
    View mConfirmView;

    private CollectionAdapter collectionAdapter;
    private CustomDialog customDialog;
    private boolean isAllSelected = false;
    private QMUIPopup mNormalPopup;
    private int mLastSelected = 0;
    private int mode = 0;
    private CustomConfirmDialog customConfirmDialog, downloadTipDialog;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_collection;
    }

    @Override
    protected void onInitView() {
        mTopTitleBar.setTitle("收藏");
        mTopTitleBar.setDisplayHomeAsUpEnabled(true);
        mTopTitleBar.setOnTitleBarMenuClickListener(new TitleBar.OnTitleBarMenuClickListener() {
            @Override
            public void onMenuClicked(TitleBar titleBar, MenuItem menuItem) {
                if (menuItem.getId() == R.id.xi_title_bar_home) {
                    CollectionActivity.this.finish();
                } else if (menuItem.getId() == android.R.id.button1) {
                    boolean isSelected = menuItem.isSelected();
                    menuItem.setText(!isSelected ? "取消" : "导出");
                    menuItem.setSelected(!isSelected);
                    collectionAdapter.notifyDataSetChanged();
                    mBottomView.setVisibility(!isSelected ? View.VISIBLE : View.GONE);
                    listview.setPadding(0, 0, 0, !isSelected ? DisplayUtil.dp2px(48) : 0);
//                    CollectionActivity.this.finish();
                } else if (menuItem.getId() == android.R.id.button2) {
                    showGuideTip();
                }
            }
        });
        mTopTitleBar.add("导出", 0xff5D9AFF, android.R.id.button1);
        mTopTitleBar.findMenuItem(android.R.id.button1).setSelected(false);
        mTopTitleBar.add(ResourceUtils.getDrawable(R.mipmap.arean_collect_question), android.R.id.button2);

        setListener();
        ShadowDrawable.setShadowDrawable(mBottomView, Color.parseColor("#ffffff"), DensityUtils.dp2px(this, 8),
                Color.parseColor("#66000000"), DensityUtils.dp2px(this, 8), 0, 0, ShadowDrawable.SHAPE_ROUND_TOP);
        mPopBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterWindow(view);
            }
        });
    }

    @Override
    protected void onLoadData() {
        loadData();
    }

    @Override
    public boolean setSupportFragment() {
        return false;
    }

    @Override
    protected int getFragmentContainerId(int clickId) {
        return 0;
    }

    @Override
    public Serializable getDataFromActivity(String tag) {
        return null;
    }

    @Override
    public void updateDataFromFragment(String tag, Serializable data) {

    }

    @Override
    public void onFragmentClickEvent(int clickId, Bundle bundle) {

    }

    private void setListener() {
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CollectionActivity.this.finish();
//            }
//        });
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        allSelectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAllSelected = true;
                if (mode != MODE_SELECTED_ALL) {
                    mode = MODE_SELECTED_ALL;
                    mConfirmView.setBackgroundResource(R.drawable.shape_red_rightcorner_bg);
                    mConfirmView.setSelected(true);
                    collectionAdapter.getSelectedBeans().clear();
                    collectionAdapter.getSelectedBeans().addAll(mAlldata);
                } else {
                    mode = MODE_CANCEL_ALL;
                    mConfirmView.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
                    mConfirmView.setSelected(false);
                    collectionAdapter.getSelectedBeans().clear();
                }
                collectionAdapter.notifyDataSetChanged();
            }
        });
        mConfirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDownloadTip("  本次导出有5题因题目变更无法导出，实际可导出195题，将消耗190图币，是否确认使用？");
            }
        });
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        loadData();
//    }

    private void loadData() {
        if (!NetUtil.isConnected()) {
            loadFailed(1);
            return;
        }
        showProgress();
        Subscription subscription = RetrofitManager.getInstance().getService().getCollectionList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TreeBasic>() {
                    @Override
                    public void onCompleted() {
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        loadFailed(3);
                    }

                    @Override
                    public void onNext(TreeBasic treeBasic) {
                        hideProgress();
                        int code = treeBasic.getCode();
                        List<TreeViewBean> data = treeBasic.getData();
                        if (code == 1000000) {
                            dealwithTreeData(data);
                        } else {
                            loadFailed(3);
                        }
//                            if (code == 1110002) {
//                            if (isFlag) {
//                                show_loadFaile("用户会话过期");
//                            } else {
//                                CommonUtils.showToast("用户会话过期");
//                            }
//                        } else {
//                            if (isFlag) {
//                                show_loadFaile("获取收藏记录失败");
//                            } else {
//                                CommonUtils.showToast("获取收藏记录失败");
//                            }
//                        }
                    }
                });

        compositeSubscription.add(subscription);
    }
    List<TreeViewBean> mAlldata;
    private void dealwithTreeData(List<TreeViewBean> data) {
        if (data != null && data.size() > 0) {
            mAlldata = data;
            errorView.setVisibility(View.GONE);
            collectionAdapter = new CollectionAdapter(listview, CollectionActivity.this, data, 0);
            listview.setAdapter(collectionAdapter);
        } else {
            loadFailed(2);
        }
    }

    private class CollectionAdapter extends BasicTreeAdapter {
        List<TreeViewBean> mSelectedBeans;

        public CollectionAdapter(ListView listView, Context context, List<TreeViewBean> trees, int def) {
            super(listView, context, trees, def);
            mSelectedBeans = new ArrayList<>();
        }

        public List<TreeViewBean> getSelectedBeans() {
            return mSelectedBeans;
        }

        @Override
        public View getConverView(final TreeViewBean tree, int position, View converView, ViewGroup parent) {
            final Holder holder;
            if (converView == null) {
                holder = new Holder();

                converView = inflater.inflate(R.layout.item_collection_tree, parent, false);

                holder.text_name = (TextView) converView.findViewById(R.id.text_name);
                holder.text_err_number = (TextView) converView.findViewById(R.id.text_err_number);
                holder.text_look = (TextView) converView.findViewById(R.id.text_look);
                holder.text_redo = (TextView) converView.findViewById(R.id.text_redo);
                holder.text_redo.setText("查看");
                holder.image_top = (ImageView) converView.findViewById(R.id.image_top);
                holder.image_select = (ImageView) converView.findViewById(R.id.image_select);
                holder.image_bottom = (ImageView) converView.findViewById(R.id.image_bottom);
                holder.view_line = converView.findViewById(R.id.view_line);
                holder.check_view = converView.findViewById(R.id.select_radio);
                converView.setTag(holder);
            } else {
                holder = (Holder) converView.getTag();
            }
            holder.text_name.setText(tree.getName());

            int level = tree.getLevel();
            boolean expand = tree.isExpand();
            List<TreeViewBean> children = tree.getChildren();

            holder.text_look.setVisibility(View.INVISIBLE);
            boolean end = tree.isEnd();
            if (level == 0) {
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
                holder.image_top.setVisibility(View.VISIBLE);
                holder.image_select.setBackgroundResource(R.mipmap.tree_indicator3);
                if (end) {
                    holder.image_bottom.setVisibility(View.INVISIBLE);
                } else {
                    holder.image_bottom.setVisibility(View.VISIBLE);
                }
            }

            if (level == 0) {
                if (position == 0) {
                    holder.view_line.setVisibility(View.INVISIBLE);
                } else {
                    holder.view_line.setVisibility(View.VISIBLE);
                }
            } else {
                holder.view_line.setVisibility(View.INVISIBLE);
            }
            if (isAllSelected)
                tree.isSelected = mode == MODE_SELECTED_ALL ? true : false;
            final int qnum = tree.getQnum();
            String allNum = String.valueOf(qnum);
            SpannableString spannableString = new SpannableString(allNum + "道题");
            int index = allNum.length();
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange003)), 0,
                    index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.text_err_number.setText(spannableString);

            holder.text_redo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getCollectionIds(tree.getId(), qnum);
                }
            });
            boolean isChoseBtnChecked = mTopTitleBar.findMenuItem(android.R.id.button1).isSelected();
            holder.check_view.setVisibility(isChoseBtnChecked ? View.VISIBLE : View.INVISIBLE);
            holder.check_view.setSelected(tree.isSelected);
            holder.check_view.setChecked(tree.isSelected);

            RelativeLayout.LayoutParams layoutParams = ((RelativeLayout.LayoutParams) holder.text_redo.getLayoutParams());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, isChoseBtnChecked ? 0 : -1);
            holder.check_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isChecked = view.isSelected();
                    holder.check_view.setSelected(!isChecked);
                    if (isChecked && mSelectedBeans.contains(tree)) {
                        mSelectedBeans.remove(tree);
                    } else if (!isChecked && !mSelectedBeans.contains(tree))
                        mSelectedBeans.add(tree);
                    if(mSelectedBeans.size() > 0 && !mConfirmView.isSelected()) {
                        mConfirmView.setSelected(true);
                        mConfirmView.setBackgroundResource(R.drawable.shape_red_rightcorner_bg);
                    }else if(mSelectedBeans.size() == 0  && mConfirmView.isSelected()){
                        mConfirmView.setSelected(false);
                        mConfirmView.setBackgroundResource(R.drawable.shape_gray_groupbuy_bg);
                    }

                    tree.isSelected = !isChecked;
                    if (isAllSelected) {
                        mode = 0;
                        isAllSelected = false;
                    }
                    List<TreeViewBean> lists = tree.getChildren();

                    if (tree.getLevel() < 2)
                        selectChild(isChecked, lists);
                    notifyDataSetChanged();
                }
            });
            return converView;
        }

        private void selectChild(boolean isChecked, List<TreeViewBean> lists) {
            if (lists == null || lists.isEmpty())
                return;
            for (int i = 0; i < lists.size(); i++) {
                TreeViewBean treeViewBean = lists.get(i);
                treeViewBean.isSelected = !isChecked;
                if (treeViewBean.getLevel() < 2)
                    selectChild(isChecked, treeViewBean.getChildren());
            }
        }
    }

    class Holder {
        TextView text_name;
        TextView text_err_number;
        ImageView image_top;
        ImageView image_select;
        ImageView image_bottom;
        View view_line;
        TextView text_look;
        TextView text_redo;
        CheckBox check_view;
    }

    private void getCollectionIds(int pointId, int qnum) {
        //判断收藏的知识点的试题数目是不是0
        if (qnum == 0) {
            CommonUtils.showToast("该知识点暂时没有收藏试题");
            return;
        }

        if (!NetUtil.isConnected()) {
            CommonUtils.showToast("无网络，请检查网络连接");
            return;
        }

        customDialog = new CustomDialog(CollectionActivity.this, R.layout.dialog_feedback_commit);
        TextView tv_notify_message = (TextView) customDialog.mContentView.findViewById(R.id.tv_notify_message);
        tv_notify_message.setText("获取试题中...");
        customDialog.show();

        Subscription collectionSubscribe = RetrofitManager.getInstance().getService().getCollectionIdsList(pointId, 1, 5000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CollectionIdsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        customDialog.dismiss();
                        CommonUtils.showToast("获取收藏试题失败");
                    }

                    @Override
                    public void onNext(CollectionIdsBean collectionIdsBean) {
                        customDialog.dismiss();
                        int code = collectionIdsBean.getCode();
                        CollectionIdsBean.CollectionIdsData data = collectionIdsBean.getData();
                        if (code == 1000000) {
                            dealwithCollectionIds(data);
                        } else if (code == 1110002) {
                            CommonUtils.showToast("用户会话过期");
                        } else {
                            CommonUtils.showToast("获取收藏试题失败");
                        }
                    }
                });

        compositeSubscription.add(collectionSubscribe);
    }

    private void dealwithCollectionIds(CollectionIdsBean.CollectionIdsData data) {
        if (data != null) {
            List<Integer> resutls = data.getResult();

            if (resutls != null && resutls.size() > 0) {
                String ids = getExerciseIds(resutls);
                ArenaDataCache.getInstance().clearCacheErrorData();
                ArenaDataCache.getInstance().setErrorIdsAry(resutls);
                ArenaDataCache.getInstance().setErrorIdsStr(ids);
                Bundle bundle = new Bundle();
                bundle.putString("exerciseIdList", ids);
                bundle.putBoolean("from_collection_activity", true);
//                bundle.putBoolean("from_error_activity", true);
                ArenaExamActivityNew.show(CollectionActivity.this, ArenaConstant.EXAM_ENTER_FORM_TYPE_JIEXI_FAVERATE, bundle);
                return;
            }
        }

        CommonUtils.showToast("试题为空！");
    }

    private String getExerciseIds(List<Integer> resutls) {
        String ids = "";
        for (Integer id : resutls) {
            ids += id + ",";
        }
        if (!TextUtils.isEmpty(ids)) {
            ids = ids.substring(0, ids.length() - 1);
        }
        return ids;
    }

    /**
     * @param flag 1、无网络
     *             2、无数据
     *             3、获取数据失败
     */
    private void loadFailed(int flag) {
        errorView.setVisibility(View.VISIBLE);
//        layoutErrorView.updateUI();
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
    protected void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    private void showFilterWindow(View anchor) {
        if (mNormalPopup == null) {
            mNormalPopup = new QMUIPopup(CollectionActivity.this, QMUIPopup.DIRECTION_BOTTOM);

            mNormalPopup.setContentView(R.layout.arena_collect_poplayout);
            mLastSelected = 0;
            mNormalPopup.getRootView().findViewById(R.id.pop_menu_upreport).setSelected(true);
            mNormalPopup.setOnViewItemClickListener(new QMUIPopup.onItemViewClickListener() {
                @Override
                public void onItemViewClick(int position, View view) {
                    if (view.isSelected()) return;
                    view.setSelected(true);
                    ViewGroup containerView = mNormalPopup.getRootView().findViewById(R.id.root);
                    containerView.getChildAt(mLastSelected).setSelected(false);
                    mLastSelected = position;
                    mNormalPopup.dismiss();
                    mSelectedTv.setText(mLastSelected == 0 ? "最近一周收藏" : mLastSelected == 1 ? "最近一月收藏" : "全部收藏");
                }
            });
        }
        mNormalPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
        mNormalPopup.show(anchor);
    }

    private void showGuideTip() {
        if (customConfirmDialog == null) {
            customConfirmDialog = DialogUtils.onShowConfirmDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customConfirmDialog.dismiss();
                }
            }, "导出说明", "1. 可最多免费导出5题；\n" +
                    "2. 5题以上1题消耗1图币；\n" +
                    "3. 已扣除过图币的题目再次导出时不再重复扣除；\n" +
                    "4. 因题目变更、知识点变更等原因，导致实际导出题目数或扣除的图币数少于选择时，请以实际导出为准。", null, "知道了");
            customConfirmDialog.setContentGravity(Gravity.LEFT);
            customConfirmDialog.setTitleBold();
        } else if (!customConfirmDialog.isShowing())
            customConfirmDialog.show();
    }

    private void showDownloadTip(String msg){
        if(downloadTipDialog == null)
            downloadTipDialog = DialogUtils.createExitConfirmDialog(this, null, "本次导出有5题因题目变更无法导出，实际可导出195题，将消耗190图币，是否确认使用？", "取消", "使用", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        downloadTipDialog.setContentGravity(Gravity.LEFT);
        downloadTipDialog.setMessage(msg);
        downloadTipDialog.show();
    }

//    private void show_loadData() {
//        ll_prompt.setVisibility(View.VISIBLE);
//        image_empty.setVisibility(View.GONE);
//        progress_bar.setVisibility(View.VISIBLE);
//        text_faile.setVisibility(View.VISIBLE);
//        text_faile.setText("获取数据中...");
//    }

//    private void show_loadFaile(String msg) {
//        ll_prompt.setVisibility(View.VISIBLE);
//        image_empty.setVisibility(View.VISIBLE);
//        progress_bar.setVisibility(View.GONE);
//        text_faile.setVisibility(View.VISIBLE);
//        text_faile.setText(msg);
//    }

//    private void show_loadSuccess() {
//        ll_prompt.setVisibility(View.GONE);
//        image_empty.setVisibility(View.GONE);
//        progress_bar.setVisibility(View.GONE);
//        text_faile.setVisibility(View.GONE);
//        text_faile.setText("");
//    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, CollectionActivity.class);
        context.startActivity(intent);
    }
}
