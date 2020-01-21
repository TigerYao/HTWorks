package com.huatu.handheld_huatu.helper.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.huatu.handheld_huatu.business.arena.textselect.abstracts.MarkInfo;


/**
 * Created by Administrator on 2020\1\17 0017.
 */

public class KnowPointInfo extends MarkInfo {

    public static final String IDIndex = "_id";
    public static final String ID = "questionId";
    public static final String TYPE = "type";
    public static final String START = "start";
    public static final String END = "end";
    public static final String PRACTICRID = "practiceId";
    public static final String USERID = "userId";
    public static final String RESERVE1 = "reserve1";//备份字段
    public static final String RESERVE2 = "reserve2";//备份字段

    public static final String CONTENT = "content";

    // public int   seq;      // 序号
    public int questionId;
    public int type;          // 标记类型 9、材料 10、题干
    // public int   start;
    // public int   end;
    public long practiceId;
    public int userId;

    // public String content;

   /*   + KnowPointInfo.ID + " INT,"            ->  questionId
            + KnowPointInfo.TYPE + " TINYINT,"  ->  TYPE
            + KnowPointInfo.STATUS + " TINYINT,"  start
            + KnowPointInfo.NETCLASSID + " INT,"  end
            + KnowPointInfo.COURSEID + " INT,"    practiceId
            + KnowPointInfo.USERID  + " INT,"     USERID
            + KnowPointInfo.REMARK  + " TEXT)"*/  // remark


    public static ContentValues buildContentValues(KnowPointInfo var0) {
        ContentValues var1 = new ContentValues();

        var1.put(KnowPointInfo.ID, var0.questionId);
        var1.put(KnowPointInfo.TYPE, var0.type);
        var1.put(KnowPointInfo.START, var0.start);
        var1.put(KnowPointInfo.END, var0.end);
        var1.put(KnowPointInfo.PRACTICRID, var0.practiceId);
        var1.put(KnowPointInfo.USERID, var0.userId);
        var1.put(KnowPointInfo.CONTENT, TextUtils.isEmpty(var0.content) ? "" : var0.content);

        return var1;
    }

    public static KnowPointInfo parseCursorToBean(Cursor var0) {
        KnowPointInfo var1 = new KnowPointInfo();

        var1.seq = (var0.getInt(var0.getColumnIndex(KnowPointInfo.IDIndex)));
        var1.questionId = (var0.getInt(var0.getColumnIndex(KnowPointInfo.ID)));
        var1.type = (var0.getInt(var0.getColumnIndex(KnowPointInfo.TYPE)));
        var1.start = (var0.getInt(var0.getColumnIndex(KnowPointInfo.START)));
        var1.end = (var0.getInt(var0.getColumnIndex(KnowPointInfo.END)));
        var1.practiceId = (var0.getLong(var0.getColumnIndex(KnowPointInfo.PRACTICRID)));
        var1.userId = (var0.getInt(var0.getColumnIndex(KnowPointInfo.USERID)));
        var1.content = (var0.getString(var0.getColumnIndex(KnowPointInfo.CONTENT)));

        return var1;
    }
}
