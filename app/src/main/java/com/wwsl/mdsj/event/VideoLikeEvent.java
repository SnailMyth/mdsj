package com.wwsl.mdsj.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by cxf on 2018/12/17.
 */

@Getter
@Setter
public class VideoLikeEvent {
    private String videoId;
    private int isLike;
    private String likeNum;

    public VideoLikeEvent(String videoId, int isLike, String likeNum) {
        this.videoId = videoId;
        this.isLike = isLike;
        this.likeNum = likeNum;
    }
}
