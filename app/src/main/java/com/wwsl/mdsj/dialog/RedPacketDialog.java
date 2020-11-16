package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import cn.hutool.core.util.StrUtil;

public class RedPacketDialog extends BasePopupView implements View.OnClickListener {

    private TextView txNum;
    private TextView txStatus;
    private TextView txGet;
    private TextView temp_tv_1;
    private OnDialogCallBackListener listener;
    private String status;
    private String packetId;
    private String price;
    private ConstraintLayout openImg;
    private ImageView ivClose;
    private ImageView ivOpen;
    private ConstraintLayout openedLayout;
    private boolean isSelfSend;

    public RedPacketDialog(@NonNull Context context, boolean isSelfSend, String packetId, String price, String status, OnDialogCallBackListener listener) {
        super(context);
        this.listener = listener;
        this.price = price;
        this.packetId = packetId;
        this.isSelfSend = isSelfSend;
        this.status = status;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_chat_red_packet;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        txNum = findViewById(R.id.txNum);
        txStatus = findViewById(R.id.txStatus);
        temp_tv_1 = findViewById(R.id.temp_tv_1);
        txGet = findViewById(R.id.txGet);
        ivOpen = findViewById(R.id.ivOpen);
        openedLayout = findViewById(R.id.openedLayout);
        openImg = findViewById(R.id.openImg);
        ivClose = findViewById(R.id.ivClose);
        txGet.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ivOpen.setOnClickListener(this);

        txNum.setText(String.format("豆丁 x%s", price));
        switch (status) {
            case "0":
                if (isSelfSend) {
                    openImg.setVisibility(INVISIBLE);
                    openedLayout.setVisibility(VISIBLE);
                    txStatus.setVisibility(VISIBLE);
                    txGet.setVisibility(GONE);
                    temp_tv_1.setVisibility(GONE);
                    txStatus.setText("待领取");
                } else {
                    openImg.setVisibility(VISIBLE);
                    openedLayout.setVisibility(GONE);
                    txStatus.setVisibility(GONE);
                    txGet.setVisibility(VISIBLE);
                    temp_tv_1.setVisibility(VISIBLE);
                }
                break;
            case "1":
                openImg.setVisibility(INVISIBLE);
                openedLayout.setVisibility(VISIBLE);
                txGet.setVisibility(GONE);
                temp_tv_1.setVisibility(GONE);
                txStatus.setVisibility(VISIBLE);
                if (isSelfSend) {
                    txStatus.setText("已被领取");
                } else {
                    txStatus.setText("已领取");
                }
                break;
            case "2":
                openImg.setVisibility(INVISIBLE);
                openedLayout.setVisibility(VISIBLE);
                txGet.setVisibility(GONE);
                temp_tv_1.setVisibility(GONE);
                txStatus.setVisibility(VISIBLE);
                txStatus.setText("已过期");
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txGet:
            case R.id.ivClose:
                dismiss();
                break;
            case R.id.ivOpen:
                if (!StrUtil.isEmpty(packetId)) {
                    receiveRedPacket(packetId);
                }
                break;
        }
    }

    private void receiveRedPacket(String redPacketId) {
        HttpUtil.receiveChatRedPack(redPacketId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show("领取成功");
                    if (listener != null) {
                        openImg.setVisibility(INVISIBLE);
                        openedLayout.setVisibility(VISIBLE);
                        txGet.setVisibility(VISIBLE);
                        txStatus.setVisibility(GONE);
                        temp_tv_1.setVisibility(VISIBLE);
                        listener.onDialogViewClick(null, packetId);
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }
}
