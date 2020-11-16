package com.wwsl.mdsj.activity.login;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.adapter.RecommendAdapter;
import com.wwsl.mdsj.bean.RecommendBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/11/2.
 */

public class RecommendActivity extends AbsActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private RecommendAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recommend;
    }

    @Override
    protected void main() {
        findViewById(R.id.btn_enter).setOnClickListener(this);
        findViewById(R.id.btn_skip).setOnClickListener(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        HttpUtil.getRecommend(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<RecommendBean> list = JSON.parseArray(Arrays.toString(info), RecommendBean.class);
                    if (mAdapter == null) {
                        mAdapter = new RecommendAdapter(mContext, list);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
            }
        });
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, RecommendActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter:
                enter();
                break;
            case R.id.btn_skip:
                skip();
                break;
        }
    }

    private void enter() {
        if (mAdapter == null) {
            skip();
            return;
        }
        String uids = mAdapter.getCheckedUid();
        if (TextUtils.isEmpty(uids)) {
            skip();
            return;
        }
        HttpUtil.recommendFollow(uids, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    skip();
                }
            }
        });
    }

    /**
     * 跳过
     */
    private void skip() {
        MainActivity.forward(mContext);
        overridePendingTransition(R.anim.anim_fade_in, 0);
        finish();
        overridePendingTransition(0, R.anim.anim_fade_out);
    }

    @Override
    public void onBackPressed() {
        skip();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.GET_RECOMMEND);
        HttpUtil.cancel(HttpConst.RECOMMEND_FOLLOW);
        super.onDestroy();
    }
}
