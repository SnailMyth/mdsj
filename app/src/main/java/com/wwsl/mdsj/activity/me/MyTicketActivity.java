package com.wwsl.mdsj.activity.me;

import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.adapter.RefreshAdapter;
import com.wwsl.mdsj.adapter.TicketMyAdapter;
import com.wwsl.mdsj.bean.TicketBean;
import com.wwsl.mdsj.custom.ItemDecoration;
import com.wwsl.mdsj.custom.RefreshView;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

public class MyTicketActivity extends AbsActivity {
    private RefreshView mRefreshView;
    private TicketMyAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_ticket;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.ticket_title));
        mRefreshView = findViewById(R.id.refreshView);
//        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 0);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRefreshView.setItemDecoration(decoration);
        mRefreshView.setDataHelper(new RefreshView.DataHelper<TicketBean>() {
            @Override
            public RefreshAdapter<TicketBean> getAdapter() {
                if (mAdapter == null) {
                    mAdapter = new TicketMyAdapter(mContext);
                    mAdapter.setOnItemClickListener(new OnItemClickListener<TicketBean>() {
                        @Override
                        public void onItemClick(TicketBean bean, int position) {
                            UserHomePageActivity.forward(mContext, bean.getTouid());
                        }
                    });
                }
                return mAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getTicketMy(p, callback);
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
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.TICKET_MY);
        super.onDestroy();
    }
}
