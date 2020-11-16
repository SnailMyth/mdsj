package com.wwsl.mdsj.activity.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.adapter.ShopWindowAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.LiveShopWindowBean;
import com.wwsl.mdsj.bean.net.NetGoodsBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;

import static com.wwsl.mdsj.adapter.ShopWindowAdapter.TYPE_LIVE;
import static com.wwsl.mdsj.adapter.ShopWindowAdapter.TYPE_VIDEO;

public class ShopWindowActivity extends BaseActivity implements SwipeRecyclerView.LoadMoreListener {

    private TextView title;
    private TextView txDone;
    private TextView tvAddGoods;
    private SegmentTabLayout tabLayout;
    private SwipeRecyclerView recycler;
    private ShopWindowAdapter mAdapter;
    private List<LiveShopWindowBean> allBeans;
    private List<LiveShopWindowBean> collectBeans;
    private int type = 0;
    private int mPage = 1;
    private int curTabIndex = 0;//tab导航index

    private boolean isNeedFreshCollect = false;
    private Map<String, LiveShopWindowBean> liveSelectedBeans;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_shop_window;
    }

    @Override
    protected void init() {
        type = getIntent().getIntExtra("type", 0);
        initView();
        initData();
        initListener();
        tabLayout.setCurrentTab(0);
    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.ivCollect:
                    //收藏/取消收藏商品
                    HttpUtil.collectGood(mAdapter.getData().get(position).getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                if (tabLayout.getCurrentTab() == 0) {
                                    LiveShopWindowBean bean = allBeans.get(position);
                                    bean.setCollect(!bean.isCollect());
                                    mAdapter.notifyItemChanged(position, ShopWindowAdapter.PAYLOAD_ITEM_COLLECT);
                                } else {
                                    String id = collectBeans.get(position).getId();
                                    collectBeans.remove(position);
                                    mAdapter.removeAt(position);
                                    for (int i = 0; i < allBeans.size(); i++) {
                                        if (allBeans.get(i).getId().equals(id)) {
                                            allBeans.get(i).setCollect(false);
                                            break;
                                        }
                                    }
                                }
                                isNeedFreshCollect = true;
                            }
                            ToastUtil.show(msg);
                        }
                    });
                    break;
                case R.id.btnAdd:
                    if (type == TYPE_LIVE) {
                        //开播前添加商品
                        LiveShopWindowBean bean;
                        if (tabLayout.getCurrentTab() == 0) {
                            bean = allBeans.get(position);
                            if (bean.isAdd()) {
                                liveSelectedBeans.remove(bean.getId());
                            } else {
                                liveSelectedBeans.put(bean.getId(), bean);
                            }
                            allBeans.get(position).setAdd(!bean.isAdd());
                            updateAddStatus(bean.getId(), 0);
                        } else {
                            bean = collectBeans.get(position);
                            if (bean.isAdd()) {
                                liveSelectedBeans.remove(bean.getId());

                            } else {
                                liveSelectedBeans.put(bean.getId(), bean);
                            }
                            collectBeans.get(position).setAdd(!bean.isAdd());
                            updateAddStatus(bean.getId(), 1);
                        }

                        mAdapter.notifyItemChanged(position, ShopWindowAdapter.PAYLOAD_LIVE_ADD);


                    } else if (type == TYPE_VIDEO) {
                        //发布视频添加商品
                        Intent intent = new Intent();
                        if (tabLayout.getCurrentTab() == 0) {
                            intent.putExtra("goods", allBeans.get(position));
                        } else {
                            intent.putExtra("goods", collectBeans.get(position));
                        }
                        setResult(1, intent);
                        finish();
                    }
                    break;
            }
        });


        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            String url = mAdapter.getData().get(position).getWebUrl();
            if (!StrUtil.isEmpty(url)) {
                WebViewActivity.forward3(ShopWindowActivity.this, url, 1);
            }
        });
    }

    private void initData() {
        String[] titles = new String[]{"我的橱窗", "我的收藏 "};
        allBeans = new ArrayList<>();
        collectBeans = new ArrayList<>();
        liveSelectedBeans = new HashMap<>();
        mAdapter = new ShopWindowAdapter(new ArrayList<>());
        tabLayout.setTabData(titles);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.useDefaultLoadMore();
        recycler.setLoadMoreListener(this);
        recycler.setAdapter(mAdapter);
        recycler.useDefaultLoadMore();
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
                        if (isNeedFreshCollect) {
                            loadData();
                        } else {
                            mAdapter.setNewInstance(collectBeans);
                        }
                    }
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        loadData();
    }

    private void loadData() {
        mPage = 1;
        int currentTab = tabLayout.getCurrentTab();
        if (currentTab == 0) {
            HttpUtil.getUserShopGoods(mPage, new HttpCallback() {
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
            List<LiveShopWindowBean> parseBeans = LiveShopWindowBean.parse(netGoodsBeans, type);
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
                isNeedFreshCollect = false;
            }
        } else {
            ToastUtil.show(msg);
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


    public void backClick(View view) {
        finish();
    }


    public void addGoodsToShop(View view) {

    }

    private void initView() {
        title = findViewById(R.id.title);
        txDone = findViewById(R.id.txDone);
        tvAddGoods = findViewById(R.id.tvAddGoods);
        tabLayout = findViewById(R.id.tabLayout);
        recycler = findViewById(R.id.recycler);

        if (type == TYPE_VIDEO) {
            title.setText("添加商品");
            tvAddGoods.setVisibility(View.GONE);
            txDone.setVisibility(View.GONE);
        } else if (type == TYPE_LIVE) {
            title.setText("添加商品");
            tvAddGoods.setVisibility(View.GONE);
            txDone.setVisibility(View.VISIBLE);
        } else {
            title.setText("橱窗");
//            tvAddGoods.setVisibility(View.VISIBLE);
            txDone.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLoadMore() {
        mPage++;
        int currentTab = tabLayout.getCurrentTab();
        if (currentTab == 0) {
            HttpUtil.getUserShopGoods(mPage, new HttpCallback() {
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

    public static void forward(Context context, int type) {
        Intent intent = new Intent(context, ShopWindowActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void forward(Activity context, int type, int requestCode) {
        Intent intent = new Intent(context, ShopWindowActivity.class);
        intent.putExtra("type", type);
        context.startActivityForResult(intent, requestCode);
    }


    public void liveDone(View view) {
        Intent i = new Intent();
        ArrayList<LiveShopWindowBean> res = new ArrayList<>();
        for (Map.Entry<String, LiveShopWindowBean> entry : liveSelectedBeans.entrySet()) {
            res.add(entry.getValue());
        }
        i.putParcelableArrayListExtra("goods", res);
        setResult(1, i);
        finish();
    }

    public static final int INTENT_SHOW = 1000;
    public static final int INTENT_VIDEO = 1001;
    public static final int INTENT_LIVE = 1002;

}
