package com.huatu.handheld_huatu.business.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.adapter.common.Category;
import com.huatu.handheld_huatu.adapter.common.CategoryItemViewBinder;
import com.huatu.handheld_huatu.adapter.common.SubjectItem;
import com.huatu.handheld_huatu.adapter.common.SubjectItemViewBinder;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.helper.statistic.StudyCourseStatistic;
import com.huatu.handheld_huatu.mvpmodel.StudyFilterBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.ui.CommloadingView;
import com.huatu.handheld_huatu.ui.recyclerview.RecyclerViewEx;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.DensityUtils;
import com.me.drakeet.multitype.Items;
import com.me.drakeet.multitype.MultiTypeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**https://www.jianshu.com/p/3ecad4bfc55e  全屏Dialog
 * Created by cjx on 2018\7\19 0019.
 */

public class StudyFilterActionDialog extends Dialog implements View.OnClickListener,DialogInterface.OnShowListener,DialogInterface.OnDismissListener
        ,MultiTypeAdapter.OnItemClickListener {


    private Context mContext;
    private String mCourseId;
    private String mCourseImg;
    MultiTypeAdapter mAdapter;
    RecyclerViewEx mRecycleView;

    CommloadingView mCommloadingView;

    private TreeSet<String> selectedSet;
    private  Items mAllItems = new Items();
    private boolean mHasLoad=false;
    private List<SubjectItem> mOtherExamType=new ArrayList<>();
    private List<SubjectItem> mOtherTeacher =new ArrayList<>();

    //  private String mExamStatus,mPriceStatus,mStudyStatus,mTeacherId;
    public interface OnSubItemClickListener {
        void onShareBtnClick(String examStatus,String priceStatus,String studyStatus,String teacherId);
    }

    CompositeSubscription mCompositeSubscription;
    public void setCompositeSubscription(CompositeSubscription ComSubscription){
        mCompositeSubscription=ComSubscription;
    }

    private OnSubItemClickListener onSubItemClickListener;

    public void setOnSubItemClickListener(OnSubItemClickListener onSubItemClickListener) {
        this.onSubItemClickListener = onSubItemClickListener;
    }

    public StudyFilterActionDialog(Context context, String rid, String coverUrl) {
        super(context, R.style.ActionhorizontalDialogStyle2);
        this.mContext = context;
        this.mCourseId = rid;
        this.mCourseImg=coverUrl;
    }


    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(mContext).inflate(R.layout.play_catalogfilter_layout, null);

        int screenWidth=Math.min(DisplayUtil.getScreenWidth(),DisplayUtil.getScreenHeight());
        int  filterWidth= screenWidth- DensityUtils.dp2px(UniApplicationContext.getContext(),38);
        rootView.setMinimumWidth(filterWidth);
       // int screenHeight = getScreenHeight(getContext());
       // int dialogHeight = screenHeight ;
        setContentView(rootView);
       // getWindow().setLayout(filterWidth,dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);

        getWindow().setLayout(filterWidth,ViewGroup.LayoutParams.MATCH_PARENT);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        // dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;//0;
        lp.y =0;//DensityUtils.getStatusHeight(mContext);// 0;
        dialogWindow.setAttributes(lp);

        //dialogWindow.setWindowAnimations(R.style.popup_anim_bottom2);
        this.findViewById(R.id.reset_btn).setOnClickListener(this);
        this.findViewById(R.id.confirm_btn).setOnClickListener(this);

        this.setOnShowListener(this);
       // this.setOnDismissListener(this);

        mCommloadingView=rootView.findViewById(R.id.xi_layout_loading);

        mCommloadingView.setEmptyImg(R.drawable.no_data_bg);//course_no_cache_icon
        mCommloadingView.setStatusStringId(R.string.xs_loading_text,R.string.xs_error_message);
        mCommloadingView.setTipText(R.string.xs_retry_message);

        mCommloadingView.findViewById(R.id.xi_tv_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 v.setVisibility(View.GONE);
                loadData();
            }
        });
        mRecycleView=rootView.findViewById(R.id.xi_comm_page_list);
      //  int paddingDistance=DensityUtils.dp2px(mContext,5);
      // mRecycleView.setPadding(paddingDistance,paddingDistance*5,paddingDistance,0);

        final GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (mAllItems.get(position) instanceof Category) ? 3 : 1;
            }
        });
        selectedSet = new TreeSet<>();
        mRecycleView.setLayoutManager(layoutManager);
        mAdapter = new MultiTypeAdapter();
       // mRecycleView.setPagesize(100000);
        mAdapter.setOnViewItemClickListener(this);
        mRecycleView.checkloadMore(0);
        mAdapter.register(Category.class, new CategoryItemViewBinder());
        mAdapter.register(SubjectItem.class, new SubjectItemViewBinder(selectedSet));

        mRecycleView.setAdapter(mAdapter);
    }

    private void resetFilter(){
        selectedSet.clear();
        for(Object bean:mAllItems){
            if(bean instanceof SubjectItem){
                ((SubjectItem)bean).isSelected=false;
            }
        }
        for(SubjectItem bean:mOtherExamType){
            bean.isSelected=false;
        }

        for(SubjectItem bean:mOtherTeacher){
            bean.isSelected=false;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_btn:
                this.dismiss();
                resetFilter();
                if(null!=onSubItemClickListener)
                    onSubItemClickListener.onShareBtnClick("","","","");
                //showShareDialog(v);
                break;
            case R.id.confirm_btn:
                //FeedbackActivity.newInstance(mContext);

                this.dismiss();
                showConfrim();
                break;
        }
    }

    private final String cultWord(String record){
        if(TextUtils.isEmpty(record)) return record;
        if(record.endsWith(",")) return record.substring(0,record.length()-1);
        return record;
    }

    private void showConfrim(){

        String examStatus="",priceStatus="",studyStatus="",teacherId="";
     //   StringBuilder content = new StringBuilder();
        String teacherNames="";
        String examStatuNames="";
        String priceStatNames="";

        for (String number : selectedSet) {

            if(number.startsWith("1_"))
                studyStatus= studyStatus.concat(number.split("_")[1]+",");
            else if(number.startsWith("2_")){

                String[] teacherArr=number.split("_");
                teacherId= teacherId.concat(teacherArr[1]+",");
                teacherNames=teacherNames.concat(teacherArr[2]+",");
            }

            else if(number.startsWith("3_")){
                String[] examStatusArr=number.split("_");
                examStatus=  examStatus.concat(examStatusArr[1]+",");
                examStatuNames=examStatuNames.concat(examStatusArr[2]+",");
            }

            else if(number.startsWith("4_")){
                String[] priceArr=number.split("_");
                priceStatus= priceStatus.concat(priceArr[1]+",");
                priceStatNames=priceStatNames.concat(priceArr[2]+",");
            }
        }

        LogUtils.e("test",examStatus+"_"+""+priceStatus+"_"+studyStatus+"_"+teacherId);
        if(null!=onSubItemClickListener){
            onSubItemClickListener.onShareBtnClick(cultWord(examStatus),cultWord(priceStatus),cultWord(studyStatus),cultWord(teacherId));
        }

        StudyCourseStatistic.sendFilterSettingTrack(teacherNames,examStatuNames,priceStatNames);
    }
//https://www.pocketdigi.com/20180704/1617.html
    @Override
    public void show() {

        //暂时注掉，部分机型沉浸有问题
        //setTranslucentStatus();
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        //fullScreenImmersive(getWindow().getDecorView());
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    public void onDismiss(DialogInterface dialog){


    }

    public void show2(Activity activity) {
        if(activity!=null){

            activity.getWindow().addFlags(1024);
        }
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        //fullScreenImmersive(getWindow().getDecorView());
        //this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }


    private void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void setTranslucentStatus() {
        String deviceInfo=  Build.MANUFACTURER + android.os.Build.MODEL+"";
        if(deviceInfo.toLowerCase().contains("oppo")&&Build.VERSION.SDK_INT<24) {
            return;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
          /*  window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);*/
           // window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    @Override
    public void onShow(DialogInterface dialog){
        if(mHasLoad) return;
        mHasLoad=true;
        loadData();
    }

    @Override
    public void onItemClick(int position, View view, int type){
        if (type == 2 || type == 4) {
            if (mAllItems != null && type == 2) {
                this.mAllItems.addAll(position + 10, mOtherTeacher);
                mAdapter.notifyItemRangeInserted(position + 10, mOtherTeacher.size());
            } else if (mAllItems != null && type == 4) {
                this.mAllItems.removeAll(mOtherTeacher);
                mAdapter.notifyItemRangeRemoved(position + 10, mOtherTeacher.size());
            }
        } else {
            if (mAllItems != null && type == 3) {
                this.mAllItems.addAll(position + 10, mOtherExamType);
                mAdapter.notifyItemRangeInserted(position + 10, mOtherExamType.size());
            } else if (mAllItems != null && type == 6) {
                this.mAllItems.removeAll(mOtherExamType);
                mAdapter.notifyItemRangeRemoved(position + 10, mOtherExamType.size());
            }
        }

    }

    private void loadData(){
        if(null!=mCommloadingView) {
            mCommloadingView.showLoadingStatus();
        }
        ServiceExProvider.visit(mCompositeSubscription, CourseApiService.getApi().getfilteredCourses(), new NetObjResponse<StudyFilterBean>() {
            @Override
            public void onSuccess(BaseResponseModel<StudyFilterBean> model) {
                if(null!=mCommloadingView) {
                    mCommloadingView.removeFromParent();
                    mCommloadingView=null;
                }
               /* if(!ArrayUtils.isEmpty(model.data.classStatus)) {
                    Category spacialCategory = new Category("课程状态",false,1);
                    mAllItems.add(spacialCategory);
                    for(StudyFilterBean.ClassStatus bean:model.data.classStatus){
                        mAllItems.add(new SubjectItem(1,bean.classStatusName,bean.studyStaus));
                    }
                }*/
                if(!ArrayUtils.isEmpty(model.data.speakTeacher)) {
                    Category spacialCategory = new Category("主讲老师",model.data.speakTeacher.size()>9? true:false,2);
                    mAllItems.add(spacialCategory);
                    int i=0;
                    for(StudyFilterBean.SpeakTeacher bean:model.data.speakTeacher){
                        if(i<9){
                            mAllItems.add(new SubjectItem(2,bean.teacherName,bean.teacherId));
                        }
                        else{
                            mOtherTeacher.add(new SubjectItem(2,bean.teacherName,bean.teacherId));
                        }
                        i++;
                    }
                }
                if(!ArrayUtils.isEmpty(model.data.examType)) {
                    Category spacialCategory = new Category("考试类型",model.data.examType.size()>9? true:false,3);
                    mAllItems.add(spacialCategory);
                    int i=0;
                    for(StudyFilterBean.ExamType bean:model.data.examType){
                        if(i<9){
                            mAllItems.add(new SubjectItem(3,bean.catName,bean.categoryId));
                        }
                        else{
                            mOtherExamType.add(new SubjectItem(3,bean.catName,bean.categoryId));
                        }
                        i++;
                    }
                } if(!ArrayUtils.isEmpty(model.data.priceAttribute)) {
                    Category spacialCategory = new Category("价格属性",false,4);
                    mAllItems.add(spacialCategory);
                    for(StudyFilterBean.PriceAttribute bean:model.data.priceAttribute){
                        mAllItems.add(new SubjectItem(4,bean.priceName,bean.priceStatus));
                    }
                }
                mAdapter.setItems(mAllItems);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message, int type) {
                if(null!=mCommloadingView) {
                    mCommloadingView.showEmptyStatus();
                 }
            }
        });
    }

}