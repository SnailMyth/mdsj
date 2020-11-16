package com.wwsl.mdsj.views;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.wwsl.mdsj.interfaces.LifeCycleListener;
import com.wwsl.mdsj.interfaces.MainAppBarLayoutListener;

import java.util.List;

/**
 * Created by cxf on 2018/10/26.
 */

public abstract class AbsMainViewHolder extends AbsViewHolder {

    protected boolean mFirstLoadData = true;
    protected boolean mShowed;//是否切换到了当前页面
    protected AppCompatActivity mainActivity;//主界面

    public AbsMainViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public AbsMainViewHolder(Context context, ViewGroup parentView, Activity main) {
        super(context, parentView, main);
    }

    public void setAppBarLayoutListener(MainAppBarLayoutListener appBarLayoutListener) {
    }


    public List<LifeCycleListener> getLifeCycleListenerList() {
        return null;
    }

    public void loadData() {
    }

    protected boolean isFirstLoadData() {
        if (mFirstLoadData) {
            mFirstLoadData = false;
            return true;
        }
        return false;
    }

    public void setShowed(boolean showed) {
        mShowed = showed;
    }

    public void onResume() {

    }

    @Override
    protected void processArguments(Object... args) {
        if (args.length == 1 && args[0] instanceof Activity) {
            mainActivity = (AppCompatActivity) args[0];
        }
    }

    public void onPause() {

    }

    public void onDestroy(){

    }

    public boolean onBackPressed() {
        return false;
    }

}
