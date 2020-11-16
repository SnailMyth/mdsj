package com.wwsl.mdsj.interfaces;

/**
 * Created by cxf on 2017/8/9.
 * RecyclerView的Adapter 长按点击事件
 */

public interface OnItemLongClickListener<T> {
    void onItemLongClick(T bean, int position);
}
