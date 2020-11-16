package com.wwsl.mdsj.activity.live;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.adapter.GuardAdapter;
import com.wwsl.mdsj.adapter.RefreshAdapter;
import com.wwsl.mdsj.bean.GuardUserBean;
import com.wwsl.mdsj.custom.RefreshView;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cxf on 2018/11/15.
 */

public class LiveGuardListActivity extends AbsActivity {

    public static void forward(Context context, String toUid) {
        Intent intent = new Intent(context, LiveGuardListActivity.class);
        intent.putExtra(Constants.TO_UID, toUid);
        context.startActivity(intent);
    }

    private RefreshView mRefreshView;
    private GuardAdapter mGuardAdapter;
    private String mToUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guard_list;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.guard_list));
        mToUid = getIntent().getStringExtra(Constants.TO_UID);
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mRefreshView = (RefreshView) findViewById(R.id.refreshView);
        boolean self = mToUid.equals(AppConfig.getInstance().getUid());
        mRefreshView.setNoDataLayoutId(self ? R.layout.view_no_data_guard_anc_2 : R.layout.view_no_data_guard_aud_2);
        mRefreshView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRefreshView.setDataHelper(new RefreshView.DataHelper<GuardUserBean>() {
            @Override
            public RefreshAdapter<GuardUserBean> getAdapter() {
                if (mGuardAdapter == null) {
                    mGuardAdapter = new GuardAdapter(mContext, false);
                }
                return mGuardAdapter;
            }

            @Override
            public void loadData(int p, HttpCallback callback) {
                HttpUtil.getGuardList(mToUid, p, callback);
            }

            @Override
            public List<GuardUserBean> processData(String[] info) {
                return JSON.parseArray(Arrays.toString(info), GuardUserBean.class);
            }

            @Override
            public void onRefresh(List<GuardUserBean> list) {

            }

            @Override
            public void onNoData(boolean noData) {

            }

            @Override
            public void onLoadDataCompleted(int dataCount) {

            }
        });
        mRefreshView.initData();
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancel(HttpConst.GET_GUARD_LIST);
        super.onDestroy();
    }
}
