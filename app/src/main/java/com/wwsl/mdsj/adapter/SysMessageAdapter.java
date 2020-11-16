package com.wwsl.mdsj.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.net.SysMsgBean;
import com.wwsl.mdsj.utils.IconUtil;
import com.wwsl.mdsj.views.CircleImageView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SysMessageAdapter extends BaseQuickAdapter<SysMsgBean, BaseViewHolder> {

    public SysMessageAdapter(@Nullable List<SysMsgBean> data) {
        super(R.layout.item_sys_message, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SysMsgBean sysMsgBean) {
        CircleImageView avatar = holder.getView(R.id.avatar);
        avatar.setImageResource(IconUtil.getSysMsgIconType(sysMsgBean.getType()));
        holder.setText(R.id.content, sysMsgBean.getContent());
        holder.setText(R.id.time, sysMsgBean.getTime());
    }
}
