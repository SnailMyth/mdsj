package com.wwsl.mdsj.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.me.user.UserApplyPartnerActivity;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.bean.PartnerRegionBean;
import com.wwsl.mdsj.bean.net.NetDaRenBean;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.views.dialog.OneChooseWheel;
import com.wwsl.mdsj.views.dialog.TwoChooseWheel;
import com.wwsl.mdsj.views.dialog.address.ChooseAddressWheel;
import com.wwsl.mdsj.views.dialog.address.OnAddressChangeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;

public class UserPartnerActivity extends BaseActivity {

    private ImageView ivQu;
    private ImageView ivShi;
    private ImageView ivSheng;
    private ImageView ivDaqu;
    private TextView tvTips;
    private final static String TAG = "UserPartnerActivity";
    private ChooseAddressWheel chooseAddressWheel;
    private TwoChooseWheel twoChooseWheel;
    private OneChooseWheel provinceChose;
    private OneChooseWheel daQuChooseWheel;
    private ConstraintLayout rootLayout;
    private int type = 0;
    private String address = "";
    private List<String> daQuList;
    private List<String> provinceList;
    private Map<String, List<String>> map;
    private List<PartnerRegionBean> regionBeans;
    private List<PartnerRegionBean> regionBeans1;
    private List<String> webUrl;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_partner;
    }

    @Override
    protected void init() {
        initView();
        webUrl = new ArrayList<>(4);
        loadWebUrl();
    }

    private void initView() {
        ivQu = findViewById(R.id.ivQu);
        ivShi = findViewById(R.id.ivShi);
        ivSheng = findViewById(R.id.ivSheng);
        ivDaqu = findViewById(R.id.ivDaqu);
        tvTips = findViewById(R.id.tvTips);
        rootLayout = findViewById(R.id.rootLayout);
        tvTips.setText(String.format("为了保护您的权力及有效的沟通,您在提交申请时需预支付%s元的意向金,审核不成功,会园路退回给您,审核成功后此意向金作为代理加盟费用使用,谢谢您的支持!", AppConfig.getInstance().getConfig().getApplyPartnerMoney()));
    }

    public void backClick(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRegion();//获取大区
        loadProvince();//获取省
    }

    private void loadWebUrl() {
        HttpUtil.getZbLevel(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<NetDaRenBean> beans = JSON.parseArray(Arrays.toString(info), NetDaRenBean.class);
                if (beans.size() != 8) return;
                for (int i = 4; i < 8; i++) {
                    webUrl.add(beans.get(i).getLink());
                }
            }
        });
    }

    //获取省
    private void loadProvince() {
        HttpUtil.getCityConfig("1", "", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    regionBeans = JSON.parseArray(Arrays.toString(info), PartnerRegionBean.class);
                    provinceList = new ArrayList<>();
                    for (PartnerRegionBean bean : regionBeans) {
                        provinceList.add(bean.getName());
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    //获取大区
    private void loadRegion() {
        HttpUtil.getCityConfig("-1", "", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    regionBeans1 = JSON.parseArray(Arrays.toString(info), PartnerRegionBean.class);
                    daQuList = new ArrayList<>();
                    for (PartnerRegionBean bean : regionBeans1) {
                        daQuList.add(bean.getName());
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    //选择省市区
    public void chooseQu(View view) {
        List<PartnerCityBean> cityBeans = AppConfig.getInstance().getCityBeans();
        if (cityBeans != null && cityBeans.size() > 0) {
            chooseAddressWheel = DialogUtil.getCityDetailDialog(this, cityBeans, new OnAddressChangeListener() {
                @Override
                public void onAddressChange(String province, String city, String district, String id) {
                    type = 1;
                    address = id;
                    goNext();
                }
            });
            chooseAddressWheel.show(rootLayout);
        }
    }

    //选择市
    public void chooseShi(View view) {
        List<PartnerCityBean> cityBeans = AppConfig.getInstance().getCityBeans();
        map = new HashMap<>();

        for (int i = 0; i < cityBeans.size(); i++) {
            List<String> temp = new ArrayList<>();
            List<PartnerCityBean.ChildrenBeanX> children = cityBeans.get(i).getChildren();
            if (children == null) continue;
            for (int j = 0; j < children.size(); j++) {
                temp.add(children.get(j).getName());
            }
            map.put(cityBeans.get(i).getName(), temp);
        }
        if (map.size() > 0) {
            twoChooseWheel = DialogUtil.getProvinceCityDialog(this, "请选择省市", map, new TwoChooseWheel.OnDataChoseListener() {
                @Override
                public void onAddressChange(String first, String sec) {
                    type = 2;
                    for (int i = 0; i < cityBeans.size(); i++) {

                        List<PartnerCityBean.ChildrenBeanX> children = cityBeans.get(i).getChildren();
                        if (children == null) continue;
                        for (int j = 0; j < children.size(); j++) {
                            if (children.get(j).getName().equals(sec)) {
                                address = children.get(j).getId();
                            }
                        }

                    }
                    goNext();
                }
            });
            twoChooseWheel.show(rootLayout);
        }
    }

    //选择大区
    public void chooseDaQu(View view) {

        if (daQuList != null && daQuList.size() > 0) {
            daQuChooseWheel = DialogUtil.getSingleCityDialog(this, "请选择大区", daQuList, new OneChooseWheel.OnDataChoseListener() {
                @Override
                public void onAddressChange(String first) {
                    type = 4;
                    for (int i = 0; i < regionBeans1.size(); i++) {
                        if (regionBeans1.get(i).getName().equals(first)) {
                            address = regionBeans1.get(i).getId();
                        }
                    }
                    goNext();
                }
            });

            daQuChooseWheel.show(rootLayout);
        }

    }

    //选择省
    public void chooseSheng(View view) {
        if (provinceList != null && provinceList.size() > 0) {
            provinceChose = DialogUtil.getSingleCityDialog(this, "请选择省市", provinceList, new OneChooseWheel.OnDataChoseListener() {
                @Override
                public void onAddressChange(String first) {
                    type = 3;
                    for (int i = 0; i < regionBeans.size(); i++) {
                        if (regionBeans.get(i).getName().equals(first)) {
                            address = regionBeans.get(i).getId();
                        }
                    }
                    goNext();
                }
            });
            provinceChose.show(rootLayout);
        }
    }

    public void goNext() {
        UserApplyPartnerActivity.forward(this, type, address);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, UserPartnerActivity.class);
        context.startActivity(intent);
    }


    public void showDes(View view) {
        switch (view.getId()) {
            case R.id.tvQues1:
                showWeb(0);
                break;
            case R.id.tvQues2:
                showWeb(1);
                break;
            case R.id.tvQues3:
                showWeb(2);
                break;
            case R.id.tvQues4:
                showWeb(3);
                break;
        }
    }

    public void showWeb(int position) {
        if (position >= 0 && position < webUrl.size()) {
            if (!StrUtil.isEmpty(webUrl.get(position))) {
                WebViewActivity.forward(this, webUrl.get(position));
            }
        }
    }
}
