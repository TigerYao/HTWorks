package com.huatu.handheld_huatu.business.ztk_zhibo.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;


import com.google.gson.Gson;
import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadCourse;
import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownLoadLesson;
import com.huatu.handheld_huatu.mvpmodel.PlayerTypeEnum;
import com.huatu.handheld_huatu.mvpmodel.zhibo.VideoBean;
import com.huatu.handheld_huatu.ui.DownBtnLayout;
import com.huatu.handheld_huatu.utils.GsonUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.UserInfoUtil;
import com.huatu.utils.ArrayUtils;
import com.huatu.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DongDong on 2016/4/8.
 */
public class SQLiteHelper {
	private static SQLiteHelper mInstance;
	// 数据库名称
	private static String DBName = "course_down.db";
	// 数据库版本
	private static int DB_VERSION = 8;
	private DBHelper helper;
	private SQLiteDatabase db;

	public static synchronized SQLiteHelper getInstance() {
		if(mInstance == null) {
			synchronized (SQLiteHelper.class) {
				if(mInstance==null){
					mInstance = new SQLiteHelper(UniApplicationContext.getContext(), null);
				}
 			}
		}
		return mInstance;
	}

	private SQLiteHelper(Context context, String dbName) {
		if (!TextUtils.isEmpty(dbName)) {
			DBName = dbName;
		}
		helper = new DBHelper(context, DBName, null, DB_VERSION);
		db = helper.getWritableDatabase();
	}

	public SQLiteOpenHelper getSQLiteOpenHelper(){
		return helper;
	}

	public void close() {
		db.close();
		helper.close();
	}


	public List<DownLoadCourse> getCourses(){
        return getCourses(true);
	}
	/**
	 * 获取下载课程的列表
	 * isPlayback  是否为回放数据
	 * @return
	 */
	public List<DownLoadCourse> getCourses(boolean isPlayback) {

		String tmpTableName=isPlayback?DBHelper.TB_COURSE_NAME:DBHelper.TB_COURSE_RECORRDNAME;
		List<DownLoadCourse> courseLists = new ArrayList<DownLoadCourse>();
	   /*	Cursor cursor = db.query(tmpTableName, null, null, null,
				null, null, null);*/

		Cursor cursor = db.query(tmpTableName, null, DownLoadCourse.USER_ID+ "=?", new String[] {String.valueOf(UserInfoUtil.userId)},
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			DownLoadCourse course = new DownLoadCourse();
			course.setCourseID(cursor.getString(cursor
					.getColumnIndex("course_ID")));
			course.setCourseName(cursor.getString(cursor
					.getColumnIndex("course_name")));
			course.setCourseType(cursor.getString(cursor
					.getColumnIndex("course_type")));
			course.setTotalNum(cursor.getInt(cursor.getColumnIndex("total_num")));
			course.setTeacher(cursor.getString(cursor.getColumnIndex("teacher")));
			course.setImagePath(cursor.getString(cursor.getColumnIndex("image_path")));
			course.userId=cursor.getLong(cursor.getColumnIndex(DownLoadCourse.USER_ID));
			courseLists.add(course);
			cursor.moveToNext();
		}
		cursor.close();
		for (int i = 0; i < courseLists.size(); i++) {
  			DownLoadCourse tmpCourse=courseLists.get(i);
			List<DownLoadLesson> lessons = getLessons(tmpCourse.getCourseID(),false);
			tmpCourse.setLessonLists(lessons);
		}
		return courseLists;
	}

	private DownLoadCourse getCourses(boolean isPlayback,String courseId) {

		String	courseID=adjustCourseId(courseId);

		String tmpTableName=isPlayback?DBHelper.TB_COURSE_NAME:DBHelper.TB_COURSE_RECORRDNAME;
		List<DownLoadCourse> courseLists = new ArrayList<DownLoadCourse>();
	   /*	Cursor cursor = db.query(tmpTableName, null, null, null,
				null, null, null);*/


		Cursor cursor = db.query(tmpTableName, null, DownLoadCourse.USER_ID+ "=? and "+DownLoadCourse.COURSE_ID+ "=?" , new String[] {String.valueOf(UserInfoUtil.userId),courseID},
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			DownLoadCourse course = new DownLoadCourse();
			course.setCourseID(cursor.getString(cursor
					.getColumnIndex("course_ID")));
			course.setCourseName(cursor.getString(cursor
					.getColumnIndex("course_name")));
			course.setCourseType(cursor.getString(cursor
					.getColumnIndex("course_type")));
			course.setTotalNum(cursor.getInt(cursor.getColumnIndex("total_num")));
			course.setTeacher(cursor.getString(cursor.getColumnIndex("teacher")));
			course.setImagePath(cursor.getString(cursor.getColumnIndex("image_path")));
			course.userId=cursor.getLong(cursor.getColumnIndex(DownLoadCourse.USER_ID));
			courseLists.add(course);
			cursor.moveToNext();
		}
		cursor.close();
		if(ArrayUtils.isEmpty(courseLists)) return null;
		return courseLists.get(0);
	}


	//todo 添加录播查询
	public List<DownLoadCourse> getAllFinishedCourses() {
 		return getAllFinishedCourses(true);
	}

	/**
	 * 获取下载完成的所有课程的列表
	 * isPlayback  是否为回放数据
	 * @return 下载完成的所有课程的列表
	 */
	public List<DownLoadCourse> getAllFinishedCourses(boolean isPlayback) {
		List<DownLoadCourse> courseLists = new ArrayList<DownLoadCourse>();
		String tmpTableName=isPlayback?DBHelper.TB_COURSE_NAME:DBHelper.TB_COURSE_RECORRDNAME;
		LogUtils.e("getAllFinishedCourses",tmpTableName);

		/*Cursor cursor = db.query(DBHelper.TB_COURSEWARE_NAME, null,
				DownLoadLesson.COURSE_ID + "=?",
				new String[] { course_ID }, null, null, null);*/

		Cursor cursor = db.query(tmpTableName, null, DownLoadCourse.USER_ID+ "=?", new String[] {String.valueOf(UserInfoUtil.userId)},
				null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast() && (cursor.getString(1) != null)) {
			DownLoadCourse course = new DownLoadCourse();
			course.setCourseID(cursor.getString(cursor
					.getColumnIndex("course_ID")));
			course.setCourseName(cursor.getString(cursor
					.getColumnIndex("course_name")));
			course.setCourseType(cursor.getString(cursor
					.getColumnIndex("course_type")));
			course.setTotalNum(cursor.getInt(cursor.getColumnIndex("total_num")));
			course.setTeacher(cursor.getString(cursor.getColumnIndex("teacher")));
			course.setImagePath(cursor.getString(cursor.getColumnIndex("image_path")));
			course.userId=cursor.getLong(cursor.getColumnIndex(DownLoadCourse.USER_ID));
			courseLists.add(course);
			cursor.moveToNext();
		}
		cursor.close();
		//LogUtils.e("getAllFinishedCourses",GsonUtil.GsonString(courseLists));

		List<Integer> tmpDelList=new ArrayList<>();
		for (int i = courseLists.size() - 1; i >= 0; i--) {
			List<DownLoadLesson> downedLesson = getLessonsByStatus(
					courseLists.get(i).getCourseID(), "2",false);
			if (null != downedLesson && downedLesson.size() > 0) {
				courseLists.get(i).setLessonLists(downedLesson);
				//添加大小计算
				if(!ArrayUtils.isEmpty(downedLesson)){
					for(DownLoadLesson lesson:downedLesson)
						courseLists.get(i).totalSpace+=lesson.getSpace();
 				}
 			} else {

 				boolean hasFinished=checkCourseHasHandout(courseLists.get(i).getRealCourseID(),true);
	 		    if(!hasFinished) {
					tmpDelList.add(new Integer(i));
				}else {
 				  // courseLists.get(i).hasHandout=hasFinished;
				}
 				//courseLists.remove(i);
 			}
		}

		//有可能只有讲义
	 	for(int j=0;j<tmpDelList.size();j++){
			courseLists.remove(tmpDelList.get(j).intValue());

		}
  		LogUtils.e("getAllFinishedCourses",courseLists.size()+"");
		return courseLists;
	}


	public List<DownLoadLesson> getLessons(String courseId){
		return getLessons(courseId,true);
	}
	/**
	 * 获取当前课程下的所有课件
	 * 
	 * @param courseId  由于coures_id是为主键，所有拼userId区分
	 *            课程ID
	 * @return 当前课程下所有课件信息
	 */
	private List<DownLoadLesson> getLessons(String courseId,boolean canAdjustId) {

		if(canAdjustId){
			courseId=adjustCourseId(courseId);
		}
 		List<DownLoadLesson> lessons = new ArrayList<>();
		Cursor cursor = db.query(DBHelper.TB_COURSEWARE_NAME, null,
				DownLoadLesson.COURSE_ID + "=? and "+ DownLoadLesson.USER_ID + "=?",
				new String[] { courseId ,String.valueOf(UserInfoUtil.userId)}, null, null, DownLoadLesson.LESSON+" desc ");
		while (cursor.moveToNext()) {
			DownLoadLesson lesson = convertLessonFromCv(cursor);
            if(lesson != null) {
                lessons.add(lesson);
            }
		}
		cursor.close();
		return lessons;
	}


	public List<DownLoadLesson> getLessonsByStatus(String courseID, String downStatus){
		return getLessonsByStatus(courseID,downStatus,true);
	}
	/**
	 * 获取课程下当前下载状态的所有课件
	 * 
	 * @param courseID
	 *            课程ID
	 * @return 当前课程下当前下载状态的所有课件
	 */
	private List<DownLoadLesson> getLessonsByStatus(String courseID, String downStatus,boolean canAdjustId) {

		if(canAdjustId){
			courseID=adjustCourseId(courseID);
		}
 		List<DownLoadLesson> lessons = new ArrayList<>();
		Cursor cursor = db.query(DBHelper.TB_COURSEWARE_NAME, null,
				DownLoadLesson.COURSE_ID + "=? and " + DownLoadLesson.DOWN_STATUS + "=? and "+DownLoadLesson.USER_ID + "=?",
				new String[] { courseID, downStatus,String.valueOf(UserInfoUtil.userId) }, null, null, DownLoadLesson.LESSON +" asc");
		while (cursor.moveToNext()) {
            DownLoadLesson ware = convertLessonFromCv(cursor);
            if(ware != null) {
                lessons.add(ware);
            }
		}
		cursor.close();
		return lessons;
	}

    private DownLoadLesson convertLessonFromCv(Cursor cursor) {
        if(cursor == null) {
            return null;
        }
        DownLoadLesson lesson = new DownLoadLesson();
        lesson.setCourseID(cursor.getString(cursor
                .getColumnIndex("course_ID")));
        lesson.setSubjectID(cursor.getString(cursor
                .getColumnIndex("subject_ID")));
        lesson.setSubjectName(cursor.getString(cursor
                .getColumnIndex("subject_name")));
        lesson.setDownloadID(cursor.getString(cursor
                .getColumnIndex("download_ID")));
        lesson.setDownStatus(cursor.getInt(cursor
                .getColumnIndex("down_status")));
        lesson.setPlayPath(cursor.getString(cursor
                .getColumnIndex("play_path")));
        lesson.setPlayProgress(cursor.getInt(cursor
                .getColumnIndex("play_progress")));
        lesson.setCourseNum(cursor.getInt(cursor
                .getColumnIndex("courseNum")));
        lesson.setDuration(cursor.getInt(cursor
                .getColumnIndex("duration")));
        lesson.setPlay_duration(cursor.getInt(cursor
                .getColumnIndex("play_duration")));
        lesson.setLesson(cursor.getInt(cursor
                .getColumnIndex("lesson")));
        lesson.setSpace(cursor.getLong(cursor.getColumnIndex("space")));
        lesson.setImagePath(
                cursor.getString(cursor.getColumnIndex("image_path")));
        lesson.setPlayerType(cursor.getInt(cursor
                .getColumnIndex(DownLoadLesson.PLAYER_TYPE)));
        lesson.setClarity(cursor.getInt(cursor
                .getColumnIndex(DownLoadLesson.VIDEO_CLARITY)));
        lesson.setEncryptType(cursor.getInt(cursor
                .getColumnIndex(DownLoadLesson.ENCRYPT_TYPE)));
        lesson.setSignalFilePath(cursor.getString(
                cursor.getColumnIndex(DownLoadLesson.SIGNAL_FILE_PATH)));
        lesson.setRoomId(cursor.getString(
                cursor.getColumnIndex(DownLoadLesson.ROOM_ID)));
        lesson.setSessionId(cursor.getString(
                cursor.getColumnIndex(DownLoadLesson.SESSION_ID)));
        lesson.setVideoToken(cursor.getString(
                cursor.getColumnIndex(DownLoadLesson.VIDEO_TOKEN)));
        lesson.setPlayParams(cursor.getString(
                cursor.getColumnIndex(DownLoadLesson.PLAY_PARAMS)));
        lesson.setDownloadProgress(cursor.getInt(
                cursor.getColumnIndex(DownLoadLesson.DOWNLOAD_PROGRESS)));

		lesson.setUserId(cursor.getLong(cursor.getColumnIndex(DownLoadLesson.USER_ID)));
		lesson.setReserve1(cursor.getString(cursor.getColumnIndex(DownLoadLesson.REVERSE_1)));
		lesson.setReserve2(cursor.getString(cursor.getColumnIndex(DownLoadLesson.REVERSE_2)));
		lesson.catalogId=(cursor.getInt(cursor.getColumnIndex(DownLoadLesson.CATALOG_ID)));
		lesson.parentId=(cursor.getInt(cursor.getColumnIndex(DownLoadLesson.PARENT_ID)));
        String params = lesson.getPlayParams();
        if(!TextUtils.isEmpty(params)) {
          /*  Gson gson = new Gson();
            VideoBean.LiveCourseParams params1 = gson.fromJson(
                    params, VideoBean.LiveCourseParams.class);*/
			VideoBean.LiveCourseParams params1= GsonUtil.GsonToBean(params,VideoBean.LiveCourseParams.class);
            if(params1 != null) {
                lesson.setDomain(params1.url);
                lesson.setLiveID(params1.JoinCode);
                lesson.setPassword(params1.password);
            }
        }
        return lesson;
    }

	public List<DownLoadLesson> getUnFinishedCourseWare(){
		List<DownLoadLesson> resultList= getAllUnFinishedCourseWare();
		if(ArrayUtils.isEmpty(resultList)) return resultList;
		List<DownLoadLesson> curlist=new ArrayList<>();
		for(DownLoadLesson item: resultList){
			/*if (item.getPlayerType() == PlayerTypeEnum.CCPlay.getValue() || item.getPlayerType() == PlayerTypeEnum.BjRecord.getValue())
				continue;*/
			curlist.add(item);
 		}
		return curlist;
	}

	public List<DownLoadLesson> getUnFinishedRecordCourseWare(){
		List<DownLoadLesson> resultList= getAllUnFinishedCourseWare();
		if(ArrayUtils.isEmpty(resultList)) return resultList;
		List<DownLoadLesson> curlist=new ArrayList<>();
		for(DownLoadLesson item: resultList){
			if (item.getPlayerType() == PlayerTypeEnum.CCPlay.getValue() || item.getPlayerType() == PlayerTypeEnum.BjRecord.getValue())
				curlist.add(item);
		}
		return curlist;
	}
	/**
	 * 获取当前课程下的所有课件
	 * 
	 *            课程ID
	 * @return 当前课程下所有课件信息
	 */
	private List<DownLoadLesson> getAllUnFinishedCourseWare() {
		List<DownLoadLesson> lessons = new ArrayList<>();
		String status = "2";
		Cursor cursor = db.query(DBHelper.TB_COURSEWARE_NAME, null,
				DownLoadLesson.DOWN_STATUS + "!=? and "+DownLoadLesson.USER_ID + "=?",
				new String[] { status ,String.valueOf(UserInfoUtil.userId)}, null, null, null);
		while (cursor.moveToNext()) {
            DownLoadLesson ware = convertLessonFromCv(cursor);
            if(ware != null) {
                lessons.add(ware);
            }
		}
		cursor.close();
		return lessons;
	}

	public DownLoadLesson getCourseWare(String downId) {
        Cursor cursor = db.query(DBHelper.TB_COURSEWARE_NAME, null,
                DownLoadLesson.DOWNLOAD_ID + "=? and "+DownLoadLesson.USER_ID + "=?",
                new String[] { downId ,String.valueOf(UserInfoUtil.userId) }, null, null, null);

		DownLoadLesson tmpWare=null;
        if (cursor.moveToNext()) {
			tmpWare = convertLessonFromCv(cursor);
        }
		cursor.close();
        return tmpWare;
    }

	/**
	 * 获取当前课程信息和当前课程下所有课件信息
	 * 
	 * @return 当前课程信息和当前课程下所有课件信息
	 */
	private DownLoadCourse getCurrentCourse(DownLoadCourse course) {
		List<DownLoadLesson> lessons = getLessons(course.getCourseID());
		course.setLessonLists(lessons);
	  	return course;
	}


	private final static String adjustCourseId(String courseID){
		return courseID.contains("_") ? courseID : SpUtils.getUid()+"_"+courseID;
	}

 	public DownLoadCourse getCourse(String courseID){
		return getCourse(courseID,true);
	}
	/**
	 * 获取课程信息
	 * 
	 * @param courseID
	 * @return
	 */
	public DownLoadCourse getCourse(String courseID,boolean hasCourseWare) {
		courseID=adjustCourseId(courseID);

		DownLoadCourse course = new DownLoadCourse();
		Cursor cursor = db.query(DBHelper.TB_COURSE_NAME, null,
				DownLoadCourse.COURSE_ID + "=?  and "+DownLoadCourse.USER_ID+"=?",
				new String[] { courseID ,String.valueOf(UserInfoUtil.userId)},null, null, null);
		while (cursor.moveToNext()) {
			course.setCourseID(cursor.getString(cursor
					.getColumnIndex("course_ID")));
			course.setCourseName(cursor.getString(cursor
					.getColumnIndex("course_name")));
			course.setCourseType(cursor.getString(cursor
					.getColumnIndex("course_type")));
			course.setTotalNum(cursor.getInt(cursor.getColumnIndex("total_num")));
			course.setTeacher(cursor.getString(cursor.getColumnIndex("teacher")));
			course.setImagePath(
					cursor.getString(cursor.getColumnIndex("image_path")));
		}
		cursor.close();
		if(hasCourseWare){
			List<DownLoadLesson> downedLesson = getLessonsByStatus(
					courseID, "2");
			course.setLessonLists(downedLesson);
		}
 		return course;
	}
	public long insertDB(DownLoadCourse course, DownLoadLesson lesson){
		return insertDB(course,lesson,true);
	}
	/**
	 * 插入数据
	 * 
	 * @param course
	 *            课程信息
	 * @return 是否插入成功，-1为插入失败
	 */
	public long insertDB(DownLoadCourse course, DownLoadLesson lesson,boolean isPlayBack) {

		String tmpCourseID=adjustCourseId(course.getCourseID());
 		if (!isHasCurrentID(tmpCourseID,isPlayBack)) {
			ContentValues values = new ContentValues();
			values.put(DownLoadCourse.COURSE_ID, tmpCourseID);
			values.put(DownLoadCourse.COURSE_NAME, course.getCourseName());
			values.put(DownLoadCourse.COURSE_TYPE, course.getCourseType());
			values.put(DownLoadCourse.TEACHER, course.getTeacher());
			values.put(DownLoadCourse.TOTAL_NUM, course.getTotalNum());
			values.put(DownLoadCourse.IMAGE_PATH, course.getImagePath());
			values.put(DownLoadCourse.USER_ID,  SpUtils.getUid());
			String tmpTableName=isPlayBack?DBHelper.TB_COURSE_NAME:DBHelper.TB_COURSE_RECORRDNAME;
			LogUtils.e("isHasCurrentID2",tmpTableName);
			long courseResult = db.insert(tmpTableName,
					DownLoadCourse.COURSE_ID, values);
			if (courseResult == -1) {
				return courseResult;
			}
		}
		if (lesson != null && !isHasCurrentLesson(lesson.getDownloadID())) {
            ContentValues values = convertLessonToCv(lesson);
            long result = db.insert(DBHelper.TB_COURSEWARE_NAME,
                    DownLoadLesson.ID, values);
			return result;
		} else {
			return -1;
		}
	}

    private ContentValues convertLessonToCv(DownLoadLesson lesson) {
        if(lesson == null) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(DownLoadLesson.COURSE_ID, lesson.getCourseID());
        values.put(DownLoadLesson.SUBJECT_ID,
                lesson.getSubjectID());
        values.put(DownLoadLesson.SUBJECT_NAME,
                lesson.getSubjectName());
        values.put(DownLoadLesson.DOWNLOAD_ID,
                lesson.getDownloadID());
        values.put(DownLoadLesson.DOWN_STATUS, lesson.getDownStatus());
        values.put(DownLoadLesson.PLAY_PATH, lesson.getPlayPath());
        values.put(DownLoadLesson.PLAY_PROGRESS,
                lesson.getPlayProgress());
        values.put(DownLoadLesson.IMAGE_PATH, lesson.getImagePath());
        values.put(DownLoadLesson.COURSE_NUM, lesson.getCourseNum());
        values.put(DownLoadLesson.LESSON, lesson.getLesson());
        values.put(DownLoadLesson.PLAYER_TYPE, lesson.getPlayerType());
        values.put(DownLoadLesson.VIDEO_CLARITY, lesson.getClarity());
        values.put(DownLoadLesson.ENCRYPT_TYPE, lesson.getEncryptType());
        values.put(DownLoadLesson.SIGNAL_FILE_PATH, lesson.getSignalFilePath());
        values.put(DownLoadLesson.ROOM_ID, lesson.getRoomId());
        values.put(DownLoadLesson.SESSION_ID, lesson.getSessionId());
        values.put(DownLoadLesson.VIDEO_TOKEN, lesson.getVideoToken());
        values.put(DownLoadLesson.PLAY_PARAMS, lesson.getPlayParams());
        values.put(DownLoadLesson.DOWNLOAD_PROGRESS, lesson.getDownloadProgress());
        //添加REVERSE_1作为 录播有没有老师字段 REVERSE_2为老师名字
		values.put(DownLoadLesson.REVERSE_1, lesson.getReserve1());
		values.put(DownLoadLesson.REVERSE_2, lesson.getReserve2());
		values.put(DownLoadLesson.SPACE, lesson.getSpace());
		values.put(DownLoadLesson.DURATION,lesson.getDuration() );
		values.put(DownLoadLesson.USER_ID,UserInfoUtil.userId);
		values.put(DownLoadLesson.PLAY_PATH,lesson.getPlayPath());//数据迁移

		values.put(DownLoadLesson.CATALOG_ID,lesson.catalogId);//数据迁移
		values.put(DownLoadLesson.PARENT_ID,lesson.parentId);
        return values;
    }

	/**
	 * 是否包含课程ID
	 * 
	 * @param courseID
	 *            课程ID
	 * @return ture为已存，false为未存
	 */
    private boolean isHasCurrentID(String courseID,boolean isPlayback) {
        boolean flag;
        String tmpTableName=isPlayback?DBHelper.TB_COURSE_NAME:DBHelper.TB_COURSE_RECORRDNAME;
        LogUtils.e("isHasCurrentID",tmpTableName);
        Cursor cursor = db.query(tmpTableName, null,
                DownLoadCourse.COURSE_ID + "=? and "+DownLoadCourse.USER_ID+"=?",
				      new String[] { courseID ,String.valueOf(UserInfoUtil.userId)},null, null, null);
        flag = cursor.moveToNext();
        cursor.close();
        return flag;
    }

    private boolean isHasCurrentLesson(String downId) {
        boolean flag;
        Cursor cursor = db.query(DBHelper.TB_COURSEWARE_NAME, null,
                DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downId },
                null, null, null);
        flag = cursor.moveToNext();
        cursor.close();
        return flag;
    }

	/**
	 * 是否含有当前课程的课件
	 * 
	 * @param courseID
	 *            课程ID
	 * @return
	 */
	private boolean isHasLesson(String courseID) {
		boolean flag;
	 	Cursor cursor = db.query(DBHelper.TB_COURSEWARE_NAME, null,
				DownLoadLesson.COURSE_ID + "=? and "+DownLoadLesson.USER_ID+"=?",
				       new String[] { courseID,String.valueOf(UserInfoUtil.userId) },null, null, null);
        flag = cursor.moveToNext();
		cursor.close();
		return flag;
	}


	public int deleteLesson(String downID, String courseID){
		return deleteLesson(downID,courseID,true);
	}
	/**
	 * 删除某个课件
	 * 
	 * @param downID
	 *            下载ID
	 * @param courseID
	 *            课程ID
	 * @return 0未删除失败，1为删除成功
	 */
	public int deleteLesson(String downID, String courseID,boolean isPlayback) {
 		courseID=adjustCourseId(courseID);
		int result = db.delete(DBHelper.TB_COURSEWARE_NAME,
				DownLoadLesson.DOWNLOAD_ID + "=? and "+DownLoadLesson.USER_ID+"=?", new String[] { downID, String.valueOf(UserInfoUtil.userId)});
		LogUtils.e("deleteLesson",downID+","+result);
		if (!isHasLesson(courseID)) {

			if(!checkCourseHasHandout(courseID,false)){//添加讲义的类型关联判断
				String tmpTableName=isPlayback?DBHelper.TB_COURSE_NAME:DBHelper.TB_COURSE_RECORRDNAME;
				int deleteCourse = db.delete(tmpTableName,
						DownLoadCourse.COURSE_ID + "=? and "+DownLoadCourse.USER_ID+"=?", new String[] { courseID, String.valueOf(UserInfoUtil.userId)});
				return deleteCourse;
 			}

		}
		return result;
	}


	public int deleteCourse(String courseID, String status){
		return deleteCourse(courseID,status,true);
	}
	/**
	 * 根据下载状态删除课件
	 * 
	 * @param status
	 *            下载状态
	 * @param courseID
	 *            课程ID
	 * @return 0未删除失败，1为删除成功
	 */
	public int deleteCourse(String courseID, String status, boolean isPlayback) {
		courseID=adjustCourseId(courseID);
 		int result = db.delete(DBHelper.TB_COURSEWARE_NAME,
				DownLoadLesson.COURSE_ID + "=? and "+ DownLoadLesson.DOWN_STATUS + "=? and "+DownLoadLesson.USER_ID+"=?",
				 new String[]{ courseID, status, String.valueOf(UserInfoUtil.userId)});
		if (!isHasLesson(courseID)) {
			boolean hasHandout=checkCourseHasHandout(courseID,false);
			if(hasHandout) return result;
 			String tmpTableName = isPlayback ? DBHelper.TB_COURSE_NAME : DBHelper.TB_COURSE_RECORRDNAME;

			int deleteCourse = db.delete(tmpTableName,
					DownLoadCourse.COURSE_ID + "=? and "+DownLoadCourse.USER_ID+"=?",
					new String[]{courseID, String.valueOf(UserInfoUtil.userId)});
			return deleteCourse;
		}

		return result;
	}

/*	public int deleteCourse(String courseID){
		return deleteCourse(courseID);
	}
	*//**
	 * 删除某个课程
	 * 
	 * @param courseID
	 *            课程ID
	 * @return 0未删除失败，1为删除成功
	 *//*
	public int deleteCourse(String courseID,boolean isPlayback) {

		String tmpTableName=isPlayback?DBHelper.TB_COURSE_NAME:DBHelper.TB_COURSE_RECORRDNAME;
		int deleteCourse = db.delete(tmpTableName,
				DownLoadCourse.COURSE_ID + "=?", new String[] { courseID });
		if (deleteCourse == 0) {
			return deleteCourse;
		}
		int deleteCourseware = db.delete(DBHelper.TB_COURSEWARE_NAME,
				DownLoadLesson.COURSE_ID + "=?", new String[] { courseID });
		return deleteCourseware;
	}*/

	/**
	 * 更新课件下载状态
	 * 
	 * @return
	 */
	public int upDateDLStatus(String downID, int status, String path) {
		ContentValues value = new ContentValues();
		value.put(DownLoadLesson.DOWN_STATUS, status);
		value.put(DownLoadLesson.PLAY_PATH, path);
		int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
				DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
		return result;
	}

    public int upDateDLProgress(String downID, int progress) {
        ContentValues value = new ContentValues();
        value.put(DownLoadLesson.DOWNLOAD_PROGRESS, progress);
        int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
                DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
        return result;
    }

	public int upDateLessionToken(String downID, String token) {
		ContentValues value = new ContentValues();
		value.put(DownLoadLesson.VIDEO_TOKEN, token);
		int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
				DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
		return result;
	}

    public int upDateSinalFilePath(String downID, String path) {
        ContentValues value = new ContentValues();
        value.put(DownLoadLesson.SIGNAL_FILE_PATH, path);
        int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
                DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
        return result;
    }

	/**
	 * 更新课件播放进度
	 * 
	 * 
	 * @return
	 */
	public int upDatePlayProgress(String downID, int play_duration) {
        LogUtils.e("upDatePlayProgress",downID+","+play_duration);
		ContentValues value = new ContentValues();
		value.put(DownLoadLesson.PLAY_DURATION, play_duration);
		int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
				DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
		return result;
	}

	//老版的录播下载的时间没有
	public int upDatePlayProgress(String downID, int play_duration,int totalDuration) {
		LogUtils.e("upDatePlayProgress",downID+","+play_duration);
		//时间毫秒存储
		play_duration=play_duration*1000;
		totalDuration=totalDuration*1000;

		DownLoadLesson tmpLession= SQLiteHelper.getInstance().getCourseWare(downID);
		if(tmpLession!=null){
	 		if(tmpLession.getDuration()==0||tmpLession.getDuration()<play_duration){
				ContentValues value = new ContentValues();
				value.put(DownLoadLesson.PLAY_DURATION, play_duration);
				value.put(DownLoadLesson.DURATION, totalDuration);
				int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
						DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
				return result;

			}
			return upDatePlayProgress(downID,play_duration);
		}
		return 0;
		//return upDatePlayProgress(downID,play_duration);
	}

	/**
	 * 更新课件下载存储大小、时长
	 * 
	 * @return
	 */
    public int upDateDLSpaceAndDuration(String downID, long space, int duration) {
        ContentValues value = new ContentValues();
        value.put(DownLoadLesson.SPACE, space);
        value.put(DownLoadLesson.DURATION, duration);
        value.put(DownLoadLesson.PLAY_DURATION, 0);
        int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
                DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
        return result;
    }

	/**
	 * 更新课件下载状态
	 * 
	 * @return
	 */
	public int upDateDLStatus(String downID, int status) {
		ContentValues value = new ContentValues();
		value.put(DownLoadLesson.DOWN_STATUS, status);
		int result = db.update(DBHelper.TB_COURSEWARE_NAME, value,
				DownLoadLesson.DOWNLOAD_ID + "=?", new String[] { downID });
		return result;
	}


	/*讲议的操作
	*
	* */
	public List<DownHandout> getAllDownHandouts(String courseId,boolean onlyFinish) {

		List<DownHandout> Handoutlessons = new ArrayList<>();
 		Cursor cursor;
		if(onlyFinish){
			cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
					DownHandout.DOWN_STATUS + "=? and "+DownHandout.COURSE_ID + "=? and "+ DownHandout.USER_ID + "=?",
					new String[] {String.valueOf(DownBtnLayout.FINISH), courseId ,String.valueOf(UserInfoUtil.userId)}, null, null, null);;
		}else {
			cursor =  db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
					DownHandout.COURSE_ID + "=? and "+ DownHandout.USER_ID + "=?",
					new String[] { courseId ,String.valueOf(UserInfoUtil.userId)}, null, null, null);;
		}
 		/*Cursor cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
				DownHandout.COURSE_ID + "=? and "+ DownHandout.USER_ID + "=?",
				new String[] { courseId ,String.valueOf(UserInfoUtil.userId)}, null, null, null);*/
 		while (cursor.moveToNext()) {

			DownHandout lesson =convertHandoutFromCv(cursor);
 			Handoutlessons.add(lesson);
 		}
		cursor.close();
		return Handoutlessons;
	}

	private DownHandout convertHandoutFromCv(Cursor cursor) {
		if (cursor == null) {
			return null;
		}
		DownHandout lesson = new DownHandout();
		lesson.setSubjectID(cursor.getString(cursor.getColumnIndex(DownHandout.SUBJECT_ID )));
		lesson.setSubjectName(cursor.getString(cursor.getColumnIndex(DownHandout.SUBJECT_NAME)));
		lesson.setDownStatus(cursor.getInt(cursor.getColumnIndex(DownHandout.DOWN_STATUS)));
		lesson.setCourseID(cursor.getString(cursor.getColumnIndex(DownHandout.COURSE_ID)));
		lesson.setSpace(cursor.getInt(cursor.getColumnIndex(DownHandout.SPACE)));
		lesson.setFileType(cursor.getInt(cursor.getColumnIndex(DownHandout.FILE_TYPE)));
		lesson.setReserve1(cursor.getString(cursor.getColumnIndex(DownHandout.REVERSE_1)));
		lesson.setFileUrl(cursor.getString(cursor.getColumnIndex(DownHandout.FILE_URL)));
		lesson.setReserve2(cursor.getString(cursor.getColumnIndex(DownHandout.REVERSE_2)));
		lesson.setUserId(cursor.getLong(cursor.getColumnIndex(DownHandout.USER_ID)));
		return lesson;
	}

	//
	public List<DownHandout> getAllUnFinishDownHandouts(boolean includePause) {

		List<DownHandout> Handoutlessons = new ArrayList<>();
		Cursor cursor;
		if(includePause){
			 cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
					DownHandout.DOWN_STATUS + "!=? and "+ DownHandout.USER_ID + "=?",
					new String[] { String.valueOf(DownBtnLayout.FINISH) ,String.valueOf(UserInfoUtil.userId)}, null, null, null);
 		}else {
			cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
					DownHandout.DOWN_STATUS + "!=? and "+DownHandout.DOWN_STATUS +"!=? and "+ DownHandout.USER_ID + "=?",
					new String[] { String.valueOf(DownBtnLayout.FINISH) ,String.valueOf(DownBtnLayout.PAUSE),String.valueOf(UserInfoUtil.userId)}, null, null, null);
 		}

		while (cursor.moveToNext()) {
 			DownHandout lesson =convertHandoutFromCv(cursor);
			Handoutlessons.add(lesson);
		}
		cursor.close();
		return Handoutlessons;
	}

	public int getUnFinishHandoutCount(){

		Cursor cursor2 = db.query(DBHelper.TB_COURSE_HANDOUTNAME, new String[]{"COUNT(*) AS COUNT_TABLE"},
				DownHandout.DOWN_STATUS + "!=? and "+ DownHandout.USER_ID + "=?",
				new String[] { String.valueOf(DownBtnLayout.FINISH) ,String.valueOf(UserInfoUtil.userId)}, null, null, null);
		int tmpCount=0;
		while (cursor2.moveToNext()) {
			tmpCount=cursor2.getInt(0);

		}
		cursor2.close();

		LogUtils.e("getUnFinishHandoutCourse",tmpCount+"____");
		return tmpCount;
	}

	public DownLoadCourse getUnFinishHandoutCourse() {

		List<DownHandout> Handoutlessons = new ArrayList<>();
		Cursor cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
				DownHandout.DOWN_STATUS + "!=? and "+ DownHandout.USER_ID + "=?",
				new String[] { String.valueOf(DownBtnLayout.FINISH) ,String.valueOf(UserInfoUtil.userId)}, null, null, null,"1");

		String tmpCourseId="";
		int downStatus=0;
		while (cursor.moveToNext()) {
			tmpCourseId=cursor.getString(cursor.getColumnIndex(DownHandout.COURSE_ID));
			downStatus=cursor.getInt(cursor.getColumnIndex(DownHandout.DOWN_STATUS));
		}
		cursor.close();
		if(TextUtils.isEmpty(tmpCourseId)) return null;

		/*cursor = db
				.rawQuery("select count(*) as count from coursehandout where down_status!=2 and user_id="+UserInfoUtil.userId,null);*/


		DownLoadCourse tmpCourse=getCourses(true,tmpCourseId);
		if(null!=tmpCourse){
			tmpCourse.setChangeStatus(downStatus);

		}

		return tmpCourse;

	}



	public int upDateHandoutStatus(String subjectId,int status){
		ContentValues value = new ContentValues();
		value.put(DownHandout.DOWN_STATUS, status);
		int result = db.update(DBHelper.TB_COURSE_HANDOUTNAME, value,
				DownLoadLesson.SUBJECT_ID + "=? and "+ DownHandout.USER_ID + "=?", new String[] { subjectId,String.valueOf(UserInfoUtil.userId) });
		return result;
 	}

	private boolean isHasCurrentHandout(String subjectId) {
		boolean flag;
		Cursor cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
				DownHandout.SUBJECT_ID + "=? and "+ DownHandout.USER_ID + "=?", new String[] { subjectId,String.valueOf(UserInfoUtil.userId) },
				null, null, null);
		flag = cursor.moveToNext();
		cursor.close();
		return flag;
	}

	/**
	 * 是否含有当前课程的讲义
	 *
	 * @param courseID
	 *            课程ID
	 * @return
	 */
	private boolean checkCourseHasHandout(String courseID,boolean onlyFinished) {
		String delCourseId=courseID;

		if(delCourseId.contains("_")) {
			delCourseId=delCourseId.split("_")[1];
 		}
		boolean flag;
		Cursor cursor;
		if(onlyFinished){
			cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
					DownHandout.DOWN_STATUS + "=? and "+DownLoadLesson.COURSE_ID + "=? and "+DownLoadLesson.USER_ID+"=?",
					new String[] { String.valueOf(DownBtnLayout.FINISH),delCourseId,String.valueOf(UserInfoUtil.userId) },null, null, null);

		}else {
			cursor = db.query(DBHelper.TB_COURSE_HANDOUTNAME, null,
					DownLoadLesson.COURSE_ID + "=? and "+DownLoadLesson.USER_ID+"=?",
					new String[] { delCourseId,String.valueOf(UserInfoUtil.userId) },null, null, null);
		}

		flag = cursor.moveToNext();
		cursor.close();
		return flag;
	}

	public long insertHandOut(DownHandout handout) {
 		if (handout != null && !isHasCurrentHandout(handout.getSubjectID())) {
			//ContentValues values = convertLessonToCv(lesson);

			ContentValues values = new ContentValues();
			values.put(DownHandout.SUBJECT_ID, handout.getSubjectID());
			values.put(DownHandout.SUBJECT_NAME,handout.getSubjectName());
			values.put(DownHandout.DOWN_STATUS,handout.getDownStatus());
			values.put(DownHandout.COURSE_ID,handout.getCourseID());
			values.put(DownHandout.FILE_TYPE, handout.getFileType());
			values.put(DownHandout.SPACE, handout.getSpace());
			values.put(DownHandout.FILE_URL,handout.getFileUrl());
			values.put(DownHandout.REVERSE_1, handout.getReserve1());
			values.put(DownHandout.REVERSE_2, handout.getReserve2());
			values.put(DownHandout.USER_ID, UserInfoUtil.userId);

			long result = db.insert(DBHelper.TB_COURSE_HANDOUTNAME,DownHandout.ID, values);
			return result;
		} else {
			return -1;
		}
	}


	public int deleteSingleHandOut(String subjectId,String courseID) {
  		int result = db.delete(DBHelper.TB_COURSE_HANDOUTNAME,
				DownHandout.SUBJECT_ID + "=? and "+DownHandout.USER_ID+"=?", new String[] { subjectId, String.valueOf(UserInfoUtil.userId)});
		if (!isHasLesson(adjustCourseId(courseID))) {

			if(!checkCourseHasHandout(courseID,false)){//添加讲义的类型关联判断

				String delCourseID=adjustCourseId(courseID);
				int deleteCourse = db.delete(DBHelper.TB_COURSE_NAME,
						DownLoadCourse.COURSE_ID + "=? and "+DownLoadCourse.USER_ID+"=?", new String[] { delCourseID, String.valueOf(UserInfoUtil.userId)});
				return deleteCourse;
			}

		}
 		return result;
	}

	//删除已下载完成的讲义
	public int deleteFinishedHandOut(String courseID){
 		String delCourseId=courseID;
 		if(delCourseId.contains("_")) {
			delCourseId=delCourseId.split("_")[1];
		}
		int result = db.delete(DBHelper.TB_COURSE_HANDOUTNAME,
				DownHandout.DOWN_STATUS + "=? and "+DownHandout.COURSE_ID + "=? and "+DownHandout.USER_ID+"=?",
				new String[] { String.valueOf(DownBtnLayout.FINISH), delCourseId, String.valueOf(UserInfoUtil.userId)});
		return result;
	}

	public int deleteBatchHandOut(List<String> subjectId,String courseID) {

		if(ArrayUtils.isEmpty(subjectId)) return -1;

		for (int i = 0; i < subjectId.size(); i++) {

			int result = db.delete(DBHelper.TB_COURSE_HANDOUTNAME,
					DownHandout.SUBJECT_ID + "=? and "+DownHandout.USER_ID+"=?", new String[] { subjectId.get(i), String.valueOf(UserInfoUtil.userId)});
			if (i == subjectId.size() - 1) {
				if (!isHasLesson(adjustCourseId(courseID))) {

					if(!checkCourseHasHandout(courseID,false)){//添加讲义的类型关联判断

						String delCourseID=adjustCourseId(courseID);
						int deleteCourse = db.delete(DBHelper.TB_COURSE_NAME,
								DownLoadCourse.COURSE_ID + "=? and "+DownLoadCourse.USER_ID+"=?", new String[] { delCourseID, String.valueOf(UserInfoUtil.userId)});
						return deleteCourse;
					}

				}
			}
		}
   		return -1;
	}
}
