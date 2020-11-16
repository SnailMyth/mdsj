package com.wwsl.mdsj.bean;


import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.bean.net.NetAtMeBean;
import com.wwsl.mdsj.bean.net.NetCommentBean;
import com.wwsl.mdsj.bean.net.NetFansBean;
import com.wwsl.mdsj.bean.net.NetLikeBean;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author :
 * @date : 2020/6/30 18:45
 * @description : MessageShowBean 粉丝,赞,@我,评论 界面的 bean
 */
@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class MessageShowBean {
    private int type;
    private String uid;
    private String username;
    private String avatar;
    private String content;
    private String actionDes;
    private String time;
    private String thumb;
    private int follow;// 1.关注我 2.相互关注
    private String videoId;

    public static List<MessageShowBean> parseFans(List<NetFansBean> beans) {
        List<MessageShowBean> res = new ArrayList<>();
        if (beans == null) return res;
        for (NetFansBean bean : beans) {
            res.add(MessageShowBean.builder()
                    .uid(bean.getUid())
                    .type(Constants.TYPE_FANS)
                    .username(bean.getName())
                    .avatar(bean.getAvatar())
                    .follow(bean.getAttention() == 1 ? 2 : 1)
                    .content("关注了我")
                    .time(bean.getAddtime())
                    .build());
        }
        return res;
    }

    public static List<MessageShowBean> parseAtMe(List<NetAtMeBean> beans) {
        List<MessageShowBean> res = new ArrayList<>();
        if (beans == null) return res;
        for (NetAtMeBean bean : beans) {
            res.add(MessageShowBean.builder()
                    .uid(bean.getUid())
                    .type(Constants.TYPE_AT_ME)
                    .username(bean.getNickname())
                    .avatar(bean.getAvatar())
                    .content(bean.getTitle())
                    .thumb(bean.getThumb())
                    .time(bean.getDatetime())
                    .build());
        }

        return res;
    }

    public static List<MessageShowBean> parseComment(List<NetCommentBean> beans) {
        List<MessageShowBean> res = new ArrayList<>();
        if (beans == null) return res;
        for (NetCommentBean bean : beans) {
            res.add(MessageShowBean.builder()
                    .uid(bean.getUid())
                    .type(Constants.TYPE_COMMENT)
                    .username(bean.getUsername())
                    .avatar(bean.getAvatar())
                    .content(bean.getContent())
                    .actionDes(bean.getNote())
                    .thumb(bean.getVideoThumb())
                    .time(bean.getDatetime())
                    .videoId(bean.getVideoId())
                    .build());
        }

        return res;
    }

    public static List<MessageShowBean> parseLike(List<NetLikeBean> beans) {
        List<MessageShowBean> res = new ArrayList<>();
        if (beans == null) return res;
        for (NetLikeBean bean : beans) {
            res.add(MessageShowBean.builder()
                    .uid(bean.getToUid())
                    .type(Constants.TYPE_LIKE)
                    .username(bean.getUsername())
                    .avatar(bean.getAvatar())
                    .content(bean.getNote())
                    .thumb(bean.getVideoThumb())
                    .time(bean.getDatetime())
                    .videoId(bean.getVideoId())
                    .build());
        }

        return res;
    }


}
