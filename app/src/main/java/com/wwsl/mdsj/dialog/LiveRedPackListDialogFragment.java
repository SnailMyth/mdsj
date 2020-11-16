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
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.adapter.RedPackAdapter;
import com.wwsl.mdsj.bean.RedPackBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/11/21.
 * 红包列表弹窗
 */

public class LiveRedPackListDialogFragment extends AbsDialogFragment implements OnItemClickListener<RedPackBean>, LiveRedPackRobDialogFragment.ActionListener {

    private TextView mCount;
    private TextView txEmpty;
    private RecyclerView mRecyclerView;
    private String mStream;
    private RedPackAdapter mRedPackAdapter;
    private String mCoinName;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_red_pack_list;
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
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(260);
        params.height = DpUtil.dp2px(330);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setCoinName(String coinName) {
        mCoinName = coinName;
    }

    public void setStream(String stream) {
        mStream = stream;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (TextUtils.isEmpty(mStream)) {
            return;
        }
        mCount = mRootView.findViewById(R.id.count);
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        txEmpty = mRootView.findViewById(R.id.tx_empty);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        HttpUtil.getRedPackList(mStream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<RedPackBean> list = JSON.parseArray(Arrays.toString(info), RedPackBean.class);
                    mRedPackAdapter = new RedPackAdapter(mContext, list);
                    mRedPackAdapter.setOnItemClickListener(LiveRedPackListDialogFragment.this);
                    mRecyclerView.setAdapter(mRedPackAdapter);
                    mCount.setText(String.format(WordUtil.getString(R.string.red_pack_9), String.valueOf(list.size())));
                    if (list.isEmpty()){
                        mRecyclerView.setVisibility(View.GONE);
                        txEmpty.setVisibility(View.VISIBLE);
                    }else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        txEmpty.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mRedPackAdapter != null) {
            mRedPackAdapter.release();
        }
        mRedPackAdapter = null;
        super.onDestroy();
    }

    @Override
    public void onItemClick(RedPackBean bean, int position) {
        if (bean.getIsRob() == 1) {
            LiveRedPackRobDialogFragment fragment = new LiveRedPackRobDialogFragment();
            fragment.setActionListener(this);
            fragment.setRedPackBean(bean);
            fragment.setStream(mStream);
            fragment.setCoinName(mCoinName);
            fragment.setRedPackAdapter(mRedPackAdapter);
            fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveRedPackRobDialogFragment");
        } else {
            LiveRedPackResultDialogFragment fragment = new LiveRedPackResultDialogFragment();
            fragment.setStream(mStream);
            fragment.setRedPackBean(bean);
            fragment.setCoinName(mCoinName);
            fragment.show(((LiveActivity) mContext).getSupportFragmentManager(), "LiveRedPackResultDialogFragment");
        }
    }

    @Override
    public void hide() {
        if (mRootView != null && mRootView.getVisibility() == View.VISIBLE) {
            mRootView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void show(boolean mNeedDelay) {
        if (mNeedDelay && mRedPackAdapter != null) {
            mRedPackAdapter.postDelay(new Runnable() {
                @Override
                public void run() {
                    if (mRootView != null && mRootView.getVisibility() != View.VISIBLE) {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                }
            }, 300);
        } else {
            if (mRootView != null && mRootView.getVisibility() != View.VISIBLE) {
                mRootView.setVisibility(View.VISIBLE);
            }
        }
    }
}
