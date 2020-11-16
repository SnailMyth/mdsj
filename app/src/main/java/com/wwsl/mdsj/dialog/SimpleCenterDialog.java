package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lxj.xpopup.core.BasePopupView;
import com.rey.material.widget.TextView;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import cn.hutool.core.util.StrUtil;

public class SimpleCenterDialog extends BasePopupView {

    private ImageView ivBg;
    private TextView textView;
    private int imgId = -1;
    private String imgRes;
    private String content;
    private int imgHeight = 0;
    private int imgWidth = 0;
    private int marginTop = 0;
    private int marginBottom = 0;
    private int marginStart = 0;
    private int marginEnd = 0;
    private OnDialogCallBackListener listener;

    public SimpleCenterDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * @param context
     * @param imgRes
     * @param imgWith   0 表示填充整个窗口
     * @param imgHeight
     * @param content
     */
    public SimpleCenterDialog(@NonNull Context context, int imgRes, int imgWith, int imgHeight, String content) {
        super(context);
        this.imgWidth = imgWith;
        this.imgHeight = imgHeight;
        this.content = content;
        this.imgId = imgRes;
    }

    public SimpleCenterDialog(@NonNull Context context, String imgRes, int imgWith, int imgHeight, String content) {
        super(context);
        this.imgWidth = imgWith;
        this.imgHeight = imgHeight;
        this.content = content;
        this.imgRes = imgRes;
    }

    public SimpleCenterDialog(@NonNull Context context, String imgRes, int imgWith, int imgHeight,
                              int marginTop, int marginBottom, int marginStart, int marginEnd,
                              String content, OnDialogCallBackListener listener) {
        super(context);
        this.imgWidth = imgWith;
        this.imgHeight = imgHeight;
        this.content = content;
        this.imgRes = imgRes;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.marginStart = marginStart;
        this.marginEnd = marginEnd;
    }

    public void setListener(OnDialogCallBackListener listener) {
        this.listener = listener;
    }

    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_simple_center;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        ivBg = findViewById(R.id.iv_bg);
        textView = findViewById(R.id.content);
        if (imgId > 0) {
            ivBg.setBackgroundResource(imgId);
        } else if (!StrUtil.isEmpty(imgRes)) {
            ImgLoader.display(imgRes, ivBg);
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivBg.getLayoutParams();
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        if (imgWidth != 0) {
            layoutParams.width = DpUtil.dp2px(imgWidth);
        } else {
            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT;
        }

        if (imgHeight != 0) {
            layoutParams.height = DpUtil.dp2px(imgHeight);
        } else {
            layoutParams.height = DpUtil.dp2px(200);
        }

        if (marginTop != 0) {
            layoutParams.topMargin = DpUtil.dp2px(marginTop);
        }
        if (marginBottom != 0) {
            layoutParams.bottomMargin = DpUtil.dp2px(marginBottom);
        }
        if (marginStart != 0) {
            layoutParams.leftMargin = DpUtil.dp2px(marginStart);
        }
        if (marginEnd != 0) {
            layoutParams.rightMargin = DpUtil.dp2px(marginEnd);
        }


        ivBg.setLayoutParams(layoutParams);

        if (!StrUtil.isEmpty(content)) {
            textView.setText(content);
        }
        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            if (listener != null) {
                listener.onDialogViewClick(null, null);
            }
            dismiss();
        });
    }

    public void setImgMargin(int top, int left, int bottom, int right) {
        if (null == ivBg) return;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivBg.getLayoutParams();
        if (top != 0) {
            layoutParams.topMargin = DpUtil.dp2px(top);
        }

        if (left != 0) {
            layoutParams.leftMargin = DpUtil.dp2px(left);
        }

        if (bottom != 0) {
            layoutParams.bottomMargin = DpUtil.dp2px(bottom);
        }

        if (right != 0) {
            layoutParams.rightMargin = DpUtil.dp2px(right);
        }
        ivBg.setLayoutParams(layoutParams);
    }
}
