package com.wwsl.mdsj.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@Builder
@AllArgsConstructor( )
@NoArgsConstructor
@ToString
public class CommentBean implements Parcelable {
    private String id;
    private String uid;
    private String touid;
    private String parentid;
    private String content;
    private String likes;
    private String addtime;
    private String datetime;
    private String islike;
    @JSONField(name = "userinfo")
    private UserInfo userInfo;
    @JSONField(name = "touserinfo")
    private UserInfo toUserInfo;

    protected CommentBean(Parcel in) {
        id = in.readString();
        uid = in.readString();
        touid = in.readString();
        parentid = in.readString();
        content = in.readString();
        likes = in.readString();
        addtime = in.readString();
        datetime = in.readString();
        islike = in.readString();
    }

    public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
        @Override
        public CommentBean createFromParcel(Parcel in) {
            return new CommentBean(in);
        }

        @Override
        public CommentBean[] newArray(int size) {
            return new CommentBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(touid);
        dest.writeString(parentid);
        dest.writeString(content);
        dest.writeString(likes);
        dest.writeString(addtime);
        dest.writeString(datetime);
        dest.writeString(islike);
    }
}
