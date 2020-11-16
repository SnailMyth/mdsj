package com.wwsl.mdsj.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.ShareBean;

import java.util.List;

public class ShareAdapter extends BaseQuickAdapter<ShareBean, BaseViewHolder> {


    public ShareAdapter(@Nullable List<ShareBean> data) {
        super(R.layout.item_share, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ShareBean shareBean) {
        helper.setImageResource(R.id.tv_icon, shareBean.getIconRes());
        helper.setText(R.id.tv_text, shareBean.getText());
    }
}
