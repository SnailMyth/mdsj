package com.wwsl.mdsj.utils.tiktok;

import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.views.LikeView;

/**
 * @author :
 * @date : 2020/6/17 16:34
 * @description : 视频控制器点击监听接口
 */
public interface OnVideoLayoutClickListener {

    void onClickEvent(int type, VideoBean bean);
}
