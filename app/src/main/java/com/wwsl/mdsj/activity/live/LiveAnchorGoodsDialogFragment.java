package com.wwsl.mdsj.activity.live;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.ShopWindowAdapter;
import com.wwsl.mdsj.bean.LiveShopWindowBean;
import com.wwsl.mdsj.bean.net.NetGoodsBean;
import com.wwsl.mdsj.dialog.AbsDialogFragment;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/10/19.
 * h5 dialog
 */

public class LiveAnchorGoodsDialogFragment extends AbsDialogFragment implements SwipeRecyclerView.LoadMoreListener {

    private SegmentTabLayout tabLayout;
    private SwipeRecyclerView recycler;
    private ShopWindowAdapter mAdapter;
    private List<LiveShopWindowBean> allBeans;
    private List<LiveShopWindowBean> collectBeans;
    private String mLiveId;
    private String mLiveName;
    private int mPage = 1;
    private int curTabIndex = 0;//tab导航index

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_anchor_goods;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(AppContext.sInstance, 450);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mLiveId = bundle.getString(Constants.LIVE_ID);
        mLiveName = bundle.getString(Constants.LIVE_NAME);
        initView();
        loadData();
        initListener();
    }

    private void initView() {
        tabLayout = mRootView.findViewById(R.id.tabLayout);
        recycler = mRootView.findViewById(R.id.recycler);
        mRootView.findViewById(R.id.back).setOnClickListener(v -> {
            dismiss();
        });
        String[] titles = new String[]{"我的橱窗", "我的收藏 "};
        allBeans = new ArrayList<>();
        collectBeans = new ArrayList<>();
        mAdapter = new ShopWindowAdapter(new ArrayList<>());
        tabLayout.setTabData(titles);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.useDefaultLoadMore();
        recycler.setLoadMoreListener(this);
        recycler.setAdapter(mAdapter);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (curTabIndex == position) return;
                curTabIndex = position;
                if (position == 0) {
                    if (allBeans.size() == 0) {
                        loadData();
                    } else {
                        mAdapter.setNewInstance(allBeans);
                    }
                } else {
                    if (collectBeans.size() == 0) {
                        loadData();
                    } else {
                        mAdapter.setNewInstance(collectBeans);
                    }
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btnAdd) {
                LiveShopWindowBean bean;
                if (tabLayout.getCurrentTab() == 0) {
                    bean = allBeans.get(position);
                } else {
                    bean = collectBeans.get(position);
                }
                if (!bean.isAdd()) {
                    HttpUtil.liveAddGood(bean.getId(), mLiveId, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            ToastUtil.show(msg);
                            if (code == 0) {
                                mAdapter.getData().get(position).setAdd(true);
                                mAdapter.notifyItemChanged(position, ShopWindowAdapter.PAYLOAD_LIVE_ADD);
                                updateAddStatus(bean.getId(), tabLayout.getCurrentTab());
                            }
                        }
                    });
                } else {
                    HttpUtil.liveRemoveGood(bean.getId(), mLiveId, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            ToastUtil.show(msg);
                            if (code == 0) {
                                mAdapter.getData().get(position).setAdd(false);
                                mAdapter.notifyItemChanged(position, ShopWindowAdapter.PAYLOAD_LIVE_ADD);
                                updateAddStatus(bean.getId(), tabLayout.getCurrentTab());
                            }
                        }
                    });
                }
            }
        });
    }

    private void loadData() {
        mPage = 1;
        int currentTab = tabLayout.getCurrentTab();
        if (currentTab == 0) {
            HttpUtil.getUserShopGoods(mPage, mLiveId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, 0, true);
                }
            });
        } else {
            HttpUtil.getUserCollectedShopGoods(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, 1, true);
                }
            });
        }
    }

    private void loadBack(int code, String msg, String[] info, int currentTab, boolean isIsFresh) {
        if (code == 0) {
            List<NetGoodsBean> netGoodsBeans = JSON.parseArray(Arrays.toString(info), NetGoodsBean.class);
            recycler.loadMoreFinish(netGoodsBeans.size() == 0, true);
            List<LiveShopWindowBean> parseBeans = LiveShopWindowBean.parse(netGoodsBeans, ShopWindowAdapter.TYPE_LIVE);

            if (currentTab == 0) {
                if (isIsFresh) {
                    allBeans.clear();
                    allBeans.addAll(parseBeans);
                    mAdapter.setNewInstance(parseBeans);
                } else {
                    allBeans.addAll(parseBeans);
                    mAdapter.addData(parseBeans);
                }
            } else {
                if (isIsFresh) {
                    collectBeans.clear();
                    collectBeans.addAll(parseBeans);
                    mAdapter.setNewInstance(parseBeans);
                } else {
                    collectBeans.addAll(parseBeans);
                    mAdapter.addData(parseBeans);
                }
            }
        } else {
            ToastUtil.show(msg);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLoadMore() {
        mPage++;
        int currentTab = tabLayout.getCurrentTab();
        if (currentTab == 0) {
            HttpUtil.getUserShopGoods(mPage, mLiveId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, 0, false);
                }
            });
        } else {
            HttpUtil.getUserCollectedShopGoods(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, 1, false);
                }
            });
        }
    }


    private void updateAddStatus(String id, int type) {
        if (type == 0) {
            for (int i = 0; i < collectBeans.size(); i++) {
                LiveShopWindowBean bean = collectBeans.get(i);
                if (bean.getId().equals(id)) {
                    collectBeans.get(i).setAdd(!bean.isAdd());
                }
            }
        } else {
            for (int i = 0; i < allBeans.size(); i++) {
                LiveShopWindowBean bean = allBeans.get(i);
                if (bean.getId().equals(id)) {
                    allBeans.get(i).setAdd(!bean.isAdd());
                }
            }
        }
    }
}
