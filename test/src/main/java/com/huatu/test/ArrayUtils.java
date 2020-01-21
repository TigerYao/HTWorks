package com.huatu.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 数组操作的工具类
 *
 * author : Soulwolf Create by 2015/7/6 17:28
 * email  : ToakerQin@gmail.com.
 */
public final class ArrayUtils {


    /**
     * 分組依據接口，用于集合分組時，獲取分組依據
     *
     * @author ZhangLiKun
     * @title GroupBy
     * @date 2013-4-23
     */
    public interface GroupBy<T> {
        T groupby(Object obj);
    }

    /**
     *
     * @param colls
     * @param gb
     * @return
     */
    public static final <T extends Comparable<T>, D> Map<T, List<D>> group(Collection<D> colls, GroupBy<T> gb) {
        if (colls == null || colls.isEmpty()) {
            System.out.println("分組集合不能為空!");
            return null;
        }
        if (gb == null) {
            System.out.println("分組依據接口不能為Null!");
            return null;
        }
        Iterator<D> iter = colls.iterator();
        Map<T, List<D>> map = new HashMap<T, List<D>>();
        while (iter.hasNext()) {
            D d = iter.next();
            T t = gb.groupby(d);
            if (map.containsKey(t)) {
                map.get(t).add(d);
            } else {
                List<D> list = new ArrayList<D>();
                list.add(d);
                map.put(t, list);
            }
        }
        return map;
    }



    public static final <D> String arrayString(Collection<D> colls, GroupBy<String> gb) {
        if(ArrayUtils.isEmpty(colls)) return "";
        if (gb == null) {
            System.out.println("分組依據接口不能為Null!");
            return "";
        }
        Iterator<D> iter = colls.iterator();
        StringBuffer sb = new StringBuffer();
        int i=0,size=ArrayUtils.size(colls);
        while (iter.hasNext()) {
            D d = iter.next();
            String t = gb.groupby(d);
            if (i == size - 1) {
                sb.append(t.toString());
            } else {
                sb.append(t.toString()+ ",");
            }
            i++;
         }
         return sb.toString();
    }

    public static final <D> List<String> arrayStringList(Collection<D> colls, GroupBy<String> gb) {
        if(ArrayUtils.isEmpty(colls)) return null;
        if (gb == null) {
            System.out.println("分組依據接口不能為Null!");
            return null;
        }
        Iterator<D> iter = colls.iterator();
        List<String> tmplist=new ArrayList<>();

        while (iter.hasNext()) {
            D d = iter.next();
            String t = gb.groupby(d);
            tmplist.add(t);

        }
        return tmplist;
    }


    /** 数组翻转 */
    public static void reversal(int ... arrays){
        for (int i = 0; i < arrays.length / 2; i++) {
            int temp = arrays[i];
            arrays[i] = arrays[arrays.length - 1 - i];
            arrays[arrays.length - 1 - i] = temp;
        }
    }

    /** 将集合转换为数组 */
    @SuppressWarnings("unchecked")
    public static Object[] collectionToArray(Collection<?> contents){
        int size = contents.size();
        ArrayList<Object> tSet  = new ArrayList<>();
        for (Object o:contents){
            tSet.add(o);
        }
        return tSet.toArray(new Object[size]);
    }

    /** 检查集合是否为空 */
    public static boolean isEmpty(Collection<?> collection){
        return collection == null ||collection.isEmpty()  ;
    }

    public static int size(Collection<?> collection){
        if(collection==null) return 0;
        return collection.size();
    }

    public static <T> int size(T[] array){
        if(array==null) return 0;
        return array.length;
    }

    public static <T> T getAtIndex(List<T> array,int index){
        if(array==null) return null;
        int size=size(array);
        if(index>=size||index<0) return null;
        return array.get(index);
    }

    /** 检查自定义对象数组是否为空 */
    public static <T> boolean isEmpty(T[] array){
        return array == null || array.length == 0;
    }

    /** 检查 基本数据类型数组是否为空 */
    public static boolean isEmpty(int [] array){
        return array == null || array.length == 0;
    }
}
