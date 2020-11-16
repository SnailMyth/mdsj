package com.wwsl.mdsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.XPopup;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.UserPartnerActivity;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.MadelBean;
import com.wwsl.mdsj.bean.net.NetDaRenBean;
import com.wwsl.mdsj.bean.net.NetPartnerDetailBean;
import com.wwsl.mdsj.dialog.H5DialogFragment;
import com.wwsl.mdsj.dialog.PartnerDetailDialog;
import com.wwsl.mdsj.dialog.SimpleCenterDialog;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class MadelWallActivity extends BaseActivity implements OnDialogCallBackListener {
    private SwipeRecyclerView madelRecycler;
    private TextView tvTip;
    private MadelAdapter adapter;
    private List<MadelBean> data;
    private int[] greyRes = new int[]{R.mipmap.icon_madel_grey_1, R.mipmap.icon_madel_grey_2, R.mipmap.icon_madel_grey_3,
            R.mipmap.icon_madel_grey_4, R.mipmap.icon_madel_grey_5, R.mipmap.icon_madel_grey_6, R.mipmap.icon_madel_grey_7
            , R.mipmap.icon_madel_grey_8};
    private int[] lightRes = new int[]{R.mipmap.icon_madel_light_1, R.mipmap.icon_madel_light_2, R.mipmap.icon_madel_light_3,
            R.mipmap.icon_madel_light_4, R.mipmap.icon_madel_light_5, R.mipmap.icon_madel_light_6, R.mipmap.icon_madel_light_7
            , R.mipmap.icon_madel_light_8};

    @Override
    protected int setLayoutId() {
        return R.layout.activity_madel_wall;
    }

    @Override
    protected void init() {
        madelRecycler = findViewById(R.id.madelRecycler);
        tvTip = findViewById(R.id.tvTip);
        data = new ArrayList<>();
        data.add(MadelBean.builder().id(5).name("新秀助播").enableUrl(lightRes[0]).disableUrl(greyRes[0]).enable(false).build());
        data.add(MadelBean.builder().id(6).name("人气助播").enableUrl(lightRes[1]).disableUrl(greyRes[1]).enable(false).build());
        data.add(MadelBean.builder().id(7).name("网红助播").enableUrl(lightRes[2]).disableUrl(greyRes[2]).enable(false).build());
        data.add(MadelBean.builder().id(8).name("明星助播").enableUrl(lightRes[3]).disableUrl(greyRes[3]).enable(false).build());
        data.add(MadelBean.builder().id(1).name("区县代理").enableUrl(lightRes[4]).disableUrl(greyRes[4]).enable(false).build());
        data.add(MadelBean.builder().id(2).name("市级代理").enableUrl(lightRes[5]).disableUrl(greyRes[5]).enable(false).build());
        data.add(MadelBean.builder().id(3).name("省级代理").enableUrl(lightRes[6]).disableUrl(greyRes[6]).enable(false).build());
        data.add(MadelBean.builder().id(4).name("大区代理").enableUrl(lightRes[7]).disableUrl(greyRes[7]).enable(false).build());

        adapter = new MadelAdapter(data);
        madelRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        madelRecycler.setAutoLoadMore(false);
        madelRecycler.loadMoreFinish(false, false);
        madelRecycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                showDetailDialog(data.get(position), MadelWallActivity.this);
            }
        });
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                String url = data.get(position).getUrl();
                if (StrUtil.isEmpty(url)) return;
                H5DialogFragment fragment = new H5DialogFragment();
                fragment.setUrl(url);
                fragment.show(getSupportFragmentManager(), "H5DialogFragment");
            }
        });
        loadData();
    }

    private void loadData() {
        HttpUtil.getApplyUserPartner(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<NetPartnerDetailBean> beans = JSON.parseArray(Arrays.toString(info), NetPartnerDetailBean.class);
                    if (beans.size() > 0) {
                        NetPartnerDetailBean bean = beans.get(0);
                        if (bean.getState().equals("0")) {
                            tvTip.setText(String.format("当前正在申请:%s--%s", bean.getTypename(), bean.getAddress()));
                            tvTip.setVisibility(View.VISIBLE);
                        } else if (bean.getState().equals("2")) {
                            tvTip.setText(String.format("审核未通过,原因%s,请重新提交", bean.getReason()));
                            tvTip.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        HttpUtil.getZbLevel(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<NetDaRenBean> beans = JSON.parseArray(Arrays.toString(info), NetDaRenBean.class);
                if (beans.size() != data.size()) return;
                for (int i = 0; i < beans.size(); i++) {
                    data.get(i).setEnable(beans.get(i).getStatus() == 1);
                    data.get(i).setUrl(beans.get(i).getLink());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showDetailDialog(MadelBean madelBean, OnDialogCallBackListener listener) {
        if (madelBean.getId() > 4) return;
        new XPopup.Builder(this)
                .hasShadowBg(false)
                .dismissOnTouchOutside(true)
                .customAnimator(new DialogUtil.DialogAnimator())
                .asCustom(new PartnerDetailDialog(this, madelBean.getName(), madelBean.getId(), listener)).show();
    }

    public void backClick(View view) {
        finish();
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MadelWallActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onDialogViewClick(View view, Object object) {
        UserPartnerActivity.forward(this);
        finish();
    }


    static class MadelAdapter extends BaseQuickAdapter<MadelBean, BaseViewHolder> {

        MadelAdapter(@Nullable List<MadelBean> data) {
            super(R.layout.item_madel, data);
            addChildClickViewIds(R.id.tvQues);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder holder, MadelBean madelBean) {
            holder.setText(R.id.tvTitle, madelBean.getName());
            int url = madelBean.isEnable() ? madelBean.getEnableUrl() : madelBean.getDisableUrl();
            ImgLoader.display(url, holder.getView(R.id.ivMadel));
        }
    }

}
