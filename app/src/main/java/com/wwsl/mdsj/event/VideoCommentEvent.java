package com.wwsl.mdsj.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoCommentEvent {
    private String id;
    private String num;

    public VideoCommentEvent(String id, String num) {
        this.id = id;
        this.num = num;
    }
}
