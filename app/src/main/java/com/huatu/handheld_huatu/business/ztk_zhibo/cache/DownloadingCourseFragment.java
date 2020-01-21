package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.adapter.course.DownloadingCourseAdapter;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLVodListener;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.DownLoadStatusEnum;
import com.huatu.handheld_huatu.ui.MenuItem;
import com.huatu.handheld_huatu.ui.TitleBar;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.NetUtil;
import com.huatu.handheld_huatu.utils.PrefStore;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by cjx on 2018\7\7 0007.
 */

public class DownloadingCourseFragment extends AbsSettingFragment implements OnDLVodListener,OnRecItemClickListener,
        AStripTabsFragment.IStripTabInitData,OnSwitchListener {

    @BindView(R.id.space_tip_txt)
    TextView mSpaceTipTxt;

    @BindView(R.id.lv_downloaded)
    RecyclerView mListView;

    @BindView(R.id.downAction_btn)
    TextView mSwitchActionBtn;

    @BindView(R.id.bt_down_delete)
    TextView mDelBtn;

    @BindView(R.id.delNum_txt)
    TextView mDelTipTxt;

    @BindView(R.id.selAll_btn)
    TextView mSelAllBtn;

    @BindView(R.id.bottom_action_layout)
    QMUILinearLayout mAction_layout;

    private boolean mHasLoaded=false;
    private int mSelectNum=0;
    private boolean mForResult=false;
    private boolean mHasDeleted=false;

    public static void lanuchForResult(Fragment context , int requestCode){

        Bundle tmpArg=new Bundle();
        tmpArg.putBoolean(ArgConstant.FROM_ACTION,true);
        UIJumpHelper.jumpFragment(context,requestCode,DownloadingCourseFragment.class,tmpArg);
    }




    @Override
    protected void parserParams(Bundle arg) {
        super.parserParams(arg);
        mForResult = arg.getBoolean(ArgConstant.FROM_ACTION);
    }

    @Override
    public int getContentView() {
        return R.layout.down_loading_manage_layout;
    }

    DownloadingCourseAdapter mDownCourseAdapter;
    SwipeItemLayout.OnSwipeItemTouchListener mOnSwipeItemTouchListener;

    private CustomConfirmDialog confirmDialog;
    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
   /*     setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);*/

    }

    @Override
    public int isEditMode(){
        if(mDownCourseAdapter.getItemCount()<=0) return 2;
        return mDownCourseAdapter.isEditMode()?1:0;
    }

    @Override
    public void switchMode(){

        if(mDownCourseAdapter.isEditMode()) {//当前删除模式->normal
            mSelectNum=0;
            mSelAllBtn.setText("全选");
            //   mRightMenu.setText(R.string.edit);
            for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                curLession.setSelect(false);
            }
            mDownCourseAdapter.setActionMode(false);
            mAction_layout.setVisibility(View.GONE);
            if(mOnSwipeItemTouchListener!=null)
                mOnSwipeItemTouchListener.setCanSwipe(true);
        }
        else {//当前normal->删除模式
            // mRightMenu.setText(R.string.pickerview_cancel);
            mAction_layout.setVisibility(View.VISIBLE);
            mDownCourseAdapter.setActionMode(true);
            mDelTipTxt.setVisibility(View.GONE);
            if(mOnSwipeItemTouchListener!=null)
                mOnSwipeItemTouchListener.setCanSwipe(false);
        }
    }

    private Runnable mDelayPauseCount=new Runnable() {
        public int mPauseCount=0;

        @Override
        public void run() {
            if(null!=mDownCourseAdapter&&mDownCourseAdapter.getItemCount()>0){
                int pauseCount=0;
                boolean hasNoPause=false;
                for(DownLoadLesson lesson:mDownCourseAdapter.getAllLession()){
                     if(lesson.getDownStatus()==DownLoadStatusEnum.STOP.getValue())
                        pauseCount++;
                     else {
                         hasNoPause=true;
                         break;
                     }
                }
                mPauseCount=pauseCount;
                boolean isAllPause=hasNoPause?false:mPauseCount==mDownCourseAdapter.getItemCount();
                if(null!=mSwitchActionBtn){
                    mSwitchActionBtn.setText(isAllPause ?"全部开始":"全部暂停");
                    mSwitchActionBtn.setSelected(isAllPause?false:true);
                }
            }
        }
    };

    @Override
    protected void setListener() {
        super.setListener(); //
        mDownCourseAdapter=new DownloadingCourseAdapter(getContext(),new ArrayList<DownLoadLesson>(),false);
        mDownCourseAdapter.setOnItemClickListener(this);
        mOnSwipeItemTouchListener=new SwipeItemLayout.OnSwipeItemTouchListener(getContext());
        mListView.addOnItemTouchListener(mOnSwipeItemTouchListener);
        DownLoadAssist.getInstance().addDownloadListener(this);

        int commonShapeRadius = DensityUtils.dp2px(getContext(), 20);
        BitmapDrawable solidImageBitmapDrawable = QMUIDrawableHelper.createDrawableWithSize(getResources(), commonShapeRadius, commonShapeRadius, commonShapeRadius/2, Color.parseColor("#FF3F47"));
        mDelTipTxt.setBackground(solidImageBitmapDrawable);
        mDelBtn.setTextColor(Color.parseColor("#FF3F47"));
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListView.setAdapter(mDownCourseAdapter);
    }

    @Override
    public void onStripTabRequestData(){
         if(!mHasLoaded){
            mHasLoaded=true;
            List<DownLoadLesson> tmpList = SQLiteHelper.getInstance().getUnFinishedCourseWare();
            mDownCourseAdapter.refresh(tmpList);
            getCurdownStatus();
        }
    }

    @Override
    public void requestData() { }

    private void getCurdownStatus(){
        if(isFragmentFinished()) return;
        List<DownLoadLesson> mDataList = mDownCourseAdapter.getAllLession();
         int stopNum = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getDownStatus() == DownLoadStatusEnum.STOP.getValue()) {//4
                stopNum++;
            }
        }

        if (stopNum == mDataList.size()) {
            mSwitchActionBtn.setText("全部开始");
            mSwitchActionBtn.setSelected(false);

        } else {
            mSwitchActionBtn.setText("全部暂停");
            mSwitchActionBtn.setSelected(true);
        }
    }

    private void continueNextDown(){
        if(mDownCourseAdapter.getItemCount()>0){
            for (DownLoadLesson downloadlession : mDownCourseAdapter.getAllLession()) {
                 if(downloadlession.getDownStatus()==DownLoadStatusEnum.PREPARE.getValue()
                        ||downloadlession.getDownStatus()==DownLoadStatusEnum.INIT.getValue()){

                    startDownLession(downloadlession,false);
                    break;
                }
            }
        }
     }


    @Override
    public void onItemClick(int position,View view,int type){

        if(mDownCourseAdapter.isEditMode()){
            DownLoadLesson curLession=mDownCourseAdapter.getCurrentItem(position);
            boolean oldSelect=curLession.isSelect();
            curLession.setSelect(!oldSelect);
            if(view instanceof CheckBox){
                ((CheckBox)view).setChecked(!oldSelect);
            }
            mSelectNum=mSelectNum+(oldSelect? -1:1);
            if(mSelectNum>0){
                mDelTipTxt.setVisibility(View.VISIBLE);
                mDelTipTxt.setText(String.valueOf(mSelectNum));
                AnimUtils.scaleView(mDelTipTxt);
            }else
                mDelTipTxt.setVisibility(View.GONE);

            if (mDownCourseAdapter.getItemCount() == mSelectNum) {
                mSelAllBtn.setText(R.string.pickerview_cancel);
            }else {
                mSelAllBtn.setText("全选");
            }
            return;
        }
         switch (type){
            case EventConstant.EVENT_ALL:
                DownLoadLesson curlession=  mDownCourseAdapter.getCurrentItem(position);
                if(curlession==null) {
                    return;
                }
                if (curlession.getDownStatus() ==DownLoadStatusEnum.ERROR.getValue()) {// 3
                    startDownLoad(curlession,false);
                 } else if (curlession.getDownStatus() ==DownLoadStatusEnum.START.getValue()      // 1
                        || curlession.getDownStatus() ==DownLoadStatusEnum.PREPARE.getValue()) { // -1

                    int oldDownStatus=curlession.getDownStatus();
                    DownLoadAssist.getInstance().setLessionStatus(curlession,DownLoadStatusEnum.STOP);//强制刷新数据库状态


                    if(curlession.getDownStatus() ==DownLoadStatusEnum.START.getValue()){
                        DownLoadAssist.getInstance().stop(curlession);
                    }
                    curlession.setDownStatus(DownLoadStatusEnum.STOP.getValue());                //4
                    mDownCourseAdapter.notifyDataSetChanged();
                    if(oldDownStatus ==DownLoadStatusEnum.START.getValue()){
                        continueNextDown();
                    }

                } else if (curlession.getDownStatus() ==DownLoadStatusEnum.INIT.getValue()      // -2
                        || curlession.getDownStatus() == DownLoadStatusEnum.STOP.getValue()) {   //4
                    startDownLoad(curlession,false);
                }
                UniApplicationLike.getApplicationHandler().removeCallbacks(mDelayPauseCount);
                UniApplicationLike.getApplicationHandler().postDelayed(mDelayPauseCount,100);
                break;
            case EventConstant.EVENT_DELETE:
                DownLoadLesson curlession2=  mDownCourseAdapter.getCurrentItem(position);
                showDeleteDlg(curlession2,position);
                UniApplicationLike.getApplicationHandler().removeCallbacks(mDelayPauseCount);
                UniApplicationLike.getApplicationHandler().postDelayed(mDelayPauseCount,100);
                break;
        }
    }

    CustomConfirmDialog mConfirmDialog;

    @OnClick({R.id.downAction_btn,R.id.selAll_btn,R.id.bt_down_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.downAction_btn:
                switchAllAction();
                break;
            case R.id.selAll_btn:
                if(mSelectNum==mDownCourseAdapter.getItemCount()){
                    mSelAllBtn.setText("全选");
                    mSelectNum=0;
                    mDelTipTxt.setVisibility(View.GONE);
                    for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                        curLession.setSelect(false);
                    }
                    mDownCourseAdapter.notifyDataSetChanged();
                }else {
                    mSelAllBtn.setText(R.string.pickerview_cancel);
                    mSelectNum=mDownCourseAdapter.getItemCount();
                    mDelTipTxt.setText(String.valueOf(mSelectNum));
                    mDelTipTxt.setVisibility(View.VISIBLE);
                    AnimUtils.scaleView(mDelTipTxt);
                    for (DownLoadLesson curLession : mDownCourseAdapter.getAllLession()) {
                        curLession.setSelect(true);
                    }
                    mDownCourseAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.bt_down_delete:
                if(mSelectNum==0){
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
                        for (DownLoadLesson curLession:mDownCourseAdapter.getAllLession()) {
                            if (curLession.isSelect()) {
                                downIDs.add(curLession);
                            }
                        }
                        DownLoadAssist.getInstance().delete(downIDs);
                        mHasDeleted=true;
                        if(getParentFragment() instanceof DownAllLoadingFragment){
                            ((DownAllLoadingFragment)getParentFragment()).setHasDelete();
                        }
                        List<DownLoadLesson> tmpList = SQLiteHelper.getInstance().getUnFinishedCourseWare();
                        mDownCourseAdapter.refresh(tmpList);
                        if(mDownCourseAdapter.getItemCount()<=0){

                            if(null!=mDelTipTxt){mDelTipTxt.setVisibility(View.GONE);}
                        }

                        /*if (mDownCourseAdapter.getItemCount() <= 0) {

                            Intent data = new Intent();
                            setResult(Activity.RESULT_OK, data);
                            DownloadingCourseFragment.this.finish();
                        }*/
                    }
                });
                mConfirmDialog.show();
                break;
        }
    }



    private void switchAllAction(){
        List<DownLoadLesson> mDataList=  mDownCourseAdapter.getAllLession();
        if(ArrayUtils.isEmpty(mDataList)) return;
        if("全部暂停".equals(mSwitchActionBtn.getText())) {
            mSwitchActionBtn.setText("全部开始");
            mSwitchActionBtn.setSelected(false);

            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getDownStatus() ==DownLoadStatusEnum.PREPARE.getValue() //-1
                        || mDataList.get(i).getDownStatus() == DownLoadStatusEnum.START.getValue()//1
                        || mDataList.get(i).getDownStatus() ==DownLoadStatusEnum.INIT.getValue() ) {//-2
                    mDataList.get(i).setDownStatus(DownLoadStatusEnum.STOP.getValue());//4
                    DownLoadAssist.getInstance().setLessionStatus(mDataList.get(i),DownLoadStatusEnum.STOP);
                    // DownLoadAssist.getInstance().stop(mDataList.get(i));
                }
            }
            DownLoadAssist.getInstance().stopAll(false);
            mDownCourseAdapter.resetAndRefresh();
        } else if("全部开始".equals(mSwitchActionBtn.getText())) {
            mSwitchActionBtn.setText("全部暂停");
            mSwitchActionBtn.setSelected(true);
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getDownStatus() !=DownLoadStatusEnum.FINISHED.getValue()  // 2
                        || mDataList.get(i).getDownStatus() != DownLoadStatusEnum.ERROR.getValue()) {//   3
                    mDataList.get(i).setDownStatus(DownLoadStatusEnum.PREPARE.getValue()); //-1
                    DownLoadAssist.getInstance().setLessionStatus(mDataList.get(i),DownLoadStatusEnum.PREPARE);
                }
            }
            startDownload(mDataList);
        }
        if(mDataList.size() == 0) {
            finish();
            return;
        }
    }

    private void startDownLoad(final DownLoadLesson item, final boolean isDelete){

        if(!NetUtil.isConnected()){
            ToastUtils.showShort("当前网络不可用");
            return;
        }
        if (NetUtil.isWifi()) {
            startDownLession(item, isDelete);
        } else {
            boolean downFlag = PrefStore.canDownloadIn3G();
            if (downFlag) startDownLession(item, isDelete);
            else {
                DialogUtils.onShowWarnTraffic(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDownLession(item, isDelete);
                    }
                });

            }
        }
    }

    private void startDownLession(final DownLoadLesson item,boolean isDelete){
        if(isDelete)  DownLoadAssist.getInstance().delete(item, false);
        List<DownLoadLesson> tmpList = new ArrayList<>();
        tmpList.add(item);
        startDownload(tmpList);
    }

    private void startDownload(final List<DownLoadLesson> lessons) {
        if(Method.isListEmpty(lessons)) {
            return;
        }
        DownLoadAssist.getInstance().download(lessons.get(0));
        lessons.get(0).setDownStatus(DownLoadStatusEnum.START.getValue());//1
        for(int i = 1; i < lessons.size(); i++) {
            lessons.get(i).setDownStatus(DownLoadStatusEnum.PREPARE.getValue());//-1
        }
        mDownCourseAdapter.resetAndRefresh();
       // mAdapter.setDataAndNotify(mDataList);
    }

    private void showDeleteDlg(final DownLoadLesson item, final int position) {
        if(confirmDialog == null) {
            confirmDialog = DialogUtils.createDialog(getActivity(), "提示","");
        }
        confirmDialog.setMessage("即将删除所选课程\n该操作不可恢复");
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownLoadLesson curDelete=mDownCourseAdapter.getCurrentItem(position);
                LogUtils.e("DownLoadLesson", GsonUtil.GsonString(curDelete));
                DownLoadAssist.getInstance().delete(curDelete);
                mDownCourseAdapter.removeAndRefresh(position);
                mHasDeleted=true;
                if(getParentFragment() instanceof DownAllLoadingFragment){
                    ((DownAllLoadingFragment)getParentFragment()).setHasDelete();
                }
                /*if (mDownCourseAdapter.getItemCount() <= 0) {
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    DownloadingCourseFragment.this.finish();
                }*/
                //showAllUnFinishLession();
            }
        });
        confirmDialog.show();
    }

    private void setDataAndRefreshView() {

        if(isFragmentFinished()) return;
        if(mDownCourseAdapter.getItemCount() == 0) {
            finish();
            return;
        }
        mDownCourseAdapter.notifyDataSetChanged();
       // mDownCourseAdapter.refresh(mDataList);
      /*  if(isEdit) {
            int selectNum = 0;
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).isSelect()) {
                    selectNum++;
                }
            }
            if (mDataList.size() == selectNum) {
                handler.sendEmptyMessage(ALL_UNSELECT); //4
            } else {
                handler.sendEmptyMessage(ALL_SELECT);  //3
            }
        } else {
            int stopNum = 0;
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getDownStatus() == DownLoadStatusEnum.STOP.getValue()) {//4
                    stopNum++;
                }
            }
            if (stopNum == mDataList.size()) {
                handler.sendEmptyMessage(ALL_START);//2
            } else {
                handler.sendEmptyMessage(ALL_CANCEL);//1
            }
        }*/
    }

    @Override
    public void onDLProgress(final String s, final int progress,final long speed) {
        if(mDownCourseAdapter.getItemCount() == 0) {
            return;
        }
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                mDownCourseAdapter.refreshPartProgess(s,progress);
            }
        });
    }

    @Override
    public void onDLStop(final String key, final boolean keepWaiting) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {

                List<DownLoadLesson> mDataList= mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), key)) {

                        if(keepWaiting)
                            mDataList.get(i).setDownStatus(DownLoadStatusEnum.PREPARE.getValue());//4
                        else
                           mDataList.get(i).setDownStatus(DownLoadStatusEnum.STOP.getValue());//4
                        mDownCourseAdapter.notifyItemChanged(i);
                        break;
                    }
                }
               // setDataAndRefreshView();
               // handler.removeMessages(UI_REFRESH);
                //handler.sendEmptyMessageDelayed(UI_REFRESH, 50);
            }
        });
    }

    @Override
    public void onDLError(final String s, int errorCode) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList= mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), s)) {
                        mDataList.get(i).setDownStatus(DownLoadStatusEnum.ERROR.getValue());//3
                        mDownCourseAdapter.notifyItemChanged(i);
                        break;
                    }
                }
                //setDataAndRefreshView();
            }
        });
    }

    @Override
    public void onDLPrepare(final String downID) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList= mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), downID)) {
                        mDataList.get(i).setDownStatus(DownLoadStatusEnum.PREPARE.getValue());//-1
                        mDownCourseAdapter.notifyItemChanged(i);
                        break;
                    }
                }
               // setDataAndRefreshView();
            }
        });
    }

    @Override
    public void onDLFinished(final String downID) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList= mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), downID)) {
                        mDownCourseAdapter.removeAndRefresh(i);
                        //mDataList.remove(i);
                        break;
                    }
                }
               // setDataAndRefreshView();
            }
        });
    }

    @Override
    public void onDLFileStorage(final String key, final long space) {
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownLoadLesson> mDataList= mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getDownloadID(), key)) {
                        mDataList.get(i).setSpace(space);
                        mDownCourseAdapter.notifyItemChanged(i);
                        break;
                    }
                }
                //setDataAndRefreshView();
            }
        });
    }

    @Override
    public void onDestroy() {
        DownLoadAssist.getInstance().removeDownloadListener(this);
        UniApplicationLike.getApplicationHandler().removeCallbacks(mDelayPauseCount);
        super.onDestroy();
        if(confirmDialog != null&&confirmDialog.isShowing()) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
    }
}



