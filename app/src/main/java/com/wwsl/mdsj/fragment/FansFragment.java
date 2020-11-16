package com.wwsl.mdsj.fragment;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.model.Response;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.adapter.FansAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.FansShowBean;
import com.wwsl.mdsj.bean.net.NetFansBean;
import com.wwsl.mdsj.bean.net.NetFriendBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.http.JsonBean;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FansFragment extends BaseFragment {
    private SwipeRecyclerView recycler;
    private FansAdapter mAdapter;
    private SmartRefreshLayout mRefreshLayout;
    private int type;
    private String uid;
    private int mPage = 1;
    private List<FansShowBean> data;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_fans;
    }

    @Override
    protected void init() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mRefreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        uid = getArguments().getString("uid");
        type = getArguments().getInt("type");
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        data = new ArrayList<>();
        mAdapter = new FansAdapter(new ArrayList<>());
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            String uid = null;
            FansShowBean fansShowBean = data.get(position);
            if (type == Constants.TYPE_FANS) {
                uid = fansShowBean.getUid();
            } else {
                uid = fansShowBean.getToUid();
            }
            switch (view.getId()) {
                case R.id.avatar:
                    UserHomePageActivity.forward(getContext(), uid);
                    break;
                case R.id.tvFocus:

                    //只有粉丝可以回关
                    HttpUtil.setAttention(Constants.FOLLOW_FROM_HOME, uid, new CommonCallback<Integer>() {
                        @Override
                        public void callback(Integer isAttention) {
                            onAttention(position, isAttention);
                        }
                    });

                    break;

            }

        });
        recycler.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(this::refreshData);
        mRefreshLayout.setOnLoadMoreListener(this::loadMoreData);
        mRefreshLayout.setEnableAutoLoadMore(true);

        String text = null;

        switch (type) {
            case Constants.TYPE_FANS:
                text = "还没有粉丝关注你~";
                break;
            case Constants.TYPE_FOLLOW:
                text = "您还没有关注别人哦~";
                break;
            case Constants.TYPE_FRIEND:
                text = "您还没有好友哦!";
                break;
        }

        mAdapter.setEmptyView(CommonUtil.getEmptyView(text, getContext(), mRefreshLayout));

    }

    private void loadMoreData(RefreshLayout refreshLayout) {
        mPage++;
        switch (type) {
            case Constants.TYPE_FANS:
                loadFans(false);
                break;
            case Constants.TYPE_FOLLOW:
                loadFollow(false);
                break;
            case Constants.TYPE_FRIEND:
                loadFriends(false);
                break;
        }
    }

    private void refreshData(RefreshLayout refreshLayout) {
        mPage = 1;
        switch (type) {
            case Constants.TYPE_FANS:
                loadFans(true);
                break;
            case Constants.TYPE_FOLLOW:
                loadFollow(true);
                break;
            case Constants.TYPE_FRIEND:
                loadFriends(true);
                break;
        }
    }

    private void onAttention(int position, int isAttention) {
        initialData();
    }


    @Override
    protected void initialData() {
        mRefreshLayout.autoRefresh();
    }

    private void loadFriends(boolean isInit) {
        HttpUtil.getFriendsList(uid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<NetFriendBean> netFansBeans = JSON.parseArray(Arrays.toString(info), NetFriendBean.class);
                    List<FansShowBean> res = FansShowBean.parseFriendBean(netFansBeans, Constants.TYPE_FRIEND);
                    loadDataBack(res, isInit);
                } else {
                    ToastUtil.show(msg);
                    loadDataError(isInit);
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                loadDataError(isInit);
            }
        });
    }


    private void loadFollow(boolean isInit) {
        HttpUtil.getFollowList(uid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<FansShowBean> netFansBeans = JSON.parseArray(Arrays.toString(info), FansShowBean.class);
                    for (int i = 0; i < netFansBeans.size(); i++) {
                        netFansBeans.get(i).setType(Constants.TYPE_FOLLOW);
                    }
                    loadDataBack(netFansBeans, isInit);
                } else {
                    ToastUtil.show(msg);
                    loadDataError(isInit);
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                loadDataError(isInit);
            }
        });
    }


    private void loadFans(boolean isInit) {
        HttpUtil.getFansList(uid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<NetFansBean> netFansBeans = JSON.parseArray(Arrays.toString(info), NetFansBean.class);
                    List<FansShowBean> res = FansShowBean.parseBean(netFansBeans, Constants.TYPE_FANS);
                    loadDataBack(res, isInit);
                } else {
                    ToastUtil.show(msg);
                    loadDataError(isInit);
                }
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                loadDataError(isInit);
            }
        });
    }

    public static FansFragment newInstance(String uid, int type) {
        Bundle args = new Bundle();
        args.putString("uid", uid);
        args.putInt("type", type);
        FansFragment fragment = new FansFragment();
        fragment.setArguments(args);
        return fragment;
    }



    private void loadDataError(boolean isInit) {
        if (isInit) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishLoadMore();
        }
    }

    private void loadDataBack(List<FansShowBean> res, boolean isInit) {
        if (isInit) {
            data.clear();
            data.addAll(res);
            mAdapter.setNewInstance(res);
            mRefreshLayout.finishRefresh();
        } else {
            data.addAll(res);
            mAdapter.addData(res);
            mRefreshLayout.finishLoadMore();
        }
    }
}
