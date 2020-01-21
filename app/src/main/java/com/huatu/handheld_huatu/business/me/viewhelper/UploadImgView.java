package com.huatu.handheld_huatu.business.me.viewhelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.me.FeedbackActivity;
import com.huatu.handheld_huatu.helper.GlideApp;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.UploadImgUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;


public class UploadImgView extends LinearLayout {

    Context mContext;
    private int resId = R.layout.activity_feedback_upload_ll;
    private View rootView;

    private RelativeLayout upload_rl_one;
    private RelativeLayout upload_rl_two;
    private RelativeLayout upload_rl_three;
    private RelativeLayout upload_rl_four;
    private ImageView upload_iv_add;

    ArrayList<ivInfo> uploadIvList=new ArrayList<ivInfo>();

    viewHolder vh_one=new viewHolder();
    viewHolder vh_two=new viewHolder();
    viewHolder vh_three=new viewHolder();
    viewHolder vh_four=new viewHolder();

    CompositeSubscription compositeSubscription;
    private View anchor;

    public UploadImgView(Context context) {
        super(context);
        initView(context);
    }

    public UploadImgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public UploadImgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(mContext).inflate(resId, this, true);
        ButterKnife.bind(this, rootView);

        upload_rl_one= (RelativeLayout) findViewById(R.id.upload_rl_one);
        upload_rl_two= (RelativeLayout) findViewById(R.id.upload_rl_two);
        upload_rl_three= (RelativeLayout) findViewById(R.id.upload_rl_three);
        upload_rl_four= (RelativeLayout) findViewById(R.id.upload_rl_four);

        upload_iv_add= (ImageView) findViewById(R.id.upload_iv_add);

        vh_one.setView(upload_rl_one);
        vh_two.setView(upload_rl_two);
        vh_three.setView(upload_rl_three);
        vh_four.setView(upload_rl_four);
    }

    public void setTagView(View v){
       this.anchor=v;
    }

    public void setCompositeSubscription(CompositeSubscription compositeSubscription) {
        this.compositeSubscription = compositeSubscription;
    }

    public ArrayList<ivInfo> getUploadIvList() {
        return uploadIvList;
    }

    public class ivInfo{
        public String url;
        String path;
        UploadImgUtil uUploadImgUtil;
        int status;
    }

    class viewHolder{
        ImageView upload_iv_del;
        TextView upload_tv_again;
        ImageView upload_iv;
        RelativeLayout rl;
        public viewHolder() {

        }
        public void setView(RelativeLayout rl) {
            this.rl=rl;
            this.upload_iv_del =  (ImageView) rl.findViewById(R.id.upload_iv_del);
            this.upload_tv_again =  (TextView) rl.findViewById(R.id.upload_tv_again);
            this.upload_iv=  (ImageView) rl.findViewById(R.id.upload_iv);
        }

        public void refreshView(final ivInfo ivInfo){
            if (ivInfo != null) {
                if (rl != null) {
                    rl.setVisibility(View.VISIBLE);
                }
                if (ivInfo.path != null) {
                    if (rl != null) {
                        rl.setVisibility(View.VISIBLE);
                    }
                    GlideApp.with(getContext()).asBitmap().load(ivInfo.path).into(upload_iv);
                    if(ivInfo.url!=null){
                        upload_tv_again.setText("");
                    }else {
                        if(ivInfo.status==-1){
                            upload_tv_again.setText("重新上传");
                            upload_tv_again.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    againIvInfo(ivInfo);
                                }
                            });
                        }else{
                            upload_tv_again.setText("");
                        }
                    }
                    upload_iv_del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteIvInfo(ivInfo);
                        }
                    });
                }else {
                    if (rl != null) {
                        rl.setVisibility(View.GONE);
                    }
                }

            }else {
                if (rl != null) {
                    rl.setVisibility(View.GONE);
                }
            }
        }
    }

    public void addIvInfo(String path) {
        if(getContext() instanceof BaseActivity) {
            ArrayList<ivInfo> removeIvList = new ArrayList<ivInfo>();
            for (ivInfo ivInfo : uploadIvList) {
                if (ivInfo.path == null) {
                    removeIvList.add(ivInfo);
                }
            }
            uploadIvList.removeAll(removeIvList);

            ivInfo iv = new ivInfo();
            iv.path = path;
            iv.uUploadImgUtil = new UploadImgUtil((BaseActivity) getContext());
            uploadIvList.add(iv);
            LogUtils.d("FeedbackActivity", uploadIvList.size());
            startUpload(uploadIvList.size() - 1);
        }else {
            LogUtils.e("getContext() instanceof BaseActivity");
        }
    }

    private void deleteIvInfo(final ivInfo ivInfo) {
        if (mContext instanceof Activity) {
            DialogUtils.onShowConfirmDialog((Activity)mContext, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ivInfo != null) {
                        uploadIvList.remove(ivInfo);
                    }
                    LogUtils.d("FeedbackActivity", uploadIvList.size());
                    refreshIvView();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }, null, "确定删除吗？", null, "确认");
        }
    }

    private void againIvInfo(ivInfo ivInfo) {
        int index= uploadIvList.indexOf(ivInfo);
        startUpload(index);
    }

    public void  startUpload(int index){
        if(index>=0&&index<uploadIvList.size()) {
            final ivInfo iv = uploadIvList.get(index);
            if (iv != null) {
                iv.uUploadImgUtil.startUpload(anchor,compositeSubscription, iv.path, new UploadImgUtil.Call() {
                    @Override
                    public void pathBack(String path, String url) {
                        if (url != null) {
                            LogUtils.d("FeedbackActivity", url);
                            iv.url=url;
                            iv.status=1;
                            if(path!=null) {
                                iv.path = path;
                            }
                            refreshIvView();
                        }else {
                            if(path!=null) {
                                iv.path = path;
                            }
                            iv.status=-1;
                            refreshIvView();
                        }
                    }

                    @Override
                    public void failBack(String path) {
                        if(path!=null) {
                            iv.path = path;
                        }
                        iv.status=-1;
                        refreshIvView();
                    }
                });
            }
        }
    }

    private void refreshIvView() {
        ivInfo ivone, ivtwo, ivthree, ivfour;
        ivone=null;
        ivtwo=null;
        ivthree=null;
        ivfour=null;
        if (0 < uploadIvList.size()) {
            ivone = uploadIvList.get(0);
        }
        if (1 < uploadIvList.size()) {
            ivtwo = uploadIvList.get(1);
        }
        if (2 < uploadIvList.size()) {
            ivthree = uploadIvList.get(2);
        }
        if (3 < uploadIvList.size()) {
            ivfour = uploadIvList.get(3);
        }
        vh_one.refreshView(ivone);
        vh_two.refreshView(ivtwo);
        vh_three.refreshView(ivthree);
        vh_four.refreshView(ivfour);

        if(uploadIvList.size()==4){
            upload_iv_add.setVisibility(View.GONE);
        }else {
            upload_iv_add.setVisibility(View.VISIBLE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (uploadIvList.size()>0) {
            ivInfo ivInfo = uploadIvList.get(uploadIvList.size()-1);
            if (ivInfo!=null&&ivInfo.uUploadImgUtil != null) {
                ivInfo.uUploadImgUtil.onActivityResult(requestCode,resultCode,data);
            }
        }
    }


    @OnClick({R.id.upload_iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.upload_iv_add:
                Method.hideKeyboard(view);
                if (view != null) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addIvInfo(null);
                        }
                    },200);
                };

                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.d("UploadImgView", "onDetachedFromWindow");
    }
}
