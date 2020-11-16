package com.wwsl.mdsj.views;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.interfaces.LivePushListener;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.ToastUtil;

/**
 * Created by cxf on 2018/10/26.
 * 连麦推流小窗口
 */

public class LiveLinkMicPushViewHolder extends AbsViewHolder implements ITXLivePushListener {

    private static final String TAG = "LivePushViewHolder";
    private TXCloudVideoView mVideoView;
    private TXLivePusher mLivePusher;
    private LivePushListener mLivePushListener;
    private boolean mPaused;
    private boolean mStartPush;

    public LiveLinkMicPushViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_link_mic_push;
    }

    @Override
    public void init() {
        mVideoView = (TXCloudVideoView) findViewById(R.id.camera_preview);
        mLivePusher = new TXLivePusher(mContext);
        TXLivePushConfig mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.enableAEC(true);
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setPushListener(this);
    }

    @Override
    public void onPushEvent(int i, Bundle bundle) {
        if (mLivePusher == null) {
            return;
        }
        switch (i) {
            case TXLiveConstants.PUSH_EVT_OPEN_CAMERA_SUCC:
                L.e(TAG, "mLivePusher--->初始化完毕");
                if (mLivePushListener != null) {
                    mLivePushListener.onPreviewStart();
                }
                break;
            case TXLiveConstants.PUSH_EVT_PUSH_BEGIN:
                L.e(TAG, "mLivePusher--->推流成功");
                if (!mStartPush) {
                    mStartPush = true;
                    if (mLivePushListener != null) {
                        mLivePushListener.onPushStart();
                    }
                }
                break;
            case TXLiveConstants.PUSH_ERR_NET_DISCONNECT:
                ToastUtil.show(R.string.live_push_failed);
                if (mStartPush && mLivePushListener != null) {
                    mLivePushListener.onPushFailed();
                }
                break;
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    public void startPush(String pushUrl) {
        if (mLivePusher != null) {
            mLivePusher.startCameraPreview(mVideoView);
            mLivePusher.startPusher(pushUrl);
        }
    }

    public void release() {
        mLivePushListener = null;
        if (mLivePusher != null) {
            mLivePusher.stopPusher();
            mLivePusher.stopCameraPreview(true);
            mLivePusher = null;
        }
    }

    public void pause() {
        mPaused = true;
        if (mStartPush && mLivePusher != null) {
            mLivePusher.pausePusher();
        }
    }

    public void resume() {
        if (mPaused && mStartPush && mLivePusher != null) {
            mLivePusher.resumePusher();
        }
        mPaused = false;
    }

    public void setLivePushListener(LivePushListener livePushListener) {
        mLivePushListener = livePushListener;
    }
}
