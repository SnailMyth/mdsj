package com.wwsl.mdsj.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.adapter.WishBillAddAdapter;
import com.wwsl.mdsj.bean.LiveGiftBean;
import com.wwsl.mdsj.bean.WishBillBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnWishBillGiftSelectListener;
import com.wwsl.mdsj.interfaces.OnWishBillItemClickListener;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

public class WishBillAddDialogFragment extends AbsDialogFragment implements View.OnClickListener, OnWishBillItemClickListener {
    private TextView tvAdd;
    private TextView tvGenerate;
    private RecyclerView mRecyclerView;
    private WishBillAddAdapter wishBillAddAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wish_bill_add;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
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
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View header = LayoutInflater.from(mContext).inflate(R.layout.view_wish_bill_add_info, null, false);
        tvAdd = mRootView.findViewById(R.id.tvAdd);
        tvGenerate = mRootView.findViewById(R.id.tvGenerate);
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        tvAdd.setOnClickListener(this);
        tvGenerate.setOnClickListener(this);
        mRootView.findViewById(R.id.btn_back).setOnClickListener(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        wishBillAddAdapter = new WishBillAddAdapter(mContext);
        wishBillAddAdapter.setHeaderView(header);
        wishBillAddAdapter.setOnWishBillItemClickListener(this);
        mRecyclerView.setAdapter(wishBillAddAdapter);

        HttpUtil.getWishList(AppConfig.getInstance().getUid(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    wishBillAddAdapter.getList().addAll(JSON.parseArray(info[0], WishBillBean.class));
                    if (wishBillAddAdapter.getItemCount() == 1) {
                        wishBillAddAdapter.getList().add(new WishBillBean());
                    }
                    wishBillAddAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
            case R.id.tvAdd:
                wishBillAddAdapter.getList().add(new WishBillBean());
                wishBillAddAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(wishBillAddAdapter.getItemCount() - 1);
                break;
            case R.id.tvGenerate:
                setWish();
                break;
        }
    }

    /**
     * 生成心愿
     */
    private void setWish() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (WishBillBean bean : wishBillAddAdapter.getList()) {
            if (!TextUtils.isEmpty(bean.getGiftid())) {
                stringBuilder.append("{\"giftid\":");
                stringBuilder.append(bean.getGiftid());
                stringBuilder.append(",\"giftcount\":");
                stringBuilder.append(bean.getNum());
                stringBuilder.append("},");
            }
        }
        if (stringBuilder.toString().endsWith(",")) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append("]");
        HttpUtil.setWish(stringBuilder.toString(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show(WordUtil.getString(R.string.wish_generate_success));
                    dismiss();
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    public void onItemClick(WishBillBean bean, int position) {

    }

    @Override
    public void onAvatarClick(final WishBillBean bean, final int position) {
        WishBillGiftDialogFragment fragment = new WishBillGiftDialogFragment();
        fragment.setOnWishBillGiftSelectListener(new OnWishBillGiftSelectListener() {
            @Override
            public void onSelectConfirm(LiveGiftBean liveGiftBean, String giftcount) {
                boolean repeat = false;
                for (WishBillBean bean : wishBillAddAdapter.getList()) {
                    if (!TextUtils.isEmpty(bean.getGiftid()) && bean.getGiftid().equals(liveGiftBean.getId() + "")) {
                        repeat = true;
                        break;
                    }
                }
                if (repeat) {
                    ToastUtil.show(WordUtil.getString(R.string.wish_gift_select_repeat));
                } else {
                    bean.setNum(giftcount);
                    bean.setGiftid(liveGiftBean.getId() + "");
                    bean.setGifticon(liveGiftBean.getIcon());
                    bean.setGiftname(liveGiftBean.getName());
                    wishBillAddAdapter.notifyDataSetChanged();
                }
            }
        });
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "WishBillGiftDialogFragment");
    }

    @Override
    public void onDeleteClick(int position) {
        wishBillAddAdapter.getList().remove(position);
        wishBillAddAdapter.notifyDataSetChanged();
    }
}
