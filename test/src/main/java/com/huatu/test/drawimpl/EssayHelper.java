package com.huatu.test.drawimpl;


import com.huatu.test.LogUtils;
import com.huatu.test.bean.CheckDetailBean;
import com.huatu.test.bean.EContent;
import com.huatu.test.bean.UnderLine;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class EssayHelper {

    private static String TAG = "EssayHelper";

    public static String getFilterTxt(String txt) {
        if (txt != null && txt.startsWith("<p>") && txt.endsWith("</p>")) {
            int start = "<p>".length();
            int end = txt.length() - "</p>".length();
            if (start < end && start > 0 && end < txt.length()) {
                return txt.substring(start, end);
            }
        }
        return txt;
    }

/*    public static void onBackDestory(Activity act) {
        SpUtils.setUserSubject(Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST);
        SignUpTypeDataCache.getInstance().curSubject = Type.CS_ExamType.ADMINISTRATIVE_APTITUDE_TEST;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.HOME_FRAGMENT_MSG_TYPE_CHANGE_SHOW_ARENA));
//        if (act instanceof Activity) {
//            (act).finish();
//        }
    }*/

    /*
     批改详情解析器

        注意：无论哪种批改，凡是有关键词得分或得分的句子，就画波浪线，并且波浪线尾部显示 +分

        小题（词批改）：
        <电商平台(+2.0分)>                              <>黄色底纹，本句划线加分
        [文化生活(文化生活……期待 +2.0分)]有新的[期待]      []红底，本句划线加分

        1.<电商平台(+2.0分)>的出现和普及，改变了农产品<销售方式(+1.0分)>，拓宽了销售渠道。\n       2.村镇居民<收入增(+2.0分)>加，生活水平提高。\n       
        3.村民对[文化生活(文化生活……期待 +2.0分)]有新的[期待]，希望有跳舞、下棋的娱乐场所。\n       
        4.村镇的交通、居住的生活[环境]得到了[改善(环境……改善 +2.0分)]。\n       5.<人口流(+2.0分)>向发生改变，回乡创业打工的人越来越多。\n       
        6.<资金流(+2.0分)>向发生改变，城里往乡镇的投资越来越多。\n       7.城镇居民[观念(观念……变 +2.0分)]转[变]，对农村未来的发展信心更足，相信生活会越来越好。


        大题（句子批改）：
        <是著名的{实业家}(1)> : {}黄色背景，句子划线，根据(1)中的序号，去addScoreList中寻找得分

        卢作孚先生于1893年出生在原四川省合川县一个世代农民家庭，<是著名的{实业家}(1)>，其创办的民生公司是中国近现代最大最有影响的民营企业之一，
        创造了中国现代经济史、社会史、文化史、教育史上的奇迹，是中国现代史上“乡村建设三杰”之一，<被誉为“北碚之父”(2)>。
        <卢作孚先生关于乡村建设的{理念内涵丰富}(3)>：（1）乡村建设的意义非常重大，<乡村现代化是中国现代化的基础(4)>。
        <（2）{乡村}建设的{内容}具有{综合性}(5)>，包括经济、文化教育、社会、环境、自治等多方面。<（3）乡村建设的过程应该将政治、
        经济、文化并重(6)>，<但经济建设是中心(7)>。<（4）乡村建设的根本目标是“{自立}”和“{立人}”(8)>。
        <其理念的现实意义有(9)>：<（1）从城乡发展关系的层面思考乡村建设(10)>，<对科学{规划}{农村}发展战略具有重要{意义}(11)>。
        <（2）将{宏观}视野和{具体}问题相结合(12)>，<有助于明确推动{乡村}运动的{范围}和{方法}(13)>。<（3）“工农互补”的发展方式(14)>，
        <表明应该建立多层次、多渠道、多行业共同参与的“{乡村建设联动模式}”(15)>。<（4）对于加{强}美丽{乡村建设}、提升大众参与意识、加大基层治理力度等都有重要作用(16)>。
        新的习惯 新的生活 \n案是手动刷的1005\n       近日，在我社区涌现出了许多新的现象。<比如{小茜}利用互联网不仅成功地买到了火车票(1)>，
        而且还为母亲挂上了专家号，省时省力；<小辉选择网上购物(2)>，便宜实惠，不用再找传统的关系；<小林在网上请了搬家公司搬家(3)>，
        <改变了过去找亲友搬家的老习惯(4)>；[区里实行新政(5)]，通过电脑派位，[贾先生的儿子顺利进入了理想的中学(5)]。\U00002028       
        <这些现象体现出了社会新风尚(6)>，<表明有许多新的习惯开始融入我们的生活(7)>。<互联网给我们的工作和生活提供了便利(8)>，
        <节约了资源(9)>，<提高了效率(10)>，<新政更是促进了社会公平正义(11)>。<这些看得见、摸得着的好处都是适应社会新风尚、改变过去旧习惯的结果(12)>。

        [{"score":1.0,"scorePoint":"是著名的实业家","sequenceNumber":1,"type":1},
        {"score":1.0,"scorePoint":"被誉为“北碚之父”","sequenceNumber":2,"type":1},
        {"score":1.0,"scorePoint":"卢作孚先生关于乡村建设的理念内涵丰富","sequenceNumber":3,"type":1},
        {"score":2.0,"scorePoint":"乡村现代化是中国现代化的基础","sequenceNumber":4,"type":1},
        {"score":2.0,"scorePoint":"（2）乡村建设的内容具有综合性","sequenceNumber":5,"type":1},
        {"score":1.0,"scorePoint":"（3）乡村建设的过程应该将政治、经济、文化并重","sequenceNumber":6,"type":1},
        {"score":1.0,"scorePoint":"但经济建设是中心","sequenceNumber":7,"type":1},
        {"score":2.0,"scorePoint":"（4）乡村建设的根本目标是“自立”和“立人”","sequenceNumber":8,"type":1},
        {"score":1.0,"scorePoint":"其理念的现实意义有","sequenceNumber":9,"type":1},
        {"score":1.0,"scorePoint":"（1）从城乡发展关系的层面思考乡村建设","sequenceNumber":10,"type":1},
        {"score":1.0,"scorePoint":"对科学规划农村发展战略具有重要意义","sequenceNumber":11,"type":1},
        {"score":1.0,"scorePoint":"（2）将宏观视野和具体问题相结合","sequenceNumber":12,"type":1},
        {"score":1.0,"scorePoint":"有助于明确推动乡村运动的范围和方法","sequenceNumber":13,"type":1},
        {"score":1.0,"scorePoint":"（3）“工农互补”的发展方式","sequenceNumber":14,"type":1},
        {"score":1.0,"scorePoint":"表明应该建立多层次、多渠道、多行业共同参与的“乡村建设联动模式”","sequenceNumber":15,"type":1},
        {"score":2.0,"scorePoint":"（4）对于加强美丽乡村建设、提升大众参与意识、加大基层治理力度等都有重要作用","sequenceNumber":16,"type":1}]

     */

    /**
     * 解析划线得分、红底纹、黄底纹
     * 注：这里两种批改规过滤放到一个方法 getString 里了，然后另外句子批改还会单独匹配一下{}里的关键词 getStringFour
     *
     * @param singleBean  题内容
     * @param info        答案内容
     * @param singleLines 划线
     * @param multLines   划线
     * @param singleChar  黄底纹
     * @param multChar    红底纹
     * @return 过滤过之后的文本
     */
    public static String getinput(CheckDetailBean singleBean, String info, ArrayList<UnderLine> singleLines, ArrayList<UnderLine> multLines, ArrayList<Integer> singleChar, ArrayList<Integer> multChar) {
        if (singleBean == null) {
            return info;
        }
        LogUtils.d(TAG, "Start Time: " + System.currentTimeMillis());
        LogUtils.d(TAG, "original content input : " + info);
        LogUtils.d(TAG, "original content input addScoreList: " + new Gson().toJson(singleBean.addScoreList));
//        String singleMatch = "<.+?\\([0-9]{1,}\\)>";        //<..(1)>尖括号正则表达式
//        String multipleMatch = "\\[.+?\\([0-9]{1,}\\)\\]";  //[..(2)]中括号正则表达式
        String singleMatch = "<.+?>";                       // 匹配尖括号标签，".+?" 非贪心匹配，匹配最少的字符 在 "<" 和 ">" 之间。
        String multipleMatch = "\\[.+?\\]";                 // 匹配方括号
        List<Character> punctuations = new ArrayList<>();   // 匹配断句标点
        punctuations.addAll(Arrays.asList('。', '.', ';', '；', '…', '!', '?', '？', ',', '，', '！', ':', '：', '。', ' '));      // , '、' 顿号不断句子

        String input = info;
        try {
            LogUtils.d(TAG, "singleBean.type: " + singleBean.type);
            if (singleBean.correctType == 1)        // 词批改（小题批改）
                input = getString(singleBean, singleLines, multLines, singleChar, multChar, singleMatch, multipleMatch, punctuations, input);
            else {                                  // 句子批改（大题批改）
                input = getString(singleBean, singleLines, multLines, singleChar, multChar, singleMatch, multipleMatch, punctuations, input);

                input = getStringFour(singleBean, singleLines, multLines, singleChar, multChar, singleMatch, multipleMatch, punctuations, input);
            }
            LogUtils.d(TAG, "Complete Time: " + System.currentTimeMillis());
            return input;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return input;
    }

    /**
     * 这里包括词的匹配和句子的匹配
     * <p>
     * <电商平台(+2.0分)>
     * [文化生活(文化生活……期待 +2.0分)]有新的[期待]
     * <是著名的{实业家}(1)>
     * <p>
     * 不包括大括号{} 大括号在 getStringFour 里匹配
     */
    private static String getString(CheckDetailBean singleBean, ArrayList<UnderLine> singleLines,
                                    ArrayList<UnderLine> multLines, ArrayList<Integer> singleChar, ArrayList<Integer> multChar,
                                    String singleMatch, String multipleMatch, List match, String input) {
        // <预算(+3.0分)>
        Pattern first = Pattern.compile(singleMatch);               // <> 的匹配
        Matcher fi = first.matcher(input);                          // <> 的匹配结果
        ArrayList<String> sNames = new ArrayList<>();                           // 记录<>中的内容         预算(+3.0分)
        ArrayList<String> sNamesScore = new ArrayList<>();                      // 记录<>中的内容 分数     3.0分

        // [费用(管理……费用 +4.0分)]
        ArrayList<String> mNames = new ArrayList<>();                           // 记录[]中的内容         费用(管理……费用 +4.0分)
        ArrayList<String> mNamesScore = new ArrayList<>();                      // 记录[]中的内容 分数     4.0分

        ArrayList<String> mNamesScore_content = new ArrayList<>();              // 有加分项的[]中()中的分数前面的内容添加进 或 不含+的 () 中的内容第几个addScoreList    费用 或者 (3)中的 3
        ArrayList<Integer> mNamesScore_c_index = new ArrayList<>();             // 含有加分项[]是mNames中的第几项                                  如果[]中有分数，就把对应sNames中的是第几个放入这里
        ArrayList<String> mNamesScore_content_index = new ArrayList<>();        // [] 不带+的() 中的 addScoreList index 中的内容                   (3) 中的  3
        ArrayList<String> siList = new ArrayList<>();                           // 记录<> 未匹配的内容，如：模仿(+2.0分)
        ArrayList<String> miList = new ArrayList<>();                           // 记录[] 未匹配的内容，如：创新(没有……创新 +2.0分)
        int[] sArray = new int[100];                                            // 记录<>单个词在文章中的位置，使标注到正确位置
        int[] mArray = new int[100];                                            // 记录[]单个词在文章中的位置，使标注到正确的位置
        int[] mArrayPosition = new int[100];                                    // 记录 没有……创新 在mArray中的位置

        // 匹配尖括号，记录字的内容sNames，位置sArray，分数sNamesScore
        // <.+?>
        // <预算(+3.0分)>          小题中
        // <是著名的{实业家}(1)>    大题中
        int count = 0;
        while (fi.find()) {
            int begin = fi.start();
            int tail = fi.end();
            String strVar = input.substring(begin + 1, tail - 1);                       // 尖括号中的内容  <预算(+3.0分)>  中的  预算(+3.0分)
            LogUtils.d(TAG, "<> name:" + strVar);
            sNames.add(strVar);                                                         // 把 <> 中的内容放到 sNames 中
            if (strVar != null && strVar.contains("+")) {
                String[] split = strVar.split("\\+");
                String score = split[split.length - 1];                                 // 把 <> 中的内容以+分开中的第二个   预算(  3.0分)
                if (score != null && score.length() > 0) {
                    if (score.contains("分")) {
                        sNamesScore.add(score.substring(0, score.length() - 1));        // 把 3.0分 添加进 sNameScore 中
                        LogUtils.d(TAG, "<> sNamesScore Score :" + sNamesScore.get(sNamesScore.size() - 1));
                    } else if (score != null && score.contains("(") && score.contains(")")) {
                        getScore(singleBean, sNamesScore, strVar);
                    }
                }
            } else if (strVar != null && strVar.contains("(") && strVar.contains(")")) {
                getScore(singleBean, sNamesScore, strVar);                              // 如果括号中不包含分数，那么它就是在addScoreList中的index，取出来，添加进 sNamesScores
            }
            sArray[count] = begin + 1;                                                  // 预算(+3.0分)  在文中的开始位置
            LogUtils.d(TAG, "<> name index:" + sArray[count]);
            count++;
        }

        // 匹配[] 方括号匹配内容mNames，分数mNamesScore，位置mArray
        // [费用(管理……费用 +4.0分)]
        Pattern second = Pattern.compile(multipleMatch);
        Matcher sd = second.matcher(input);
        int position = 0;
        while (sd.find()) {
            int begin = sd.start();
            int tail = sd.end();
            String strVar = input.substring(begin + 1, tail - 1);                                   // 匹配的          费用(管理……费用 +4.0分)
            LogUtils.d(TAG, "[] name:" + strVar);
            mNames.add(strVar);                                                                     // 匹配 [] 中的内容添加进 mNames
            if (strVar != null && strVar.contains("(") && strVar.contains("+")) {
                String[] strAry = strVar.split("\\+");                                       // 以 + 分开        费用(管理……费用  4.0分)
                if (strAry != null && strAry.length > 1) {
                    String s_content = getSplitKeyWords(strAry);                                    // 第0个    费用(管理……费用
                    if (s_content != null && s_content.contains("(")) {
                        String ss_content = s_content.substring(s_content.indexOf("(") + 1);        // 截取 ( 后边的     管理……费用
                        if (ss_content != null) {
                            mNamesScore_content.add(ss_content);                                    // 把带分数的 [] 中()中的分数前面的内容添加进 mNamesScore_content 管理……费用
                            mArrayPosition[mNamesScore_content.size() - 1] = position;              // 记录在mArray相应的index以便找出划线 管理……费用
                            if (mNames.size() > 0) {
                                mNamesScore_c_index.add(mNames.size() - 1);                         // 把对应的在mNames中的位置记录到 mNamesScore_c_index
                            }
                        }
                        LogUtils.d(TAG, "[] s_content:" + ss_content);
                    }
                    String score = strAry[strAry.length - 1];                                       // 4.0分)
                    if (score != null && score.length() > 0) {
                        String score1 = score.substring(0, score.length() - 1);                     // 4.0分
                        if (score1 != null) {
                            mNamesScore.add(score1);                                                // () 中的分数添加进 mNamesScore  4.0分
                        }
                        LogUtils.d(TAG, "[] mNamesScore Score :" + mNamesScore.get(mNamesScore.size() - 1));
                    }
                }
            } else if (strVar != null && strVar.contains("(") && strVar.contains(")")) {
                String index = getIndexM(strVar);                                                   // 如果不是(+3.0分)  就是(3) 中的3   就从addScoreList中去取
                if (index != null) {
                    mNamesScore_content_index.add(index);                                           // 把不带分数的 [] 中的 () 中的内容添加进 mNamesScore_content_index
                    if (!mNamesScore_content.contains(index)) {                                     // 如果mNamesScore_content不包含 (3) 中的 3 就把 3 添加进去
                        getScore(singleBean, mNamesScore, strVar);
                        mNamesScore_content.add(index);                                             // 如果 mNamesScore_content 中不包含 此内容，就添加进去
                        mArrayPosition[mNamesScore_content.size() - 1] = position;                  // 记录在mArray相应的index以便找出划线 管理……费用
                        mNamesScore_c_index.add(-1);
                    }
                }
            }
            mArray[position] = begin + 1;                                                           // [] 中内容的位置
            LogUtils.d(TAG, "[] name index:" + mArray[position]);
            position++;
        }

        // 把所有匹配的 [] <> ，替换为纯内容，并把记录字符位置的Array相应改变
        int number = 0;
        int sk = 0, mk = 0;
        while (number < sNames.size() + mNames.size()) {
            if (sArray[sk] < mArray[mk] && sk < sNames.size()) {
                LogUtils.d(TAG, "in if " + sk);
                String key = sNames.get(sk);                                                        // <> 中的内容
                EContent ec = getRpStr(key);                                                        // 预算(+3.0分)  预算 和 (+3.0分) 的长度
                if (ec != null) {
                    input = input.replace("<" + sNames.get(sk) + ">", ec.rpStr);             // 把原字符串带分数的 < > 替换成 纯内容字符串
                    sNames.set(sk, ec.rpStr);                                                       // 把记录 < > 内容的 list 中也替换成 纯字符串
                    sArray[sk] = sArray[sk] - 1;                                                    // 少了个 < 所以记录词位置的数字 - 1
                    for (int i = sk + 1; i < sNames.size(); i++) {
                        sArray[i] = sArray[i] - 2 - ec.dvalue;                                      // 少了个 < (+3.0分)> 把之后所有记录 字符位置的数字 都减少 对应长度
                    }
                    for (int j = mk; j < mNames.size(); j++) {
                        mArray[j] = mArray[j] - 2 - ec.dvalue;                                      // 记录 [] 的位置的同理，只要方括号在尖括号之后
                    }
                }
                ++sk;
            } else if (mArray[mk] < sArray[sk] && mk < mNames.size()) {
                LogUtils.d(TAG, "in else if " + mk);
                String key = mNames.get(mk);
                EContent ec = getRpStr(key);
                if (ec != null) {
                    input = input.replace("[" + mNames.get(mk) + "]", ec.rpStr);
                    mNames.set(mk, ec.rpStr);
                    mArray[mk] = mArray[mk] - 1;
                    for (int j = mk + 1; j < mNames.size(); j++) {
                        mArray[j] = mArray[j] - 2 - ec.dvalue;
                    }
                    for (int i = sk; i < sNames.size(); i++) {
                        sArray[i] = sArray[i] - 2 - ec.dvalue;
                    }
                }
                ++mk;
            } else if (sk < sNames.size()) {
                LogUtils.d(TAG, "sk < sNames.size() " + mk);
                String key = sNames.get(sk);
                EContent ec = getRpStr(key);
                if (ec != null) {
                    input = input.replace("<" + sNames.get(sk) + ">", ec.rpStr);
                    sNames.set(sk, ec.rpStr);
                    sArray[sk] = sArray[sk] - 1;
                    for (int i = sk + 1; i < sNames.size(); i++) {
                        sArray[i] = sArray[i] - 2 - ec.dvalue;
                    }
                }
                ++sk;
            } else if (mk < mNames.size()) {
                LogUtils.d(TAG, "in mk < mNames.size() " + mk);
                String key = mNames.get(mk);
                EContent ec = getRpStr(key);
                if (ec != null) {
                    input = input.replace("[" + mNames.get(mk) + "]", ec.rpStr);
                    mNames.set(mk, ec.rpStr);
                    mArray[mk] = mArray[mk] - 1;
                    for (int j = mk + 1; j < mNames.size(); j++) {
                        mArray[j] = mArray[j] - 2 - ec.dvalue;
                    }
                }
                ++mk;
            }
            number++;
        }
        LogUtils.d(TAG, "<> input:" + input);

        for (int i = 0; i < sNames.size(); i++) {
            LogUtils.d(TAG, "<> after remove  index:" + sArray[i]);
        }
        for (int i = 0; i < mNames.size(); i++) {
            LogUtils.d(TAG, "[] after remove  index:" + mArray[i]);
        }

        // <> 记录划线内容，添加进 singleLines
        for (int i = 0; i < sNames.size(); i++) {
            int index = sArray[i];
            UnderLine underLine = new UnderLine();
            int start = index;                                                          // 字符开始处
            String varStr = sNames.get(i);                                              // 对应字符
            int length = 0;
            if (varStr != null) {
                length = varStr.length();                                               // 字符串长度
            }
            int end = index + length;                                                   // 字符串结束处
            while (start > 0 && isaBooleanBreak(match, input, start)) {                 // 找到字符串前面第一个的语句分割号的位置
                start--;
            }
            while (end < input.length() && isaBooleanBreak(match, input, end)) {        // 找到字符串后面第一个语句分割号的位置
                end++;
            }
            if (start != 0) {
                underLine.start = start + 1;                                            // 记录划线语句的其实位置
            } else {
                underLine.start = start;
            }
            underLine.end = end;
            if (i < sNamesScore.size()) {
                underLine.score = "+" + sNamesScore.get(i);                             // 划线语句的分数
                LogUtils.d(TAG, "sNamesScore :" + sNamesScore.get(i));
            }

            int hasIndex = hasLine(singleLines, underLine.start, underLine.end);        // 此线是否在已经划线的里边

            if (hasIndex != Integer.MAX_VALUE) {                                        // 不等于 Integer.MAX_VALUE 说明找到了
                if (hasIndex < singleLines.size()) {
                    UnderLine var = singleLines.get(hasIndex);
                    if (var != null && var.score != null) {
                        if (var.end == underLine.end) {
                            var.score = getUpdateScore(var.score, underLine.score);     // 把两个分值加起来
                            if (var.start > underLine.start) {
                                var.start = underLine.start;
                            }
                        } else {
                            singleLines.add(underLine);
                        }
                    } else {
                        singleLines.add(underLine);
                    }
                } else {
                    singleLines.add(underLine);
                }
            } else {                                                                    // 没找到就直接添加进singleLines
                singleLines.add(underLine);
            }

            LogUtils.d(TAG, "start:" + start);
            LogUtils.d(TAG, "end:" + end);
        }

        // 解析 [] 划线内容
        // [费用(管理……费用 +4.0分)]
        for (int i = 0; i < mNamesScore_content.size(); i++) {
            UnderLine underLine = new UnderLine();
            if (i < mNamesScore.size()) {
                underLine.score = "+" + mNamesScore.get(i);
            }
            String str = mNamesScore_content.get(i);                                                // 管理……费用
            if (str != null && str.contains("……")) {
                String[] strary = str.split("……");                                           // 管理  费用
                if (strary != null && strary.length > 0) {
//                    String startStr = strary[0];
//                    if (startStr != null) {
//                        startStr = startStr.trim();                                                 // 管理
//                    }
//                    String endStr = strary[strary.length - 1];
//                    if (endStr != null) {
//                        endStr = endStr.trim();                                                     // 费用
//                    }
//                    int index = 0;
//                    int s_index = 0;
//                    if (i < mNamesScore_c_index.size()) {
//                        s_index = mNamesScore_c_index.get(i);                                       // 是sName中的第几个
//                        index = s_index;
//                    }

                    // 匹配前边的字符
//                    boolean has = false;
//                    while (index >= 0 && index < mNames.size() && !(has = startStr.equals(mNames.get(index)))) {
//                        index--;
//                    }
//                    if (!has) {
//                        index = s_index;
//                        while (index < mNames.size() && !(has = startStr.equals(mNames.get(index)))) {
//                            index++;
//                        }
//                    }
//
//                    if (index >= 0 && index < mArray.length) {
//                        underLine.start = mArray[index];
//                    }

                    // 匹配后边的字符
//                    index = s_index;
//                    while (index < mNames.size() && !(has = endStr.equals(mNames.get(index)))) {
//                        index++;
//                    }
//                    if (!has) {
//                        index = s_index;
//                        while (index >= 0 && index < mNames.size() && !(has = endStr.equals(mNames.get(index)))) {
//                            index--;
//                        }
//                    }
//
//                    if (index >= 0 && index < mArray.length && index < mNames.size()) {
//                        underLine.end = mArray[index] + mNames.get(index).length();
//                    }
//
//                    if (underLine.end < underLine.start) {
//                        int s = underLine.start;
//                        underLine.start = underLine.end;
//                        underLine.end = s;
//                    }

//                    int start = underLine.start;
//                    int end = underLine.end;

                    // 这里不做词的匹配了，而是，只是断句划线

                    int start = mArray[mArrayPosition[i]];
                    int end = mArray[mArrayPosition[i]];

                    while (start > 0 && isaBooleanBreak(match, input, start)) {
                        start--;
                    }
                    while (end < input.length() && isaBooleanBreak(match, input, end)) {
                        end++;
                    }
                    if (start != 0) {
                        underLine.start = start + 1;
                    } else {
                        underLine.start = start;
                    }
                    underLine.end = end;

                    multLines.add(underLine);
                }
            } else if (str != null) {
                String startStr = str;
                if (startStr != null) {
                    startStr = startStr.trim();
                }

                int index = 0;
                while (index < mNamesScore_content_index.size() && !startStr.equals(mNamesScore_content_index.get(index))) {
                    index++;
                }

                if (index >= 0 && index < mArray.length) {
                    underLine.start = mArray[index];
                }

                for (int j = index + 1; j < mNamesScore_content_index.size(); j++) {
                    if (startStr.equals(mNamesScore_content_index.get(j))) {
                        index = j;
                    }
                }

                if (index >= 0 && index < mArray.length && index < mNames.size()) {
                    underLine.end = mArray[index] + mNames.get(index).length();
                }


                int start = underLine.start;
                int end = underLine.end;
                while (start > 0 && isaBooleanBreak(match, input, start)) {
                    start--;
                }
                while (end < input.length() && isaBooleanBreak(match, input, end)) {
                    end++;
                }

                if (start != 0) {
                    underLine.start = start + 1;
                } else {
                    underLine.start = start;
                }
                underLine.end = end;

                multLines.add(underLine);
            }
        }

        // 把multLines中的数据全部添加进singleLines中
        if (multLines != null && multLines.size() > 0) {
            for (UnderLine multLine : multLines) {
                if (multLine != null) {
                    multLine.isMult = true;
                    int startIndex = multLine.start;
                    int endIndex = multLine.end;
                    int hasIndex = hasLine(singleLines, startIndex, endIndex);

                    if (hasIndex != Integer.MAX_VALUE) {
                        if (hasIndex < singleLines.size()) {
                            UnderLine var = singleLines.get(hasIndex);
                            if (var != null && var.score != null) {
                                if (var.end == multLine.end) {
//                                    var.score = var.score + "+" + multLine.score;
                                    var.score = getUpdateScore(var.score, multLine.score);
                                    if (var.start > multLine.start) {
                                        var.start = multLine.start;
                                    }
                                } else {
                                    singleLines.add(multLine);
                                }
                            } else {
                                singleLines.add(multLine);
                            }
                        } else {
                            singleLines.add(multLine);
                        }
                    } else {
                        singleLines.add(multLine);
                    }
                }
            }
            multLines.clear();
        }

        // 把singleLines内部过滤一遍，是不是有线重合的情况。
        ArrayList<UnderLine> newSingleLines = new ArrayList<>();
        while (!singleLines.isEmpty()) {
            UnderLine underLine = singleLines.remove(0);
            int overIndex = hasOverlap(newSingleLines, underLine);
            if (overIndex == -1) {
                newSingleLines.add(underLine);
            } else {
                // 只要有重线，就再次全部过滤，因为或许有一个线和多条线有交叉的情况。
                UnderLine overLine = newSingleLines.get(overIndex);
                overLine.start = overLine.start <= underLine.start ? overLine.start : underLine.start;
                overLine.end = overLine.end >= underLine.end ? overLine.end : underLine.end;
                overLine.score = getUpdateScore(overLine.score, underLine.score);
                singleLines.addAll(newSingleLines);
                newSingleLines.clear();
            }
        }

        singleLines.clear();
        singleLines.addAll(newSingleLines);

        // 把整分数的 .0 去掉
        for (UnderLine singleLine : singleLines) {
            singleLine.score = singleLine.score.replace(".0", "");
        }

        Collections.sort(singleLines, new Comparator<UnderLine>() {
            @Override
            public int compare(UnderLine o1, UnderLine o2) {
                return o1.start - o2.start;
            }
        });

        LogUtils.d(TAG, "modify info:" + input);
        for (int i = 0; i < sNames.size(); i++) {
            if (i < sArray.length) {
                singleChar.add(sArray[i]);
                singleChar.add(sArray[i] + sNames.get(i).length());
            }
        }
        for (int j = 0; j < mNames.size(); j++) {
            if (j < mArray.length) {
                multChar.add(mArray[j]);
                multChar.add(mArray[j] + mNames.get(j).length());
            }
        }
        return input;
    }

    private static String getSplitKeyWords(String[] strAry) {
        if (strAry != null && strAry.length > 0) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < strAry.length - 1; i++) {
                result.append(strAry[i]).append("\\+");
            }
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        }
        return "";
    }

    // 是否是断行点 返回false就是断行
    private static boolean isaBooleanBreak(List match, String input, int start) {
        if (match != null && input != null && start < input.length()) {
            char a = 0;

            // 全不是这些，才能继续，才不是断句
            return !match.contains(a = input.charAt(start))
                    && a != 160             // 空格
                    && a != 10              // 换行符
                    && a != 12288;          // 全角空格
        }
        return false;
    }

    private static String getUpdateScore(String score, String score1) {
        String n_score = score + "+" + score1;
        if (score != null && score1 != null && score.contains("分") && score1.contains("分")) {
            score = score.substring(1, score.length() - 1);
            score1 = score1.substring(1, score1.length() - 1);
            if (score != null && score1 != null) {
                try {
                    float sVar = Float.parseFloat(score);
                    float sVar2 = Float.parseFloat(score1);
                    n_score = "+" + (sVar + sVar2) + "分";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return n_score;
    }

    /**
     * 匹配句子批改规则里的大题里的 {}
     */
    private static String getStringFour(CheckDetailBean singleBean, ArrayList<UnderLine> singleLines,
                                        ArrayList<UnderLine> multLines, ArrayList<Integer> singleChar, ArrayList<Integer>
                                                multChar, String singleMatch, String multipleMatch, List match, String input) {

        String yingyongMatch = "\\{.+?\\}";

        if (singleChar != null) {
            singleChar.clear();
        }

        if (multChar != null) {
            multChar.clear();
        }

        Pattern first = Pattern.compile(yingyongMatch);
        Matcher fi = first.matcher(input);
        ArrayList<String> sNames = new ArrayList<>();                               // 记录<>中的内容
        ArrayList<String> sNamesScore = new ArrayList<>();                          // 记录<>中的内容 分数
        ArrayList<String> mNames = new ArrayList<>();                               // 记录[]中的内容
        ArrayList<String> mNamesScore = new ArrayList<>();                          // 记录[]中的内容 分数
        ArrayList<String> mNamesScore_content = new ArrayList<>();                  //
        ArrayList<String> mNamesScore_content_index = new ArrayList<>();            //
        ArrayList<String> siList = new ArrayList<>();                               // 记录<> 未匹配的内容，如：模仿(+2.0分)
        ArrayList<String> miList = new ArrayList<>();                               // 记录[] 未匹配的内容，如：创新(没有……创新 +2.0分)
        int[] sArray = new int[100];                                                // 记录<>单个词在文章中的位置，使标注到正确位置
        int[] mArray = new int[100];                                                // 记录[]单个词在文章中的位置，使标注到正确的位置

        int count = 0;
        while (fi.find()) {
            int begin = fi.start();
            int tail = fi.end();
            String strVar = input.substring(begin + 1, tail - 1);
            LogUtils.d(TAG, "<> name:" + strVar);
            sNames.add(strVar);
            sArray[count] = begin + 1;
            LogUtils.d(TAG, "<> name index:" + sArray[count]);
            count++;
        }

        int number = 0;
        int sk = 0, mk = 0;
        while (number < sNames.size() + mNames.size()) {
            if (sArray[sk] < mArray[mk] && sk < sNames.size()) {
                LogUtils.d(TAG, "in if " + sk);
                String key = sNames.get(sk);
                EContent ec = getRpStrFour(key);
                if (ec != null) {
                    input = input.replace("{" + sNames.get(sk) + "}", ec.rpStr);
                    sNames.set(sk, ec.rpStr);
                    sArray[sk] = sArray[sk] - 1;
                    for (int i = sk + 1; i < sNames.size(); i++) {
                        sArray[i] = sArray[i] - 2 - ec.dvalue;
                    }
                    for (int j = mk; j < mNames.size(); j++) {
                        mArray[j] = mArray[j] - 2 - ec.dvalue;
                    }
                }
                ++sk;
            } else if (mArray[mk] < sArray[sk] && mk < mNames.size()) {
                LogUtils.d(TAG, "in else if " + mk);
                String key = mNames.get(mk);
                EContent ec = getRpStrFour(key);
                if (ec != null) {
                    input = input.replace("[" + mNames.get(mk) + "]", ec.rpStr);
                    mNames.set(mk, ec.rpStr);
                    mArray[mk] = mArray[mk] - 1;
                    for (int j = mk + 1; j < mNames.size(); j++) {
                        mArray[j] = mArray[j] - 2 - ec.dvalue;
                    }
                    for (int i = sk; i < sNames.size(); i++) {
                        sArray[i] = sArray[i] - 2 - ec.dvalue;
                    }
                }
                ++mk;
            } else if (sk < sNames.size()) {
                LogUtils.d(TAG, "sk < sNames.size() " + mk);
                String key = sNames.get(sk);
                EContent ec = getRpStrFour(key);
                if (ec != null) {
                    input = input.replace("{" + sNames.get(sk) + "}", ec.rpStr);
                    sNames.set(sk, ec.rpStr);
                    sArray[sk] = sArray[sk] - 1;
                    for (int i = sk + 1; i < sNames.size(); i++) {
                        sArray[i] = sArray[i] - 2 - ec.dvalue;
                    }
                    if (singleLines != null) {
                        for (UnderLine singleLine : singleLines) {
                            if (singleLine.start > sArray[sk]) {
                                singleLine.start = singleLine.start - 2;
                            }
                            if (singleLine.end > sArray[sk]) {
                                singleLine.end = singleLine.end - 2;
                            }
                        }
                    }
                    if (multLines != null) {
                        for (UnderLine multLine : multLines) {
                            if (multLine.start > sArray[sk]) {
                                multLine.start = multLine.start - 2;
                            }
                            if (multLine.end > sArray[sk]) {
                                multLine.end = multLine.end - 2;
                            }
                        }
                    }
                }
                ++sk;
            } else if (mk < mNames.size()) {
                LogUtils.d(TAG, "in mk < mNames.size() " + mk);
                String key = mNames.get(mk);
                EContent ec = getRpStrFour(key);
                if (ec != null) {
                    input = input.replace("[" + mNames.get(mk) + "]", ec.rpStr);
                    mNames.set(mk, ec.rpStr);
                    mArray[mk] = mArray[mk] - 1;
                    for (int j = mk + 1; j < mNames.size(); j++) {
                        mArray[j] = mArray[j] - 2 - ec.dvalue;
                    }
                }
                ++mk;
            }
            number++;
        }
        LogUtils.d(TAG, "<> input:" + input);

        for (int i = 0; i < sNames.size(); i++) {
            LogUtils.d(TAG, "<> after remove  index:" + sArray[i]);
        }
        for (int i = 0; i < mNames.size(); i++) {
            LogUtils.d(TAG, "[] after remove  index:" + mArray[i]);
        }

        LogUtils.d(TAG, "modify four info:" + input);
        for (int i = 0; i < sNames.size(); i++) {
            if (i < sArray.length) {
                singleChar.add(sArray[i]);
                singleChar.add(sArray[i] + sNames.get(i).length());
            }
        }
        for (int j = 0; j < mNames.size(); j++) {
            if (j < mArray.length) {
                multChar.add(mArray[j]);
                multChar.add(mArray[j] + mNames.get(j).length());
            }
        }
        return input;
    }

    /**
     * 检查此线是否在当前含有的线内
     * 1、是在含有的线内
     * 2、结束的位置相等
     * 注意，没有说重叠的位置
     */
    private static int hasLine(ArrayList<UnderLine> singleLines, int startIndex, int endIndex) {
        for (int i = 0; i < singleLines.size(); i++) {
            UnderLine var = singleLines.get(i);
            if (var != null) {
                int s_var = var.start;
                int e_var = var.end;
                if (startIndex >= s_var && endIndex <= e_var) {
                    return i;
                }
                if (endIndex == e_var) {
                    return i;
                }
            }
        }
        return Integer.MAX_VALUE;
    }

    // 判断 underLine 是否和 newSingleLines 中的线有重合的部分
    private static int hasOverlap(ArrayList<UnderLine> newSingleLines, UnderLine underLine) {
        for (int i = 0; i < newSingleLines.size(); i++) {
            UnderLine newSingle = newSingleLines.get(i);
            if (newSingle.start <= underLine.start && newSingle.end >= underLine.start) {
                return i;
            }
            if (underLine.start <= newSingle.start && underLine.end >= newSingle.start) {
                return i;
            }
        }
        return -1;
    }

    private static String removeinfohead(String info) {
        if (info != null) {
            int index = 0;
            if (isFirstLineOfParagraph(info)) {
                while (info.charAt(index) == ' ' && index < info.length()) {
                    index++;
                }
                info = info.substring(index);
            }
        }
        return info;
    }

    public static boolean isFirstLineOfParagraph(String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }

    private static String getIndexM(String strVar) {
        if (strVar != null) {
            int start = strVar.indexOf("(") + 1;
            int end = strVar.indexOf(")");
            if (start >= 0 && end >= 0 && start < end) {
                String index = strVar.substring(start, end);
                return index;
            }
        }
        return null;
    }

    private static void getScore(CheckDetailBean singleBean, ArrayList<String> namesScore, String strVar) {
        int start = strVar.indexOf("(") + 1;
        int end = strVar.indexOf(")");
        if (start >= 0 && end >= 0 && start < end) {
            String index = strVar.substring(start, end);
            if (index != null) {
                int i = Integer.valueOf(index);
                i--;
                if (singleBean.addScoreList != null) {
                    if (i < singleBean.addScoreList.size()) {
                        CheckDetailBean.ScoreListEntity var = singleBean.addScoreList.get(i);
                        if (var != null) {
                            namesScore.add(var.score + "分");
                            LogUtils.d(TAG, "var.score+\"分\"----------" + var.score + "分");
                        }
                    }

                }
            }
        }
    }

    private static EContent getRpStr(String key) {                      // 预算(+3.0分)
        int index = 0;
        int dvalue = 0;
        String rpStr = key;
        if (key != null) {
            if (key != null && key.contains("(")) {
                index = key.indexOf("(");
                if (index > 0) {
                    rpStr = key.substring(0, index);                    // 预算
                }
            }
            if (rpStr != null) {
                dvalue = key.length() - rpStr.length();                 // (+3.0分) 的长度
            }
        }
        EContent var = new EContent();
        var.rpStr = rpStr;
        var.dvalue = dvalue;
        LogUtils.d(TAG, "key:" + key);
        LogUtils.d(TAG, "index:" + index);
        LogUtils.d(TAG, "dvalue:" + dvalue);
        LogUtils.d(TAG, "rpStr : " + rpStr);
        return var;
    }

    private static EContent getRpStrFour(String key) {
        int index = 0;
        int dvalue = 0;
        EContent var = new EContent();
        var.rpStr = key;
        var.dvalue = 0;
        LogUtils.d(TAG, "key:" + key);
        LogUtils.d(TAG, "index:" + index);
        LogUtils.d(TAG, "dvalue:" + dvalue);
        return var;
    }


    public static String getString(String info, ArrayList<String> uContentls) {

        if (info != null && uContentls != null) {
            String uMatch = "<u>.+?</u>";
            Pattern first = Pattern.compile(uMatch);
            Matcher fi = first.matcher(info);
            while (fi.find()) {
                int begin = fi.start();
                int tail = fi.end();
                if (begin + 3 > 0 && begin + 3 < tail - 4 && tail - 4 < info.length()) {
                    String strVar = info.substring(begin + 3, tail - 4);
                    uContentls.add(strVar);
                }
            }
        }
        return "";
    }

    public static String uContentls2Indexls(String info, ArrayList<String> uContentls, ArrayList<Integer> indexls) {
        if (info != null && uContentls != null && indexls != null) {
            for (String uContentl : uContentls) {
                if (uContentl != null && info.contains(uContentl)) {
                    int start = info.indexOf(uContentl);
                    int end = start + uContentl.length();
                    indexls.add(start);
                    indexls.add(end);
                }
            }
        }
        return "";
    }


}
