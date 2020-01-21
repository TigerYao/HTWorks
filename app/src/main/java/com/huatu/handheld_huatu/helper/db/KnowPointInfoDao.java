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
 * Created by cjx on 2019\12\2 0002.
 */
public class KnowPointInfoDao extends AbsDataBaseDao<KnowPointInfo> {

    private static final Singleton<KnowPointInfoDao> gDefault = new Singleton<KnowPointInfoDao>() {

        @Override
        protected KnowPointInfoDao create() {
            return new KnowPointInfoDao();
        }
    };

    public static KnowPointInfoDao getInstance() {
        return gDefault.get();
    }

    private final String mType = "10";

    private KnowPointInfoDao() {
        super(SQLiteHelper.getInstance().getSQLiteOpenHelper());
    }

    /**
     * @param type  9、材料 10、题干
     */
    public int create(int questionId, long practiceId, int type, int start, int end, String content) {
        KnowPointInfo tmpInfo = new KnowPointInfo();
        tmpInfo.practiceId = practiceId;
        tmpInfo.questionId = questionId;
        tmpInfo.type = type;
        tmpInfo.start = start;
        tmpInfo.end = end;
        tmpInfo.content = content;

        tmpInfo.userId = UserInfoUtil.userId;

        return (int) this.create(tmpInfo);
    }

    public KnowPointInfo get(int questionId, long practiceId) {
        List var4 = this.get(KnowPointInfo.USERID + "=? and " + KnowPointInfo.ID + "=? and " + KnowPointInfo.PRACTICRID + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), questionId + "", practiceId + ""});
        return var4.size() > 0 ? (KnowPointInfo) var4.get(0) : null;
    }

    public void delete(int seq) {
        this.delete(KnowPointInfo.USERID + "=? and " + KnowPointInfo.IDIndex + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), seq + ""});
    }

    public void clearPaper(long practiceId) {
        this.delete(KnowPointInfo.USERID + "=? and " + KnowPointInfo.PRACTICRID + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), practiceId + ""});
    }

 /*   public int update(KnowPointInfo var1, int keyId) {
        return this.update(var1, KnowPointInfo.USERID + "=? and " + KnowPointInfo.ID + "=? and " + KnowPointInfo.TYPE + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), keyId + "", mType});
    }
*/
 /*   public int updateById(KnowPointInfo var1) {
        return this.update(var1, "_id=?", new String[]{String.valueOf(var1.id)});
    }*/

    public List<KnowPointInfo> getAll(long practiceId) {
        return this.get((String[]) null,
                KnowPointInfo.USERID + "=? and " + KnowPointInfo.PRACTICRID + "=?"
                , new String[]{String.valueOf(UserInfoUtil.userId), practiceId + ""}
                , (String) null, (String) null, "_id ASC", (String) null);
    }

    public List<KnowPointInfo> getQuestionAll(int questionId, long practiceId) {
      /*  return this.get(KnowPointInfo.USERID + "=? and " + KnowPointInfo.ID + "=? and " + KnowPointInfo.PRACTICRID + "=?",
                new String[]{String.valueOf(UserInfoUtil.userId), questionId+"", practiceId+""});*/

        return this.get((String[]) null,
                KnowPointInfo.USERID + "=? and " + KnowPointInfo.ID + "=? and " + KnowPointInfo.PRACTICRID + "=?"
                , new String[]{String.valueOf(UserInfoUtil.userId), questionId + "", practiceId + ""}
                , (String) null, (String) null, "_id ASC", (String) null);

    }

    public KnowPointInfo parseCursorToBean(Cursor var1) {
        return KnowPointInfo.parseCursorToBean(var1);
    }

    public ContentValues getContentValues(KnowPointInfo var1) {
     /*   if (var1.questionId == -1) {// 只更新一个字段
            ContentValues contentValues = new ContentValues();
            contentValues.put(KnowPointInfo.STATUS, var1.status);
            return contentValues;
        }*/
        return KnowPointInfo.buildContentValues(var1);
    }

    protected String getTableName() {
        return DBHelper.TB_KNOWPOINT_COLLECTNAME;
    }


}
