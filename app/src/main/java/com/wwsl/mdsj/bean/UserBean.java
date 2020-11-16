package com.wwsl.mdsj.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by cxf on 2017/8/14.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserBean implements Parcelable {

    protected String id;
    @JSONField(name = "user_nicename")
    protected String username;
    protected String mobile;
    protected String avatar;
    @JSONField(name = "avatar_thumb")
    protected String avatarThumb;
    protected int sex;
    protected String signature;

    /**
     * 座右铭
     */
    protected String coin;
    protected String votes;
    protected String consumption;
    protected String votestotal;
    protected String province;
    protected String city;
    protected String birthday;
    protected int level;
    @JSONField(name = "level_anchor")
    protected int levelAnchor;
    /**
     * 直播数量
     */
    protected int lives;
    @JSONField(name = "dznums")
    protected int dzNum;

    @JSONField(name = "myvideonums")
    protected int videoNum;

    @JSONField(name = "myvideolikenums")
    protected int likeVideoNum;
    /**
     * 关注数量
     */
    protected int follows;
    /**
     * 是否已关注
     */
    protected int follow;

    protected int fans;

    @JSONField(name = "haoyounums")
    protected int friendNum;//好友数量
    /**
     * 粉丝数量
     */
    protected int vip;
    @JSONField(name = "liang")
    protected String specialAccount;
    protected Car car;
    protected int circle;
    @JSONField(name = "parentinfo")
    protected ParentUser parentInfo;

    //是否直播已认证
    protected int auth;

    @JSONField(name = "live_thumb")
    protected String liveThumb;

    @JSONField(name = "tg_code")
    protected String tgCode;
    //大于0时，正在直播
    protected String islive;

    @JSONField(name = "living")
    protected LiveBean liveBean;

    @JSONField(name = "list")
    protected List<List<UserItemBean>> itemList;//个人中心功能列表

    @JSONField(name = "is_have_code")
    protected String isHaveCode;

    protected String age;

    protected int workCount;
    /**
     * 作品数量
     */

    protected int dynamicCount;
    /**
     * 动态数量
     */

    protected int likeCount;

    /**
     * 喜欢数量
     */
    @JSONField(name = "coin_password")
    protected String coinPassword;

    @JSONField(name = "money_rate")
    protected float moneyRate;
    @JSONField(name = "tips")
    protected String tips;

    @JSONField(name = "is_vip")
    protected int isVip;

    @JSONField(name = "is_eye_phone")
    protected int isPhonePublic;//是否允许他人查看手机号

    @JSONField(name = "is_up_video")
    protected int canUploadVideo;//是否可以上传视频

    @JSONField(name = "is_comment")
    protected int canComment;//是否可以评论

    @JSONField(name = "is_chuchuang")
    protected int isHaveShowWindow;//是否有橱窗功能

    @JSONField(name = "is_auth")
    protected int isIdIdentify;//是否实名认证

    @JSONField(name = "partner_id")
    protected String partnerId;//合伙人id

    @JSONField(name = "master_level")
    private int masterLevel;
    @JSONField(name = "task_nums")
    private int taskNum;
    @JSONField(name = "wechat")
    private String wxName;
    private String pid;//上级id
    @JSONField(name = "maodou")
    protected String maodou;//毛豆数量
    @JSONField(name = "is_transfer_maodou")
    protected int canTransMd;//是否有好友转换
    @JSONField(name = "transfer_maodou_num")
    protected String maxTransNum;//最大毛豆

    @JSONField(name = "mdsjsc_order")
    protected String marketUrl;//商城url

    @JSONField(name = "servicecharge")
    protected String depositRate;//提现比例
    @JSONField(name = "welfare_money_rate")
    protected String welfareDepositRate;//福利豆丁提现比例

    @JSONField(name = "commission")
    protected String commission;//福利豆丁

    @JSONField(name = "is_wechat")
    protected String isWxAuth;//微信是否授权

    @JSONField(name = "is_high_grade_auth")
    protected String isCanPubYxYp;//是否能发布一乡一品视频

    @JSONField(name = "zn_register_url")
    protected String znUrl;//助农url

    @JSONField(name = "aixin_zn_url")
    protected String axUrl;//爱心大使url

    @JSONField(name = "is_aixin")
    protected int showAx;//爱心大使图标 1显示,0隐藏

    public int getLevel() {
        if (level == 0) {
            level = 1;
        }
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 显示靓号
     */
    public String getSpecialNameTip() {
        if (this.specialAccount != null) {
            if (!TextUtils.isEmpty(specialAccount) && !"0".equals(specialAccount)) {
//                return WordUtil.getString(R.string.live_liang) + ":" + liangName;
                return specialAccount;
            }
        }
        return "" + this.id;
    }

    /**
     * 获取靓号
     */
    public String getGoodName() {
        if (this.specialAccount != null) {
            return this.specialAccount;
        }
        return "0";
    }

    protected UserBean(Parcel in) {
        id = in.readString();
        username = in.readString();
        mobile = in.readString();
        avatar = in.readString();
        avatarThumb = in.readString();
        sex = in.readInt();
        signature = in.readString();
        coin = in.readString();
        votes = in.readString();
        consumption = in.readString();
        votestotal = in.readString();
        province = in.readString();
        city = in.readString();
        birthday = in.readString();
        level = in.readInt();
        levelAnchor = in.readInt();
        lives = in.readInt();
        dzNum = in.readInt();
        videoNum = in.readInt();
        likeVideoNum = in.readInt();
        follows = in.readInt();
        follow = in.readInt();
        fans = in.readInt();
        friendNum = in.readInt();
        vip = in.readInt();
        specialAccount = in.readString();
        car = in.readParcelable(Car.class.getClassLoader());
        circle = in.readInt();
        parentInfo = in.readParcelable(ParentUser.class.getClassLoader());
        auth = in.readInt();
        liveThumb = in.readString();
        tgCode = in.readString();
        islive = in.readString();
        liveBean = in.readParcelable(LiveBean.class.getClassLoader());
        isHaveCode = in.readString();
        age = in.readString();
        workCount = in.readInt();
        dynamicCount = in.readInt();
        likeCount = in.readInt();
        coinPassword = in.readString();
        moneyRate = in.readFloat();
        tips = in.readString();
        isVip = in.readInt();
        isPhonePublic = in.readInt();
        canUploadVideo = in.readInt();
        canComment = in.readInt();
        isHaveShowWindow = in.readInt();
        isIdIdentify = in.readInt();
        partnerId = in.readString();
        masterLevel = in.readInt();
        taskNum = in.readInt();
        wxName = in.readString();
        pid = in.readString();
        maodou = in.readString();
        canTransMd = in.readInt();
        maxTransNum = in.readString();
        marketUrl = in.readString();
        depositRate = in.readString();
        welfareDepositRate = in.readString();
        commission = in.readString();
        isWxAuth = in.readString();
        isCanPubYxYp = in.readString();
        znUrl = in.readString();
        axUrl = in.readString();
        showAx = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(mobile);
        dest.writeString(avatar);
        dest.writeString(avatarThumb);
        dest.writeInt(sex);
        dest.writeString(signature);
        dest.writeString(coin);
        dest.writeString(votes);
        dest.writeString(consumption);
        dest.writeString(votestotal);
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(birthday);
        dest.writeInt(level);
        dest.writeInt(levelAnchor);
        dest.writeInt(lives);
        dest.writeInt(dzNum);
        dest.writeInt(videoNum);
        dest.writeInt(likeVideoNum);
        dest.writeInt(follows);
        dest.writeInt(follow);
        dest.writeInt(fans);
        dest.writeInt(friendNum);
        dest.writeInt(vip);
        dest.writeString(specialAccount);
        dest.writeParcelable(car, flags);
        dest.writeInt(circle);
        dest.writeParcelable(parentInfo, flags);
        dest.writeInt(auth);
        dest.writeString(liveThumb);
        dest.writeString(tgCode);
        dest.writeString(islive);
        dest.writeParcelable(liveBean, flags);
        dest.writeString(isHaveCode);
        dest.writeString(age);
        dest.writeInt(workCount);
        dest.writeInt(dynamicCount);
        dest.writeInt(likeCount);
        dest.writeString(coinPassword);
        dest.writeFloat(moneyRate);
        dest.writeString(tips);
        dest.writeInt(isVip);
        dest.writeInt(isPhonePublic);
        dest.writeInt(canUploadVideo);
        dest.writeInt(canComment);
        dest.writeInt(isHaveShowWindow);
        dest.writeInt(isIdIdentify);
        dest.writeString(partnerId);
        dest.writeInt(masterLevel);
        dest.writeInt(taskNum);
        dest.writeString(wxName);
        dest.writeString(pid);
        dest.writeString(maodou);
        dest.writeInt(canTransMd);
        dest.writeString(maxTransNum);
        dest.writeString(marketUrl);
        dest.writeString(depositRate);
        dest.writeString(welfareDepositRate);
        dest.writeString(commission);
        dest.writeString(isWxAuth);
        dest.writeString(isCanPubYxYp);
        dest.writeString(znUrl);
        dest.writeString(axUrl);
        dest.writeInt(showAx);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public static class Vip implements Parcelable {
        protected int type;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public Vip() {

        }

        public Vip(Parcel in) {
            this.type = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.type);
        }

        public static final Creator<Vip> CREATOR = new Creator<Vip>() {
            @Override
            public Vip[] newArray(int size) {
                return new Vip[size];
            }

            @Override
            public Vip createFromParcel(Parcel in) {
                return new Vip(in);
            }
        };
    }

    public static class SpecialAccount implements Parcelable {
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SpecialAccount() {

        }

        public SpecialAccount(Parcel in) {
            this.name = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
        }

        public static final Creator<SpecialAccount> CREATOR = new Creator<SpecialAccount>() {

            @Override
            public SpecialAccount createFromParcel(Parcel in) {
                return new SpecialAccount(in);
            }

            @Override
            public SpecialAccount[] newArray(int size) {
                return new SpecialAccount[size];
            }
        };

    }

    public static class ParentUser implements Parcelable {


        /**
         * id : 10000000
         * user_nicename : 刘犇
         * avatar :
         */

        private String id;
        @JSONField(name = "user_nicename")
        private String username;
        private String avatar;
        private String mobile;


        public ParentUser() {

        }

        protected ParentUser(Parcel in) {
            id = in.readString();
            username = in.readString();
            avatar = in.readString();
            mobile = in.readString();
        }

        public static final Creator<ParentUser> CREATOR = new Creator<ParentUser>() {
            @Override
            public ParentUser createFromParcel(Parcel in) {
                return new ParentUser(in);
            }

            @Override
            public ParentUser[] newArray(int size) {
                return new ParentUser[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(username);
            dest.writeString(avatar);
            dest.writeString(mobile);
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    public static class Car implements Parcelable {
        protected int id;
        protected String swf;
        protected float swftime;
        protected String words;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSwf() {
            return swf;
        }

        public void setSwf(String swf) {
            this.swf = swf;
        }

        public float getSwftime() {
            return swftime;
        }

        public void setSwftime(float swftime) {
            this.swftime = swftime;
        }

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }

        public Car() {

        }

        public Car(Parcel in) {
            this.id = in.readInt();
            this.swf = in.readString();
            this.swftime = in.readFloat();
            this.words = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.swf);
            dest.writeFloat(this.swftime);
            dest.writeString(this.words);
        }


        public static final Creator<Car> CREATOR = new Creator<Car>() {
            @Override
            public Car[] newArray(int size) {
                return new Car[size];
            }

            @Override
            public Car createFromParcel(Parcel in) {
                return new Car(in);
            }
        };
    }

}
