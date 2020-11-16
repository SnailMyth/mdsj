package com.wwsl.mdsj.activity.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.SysMessageAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.SystemMessageBean;
import com.wwsl.mdsj.bean.net.SysMsgBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.IconUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SysMessageActivity extends BaseActivity {

    private TextView title;
    private SwipeRecyclerView recycler;
    private SmartRefreshLayout refreshLayout;
    private SysMessageAdapter mAdapter;
    private List<SysMsgBean> msgBeans;
    private int type;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_sys_message;
    }

    @Override
    protected void init() {
        initView();
        type = getIntent().getIntExtra("type", 1);
        title.setText(IconUtil.getSysMsgName(type));
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(this::refreshData);
        msgBeans = new ArrayList<>();
        mAdapter = new SysMessageAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(mAdapter);
        mAdapter.setEmptyView(CommonUtil.getEmptyView("暂无消息", this, refreshLayout));
    }

    private void refreshData(RefreshLayout refreshLayout) {
        loadData();
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        title = findViewById(R.id.title);
        recycler = findViewById(R.id.recycler);
        refreshLayout = findViewById(R.id.refreshLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (msgBeans.size() == 0) {
            refreshLayout.autoRefresh();
        }
    }

    private void loadData() {
        HttpUtil.getSystemMessageList(type, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<SystemMessageBean> systemMessageBeans = JSON.parseArray(Arrays.toString(info), SystemMessageBean.class);
                    msgBeans.clear();
                    msgBeans.addAll(SysMsgBean.parse(systemMessageBeans, type));
                    mAdapter.setNewInstance(msgBeans);
                }
                refreshLayout.finishRefresh();
            }

            @Override
            public void onError() {
                refreshLayout.finishRefresh();
            }
        });
    }

    public static void forward(Context context, int type) {
        Intent intent = new Intent(context, SysMessageActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
}
