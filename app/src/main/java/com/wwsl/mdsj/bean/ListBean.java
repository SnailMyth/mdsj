package com.wwsl.mdsj.bean;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by cxf on 2018/2/2.
 * 排行榜实体类
 */

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor( )
public class ListBean {
    @JSONField(name = "totalcoin")
    private String totalCoin;
    private String uid;
    @JSONField(name = "user_nicename")
    private String userNiceName;
    @JSONField(name = "avatar_thumb")
    private String avatarThumb;
    private int sex;
    private int levelAnchor;
    private int level;

    private int attention;
    private int type;

    @JSONField(name = "is_live")
    private int isLiving;


    public String getTotalCoinFormat() {
        return String.valueOf(totalCoin);
    }
}
