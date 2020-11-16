package com.wwsl.mdsj.activity.maodou;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.lxj.xpopup.XPopup;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.maodou.MwTaskAdapter;
import com.wwsl.mdsj.base.BaseFragment;
import com.wwsl.mdsj.bean.maodou.NetMwTaskBean;
import com.wwsl.mdsj.dialog.MwDetailDialog;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.InputPwdDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * @author :
 * @date : 2020/7/8 18:45
 * @description : 任务秘籍
 */
public class MingWenTaskFragment extends BaseFragment {


    private SwipeRecyclerView recycler;
    private SmartRefreshLayout refreshLayout;
    private MwTaskAdapter adapter;
    private List<NetMwTaskBean> data;

    public static MingWenTaskFragment newInstance() {
        return new MingWenTaskFragment();
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_ming_wen_task;
    }

    @Override
    protected void init() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setOnRefreshListener(this::refreshData);
        data = new ArrayList<>();
        adapter = new MwTaskAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                new XPopup.Builder(getContext())
                        .hasShadowBg(true)
                        .customAnimator(new DialogUtil.DialogAnimator())
                        .asCustom(new InputPwdDialog(getContext(), String.format("将要扣除%s毛豆", data.get(position).getPrice()), new OnDialogCallBackListener() {
                            @Override
                            public void onDialogViewClick(View view, Object object) {
                                String pwd = (String) object;
                                buyMwTask(position, pwd);
                            }
                        }))
                        .show();
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                new XPopup.Builder(getContext())
                        .hasShadowBg(false)
                        .customAnimator(new DialogUtil.DialogAnimator())
                        .asCustom(new MwDetailDialog(Objects.requireNonNull(getContext()), data.get(position)))
                        .show();
            }
        });

    }

    private void refreshData(RefreshLayout refreshLayout) {
        loadData();
    }

    private void buyMwTask(int position, String pwd) {

        HttpUtil.buyMwTask(AppConfig.getInstance().getUserBean().getMobile(), data.get(position).getId(), pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    //购买成功
                    String i = String.valueOf(Integer.parseInt(adapter.getData().get(position).getProductCount()) + 1);
                    adapter.getData().get(position).setProductCount(i);
                    adapter.notifyItemChanged(position, MwTaskAdapter.PAYLOAD_BUY_MW);
                }

                ToastUtil.show(msg);
            }
        });
    }

    @Override
    protected void initialData() {
        refreshLayout.autoRefresh();
    }

    public void loadData() {
        HttpUtil.getAllMwTask(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    data.clear();
                    List<NetMwTaskBean> netMwTaskBeans = JSON.parseArray(Arrays.toString(info), NetMwTaskBean.class);
                    data.addAll(netMwTaskBeans);
                    adapter.setNewInstance(netMwTaskBeans);
                }
                refreshLayout.finishRefresh();
            }

            @Override
            public void onError() {
                refreshLayout.finishRefresh();
            }
        });
    }
}
