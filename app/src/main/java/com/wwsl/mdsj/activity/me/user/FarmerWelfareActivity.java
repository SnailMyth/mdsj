package com.wwsl.mdsj.activity.me.user;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.adapter.FarmerWelfareAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.FarmerWelfareBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * 助农福利
 */
public class FarmerWelfareActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.txTotal)
    TextView txTotal;
    private String total;

    private List<FarmerWelfareBean> listData = new ArrayList<>();

    private int page = 1;
    private FarmerWelfareAdapter adapter;

    public void backClick(View view) {
        finish();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_farmer_welfare;
    }

    @Override
    protected void init() {

        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(this::refreshData);
        refreshLayout.setOnLoadMoreListener(this::loadMoreData);
        adapter = new FarmerWelfareAdapter(listData);
        recyclerView.useDefaultLoadMore();
        recyclerView.setAdapter(adapter);
        adapter.setEmptyView(CommonUtil.getEmptyView(null, this, refreshLayout));
        refreshLayout.autoRefresh();
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
    protected void onResume() {
        super.onResume();
    }

    private void loadData(boolean isFresh) {
        HttpUtil.farmerWelfare(page, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject jsonObject = JSON.parseObject(info[0]);
                    total = jsonObject.getString("total");
                    txTotal.setText(total);
                    JSONArray list = jsonObject.getJSONArray("list");
                    List<FarmerWelfareBean> welfareBeans = JSON.parseArray(list.toJSONString(), FarmerWelfareBean.class);

                    if (page == 1) {
                        listData.clear();
                    }

                    listData.addAll(welfareBeans);
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
        });
    }

    public static void invoke(Activity activity) {
        Intent intent = new Intent(activity, FarmerWelfareActivity.class);
        activity.startActivity(intent);
    }

    public void showDes(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_ZN_WELFARE__DES);
    }

}
