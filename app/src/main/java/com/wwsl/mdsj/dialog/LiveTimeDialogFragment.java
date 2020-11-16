package com.wwsl.mdsj.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.LiveTimeChargeAdapter;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

/**
 * Created by cxf on 2018/10/8.
 */

public class LiveTimeDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private LiveTimeChargeAdapter mAdapter;
    private CommonCallback<Integer> mCommonCallback;
    private EditText tvContent;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_room_time_new;
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
        params.width = DpUtil.dp2px(280);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        int checkedCoin = bundle.getInt(Constants.CHECKED_COIN, 0);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new LiveTimeChargeAdapter(mContext, checkedCoin);
        mRecyclerView.setAdapter(mAdapter);

        tvContent = mRootView.findViewById(R.id.content);
        ((TextView) mRootView.findViewById(R.id.tvFeeInfo)).setText(AppConfig.getInstance().getCoinName() + WordUtil.getString(R.string.live_time_fee_info_2));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_confirm:
                if (TextUtils.isEmpty(tvContent.getText().toString().trim())) {
                    ToastUtil.show(WordUtil.getString(R.string.live_set_fee_empty));
                    return;
                } else {
                    setCheckedCoin();
                }
                break;
        }
        dismiss();
    }

    private void setCheckedCoin() {
        if (mAdapter != null && mCommonCallback != null) {
//            mCommonCallback.callback(mAdapter.getCheckedCoin());
            mCommonCallback.callback(Integer.parseInt(tvContent.getText().toString().trim()));
        }
    }


    public void setCommonCallback(CommonCallback<Integer> commonCallback) {
        mCommonCallback = commonCallback;
    }

    @Override
    public void onDestroy() {
        mCommonCallback = null;
        super.onDestroy();
    }
}
