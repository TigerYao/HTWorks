package com.huatu.handheld_huatu.view;


import android.content.Context;
import android.view.Gravity;

import com.huatu.Indicator.AVLoadingIndicatorView;
import com.huatu.handheld_huatu.R;


public class ProgressDialogEx {

	public static Tip Show(Context context, String title, String loadtxt,boolean indeterminate, boolean cancelable)
	{

		Tip _actionTip = new Tip(context, R.style.customDialog);

		_actionTip.SetContentView(R.layout.loadingwait);
		_actionTip.SetGravity(Gravity.CENTER);
		_actionTip.Show(loadtxt);
		return _actionTip;

	}


	public static Tip ShowSmall(Context context, String title, String loadtxt,boolean indeterminate, boolean cancelable)
	{

		Tip _actionTip = new Tip(context, R.style.customDialog);

		_actionTip.SetContentView(R.layout.loading_smallwait);
		_actionTip.SetGravity(Gravity.CENTER);
		_actionTip.Show(loadtxt);
		return _actionTip;

	}


	public static Tip ShowonLiveSmall(Context context, boolean cancelable)
	{

		Tip _actionTip = new Tip(context, R.style.customDialog);
 		_actionTip.SetContentView(R.layout.loading_onlive_wait);
		_actionTip.SetGravity(Gravity.CENTER);
		((AVLoadingIndicatorView)_actionTip.getContentView().findViewById(R.id.xi_loading_img)).start();
		_actionTip.SimpleShow();
		return _actionTip;

	}
}
