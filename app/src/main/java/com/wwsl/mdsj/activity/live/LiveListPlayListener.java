package com.wwsl.mdsj.activity.live;

import android.os.Bundle;
import android.view.View;

import com.frame.fire.util.LogUtils;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

public class LiveListPlayListener implements ITXLivePlayListener {

    private final static String TAG = "LiveListPlayListener";

    private View readyView;
    private View playContain;

    public LiveListPlayListener(View readyView, View playContain) {
        this.readyView = readyView;
        this.playContain = playContain;
    }


    @Override
    public void onPlayEvent(int e, Bundle bundle) {
        LogUtils.e(TAG, "onPlayEvent: " + e);
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN://播放开始

                if (readyView != null) {
                    readyView.setVisibility(View.GONE);
                }

                if (playContain != null) {
                    playContain.setVisibility(View.VISIBLE);
                }

                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:

                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME://第一帧

                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END://播放结束
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT://播放结束
                // TODO: 2020/8/18 接收不到stop 消息
                if (readyView != null) {
                    readyView.setVisibility(View.VISIBLE);
                }

                if (playContain != null) {
                    playContain.setVisibility(View.GONE);
                }
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION://获取视频宽高
                break;
            case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                ToastUtil.show(WordUtil.getString(R.string.live_play_error));
                break;
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}
