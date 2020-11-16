package com.wwsl.mdsj.views.dialog.address;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.PartnerCityBean;
import com.wwsl.mdsj.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;


public class ChooseAddressWheel implements MyOnWheelChangedListener, View.OnClickListener {


    private Button cancelButton;
    private Button confirmButton;
    private MyWheelView provinceWheel;
    private MyWheelView cityWheel;
    private MyWheelView districtWheel;
    private Activity context;
    private View parentView;
    private PopupWindow popupWindow = null;
    private WindowManager.LayoutParams layoutParams = null;
    private LayoutInflater layoutInflater = null;

    private List<PartnerCityBean> province = null;

    private OnAddressChangeListener onAddressChangeListener = null;
    private String areaId;

    public ChooseAddressWheel(Activity context) {
        this.context = context;
        init();
    }

    private void init() {
        layoutParams = context.getWindow().getAttributes();
        layoutInflater = context.getLayoutInflater();
        initView();
        initPopupWindow();
    }

    private void initView() {
        parentView = layoutInflater.inflate(R.layout.dialog_choose_city_layout, null);
        provinceWheel = parentView.findViewById(R.id.province_wheel);
        cityWheel = parentView.findViewById(R.id.city_wheel);
        districtWheel = parentView.findViewById(R.id.district_wheel);
        cancelButton = parentView.findViewById(R.id.cancel_button);
        confirmButton = parentView.findViewById(R.id.confirm_button);

        provinceWheel.setVisibleItems(7);
        cityWheel.setVisibleItems(7);
        districtWheel.setVisibleItems(7);

        provinceWheel.addChangingListener(this);
        cityWheel.addChangingListener(this);
        districtWheel.addChangingListener(this);
        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

    }

    private void initPopupWindow() {
        popupWindow = new PopupWindow(parentView, WindowManager.LayoutParams.MATCH_PARENT, (int) (CommonUtil.getScreenHeight(context) * (2.0 / 5)));
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setAnimationStyle(R.style.anim_push_bottom);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                layoutParams.alpha = 1.0f;
                context.getWindow().setAttributes(layoutParams);
                popupWindow.dismiss();
            }
        });
    }

    private void bindData() {
        provinceWheel.setViewAdapter(new ProvinceWheelAdapter(context, province));
        updateCity();
        updateDistrict();
    }

    @Override
    public void onChanged(MyWheelView wheel, int oldValue, int newValue) {
        if (wheel == provinceWheel) {
            updateCity();//省份改变后城市和地区联动
        } else if (wheel == cityWheel) {
            updateDistrict();//城市改变后地区联动
        } else if (wheel == districtWheel) {
        }
    }

    private void updateCity() {
        int index = provinceWheel.getCurrentItem();
        List<PartnerCityBean.ChildrenBeanX> citys = province.get(index).getChildren();
        if (citys != null && citys.size() > 0) {
            cityWheel.setViewAdapter(new CityWheelAdapter(context, citys));
            cityWheel.setCurrentItem(0);
            updateDistrict();
        } else {
            cityWheel.setViewAdapter(new CityWheelAdapter(context, new ArrayList<>()));
        }
    }

    private void updateDistrict() {
        int provinceIndex = provinceWheel.getCurrentItem();
        List<PartnerCityBean.ChildrenBeanX> citys = province.get(provinceIndex).getChildren();
        if (citys != null) {
            int cityIndex = cityWheel.getCurrentItem();
            List<PartnerCityBean.ChildrenBeanX.ChildrenBean> districts = citys.get(cityIndex).getChildren();
            if (districts != null && districts.size() > 0) {
                districtWheel.setViewAdapter(new AreaWheelAdapter(context, districts));
                districtWheel.setCurrentItem(0);
            } else {
                districtWheel.setViewAdapter(new AreaWheelAdapter(context, new ArrayList<>()));
            }
        }
    }

    public void show(View v) {
        layoutParams.alpha = 0.6f;
        context.getWindow().setAttributes(layoutParams);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    public void setProvince(List<PartnerCityBean> province) {
        this.province = province;
        bindData();
    }

    public void defaultValue(String provinceStr, String city, String arae) {
        if (TextUtils.isEmpty(provinceStr)) return;
        for (int i = 0; i < province.size(); i++) {
            PartnerCityBean provinces = province.get(i);
            if (provinces != null && provinces.getName().equalsIgnoreCase(provinceStr)) {
                provinceWheel.setCurrentItem(i);
                if (TextUtils.isEmpty(city)) return;
                List<PartnerCityBean.ChildrenBeanX> citys = provinces.getChildren();
                for (int j = 0; j < citys.size(); j++) {
                    PartnerCityBean.ChildrenBeanX cityEntity = citys.get(j);
                    if (cityEntity != null && cityEntity.getName().equalsIgnoreCase(city)) {
                        cityWheel.setViewAdapter(new CityWheelAdapter(context, citys));
                        cityWheel.setCurrentItem(j);
                        if (TextUtils.isEmpty(arae)) return;
                        List<PartnerCityBean.ChildrenBeanX.ChildrenBean> areas = cityEntity.getChildren();
                        for (int k = 0; k < areas.size(); k++) {
                            PartnerCityBean.ChildrenBeanX.ChildrenBean areaEntity = areas.get(k);
                            if (areaEntity != null && areaEntity.getName().equalsIgnoreCase(arae)) {
                                districtWheel.setViewAdapter(new AreaWheelAdapter(context, areas));
                                districtWheel.setCurrentItem(k);
                            }
                        }
                    }
                }
            }
        }
    }

    public void confirm() {
        if (onAddressChangeListener != null) {
            int provinceIndex = provinceWheel.getCurrentItem();
            int cityIndex = cityWheel.getCurrentItem();
            int areaIndex = districtWheel.getCurrentItem();

            String provinceName = null, cityName = null, areaName = null;

            List<PartnerCityBean.ChildrenBeanX> citys = null;
            if (province != null && province.size() > provinceIndex) {
                PartnerCityBean provinceEntity = province.get(provinceIndex);
                citys = provinceEntity.getChildren();
                provinceName = provinceEntity.getName();
            }

            List<PartnerCityBean.ChildrenBeanX.ChildrenBean> districts = null;
            if (citys != null && citys.size() > cityIndex) {
                PartnerCityBean.ChildrenBeanX cityEntity = citys.get(cityIndex);
                districts = cityEntity.getChildren();
                cityName = cityEntity.getName();
            }

            if (districts != null && districts.size() > areaIndex) {
                PartnerCityBean.ChildrenBeanX.ChildrenBean areaEntity = districts.get(areaIndex);
                areaName = areaEntity.getName();
                areaId = areaEntity.getId();
            }

            onAddressChangeListener.onAddressChange(provinceName, cityName, areaName, areaId);
        }
        cancel();
    }

    public void cancel() {
        popupWindow.dismiss();
    }

    public void setOnAddressChangeListener(OnAddressChangeListener onAddressChangeListener) {
        this.onAddressChangeListener = onAddressChangeListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancel_button) {
            cancel();
        }
        if (i == R.id.confirm_button) {
            confirm();
        }
    }
}
