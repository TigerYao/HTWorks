package com.huatu.handheld_huatu.business.arena.helper;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.business.arena.activity.ArenaExamActivityNew;
import com.huatu.handheld_huatu.business.arena.adapter.TreeViewAdapterNew;
import com.huatu.handheld_huatu.business.arena.utils.ArenaConstant;
import com.huatu.handheld_huatu.event.BaseMessageEvent;
import com.huatu.handheld_huatu.event.me.ExamTypeAreaMessageEvent;
import com.huatu.handheld_huatu.helper.LoginTrace;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.HomeTreeBeanNew;
import com.huatu.handheld_huatu.mvppresenter.impl.HomePresenterImpl;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.custom.CatchLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页题库树Adapter，内容刷新，展开，收起的操作，都挪到这里
 */
public class HomeTreeHelper {

    private Activity mActivity;
    private HomePresenterImpl mPresenter;
    private View llNoDataX, rlBottomLine, llNoData;
    private RecyclerView rlTree;

    private TreeViewAdapterNew treeViewAdapterNew;
    private ArrayList<HomeTreeBeanNew> homeTreeList = new ArrayList<>();                        // 主页下列表展示的内容
    private SparseArray<ArrayList<HomeTreeBeanNew>> homeTreeCatch = new SparseArray<>();        // 缓存的内容 key:parentId value:childList

    public HomeTreeHelper(Activity activity, HomePresenterImpl presenter, RecyclerView rlTree, View llNoDataX, View rlBottomLine, View llNoData) {
        this.mActivity = activity;
        this.mPresenter = presenter;
        this.rlTree = rlTree;
        this.llNoDataX = llNoDataX;
        this.rlBottomLine = rlBottomLine;
        this.llNoData = llNoData;
        initTreeAdapter();
    }

    public ArrayList<HomeTreeBeanNew> getHomeTreeList() {
        return homeTreeList;
    }

    // 知识树Adapter
    private void initTreeAdapter() {
        rlTree.setNestedScrollingEnabled(false);
        rlTree.setLayoutManager(new CatchLinearLayoutManager(mActivity));
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(100);
        animator.setRemoveDuration(100);
        animator.setChangeDuration(200);
        rlTree.setItemAnimator(animator);

        treeViewAdapterNew = new TreeViewAdapterNew(mActivity, homeTreeList, rlTree, new TreeViewAdapterNew.OnItemClickListener() {
            @Override
            public void expandOrAnswerItem(int position) {
                HomeTreeBeanNew homeTreeBeanNew = treeViewAdapterNew.getItem(position);
                if (homeTreeBeanNew == null) return;
                if (homeTreeBeanNew.getLevel() != 2) {  // 如果不是2，才判断
                    if (homeTreeBeanNew.isExpand()) {        // 如果展开了就收起
                        closeTreeChild(position);
                    } else {                                 // 如果没展开就展开
                        ArrayList<HomeTreeBeanNew> homeTreeBeanNews = homeTreeCatch.get(homeTreeBeanNew.getId());
                        if (homeTreeBeanNews != null) {             // 本地有
                            expendTreeChild(homeTreeBeanNew.getId(), homeTreeBeanNews);
                        } else {                                    // 网络获取
                            mPresenter.getHomeTreeDataById(homeTreeBeanNew.getId());
                        }
                    }
                } else {                                // 如果是2，就直接打开
                    goAnswer(position);
                }
            }

            @LoginTrace(type = 0)
            @Override
            public void goAnswer(int position) {
                HomeTreeBeanNew tree = treeViewAdapterNew.getItem(position);
                if (tree == null) return;
                // 神策数据上报
                studyReport(tree);
                if (tree.getQnum() > 0) {
                    Bundle bundle = new Bundle();
                    if (tree.getUnfinishedPracticeId() > 0) {
                        bundle.putBoolean("continue_answer", true);
                        bundle.putLong("practice_id", tree.getUnfinishedPracticeId());
                    }
                    bundle.putLong("point_ids", tree.getId());
                    if (SpUtils.getHomeQuestionMode() == 0) {   // 专项练习做题模式
                        ArenaExamActivityNew.show(mActivity, ArenaConstant.EXAM_ENTER_FORM_TYPE_ZHUANXIANG_LIANXI, bundle);
                    } else {                                    // 专项练习背题模式
                        bundle.putInt("fromType", ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI);
                        ArenaExamActivityNew.show(mActivity, ArenaConstant.EXAM_ENTER_FORM_TYPE_RECITE_ZHUANXIANG_LIANXI, bundle);
                    }
                } else {
                    ToastUtils.showShort("暂无题目");
                }
            }
        });

        rlTree.setAdapter(treeViewAdapterNew);
    }


    // 展开
    private void expendTreeChild(int parentId, ArrayList<HomeTreeBeanNew> homeTreeBeanNews) {
        for (int i = 0; i < homeTreeList.size(); i++) {
            HomeTreeBeanNew homeTreeBeanNew = homeTreeList.get(i);
            if (homeTreeBeanNew.getId() == parentId) {
                homeTreeBeanNew.setExpand(true);
                homeTreeList.addAll(i + 1, homeTreeBeanNews);
                handleTreeLine();
                treeViewAdapterNew.notifyItemChanged(i);
                treeViewAdapterNew.notifyItemRangeInserted(i + 1, homeTreeBeanNews.size());
                break;
            }
        }
    }

    // 收起
    private void closeTreeChild(int position) {

        HomeTreeBeanNew homeTreeBeanNew = homeTreeList.get(position);
        homeTreeBeanNew.setExpand(false);

        ArrayList<HomeTreeBeanNew> deleteTree = new ArrayList<>();
        for (int i = position + 1; i < homeTreeList.size(); i++) {
            HomeTreeBeanNew treeBeanNew = homeTreeList.get(i);
            if (treeBeanNew.getLevel() > homeTreeBeanNew.getLevel()) {
                deleteTree.add(treeBeanNew);
            } else {
                break;
            }
        }
        for (HomeTreeBeanNew treeBeanNew : deleteTree) {
            treeBeanNew.setExpand(false);
        }
        homeTreeList.removeAll(deleteTree);
        handleTreeLine();
        treeViewAdapterNew.notifyItemChanged(position);
        treeViewAdapterNew.notifyItemRangeRemoved(position + 1, deleteTree.size());
    }

    // 处理知识树列表数据，是否显示上下线
    private void handleTreeLine() {
        int size = homeTreeList.size();
        for (int i = 0; i < size; i++) {
            HomeTreeBeanNew bean = homeTreeList.get(i);
            if (i == size - 1) {     // 最后一个
                if (bean.getLevel() == 0) {     // 如果是第一个上面没线
                    bean.setLineUp(false);
                } else {                        // 否则上面一定有线
                    bean.setLineUp(true);
                }
                bean.setLineDown(false);        // 下面一定没线
            } else {
                HomeTreeBeanNew beanAfter = homeTreeList.get(i + 1);
                if (bean.getLevel() == 0) {
                    bean.setLineUp(false);
                    if (beanAfter.getLevel() == 0) {
                        bean.setLineDown(false);
                    } else {
                        bean.setLineDown(true);
                    }
                } else {
                    bean.setLineUp(true);
                    if (beanAfter.getLevel() == 0) {
                        bean.setLineDown(false);
                    } else {
                        bean.setLineDown(true);
                    }
                }
            }
        }
    }

    /**
     * 神策点击题库知识树，上报
     *
     * @param treeBean 点击节点
     */
    private void studyReport(HomeTreeBeanNew treeBean) {
        // 一级知识点
        if (treeBean.getLevel() == 0) {
            StudyCourseStatistic.clickStatistic("题库", "页面第五模块", treeBean.getName());
        } else {
            // tab  题库->言语理解与表达->篇章阅读
            StringBuilder tab = new StringBuilder();
            String firstModule = "页面中部";
            String btnName = treeBean.getName();
            HomeTreeBeanNew bean = treeBean;
            while (bean.getParent() != null) {
                HomeTreeBeanNew parentBean = bean.getParent();
                tab.insert(0, "->" + parentBean.getName());
                bean = parentBean;
            }
            tab.insert(0, "题库");
            StudyCourseStatistic.clickStatistic(tab.toString(), firstModule, btnName);
        }
    }

    /**
     * 更新知识树节点
     */
    public void updateTreePoint(BaseListResponseModel<HomeTreeBeanNew> data) {
        homeTreeCatch.clear();
        SparseArray<HomeTreeBeanNew> treeBeanMap = new SparseArray<>();
        List<HomeTreeBeanNew> tree = data.data;
        recursionHandleTreeData(tree, treeBeanMap, homeTreeCatch);
        // 递归遍历完成已经解析完了数据
        // 更新现有的展示树
        for (HomeTreeBeanNew treeBean : homeTreeList) {
            HomeTreeBeanNew treeBeanNew = treeBeanMap.get(treeBean.getId());
            if (treeBeanNew != null) {
                treeBean.setName(treeBeanNew.getName());
                treeBean.setQnum(treeBeanNew.getQnum());
                treeBean.setRnum(treeBeanNew.getRnum());
                treeBean.setWnum(treeBeanNew.getWnum());
                treeBean.setTimes(treeBeanNew.getTimes());
                treeBean.setSpeed(treeBeanNew.getSpeed());
                treeBean.setAccuracy(treeBeanNew.getAccuracy());
                treeBean.setUnfinishedPracticeId(treeBeanNew.getUnfinishedPracticeId());
            }
        }
        treeViewAdapterNew.notifyDataSetChanged();
    }

    /**
     * 获取全部知识树数据，递归处理知识树数据，为了更新知识树内容而不改变展开结构
     */
    private void recursionHandleTreeData(List<HomeTreeBeanNew> tree, SparseArray<HomeTreeBeanNew> treeBeanMap, SparseArray<ArrayList<HomeTreeBeanNew>> homeTreeCatch) {
        if (tree == null || tree.size() == 0) {
            return;
        }
        for (HomeTreeBeanNew treeBean : tree) {
            treeBeanMap.put(treeBean.getId(), treeBean);
            if (treeBean.getChildren() != null && treeBean.getChildren().size() > 0) {
                for (HomeTreeBeanNew child : treeBean.getChildren()) {
                    child.setParent(treeBean);
                }
                homeTreeCatch.put(treeBean.getId(), treeBean.getChildren());
                recursionHandleTreeData(treeBean.getChildren(), treeBeanMap, homeTreeCatch);
            }
        }
    }

    public void updateTreePoint(int parentId, BaseListResponseModel<HomeTreeBeanNew> list) {
        if (list == null || list.data == null) {
            if (parentId == 0) {
                homeTreeList.clear();
                homeTreeCatch.clear();
                treeViewAdapterNew.notifyDataSetChanged();
                llNoDataX.setVisibility(View.VISIBLE);
                rlBottomLine.setVisibility(View.GONE);
            }
            return;
        }
        llNoDataX.setVisibility(View.GONE);
        rlBottomLine.setVisibility(View.VISIBLE);

        HomeTreeBeanNew parent = null;

        for (HomeTreeBeanNew homeTreeBeanNew : homeTreeList) {
            if (homeTreeBeanNew.getId() == parentId) {
                parent = homeTreeBeanNew;
                break;
            }
        }

        for (HomeTreeBeanNew datum : list.data) {
            datum.setParent(parent);
        }

        if (parentId == 0) {
            homeTreeList.clear();
            homeTreeCatch.clear();
            homeTreeList.addAll(list.data);
            handleTreeLine();
            treeViewAdapterNew.notifyItemRangeInserted(0, homeTreeList.size());
        } else {
            // 这里就是点击了展开，获取数据回来
            if (list.data.size() != 0) {
                homeTreeCatch.put(parentId, new ArrayList<>(list.data));
                expendTreeChild(parentId, new ArrayList<>(list.data));
            }
        }

        if (list.data.size() == 0 && parentId == 0) {       // 如果是第一层级没有数据，就是没有题
            llNoData.setVisibility(View.VISIBLE);
            // 通知TitleView不显示搜索按钮
            EventBus.getDefault().post(new BaseMessageEvent<>(ExamTypeAreaMessageEvent.ETA_MSG_HOME_FRAGMENT_TITLE_VIEW_SEARCH_NOT_SHOW, new ExamTypeAreaMessageEvent()));
        } else {
            llNoData.setVisibility(View.GONE);
            // 通知TitleView显示搜索按钮
            EventBus.getDefault().post(new BaseMessageEvent<>(ExamTypeAreaMessageEvent.ETA_MSG_HOME_FRAGMENT_TITLE_VIEW_SEARCH_SHOW, new ExamTypeAreaMessageEvent()));
        }
    }

    public void clearData(int type) {
        if (type == 0) {
            homeTreeCatch.clear();
        } else if (type == 1) {
            homeTreeList.clear();
            homeTreeCatch.clear();
            treeViewAdapterNew.notifyDataSetChanged();
        }
    }
}
