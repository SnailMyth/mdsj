package com.wwsl.mdsj.bean.net;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by cxf on 2018/6/20.
 */

@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class VideoMusicBean implements Parcelable {

    /**
     * id : 1
     * title : 一个人
     * author : 张艺兴
     * img_url : http://maodousj.wenzuxz.com/20200715/5f0ee2649ac15.png
     * length : 03:04
     * file_url : http://maodousj1.wenzuxz.com/20200715/5f0ee27a0e329.mp3
     * use_nums : 58
     * music_format : 一个人--
     */

    private String id;
    private String title;
    private String author;
    @JSONField(name = "img_url")
    private String imgUrl;
    private String length;
    @JSONField(name = "file_url")
    private String fileUrl;
    @JSONField(name = "use_nums")
    private String useNum;
    @JSONField(name = "music_format")
    private String musicName;

    private String localPath;

    @Override
    public int describeContents() {
        return 0;
    }

    public VideoMusicBean(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.imgUrl = in.readString();
        this.length = in.readString();
        this.fileUrl = in.readString();
        this.useNum = in.readString();
        this.musicName = in.readString();
        this.localPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.imgUrl);
        dest.writeString(this.length);
        dest.writeString(this.fileUrl);
        dest.writeString(this.useNum);
        dest.writeString(this.musicName);
        dest.writeString(this.localPath);
    }

    public static final Creator<VideoMusicBean> CREATOR = new Creator<VideoMusicBean>() {
        @Override
        public VideoMusicBean[] newArray(int size) {
            return new VideoMusicBean[size];
        }

        @Override
        public VideoMusicBean createFromParcel(Parcel in) {
            return new VideoMusicBean(in);
        }
    };

}
