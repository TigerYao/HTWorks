package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huatu.event.IonLoadMoreListener;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.adapter.course.DownloadingAddCourseAdapter;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.business.ztk_vod.fragment.CourseHandDownFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLVodListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDeleteCacheListener;
import com.huatu.handheld_huatu.business.ztk_zhibo.play.CourseDataConverter;
import com.huatu.handheld_huatu.event.LocalVideoDeleteEvent;
import com.huatu.handheld_huatu.helper.image.MyPreloadTarget;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseInfoBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.CourseWareBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.SwitchFrameLayout;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Md5Util;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

//选择课件下载
public class DownLoadListActivity extends SimpleBaseActivity implements OnDLVodListener
        , DownLoadAddPresenter.onTaskAddListener, OnRecItemClickListener, OnDeleteCacheListener {

    @BindView(R.id.download_act_list_view)
    RecyclerViewEx mListView;

    @BindView(R.id.selAll_btn)
    public TextView mSelectAllBtn;

    @BindView(R.id.bt_down_delete)
    public TextView mStartDownBtn;

    @BindView(R.id.bottom_action_layout)
    QMUILinearLayout mAction_layout;

    @BindView(R.id.delNum_txt)
    TextView mDelTipTxt;

    @BindView(R.id.space_tip_txt)
    TextView mSpaceTipTxt;

    @BindView(R.id.switchTabLayout)
    SwitchFrameLayout mSwitchTabLayout;

    @BindView(R.id.tv_definition)
    TextView mDefinitionTxt;

    private int mDefinitonIndex = 0;

    /*  private DownAddAdapter mAdapter;*/

    private DownloadingAddCourseAdapter mDownCourseAdapter;

    private final List<DownLoadLesson> mDownLoadlists = new ArrayList<>();

    private final HashMap<String, DownLoadLesson> mDownlocalMap = new HashMap<>();

    private CustomConfirmDialog mConfirmDialog;
    private String reqFrom;
    private int mSelectCount = 0;//选择的数量
    private long mSelectFileSize = 0;//待下载的文件大小

    int mCacheLessionNum = 0;
    DownLoadAddPresenter mDownLoadAddPresenter;
    boolean mIsFirst = true;
    private CourseInfoBean mCourserInfo;
    private int mCurrentPageIndex = 1;
    private final int mMaxPageSize = 100;
    private boolean mFromCachedFragment = false;

    private final String[] mDefinitonDes = {"省流", "流畅360P", "高清480P"};

    private int defShowIndex;                                               // 默认显示哪一页

    public static void lanuch(Context context, CourseInfoBean mRecordCourseInfo,int defShowIndex) {
        Intent intent = new Intent(context, DownLoadListActivity.class);
        intent.putExtra(ArgConstant.COURSE_ID, mRecordCourseInfo);
        intent.putExtra("index",defShowIndex);
        context.startActivity(intent);
    }

    public void setCustomDefinition(int index) {
        mDefinitonIndex = index;
        if (null != mDefinitionTxt) {
            mDefinitionTxt.setText(mDefinitonDes[Math.min(mDefinitonIndex, 2)]);
        }
        if (null != mDownLoadAddPresenter)
            mDownLoadAddPresenter.setCustomDefinition(index);
    }

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_download;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        initIntent();
        initView();
        DownLoadAssist.getInstance().addDownloadListener(this);
        DownLoadAssist.getInstance().addDeleteCacheListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.e("onNewIntent", "onNewIntent");
        mCurrentPageIndex = 1;
        initIntent();
    }

    private void initIntent() {
        mCourserInfo = (CourseInfoBean) originIntent.getSerializableExtra(ArgConstant.COURSE_ID);
        if (null == mCourserInfo) {
            this.finish();
            return;
        }
        reqFrom = originIntent.getStringExtra("from_act");
        defShowIndex = originIntent.getIntExtra("index", 0);
        mFromCachedFragment = originIntent.getBooleanExtra(ArgConstant.TYPE, false);
        getLessonList(mCourserInfo.courseId);
    }

    public void checkHasCourseInfo() {
        if (mCourserInfo == null) {
            // ToastUtils.showShort("选择出错");
            return;
        }

        LogUtils.e("startDown", mCourserInfo == null ? "" : GsonUtil.GsonString(mCourserInfo));
        DownLoadCourse downLoadCourse = CourseDataConverter.convertCatalogInfoListToDownCourse(mCourserInfo);

        final String imgDataPath = FileUtil.getDownloadCourseImagePath(Md5Util.toMD5(downLoadCourse.getCourseName()));//加密一次防止出现关键字"/"
        if (!FileUtil.isFileExist(imgDataPath)) {
             ImageLoad.downloadPhotoCover(downLoadCourse.getImageURL(), new MyPreloadTarget(imgDataPath) {
                @Override
                public void onDownFinished(boolean isSuccess, String filePath) {
                    try {
                        IoExUtils.copyFile(new File(filePath), new File(imgDataPath), false);
                    } catch (IOException e) {
                    }
                }
            });
        }
        downLoadCourse.setImagePath(imgDataPath);
        LogUtils.e("startDown2", GsonUtil.GsonString(downLoadCourse));
        SQLiteHelper.getInstance().insertDB(downLoadCourse, null);
    }


    private void initView() {
        mAction_layout.setVisibility(View.VISIBLE);
        mStartDownBtn.setText("查看缓存");
        mStartDownBtn.setTextColor(Color.parseColor("#4A4A4A"));

        this.findViewById(R.id.left_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSwitchTabLayout.setOnTabChangeListener(new SwitchFrameLayout.OnTabChangeListener() {
            @Override
            public void onTabChange(int pos) {
                changeTabShowPosition(pos);
            }
        });

        CommonUtils.setSpaceTip(mSpaceTipTxt, 0);
        int commonShapeRadius = DensityUtils.dp2px(this, 20);
        BitmapDrawable solidImageBitmapDrawable = QMUIDrawableHelper.createDrawableWithSize(getResources(), commonShapeRadius, commonShapeRadius, commonShapeRadius / 2, Color.parseColor("#5163F1"));
        mDelTipTxt.setBackground(solidImageBitmapDrawable);

        mDownCourseAdapter = new DownloadingAddCourseAdapter(this, mDownLoadlists);
        mDownCourseAdapter.setOnItemClickListener(this);

        mListView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
        mListView.setPagesize(2);
        mListView.setOnLoadMoreListener(new IonLoadMoreListener() {
            @Override
            public void OnLoadMoreEvent(boolean isRetry) {
                mCurrentPageIndex = isRetry ? mCurrentPageIndex : (mCurrentPageIndex + 1);
                getLessonList(mCourserInfo.courseId);
            }
        });
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setRecyclerAdapter(mDownCourseAdapter);

        if (defShowIndex != 0) {
            changeTabShowPosition(defShowIndex);
            mSwitchTabLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwitchTabLayout.switchTab(defShowIndex);
                        }
                    });
                }
            }, 500);
        }
    }

    private void changeTabShowPosition(int position) {
        if (position == 0) {
            mAction_layout.setVisibility(View.VISIBLE);
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag("handout_down_fag");
            if (fragment != null && fragment.isAdded()) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.hide(fragment);
                // ft.addToBackStack("figure_action_fag");
                ft.commitAllowingStateLoss();
            }
        } else {
            mAction_layout.setVisibility(View.GONE);
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag("handout_down_fag");
            if (fragment == null || !fragment.isAdded()) {
                FragmentTransaction ft = fm.beginTransaction();

                ft.add(R.id.handout_contain_layout, CourseHandDownFragment.getInstance(String.valueOf(mCourserInfo.courseId), false), "handout_down_fag");
                // ft.addToBackStack("figure_action_fag");
                ft.commitAllowingStateLoss();
            } else {
                FragmentTransaction ft = fm.beginTransaction();
                ft.show(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }


    private void checklocalLessionSelect(List<DownLoadLesson> netLoadLession, boolean isFrist) {
        if (isFrist) {
            mDownlocalMap.clear();
            List<DownLoadLesson> localCoursewares = SQLiteHelper.getInstance().getLessons(String.valueOf(mCourserInfo.courseId));
            for (DownLoadLesson bean : localCoursewares) {
                mDownlocalMap.put(bean.getDownloadID(), bean);
            }
            mCacheLessionNum = ArrayUtils.size(localCoursewares);
        }
        if (mCacheLessionNum == 0) {
            for (int j = 0; j < netLoadLession.size(); j++) {
                netLoadLession.get(j).setDownStatus(0);
                netLoadLession.get(j).setSelect(false);
            }
        } else {
            int tmpCacheNum = 0;
            for (int j = 0; j < netLoadLession.size(); j++) {

                DownLoadLesson localLession = mDownlocalMap.get(netLoadLession.get(j).getDownloadID());
                if (localLession != null) {
                    netLoadLession.get(j).setSelect(false);
                    netLoadLession.get(j).setDownStatus(localLession.getDownStatus());
                    tmpCacheNum++;
                } else {
                    netLoadLession.get(j).setDownStatus(0);
                }
            }
            if (mCacheLessionNum < tmpCacheNum) mCacheLessionNum = tmpCacheNum;
        }
    }

    private void setDownloadBtnState(boolean isRefresh) {
        if (mSelectCount > 0) {
            mDelTipTxt.setVisibility(View.VISIBLE);
            mDelTipTxt.setText(String.valueOf(mSelectCount > 99 ? "···" : mSelectCount));
            mStartDownBtn.setText("开始缓存");
            mStartDownBtn.setTextColor(Color.parseColor("#5163F1"));
            AnimUtils.scaleView(mDelTipTxt);
        } else {
            mDelTipTxt.setVisibility(View.GONE);
            mStartDownBtn.setText("查看缓存");
            mStartDownBtn.setTextColor(Color.parseColor("#4A4A4A"));

        }
        CommonUtils.setSpaceTip(mSpaceTipTxt, mSelectFileSize);
        if (isRefresh) {
            mSelectCount = 0;
            mSelectAllBtn.setText("全选");
            return;
        }
        if (mDownCourseAdapter.getItemCount() == (mCacheLessionNum + mSelectCount)) {
            mSelectAllBtn.setText(R.string.pickerview_cancel);
        } else {
            mSelectAllBtn.setText("全选");
        }
    }

    private void selectAll() {
        if (mDownCourseAdapter.getItemCount() == mCacheLessionNum) {
            ToastUtils.showShort("没有可以下载的课件");
            return;
        }
        if (mSelectCount == (mDownCourseAdapter.getItemCount() - mCacheLessionNum)) {
            mSelectCount = 0;
            mSelectFileSize = 0;
            for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                curLession.setSelect(false);
            }
        } else {
            mSelectCount = mDownCourseAdapter.getItemCount() - mCacheLessionNum;
            mSelectFileSize = 0;
            for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                if (curLession.getDownStatus() == 0) {
                    mSelectFileSize += curLession.getSpace();
                    curLession.setSelect(true);
                }
            }
        }
        setDownloadBtnState(false);
        mDownCourseAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.selAll_btn, R.id.bt_down_delete, R.id.changeDefinition_txt})
    public void onClick(View view) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShortToast(R.string.xs_none_net_down);
            return;
        }
        switch (view.getId()) {
            case R.id.changeDefinition_txt:
                DefinitionDialogFragment ratefragment = DefinitionDialogFragment.getInstance((float) mDefinitonIndex);
                ratefragment.show(getSupportFragmentManager(), "definition_down");
                ;
                break;
            case R.id.selAll_btn:
                selectAll();
                break;
            case R.id.bt_down_delete:
                if (mSelectCount == 0) {
                 /*  IoExUtils.saveJsonFile(GsonUtil.GsonString(mDownLoadlists),Constant.NEEDDOWN_CACHE_LIST);
                   onClickPreview();*/

                    if (mFromCachedFragment || "MeFragment".equals(reqFrom)) {
                        setResult(Activity.RESULT_OK);
                        finish();
                        return;
                    }
                    DownCourseManageFragment.lanuch(this, reqFrom);
                    return;
                }
                if (mDownLoadAddPresenter != null) {
                    mDownLoadAddPresenter.checkStartDownload(mDownLoadlists);
                }
                break;
        }
    }

    public void showNextPage() {
        if (mFromCachedFragment || "MeFragment".equals(reqFrom)) {
            setResult(Activity.RESULT_OK);
            finish();
            return;
        }
        DownCourseManageFragment.lanuch(this, reqFrom);
    }

    private void onClickPreview() {
        if ("MeFragment".equals(reqFrom)) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            mDownlocalMap.clear();
            List<DownLoadLesson> localCoursewares = SQLiteHelper.getInstance().getLessons(String.valueOf(mCourserInfo.courseId));
            for (DownLoadLesson bean : localCoursewares) {
                mDownlocalMap.put(bean.getDownloadID(), bean);
            }
            mCacheLessionNum = ArrayUtils.size(localCoursewares);
            mSelectCount = 0;
            mSelectFileSize = 0;
            setDownloadBtnState(true);
        }
    }

    @Override
    public void onTaskAdd() {
        mDownCourseAdapter.notifyDataSetChanged();

        // mAdapter.setDataAndNotify(mDownLoadlists);
    }

    @Override
    public void onAllTaskAdd() {
        PrefStore.putUserSettingInt(String.valueOf(mCourserInfo.courseId), 1);
        UniApplicationLike.getApplicationHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onClickPreview();
            }
        }, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if ("VideoPlay".equals(reqFrom)) {
                if (data != null) {
                    String curCourseId = data.getStringExtra(ArgConstant.COURSE_ID);
                    if (!TextUtils.isEmpty(curCourseId) && (mCourserInfo != null)) {
                        if (String.valueOf(mCourserInfo.courseId).equals(curCourseId)) return;
                        mCurrentPageIndex = 1;
                        getLessonList(StringUtils.parseLong(curCourseId));
                    }
                }
            }
        }
    }

    private void getLessonList(long courseID) {
        if (mCurrentPageIndex == 1) showProgress();
        ServiceExProvider.visit(getSubscription(), CourseApiService.getApi().getAllCourseWare(courseID, mCurrentPageIndex, mMaxPageSize),
                new NetObjResponse<CourseWareBean>() {
                    @Override
                    public void onError(String message, int type) {
                        if (mCurrentPageIndex == 1) {
                            hideProgess();
                            ToastUtils.showMessage(message);
                        } else {
                            mListView.showNetWorkError();
                        }
                    }

                    @Override
                    public void onSuccess(BaseResponseModel<CourseWareBean> model) {
                        if (Method.isActivityFinished(DownLoadListActivity.this)) {
                            return;
                        }

                        CourseWareBean data = model.data;
                        if (mCurrentPageIndex == 1) {
                            mListView.checkloadMore(data.next == 1);
                            hideProgess();
                        } else {
                            mListView.checkloadMore(data.next == 1);
                            mListView.hideloading();
                        }
                        DownLoadCourse mCourse = CourseDataConverter.convertCatalogInfoListToDownCourse(mCourserInfo, data.list);
                        if (mCurrentPageIndex == 1) mDownLoadlists.clear();

                        if (mCourse == null || TextUtils.isEmpty(mCourse.getCourseID()) || ArrayUtils.isEmpty(mCourse.getLessonLists())) {
                            setDownloadBtnState(true);
                            //mAdapter.setDataAndNotify(mDownLoadlists);
                            mDownCourseAdapter.notifyDataSetChanged();
                            return;
                        }
                        if (mDownLoadAddPresenter == null) {
                            mDownLoadAddPresenter = new DownLoadAddPresenter(mCourse, DownLoadListActivity.this, DownLoadListActivity.this);
                        } else
                            mDownLoadAddPresenter.setCurCourse(mCourse);

                        checklocalLessionSelect(mCourse.getLessonLists(), mCurrentPageIndex == 1);
                        mDownLoadlists.addAll(mCourse.getLessonLists());
                        setDownloadBtnState(false);
                        //mAdapter.setDataAndNotify(mDownLoadlists);
                        mDownCourseAdapter.notifyDataSetChanged();

                    }
                });
    }


    @Override
    public void onItemClick(int position, View view, int type) {
        DownLoadLesson curlession = mDownCourseAdapter.getCurrentItem(position);
        if (curlession == null) return;
        switch (type) {
            case EventConstant.EVENT_ALL:
                if (curlession.getDownStatus() == DownLoadStatusEnum.ERROR.getValue()) {// 3
                    startDownLoad(curlession, false);
                }/* else if (curlession.getDownStatus() ==DownLoadStatusEnum.START.getValue()      // 1
                        || curlession.getDownStatus() ==DownLoadStatusEnum.PREPARE.getValue()) { // -1

                    DownLoadAssist.getInstance().setLessionStatus(curlession,DownLoadStatusEnum.STOP);//强制刷新数据库状态
                    DownLoadAssist.getInstance().stop(curlession);
                    curlession.setDownStatus(DownLoadStatusEnum.STOP.getValue());                //4
                    mDownCourseAdapter.notifyDataSetChanged();
                    continueNextDown();
                }*/ else if (curlession.getDownStatus() == DownLoadStatusEnum.INIT.getValue()      // -2
                        || curlession.getDownStatus() == DownLoadStatusEnum.STOP.getValue()) {   //4
                    startDownLoad(curlession, false);
                }
                break;
            case EventConstant.EVENT_DELETE:
                showDeleteDlg(curlession, position);
                break;
            case EventConstant.EVENT_JOIN_IN:
                if (!NetUtil.isConnected()) {
                    ToastUtils.showShortToast(R.string.xs_none_net_down);
                    return;
                }
                showJoinMore(curlession, view);
                mDownCourseAdapter.notifyItemChanged(position);
                break;
        }
    }

    private void showJoinMore(DownLoadLesson curLession, View view) {
        boolean oldSelect = curLession.isSelect();
        curLession.setSelect(!oldSelect);
        if (view instanceof CheckBox) {
            ((CheckBox) view).setChecked(!oldSelect);
        }
        mSelectCount = mSelectCount + (oldSelect ? -1 : 1);
        mSelectFileSize += (oldSelect ? -curLession.getSpace() : curLession.getSpace());
        setDownloadBtnState(false);
        // mAdapter.setDataAndNotify(mDownLoadlists);
    }

    private void continueNextDown() {
        if (mDownlocalMap.size() > 0) {
            for (Map.Entry<String, DownLoadLesson> entry : mDownlocalMap.entrySet()) {
                DownLoadLesson downloadlession = entry.getValue();
                if (downloadlession.getDownStatus() == DownLoadStatusEnum.PREPARE.getValue()
                        || downloadlession.getDownStatus() == DownLoadStatusEnum.INIT.getValue()) {

                    startDownLession(downloadlession, false);
                    break;
                }
            }
        }
    }

  /*  @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        DownLoadLesson item= mAdapter.getItem(position);
        if(mAdapter.isCoursewareDownloaded(item)) {
            ToastUtils.showShort("该课件已缓存完成");
        } else if (mAdapter.isCoursewareDownloading(item)) {
            ToastUtils.showShort("该课件缓存中");
        } else if(item.isSelect()) {
            item.setSelect(false);
            mSelectCount--;
            mSelectFileSize-=item.getSpace();
            setDownloadBtnState(false);
            mAdapter.setDataAndNotify(mDownLoadlists);
        } else {
            item.setSelect(true);
            mSelectCount++;
            mSelectFileSize+=item.getSpace();
            setDownloadBtnState(false);
            mAdapter.setDataAndNotify(mDownLoadlists);
        }
    }*/

    private void startDownLoad(final DownLoadLesson item, final boolean isDelete) {
        if (!NetUtil.isConnected()) {
            ToastUtils.showShort("当前网络不可用");
            return;
        }
        if (NetUtil.isWifi()) {
            startDownLession(item, isDelete);
        } else {
            boolean downFlag = PrefStore.canDownloadIn3G();
            if (downFlag) startDownLession(item, isDelete);
            else {
                DialogUtils.onShowWarnTraffic(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownLession(item, isDelete);
                    }
                });
            }
        }
    }

    private void startDownLession(final DownLoadLesson item, boolean isDelete) {
        if (isDelete) DownLoadAssist.getInstance().delete(item, false);
        List<DownLoadLesson> tmpList = new ArrayList<>();
        tmpList.add(item);
        startDownload(tmpList);
    }

    private void startDownload(final List<DownLoadLesson> lessons) {
        if (Method.isListEmpty(lessons)) {
            return;
        }
        DownLoadAssist.getInstance().download(lessons.get(0));
        lessons.get(0).setDownStatus(DownLoadStatusEnum.START.getValue());//1
        for (int i = 1; i < lessons.size(); i++) {
            lessons.get(i).setDownStatus(DownLoadStatusEnum.PREPARE.getValue());//-1
        }
        mDownCourseAdapter.notifyDataSetChanged();
        // mAdapter.setDataAndNotify(mDataList);
    }

    CustomConfirmDialog confirmDialog;

    private void showDeleteDlg(final DownLoadLesson item, final int position) {
        if (confirmDialog == null) {
            confirmDialog = DialogUtils.createDialog(this, "提示", "");
        }
        confirmDialog.setMessage("即将删除所选课程\n该操作不可恢复");
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownLoadLesson curDelete = mDownCourseAdapter.getCurrentItem(position);
                final int oldDownStatus = curDelete.getDownStatus();
                curDelete.setDownStatus(0);
                curDelete.setSelect(false);
                mCacheLessionNum--;
                LogUtils.e("DownLoadLesson", GsonUtil.GsonString(curDelete));
                DownLoadAssist.getInstance().delete(curDelete);
                mDownCourseAdapter.notifyItemChanged(position);

                //需要刷新下列中的列表了
                if (mFromCachedFragment && (oldDownStatus != DownLoadStatusEnum.FINISHED.getValue()))
                    EventBus.getDefault().post(new LocalVideoDeleteEvent("deletelocalvideo"));
                //showAllUnFinishLession();

                if(oldDownStatus != DownLoadStatusEnum.FINISHED.getValue()){
                     if(null!=mDownLoadAddPresenter&&(null!=mCourserInfo))
                        mDownLoadAddPresenter.sendDownTrack(String.valueOf(mCourserInfo.courseId),curDelete.classId,curDelete.getSubjectName(),false);
                }
            }
        });
        confirmDialog.show();
    }


    @Override
    public void onDeleteDownFile(final String downloadId, final String subjectId) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList = mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getSubjectID(), subjectId)) {
                        mDataList.get(i).setDownStatus(0);//3
                        mDataList.get(i).setSelect(false);
                        mDownCourseAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onDLProgress(final String s, final int progress, final long speed) {
        // TODO Auto-generated method stub
        if (mDownCourseAdapter.getItemCount() == 0) {
            return;
        }
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                mDownCourseAdapter.refreshPartProgess(s, progress);
            }
        });
    }

    @Override
    public void onDLError(final String s, int errorCode) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList = mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), s)) {
                        mDataList.get(i).setDownStatus(DownLoadStatusEnum.ERROR.getValue());//3
                        break;
                    }
                }
                setDataAndRefreshView();
            }
        });
    }

    @Override
    public void onDLFinished(final String downID) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList = mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), downID)) {
                        mDataList.get(i).setDownStatus(DownLoadStatusEnum.FINISHED.getValue());//-1
                        // mDataList.remove(i);
                        break;
                    }
                }
                setDataAndRefreshView();
            }
        });
    }

    @Override
    public void onDLPrepare(final String downID) {
        // do nothing
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList = mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), downID)) {
                        mDataList.get(i).setDownStatus(DownLoadStatusEnum.PREPARE.getValue());//-1
                        break;
                    }
                }
                setDataAndRefreshView();
            }
        });
    }

    @Override
    public void onDLStop(final String key,final boolean keepWaiting) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {

                List<DownLoadLesson> mDataList = mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), key)) {
                        if(keepWaiting)
                            mDataList.get(i).setDownStatus(DownLoadStatusEnum.PREPARE.getValue());//4
                        else{
                            mDataList.get(i).setDownStatus(DownLoadStatusEnum.STOP.getValue());//4
                        }

                        break;
                    }
                }
                setDataAndRefreshView();
                // handler.removeMessages(UI_REFRESH);
                //handler.sendEmptyMessageDelayed(UI_REFRESH, 50);
            }
        });
    }

    @Override
    public void onDLFileStorage(final String key, final long space) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList = mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), key)) {
                        mDataList.get(i).setSpace(space);
                        break;
                    }
                }
                setDataAndRefreshView();
            }
        });
    }

    private final void setDataAndRefreshView() {
        if (mDownCourseAdapter.getItemCount() == 0) {
            finish();
            return;
        }
        mDownCourseAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        DownLoadAssist.getInstance().removeDownloadListener(this);
        DownLoadAssist.getInstance().removeDeleteCacheListener(this);
        super.onDestroy();
        if (mListView != null)
            mListView.setOnLoadMoreListener(null);
        if (mDownLoadAddPresenter != null) mDownLoadAddPresenter.destory();
        if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
            mConfirmDialog.dismiss();
            mConfirmDialog = null;
        }
    }
}
