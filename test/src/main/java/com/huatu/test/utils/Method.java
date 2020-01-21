package com.huatu.test.utils;

import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saiyuan on 2016/11/5.
 */
public class Method {
 /*   private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long step = time - lastClickTime;
        if (0 < step && step < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }*/

    public static boolean isActivityFinished(Activity activity) {
        if(activity != null && !activity.isFinishing()) {
            if((!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())
                    || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)) {
                return false;
            }
        }
        return true;
    }

   /* public static void runOnUiThread(BaseActivity activity, Runnable runnable) {
        if(!isActivityFinished(activity)) {
            activity.runOnUiThread(runnable);
        }
    }

    public static void runOnUiThread(Activity activity, Runnable runnable) {
        if(!isActivityFinished(activity)) {
            activity.runOnUiThread(runnable);
        }
    }

    public static void runOnUiThread(AbsFragment fragment, Runnable runnable) {
        if(!fragment.isFragmentFinished()) {
            fragment.getActivity().runOnUiThread(runnable);
        }
    }*/

    public static float parseFloat(String num) {
        if(TextUtils.isEmpty(num)) {
            return 0f;
        }
        float number = 0f;
        try {
            number = Float.parseFloat(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public static long parseLong(String num) {
        if(TextUtils.isEmpty(num)) {
            return 0L;
        }
        long number = 0L;
        try {
            number = Long.parseLong(num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public static int parseInt(String num) {
        if(TextUtils.isEmpty(num)) {
            return 0;
        }
        int number = 0;
        try {
            String strNum = "";
            for(int i = 0; i < num.length(); i++) {
                char curChar = num.charAt(i);
                if(curChar >= '0' && curChar <= '9') {
                    strNum += curChar;
                } else if(curChar < '0') {
                    if(i == 0) {
                        if (curChar == '-' || curChar == '+') {
                            strNum += curChar;
                        } else if(TextUtils.isEmpty(strNum)) {
                            continue;
                        } else {
                            break;
                        }
                    } else if(TextUtils.isEmpty(strNum)) {
                        continue;
                    } else {
                        break;
                    }
                } else if(curChar > '9') {
                    if(TextUtils.isEmpty(strNum)) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
            number = Integer.parseInt(strNum);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    public static boolean isListEmpty(List<?> list) {
        if(list != null && list.size() > 0) {
            return false;
        }
        return true;
    }

    public static boolean isEqualString(String str1, String str2) {
        if(str1 != null && str1.equals(str2)) {
            return true;
        }
        return false;
    }

    /****************
     *
     * 发起添加群流程。群号：这居然还有一个群(198144676) 的 key 为： UQ4hwnMldN5FGlqwPAeUmayRbAtxojk9
     * 调用 joinQQGroup(UQ4hwnMldN5FGlqwPAeUmayRbAtxojk9) 即可发起手Q客户端申请加群 这居然还有一个群(198144676)
     *
     *  param key 由官网生成的key
     *  return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
   /* public static boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            UniApplicationContext.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public static void hideKeyboard(View view) {
        if(view != null && UniApplicationContext.getContext()!=null) {
            InputMethodManager manager = (InputMethodManager)UniApplicationContext.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if(manager != null) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }*/

    public static boolean isIdValid(String idCard) {
        int length = idCard.length();
        if(length != 18 && length != 15) {
            return false;
        }
        char[] array = idCard.toCharArray();
        boolean isNum = true;
        for(int i = 0; i < length - 2; i++) {
            if(array[i] < '0' || array[i] > '9') {
                isNum = false;
                break;
            }
        }
        if(!isNum) {
            return false;
        }
        int num = (array[6] - '0') * 10 + (array[7] - '0');
        if(num != 19 && num != 20) {
            return false;
        }
        num = (array[10] - '0') * 10 + (array[11] - '0');
        if(num > 12 || num <= 0) {
            return false;
        }
        num = (array[12] - '0') * 10 + (array[13] - '0');
        if(num > 31 || num <= 0) {
            return false;
        }
        boolean isX = (array[length - 1] == 'x' || array[length - 1] == 'X');
        if(array[length - 1] < '0' || (array[length - 1] > '9' && !isX)) {
            return false;
        }
        return true;
    }

    public static boolean isPhoneValid(String phone) {
        Pattern pattern = Pattern.compile("1[3456789]{1}\\d{9}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
