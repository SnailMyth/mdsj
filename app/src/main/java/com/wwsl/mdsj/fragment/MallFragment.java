package com.wwsl.mdsj.fragment;

import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.MallWelfareAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.MallWelfareBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MallFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    public static final String PREDICT_WELFARE = "1";//预计福利
    public static final String HAS_WELFARE = "2";//已获福利
    private String type;
    private int page = 1;
    private List<MallWelfareBean.WelfareBean> listData = new ArrayList<>();
    private MallWelfareAdapter adapter;

    public static MallFragment getInstance(String type) {
        MallFragment fragment = new MallFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_mall;
    }

    @Override
    protected void init() {
        if (getArguments() != null) {
            type = getArguments().getString("type", "");
        }
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnRefreshListener(this::refreshData);
        refreshLayout.setOnLoadMoreListener(this::loadMoreData);
        adapter = new MallWelfareAdapter(listData);
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(CommonUtil.getEmptyView(null, getContext(), refreshLayout));
    }

    private void loadMoreData(RefreshLayout refreshLayout) {
        page++;
        loadData(false);
    }

    private void refreshData(RefreshLayout refreshLayout) {
        page = 1;
        loadData(true);
    }


    @Override
    protected void initialData() {
        //网络请求接口
        loadData(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listData.size() == 0) {
            refreshLayout.autoRefresh();
        }
    }

    public void loadData(boolean isFresh) {
        HttpUtil.mallWelfare(type, page, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<MallWelfareBean> welfareBeans = JSON.parseArray(Arrays.toString(info), MallWelfareBean.class);
                    List<MallWelfareBean.WelfareBean> welfare = welfareBeans.get(0).getWelfare();

                    if (page == 1) {
                        listData.clear();
                    }

                    listData.addAll(welfare);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(msg);
                }
                if (isFresh) {
                    refreshLayout.finishRefresh();
                } else {
                    refreshLayout.finishLoadMore();
                }
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

}
