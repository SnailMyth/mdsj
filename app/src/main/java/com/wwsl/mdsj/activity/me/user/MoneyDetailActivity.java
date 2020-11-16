package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.message.MessageSecondActivity;
import com.wwsl.mdsj.activity.video.VideoPlayActivity;
import com.wwsl.mdsj.adapter.GiftDetailAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.GiftDetailBean;
import com.wwsl.mdsj.bean.GiftDetailShowBean;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class MoneyDetailActivity extends BaseActivity {

    private SwipeRecyclerView billRecycler;
    private GiftDetailAdapter mAdapter;
    private SmartRefreshLayout refreshLayout;
    private TextView title;

    private int mPage = 1;

    private String type;
    private String action;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_money_detail;
    }

    @Override
    protected void init() {
        type = getIntent().getStringExtra("type");
        action = getIntent().getStringExtra("action");
        initView();
        if (HttpConst.DETAIL_ACTION_ALL.equals(action)) {
            title.setText(WordUtil.getString(R.string.bill));
        } else if (HttpConst.DETAIL_ACTION_GIFT.equals(action)) {
            if (HttpConst.DETAIL_TYPE_GIFT_INCOME.equals(type)) {
                title.setText(WordUtil.getString(R.string.gift_detail));
            } else {
                title.setText(WordUtil.getString(R.string.gift_send_detail));
            }
        }
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        billRecycler = findViewById(R.id.billRecycler);
        title = findViewById(R.id.title);
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(this::refreshData);
        refreshLayout.setOnLoadMoreListener(this::loadMoreData);

        mAdapter = new GiftDetailAdapter(new ArrayList<>());
        billRecycler.setLayoutManager(new LinearLayoutManager(this));
        billRecycler.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.videoCover) {
                    GiftDetailShowBean bean = mAdapter.getData().get(position);
                    if (bean.getAction().equals(HttpConst.DETAIL_ACTION_VIDEO) && !StrUtil.isEmpty(bean.getShowId())) {
                        //视频打赏二次检验类型
                        goVideo(bean.getShowId());
                    }
                }
            }
        });
        mAdapter.setEmptyView(CommonUtil.getEmptyView(null, this, refreshLayout));
    }

    private void loadMoreData(RefreshLayout refreshLayout) {
        mPage++;
        getData(false);
    }

    private void refreshData(RefreshLayout refreshLayout) {
        mPage = 1;
        getData(true);
    }

    public static void forward(Context context, String type, String action) {
        Intent intent = new Intent(context, MoneyDetailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("action", action);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null && mAdapter.getData().size() <= 0) {
            refreshLayout.autoRefresh();
        }
    }

    private void getData(boolean isFresh) {
        HttpUtil.getAllGiftDetail(type, action, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                showData(code, msg, info, isFresh);
            }

            @Override
            public void onError() {
                if (isFresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
            }
        });
    }

    private void showData(int code, String msg, String[] info, boolean isFresh) {
        if (code != 0) {
            ToastUtil.show(msg);
            mPage--;
            return;
        }
        List<GiftDetailShowBean> list = new ArrayList<>();
        if (info != null) {
            list = parseData(JSON.parseArray(Arrays.toString(info), GiftDetailBean.class));


            if (list.size() > 0) {

                if (isFresh) {
                    mAdapter.setNewInstance(list);
                } else {
                    mAdapter.insertList(list);
                }

            } else {
                mPage--;
            }
        } else {
            mPage--;
        }

        if (isFresh) {
            refreshLayout.finishRefresh();
        } else {
            refreshLayout.finishLoadMore();
        }

    }

    private List<GiftDetailShowBean> parseData(List<GiftDetailBean> parseArray) {
        List<GiftDetailShowBean> list = new ArrayList<>();
        if (null != parseArray) {
            for (GiftDetailBean bean : parseArray) {
                list.add(GiftDetailShowBean.builder()
                        .id(bean.getId())
                        .action(bean.getAction())
                        .addtime(bean.getAddtime())
                        .belongType(HttpConst.DETAIL_ACTION_ALL)
                        .giftid(bean.getGiftid())
                        .giftcount(bean.getGiftcount())
                        .gifticon(bean.getGifticon())
                        .showId(bean.getShowid())
                        .totalcoin(bean.getTotalcoin())
                        .touid(bean.getTouid())
                        .touserinfo(bean.getTouserinfo())
                        .userinfo(bean.getUserinfo())
                        .type(bean.getType())
                        .touid(bean.getTouid())
                        .uid(bean.getUid())
                        .giftinfo(bean.getGiftinfo())
                        .votes(bean.getVotes())
                        .cover(bean.getThumb())
                        .build());
            }
        }
        return list;
    }


    private void goVideo(String videoId) {
        showLoadCancelable(false, "加载视频...");
        HttpUtil.getVideo(videoId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && null != info[0]) {
                    dismissLoad();
                    VideoBean bean = JSON.parseObject(info[0], VideoBean.class);
                    if (bean != null) {
                        VideoPlayActivity.forward(MoneyDetailActivity.this, bean);
                    } else {
                        ToastUtil.show("获取视频失败");
                    }
                } else {
                    ToastUtil.show(msg);
                    dismissLoad();
                }
            }

            @Override
            public void onError() {
                dismissLoad();
            }
        });
    }
}
