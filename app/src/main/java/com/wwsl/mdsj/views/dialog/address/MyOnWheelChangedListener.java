package com.wwsl.mdsj.views.dialog.address;

public interface MyOnWheelChangedListener {
    /**
     * Callback method to be invoked when current item changed
     * @param wheel the wheel view whose state has changed
     * @param oldValue the old value of current item
     * @param newValue the new value of current item
     */
    void onChanged(MyWheelView wheel, int oldValue, int newValue);
}
