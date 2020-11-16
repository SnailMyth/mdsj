package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lxj.xpopup.core.BasePopupView;
import com.lzy.okgo.model.Response;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.AtFiendAdapter;
import com.wwsl.mdsj.bean.FansShowBean;
import com.wwsl.mdsj.bean.net.NetFriendBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.http.JsonBean;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class AtFriendDialog extends BasePopupView implements SwipeRecyclerView.LoadMoreListener {


    private String uid;

    private OnDialogCallBackListener listener;
    private AtFiendAdapter adapter;
    private List<NetFriendBean> data;
    private SwipeRecyclerView recycler;
    private int mPage = 1;

    public AtFriendDialog(@NonNull Context context) {
        super(context);
    }

    public AtFriendDialog(@NonNull Context context, String uid, OnDialogCallBackListener listener) {
        super(context);
        this.uid = uid;
        this.listener = listener;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_at_user;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        data = new ArrayList<>();
        adapter = new AtFiendAdapter(new ArrayList<>());
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (null != listener) {
                    listener.onDialogViewClick(null, data.get(position));
                }
                dismiss();
            }
        });
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        recycler.useDefaultLoadMore();
        recycler.setAdapter(adapter);
        if (!StrUtil.isEmpty(uid)) {
            loadData();
        }
        recycler.setLoadMoreListener(this);
        findViewById(R.id.ivClose).setOnClickListener(v -> {
            dismiss();
        });
    }

    private void loadData() {
        HttpUtil.getFriendsList(uid, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<NetFriendBean> netFansBeans = JSON.parseArray(Arrays.toString(info), NetFriendBean.class);
                if (code == 0) {
                    data.addAll(netFansBeans);
                    adapter.addData(netFansBeans);
                } else {
                    ToastUtil.show(msg);
                }
                boolean b = netFansBeans.size() == HttpConst.ITEM_COUNT;
                if (b) {
                    mPage++;
                }
                recycler.loadMoreFinish(false, b);
            }

            @Override
            public void onError(Response<JsonBean> response) {
                super.onError(response);
                recycler.loadMoreFinish(false, true);
            }
        });
    }

    @Override
    public void onLoadMore() {
        loadData();
    }
}
