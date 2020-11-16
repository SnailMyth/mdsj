package com.wwsl.mdsj.utils;

import android.os.Looper;

import com.frame.fire.util.LogUtils;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.bean.TxLocationBean;
import com.wwsl.mdsj.event.LocationEvent;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;

import org.greenrobot.eventbus.EventBus;

public class LocationUtil {
    private static final String TAG = "location";
    private static LocationUtil sInstance;
    private TencentLocationManager mLocationManager;
    private boolean mLocationStarted;
    private boolean mNeedPostLocationEvent;//是否需要发送定位成功事件

    private LocationUtil() {
        mLocationManager = TencentLocationManager.getInstance(AppContext.sInstance);
    }

    public static LocationUtil getInstance() {
        if (sInstance == null) {
            synchronized (LocationUtil.class) {
                if (sInstance == null) {
                    sInstance = new LocationUtil();
                }
            }
        }
        return sInstance;
    }


    private TencentLocationListener mLocationListener = new TencentLocationListener() {
        @Override
        public void onLocationChanged(TencentLocation location, int code, String reason) {
            if (code == TencentLocation.ERROR_OK) {
                double lng = location.getLongitude();//经度
                double lat = location.getLatitude();//纬度
                String nation = location.getNation();//国家
                String province = location.getProvince();//省
                String city = location.getCity();//市
                String district = location.getDistrict();//区
                String town = location.getTown();//镇
                String village = location.getVillage();//村
                String street = location.getStreet();//街道
                String streetNo = location.getStreetNo();//门号
                String name = location.getName();//名称
                String address = location.getAddress();//地址

                LogUtils.e(TAG, "获取定位成功------>" + location.toString());

                AppConfig.getInstance().setLocationInfo(
                        lng, lat, province, city, district);

                if (mNeedPostLocationEvent) {
                    EventBus.getDefault().post(new LocationEvent(lng, lat));
                }
            }
        }

        @Override
        public void onStatusUpdate(String s, int i, String s1) {
            LogUtils.e(TAG, "onStatusUpdate: ");
        }
    };


    //启动定位
    public void startLocation() {
        if (!mLocationStarted && mLocationManager != null) {
            mLocationStarted = true;
            LogUtils.e(TAG, "开启定位");
            TencentLocationRequest request = TencentLocationRequest
                    .create()
                    .setAllowGPS(true)
                    .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA)
                    .setInterval(30 * 60 * 1000);//1小时定一次位

            //当定位周期大于0时, 不论是否有得到新的定位结果, 位置监听器都会按定位周期定时被回调;
            // 当定位周期等于0时, 仅当有新的定位结果时, 位置监听器才会被回调(即, 回调时机存在不确定性).
            // 如果需要周期性回调, 建议将 定位周期 设置为 5000-10000ms
            mLocationManager.requestLocationUpdates(request, mLocationListener);
        }
    }

    //停止定位
    public void stopLocation() {
        HttpUtil.cancel(HttpConst.GET_LOCATION);
        if (mLocationStarted && mLocationManager != null) {
            L.e(TAG, "关闭定位");
            mLocationManager.removeUpdates(mLocationListener);
            mLocationStarted = false;
        }
    }

    public void setNeedPostLocationEvent(boolean needPostLocationEvent) {
        mNeedPostLocationEvent = needPostLocationEvent;
    }

    public void getSingleLocation(TencentLocationListener mLocationListener) {
        mLocationManager.requestSingleFreshLocation(null, mLocationListener, Looper.getMainLooper());
    }

}
