package com.wwsl.mdsj.fragment;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.video.VideoPlayListActivity;
import com.wwsl.mdsj.adapter.WorkAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VideoStorage;
import com.wwsl.mdsj.utils.cache.PreloadManager;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.hutool.core.util.StrUtil;


/**
 * @author :
 * @date : 2020/6/17 15:57
 * @description : 个人作品fragment
 */
public class WorkFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private WorkAdapter workAdapter;
    private String type;
    private String uid;
    private int mPage = 1;
    private List<VideoBean> data;
    private int videoIndex = HttpConst.VIDEO_TYPE_PRODUCT << 10;
    private boolean isFresh = true;

    public void setType(String type) {
        this.type = type;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static WorkFragment newInstance(String type, String uid) {
        WorkFragment fragment = new WorkFragment();
        fragment.setType(type);
        fragment.setUid(uid);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_work;
    }

    @Override
    protected void init() {

        if (HttpConst.USER_VIDEO_TYPE_PRODUCT.equals(type)) {
            videoIndex = HttpConst.VIDEO_TYPE_PRODUCT << 10;
        } else if (HttpConst.USER_VIDEO_TYPE_TREND.equals(type)) {
            videoIndex = HttpConst.VIDEO_TYPE_TREND << 10;
        } else if (HttpConst.USER_VIDEO_TYPE_LIKE.equals(type)) {
            videoIndex = HttpConst.VIDEO_TYPE_YPE_LIKE << 10;
        }

        data = new ArrayList<>();
        workAdapter = new WorkAdapter(data);

        mRefreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        mRefreshLayout.setOnRefreshListener(this::refreshData);
        mRefreshLayout.setOnLoadMoreListener(this::loadMoreData);
        mRefreshLayout.setEnableAutoLoadMore(true);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        workAdapter.setOnItemClickListener((adapter, view, position) -> {
            VideoPlayListActivity.forward(mContext, uid, type, position);
        });

        recyclerView.setAdapter(workAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRefreshLayout != null) {
            mRefreshLayout.autoRefresh();
        }
    }

    private void loadMoreData(RefreshLayout refreshLayout) {
        isFresh = false;
        mPage++;
        loadData();
    }

    private void refreshData(RefreshLayout refreshLayout) {
        isFresh = true;
        mPage = 1;
        loadData();
    }


    public void loadData() {
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
                        mRefreshLayout.finishRefresh();
                    } else {
                        data.addAll(videoBeans);
                        workAdapter.addData(videoBeans);
                        if (videoBeans.size() == 0) {
                            mRefreshLayout.finishLoadMoreWithNoMoreData();
                        } else {
                            mRefreshLayout.finishLoadMore();
                        }
                    }
                    VideoStorage.getInstance().putVideoList(uid, type, data);

                } else {
                    ToastUtil.show(msg);
                    if (isFresh) {
                        mRefreshLayout.finishRefresh();
                    } else {
                        mRefreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
            }

            @Override
            public void onError() {
                super.onError();
                if (isFresh) {
                    mRefreshLayout.finishRefresh();
                } else {
                    mRefreshLayout.finishLoadMore();
                }
            }
        });
    }

    @Override
    protected void initialData() {
        mRefreshLayout.autoRefresh();
    }

    public void setNewData(String newUid) {
        this.uid = newUid;
        isFresh = true;
        isFirst = true;
    }

    public void clearData() {
        if (data != null) {
            data.clear();
        }

        if (workAdapter != null) {
            workAdapter.setNewInstance(new ArrayList<>());
        }
        VideoStorage.getInstance().remove(uid, Integer.parseInt(type));
    }
}
