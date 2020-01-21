package com.huatu.handheld_huatu.view.custom;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.business.essay.bean.TagBean;
import com.huatu.handheld_huatu.business.essay.bhelper.EssayHelper;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.OnSelectListener;
import com.huatu.handheld_huatu.business.essay.bhelper.textselect.SelectableTextHelper;
import com.huatu.handheld_huatu.business.essay.cusview.bean.UnderLine;
import com.huatu.handheld_huatu.business.essay.cusview.drawimpl.CusNumAlignText;
import com.huatu.handheld_huatu.business.essay.cusview.drawimpl.CusNumCheckDetailTag;
import com.huatu.handheld_huatu.mvpmodel.essay.CheckDetailBean;
import com.huatu.handheld_huatu.utils.ResourceUtils;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cjx on 2019\6\26 0026.
 */

public class EssayExerciseTextView extends ExerciseTextView {

    static ArrayList<UnderLine> mMultLines = new ArrayList<>();           // 没啥用，暂时不让代码报错

    public interface OnTextSelectListener {
        void clearView();
    }

    OnTextSelectListener mOnTextSelectListener;

    public void setOnTextSelectListener(OnTextSelectListener textSelectListener) {
        this.mOnTextSelectListener = textSelectListener;
    }

    @Override
    public void clearView() {
        super.clearView();
        if (mSelectableTextHelperD != null) {
            mSelectableTextHelperD.clearView();
        }
    }

    private SelectableTextHelper mSelectableTextHelperD;

    public EssayExerciseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EssayExerciseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    //老的解析方式
    public void initContent(CheckDetailBean singleBean) {
        if (singleBean == null) {
            return;
        }
        String info = singleBean.correctedContent;
        if (TextUtils.isEmpty(info) || "null".equals(info)) {
            {
                this.setText("本题未做答");
            }
            return;
        }
        if (singleBean.correctedContent.contains("\u2028")) {
            // 有的机型把分行符识别成 不认识的符号，这里过滤一下
            singleBean.correctedContent = singleBean.correctedContent.replaceAll("\u2028", "\n");
        }
        ArrayList<Integer> singleChar = new ArrayList<>();              // 黄底纹
        ArrayList<Integer> multChar = new ArrayList<>();                // 红底纹
        ArrayList<UnderLine> singleLines = new ArrayList<>();           // 记录划线的位置 开始 结束 分数
        final ArrayList<UnderLine> multLines = new ArrayList<>();       // 记录划线，不过最后都添加到singleLines中了
        // 解析划线数据
        String input = EssayHelper.getinput(singleBean, info, singleLines, multLines, singleChar, multChar);
        final CusNumAlignText cusAlignText = new CusNumAlignText(this);
        final CusNumCheckDetailTag mDrawLine = new CusNumCheckDetailTag(cusAlignText);

        if (mSelectableTextHelperD == null) {
            mSelectableTextHelperD = new SelectableTextHelper.Builder(this)
                    .setSelectedColor(ResourceUtils.getColor(R.color.essay_sel_color))
                    .setCursorHandleSizeInDp(10)
                    .setShowType(SelectableTextHelper.SHOW_TYPE_ONLY_COPY)
                    .setCursorHandleColor(ResourceUtils.getColor(R.color.main_color))
                    .build();
        }
        mSelectableTextHelperD.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {
                // 如果选择复制，清除其他的选择
                if (null != mOnTextSelectListener) {
                    mOnTextSelectListener.clearView();
                }
            }

            @Override
            public void updateView(int type) {
                if (cusAlignText != null) {
                    if (type == 0) {
                        cusAlignText.setSelectableTextHelper(null);
                    } else if (type == 1) {
                        cusAlignText.setSelectableTextHelper(null);
                    } else {
                        cusAlignText.setSelectableTextHelper(mSelectableTextHelperD.mSelectionInfo);
                    }
                    cusAlignText.invalidate();
                }
            }
        });
        cusAlignText.setSelectableTextHelper(mSelectableTextHelperD.mSelectionInfo);
        mDrawLine.setUnderLine(singleLines, multLines, singleChar, multChar);

        this.setmCusDraw(cusAlignText);
        this.setTextImgTagEssay(input);               // 是否有图片标签
        this.setText(input);
        if (TextUtils.isEmpty(input)) {
            this.setHighlightColor(getResources().getColor(android.R.color.transparent));
        }
    }

/*

    public static String TESTSTRING = "<titleScore value=3 seq=-1 describe=“描述语言” score= >科技人性化之路</titleScore>\n" +
            "\n" +
            "       科技是国家强盛之基，科技创新是提高社会生产力和综合国力的战略支撑，我国坚定不移地走科技强国之路。创新驱动发展战略的实施，使未来科技的发展方向走向人性化之路。<thesisScore value=4 id=152582 seq=-1 describe=“描述语言” score= >许多高科技产品被赋予灵动的生命，但事实上科技是一种生命，具有生命的普遍特征。</thesisScore>\n" +
            "       <thesisScore value=4 id=152583 seq=-1 describe=“描述语言” score= >科技发展的本质是为人民服务，不能忽略民众的精神力量。</thesisScore><evidenceScore value=3-152583 seq=-1 describe=“描述语言” score= >著名学者刘易斯•芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为“<literaryScore value=1-10 seq=-1 describe=“描述语言” score=4 >无目的的物质至上主义</literaryScore>”，科技发展突飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。但是科技发展的过程，人民群众的精神力量尤为重要。因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。</evidenceScore>\n" +
            "      <thesisScore value=4 id=152585 seq=-1 describe=“描述语言” score= > 科技是一种生命，坚持走科技人性化之路。</thesisScore><evidenceScore value=3-152585 seq=-1 describe=“描述语言” score= >人才队伍的建设促进了高科技的发展，科研人员卓越的专业技能使高科技产品被赋予了生命，使科技和人性之间嫁接了桥梁，，人性化之路将赋予高科技以新的价值观。比如，电子产品的更新换代，智能手机的出现满足了人们的一些需求，秀才不出门，便知天下事，拥有一部手机可以方便人们之间的交流，随时随地关住国家大事和热点新闻。</evidenceScore><evidenceScore value=3-152585 seq=-1 describe=“描述语言” score= >但是长时间使用电子产品对人们身体健康不好，尤其是眼睛，高科技发展的时代，电子产品创造了护眼模式，一方面满足了人们的需求，另一方面，对人体身心伤害小，所以科技发展需要具备人性化的要求。</evidenceScore>\n" +
            "       <thesisScore value=3-0(其他) id=152588 seq=-1 describe=“描述语言” score=3 >科技发展与人性化之间追求一种平衡，只有将科技和人文两者紧密联系在一起，才能体现出科技之美，</thesisScore><evidenceScore value=3-152588 seq=-1 describe=“描述语言” score= >我国科研人才队伍的独特的专业技能，高尚的道德修养，同时遵守了职业道德的基本准则，更好地为人民服务，为把我国建设成现代化强国贡献力量。国与国之间的竞争实质是人才竞争，只有强大的科技人才队伍，我国的科技才能居世界前列，科技水平提高了，科技产品才具有创新性，同时，人性化的科技才能为人类创造出科技成就和物质财富，所以科技发展要坚持人性化原则，实现科技与人类和谐共生。</evidenceScore>\n" +
            "      <thoughtScore value=2 seq=-1 describe=“描述语言” score= > 互联网时代，每一项科技成果都应该以人为本，人民至上，只有体现人性化的科技成果，才能造福于人类，为人类社会科技和谐发展奠定基石。</thoughtScore><structScore value=1 seq=-1 describe=“描述语言” score= >只有在科技化与人性化之间取得了平衡，才体现了一种智慧和态度。</structScore>";

    public static String TESTSTRING9 = "1.“连接”，又称联接，指事物互相衔接。“连接”成为热词，源于互联网<label_1 seq=\"54\" description=\"论点准确,结构深刻\" score=\"4.0\" drawType=\"0\">信息技术以独特的勾连方式，将原本散落在世界各个角落的、不同肤色、不同信仰的人连接起来，构成网络社会。因“连接”</label_1>而产<label_2 seq=\"2\" description=\"论据深刻\" score=\"4.0\" drawType=\"0\"><label_3 seq=\"3\" description=\"论点强有力\" score=\"0.0\" drawType=\"0\">生的各种社会系统，重构世界、重构人类社会，导致人类社会进入了一个全新的人机信息勾连</label_4></label_2><label_4 seq=\"4\" description=\"论点完整\" score=\"4.0\" drawType=\"0\"><label_5 seq=\"5\" description=\"文采强有力\" score=\"2.0\" drawType=\"1\">的社</label_5></label_3>会<label_6 seq=\"6\" description=\"论据准确,论点完整\" score=\"5.0\" drawType=\"0\">新纪</label_6>元。<label_7 seq=\"7\" description=\"论点流畅,结构准确\" score=\"6.0\" drawType=\"0\">互联网连接过去、现在和未来，影响范围越来越大。成长于互联网蓬勃发展时期的“90后”，他们</label_7>有自己的文化、语言和价值体系<label_8 seq=\"8\" description=\"论据深刻,论点流畅\" score=\"3.0\" drawType=\"0\">，他们的成长由互联网全程参</label_8>与，他<label_9 seq=\"9\" description=\"论点流畅,句子强有力\" score=\"4.0\" drawType=\"2\">们相信一切都是相互连接的，不光是人与人相连接，世间万物都是相互连接的，万物之间的“连接”使互联网成为当前社会变革的驱动力，进而影响人类未来发展的方式。\n" +
            "       Q市“爱城市”网是全国第一个基于政府开放数据搭建的公共服务平台，也是国内首个智慧城市开放平台，它以市民需求为中心提供更优质、更便捷的服务。该网站设置了“政府服务”“生活服务”“城市声音”“应用中心”“专题服务”五大板块，可为市民提供一站式的综合服务。在公共服务数据的基</label_9>础上，该网站运用云计算、大数据、物联网、移动互联网等新一代信息技术，以人为本，强调用户的参与性和互动、协作，使市民像在购物网站<label_10 seq=\"89\" description=\"论点深刻,思想性强有力\" score=\"5.0\" drawType=\"2\">上买东西一样，方便快捷地获取政务、公共、社会的各种在线服务，内容涉及与市民生活息息相关的便民信息、教育培训、公共事业、交通出行、社区服务、医疗卫生、旅游环境、社会保障等领域。智慧城市服务体系帮助政府机关以及传统服务行业将原有的服务功能和商业模式“移植”到云端，连接了生活的方方面面，改变了人们传统的生活方式。\n" +
            "       N县广坪镇八一中心小学，一个边远山区的小学校园，已实现无线网络全覆盖，师生的活动情况可以实时呈现在校园各处的大屏幕上；老师们教学教研碰到难题可随时通过互联网远程与省内外专家互动交流；所有班级都能利用QQ群空间发送作业、进行学习交流，还能让家长及时掌握学生的在校学习情况。学校刚开始没有专业</label_10>的足球教练，通过互联网下载视频资源开展足球课教学，学校男女足球队在全县比赛中都进入前三名，后来被教育部命名为全国首批青少年“校园足球特色学校”。该校的罗校长说：<label_11 seq=\"11\" description=\"论点完整,结构强有力\" score=\"5.0\" drawType=\"2\">“是互联网让山区的教育和城镇站在了同一起跑线上，我们农村的孩子也同样享受到了信</label_11>息化带来的好处！”\n" +
            "       在相对偏远落后的地区，互联网的连接能够让信息自由流通、方便获取，让偏远地区<label_12 seq=\"32\" description=\"论点深刻,结构有文采\" score=\"3.0\" drawType=\"0\">在经济、文化、教育等各个方面发生彻底改变。互联网作为基础设施，像水和电一样融入生活、融入各个领域，带来生产力的大幅提升，让各个行业焕发生机。对于中国来说，互联网的连接作用是中国持续发展的重要动力之一。国家已出台了“互联网+”行动计划以及《中国制造2025》，这些都将把实现“中国梦”带入全新的数字时代，让移动互联网、云计算、大数据、物联网与现代制造相结合，创造新的消费需求，刺</label_12>激社会经济的发展，提高中国经济的竞争力，促进中国经济的跨<label_13 seq=\"13\" description=\"论点流畅,论据有文采\" score=\"5.0\" drawType=\"0\">越式发展。</label_13>";
*/




    public void initContent(String singleBean) {

        if (TextUtils.isEmpty(singleBean) || "null".equals(singleBean)) {
            this.setText("本题未做答");
            return;
        }

        ArrayList<Integer> singleChar = new ArrayList<>();              // 黄底纹
        ArrayList<Integer> multChar = new ArrayList<>();                // 红底纹
        ArrayList<UnderLine> singleLines = new ArrayList<>();           // 记录划线的位置 开始 结束 分数

        String spannedx = formatStringV3(singleBean, singleLines);
        final CusNumAlignText cusAlignText = new CusNumAlignText(this);
        final CusNumCheckDetailTag mDrawLine = new CusNumCheckDetailTag(cusAlignText);

        if (mSelectableTextHelperD == null) {
            mSelectableTextHelperD = new SelectableTextHelper.Builder(this)
                    .setSelectedColor(ResourceUtils.getColor(R.color.essay_sel_color))
                    .setCursorHandleSizeInDp(10)
                    .setShowType(SelectableTextHelper.SHOW_TYPE_ONLY_COPY)
                    .setCursorHandleColor(ResourceUtils.getColor(R.color.main_color))
                    .build();
        }
        mSelectableTextHelperD.setSelectListener(new OnSelectListener() {
            @Override
            public void onTextSelected(CharSequence content) {
                // 如果选择复制，清除其他的选择
                if (null != mOnTextSelectListener) {
                    mOnTextSelectListener.clearView();
                }
            }

            @Override
            public void updateView(int type) {
                if (cusAlignText != null) {
                    if (type == 0) {
                        cusAlignText.setSelectableTextHelper(null);
                    } else if (type == 1) {
                        cusAlignText.setSelectableTextHelper(null);
                    } else {
                        cusAlignText.setSelectableTextHelper(mSelectableTextHelperD.mSelectionInfo);
                    }
                    cusAlignText.invalidate();
                }
            }
        });
        cusAlignText.setSelectableTextHelper(mSelectableTextHelperD.mSelectionInfo);
        mDrawLine.setUnderLine(singleLines, mMultLines, singleChar, multChar);
        this.setmCusDraw(cusAlignText);

        String input = spannedx.toString();
        this.setTextImgTagEssay(input);               // 是否有图片标签
        this.setText(input);
        if (TextUtils.isEmpty(input)) {
            this.setHighlightColor(getResources().getColor(android.R.color.transparent));
        }
    }

    static final Pattern tagPattern = Pattern.compile("<[^>/]+>");//只匹配开始标签
    //通过读取属性，取加分项
    public static ArrayList<CheckDetailBean.ScoreListEntity> getAddScoreList(String correctContent) {
        if (TextUtils.isEmpty(correctContent)) return new ArrayList<>();

        //String testStr="<label_1 seq=\"1\" description=\"文采文采不优美(丑)要点准确(不准确)\" score=\"0.0\" drawType=\"0\"><label_2 seq=\"2\" description=\"要点不准确\" score=\"0.0\" drawType=\"0\"><label_3 seq=\"3\" description=\"文采文采斐然(美)\" score=\"0.0\" drawType=\"0\">";
        Matcher matcher = tagPattern.matcher(correctContent);
        ArrayList<CheckDetailBean.ScoreListEntity> tagBeans = new ArrayList<>();
        boolean hasFind = false;
        while (matcher.find()) {

            String content = matcher.group();
            CheckDetailBean.ScoreListEntity bean = new CheckDetailBean.ScoreListEntity();

            Matcher attribmatcher = EssayExerciseTextView.ATTRIBPATTERN.matcher(content);
            hasFind = false;
            while (attribmatcher.find()) {
                hasFind = true;
                // LogUtils.e("attribmatcher", attribmatcher.group(1)+","+ attribmatcher.group(2));
                String attrName = attribmatcher.group(1);
                String attrValue = attribmatcher.group(2);
                attrValue = TextUtils.isEmpty(attrValue) ? attrValue : attrValue.replace(">", "").replaceAll("\"", "");
                if ("seq".equals(attrName)) {
                    bean.sequenceNumber = StringUtils.parseInt(attrValue);

                } else if ("description".equals(attrName)) {
                    bean.scorePoint = attrValue;
                } else if ("score".equals(attrName)) {
                    bean.score = StringUtils.parseDouble(attrValue);
                }
            }
            if (hasFind)
                tagBeans.add(bean);
        }
        return tagBeans;
    }

    private final static Pattern ENDTAGPATTERN = Pattern.compile("</([^>]*)>");
    public final static Pattern ATTRIBPATTERN = Pattern.compile("(seq|description|score|drawType)" +  "=\\s*\"([^\"]+)\"");//"=\\s*([^\\s]+)"

     //以科学技术推动大健康\n   "修正如此行文字显示问题
    private final static Pattern ENDBLANK_PATTERN = Pattern.compile("([^\\n]+)(\\n\\s*)");

    private String formatStringV3(String singleBean, ArrayList<UnderLine> singleLines) {

        String str = singleBean;
        Matcher tagMatcher = ENDTAGPATTERN.matcher(str);
        HashSet<String> tmpListTag = new HashSet<>();
        while (tagMatcher.find()) {
            String tag = tagMatcher.group(1);
            tag.replace(" ", "");

            // p标签或者长度小于3的标签都忽略
            if ("p".equals(tag) || tag.length() < 3) continue;
            tmpListTag.add(tag);
        }
        if (tmpListTag.size() <= 0) {
            return str;
        }
        StringBuffer tagBuffer = new StringBuffer(200);

        int j = 0;
        for (String tagbean : tmpListTag) {
            tagBuffer.append("<" + tagbean + ".*?>|</" + tagbean + ">");
            if (j < tmpListTag.size() - 1) {
                tagBuffer.append("|");
            }
            j++;
        }
        String matchAllStr = tagBuffer.toString();
        Pattern pattern = Pattern.compile(matchAllStr);
        Matcher matcher = pattern.matcher(str);
        ArrayList<TagBean> tagBeans = new ArrayList<>();
        while (matcher.find()) {

            int start = matcher.start();
            int end = matcher.end();
            String content = matcher.group();
            TagBean bean = new TagBean();
            bean.isStart = !content.startsWith("</");
            bean.tag = bean.isStart ?
                    content.split(" ")[0].replace("<", "").replace(">", "") : (
                    content.contains(" ") ? content.split(" ")[1].replace(">", "")
                            : content.replace("</", "").replace(">", ""));
            bean.content = content;
            bean.start = start;
            bean.end = end;
            tagBeans.add(bean);
        }

        // 这里操作变量，得到最后去标签的内容
        StringBuffer buffer = new StringBuffer(str);

        // 存储临时未闭合的标签变量
        LinkedList<TagBean> tempTag = new LinkedList<>();
        // 存储闭合的解析
        ArrayList<UnderLine> lines = singleLines;

        int drawType = 0;
        // 从后往前遍历，为了容易计算位置
        for (int i = tagBeans.size() - 1; i >= 0; i--) {
            TagBean bean = tagBeans.get(i);
            // 还要去除buffer中的标签
            buffer.replace(bean.start, bean.end, "");
            if (bean.isStart) { // 开始，去寻找对应的就近的结束标签，然后解析变量，完成配对，放入lines
                TagBean endTag = findEndTag(tempTag, bean);
                if (endTag == null) continue;//忽略没有结束的标签

                int realStart = buffer.length() - bean.start;
                UnderLine line = new UnderLine();
                line.start = realStart;
                line.end = endTag.realEnd;

                Matcher attribmatcher = ATTRIBPATTERN.matcher(bean.content);
                drawType = 0;
                while (attribmatcher.find()) {
                    // LogUtils.e("attribmatcher", attribmatcher.group(1)+","+ attribmatcher.group(2));
                    String attrName = attribmatcher.group(1);
                    String attrValue = attribmatcher.group(2);
                    attrValue = TextUtils.isEmpty(attrValue) ? attrValue : attrValue.replace(">", "").replaceAll("\"", "");
                    if ("seq".equals(attrName)) {
                        line.seq = StringUtils.parseInt(attrValue);
                        if (line.seq < 0) {
                            line.seq = 0;
                        }
                    } else if ("description".equals(attrName)) {
                        line.addscore = attrValue;
                    } else if ("score".equals(attrName)) {
                        line.score = attrValue;
                    } else if ("drawType".equalsIgnoreCase(attrName)) {
                        drawType = StringUtils.parseInt(attrValue);
                    }
                }

                //三种标签高亮显示
               /* if(bean.tag.equals("sentenceScore")||bean.tag.equals("literaryScore")||bean.tag.equals("thoughtScore")){
                    line.seq=MeasureSpec.makeMeasureSpec(line.seq, MeasureSpec.EXACTLY);//高位存类型，低位存真实的序号
                }*/
                if (drawType == 1) {//drawType=0,画线，1背景，2两个
                    line.seq = MeasureSpec.makeMeasureSpec(line.seq, MeasureSpec.EXACTLY);//高位存类型，低位存真实的序号
                } else if (drawType == 2) {
                    line.seq = MeasureSpec.makeMeasureSpec(line.seq, MeasureSpec.AT_MOST);
                }
                lines.add(line);
            } else {            // 如果是结束就直接存储进tempTag，因为只有开始标签才能和结束标签闭合
                // 真实倒数位置    相对尾部的位置，
                bean.realEnd = buffer.length() - bean.start;
                tempTag.add(0, bean);
            }
        }

        // boolean checkSeq=true;//是否需要重新赋序号
        int linesCount = ArrayUtils.size(lines);
        // 重新计算位置
        for (UnderLine line : lines) {
            line.start = buffer.length() - line.start;
            line.end = buffer.length() - line.end;

            /*if(checkSeq){
                int lightMode=  MeasureSpec.getMode(line.seq);
                line.seq=MeasureSpec.makeMeasureSpec(linesCount,lightMode);
                if(linesCount>1)
                   linesCount--;
            }*/
            if (line.end > (line.start + 2)) {
               /* String endChar = buffer.substring(line.end - 1, line.end);
                //修正一下结尾位置，避免画的过长
                if (endChar.equals("，") || endChar.equals("。") || endChar.equals(",") || endChar.equals(".")||endChar.equals("\n")) {
                    line.end--;
                }*/

                int minlen=Math.min(8,line.end-line.start);
                String endChar=buffer.substring(line.end-minlen,line.end);
                //修正一下结尾位置，避免画的过长
                if(endChar.endsWith("，")||endChar.endsWith("。")||endChar.endsWith(",")||endChar.endsWith(".")){
                    line.end--;
                }else{
                    Matcher blankmatcher= ENDBLANK_PATTERN.matcher(endChar);
                    if(blankmatcher.find()){
                        String blankend=blankmatcher.group(2);
                        line.end=line.end-blankend.length();
                    }
                }

            }
        }
        Collections.reverse(lines);
        return buffer.toString();
    }


    //str="\n   " "   "
    private static TagBean findEndTag(LinkedList<TagBean> tempTag, TagBean bean) {
        for (TagBean tagBean : tempTag) {
            if (tagBean.tag.equals(bean.tag)) {
                tempTag.remove(tagBean);
                return tagBean;
            }
        }
        return null;
    }
}
