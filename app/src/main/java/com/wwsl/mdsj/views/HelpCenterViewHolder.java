package com.wwsl.mdsj.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.adapter.ActionAdapter;
import com.wwsl.mdsj.adapter.RefreshAdapter;
import com.wwsl.mdsj.bean.TicketBean;
import com.wwsl.mdsj.custom.ItemDecoration;
import com.wwsl.mdsj.custom.RefreshView;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.interfaces.OnItemClickListener;

import java.util.Arrays;
import java.util.List;

public class HelpCenterViewHolder extends AbsMainViewHolder implements OnItemClickListener<TicketBean> {
    protected RefreshView mRefreshView;
    protected ActionAdapter mAdapter;
    private FrameLayout layoutTrendTitle;

    public HelpCenterViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public void hideTop() {
        if (layoutTrendTitle != null) {
            layoutTrendTitle.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_help_center;
    }

    @Override
    public void init() {
        layoutTrendTitle = (FrameLayout) findViewById(R.id.layoutTrendTitle);
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_list);
//        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 0, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<TicketBean>() {
            @Override
            public RefreshAdapter<TicketBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new ActionAdapter(mContext);
                    mAdapter.setOnItemClickListener(HelpCenterViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getActCenter(0, p, callback);
            }

            @Override
            public List<TicketBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), TicketBean.class);
            }

            @Override
            public void onRefresh(List<TicketBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {
                if (dataCount < HttpConst.ITEM_COUNT) {
                    mRefreshView.setLoadMoreEnable(false);
                } else {
                    mRefreshView.setLoadMoreEnable(true);
                }
            }
        });
        mLifeCycleListener = new LifeCycleAdapter() {

            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConst.TICKET);
            }
        };
    }

    @Override
    public void onItemClick(TicketBean bean, int position) {
        if (!TextUtils.isEmpty(bean.getUrl())) {
            WebViewActivity.forward(mContext, bean.getUrl());
        }
    }

    @Override
    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }
}
