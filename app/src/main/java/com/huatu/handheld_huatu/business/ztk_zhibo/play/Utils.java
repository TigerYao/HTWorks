package com.huatu.handheld_huatu.business.ztk_zhibo.play;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类;
 * 
 * @author Archer
 * 
 */
public class Utils {

	/**
	 * 判断手机上是否已经安装某一应用;
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAvilible(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager(); // 获取packagemanager;
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0); // 获取所有已安装程序的包信息;
		List<String> pName = new ArrayList<String>(); // 用于存储所有已安装程序的包名;

		if (pinfo != null) { // 从pinfo中将包名字逐一取出，压入pName list中;
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}

		return pName.contains(packageName); // 判断pName中是否有目标程序的包名，有TRUE，没有FALSE;
	}

	/**
	 * 改变时间的显示
	 * 
	 * @param time
	 *            时间大小 单位：秒
	 * @return 格式后的时间 0:0:0
	 */
	public static String getTime(long time, int type) {
		StringBuffer sb = new StringBuffer();

		int HOUR = 60 * 60;
		int MINUTE = 60;
		int SECOND = 1;

		int hour = (int) (time / HOUR);
		long remainTime = time % HOUR;

		int minute = (int) (remainTime / MINUTE);
		remainTime = remainTime % MINUTE;

		int second = (int) (remainTime / SECOND);

		String formatTime = String.format("%d:%d:%d", hour, minute, second);
		String[] timeArr = formatTime.split(":");
		String formatHour = timeArr[0];// 时
		String formatMinute = timeArr[1];// 分
		String formatSecond = timeArr[2];// 秒

		if (type == 1) {
			if (!"0".equals(formatHour)) {
				sb.append(hour).append("小时");
			}

			if (!"0".equals(formatMinute)) {
				sb.append(minute).append("分");
			}
			
			if ("0".equals(formatHour) && "0".equals(formatMinute)) {
				sb.append("0分");
			}
		} else if (type == 2) {
			sb.append(formatTime);
		}

		return sb.toString();
	}
	public static String formatTime(long ms) {

		int ss = 1;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		String strDay = day < 10 ? "0" + day : "" + day; // 澶�
		String strHour = hour < 10 ? "0" + hour : "" + hour;// 灏忔椂
		String strMinute = minute < 10 ? "0" + minute : "" + minute;// 鍒嗛挓
		String strSecond = second < 10 ? "0" + second : "" + second;// 绉�
		String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
				+ milliSecond;// 姣
		strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : ""
				+ strMilliSecond;

		return strDay + ";" + strHour + ";" + strMinute + ";" + strSecond;
	}

	//判断当前应用是否是debug状态
	public static boolean isApkInDebug(Context context) {
		try {
			ApplicationInfo info = context.getApplicationInfo();
			return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		} catch (Exception e) {
			return false;
		}
	}

}
