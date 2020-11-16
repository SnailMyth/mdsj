package com.wwsl.mdsj.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor( )
@NoArgsConstructor
@Builder
public class GiftDetailShowBean {


    /**
     * id : 43
     * type : expend
     * action : sendgift
     * uid : 103007
     * touid : 102984
     * giftid : 109
     * giftcount : 1
     * totalcoin : 48888
     * showid : 1588235487
     * addtime : 2020-04-30 16:37:00
     * userinfo : 哈哈哈哈
     * touserinfo : 这个人不错
     * giftinfo : 兰博基尼
     * gifticon : null
     *
     */

    private String id;
    private String type;
    private String action;
    private String uid;
    private String touid;
    private String giftid;
    private String giftcount;
    private String totalcoin;
    private String showId;
    private String addtime;
    private String userinfo;
    private String touserinfo;
    private String giftinfo;
    private String gifticon;
    private String votes;
    private String belongType;
    private String cover;
}
