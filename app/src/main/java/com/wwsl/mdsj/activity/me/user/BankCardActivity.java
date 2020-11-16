package com.wwsl.mdsj.activity.me.user;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
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
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankCardActivity extends BaseActivity {

    private SwipeRecyclerView cardRecycler;
    private SmartRefreshLayout refreshLayout;
    private BankAdapter bankAdapter;
    private TextView tvDes;
    private boolean isSHowManage = false;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_bank_card;
    }

    @Override
    protected void init() {
        cardRecycler = findViewById(R.id.cardRecycler);
        refreshLayout = findViewById(R.id.refreshLayout);
        tvDes = findViewById(R.id.tvDes);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        bankAdapter = new BankAdapter(new ArrayList<>());
        cardRecycler.setLayoutManager(new LinearLayoutManager(this));
        cardRecycler.setAdapter(bankAdapter);
        bankAdapter.setEmptyView(CommonUtil.getEmptyView("暂无银行卡", this, refreshLayout));

        refreshLayout.setOnRefreshListener(this::refreshCard);

        bankAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.txDelete) {
                showLoad();
                HttpUtil.deleteAccountBank(bankAdapter.getData().get(position).getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            HttpUtil.getUserBankCard(new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    dismissLoad();
                                    if (code == 0) {
                                        List<NetBankCardBean> netBankCardBeans = JSON.parseArray(Arrays.toString(info), NetBankCardBean.class);

                                        for (int i = 0; i < netBankCardBeans.size(); i++) {
                                            netBankCardBeans.get(i).setShowManager(isSHowManage);
                                        }

                                        bankAdapter.setNewInstance(netBankCardBeans);
                                    }
                                    refreshLayout.finishRefresh();
                                }

                                @Override
                                public void onError() {
                                    dismissLoad();
                                }
                            });
                        } else {
                            dismissLoad();
                            ToastUtil.show(msg);
                        }
                    }

                    @Override
                    public void onError() {
                        dismissLoad();
                    }
                });
            }
        });
    }

    private void refreshCard(RefreshLayout refreshLayout) {
        loadData();
    }

    public void backClick(View view) {
        finish();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, BankCardActivity.class);
        context.startActivity(intent);
    }

    public void addBankCard(View view) {
        BankCardAddActivity.forward(this);
    }


    private void loadData() {
        HttpUtil.getUserBankCard(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<NetBankCardBean> netBankCardBeans = JSON.parseArray(Arrays.toString(info), NetBankCardBean.class);
                    bankAdapter.setNewInstance(netBankCardBeans);
                }
                refreshLayout.finishRefresh();
            }
        });
    }

    public void changeMode(View view) {
        if (bankAdapter != null) {
            isSHowManage = !isSHowManage;

            tvDes.setText(isSHowManage ? "确定" : "管理");
            int size = bankAdapter.getData().size();
            for (int i = 0; i < size; i++) {
                bankAdapter.getData().get(i).setShowManager(isSHowManage);
            }
            bankAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != refreshLayout) {
            refreshLayout.autoRefresh();
        }
    }

}
