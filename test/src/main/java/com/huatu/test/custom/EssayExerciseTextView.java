package com.huatu.test.custom;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;


import com.huatu.test.ArrayUtils;
import com.huatu.test.LogUtils;
import com.huatu.test.R;
import com.huatu.test.StringUtils;
import com.huatu.test.bean.CheckDetailBean;
import com.huatu.test.bean.TagBean;
import com.huatu.test.bean.UnderLine;
import com.huatu.test.drawimpl.CusNumAlignText;
import com.huatu.test.drawimpl.CusNumCheckDetailTag;
import com.huatu.test.drawimpl.EssayHelper;
import com.huatu.test.drawimpl.XmlSimpleHandler;
import com.huatu.test.drawimpl.XmlSimpleV3Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
    public void setOnTextSelectListener(OnTextSelectListener textSelectListener){
         this.mOnTextSelectListener=textSelectListener;
    }

 /*   @Override
    public void clearView() {
        super.clearView();
        if (mSelectableTextHelperD != null) {
            mSelectableTextHelperD.clearView();
        }
    }*/

   // private SelectableTextHelper mSelectableTextHelperD;

    public EssayExerciseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EssayExerciseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

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

       /*  if(mSelectableTextHelperD==null){
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
                if(null!=mOnTextSelectListener){
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
        cusAlignText.setSelectableTextHelper(mSelectableTextHelperD.mSelectionInfo); */
        mDrawLine.setUnderLine(singleLines, multLines, singleChar, multChar);
        this.setmCusDraw(cusAlignText);
        this.setTextImgTagEssay(input);               // 是否有图片标签
        this.setText(input);
        if (TextUtils.isEmpty(input)) {
            this.setHighlightColor(getResources().getColor(android.R.color.transparent));
        }
    }



    public static String TESTSTRING="<titleScore        value=3 seq=-1 describe=“描述语言” score=3 >科技人性化之路</titleScore>\n" +
            "\n" +
            "       科技是国家强盛之基，科技创新是提高社会生产力和综合国力的战略支撑，我国坚定不移地走科技强国之路。创新驱动发展战略的实施，使未来科技的发展方向走向人性化之路。<thesisScore value=4 id=152582 seq=-1 describe=“描述语言” score= >许多高科技产品被赋予灵动的生命，但事实上科技是一种生命，具有生命的普遍特征。</thesisScore>\n" +
            "       <thesisScore value=4 id=152583 seq=-1 describe=“描述语言” score= >科技发展的本质是为人民服务，不能忽略民众的精神力量。</thesisScore><evidenceScore value=3-152583 seq=-1 describe=“描述语言” score= >著名学者刘易斯•芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为“<literaryScore value=1-10 seq=-1 describe=“描述语言” score= >无目的的物质至上主义</literaryScore>”，科技发展突飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。但是科技发展的过程，人民群众的精神力量尤为重要。因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。</evidenceScore>\n" +
            "      <thesisScore value=4 id=152585 seq=-1 describe=“描述语言” score= > 科技是一种生命，坚持走科技人性化之路。</thesisScore>x人才队伍的建设促进了高科技的发展，科研人员卓越的专业技能使高科技产品被赋予了生命，使科技和人性之间嫁接了桥梁，，人性化之路将赋予高科技以新的价值观。比如，电子产品的更新换代，智能手机的出现满足了人们的一些需求，秀才不出门，便知天下事，拥有一部手机可以方便人们之间的交流，随时随地关住国家大事和热点新闻X。<evidenceScore value=3-152585 seq=-1 describe=“描述语言” score= >但是长时间使用电子产品对人们身体健康不好，尤其是眼睛，高科技发展的时代，电子产品创造了护眼模式，一方面满足了人们的需求，另一方面，对人体身心伤害小，所以科技发展需要具备人性化的要求。</evidenceScore>\n" +
            "       <thesisScore value=3-0(其他) id=152588 seq=-1 describe=“描述语言” score= >科技发展与人性化之间追求一种平衡，只有将科技和人文两者紧密联系在一起，才能体现出科技之美，</thesisScore><evidenceScore value=3-152588 seq=-1 describe=“描述语言” score= >我国科研人才队伍的独特的专业技能，高尚的道德修养，同时遵守了职业道德的基本准则，更好地为人民服务，为把我国建设成现代化强国贡献力量。国与国之间的竞争实质是人才竞争，只有强大的科技人才队伍，我国的科技才能居世界前列，科技水平提高了，科技产品才具有创新性，同时，人性化的科技才能为人类创造出科技成就和物质财富，所以科技发展要坚持人性化原则，实现科技与人类和谐共生。</evidenceScore>\n" +
            "      <thoughtScore value=2 seq=-1 describe=“描述语言” score= > 互联网时代，每一项科技成果都应该以人为本，人民至上，只有体现人性化的科技成果，才能造福于人类，为人类社会科技和谐发展奠定基石。</thoughtScore><structScore           value=1 seq=-1 describe=“描述语言” score= >只有在科技化与人性化之间取得了平衡，才体现了一种智慧和态度。</structScore>";




    public static String TESTSTRING9="1.“连接”，又称联接，指事物互相衔接。“连接”成为热词，源于互联网<label_1 seq=\"1\" description=\"论点准确,结构深刻\" score=\"4.0\" drawType=\"0\">信息技术以独特的勾连方式，将原本散落在世界各个角落的、不同肤色、不同信仰的人连接起来，构成网络社会。因“连接”</label_1>而产<label_2 seq=\"2\" description=\"论据深刻\" score=\"4.0\" drawType=\"0\"><label_3 seq=\"3\" description=\"论点强有力\" score=\"0.0\" drawType=\"0\">生的各种社会系统，重构世界、重构人类社会，导致人类社会进入了一个全新的人机信息勾连</label_4></label_2><label_4 seq=\"4\" description=\"论点完整\" score=\"4.0\" drawType=\"0\"><label_5 seq=\"5\" description=\"文采强有力\" score=\"2.0\" drawType=\"1\">的社</label_5></label_3>会<label_6 seq=\"6\" description=\"论据准确,论点完整\" score=\"5.0\" drawType=\"0\">新纪</label_6>元。<label_7 seq=\"7\" description=\"论点流畅,结构准确\" score=\"6.0\" drawType=\"0\">互联网连接过去、现在和未来，影响范围越来越大。成长于互联网蓬勃发展时期的“90后”，他们</label_7>有自己的文化、语言和价值体系<label_8 seq=\"8\" description=\"论据深刻,论点流畅\" score=\"3.0\" drawType=\"0\">，他们的成长由互联网全程参</label_8>与，他<label_9 seq=\"9\" description=\"论点流畅,句子强有力\" score=\"4.0\" drawType=\"2\">们相信一切都是相互连接的，不光是人与人相连接，世间万物都是相互连接的，万物之间的“连接”使互联网成为当前社会变革的驱动力，进而影响人类未来发展的方式。\n" +
            "       Q市“爱城市”网是全国第一个基于政府开放数据搭建的公共服务平台，也是国内首个智慧城市开放平台，它以市民需求为中心提供更优质、更便捷的服务。该网站设置了“政府服务”“生活服务”“城市声音”“应用中心”“专题服务”五大板块，可为市民提供一站式的综合服务。在公共服务数据的基</label_9>础上，该网站运用云计算、大数据、物联网、移动互联网等新一代信息技术，以人为本，强调用户的参与性和互动、协作，使市民像在购物网站<label_10 seq=\"10\" description=\"论点深刻,思想性强有力\" score=\"5.0\" drawType=\"2\">上买东西一样，方便快捷地获取政务、公共、社会的各种在线服务，内容涉及与市民生活息息相关的便民信息、教育培训、公共事业、交通出行、社区服务、医疗卫生、旅游环境、社会保障等领域。智慧城市服务体系帮助政府机关以及传统服务行业将原有的服务功能和商业模式“移植”到云端，连接了生活的方方面面，改变了人们传统的生活方式。\n" +
            "       N县广坪镇八一中心小学，一个边远山区的小学校园，已实现无线网络全覆盖，师生的活动情况可以实时呈现在校园各处的大屏幕上；老师们教学教研碰到难题可随时通过互联网远程与省内外专家互动交流；所有班级都能利用QQ群空间发送作业、进行学习交流，还能让家长及时掌握学生的在校学习情况。学校刚开始没有专业</label_10>的足球教练，通过互联网下载视频资源开展足球课教学，学校男女足球队在全县比赛中都进入前三名，后来被教育部命名为全国首批青少年“校园足球特色学校”。该校的罗校长说：<label_11 seq=\"11\" description=\"论点完整,结构强有力\" score=\"5.0\" drawType=\"0\">“是互联网让山区的教育和城镇站在了同一起跑线上，我们农村的孩子也同样享受到了信</label_11>息化带来的好处！”\n" +
            "       在相对偏远落后的地区，互联网的连接能够让信息自由流通、方便获取，让偏远地区<label_12 seq=\"12\" description=\"论点深刻,结构有文采\" score=\"3.0\" drawType=\"0\">在经济、文化、教育等各个方面发生彻底改变。互联网作为基础设施，像水和电一样融入生活、融入各个领域，带来生产力的大幅提升，让各个行业焕发生机。对于中国来说，互联网的连接作用是中国持续发展的重要动力之一。国家已出台了“互联网+”行动计划以及《中国制造2025》，这些都将把实现“中国梦”带入全新的数字时代，让移动互联网、云计算、大数据、物联网与现代制造相结合，创造新的消费需求，刺</label_12>激社会经济的发展，提高中国经济的竞争力，促进中国经济的跨<label_13 seq=\"13\" description=\"论点流畅,论据有文采\" score=\"5.0\" drawType=\"0\">越式发展。</label_13>";
    //嵌套
    public static String TESTSTRING2= "&#160;<titleScore value=3 seq=-1 content=“描述语言” score= >科技人性化之路</titleScore>\n"+
            "\n"+"<evidenceScore value=3-152583 seq=-1 content=“描述语言” score= >著名学者刘易斯•芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为“<literaryScore value=1-10 seq=-1 content=“描述语言” score= >无目的的物质至上主义</literaryScore>”，科技发展突飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。但是科技发展的过程，人民群众的精神力量尤为重要。因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。</evidenceScore>\n" +
            "      <thesisScore value=4 id=152585 seq=-1 content=“描述语言” score= > 科技是一种生命，坚持走科技人性化之路。</thesisScore><evidenceScore value=3-152585 seq=-1 content=“描述语言” score= >人才队伍的建设促进了高科技的发展，科研人员卓越的专业技能使高科技产品被赋予了生命，使科技和人性之间嫁接了桥梁，，人性化之路将赋予高科技以新的价值观。比如，电子产品的更新换代，智能手机的出现满足了人们的一些需求，秀才不出门，便知天下事，拥有一部手机可以方便人们之间的交流，随时随地关住国家大事和热点新闻。</evidenceScore><evidenceScore value=3-152585 seq=-1 content=“描述语言” score= >但是长时间使用电子产品对人们身体健康不好，尤其是眼睛，高科技发展的时代，电子产品创造了护眼模式，一方面满足了人们的需求，另一方面，对人体身心伤害小，所以科技发展需要具备人性化的要求。</evidenceScore>\n" +
            "       <thesisScore value=3-0(其他) id=152588 seq=-1 content=“描述语言” score= >科技发展与人性化之间追求一种平衡，只有将科技和人文两者紧密联系在一起，才能体现出科技之美，</thesisScore><evidenceScore value=3-152588 seq=-1 content=“描述语言” score= >我国科研人才队伍的独特的专业技能，高尚的道德修养，同时遵守了职业道德的基本准则，更好地为人民服务，为把我国建设成现代化强国贡献力量。国与国之间的竞争实质是人才竞争，只有强大的科技人才队伍，我国的科技才能居世界前列，科技水平提高了，科技产品才具有创新性，同时，人性化的科技才能为人类创造出科技成就和物质财富，所以科技发展要坚持人性化原则，实现科技与人类和谐共生。</evidenceScore>\n" +
            "      <thoughtScore value=2 seq=-1 content=“描述语言” score= > 互联网时代，每一项科技成果都应该以人为本，人民至上，只有体现人性化的科技成果，才能造福于人类，为人类社会科技和谐发展奠定基石。</thoughtScore><structScore value=1 seq=-1 content=“描述语言” score= >只有在科技化与人性化之间取得了平衡，才体现了一种智慧和态度。</structScore>\n";


    //  交叉嵌套
    public static String TESTSTRING3="<thesisScore value=4 id=152583 seq=-1 describe=“描述语言” score= >科技发展的本质是为人民服务，不能忽略民众的精神力量。</thesisScore><evidenceScore value=3-152583 seq=-1 describe=“描述语言” score= >著名学者刘易斯·芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为“<literaryScore value=1-10 seq=-1 describe=“描述语言” score= >无目的的物质至上主义”，科技发展突飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。</evidenceScore>但是科技发展的过程，人民群众的精神力量尤为重要。</literaryScore>因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。";


    //  多重嵌套
    public static String TESTSTRING4="&#160;<evidenceScore value=3-152583 seq=-1 describe=“描述语言1” score= >著名学者刘易斯•芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为无目的的物质至上主义”，科技发展突<literaryScore value=1-10 seq=-1 describe=“描述语言3” score= >无目的的<literaryScore value=1-10 seq=-1 describe=“描述语言4” score= >无目的的物质至上主义</literaryScore>物质至上主义</literaryScore>飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。但是科技发展的过程，人民群众的精神力量尤为重要。因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。</evidenceScore>";




    public static String TESTSTRING5="&#160;<evidenceScore value=3-152583 seq=-1 describe=“描述语言1” score= >著名学者刘易斯•芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为“<literaryScore value=1-10 seq=-1 describe=“描述语言2” score= >无目的的物质至上主义</literaryScore>”，科技发展突<literaryScore1 value=1-10 seq=-1 describe=“描述语言” score= >无目的的<literaryScore2 value=1-10 seq=-1 describe=“描述语言” score= >无目的的物质至上主义</literaryScore2>物质至上主义</literaryScore1>飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。但是科技发展的过程，人民群众的精神力量尤为重要。因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。</evidenceScore>";



    // public static String TESTSTRING3= "&#160;<evidenceScore value=3-152583 seq=-1 content=“描述语言” score= >著名学者刘易斯•芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为“<literaryScore value=1-10 seq=-1 content=“描述语言” score= >无目的的物质至上主义</literaryScore>”，科技发展突飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。但是科技发展的过程，人民群众的精神力量尤为重要。因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。</evidenceScore>\n";
  /*  public static String TESTSTRING="科技是国家强盛之基，科技创新是提高社会生产力和综合国力的战略支撑，我国坚定不移地走科技强国之路。创新驱动发展战略的实施，使未来科技的发展方向走向人性化之路。<thesisScore value=4 id=152582 seq=-1 describe=“描述语言” score= >许多高科技产品被赋予灵动的生命，但事实上科技是一种生命，具有生命的普遍特征。</thesisScore>\n"+
        "       <thesisScore value=4 id=152583 seq=-1 describe=“描述语言” score= >科技发展的本质是为人民服务，不能忽略民众的精神力量。</thesisScore><evidenceScore value=3-152583 seq=-1 describe=“描述语言” score= >著名学者刘易斯·芒福德指出：为了获得更多、更丰富的物质，人们牺牲了时间和当前的快乐，只是将幸福简单地与拥有机械产品的数量划上等号，称之为“<literaryScore value=1-10 seq=-1 describe=“描述语言” score= >无目的的物质至上主义”，科技发展突飞猛进，而忽略和遮蔽了人类的个性。人民群众是历史的创造者，科技发展最大的受益者是人民群众，提高了人民的生活质量。</evidenceScore>但是科技发展的过程，人民群众的精神力量尤为重要。</literaryScore>因此。科技发展的过程应该以人民为主，人民至上，一切的出发点和落脚点都为了人民，所以不能忽略人民群众的精神力量。\n"+
        "      <thesisScore value=4 id=152585 seq=-1 describe=“描述语言” score= > 科技是一种生命，坚持走科技人性化之路。</thesisScore><evidenceScore value=3-152585 seq=-1 describe=“描述语言” score= >人才队伍的建设促进了高科技的发展，科研人员卓越的专业技能使高科技产品被赋予了生命，使科技和人性之间嫁接了桥梁，，人性化之路将赋予高科技以新的价值观。比如，电子产品的更新换代，智能手机的出现满足了人们的一些需求，秀才不出门，便知天下事，拥有一部手机可以方便人们之间的交流，随时随地关住国家大事和热点新闻。</evidenceScore><evidenceScore value=3-152585 seq=-1 describe=“描述语言” score= >但是长时间使用电子产品对人们身体健康不好，尤其是眼睛，高科技发展的时代，电子产品创造了护眼模式，一方面满足了人们的需求，另一方面，对人体身心伤害小，所以科技发展需要具备人性化的要求。</evidenceScore>\n"+
        "       <thesisScore value=3-0(其他) id=152588 seq=-1 describe=“描述语言” score= >科技发展与人性化之间追求一种平衡，只有将科技和人文两者紧密联系在一起，才能体现出科技之美，</thesisScore><evidenceScore value=3-152588 seq=-1 describe=“描述语言” score= >我国科研人才队伍的独特的专业技能，高尚的道德修养，同时遵守了职业道德的基本准则，更好地为人民服务，为把我国建设成现代化强国贡献力量。国与国之间的竞争实质是人才竞争，只有强大的科技人才队伍，我国的科技才能居世界前列，科技水平提高了，科技产品才具有创新性，同时，人性化的科技才能为人类创造出科技成就和物质财富，所以科技发展要坚持人性化原则，实现科技与人类和谐共生。</evidenceScore>\n"+
        "      <thoughtScore value=2 seq=-1 describe=“描述语言” score= > 互联网时代，每一项科技成果都应该以人为本，人民至上，只有体现人性化的科技成果，才能造福于人类，为人类社会科技和谐发展奠定基石。</thoughtScore><structScore value=1 seq=-1 describe=“描述语言” score= >只有在科技化与人性化之间取得了平衡，才体现了一种智慧和态度。</structScore>";
 */   /*public static String replaceHtmlTag(String str, String tag, String tagAttrib, String startTag, String endTag, HashMap<String, OssPicInfo> imgUrlMap) {
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

                  *//*  //"data-ratio=\"0.58203125\"  data-w=\"1280\""
                    float tmpRatio = ((float) imgUrlMap.get(attributeStr).height) / imgUrlMap.get(attributeStr).width;
                    endTag = "\" data-ratio=\"" + String.valueOf(tmpRatio) + "\"  data-w=\"" + imgUrlMap.get(attributeStr).width + "\" ";*//*
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
*/

    public void formatContent(){


        HashMap<String, StringUtils.OssPicInfo> picMap=new HashMap();

        StringUtils.OssPicInfo tmpInfo=new StringUtils.OssPicInfo();
        tmpInfo.width=300;
        tmpInfo.height=400;
        tmpInfo.size=100;

        picMap.put("uploads/allimg/160424/1-160424120T1-50.jpg",tmpInfo);
        StringBuffer content = new StringBuffer();
        content.append("<ul class=\"imgBox\"><li><img id=\"160424\" src=\"uploads/allimg/160424/1-160424120T1-50.jpg\" class=\"src_class\"></li>");
        content.append("<li><img id=\"150628\" src=\"uploads/allimg/150628/1-15062Q12247.jpg\" class=\"src_class\"></li></ul>");
        System.out.println("原始字符串为:"+content.toString());
        String newStr = StringUtils.replaceHtmlTag(content.toString(), "img", "src", "src=\"uploads/allimg/160424/1-160424120T1-50.jpg", "\"",picMap);
        System.out.println("       替换后为:"+newStr);

    }


    //交叉嵌套有问题，
    private Spanned fomatString(String singleBean,ArrayList<UnderLine> singleLines){


        String tagAttrib = "(\\d+.\\d+|\\w+)";

        // String regxpForTagAttrib = tagAttrib + "=\\s*\"([^\"]+)\"";
        Pattern patternForTag = Pattern.compile(regxpForHtml, Pattern.CASE_INSENSITIVE);
        String regxpForTagAttrib = tagAttrib + "=\\s*([^\\s+]+)";  //  \s 空格  ^\w{0,10}$
        // String regxpForTagAttrib = tagAttrib + "=\\s*(^\\w{0,10}$)";
        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib, Pattern.CASE_INSENSITIVE);

        String blankForTagAttrib =   "([\\s])";
        Pattern blankForAttrib = Pattern.compile(blankForTagAttrib, Pattern.CASE_INSENSITIVE);
        // String[] tmpList=singleBean.split(regxpForHtml);
        Matcher matcherForTag = patternForTag.matcher(singleBean);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();

        while (result) {
            StringBuffer sbreplace = new StringBuffer("<");
           /* Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group(1));
            while (matcherForAttrib.find()) {
                String attributeName = matcherForAttrib.group(1);
                String attributeValue = matcherForAttrib.group(2);
                matcherForAttrib.appendReplacement(sbreplace,attributeName+"="+ "\""+attributeValue.replaceAll("“","").replaceAll("”","")+ "\"");
            }*/

            Matcher matcherForAttrib = blankForAttrib.matcher(matcherForTag.group(1));
            while (matcherForAttrib.find()) {
                String attributeName = matcherForAttrib.group(1);

                matcherForAttrib.appendReplacement(sbreplace,"л");
            }
            matcherForAttrib.appendTail(sbreplace);
            sbreplace.append(">");
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);

        String result22=sb.toString();
        singleBean= result22.replaceAll(" ","и").replaceAll("л"," ").replaceAll("\n","<br>");


        Spanned spannedx;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            spannedx = Html.fromHtml(singleBean, Html.FROM_HTML_MODE_COMPACT, null, new XmlSimpleV3Handler(singleLines));
        } else {
            spannedx = Html.fromHtml(singleBean, null, new XmlSimpleHandler(singleLines));
        }
        return spannedx;

    }


     //div的多重嵌套
    //https://www.cnblogs.com/KevinYang/archive/2010/07/30/1788366.html
    public static List<String> getTagContent(String source, String element) {
        HashMap<String,Integer> keyWordlist=new HashMap<>();//记录关键字出现的的次数
        List<String> result = new ArrayList<String>();
        String reg = "<" + element + "([^>]*)>" + "(.+?)</" + element + ">";
        Matcher m = Pattern.compile(reg).matcher(source);
        while (m.find()) {
            String r = m.group(1);
            result.add(r);

            String keyword=StringUtils.delHTMLTag(m.group(2));
            Integer value=1;
            if(keyWordlist.containsKey(keyword)){
                value=  keyWordlist.get(keyword);
                keyWordlist.put(keyword,value+1);
            }else {
                keyWordlist.put(keyword,value);
            }
            result.add(keyword);
            result.add(String.valueOf(value));
        }
        return result;
    }

    private static TagBean findEndTag(LinkedList<TagBean> tempTag, TagBean bean) {
        for (TagBean tagBean : tempTag) {
            if (tagBean.tag.equals(bean.tag)) {
                tempTag.remove(tagBean);
                return tagBean;
            }
        }
        return null;
    }

    /*        String match = "<titleScore.*?>|<evidenceScore.*?>|<literaryScore.*?>|<thesisScore.*?>|<thoughtScore.*?>|<structScore.*?>" +
                         "|</structScore>|</thoughtScore>|</thesisScore>|</literaryScore>|</evidenceScore>|</titleScore>";//thesisScore   thoughtScore structScore

  */


    public static String delHTMLTag(String htmlStr) {
        //String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        //String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+p>"; //定义HTML标签的正则表达式

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
    private final static Pattern ENDTAGPATTERN = Pattern.compile("</([^>]*)>");
    private final static Pattern ATTRIBPATTERN = Pattern.compile("(seq|description|score|drawType)" + "=\\s*([^\\s]+)");
    private final static Pattern ENDBlankTAGPATTERN = Pattern.compile("([^\\n]+)(\\n\\s*)");

    public final static String StringTest= "<label_1 seq=\"1\" description=\"标题切题 \" score=\"0.0\" drawType=\"0\">以科学技术推动大健康\n   </label_1>    一个人无病无痛、乐观开朗，是身心健康；人与人之间平等友善、和睦相处，是关系健康；人与自然之间和谐共生、相互促进，是生态健康……<label_2 seq=\"2\" description=\"论点准确\" score=\"0.0\" drawType=\"0\">健康包括方方面面，因此“大健康”的概念应运而生。</label_2>然而，处于信息社会，要解决这些以人为核心，涉及民生、生态、经济等方面的“大健康”问题，<label_3 seq=\"3\" description=\"论点准确\" score=\"0.0\" drawType=\"0\">最根本的是要依靠科学技术，以科学技术的力量推动大健康发展。</label_3>\n      <label_4 seq=\"4\" description=\"论点准确\" score=\"0.0\" drawType=\"0\"> 从科技最本质的特征来看，科技的研发与进步能够解决健康难题，减少威胁健康的因素</label_4>。<label_5 seq=\"5\" description=\"论证强有力\" score=\"0.0\" drawType=\"0\">拿医药领域来说，生命医学药学技术的进步研发新药物、更新治疗方式，会使得更多的疾病得到预防和治疗，从而减少乃至消除人类痛苦、保障健康。疟疾曾被称为最具伤害性的寄生虫病，并发症多、传染性强，每年世界上数百万人因之死亡。而我国药学家屠呦呦发现的青蒿素为人类抗击疟疾的战斗提供了“有效武器”，它能够有效降低疟疾患者的死亡率，也因此让她荣获诺贝尔医学奖。</label_5><label_6 seq=\"6\" description=\"论证比较有力\" score=\"0.0\" drawType=\"0\">当然，从更大范围来看，如何让农药化肥的使用减少对水土的污染与破坏，何以升级产业以减少污染物排放，又怎样以更高水平检测食品安全等等问题，最终都要依靠科技力量来解决。“科学技术是第一生产力”是历史铁律。\n</label_6>       “<label_7 seq=\"7\" description=\"论点 比较准确\" score=\"0.0\" drawType=\"0\">科技向人类对健康的最大需求发展”，不仅体现在科技能够直接解决健康问题，还体现在服务健康的技术方面，如信息技术、大数据。</label_7><label_8 seq=\"8\" description=\"论证强有力\" score=\"0.0\" drawType=\"0\">毫无疑问，当下信息技术、大数据大大提高了社会运作效率，已经渗透到了每一个领域，同时，大健康的发展也离不开它们。“智慧医疗”就是一例，它通过物联网技术，打造健康档案医疗信息平台，实现患者与医务人员、医疗机构及设备之间的互动，从而为健康问题提供最全面、及时的信息。而大数据采集、存储和分析技术，通过快速、精准的信息采集以低成本进行存储、处理、分析和共享，能够为慢性病的治疗以及老年人的健康保健提供全方位的信息支撑。可以说，信息快速便捷地汇集与流通，让复杂的健康问题变得更加清晰明了，让问题的解决也有脉络可寻。因此，科技还能护卫大健康。\n      </label_8> 同时，我们在“利用科学技术向生命科学、向人类对健康的最大需求发展”的同时，也要意识到，科技是一把双刃剑。只有赋予科技人性化，科技才会更好地服务人类，推动健康不断完善。因为科技成果不一定全是人类福音，如果科技成果不符合人类需求、科技成果不被正面应用，那么它就是人类灾难。2018年我国南方科技大学副教授贺建奎的宣布（内容为“世界首例基因编辑婴儿在中国诞生”）在今天听来还令人毛骨悚然。这一研究存在很多不确定性，也违反了人类伦理。因此手握利剑的科技研究者们如果不自律，缺少外在的监管与制裁，那么科技就会沦为不健康的帮凶，给人类带来无尽的劫难。所以，科学技术要想真正推动大健康，离不开法律法规以及完善的监管体系的保驾护航。\n       当前，随着国家“创新驱动”和“大健康”概念的提出及践行，科学技术为大健康创造机遇的同时，也不断提出挑战，关键看以怎样的态度对待科技。相信在发展科学技术与规范科学技术的追求中，大健康建设之路将会越走越宽，生活将会更加美好。\n";


    private String formatStringV3(String singleBean,ArrayList<UnderLine> singleLines){

        String str = singleBean;
        Matcher tagMatcher = ENDTAGPATTERN.matcher(str);
        HashSet<String> tmpListTag=new HashSet<>();
        while (tagMatcher.find()){
            String tag = tagMatcher.group(1);
            tag.replace(" ","");

            //p标签或者长度小于3的标签都忽略
            if("p".equals(tag)||tag.length()<3) continue;
            tmpListTag.add(tag);
        }
        if(tmpListTag.size()<=0){
            return str;
        }
        StringBuffer tagBuffer = new StringBuffer(200);

        int j=0;
        for(String tagbean:tmpListTag){
            tagBuffer.append("<"+tagbean+".*?>|</"+tagbean+">");
            if(j<tmpListTag.size()-1){
                tagBuffer.append("|");
            }
            j++;
        }
        String matchAllStr=tagBuffer.toString();
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
                    content.split(" ")[0].replace("<", "").replace(">", "") :(
                    content.contains(" ")?content.split(" ")[1].replace(">", "")
                            :content.replace("</","").replace(">", ""));
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
        ArrayList<UnderLine> lines=singleLines;

        int drawType=0;
        // 从后往前遍历，为了容易计算位置
        for (int i = tagBeans.size() - 1; i >= 0; i--) {
            TagBean bean = tagBeans.get(i);
            // 还要去除buffer中的标签
            buffer.replace(bean.start, bean.end, "");
            if (bean.isStart) { // 开始，去寻找对应的就近的结束标签，然后解析变量，完成配对，放入lines
                TagBean endTag = findEndTag(tempTag, bean);
                if(endTag==null) continue;//忽略没有结束的标签

                int realStart = buffer.length() - bean.start;
                UnderLine line = new UnderLine();
                line.start = realStart;
                line.end = endTag.realEnd;

                Matcher attribmatcher= ATTRIBPATTERN.matcher( bean.content);
                drawType=0;
                while (attribmatcher.find()){
                    // LogUtils.e("attribmatcher", attribmatcher.group(1)+","+ attribmatcher.group(2));
                    String attrName=attribmatcher.group(1);
                    String attrValue=attribmatcher.group(2);
                    attrValue=TextUtils.isEmpty(attrValue)? attrValue:attrValue.replace(">","").replaceAll("\"","");
                    if("seq".equals(attrName)){
                        line.seq= StringUtils.parseInt(attrValue);
                        if(line.seq<0){
                            line.seq=0;
                        }
                    }else if("description".equals(attrName)){
                        line.addscore=attrValue;
                    }else if("score".equals(attrName)){
                        line.score=attrValue;
                    }else if("drawType".equalsIgnoreCase(attrName)){
                        drawType=StringUtils.parseInt(attrValue);
                    }
                }

                //三种标签高亮显示
               /* if(bean.tag.equals("sentenceScore")||bean.tag.equals("literaryScore")||bean.tag.equals("thoughtScore")){
                    line.seq=MeasureSpec.makeMeasureSpec(line.seq, MeasureSpec.EXACTLY);//高位存类型，低位存真实的序号
                }*/
                if(drawType==1){//drawType=0,画线，1背景，2两个
                    line.seq=MeasureSpec.makeMeasureSpec(line.seq, MeasureSpec.EXACTLY);//高位存类型，低位存真实的序号
                }else if(drawType==2){
                    line.seq=MeasureSpec.makeMeasureSpec(line.seq, MeasureSpec.AT_MOST);
                }
                lines.add(line);
            } else {            // 如果是结束就直接存储进tempTag，因为只有开始标签才能和结束标签闭合
                // 真实倒数位置    相对尾部的位置，
                bean.realEnd = buffer.length() - bean.start;
                tempTag.add(0, bean);
            }
        }

        // boolean checkSeq=true;//是否需要重新赋序号
        int linesCount= ArrayUtils.size(lines);
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
            if(line.end>(line.start+2)){

                int minlen=Math.min(8,line.end-line.start);
                String endChar=buffer.substring(line.end-minlen,line.end);
                //修正一下结尾位置，避免画的过长
                if(endChar.endsWith("，")||endChar.endsWith("。")||endChar.endsWith(",")||endChar.endsWith(".")){
                    line.end--;
                }else{
                    Matcher attribmatcher= ENDBlankTAGPATTERN.matcher(endChar);
                    if(attribmatcher.find()){

                        String blankend=attribmatcher.group(2);
                        line.end=line.end-blankend.length();
                    }
                }
            }
        }
        Collections.reverse(lines);
        return  buffer.toString();
    }

    private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签
 public void initContent(String singleBean) {
        formatContent();
        if (TextUtils.isEmpty(singleBean) || "null".equals(singleBean)) {
             this.setText("本题未做答");
             return;
        }
        LogUtils.d("initContent", "Start Time: " + System.currentTimeMillis());
    /*  if (singleBean.contains("\n")) {       // \u2028 行分隔符 \u2029 段落分隔符
            // 有的机型把分行符识别成 不认识的符号，这里过滤一下
          singleBean = singleBean.replaceAll("\n", "<br>");
        }*/
        ArrayList<Integer> singleChar = new ArrayList<>();              // 黄底纹
        ArrayList<Integer> multChar = new ArrayList<>();                // 红底纹
        ArrayList<UnderLine> singleLines = new ArrayList<>();           // 记录划线的位置 开始 结束 分数
      /*  Spanned  spannedx;
        spannedx = HtmlV2.fromHtml(singleBean, Html.FROM_HTML_MODE_COMPACT, null, new XmlSimpleV2Handler(singleLines));
*/
    //  Spanned  spannedx = HtmlV2.fromHtml(singleBean, Html.FROM_HTML_MODE_COMPACT, null, new XmlSimpleV2Handler(singleLines));


        String  spannedx =  formatStringV3(singleBean,singleLines);

       /* ArrayList<UnderLine> tmpSingleLines = new ArrayList<>();
        //单独计算交叉嵌套的问题
        List<String> tmplist= getTagContent(singleBean,"literaryScore");
        int k=0;
        for(int i=0;i<(tmplist.size()/3);i++){ //属性，关键字，次数

            k=3*i+0;
            UnderLine tmpLine=new UnderLine();
            tmpLine.addscore="test2";
            tmpLine.score="1";

            String keyword=tmplist.get(k+1);
            int times=StringUtils.parseInt(tmplist.get(k+2));
            if(times>1){     //反向查找上次出现的字符
                int index=0;
                for(int j=i-1;j>=0;j--){
                    if(tmplist.get(j*3+1).equals(keyword)){
                        index=j;
                        break;
                    }
                }
                int lastStart= tmpSingleLines.get(index).start;
                tmpLine.start=spannedx.toString().indexOf(tmplist.get(k+1),lastStart);

            }else {
                tmpLine.start=spannedx.toString().indexOf(tmplist.get(k+1));
            }
            tmpLine.start=spannedx.toString().indexOf(tmplist.get(k+1));
            tmpLine.end=tmpLine.start+tmplist.get(k+1).length();
            tmpLine.isSet=2;
            tmpSingleLines.add(tmpLine);

        }*/

        LogUtils.d("initContent", "end Time: " + System.currentTimeMillis());
        final CusNumAlignText cusAlignText = new CusNumAlignText(this);
        final CusNumCheckDetailTag mDrawLine = new CusNumCheckDetailTag(cusAlignText);

       /* if(mSelectableTextHelperD==null){
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
                if(null!=mOnTextSelectListener){
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
        cusAlignText.setSelectableTextHelper(mSelectableTextHelperD.mSelectionInfo);*/
        mDrawLine.setUnderLine(singleLines, mMultLines, singleChar, multChar);
        this.setmCusDraw(cusAlignText);
        String input=spannedx.toString().replace("и"," ");
        this.setTextImgTagEssay(input);               // 是否有图片标签
        this.setText(input);
        if (TextUtils.isEmpty(input)) {
            this.setHighlightColor(getResources().getColor(android.R.color.transparent));
        }
    }
}
