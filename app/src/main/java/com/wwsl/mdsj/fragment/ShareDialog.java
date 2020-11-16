package com.wwsl.mdsj.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.ShareBean;
import com.wwsl.mdsj.bean.net.NetFriendBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ButtonUtils;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.views.BaseBottomSheetDialog;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * create by libo
 * create on 2020-05-25
 * description 分享弹框
 */
public class ShareDialog extends BaseBottomSheetDialog {
    RecyclerView rvPrivateLetter;
    RecyclerView rvShare;
    TextView btnCancel;
    private PrivateLetterAdapter privateLetterAdapter;
    private ShareAdapter shareAdapter;
    private View view;
    private ArrayList<ShareBean> shareBeans = new ArrayList<>();
    private int mPage = 1;
    private OnDialogCallBackListener listener;
    private List<NetFriendBean> friendBeans = new ArrayList<>();
    private int type = 0;//0 视频 1 文字
    private String videoUid;//视频用户id


    public ShareDialog() {

    }

    public ShareDialog(String videoUid) {
        this.videoUid = videoUid;
    }

    public ShareDialog(int type) {
        this.type = type;
    }

    public void setListener(OnDialogCallBackListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_share, container);
        rvPrivateLetter = view.findViewById(R.id.rv_private_letter);
        rvShare = view.findViewById(R.id.rv_share);
        btnCancel = view.findViewById(R.id.btnCancel);
        init();
        return view;
    }

    private void init() {

        friendBeans.clear();
        rvPrivateLetter.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        privateLetterAdapter = new PrivateLetterAdapter(new ArrayList<>());
        rvPrivateLetter.setAdapter(privateLetterAdapter);

        rvShare.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        shareAdapter = new ShareAdapter(shareBeans);
        rvShare.setAdapter(shareAdapter);

        initData();
        initListener();

    }

    private void initListener() {
        shareAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (!ButtonUtils.isFastClick()) {
                ShareBean shareBean = shareBeans.get(position);
                if (listener != null) {
                    dismiss();
                    listener.onDialogViewClick(null, shareBean);
                }
            }
        });

        privateLetterAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (listener != null) {
                    listener.onDialogViewClick(null, friendBeans.get(position));
                }
            }
        });

        btnCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void initData() {
        shareBeans.clear();
        if (type == 0) {
            shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_circle).text("朋友圈").bgRes(R.color.color_wechat_iconbg)
                    .type(Constants.WEIXIN_CIRCLE).build());

            shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_wxchat).text("微信").bgRes(R.color.color_wechat_iconbg)
                    .type(Constants.WEIXIN).build());


            if (!StrUtil.isEmpty(videoUid) && AppConfig.getInstance().getUid().equals(videoUid)) {
                shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_delete).text("删除视频").bgRes(R.color.color_qq_iconbg)
                        .type(Constants.DELETE_VIDEO).build());
            } else {
                shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_share_video_report).text("举报").bgRes(R.color.color_qq_iconbg)
                        .type(Constants.VIDEO_REPORT).build());
            }

            shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_download).text("保存本地").bgRes(R.color.color_qq_iconbg)
                    .type(Constants.SAVE_LOCAL).build());

        } else if (type == 1) {
            shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_circle).text("朋友圈").bgRes(R.color.color_wechat_iconbg)
                    .type(Constants.WEIXIN_CIRCLE).build());

            shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_wxchat).text("微信").bgRes(R.color.color_wechat_iconbg)
                    .type(Constants.WEIXIN).build());
        }

        shareAdapter.notifyDataSetChanged();

        if (type == 0) {
            HttpUtil.getFriendsList(AppConfig.getInstance().getUid(), mPage, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<NetFriendBean> netFansBeans = JSON.parseArray(Arrays.toString(info), NetFriendBean.class);
                        friendBeans.addAll(netFansBeans);
                        privateLetterAdapter.setNewInstance(netFansBeans);
                    }
                }
            });
        } else if (type == 1) {
            rvPrivateLetter.setVisibility(View.GONE);
        }


    }

    public void updateAction(String uid) {
        this.videoUid = uid;

        if (null != shareAdapter) {
            shareBeans.clear();
            shareBeans.add(ShareBean.builder().iconRes(R.string.icon_friends).text("朋友圈").bgRes(R.color.color_wechat_iconbg)
                    .type(Constants.WEIXIN_CIRCLE).build());

            shareBeans.add(ShareBean.builder().iconRes(R.string.icon_wechat).text("微信").bgRes(R.color.color_wechat_iconbg)
                    .type(Constants.WEIXIN).build());

            if (!StrUtil.isEmpty(videoUid) && AppConfig.getInstance().getUid().equals(videoUid)) {
                shareBeans.add(ShareBean.builder().iconRes(R.string.icon_delete).text("删除视频").bgRes(R.color.color_qq_iconbg)
                        .type(Constants.DELETE_VIDEO).build());
            } else {
                shareBeans.add(ShareBean.builder().iconRes(R.mipmap.icon_share_video_report).text("举报").bgRes(R.color.color_qq_iconbg)
                        .type(Constants.VIDEO_REPORT).build());
            }


            shareBeans.add(ShareBean.builder().iconRes(R.string.icon_save_local).text("保存本地").bgRes(R.color.color_qq_iconbg)
                    .type(Constants.SAVE_LOCAL).build());

            shareAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected int getHeight() {
        return DpUtil.dp2px(getContext(), 355);
    }
}
