package com.huatu.handheld_huatu.mvpmodel.arena;

import android.os.Parcel;
import android.os.Parcelable;

import com.huatu.handheld_huatu.base.BaseResponseModel;


/**
 * Created by dongd on 2016/10/17.
 */

public class AthleticsResponse<T> extends BaseResponseModel implements Parcelable {
    public String ticket;
    private static ClassLoader mClassLoader;

    public AthleticsResponse(T clazz) {
        super();
        this.data = clazz;
        if (this.data != null) {
            AthleticsResponse.mClassLoader = data.getClass().getClassLoader();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeString(ticket);
        dest.writeValue(data);
    }

    protected AthleticsResponse(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        try {
            this.ticket = in.readString();
            data = (T) in.readValue(AthleticsResponse.mClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final Parcelable.Creator<AthleticsResponse> CREATOR = new Parcelable.Creator<AthleticsResponse>() {
        public AthleticsResponse createFromParcel(Parcel source) {
            return new AthleticsResponse(source);
        }

        public AthleticsResponse[] newArray(int size) {
            return new AthleticsResponse[size];
        }
    };
}
