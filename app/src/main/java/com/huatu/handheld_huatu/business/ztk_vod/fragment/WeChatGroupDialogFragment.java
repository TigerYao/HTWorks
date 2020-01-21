package com.huatu.handheld_huatu.business.ztk_vod.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.helper.image.MyPreloadTarget;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.CommonUtils;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.ImageLoad;
import com.huatu.handheld_huatu.utils.IoExUtils;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.StorageUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.utils.WXUtils;
import com.huatu.utils.DensityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.functions.Action1;

/**
 * Created by Administrator on 2019\4\8 0008.
 */

public class WeChatGroupDialogFragment extends DialogFragment implements View.OnClickListener {

    private String mWechatNumber;
    private String mWechatQrCode;

    public static WeChatGroupDialogFragment getInstance(String wechatNumber,String wechatQrCode) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.TITLE, wechatNumber);
        args.putString(ArgConstant.TYPE,wechatQrCode);

        WeChatGroupDialogFragment tmpFragment = new WeChatGroupDialogFragment();
        tmpFragment.setArguments(args);
        return tmpFragment;
    }

    protected void parserParams(Bundle args) {
        mWechatNumber = args.getString(ArgConstant.TITLE);
        mWechatQrCode=args.getString(ArgConstant.TYPE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parserParams(getArguments());
        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }



    View mRootView;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.play_wechat_add_layout, null);
        Dialog dialog = new Dialog(getActivity(), R.style.DimThemeDialogPopup );
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);


        //dialog.setOnShowListener(this);

        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
      /*  WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =DensityUtils.dp2px(mContext,60);// 0;
        dialogWindow.setAttributes(lp);*/

        mRootView=view;
        ((TextView)view.findViewById(R.id.wechat_num_txt)).setText("微信号："+String.valueOf(mWechatNumber));
        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.scan_add_wechat).setOnClickListener(this);
        view.findViewById(R.id.save_wechatpic_btn).setOnClickListener(this);

        ImageView tmpImgView=view.findViewById(R.id.wechat_code_img);
        ImageLoad.displaynoCacheImage(getContext(),R.drawable.trans_bg,mWechatQrCode,tmpImgView);
        return dialog;
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.scan_add_wechat:
                WXUtils.openWeXinQr(getContext());
                break;
            case R.id.save_wechatpic_btn:
               // saveCurPic();
                CommonUtils.checkPower(getActivity(), new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                       if (TextUtils.isEmpty(mWechatQrCode)) {
                            ToastUtils.showShort("二维码图片为空");
                            return;
                        }
                        shootScreen();
                   }
                });
                break;
         }
    }

    private void shootScreen(){

        View rootView=mRootView.findViewById(R.id.whole_content);
        int viewHeight=rootView.getHeight()- DensityUtils.dp2px(getContext(),74);
        Bitmap  mSourceBitmap = ImageLoad.getBitmapPool().get(DisplayUtil.getScreenWidth(),viewHeight , Bitmap.Config.RGB_565);
      //  LogUtils.e("BlurRxTask",mSourceBitmap==null? "null":"not null"+"  "+mSourceBitmap.getConfig().name()+"_");
        if (mSourceBitmap == null) {
            mSourceBitmap = Bitmap.createBitmap(DisplayUtil.getScreenWidth(), viewHeight, Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(mSourceBitmap);
        rootView.draw(canvas);

        try {
            File fileDir = StorageUtils.getGalleryPath();
            File desfile = new File(fileDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg");

            //文件输出流
            FileOutputStream fileOutputStream=new FileOutputStream(desfile);
            //压缩图片，如果要保存png，就用Bitmap.CompressFormat.PNG，要保存jpg就用Bitmap.CompressFormat.JPEG,质量是100%，表示不压缩
            mSourceBitmap.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
            //写入，这里会卡顿，因为图片较大
            fileOutputStream.flush();
            //记得要关闭写入流
            fileOutputStream.close();
            //成功的提示，写入成功后，请在对应目录中找保存的图片

            MediaScannerConnection.scanFile(UniApplicationContext.getContext(),
                    new String[]{desfile.getAbsolutePath()}, new String[]{"image/jpeg"},null);
            ToastUtils.showShort("保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //失败的提示
            ToastUtils.showShort("保存失败");
        } catch (IOException e) {
            e.printStackTrace();
            //失败的提示
            ToastUtils.showShort("保存失败");
        }finally {
            ImageLoad.getBitmapPool().put(mSourceBitmap);
        }
     }

    private void saveCurPic(){

        if(TextUtils.isEmpty(mWechatQrCode)){
            ToastUtils.showShort("二维码图片为空");
            return;
        }



       CommonUtils.checkPower(getActivity(), new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                String picUrl= mWechatQrCode;
                LogUtils.e("saveCurPic",picUrl);
                ImageLoad.downloadPhotoCover(picUrl,new MyPreloadTarget(picUrl) {

                    @Override
                    public void onDownFinished(boolean isSuccess, String filePath) {
                        if (!isSuccess) {
                            ToastUtils.showShort("下载失败");
                            return;
                        }
                        try {
                            File fileDir = StorageUtils.getGalleryPath();
                            File desfile = new File(fileDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg");
                            IoExUtils.copyFile(new File(filePath), desfile, false);

                            MediaScannerConnection.scanFile(UniApplicationContext.getContext(),
                                    new String[]{desfile.getAbsolutePath()}, new String[]{"image/jpeg"},null);
                            ToastUtils.showShort("保存成功");

                        } catch (IOException e) {
                            ToastUtils.showShort("保存失败");
                        }
                    }

                });
            }
        });


    }
}
