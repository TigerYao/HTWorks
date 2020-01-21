package com.huatu.handheld_huatu.helper.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.huatu.handheld_huatu.business.ztk_zhibo.cache.DBHelper;
import com.huatu.handheld_huatu.business.ztk_zhibo.cache.SQLiteHelper;
import com.huatu.handheld_huatu.helper.AbsDataBaseDao;
import com.huatu.handheld_huatu.helper.Singleton;
import com.huatu.handheld_huatu.utils.UserInfoUtil;

import java.util.List;

/**
 * Created by Administrator on 2019\12\2 0002.
 */

public class ArticleInfoDao extends AbsDataBaseDao<CourseWareCollectInfo> {
    private static final Singleton<ArticleInfoDao> gDefault = new Singleton<ArticleInfoDao>() {

        @Override
        protected ArticleInfoDao create() {
            return new ArticleInfoDao();
        }
    };

    public static ArticleInfoDao getInstance() {
        return gDefault.get();
    }


    private final String type = "1";

    private ArticleInfoDao() {
        super(SQLiteHelper.getInstance().getSQLiteOpenHelper());

    }

    public void create(int keyId, int status, int netClassId, int courseId) {
        CourseWareCollectInfo tmpInfo = new CourseWareCollectInfo();
        tmpInfo.id = keyId;
        tmpInfo.status = status;
        tmpInfo.type = 1;
        tmpInfo.netClassId = netClassId;
        tmpInfo.courseId = courseId;
        tmpInfo.userId = UserInfoUtil.userId;
        this.create(tmpInfo);

    }

    public CourseWareCollectInfo get(String keyId) {
        List var4 = this.get(CourseWareCollectInfo.USERID + "=? and " + CourseWareCollectInfo.ID + "=? and " + CourseWareCollectInfo.TYPE + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), keyId, type});
        return var4.size() > 0 ? (CourseWareCollectInfo) var4.get(0) : null;
    }

    public void delete(String keyId) {
        this.delete(CourseWareCollectInfo.USERID + "=? and " + CourseWareCollectInfo.ID + "=? and " + CourseWareCollectInfo.TYPE + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), keyId, type});
    }


    public int update(CourseWareCollectInfo var1, int keyId) {
        return this.update(var1, CourseWareCollectInfo.USERID + "=? and " + CourseWareCollectInfo.ID + "=? and " + CourseWareCollectInfo.TYPE + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), keyId + "", type});
    }

 /*   public int updateById(CourseWareCollectInfo var1) {
        return this.update(var1, "_id=?", new String[]{String.valueOf(var1.id)});
    }*/

    public List<CourseWareCollectInfo> getAll() {
        return this.get((String[]) null, (String) null, (String[]) null, (String) null, (String) null, "_id ASC", (String) null);
    }

    public CourseWareCollectInfo parseCursorToBean(Cursor var1) {
        return CourseWareCollectInfo.parseCursorToBean(var1);
    }

    public ContentValues getContentValues(CourseWareCollectInfo var1) {
        if (var1.id == -1) {// 只更新一个字段
            ContentValues contentValues = new ContentValues();
            contentValues.put(CourseWareCollectInfo.STATUS, var1.status);
            return contentValues;
        }
        return CourseWareCollectInfo.buildContentValues(var1);
    }

    protected String getTableName() {
        return DBHelper.TB_COURSE_COLLECTNAME;
    }


}
