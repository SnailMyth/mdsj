package com.wwsl.mdsj.fragment;

import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.WithdrawalsAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.Withdrawals;
import com.wwsl.mdsj.custom.ItemDecoration;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 提现记录列表
 */
public class WithdrawalsRecordFragment extends BaseFragment {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.recyclerview)
    SwipeRecyclerView recyclerview;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout refreshlayout;

    /**
     * 2 礼物提现记录
     * 3 豆丁提现记录
     */
    private int type = -1;
    private WithdrawalsAdapter adapter;

    public WithdrawalsRecordFragment() {
    }

    @Override
    protected int setLayoutId() {
        return R.layout.view_rv_header_footer;
    }

    @Override
    protected void init() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            type = intent.getIntExtra("type", -1);
            title.setText(intent.getStringExtra("title"));
        }
    }

    @Override
    protected void initialData() {
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        ItemDecoration decoration = new ItemDecoration(mContext, getResources().getColor(R.color.line_color), 200, 1);
        recyclerview.addItemDecoration(decoration);
        adapter = new WithdrawalsAdapter(type);
        recyclerview.setAdapter(adapter);

        refreshlayout.setEnableRefresh(false);
        refreshlayout.setEnableLoadMore(false);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        HttpUtil.getTxRecord(type, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code != 0 || info == null || info.length == 0) {
                    ToastUtil.show(msg);
                    return;
                }
                List<Withdrawals> list = JSON.parseArray(Arrays.toString(info), Withdrawals.class);
                adapter.getData().clear();
                adapter.notifyDataSetChanged();
                adapter.getData().addAll(list);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HttpUtil.cancel(HttpConst.TXRECORD);
    }
}
