package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.helper.db.CourseWareCollectInfo;
import com.huatu.handheld_huatu.helper.db.KnowPointInfo;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;

/**
 * Created by DongDong on 2016/4/7.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String TB_COURSE_NAME = "course";
    public static final String TB_COURSEWARE_NAME = "courseware";
    public static final String TB_COURSE_RECORRDNAME = "recordcourse";

    public static final String TB_COURSE_HANDOUTNAME = "coursehandout";

    public static final String TB_COURSE_COLLECTNAME = "coursecollect";

    public static final String TB_KNOWPOINT_COLLECTNAME = "knowpointcollect";

    public DBHelper(Context context, String name,
                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onCreateCourseTable(db, TB_COURSE_NAME);
        onCreateCourseWareTable(db, TB_COURSEWARE_NAME);
        onCreateCourseTable(db, TB_COURSE_RECORRDNAME);

        onCreateCourseHandoutTable(db, TB_COURSE_HANDOUTNAME);//讲议

        onCreateCollectCourseTable(db,TB_COURSE_COLLECTNAME);//收藏

        onCreateKnowPointTable(db,TB_KNOWPOINT_COLLECTNAME);//高亮
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            LogUtils.d("onUpgrade---start>" +oldVersion+"_"+ System.currentTimeMillis());
            db.beginTransaction();//

            if(oldVersion==7){
                onCreateKnowPointTable(db,TB_KNOWPOINT_COLLECTNAME);
            }
            else if(oldVersion==6){
                onCreateCollectCourseTable(db,TB_COURSE_COLLECTNAME);
                onCreateKnowPointTable(db,TB_KNOWPOINT_COLLECTNAME);
            }
            else if(oldVersion==5){
                addTableCourseWareCatalogId(db);

                onCreateCollectCourseTable(db,TB_COURSE_COLLECTNAME);
                onCreateKnowPointTable(db,TB_KNOWPOINT_COLLECTNAME);
            }
           else if(oldVersion==4){
                addTableCourseWareUserId(db);
                onCreateCourseHandoutTable(db, TB_COURSE_HANDOUTNAME);//讲议

                addTableCourseWareCatalogId(db);
                onCreateCollectCourseTable(db,TB_COURSE_COLLECTNAME);
                onCreateKnowPointTable(db,TB_KNOWPOINT_COLLECTNAME);
            }

            else if(oldVersion==3){
                onCreateCourseTable(db, TB_COURSE_RECORRDNAME);
                addTableCourseUserId(db);
                addTableCourseWareUserId(db);

                onCreateCourseHandoutTable(db, TB_COURSE_HANDOUTNAME);//讲议
                addTableCourseWareCatalogId(db);
                onCreateCollectCourseTable(db,TB_COURSE_COLLECTNAME);
                onCreateKnowPointTable(db,TB_KNOWPOINT_COLLECTNAME);
            }
            else if (oldVersion == 2) {
                dbUpgrade1t2To3(db);
                onCreateCourseTable(db, TB_COURSE_RECORRDNAME);
                addTableCourseUserId(db);
                addTableCourseWareUserId(db);

                onCreateCourseHandoutTable(db, TB_COURSE_HANDOUTNAME);//讲议
                addTableCourseWareCatalogId(db);
                onCreateCollectCourseTable(db,TB_COURSE_COLLECTNAME);
                onCreateKnowPointTable(db,TB_KNOWPOINT_COLLECTNAME);
            } else {
                dropDb(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e);
        } finally {
            try {
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(e);
            }
        }
        LogUtils.d("onUpgrade---end>" + System.currentTimeMillis());
    }

    private void dropDb(final SQLiteDatabase db) {
        try {
            String alterSql1 = " DROP TABLE IF EXISTS " + TB_COURSE_NAME ;
            db.execSQL(alterSql1);
            String alterSql2 = " DROP TABLE IF EXISTS " + TB_COURSEWARE_NAME ;
            db.execSQL(alterSql2);
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.e(e);
        } finally {
        }
    }

    private void addTableCourseUserId(final SQLiteDatabase db) {
        try {
              String tmpSql=String.format(" ALTER TABLE %1$s  ADD COLUMN %2$s INTEGER DEFAULT %3$d",TB_COURSE_NAME,DownLoadCourse.USER_ID, SpUtils.getUid());
             LogUtils.e("addTableCourseUserId",tmpSql);
             db.execSQL(tmpSql);
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.e(e);
        } finally {
        }
    }

    private void addTableCourseWareUserId(final SQLiteDatabase db) {
        try {
            String tmpSql=String.format(" ALTER TABLE %1$s  ADD COLUMN %2$s INTEGER DEFAULT %3$d",TB_COURSEWARE_NAME,DownLoadLesson.USER_ID, SpUtils.getUid());
            LogUtils.e("addTableCourseWareUserId",tmpSql);
            db.execSQL(tmpSql);

            //update 表名 set 列名= '要加的字符串'||列名
            String updateCourseSql="UPDATE " +TB_COURSE_NAME+ " SET "+DownLoadCourse.COURSE_ID+"='"+SpUtils.getUid()+"_'||"+DownLoadCourse.COURSE_ID;
            db.execSQL(updateCourseSql);

            String updateCourseWareSql="UPDATE " +TB_COURSEWARE_NAME+ " SET "+DownLoadLesson.COURSE_ID+"='"+SpUtils.getUid()+"_'||"+DownLoadLesson.COURSE_ID;
            db.execSQL(updateCourseWareSql);

        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.e(e);
        } finally {
        }
    }


    private void addTableCourseWareCatalogId(final SQLiteDatabase db) {
        try {
            String tmpSql=String.format(" ALTER TABLE %1$s  ADD COLUMN %2$s INTEGER DEFAULT %3$d",TB_COURSEWARE_NAME,DownLoadLesson.CATALOG_ID, 0);
            LogUtils.e("addTableCourseWareUserId",tmpSql);
            String tmpSql2=String.format(" ALTER TABLE %1$s  ADD COLUMN %2$s INTEGER DEFAULT %3$d",TB_COURSEWARE_NAME,DownLoadLesson.PARENT_ID, 0);
            LogUtils.e("addTableCourseWareUserId",tmpSql2);
            db.execSQL(tmpSql);
            db.execSQL(tmpSql2);

        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.e(e);
        } finally {
        }
    }

    private void dbUpgrade1t2To3(final SQLiteDatabase db) {
        try {
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  player_type TINYINT DEFAULT 0");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  download_progress TINYINT DEFAULT 0");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  lesson_clarity TINYINT DEFAULT 0");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  encrypt_type TINYINT DEFAULT 0");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  signal_file_path TEXT");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  room_id TEXT");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  session_id TEXT");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  video_token TEXT");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  play_params TEXT");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  reserve1 TEXT");
            db.execSQL(" ALTER TABLE " + TB_COURSEWARE_NAME + " ADD COLUMN  reserve2 TEXT");
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.e(e);
        } finally {
        }
    }

    private void onCreateCourseTable(SQLiteDatabase db, String courseName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + courseName + "("
                + DownLoadCourse.COURSE_ID + " VARCHAR PRIMARY KEY,"
                + DownLoadCourse.COURSE_NAME + " TEXT,"
                + DownLoadCourse.COURSE_TYPE + " TEXT,"
                + DownLoadCourse.TOTAL_NUM + " TINYINT,"
                + DownLoadCourse.TEACHER + " TEXT,"
                + DownLoadCourse.IMAGE_PATH  + " TEXT,"
                + DownLoadCourse.USER_ID  + " TEXT)"
        );
    }

    //SQLite 外键
    //https://blog.csdn.net/woshinia/article/details/41679709
    private void onCreateCourseWareTable(SQLiteDatabase db, String courseWareName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + courseWareName + "("
                + DownLoadLesson.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DownLoadLesson.DOWNLOAD_ID + " VARCHAR,"
                + DownLoadLesson.DOWN_STATUS + " TINYINT,"
                + DownLoadLesson.SUBJECT_ID + " VARCHAR,"
                + DownLoadLesson.SUBJECT_NAME + " TEXT,"
                + DownLoadLesson.PLAY_PATH + " VARCHAR,"
                + DownLoadLesson.PLAY_PROGRESS + " TINYINT,"
                + DownLoadLesson.SPACE + " BIGINT,"
                + DownLoadLesson.DURATION + " INT,"
                + DownLoadLesson.PLAY_DURATION + " INT,"
                + DownLoadLesson.COURSE_NUM + " TINYINT,"
                + DownLoadLesson.IMAGE_PATH + " TEXT,"
                + DownLoadLesson.LESSON + " TINYINT,"
                + DownLoadLesson.PLAYER_TYPE + " TINYINT,"
                + DownLoadLesson.DOWNLOAD_PROGRESS + " TINYINT,"
                + DownLoadLesson.VIDEO_CLARITY + " TINYINT,"
                + DownLoadLesson.ENCRYPT_TYPE + " TINYINT,"
                + DownLoadLesson.SIGNAL_FILE_PATH + " TEXT,"
                + DownLoadLesson.ROOM_ID + " TEXT,"
                + DownLoadLesson.SESSION_ID + " TEXT,"
                + DownLoadLesson.VIDEO_TOKEN + " TEXT,"
                + DownLoadLesson.PLAY_PARAMS + " TEXT,"
                + DownLoadLesson.REVERSE_1 + " TEXT,"
                + DownLoadLesson.REVERSE_2 + " TEXT,"
                + DownLoadLesson.USER_ID + " INT,"
                + DownLoadLesson.CATALOG_ID + " INT,"
                + DownLoadLesson.PARENT_ID + " INT,"
                + DownLoadLesson.COURSE_ID
                + " VARCHAR,FOREIGN KEY(" + DownLoadLesson.COURSE_ID + ")REFERENCES "
                + TB_COURSE_NAME + "(" + DownLoadCourse.COURSE_ID + "))"
         );
    }

    private void onCreateCourseHandoutTable(SQLiteDatabase db, String courseHandoutName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + courseHandoutName + "("
                + DownHandout.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DownHandout.SUBJECT_ID + " VARCHAR,"
                + DownHandout.SUBJECT_NAME + " VARCHAR,"
                + DownHandout.DOWN_STATUS + " TINYINT,"
                + DownHandout.COURSE_ID + " VARCHAR,"
                + DownHandout.FILE_TYPE  + " TINYINT,"
                + DownHandout.SPACE  + " BIGINT,"
                + DownHandout.FILE_URL  + " VARCHAR,"
                + DownHandout.REVERSE_1  + " VARCHAR,"
                + DownHandout.REVERSE_2  + " TEXT,"
                + DownHandout.USER_ID  + " INT)"
        );
    }


/*    public static final String ID = "idkey";
    public static final String TYPE = "type";
    public static final String STATUS="status";
    public static final String NETCLASSID = "netClassId";
    public static final String COURSEID = "courseId";
    public static final String USERID = "userId";

    public static final String REMARK = "remark";*/

    private void onCreateCollectCourseTable(SQLiteDatabase db, String collectCourseName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + collectCourseName + "("
                + CourseWareCollectInfo.IDIndex + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CourseWareCollectInfo.ID + " INT,"
                + CourseWareCollectInfo.TYPE + " TINYINT,"
                + CourseWareCollectInfo.STATUS + " TINYINT,"
                + CourseWareCollectInfo.NETCLASSID + " INT,"
                + CourseWareCollectInfo.COURSEID + " INT,"
                + CourseWareCollectInfo.USERID  + " INT,"
                + CourseWareCollectInfo.REMARK  + " TEXT)"

        );
    }

    private void onCreateKnowPointTable(SQLiteDatabase db, String collectCourseName) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + collectCourseName + "("
                + KnowPointInfo.IDIndex + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KnowPointInfo.ID + " INT,"
                + KnowPointInfo.TYPE + " INT,"
                + KnowPointInfo.START + " INT,"
                + KnowPointInfo.END + " INT,"
                + KnowPointInfo.PRACTICRID + " BIGINT,"
                + KnowPointInfo.USERID  + " INT,"
                + KnowPointInfo.RESERVE1  + " INT,"
                + KnowPointInfo.RESERVE2  + " TEXT,"
                + KnowPointInfo.CONTENT  + " TEXT)"

        );
    }
}
