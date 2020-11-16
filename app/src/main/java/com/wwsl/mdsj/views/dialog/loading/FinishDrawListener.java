package com.wwsl.mdsj.views.dialog.loading;

import android.view.View;

public interface FinishDrawListener {
    /**
     * 分发绘制完成事件
     *
     * @param v 绘制完成的View
     */
    void dispatchFinishEvent(View v);
}
