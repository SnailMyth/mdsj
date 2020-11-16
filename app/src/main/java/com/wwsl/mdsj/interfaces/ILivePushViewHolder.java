package com.wwsl.mdsj.interfaces;

import com.wwsl.mdsj.beauty.EffectListener;

/**
 * Created by cxf on 2018/12/22.
 */

public interface ILivePushViewHolder extends ILiveLinkMicViewHolder {

    /**
     * 设置推流监听
     */
    void setLivePushListener(LivePushListener livePushListener);
    /**
     * 开始推流
     */
    void startPush(String pushUrl);
    /**
     * 切换闪光灯
     */
    void toggleFlash();

    /**
     * activity pause
     */
    void pause();

    /**
     * activity resume
     */
    void resume();

    /**
     * 结束推流，是否资源
     */
    void release();

    /**
     * 切换摄像头
     */
    void toggleCamera();
    /**
     * 是否打开过相机，预览的时候，开启相机会夺取摄像头
     */
    void setOpenCamera(boolean openCamera);

    /**
     * 开始播放背景音乐
     */
    void startBgm(String path);

    /**
     * 暂停播放背景音乐
     */
    void pauseBgm();

    /**
     * 恢复播放背景音乐
     */
    void resumeBgm();

    /**
     * 停止播放背景音乐
     */
    void stopBgm();

    /**
     * 获取美颜效果监听
     */
    EffectListener getEffectListener();
}
