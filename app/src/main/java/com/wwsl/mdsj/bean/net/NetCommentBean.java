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
public class NetCommentBean {
    /**
     * id : 91
     * content : 你你你
     * likes : 0
     * uid : 103098
     * videoid : 3908
     * touid : 102984
     * datetime : 2天前
     * isAttention : 0
     * note : 评论了你的作品
     * user_avatar : http://ek023.wanwusl.com//default.jpg
     * user_nicename : 手机用户0001
     * video_title : 80后的我们面临着工作、家庭、孩子、老人各方面的压力，有同感的吗？别太累，和我一起跳舞锻炼身体吧?@
     * video_thumb : https://shangbang-dev-1301614530.cos.ap-chongqing.myqcloud.com/0526/451.jpg
     * video_href : https://shangbang-dev-1301614530.cos.ap-chongqing.myqcloud.com/0526/451.mp4
     */

    private String id;
    private String content;
    private String likes;
    private String uid;
    @JSONField(name = "videoid")
    private String videoId;
    @JSONField(name = "touid")
    private String toUid;
    private String datetime;
    @JSONField(name = "isAttention")
    private int attention;
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
