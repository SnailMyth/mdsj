package com.wwsl.mdsj.bean;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class LaunchAdBean implements Parcelable {

    private String type;//1.图片 2.视频
    private String url;
    private String thumb;

    protected LaunchAdBean(Parcel in) {
        type = in.readString();
        url = in.readString();
        thumb = in.readString();
    }

    public static final Creator<LaunchAdBean> CREATOR = new Creator<LaunchAdBean>() {
        @Override
        public LaunchAdBean createFromParcel(Parcel in) {
            return new LaunchAdBean(in);
        }

        @Override
        public LaunchAdBean[] newArray(int size) {
            return new LaunchAdBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(thumb);
    }
}
