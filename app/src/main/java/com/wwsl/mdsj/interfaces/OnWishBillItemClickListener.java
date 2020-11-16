package com.wwsl.mdsj.interfaces;

import com.wwsl.mdsj.bean.WishBillBean;

public interface OnWishBillItemClickListener {
    void onItemClick(WishBillBean bean, int position);

    void onAvatarClick(WishBillBean bean, int position);

    void onDeleteClick(int position);
}
