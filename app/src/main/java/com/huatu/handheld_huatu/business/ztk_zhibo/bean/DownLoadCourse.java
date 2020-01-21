package com.huatu.handheld_huatu.business.ztk_zhibo.bean;

import android.text.TextUtils;

import com.huatu.utils.ArrayUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 课程Bean Created by DongDong on 2016/4/7.
 */
public class DownLoadCourse implements Serializable {
	public static final String COURSE_ID = "course_ID";
	public static final String COURSE_NAME = "course_name";
	public static final String COURSE_TYPE = "course_type";
	public static final String TEACHER = "teacher";
	public static final String IMAGE_PATH = "image_path";
	public static final String TOTAL_NUM = "total_num";
	public static final String USER_ID ="user_id";

	public static final String LEARN_PERCENT = "lean_percent";
	public static final String  REVERSE_1 = "reserve1";
	public static final String  REVERSE_2 = "reserve2";
	public static final String  QQ = "qq";


	private String course_ID; // 课程ID
	private String course_name; // 课程名称
	private String course_type; // 科目类型
	private String teacher; // 讲师
	private String image_path; // 图片
	private String imageURL;
	private boolean isSelect;// 是否被选中
	private int totalNum;// 总共课程数
	public long userId;

	public long totalSpace;


	public String reserve1;
	public String reserve2;
	public String qq;
	public boolean    hasHandout;//是否有讲义


	private List<DownLoadLesson> lists;
	private int changeStatus; // 0默认为不更新，1更新课程，2更新课件，3更新课程和课件

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public int getChangeStatus() {
		return changeStatus;
	}

	public void setChangeStatus(int changeStatus) {
		this.changeStatus = changeStatus;
	}

	public String getCourseID() {
		return course_ID;
	}


	public String getRealCourseID() {
        if(TextUtils.isEmpty(course_ID)) return course_ID;
        return course_ID.contains("_") ? course_ID.split("_")[1]:course_ID;
 	}

	public void setCourseID(String course_ID) {
		this.course_ID = course_ID;
	}

	public String getCourseName() {
		return course_name;
	}

	public void setCourseName(String course_name) {
		this.course_name = course_name;
	}

	public String getCourseType() {
		return course_type;
	}

	public void setCourseType(String course_type) {
		this.course_type = course_type;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getImagePath() {
		return image_path;
	}

	public void setImagePath(String image_path) {
		this.image_path = image_path;
	}

	public List<DownLoadLesson> getLessonLists() {
		return lists;
	}

	public void setLessonLists(List<DownLoadLesson> lists) {
		this.lists = lists;

	}


}
