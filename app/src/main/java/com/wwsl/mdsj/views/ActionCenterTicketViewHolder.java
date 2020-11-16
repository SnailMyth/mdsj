package com.wwsl.mdsj.views;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.me.BuyTicketActivity;
import com.wwsl.mdsj.adapter.RefreshAdapter;
import com.wwsl.mdsj.adapter.TicketAdapter;
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

public class ActionCenterTicketViewHolder extends AbsMainViewHolder implements OnItemClickListener<TicketBean> {
    protected int mType;
    protected RefreshView mRefreshView;
    protected TicketAdapter mAdapter;

    public ActionCenterTicketViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
        mType = Constants.TICKET_CENTER_ING;
    }

    public void refreshData(int type) {
        if (type == mType) {
            return;
        }
        mType = type;
        mFirstLoadData = true;
        loadData();
    }

    public void setCanRefresh(boolean canRefresh) {
        if (mRefreshView != null) {
            mRefreshView.setRefreshEnable(canRefresh);
        }
    }

    @Override
    public void onItemClick(TicketBean bean, int position) {
        BuyTicketActivity.forward(mContext, bean, mType);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.view_ticket_list_page;
    }

    @Override
    public void init() {
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        mRefreshView.setNoDataLayoutId(R.layout.view_no_data_list);
//        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<TicketBean>() {
            @Override
            public RefreshAdapter<TicketBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new TicketAdapter(mContext);
                    mAdapter.setHideUser(true);
                    mAdapter.setOnItemClickListener(ActionCenterTicketViewHolder.this);
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                if (mType == Constants.TICKET_CENTER_ING) {
                    HttpUtil.getActCenter(0, p, callback);
                } else {
                    HttpUtil.getActCenter(1, p, callback);
                }
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
    public void loadData() {
        if (!isFirstLoadData()) {
            return;
        }
        if (mRefreshView != null) {
            mRefreshView.initData();
        }
    }
}
