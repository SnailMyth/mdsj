package com.wwsl.mdsj.interfaces;

public interface OnPictureChooseItemClickListener<T> {
    void onItemClick(T bean, int position);

    void onItemSelect(T bean, int position);

    void onItemDelete(T bean, int position);
}
