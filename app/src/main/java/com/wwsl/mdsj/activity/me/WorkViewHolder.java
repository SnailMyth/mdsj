package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.video.VideoPlayListActivity;
import com.wwsl.mdsj.adapter.WorkAdapter;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VideoStorage;
import com.wwsl.mdsj.utils.cache.PreloadManager;
import com.wwsl.mdsj.views.AbsViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class WorkViewHolder extends AbsViewHolder implements SwipeRecyclerView.LoadMoreListener {
    SwipeRecyclerView recyclerView;
    private WorkAdapter workAdapter;
    private String type;
    private String uid;
    private int mPage = 1;
    private List<VideoBean> data;
    private int videoIndex = HttpConst.VIDEO_TYPE_PRODUCT << 10;

    public WorkViewHolder(Context context, ViewGroup parentView, String type) {
        super(context, parentView);
        this.type = type;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_user_work;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public void init() {
        recyclerView = (SwipeRecyclerView) findViewById(R.id.recyclerview);
        recyclerView.useDefaultLoadMore();
        recyclerView.setLoadMoreListener(this);
        if (HttpConst.USER_VIDEO_TYPE_PRODUCT.equals(type)) {
            videoIndex = HttpConst.VIDEO_TYPE_PRODUCT << 10;
        } else if (HttpConst.USER_VIDEO_TYPE_TREND.equals(type)) {
            videoIndex = HttpConst.VIDEO_TYPE_TREND << 10;
        } else if (HttpConst.USER_VIDEO_TYPE_LIKE.equals(type)) {
            videoIndex = HttpConst.VIDEO_TYPE_YPE_LIKE << 10;
        }



        data = new ArrayList<>();
        workAdapter = new WorkAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        workAdapter.setOnItemClickListener((adapter, view, position) -> {
            VideoPlayListActivity.forward(mContext, uid, type, position);
        });
        recyclerView.setAdapter(workAdapter);
        recyclerView.useDefaultLoadMore();
    }

    private void loadData(boolean isFresh) {
        if (StrUtil.isEmpty(uid)) return;
        HttpUtil.getHomeVideo(type, uid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && null != info) {
                    List<VideoBean> videoBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);

                    for (int i = 0; i < videoBeans.size(); i++) {
                        PreloadManager.getInstance(mContext).addPreloadTask(videoBeans.get(i).getVideoUrl(), videoIndex + i);
                    }
                    if (isFresh) {
                        data.clear();
                        data.addAll(videoBeans);
                        workAdapter.setNewInstance(videoBeans);
                    } else {
                        data.addAll(videoBeans);
                        workAdapter.addData(videoBeans);
                    }
                    boolean hasMore = videoBeans.size() == HttpConst.ITEM_COUNT;
                    if (hasMore) {
                        mPage++;
                    }
                    VideoStorage.getInstance().putVideoList(uid, type, data);
                    recyclerView.loadMoreFinish(false, hasMore);
                } else {
                    ToastUtil.show(msg);
                    recyclerView.loadMoreFinish(false, true);
                }
            }

            @Override
            public void onError() {
                super.onError();
                recyclerView.loadMoreFinish(false, true);
            }
        });
    }

    void setNewData(String uid) {
        this.uid = uid;
        data.clear();
        loadData(true);
    }

    void updateDate() {
        if (data.size() < HttpConst.ITEM_COUNT) {
            loadData(true);
        }
    }

    @Override
    public void onLoadMore() {
        loadData(false);
    }
}
