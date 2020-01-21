package com.huatu.test.drawimpl;

import android.text.Editable;


import com.huatu.test.bean.UnderLine;

import org.xml.sax.XMLReader;

import java.util.ArrayList;

public class XmlSimpleHandler extends XmlBaseHandler {

    //final String TAGNAME="thesisScore";
    final String TagName1="titleScore";
    ArrayList<UnderLine> list;



    public XmlSimpleHandler(ArrayList<UnderLine> list) {
        this.list = list;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (opening) {
            handlerStartTAG(tag, output, xmlReader);
        } else {
            handlerEndTAG(tag, output);
        }
    }

    /**
     * 处理开始的标签位
     */
    private void handlerStartTAG(String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase(TagName1)) {
            handlerStartLatex(output, xmlReader);
        }
    }

    private void handlerStartLatex(Editable output, XMLReader xmlReader) {
        UnderLine big = new  UnderLine();
        list.add(big);
        big.start = output.length();
        getProperty(xmlReader, big);
    }

    /**
     * 处理结尾的标签位
     */
    private void handlerEndTAG(String tag, Editable output) {
        if (tag.equalsIgnoreCase(TagName1)) {
            handlerEnd(output);
        }
    }

    private void handlerEnd(Editable output) {
        UnderLine big = list.get(list.size() - 1);
        big.end = output.length();
    }

    /**
     * 利用反射获取html标签的属性值
     */
    private void getProperty(XMLReader xmlReader, UnderLine big) {
        try {

           // Field elementField =getElementField(xmlReader);
            Object element = getElementField(xmlReader).get(xmlReader);

            //Field attsField =getAttsField(element);
            Object atts = getAttsField(element).get(element);

            //Field dataField =getDataField(atts); //atts.getClass().getDeclaredField("data");
            String[] data = (String[]) getDataField(atts).get(atts);

            // Field lengthField =getLengthField(atts) ;//
            int len = (Integer) getLengthField(atts).get(atts);
            for (int i = 0; i < len; i++) {
                // 这边的property换成你自己的属性名就可以了
              /*  if ("c".equals(data[i * 5 + 1])) {
                    big.c = Integer.parseInt(data[i * 5 + 4]);
                }
                if ("l".equals(data[i * 5 + 1])) {
                    big.l = Integer.parseInt(data[i * 5 + 4]);
                }*/
                if ("describe".equals(data[i * 5 + 1])) {
                    big.addscore = data[i * 5 + 4];
                }
                else if ("score".equals(data[i * 5 + 1])) {
                    big.score = data[i * 5 + 4];
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
