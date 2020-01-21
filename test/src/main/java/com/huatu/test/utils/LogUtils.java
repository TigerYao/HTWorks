package com.huatu.test.utils;

import android.text.TextUtils;
import android.util.Log;


import com.huatu.music.bean.Music;
import com.huatu.test.BuildConfig;

import java.util.Locale;

/**
 * Created by saiyuan on 2016/10/13.
 */
public class LogUtils {
    public static String TAG = "LogUtils";
    private static final boolean LOGGABLE = BuildConfig.DEBUG;
    private static final boolean isLogOnFile = false;
    public static final String APP_NAME = "Netschool";
    static StringBuffer mSb = new StringBuffer();

    public static int v(String msg) {
        return v("", msg);
    }

    public static int d(String msg) {
        return d("", msg);
    }

    public static int i(String msg) {
        return i("", msg);
    }

    public static int w(String msg) {
        return w("", msg);
    }

    public static int e(String msg) {
        return e("", msg);
    }

    public static int v(String tag, String msg) {
        logOnFile(msg);
        return LOGGABLE ? Log.w(APP_NAME, getTracePrefix("v") + msg) : 0;
    }

    public static int d(String tag, String msg) {
        logOnFile(msg);
        return LOGGABLE ? Log
                .w(APP_NAME, getTracePrefix("d") + tag + " " + msg) : 0;
    }

    public static int d(Object... objects) {
        Object[] mAry = objects;
        if (mAry == null) {
            Log.d("tag","d --> null");
            return -1;
        }
        mSb.setLength(0);
        for (int i = 0; i < mAry.length; i++) {
            mSb.append(mAry[i] + "\n");
        }
        logOnFile(mSb.toString());
        return LOGGABLE ? Log
                .w(APP_NAME, getTracePrefix("d") + " " + mSb.toString()) : 0;
    }

    public static int i(String tag, String msg) {
        logOnFile(msg);
        return LOGGABLE ? Log
                .w(APP_NAME, getTracePrefix("i") + tag + " " + msg) : 0;
    }

    public static int w(String tag, String msg) {
        logOnFile(msg);
        return LOGGABLE ? Log
                .w(APP_NAME, getTracePrefix("w") + tag + " " + msg) : 0;
    }

    public static int e(String tag, String msg) {
        logOnFile(msg);
        return LOGGABLE ? Log
                .e(APP_NAME, getTracePrefix("e") + tag + " " + msg) : 0;
    }

    public static void ex(String tag, String msg) {

        if(LOGGABLE){Log.e(tag, msg);
        }
    }

    private static synchronized void logOnFile(String msg) {
        if(!isLogOnFile || TextUtils.isEmpty(msg)) {
            return;
        }
       /* String filePath = FileUtil.getFilePath("log", "log.txt");
        boolean succ = FileUtil.createFile(filePath, false);
        if(!succ) {
            return;
        }
        File file = new File(filePath);
        if(file == null || !file.exists()) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write((msg + "\n").getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static int e(Exception exception) {
        StackTraceElement[] ste = exception.getStackTrace();
        StringBuilder sb = new StringBuilder();
        try {
            String name = exception.getClass().getName();
            String message = exception.getMessage();
            String content = (message != null) ? (name + ":" + message) : name;
            sb.append(content + "\n");
            for (StackTraceElement s : ste) {
                sb.append(s.toString() + "\n");
            }
        } catch (Exception e) {
        }
        return w("", sb.toString());
    }

    public static int v(String tag, String msg, Throwable tr) {
        return LOGGABLE ? Log.v(APP_NAME, tag + ": " + msg, tr) : 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        return LOGGABLE ? Log.d(APP_NAME, tag + ": " + msg, tr) : 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        return LOGGABLE ? Log.i(APP_NAME, tag + ": " + msg, tr) : 0;
    }
    public static void i(String tag, String msg, Object arg1, Object arg2) {
        if(LOGGABLE){Log.i(tag, formatString(msg, new Object[]{arg1, arg2}));}
    }
    private static String formatString(String str, Object... args) {
        return String.format((Locale) null, str, args);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return LOGGABLE ? Log.w(APP_NAME, tag + ": " + msg, tr) : 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        return LOGGABLE ? Log.e(APP_NAME, tag + ": " + msg, tr) : 0;
    }

    private static String getTracePrefix(String logLevel) {
        StackTraceElement[] sts = new Throwable().getStackTrace();
        StackTraceElement st = null;
        for (int i = 0; i < sts.length; i++) {
            if (sts[i].getMethodName().equalsIgnoreCase(logLevel)
                    && i + 2 < sts.length) {

                if (sts[i + 1].getMethodName().equalsIgnoreCase(logLevel)) {
                    st = sts[i + 2];
                    break;
                } else {
                    st = sts[i + 1];
                    break;
                }
            }
        }
        if (st == null) {
            return "";
        }

        String clsName = st.getClassName();
        if (clsName.contains("$")) {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1,
                    clsName.indexOf("$"));
        } else {
            clsName = clsName.substring(clsName.lastIndexOf(".") + 1);
        }
        return clsName + "-> " + st.getMethodName() + "():";
    }




    public static void logInfo(String msg) {
        logInfo(null, msg);
    }

    public static void logInfo(Object tag, String msg) {
        if (!LOGGABLE) {
            return;
        }

        if (tag != null) {
            if (tag instanceof String) {
                Log.i(TAG, tag + ": " + msg);
            } else {
                Log.i(TAG, tag.getClass().getSimpleName() + ": " + msg);
            }
        } else {
            Log.i(TAG, msg);
        }
    }

    public static void logError(Object tag, String msg) {
        if (!LOGGABLE) {
            return;
        }

        if (tag != null) {
            if (tag instanceof String) {
                Log.e(TAG, tag + ": " + msg);
            } else {
                Log.e(TAG, tag.getClass().getSimpleName() + ": " + msg);
            }
        } else {
            Log.e(TAG, msg);
        }
    }

    public static void logWarn(Object tag, String msg) {
        if (!LOGGABLE) {
            return;
        }

        if (tag != null) {
            if (tag instanceof String) {
                Log.v(TAG, tag + ": " + msg);
            } else {
                Log.v(TAG, tag.getClass().getSimpleName() + ": " + msg);
            }
        } else {
            Log.v(TAG, msg);
        }
    }

    public static Music convertCoursewareToMusic(){

        //"videoId":"16091936","token":"w_ZgK9dOGftMyA0LsBVeTLKXgwsBKRfiE0xsO4HlQ9acpc4W_VwHrDTSEzZrILF4"

      //  if(checkType&&(courseWareInfo.videoType!=5)) return null;

        Music tmpDto=new Music();
        tmpDto.type="online";
        //tmpDto.id=54;
        tmpDto.mid="54";
        tmpDto.title="courseWareInfo.title";
        tmpDto.album="courseWareInfo.title";
        tmpDto.artist="courseWareInfo.teacher";
      /*  if(null!=courseInfo){
            tmpDto.album=courseInfo.title;
            tmpDto.albumId=String.valueOf(courseInfo.courseId);
        }*/

        tmpDto.artistId="";

        tmpDto.trackNumber=0;
        tmpDto.duration=0;
        tmpDto.isLove=false;
        tmpDto.isOnline=true;
        tmpDto.isCp=true;

        //  tmpDto.uri="http://audio01.dmhmusic.com/71_53_T10047981991_128_4_1_0_sdk-cpm/0207/M00/7B/93/ChR461z87ZCAcB1eAD_XaNRR9N4986.mp3?xcode=e39838a8d114bbea4f4df19d92731d19f5f4796";
          /*  tmpDto.coverUri="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_150,h_150";
            tmpDto.coverBig="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_450,h_450";
            tmpDto.coverSmall="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_90,h_90";*/

        /*if(null!=courseInfo)*/{
            tmpDto.coverUri="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_450,h_450";
            tmpDto.coverBig="http://qukufile2.qianqian.com/data2/pic/45cb673e5bb4fe1214073c6ce3bc849f/660147167/660147167.jpg@s_1,w_450,h_450";
        }

        tmpDto.coverSmall="";
        tmpDto.fileSize=4183912;
        tmpDto.date=0;
        tmpDto.isDl=true;
        tmpDto.quality=1;

       /* if(!TextUtils.isEmpty(courseWareInfo.targetPath)){
            tmpDto.uri=courseWareInfo.targetPath;
            tmpDto.type="local";
        }
        tmpDto.id=StringUtils.parseLong(courseWareInfo.videoId);
        tmpDto.collectId=courseWareInfo.token;*/
        return tmpDto;
    }

    public static synchronized void logCrashOnFile(String msg) {
       /* if(!TextUtils.isEmpty(msg)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            String filePath = FileUtil.getFilePath("log/crash", "crash_" + sdf.format(Long.valueOf(System.currentTimeMillis())) + ".txt");
            boolean succ = FileUtil.createFile(filePath, false);
            if(succ) {
                File file = new File(filePath);
                if(file != null && file.exists()) {
                    try {
                        FileOutputStream e = new FileOutputStream(file, true);
                        e.write((msg + "\n").getBytes());
                        e.flush();
                        e.close();
                    } catch (FileNotFoundException var6) {
                        var6.printStackTrace();
                    } catch (Exception var7) {
                        var7.printStackTrace();
                    }

                }
            }
        }*/
    }
}
