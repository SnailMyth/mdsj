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
public class NetLikeBean {


    /**
     * id : 1
     * uid : 102984
     * videoid : 2190
     * commentid : 72
     * type : 1
     * touid : 102984
     * datetime : 1970-01-01
     * note : 赞了你的作品
     * user_avatar : http://hougong001.mdmz.xyz/20200620/5eed8421165a8.png
     * user_nicename : 豆豆
     * video_title : 春风得意时布好局，才能在四面楚歌时有条路@容易保保险  #保险升级1
     * video_thumb : https://shangbang-dev-1301614530.cos.ap-chongqing.myqcloud.com/0523/191.jpg
     * video_href : https://shangbang-dev-1301614530.cos.ap-chongqing.myqcloud.com/0523/191.mp4
     */

    private String id;
    private String uid;
    @JSONField(name = "videoid")
    private String videoId;
    @JSONField(name = "commentid")
    private String commentId;
    private String type;
    @JSONField(name = "touid")
    private String toUid;
    private String datetime;
    private String note;
    @JSONField(name = "user_avatar")
    private String avatar;
    @JSONField(name = "user_nicename")
    private String username;
    @JSONField(name = "video_title")
    private String videoTitle;
    @JSONField(name = "video_thumb")
    private String videoThumb;
    @JSONField(name = "video_href")
    private String videoHref;

}
