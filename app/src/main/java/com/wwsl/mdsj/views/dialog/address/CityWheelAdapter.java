
package com.wwsl.mdsj.views.dialog.address;

import android.content.Context;

import com.wwsl.mdsj.bean.PartnerCityBean;

import java.util.List;

/**
 * 城市适配器
 */
public class CityWheelAdapter extends BaseWheelAdapter<PartnerCityBean.ChildrenBeanX> {
    public CityWheelAdapter(Context context, List<PartnerCityBean.ChildrenBeanX> list) {
        super(context, list);
    }

    @Override
    protected CharSequence getItemText(int index) {
        PartnerCityBean.ChildrenBeanX itemData = getItemData(index);
        if (itemData != null) {
            return itemData.getName();
        }
        return null;
    }
}
