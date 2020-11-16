package com.wwsl.mdsj.activity.maodou;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.maodou.TaskProcessAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.maodou.NetTaskProcessBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author :
 * @date : 2020/7/8 18:10
 * @description : ProcessingFragment
 */
public class ProcessingFragment extends BaseFragment {

    private SwipeRecyclerView recycler;
    private SmartRefreshLayout root;
    private TaskProcessAdapter adapter;
    private List<NetTaskProcessBean> data;

    public static ProcessingFragment newInstance() {
        return new ProcessingFragment();
    }


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_processing;
    }

    @Override
    protected void init() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        root = (SmartRefreshLayout) findViewById(R.id.root);

        root.setRefreshHeader(new ClassicsHeader(getContext()));
        root.setOnRefreshListener(this::refreshData);
//        root.setOnLoadMoreListener(this::loadMoreData);

        data = new ArrayList<>();
        adapter = new TaskProcessAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        recycler.useDefaultLoadMore();
        adapter.setEmptyView(CommonUtil.getEmptyView("暂无进度记录", getContext(), root));
    }

//    private void loadMoreData(RefreshLayout refreshLayout) {
//
//    }

    private void refreshData(RefreshLayout refreshLayout) {

        loadData();
    }


    @Override
    protected void initialData() {
        root.autoRefresh();
    }

    public void loadData() {
        HttpUtil.getProMwTask(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    data.clear();
                    List<NetTaskProcessBean> netTaskProcessBeans = JSON.parseArray(Arrays.toString(info), NetTaskProcessBean.class);
                    data.addAll(netTaskProcessBeans);
                    adapter.setNewInstance(netTaskProcessBeans);
                } else {
                    ToastUtil.show(msg);
                }
                root.finishRefresh();
            }

            @Override
            public void onError() {
                root.finishRefresh();
            }
        });
    }


}
