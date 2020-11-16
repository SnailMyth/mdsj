package com.wwsl.mdsj.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.WishBillListAdapter;
import com.wwsl.mdsj.bean.WishBillBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnWishBillSendItemClickListener;
import com.wwsl.mdsj.utils.WordUtil;

public class WishBillListDialogFragment extends AbsDialogFragment implements View.OnClickListener {
    private OnWishBillSendItemClickListener onWishBillSendItemClickListener;
    private TextView tvTitle;
    private RecyclerView mRecyclerView;
    private WishBillListAdapter wishBillListAdapter;
    private String mLiveUid;
    private String mLiveName;

    public void setOnWishBillSendItemClickListener(OnWishBillSendItemClickListener onWishBillSendItemClickListener) {
        this.onWishBillSendItemClickListener = onWishBillSendItemClickListener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wish_bill_list;
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
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        mLiveUid = bundle.getString(Constants.LIVE_UID);
        mLiveName = bundle.getString(Constants.LIVE_NAME);
        tvTitle = mRootView.findViewById(R.id.tvTitle);
        if (TextUtils.isEmpty(mLiveName)) {
            tvTitle.setText(WordUtil.getString(R.string.wish_live_gift_ready));
        } else {
            tvTitle.setText(mLiveName + WordUtil.getString(R.string.wish_list_title));
        }
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mRootView.findViewById(R.id.btn_back).setOnClickListener(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        wishBillListAdapter = new WishBillListAdapter(mContext);
        wishBillListAdapter.setOnWishBillSendItemClickListener(new OnWishBillSendItemClickListener() {
            @Override
            public void onAvatarClick(WishBillBean.SendUser bean) {
                if (onWishBillSendItemClickListener != null) {
                    onWishBillSendItemClickListener.onAvatarClick(bean);
                }
            }
        });
        mRecyclerView.setAdapter(wishBillListAdapter);

        HttpUtil.getWishList(mLiveUid, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (info != null && info.length > 0) {
                    wishBillListAdapter.getList().addAll(JSON.parseArray(info[0], WishBillBean.class));
                    wishBillListAdapter.notifyDataSetChanged();
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
        }
    }

}