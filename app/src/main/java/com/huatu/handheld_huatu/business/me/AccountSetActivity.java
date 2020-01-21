package com.huatu.handheld_huatu.business.me;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseListResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.base.SimpleBaseActivity;
import com.huatu.handheld_huatu.business.arena.activity.TeacherPreviewPaperActivity;
import com.huatu.handheld_huatu.mvpmodel.previewpaper.PreviewPaperBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.widget.switchbutton.SwitchButton;

import java.util.ArrayList;

/**
 * 我的==设置
 * Created by chq on 2017/8/25.
 */

public class AccountSetActivity extends SimpleBaseActivity {

    private final static String TAG = "AccountSetActivity";
    private RelativeLayout rl_top_title_bar;
    private RelativeLayout rl_left_top_bar;
    private RelativeLayout rl_download;
    private SwitchButton image_download;
    private RelativeLayout rl_about;
    //缓存
    private boolean downloadFlag = true;
    private ArrayList<PreviewPaperBean> previewPaperBeans;          // 白名单老师课件的试卷

    @Override
    protected int onSetRootViewId() {
        return R.layout.activity_account_set;
    }

    @Override
    protected void onInitView() {
        initView(rootView);
        setListener();
    }

    private void initView(View view) {
        rl_top_title_bar = view.findViewById(R.id.rl_top_title_bar);
        rl_left_top_bar = view.findViewById(R.id.rl_left_top_bar);
        rl_download = view.findViewById(R.id.rl_download);
        rl_about = view.findViewById(R.id.rl_about);
        image_download = view.findViewById(R.id.image_download);
        image_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDownStatus();
            }
        });
      /*  image_download.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                switchDownStatus();
                return true;
            }
        });*/
        downloadFlag = PrefStore.canDownloadIn3G();
        image_download.setCheckedImmediately(downloadFlag);


    }

    private void setListener() {
        rl_top_title_bar.setOnClickListener(new View.OnClickListener() {

            int clickTimes = 1;

            @Override
            public void onClick(View v) {
                if (clickTimes % 3 == 0) {
                    getPreviewPaper();
                }
                clickTimes++;
            }
        });
        //返回栏
        rl_left_top_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSetActivity.this.finish();
            }
        });
        // 允许2G/3G/4G环境缓存
        rl_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDownStatus();
            }
        });
        // 关于
        rl_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.newInstance(AccountSetActivity.this);
            }
        });
    }

    private void switchDownStatus() {
        if (downloadFlag) {
            downloadFlag = false;
            image_download.setChecked(false);

            PrefStore.setCanDownloadIn3G(false);
            //CommonUtils.showToast("关闭2G/3G/4G环境缓存");
            ToastUtils.showBottom(AccountSetActivity.this,"关闭2G/3G/4G环境缓存");
        } else {
            downloadFlag = true;
            // image_download.setBackgroundResource(R.mipmap.open_icon);
            image_download.setChecked(true);
            PrefStore.setCanDownloadIn3G(true);
           // CommonUtils.showToast("开启2G/3G/4G环境缓存");
            ToastUtils.showBottom(AccountSetActivity.this,"开启2G/3G/4G环境缓存");
        }
    }

    public void getPreviewPaper() {
        // 老师是否是白名单，并且获取试题，如果试题不为null，就显示跳转按钮
        ServiceProvider.getPreviewPaper(getSubscription(), SpUtils.getUserSubject(), new NetResponse() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onListSuccess(BaseListResponseModel model) {
                super.onListSuccess(model);
                if (model.code == 1000000 && model.data != null && model.data.size() > 0) {
                    previewPaperBeans = (ArrayList<PreviewPaperBean>) model.data;
                    Intent intent = new Intent(AccountSetActivity.this, TeacherPreviewPaperActivity.class);
                    intent.putParcelableArrayListExtra("previewPaperBeans", previewPaperBeans);
                    startActivity(intent);
                }
            }
        });
    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, AccountSetActivity.class);
        context.startActivity(intent);
    }
}
