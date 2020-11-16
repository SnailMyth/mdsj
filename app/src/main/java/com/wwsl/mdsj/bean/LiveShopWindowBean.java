package com.wwsl.mdsj.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.wwsl.mdsj.bean.net.NetGoodsBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor()
@NoArgsConstructor
@ToString
public class LiveShopWindowBean implements Parcelable {

    private int showType;//0我的橱窗 1.直播橱窗
    private String id;
    private String title;
    private String thumb;//sales
    private boolean isCollect;
    private boolean isAdd;
    private String saleNum;
    private String totalNum;//库存
    private String price;
    private String gainMoney;
    private String oldPrice;
    private String webUrl;//浏览地址

    protected LiveShopWindowBean(Parcel in) {
        showType = in.readInt();
        id = in.readString();
        title = in.readString();
        thumb = in.readString();
        isCollect = in.readByte() != 0;
        isAdd = in.readByte() != 0;
        saleNum = in.readString();
        totalNum = in.readString();
        price = in.readString();
        gainMoney = in.readString();
        oldPrice = in.readString();
        webUrl = in.readString();
    }

    public static final Creator<LiveShopWindowBean> CREATOR = new Creator<LiveShopWindowBean>() {
        @Override
        public LiveShopWindowBean createFromParcel(Parcel in) {
            return new LiveShopWindowBean(in);
        }

        @Override
        public LiveShopWindowBean[] newArray(int size) {
            return new LiveShopWindowBean[size];
        }
    };

    public static List<LiveShopWindowBean> parse(List<NetGoodsBean> data, int type) {
        List<LiveShopWindowBean> bean = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                NetGoodsBean tempBean = data.get(i);
                bean.add(LiveShopWindowBean.builder().showType(type)
                        .id(tempBean.getId())
                        .title(tempBean.getTitle())
                        .thumb(tempBean.getThumb())
                        .saleNum(tempBean.getSales())
                        .totalNum(tempBean.getTotal())
                        .price(tempBean.getMarketprice())
                        .oldPrice(tempBean.getProductprice())
                        .webUrl(tempBean.getWebView())
                        .isCollect(tempBean.getCollection() == 1)
                        .isAdd(tempBean.getIsAdd() == 1)
                        .build());
            }
        }
        return bean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(showType);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(thumb);
        dest.writeByte((byte) (isCollect ? 1 : 0));
        dest.writeByte((byte) (isAdd ? 1 : 0));
        dest.writeString(saleNum);
        dest.writeString(totalNum);
        dest.writeString(price);
        dest.writeString(gainMoney);
        dest.writeString(oldPrice);
        dest.writeString(webUrl);
    }
}
