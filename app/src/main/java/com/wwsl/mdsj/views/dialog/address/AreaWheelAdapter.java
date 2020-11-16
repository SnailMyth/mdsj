
package com.wwsl.mdsj.views.dialog.address;

import android.content.Context;

import com.wwsl.mdsj.bean.PartnerCityBean;

import java.util.List;

/**
 * 区域适配器
 */
public class AreaWheelAdapter extends BaseWheelAdapter<PartnerCityBean.ChildrenBeanX.ChildrenBean> {
    public AreaWheelAdapter(Context context, List<PartnerCityBean.ChildrenBeanX.ChildrenBean> list) {
        super(context,list);
    }

    @Override
    protected CharSequence getItemText(int index) {
        PartnerCityBean.ChildrenBeanX.ChildrenBean data = getItemData(index);
        if (data != null) {
            return data.getName();
        }
        return null;
    }
}
