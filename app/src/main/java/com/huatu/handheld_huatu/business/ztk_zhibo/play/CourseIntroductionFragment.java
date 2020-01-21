package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseFragment;
import com.huatu.handheld_huatu.base.BaseWebViewFragment;
import com.huatu.handheld_huatu.network.RetrofitManager;
import com.huatu.handheld_huatu.utils.SpUtils;

/**
 * Created by saiyuan on 2017/8/28.
 */

public class CourseIntroductionFragment extends BaseFragment {
    private TextView tvPhoneNumber;
    BaseWebViewFragment webViewFragment;

    private int courseId;

    @Override
    public int onSetRootViewId() {
        return R.layout.fragment_course_introduction_layout;
    }

    @Override
    protected void onInitView() {
        super.onInitView();
        courseId = args.getInt("course_id");
        tvPhoneNumber = (TextView) rootView.findViewById(R.id.course_introduction_phone_num_tv);
        tvPhoneNumber.setText(SpUtils.getCoursePhone());
        initFragment();
    }

    private void initFragment() {
        String webUrl = RetrofitManager.getInstance().getBaseUrl() + "c/v3/courses/" + courseId;
        if(webViewFragment == null) {
            webViewFragment = BaseWebViewFragment.newInstance(webUrl, "","", false, false);
        }
//        if(!webViewFragment.isAdded()) {
//            FragmentManager fragmentManager = getChildFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.add(R.id.course_intro_fragment_container, webViewFragment, "webview");
//            fragmentTransaction.commitAllowingStateLoss();
//        }
    }
}
