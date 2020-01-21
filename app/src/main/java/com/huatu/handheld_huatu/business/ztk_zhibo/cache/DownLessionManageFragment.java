package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.DownloadingCourseAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.ztk_vod.BJRecordPlayActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLVodListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseDataConverter;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.LiveVideoForLiveActivity;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareInfo;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by cjx on 2018\7\7 0007.
 */

public class DownLessionManageFragment extends AbsSettingFragment implements OnDLVodListener, OnRecItemClickListener,
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


    int mSelectNum = 0;

    private boolean mHasDeleted = false;

    private String courseID;
    private String courseName;
    private String reqFrom;
    DownloadingCourseAdapter mDownCourseAdapter;
    private List<DownLoadLesson> downedCoursewares;
    private boolean mHasLoaded = false;

    public static DownLessionManageFragment getInstance(String courseId, String CourseName, String from) {

        Bundle tmpArg = new Bundle();

        tmpArg.putString(ArgConstant.FROM_ACTION, from);
        tmpArg.putString(ArgConstant.COURSE_ID, courseId);
        tmpArg.putString(ArgConstant.COURSE_NAME, CourseName);
        DownLessionManageFragment tmpFragment = new DownLessionManageFragment();
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
        if (mDownCourseAdapter.getItemCount() <= 0) return 2;
        return mDownCourseAdapter.isEditMode() ? 1 : 0;
    }

    @Override
    public void switchMode() {

        if (mDownCourseAdapter.isEditMode()) {//当前删除模式->normal
            mSelectNum = 0;
            mSelAllBtn.setText("全选");
            //   mRightMenu.setText(R.string.edit);
            for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                curLession.setSelect(false);
            }
            mDownCourseAdapter.setActionMode(false);
            mAction_layout.setVisibility(View.GONE);
            if (mOnSwipeItemTouchListener != null)
                mOnSwipeItemTouchListener.setCanSwipe(true);
        } else {//当前normal->删除模式
            // mRightMenu.setText(R.string.pickerview_cancel);
            mAction_layout.setVisibility(View.VISIBLE);
            mDownCourseAdapter.setActionMode(true);
            mDelTipTxt.setVisibility(View.GONE);
            if (mOnSwipeItemTouchListener != null)
                mOnSwipeItemTouchListener.setCanSwipe(false);
        }
    }

    SwipeItemLayout.OnSwipeItemTouchListener mOnSwipeItemTouchListener;

    @Override
    protected void setListener() {
        super.setListener(); //
        mDownCourseAdapter = new DownloadingCourseAdapter(getContext(), new ArrayList<DownLoadLesson>(), true);
        mDownCourseAdapter.setOnItemClickListener(this);
        mOnSwipeItemTouchListener = new SwipeItemLayout.OnSwipeItemTouchListener(getContext());
        mListView.addOnItemTouchListener(mOnSwipeItemTouchListener);
        DownLoadAssist.getInstance().addDownloadListener(this);

        int commonShapeRadius = DensityUtils.dp2px(getContext(), 20);
        BitmapDrawable solidImageBitmapDrawable = QMUIDrawableHelper.createDrawableWithSize(getResources(), commonShapeRadius, commonShapeRadius, commonShapeRadius / 2, Color.parseColor("#FF3F47"));
        mDelTipTxt.setBackground(solidImageBitmapDrawable);
        mDelBtn.setTextColor(Color.parseColor("#FF3F47"));

        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListView.setAdapter(mDownCourseAdapter);
    }

    @Override
    public void onStripTabRequestData() {
        // onFirstLoad();

        if (!mHasLoaded) {
            mHasLoaded = true;
            getFinishedCourseWares();
            mDownCourseAdapter.refresh(downedCoursewares);
        }

    }

    @Override
    public void requestData() {
    /*    int mRadius = DensityUtils.dp2px(getContext(), 15);
        mAction_layout.setRadiusAndShadow(mRadius,
                DensityUtils.dp2px(getContext(), mShadowElevationDp), mShadowAlpha);*/
    }

    private void getFinishedCourseWares() {
        downedCoursewares = SQLiteHelper.getInstance()
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
        }

        if (downedCoursewares == null || downedCoursewares.size() == 0) {
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
        if (mDownCourseAdapter.getItemCount() == (mSelectNum)) {
            mSelAllBtn.setText(R.string.pickerview_cancel);
        } else {
            mSelAllBtn.setText("全选");
        }
    }

    @Override
    public void onItemClick(int position, View view, int type) {
        if (mDownCourseAdapter.isEditMode()) {
            DownLoadLesson curLession = mDownCourseAdapter.getCurrentItem(position);
            boolean oldSelect = curLession.isSelect();
            curLession.setSelect(!oldSelect);
            if (view instanceof CheckBox) {
                ((CheckBox) view).setChecked(!oldSelect);
            }
            mSelectNum = mSelectNum + (oldSelect ? -1 : 1);
            setDownloadBtnState();
            return;
        }

        switch (type) {
            case EventConstant.EVENT_ALL:
                final DownLoadLesson cuLession = mDownCourseAdapter.getCurrentItem(position);

                if (cuLession.getPlayerType() == PlayerTypeEnum.BjAudio.getValue()) {
                    BJRecordPlayActivity.lanuchForLocal(getContext(), cuLession.getRealCourseID(), courseName, cuLession.getImagePath(), cuLession, false);

                }
                if (cuLession.getPlayerType() == PlayerTypeEnum.BjRecord.getValue()) {
                    BJRecordPlayActivity.lanuchForLocal(getContext(), cuLession.getRealCourseID(), courseName, cuLession.getImagePath(), cuLession, false);

                } else if (cuLession.getPlayerType() == PlayerTypeEnum.Gensee.getValue()) {

//                    Intent intent = new Intent(getActivity(), LiveVideoActivity.class);
//                    intent.putExtra(ArgConstant.IS_LOCAL_VIDEO, true);
//                    intent.putExtra("course_id", downedCoursewares.get(position).getRealCourseID());
//                    intent.putExtra("play_index", position);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    startActivity(intent);
                    ToastUtils.showShort("此缓存视频已失效，请删除后重新下载观看");

                } else if (cuLession.getPlayerType() == PlayerTypeEnum.BaiJia.getValue()) {
                    final boolean isLandscape= CommonUtils.isPad(getContext()) ? this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE : true;
                    if (TextUtils.isEmpty(cuLession.getSignalFilePath())) {
                        if (NetUtil.isConnected()) {
                            final int curPostion = position;
                            long sessionId = StringUtils.parseLong(cuLession.getSessionId());
                            long roomId = StringUtils.parseLong(cuLession.getRoomId());
                            Observable<BaseResponseModel<CourseApiService.DownToken>> refreshObser = sessionId == 0 ? CourseApiService.getApi().refreshPlaybackToken(roomId)
                                    : CourseApiService.getApi().refreshPlaybackToken(roomId, sessionId);

                            ServiceExProvider.visit(getSubscription(), refreshObser,
                                    new NetObjResponse<CourseApiService.DownToken>() {
                                        @Override
                                        public void onSuccess(BaseResponseModel<CourseApiService.DownToken> model) {
                                            String downloadId = cuLession.getDownloadID();
                                            DownLoadLesson curLession = SQLiteHelper.getInstance().getCourseWare(downloadId);
                                            if (curLession != null) {

                                                SQLiteHelper.getInstance().upDateLessionToken(downloadId, model.data.token);
                                                curLession.setVideoToken(model.data.token);
                                                //LiveVideoActivity.lanuchForLocal(getContext(),curPostion, cuLession.getRealCourseID(),cuLession);
                                                // BJRecordPlayActivity.lanuchForLocal(getContext(), cuLession.getRealCourseID(), courseName, cuLession.getImagePath(), cuLession, true);
                                                CourseWareInfo info= CourseDataConverter.convertLocalLessonToCourseware(cuLession,true);
                                                showLiveVideoForLiveActivity(info,cuLession.getRealCourseID(),cuLession.getImagePath(),isLandscape);
                                                //LiveVideoForLiveActivity.startForLandScapeResult(getActivity(), cuLession.getRealCourseID(), 0, true, info, cuLession.getImagePath(),isLandscape);
                                            } else {
                                                LogUtils.e("refreshToken", downloadId + "");
                                            }
                                        }

                                        @Override
                                        public void onError(String message, int type) {
                                            ToastUtils.showShort("刷新token失败");
                                        }
                                    });
                        } else {
                            ToastUtils.showShort("此缓存视频已失效，请删除后重新下载观看~");
                            return;
                        }
                    } else {
                        //LiveVideoActivity.lanuchForLocal(getContext(),position, cuLession.getRealCourseID(),cuLession);
                      //  BJRecordPlayActivity.lanuchForLocal(getContext(), cuLession.getRealCourseID(), courseName, cuLession.getImagePath(), cuLession, true);
                        CourseWareInfo info= CourseDataConverter.convertLocalLessonToCourseware(cuLession,true);
                        showLiveVideoForLiveActivity(info,cuLession.getRealCourseID(),cuLession.getImagePath(),isLandscape);
                    }


                }
                break;
            case EventConstant.EVENT_DELETE:
                showDeleteDlg(mDownCourseAdapter.getCurrentItem(position), position);
                break;
        }
    }

    private void showLiveVideoForLiveActivity(CourseWareInfo info,String realCourseID,String imagePath,boolean isLandscape){

        Intent intent = new Intent(getActivity(), LiveVideoForLiveActivity.class);
        intent.putExtra("course_id", realCourseID);
        intent.putExtra("play_index", 0);
        intent.putExtra("from_off_line", true);
        intent.putExtra("imgPath", imagePath);
        intent.putExtra(ArgConstant.BEAN, info);
        intent.putExtra("forLandScape",isLandscape);
        this.startActivity(intent);
    }

    private void showDeleteDlg(final DownLoadLesson item, final int position) {
        if (mConfirmDialog == null) {
            mConfirmDialog = DialogUtils.createDialog(getActivity(),
                    "提示", "即将删除所选课程\n该操作不可恢复");
        }
        mConfirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<DownLoadLesson> downIDs = new ArrayList<>();
                DownLoadLesson cuLession = mDownCourseAdapter.getCurrentItem(position);
                downIDs.add(cuLession);
                DownLoadAssist.getInstance().delete(downIDs);
                if (getParentFragment() instanceof DownAllFinishedFragment) {
                    ((DownAllFinishedFragment) getParentFragment()).setHasDelete();
                }
                mHasDeleted = true;
                mDownCourseAdapter.removeAndRefresh(position);
                if (mDownCourseAdapter.getItemCount() == 0) {
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    DownLessionManageFragment.this.finish();
                }
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
                    ((DownAllFinishedFragment) getParentFragment()).onClickDownloadMore(0);
                }
                break;
            case R.id.selAll_btn:
                if (mSelectNum == mDownCourseAdapter.getItemCount()) {
                    mSelectNum = 0;
                    for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                        curLession.setSelect(false);
                    }
                    setDownloadBtnState();
                    mDownCourseAdapter.notifyDataSetChanged();
                } else {
                    mSelectNum = mDownCourseAdapter.getItemCount();
                    for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                        curLession.setSelect(true);
                    }
                    setDownloadBtnState();
                    mDownCourseAdapter.notifyDataSetChanged();
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
                        List<DownLoadLesson> downIDs = new ArrayList<>();
                        for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                            if (curLession.isSelect()) {
                                downIDs.add(curLession);
                            }
                        }
                        if (getParentFragment() instanceof DownAllFinishedFragment) {
                            ((DownAllFinishedFragment) getParentFragment()).setHasDelete();
                        }
                        mHasDeleted = true;
                        DownLoadAssist.getInstance().delete(downIDs);
                        getFinishedCourseWares();
                        mDownCourseAdapter.refresh(downedCoursewares);
                        if (null == downedCoursewares
                                || downedCoursewares.size() <= 0) {
                            Intent data = new Intent();
                            setResult(Activity.RESULT_OK, data);
                            DownLessionManageFragment.this.finish();
                        }
                        if (downedCoursewares.size() == 0) {
                            showNoData(true);
                        }
                    }
                });
                mConfirmDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDLProgress(String s, int i, final long speed) {
    }

    @Override
    public void onDLError(String s, int errorCode) {
    }

    @Override
    public void onDLFinished(String downID) {
        Method.runOnUiThread(getActivity(), new Runnable() {
            @Override
            public void run() {
                getFinishedCourseWares();
                mDownCourseAdapter.refresh(downedCoursewares);
            }
        });
    }

    @Override
    public void onDLPrepare(String downID) {
    }

    @Override
    public void onDLStop(String key,boolean keepWaiting) {
    }

    @Override
    public void onDLFileStorage(String key, long space) {
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

    @Override
    public void onDestroy() {
        DownLoadAssist.getInstance().removeDownloadListener(this);
        // handler.removeCallbacksAndMessages(null);
        super.onDestroy();
       /* if(confirmDialog != null) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }*/
    }


}
