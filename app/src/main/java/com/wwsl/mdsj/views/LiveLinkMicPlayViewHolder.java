package com.wwsl.mdsj.views;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

/**
 * Created by cxf on 2018/10/25.
 * 连麦播放小窗口
 */

public class LiveLinkMicPlayViewHolder extends AbsViewHolder implements ITXVodPlayListener {

    private static final String TAG = "LiveLinkMicPlayViewHolder";
    private TXCloudVideoView mVideoView;
    private TXVodPlayer mPlayer;
    private View mBtnClose;
    private View mLoading;
    private boolean mPaused;//是否切后台了
    private boolean mStartPlay;//是否开始了播放
    private boolean mEndPlay;//是否结束了播放

    public LiveLinkMicPlayViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_link_mic_play;
    }

    @Override
    public void init() {
        mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mLoading = findViewById(R.id.loading);
        mBtnClose = findViewById(R.id.btn_close_link_mic);

        mPlayer = new TXVodPlayer(mContext);
        TXVodPlayConfig mLivePlayConfig = new TXVodPlayConfig();
        mPlayer.setConfig(mLivePlayConfig);
        mPlayer.setPlayerView(mVideoView);
        mPlayer.setVodListener(this);
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onResume() {
                if (mPaused && mPlayer != null) {
                    mPlayer.resume();
                }
                mPaused = false;
            }

            @Override
            public void onPause() {
                if (mPlayer != null) {
                    mPlayer.pause();
                }
                mPaused = true;
            }

            @Override
            public void onDestroy() {
                release();
            }
        };
    }

    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int e, Bundle bundle) {
        if (mEndPlay) {
            return;
        }
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN://播放开始
                if (mLoading != null && mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION://获取视频宽高
                float width = bundle.getInt("EVT_PARAM1", 0);
                float height = bundle.getInt("EVT_PARAM2", 0);
                L.e(TAG, "流---width----->" + width);
                L.e(TAG, "流---height----->" + height);
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
//                int targetH = 0;
//                if (width / height > 0.5625f) {//横屏 9:16=0.5625
//                    targetH = (int) (mVideoView.getWidth() / width * height);
//                } else {
//                    targetH = ViewGroup.LayoutParams.MATCH_PARENT;
//                }
//                if (targetH != params.height) {
//                    params.height = targetH;
//                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
//                    mVideoView.requestLayout();
//                }
                break;
            case TXLiveConstants.PLAY_ERR_NET_DISCONNECT://播放失败
            case TXLiveConstants.PLAY_ERR_FILE_NOT_FOUND:
                ToastUtil.show(WordUtil.getString(R.string.live_play_error));
                break;
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }

    public void setOnCloseListener(View.OnClickListener onClickListener) {
        if (onClickListener != null) {
            mBtnClose.setVisibility(View.VISIBLE);
            mBtnClose.setOnClickListener(onClickListener);
        }
    }

    /**
     * 开始播放
     *
     * @param url 流地址
     */
    public void play(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mEndPlay = false;
        if (!mStartPlay && mPlayer != null) {
            mPlayer.startPlay(url);
        }
        mStartPlay = true;
        L.e(TAG, "play----url--->" + url);
    }

    public void stop() {
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
        }
        mStartPlay = false;
    }

    public void release() {
        mEndPlay = true;
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setVodListener(null);
        }
        mStartPlay = false;
        if (mBtnClose != null) {
            mBtnClose.setOnClickListener(null);
        }
        L.e(TAG, "release------->");
    }

    public void resume() {
        if (mPaused && mVideoView != null) {
            mPlayer.resume();
        }
        mPaused = false;
    }

    public void pause() {
        if (mVideoView != null) {
            mPlayer.pause();
        }
        mPaused = true;
    }
}
