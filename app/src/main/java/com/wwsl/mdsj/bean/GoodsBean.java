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

/**
 * Created by cxf on 2018/6/20.
 */
@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class GoodsBean implements Parcelable {


    /**
     * goods_id : 1
     * goods_name : null
     * goods_img : null
     * goods_price : null
     * goods_url : &mobile=15611780929&sctoken=604f17c256da6151f444c8e6f46d1fc7&nickname=任我行
     */

    @JSONField(name = "goods_id")
    private String id;
    @JSONField(name = "goods_name")
    private String name;
    @JSONField(name = "goods_img")
    private String img;
    @JSONField(name = "goods_price")
    private String price;
    @JSONField(name = "goods_url")
    private String url;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.img);
        dest.writeString(this.price);
        dest.writeString(this.url);
    }

    public GoodsBean(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.img = in.readString();
        this.price = in.readString();
        this.url = in.readString();
    }


    public static final Creator<GoodsBean> CREATOR = new Creator<GoodsBean>() {
        @Override
        public GoodsBean[] newArray(int size) {
            return new GoodsBean[size];
        }

        @Override
        public GoodsBean createFromParcel(Parcel in) {
            return new GoodsBean(in);
        }
    };
}
