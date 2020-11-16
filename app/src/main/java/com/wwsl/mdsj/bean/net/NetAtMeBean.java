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
public class NetAtMeBean {

    /**
     * id : 197
     * thumb : http://1301614530.vod2.myqcloud.com/deac1efavodcq1301614530/a5c675395285890801714799296/5285890801714799297.png
     * title : 真材实料，赶紧放马过来捏呀！！哼……………
     * uid : 103007
     * at_user : 102984
     * datetime : 2020-04-25
     * isAttention : 1
     * user_avatar : http://ek023.wanwusl.com//api/upload/avatar/20200620/06459693982596392.png
     * user_nicename : 哈哈哈哈古古怪怪
     */

    private String id;
    private String thumb;
    private String title;
    private String uid;
    @JSONField(name = "at_user")
    private String atUser;
    private String datetime;
    @JSONField(name = "isAttention")
    private int attention;
    @JSONField(name = "user_avatar")
    private String avatar;
    @JSONField(name = "user_nicename")
    private String nickname;

}
