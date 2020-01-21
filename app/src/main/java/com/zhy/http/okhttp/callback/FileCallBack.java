package com.zhy.http.okhttp.callback;

import com.huatu.handheld_huatu.business.ztk_zhibo.bean.DownHandout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/15.
 */
public abstract class FileCallBack extends Callback<File>
{
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public abstract void inProgress(float progress,long total);

    public FileCallBack(String destFileDir, String destFileName)
    {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }


    private DownHandout mHandout;
    public FileCallBack(String destFileDir, String destFileName, DownHandout handout)
    {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        this.mHandout=handout;
    }

    public DownHandout getDownHandout(){
        return mHandout;
    }

    @Override
    public File parseNetworkResponse(Response response) throws Exception
    {
        return saveFile(response);
    }

    /**
     * 每隔一秒测量一次下载速度
     */
    private static final long SPACE_TIME = 500;
    private long oldTime = -1, oldLength = -1;
    /** 下载速度 */
    private float mDownloadSpeed;
    private void downLoadSpeedUpdate(long finalSum,long total) {
        if ((System.currentTimeMillis() - oldTime) < SPACE_TIME) {
            return;
        }
         if (oldTime > 0) {
            if ((total - finalSum)
                    <mDownloadSpeed / 2f) {
                mDownloadSpeed=0;
            } else {
                float speed = (finalSum - oldLength)
                        / ((System.currentTimeMillis() - oldTime) / SPACE_TIME);
                //mDownloadBean.setDownloadSpeed(speed);
                mDownloadSpeed=speed;
            }
        } else {
            //mDownloadBean.setDownloadSpeed(0.00f);
             mDownloadSpeed=0.00f;
        }
        oldTime = System.currentTimeMillis();
        oldLength =finalSum;// mDownloader.getLoadedSize();
    }

    public float getDownloadSpeed(){
        return  mDownloadSpeed;
    }

    public File saveFile(Response response) throws IOException
    {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try
        {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1)
            {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;
                downLoadSpeedUpdate(finalSum,total);
                OkHttpUtils.getInstance().getDelivery().post(new Runnable()
                {
                    @Override
                    public void run()
                    {

                        inProgress(finalSum * 1.0f / total,total);
                    }
                });
            }
            fos.flush();

            return file;

        } finally
        {
            try
            {
                if (is != null) is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (fos != null) fos.close();
            } catch (IOException e)
            {
            }

        }
    }


}
