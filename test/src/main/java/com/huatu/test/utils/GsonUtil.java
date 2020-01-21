package com.huatu.test.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 */
public class GsonUtil {

    //对TypeToken包一次，减少添加引用
    public static class TypeToken<T> extends com.google.gson.reflect.TypeToken<T>{   }

    private static Gson gson = null;

    public static synchronized Gson getGson() {
        if(gson == null) {
            synchronized (GsonUtil.class) {
                if(gson == null){
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    private GsonUtil() {
    }

    //子类序列化成父类
    public static <T> String toJson(Object object, Class<T> cls){
        String gsonString = null;
        if (gson != null) {
            try {
                gsonString = gson.toJson(object,cls);
            }
            catch (Exception e){}
        }
        return gsonString;

    }
/*
    //对TypeToken包一次，减少添加引用
    public static <T>  Type getTokenType(){
       return new TypeToken<List<T>>() {
        }.getType();
    }*/

    public static String GsonString(Object object) {
        String gsonString = null;
        if (gson != null) {
            try {
                gsonString = gson.toJson(object);
            }
            catch (Exception e){}
         }
        return gsonString;
    }

    public static <T> T GsonToBean(String gsonString, Class<T> cls) {
        T t = null;
       try {
            if (gson != null) {
                t = gson.fromJson(gsonString, cls);
            }
        }
        catch (Exception e){}
        return t;
    }


    //有问题
   public static <T> Object GsonToList(String gsonString, Class<T> cls) {

        Object list = null;
        try{
            if (gson != null) {
                list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
                }.getType());
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            LogUtils.e(ex.getMessage());
            return null;
        }
       return list;
    }

    public static <T> List<T> jsonToList(String gsonStr, Type typeOfT) {
        try {
            if (gsonStr == null) {
                return null;
            }
            List<T> list = null;
            try {
                if (gson != null) {
                    list = gson.fromJson(gsonStr, typeOfT);
                }
            }
            catch (Exception e){}
            return  list;
        } catch (Exception ex) {
            if(typeOfT!=null)
                LogUtils.e("LogUtils", "=== exception===\n , " + ex.toString() + "\n************\n" + ex.getMessage()+ "\n************\n" +typeOfT.toString());
            ex.printStackTrace();
            return null;
        }
    }

    //    List<Person> ps = gson.fromJson(str, new TypeToken<List<Person>>(){}.getType());
    public static <T> Object string2JsonObject(String gsonStr, Type typeOfT) {
        try {
            if (gsonStr == null) {
                return null;
            }
/*            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();*/
            Object t=null;
            try {
                if (gson != null) {
                    t = gson.fromJson(gsonStr, typeOfT);
                }
            }
            catch (Exception e){}
            return  t;
        } catch (Exception ex) {
            if(typeOfT!=null)
            LogUtils.e("LogUtils", "=== exception===\n , " + ex.toString() + "\n************\n" + ex.getMessage()+ "\n************\n" +typeOfT.toString());
            ex.printStackTrace();
            return null;
        }
    }

    public static String toJsonStr(Object obj) {
     /*   try {
            if (obj == null) {
                return null;
            }
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            return gson.toJson(obj);
        } catch (Exception ex) {
            LogUtils.e("LogUtils", " exception , " + ex.getMessage());
            return null;
        }*/
        return GsonString(obj);
    }
}
