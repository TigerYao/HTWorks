package com.huatu.test.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;



/**
 * Toast统一管理类
 */
public class ToastUtils {

    private ToastUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;


    /**
     * 短时间显示Toast
     *
     * @param context 上下文
     * @param message String
     */
    public static void showShort(final Context context, final CharSequence message) {
      /*  if (isShow && !TextUtils.isEmpty(message)) {
            UniApplicationLike.getApplicationHandler().post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    makeText(context, message + "").show();
                }
            });
        }*/
    }

    public static void showShort(final CharSequence message) {
       /* if (isShow && !TextUtils.isEmpty(message)) {
            UniApplicationLike.getApplicationHandler().post(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(UniApplicationContext.getContext(), message, Toast.LENGTH_SHORT).show();
                    makeText(UniApplicationContext.getContext(), message).show();
                }
            });
        }*/
    }

  /*  public static void showRewardToast(String key) {
        RewardInfoBean bean = UniApplicationContext.getRewardInfo(key);
        if (bean == null) {
            return;
        }
        final String text = "任务完成，获得" + bean.gold + "图币和" + bean.experience + "成长值";
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = new Toast(UniApplicationContext.getContext());
                    View toastView = LayoutInflater.from(UniApplicationContext.getContext())
                            .inflate(R.layout.reward_toast_layout, null, false);
                    mToast.setView(toastView);
                    mToast.setGravity(Gravity.TOP, 0, 0);
                    mToast.setDuration(Toast.LENGTH_LONG);
                }
                if (mToast.getView() != null) {
                    TextView msgView = (TextView) mToast.getView().findViewById(R.id.reward_toast_message_tv);
                    if (msgView != null) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) msgView.getLayoutParams();
                        if (lp.width != DisplayUtil.getScreenWidth()) {
                            lp.width = DisplayUtil.getScreenWidth();
                            msgView.setLayoutParams(lp);
                        }
                        msgView.setText(text);
                    }
                }
                if (Build.VERSION.SDK_INT >= 22) {
                    try {
                        Class clazz = mToast.getClass();
                        java.lang.reflect.Method m1 = clazz.getDeclaredMethod("getWindowParams");
                        WindowManager.LayoutParams tmpPar = (WindowManager.LayoutParams) m1.invoke(mToast);
                        if (null != tmpPar) tmpPar.windowAnimations = R.style.toast_myanim;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mToast.show();
            }
        });
    }

    public static void showMyRewardToast(final String message1, final String message2) {
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = null;
                if (toast == null) {
                    toast = new Toast(UniApplicationContext.getContext());
                    View toastView = LayoutInflater.from(UniApplicationContext.getContext())
                            .inflate(R.layout.reward_toast_my_layout, null, false);
                    toast.setView(toastView);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                }
                if (toast.getView() != null) {
                    TextView msgView1 = (TextView) toast.getView().findViewById(R.id.reward_toast_message1_tv);
                    TextView msgView2 = (TextView) toast.getView().findViewById(R.id.reward_toast_message2_tv);
                    msgView1.setText(message1);
                    msgView2.setText(message2);
                }
                if (Build.VERSION.SDK_INT >= 22) {
                    try {

                        Class clazz = toast.getClass();
                        java.lang.reflect.Method m1 = clazz.getDeclaredMethod("getWindowParams");
                        WindowManager.LayoutParams tmpPar = (WindowManager.LayoutParams) m1.invoke(toast);
                        if (null != tmpPar) tmpPar.windowAnimations = R.style.toast_myanim;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                toast.show();
            }
        });

    }

    public static void showEssayToast(final String message) {
        UniApplicationLike.getApplicationHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = null;
                if (toast == null) {
                    toast = new Toast(UniApplicationContext.getContext());
                    View toastView = LayoutInflater.from(UniApplicationContext.getContext())
                            .inflate(R.layout.essay_toast_layout, null, false);
                    toast.setView(toastView);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                }
                if (toast.getView() != null) {
                    TextView msgView1 = (TextView) toast.getView().findViewById(R.id.essay_toast_message_tv);
                    msgView1.setText(message);
                }
                if (Build.VERSION.SDK_INT >= 22) {
                    try {

                        Class clazz = toast.getClass();
                        java.lang.reflect.Method m1 = clazz.getDeclaredMethod("getWindowParams");
                        WindowManager.LayoutParams tmpPar = (WindowManager.LayoutParams) m1.invoke(toast);
                        if (null != tmpPar) tmpPar.windowAnimations = R.style.toast_myanim;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                toast.show();
            }
        });

    }

    public static void showMessage(String message) {
        //if (isShow)
        makeText(UniApplicationContext.getContext(), message + "").show();
    }

    public static void showMessage(String message, int gravity) {
        //if (isShow)
        makeText(UniApplicationContext.getContext(), message, gravity, R.layout.toast_network_weak_layout).show();
    }*/

    /**
     * 短时间显示Toast
     *
     * @param context 上下文
     * @param message int
     */
    public static void showShort(Context context, int message) {
        //if (isShow)
      //  makeText(context, message + "").show();
    }

    /* *//**
     * 自定义显示Toast时间
     *
     * @param context  上下文
     * @param message  String
     * @param duration int时间
     *//*
    public static void show(Context context, CharSequence message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    */
    /**
     * 自定义显示Toast时间
     *
     * @param context  上下文
     * @param message  int
     * @param duration int时间
     *//*
    public static void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }*/

    //new begin


    private static Toast mToast;
    private static Handler myHandler = new Handler(Looper.getMainLooper());
    private static Runnable r = new Runnable() {
        public void run() {
            if (mToast == null) return;
            mToast.cancel();
            mToast = null;//toast隐藏后，将其置为null
        }
    };

  /*  public static void showShortToast(@StringRes int textRes) {
        String resultStr = ResourceUtils.getString(textRes);
        showShortToast(UniApplicationContext.getContext(), resultStr);
    }

    public static void showShortToast(Context context, @StringRes int textRes) {
        if (context == null) {
            context = UniApplicationContext.getContext();
        }
        showShortToast(context, context.getResources().getString(textRes));
    }

    public static void showShortToast(Context context, CharSequence text) {

        myHandler.removeCallbacks(r);
        if (mToast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            mToast = makeText(context, text);
        } else
            setText(mToast, text);

        myHandler.postDelayed(r, 2000);//延迟1秒隐藏toast
        mToast.show();
    }

    public static Toast makeText(Context context, @StringRes int textRes) {
        if (context == null) {
            context = UniApplicationContext.getContext();
        }
        return makeText(context, context.getResources().getString(textRes));
    }

    public static Toast makeSuccessText(Context context, CharSequence text) {
        if (context == null) {
            context = UniApplicationContext.getContext();
        }
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_success_layout, null);
        TextView tv = (TextView) layout.findViewById(R.id.xi_toast_default_text);
        if (tv != null && !TextUtils.isEmpty(text))
            tv.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER, 0, -(int) context.getResources().getDimension(R.dimen.xd_toast_y_offset));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        try {
            Class clazz = toast.getClass();
            java.lang.reflect.Method m1 = clazz.getDeclaredMethod("getWindowParams");
            WindowManager.LayoutParams tmpPar = (WindowManager.LayoutParams) m1.invoke(toast);
            if (null != tmpPar) tmpPar.windowAnimations = R.style.popWinAnimation;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return toast;
    }


    public static Toast makeText(Context context, CharSequence text) {
        if (context == null) {
            context = UniApplicationContext.getContext();
        }
        View layout = LayoutInflater.from(context).inflate(R.layout.toast_default_layout, null);
        TextView tv = (TextView) layout.findViewById(R.id.xi_toast_default_text);
        if (tv != null && !TextUtils.isEmpty(text))
            tv.setText(text);


        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, (int) context.getResources().getDimension(R.dimen.xd_toast_y_offset));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        if (Build.VERSION.SDK_INT >= 22) {
            try {

                Class clazz = toast.getClass();
                java.lang.reflect.Method m1 = clazz.getDeclaredMethod("getWindowParams");
                WindowManager.LayoutParams tmpPar = (WindowManager.LayoutParams) m1.invoke(toast);
                if (null != tmpPar) tmpPar.windowAnimations = R.style.toast_myanim;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return toast;
    }

    public static Toast makeText(Context context, CharSequence text, int gravity, int layoutid){
        if (context == null) {
            context = UniApplicationContext.getContext();
        }
        View layout = LayoutInflater.from(context).inflate(layoutid > 0 ? layoutid : R.layout.toast_default_layout, null);
        TextView tv = (TextView) layout.findViewById(R.id.xi_toast_default_text);
        if (tv != null && !TextUtils.isEmpty(text))
            tv.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(gravity, 0, (int) context.getResources().getDimension(R.dimen.common_10dp));
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        if (Build.VERSION.SDK_INT >=22){
            try{
                Class clazz = toast.getClass();
                java.lang.reflect.Method m1 = clazz.getDeclaredMethod("getWindowParams");
                WindowManager.LayoutParams tmpPar=(WindowManager.LayoutParams) m1.invoke(toast);
                if(null!=tmpPar) tmpPar.windowAnimations = R.style.toast_myanim;
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return toast;
    }

    public static Toast makeTextTip(Context context,boolean isRight, int gravity, View layout,  int yOffset){
        if (context == null) {
            context = UniApplicationContext.getContext();
        }
        ImageView iv = (ImageView) layout.findViewById(R.id.answer_tip);
        if (gravity == Gravity.FILL){
            layout.setBackgroundResource(R.color.live_transparent_opacity_06);
            layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }else
            layout.setBackgroundResource(R.color.transparent);
        if (iv != null)
            iv.setImageResource(isRight ? R.mipmap.live_video_test_right : R.mipmap.live_video_test_wrong);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(gravity, 0, yOffset);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        if (Build.VERSION.SDK_INT >=22){
            try{
                Class clazz = toast.getClass();
                java.lang.reflect.Method m1 = clazz.getDeclaredMethod("getWindowParams");
                WindowManager.LayoutParams tmpPar=(WindowManager.LayoutParams) m1.invoke(toast);
                if(null!=tmpPar) tmpPar.windowAnimations = R.style.toast_myanim;
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return toast;
    }
*/

   /* public static Toast setText(Toast curToast, CharSequence text) {
        if (curToast == null || TextUtils.isEmpty(text)) return null;
        TextView tv = (TextView) curToast.getView().findViewById(R.id.xi_toast_default_text);
        if (tv != null)
            tv.setText(text);
        curToast.setDuration(Toast.LENGTH_SHORT);
        return curToast;
    }

    public static void cancle() {
        if (mToast != null)
            mToast.cancel();
    }*/

}