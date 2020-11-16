package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.lxj.xpopup.core.BasePopupView;
import com.rey.material.widget.TextView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import cn.hutool.core.util.StrUtil;

public class InputInviteDialog extends BasePopupView {


    private EditText etCode;

    public InputInviteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_input_invate;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        etCode = findViewById(R.id.etInvitedCode);

        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            String content = etCode.getText().toString().trim();
            HttpUtil.setInvitation(content, new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        AppConfig.getInstance().getUserBean().setIsHaveCode("1");
                        ToastUtil.show("绑定成功");
                        dismiss();
                    } else {
                        ToastUtil.show(msg);
                    }
                }
            });
        });
        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            dismiss();
        });

    }
}
