package com.huatu.handheld_huatu.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.haibin.calendarview.Calendar;

import java.util.TimeZone;

public class CalendarReminderUtils {

    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = SpUtils.getUname();
    private static String CALENDARS_ACCOUNT_NAME = SpUtils.getAboutEmail();
    private static String CALENDARS_ACCOUNT_TYPE = SpUtils.getAboutPhone();
    private static String CALENDARS_DISPLAY_NAME = "华图在线账户";
    public static final int REQUEST_CODE = 0X1010;
    public static final int REQUEST_READ_CODE = 0X1011;

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }


    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    private static int checkCalendarAccount(Context context) {

        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null);
        try {
            if (userCursor == null) { //查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    /**
     * 添加日历事件
     */
    public static boolean addCalendarEvent(Activity context, String title, String description, long reminderTime, long endTime) {
        if (context == null) {
            return false;
        }
        if (!checkPermission(context, REQUEST_CODE))
            return false;
        int calId = checkAndAddCalendarAccount(context); //获取日历账户的id
        if (calId < 0) { //获取账户id失败直接返回，添加日历事件失败
            ToastUtils.showEssayToast("没有日历账号，请先添加账户");
            return false;
        }
        LogUtils.d("...reminderTime..."+reminderTime);
        try {
            ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.TITLE, "华图在线模考大赛马上开始了，快来考试吧~");
            event.put(CalendarContract.Events.DESCRIPTION, description);
            event.put(CalendarContract.Events.CALENDAR_ID, calId); //插入账户的id
            event.put(CalendarContract.Events.DTSTART, reminderTime);
            event.put(CalendarContract.Events.DTEND, endTime);
            event.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, context.getPackageName());
            event.put(CalendarContract.Events.STATUS, CalendarContract.Events.STATUS_CONFIRMED);
            event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
            TimeZone tz = TimeZone.getDefault(); // 获取默认时区
            event.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());//这个是时区，必须有

            return insertCalendar(context, event);
        } catch (Exception e) {
            ToastUtils.showEssayToast("提醒添加失败");
            return false;
        }
    }

    private static boolean insertCalendar(Activity context, ContentValues event) {
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALENDER_EVENT_URL), event); //添加事件
        if (newEvent == null) { //添加日历事件失败直接返回
            ToastUtils.showEssayToast("提醒添加失败");
            return false;
        }

        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, 10);// 提前10分钟有提醒
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALENDER_REMINDER_URL), values);
        if (uri == null || ContentUris.parseId(uri) == 0) { //添加事件提醒失败直接返回
            ToastUtils.showEssayToast("提醒添加失败");
            return false;
        }
        ToastUtils.showEssayToast("已成功加入日历提醒");
        return true;
    }

    /**
     * 删除日历事件
     */
    public static void deleteCalendarEvent(Context context, String title) {
        if (context == null) {
            return;
        }
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    /**
     * 查询日历事件
     */
    public static boolean queryCalendarEvent(Activity context, String des, String startTime) {
        boolean isAdded = false;
        if (context == null || !checkPermission(context, REQUEST_READ_CODE)) {
            return true;
        }
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, CalendarContract.Events.DESCRIPTION +"='" +
                des +"' AND " + CalendarContract.Events.DTSTART + "="+startTime, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                isAdded = false;
            } else  if (eventCursor.getCount() > 0) {
                eventCursor.moveToFirst();
                String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                LogUtils.d(eventTitle);
                isAdded = true;
            }else {
                isAdded = false;
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        return isAdded;
    }

//    /**
//     * 日历权限检查
//     *
//     * @param ctx
//     * @return
//     */
//    public static boolean checkReadPermission(Activity ctx) {
//        int checkSelfPermission;
//        try {
//            checkSelfPermission = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_CALENDAR);
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//            ToastUtils.showEssayToast("获取日历权限失败");
//            return true;
//        }
//
//        // 如果有授权，走正常插入日历逻辑
//        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        } else {
//            // 如果没有授权，就请求用户授权
//            ActivityCompat.requestPermissions(ctx, new String[]{
//                    Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_READ_CODE);
//        }
//        return false;
//    }
    /**
     * 日历权限检查
     *
     * @param ctx
     * @return
     */
    public static boolean checkPermission(Activity ctx, int requedCode) {
        int checkSelfPermissionWrite, checkSelfPermissionRead;
        try {
            checkSelfPermissionWrite = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALENDAR);
            checkSelfPermissionRead = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_CALENDAR);
        } catch (RuntimeException e) {
            e.printStackTrace();
            ToastUtils.showEssayToast("获取日历权限失败, 暂时无法使用提醒功能");
            return false;
        }

        // 如果有授权，走正常插入日历逻辑
        if (checkSelfPermissionWrite == PackageManager.PERMISSION_GRANTED && checkSelfPermissionRead == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, requedCode);
        }
        return false;
    }

    public static void acceptPermission(Activity context, int requestCode, String title, String description, long reminderTime, long endTime) {
        if (requestCode == REQUEST_CODE) {
            addCalendarEvent(context, title, description, reminderTime, endTime);
        }
    }
}