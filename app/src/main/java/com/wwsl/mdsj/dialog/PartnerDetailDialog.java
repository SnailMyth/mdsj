package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.net.NetPartnerDetailBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartnerDetailDialog extends BasePopupView {


    private TextView tvTitle;
    private TextView txApply;
    private SwipeRecyclerView recycler;
    private String title;
    private int type;
    private OnDialogCallBackListener listener;

    private PartnerAdapter adapter;

    public PartnerDetailDialog(@NonNull Context context, String title, int type, OnDialogCallBackListener listener) {
        super(context);
        this.title = title;
        this.type = type;
        this.listener = listener;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_parter_detail;
    }


    @Override
    protected void onCreate() {
        super.onCreate();

        tvTitle = findViewById(R.id.title);
        txApply = findViewById(R.id.txApply);
        recycler = findViewById(R.id.recycler);
        tvTitle.setText(title);
        adapter = new PartnerAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        findViewById(R.id.ivClose).setOnClickListener(v -> {
            dismiss();
        });
        txApply.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onDialogViewClick(null, null);
            }
        });
        recycler.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        HttpUtil.getUserPartner(type, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<NetPartnerDetailBean> netPartnerDetailBeans = JSON.parseArray(Arrays.toString(info), NetPartnerDetailBean.class);
                adapter.setNewInstance(netPartnerDetailBeans);
            }
        });
    }

    static class PartnerAdapter extends BaseQuickAdapter<NetPartnerDetailBean, BaseViewHolder> {


        public PartnerAdapter(@Nullable List<NetPartnerDetailBean> data) {
            super(R.layout.item_partner_detail, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, NetPartnerDetailBean bean) {
            holder.setText(R.id.name, bean.getAddress());
            switch (bean.getState()) {
                case "0":
                    holder.setText(R.id.txStatus, "审核中");
                    break;
                case "1":
                    holder.setText(R.id.txStatus, "申请失败");
                    break;
                case "2":
                    holder.setText(R.id.txStatus, "申请成功");
                    break;
            }

        }
    }

}
