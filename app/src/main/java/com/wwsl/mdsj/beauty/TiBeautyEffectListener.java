package com.wwsl.mdsj.beauty;

import cn.tillusory.sdk.bean.TiDistortionEnum;
import cn.tillusory.sdk.bean.TiFilterEnum;
import cn.tillusory.sdk.bean.TiRockEnum;

/**
 * Created by cxf on 2018/10/8.
 * 萌颜美颜回调
 */

public interface TiBeautyEffectListener extends EffectListener {

    void onFilterChanged(TiFilterEnum tiFilterEnum);

    void onMeiBaiChanged(int progress);

    void onMoPiChanged(int progress);

    void onBaoHeChanged(int progress);

    void onFengNenChanged(int progress);

    void onBigEyeChanged(int progress);

    void onFaceChanged(int progress);

    void onChinChanged(int progress);

    void onForeheadChanged(int progress);

    void onMouthChanged(int progress);

    void onTieZhiChanged(String tieZhiName);

    void onHaHaChanged(TiDistortionEnum tiDistortionEnum);

    void onRockChanged(TiRockEnum tiRockEnum);
}
