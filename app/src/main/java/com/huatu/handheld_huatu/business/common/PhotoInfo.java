package com.huatu.handheld_huatu.business.common;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2019\7\27 0027.
 */


public  class PhotoInfo implements Parcelable {

    public String path;         // 本地地址、url等
    public Uri uri;

    public PhotoInfo() {
    }

    public PhotoInfo(Parcel in) {
        path = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {
        @Override
        public PhotoInfo createFromParcel(Parcel in) {
            return new PhotoInfo(in);
        }

        @Override
        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeParcelable(uri, flags);
    }
}