package com.huatu.test.drawimpl;

import android.text.Editable;

import com.huatu.test.bean.UnderLine;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class XmlSimpleV2Handler implements   HtmlV2.TagHandler {

    final String TAGNAME="thesisScore";

    ArrayList<UnderLine> list;
    Stack<String> nameList=new Stack<>();
    HashMap<String,UnderLine> oldMap=new HashMap<>();
    String lastName="";

    public XmlSimpleV2Handler(ArrayList<UnderLine> list) {
        this.list = list;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader, Attributes attributes) {
        if (opening) {
            handlerStartTAG(tag, output, xmlReader,  attributes);
        } else {
            handlerEndTAG(tag, output);
        }
    }

    /**
     * 处理开始的标签位
     */
    private void handlerStartTAG(String tag, Editable output, XMLReader xmlReader, Attributes attributes) {

        if(nameList.size()>0){  //发生了嵌套
            nameList.push(tag);
            oldMap.put(lastName,list.get(list.size() - 1));
            lastName=tag;
            handlerStartLatex(output, xmlReader,attributes);
            return;
        }
        if(!tag.equals("html")&&(!tag.equals("body"))){
            nameList.push(tag);
            lastName=tag;
            handlerStartLatex(output, xmlReader,attributes);
        }

    }

    private void handlerStartLatex(Editable output, XMLReader xmlReader,Attributes attributes) {
        UnderLine big = new  UnderLine();
        list.add(big);
        big.start = output.length();

        String style = attributes.getValue("", "describe");
        big.addscore =lastName;
         big.score="";
    }

    /**
     * 处理结尾的标签位
     */
    private void handlerEndTAG(String tag, Editable output) {
       if(tag.equals(lastName)){
            nameList.pop();
            handlerEnd(output);
        }else {

            for(int i=0;i<nameList.size();i++){

                if(nameList.get(i).equals(tag)){

                    UnderLine tmpline= oldMap.get(tag);
                    if(null!=tmpline) tmpline.end=output.length();
                    break;
                }
            }
            if(!nameList.empty())
             nameList.pop();
        }

     /*   if (tag.equalsIgnoreCase(TAGNAME)) {
            handlerEnd(output);
        }*/
    }

    private void handlerEnd(Editable output) {
        UnderLine big = list.get(list.size() - 1);
        big.end = output.length();
    }

    /**
     * 利用反射获取html标签的属性值
     */
  /*  private void getProperty(XMLReader xmlReader, UnderLine big) {
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
              *//*  if ("c".equals(data[i * 5 + 1])) {
                    big.c = Integer.parseInt(data[i * 5 + 4]);
                }
                if ("l".equals(data[i * 5 + 1])) {
                    big.l = Integer.parseInt(data[i * 5 + 4]);
                }*//*
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
    }*/
}
