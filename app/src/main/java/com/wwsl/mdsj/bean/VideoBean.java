package com.wwsl.mdsj.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wwsl.mdsj.bean.net.VideoMusicBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * Created by cxf on 2017/10/25.
 */
@AllArgsConstructor()
@NoArgsConstructor()
@Builder
@Getter
@Setter
@ToString
public class VideoBean implements Parcelable, MultiItemEntity {

    private String id;
    private String uid;
    private String title;//视频文案内容
    @JSONField(name = "thumb")
    private String coverUrl;//封面
    @JSONField(name = "thumb_s")
    private String thumbs;
    @JSONField(name = "href")
    private String videoUrl;//视频播放资源
    @JSONField(name = "likes")
    private String likeNum;//点赞数
    @JSONField(name = "views")
    private String viewNum;//视频点击 观看数
    @JSONField(name = "comments")
    private String commentNum;//评论数
    @JSONField(name = "steps")
    private String stepNum;//踩数
    @JSONField(name = "shares")
    private String shareNum;//转发数
    private String addtime;
    private String lat;
    private String lng;
    private String city;
    @JSONField(name = "isdel")
    private String isDel;
    private String status;
    @JSONField(name = "music_id")
    private String musicId;
    @JSONField(name = "xiajia_reason")
    private String xjReason;
    @JSONField(name = "show_val")
    private String showVal;
    @JSONField(name = "nopass_time")
    private String nopass_time;
    @JSONField(name = "watch_ok")
    private String watch_ok;
    @JSONField(name = "is_ad")
    private String isAd;
    @JSONField(name = "ad_endtime")
    private String adEndTime;
    @JSONField(name = "ad_url")
    private String adUrl;
    @JSONField(name = "orderno")
    private String orderNum;
    private String images;
    private String type;
    @JSONField(name = "video_time")
    private String videoTime;
    private String price;
    @JSONField(name = "video_type")
    private String videoType;
    @JSONField(name = "votes")
    private String votes;
    @JSONField(name = "goods_id")
    private String goodsId;
    @JSONField(name = "update_time")
    private String updateTime;
    @JSONField(name = "city_id")
    private int cityId;
    @JSONField(name = "file_id")
    private String fileId;
    @JSONField(name = "at_user")
    private String atUser;
    @JSONField(name = "is_public")
    private String isPublic;
    @JSONField(name = "is_zn")
    private String isZn;
    private String tag;
    @JSONField(name = "userinfo")
    private UserBean userBean;//作者

    private String datetime;

    @JSONField(name = "islike")
    private int like;//是否已点赞
    @JSONField(name = "isstep")
    private int step;//是否踩过
    @JSONField(name = "isattent")
    private int attent;//是否已关注

    @Builder.Default()
    private int follow = 0;


    private String distance;//与视频发布距离

    @JSONField(name = "music")
    private VideoMusicBean musicInfo;
    @JSONField(name = "goods")
    private GoodsBean goods;

    @JSONField(name = "watch_ok")
    private String viewOkNum;//视频播放完 观看数


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.title);
        dest.writeString(this.coverUrl);
        dest.writeString(this.thumbs);
        dest.writeString(this.videoUrl);
        dest.writeString(this.likeNum);
        dest.writeString(this.viewNum);
        dest.writeString(this.commentNum);
        dest.writeString(this.stepNum);
        dest.writeString(this.shareNum);
        dest.writeString(this.addtime);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.city);
        dest.writeString(this.isDel);
        dest.writeString(this.status);
        dest.writeString(this.musicId);
        dest.writeString(this.xjReason);
        dest.writeString(this.showVal);
        dest.writeString(this.nopass_time);
        dest.writeString(this.watch_ok);
        dest.writeString(this.isAd);
        dest.writeString(this.adEndTime);
        dest.writeString(this.adUrl);
        dest.writeString(this.orderNum);
        dest.writeString(this.images);
        dest.writeString(this.type);
        dest.writeString(this.videoTime);
        dest.writeString(this.price);
        dest.writeString(this.videoType);
        dest.writeString(this.votes);
        dest.writeString(this.goodsId);
        dest.writeString(this.updateTime);
        dest.writeInt(this.cityId);
        dest.writeString(this.fileId);
        dest.writeString(this.atUser);
        dest.writeString(this.isPublic);
        dest.writeString(this.isZn);
        dest.writeString(this.tag);
        dest.writeParcelable(this.userBean, flags);
        dest.writeString(this.datetime);
        dest.writeInt(this.like);
        dest.writeInt(this.step);
        dest.writeInt(this.attent);
        dest.writeInt(this.follow);
        dest.writeString(this.distance);
        dest.writeString(this.viewOkNum);
        dest.writeParcelable(this.musicInfo, flags);
        dest.writeParcelable(this.goods, flags);
    }


    public VideoBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.title = in.readString();
        this.coverUrl = in.readString();
        this.thumbs = in.readString();
        this.videoUrl = in.readString();
        this.likeNum = in.readString();
        this.viewNum = in.readString();
        this.commentNum = in.readString();
        this.stepNum = in.readString();
        this.shareNum = in.readString();
        this.addtime = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.city = in.readString();
        this.isDel = in.readString();
        this.status = in.readString();
        this.musicId = in.readString();
        this.xjReason = in.readString();
        this.showVal = in.readString();
        this.nopass_time = in.readString();
        this.watch_ok = in.readString();
        this.isAd = in.readString();
        this.adEndTime = in.readString();
        this.adUrl = in.readString();
        this.orderNum = in.readString();
        this.images = in.readString();
        this.type = in.readString();
        this.videoTime = in.readString();
        this.price = in.readString();
        this.videoType = in.readString();
        this.votes = in.readString();
        this.goodsId = in.readString();
        this.updateTime = in.readString();
        this.cityId = in.readInt();
        this.fileId = in.readString();
        this.atUser = in.readString();
        this.isPublic = in.readString();
        this.isZn = in.readString();
        this.tag = in.readString();
        this.userBean = in.readParcelable(UserBean.class.getClassLoader());
        this.datetime = in.readString();
        this.like = in.readInt();
        this.step = in.readInt();
        this.attent = in.readInt();
        this.follow = in.readInt();
        this.distance = in.readString();
        this.viewOkNum = in.readString();
        this.musicInfo = in.readParcelable(VideoMusicBean.class.getClassLoader());
        this.goods = in.readParcelable(GoodsBean.class.getClassLoader());
    }


    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }

        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }
    };

    public String getTag() {
        return "VideoBean" + this.getId() + this.hashCode();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof VideoBean)) return false;
        VideoBean bean = (VideoBean) obj;
        return bean.getId().equals(id);

    }

    //广告isAd=1 助农isZn=1 众简广告isAd=99
    @Override
    public int getItemType() {
        try {
            if (isZn.equals("1")) {
                return 1;
            } else if (isAd.equals("1")) {
                return 2;
            } else if (isAd.equals("99")) {
                return 99;
            } else {
                return 3;
            }
        } catch (Exception e) {
            return 3;
        }
    }
}
