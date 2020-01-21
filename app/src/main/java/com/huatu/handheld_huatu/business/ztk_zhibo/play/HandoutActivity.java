package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baijiahulian.common.permission.AppPermissions;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.adapter.HandoutAdapter;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.HandoutClickListener;
import com.huatu.handheld_huatu.listener.OnFileDownloadListener;
import com.huatu.handheld_huatu.mvpmodel.zhibo.HandoutBean;
import com.huatu.handheld_huatu.mvppresenter.HandoutPresenterImpl;
import com.huatu.handheld_huatu.mvpview.HandoutView;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.DownloadBaseInfo;
import com.huatu.handheld_huatu.utils.DownloadManager;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.UriUtil;
import com.huatu.handheld_huatu.view.CommonErrorView;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.handheld_huatu.view.CustomProgressDialog;


import java.io.File;
import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

public class HandoutActivity extends BaseActivity implements HandoutView {
    @BindView(R.id.rl_left)
    RelativeLayout rl_left;
    @BindView(R.id.rl_no_handout)
    RelativeLayout rl_no_handout;
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.errorview)
    CommonErrorView errorView;
    private int courseId;
    private HandoutPresenterImpl handoutPresenter;
    private List<HandoutBean.Course> courseList;
    private HandoutAdapter handoutAdapter;
    private CustomProgressDialog updateProgressDialog;
    private AppPermissions rxPermissions;
    private CustomConfirmDialog customDialog;

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_handout;
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

    @Override
    protected void onInitView() {
        super.onInitView();
        String course = originIntent.getStringExtra("courseId");
        if (TextUtils.isEmpty(course) || !TextUtils.isDigitsOnly(course)) {
            return;
        }
        rxPermissions = new AppPermissions(this);
        courseId = Integer.valueOf(course);
        handoutPresenter = new HandoutPresenterImpl(this, compositeSubscription);
        handoutPresenter.getHandoutInfo(courseId);
        errorView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        rl_no_handout.setVisibility(View.GONE);

        handoutAdapter = new HandoutAdapter(courseId, courseList, this);
        listView.setAdapter(handoutAdapter);

        setListener();
    }

    private void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (null != courseList) {
                    HandoutBean.Course course = courseList.get(i);
                    checkoutPres(course);
                }
            }
        });

        handoutAdapter.setOnHandoutClickListener(new HandoutClickListener() {
            @Override
            public void downLoadHandout(int position) {
                HandoutBean.Course course = courseList.get(position);
                checkoutPres(course);
            }

            @Override
            public void deleteHandout(int position) {
                showDeleteDialog(position);
            }
        });
    }

    public void showDeleteDialog(final int position) {
        customDialog = DialogUtils.createExitConfirmDialog(this, null,
                "删除该下载内容", "取消", "删除");
        customDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HandoutBean.Course course = courseList.get(position);
                String substring = course.downloadurl.substring(course.downloadurl.lastIndexOf("/") + 1);
                String newName = courseId + "_" + course.id + "_" + substring;
                String handout = FileUtil.getFilePath("handout", newName);
                FileUtil.deleteFile(handout);
                handoutAdapter.notifyDataSetChanged();
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }

    private void checkoutPres(final HandoutBean.Course course) {
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        CommonUtils.showToast("获取读写SD卡权限失败");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            readHandout(course);
                        } else {
                            CommonUtils.showToast("没有SD卡读写权限");
                        }
                    }
                });
    }

    @OnClick({R.id.rl_left, R.id.errorview})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rl_left:
                this.finish();
                break;
            case R.id.errorview:
                errorView.setVisibility(View.GONE);
                handoutPresenter.getHandoutInfo(courseId);
                break;
        }
    }

    @Override
    public void showProgressBar() {
        this.showProgress();
    }

    @Override
    public void dismissProgressBar() {
        this.hideProgress();
    }

    @Override
    public void onSetData(List<HandoutBean.Course> handoutBeanList) {
        listView.setVisibility(View.VISIBLE);
        rl_no_handout.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        courseList = handoutBeanList;

        handoutAdapter.setCourseList(courseList);
        handoutAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadDataFailed(int type) {
        if (type == 0) {
            listView.setVisibility(View.GONE);
            rl_no_handout.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            rl_no_handout.setVisibility(View.GONE);
            errorView.setVisibility(View.VISIBLE);
        }
    }

    private void readHandout(HandoutBean.Course course) {
        String substring = course.downloadurl.substring(course.downloadurl.lastIndexOf("/") + 1);
        String newName = courseId + "_" + course.id + "_" + substring;
        final String handout = FileUtil.getFilePath("handout", newName);
        if (FileUtil.isFileExist(handout)) {
            readPDF(handout);
        } else {
            DownloadBaseInfo info = new DownloadBaseInfo(course.downloadurl);
            updateProgressDialog = new CustomProgressDialog(HandoutActivity.this);
            updateProgressDialog.setTitle("下载讲义");
            updateProgressDialog.setCancelable(false);

            DownloadManager.getInstance().addDownloadTask(info, handout, new OnFileDownloadListener() {
                @Override
                public void onStart(DownloadBaseInfo baseInfo) {

                }

                @Override
                public void onProgress(DownloadBaseInfo baseInfo, final int percent, final int byteCount, final int totalCount) {
                    Method.runOnUiThread(HandoutActivity.this, new Runnable() {
                        @Override
                        public void run() {
                            updateProgressDialog.setMax(totalCount);
                            updateProgressDialog.setProgress(byteCount);
                        }
                    });
                }

                @Override
                public void onCancel(DownloadBaseInfo baseInfo) {
                    updateProgressDialog.dismiss();
                    CommonUtils.showToast(R.string.download_error);
                }

                @Override
                public void onSuccess(DownloadBaseInfo baseInfo, String mFileSavePath) {
                    updateProgressDialog.dismiss();
                    Method.runOnUiThread(HandoutActivity.this, new Runnable() {
                        @Override
                        public void run() {
                            handoutAdapter.notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onFailed(DownloadBaseInfo baseInfo) {
                    updateProgressDialog.dismiss();
                    CommonUtils.showToast(R.string.download_error);
                }
            }, false);
        }
    }

    private void readPDF(final String handout) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String type = getMIMEType(new File(handout));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(UriUtil.setIntentDataAndType(intent,type,new File(handout),true), 100);
                    } else {
                        CommonUtils.showToast("请安装能打开此文件的程序哦");
                    }
                } catch (Exception e) {
                    CommonUtils.showToast("请安装能打开此文件的程序哦");
                }
            }
        });
    }

    //查看文件的后缀名，对应的MIME类型
    private final String[][] MIME_MapTable = {
            //word文档
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            //excel文档
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            //ppt文档
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            //pdf文档
            {".pdf", "application/pdf"},
    };

    private String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("11-resultCode-" + resultCode);
    }

    public static void newInstance(Context context, String courseId) {
        Intent intent = new Intent(context, HandoutActivity.class);
        intent.putExtra("courseId", courseId);
        context.startActivity(intent);
    }
}
