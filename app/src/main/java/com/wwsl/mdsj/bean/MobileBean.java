package com.wwsl.mdsj.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor()
public class MobileBean {
    private String deviceId;
    private String deviceName;
    private String deviceBrand;
    private String tel;
    private String imei;
    private String imsi;
}
