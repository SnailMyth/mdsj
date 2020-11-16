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
public class AdvertiseBean implements Parcelable {

    /**
     * id : 11
     * sid : 4
     * name : 广告1
     * des :
     * url : http://www.baidu.com
     * thumb : http://hougong001.mdmz.xyz/20200617/5ee9d502c79ae.jpg
     * orderno : 1
     * addtime : 1592382759
     */

    private String id;
    private String sid;
    private String name;
    private String des;
    private String url;
    private String thumb;
    private String orderno;
    private String addtime;

    protected AdvertiseBean(Parcel in) {
        id = in.readString();
        sid = in.readString();
        name = in.readString();
        des = in.readString();
        url = in.readString();
        thumb = in.readString();
        orderno = in.readString();
        addtime = in.readString();
    }

    public static final Creator<AdvertiseBean> CREATOR = new Creator<AdvertiseBean>() {
        @Override
        public AdvertiseBean createFromParcel(Parcel in) {
            return new AdvertiseBean(in);
        }

        @Override
        public AdvertiseBean[] newArray(int size) {
            return new AdvertiseBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sid);
        dest.writeString(name);
        dest.writeString(des);
        dest.writeString(url);
        dest.writeString(thumb);
        dest.writeString(orderno);
        dest.writeString(addtime);
    }
}
