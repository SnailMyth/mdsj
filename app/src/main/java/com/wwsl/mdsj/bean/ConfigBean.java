package com.wwsl.mdsj.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cxf on 2017/8/5.
 */
@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class ConfigBean implements Parcelable {

    @JSONField(name = "apk_ver")
    private String version;//Android apk安装包 版本号
    @JSONField(name = "app_android")
    private String appAndroid;//直播分享下载连接
    @JSONField(name = "apk_url")
    private String downloadApkUrl;//Android apk安装包 下载地址
    @JSONField(name = "apk_des")
    private String updateDes;//版本更新描述
    @JSONField(name = "wx_siteurl")
    private String liveWxShareUrl;//直播间微信分享地址
    @JSONField(name = "share_title")
    private String liveShareTitle;//直播间分享标题
    @JSONField(name = "share_des")
    private String liveShareDes;//直播间分享描述
    @JSONField(name = "video_share_title")
    private String videoShareTitle;//短视频分享标题
    @JSONField(name = "video_share_des")
    private String videoShareDes;//短视频分享描述
    @JSONField(name = "video_audit_switch")
    private int videoAuditSwitch;//短视频审核是否开启

    @JSONField(name = "cloudtype")
    private int videoCloudType;//短视频云储存类型 1七牛云 2腾讯云
    @JSONField(name = "qiniu_domain")
    private String videoQiNiuHost;//短视频七牛云域名

    @JSONField(name = "txcloud_appid")
    private String txCosAppId;//腾讯云存储appId
    @JSONField(name = "txcloud_region")
    private String txCosRegion;//腾讯云存储区域
    @JSONField(name = "txcloud_bucket")
    private String txCosBucketName;//腾讯云存储桶名字
    @JSONField(name = "txvideofolder")
    private String txCosVideoPath;//腾讯云存储视频文件夹
    @JSONField(name = "tximgfolder")
    private String txCosImagePath;//腾讯云存储图片文件夹
    @JSONField(name = "name_coin")
    private String coinName;//钻石名称
    @JSONField(name = "name_votes")
    private String votesName;//映票名称

    @JSONField(name = "live_time_coin")
    private String[] liveTimeCoin;//直播间计时收费规则
    @JSONField(name = "login_type")
    private String[] loginType;//三方登录类型
    @JSONField(name = "live_type")
    private String[][] liveType;//直播间开播类型
    @JSONField(name = "share_type")
    private String[] shareType;//分享类型
    @JSONField(name = "liveclass")
    private List<LiveClassBean> liveClass;//直播分类
    @JSONField(name = "maintain_switch")
    private int maintainSwitch;//维护开关
    @JSONField(name = "maintain_tips")
    private String maintainTips;//维护提示
    @JSONField(name = "sprout_key")
    private String beautyKey;//萌颜鉴权码
    @JSONField(name = "sprout_white")
    private int beautyMeiBai;//萌颜美白数值
    @JSONField(name = "sprout_skin")
    private int beautyMoPi;//萌颜磨皮数值
    @JSONField(name = "sprout_saturated")
    private int beautyBaoHe;//萌颜饱和数值
    @JSONField(name = "sprout_pink")
    private int beautyFenNen;//萌颜粉嫩数值
    @JSONField(name = "sprout_eye")
    private int beautyBigEye;//萌颜大眼数值
    @JSONField(name = "sprout_face")
    private int beautyFace;//萌颜瘦脸数值

    @JSONField(name = "sprout_chin")
    private int beautyChin;//萌颜下巴数值
    @JSONField(name = "sprout_forehead")
    private int beautyForehead;//萌颜额头数值
    @JSONField(name = "sprout_mouth")
    private int beautyMouth;//萌颜嘴型数值

    @JSONField(name = "index_html")
    private IndexHtml indexHtml;
    @JSONField(name = "video_comment_switch")
    private String videoCommentSwitch;//短视频评论是否开启，0开启  1关闭
    @JSONField(name = "pk_time")
    private String pkTime;//主播PK时间
    @JSONField(name = "punish_time")
    private String punishTime;//主播PK惩罚时间
    @JSONField(name = "start_img")
    private List<StartImg> startImg;

    @JSONField(name = "is_reg_code")
    private String isRegCode;

    @JSONField(name = "share_logo")
    private String shareLogo;
    @JSONField(name = "image_quality")
    private String imageQuality;//直播清晰度; 1 或没有保持不变，普通清晰度   2，高清   3、超清

    @JSONField(name = "gonggao_switch")
    private String showSysTips;

    @JSONField(name = "gonggao_tips")
    private String sysTips;

    @JSONField(name = "share_url")
    private String shareUrl;

    @JSONField(name = "bank_addre")
    private String bankAddress;
    @JSONField(name = "bank_card")
    private String bankCard;
    @JSONField(name = "bank_user_name")
    private String bankUserName;
    @JSONField(name = "bank_name")
    private String bankName;
    @JSONField(name = "daohang")
    private List<HomeRightNavBean> navBeans;

    @JSONField(name = "trade_type")
    private int tradeType;//99 关闭交易  11双开 12仅点对点 21仅平台

    @JSONField(name = "mizang_switch")
    private String userCenterShow;//是否显示个人空间

    @JSONField(name = "is_wx_auth")
    private String isNeedAuthWx;//是否强制绑定微信

    @JSONField(name = "ads_list")
    private List<LaunchAdBean> adList;//启动页广告

    @JSONField(name = "ads_value_list")
    private List<AdvertiseBean> adInnerList;//app内广告

    @JSONField(name = "is_launch")
    private String isLaunch;//是否显示广告

    @JSONField(name = "is_advertise")
    private String isAdvertise;//是否显示广告

    @JSONField(name = "nb_apk_link")
    private String updateApkUrl;//app更新url

    @JSONField(name = "is_kf_switch")
    private String isOpenKf;//是否打开客服

    @JSONField(name = "apply_partner_money")
    private String applyPartnerMoney;//是否打开客服

    @JSONField(name = "video_ad_link")
    private String videoAdLink;//首页视频广告链接

    @JSONField(name = "is_keep_new")
    private String isNeedUpdate;//是否强制更新

    @JSONField(name = "is_zn_activity")
    private int isOpenZnImg;//助农图标是否开启 1开启,0隐藏

    @JSONField(name = "zn_activity_tb")
    private String znImgUrl;//助农图标url

    protected ConfigBean(Parcel in) {
        version = in.readString();
        appAndroid = in.readString();
        downloadApkUrl = in.readString();
        updateDes = in.readString();
        liveWxShareUrl = in.readString();
        liveShareTitle = in.readString();
        liveShareDes = in.readString();
        videoShareTitle = in.readString();
        videoShareDes = in.readString();
        videoAuditSwitch = in.readInt();
        videoCloudType = in.readInt();
        videoQiNiuHost = in.readString();
        txCosAppId = in.readString();
        txCosRegion = in.readString();
        txCosBucketName = in.readString();
        txCosVideoPath = in.readString();
        txCosImagePath = in.readString();
        coinName = in.readString();
        votesName = in.readString();
        liveTimeCoin = in.createStringArray();
        loginType = in.createStringArray();
        shareType = in.createStringArray();
        maintainSwitch = in.readInt();
        maintainTips = in.readString();
        beautyKey = in.readString();
        beautyMeiBai = in.readInt();
        beautyMoPi = in.readInt();
        beautyBaoHe = in.readInt();
        beautyFenNen = in.readInt();
        beautyBigEye = in.readInt();
        beautyFace = in.readInt();
        beautyChin = in.readInt();
        beautyForehead = in.readInt();
        beautyMouth = in.readInt();
        videoCommentSwitch = in.readString();
        pkTime = in.readString();
        punishTime = in.readString();
        isRegCode = in.readString();
        shareLogo = in.readString();
        imageQuality = in.readString();
        showSysTips = in.readString();
        sysTips = in.readString();
        shareUrl = in.readString();
        bankAddress = in.readString();
        bankCard = in.readString();
        bankUserName = in.readString();
        bankName = in.readString();
        tradeType = in.readInt();
        userCenterShow = in.readString();
        isNeedAuthWx = in.readString();
        adList = in.createTypedArrayList(LaunchAdBean.CREATOR);
        adInnerList = in.createTypedArrayList(AdvertiseBean.CREATOR);
        isLaunch = in.readString();
        isAdvertise = in.readString();
        updateApkUrl = in.readString();
        isOpenKf = in.readString();
        applyPartnerMoney = in.readString();
        videoAdLink = in.readString();
        isNeedUpdate = in.readString();
        isOpenZnImg = in.readInt();
        znImgUrl = in.readString();
    }

    public static final Creator<ConfigBean> CREATOR = new Creator<ConfigBean>() {
        @Override
        public ConfigBean createFromParcel(Parcel in) {
            return new ConfigBean(in);
        }

        @Override
        public ConfigBean[] newArray(int size) {
            return new ConfigBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(version);
        dest.writeString(appAndroid);
        dest.writeString(downloadApkUrl);
        dest.writeString(updateDes);
        dest.writeString(liveWxShareUrl);
        dest.writeString(liveShareTitle);
        dest.writeString(liveShareDes);
        dest.writeString(videoShareTitle);
        dest.writeString(videoShareDes);
        dest.writeInt(videoAuditSwitch);
        dest.writeInt(videoCloudType);
        dest.writeString(videoQiNiuHost);
        dest.writeString(txCosAppId);
        dest.writeString(txCosRegion);
        dest.writeString(txCosBucketName);
        dest.writeString(txCosVideoPath);
        dest.writeString(txCosImagePath);
        dest.writeString(coinName);
        dest.writeString(votesName);
        dest.writeStringArray(liveTimeCoin);
        dest.writeStringArray(loginType);
        dest.writeStringArray(shareType);
        dest.writeInt(maintainSwitch);
        dest.writeString(maintainTips);
        dest.writeString(beautyKey);
        dest.writeInt(beautyMeiBai);
        dest.writeInt(beautyMoPi);
        dest.writeInt(beautyBaoHe);
        dest.writeInt(beautyFenNen);
        dest.writeInt(beautyBigEye);
        dest.writeInt(beautyFace);
        dest.writeInt(beautyChin);
        dest.writeInt(beautyForehead);
        dest.writeInt(beautyMouth);
        dest.writeString(videoCommentSwitch);
        dest.writeString(pkTime);
        dest.writeString(punishTime);
        dest.writeString(isRegCode);
        dest.writeString(shareLogo);
        dest.writeString(imageQuality);
        dest.writeString(showSysTips);
        dest.writeString(sysTips);
        dest.writeString(shareUrl);
        dest.writeString(bankAddress);
        dest.writeString(bankCard);
        dest.writeString(bankUserName);
        dest.writeString(bankName);
        dest.writeInt(tradeType);
        dest.writeString(userCenterShow);
        dest.writeString(isNeedAuthWx);
        dest.writeTypedList(adList);
        dest.writeTypedList(adInnerList);
        dest.writeString(isLaunch);
        dest.writeString(isAdvertise);
        dest.writeString(updateApkUrl);
        dest.writeString(isOpenKf);
        dest.writeString(applyPartnerMoney);
        dest.writeString(videoAdLink);
        dest.writeString(isNeedUpdate);
        dest.writeInt(isOpenZnImg);
        dest.writeString(znImgUrl);
    }


    public static class IndexHtml {
        private String title;
        private String content;
        private String status;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class StartImg {
        private String pic;
        private String url;
        private String jump;

        @JSONField(name = "slide_pic")
        public String getPic() {
            return pic;
        }

        @JSONField(name = "slide_pic")
        public void setPic(String pic) {
            this.pic = pic;
        }

        @JSONField(name = "slide_url")
        public String getUrl() {
            return url;
        }

        @JSONField(name = "slide_url")
        public void setUrl(String url) {
            this.url = url;
        }

        @JSONField(name = "slide_jump")
        public String getJump() {
            return jump;
        }

        @JSONField(name = "slide_jump")
        public void setJump(String jump) {
            this.jump = jump;
        }
    }
}
