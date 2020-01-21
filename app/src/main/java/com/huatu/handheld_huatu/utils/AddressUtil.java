package com.huatu.handheld_huatu.utils;

import android.content.res.AssetManager;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.mvpmodel.area.ProvinceModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by saiyuan on 2017/9/29.
 */

public class AddressUtil {
    public static List<ProvinceModel> getAddressListFromXml() {
        List<ProvinceModel> provinceList = new ArrayList<>();
        AssetManager asset = UniApplicationContext.getContext().getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            if(!Method.isListEmpty(handler.getDataList())) {
                provinceList.addAll(handler.getDataList());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
        return provinceList;
    }
}
