package com.wwsl.mdsj.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class PictureChooseBean implements Parcelable {
    public PictureChooseBean() {

    }

    public PictureChooseBean(String path, int num) {
        this.path = path;
        this.num = num;
    }

    private String path;
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 序列化过程：必须按成员变量声明的顺序进行封装
        dest.writeString(path);
        dest.writeInt(num);
    }

    // 反序列过程：必须实现Parcelable.Creator接口，并且对象名必须为CREATOR
    // 读取Parcel里面数据时必须按照成员变量声明的顺序，Parcel数据来源上面writeToParcel方法，读出来的数据供逻辑层使用
    public static final Parcelable.Creator<PictureChooseBean> CREATOR = new Creator<PictureChooseBean>() {

        @Override
        public PictureChooseBean createFromParcel(Parcel source) {
            return new PictureChooseBean(source.readString(), source.readInt());
        }

        @Override
        public PictureChooseBean[] newArray(int size) {
            return new PictureChooseBean[size];
        }
    };
}
