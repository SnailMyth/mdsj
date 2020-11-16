package com.wwsl.mdsj.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.maodou.NetMdStallListBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class MdEditDialog extends BasePopupView implements View.OnClickListener, View.OnLongClickListener {

    private OnDialogCallBackListener listener;
    private TextView tvTitle;
    private EditText etSaleNum;
    private TextView saleLabel;
    private EditText etSalePrice;
    private TextView tvSaleTotal;
    private ConstraintLayout priceLayout;
    private ImageView tvUploadQr;
    private ConstraintLayout saleLayout;
    private TextView tvBuyNum;
    private TextView tvUnitPrice;
    private TextView buyTotal;
    private ImageView ivBuyQr;
    private ConstraintLayout buyLayout;
    private ImageView ivDone;
    private TextView ivClose;
    private TextView tvUpload;
    private int type = 0;
    private NetMdStallListBean data;
    private String qrUrl;


    public MdEditDialog(@NonNull Context context) {
        super(context);
    }

    public MdEditDialog(@NonNull Context context, OnDialogCallBackListener listener, int type, NetMdStallListBean saleBean) {
        super(context);
        this.listener = listener;
        this.type = type;
        data = saleBean;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_md_edit;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        findView();
        initView();
        initListener();
    }

    private void initView() {
        if (type == TYPE_SALE) {
            tvTitle.setText("我有毛豆");
            saleLayout.setVisibility(VISIBLE);
            buyLayout.setVisibility(GONE);
            ivDone.setBackgroundResource(R.mipmap.icon_md_on_sale);
            HttpUtil.getTodayPrice(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 200) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        String todayPrice = obj.getString("today_price");
                        AppConfig.getInstance().setTodayPrice(todayPrice);
                        saleLabel.setText(String.format("当前毛豆市场价格：%sR/个", todayPrice));
                    }
                }
            });

        } else {
            tvTitle.setText("我要毛豆");
            saleLayout.setVisibility(GONE);
            buyLayout.setVisibility(VISIBLE);
            ImgLoader.display(data.getImgUrl(), R.mipmap.icon_default_image_mini, R.mipmap.icon_default_image_mini, ivBuyQr);
            tvBuyNum.setText(String.format("毛豆数量：%s个", data.getNumber()));
            ivDone.setBackgroundResource(R.mipmap.icon_md_subscribe);
            tvUnitPrice.setText(String.format("%sR/个", data.getPrice()));

            if (!StrUtil.isEmpty(data.getNumber()) && !StrUtil.isEmpty(data.getPrice())) {
                BigDecimal price = new BigDecimal(data.getPrice());
                BigDecimal num = new BigDecimal(data.getNumber());
                BigDecimal bigDecimal = price.multiply(num).setScale(2, BigDecimal.ROUND_DOWN);
                buyTotal.setText(String.format("合计:%s", bigDecimal.toString()));
            }
        }


    }

    private void initListener() {
        etSalePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtil.isInteger(s.toString())) return;
                BigDecimal price = new BigDecimal(s.toString());
                String sNum = etSaleNum.getText().toString().trim();
                if (!StrUtil.isEmpty(sNum)) {
                    BigDecimal num = new BigDecimal(sNum);
                    BigDecimal bigDecimal = price.multiply(num).setScale(2, BigDecimal.ROUND_DOWN);
                    tvSaleTotal.setText(String.format("合计:%s", bigDecimal.toString()));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etSaleNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!StringUtil.isInteger(s.toString())) return;
                BigDecimal num = new BigDecimal(s.toString());
                String sPrice = etSalePrice.getText().toString().trim();
                if (!StrUtil.isEmpty(sPrice) && StringUtil.isInteger(sPrice)) {
                    BigDecimal price = new BigDecimal(sPrice);
                    BigDecimal bigDecimal = price.multiply(num).setScale(2, BigDecimal.ROUND_DOWN);
                    tvSaleTotal.setText(String.format("合计:%s", bigDecimal.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvUploadQr.setOnClickListener(this);
        ivDone.setOnClickListener(this);
        ivBuyQr.setOnLongClickListener(this);

        ivClose.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void findView() {
        tvTitle = findViewById(R.id.tvTitle);
        etSaleNum = findViewById(R.id.etSaleNum);
        saleLabel = findViewById(R.id.saleLabel);
        etSalePrice = findViewById(R.id.etSalePrice);
        tvSaleTotal = findViewById(R.id.tvSaleTotal);
        priceLayout = findViewById(R.id.priceLayout);
        tvUploadQr = findViewById(R.id.tvUploadQr);
        saleLayout = findViewById(R.id.saleLayout);
        tvUpload = findViewById(R.id.temp_tv_upload);
        tvBuyNum = findViewById(R.id.tvBuyNum);
        tvUnitPrice = findViewById(R.id.tvUnitPrice);
        buyTotal = findViewById(R.id.buyTotal);
        ivBuyQr = findViewById(R.id.ivBuyQr);
        buyLayout = findViewById(R.id.buyLayout);
        ivDone = findViewById(R.id.ivDone);
        ivClose = findViewById(R.id.ivClose);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvUploadQr:
                if (listener != null) {
                    listener.onDialogViewClick(null, 0);
                }
                break;
            case R.id.ivDone:
                done();
                break;
        }
    }

    private void done() {
        if (type == TYPE_SALE) {
            if (StrUtil.isEmpty(qrUrl)) {
                ToastUtil.show("请上传二维码图片");
                return;
            }
            if (listener != null) {
                HashMap<String, String> data = new HashMap<>();
                data.put("num", etSaleNum.getText().toString().trim());
                data.put("price", etSalePrice.getText().toString().trim());
                data.put("qrUrl", qrUrl);
                listener.onDialogViewClick(null, data);
            }
        } else {
            if (listener != null) {
                listener.onDialogViewClick(null, data);
            }
        }
        dismiss();
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.ivBuyQr) {
            if (listener != null) {
                listener.onDialogViewClick(v, 1);
            }
        }
        return false;
    }

    public void loadQrImage(String path) {
        tvUpload.setVisibility(GONE);
        qrUrl = path;
        ImgLoader.display(path, tvUploadQr);
    }

    public static final int TYPE_SALE = 0;
    public static final int TYPE_BUY = 1;

    @Override
    protected void onDismiss() {
        super.onDismiss();
        etSaleNum.setText("");
        etSalePrice.setText("");
        tvSaleTotal.setText("合计:");
        tvUpload.setVisibility(VISIBLE);
        tvUploadQr.setImageResource(R.drawable.bg_upload_qr);
    }
}
