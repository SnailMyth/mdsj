package com.wwsl.mdsj.dialog;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.lxj.xpopup.core.BasePopupView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.SpUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;
import com.wwsl.mdsj.views.dialog.address.ChooseAddressWheel;
import com.wwsl.mdsj.views.dialog.address.OnAddressChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HometownSetDialog extends BasePopupView {


    private Activity activity;
    private LinearLayout loadLayout;
    private ChooseAddressWheel cityDetailDialog;
    private ConstraintLayout root;
    private OnDialogCallBackListener listener;
    private List<PartnerCityBean> cityBeans;

    public HometownSetDialog(@NonNull Context context) {
        super(context);
    }

    public HometownSetDialog(@NonNull Context context, Activity activity, OnDialogCallBackListener listener) {
        super(context);
        this.activity = activity;
        this.listener = listener;
    }


    @Override
    protected int getPopupLayoutId() {
        return R.layout.dialog_set_hometown;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        root = findViewById(R.id.root);
        loadLayout = findViewById(R.id.loadLayout);
        OnAddressChangeListener addressChangeListener = (province, city, district, id) -> {
            if (listener != null) {
                StringBuilder content = new StringBuilder();
                if (!StringUtil.isEmpty(province)) {
                    content.append(province);
                }

                if (!StringUtil.isEmpty(city)) {
                    content.append("-").append(city);
                }

                if (!StringUtil.isEmpty(district)) {
                    content.append("-").append(district);
                }

                listener.onDialogViewClick(null, content.toString());
            }
        };

        cityBeans = AppConfig.getInstance().getCityBeans();


        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            dismiss();
        });

        findViewById(R.id.btnSet).setOnClickListener(v -> {
            if (null == cityDetailDialog) {
                if (cityBeans != null) {
                    cityDetailDialog = DialogUtil.getCityDetailDialog(activity, cityBeans, addressChangeListener);
                    cityDetailDialog.show(root);
                } else {
                    loadLayout.setVisibility(VISIBLE);
                    HttpUtil.getCityConfig("1", "1", new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            loadLayout.setVisibility(GONE);
                            if (code == 0) {
                                List<PartnerCityBean> tempData = JSON.parseArray(Arrays.toString(info), PartnerCityBean.class);
                                SpUtil.getInstance().setStringValue(SpUtil.CITY, JSON.toJSONString(cityBeans));
                                cityBeans = new ArrayList<>();
                                cityBeans.addAll(tempData);
                                AppConfig.getInstance().setCityBeans(cityBeans);
                                cityDetailDialog = DialogUtil.getCityDetailDialog(activity, cityBeans, addressChangeListener);
                                cityDetailDialog.show(root);
                            } else {
                                ToastUtil.show(msg);
                            }
                        }

                        @Override
                        public void onError() {
                            loadLayout.setVisibility(GONE);
                        }
                    });
                }
            } else {
                cityDetailDialog.show(root);
            }

        });
    }
}
