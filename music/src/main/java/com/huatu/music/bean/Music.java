package com.huatu.music.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by Administrator on 2019\8\20 0020.
 */

public class Music implements Parcelable{

    // 歌曲类型 本地/网络
    public String  type;
    //数据库存储id
    public long id;// Long = 0
    // 歌曲id
    public String  mid;// String? = null
    // 音乐标题
    public String  title;// String? = null
    // 艺术家
    public String artist;// String? = null//{123,123,13}
    // 专辑
    public String  album;// String? = null
    // 专辑id
    public String artistId;// String? = null//{123,123,13}
    // 专辑id
    public String  albumId;// String? = null
    // 专辑内歌曲个数   --->  用作startPos
    public int trackNumber;// Int = 0
    // 持续时间
    public long  duration;// Long = 0
    // 收藏      --->
    public boolean isLove;// Boolean = false
    // [本地|网络]
    public boolean isOnline;// Boolean = true
    // 音乐路径
    public String  uri;// String? = null
    // [本地|网络] 音乐歌词地址
    public String  lyric;// String? = null
    // [本地|网络]专辑封面路径
    public String  coverUri;// String? = null
    // [网络]专辑封面
    public String  coverBig;// String? = null
    // [网络]small封面
    public String  coverSmall;// String? = null
    // 文件名
    public String  fileName;// String? = null
    // 文件大小
    public long  fileSize;// Long = 0
    // 发行日期
    public String  year;// String? = null
    //更新日期
    public long  date;// Long = 0
    //在线歌曲是否限制播放，false 可以播放      -->用作是否有老师
    public boolean isCp;// Boolean = false
    //在线歌曲是否付费歌曲，false 不能下载
    public boolean isDl;// Boolean = true
    //收藏id
    public String collectId;// String? = null
    //音乐品质，默认标准模式
    public int quality;// Int = 128000

    //音乐品质选择
    public boolean hq;// Boolean = false //192
    public boolean sq;// Boolean = false //320
    public boolean high;// Boolean = false //999
    //是否有mv 0代表无，1代表有
    public int hasMv;// Int = 0
    public Music(){}
    public Music(Parcel parcel) {

        type = parcel.readString();
        id = parcel.readLong();
        mid = parcel.readString();
        title = parcel.readString();
        artist = parcel.readString();
        album = parcel.readString();
        artistId = parcel.readString();
        albumId = parcel.readString();
        trackNumber = parcel.readInt();
        duration = parcel.readLong();
        isLove = parcel.readByte() != 0;
        isOnline = parcel.readByte() != 0;
        uri = parcel.readString();
        lyric = parcel.readString();
        coverUri = parcel.readString();
        coverBig = parcel.readString();
        coverSmall = parcel.readString();
        fileName = parcel.readString();
        fileSize = parcel.readLong();
        year = parcel.readString();
        date = parcel.readLong();
        isCp = parcel.readByte() != 0;
        isDl = parcel.readByte() != 0;
        collectId = parcel.readString();
        quality = parcel.readInt();
        hq = parcel.readByte() != 0;
        sq = parcel.readByte() != 0;
        high = parcel.readByte() != 0;

    }

    public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>() {

        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);

        }

        @Override
        public Music[] newArray(int size) {

            return new Music[size];

        }

    };

    @Override
    public int describeContents() {
        return 0;

    }


    @Override
    public void writeToParcel(Parcel p0, int p1) {

        p0.writeString(type);
        p0.writeLong(id);
        p0.writeString(mid);
        p0.writeString(title);
        p0.writeString(artist);
        p0.writeString(album);
        p0.writeString(artistId);
        p0.writeString(albumId);
        p0.writeInt(trackNumber);
        p0.writeLong(duration);

        p0.writeByte((byte)(isLove ? 0x01:0x00));
        p0.writeByte((byte)(isOnline ? 0x01:0x00));
        p0.writeString(uri);
        p0.writeString(lyric);
        p0.writeString(coverUri);
        p0.writeString(coverBig);
        p0.writeString(coverSmall);
        p0.writeString(fileName);
        p0.writeLong(fileSize);
        p0.writeString(year);
        p0.writeLong(date);
        p0.writeByte((byte)(isCp ? 0x01:0x00));
        p0.writeByte((byte)(isDl ? 0x01:0x00));
        p0.writeString(collectId);
        p0.writeInt(quality);
        p0.writeByte((byte)(hq ? 0x01:0x00));
        p0.writeByte((byte)(sq ? 0x01:0x00));
        p0.writeByte((byte)(high ? 0x01:0x00));
    }

    @Override
    public String toString() {
        return "Music{" +
                "type=" + type +
                ", id=" + id +
                ", mid='" + mid + '\'' +
                ", title=" + title +
                ", artist=" + artist +
                ", album=" + album +
                ", artistId=" + artistId +
                ", albumId=" + albumId +
                ", coverUri=" + coverUri +
                ", fileName=" + fileName +
                '}';

      /*  return "Music(type=$type, id=$id, mid=$mid, title=$title, " +
                "artist=$artist, album=$album, artistId=$artistId, " +
                "albumId=$albumId, trackNumber=$trackNumber," +
                " duration=$duration, isLove=$isLove, isOnline=$isOnline, " +
                "uri=$uri, lyric=$lyric, coverUri=$coverUri, coverBig=$coverBig, coverSmall=$coverSmall," +
                " fileName=$fileName, fileSize=$fileSize, year=$year, date=$date, isCp=$isCp, isDl=$isDl, " +
                "collectId=$collectId, quality=$quality," +
                "qualityList=$high $hq $sq)"*/
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Music))
            return false;
        Music music = (Music) obj;

        return mid.equals(music.mid);
    }
}
