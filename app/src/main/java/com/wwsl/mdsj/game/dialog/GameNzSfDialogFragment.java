package com.wwsl.mdsj.game.dialog;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.dialog.AbsDialogFragment;
import com.wwsl.mdsj.game.adapter.GameNzSfAdapter;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;

import java.util.Arrays;

/**
 * Created by cxf on 2018/11/5.
 * 开心牛仔胜负走势图
 */

public class GameNzSfDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.game_dialog_nz_sf;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return false;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(280);
        params.height = DpUtil.dp2px(360);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        String stream = bundle.getString(Constants.STREAM);
        if (TextUtils.isEmpty(stream)) {
            return;
        }
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        HttpUtil.gameNiuRecord(stream, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    int[][] arrays = JSON.parseObject(Arrays.toString(info), int[][].class);
                    GameNzSfAdapter adapter = new GameNzSfAdapter(mContext, arrays);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpConst.GAME_NIU_RECORD);
        super.onDestroy();
    }
}
