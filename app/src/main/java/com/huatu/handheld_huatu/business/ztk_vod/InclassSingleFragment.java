package com.huatu.handheld_huatu.business.ztk_vod;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetObjResponse;
import com.huatu.handheld_huatu.base.adapter.SimpleBaseRecyclerAdapter;
import com.huatu.handheld_huatu.base.fragment.AbsFragment;
import com.huatu.handheld_huatu.business.arena.downloadpaper.utils.ToastUtil;
import com.huatu.handheld_huatu.business.ztk_vod.view.TipPanel;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.listener.OnRecItemClickListener;
import com.huatu.handheld_huatu.mvpmodel.arena.ArenaExamQuestionBean;
import com.huatu.handheld_huatu.mvpmodel.exercise.AnswerCardBean;
import com.huatu.handheld_huatu.mvpmodel.zhibo.PointExercisesBean;
import com.huatu.handheld_huatu.network.ServiceExProvider;
import com.huatu.handheld_huatu.network.api.CourseApiService;
import com.huatu.handheld_huatu.utils.ArgConstant;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.TimeUtils;
import com.huatu.handheld_huatu.view.custom.ExerciseTextView;
import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;
import com.huatu.widget.CountDownExTimer;
import com.qmuiteam.qmui.layout.QMUILinearLayout;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by cjx on 2018\7\24 0024.
 * 参考
 * ArenaExamAcitivty.java  setQuestionMain  ArenaExamRecycleQuestionMainFragment
 */

public class InclassSingleFragment extends AbsFragment implements OnRecItemClickListener {

    BitmapDrawable greyBitmapDrawable,errorBitmapDrawable,okBitmapDrawable,selBitmapDrawable;
    @BindView(R.id.listView)
    RecyclerView mRecyclerView;

    @BindView(R.id.bottom_action_layout)
    QMUILinearLayout mAction_layout;

    @BindView(R.id.title_name_txt)
    ExerciseTextView mTitleView;

    @BindView(R.id.title_pic_txt)
    ExerciseTextView mTitlePicView;

    @BindView(R.id.piclayout)
    ScrollView mPicviewLayout;

    @BindView(R.id.choose_type_txt)
    TextView mTechTypeView;

    @BindView(R.id.count_time_txt)
    TextView mCountTimeView;

    @BindView(R.id.tip_panel)
    TipPanel mTipView;

    PointExercisesBean mExericseBean;
    ChooseAdapter mChooseAdapter;
    String mParcaticeId;
    String mUserAnswer="";
    int mUserTime=1;

    private CompositeSubscription mCompositeSubscription = null;
    protected CompositeSubscription getSubscription(){
        mCompositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
        return mCompositeSubscription;
    }

    public void setCurrentExercise(PointExercisesBean exerciseInfo){
        this.mExericseBean=exerciseInfo;
        refreshUI();
    }

    public static InclassSingleFragment getInstance(String courseId,String practiceId ) {
        Bundle args = new Bundle();
        args.putString(ArgConstant.KEY_ID, courseId);
        args.putString(ArgConstant.FROM_ACTION, practiceId);
        InclassSingleFragment tmpFragment = new InclassSingleFragment();
        tmpFragment.setArguments(args);
       // tmpFragment.setCurrentExercise(exerciseInfo);
        return tmpFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            parserParams(getArguments());
        }
    }

    protected void parserParams(Bundle args) {
         String tmpParcaticeId= args.getString(ArgConstant.FROM_ACTION);
          if(!TextUtils.isEmpty(tmpParcaticeId)&&tmpParcaticeId.contains("_")){
              try{
                 String[] tmpStr=tmpParcaticeId.split("_");
                  mParcaticeId=tmpStr[0];
                  mUserAnswer=tmpStr[1];
                //  mUserAnswer="";

              }catch (Exception e){
                  e.printStackTrace();
              }
         }
    }

    @Override
    public int getContentView() {
        return R.layout.play_inclass_single_layout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //if (savedInstanceState == null)
        int mRadius = DensityUtils.dp2px(getContext(), 8);
        mAction_layout.setRadiusAndShadow(mRadius,DensityUtils.dp2px(getContext(), 8), 0.25f);
        this.findViewById(R.id.bottom_action_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Method.isActivityFinished(getActivity())){
                    ((BJRecordPlayActivity)getActivity()).showInclassFragment(false,0,"");
                }
            }
        });
    }

    private void buildShapeDrawable(boolean isMultChoose ){
        int commonShapeRadius = DensityUtils.dp2px(getContext(), 26);

        int corRadius=DensityUtils.dp2px(getContext(), 3);
        greyBitmapDrawable = QMUIDrawableHelper.createBordDrawableWithSizeV2(getResources(), commonShapeRadius, commonShapeRadius, isMultChoose? corRadius:commonShapeRadius/2, Color.parseColor("#ffffff"),Color.parseColor("#9B9B9B"));
        errorBitmapDrawable= QMUIDrawableHelper.createBordDrawableWithSizeV2(getResources(), commonShapeRadius, commonShapeRadius, isMultChoose? corRadius:commonShapeRadius/2, Color.parseColor("#FFF0F1"),Color.parseColor("#FFB4B7"));
        okBitmapDrawable= QMUIDrawableHelper.createBordDrawableWithSizeV2(getResources(), commonShapeRadius, commonShapeRadius, isMultChoose? corRadius:commonShapeRadius/2,Color.parseColor("#ffffff"), Color.parseColor("#1CE28D"));
        selBitmapDrawable=QMUIDrawableHelper.createBordDrawableWithSizeV2(getResources(),commonShapeRadius,commonShapeRadius,isMultChoose? corRadius:commonShapeRadius/2,Color.parseColor("#EC74A0"),Color.parseColor("#EC74A0"));

    }

    @Override
    public void requestData(){

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mChooseAdapter=new ChooseAdapter(getContext(),new ArrayList<String>());
        mChooseAdapter.setOnViewItemClickListener(this);
        mRecyclerView.setAdapter(mChooseAdapter);
   }

    MyCount mReformTimer;
    @Override
    public void onDestroy() {
        super.onDestroy();
        RxUtils.unsubscribeIfNotNull(mCompositeSubscription);
        if(mReformTimer!=null) mReformTimer.cancel();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mReformTimer!=null&&(mReformTimer.isPaused())){
            mReformTimer.resume();
            mReformTimer.setRunning();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mReformTimer!=null&&mReformTimer.isRunning()){
            mReformTimer.cancel();
            mReformTimer.setPause();
        }
    }

    //<p>关于下图所示机构的说法不正确的是：</p><p>图1.<img src=\"http://tiku.huatu.com/cdn/images/vhuatu/tiku/4/479cbd1b5093fc8dab211147d1467e6b.png\" width=\"118\" height=\"117\" style=\"width:118;height:117\"></p><p>图2.<img src=\"http://tiku.huatu.com/cdn/images/vhuatu/tiku/3/39efcc1dceab81eb849d8cd87125b0c9.png\" width=\"116\" height=\"99\" style=\"width:116;height:99\"></p>
    private final int MAXCOUNT=7200000;//2小时的计时
    private void refreshUI(){

        if(mExericseBean!=null){
             if(false&&StringUtils.hasImageTag(mExericseBean.stem)){

                try{
                    mTitleView.setType(1);
                    mTitleView.setText(StringUtils.delHTMLTag(mExericseBean.stem));
                    mTitlePicView.setType(1);

                    // 过虑文本
                    String regEx_style="<p[^>]*?>[\\s\\S]*?<\\/p>"; //定义style的正则表达式
                    Pattern pattern = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
                    List<String> filterArr=new ArrayList<>();
                    if (mExericseBean.stem != null) {
                        Matcher m = pattern.matcher(mExericseBean.stem);
                        while (m.find()) {
                            // System.out.println(m.group(1));
                            String matchStr=m.group(0);
                            if(!StringUtils.hasImageTag(matchStr))
                                filterArr.add(matchStr);
                        }
                    }
                    String resultStr=mExericseBean.stem;
                    for(String str:filterArr){
                        resultStr=resultStr.replace(str,"");
                    }
                    mTitlePicView.setHtmlSource(resultStr);
                    mPicviewLayout.setVisibility(View.VISIBLE);

                }catch (Exception e){

                }

               LinearLayout.LayoutParams tmpPar= (LinearLayout.LayoutParams)mRecyclerView.getLayoutParams();
               tmpPar.leftMargin=0;
               mRecyclerView.setLayoutParams(tmpPar);
           }else {
                mPicviewLayout.setVisibility(View.GONE);
               // mTitleView.setText(StringUtils.delHTMLTag(mExericseBean.stem));
                boolean hasImg= StringUtils.hasImageTag(mExericseBean.stem);
                if(hasImg){
                    mTitleView.setHtmlSource(mExericseBean.stem.replace("<p>","").replace("</p>",""));
                }
                else {
                    mTitleView.setText(StringUtils.delHTMLTag(mExericseBean.stem));
                }

           }

           if(mExericseBean.type==99)        mTechTypeView.setText("单选题");
           else if(mExericseBean.type==100)  mTechTypeView.setText("多选题");
           else if(mExericseBean.type==101)  mTechTypeView.setText("不定项");
           else if(mExericseBean.type==109)  mTechTypeView.setText("对错题");
           else if(mExericseBean.type==105)  mTechTypeView.setText("复合题");

           // 单选99|多选100|不定项101|对错题109|复合题105

           buildShapeDrawable(mExericseBean.type==100||mExericseBean.type==101);
           mChooseAdapter.setMultChoose(mExericseBean.type==100||mExericseBean.type==101);
           mChooseAdapter.refresh(mExericseBean.choices);
           if(!TextUtils.isEmpty(mUserAnswer)){
               mCountTimeView.setVisibility(View.INVISIBLE);
               if(mExericseBean.type==99)//单选
               {
                   int tmpUserAnswer = StringUtils.parseInt(mUserAnswer);
                   if (tmpUserAnswer == mExericseBean.answer) {//success
                       mChooseAdapter.setChooseStatus(tmpUserAnswer - 1, -1);
                   } else {
                       mChooseAdapter.setChooseStatus(mExericseBean.answer - 1, tmpUserAnswer - 1);
                   }
               }else {

                   char[] tmpUserAnswer=mUserAnswer.toCharArray();
                   String correctAnswer=String.valueOf(mExericseBean.answer);
                   for(int i=0;i<tmpUserAnswer.length;i++){

                       String StrUserAnswer=String.valueOf(tmpUserAnswer[i]);
                       if(correctAnswer.contains(StrUserAnswer)){
                           mChooseAdapter.setChooseStatus(StringUtils.parseInt(StrUserAnswer)-1 , -1);
                       }else {
                           mChooseAdapter.setChooseStatus(-1 , StringUtils.parseInt(StrUserAnswer)-1);
                       }
                       //还差漏选
                   }

                 /*  int okAnswer=mExericseBean.answer;
                   int index=-1;
                   while (okAnswer > 0) {
                       index = okAnswer % 10;
                       if (index - 1 < realExamBean.paper.questionBeanList.get(i).questionOptions.size()) {
                           realExamBean.paper.questionBeanList.get(i).questionOptions.get(index - 1).isSelected = true;
                       }

                       if(mUserAnswer.contains(index))
                       okAnswer /= 10;
                   }*/
                }

              return;
           }
           if(mReformTimer==null) {
               mReformTimer = new MyCount(MAXCOUNT, 1000);
           }
           if(mReformTimer.isRunning()) return;
            mReformTimer.doStart();
       }
   }

    class MyCount extends CountDownExTimer {

        private int mRunningStatus=0;//0 unstart,1 running,2,pause,3 finish

        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void doStart(){
            mRunningStatus=1;
            super.start();
        }

        public void setRunning(){  mRunningStatus=1;  }
        public boolean isRunning(){
            return mRunningStatus==1;
        }
        public void setPause(){  mRunningStatus=2;  }
        public boolean isPaused(){  return mRunningStatus==2; }

        @Override
        public void onFinish() {
            super.onFinish();
            mRunningStatus=3;
        }
        @Override
        public void onTick(long millisUntilFinished) {
            super.onTick(millisUntilFinished);

             if(mCountTimeView!=null){
                 mCountTimeView.setText(TimeUtils.getTime(MAXCOUNT-(int)millisUntilFinished));
                 mUserTime=(MAXCOUNT-(int)millisUntilFinished)/1000;
             }

            // Toast.makeText(NewActivity.this, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延�?
        }
    }

    @Override
    public void onItemClick(int position,View view,int type){
        mChooseAdapter.setSelectStatus(position);
        ((InclassPracticeFragment)getParentFragment()).showSubmitAction();
    }

    public void showAnswer(){
        int position=mChooseAdapter.getSelectAnswerIndex();
        if(mExericseBean.type==100){
            if(position==0){
                ToastUtil.showToast(" 请选择答案");
                return;
            }
            char[] tmpUserAnswer=String.valueOf(position).toCharArray();
            String correctAnswer=String.valueOf(mExericseBean.answer);

            for(int i=0;i<tmpUserAnswer.length;i++){

                String StrUserAnswer=String.valueOf(tmpUserAnswer[i]);
                if(correctAnswer.contains(StrUserAnswer)){
                    mChooseAdapter.setChooseStatus(StringUtils.parseInt(StrUserAnswer)-1 , -1);
                }else {
                    mChooseAdapter.setChooseStatus(-1 , StringUtils.parseInt(StrUserAnswer)-1);
                }
                //还差漏选
            }
            if(position==mExericseBean.answer){

                mTipView.showSuccess();
                saveParcatice(true,position);
            }else {
                mTipView.showError();
                saveParcatice(false,position);
            }
         }else {

            if((position+1)==mExericseBean.answer){//success
                mChooseAdapter.setChooseStatus(position,-1);
                mTipView.showSuccess();
                saveParcatice(true,(position+1));
            }
            else {
                mChooseAdapter.setChooseStatus(mExericseBean.answer-1,position);
                mTipView.showError();
                saveParcatice(false,(position+1));
            }

        }

    }

   //[{"answer":2,"correct":2,"doubt":0,"questionId":56215,"time":1},
   private void saveParcatice(boolean isCorrect,int userAnswer){
       if(getParentFragment() instanceof InclassPracticeFragment){
            ((InclassPracticeFragment)getParentFragment()).hideJump();
       }

       final   AnswerCardBean answerCardBean = new AnswerCardBean();
       answerCardBean.questionId = mExericseBean.id;
       answerCardBean.answer = userAnswer;//  //对应的题的作答答案. 0表示未做答,数字和字母对应关系: 1=>A,2=>B,3=>C,4=>D,5=>E,6=>F,7=>G,8=>H,答案AB转换后为12
       answerCardBean.time =mUserTime;// bean.usedTime;
       answerCardBean.correct =isCorrect?1:2;  //是否正确 00表示未做答 1:正确 2:错误
       answerCardBean.doubt =0;;// bean.doubt; //1:用户对该试题有疑问,0:没有疑问
       if (answerCardBean.answer != 0 && answerCardBean.correct != 0 && answerCardBean.time <= 0) {
           answerCardBean.time = 1;
       }

       List<AnswerCardBean> list = new ArrayList<>();
       list.add(answerCardBean);
       JsonArray jsonArray = GsonUtil.getGson().toJsonTree(list,new TypeToken<List<AnswerCardBean>>(){}.getType()).getAsJsonArray();


       // CourseApiService.getApi().submitAnswerCard(mParcaticeId, jsonArray)
       ServiceExProvider.visitSimple(getSubscription(), CourseApiService.getApi().saveAnswerCard(StringUtils.parseLong(mParcaticeId), jsonArray)
               , new NetObjResponse<String>() {
                   @Override
                   public void onSuccess(BaseResponseModel<String> model) {
                       if(!Method.isActivityFinished(getActivity())){
                           ((BJRecordPlayActivity)getActivity()).setPointStatus(answerCardBean);
                       }
                   }

                   @Override
                   public void onError(String message, int type) {

                   }
               });
   }


    public static AnswerCardBean createAnswerCardBean(ArenaExamQuestionBean bean) {
        if(bean.isSubmitted || (bean.userAnswer == 0 && bean.usedTime == 0 && bean.doubt == 0) ) {
            return null;
        }
        AnswerCardBean answerCardBean = new AnswerCardBean();
        answerCardBean.questionId = bean.id;
        answerCardBean.answer = bean.userAnswer;
        answerCardBean.time = bean.usedTime;
        answerCardBean.correct = bean.isCorrect;
        answerCardBean.doubt = bean.doubt;
        if (answerCardBean.answer != 0 && answerCardBean.correct != 0 && answerCardBean.time <= 0) {
            answerCardBean.time = 1;
        }
        return answerCardBean;
    }

    private class ChooseAdapter extends SimpleBaseRecyclerAdapter<String> {

        private String[] chooseChar=new String[]{"A","B","C","D","E","F","G","H"};

        private boolean mDisable=true;

        private int mStatus=0;//0，选择状态，1答案状态
        private boolean mIsMultChoose=false;//是否多选

        private SparseArray<Integer> mChooseAnswer = new SparseArray<Integer>();//0为选择，1为选择正确，2为选择错误

        public void setMultChoose(boolean isMultChoose){
            mIsMultChoose=isMultChoose;
        }

        public ChooseAdapter(Context context, List<String> items) {
            super(context, items);
         }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View collectionView = LayoutInflater.from(mContext).inflate(R.layout.play_inclass_list_item, parent, false);
            return new ChooseViewHolder(collectionView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            ChooseViewHolder holderfour = (ChooseViewHolder) holder;
            holderfour.bindUI(mItems.get(position), position);
        }

        public int  getSelectAnswerIndex(){
            if(!mIsMultChoose){
               int key = 0;
                for(int i = 0; i < mChooseAnswer.size(); i++) {
                    key = mChooseAnswer.keyAt(i);
                    return key;
                }
            }else {
                int answerIndex=0;
                for(int i = 0; i < mExericseBean.choices.size(); i++) {
                     if(null!=mChooseAnswer.get(i)){
                        answerIndex=answerIndex*10+ (i+1);
                    }
                 }
                return answerIndex;
            }
            return 0;
        }
        public void setSelectStatus(int selPostion){
            if (mIsMultChoose) {//如果包含，移除，否则添加
                if(null!=mChooseAnswer.get(selPostion)){
                    mChooseAnswer.remove(selPostion);
                }else {
                    mChooseAnswer.put(selPostion, 0);
                }
            } else {
                mChooseAnswer.clear();
                mChooseAnswer.put(selPostion, 0);
            }
            mStatus=0;
            this.notifyDataSetChanged();
        }


        public void setChooseStatus(int okPostion,int errorPostion){
            mStatus=1;
            if(okPostion>-1){
                mChooseAnswer.put(okPostion,1);
            }
            if(errorPostion>-1)  {
                mChooseAnswer.put(errorPostion,2);
            }

             mDisable=false;
            this.notifyDataSetChanged();
        }

        protected class ChooseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mNumTxt;
            ExerciseTextView mTitleView;
            ChooseViewHolder(View itemView) {
                super(itemView);
                mTitleView = (ExerciseTextView) itemView.findViewById(R.id.text_content);
                mTitleView.setType(1);
                mNumTxt = (TextView) itemView.findViewById(R.id.num_txt);
                itemView.findViewById(R.id.whole_content).setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.whole_content:
                      if (onRecyclerViewItemClickListener != null&&mDisable)
                            onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), v, EventConstant.EVENT_ALL);

                        break;
                }
            }

            public void bindUI(String title, int pos) {

                if(StringUtils.hasImageTag(title)){
                    String data = title;
                    data = data.replace("\t", "").replace("<p>", "").replace("</p>", "");
                    if (data.contains("http") || data.contains("png") || data.contains("jpg")
                            ||data.contains("width") && data.contains("height")) {
                        data = data + "&nbsp";
                    }
                    mTitleView.setHtmlSource(DisplayUtil.dp2px(360 - 30),data);
                }else {
                    mTitleView.setText(StringUtils.delHTMLTag(title));
                }
                mNumTxt.setText(chooseChar[pos]);
                Integer curValue= mChooseAnswer.get(pos);
                if(mStatus==0){//答题状态
                    if(null!=curValue){
                        mNumTxt.setBackground(selBitmapDrawable);
                        mNumTxt.setTextColor(Color.WHITE);
                    }else {
                        mNumTxt.setTextColor(Color.parseColor("#9b9b9b"));
                        mNumTxt.setBackground(greyBitmapDrawable);
                    }
                    return;
                }

                //查看答题状态
                ////0为选择，1为选择正确，2为选择错误
                if(null!=curValue){

                    if(curValue.intValue()==2) {
                        mNumTxt.setBackground(errorBitmapDrawable);
                        mNumTxt.setTextColor(Color.parseColor("#FF3F47"));
                    }
                    else if(curValue.intValue()==1){
                        mNumTxt.setTextColor(Color.parseColor("#1CE28D"));
                        mNumTxt.setBackground(okBitmapDrawable);
                    }
                }
                else  {
                    mNumTxt.setTextColor(Color.parseColor("#9b9b9b"));
                    mNumTxt.setBackground(greyBitmapDrawable);
                }
               // mTypeImg.setImageResource(resArr[pos]);

            }
        }


    }
}
