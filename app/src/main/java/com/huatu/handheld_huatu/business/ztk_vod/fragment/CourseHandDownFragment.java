package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.huatu.handheld_huatu.BuildConfig;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.course.CourseHandDownAdapter;
import com.huatu.handheld_huatu.base.fragment.ABaseListFragment;
import com.huatu.handheld_huatu.base.fragment.IPageStripTabInitData;
import com.huatu.handheld_huatu.business.other.PdfViewFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownCourseManageFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownHandoutAssist;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DownLoadListActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLHandoutListener;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.SimpleListResponse;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.ui.recyclerview.SpaceItemDecoration;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.StorageUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UriUtil;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.scrollablelayoutlib.ScrollableHelper;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 讲议下载页面
 */

public class CourseHandDownFragment extends ABaseListFragment<SimpleListResponse<HandoutBean.Course>> implements IPageStripTabInitData,
        OnRecItemClickListener, ScrollableHelper.ScrollableContainer,OnDLHandoutListener {

    @BindView(R.id.xi_comm_page_list)
    RecyclerViewEx mWorksListView;

    @BindView(R.id.bottom_action_layout2)
    QMUILinearLayout bottomLayout;

    @BindView(R.id.selAll_btn2)
    public TextView mSelectAllBtn;

    @BindView(R.id.bt_down_delete2)
    public TextView mStartDownBtn;

    @BindView(R.id.delNum_txt2)
    TextView mDelTipTxt;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 110 && mListAdapter != null) {
                mListAdapter.notifyDataSetChanged();
            }
            return true;
        }
    });

    @Override
    protected int getLimit() {
        return 200;
    }

    public int getContentView() {
        return R.layout.course_lesson_down;
    }

    @Override
    protected RecyclerViewEx getListView() {
        return mWorksListView;
    }


    @Override
    public View getScrollableView() {
        return mWorksListView;
    }

    CourseHandDownAdapter mListAdapter;

    private String mCourseId = "";


    List<DownHandout> mDownHandoutList;

    HashMap<String, DownHandout> mDownHandoutMap = new HashMap<>();  //本地缓存
    private HashMap<String,Integer> mDownLoadingMap=new HashMap<>(); //下载中

    public static CourseHandDownFragment getInstance(String courseId, boolean isLocal) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putBoolean(ArgConstant.IS_LOCAL_VIDEO, isLocal);
        CourseHandDownFragment tmpFragment = new CourseHandDownFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    public static void start(Context context, String courseId, boolean isLocal) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.COURSE_ID, courseId);
        args.putBoolean(ArgConstant.IS_LOCAL_VIDEO, isLocal);
        UIJumpHelper.jumpFragment(context,CourseHandDownFragment.class,args);
    }

    @Override
    protected void parserParams(Bundle args) {
        mCourseId = args.getString(ArgConstant.COURSE_ID);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListResponse = new SimpleListResponse<>();
        mListResponse.mAdapterList = new ArrayList<>();
        mListAdapter = new CourseHandDownAdapter(getContext(), mListResponse.mAdapterList);
        mListAdapter.setOnViewItemClickListener(this);

        DownHandoutAssist.getInstance().addDownloadListener(this);
    }

    @Override
    public void onDLError(String s, int errorCode){

    }

    @Override
    public void onDLFinished(String downID){
        if(mDownLoadingMap.containsKey(downID)){
              int postion=mDownLoadingMap.get(downID);
            mListAdapter.getItem(postion).selected=false;
            mListAdapter.getItem(postion).downStatus=DownBtnLayout.FINISH;
            mListAdapter.notifyItemChanged(postion);
         }
    }

    @Override
    public void requestData() {
        super.requestData() ;
        onStripTabRequestData();
    }

    @Override
    public void onStripTabRequestData() {
        mDownHandoutList = SQLiteHelper.getInstance().getAllDownHandouts(mCourseId,false);
        for (DownHandout bean : mDownHandoutList) {
            mDownHandoutMap.put(bean.getSubjectID(), bean);
        }
        onFirstLoad();
    }

    @Override
    protected void onPrepare() {
        super.onPrepare();
        getEmptyLayout().setStatusStringId(R.string.xs_loading_text, R.string.xs_none_date);
        getEmptyLayout().setTipText(R.string.xs_my_empty);
        getEmptyLayout().setEmptyImg(R.drawable.down_no_num);

        int commonShapeRadius = DensityUtils.dp2px(getActivity(), 20);
        BitmapDrawable solidImageBitmapDrawable = QMUIDrawableHelper.createDrawableWithSize(getResources(), commonShapeRadius, commonShapeRadius, commonShapeRadius / 2, Color.parseColor("#5163F1"));
        mDelTipTxt.setBackground(solidImageBitmapDrawable);

        mWorksListView.setPagesize(getLimit());
        mWorksListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWorksListView.setRecyclerAdapter(mListAdapter);

        mDelTipTxt.setVisibility(View.GONE);
        mStartDownBtn.setText("查看缓存");
        mStartDownBtn.setTextColor(Color.parseColor("#4A4A4A"));

        // 侧滑实现
        mWorksListView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getActivity()));
        mWorksListView.addItemDecoration(new SpaceItemDecoration(DensityUtils.dp2px(getContext(), 0.6f)));
    }

    @Override
    protected void setListener() {
        mWorksListView.setOnLoadMoreListener(this);
    }

    @Override
    protected void onLoadData(int offset, int limit) {
        CourseApiService.getApi().getHandoutInfo(StringUtils.parseInt(mCourseId),"1").enqueue(getCallback());
    }

    @Override
    public void onSuccess(SimpleListResponse<HandoutBean.Course> response) {
        for (HandoutBean.Course bean : response.getListResponse()) {
            if (mDownHandoutMap.containsKey(bean.id)) {
                bean.downStatus = DownBtnLayout.FINISH;
                bean.localPath = mDownHandoutMap.get(bean.id).getFileUrl();
            }
        }
        super.onSuccess(response);
    }

    @OnClick({R.id.selAll_btn2, R.id.bt_down_delete2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selAll_btn2:               // 全选、取消全选
                selectAll();
                break;
            case R.id.bt_down_delete2:           // 查看缓存、开始缓存
                downLoad();
                break;
        }
    }

    private void selectAll() {
        List<HandoutBean.Course> list = mListResponse.mAdapterList;
        if (mSelectAllBtn.getText().equals("全选")) {             // 全选
            for (HandoutBean.Course course : list) {
                if (course.downStatus == DownBtnLayout.NORMAL) {
                    course.selected = true;
                }
            }
        } else {                                                 // 取消全选
            for (HandoutBean.Course course : list) {
                course.selected = false;
            }
        }
//        mListAdapter.notifyDataSetChanged();
        handler.sendMessage(Message.obtain(handler, 110));
        reFreshViewState();
    }

    // private SparseArray<DownHandout> mDownLoadingMap = new SparseArray<DownHandout>();


    private void downLoad() {
        if (mStartDownBtn.getText().equals("查看缓存")) {           // 去缓存页面
            //DownCourseManageFragment.lanuch(getActivity(), null);
            if(getActivity() instanceof DownLoadListActivity){
                ((DownLoadListActivity)getActivity()).showNextPage();
            }
        } else {                                                    // 下载选中项
            List<HandoutBean.Course> list = mListResponse.mAdapterList;
            for (int i = 0; i < list.size(); i++) {
                if(i==0&&(getActivity() instanceof DownLoadListActivity)){
                    ((DownLoadListActivity)getActivity()).checkHasCourseInfo();
                }
                HandoutBean.Course course = list.get(i);
                if (course.selected && course.downStatus == DownBtnLayout.NORMAL) {
                    // readHandout(course, i);
                    //if (course.downStatus == DownBtnLayout.DOWNLOADING) return;
                    DownHandoutAssist.getInstance().add(course,mCourseId);
                    mDownLoadingMap.put(course.id,i);
                }
            }
            handler.sendMessage(Message.obtain(handler, 110));
            reFreshViewState();
        }
    }




    CustomConfirmDialog mDeleteDlg;
    private HandoutBean.Course mCurrentItem;
    private int mCurrentPostion;

    private void deleteConfrim() {

        if (mCurrentItem == null) return;
        if (mDeleteDlg == null) {
            mDeleteDlg = DialogUtils.createDialog(getActivity(),
                    "提示", "即将删除所选下载讲义\n该操作不可恢复");
            mDeleteDlg.setPositiveButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int flag = SQLiteHelper.getInstance().deleteSingleHandOut(mCurrentItem.id,mCourseId);
                    if (flag > -1) {
                        FileUtil.deleteFile(mCurrentItem.localPath);
                        LogUtils.e("onItemClick", mCurrentItem.localPath + "");
                        if (false) {
                            mListAdapter.removeAt(mCurrentPostion, mCurrentItem);
                            if (mListAdapter.getItemCount() == 0) showEmptyLayout();
                        } else {
                            mCurrentItem.downStatus = DownBtnLayout.NORMAL;
//                            mListAdapter.notifyDataSetChanged();
                            handler.sendMessage(Message.obtain(handler, 110));
                        }

                    } else ToastUtils.showShort("操作失败");
                }
            });
        }
        if (!mDeleteDlg.isShowing()) {
            mDeleteDlg.show();
        }
    }

    @Override
    public void onItemClick(int position, View view, int type) {
        mCurrentPostion = position;
        mCurrentItem = mListAdapter.getItem(position);
        if (null == mCurrentItem) return;
        switch (type) {
            case EventConstant.SELECT_LESSON:               // 点击Item
                if (mCurrentItem.downStatus == DownBtnLayout.FINISH) {                  // 下载完成，直接打开
                    readPDF(mCurrentItem.localPath);
                } else if (mCurrentItem.downStatus == DownBtnLayout.NORMAL) {           // 否则就选中
                    mCurrentItem.selected = !mCurrentItem.selected;
//                    mListAdapter.notifyItemChanged(position);
                    handler.sendMessage(Message.obtain(handler, 110));
                    reFreshViewState();
                } else {
                    ToastUtils.showShort("正在下载中");
                }
                break;
            case EventConstant.DOWN_SHARE_LESSON:           // 单个下载或分享课件
                if (mCurrentItem.downStatus == DownBtnLayout.FINISH) {               // 下载完成，分享
                    sharePaper(mCurrentItem);
                } else if (mCurrentItem.downStatus == DownBtnLayout.NORMAL) {         // 开始下载

                    if((getActivity() instanceof DownLoadListActivity)){
                        ((DownLoadListActivity)getActivity()).checkHasCourseInfo();
                    }
                    DownHandoutAssist.getInstance().add(mCurrentItem,mCourseId);
                    handler.sendMessage(Message.obtain(handler, 110));
                }
                break;

        }
    }

    public void sharePaper(HandoutBean.Course course) {

        String localPath = course.localPath;
        String newPath = localPath.replace(localPath.substring(localPath.lastIndexOf("/") + 1, localPath.lastIndexOf(".")), course.title);

//        File data = new File(course.localPath);

        if (!FileUtil.isFileExist(newPath)) {
            FileUtil.copyFile(localPath, newPath);
        }
        File newFile = new File(newPath);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", newFile);
        } else {
            uri = Uri.fromFile(newFile);
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType(CommonUtils.getMIMEType(newFile));

        try {
            startActivity(Intent.createChooser(intent, "试卷分享到"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reFreshViewState() {
        if (mListResponse == null || mListResponse.mAdapterList == null || mListResponse.mAdapterList.size() == 0) {
            return;
        }
        List<HandoutBean.Course> mAdapterList = mListResponse.mAdapterList;
        int mSelectCount = 0;
        int mDownLoadCount = 0;
        for (int i = 0; i < mAdapterList.size(); i++) {
            if (mAdapterList.get(i).selected) {
                mSelectCount++;
            }
            if (mAdapterList.get(i).downStatus == DownBtnLayout.FINISH || mAdapterList.get(i).downStatus == DownBtnLayout.DOWNLOADING) {
                mDownLoadCount++;
            }
        }

        // 右下按钮显示
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

        // 坐下角按钮显示
        if (mAdapterList.size() == (mSelectCount + mDownLoadCount)) {
            mSelectAllBtn.setText("取消");
        } else {
            mSelectAllBtn.setText("全选");
        }

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


    @Override
    public void onDestroy() {
        mListAdapter = null;
        //OkHttpUtils.getInstance().cancelTag(this);
        DownHandoutAssist.getInstance().removeDownloadListener(this);
        if (mDeleteDlg != null && mDeleteDlg.isShowing())
            mDeleteDlg.dismiss();
        // OkHttpUtils.cancelTag(this);//取消以Activity.this作为tag的请求
        handler.removeMessages(110);
        super.onDestroy();
    }
}
