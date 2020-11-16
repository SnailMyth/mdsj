package com.wwsl.mdsj.activity.live;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.AudienceShopWindowAdapter;
import com.wwsl.mdsj.adapter.ShopWindowAdapter;
import com.wwsl.mdsj.bean.LiveShopWindowBean;
import com.wwsl.mdsj.bean.net.NetGoodsBean;
import com.wwsl.mdsj.dialog.AbsDialogFragment;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class LiveAudienceGoodsDialogFragment extends AbsDialogFragment {
    private SwipeRecyclerView recycler;
    private AudienceShopWindowAdapter mAdapter;
    private List<LiveShopWindowBean> data;
    private String mLiveId;
    private String mLiveName;
    private OnDialogCallBackListener listener;

    public void setListener(OnDialogCallBackListener listener) {
        this.listener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_audience_goods;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(AppContext.sInstance, 450);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mLiveId = bundle.getString(Constants.LIVE_ID);
        mLiveName = bundle.getString(Constants.LIVE_NAME);
        initView();
        loadData();
        initListener();
    }

    private void initListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (listener != null) {
                listener.onDialogViewClick(null, mAdapter.getData().get(position).getWebUrl());
            }
            dismiss();
        });

        mRootView.findViewById(R.id.back).setOnClickListener(v -> {
            dismiss();
        });
    }

    private void loadData() {
        data = new ArrayList<>();
        if ( StrUtil.isEmpty(mLiveId)) {
            ToastUtil.show("直播获取失败");
            dismiss();
        } else {
            HttpUtil.getLiveAllGoods(mLiveId, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    List<NetGoodsBean> netBean = JSON.parseArray(Arrays.toString(info), NetGoodsBean.class);
                    List<LiveShopWindowBean> parseBean = LiveShopWindowBean.parse(netBean, ShopWindowAdapter.TYPE_LIVE);
                    data.clear();
                    data.addAll(parseBean);
                    mAdapter.setNewInstance(parseBean);
                }
            });
        }

    }

    private void initView() {
        recycler = mRootView.findViewById(R.id.recycler);
        mAdapter = new AudienceShopWindowAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.useDefaultLoadMore();
        recycler.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
