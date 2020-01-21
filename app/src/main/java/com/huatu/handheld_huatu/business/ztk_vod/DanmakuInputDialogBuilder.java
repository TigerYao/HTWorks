package com.huatu.handheld_huatu.business.ztk_vod;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.listener.SimpleTextWatcher;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.RoundbgView;
import com.huatu.handheld_huatu.utils.Constant;
import com.huatu.handheld_huatu.utils.EmotionKeyboard;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

/**
 * Created by cjx on 2018\7\26 0026.
 */

public class DanmakuInputDialogBuilder {

    public interface OnDanmaSendListener{
        float getDanmaCurrentTime();
        void  onAddSingleDanmaMessage(int textColor,String content);
    }

    OnDanmaSendListener mOnDanmaSendListener;
    public void setOnDanmaSendListener(OnDanmaSendListener danmaSendListener){
        this.mOnDanmaSendListener=danmaSendListener;
    }

    private Context mContext;

    private int mSelectIndex=-1;
    protected QMUIDialog mDialog;
    private LinearLayout mRootView;
    private EditText mEditText;
    private boolean mCancelable = true;
    private boolean mCanceledOnTouchOutside = true;
    //表情面板
    private EmotionKeyboard mEmotionKeyboard;

    private int[] colorArr=new int[]{Color.WHITE,Color.parseColor("#FF3F47"),Color.parseColor("#FFCA0E"),
            Color.parseColor("#F5FF00"),Color.parseColor("#A8FF3F"),Color.parseColor("#00AB26"),
            Color.parseColor("#56D1FF"),Color.parseColor("#5163F1"),Color.parseColor("#BA10FF"),Color.parseColor("#FF3F9C")
    };

    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";

    private FrameLayout mEmotionLayout;
    private InputMethodManager mInputManager;//软键盘管理类
    private View mContentView;

    TextView mTxtColorView;
     TextView mLimitTxtView;
    private Activity mActivity ;

    private long mClassId,mlessionId;

    public DanmakuInputDialogBuilder(Context context,Activity activity) {

        this.mContext = context;
        mActivity=activity;
    }

    public DanmakuInputDialogBuilder setCancelable(boolean cancelable) {
        mCancelable = cancelable;
        return   this;
    }

    public DanmakuInputDialogBuilder setCourseId(long classId) {
        mClassId = classId;
        return   this;
    }

    public DanmakuInputDialogBuilder setLessionId(long lessionId) {
        mlessionId = lessionId;
        return   this;
    }

    @SuppressWarnings("unchecked")
    public DanmakuInputDialogBuilder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mCanceledOnTouchOutside = canceledOnTouchOutside;
        return   this;
    }

    private View.OnClickListener childOnclickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           if(v.getTag()==null) return;
           int selIndex=StringUtils.parseInt(v.getTag().toString());
           if(selIndex==mSelectIndex) return;

           int curColor=colorArr[selIndex];
           if(v instanceof RoundbgView){
               ((RoundbgView)v).setDotsVisibility(true);
           }
            PrefStore.putSettingInt(Constant.DANMU_SELECTFONT_COLOR,curColor);

            int commonShapeRadius = DensityUtils.dp2px(mRootView.getContext(), 32);
            BitmapDrawable solidImageBitmapDrawable = QMUIDrawableHelper.createDrawableWithSize(mRootView.getContext().getResources(), commonShapeRadius, commonShapeRadius, commonShapeRadius/2, curColor);
            mTxtColorView.setBackground(solidImageBitmapDrawable);
            mTxtColorView.setTextColor(curColor==Color.WHITE?Color.BLACK:Color.WHITE);
            mEditText.setTextColor(curColor);
            mEditText.setHintTextColor(curColor);
            if(mSelectIndex!=-1){
                LinearLayout colorsetLayout=(LinearLayout)mRootView.findViewById(R.id.color_set_layout);
                ((RoundbgView)colorsetLayout.getChildAt(mSelectIndex)).setDotsVisibility(false);
            }
            mSelectIndex=selIndex;
        }
    };

    public void show(){
        if(mDialog!=null) {
            mDialog.show();
            if(!mEmotionLayout.isShown()){
                mEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEditText.requestFocus();
                        mInputManager.showSoftInput(mEditText, 0);
                    }
                }, 300);
            }

        }
    }

    public QMUIDialog create(@StyleRes int style) {
        mDialog = new QMUIDialog(mContext, style);

      /*  Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        // dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =0;//DensityUtils.getStatusHeight(mContext);// 0;
        dialogWindow.setAttributes(lp);*/
        Context dialogContext = mDialog.getContext();

        mRootView = (LinearLayout) LayoutInflater.from(dialogContext).inflate(
                R.layout.play_bottom_input_layout, null);

        mEditText=(EditText)mRootView.findViewById(R.id.input_edTxt);
        mLimitTxtView=(TextView)mRootView.findViewById(R.id.input_num_txt);
        mEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(mLimitTxtView!=null)
                    mLimitTxtView.setText(String.format("%1$d/25", s.toString().length()));
            }
        });
        mEmotionLayout=(FrameLayout)mRootView.findViewById(R.id.emotion_layout);
        mContentView=mRootView.findViewById(R.id.contentView);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
       /* mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event)
                mDialog.dismiss();
                return true;
            }
        });*/
        LinearLayout colorsetLayout=(LinearLayout)mRootView.findViewById(R.id.color_set_layout);
        int selectColor=PrefStore.getSettingInt(Constant.DANMU_SELECTFONT_COLOR,Color.WHITE);
        for(int i=0;i<colorsetLayout.getChildCount();i++){
            colorsetLayout.getChildAt(i).setOnClickListener(childOnclickListener);
            if(selectColor==colorArr[i]){
                mSelectIndex=i;
               ((RoundbgView)colorsetLayout.getChildAt(i)).setDotsVisibility(true);
            }else {
                ((RoundbgView)colorsetLayout.getChildAt(i)).setDotsVisibility(false);
            }
        }

        mTxtColorView=(TextView)mRootView.findViewById(R.id.txtcolor_txt);
        mEmotionKeyboard = EmotionKeyboard.with(mActivity)
                .setEmotionView(mEmotionLayout)//绑定表情面板
                .bindToContent(mContentView)//绑定内容view
                .bindToEditText(mEditText)//判断绑定那种EditView
                .bindToEmotionButton(mTxtColorView);//绑定表情按钮

        mInputManager=mEmotionKeyboard.getInPutManager();

        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        int commonShapeRadius = DensityUtils.dp2px(mRootView.getContext(), 32);
        BitmapDrawable solidImageBitmapDrawable = QMUIDrawableHelper.createDrawableWithSize(mRootView.getContext().getResources(), commonShapeRadius, commonShapeRadius, commonShapeRadius/2, selectColor);
        mTxtColorView.setBackground(solidImageBitmapDrawable);
        mTxtColorView.setTextColor(selectColor==Color.WHITE?Color.BLACK:Color.WHITE);
        mEditText.setTextColor(selectColor);
        mEditText.setHintTextColor(selectColor);
        mRootView.findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              sendDanmuMessage();
            }
        });

/*

        mDialogView = (QMUIDialogView) mRootView.findViewById(R.id.dialog);
        mDialogView.setOnDecorationListener(mOnDecorationListener);
        mAnchorTopView = mRootView.findViewById(R.id.anchor_top);
        mAnchorBottomView = mRootView.findViewById(R.id.anchor_bottom);
        // title
        onCreateTitle(mDialog, mDialogView, dialogContext);

        //content
        onCreateContent(mDialog, mDialogView, dialogContext);

        // 操作
        onCreateHandlerBar(mDialog, mDialogView, dialogContext);
*/


        mDialog.addContentView(mRootView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.setCancelable(mCancelable);
        mDialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        onAfter(mDialog, mRootView, dialogContext);
        return mDialog;
    }

    public void destory(){
          mActivity=null;
          mLimitTxtView=null;
          mEmotionKeyboard.destory();
     }

    private void sendDanmuMessage( ){

        if(!NetUtil.isConnected()){
            ToastUtils.showShort("当前网络不可用");
            return;
        }
        String msgContent=mEditText.getText().toString();
        if(TextUtils.isEmpty(msgContent)){
            ToastUtils.showShort("请输入内容");
            return;
        }
        if(mOnDanmaSendListener!=null){
            float time= mOnDanmaSendListener.getDanmaCurrentTime();
            int selectColor=PrefStore.getSettingInt(Constant.DANMU_SELECTFONT_COLOR,Color.WHITE);

            ServiceExProvider.visit(CourseApiService.getApi().addDanmu(selectColor,mClassId,msgContent,mlessionId,String.valueOf(time)));
            mOnDanmaSendListener.onAddSingleDanmaMessage(selectColor,msgContent);
        }
        if(mDialog!=null&&mDialog.isShowing())
            mDialog.dismiss();


    }

    //@Override
    protected void onAfter(QMUIDialog dialog, LinearLayout parent, Context context) {
        // super.onAfter(dialog, parent, context);
       // mInputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mEditText.setText("");
                if(null!=mEmotionLayout) mEmotionLayout.setVisibility(View.GONE);

                mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        });
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mEditText.requestFocus();
                mInputManager.showSoftInput(mEditText, 0);
            }
        }, 300);
    }


}
