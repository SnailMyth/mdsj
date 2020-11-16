package com.wwsl.mdsj.event;

import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.bean.VideoCommentBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class CommentDialogEvent {
    boolean openFace;
    VideoBean videoBean;
    VideoCommentBean commentBean;
}