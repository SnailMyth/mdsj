package com.wwsl.mdsj.bean;

import java.util.List;

/**
 * @author sushi
 * @description
 * @date 2020/7/20.
 */
public class MallWelfareBean {

    /**
     * welfare : [{"goods_name":"商品名称","goods_cover":"图片","user_name":"粉丝","price":"6"}]
     * sum_price : 100
     */

    private int sum_price;
    private int estimate_price;//预计
    private int obtained_price;//已获得
    private List<WelfareBean> welfare;

    public int getEstimate_price() {
        return estimate_price;
    }

    public void setEstimate_price(int estimate_price) {
        this.estimate_price = estimate_price;
    }

    public int getObtained_price() {
        return obtained_price;
    }

    public void setObtained_price(int obtained_price) {
        this.obtained_price = obtained_price;
    }

    public int getSum_price() {
        return sum_price;
    }

    public void setSum_price(int sum_price) {
        this.sum_price = sum_price;
    }

    public List<WelfareBean> getWelfare() {
        return welfare;
    }

    public void setWelfare(List<WelfareBean> welfare) {
        this.welfare = welfare;
    }

    public static class WelfareBean {
        /**
         * goods_name : 商品名称
         * goods_cover : 图片
         * user_name : 粉丝
         * price : 6
         */

        private String goods_name;
        private String goods_cover;
        private String user_name;
        private String price;

        public String getGoods_name() {
            return goods_name;
        }

        public void setGoods_name(String goods_name) {
            this.goods_name = goods_name;
        }

        public String getGoods_cover() {
            return goods_cover;
        }

        public void setGoods_cover(String goods_cover) {
            this.goods_cover = goods_cover;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
