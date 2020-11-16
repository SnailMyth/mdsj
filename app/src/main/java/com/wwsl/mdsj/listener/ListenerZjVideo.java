package com.wwsl.mdsj.listener;

import com.frame.fire.util.LogUtils;
import com.zj.zjsdk.ad.ZjAdError;
import com.zj.zjsdk.ad.ZjRewardVideoAdListener;

/**
 * 众简广告播放事件回调
 */
public class ListenerZjVideo implements ZjRewardVideoAdListener {

    public ListenerZjVideo() {

    }

    @Override
    public void onZjAdLoaded() {
        LogUtils.e("onZjAdLoaded()===");
    }

    @Override
    public void onZjAdVideoCached() {
        LogUtils.e("onZjAdVideoCached()===");
    }

    @Override
    public void onZjAdShow() {
        LogUtils.e("onZjAdShow()===");
    }

    @Override
    public void onZjAdShowError(ZjAdError zjAdError) {
        LogUtils.e("onZjAdShowError()===" + zjAdError.getErrorCode() + "--" + zjAdError.getErrorMsg());
    }

    @Override
    public void onZjAdClick() {
        LogUtils.e("onZjAdClick()===");
    }

    @Override
    public void onZjAdVideoComplete() {
        LogUtils.e("onZjAdVideoComplete()===");
    }

    @Override
    public void onZjAdExpose() {
        LogUtils.e("onZjAdExpose()===");
    }

    @Override
    public void onZjAdReward() {
        LogUtils.e("onZjAdReward()===");
    }

    @Override
    public void onZjAdClose() {
        LogUtils.e("onZjAdClose()===");
    }

    @Override
    public void onZjAdError(ZjAdError zjAdError) {
        LogUtils.e("onZjAdError()===" + zjAdError.getErrorCode() + "--" + zjAdError.getErrorMsg());
    }
}
