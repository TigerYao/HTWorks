package com.huatu.test.drawimpl;

import android.text.Html;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;

/**
 * Created by cjx on 2019\7\3 0003.
 */

public abstract class XmlBaseHandler implements Html.TagHandler {

    static Field mElementField;
    static Field mAttsField;
    static Field mDataField;
    static Field mLengthField;

    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);

                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    public static Field getElementField(XMLReader xmlReader) throws NoSuchFieldException {
        if (null == mElementField) {
            mElementField = findField(xmlReader,"theNewElement");
        }
        return mElementField;
    }

    public static Field getAttsField(Object element) throws NoSuchFieldException {
        if (null == mAttsField) {

            mAttsField=findField(element,"theAtts");;
        }
        return mAttsField;
    }

    public static Field getDataField(Object atts) throws NoSuchFieldException {
        if (null == mDataField) {
            mDataField=findField(atts,"data");
        }
        return mDataField;
    }

    public static Field getLengthField(Object atts) throws NoSuchFieldException {
        if(null==mLengthField){
             mLengthField=findField(atts,"length");
        }
        return mLengthField;
    }


}
