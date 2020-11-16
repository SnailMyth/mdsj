package com.wwsl.mdsj.views;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.frame.fire.util.LogUtils;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wwsl.beauty.bean.FilterBean;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.BuildConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.beauty.DefaultEffectListener;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.ToastUtil;

import cn.tillusory.sdk.bean.TiRotation;

public class LiveTxPushViewHolder extends AbsLivePushViewHolder implements ITXLivePushListener {

    private TXCloudVideoView mVideoView;
    private TXLivePusher mLivePusher;
    private int mMeiBaiVal;//基础美颜 美白
    private int mMoPiVal;//基础美颜 磨皮
    private int mHongRunVal;//基础美颜 红润

    public LiveTxPushViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_push_tx;
    }

    @Override
    public void init() {
        super.init();
        mVideoView = (TXCloudVideoView) findViewById(R.id.camera_preview);
        mVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPreView = mVideoView;
        mLivePusher = new TXLivePusher(mContext);
        TXLivePushConfig mLivePushConfig = new TXLivePushConfig();
        mLivePushConfig.enableAEC(true);
        mLivePusher.setConfig(mLivePushConfig);
        mLivePusher.setPushListener(this);
        mLivePusher.startCameraPreview(mVideoView);
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        if (configBean != null && configBean.getImageQuality() != null) {
            switch (configBean.getImageQuality()) {
                case "1":
                    mLivePusher.setVideoQuality(1, false, false);
                    break;
                case "2":
                    mLivePusher.setVideoQuality(2, false, false);
                    break;
                case "3":
                    mLivePusher.setVideoQuality(3, false, false);
                    break;
                default:
                    mLivePusher.setVideoQuality(1, false, false);
                    break;
            }
        } else {
            mLivePusher.setVideoQuality(1, false, false);
        }
        mCameraFront = true;
        mFlashOpen = false;
        if (BuildConfig.isOpenTiui) {
            mLivePusher.setVideoProcessListener(new TXLivePusher.VideoCustomProcessListener() {
                @Override
                public int onTextureCustomProcess(int i, int i1, int i2) {
                    return mTiSDKManager.renderTexture2D(i, i1, i2, TiRotation.CLOCKWISE_ROTATION_0, true);
                }

                @Override
                public void onDetectFacePoints(float[] floats) {

                }

                @Override
                public void onTextureDestoryed() {
                    mTiSDKManager.destroy();
                }
            });
        }

    }

    @Override
    protected void onCameraRestart() {
        if (mLivePusher != null) {
            mLivePusher.startCameraPreview(mVideoView);
        }
    }

    @Override
    protected DefaultEffectListener getDefaultEffectListener() {
        return new DefaultEffectListener() {
            @Override
            public void onFilterChanged(FilterBean filterBean) {
                if (filterBean.getFilterSrc() == 0) {
                    mLivePusher.setFilter(null);
                    mLivePusher.setSpecialRatio(0.5f);
                } else {
                    mLivePusher.setFilter(BitmapFactory.decodeResource(mContext.getResources(), filterBean.getFilterSrc()));
                    mLivePusher.setSpecialRatio(0.5f);
                }
            }

            @Override
            public void onMeiBaiChanged(int progress) {
                if (mLivePusher != null) {
                    mMeiBaiVal = progress / 10;
                    mLivePusher.setBeautyFilter(0, mMoPiVal, mMeiBaiVal, mHongRunVal);
                }
            }

            @Override
            public void onMoPiChanged(int progress) {
                if (mLivePusher != null) {
                    mMoPiVal = progress / 10;
                    mLivePusher.setBeautyFilter(0, mMoPiVal, mMeiBaiVal, mHongRunVal);
                }
            }

            @Override
            public void onHongRunChanged(int progress) {
                if (mLivePusher != null) {
                    mHongRunVal = progress / 10;
                    mLivePusher.setBeautyFilter(0, mMoPiVal, mMeiBaiVal, mHongRunVal);
                }
            }
        };
    }

    @Override
    public void onPushEvent(int i, Bundle bundle) {
        LogUtils.e(TAG, "onPushEvent: " + i);
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

    @Override
    public void onPause() {
        if (mLivePusher != null) {
            mLivePusher.pausePusher();
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void onResume() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
            mLivePusher.resumePusher();
        }
    }

    @Override
    public void onRelease() {
        if (mLivePusher != null) {
            mLivePusher.stopPusher();
            mLivePusher.stopBGM();
            mLivePusher.stopCameraPreview(true);
            mLivePusher.setPushListener(null);
            mLivePusher = null;
        }
    }

    @Override
    public void startPush(String pushUrl) {
        if (mLivePusher != null) {
            mLivePusher.startPusher(pushUrl);
        }
        startCountDown();
    }

    @Override
    public void toggleFlash() {
        if (mCameraFront) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mLivePusher != null) {
            mFlashOpen = !mFlashOpen;
            mLivePusher.turnOnFlashLight(mFlashOpen);
        }
    }

    @Override
    public void toggleCamera() {
        if (mLivePusher != null) {
            if (mFlashOpen) {
                mFlashOpen = !mFlashOpen;
                mLivePusher.turnOnFlashLight(mFlashOpen);
            }
            mCameraFront = !mCameraFront;
            mLivePusher.switchCamera();
        }
    }

    @Override
    public void startBgm(String path) {
        if (mLivePusher != null) {
            mLivePusher.playBGM(path);
        }
    }

    @Override
    public void pauseBgm() {
        if (mLivePusher != null) {
            mLivePusher.pauseBGM();
        }
    }

    @Override
    public void resumeBgm() {
        if (mLivePusher != null) {
            mLivePusher.resumeBGM();
        }
    }

    @Override
    public void stopBgm() {
        if (mLivePusher != null) {
            mLivePusher.stopBGM();
        }
    }

    @Override
    public void changeToLeft() {
        if (mVideoView != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mVideoView.getWidth() / 2, AppConfig.getVidowHeight());
            params.setMargins(0, DpUtil.dp2px(130), 0, 0);
            mVideoView.setLayoutParams(params);
        }
    }

    @Override
    public void changeToBig() {
        if (mVideoView != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mVideoView.setLayoutParams(params);
        }
    }
}
