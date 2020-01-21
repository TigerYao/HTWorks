package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseHandoutManageAdapter;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.other.PdfViewFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UriUtil;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.DensityUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by cjx on 2018\12\12 0012.
 * 讲义下载完成
 */

public class DownHandoutManageFragment extends AbsSettingFragment implements OnRecItemClickListener,
        AStripTabsFragment.IStripTabInitData, OnSwitchListener {

    @Override
    public int getContentView() {
        return R.layout.down_finish_manage_layout;
    }

    @BindView(R.id.lv_downloaded)
    RecyclerView mListView;

    @BindView(R.id.bt_down_delete)
    TextView mDelBtn;

    @BindView(R.id.delNum_txt)
    TextView mDelTipTxt;


    @BindView(R.id.selAll_btn)
    TextView mSelAllBtn;

    @BindView(R.id.view_err)
    CommonErrorView errorView;

    @BindView(R.id.bottom_action_layout)
    QMUILinearLayout mAction_layout;

    private DownLoadCourse mLiveVideoInfo;
    CustomConfirmDialog mConfirmDialog;

    private float mShadowAlpha = 0.25f;
    private int mShadowElevationDp = 14;
    int mSelectNum = 0;

    private boolean mHasDeleted = false;
    private boolean mHasLoaded = false;
    private String courseID;
    private String courseName;
    private String reqFrom;

    CourseHandoutManageAdapter mListAdapter;
    private List<DownHandout> mDownHandoutList;

    public static DownHandoutManageFragment getInstance(String courseId, String CourseName, String from) {

        Bundle tmpArg = new Bundle();

        tmpArg.putString(ArgConstant.FROM_ACTION, from);
        tmpArg.putString(ArgConstant.COURSE_ID, courseId);
        tmpArg.putString(ArgConstant.COURSE_NAME, CourseName);
        DownHandoutManageFragment tmpFragment = new DownHandoutManageFragment();
        tmpFragment.setArguments(tmpArg);
        return tmpFragment;

    }

    public static void lanuchForResult(Fragment context, DownLoadCourse curCourse, String from) {
        Bundle tmpArg = new Bundle();

        tmpArg.putString(ArgConstant.FROM_ACTION, from);
        tmpArg.putString(ArgConstant.COURSE_ID, curCourse.getRealCourseID());
        tmpArg.putString(ArgConstant.COURSE_NAME, curCourse.getCourseName());

        UIJumpHelper.jumpFragment(context, 1001, DownLessionManageFragment.class, tmpArg);
    }

    @Override
    protected void parserParams(Bundle arg) {
        super.parserParams(arg);

        reqFrom = arg.getString(ArgConstant.FROM_ACTION);
        courseID = arg.getString(ArgConstant.COURSE_ID);
        courseName = arg.getString(ArgConstant.COURSE_NAME);
    }

    @Override
    public int isEditMode() {
        if (mListAdapter.getItemCount() <= 0) return 2;
        return mListAdapter.isEditMode() ? 1 : 0;
    }

    @Override
    public void switchMode() {

        if (mListAdapter.isEditMode()) {//当前删除模式->normal
            mSelectNum = 0;
            mSelAllBtn.setText("全选");
            //   mRightMenu.setText(R.string.edit);
            for (DownHandout curLession : mListAdapter.getAllItems()) {
                curLession.setSelect(false);
            }
            mListAdapter.setActionMode(false);
            mAction_layout.setVisibility(View.GONE);
            if (mOnSwipeItemTouchListener != null)
                mOnSwipeItemTouchListener.setCanSwipe(true);
        } else {//当前normal->删除模式
            // mRightMenu.setText(R.string.pickerview_cancel);
            mAction_layout.setVisibility(View.VISIBLE);
            mListAdapter.setActionMode(true);
            mDelTipTxt.setVisibility(View.GONE);
            if (mOnSwipeItemTouchListener != null)
                mOnSwipeItemTouchListener.setCanSwipe(false);
        }
    }

    SwipeItemLayout.OnSwipeItemTouchListener mOnSwipeItemTouchListener;

    @Override
    protected void setListener() {
        super.setListener(); //
        mListAdapter = new CourseHandoutManageAdapter(getContext(), new ArrayList<DownHandout>());
        mListAdapter.setOnViewItemClickListener(this);
        mOnSwipeItemTouchListener = new SwipeItemLayout.OnSwipeItemTouchListener(getContext());
        mListView.addOnItemTouchListener(mOnSwipeItemTouchListener);

        ((TextView) this.findViewById(R.id.downmore_btn)).setText("缓存更多讲义");
        int commonShapeRadius = DensityUtils.dp2px(getContext(), 20);
        BitmapDrawable solidImageBitmapDrawable = QMUIDrawableHelper.createDrawableWithSize(getResources(), commonShapeRadius, commonShapeRadius, commonShapeRadius / 2, Color.parseColor("#FF3F47"));
        mDelTipTxt.setBackground(solidImageBitmapDrawable);
        mDelBtn.setTextColor(Color.parseColor("#FF3F47"));

        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public void onStripTabRequestData() {
        // onFirstLoad();
        if (!mHasLoaded) {
            mHasLoaded = true;
            getFinishedCourseWares();
            mListAdapter.refresh(mDownHandoutList);
        }
    }


    private void getFinishedCourseWares() {
        /*downedCoursewares = SQLiteHelper.getInstance()
                .getLessonsByStatus(courseID, "2");
        for (int i = 0; i < downedCoursewares.size(); i++) {
            if (downedCoursewares.get(i).getSpace() <= 0) {
                if (!TextUtils.isEmpty(downedCoursewares.get(i).getPlayPath())) {
                    File file = new File(downedCoursewares.get(i).getPlayPath());
                    if (file != null && file.exists()) {
                        File parFile = file.getParentFile();
                        LogUtils.i("getFileSize: " + FileUtil.getFileSize(parFile));
                        downedCoursewares.get(i).setSpace(FileUtil.getFileSize(parFile));
                    }
                }
            }
        }*/

        mDownHandoutList = SQLiteHelper.getInstance().getAllDownHandouts(courseID, true);

        if (mDownHandoutList == null || mDownHandoutList.size() == 0) {
            showNoData(true);
        } else {
            showNoData(false);
        }
    }

    private void setDownloadBtnState() {
        if (mSelectNum > 0) {
            mDelTipTxt.setVisibility(View.VISIBLE);
            mDelTipTxt.setText(String.valueOf(mSelectNum));
            AnimUtils.scaleView(mDelTipTxt);
        } else {
            mDelTipTxt.setVisibility(View.GONE);
        }
        if (mListAdapter.getItemCount() == (mSelectNum)) {
            mSelAllBtn.setText(R.string.pickerview_cancel);
        } else {
            mSelAllBtn.setText("全选");
        }
    }

    @Override
    public void onItemClick(int position, View view, int type) {
        if (mListAdapter.isEditMode()) {
            DownHandout curLession = mListAdapter.getItem(position);
            boolean oldSelect = curLession.isSelect();
            curLession.setSelect(!oldSelect);
            if (view instanceof CheckBox) {
                ((CheckBox) view).setChecked(!oldSelect);
            }
            mSelectNum = mSelectNum + (oldSelect ? -1 : 1);
            setDownloadBtnState();
            return;
        }
        DownHandout curLession = mListAdapter.getItem(position);
        if (null == curLession) return;
        switch (type) {
            case EventConstant.EVENT_ALL:
                if (curLession.getDownStatus() == DownBtnLayout.FINISH
                        && FileUtil.isFileExist(curLession.getFileUrl()))
                    readPDF(curLession.getFileUrl());
                break;
            case EventConstant.EVENT_DELETE:
               // showDeleteDlg(mListAdapter.getItem(position), position);

                CommonUtils.sharePdfFile(curLession.getFileUrl(),curLession.getSubjectName(),getActivity());
                break;
        }
    }


    private void showDeleteDlg(final DownHandout item, final int position) {
        if (mConfirmDialog == null) {
            mConfirmDialog = DialogUtils.createDialog(getActivity(),
                    "提示", "即将删除所选课程\n该操作不可恢复");
        }
        mConfirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DownLoadLesson> downIDs = new ArrayList<>();
                DownHandout cuLession = mListAdapter.getItem(position);
                //  downIDs.add(cuLession);
                //  DownLoadAssist.getInstance().delete(downIDs);
                mHasDeleted = true;
               /* mListAdapter.removeAndRefresh(position);
                if(mDownCourseAdapter.getItemCount()==0){
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    DownLessionManageFragment.this.finish();
                }*/

                int flag = SQLiteHelper.getInstance().deleteSingleHandOut(cuLession.getSubjectID(), courseID);
                if (flag > -1) {
                    FileUtil.deleteFile(cuLession.getFileUrl());
                    LogUtils.e("onItemClick", cuLession.getFileUrl() + "");
                    mListAdapter.removeAt(position, item);
                    if (mListAdapter.getItemCount() == 0) {
                        if (getParentFragment() instanceof DownAllFinishedFragment) {
                            ((DownAllFinishedFragment) getParentFragment()).setHasDelete();
                        }
                    }

                } else ToastUtils.showShort("操作失败");
            }
        });
        if (!mConfirmDialog.isShowing()) {
            mConfirmDialog.show();
        }
    }


    @OnClick({R.id.downmore_btn, R.id.selAll_btn, R.id.bt_down_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downmore_btn:
                if (getParentFragment() instanceof DownAllFinishedFragment) {
                    ((DownAllFinishedFragment) getParentFragment()).onClickDownloadMore(1);
                }
                break;
            case R.id.selAll_btn:
                if (mSelectNum == mListAdapter.getItemCount()) {
                    mSelectNum = 0;
                    for (DownHandout curLession : mListAdapter.getAllItems()) {
                        curLession.setSelect(false);
                    }
                    setDownloadBtnState();
                    mListAdapter.notifyDataSetChanged();
                } else {
                    mSelectNum = mListAdapter.getItemCount();
                    for (DownHandout curLession : mListAdapter.getAllItems()) {
                        curLession.setSelect(true);
                    }
                    setDownloadBtnState();
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.bt_down_delete:
                if (mSelectNum == 0) {
                    ToastUtils.showShort("请先选择课件");
                    return;
                }
                if (mConfirmDialog == null) {
                    mConfirmDialog = DialogUtils.createDialog(getActivity(), "提示",
                            "即将删除所选课程\n该操作不可恢复");
                }
                mConfirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<DownHandout> downIDs = new ArrayList<>();
                        List<String> delIds = new ArrayList<>();
                        for (DownHandout curLession : mListAdapter.getAllItems()) {
                            if (curLession.isSelect()) {
                                downIDs.add(curLession);
                                delIds.add(curLession.getSubjectID());
                            }
                        }
                        mHasDeleted = true;
                        int flag = SQLiteHelper.getInstance().deleteBatchHandOut(delIds, courseID);
                        for (DownHandout bean : downIDs) {
                            FileUtil.deleteFile(bean.getFileUrl());
                            LogUtils.e("onItemClick", bean.getFileUrl() + "");
                        }
                        mListAdapter.getAllItems().removeAll(downIDs);
                        mListAdapter.notifyDataSetChanged();
                        mSelectNum = 0;
                        setDownloadBtnState();
                        if (mListAdapter.getItemCount() == 0) {
                            if (getParentFragment() instanceof DownAllFinishedFragment) {
                                ((DownAllFinishedFragment) getParentFragment()).setHasDelete();
                            }
                        }
                        if (mListAdapter.getAllItems().size() == 0) {
                            showNoData(true);
                        }
                      /*  DownLoadAssist.getInstance().delete(downIDs);
                        getFinishedCourseWares();
                        mDownCourseAdapter.refresh(downedCoursewares);
                        if (null == downedCoursewares
                                || downedCoursewares.size() <= 0) {
                            Intent data = new Intent();
                            setResult(Activity.RESULT_OK, data);
                            finish();
                        }*/
                    }
                });
                mConfirmDialog.show();
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroy() {

        // handler.removeCallbacksAndMessages(null);
        super.onDestroy();
       /* if(confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }*/
    }


    private void readPDF(final String handout) {
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String type = CommonUtils.getMIMEType(new File(handout));
                    intent= UriUtil.setIntentDataAndType(intent,type,new File(handout),false);

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, 100);
                    } else {
                        if(CommonUtils.hasX5nited())
                            PdfViewFragment.lanuch(getContext(),handout);
                        else
                            CommonUtils.showToast("请安装能打开此文件的程序哦");
                    }
                } catch (Exception e) {
                    if(CommonUtils.hasX5nited())
                        PdfViewFragment.lanuch(getContext(),handout);
                    else
                        CommonUtils.showToast("请安装能打开此文件的程序哦");
                }
            }
        });
    }



    private void showNoData(boolean show) {
        if (show) {
            errorView.setVisibility(View.VISIBLE);
            errorView.setErrorText("什么都没有");
            errorView.setErrorImage(R.drawable.no_data_bg);
            errorView.setErrorImageVisible(true);
        } else {
            errorView.setVisibility(View.GONE);
        }
    }
}
