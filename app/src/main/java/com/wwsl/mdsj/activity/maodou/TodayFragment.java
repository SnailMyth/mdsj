package com.wwsl.mdsj.activity.maodou;

import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.maodou.ViewHistoryAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.maodou.ViewVideoHistoryBean;
import com.wwsl.mdsj.bean.net.MdBaseDataBean;
import com.wwsl.mdsj.bean.net.NetTodayProcessBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author :
 * @date : 2020/7/8 18:44
 * @description : TodayFragment
 */
public class TodayFragment extends BaseFragment implements SwipeRecyclerView.LoadMoreListener {

    private ViewHistoryAdapter adapter;
    private List<ViewVideoHistoryBean> data;
    private TextView tvTodaySettle;
    private TextView tvPercent;
    private SwipeRecyclerView recycler;
    private int mPage = 1;
    private ConstraintLayout recyclerContainer;

    public static TodayFragment newInstance() {
        return new TodayFragment();
    }


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_today;
    }

    @Override
    protected void init() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        tvTodaySettle = (TextView) findViewById(R.id.tvTodaySettle);
        recyclerContainer = (ConstraintLayout) findViewById(R.id.recyclerContainer);
        tvPercent = (TextView) findViewById(R.id.tvPercent);
        data = new ArrayList<>();
        adapter = new ViewHistoryAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        adapter.setEmptyView(CommonUtil.getEmptyView("暂无观看记录", getContext(), recyclerContainer));
    }

    @Override
    protected void initialData() {
        mPage = 1;
        HttpUtil.getTodayRecord(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadBack(code, msg, info, true);
            }
        });
        showLoadCancelable(false, "加载中...");
        HttpUtil.getMDBaseInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                dismissLoad();
                if (code == 200 && null != info && info.length > 0) {
                    MdBaseDataBean parse = JSON.parseObject(info[0], MdBaseDataBean.class);
                    if (null != parse) {
                        AppConfig.getInstance().setMdBaseDataBean(parse);
                        tvTodaySettle.setText(String.format("已收豆:%s", parse.getTodaySettlement()));
                        tvPercent.setText(parse.getTodayRate());
                    }
                }
            }

            @Override
            public void onError() {
                dismissLoad();
                super.onError();
            }
        });

    }

    private void loadBack(int code, String msg, String[] info, boolean isFresh) {
        if (code == 200) {
            List<NetTodayProcessBean> netBeans = JSON.parseArray(Arrays.toString(info), NetTodayProcessBean.class);
            List<ViewVideoHistoryBean> parseBeans = NetTodayProcessBean.parse(netBeans);

            if (isFresh) {
                data.clear();
                data.addAll(parseBeans);
                adapter.setNewInstance(parseBeans);
            } else {
                data.addAll(parseBeans);
                adapter.addData(parseBeans);
            }

            recycler.loadMoreFinish(parseBeans.size() == 0, true);
        } else {
            ToastUtil.show(msg);
            recycler.loadMoreFinish(true, true);
        }
    }

    @Override
    public void onLoadMore() {
        mPage++;
        HttpUtil.getTodayRecord(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                loadBack(code, msg, info, false);
            }
        });
    }
}
