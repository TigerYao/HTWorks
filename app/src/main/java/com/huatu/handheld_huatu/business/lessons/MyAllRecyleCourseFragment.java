package com.huatu.handheld_huatu.business.lessons;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.huatu.handheld_huatu.base.FragmentParameter;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.utils.StringUtils;

/**
 * Created by cjx on 2018\7\17 0017.
 */

public class MyAllRecyleCourseFragment extends MyAllCourseFragment {

    @Override
    public boolean isRecylerView(){
        return true;
    }

    public static void lanuch(Context mContext, int pos) {
        Bundle args = new Bundle();
        args.putInt(SET_INDEX, pos);
        //XLog.e("lanuch", "lanuch" + pos);
        FragmentParameter tmpPar = new FragmentParameter(MyAllRecyleCourseFragment.class, args);
        UIJumpHelper.jumpFragment(mContext, tmpPar);
    }

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        if(savedState!=null){
            setCurrentPosition(savedState.getInt(SET_INDEX));
        }
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        //  EventBus.getDefault().register(this);
        setTitle("回收站");
    }

    @Override
    protected Fragment newFragment(StripTabItem bean) {
        if (bean != null) {
//            if (StringUtils.parseInt(bean.getType()) == PayStatusEnums.DEFAULT.getAction())
//                return OrderToBePaidFragment.getInstance(StringUtils.parseInt(bean.getType()));
            return MyRecylerCourseFragment.getRecylerInstance(StringUtils.parseInt(bean.getType()));
//            return UserAllOrderSubFragment.getInstance(StringUtils.parseInt(bean.getType()));
        }
        return null;


    }
}
