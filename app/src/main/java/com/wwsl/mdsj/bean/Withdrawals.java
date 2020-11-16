package com.wwsl.mdsj.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Withdrawals {

    /**
     * money : 10000
     * votes : 10000
     * orderno :
     * addtime : 1599027050
     * status : 1
     * name :
     * account :
     * account_bank :
     */

    private String money;//金额
    private String votes;//提现映票数
    private String orderno;//订单号
    private String addtime;//申请时间
    private int status;//状态 0=待审核 1=已通过
    private String name;//姓名
    private String account;//账号
    private String account_bank;//银行名称

    public Withdrawals() {
    }

}
