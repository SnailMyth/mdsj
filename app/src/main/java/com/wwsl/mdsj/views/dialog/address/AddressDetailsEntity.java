package com.wwsl.mdsj.views.dialog.address;

import java.util.List;

/**
 * 地址详细实体类
 */
public class AddressDetailsEntity {
    public int SellerId;
    public int OrderId;
    public String RecipientName;
    public String Mobile;
    public String ZipCode;
    public String Province;
    public String City;
    public String Area;
    public String Address;
    public Data ProvinceItems;

    public class Data {
        public List<ProvinceEntity> Province;
    }

    public class ProvinceEntity {
        public String Name;
        public List<CityEntity> City;

        public class CityEntity {
            public String Name;
            public List<AreaEntity> Area;
        }
        public class AreaEntity {
            public String Name;
        }
    }
}
