package com.wwsl.mdsj.bean.net;


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
public class NetFriendBean {
    /**
     * touid : 102984
     * user_nicename : 吃饭睡觉打豆豆
     * avatar : http://zy888.wanwusl.com/api/upload/avatar/20200704/maodou.png
     * signature : 这家伙很懒，什么都没留下
     * sex : 1
     * isattention1 : 1
     * isattention2 : 1
     * uid : 10000013
     */

    private String touid;
    @JSONField(name = "user_nicename")
    private String username;
    private String avatar;
    private String signature;
    private String sex;
    @JSONField(name = "isattention1")
    private int isAttention;
    @JSONField(name = "isattention2")
    private int isBeAttention;
    private String uid;
    /**
     * isattention : 0
     * name : 用户不存在
     * uid : 102958
     * avatar : http://ek023.wanwusl.com//default.jpg
     * age : 0
     * city : 0
     * sex : 0
     * addtime : 0
     */
}
