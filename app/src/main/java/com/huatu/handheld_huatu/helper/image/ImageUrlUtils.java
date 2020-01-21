package com.huatu.handheld_huatu.helper.image;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;


import com.huatu.handheld_huatu.helper.Images;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片url工具类
 * Created by baron
 * Date : 2016/5/3 0003 11:43
 * Email: 5267621@qq.com
 */

public class ImageUrlUtils {

    public enum TYPE {
        FILE, DRAWAABLE, WEB, ASSET;
    }

    public static final String FILE_HEAD = "file:///";

    public static final String FILE_ASSET = "assets://";

    public static final String FILE_DRAWABLE = "drawable://";

    public static final String FILE_WEB = "http://";

    public static final String FILE_WEBS = "https://";

/*    public static ArrayList<Images> builderUploads(){
        ArrayList<Images> tmplist=new ArrayList<>();
        tmplist.add(new Images(ImageUrlUtils.FILE_DRAWABLE + R.drawable.post_upload_pictures, -1));
        return tmplist;
    }*/
    /**
     * 判断url的类型
     *
     * @param url
     * @return
     */
    public static TYPE isImageType(String url) {
        if (TextUtils.isEmpty(url)) {
            return TYPE.WEB;
        }
        if (url.indexOf(FILE_WEB, 0) == 0 || url.indexOf(FILE_WEBS, 0) == 0) {
            return TYPE.WEB;
        }
        if (url.indexOf(FILE_ASSET, 0) == 0) {
            return TYPE.ASSET;
        }
        if (url.indexOf(FILE_DRAWABLE, 0) == 0) {
            return TYPE.DRAWAABLE;
        }
        if (url.indexOf(FILE_HEAD, 0) == 0) {
            return TYPE.FILE;
        }
        return TYPE.WEB;
    }

    /**
     * 获取drawable资源图片
     *
     * @param url
     * @return
     */
    public static int getImageDrawable(String url) {
        try {
            url = url.replace(FILE_DRAWABLE, "");
            return Integer.parseInt(url);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取File文件资源图片
     *
     * @param url
     * @return
     */
    public static String getImageFile(String url) {
        return Uri.fromFile(new File(url.replace(FILE_HEAD, ""))).toString();
    }

    public static String getAbsImageFile(String url) {

        String tmpstr=url.replace(FILE_HEAD, "");
        return  tmpstr;
    }

    /**
     * 获取asset文件资源图片
     *
     * @param url
     * @return
     */
    public static String getImageAsset(String url) {
        url = url.replace(FILE_ASSET, "");
        return url;
    }


    /**
     * 根据本地图片url获得上传的url
     *
     * @param images
     * @return
     */
    public static Images getUpLoadUrl(Images images) {
        images.setUrl(images.getUrl().replace(FILE_HEAD, ""));
        return images;
    }

    /**
     * 获取上传图片的地址数组
     *
     * @param url
     * @return
     */
    public static List<Images> getUpLoadUrl(List<Images> url) {
        for (Images str : url) {
            Images images = getUpLoadUrl(str);
            images.setUrl(images.getUrl().replace(FILE_HEAD, ""));
        }
        return url;
    }


    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


}
