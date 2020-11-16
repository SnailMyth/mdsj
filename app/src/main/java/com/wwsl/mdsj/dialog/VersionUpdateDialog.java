package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

public class VersionUpdateDialog extends BasePopupView implements View.OnClickListener {

    private String content;
    private String version;
    private TextView txCancel;
    private TextView txDone;
    private TextView txContent;
    private TextView txVersion;
    private OnDialogCallBackListener listener;
    private boolean isCancelable = true;

    public VersionUpdateDialog(@NonNull Context context, boolean cancelable, String version, String content, OnDialogCallBackListener listener) {
        super(context);
        this.content = content;
        this.listener = listener;
        this.version = version;
        this.isCancelable = cancelable;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_version_update;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        txContent = findViewById(R.id.txContent);
        txCancel = findViewById(R.id.txCancel);
        txCancel.setVisibility(isCancelable ? VISIBLE : INVISIBLE);
        txDone = findViewById(R.id.txDone);
        txVersion = findViewById(R.id.txVersion);
        txContent.setText(content);
        txVersion.setText(version);
        txCancel.setOnClickListener(this);
        txDone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txCancel:
                dismiss();
                if (listener != null) {
                    listener.onDialogViewClick(null, 0);
                }
                break;
            case R.id.txDone:
                dismiss();
                if (listener != null) {
                    listener.onDialogViewClick(null, 1);
                }
                break;
        }
    }
}
