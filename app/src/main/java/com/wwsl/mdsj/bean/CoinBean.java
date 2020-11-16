package com.wwsl.mdsj.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by cxf on 2017/9/21.
 */

public class CoinBean implements Serializable {

    private String id;
    private String coin;
    private String money;
    private String give;
    @JSONField(name = "money_ios")
    private String moneyIos;
    @JSONField(name = "product_id")
    private String productId;
    private boolean select = false;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getGive() {
        return give;
    }

    public void setGive(String give) {
        this.give = give;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public CoinBean(String coin, String money) {
        this.coin = coin;
        this.money = money;
    }

    public CoinBean() {

    }

    public String getMoneyIos() {
        return moneyIos;
    }

    public void setMoneyIos(String moneyIos) {
        this.moneyIos = moneyIos;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
