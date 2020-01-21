package com.huatu.utils;


import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * <p/>
 * author : Soulwolf Create by 2015/6/16 11:36
 * email  : ToakerQin@gmail.com.
 */
public class StringUtils {

    //Arrays.asList
    public static <T> boolean isDiffSuccess(List<T> cacheList, List<T> httpList) {
        if (cacheList == null || cacheList.size() == 0 || httpList == null || httpList.size() == 0) {
            return true;
        }
        if (cacheList.size() != httpList.size()) {
            return true;
        }
        for (int i = 0; i < cacheList.size(); i++) {
            if (!cacheList.get(i).toString().equals(httpList.get(i).toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串合并
     */
    public static String and(Object... args) {
        if (args == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Object obj : args) {
            builder.append(obj);
        }
        return builder.toString();
    }

    /**
     * 为字符串添加 font标签
     */
    public static String fontColor(String color, Object str) {
        return String.format("<font color='%s'>%s</font>", color, str);
    }

    /**
     * 为字符串添加 font标签
     */
    public static String fontColor(Context context, @ColorRes int colorRes, @StringRes int strRes) {
        int color = context.getResources().getColor(colorRes);
        String str = context.getString(strRes);
        return String.format("<font color='%s'>%s</font>", color, str);
    }

    /**
     * 为字符串添加 font标签
     */
    public static CharSequence fontColorHtml(String color, Object str) {
        return Html.fromHtml(String.format("<font color='%s'>%s</font>", color, str));
    }

    public static String asStrong(String str) {
        return String.format("<strong>%s</strong>",  str);
    }

    public static String asItalic(String str) {
        return String.format("<i>%s</i>",  str);
    }

    public static CharSequence fontColorHtml(Context context, @ColorRes int colorRes, String strRes) {
        int color = context.getResources().getColor(colorRes);

        return Html.fromHtml(String.format("<font color='%s'>%s</font>", color, strRes));
    }

    /**
     * 从html模式解析字符串
     */
    public static CharSequence forHtml(String text) {
        return Html.fromHtml(text);
    }

    public static String formatInt(int size) {
        if (size < 10) return "0" + size;
        return size + "";
    }

    /**
     * 字符串转换成 int
     */
    public static int parseInt(String str) {
        if (TextUtils.isEmpty(str)) return 0;
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static int formatfloatPercent(float percent) {
        if (percent > 0) {
            if (percent < 1) return 1;
            return (int) percent;

        }
        return 0;

    }

    /**
     * 字符串转换成 long
     */
    public static long parseLong(String str) {
        if (TextUtils.isEmpty(str)) return 0;
        try {
            return Long.parseLong(str.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getIntSize(int num) {
        if (num < 10) return 1;
        if (num < 100) return 2;
        if (num < 1000) return 3;
        return 4;
    }

    /**
     * 字符串转换成 long
     */
    public static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float parseFloat(String str) {
        try {
            return Float.valueOf(str);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Object 转换为字符串
     */
    public static String toString(Object value) {
        return String.format("%s", value);
    }

    /**
     * 将字符串转成MD5值
     */
    public static String digestMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String Trim(String o) {
        return o == null ? "" : o.trim();
    }

    public static boolean equals(String oldStr, String tmpStr) {
        if (TextUtils.isEmpty(oldStr)) return TextUtils.isEmpty(tmpStr);
        else {
            return oldStr.equals(tmpStr);
        }
    }

    /**
     * Object 转换为字符串
     */
    public static String valueOf(Object o) {
        return o == null ? "" : o.toString();
    }

    /**
     * 活动详情周期性活动时间处理
     */
    public static String week2Chinese(int[] number, String separator) {
        if (ArrayUtils.isEmpty(number)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < number.length; i++) {
            switch (number[i]) {
                case 1:
                    builder.append("一");
                    break;
                case 2:
                    builder.append("二");
                    break;
                case 3:
                    builder.append("三");
                    break;
                case 4:
                    builder.append("四");
                    break;
                case 5:
                    builder.append("五");
                    break;
                case 6:
                    builder.append("六");
                    break;
                case 7:
                    builder.append("日");
                    break;

            }
            if (i != number.length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    /**
     * arrayList转String工具(,号分隔开)
     *
     * @param list
     * @return
     */
    public static String arrayString(List<String> list) {
        if(ArrayUtils.isEmpty(list)) return "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                sb.append(list.get(i));
            } else {
                sb.append(list.get(i) + ",");
            }
        }
        return sb.toString();
    }



   /* public static String joinString(Collection<String> list) {
        if(ArrayUtils.isEmpty(list)) return "";
        StringBuffer sb = new StringBuffer();
       *//* for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                sb.append(list..get(i));
            } else {
                sb.append(list.get(i) + ",");
            }
        }*//*
        return sb.toString();
    }*/

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyOrNull(String input){
      return   TextUtils.isEmpty(input)||"null".equals(input);
    }

    /**
     * 判断给定字符串是否为全数字
     *
     * @param input
     * @return
     */
    public static boolean isNumber(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "/n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static boolean isLength(String str, int min, int max) {
        if (!isEmpty(str) && str.length() >= min && str.length() <= max) return true;
        return false;
    }

    public static String cultString(String str, int len) {

        if (TextUtils.isEmpty(str)) return "";
        else {

            if (str.length() <= len) return str;
            else return str.substring(0, len) + "...";
        }
    }

    public static String cultString2(String str, int len) {

        if (TextUtils.isEmpty(str)) return "";
        else {

            if (str.length() <= len) return str;
            else return str.substring(0, len-1) + "...";
        }
    }

    public static String cultExString(String str, int maxLen) {

        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int count = 0;
        int endIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            char item = str.charAt(i);
            if (item < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
            if (maxLen == count || (item >= 128 && maxLen + 1 == count)) {
                endIndex = i;
            }
        }
        if (count <= maxLen) {
            return str;
        } else {

            return str.substring(0, endIndex) + "...";
        }
    }


    /**
     * 移除字符串
     *
     * @param target
     * @param replacement
     * @return
     */
    public static String removeString(String target, String replacement) {
        if (isEmpty(target) || isEmpty(replacement)) {
            return target;
        }
        if (target.contains(replacement)) {
            target = target.replace(replacement, "");
        }
        return target;
    }

    /**
     * 字符串A是否包含字符串B
     *
     * @param target
     * @param str
     * @return
     */
    public static boolean contains(String target, String str) {
        if (isEmpty(str) || isEmpty(target))
            return false;
        return target.contains(str);
    }

    /**
     * 阿拉伯数字转汉字
     *
     * @param number
     * @return
     */
    public static String convertNumberToChinese(int number) {
        if (number > 999)
            return valueOf(number);

        final String chn[] = {"百", "十", "九", "八", "七", "六", "五", "四", "三", "二", "一"};
        String chinese = "";
        //百位
        int hundred = number / 100;
        if (hundred > 0 && chn.length - 1 > hundred) {
            chinese += chn[chn.length - hundred] + "百";
            number %= 100;
        }

        //十位
        int decade = number / 10;
        if (decade > 0 && chn.length - 1 > decade) {
            if (decade > 1)
                chinese += chn[chn.length - decade] + "十";
            else
                chinese += "十";
            number %= 10;
        }

        //个位
        if (number > 0 && chn.length - 1 > number) {
            chinese += chn[chn.length - number];
        }
        return chinese;
    }

    /**
     * 话题格式化
     *
     * @param topic
     * @return
     */
    public static String TopicFormat(String topic) {
        if (topic == null)
            return topic;

        if (topic.startsWith("#")) {
            if (topic.endsWith("#"))
                return topic;
            else
                return topic + "#";
        } else if (topic.endsWith("#")) {
            return "#" + topic;
        } else {
            return "#" + topic + "#";
        }
    }

    /**
     * 清除话题#
     *
     * @param topic
     * @return
     */
    public static String clearTopicFormat(String topic) {
        if (topic == null)
            return topic;

        if (topic.length() >= 3 && topic.startsWith("#") && topic.endsWith("#")) {
            topic = topic.substring(1, topic.length() - 1);
        }
        return topic;
    }


    public static String delHTMLTag(String htmlStr) {
        //String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        //String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式

      /*      Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
            Matcher m_script=p_script.matcher(htmlStr);
            htmlStr=m_script.replaceAll(""); //过滤script标签

            Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
            Matcher m_style=p_style.matcher(htmlStr);
            htmlStr=m_style.replaceAll(""); //过滤style标签 */

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }

    public static String filterFileName(String fileName) {
        Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.replaceAll(""); // 将匹配到的非法字符以空替换
    }


    //[^ ] 匹配中括号中字符之外的任意一个字符
    //https://blog.csdn.net/itlwc/article/details/10145987
    static final Pattern IMG_PATTERN = Pattern.compile("<\\s*" + "img" + "\\s+([^>]*)\\s*", Pattern.CASE_INSENSITIVE);

    public static boolean hasImageTag(String inputStr) {
        if (TextUtils.isEmpty(inputStr)) return false;
        return IMG_PATTERN.matcher(inputStr).find();
    }

    /**
     * 替换指定标签的属性和值
     *
     * @param str       需要处理的字符串
     * @param tag       标签名称
     * @param tagAttrib 要替换的标签属性值
     * @param startTag  新标签开始标记
     * @param endTag    新标签结束标记
     * @return
     * @author huweijun
     * @date 2016年7月13日 下午7:15:32                     "img"       "src"            "src=\"http://junlenet.com/" "\""
     */
    public static String replaceHtmlTag(String str, String tag, String tagAttrib, String startTag, String endTag, HashMap<String, OssPicInfo> imgUrlMap) {
        String regxpForTag = "<\\s*" + tag + "\\s+([^>]*)\\s*";
        String regxpForTagAttrib = tagAttrib + "=\\s*\"([^\"]+)\"";
        Pattern patternForTag = Pattern.compile(regxpForTag, Pattern.CASE_INSENSITIVE);
        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib, Pattern.CASE_INSENSITIVE);
        Matcher matcherForTag = patternForTag.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();
        while (result) {
            StringBuffer sbreplace = new StringBuffer("<" + tag + " ");
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group(1));
            if (matcherForAttrib.find()) {
                String attributeStr = matcherForAttrib.group(1);
                if (imgUrlMap.containsKey(attributeStr)) {

                    //"data-ratio=\"0.58203125\"  data-w=\"1280\""
                    float tmpRatio = ((float) imgUrlMap.get(attributeStr).height) / imgUrlMap.get(attributeStr).width;
                    endTag = "\" data-ratio=\"" + String.valueOf(tmpRatio) + "\"  data-w=\"" + imgUrlMap.get(attributeStr).width + "\" ";
                }
                matcherForAttrib.appendReplacement(sbreplace, startTag + attributeStr + endTag);
            }
            matcherForAttrib.appendTail(sbreplace);
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);
        return sb.toString();
    }

/*    public static void main(String[] args) {
        StringBuffer content = new StringBuffer();
        content.append("<ul class=\"imgBox\"><li><img id=\"160424\" src=\"uploads/allimg/160424/1-160424120T1-50.jpg\" class=\"src_class\"></li>");
        content.append("<li><img id=\"150628\" src=\"uploads/allimg/150628/1-15062Q12247.jpg\" class=\"src_class\"></li></ul>");
        System.out.println("原始字符串为:"+content.toString());
        String newStr = replaceHtmlTag(content.toString(), "img", "src", "src=\"http://junlenet.com/", "\"");
        System.out.println("       替换后为:"+newStr);
    }*/




     /*String htmlContent= DbCacheManager.getTopicJson(curDto.topicDetail.id);
                   if(TextUtils.isEmpty(htmlContent)){
                       List<String> mImgList=curDto.topicDetail.imgList;
                       HashMap<String,StringUtils.OssPicInfo> tmpUrlMap=new HashMap<String,StringUtils.OssPicInfo>();
                       OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
                       for(int i=0;i<mImgList.size();i++){
                           Request request = new Request.Builder().url(Host.BUOUMALL_ALIYUN_ADDR + mImgList.get(i) + "@info").build();
                           okhttp3.Response response = okHttpClient.newCall(request).execute();

                           String tmpStr=response.body().string();
                           XLog.e("CopyResponse",tmpStr);
                           tmpUrlMap.put(Host.BUOUMALL_ALIYUN_ADDR + mImgList.get(i), JsonUtil.getEntity(tmpStr, StringUtils.OssPicInfo.class))  ;
                       }
                       htmlContent = StringUtils.replaceHtmlTag(curDto.topicDetail.content, "img", "src", "src=\"", "\" ", tmpUrlMap);//"data-src=\""
                       XLog.e("addcache","1");
                       DbCacheManager.addTopicJson(Long.parseLong(curDto.topicDetail.id),htmlContent);
                   }*/

    public static class OssPicInfo {
        public int height;
        public int width;
        public int size;
    }


    /**
     * 计算字符串s的长度
     * 特别：连续的 字母、数字、'.' 算一个
     * 空格、回车、tab不算字符
     * 160 是&nbsp
     */
    public static WordsCount getStringLength(String s, int limitLength) {
        if (s.isEmpty()) {
            return null;
        }

        // 计算前总字数
        int count = s.length();
        // 截取的字符串
        StringBuilder subContent = new StringBuilder();
        // 空白字符的个数
        int whiteChar = 0;

        char[] chars = s.toCharArray();

        boolean preIsCharacter = isCharacter(chars[0]);

        if (Character.isWhitespace(chars[0]) || chars[0] == 160) {
            count--;
            if (subContent.length() - whiteChar < limitLength) {
                whiteChar++;
            }
        }

        if (subContent.length() - whiteChar < limitLength) {
            subContent.append(chars[0]);
        }

        for (int i = 1; i < chars.length; i++) {
            boolean nowIsCharacter = isCharacter(chars[i]);
            if (nowIsCharacter && preIsCharacter) {
                count--;
                if (subContent.length() - whiteChar < limitLength) {
                    whiteChar++;
                }
            }
            if (Character.isWhitespace(chars[i]) || chars[i] == 160) {
                count--;
                if (subContent.length() - whiteChar < limitLength) {
                    whiteChar++;
                }
            }

            if (subContent.length() - whiteChar < limitLength) {
                subContent.append(chars[i]);
            }

            preIsCharacter = nowIsCharacter;
        }

        WordsCount wordsCount = new WordsCount();
        wordsCount.length = count;
        wordsCount.subContent = subContent.toString();

        return wordsCount;
    }

    /**
     * 字符c是否是 字符串 或 数字
     */
    public static boolean isCharacter(char c) {
        return Character.isDigit(c) || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '.';
    }

    public static class WordsCount {
        public int length;
        public String subContent;
    }
}
