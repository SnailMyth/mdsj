package com.wwsl.mdsj.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.HtmlConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.adapter.ActiveAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.ActiveShowBean;
import com.wwsl.mdsj.bean.net.MdBaseDataBean;
import com.wwsl.mdsj.bean.net.NetActiveRecordBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HuoYueActivity extends BaseActivity implements SwipeRecyclerView.LoadMoreListener {

    private SegmentTabLayout tabLayout;
    private TextView tvLabel5;
    private TextView tvLabel4;
    private TextView num1;
    private TextView num2;
    private TextView num3;
    private TextView num4;
    private TextView num5;
    private TextView num6;
    private TextView num7;
    private ConstraintLayout mdLayout;
    private ConstraintLayout hyLayout;
    private ConstraintLayout recyclerContainer;
    private SwipeRecyclerView recycler;

    private ActiveAdapter activeAdapter;
    private List<ActiveShowBean> activeData;
    private List<ActiveShowBean> maodouData;
    private int mPage = 1;


    private int selectedIndex = 0;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_huo_yue;
    }

    @Override
    protected void init() {
        initView();
        initData();
        changeMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLoad();
        loadMdBase();
    }

    private void loadMdBase() {
        HttpUtil.getMDBaseInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                dismissLoad();
                if (code == 200 && null != info && info.length > 0) {
                    MdBaseDataBean parse = JSON.parseObject(info[0], MdBaseDataBean.class);
                    if (null != parse) {
                        AppConfig.getInstance().setMdBaseDataBean(parse);
                        num4.setText(parse.getMaoDou());
                        num2.setText(parse.getTodayGet());
                        num3.setText(parse.getMaodouFrozen());
                        num1.setText(parse.getRemainIncome());
                        num5.setText(parse.getUserTotalActive());
                        num6.setText(parse.getBaseActive());
                        num7.setText(parse.getActivePlus());
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                super.onError();
                dismissLoad();
            }
        });
    }

    private void initData() {
        activeData = new ArrayList<>();
        maodouData = new ArrayList<>();
        activeAdapter = new ActiveAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(activeAdapter);
        recycler.useDefaultLoadMore();
        recycler.setLoadMoreListener(this);

        activeAdapter.setEmptyView(CommonUtil.getEmptyView(null, this, recyclerContainer));
        loadData(0);

    }

    private void loadData(int index) {
        mPage = 1;
        if (index == 0) {
            HttpUtil.getMdRecord(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, true);
                }
            });
        } else {
            HttpUtil.getActiveRecord(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, true);
                }
            });
        }
    }

    private void loadBack(int code, String msg, String[] info, boolean isFresh) {
        if (code == 200) {
            List<NetActiveRecordBean> netBeans = JSON.parseArray(Arrays.toString(info), NetActiveRecordBean.class);
            List<ActiveShowBean> parseBean = NetActiveRecordBean.parse(netBeans);
            if (selectedIndex == 0) {
                if (isFresh) {
                    maodouData.clear();
                    maodouData.addAll(parseBean);
                    activeAdapter.setNewInstance(parseBean);
                } else {
                    maodouData.addAll(parseBean);
                    activeAdapter.addData(parseBean);
                }
            } else {
                if (isFresh) {
                    activeData.clear();
                    activeData.addAll(parseBean);
                    activeAdapter.setNewInstance(parseBean);
                } else {
                    activeData.addAll(parseBean);
                    activeAdapter.addData(parseBean);
                }
            }
            recycler.loadMoreFinish(netBeans.size() == 0, true);
        } else {
            ToastUtil.show(msg);
            recycler.loadMoreFinish(!isFresh, true);
        }

    }

    private void changeMode() {
        tvLabel4.setText(selectedIndex == 1 ? "我的活跃记录" : "收豆明细");
        activeAdapter.setNewInstance(selectedIndex == 1 ? activeData : maodouData);
        if (selectedIndex == 1) {
            //活跃度
            mdLayout.setVisibility(View.GONE);
            hyLayout.setVisibility(View.VISIBLE);
            if (activeData.size() == 0) {
                loadData(1);
            }
        } else {
            //毛豆
            if (maodouData.size() == 0) {
                loadData(0);
            }
            mdLayout.setVisibility(View.VISIBLE);
            hyLayout.setVisibility(View.GONE);
        }
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        tabLayout = findViewById(R.id.tablayout);
        mdLayout = findViewById(R.id.mdLayout);
        hyLayout = findViewById(R.id.hyLayout);
        recyclerContainer = findViewById(R.id.recyclerContainer);
        tvLabel5 = findViewById(R.id.tvLabel5);
        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        num3 = findViewById(R.id.num3);
        num4 = findViewById(R.id.num4);
        num5 = findViewById(R.id.num5);
        num6 = findViewById(R.id.num6);
        num7 = findViewById(R.id.num7);
        tvLabel4 = findViewById(R.id.tvLabel4);
        recycler = findViewById(R.id.recycler);
        String[] mTitles = {"毛豆", "活跃度"};
        tabLayout.setTabData(mTitles);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (selectedIndex != tabLayout.getCurrentTab()) {
                    selectedIndex = tabLayout.getCurrentTab();
                    changeMode();
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    public void jumpLevel(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_ACTIVE_LEVEL);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, HuoYueActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onLoadMore() {
        mPage++;
        if (selectedIndex == 0) {
            HttpUtil.getMdRecord(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, false);
                }
            });
        } else {
            HttpUtil.getActiveRecord(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, false);
                }
            });
        }
    }
}
