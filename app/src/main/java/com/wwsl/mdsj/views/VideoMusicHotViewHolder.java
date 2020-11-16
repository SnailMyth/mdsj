package com.wwsl.mdsj.views;

import android.content.Context;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.MusicAdapter;
import com.wwsl.mdsj.adapter.RefreshAdapter;
import com.wwsl.mdsj.bean.MusicBean;
import com.wwsl.mdsj.custom.RefreshView;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.VideoMusicActionListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/12/7.
 * 视频热门音乐
 */

public class VideoMusicHotViewHolder extends VideoMusicChildViewHolder {

    public VideoMusicHotViewHolder(Context context, ViewGroup parentView, VideoMusicActionListener actionListener) {
        super(context, parentView, actionListener);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_music_hot;
    }

    @Override
    public void init() {
        super.init();
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_music);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<MusicBean>() {
            @Override
            public RefreshAdapter<MusicBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MusicAdapter(mContext);
                    mAdapter.setActionListener(mActionListener);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getHotMusicList(p, callback);
            }

            @Override
            public List<MusicBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), MusicBean.class);
            }

            @Override
            public void onRefresh(List<MusicBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }
        });

    }

}
