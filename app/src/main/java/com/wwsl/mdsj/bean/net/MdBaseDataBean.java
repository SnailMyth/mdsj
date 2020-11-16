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

@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class MdBaseDataBean implements Parcelable {


    /**
     * myactivecount : 16
     * huoyuecountagent : 0
     * heroactive : 0
     * allactivecount : 16.00
     * maodou_frozen : 164.00
     * todayget : 0.00
     * maodou : 57.28
     * activecount : 16.00
     * activecountplus : 1.00
     * todayrate : 40%
     * today_jie : 0.3666
     * friend_count
     * is_auth_count:
     * not_auth_count:
     */

    @JSONField(name = "myactivecount")
    private String myActive;//个人活跃度
    @JSONField(name = "huoyuecountagent")
    private String teamActive;//联盟活跃度
    @JSONField(name = "heroactive")
    private String heroActive;//英雄活跃度
    @JSONField(name = "allactivecount")
    private String userTotalActive;//用户总活跃度
    @JSONField(name = "maodou_frozen")
    private String maodouFrozen;//毛豆冻结
    @JSONField(name = "todayget")
    private String todayGet;//今日所得毛豆
    @JSONField(name = "maodou")
    private String maoDou;//我的毛豆
    @JSONField(name = "activecount")
    private String baseActive;//基础活跃度
    @JSONField(name = "activecountplus")
    private String activePlus;//活跃度加成
    @JSONField(name = "todayrate")
    private String todayRate;//今日进度结算所得比例
    @JSONField(name = "today_jie")
    private String todaySettlement;//今日进度结算所得毛豆
    @JSONField(name = "family_active")
    private String familyActive;//家族活跃度

    @JSONField(name = "allamountmoney_last")
    private String remainIncome;//剩余可收益

    @JSONField(name = "friend_count")
    private String friendCount;//活跃好友

    @JSONField(name = "is_auth_count")
    private String authCount;//家族认证数量
    @JSONField(name = "not_auth_count")
    private String notAuthCount;//家族未认证认证数量

    protected MdBaseDataBean(Parcel in) {
        myActive = in.readString();
        teamActive = in.readString();
        heroActive = in.readString();
        userTotalActive = in.readString();
        maodouFrozen = in.readString();
        todayGet = in.readString();
        maoDou = in.readString();
        baseActive = in.readString();
        activePlus = in.readString();
        todayRate = in.readString();
        todaySettlement = in.readString();
        familyActive = in.readString();
        remainIncome = in.readString();
        friendCount = in.readString();
        authCount = in.readString();
        notAuthCount = in.readString();
    }

    public static final Creator<MdBaseDataBean> CREATOR = new Creator<MdBaseDataBean>() {
        @Override
        public MdBaseDataBean createFromParcel(Parcel in) {
            return new MdBaseDataBean(in);
        }

        @Override
        public MdBaseDataBean[] newArray(int size) {
            return new MdBaseDataBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(myActive);
        dest.writeString(teamActive);
        dest.writeString(heroActive);
        dest.writeString(userTotalActive);
        dest.writeString(maodouFrozen);
        dest.writeString(todayGet);
        dest.writeString(maoDou);
        dest.writeString(baseActive);
        dest.writeString(activePlus);
        dest.writeString(todayRate);
        dest.writeString(todaySettlement);
        dest.writeString(familyActive);
        dest.writeString(remainIncome);
        dest.writeString(friendCount);
        dest.writeString(authCount);
        dest.writeString(notAuthCount);
    }
}
