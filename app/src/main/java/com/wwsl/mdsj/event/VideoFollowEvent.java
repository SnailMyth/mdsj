package com.wwsl.mdsj.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by cxf on 2018/9/28.
 */
@Getter
@Setter
public class VideoFollowEvent {

    private int mFrom;
    private String mToUid;
    private int mIsAttention;

    public VideoFollowEvent(int from, String toUid, int isAttention) {
        mFrom = from;
        mToUid = toUid;
        mIsAttention = isAttention;
    }

}
