package com.wwsl.mdsj.utils.tiktok;

import com.dueeeke.videoplayer.player.ProgressManager;

/**
 * @author :
 * @date : 2020/6/18 15:37
 * @description : 抖音
 */
public class TikTokProgressManager extends ProgressManager {


    @Override
    public void saveProgress(String url, long progress) {

    }

    @Override
    public long getSavedProgress(String url) {
        return 0;
    }
}
