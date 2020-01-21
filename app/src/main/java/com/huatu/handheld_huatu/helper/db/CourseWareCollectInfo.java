package com.huatu.handheld_huatu.helper.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

/**
 * Created by Administrator on 2019\4\10 0010.
 */

public class CourseWareCollectInfo {

    public static final String IDIndex = "_id";
    public static final String ID = "idkey";
    public static final String TYPE = "type";
    public static final String STATUS="status";
    public static final String NETCLASSID = "netClassId";
    public static final String COURSEID = "courseId";
    public static final String USERID = "userId";

    public static final String REMARK = "remark";



    public int id;
    public int type;
    public int status;
    public int netClassId;
    public int courseId;
    public int userId;

    public String remark;



    public static ContentValues buildContentValues(CourseWareCollectInfo var0) {
        ContentValues var1 = new ContentValues();

        var1.put(ID, var0.id);
        var1.put(TYPE, var0.type);
        var1.put(STATUS, var0.status);
        var1.put(NETCLASSID, var0.netClassId);
        var1.put(COURSEID, var0.courseId);
        var1.put(USERID, var0.userId);
        var1.put(REMARK,  TextUtils.isEmpty(var0.remark)?"":var0.remark);

        return var1;
    }

    public static CourseWareCollectInfo parseCursorToBean(Cursor var0) {
        CourseWareCollectInfo var1 = new CourseWareCollectInfo();
        var1.id=(var0.getInt(var0.getColumnIndex(ID)));
        var1.type=(var0.getInt(var0.getColumnIndex(TYPE)));
        var1.status=(var0.getInt(var0.getColumnIndex(STATUS)));
        var1.netClassId=(var0.getInt(var0.getColumnIndex(NETCLASSID)));
       // var1.courseId=(var0.getInt(var0.getColumnIndex(COURSEID)));
        var1.userId=(var0.getInt(var0.getColumnIndex(USERID)));
        var1.remark=(var0.getString(var0.getColumnIndex(REMARK)));

        return var1;
    }
}
