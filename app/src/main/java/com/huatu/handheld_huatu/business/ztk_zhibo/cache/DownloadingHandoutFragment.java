package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationLike;
import com.huatu.handheld_huatu.adapter.course.DownloadingHandoutAdapter;
import com.huatu.handheld_huatu.base.fragment.AStripTabsFragment;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.business.ztk_zhibo.listener.OnDLHandoutListener;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DialogUtils;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomConfirmDialog;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.nalan.swipeitem.recyclerview.SwipeItemLayout;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by cjx on 2018\7\7 0007.
 */

public class DownloadingHandoutFragment extends AbsSettingFragment implements OnDLHandoutListener,OnRecItemClickListener,
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



    @Override
    protected void parserParams(Bundle arg) {
        super.parserParams(arg);
        mForResult = arg.getBoolean(ArgConstant.FROM_ACTION);
    }

    @Override
    public int getContentView() {
        return R.layout.down_loading_manage_layout;
    }

    DownloadingHandoutAdapter mDownCourseAdapter;
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
            for (DownHandout curLession : mDownCourseAdapter.getAllLession()) {
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
                for(DownHandout lesson:mDownCourseAdapter.getAllLession()){
                     if(lesson.getDownStatus()==DownBtnLayout.PAUSE)
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
        mDownCourseAdapter=new DownloadingHandoutAdapter(getContext(),new ArrayList<DownHandout>());
        mDownCourseAdapter.setOnItemClickListener(this);
         mOnSwipeItemTouchListener=new SwipeItemLayout.OnSwipeItemTouchListener(getContext());
        mListView.addOnItemTouchListener(mOnSwipeItemTouchListener);
        DownHandoutAssist.getInstance().addDownloadListener(this);

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
            List<DownHandout> tmpList = SQLiteHelper.getInstance().getAllUnFinishDownHandouts(true);
            mDownCourseAdapter.refresh(tmpList);
            getCurdownStatus();
        }
    }

    @Override
    public void requestData() { }

    private void getCurdownStatus(){
        if(isFragmentFinished()) return;
        List<DownHandout> mDataList = mDownCourseAdapter.getAllLession();
        int stopNum = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getDownStatus() == DownBtnLayout.PAUSE) {//4
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


    @Override
    public void onItemClick(int position,View view,int type){

        if(mDownCourseAdapter.isEditMode()){
            DownHandout curLession=mDownCourseAdapter.getCurrentItem(position);
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

                break;
            case EventConstant.EVENT_DELETE:
                 DownHandout curlession2=  mDownCourseAdapter.getCurrentItem(position);
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
                    for (DownHandout curLession : mDownCourseAdapter.getAllLession()) {
                        curLession.setSelect(false);
                    }
                    mDownCourseAdapter.notifyDataSetChanged();
                }else {
                    mSelAllBtn.setText(R.string.pickerview_cancel);
                    mSelectNum=mDownCourseAdapter.getItemCount();
                    mDelTipTxt.setText(String.valueOf(mSelectNum));
                    mDelTipTxt.setVisibility(View.VISIBLE);
                    AnimUtils.scaleView(mDelTipTxt);
                    for (DownHandout curLession : mDownCourseAdapter.getAllLession()) {
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
                        List<DownHandout> downIDs = new ArrayList<>();

                        for (Iterator<DownHandout> i = mDownCourseAdapter.getAllLession().iterator(); i.hasNext(); ) {
                            DownHandout curLession = i.next();
                            if (curLession.isSelect()) {
                                downIDs.add(curLession);
                                i.remove();
                            }
                        }
                        mDownCourseAdapter.notifyDataSetChanged();
                        DownHandoutAssist.getInstance().DeleteTaskList(downIDs);
                        mSelectNum=0;
                        mSelAllBtn.setText("全选");
                        mDelTipTxt.setVisibility(View.GONE);
                        //mDownCourseAdapter.getAllLession().removeAll(downIDs);
                       // mDownCourseAdapter.notifyDataSetChanged();
                        if (mDownCourseAdapter.getItemCount() <= 0) {
                            if (getParentFragment() instanceof DownAllLoadingFragment) {
                                ((DownAllLoadingFragment) getParentFragment()).setHasDelete();
                            }
                        }

                     /*   DownLoadAssist.getInstance().delete(downIDs);
                        mHasDeleted=true;
                        List<DownLoadLesson> tmpList = SQLiteHelper.getInstance().getUnFinishedCourseWare();
                        mDownCourseAdapter.refresh(tmpList);
                        if (mDownCourseAdapter.getItemCount() <= 0) {

                            Intent data = new Intent();
                            setResult(Activity.RESULT_OK, data);
                            DownloadingHandoutFragment.this.finish();
                        }*/
                    }
                });
                mConfirmDialog.show();
                break;
        }
    }


    private void switchAllAction(){
        List<DownHandout> mDataList=  mDownCourseAdapter.getAllLession();
        if(ArrayUtils.isEmpty(mDataList)) return;
        if("全部暂停".equals(mSwitchActionBtn.getText())) {
            mSwitchActionBtn.setText("全部开始");
            mSwitchActionBtn.setSelected(false);
            DownHandoutAssist.getInstance().stopAll();
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getDownStatus() != DownBtnLayout.FINISH) {//-2
                    mDataList.get(i).setDownStatus(DownBtnLayout.PAUSE);//4

                    SQLiteHelper.getInstance().upDateHandoutStatus(mDataList.get(i).getSubjectID(), DownBtnLayout.PAUSE);

                    // DownLoadAssist.getInstance().stop(mDataList.get(i));
                }
            }
            DownHandoutAssist.getInstance().stopAll();
            mDownCourseAdapter.resetAndRefresh();
        } else if("全部开始".equals(mSwitchActionBtn.getText())) {
            mSwitchActionBtn.setText("全部暂停");
            mSwitchActionBtn.setSelected(true);
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).getDownStatus() != DownBtnLayout.FINISH  ) {//   3
                    mDataList.get(i).setDownStatus(DownBtnLayout.DOWNLOADING); //-1
                    SQLiteHelper.getInstance().upDateHandoutStatus(mDataList.get(i).getSubjectID(), DownBtnLayout.DOWNLOADING);
                }
            }
            DownHandoutAssist.getInstance().reStartAll();
            mDownCourseAdapter.resetAndRefresh();
        }
       /* if(mDataList.size() == 0) {
            finish();
            return;
        }*/
    }

    private void showDeleteDlg(final DownHandout item, final int position) {
        if(confirmDialog == null) {
            confirmDialog = DialogUtils.createDialog(getActivity(), "提示","");
        }
        confirmDialog.setMessage("即将删除所选讲义\n该操作不可恢复");
        confirmDialog.setPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownHandout curDelete = mDownCourseAdapter.getCurrentItem(position);
                LogUtils.e("DownLoadLesson", GsonUtil.GsonString(curDelete));

                DownHandoutAssist.getInstance().DeleteTask(curDelete);
                mDownCourseAdapter.removeAndRefresh(position);
                mHasDeleted = true;
                if (mDownCourseAdapter.getItemCount() <= 0)
                    if (getParentFragment() instanceof DownAllLoadingFragment) {
                        ((DownAllLoadingFragment) getParentFragment()).setHasDelete();
                    }
                }
            }
        );
        confirmDialog.show();
    }


    @Override
    public void onDLError(String s, int errorCode){

    }

    @Override
    public void onDLFinished(final String downID){
        Method.runOnUiThread(this, new Runnable() {
            @Override
            public void run() {
                List<DownHandout> mDataList= mDownCourseAdapter.getAllLession();
                for (int i = 0; i < mDataList.size(); i++) {
                    if (Method.isEqualString(mDataList.get(i).getSubjectID(), downID)) {
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
    public void onDestroy() {
        DownHandoutAssist.getInstance().removeDownloadListener(this);
        UniApplicationLike.getApplicationHandler().removeCallbacks(mDelayPauseCount);
        super.onDestroy();
        if(confirmDialog != null&&confirmDialog.isShowing()) {
            confirmDialog.dismiss();
            confirmDialog = null;
        }
    }
}



