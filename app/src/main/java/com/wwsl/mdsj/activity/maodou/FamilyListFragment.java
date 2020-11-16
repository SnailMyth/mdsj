package com.wwsl.mdsj.activity.maodou;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserHomePageActivity;
import com.wwsl.mdsj.adapter.maodou.FamilyActiveAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.maodou.FamilyActiveBean;
import com.wwsl.mdsj.bean.maodou.NetFamilyActiveBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/8/3 16:20
 * @description : FamilyListFragment
 */
public class FamilyListFragment extends BaseFragment implements SwipeRecyclerView.LoadMoreListener {

    private int type;
    private ConstraintLayout recyclerContainer;
    private FamilyActiveAdapter mAdapter;
    private SwipeRecyclerView recycler;
    private List<FamilyActiveBean> beans;
    private int mPage;

    public FamilyListFragment() {
        // Required empty public constructor
    }


    public static FamilyListFragment newInstance(int type) {
        FamilyListFragment fragment = new FamilyListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_family_list;
    }

    @Override
    protected void init() {
        initView();
        initBaseData();
    }

    private void initBaseData() {

        beans = new ArrayList<>();
        mAdapter = new FamilyActiveAdapter(new ArrayList<>());

        mAdapter.setOnItemChildClickListener((mAdapter, view, position) -> {
            String phone = this.mAdapter.getData().get(position).getPhone();
            if (!StrUtil.isEmpty(phone)) {
                PermissionX.init(getActivity())
                        .permissions(Manifest.permission.CALL_PHONE)
                        .request((allGranted, grantedList, deniedList) -> {
                            if (allGranted) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse("tel:" + phone));
                                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                                    startActivity(intent);
                                }
                            } else {
                                ToastUtil.show("未授权无法拨打电话");
                            }
                        });
            }
        });

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            FamilyActiveBean bean = mAdapter.getData().get(position);
            UserHomePageActivity.forward(getContext(), bean.getId());
        });

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.useDefaultLoadMore();
        recycler.setLoadMoreListener(this);
        recycler.setAdapter(mAdapter);
        mAdapter.setEmptyView(CommonUtil.getEmptyView("暂无家族成员", getContext(), recyclerContainer));
    }

    private void initView() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        recyclerContainer = (ConstraintLayout) findViewById(R.id.recyclerContainer);
    }

    @Override
    protected void initialData() {
        mPage = 1;
        loadData(true);
    }


    private void loadData(boolean isFresh) {
        if (type == 0) {
            HttpUtil.getFamilyIdentify(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, isFresh);
                }
            });
        } else {
            HttpUtil.getFamilyNoIdentify(mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    loadBack(code, msg, info, isFresh);
                }
            });
        }
    }

    private void loadBack(int code, String msg, String[] info, boolean isFresh) {
        if (code == 200) {
            List<NetFamilyActiveBean> netBeans = JSON.parseArray(Arrays.toString(info), NetFamilyActiveBean.class);
            List<FamilyActiveBean> parse = NetFamilyActiveBean.parse(netBeans);
            if (isFresh) {
                beans.clear();
                beans.addAll(parse);
                mAdapter.setNewInstance(parse);
                recycler.loadMoreFinish(parse.size() == 0, true);
            } else {
                beans.addAll(parse);
                mAdapter.addData(parse);
                recycler.loadMoreFinish(false, true);
            }

        } else {
            ToastUtil.show(msg);
            recycler.loadMoreFinish(false, false);
        }
    }

    @Override
    public void onLoadMore() {
        mPage++;
        loadData(false);
    }
}
