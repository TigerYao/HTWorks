package com.baijiahulian.live.ui.answersheet;

import com.baijiahulian.live.ui.activity.LiveRoomRouterListener;
import com.baijiahulian.livecore.models.LPAnswerSheetOptionModel;
import com.baijiahulian.livecore.models.LPAnswerSheetModel;
import com.baijiahulian.livecore.utils.LPErrorPrintSubscriber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by yangjingming on 2018/6/5.
 */

public class QuestionToolPresenter implements QuestionToolContract.Presenter{
    @Override
    public void removeQuestionTool(boolean isEnded) {
        roomRouterListener.answerEnd(isEnded);
    }

    private LiveRoomRouterListener roomRouterListener;
    private List<LPAnswerSheetOptionModel> options = new ArrayList<>();
    private long countDownTime, currentTime;
    private QuestionToolContract.View view;
    private Subscription countDownSubscription;
    private List<String> checkedOptions = new ArrayList<>();

    @Override
    public void setRouter(LiveRoomRouterListener liveRoomRouterListener) {
        roomRouterListener = liveRoomRouterListener;
    }

    @Override
    public void subscribe() {
        if (countDownSubscription == null){
            countDownSubscription = rx.Observable.interval(0,1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LPErrorPrintSubscriber<Long>() {
                        @Override
                        public void call(Long aLong) {
                            currentTime = countDownTime - aLong;
                            if (currentTime < 0){
                                SimpleDateFormat formatter = new SimpleDateFormat("mm: ss");
                                String ms = formatter.format(0);
//                                roomRouterListener.answerEnd(true);
                                view.timeDown(ms);
                            }else{
                                SimpleDateFormat formatter = new SimpleDateFormat("mm: ss");
                                String ms = formatter.format(currentTime * 1000);
                                view.timeDown(ms);
                            }
                        }
                    });
        }

    }

    @Override
    public void unSubscribe() {
        if (countDownSubscription != null && !countDownSubscription.isUnsubscribed()) {
            countDownSubscription.unsubscribe();
            countDownSubscription = null;
        }
    }

    public void setLpQuestionToolModel(LPAnswerSheetModel lpAnswerSheetModel){
        options.clear();
        options.addAll(lpAnswerSheetModel.options);
        countDownTime = lpAnswerSheetModel.countDownTime;
    }

    @Override
    public void destroy() {
        roomRouterListener = null;
        view = null;
    }

    public void setView(QuestionToolContract.View view){
        this.view = view;
    }

    @Override
    public List<LPAnswerSheetOptionModel> getOptions() {
        return options;
    }

    @Override
    public void addCheckedOption(int index) {
        if (!checkedOptions.contains(String.valueOf(index)))
            checkedOptions.add(String.valueOf(index));
    }

    @Override
    public void deleteCheckedOption(int index) {
        if (checkedOptions.contains(String.valueOf(index)))
            checkedOptions.remove(String.valueOf(index));
    }

    @Override
    public boolean submitAnswers() {
        return roomRouterListener.getLiveRoom().submitAnswerSheet(checkedOptions);
    }


}
