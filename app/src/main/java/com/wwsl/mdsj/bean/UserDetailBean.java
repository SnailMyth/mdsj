package com.wwsl.mdsj.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor()
@Builder
public class UserDetailBean {

    /**
     * id : 102984
     * user_nicename : 豆豆
     * avatar : http://hougong001.mdmz.xyz/20200620/5eed8421165a8.png
     * avatar_thumb : http://hougong001.mdmz.xyz/20200620/5eed8421165a8.png
     * sex : 2
     * signature : 这家伙很懒，什么都没留下
     * coin : 8927885.60
     * consumption : 500977575
     * votestotal : 7307911.52
     * province :
     * city : 未知地址
     * birthday :
     * user_status : 1
     * issuper : 0
     * live_thumb : http://www.hougong1.net/api/upload/thumb/20200430/06415835410590457.png
     * level : 23
     * level_anchor : 7
     * vip : 0
     * liang : 66666
     * tgcode : 37VL3T
     * follows : 4
     * fans : 11
     * isattention : 1
     * isblack : 0
     * isblack2 : 0
     * islive : 0
     * living :
     * label : [{"uid":"102984","touid":"0","label":"17岁","addtime":"0","uptime":"0"},{"uid":"102984","touid":"0","label":"重庆师范大学","addtime":"0","uptime":"0"}]
     * contribute : [{"uid":"103126","total":"74252.00","avatar":"http://zy888.wanwusl.com//default.jpg"},{"uid":"103063","total":"38971.40","avatar":"http://zy888.wanwusl.com//default.jpg"},{"uid":"103075","total":"28542.10","avatar":"http://hougong001.mdmz.xyz/20200505/5eb13e731a503.jpg"}]
     * circle : 6442
     * videonums : 6442
     * dianzannums :
     * livenums : 60
     * likevideonums : 1
     */

    private String id;
    @JSONField(name = "user_nicename")
    private String username;
    private String avatar;
    @JSONField(name = "avatar_thumb")
    private String avatarThumb;//用户背景图
    private String sex;
    private String signature;
    private String coin;
    private String consumption;//总消费金额
    @JSONField(name = "votestotal")
    private String votesTotal;
    private String province;
    private String city;
    private String birthday;
    @JSONField(name = "user_status")
    private String status;
    @JSONField(name = "issuper")
    private String isSuper;
    private String live_thumb;
    private String level;
    @JSONField(name = "level_anchor")
    private String levelAnchor;
    private String vip;
    private String liang;
    private String tgcode;


    @JSONField(name = "isattention")
    private int attention;//是否关注
    private String isblack;
    private String isblack2;
    private String islive;
    private String living;


    @JSONField(name = "dianzannums")
    private String dzNames;//点赞数
    @JSONField(name = "livenums")
    private String liveNum;//直播次数

    private String follows;//关注数
    private String fans;//粉丝数
    @JSONField(name = "circle")
    private String trends;//动态数
    @JSONField(name = "videonums")
    private String videoNum;//作品数
    @JSONField(name = "likevideonums")
    private String likeVideoNum;//喜欢的视频数量
    private String mobile;

    @JSONField(name = "is_eye_phone")
    protected int isPhonePublic;//是否允许他人查看手机号

    private List<UserLabelBean> label;
    private List<UserContributeBean> contribute;

}
