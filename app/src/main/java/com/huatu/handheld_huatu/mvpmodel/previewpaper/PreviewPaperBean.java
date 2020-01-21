package com.huatu.handheld_huatu.mvpmodel.previewpaper;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 白名单老师，可提前预览试题
 */
public class PreviewPaperBean implements Parcelable {

    public long id;
    public String name;                     // 名称
    public List<Integer> questions;         // 题号

    public PreviewPaperBean() {
    }

    protected PreviewPaperBean(Parcel in) {
        id = in.readLong();
        name = in.readString();
        int qSize = in.readInt();
        if (qSize > 0) {
            int[] qs = new int[qSize];
            questions = new ArrayList<>();
            in.readIntArray(qs);
            for (int q : qs) {
                questions.add(q);
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        if (questions != null && questions.size() > 0) {
            int[] qs = new int[questions.size()];
            for (int i = 0; i < questions.size(); i++) {
                qs[i] = questions.get(i);
            }
            dest.writeInt(questions.size());
            dest.writeIntArray(qs);
        } else {
            dest.writeInt(0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PreviewPaperBean> CREATOR = new Creator<PreviewPaperBean>() {
        @Override
        public PreviewPaperBean createFromParcel(Parcel in) {
            return new PreviewPaperBean(in);
        }

        @Override
        public PreviewPaperBean[] newArray(int size) {
            return new PreviewPaperBean[size];
        }
    };
}
