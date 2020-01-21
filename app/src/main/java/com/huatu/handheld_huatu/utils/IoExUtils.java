/**
 * <pre>
 * Copyright (C) 2015  Soulwolf XiaoDaoW3.0
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </pre>
 */
package com.huatu.handheld_huatu.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;


import com.huatu.handheld_huatu.UniApplicationContext;
import com.huatu.utils.Preconditions;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

//import org.apache.http.util.EncodingUtils;

/**
 * IO操作工具类,
 *
  */

public final class IoExUtils {

    private static final boolean DEBUG = false;

    private static final String LOG_TAG = "IoUtils:";

    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

  /*  public static int getExtensionNameType(){


    }
*/
    public static String getFileName(String fileUrl){

        if(TextUtils.isEmpty(fileUrl))  return "";
        if(fileUrl.lastIndexOf("/")<0) return "";
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/")+1);
        return fileName;
    }


    public static String assetFile2Str(Context c, String urlStr,boolean isAppendEnd){
        InputStream in = null;
        try{
            in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null&& !line.matches("^\\s*\\/\\/.*") ) {//过滤掉注释
                    sb.append(line);
                    if(isAppendEnd){
                        sb.append("\r\n");
                    }
                }
            } while (line != null);

            bufferedReader.close();
            in.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static File makerDirectory(File dir){
        if(! dir.getParentFile().exists()) {
            makerDirectory(dir.getParentFile());
        }
        boolean result = dir.mkdir();
        if(!result){
            if(DEBUG){
                LogUtils.e(LOG_TAG, "makerDirectory error %s"+ dir);
            }
        }
        return dir;
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static int copy(InputStream input, OutputStream output) throws IOException {

        long count = copyLarge(input, output,new byte[DEFAULT_BUFFER_SIZE]);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

   //关闭会出错？？？
    public static boolean copyAssetsDatabase(Context context,String dbName){
        Preconditions.checkNotNull(context, "context == NULL");
        Preconditions.checkNotNull(dbName, "dbName == NULL");
        InputStream is = null;
        OutputStream ous = null;
        boolean flag = false;
        try {
            is = context.getAssets().open(dbName);
            File databasePath = context.getDatabasePath(dbName);


            IoExUtils.makerDirectory(databasePath.getParentFile());

            LogUtils.e(LOG_TAG, "makerDirectory   %s"+ databasePath.getAbsolutePath());
            if(!databasePath.exists()){
                boolean newFile = databasePath.createNewFile();
                if(DEBUG){
                    LogUtils.d(LOG_TAG, "createNewFile error %s", newFile);
                }
            }
            ous = new FileOutputStream(databasePath);
            copy(is, ous);

            ous.flush();
            flag = true;
        } catch (IOException e) {
            if(DEBUG){
                LogUtils.e(LOG_TAG, "copyAssetsDatabase error", e);
            }
            flag = false;
        }finally {

            IOUtils.closeQuietly(ous);
            IOUtils.closeQuietly(is);

          /*try{IOUtils.closeQuietly(is);
               IOUtils.closeQuietly(ous);}
           catch (Exception e){}*/

        }
        return flag;
    }


    public static String InputStreamTOString(InputStream in) throws Exception{

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,DEFAULT_BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return new String(outStream.toByteArray(),"UTF-8");
    }

    public static boolean checkFileExist(String filename){
        File mSaveFile = StorageUtils.getTempFile(UniApplicationContext.getContext(), filename, "json");

        return mSaveFile.exists();
    }

    public static String getJsonString(String filename){
        File mSaveFile = StorageUtils.getTempFile(UniApplicationContext.getContext(), filename, "json");
        if(mSaveFile.exists()) {
            String res = "";
             try {
                FileInputStream fin = new FileInputStream(mSaveFile);
                int length = fin.available();
                byte[] buffer = new byte[length];

                fin.read(buffer);
                res =new String(buffer,"UTF-8");//
                fin.close();
             }
            catch (Exception e) {
                 e.printStackTrace();
            }
            return res;
        }
        return "";
    }

    public static boolean saveJsonFile(String content,String filename){
        OutputStream ous = null;
        try {
            File mSaveFile = StorageUtils.getTempFile(UniApplicationContext.getContext(), filename, "json");
            FileOutputStream outStream = new FileOutputStream(mSaveFile);
            outStream.write(content.getBytes());
            outStream.flush();
            return true;
        }catch (FileNotFoundException e){
             return false;
        }
        catch (IOException e) {
            return false;
        }
        finally {
            IOUtils.closeQuietly(ous);
        }
     }


    /**
     * 根据byte数组生成文件
     *
     * @param bytes
     *            生成文件用到的byte数组
     */
    public static void createFileWithByte(byte[] bytes,String fileName) {
        // TODO Auto-generated method stub
        /**
         * 创建File对象，其中包含文件所在的目录以及文件的命名
         */
        File file = new File(StorageUtils.getTempDirectory(UniApplicationContext.getContext()),  fileName);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在则删除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系统中根据路径创建一个新的空文件
            file.createNewFile();
            // 获取FileOutputStream对象
            outputStream = new FileOutputStream(file);
            // 获取BufferedOutputStream对象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes);
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            // 打印异常信息
            e.printStackTrace();
        } finally {
            // 关闭创建的流对象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public static class IOUtils{
        public static void closeQuietly(OutputStream output) {
            closeQuietly((Closeable)output);
        }
        /**/
        public static void closeQuietly(Closeable closeable) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }

        public static void close(Closeable... closeables) {
            if(closeables != null && closeables.length != 0) {
                Closeable[] var1 = closeables;
                int var2 = closeables.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    Closeable closeable = var1[var3];
                    if(closeable != null) {
                        try {
                            closeable.close();
                        } catch (IOException var6) {
                            var6.printStackTrace();
                        }
                    }
                }

            }
        }

        public static void closeIO(Closeable... closeables) {
            if(null != closeables && closeables.length > 0) {
                Closeable[] var1 = closeables;
                int var2 = closeables.length;

                for(int var3 = 0; var3 < var2; ++var3) {
                    Closeable cb = var1[var3];

                    try {
                        if(null != cb) {
                            cb.close();
                        }
                    } catch (IOException var6) {
                        throw new RuntimeException(IOUtils.class.getClass().getName(), var6);
                    }
                }

            }
        }

    }




   /* public static boolean saveApk(InputStream ins,String apkName)  {

        OutputStream ous = null;
        try {
            File  mSaveFile = StorageUtils.getTempFile(AppStructure.getInstance().getContext(),  apkName, "apk");
                     //ResponseBody body = response.body();
            InputStream inputStream =ins;//body.byteStream();
            ous = new FileOutputStream(mSaveFile);
            IOUtils.copy(inputStream, ous);
            ous.flush();
            return true;
        }
        catch (Exception e){
             return false ;
        }
        finally {
             IOUtils.closeQuietly(ins);
            IOUtils.closeQuietly(ous);
        }

    }*/

    private static final String  SDPATH= Environment.getExternalStorageDirectory()+"";
    /*
*在SD卡上创建文件
*/
    public static File CreatSDFile(String fileNmae){
        File file =new File(SDPATH+fileNmae);
        try {
            file.createNewFile();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }
    /*
    * 在SD卡上创建目录
    */
    public static File creatSDDir(String dirName){
        File dir=new File(SDPATH+dirName);
        if(!dir.exists())
        {
            dir.mkdirs();
        }
        return dir;
    }

    /*
*将一个InputSteam里面的数据写入到SD卡中
*/
    public static File write2SDFromInput(String path,String fileName,InputStream input){
        System.out.println("path="+path+";fileName="+fileName+";");
        File file =null;
        File folder=null;
        OutputStream output=null;
        try {
            folder=creatSDDir(path);
            System.out.println("folder="+folder);///storage/emulated/0/temp/
            file=CreatSDFile(path+fileName);
            System.out.println("file="+file);
            output=new FileOutputStream(file);
            byte buffer[]=new byte[4*1024];

            while((input.read())!=-1){
                output.write(buffer);
            }
            output.flush();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try{
                output.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return file;
    }



   // org.apache.commons.io;
    /**
     * Copies a file to a new location.
     * <p>
     * This method copies the contents of the specified source file
     * to the specified destination file.
     * The directory holding the destination file is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     * <p>
     * <strong>Note:</strong> Setting <code>preserveFileDate</code> to
     * {@code true} tries to preserve the file's last modified
     * date/times using {@link File#setLastModified(long)}, however it is
     * not guaranteed that the operation will succeed.
     * If the modification operation fails, no indication is provided.
     *
     * @param srcFile  an existing file to copy, must not be {@code null}
     * @param destFile  the new file, must not be {@code null}
     * @param preserveFileDate  true if the file date of the copy
     *  should be the same as the original
     *
     * @throws NullPointerException if source or destination is {@code null}
     * @throws IOException if source or destination is invalid
     * @throws IOException if an IO error occurs during copying
     * @see #copyFileToDirectory(File, File, boolean)
     */
    public static void copyFile(File srcFile, File destFile,
                                boolean preserveFileDate) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        }
        if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        }
        if (srcFile.exists() == false) {
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        }
        if (srcFile.isDirectory()) {
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        }
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        }
        File parentFile = destFile.getParentFile();
        if (parentFile != null) {
            if (!parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new IOException("Destination '" + parentFile + "' directory cannot be created");
            }
        }
        if (destFile.exists() && destFile.canWrite() == false) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        }
        doCopyFile(srcFile, destFile, preserveFileDate);
    }


    /**
     * Internal copy file method.
     *
     * @param srcFile  the validated source file, must not be {@code null}
     * @param destFile  the validated destination file, must not be {@code null}
     * @param preserveFileDate  whether to preserve the file date
     * @throws IOException if an error occurs
     */
    private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input  = fis.getChannel();
            output = fos.getChannel();
            long size = input.size();
            long pos = 0;
            long count = 0;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(fos);
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fis);
        }

        if (srcFile.length() != destFile.length()) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "'");
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static final long ONE_KB = 1024;



    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The file copy buffer size (30 MB)
     */
    private static final long FILE_COPY_BUFFER_SIZE = ONE_MB * 30;


}
