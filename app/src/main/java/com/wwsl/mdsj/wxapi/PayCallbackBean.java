package com.wwsl.mdsj.wxapi;

/**
 * @author:
 * @date: 2020/4/17 11:25
 * @description :
 */
public class PayCallbackBean {
    public int errCode;
    public String errStr;
    public String transaction;
    public String openId;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrStr() {
        return errStr;
    }

    public void setErrStr(String errStr) {
        this.errStr = errStr;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public PayCallbackBean() {
    }

    public PayCallbackBean(int errCode, String errStr, String transaction, String openId) {
        this.errCode = errCode;
        this.errStr = errStr;
        this.transaction = transaction;
        this.openId = openId;
    }
}
