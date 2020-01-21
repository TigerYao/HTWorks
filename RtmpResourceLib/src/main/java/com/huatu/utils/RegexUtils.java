package com.huatu.utils;


import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * <p/>
 * author : Soulwolf Create by 2015/7/13 14:19
 * email  : ToakerQin@gmail.com.
 */
public final class RegexUtils {



    static final Pattern PHONE_PATTERN = Pattern.compile("^[1]\\d{10}$");

    static final Pattern EMAIL_PATTERN = Pattern.compile("^(\\w-*\\.*)+@(\\w-?)+(\\.\\w{2,})+$");

   // static final Pattern PASSWORD_PATTERN = Pattern.compile("^\\w{6,20}$");

    /**
     * 正则表达式:验证密码(不包含特殊字符) ^[a-zA-Z0-9]{6,18}$
     */
    static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]{6,18})$");//

    static final Pattern NICKNAME_PATTERN = Pattern.compile("^\\w{0,10}$");

    static final Pattern CHINA_PATTERN = Pattern.compile("[^[\\u4e00-\\u9fa5]*]*");

    //检查phone是否是合格的手机号(标准:1开头，第二位为3,5,8，后9位为任意数字)
    //System.out.println(phone + ":" + phone.matches("1[358][0-9]{9,9}")); //true
    /**
     * 检查手机号是否合法
     */
    public static boolean matcherPhone(String phone) {
        return !TextUtils.isEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 检测邮箱地址是否合法
     */
    public static boolean matcherEmail(String email) {
        return !TextUtils.isEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }


    /**
     * 检测密码是否合法
     */
    public static boolean matcherPsw(String psw) {
        return !TextUtils.isEmpty(psw) && PASSWORD_PATTERN.matcher(psw).matches();
    }

    /**
     * 检测 用户昵称是否合法
     */
    public static boolean matcherNickname(String nickname) {
        return !TextUtils.isEmpty(nickname) && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    /**
     * 检查是否为数字
     */
    public static boolean matcherNumber(String number, int length) {
        String regex = "^\\d{" + length + "}$";
        return !TextUtils.isEmpty(number) && number.matches(regex);
    }

/*    var patrn = /^\d+(\.\d+)?$/;
    /^[0-9]{0}([0-9]|[.])+$/;*/

    public static boolean matcherFloat(String number) {
        String regex = "^[0-9]{0}([0-9]|[.])+$";
        return !TextUtils.isEmpty(number) && number.matches(regex);
    }


    /**
     * 检查是否为数字
     */
    public static boolean matcherNumber(String number) {
        String regex = "^\\d+$";
        return !TextUtils.isEmpty(number) && number.matches(regex);
    }

    /**
     * 检查是否为数字
     */
    public static boolean matcherNumber(String number, int minLength, int maxLength) {
        String regex = "^\\d{" + minLength + "," + maxLength + "}$";
        return !TextUtils.isEmpty(number) && number.matches(regex);
    }

    /**
     * 检测是否为非中文
     *
     * @param text
     * @return
     */
    public static boolean matcherNoChina(String text) {
        return !TextUtils.isEmpty(text) && CHINA_PATTERN.matcher(text).matches();
    }

    /**
     * 检查Url是否合法(Http https不包括ftp)
     */
    public static boolean matcherUrl(String url) {
        if (url.contains("http") || url.contains("https")) {
            return true;
        }
        return false;
    }

    /**
     * 验证汉字
     *
     * @param realName
     * @return
     */
    public static boolean checkRealName(String realName) {
        boolean flag = false;
        try {
            Pattern regex = Pattern.compile("^[\u4e00-\u9fa5]{2,10}");
            Matcher matcher = regex.matcher(realName);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证身份证
     *
     * @param identityNo
     * @return
     */
    public static boolean checkIdentityNo(String identityNo) {
        boolean flag = false;
        try {
            Pattern regex = Pattern
                    .compile("(^\\d{17}([0-9]|X|x)$)|(^\\d{15}$)");
            Matcher matcher = regex.matcher(identityNo);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    public static String matchUrlId(String urlKey,String url){
        // String regex = "(^|\\?|&)"+urlKey+"=\\d*(&|$)";
        String regex =   "(^|\\?|&)"+urlKey+"=(\\d*)";
        Pattern urlRegex = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = urlRegex.matcher(url);
        if(m.find()) {
             try{
                return m.group(2);
            }
            catch (Exception e){
                return "";
            }

        }
        return "";

    }
}
