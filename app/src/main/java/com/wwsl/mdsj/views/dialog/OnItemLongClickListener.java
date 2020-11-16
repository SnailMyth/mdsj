package com.wwsl.mdsj.views.dialog;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


/**
 * @author :
 * @date : 2020/6/9 15:23
 * @description : 长按事件
 */
public interface OnItemLongClickListener {
    void onItemLongClick(RecyclerView.ViewHolder holder, int position, View v);
}
