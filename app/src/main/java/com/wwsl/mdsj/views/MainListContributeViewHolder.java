package com.wwsl.mdsj.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.MainListAdapter;
import com.wwsl.mdsj.adapter.RefreshAdapter;
import com.wwsl.mdsj.bean.ListBean;
import com.wwsl.mdsj.custom.RefreshView;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.utils.L;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/9/27.
 * 首页 排行 贡献榜
 */

public class MainListContributeViewHolder extends AbsMainListViewHolder {
    //是否为礼物贡献榜
    private boolean isGiftContribute;
    private String mGifUid;

    public MainListContributeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public void setGiftContribute(boolean isGiftContribute, String gifUid) {
        this.isGiftContribute = isGiftContribute;
        this.mGifUid = gifUid;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_main_list_page;
    }

    @Override
    public void init() {
        // super.init();
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_list);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<ListBean>() {
            @Override
            public RefreshAdapter<ListBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new MainListAdapter(mContext, MainListAdapter.TYPE_CONTRIBUTE);
                    mAdapter.setGiftContribute(isGiftContribute);
                    mAdapter.setOnItemClickListener(MainListContributeViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (isGiftContribute) {
                    HttpUtil.consumeList(mGifUid, mType, p, callback);
                } else {
                    HttpUtil.consumeList(mType, p, callback);
                }
            }

            @Override
            public List<ListBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), ListBean.class);
            }

            @Override
            public void onRefresh(List<ListBean> list) {

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
                L.e("main----MainListContributeViewHolder-------LifeCycle---->onDestroy");
                HttpUtil.cancel(HttpConst.CONSUME_LIST);
                HttpUtil.cancel(HttpConst.SET_ATTENTION);
            }
        };
    }

    @Override
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }

}
