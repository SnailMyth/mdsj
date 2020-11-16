package com.wwsl.mdsj.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.bean.net.NetFansBean;
import com.wwsl.mdsj.bean.net.NetFriendBean;

import java.util.ArrayList;
import java.util.List;

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
public class FansShowBean {


    /**
     * id : 100223
     * user_nicename : 无敌
     * avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLSqLAA8RoUqZ1tiaqcosekkxVt2Rpj3vH9lpyhicbcYsQnYwhs6QgTLrsAJrYP7LVUQtficPXxIGUxg/132
     * signature : 这家伙很懒，什么都没留下
     * sex : 1
     * isattention1 : 1
     * isattention2 : 0
     */

    private String uid;
    @JSONField(name = "touid")
    private String toUid;
    @JSONField(name = "user_nicename")
    private String username;
    private String avatar;
    private String signature;
    private String sex;
    @JSONField(name = "isattention1")
    private int attention1;//uid 关注 toUid
    @JSONField(name = "isattention2")
    private int attention2;
    private int type;

    public static List<FansShowBean> parseBean(List<NetFansBean> beans, int type) {
        String uid = AppConfig.getInstance().getUid();
        List<FansShowBean> res = new ArrayList<>();
        if (beans == null) return res;
        for (NetFansBean bean : beans) {
            res.add(FansShowBean.builder()
                    .uid(bean.getUid())
                    .toUid(uid)
                    .type(type)
                    .username(bean.getName())
                    .signature(bean.getSignature())
                    .avatar(bean.getAvatar())
                    .sex(bean.getSex())
                    .attention1(1)
                    .attention2(bean.getAttention())
                    .build());
        }
        return res;
    }

    public static List<FansShowBean> parseFriendBean(List<NetFriendBean> beans, int type) {
        String uid = AppConfig.getInstance().getUid();
        List<FansShowBean> res = new ArrayList<>();
        if (beans == null) return res;
        for (NetFriendBean bean : beans) {
            res.add(FansShowBean.builder()
                    .uid(bean.getUid())
                    .toUid(bean.getTouid())
                    .type(type)
                    .username(bean.getUsername())
                    .avatar(bean.getAvatar())
                    .signature(bean.getSignature())
                    .sex(bean.getSex())
                    .attention1(1)
                    .attention2(1)
                    .build());
        }
        return res;
    }

}
