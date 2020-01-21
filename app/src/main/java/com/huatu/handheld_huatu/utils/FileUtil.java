package com.huatu.handheld_huatu.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.text.TextUtils;

import android.util.Log;

import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.handheld_huatu.base.BaseActivity;
import com.huatu.handheld_huatu.business.other.PdfViewFragment;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.List;

//import static com.gensee.utils.FileUtil.getSDCardPath;

/**
 * Created by saiyuan on 2016/11/17.
 */

public class FileUtil {

    /**
     * 获取手机内部空间总大小
     *
     * @return 大小，字节为单位
     */
    static public long getTotalInternalMemorySize() {
        //获取内部存储根目录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSize();
        //区块总数
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return 大小，字节为单位
     */
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize/ 1024L;
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static long getAvailableSpaceSize(){
        if(isSDCardEnable()) return getSDAvailableSize();
        return getAvailableInternalMemorySize();

    }
    /**
     * 获取手机外部总空间大小
     *
     * @return 总大小，字节为单位
     */
    static public long getTotalExternalMemorySize() {
        if (isSDCardEnable()) {
            //获取SDCard根目录
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    private static long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long size = availableBlocks * blockSize / 1024L;
        return size;
         //return Formatter.formatFileSize(MainActivity.this, blockSize * availableBlocks);
    }

    public static String mCacheDirPath = "";
    private static final String TAG = "FileUtil";
    public static String getCacheDir() {
        if(!TextUtils.isEmpty(mCacheDirPath)) {
            return mCacheDirPath;
        }
        String cacheDirPath = "";
        Context context = UniApplicationContext.getContext();
        if(context == null) {
            return "";
        }
        File externalCacheDir;
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
            externalCacheDir = context.getExternalCacheDir();
        } else {
            externalCacheDir = null;
        }
        File cacheDir = context.getCacheDir();
        if (externalCacheDir != null) {
            cacheDirPath = externalCacheDir.getAbsolutePath();
        } else if (cacheDir != null) {
            cacheDirPath = cacheDir.getAbsolutePath();
        }
        mCacheDirPath = cacheDirPath;
        return cacheDirPath;
    }

    public static String getFilePath(String parentDir, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.i("FileUtil", "fileName is empty");
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String cacheDirPath = getCacheDir();
        if (TextUtils.isEmpty(cacheDirPath)) {
            return null;
        }
        stringBuilder.append(cacheDirPath);
        if (!TextUtils.isEmpty(parentDir)) {
            stringBuilder.append(File.separator + parentDir);
        }
        stringBuilder.append(File.separator + fileName);
        return stringBuilder.toString();
    }

    public static String getDownloadFilePath(String parentDir, String fileName) {
        if(TextUtils.isEmpty(fileName)) {
            Log.i("FileUtil", "fileName is empty");
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            if("mounted".equals(Environment.getExternalStorageState())) {
                File externalCacheDir = Environment.getExternalStorageDirectory();
                String cacheDirPath = externalCacheDir.getAbsolutePath();
                if(TextUtils.isEmpty(cacheDirPath)) {
                    return null;
                } else {
                    stringBuilder.append(cacheDirPath + File.separator + "Download");
                    if(!TextUtils.isEmpty(parentDir)) {
                        stringBuilder.append(File.separator + parentDir);
                    }

                    stringBuilder.append(File.separator + fileName);
                    return stringBuilder.toString();
                }
            } else {
                return getFilePath(parentDir,fileName);
            }
        }
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.i("FileUtil", "filePath is empty");
            return false;
        }
        File file = new File(filePath);
        return file != null && file.exists();
    }

    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.i("FileUtil", "filePath is empty");
            return false;
        }
        if (!isFileExist(filePath)) {
            return true;
        }
        File file = new File(filePath);
        return file.delete();
    }

    public static boolean createFile(String filePath) {
        return createFile(filePath, true);
    }

    public static String getFileSize(String filePath) {
        DecimalFormat df = new DecimalFormat("#0.00");
        String size = "";
        if (!isFileExist(filePath)) {
            size = "";
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            int available = fileInputStream.available();
            size = df.format((double) available / 1048576) + "M";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public static long getFileSize(File file) {
        long size = 0;
        if(file != null && file.exists()) {
            if(file.isFile()) {
                return file.length();
            } else {
                File[] child = file.listFiles();
                if(child != null) {
                    for(int i = 0; i < child.length; i++) {
                        size += getFileSize(child[i]);
                    }
                }
            }
        }
        return size;
    }

    public static int getFileSizeInt(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            int available = fileInputStream.available();
            return available;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*
     * isOverride: if file is exist, delete and renew one
     */
    public static boolean createFile(String filePath, boolean isOverride) {
        if (TextUtils.isEmpty(filePath)) {
            Log.i("FileUtil", "filePath is empty");
            return false;
        }
        File file = new File(filePath);
        if (file != null && file.exists()) {
            if (!isOverride) {
                return true;
            }
            file.delete();
        }
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        boolean isSuccess = false;
        try {
            isSuccess = file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess && file.exists();
    }

    /**
     * 获取SD卡剩余空间大小，单位B
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String state = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File filePath = Environment.getExternalStorageDirectory();
                StatFs statFs = new StatFs(filePath.getPath());
                long blockSize = statFs.getBlockSize();
                long availableBlocks = statFs.getAvailableBlocks();
                freeSpace = blockSize * availableBlocks;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return freeSpace;
    }

    /**
     * 获取SD卡总空间大小，单位B
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getTotalDiskSpace() {
        String state = Environment.getExternalStorageState();
        long totalSpace = 0;
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs statFs = new StatFs(path.getPath());
                long totalBlocks = statFs.getBlockCount();
                long block = statFs.getBlockSize();
                totalSpace = block * totalBlocks;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return totalSpace;
    }

    public static void clearFileWithPath(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                clearFileWithPath(childFiles[i]);
            }
            file.delete();
        }
    }

    public static String getDownloadCourseImagePath(String course_name) {
        String strpath = getBasePath();
        if(strpath==null){
            return "";
        }
        StringBuffer sb=new StringBuffer(strpath);
        addAppBasePath(sb);
        addAppImagePath(sb);
        addPath(sb, File.separator, "downloadcourse_image");
        addPath(sb, File.separator, course_name);
        sb.append(".jpg");
        strpath=sb.toString();
        return strpath;
    }

    private static void addAppBasePath(StringBuffer sb) {
        sb.append(File.separator);
        sb.append("ztk");
    }
    private static void addAppImagePath(StringBuffer sb) {
        sb.append(File.separator);
        sb.append("image");
    }
    private static void addPath(StringBuffer sb, String separator, String ztk) {
        sb.append(separator);
        sb.append(ztk);
    }

    @NonNull
    private static String getBasePath() {
        return  getCacheDir();
    }

    public static boolean byte2File(byte[] buf, String filePathName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            boolean isSus = FileUtil.createFile(filePathName);
            if (isSus) {
                file = new File(filePathName);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(buf);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @NonNull
    public static  String saveImageFileRtPath(InputStream inputStream,String imagePath) throws IOException {
        FileOutputStream fos=null;
        try {
            boolean isSus = FileUtil.createFile(imagePath);
            if (isSus) {
                fos= new FileOutputStream(imagePath);
                int len = 0;
                byte[] buff = new byte[1024];
                while ((len = inputStream.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }
                fos.close();
                inputStream.close();
                return imagePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static void readPDF(final Activity mActivity, final String filePath) {
        Method.runOnUiThread(mActivity, new Runnable() {
            @Override
            public void run() {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String type = CommonUtils.getMIMEType(new File(filePath));
                    intent= UriUtil.setIntentDataAndType(intent,type,new File(filePath),false);

                    if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                        mActivity.startActivityForResult(intent, 100);
                    } else {
                        if(CommonUtils.hasX5nited())
                            PdfViewFragment.lanuch(mActivity,filePath);
                        else
                            CommonUtils.showToast("请安装能打开此文件的程序哦");
                    }
                } catch (Exception e) {
                    if(CommonUtils.hasX5nited())
                        PdfViewFragment.lanuch(mActivity,filePath);
                    else
                        CommonUtils.showToast("请安装能打开此文件的程序哦");
                }

                /*try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String type = getMIMEType(new File(filePath));
                    if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                        Intent intent2= UriUtil.setIntentDataAndType(intent,type,new File(filePath),true);
                        PackageManager pManager = mActivity.getPackageManager();//this 是context
                        List<ResolveInfo> list = pManager.queryIntentActivities(intent2, PackageManager.GET_ACTIVITIES);
                        if (list != null && list.size()>0) {
                            if(list.size()==1){
                                if(list.get(0).activityInfo!=null && list.get(0).activityInfo.name!=null) {
                                    if (list.get(0).activityInfo.name.contains("com.tencent.mobileqq")) {
                                        CommonUtils.showToast("请安装能打开此文件的程序哦");
                                        return;
                                    }
                                }
                            }
                            mActivity.startActivityForResult(intent2, 100);
                        }else {
                            CommonUtils.showToast("请安装能打开此文件的程序哦");
                        }
                    } else {
                        CommonUtils.showToast("请安装能打开此文件的程序哦");
                    }
                } catch (Exception e) {
                    CommonUtils.showToast("请安装能打开此文件的程序哦");
                }*/
            }
        });
    }

    //查看文件的后缀名，对应的MIME类型
    private static final String[][] MIME_MapTable = {
            //word文档
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            //excel文档
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            //ppt文档
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            //pdf文档
            {".pdf", "application/pdf"},
    };

    private  static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }



    /**
     * 获取文件校验值
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static byte[] createChecksum(String filename, String entry) throws Exception {
        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance(entry);
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    /**
     * 获取文件md5值
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename, "MD5");
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static String getFromRaw(Context context, @RawRes int rawId) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().openRawResource(rawId));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制1个目录
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static int copyFiles(String fromFile, String toFile) {
        //要复制的文件目录
        File[] currentFiles;
        File root = new File(fromFile);
        //如同判断SD卡是否存在或者文件是否存在
        //如果不存在则 return出去
        if (!root.exists()) {
            return -1;
        }
        //如果存在则获取当前目录下的全部文件 填充数组
        currentFiles = root.listFiles();

        //目标目录
        File targetDir = new File(toFile);
        //创建目录
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        //遍历要复制该目录下的全部文件
        for (int i = 0; i < currentFiles.length; i++) {
            if (currentFiles[i].isDirectory())//如果当前项为子目录 进行递归
            {
                copyFiles(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");

            } else//如果当前项为文件则进行文件拷贝
            {
                CopySdcardFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());
            }
        }
        return 0;
    }

    //文件拷贝
    //要复制的目录下的所有非子目录(文件夹)文件拷贝
    public static int CopySdcardFile(String fromFile, String toFile) {

        try {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * 文件重命名
     *
     * @param file    源文件
     * @param newName 新文件名
     */
    public static File renameFile(File file, String newName) {
        File newFile = new File(file.getParent() + File.separator + newName);
        try {
            file.renameTo(newFile);
            return newFile;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] readFileToBytes(File file) {
        try {
            return readStreamToBytes(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readStreamToBytes(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int length = -1;
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        out.flush();
        byte[] result = out.toByteArray();
        in.close();
        out.close();
        return result;
    }

    public static File getUploadFile(File source,int width,int height,int size,boolean needCompress){
        LogUtils.e("FileUtils", "原图图片大小" + (source.length() / 1024) + "KB");

        if (source.getName().toLowerCase().endsWith(".gif")) {
            LogUtils.e("FileUtils", "上传图片是GIF图片，上传原图");
            return source;
        }

        File file = null;

        String imagePath=StorageUtils.getTempDirectory(UniApplicationContext.getContext()) + File.separator;
        // String imagePath = Environment.getExternalStorageDirectory().getPath();
        //
        int sample = 1;
        int maxSize = 0;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), opts);

        sample = BitmapDecoder.calculateInSampleSize(opts, height, width);//1920, 1080
        LogUtils.e("FileUtils", "高质量上传" + sample);
        maxSize = size * 1024;
        imagePath = imagePath + "高" + File.separator + source.getName();

        //imagePath = imagePath + File.separator + "thum"+source.getName();
        file = new File(imagePath);


        // 压缩图片
        if (!file.exists()) {
            LogUtils.e("FileUtils", String.format("压缩图片，原图片 path = %s", source.getAbsolutePath()));
            byte[] imageBytes = readFileToBytes(source);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(imageBytes);
            } catch (Exception e) {
            }

            LogUtils.e("FileUtils", String.format("原图片大小%sK", String.valueOf(imageBytes.length / 1024)));
            if (sample>1||imageBytes.length > maxSize) {
                // 尺寸做压缩
                BitmapFactory.Options options = new BitmapFactory.Options();

                if (sample > 1) {
                    options.inSampleSize = sample;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                    LogUtils.e("FileUtils", String.format("压缩图片至大小：%d*%d", bitmap.getWidth(), bitmap.getHeight()));
                    out.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    imageBytes = out.toByteArray();
                }

                options.inSampleSize = 1;
                if (needCompress&&imageBytes.length > maxSize) {
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);

                    int quality = 95;
                    out.reset();
                    LogUtils.e("FileUtils", String.format("压缩图片至原来的百分之%d大小", quality));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                    while (out.toByteArray().length > maxSize) {
                        out.reset();
                        quality -= 10;
                        LogUtils.e("FileUtils", String.format("压缩图片至原来的百分之%d大小", quality));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                    }
                }

            }

            try {
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();

                LogUtils.e("FileUtils", String.format("最终图片大小%sK", String.valueOf(out.toByteArray().length / 1024)));
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(out.toByteArray());
                fo.flush();
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;

    }

    public static File getUploadFile(File source) {
        LogUtils.e("FileUtils", "原图图片大小" + (source.length() / 1024) + "KB");

        if (source.getName().toLowerCase().endsWith(".gif")) {
            LogUtils.e("FileUtils", "上传图片是GIF图片，上传原图");
            return source;
        }

        File file = null;

        //  String imagePath = GlobalContext.getInstance().getAppPath() + SettingUtility.getStringSetting("draft") + File.separator;

        String imagePath=StorageUtils.getTempDirectory(UniApplicationContext.getContext()) + File.separator;
        int sample = 1;
        int maxSize = 0;

        int type =0;// AppSettings.getUploadSetting();
        // 自动，WIFI时原图，移动网络时高
        if (type == 0) {
            if (NetUtil.getNetWorkType(UniApplicationContext.getContext()) == NetUtil.NETWORKTYPE_WIFI)
                type = 2;
            else
                type = 3;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), opts);
        switch (type) {
            // 原图
            case 1:
                // Logger.w("原图上传");
                file = source;
                break;
            // 高
            case 2:
                sample = BitmapDecoder.calculateInSampleSize(opts, 1280, 720);//1920, 1080,1536, 860
                LogUtils.e("FileUtils","高质量上传"+sample);
                maxSize = 400 * 1024;
                imagePath = imagePath + "高" + File.separator + source.getName();
                file = new File(imagePath);
                break;
            // 中
            case 3:
                sample = BitmapDecoder.calculateInSampleSize(opts, 1024, 720);// 1280, 720
                LogUtils.e("FileUtils", "中质量上传"+sample);
                maxSize = 300 * 1024;
                imagePath = imagePath + "中" + File.separator + source.getName();
                file = new File(imagePath);
                break;
            // 低
            case 4:
                sample = BitmapDecoder.calculateInSampleSize(opts, 1024, 600);
                LogUtils.e("FileUtils", "低质量上传"+sample);
                maxSize = 100 * 1024;
                imagePath = imagePath + "低" + File.separator + source.getName();
                file = new File(imagePath);
                break;
            default:
                break;
        }

        // 压缩图片
        if (type != 1 && !file.exists()) {
            LogUtils.e("FileUtils", String.format("压缩图片，原图片 path = %s", source.getAbsolutePath()));
            byte[] imageBytes = readFileToBytes(source);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(imageBytes);
            } catch (Exception e) {
            }

            LogUtils.e("FileUtils", String.format("原图片大小%sK", String.valueOf(imageBytes.length / 1024)));
            if (imageBytes.length > maxSize) {
                // 尺寸做压缩
                BitmapFactory.Options options = new BitmapFactory.Options();

                if (sample > 1) {
                    options.inSampleSize = sample;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                    LogUtils.e("FileUtils", String.format("压缩图片至大小：%d*%d", bitmap.getWidth(), bitmap.getHeight()));
                    out.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    imageBytes = out.toByteArray();
                }

                options.inSampleSize = 1;
                if (imageBytes.length > maxSize) {
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);

                    int quality = 95;
                    out.reset();
                    LogUtils.e("FileUtils", String.format("压缩图片至原来的百分之%d大小", quality));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                    while (out.toByteArray().length > maxSize) {
                        out.reset();
                        quality -= 10;
                        LogUtils.e("FileUtils", String.format("压缩图片至原来的百分之%d大小", quality));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
                    }
                }

            }

            try {
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();

                LogUtils.e("FileUtils", String.format("最终图片大小%sK", String.valueOf(out.toByteArray().length / 1024)));
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(out.toByteArray());
                fo.flush();
                fo.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return file;
    }
}
