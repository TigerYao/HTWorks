package com.huatu.handheld_huatu.utils;


import com.huatu.handheld_huatu.mvpmodel.area.CityModel;
import com.huatu.handheld_huatu.mvpmodel.area.DistrictModel;
import com.huatu.handheld_huatu.mvpmodel.area.ProvinceModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class XmlParserHandler extends DefaultHandler {

    /**
     * �洢���еĽ�������
     */
    private List<ProvinceModel> provinceList = new ArrayList<ProvinceModel>();

    public XmlParserHandler() {

    }

    public List<ProvinceModel> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
        // ��������һ����ʼ��ǩ��ʱ�򣬻ᴥ���������
    }

    ProvinceModel provinceModel = new ProvinceModel();
    CityModel cityModel = new CityModel();
    DistrictModel districtModel = new DistrictModel();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // ��������ʼ��ǵ�ʱ�򣬵����������
        if ("province".equals(qName)) {
            provinceModel = new ProvinceModel();
            provinceModel.id = attributes.getValue(1);
            provinceModel.setName(attributes.getValue(0));
            provinceModel.setCityList(new ArrayList<CityModel>());
        } else if ("city".equals(qName)) {
            cityModel = new CityModel();
            cityModel.id = attributes.getValue(1);
            cityModel.setName(attributes.getValue(0));
            cityModel.setDistrictList(new ArrayList<DistrictModel>());
        } else if ("district".equals(qName)) {
            districtModel = new DistrictModel();
            districtModel.id = attributes.getValue(1);
            districtModel.setName(attributes.getValue(0));
            districtModel.setZipcode(attributes.getValue(1));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // ����������ǵ�ʱ�򣬻�����������
        if (qName.equals("district")) {
            cityModel.getDistrictList().add(districtModel);
        } else if (qName.equals("city")) {
            provinceModel.getCityList().add(cityModel);
        } else if (qName.equals("province")) {
            provinceList.add(provinceModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    }

}
