package com.wwsl.mdsj.interfaces;

import com.wwsl.mdsj.bean.CommentBean;

public interface OnTrendCommentItemClickListener {
    void onItemClick(CommentBean commentBean);

    void onUserName(CommentBean commentBean);

    void onToUserName(CommentBean commentBean);
}
