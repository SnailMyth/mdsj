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
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.BankAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.net.NetBankCardBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectBankCardActivity extends BaseActivity {

    private SwipeRecyclerView cardRecycler;
    private BankAdapter bankAdapter;
    private List<NetBankCardBean> beans;
    private SmartRefreshLayout refreshLayout;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_bank_card_selected;
    }

    @Override
    protected void init() {
        cardRecycler = findViewById(R.id.cardRecycler);
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        beans = new ArrayList<>();
        bankAdapter = new BankAdapter(beans);
        cardRecycler.setLayoutManager(new LinearLayoutManager(this));
        cardRecycler.setAdapter(bankAdapter);
        bankAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                NetBankCardBean bean = beans.get(position);
                Intent intent = new Intent();
                intent.putExtra("bank", bean);
                setResult(1, intent);
                finish();
            }
        });

        bankAdapter.setEmptyView(CommonUtil.getEmptyView("暂无银行卡", this, refreshLayout));

        refreshLayout.setOnRefreshListener(this::refreshCard);
    }

    private void refreshCard(RefreshLayout refreshLayout) {
        loadData();
    }


    public void backClick(View view) {
        finish();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, SelectBankCardActivity.class);
        context.startActivity(intent);
    }


    private void loadData() {
        HttpUtil.getUserBankCard(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<NetBankCardBean> netBankCardBeans = JSON.parseArray(Arrays.toString(info), NetBankCardBean.class);
                    beans.clear();
                    beans.addAll(netBankCardBeans);
                    bankAdapter.setNewInstance(netBankCardBeans);
                }
                refreshLayout.finishRefresh();
            }
        });
    }

    public void addBankCard(View view) {
        BankCardAddActivity.forward(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != refreshLayout) {
            refreshLayout.autoRefresh();
        }
    }

}
